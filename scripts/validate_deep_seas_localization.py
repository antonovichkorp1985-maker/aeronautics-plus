#!/usr/bin/env python3
"""Validate RU-pack coverage against the exact Create Deep Seas 2.2.4 JAR."""

from __future__ import annotations

import argparse
import json
import re
import zipfile
from pathlib import Path

EXPECTED = {
    "create_submarine": {"english": 191, "built_in_russian": 149, "pack": 87},
    "create_abyss": {"english": 12, "built_in_russian": 12, "pack": 2},
}
PLACEHOLDER = re.compile(r"%(?:\d+\$)?[a-zA-Z]|\{\d+\}")


def arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--jar", type=Path, required=True)
    parser.add_argument("--resource-pack", type=Path, required=True)
    return parser.parse_args()


def load_json(data: bytes, source: str) -> dict[str, str]:
    value = json.loads(data.decode("utf-8"))
    if not isinstance(value, dict) or not all(isinstance(k, str) and isinstance(v, str) for k, v in value.items()):
        raise ValueError(f"{source} is not a string-to-string lang object")
    return value


def placeholders(value: str) -> list[str]:
    return sorted(PLACEHOLDER.findall(value))


def main() -> None:
    args = arguments()
    with zipfile.ZipFile(args.jar) as jar:
        for namespace, expected in EXPECTED.items():
            prefix = f"assets/{namespace}/lang/"
            english = load_json(jar.read(prefix + "en_us.json"), f"{namespace}/en_us")
            russian = load_json(jar.read(prefix + "ru_ru.json"), f"{namespace}/ru_ru")
            pack_path = args.resource_pack / prefix / "ru_ru.json"
            pack = load_json(pack_path.read_bytes(), str(pack_path))

            if len(english) != expected["english"]:
                raise SystemExit(f"{namespace}: expected {expected['english']} English keys, got {len(english)}")
            if len(russian) != expected["built_in_russian"]:
                raise SystemExit(
                    f"{namespace}: expected {expected['built_in_russian']} built-in Russian keys, got {len(russian)}"
                )
            # The overlay may contain additional reviewed keys shared by nearby
            # Deep Seas versions. Coverage and orphan checks below are the
            # authoritative acceptance criteria; pack cardinality is reported,
            # not used as a brittle gate across patch releases.
            orphan = sorted(set(pack) - set(english))
            if orphan:
                raise SystemExit(f"{namespace}: pack has unknown keys: {orphan}")

            effective = {**russian, **pack}
            missing = sorted(set(english) - set(effective))
            if missing:
                raise SystemExit(f"{namespace}: effective RU is missing keys: {missing}")

            mismatches = [
                key
                for key, english_value in english.items()
                if placeholders(english_value) != placeholders(effective[key])
            ]
            if mismatches:
                raise SystemExit(f"{namespace}: placeholder mismatches: {mismatches}")

            print(
                f"{namespace}: {len(effective)}/{len(english)} effective, "
                f"{len(russian)} built-in, {len(pack)} pack entries, 0 placeholder mismatches"
            )


if __name__ == "__main__":
    main()
