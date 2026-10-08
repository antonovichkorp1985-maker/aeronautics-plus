#!/usr/bin/env python3
"""Apply reviewed translation-memory and Argos drafts to the RU resource pack.

The all-mod audit is the source of keys. Technical-only strings and official
mod names are copied verbatim when a missing key still needs to be supplied.
Existing hand-written overlay values win over machine output unless the audit
explicitly marks them as identical English or Latin-only.
"""

from __future__ import annotations

import argparse
import csv
import json
import re
from collections import defaultdict
from pathlib import Path

from prepare_machine_translation import OFFICIAL_NAMES, TECHNICAL_ONLY_RE, needs_translation

MOD_NAME_KEY_RE = re.compile(r"(?:^|\.)(?:mod_name|modname)(?:\.|$)")
CYRILLIC_RE = re.compile(r"[А-Яа-яЁё]")
PLACEHOLDER_RE = re.compile(
    r"%(?:(?:\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]"
    r"|\{\d+\}|§.|\$\([^)]*\)"
)
BRAND_FIXES = {
    "Mekanism": ("Механизм", "Меканизм"),
    "Immersive Engineering": ("Иммерсивная инженерия",),
    "PneumaticCraft": ("ПневматикКрафт", "Пневматиккрафт"),
    "Productive Bees": ("Продуктивные пчёлы",),
    "Applied Energistics 2": ("Прикладная энергетика 2",),
    "Xaero": ("Ксаэро",),
}


def placeholders(value: str) -> list[str]:
    return PLACEHOLDER_RE.findall(value)


def preserve_brands(english: str, russian: str) -> str:
    result = russian
    for brand, variants in BRAND_FIXES.items():
        if brand not in english:
            continue
        for variant in variants:
            result = re.sub(re.escape(variant), brand, result, flags=re.IGNORECASE)
    return result


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--audit-csv", required=True, type=Path)
    parser.add_argument("--machine", required=True, type=Path)
    parser.add_argument("--translation-memory", required=True, type=Path)
    parser.add_argument("--resource-pack", required=True, type=Path)
    args = parser.parse_args()

    machine_payload = json.loads(args.machine.read_text(encoding="utf-8"))
    machine = {
        item["english"]: item["russian"]
        for item in machine_payload["translations"]
    }
    memory = json.loads(args.translation_memory.read_text(encoding="utf-8"))
    with args.audit_csv.open(encoding="utf-8", newline="") as stream:
        rows = list(csv.DictReader(stream))

    existing_catalogues: dict[str, dict[str, str]] = {}
    for namespace in {row["namespace"] for row in rows}:
        path = args.resource_pack / "assets" / namespace / "lang" / "ru_ru.json"
        existing_catalogues[namespace] = (
            json.loads(path.read_text(encoding="utf-8")) if path.is_file() else {}
        )

    updates: dict[str, dict[str, str]] = defaultdict(dict)
    counts = defaultdict(int)
    for row in rows:
        if not needs_translation(row):
            continue
        if (
            row["status"] == "missing"
            and row["key"] in existing_catalogues[row["namespace"]]
        ):
            # The audit predates a newer hand-written correction (for example,
            # the current Create: Deep Seas Ponder batch). Never overwrite it.
            counts["preserved_new_manual"] += 1
            continue
        english = row["english_value"]
        if not english.strip():
            updates[row["namespace"]][row["key"]] = ""
            counts["empty_service_key"] += 1
            continue
        official = english.strip() in OFFICIAL_NAMES or bool(MOD_NAME_KEY_RE.search(row["key"]))
        technical = bool(TECHNICAL_ONLY_RE.fullmatch(english.strip()))
        if official or technical:
            if row["status"] == "missing":
                russian = english
                counts["verbatim_missing"] += 1
            else:
                counts["skipped_existing_verbatim"] += 1
                continue
        elif english in memory:
            russian = memory[english]
            counts["translation_memory"] += 1
        else:
            russian = machine.get(english, "")
            if not russian:
                raise SystemExit(f"machine translation missing for {english!r}")
            counts["machine"] += 1
        russian = preserve_brands(english, russian)
        if placeholders(english) != placeholders(russian):
            raise SystemExit(
                f"placeholder mismatch for {row['namespace']}:{row['key']}: "
                f"{placeholders(english)!r} != {placeholders(russian)!r}"
            )
        if not CYRILLIC_RE.search(russian) and not (official or technical):
            counts["non_cyrillic_draft"] += 1
        updates[row["namespace"]][row["key"]] = russian

    for namespace, values in sorted(updates.items()):
        path = args.resource_pack / "assets" / namespace / "lang" / "ru_ru.json"
        path.parent.mkdir(parents=True, exist_ok=True)
        current = json.loads(path.read_text(encoding="utf-8")) if path.is_file() else {}
        current.update(values)
        path.write_text(
            json.dumps(current, ensure_ascii=False, indent=2) + "\n",
            encoding="utf-8",
        )
    counts["namespaces"] = len(updates)
    counts["keys"] = sum(len(values) for values in updates.values())
    print(json.dumps(dict(sorted(counts.items())), ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
