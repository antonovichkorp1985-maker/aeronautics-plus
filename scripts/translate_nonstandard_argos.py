#!/usr/bin/env python3
"""Translate extracted non-lang resources into RU-pack paths with Argos.

Input files are exact-JAR English resources selected by the nonstandard audit.
Patchouli JSON is translated field-by-field; manuals and localized text files
are translated line-by-line while markup, resource IDs and placeholders remain
byte-for-byte intact.
"""

from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
from typing import Any

import argostranslate.translate

from machine_translate_argos_ru import install_model

TEXT_FIELDS = {
    "name",
    "title",
    "description",
    "text",
    "subtitle",
    "tooltip",
    "landing_text",
}
PROTECTED_RE = re.compile(
    r"(?:"
    r"%(?:(?:\d+)\$)?[-#+0,(<]*\d*(?:\.\d+)?[a-zA-Z%]"
    r"|\{\d+\}"
    r"|§."
    r"|\$\([^)]*\)"
    r"|<[^>\n]+>"
    r"|`[^`\n]+`"
    r"|https?://[^\s]+"
    r"|(?:\.\.?/)?[A-Za-z0-9_./-]+\.md(?:#[A-Za-z0-9_.-]+)?"
    r"|\b[a-z0-9_.-]+:[a-z0-9_./-]+\b"
    r")"
)
LATIN_WORD_RE = re.compile(r"[A-Za-z]{2,}")


def translate_spans(translation: Any, text: str) -> str:
    if not LATIN_WORD_RE.search(text):
        return text
    result: list[str] = []
    cursor = 0
    for match in PROTECTED_RE.finditer(text):
        segment = text[cursor : match.start()]
        result.append(translation.translate(segment) if LATIN_WORD_RE.search(segment) else segment)
        result.append(match.group(0))
        cursor = match.end()
    segment = text[cursor:]
    result.append(translation.translate(segment) if LATIN_WORD_RE.search(segment) else segment)
    return "".join(result)


def translate_json(value: Any, translation: Any, parent_key: str | None = None) -> Any:
    if isinstance(value, dict):
        return {
            key: translate_json(child, translation, key)
            for key, child in value.items()
        }
    if isinstance(value, list):
        return [translate_json(child, translation, parent_key) for child in value]
    if isinstance(value, str) and parent_key in TEXT_FIELDS:
        return translate_spans(translation, value)
    return value


def translate_plain(text: str, translation: Any, suffix: str) -> str:
    lines: list[str] = []
    in_frontmatter = False
    in_code_fence = False
    for line in text.splitlines(keepends=True):
        ending = "\n" if line.endswith("\n") else ""
        body = line[:-1] if ending else line
        if body.endswith("\r"):
            body = body[:-1]
            ending = "\r" + ending

        if suffix in {".md", ".mdx"} and body.strip() == "---":
            in_frontmatter = not in_frontmatter
            lines.append(body + ending)
            continue
        if suffix in {".md", ".mdx"} and body.lstrip().startswith("```"):
            in_code_fence = not in_code_fence
            lines.append(body + ending)
            continue
        if in_code_fence:
            lines.append(body + ending)
            continue
        if in_frontmatter:
            match = re.fullmatch(r"(\s*)([A-Za-z_][A-Za-z0-9_-]*):(\s*)(.*)", body)
            if match and match.group(2) == "title":
                body = (
                    match.group(1)
                    + match.group(2)
                    + ":"
                    + match.group(3)
                    + translate_spans(translation, match.group(4))
                )
            lines.append(body + ending)
            continue

        if suffix == ".lang" and "=" in body and not body.lstrip().startswith("#"):
            key, value = body.split("=", 1)
            body = key + "=" + translate_spans(translation, value)
        elif LATIN_WORD_RE.search(body):
            body = translate_spans(translation, body)
        lines.append(body + ending)
    return "".join(lines)


def russian_path(relative: Path) -> Path:
    value = relative.as_posix()
    value, count = re.subn(r"(?i)(^|/)en_us(?=/|\.)", r"\1ru_ru", value, count=1)
    if count:
        return Path(value)
    if "/ae2guide/" in value.lower():
        prefix, tail = re.split(r"(?i)/ae2guide/", value, maxsplit=1)
        return Path(prefix + "/ae2guide/_ru_ru/" + tail)
    raise ValueError(f"cannot determine Russian path for {relative}")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--input", required=True, type=Path)
    parser.add_argument("--resource-pack", required=True, type=Path)
    args = parser.parse_args()

    install_model()
    installed = argostranslate.translate.get_installed_languages()
    english = next(language for language in installed if language.code == "en")
    russian = next(language for language in installed if language.code == "ru")
    translation = english.get_translation(russian)

    files = sorted(path for path in args.input.rglob("*") if path.is_file())
    for index, source in enumerate(files, start=1):
        relative = source.relative_to(args.input)
        target = args.resource_pack / russian_path(relative)
        target.parent.mkdir(parents=True, exist_ok=True)
        raw = source.read_text(encoding="utf-8-sig")
        if source.suffix.lower() == ".json":
            data = json.loads(raw)
            translated = json.dumps(
                translate_json(data, translation),
                ensure_ascii=False,
                indent=2,
            ) + "\n"
        else:
            translated = translate_plain(raw, translation, source.suffix.lower())
        target.write_text(translated, encoding="utf-8")
        print(f"{index}/{len(files)} {relative} -> {target}", flush=True)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
