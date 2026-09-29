#!/usr/bin/env python3
"""Audit Russian localization coverage across a directory of Minecraft mod JARs.

The audit compares each discovered ``assets/<namespace>/lang/en_us`` catalogue
against the mod's built-in ``ru_ru`` catalogue and an optional resource-pack
overlay. Embedded Jar-in-Jar dependencies are inspected recursively, which is
required for mods such as Chisels & Bits whose language catalogue lives in an
embedded core JAR.

Only standard-library modules are used so the report can be reproduced with a
stock Python 3 installation.
"""

from __future__ import annotations

import argparse
import csv
import hashlib
import io
import json
import re
import sys
import tomllib
import zipfile
from collections import defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from typing import Any, Iterable

LANG_RE = re.compile(
    r"(?:^|/)assets/([^/]+)/lang/(en_us|ru_ru)\.(json|lang)$", re.IGNORECASE
)
CYRILLIC_RE = re.compile(r"[А-Яа-яЁё]")
LETTER_RE = re.compile(r"[A-Za-zА-Яа-яЁё]")
FORMAT_CODE_RE = re.compile(r"(?:§.|&[0-9A-FK-ORa-fk-or])")
PRINTF_RE = re.compile(r"%(?:(\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]")
BRACE_RE = re.compile(r"\{\d+\}")


@dataclass(frozen=True)
class CatalogueSource:
    artifact: str
    member: str


@dataclass
class Catalogue:
    values: dict[str, str] = field(default_factory=dict)
    sources: dict[str, set[CatalogueSource]] = field(
        default_factory=lambda: defaultdict(set)
    )


@dataclass
class Artifact:
    file: str
    size: int
    sha256: str
    valid_zip: bool = True
    en_namespaces: set[str] = field(default_factory=set)
    ru_namespaces: set[str] = field(default_factory=set)
    mod_ids: list[str] = field(default_factory=list)
    display_names: list[str] = field(default_factory=list)
    nested_jars: int = 0
    errors: list[str] = field(default_factory=list)


@dataclass(frozen=True)
class ModMetadata:
    artifact: str
    mod_id: str
    display_name: str
    source: str


class Audit:
    def __init__(self) -> None:
        self.english: dict[str, Catalogue] = defaultdict(Catalogue)
        self.builtin_ru: dict[str, Catalogue] = defaultdict(Catalogue)
        self.pack_ru: dict[str, Catalogue] = defaultdict(Catalogue)
        self.artifacts: list[Artifact] = []
        self.metadata: list[ModMetadata] = []
        self.conflicts: list[dict[str, str]] = []
        self.parse_errors: list[dict[str, str]] = []
        self.duplicate_keys: list[dict[str, str]] = []
        self._nested_hashes: set[str] = set()

    def add_catalogue(
        self,
        target: dict[str, Catalogue],
        namespace: str,
        values: dict[str, str],
        source: CatalogueSource,
    ) -> None:
        namespace = namespace.lower()
        catalogue = target[namespace]
        for key, value in values.items():
            if key in catalogue.values and catalogue.values[key] != value:
                catalogue_name = (
                    "en_us"
                    if target is self.english
                    else "built_in_ru_ru"
                    if target is self.builtin_ru
                    else "resource_pack_ru_ru"
                )
                self.conflicts.append(
                    {
                        "catalogue": catalogue_name,
                        "namespace": namespace,
                        "key": key,
                        "first_value": catalogue.values[key],
                        "other_value": value,
                        "other_source": f"{source.artifact}!/{source.member}",
                    }
                )
                # Resource ordering differs by loader. Keep the first stable
                # value but expose every conflict instead of hiding it.
            else:
                catalogue.values.setdefault(key, value)
            catalogue.sources[key].add(source)

    def record_parse_error(self, artifact: str, member: str, error: Exception | str) -> None:
        self.parse_errors.append(
            {"artifact": artifact, "member": member, "error": str(error)}
        )


def _strip_json_comments(text: str) -> str:
    """Remove JS comments without touching comment-like text inside strings."""
    out: list[str] = []
    i = 0
    in_string = False
    escaped = False
    while i < len(text):
        char = text[i]
        nxt = text[i + 1] if i + 1 < len(text) else ""
        if in_string:
            out.append(char)
            if escaped:
                escaped = False
            elif char == "\\":
                escaped = True
            elif char == '"':
                in_string = False
            i += 1
            continue
        if char == '"':
            in_string = True
            out.append(char)
            i += 1
            continue
        if char == "/" and nxt == "/":
            i += 2
            while i < len(text) and text[i] not in "\r\n":
                i += 1
            continue
        if char == "/" and nxt == "*":
            end = text.find("*/", i + 2)
            i = len(text) if end == -1 else end + 2
            continue
        out.append(char)
        i += 1
    return "".join(out)


def _parse_json_with_duplicates(text: str) -> tuple[Any, list[str]]:
    duplicates: list[str] = []

    def object_pairs(pairs: list[tuple[str, Any]]) -> dict[str, Any]:
        result: dict[str, Any] = {}
        for key, value in pairs:
            if key in result:
                duplicates.append(str(key))
            # JSON parsers conventionally retain the last duplicate value.
            result[key] = value
        return result

    parsed = json.loads(text, object_pairs_hook=object_pairs)
    return parsed, duplicates


def parse_lang_bytes(data: bytes, extension: str) -> tuple[dict[str, str], list[str]]:
    text = data.decode("utf-8-sig")
    if extension.lower() == "lang":
        result: dict[str, str] = {}
        duplicates: list[str] = []
        for raw_line in text.splitlines():
            line = raw_line.strip()
            if not line or line.startswith("#") or "=" not in line:
                continue
            key, value = line.split("=", 1)
            key = key.strip()
            if key in result:
                duplicates.append(key)
            result[key] = value.strip()
        return result, duplicates

    try:
        parsed, duplicates = _parse_json_with_duplicates(text)
    except json.JSONDecodeError:
        relaxed = _strip_json_comments(text)
        relaxed = re.sub(r",\s*([}\]])", r"\1", relaxed)
        parsed, duplicates = _parse_json_with_duplicates(relaxed)
    if not isinstance(parsed, dict):
        raise ValueError("language catalogue root is not an object")
    return {str(key): str(value) for key, value in parsed.items()}, duplicates


def _safe_toml(text: str) -> dict[str, Any]:
    # Some mods write ${...} placeholders, which are valid inside quoted TOML
    # strings and therefore need no special handling.
    return tomllib.loads(text)


def parse_metadata_from_zip(
    zf: zipfile.ZipFile, artifact_name: str, audit: Audit, artifact: Artifact
) -> None:
    names = set(zf.namelist())
    toml_member = next(
        (
            candidate
            for candidate in ("META-INF/neoforge.mods.toml", "META-INF/mods.toml")
            if candidate in names
        ),
        None,
    )
    if toml_member:
        try:
            data = _safe_toml(zf.read(toml_member).decode("utf-8-sig"))
            mods = data.get("mods", [])
            if isinstance(mods, dict):
                mods = [mods]
            for entry in mods:
                if not isinstance(entry, dict):
                    continue
                mod_id = str(entry.get("modId", entry.get("modid", ""))).strip()
                display = str(entry.get("displayName", entry.get("displayname", mod_id))).strip()
                if mod_id:
                    artifact.mod_ids.append(mod_id)
                    artifact.display_names.append(display)
                    audit.metadata.append(
                        ModMetadata(artifact_name, mod_id, display, toml_member)
                    )
        except Exception as exc:  # report malformed third-party metadata
            artifact.errors.append(f"{toml_member}: {exc}")

    fabric_member = "fabric.mod.json"
    if fabric_member in names:
        try:
            data = json.loads(zf.read(fabric_member).decode("utf-8-sig"))
            mod_id = str(data.get("id", "")).strip()
            display = str(data.get("name", mod_id)).strip()
            if mod_id:
                artifact.mod_ids.append(mod_id)
                artifact.display_names.append(display)
                audit.metadata.append(
                    ModMetadata(artifact_name, mod_id, display, fabric_member)
                )
        except Exception as exc:
            artifact.errors.append(f"{fabric_member}: {exc}")


def scan_zip(
    audit: Audit,
    source: str | Path | bytes,
    artifact: Artifact,
    artifact_label: str,
    chain: str = "",
    depth: int = 0,
    max_depth: int = 3,
) -> None:
    try:
        with zipfile.ZipFile(io.BytesIO(source) if isinstance(source, bytes) else source) as zf:
            if depth == 0:
                parse_metadata_from_zip(zf, artifact_label, audit, artifact)
            names = zf.namelist()
            for member in names:
                match = LANG_RE.search(member)
                if not match:
                    continue
                namespace, locale, extension = (
                    match.group(1).lower(),
                    match.group(2).lower(),
                    match.group(3).lower(),
                )
                full_member = f"{chain}!/{member}" if chain else member
                try:
                    values, duplicates = parse_lang_bytes(zf.read(member), extension)
                except Exception as exc:
                    audit.record_parse_error(artifact_label, full_member, exc)
                    artifact.errors.append(f"{full_member}: {exc}")
                    continue
                for key in duplicates:
                    audit.duplicate_keys.append(
                        {
                            "artifact": artifact_label,
                            "member": full_member,
                            "locale": locale,
                            "key": key,
                        }
                    )
                target = audit.english if locale == "en_us" else audit.builtin_ru
                audit.add_catalogue(
                    target,
                    namespace,
                    values,
                    CatalogueSource(artifact_label, full_member),
                )
                if locale == "en_us":
                    artifact.en_namespaces.add(namespace)
                else:
                    artifact.ru_namespaces.add(namespace)

            if depth >= max_depth:
                return
            for member in names:
                if not member.lower().endswith(".jar"):
                    continue
                # Embedded dependencies conventionally live under META-INF,
                # but scan every embedded JAR: several bundled mods use custom
                # paths. A size guard prevents accidental expansion bombs.
                info = zf.getinfo(member)
                if info.file_size > 100 * 1024 * 1024:
                    artifact.errors.append(
                        f"embedded JAR skipped (>100 MiB): {member}"
                    )
                    continue
                try:
                    payload = zf.read(member)
                except Exception as exc:
                    artifact.errors.append(f"cannot read embedded JAR {member}: {exc}")
                    continue
                digest = hashlib.sha256(payload).hexdigest()
                artifact.nested_jars += 1
                # Duplicated embedded libraries are common. Their catalogues
                # were already accounted for globally, so avoid scanning the
                # same bytes repeatedly while still counting them per artifact.
                if digest in audit._nested_hashes:
                    continue
                audit._nested_hashes.add(digest)
                nested_chain = f"{chain}!/{member}" if chain else member
                try:
                    scan_zip(
                        audit,
                        payload,
                        artifact,
                        artifact_label,
                        nested_chain,
                        depth + 1,
                        max_depth,
                    )
                except zipfile.BadZipFile as exc:
                    artifact.errors.append(f"invalid embedded JAR {nested_chain}: {exc}")
    except zipfile.BadZipFile:
        artifact.valid_zip = False
        raise


def scan_artifacts(audit: Audit, mods_dir: Path, max_nested_depth: int = 3) -> None:
    jars = sorted(mods_dir.glob("*.jar"), key=lambda path: path.name.casefold())
    for index, path in enumerate(jars, start=1):
        digest = hashlib.sha256()
        with path.open("rb") as stream:
            for chunk in iter(lambda: stream.read(1024 * 1024), b""):
                digest.update(chunk)
        artifact = Artifact(path.name, path.stat().st_size, digest.hexdigest())
        try:
            scan_zip(
                audit,
                path,
                artifact,
                path.name,
                max_depth=max_nested_depth,
            )
        except Exception as exc:
            artifact.valid_zip = False
            artifact.errors.append(str(exc))
        artifact.mod_ids = sorted(set(artifact.mod_ids))
        artifact.display_names = sorted(set(artifact.display_names))
        audit.artifacts.append(artifact)
        if index % 25 == 0:
            print(f"Scanned {index}/{len(jars)} JARs", file=sys.stderr)


def scan_pack_directory(audit: Audit, pack_dir: Path) -> None:
    for path in sorted(pack_dir.rglob("*")):
        if not path.is_file():
            continue
        rel = path.relative_to(pack_dir).as_posix()
        match = LANG_RE.search(rel)
        if not match or match.group(2).lower() != "ru_ru":
            continue
        namespace, extension = match.group(1).lower(), match.group(3).lower()
        try:
            values, duplicates = parse_lang_bytes(path.read_bytes(), extension)
        except Exception as exc:
            audit.record_parse_error(pack_dir.name, rel, exc)
            continue
        for key in duplicates:
            audit.duplicate_keys.append(
                {"artifact": pack_dir.name, "member": rel, "locale": "ru_ru", "key": key}
            )
        audit.add_catalogue(
            audit.pack_ru,
            namespace,
            values,
            CatalogueSource(pack_dir.name, rel),
        )


def scan_pack_zip(audit: Audit, pack_zip: Path) -> None:
    with zipfile.ZipFile(pack_zip) as zf:
        for member in sorted(zf.namelist()):
            match = LANG_RE.search(member)
            if not match or match.group(2).lower() != "ru_ru":
                continue
            namespace, extension = match.group(1).lower(), match.group(3).lower()
            try:
                values, duplicates = parse_lang_bytes(zf.read(member), extension)
            except Exception as exc:
                audit.record_parse_error(pack_zip.name, member, exc)
                continue
            for key in duplicates:
                audit.duplicate_keys.append(
                    {"artifact": pack_zip.name, "member": member, "locale": "ru_ru", "key": key}
                )
            audit.add_catalogue(
                audit.pack_ru,
                namespace,
                values,
                CatalogueSource(pack_zip.name, member),
            )


def category_for(key: str) -> str:
    low = key.lower()
    # Classify semantic suffixes before broad block/item prefixes so
    # block.foo.tooltip.* is counted as a tooltip rather than as a block name.
    if any(token in low for token in ("tooltip", ".help", ".description", ".summary")):
        return "tooltip_help"
    if low.startswith("block."):
        return "block"
    if low.startswith("item."):
        return "item"
    if low.startswith("entity."):
        return "entity"
    if low.startswith(("itemgroup.", "item_group.", "creative_tab.")):
        return "creative_tab"
    if low.startswith(
        (
            "gui.",
            "screen.",
            "menu.",
            "config.",
            "button.",
            "message.",
            "command.",
            "key.",
            "keybind.",
            "category.",
            "options.",
            "advancement.",
        )
    ) or any(token in low for token in (".gui.", ".screen.", ".config.", ".message.")):
        return "interface"
    return "other"


def normalized_text(value: str) -> str:
    value = FORMAT_CODE_RE.sub("", value)
    return " ".join(value.casefold().split())


def markdown_cell(value: Any) -> str:
    return str(value).replace("|", "\\|").replace("\n", "<br>")


def placeholders(value: str) -> tuple[str, ...]:
    result = [match.group(0) for match in PRINTF_RE.finditer(value)]
    result.extend(BRACE_RE.findall(value))
    return tuple(sorted(result))


def source_jars(catalogue: Catalogue, keys: Iterable[str] | None = None) -> str:
    selected = keys if keys is not None else catalogue.sources.keys()
    names: set[str] = set()
    for key in selected:
        names.update(source.artifact for source in catalogue.sources.get(key, set()))
    return "; ".join(sorted(names, key=str.casefold))


def write_csv(path: Path, fieldnames: list[str], rows: Iterable[dict[str, Any]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8-sig", newline="") as stream:
        writer = csv.DictWriter(
            stream,
            fieldnames=fieldnames,
            extrasaction="ignore",
            lineterminator="\n",
        )
        writer.writeheader()
        for row in rows:
            normalized = {
                key: (
                    "\\n".join(
                        line.rstrip()
                        for line in str(value).replace("\r\n", "\n").replace("\r", "\n").split("\n")
                    )
                    if isinstance(value, str)
                    else value
                )
                for key, value in row.items()
            }
            writer.writerow(normalized)


def build_reports(audit: Audit, output_dir: Path, pack_label: str) -> dict[str, Any]:
    output_dir.mkdir(parents=True, exist_ok=True)
    namespace_rows: list[dict[str, Any]] = []
    missing_rows: list[dict[str, Any]] = []
    suspicious_rows: list[dict[str, Any]] = []
    orphan_pack_rows: list[dict[str, Any]] = []
    category_totals: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))

    all_namespaces = sorted(
        set(audit.english) | set(audit.builtin_ru) | set(audit.pack_ru)
    )
    for namespace in all_namespaces:
        en_catalogue = audit.english[namespace]
        builtin_catalogue = audit.builtin_ru[namespace]
        pack_catalogue = audit.pack_ru[namespace]
        en = en_catalogue.values
        builtin = builtin_catalogue.values
        pack = pack_catalogue.values
        effective: dict[str, str] = {}
        effective_source: dict[str, str] = {}
        for key in en:
            if key in pack:
                effective[key] = pack[key]
                effective_source[key] = "resource_pack"
            elif key in builtin:
                effective[key] = builtin[key]
                effective_source[key] = "built_in"

        en_keys = set(en)
        builtin_match = en_keys & set(builtin)
        pack_match = en_keys & set(pack)
        for key in sorted(set(pack) - en_keys):
            orphan_pack_rows.append(
                {
                    "namespace": namespace,
                    "key": key,
                    "russian_value": pack[key],
                    "pack_sources": source_jars(pack_catalogue, [key]),
                }
            )
        effective_keys = set(effective)
        missing = sorted(en_keys - effective_keys)
        identical = sorted(
            key
            for key in effective_keys
            if normalized_text(effective[key]) == normalized_text(en[key])
            and LETTER_RE.search(en[key])
        )
        no_cyrillic = sorted(
            key
            for key in effective_keys
            if LETTER_RE.search(effective[key]) and not CYRILLIC_RE.search(effective[key])
        )
        placeholder_mismatch = sorted(
            key
            for key in effective_keys
            if placeholders(en[key]) != placeholders(effective[key])
        )

        category_counts: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
        for key, en_value in en.items():
            category = category_for(key)
            category_counts[category]["english"] += 1
            category_totals[category]["english"] += 1
            if key in effective:
                category_counts[category]["covered"] += 1
                category_totals[category]["covered"] += 1
            else:
                missing_rows.append(
                    {
                        "namespace": namespace,
                        "category": category,
                        "key": key,
                        "english_value": en_value,
                        "source_jars": source_jars(en_catalogue, [key]),
                    }
                )
        for key in sorted(effective_keys):
            reasons: list[str] = []
            if key in identical:
                reasons.append("identical_to_english")
            if key in no_cyrillic:
                reasons.append("no_cyrillic")
            if key in placeholder_mismatch:
                reasons.append("placeholder_mismatch")
            if reasons:
                suspicious_rows.append(
                    {
                        "namespace": namespace,
                        "category": category_for(key),
                        "key": key,
                        "english_value": en[key],
                        "russian_value": effective[key],
                        "translation_source": effective_source[key],
                        "reason": ";".join(reasons),
                        "source_jars": source_jars(en_catalogue, [key]),
                    }
                )

        english_count = len(en)
        covered_count = len(effective_keys)
        coverage = (100.0 * covered_count / english_count) if english_count else 0.0
        if not english_count:
            status = "no_en_us"
        elif covered_count == 0:
            status = "none"
        elif missing:
            status = "partial"
        else:
            status = "complete"
        quality = "ok"
        if placeholder_mismatch:
            quality = "format_error"
        elif missing:
            quality = "missing_keys"
        elif identical or no_cyrillic:
            quality = "review_non_russian_values"

        namespace_rows.append(
            {
                "namespace": namespace,
                "status": status,
                "quality": quality,
                "english_keys": english_count,
                "built_in_ru_matches": len(builtin_match),
                "pack_ru_matches": len(pack_match),
                "effective_covered": covered_count,
                "coverage_percent": f"{coverage:.2f}",
                "missing_keys": len(missing),
                "identical_to_english": len(identical),
                "no_cyrillic": len(no_cyrillic),
                "placeholder_mismatches": len(placeholder_mismatch),
                "blocks": category_counts["block"]["english"],
                "blocks_covered": category_counts["block"]["covered"],
                "items": category_counts["item"]["english"],
                "items_covered": category_counts["item"]["covered"],
                "tooltips_help": category_counts["tooltip_help"]["english"],
                "tooltips_help_covered": category_counts["tooltip_help"]["covered"],
                "interfaces": category_counts["interface"]["english"],
                "interfaces_covered": category_counts["interface"]["covered"],
                "source_jars": source_jars(en_catalogue),
            }
        )

    namespace_fields = [
        "namespace",
        "status",
        "quality",
        "english_keys",
        "built_in_ru_matches",
        "pack_ru_matches",
        "effective_covered",
        "coverage_percent",
        "missing_keys",
        "identical_to_english",
        "no_cyrillic",
        "placeholder_mismatches",
        "blocks",
        "blocks_covered",
        "items",
        "items_covered",
        "tooltips_help",
        "tooltips_help_covered",
        "interfaces",
        "interfaces_covered",
        "source_jars",
    ]
    write_csv(output_dir / "namespaces.csv", namespace_fields, namespace_rows)
    write_csv(
        output_dir / "missing_keys.csv",
        ["namespace", "category", "key", "english_value", "source_jars"],
        missing_rows,
    )
    write_csv(
        output_dir / "suspicious_values.csv",
        [
            "namespace",
            "category",
            "key",
            "english_value",
            "russian_value",
            "translation_source",
            "reason",
            "source_jars",
        ],
        suspicious_rows,
    )
    write_csv(
        output_dir / "orphan_pack_keys.csv",
        ["namespace", "key", "russian_value", "pack_sources"],
        orphan_pack_rows,
    )
    write_csv(
        output_dir / "artifacts.csv",
        [
            "file",
            "size",
            "sha256",
            "valid_zip",
            "mod_ids",
            "display_names",
            "en_namespaces",
            "ru_namespaces",
            "nested_jars",
            "errors",
        ],
        (
            {
                "file": item.file,
                "size": item.size,
                "sha256": item.sha256,
                "valid_zip": item.valid_zip,
                "mod_ids": "; ".join(item.mod_ids),
                "display_names": "; ".join(item.display_names),
                "en_namespaces": "; ".join(sorted(item.en_namespaces)),
                "ru_namespaces": "; ".join(sorted(item.ru_namespaces)),
                "nested_jars": item.nested_jars,
                "errors": " | ".join(item.errors),
            }
            for item in audit.artifacts
        ),
    )
    write_csv(
        output_dir / "mod_metadata.csv",
        ["artifact", "mod_id", "display_name", "source", "resource_pack_localizable"],
        (
            {
                "artifact": item.artifact,
                "mod_id": item.mod_id,
                "display_name": item.display_name,
                "source": item.source,
                "resource_pack_localizable": "no",
            }
            for item in sorted(
                audit.metadata,
                key=lambda item: (item.artifact.casefold(), item.mod_id.casefold()),
            )
        ),
    )
    write_csv(
        output_dir / "catalogue_conflicts.csv",
        ["catalogue", "namespace", "key", "first_value", "other_value", "other_source"],
        audit.conflicts,
    )
    write_csv(
        output_dir / "parse_errors.csv",
        ["artifact", "member", "error"],
        audit.parse_errors,
    )
    write_csv(
        output_dir / "duplicate_keys.csv",
        ["artifact", "member", "locale", "key"],
        audit.duplicate_keys,
    )

    total_english = sum(int(row["english_keys"]) for row in namespace_rows)
    total_covered = sum(int(row["effective_covered"]) for row in namespace_rows)
    total_missing = sum(int(row["missing_keys"]) for row in namespace_rows)
    total_identical = sum(int(row["identical_to_english"]) for row in namespace_rows)
    total_no_cyrillic = sum(int(row["no_cyrillic"]) for row in namespace_rows)
    total_placeholder_mismatches = sum(
        int(row["placeholder_mismatches"]) for row in namespace_rows
    )
    total_pack_keys = sum(len(catalogue.values) for catalogue in audit.pack_ru.values())
    total_pack_matches = total_pack_keys - len(orphan_pack_rows)
    english_namespaces = [row for row in namespace_rows if int(row["english_keys"])]
    complete = [row for row in english_namespaces if row["status"] == "complete"]
    partial = [row for row in english_namespaces if row["status"] == "partial"]
    none = [row for row in english_namespaces if row["status"] == "none"]
    review = [row for row in english_namespaces if int(row["identical_to_english"])]

    metadata_no_cyrillic = [
        item
        for item in audit.metadata
        if LETTER_RE.search(item.display_name) and not CYRILLIC_RE.search(item.display_name)
    ]

    report_lines = [
        "# Полный аудит русификации сборки",
        "",
        "> Отчёт сгенерирован сравнением реальных `en_us`/`ru_ru` из JAR, "
        "включая вложенные Jar-in-Jar, с наложением текущего RU-pack.",
        "",
        "## Объём проверки",
        "",
        f"- Проверено активных JAR: **{len(audit.artifacts)}**.",
        f"- Все JAR корректны как ZIP: **{sum(a.valid_zip for a in audit.artifacts)}/{len(audit.artifacts)}**.",
        f"- Найдено пространств имён с `en_us`: **{len(english_namespaces)}**.",
        f"- Английских ключей: **{total_english:,}**.",
        f"- Ключей в RU-pack: **{total_pack_keys:,}**; совпадают с текущими `en_us`: "
        f"**{total_pack_matches:,}**; устаревших/лишних: **{len(orphan_pack_rows):,}**.",
        f"- Покрыто встроенным `ru_ru` или `{pack_label}`: **{total_covered:,}** "
        f"(**{(100.0 * total_covered / total_english if total_english else 0):.2f}%**).",
        f"- Отсутствует переводов: **{total_missing:,}**.",
        f"- Ключей, где effective RU дословно совпадает с English: **{total_identical:,}**; "
        f"значений без кириллицы: **{total_no_cyrillic:,}** (это очередь ручной проверки, "
        "а не автоматический приговор для брендов и аббревиатур).",
        f"- Несовпадений format placeholders (`%s`, `%1$s`, `{{0}}`): **{total_placeholder_mismatches:,}**.",
        f"- Дублирующихся ключей внутри исходных lang-файлов: **{len(audit.duplicate_keys):,}** "
        "(сохранено последнее значение, как у обычного JSON-парсера).",
        f"- Полностью покрытых пространств: **{len(complete)}**; частичных: **{len(partial)}**; "
        f"без единого русского ключа: **{len(none)}**.",
        f"- Пространств с английскими/неизменёнными значениями, требующими ручной проверки: **{len(review)}**.",
        f"- Статических англоязычных названий модов в metadata: **{len(metadata_no_cyrillic)}**. "
        "Обычный resource pack их не переопределяет.",
        "",
        "## Покрытие по типам ключей",
        "",
        "| Тип | Покрыто | Всего | Доля |",
        "|---|---:|---:|---:|",
    ]
    category_labels = {
        "block": "Блоки",
        "item": "Предметы",
        "tooltip_help": "Подсказки/описания",
        "interface": "Интерфейсы/config/messages",
        "entity": "Сущности",
        "creative_tab": "Творческие вкладки",
        "other": "Прочие строки",
    }
    for category in (
        "block",
        "item",
        "tooltip_help",
        "interface",
        "entity",
        "creative_tab",
        "other",
    ):
        counts = category_totals[category]
        total = counts["english"]
        covered = counts["covered"]
        percent = 100.0 * covered / total if total else 0.0
        report_lines.append(
            f"| {category_labels[category]} | {covered:,} | {total:,} | {percent:.2f}% |"
        )

    report_lines.extend(
        [
            "",
            "## Показанные пользователем проблемы",
            "",
        ]
    )
    focus = {
        "chiselsandbits": [
            "item.chiselsandbits.chisel_stone",
            "mod.chiselsandbits.chiselmode.mode_grouped",
            "mod.chiselsandbits.chiselmode.cubed",
            "mod.chiselsandbits.chiselmode.single",
        ],
        "aeronauticswinds": ["item.aeronauticswinds.cotton_seeds"],
        "create_no_touching": ["item_group.create_no_touching.create_no_touching"],
    }
    for namespace, keys in focus.items():
        en = audit.english[namespace].values
        builtin = audit.builtin_ru[namespace].values
        pack = audit.pack_ru[namespace].values
        report_lines.append(f"### `{namespace}`")
        report_lines.append("")
        row = next((row for row in namespace_rows if row["namespace"] == namespace), None)
        if row:
            report_lines.append(
                f"Покрытие ключей: **{row['effective_covered']}/{row['english_keys']} "
                f"({row['coverage_percent']}%)**, отсутствует **{row['missing_keys']}**."
            )
            report_lines.append("")
        report_lines.append("| Ключ | English | Текущее effective RU | Источник |")
        report_lines.append("|---|---|---|---|")
        for key in keys:
            if key not in en:
                continue
            value = pack.get(key, builtin.get(key, "—"))
            source = "RU-pack" if key in pack else ("встроенный ru_ru" if key in builtin else "нет")
            report_lines.append(
                f"| `{key}` | {markdown_cell(en[key])} | {markdown_cell(value)} | {source} |"
            )
        report_lines.append("")

    report_lines.extend(
        [
            "Примечание к `Monook Seeds`/`Moonoak Seeds`: такой строки, ключа или ресурса "
            "нет в проверенном `aeronauticswinds-1.3.1.jar`; единственные семена в нём — "
            "`item.aeronauticswinds.cotton_seeds` (`Cotton Seeds`). Значит, показанный "
            "экземпляр игры использует другой JAR либо динамическую строку, которой нет в "
            "папке сборки. В v0.8-test исправлено доступное `Cotton Seeds` → `Семена хлопка`.",
            "",
        ]
    )

    no_touching_metadata = [
        item for item in audit.metadata if item.mod_id == "create_no_touching"
    ]
    if no_touching_metadata:
        shown_name = no_touching_metadata[0].display_name
        report_lines.extend(
            [
                f"Название **{shown_name}** в строке принадлежности моду берётся из "
                "`META-INF/neoforge.mods.toml`, а не из lang-файла. Его нельзя корректно "
                "заменить обычным resource pack; для этого нужен отдельный кодовый патч/аддон "
                "или изменение исходного стороннего мода.",
                "",
            ]
        )

    report_lines.extend(
        [
            "## Главная очередь по числу отсутствующих ключей",
            "",
            "| Namespace | Нет ключей | Покрытие | Block | Item | Tooltip | GUI/config |",
            "|---|---:|---:|---:|---:|---:|---:|",
        ]
    )
    for row in sorted(
        english_namespaces,
        key=lambda item: (-int(item["missing_keys"]), item["namespace"]),
    )[:30]:
        report_lines.append(
            f"| `{row['namespace']}` | {row['missing_keys']} | "
            f"{row['effective_covered']}/{row['english_keys']} ({row['coverage_percent']}%) | "
            f"{row['blocks_covered']}/{row['blocks']} | {row['items_covered']}/{row['items']} | "
            f"{row['tooltips_help_covered']}/{row['tooltips_help']} | "
            f"{row['interfaces_covered']}/{row['interfaces']} |"
        )

    report_lines.extend(
        [
            "",
            "## Полная таблица по пространствам имён",
            "",
            "| Namespace | Статус | Покрытие | Нет ключей | Block | Item | Tooltip | GUI/config | JAR |",
            "|---|---|---:|---:|---:|---:|---:|---:|---|",
        ]
    )
    ranked = sorted(
        english_namespaces,
        key=lambda row: (
            float(row["coverage_percent"]),
            -int(row["english_keys"]),
            row["namespace"],
        ),
    )
    for row in ranked:
        report_lines.append(
            f"| `{row['namespace']}` | {row['status']} | {row['effective_covered']}/{row['english_keys']} "
            f"({row['coverage_percent']}%) | {row['missing_keys']} | "
            f"{row['blocks_covered']}/{row['blocks']} | {row['items_covered']}/{row['items']} | "
            f"{row['tooltips_help_covered']}/{row['tooltips_help']} | "
            f"{row['interfaces_covered']}/{row['interfaces']} | "
            f"{markdown_cell(row['source_jars'])} |"
        )

    report_lines.extend(
        [
            "",
            "## Файлы детального аудита",
            "",
            "- `artifacts.csv` — все проверенные JAR, SHA-256, mod IDs и найденные языки.",
            "- `namespaces.csv` — точное покрытие каждого namespace и каждой категории.",
            "- `missing_keys.csv` — каждый отсутствующий ключ с английским значением и JAR-источником.",
            "- `orphan_pack_keys.csv` — ключи RU-pack, которых нет в текущих `en_us` (устаревшие или ошибочные).",
            "- `suspicious_values.csv` — совпавшие с English, без кириллицы или с повреждёнными placeholders.",
            "- `mod_metadata.csv` — статические названия модов, которые resource pack не локализует.",
            "- `catalogue_conflicts.csv` — конфликтующие определения одного ключа между JAR.",
            "- `duplicate_keys.csv` — повторяющиеся ключи внутри одного lang-файла.",
            "- `parse_errors.csv` — ошибки чтения каталогов (должен быть пустым кроме заголовка).",
            "",
            "## Методика и ограничения",
            "",
            "- Покрытие считается строго по ключам: `pack ru_ru` → встроенный `ru_ru` → missing.",
            "- Совпадение русского значения с английским помечается на ручную проверку, но не "
            "автоматически считается ошибкой: аббревиатуры, бренды и имена могут быть корректны.",
            "- Наличие кириллицы — только индикатор качества; оно не заменяет редакторскую проверку.",
            "- Динамически составляемые строки без lang-ключей и текст внутри изображений/книг "
            "нужно проверять отдельно в игре.",
        ]
    )
    (output_dir / "REPORT.md").write_text(
        "\n".join(report_lines) + "\n", encoding="utf-8"
    )

    result = {
        "jars": len(audit.artifacts),
        "valid_jars": sum(a.valid_zip for a in audit.artifacts),
        "namespaces_with_english": len(english_namespaces),
        "english_keys": total_english,
        "covered_keys": total_covered,
        "missing_keys": total_missing,
        "identical_to_english": total_identical,
        "no_cyrillic": total_no_cyrillic,
        "placeholder_mismatches": total_placeholder_mismatches,
        "resource_pack_keys": total_pack_keys,
        "resource_pack_matching_keys": total_pack_matches,
        "resource_pack_orphan_keys": len(orphan_pack_rows),
        "complete_namespaces": len(complete),
        "partial_namespaces": len(partial),
        "untranslated_namespaces": len(none),
        "parse_errors": len(audit.parse_errors),
        "duplicate_keys": len(audit.duplicate_keys),
        "conflicts": len(audit.conflicts),
    }
    (output_dir / "summary.json").write_text(
        json.dumps(result, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )
    return result


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--mods", type=Path, required=True, help="Directory containing active .jar files")
    parser.add_argument(
        "--resource-pack",
        type=Path,
        required=True,
        help="Resource-pack directory or ZIP containing ru_ru catalogues",
    )
    parser.add_argument("--output", type=Path, required=True, help="Output report directory")
    parser.add_argument(
        "--max-nested-depth",
        type=int,
        default=3,
        help="Maximum Jar-in-Jar recursion depth (default: 3)",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if not args.mods.is_dir():
        raise SystemExit(f"mods directory not found: {args.mods}")
    if not args.resource_pack.exists():
        raise SystemExit(f"resource pack not found: {args.resource_pack}")

    audit = Audit()
    scan_artifacts(audit, args.mods, args.max_nested_depth)
    if args.resource_pack.is_dir():
        scan_pack_directory(audit, args.resource_pack)
    else:
        scan_pack_zip(audit, args.resource_pack)
    summary = build_reports(audit, args.output, args.resource_pack.name)
    print(json.dumps(summary, ensure_ascii=False, indent=2))
    return 0 if summary["parse_errors"] == 0 else 2


if __name__ == "__main__":
    raise SystemExit(main())
