#!/usr/bin/env python3
"""Validate the Sable 2.0.5 Russian overlay against an exact release JAR."""

from __future__ import annotations

import argparse
import json
import re
import zipfile
from pathlib import Path

PRINTF_RE = re.compile(r"%(?:(\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]")
BRACE_RE = re.compile(r"\{\d+\}")


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


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", required=True, type=Path)
    parser.add_argument("--resource-pack", required=True, type=Path)
    args = parser.parse_args()

    member = "assets/sable/lang/en_us.json"
    with zipfile.ZipFile(args.jar) as archive:
        try:
            english = load_object(archive.read(member), f"{args.jar}!/{member}")
        except KeyError as error:
            raise SystemExit(f"missing {member} in {args.jar}") from error

    overlay_path = args.resource_pack / "assets/sable/lang/ru_ru.json"
    if not overlay_path.is_file():
        raise SystemExit(f"missing {overlay_path}")
    russian = load_object(overlay_path.read_text(encoding="utf-8-sig"), str(overlay_path))

    missing = sorted(set(english) - set(russian))
    orphan = sorted(set(russian) - set(english))
    mismatches = sorted(
        key
        for key in set(english) & set(russian)
        if placeholders(english[key]) != placeholders(russian[key])
    )

    if missing or orphan or mismatches:
        if missing:
            print(f"missing Sable keys ({len(missing)}): {missing[:10]}")
        if orphan:
            print(f"orphan Sable keys ({len(orphan)}): {orphan[:10]}")
        if mismatches:
            print(f"placeholder mismatches ({len(mismatches)}): {mismatches[:10]}")
        return 1

    print(
        f"sable: {len(russian)}/{len(english)} effective, "
        "0 built-in, 0 placeholder mismatches"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
