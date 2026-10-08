#!/usr/bin/env python3
"""Translate a prepared English queue to Russian through Google web translate.

The script batches values inside stable XML wrappers, protects Minecraft/printf
placeholders and writes its cache after every successful request.  It is meant
for CI runners with network access; its output is a draft requiring validation
and editorial review, not an automatically releasable localization.
"""

from __future__ import annotations

import argparse
import html
import json
import random
import re
import time
from pathlib import Path
from typing import Any

import requests

ENDPOINT = "https://translate.googleapis.com/translate_a/single"
WRAPPER_RE = re.compile(r'<x\s+id=["\'](\d{6})["\']\s*>(.*?)</x\s*>', re.DOTALL | re.IGNORECASE)
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
PH_RE = re.compile(
    r"<ph\s+id=[\"'](\d+)[\"']\s*(?:/\s*>|>\s*</ph\s*>)",
    re.IGNORECASE,
)


def protect(text: str) -> tuple[str, list[str]]:
    placeholders: list[str] = []

    def replace(match: re.Match[str]) -> str:
        index = len(placeholders)
        placeholders.append(match.group(0))
        return f"@@PH{index:04d}@@"

    protected = PLACEHOLDER_RE.sub(replace, text)
    protected = html.escape(protected, quote=False)
    for index in range(len(placeholders)):
        protected = protected.replace(
            f"@@PH{index:04d}@@", f'<ph id="{index}"></ph>'
        )
    return protected, placeholders


def restore(text: str, placeholders: list[str]) -> str:
    seen: set[int] = set()

    def replace(match: re.Match[str]) -> str:
        index = int(match.group(1))
        if index >= len(placeholders):
            raise ValueError(f"unknown placeholder id {index}")
        seen.add(index)
        return placeholders[index]

    result = PH_RE.sub(replace, text)
    expected = set(range(len(placeholders)))
    if seen != expected:
        raise ValueError(f"placeholder loss: expected {sorted(expected)}, got {sorted(seen)}")
    return html.unescape(result)


def make_payload(items: list[dict[str, str]]) -> tuple[str, dict[str, list[str]]]:
    protected: dict[str, list[str]] = {}
    lines: list[str] = []
    for item in items:
        text, placeholders = protect(item["text"])
        protected[item["id"]] = placeholders
        lines.append(f'<x id="{item["id"]}">{text}</x>')
    return "\n".join(lines), protected


def parse_response(
    translated: str,
    expected: list[dict[str, str]],
    protected: dict[str, list[str]],
) -> dict[str, str]:
    found = {match.group(1): match.group(2) for match in WRAPPER_RE.finditer(translated)}
    expected_ids = {item["id"] for item in expected}
    if set(found) != expected_ids:
        raise ValueError(
            f"wrapper mismatch: missing={sorted(expected_ids - set(found))}, "
            f"extra={sorted(set(found) - expected_ids)}"
        )
    return {
        item_id: restore(value.strip(), protected[item_id])
        for item_id, value in found.items()
    }


def request_translation(
    session: requests.Session,
    items: list[dict[str, str]],
    *,
    retries: int,
) -> dict[str, str]:
    payload, protected = make_payload(items)
    params = {
        "client": "gtx",
        "sl": "en",
        "tl": "ru",
        "dt": "t",
        "q": payload,
    }
    last_error: Exception | None = None
    for attempt in range(retries):
        try:
            response = session.get(ENDPOINT, params=params, timeout=60)
            response.raise_for_status()
            data: Any = response.json()
            translated = "".join(
                segment[0] for segment in data[0] if segment and segment[0] is not None
            )
            return parse_response(translated, items, protected)
        except (requests.RequestException, ValueError, KeyError, TypeError, json.JSONDecodeError) as error:
            last_error = error
            if len(items) > 1 and attempt >= 1:
                midpoint = len(items) // 2
                left = request_translation(session, items[:midpoint], retries=retries)
                right = request_translation(session, items[midpoint:], retries=retries)
                return {**left, **right}
            time.sleep(min(30.0, (2**attempt) + random.random()))
    raise RuntimeError(f"translation request failed after {retries} attempts: {last_error}")


def chunks(items: list[dict[str, str]], max_chars: int) -> list[list[dict[str, str]]]:
    result: list[list[dict[str, str]]] = []
    current: list[dict[str, str]] = []
    current_size = 0
    for item in items:
        # XML wrapper and escaped characters add overhead; this is conservative.
        size = len(item["text"]) + 40
        if current and current_size + size > max_chars:
            result.append(current)
            current = []
            current_size = 0
        current.append(item)
        current_size += size
    if current:
        result.append(current)
    return result


def save(path: Path, source: dict[str, Any], translated: dict[str, str]) -> None:
    by_id = {item["id"]: item["text"] for item in source["items"]}
    payload = {
        "format": 1,
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
    parser.add_argument("--max-chars", type=int, default=3800)
    parser.add_argument("--retries", type=int, default=6)
    parser.add_argument("--delay", type=float, default=0.25)
    args = parser.parse_args()

    source = json.loads(args.input.read_text(encoding="utf-8"))
    items = source["items"]
    existing: dict[str, str] = {}
    if args.output.is_file():
        old = json.loads(args.output.read_text(encoding="utf-8"))
        existing = {item["id"]: item["russian"] for item in old.get("translations", [])}

    pending = [item for item in items if item["id"] not in existing]
    batches = chunks(pending, args.max_chars)
    print(f"items={len(items)} cached={len(existing)} batches={len(batches)}", flush=True)

    session = requests.Session()
    session.headers["User-Agent"] = "Mozilla/5.0 localization-audit/1.0"
    for index, batch in enumerate(batches, start=1):
        existing.update(request_translation(session, batch, retries=args.retries))
        save(args.output, source, existing)
        print(
            f"batch={index}/{len(batches)} translated={len(existing)}/{len(items)}",
            flush=True,
        )
        time.sleep(args.delay)

    missing = {item["id"] for item in items} - set(existing)
    if missing:
        raise SystemExit(f"missing translations: {len(missing)}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
