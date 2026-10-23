#!/usr/bin/env python3
"""Validate the Create 6.0.10 Russian overlay, Ponder and descriptions."""

from __future__ import annotations

import argparse
import json
import re
import zipfile
from pathlib import Path

EXPECTED_ENGLISH_KEYS = 3639
EXPECTED_BUILT_IN_RUSSIAN_KEYS = 3621
EXPECTED_PACK_KEYS = 21
EXPECTED_PONDER_KEYS = 1053

PRINTF_RE = re.compile(r"%(?:(\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]")
BRACE_RE = re.compile(r"\{\d+\}")
PONDER_IDENTICAL_ALLOWLIST = {
    "create.ponder.clockwork_bearing.text_3",  # Clock face label 3:00.
    "create.ponder.clockwork_bearing.text_4",  # Clock face label 4:00.
    "create.ponder.mod_name",                  # Product name Create.
}


def placeholders(value: str) -> tuple[str, ...]:
    result = [match.group(0) for match in PRINTF_RE.finditer(value)]
    result.extend(BRACE_RE.findall(value))
    return tuple(sorted(result))


def load_object(raw: bytes | str, source: str) -> dict[str, str]:
    text = raw.decode("utf-8-sig") if isinstance(raw, bytes) else raw
    data = json.loads(text)
    if not isinstance(data, dict) or any(
        not isinstance(key, str) or not isinstance(value, str)
        for key, value in data.items()
    ):
        raise SystemExit(f"{source} is not a string-to-string JSON object")
    return data


def is_ponder_key(key: str) -> bool:
    return key.startswith("create.ponder.") or key == "create.menu.ponder_index"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", required=True, type=Path)
    parser.add_argument("--resource-pack", required=True, type=Path)
    args = parser.parse_args()

    english_member = "assets/create/lang/en_us.json"
    russian_member = "assets/create/lang/ru_ru.json"
    with zipfile.ZipFile(args.jar) as archive:
        try:
            english = load_object(archive.read(english_member), f"{args.jar}!/{english_member}")
            built_in_russian = load_object(
                archive.read(russian_member), f"{args.jar}!/{russian_member}"
            )
        except KeyError as error:
            raise SystemExit(f"missing Create language catalogue in {args.jar}: {error}") from error

    overlay_path = args.resource_pack / russian_member
    if not overlay_path.is_file():
        raise SystemExit(f"missing {overlay_path}")
    overlay = load_object(overlay_path.read_text(encoding="utf-8-sig"), str(overlay_path))

    if len(english) != EXPECTED_ENGLISH_KEYS:
        raise SystemExit(
            f"expected {EXPECTED_ENGLISH_KEYS} Create English keys, got {len(english)}"
        )
    if len(built_in_russian) != EXPECTED_BUILT_IN_RUSSIAN_KEYS:
        raise SystemExit(
            "expected "
            f"{EXPECTED_BUILT_IN_RUSSIAN_KEYS} built-in Russian keys, "
            f"got {len(built_in_russian)}"
        )
    if len(overlay) != EXPECTED_PACK_KEYS:
        raise SystemExit(f"expected {EXPECTED_PACK_KEYS} Create pack keys, got {len(overlay)}")

    orphan = sorted(set(overlay) - set(english))
    effective = {**built_in_russian, **overlay}
    missing = sorted(set(english) - set(effective))
    mismatches = sorted(
        key
        for key, english_value in english.items()
        if key in effective and placeholders(english_value) != placeholders(effective[key])
    )

    ponder_keys = sorted(key for key in english if is_ponder_key(key))
    untranslated_ponder = sorted(
        key
        for key in ponder_keys
        if effective.get(key) == english[key] and key not in PONDER_IDENTICAL_ALLOWLIST
    )

    if orphan or missing or mismatches or len(ponder_keys) != EXPECTED_PONDER_KEYS or untranslated_ponder:
        if orphan:
            print(f"orphan Create overlay keys ({len(orphan)}): {orphan[:10]}")
        if missing:
            print(f"missing effective Create keys ({len(missing)}): {missing[:10]}")
        if mismatches:
            print(f"placeholder mismatches ({len(mismatches)}): {mismatches[:10]}")
        if len(ponder_keys) != EXPECTED_PONDER_KEYS:
            print(
                f"expected {EXPECTED_PONDER_KEYS} Ponder keys, got {len(ponder_keys)}"
            )
        if untranslated_ponder:
            print(
                "Ponder values still identical to English "
                f"({len(untranslated_ponder)}): {untranslated_ponder[:10]}"
            )
        return 1

    description_keys = [key for key in english if ".description" in key or ".desc" in key]
    covered_keys = sum(key in effective for key in english)
    print(
        f"create: {covered_keys}/{len(english)} effective, "
        f"{len(built_in_russian)} built-in, {len(overlay)} pack entries; "
        f"Ponder {len(ponder_keys)}/{len(ponder_keys)}, "
        f"descriptions {len(description_keys)}/{len(description_keys)}, "
        "0 placeholder mismatches"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
