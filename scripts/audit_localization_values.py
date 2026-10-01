#!/usr/bin/env python3
"""Audit Russian language values, not just localization key coverage.

The CSV is deliberately a review queue: Latin text can be a valid brand,
formula, unit or key hint, so this script never rewrites translations.
Optional exact mod JARs add key-coverage and printf-placeholder validation.
"""

from __future__ import annotations

import argparse
import csv
import hashlib
import json
import re
from collections import Counter, defaultdict
from dataclasses import dataclass
from pathlib import Path
from zipfile import ZipFile

CYRILLIC_RE = re.compile(r"[А-Яа-яЁё]")
LATIN_RE = re.compile(r"[A-Za-z]")
LATIN_FRAGMENT_RE = re.compile(r"[A-Za-z][A-Za-z0-9_+'’./:-]*")
PRINTF_RE = re.compile(
    r"%(?:\d+\$)?[-#+ 0,(<]*\d*(?:\.\d+)?(?:[tT])?[a-zA-Z%]"
)
BRACE_RE = re.compile(r"\{(?:\d+|[A-Za-z_][A-Za-z0-9_.-]*)(?::[^{}]+)?\}")
FORMAT_CODE_RE = re.compile(r"§[0-9A-FK-ORa-fk-or]")
URL_RE = re.compile(r"(?:https?://|www\.)\S+")

HIGH_PRIORITY_PREFIXES = (
    "advancement", "button", "config", "container", "death", "effect", "entity",
    "error", "gui", "item.", "jei", "key.", "menu", "message", "screen",
    "subtitle", "text", "tooltip",
)


@dataclass(frozen=True)
class QueueRow:
    namespace: str
    key: str
    classification: str
    priority: str
    latin_fragments: str
    value: str


def visible_text(value: str) -> str:
    """Remove syntax that should not make a Russian value look Latin."""
    value = URL_RE.sub(" ", value)
    value = PRINTF_RE.sub(" ", value)
    value = BRACE_RE.sub(" ", value)
    value = FORMAT_CODE_RE.sub(" ", value)
    value = value.replace("\\n", " ")
    return value


def classify(value: str) -> tuple[str | None, list[str]]:
    visible = visible_text(value)
    has_latin = bool(LATIN_RE.search(visible))
    if not has_latin:
        return None, []
    kind = "mixed" if CYRILLIC_RE.search(visible) else "latin_only"
    fragments = list(dict.fromkeys(LATIN_FRAGMENT_RE.findall(visible)))
    return kind, fragments


def placeholders(value: str) -> Counter[str]:
    """Return placeholders while treating %% as an escaped percent sign."""
    found = [token for token in PRINTF_RE.findall(value) if token != "%%"]
    found.extend(BRACE_RE.findall(value))
    return Counter(found)


def read_json(path: Path) -> dict[str, str]:
    data = json.loads(path.read_text(encoding="utf-8"))
    if not isinstance(data, dict) or not all(isinstance(k, str) and isinstance(v, str) for k, v in data.items()):
        raise ValueError(f"{path}: language file must be a string-to-string JSON object")
    return data


def load_jar_catalogs(jar: Path) -> dict[str, dict[str, str]]:
    result: dict[str, dict[str, str]] = {}
    with ZipFile(jar) as archive:
        for name in archive.namelist():
            match = re.fullmatch(r"assets/([^/]+)/lang/en_us\.json", name)
            if not match:
                continue
            data = json.loads(archive.read(name))
            if isinstance(data, dict) and all(isinstance(k, str) and isinstance(v, str) for k, v in data.items()):
                result[match.group(1)] = data
    return result


def priority_for(key: str, kind: str) -> str:
    lowered = key.lower()
    if kind == "latin_only" and lowered.startswith(HIGH_PRIORITY_PREFIXES):
        return "high"
    if lowered.startswith(("container", "gui", "menu", "screen", "text", "tooltip")):
        return "high"
    return "normal"


def write_csv(path: Path, rows: list[QueueRow], review: dict[str, dict[str, str]]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", encoding="utf-8", newline="") as stream:
        writer = csv.writer(stream, lineterminator="\n")
        writer.writerow(("namespace", "key", "classification", "priority", "latin_fragments", "value", "status", "review_note"))
        for row in rows:
            decision = review.get(f"{row.namespace}|{row.key}", {})
            writer.writerow((
                row.namespace, row.key, row.classification, row.priority, row.latin_fragments, row.value,
                decision.get("status", "pending"), decision.get("note", ""),
            ))


def write_summary(
    path: Path,
    ru_counts: dict[str, int],
    class_counts: dict[str, Counter[str]],
    references: dict[str, dict[str, str]],
    russian: dict[str, dict[str, str]],
    placeholder_mismatches: list[tuple[str, str, Counter[str], Counter[str]]],
    jars: list[Path],
    review: dict[str, dict[str, str]],
) -> None:
    total_values = sum(ru_counts.values())
    total_mixed = sum(c["mixed"] for c in class_counts.values())
    total_latin = sum(c["latin_only"] for c in class_counts.values())
    lines = [
        "# Аудит латиницы в русской локализации",
        "",
        "> Это очередь ручной проверки, а не список строк для автоматической замены. Бренды, формулы,",
        "> аббревиатуры и единицы измерения могут быть оставлены на латинице намеренно.",
        "",
        "## Итог",
        "",
        f"- Проверено значений: **{total_values:,}**".replace(",", " "),
        f"- Смешанных значений: **{total_mixed:,}**".replace(",", " "),
        f"- Значений только с латиницей: **{total_latin:,}**".replace(",", " "),
        f"- Уже проверено вручную и принято: **{sum(item['status'] == 'accepted' for item in review.values())}**",
        f"- Несовпадений placeholders с подключёнными exact JAR: **{len(placeholder_mismatches)}**",
        "",
        "## По пространствам имён",
        "",
        "| Namespace | Всего | Mixed | Latin-only |",
        "|---|---:|---:|---:|",
    ]
    for namespace in sorted(ru_counts, key=lambda ns: (-(class_counts[ns]["mixed"] + class_counts[ns]["latin_only"]), ns)):
        counts = class_counts[namespace]
        lines.append(f"| `{namespace}` | {ru_counts[namespace]} | {counts['mixed']} | {counts['latin_only']} |")

    lines.extend(("", "## Exact-JAR проверки", ""))
    if not references:
        lines.append("Exact JAR не переданы; выполнен только анализ значений.")
    else:
        lines.extend(("| Namespace | EN keys | RU keys | Missing | Extra | Placeholder mismatches |", "|---|---:|---:|---:|---:|---:|"))
        mismatch_by_ns = Counter(ns for ns, *_ in placeholder_mismatches)
        for namespace in sorted(references):
            en = references[namespace]
            ru = russian.get(namespace, {})
            lines.append(
                f"| `{namespace}` | {len(en)} | {len(ru)} | {len(en.keys() - ru.keys())} | "
                f"{len(ru.keys() - en.keys())} | {mismatch_by_ns[namespace]} |"
            )

    if placeholder_mismatches:
        lines.extend(("", "### Несовпадения placeholders", ""))
        for namespace, key, en_tokens, ru_tokens in placeholder_mismatches:
            lines.append(f"- `{namespace}` / `{key}`: EN `{dict(en_tokens)}`, RU `{dict(ru_tokens)}`")

    if jars:
        lines.extend(("", "### Проверенные JAR", ""))
        for jar in jars:
            lines.append(f"- `{jar.name}` — SHA-1 `{hashlib.sha1(jar.read_bytes()).hexdigest()}`")

    lines.extend((
        "",
        "## Воспроизведение",
        "",
        "```bash",
        "python3 scripts/audit_localization_values.py --jar path/to/mod.jar [--jar path/to/another.jar]",
        "```",
        "",
        "Подробная очередь находится в `LATIN_SCRIPT_QUEUE.csv`. Placeholders и управляющие коды",
        "исключаются из определения латиницы, но сохраняются в полном тексте строки.",
        "",
    ))
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines), encoding="utf-8")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--assets", type=Path, default=Path("localization/ru-pack/assets"))
    parser.add_argument("--jar", action="append", type=Path, default=[])
    parser.add_argument("--csv", type=Path, default=Path("docs/localization-audit/LATIN_SCRIPT_QUEUE.csv"))
    parser.add_argument("--summary", type=Path, default=Path("docs/localization-audit/LATIN_SCRIPT_AUDIT.md"))
    parser.add_argument("--review", type=Path, default=Path("docs/localization-audit/LATIN_SCRIPT_REVIEW.json"))
    args = parser.parse_args()

    review: dict[str, dict[str, str]] = {}
    if args.review.is_file():
        raw_review = json.loads(args.review.read_text(encoding="utf-8"))
        if not isinstance(raw_review, dict):
            raise SystemExit(f"{args.review}: review data must be a JSON object")
        for identity, decision in raw_review.items():
            if not isinstance(identity, str) or not isinstance(decision, dict):
                raise SystemExit(f"{args.review}: invalid decision for {identity!r}")
            if decision.get("status") not in {"accepted", "fix", "pending"}:
                raise SystemExit(f"{args.review}: invalid status for {identity!r}")
            review[identity] = {"status": decision["status"], "note": str(decision.get("note", ""))}

    russian: dict[str, dict[str, str]] = {}
    rows: list[QueueRow] = []
    class_counts: dict[str, Counter[str]] = defaultdict(Counter)
    ru_counts: dict[str, int] = {}

    for path in sorted(args.assets.glob("*/lang/ru_ru.json")):
        namespace = path.parts[-3]
        data = read_json(path)
        russian[namespace] = data
        ru_counts[namespace] = len(data)
        for key, value in data.items():
            kind, fragments = classify(value)
            if not kind:
                continue
            class_counts[namespace][kind] += 1
            rows.append(QueueRow(namespace, key, kind, priority_for(key, kind), "; ".join(fragments), value))

    rows.sort(key=lambda row: ({"high": 0, "normal": 1}[row.priority], {"latin_only": 0, "mixed": 1}[row.classification], row.namespace, row.key))
    row_ids = {f"{row.namespace}|{row.key}" for row in rows}
    stale_review = sorted(review.keys() - row_ids)
    if stale_review:
        raise SystemExit(f"{args.review}: stale decisions not present in the current queue: {stale_review}")
    write_csv(args.csv, rows, review)

    references: dict[str, dict[str, str]] = {}
    for jar in args.jar:
        for namespace, data in load_jar_catalogs(jar).items():
            if namespace in references and references[namespace] != data:
                raise SystemExit(f"Conflicting en_us catalogs for namespace {namespace!r}")
            references[namespace] = data

    mismatches: list[tuple[str, str, Counter[str], Counter[str]]] = []
    for namespace, english in references.items():
        ru = russian.get(namespace, {})
        for key in english.keys() & ru.keys():
            en_tokens, ru_tokens = placeholders(english[key]), placeholders(ru[key])
            if en_tokens != ru_tokens:
                mismatches.append((namespace, key, en_tokens, ru_tokens))
    mismatches.sort(key=lambda row: (row[0], row[1]))

    write_summary(args.summary, ru_counts, class_counts, references, russian, mismatches, args.jar, review)
    print(f"Scanned {sum(ru_counts.values())} values in {len(russian)} namespaces")
    print(f"Queued {len(rows)} values ({sum(c['mixed'] for c in class_counts.values())} mixed, "
          f"{sum(c['latin_only'] for c in class_counts.values())} Latin-only)")
    print(f"Exact references: {len(references)} namespaces; placeholder mismatches: {len(mismatches)}")
    print(f"CSV: {args.csv}\nSummary: {args.summary}")
    if mismatches:
        raise SystemExit(2)


if __name__ == "__main__":
    main()
