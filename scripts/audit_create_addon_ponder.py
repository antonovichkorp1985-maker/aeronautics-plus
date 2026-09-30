#!/usr/bin/env python3
"""Audit effective Russian Ponder text in installed Create add-on JARs.

The audit reads the add-ons themselves (including nested jar-in-jar archives),
then applies the repository's resource-pack overlays over each built-in ru_ru
catalogue. It deliberately treats missing keys and English values separately:
key coverage alone is not evidence that a Ponder scene is translated.
"""

from __future__ import annotations

import argparse
import csv
import io
import json
import re
import zipfile
from collections import defaultdict
from dataclasses import dataclass, field
from pathlib import Path
from typing import BinaryIO, Iterable

LANG_MEMBER_RE = re.compile(r"^assets/([^/]+)/lang/(en_us|ru_ru)\.json$", re.IGNORECASE)
PONDER_KEY_RE = re.compile(
    r"(?:^|[._/])ponder(?:[._/]|$)|_ponder(?:[._/]|$)", re.IGNORECASE
)
CYRILLIC_RE = re.compile(r"[А-Яа-яЁё]")
LATIN_RE = re.compile(r"[A-Za-z]")
PRINTF_RE = re.compile(r"%(?:(\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]")
BRACE_RE = re.compile(r"\{\d+\}")
BASE_NAMESPACES = {"create", "flywheel", "ponder"}


@dataclass
class Catalogue:
    english: dict[str, str] = field(default_factory=dict)
    russian: dict[str, str] = field(default_factory=dict)
    sources: set[str] = field(default_factory=set)
    conflicts: set[str] = field(default_factory=set)


@dataclass(frozen=True)
class AuditRow:
    namespace: str
    key: str
    status: str
    english_value: str
    russian_value: str
    source_jars: str
    placeholder_match: bool


def load_object(raw: bytes, source: str) -> dict[str, str]:
    try:
        data = json.loads(raw.decode("utf-8-sig"))
    except (UnicodeDecodeError, json.JSONDecodeError) as error:
        raise ValueError(f"invalid language JSON at {source}: {error}") from error
    if not isinstance(data, dict) or any(
        not isinstance(key, str) or not isinstance(value, str)
        for key, value in data.items()
    ):
        raise ValueError(f"{source} is not a string-to-string JSON object")
    return data


def placeholders(value: str) -> tuple[str, ...]:
    result = [match.group(0) for match in PRINTF_RE.finditer(value)]
    result.extend(BRACE_RE.findall(value))
    return tuple(sorted(result))


def merge_values(
    target: dict[str, str],
    incoming: dict[str, str],
    conflicts: set[str],
) -> None:
    for key, value in incoming.items():
        if key in target and target[key] != value:
            conflicts.add(key)
            continue
        target.setdefault(key, value)


def iter_archives(
    stream: BinaryIO,
    label: str,
    *,
    depth: int = 0,
    max_depth: int = 3,
) -> Iterable[tuple[zipfile.ZipFile, str]]:
    """Yield an archive and nested jar-in-jar archives while their streams live."""
    with zipfile.ZipFile(stream) as archive:
        yield archive, label
        if depth >= max_depth:
            return
        for member in sorted(archive.namelist()):
            if not member.lower().endswith(".jar"):
                continue
            try:
                nested = io.BytesIO(archive.read(member))
                if not zipfile.is_zipfile(nested):
                    continue
                nested.seek(0)
                yield from iter_archives(
                    nested,
                    f"{label}!/{member}",
                    depth=depth + 1,
                    max_depth=max_depth,
                )
            except (KeyError, OSError, zipfile.BadZipFile):
                continue


def read_catalogues(jars_dir: Path) -> tuple[dict[str, Catalogue], int, list[str]]:
    catalogues: dict[str, Catalogue] = defaultdict(Catalogue)
    jar_count = 0
    errors: list[str] = []

    for jar_path in sorted(jars_dir.glob("*.jar"), key=lambda path: path.name.lower()):
        jar_count += 1
        try:
            with jar_path.open("rb") as stream:
                for archive, archive_label in iter_archives(stream, jar_path.name):
                    names = archive.namelist()
                    for member in names:
                        match = LANG_MEMBER_RE.fullmatch(member)
                        if not match:
                            continue
                        namespace, language = match.groups()
                        namespace = namespace.lower()
                        source = f"{archive_label}!/{member}"
                        try:
                            values = load_object(archive.read(member), source)
                        except ValueError as error:
                            errors.append(str(error))
                            continue
                        catalogue = catalogues[namespace]
                        catalogue.sources.add(jar_path.name)
                        if language.lower() == "en_us":
                            merge_values(catalogue.english, values, catalogue.conflicts)
                        else:
                            merge_values(catalogue.russian, values, catalogue.conflicts)
        except (OSError, zipfile.BadZipFile) as error:
            errors.append(f"cannot inspect {jar_path}: {error}")

    return dict(catalogues), jar_count, errors


def load_overlays(pack_root: Path) -> dict[str, dict[str, str]]:
    overlays: dict[str, dict[str, str]] = {}
    assets = pack_root / "assets"
    if not assets.is_dir():
        raise SystemExit(f"resource-pack assets directory not found: {assets}")
    for path in sorted(assets.glob("*/lang/ru_ru.json")):
        namespace = path.parent.parent.name.lower()
        try:
            overlays[namespace] = load_object(path.read_bytes(), str(path))
        except ValueError as error:
            raise SystemExit(str(error)) from error
    return overlays


def classify(english: str, russian: str | None) -> str:
    if russian is None:
        return "missing"
    if russian == english:
        return "identical_english"
    has_cyrillic = bool(CYRILLIC_RE.search(russian))
    has_latin = bool(LATIN_RE.search(russian))
    if has_latin and not has_cyrillic:
        return "latin_only"
    if has_latin and has_cyrillic:
        return "mixed_script"
    if has_cyrillic:
        return "translated"
    return "neutral"


def audit(
    catalogues: dict[str, Catalogue],
    overlays: dict[str, dict[str, str]],
    *,
    include_base: bool,
) -> list[AuditRow]:
    rows: list[AuditRow] = []
    for namespace, catalogue in sorted(catalogues.items()):
        if not include_base and namespace in BASE_NAMESPACES:
            continue
        overlay = overlays.get(namespace, {})
        for key, english in sorted(catalogue.english.items()):
            if not PONDER_KEY_RE.search(key):
                continue
            russian = overlay.get(key, catalogue.russian.get(key))
            rows.append(
                AuditRow(
                    namespace=namespace,
                    key=key,
                    status=classify(english, russian),
                    english_value=english,
                    russian_value="" if russian is None else russian,
                    source_jars="; ".join(sorted(catalogue.sources)),
                    placeholder_match=(
                        russian is None or placeholders(english) == placeholders(russian)
                    ),
                )
            )
    return rows


def write_csv(path: Path, rows: list[AuditRow]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.writer(stream)
        writer.writerow(
            [
                "namespace",
                "status",
                "key",
                "english_value",
                "russian_value",
                "placeholder_match",
                "source_jars",
            ]
        )
        for row in rows:
            writer.writerow(
                [
                    row.namespace,
                    row.status,
                    row.key,
                    row.english_value,
                    row.russian_value,
                    str(row.placeholder_match).lower(),
                    row.source_jars,
                ]
            )


def write_report(
    path: Path,
    rows: list[AuditRow],
    *,
    jar_count: int,
    errors: list[str],
    conflicts: int,
) -> None:
    by_namespace: dict[str, list[AuditRow]] = defaultdict(list)
    for row in rows:
        by_namespace[row.namespace].append(row)

    status_order = [
        "missing",
        "identical_english",
        "latin_only",
        "mixed_script",
        "translated",
        "neutral",
    ]
    total_by_status = {
        status: sum(row.status == status for row in rows) for status in status_order
    }
    placeholder_mismatches = sum(not row.placeholder_match for row in rows)

    lines = [
        "# Create add-on Ponder audit",
        "",
        "Exact audit of installed Create add-on JAR catalogues, nested JARs and the",
        "effective Russian values after applying `localization/ru-pack` overlays.",
        "Base Create is excluded and audited separately in `CREATE_6_0_10.md`.",
        "",
        "## Totals",
        "",
        f"- top-level JARs inspected: **{jar_count}**",
        f"- add-on namespaces with Ponder keys: **{len(by_namespace)}**",
        f"- Ponder keys: **{len(rows)}**",
        f"- missing effective Russian values: **{total_by_status['missing']}**",
        f"- values identical to English: **{total_by_status['identical_english']}**",
        f"- other Latin-only values: **{total_by_status['latin_only']}**",
        f"- mixed Cyrillic/Latin values requiring editorial review: **{total_by_status['mixed_script']}**",
        f"- placeholder mismatches: **{placeholder_mismatches}**",
        f"- conflicting duplicate catalogue keys: **{conflicts}**",
        f"- archive/JSON errors: **{len(errors)}**",
        "",
        "`Identical` and `Latin-only` are review candidates rather than an automatic",
        "claim that every value is wrong: abbreviations and product names may be valid.",
        "Mixed-script values are listed because earlier machine-assisted overlays can",
        "contain untranslated fragments even when the key itself is present.",
        "",
        "## Per namespace",
        "",
        "| Namespace | Ponder | Missing | Identical EN | Latin only | Mixed | Cyrillic | Neutral |",
        "|---|---:|---:|---:|---:|---:|---:|---:|",
    ]
    for namespace, namespace_rows in sorted(by_namespace.items()):
        counts = {
            status: sum(row.status == status for row in namespace_rows)
            for status in status_order
        }
        lines.append(
            f"| `{namespace}` | {len(namespace_rows)} | {counts['missing']} | "
            f"{counts['identical_english']} | {counts['latin_only']} | "
            f"{counts['mixed_script']} | {counts['translated']} | {counts['neutral']} |"
        )

    lines.extend(
        [
            "",
            "## Interpretation",
            "",
            "Namespaces with missing or English-identical scene text are the first",
            "translation queue. Mixed-script values are the second queue and must be",
            "reviewed manually rather than accepted as translated merely because they",
            "contain Cyrillic characters. Full row-level evidence is in",
            "`CREATE_ADDON_PONDER.csv`.",
        ]
    )
    if errors:
        lines.extend(["", "## Errors", ""])
        lines.extend(f"- {error}" for error in errors)

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jars-dir", required=True, type=Path)
    parser.add_argument("--resource-pack", required=True, type=Path)
    parser.add_argument("--csv", type=Path)
    parser.add_argument("--report", type=Path)
    parser.add_argument("--include-base", action="store_true")
    args = parser.parse_args()

    catalogues, jar_count, errors = read_catalogues(args.jars_dir)
    overlays = load_overlays(args.resource_pack)
    rows = audit(catalogues, overlays, include_base=args.include_base)
    conflicts = sum(
        PONDER_KEY_RE.search(key) is not None
        for namespace, catalogue in catalogues.items()
        if args.include_base or namespace not in BASE_NAMESPACES
        for key in catalogue.conflicts
    )

    if args.csv:
        write_csv(args.csv, rows)
    if args.report:
        write_report(
            args.report,
            rows,
            jar_count=jar_count,
            errors=errors,
            conflicts=conflicts,
        )

    by_status = {
        status: sum(row.status == status for row in rows)
        for status in (
            "missing",
            "identical_english",
            "latin_only",
            "mixed_script",
            "translated",
            "neutral",
        )
    }
    summary = {
        "top_level_jars": jar_count,
        "ponder_namespaces": len({row.namespace for row in rows}),
        "ponder_keys": len(rows),
        **by_status,
        "placeholder_mismatches": sum(not row.placeholder_match for row in rows),
        "catalogue_conflicts": conflicts,
        "errors": len(errors),
    }
    print(json.dumps(summary, ensure_ascii=False, sort_keys=True))
    return 1 if errors else 0


if __name__ == "__main__":
    raise SystemExit(main())
