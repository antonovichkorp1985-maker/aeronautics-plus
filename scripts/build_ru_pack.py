#!/usr/bin/env python3
"""Validate and build the Aeronautics Plus Russian resource-pack ZIP."""

from __future__ import annotations

import argparse
import hashlib
import json
import zipfile
from pathlib import Path

FIXED_ZIP_TIMESTAMP = (2026, 9, 30, 0, 0, 0)


def validate(source: Path) -> tuple[int, int]:
    metadata = source / "pack.mcmeta"
    if not metadata.is_file():
        raise SystemExit(f"missing {metadata}")
    parsed_metadata = json.loads(metadata.read_text(encoding="utf-8"))
    if not isinstance(parsed_metadata.get("pack"), dict):
        raise SystemExit("pack.mcmeta has no pack object")

    files = 0
    keys = 0
    for path in sorted(source.rglob("*")):
        if not path.is_file():
            continue
        files += 1
        if path.suffix.lower() != ".json":
            continue
        rel = path.relative_to(source).as_posix()
        if not rel.endswith("/lang/ru_ru.json"):
            raise SystemExit(f"unexpected JSON outside ru_ru catalogue: {rel}")
        data = json.loads(path.read_text(encoding="utf-8-sig"))
        if not isinstance(data, dict):
            raise SystemExit(f"catalogue is not an object: {rel}")
        non_strings = [key for key, value in data.items() if not isinstance(key, str) or not isinstance(value, str)]
        if non_strings:
            raise SystemExit(f"non-string language entries in {rel}: {non_strings[:5]}")
        keys += len(data)
    return files, keys


def build(source: Path, output: Path) -> None:
    output.parent.mkdir(parents=True, exist_ok=True)
    if output.exists():
        output.unlink()
    with zipfile.ZipFile(output, "w", compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
        for path in sorted(source.rglob("*")):
            if not path.is_file():
                continue
            rel = path.relative_to(source).as_posix()
            info = zipfile.ZipInfo(rel, FIXED_ZIP_TIMESTAMP)
            info.compress_type = zipfile.ZIP_DEFLATED
            info.external_attr = 0o100644 << 16
            archive.writestr(info, path.read_bytes(), compress_type=zipfile.ZIP_DEFLATED, compresslevel=9)


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--source", type=Path, default=Path("localization/ru-pack"))
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()

    files, keys = validate(args.source)
    build(args.source, args.output)
    digest = hashlib.sha256(args.output.read_bytes()).hexdigest()
    with zipfile.ZipFile(args.output) as archive:
        bad = archive.testzip()
        if bad:
            raise SystemExit(f"corrupt ZIP member: {bad}")
    print(f"Built {args.output} ({files} files, {keys} ru_ru entries)")
    print(f"SHA-256 {digest}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
