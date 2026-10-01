#!/usr/bin/env python3
"""Validate navigation, local links, and consolidated science-source catalogs."""

from __future__ import annotations

import re
import sys
from pathlib import Path
from urllib.parse import unquote

ROOT = Path(__file__).resolve().parents[1]
REF = ROOT / "docs" / "references"

SUBJECTS = {
    REF / "MATHEMATICS_STUDY_GUIDE_RU.md": 120,
    REF / "REALISM_STUDY_GUIDE_RU.md": 150,
    REF / "CHEMISTRY_MOD_STUDY_GUIDE_RU.md": 200,
    REF / "MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md": 75,
    REF / "INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md": 75,
    REF / "EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md": 110,
}
MAP = REF / "SCIENCE_COVERAGE_MAP_RU.md"
CURRICULUM = REF / "INTEGRATED_SCIENCE_CURRICULUM_RU.md"
DOCS = [*SUBJECTS, MAP, CURRICULUM]

TOC_START = "<!-- TOC:START -->"
TOC_END = "<!-- TOC:END -->"
SOURCES_START = "<!-- SOURCES:START -->"
SOURCES_END = "<!-- SOURCES:END -->"
URL_RE = re.compile(r"https?://[^\s<>)]+")
LINK_RE = re.compile(r"(?<!!)\[[^\]]+\]\(([^)\s]+)(?:\s+\"[^\"]*\")?\)")
ANCHOR_RE = re.compile(r'^<a id="([^"]+)"></a>$', re.M)


def fail(errors: list[str], path: Path, message: str) -> None:
    errors.append(f"{path.relative_to(ROOT)}: {message}")


def urls(text: str) -> set[str]:
    return {match.rstrip(".,;:\"") for match in URL_RE.findall(text)}


def list_items(text: str) -> set[str]:
    """Flatten Markdown bullet continuation lines for exact catalog comparison."""

    items: set[str] = set()
    current: list[str] | None = None
    for line in text.splitlines():
        if line.startswith("- "):
            if current:
                items.add(re.sub(r"\s+", " ", " ".join(current)).strip())
            current = [line[2:].strip()]
        elif current is not None and (line.startswith("  ") or not line.strip()):
            if line.strip():
                current.append(line.strip())
            elif current:
                items.add(re.sub(r"\s+", " ", " ".join(current)).strip())
                current = None
        elif current:
            items.add(re.sub(r"\s+", " ", " ".join(current)).strip())
            current = None
    if current:
        items.add(re.sub(r"\s+", " ", " ".join(current)).strip())
    return items


def validate_structure(path: Path, text: str, errors: list[str]) -> None:
    for marker in (TOC_START, TOC_END, SOURCES_START, SOURCES_END):
        if text.count(marker) != 1:
            fail(errors, path, f"expected exactly one {marker}, found {text.count(marker)}")

    if SOURCES_END in text and text.split(SOURCES_END, 1)[1].strip():
        fail(errors, path, "source catalog is not the final content")

    anchors = ANCHOR_RE.findall(text)
    duplicates = sorted({anchor for anchor in anchors if anchors.count(anchor) > 1})
    if duplicates:
        fail(errors, path, f"duplicate explicit anchors: {', '.join(duplicates)}")

    lines = text.splitlines()
    for index, line in enumerate(lines):
        if not line.startswith("## ") or line == "## Оглавление":
            continue
        previous = lines[index - 1] if index else ""
        if not re.fullmatch(r'<a id="[^"]+"></a>', previous):
            fail(errors, path, f"H2 without adjacent explicit anchor at line {index + 1}: {line}")
            continue
        anchor = re.fullmatch(r'<a id="([^"]+)"></a>', previous).group(1)
        toc = text.split(TOC_END, 1)[0]
        if f"](#{anchor})" not in toc:
            fail(errors, path, f"anchor #{anchor} missing from generated contents")


def validate_links(path: Path, text: str, errors: list[str]) -> None:
    own_anchors = set(ANCHOR_RE.findall(text))
    for destination in LINK_RE.findall(text):
        destination = destination.strip("<>")
        if destination.startswith(("http://", "https://", "mailto:")):
            continue
        raw_path, separator, fragment = destination.partition("#")
        target = path if not raw_path else (path.parent / unquote(raw_path)).resolve()
        if raw_path and not target.exists():
            fail(errors, path, f"broken relative link: {destination}")
            continue
        if not separator or not fragment:
            continue
        if target == path:
            target_anchors = own_anchors
        elif target.suffix.casefold() == ".md":
            target_anchors = set(ANCHOR_RE.findall(target.read_text(encoding="utf-8")))
        else:
            continue
        if unquote(fragment) not in target_anchors:
            fail(errors, path, f"missing explicit anchor in link: {destination}")


def main() -> int:
    errors: list[str] = []
    texts: dict[Path, str] = {}
    for path in DOCS:
        if not path.exists():
            fail(errors, path, "file does not exist")
            continue
        text = path.read_text(encoding="utf-8")
        texts[path] = text
        validate_structure(path, text, errors)
        validate_links(path, text, errors)

    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1

    curriculum_sources = texts[CURRICULUM].split(SOURCES_START, 1)[1].split(SOURCES_END, 1)[0]
    curriculum_urls = urls(curriculum_sources)
    curriculum_items = list_items(curriculum_sources)

    total_references = 0
    for path, minimum in SUBJECTS.items():
        body, appendix_tail = texts[path].split(SOURCES_START, 1)
        appendix = appendix_tail.split(SOURCES_END, 1)[0]
        body_urls = urls(body)
        appendix_urls = urls(appendix)
        missing_urls = sorted(body_urls - appendix_urls)
        if missing_urls:
            fail(errors, path, f"{len(missing_urls)} body URL(s) absent from source appendix")

        appendix_items = list_items(appendix)
        reference_items = {
            item for item in appendix_items if not item.startswith("[") or "](http" in item
        }
        total_references += len(reference_items)
        if len(reference_items) < minimum:
            fail(
                errors,
                path,
                f"source appendix has {len(reference_items)} entries; expected at least {minimum}",
            )

        missing_global_urls = sorted(appendix_urls - curriculum_urls)
        if missing_global_urls:
            fail(errors, CURRICULUM, f"missing URLs from {path.name}: {len(missing_global_urls)}")

        missing_global_items = sorted(reference_items - curriculum_items)
        if missing_global_items:
            fail(
                errors,
                CURRICULUM,
                f"missing source entries from {path.name}: {len(missing_global_items)}",
            )

    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1

    anchor_count = sum(len(ANCHOR_RE.findall(text)) for text in texts.values())
    print(
        f"Science docs OK: {len(DOCS)} files, {anchor_count} explicit anchors, "
        f"{total_references} subject source entries, all local links resolved."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
