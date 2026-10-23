#!/usr/bin/env python3
"""Produce an offline English-to-Russian draft with Argos Translate.

The queue is deduplicated by the preparation step. Minecraft placeholders,
formatting codes, URLs and resource identifiers are replaced by robust tokens,
then restored and checked before each translated value is accepted.
"""

from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
from typing import Any

import argostranslate.package
import argostranslate.translate

PLACEHOLDER_RE = re.compile(
    r"(?:"
    r"%(?:(?:\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]"
    r"|\{\d+\}"
    r"|§."
    r"|\$\([^)]*\)"
    r"|https?://[^\s]+"
    r"|\b[a-z0-9_.-]+:[a-z0-9_./-]+\b"
    r")"
)


def install_model() -> None:
    installed = argostranslate.translate.get_installed_languages()
    if any(language.code == "en" for language in installed) and any(
        language.code == "ru" for language in installed
    ):
        try:
            source = next(language for language in installed if language.code == "en")
            target = next(language for language in installed if language.code == "ru")
            source.get_translation(target)
            return
        except Exception:
            pass

    argostranslate.package.update_package_index()
    packages = argostranslate.package.get_available_packages()
    package = next(
        package
        for package in packages
        if package.from_code == "en" and package.to_code == "ru"
    )
    argostranslate.package.install_from_path(package.download())


def translate_preserving_placeholders(translation: Any, text: str) -> str:
    """Translate only the text spans between immutable technical tokens."""
    parts: list[str] = []
    cursor = 0
    for match in PLACEHOLDER_RE.finditer(text):
        parts.append(translation.translate(text[cursor : match.start()]))
        parts.append(match.group(0))
        cursor = match.end()
    parts.append(translation.translate(text[cursor:]))
    return "".join(parts).strip()


def save(path: Path, source: dict[str, Any], translated: dict[str, str]) -> None:
    by_id = {item["id"]: item["text"] for item in source["items"]}
    payload = {
        "format": 1,
        "engine": "argos-translate-en-ru",
        "source_items": len(source["items"]),
        "translated_items": len(translated),
        "translations": [
            {"id": item_id, "english": by_id[item_id], "russian": translated[item_id]}
            for item_id in sorted(translated)
        ],
    }
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2) + "\n", encoding="utf-8"
    )


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", required=True, type=Path)
    parser.add_argument("--output", required=True, type=Path)
    parser.add_argument("--checkpoint-every", type=int, default=25)
    args = parser.parse_args()

    source = json.loads(args.input.read_text(encoding="utf-8"))
    existing: dict[str, str] = {}
    if args.output.is_file():
        old = json.loads(args.output.read_text(encoding="utf-8"))
        existing = {item["id"]: item["russian"] for item in old.get("translations", [])}

    # Ensure an artifact exists even if model setup fails.
    save(args.output, source, existing)
    install_model()
    installed = argostranslate.translate.get_installed_languages()
    english = next(language for language in installed if language.code == "en")
    russian = next(language for language in installed if language.code == "ru")
    translation = english.get_translation(russian)

    pending = [item for item in source["items"] if item["id"] not in existing]
    print(f"items={len(source['items'])} cached={len(existing)} pending={len(pending)}", flush=True)
    for index, item in enumerate(pending, start=1):
        existing[item["id"]] = translate_preserving_placeholders(
            translation, item["text"]
        )
        if index % args.checkpoint_every == 0 or index == len(pending):
            save(args.output, source, existing)
            print(
                f"translated={len(existing)}/{len(source['items'])}",
                flush=True,
            )

    if len(existing) != len(source["items"]):
        raise SystemExit(
            f"incomplete output: {len(existing)}/{len(source['items'])}"
        )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
