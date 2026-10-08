#!/usr/bin/env python3
"""Regenerate linked tables of contents and consolidated source appendices.

The science guides intentionally cite sources next to the topic where they are used.
This script duplicates those citations at the end of each guide and builds one global
catalog in INTEGRATED_SCIENCE_CURRICULUM_RU.md. It also gives every H2 section a stable,
explicit anchor and regenerates H2-level navigation.
"""

from __future__ import annotations

import hashlib
import re
import textwrap
from dataclasses import dataclass
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REF = ROOT / "docs" / "references"

TOC_START = "<!-- TOC:START -->"
TOC_END = "<!-- TOC:END -->"
SOURCES_START = "<!-- SOURCES:START -->"
SOURCES_END = "<!-- SOURCES:END -->"


@dataclass(frozen=True)
class Guide:
    path: Path
    prefix: str
    catalog_title: str
    collect_sources: bool = True


GUIDES = [
    Guide(REF / "MATHEMATICS_STUDY_GUIDE_RU.md", "math", "Математика"),
    Guide(REF / "REALISM_STUDY_GUIDE_RU.md", "physics", "Физика и инженерная механика"),
    Guide(REF / "CHEMISTRY_MOD_STUDY_GUIDE_RU.md", "chem", "Химия и материаловедение"),
    Guide(
        REF / "MEASUREMENT_SYSTEMS_RELIABILITY_STUDY_GUIDE_RU.md",
        "measure",
        "Метрология, systems и надёжность",
    ),
    Guide(
        REF / "INTERFACIAL_TRANSPORT_ENERGY_STUDY_GUIDE_RU.md",
        "interface",
        "Поверхности, сложные потоки и энергетическая физика",
    ),
    Guide(
        REF / "EARTH_MATERIALS_ENVIRONMENT_STUDY_GUIDE_RU.md",
        "earth",
        "Земля, специальные материалы и окружающая среда",
    ),
    Guide(REF / "SCIENCE_COVERAGE_MAP_RU.md", "coverage", "Карта наук", False),
    Guide(
        REF / "INTEGRATED_SCIENCE_CURRICULUM_RU.md",
        "curriculum",
        "Единая программа",
        False,
    ),
]

SUBJECT_GUIDES = [guide for guide in GUIDES if guide.collect_sources]


def strip_generated(text: str, prefix: str) -> str:
    """Remove generated blocks and explicit anchors for an idempotent rerun."""

    text = re.sub(
        rf"\n?{re.escape(TOC_START)}.*?{re.escape(TOC_END)}\n?",
        "\n",
        text,
        flags=re.S,
    )
    text = re.sub(
        rf"(?:\n---[ \t]*\n)+\n*{re.escape(SOURCES_START)}.*?{re.escape(SOURCES_END)}\n?",
        "\n",
        text,
        flags=re.S,
    )
    text = re.sub(rf"^<a id=\"{re.escape(prefix)}-[^\"]+\"></a>\n", "", text, flags=re.M)
    text = re.sub(r"\A(#[^\n]+\n)(?:[ \t]*\n)+", r"\1\n", text)
    return text.strip() + "\n"


def iter_list_blocks(text: str) -> list[str]:
    """Flatten top-level Markdown list entries while retaining continuation text."""

    lines = text.splitlines()
    blocks: list[str] = []
    index = 0
    while index < len(lines):
        if not re.match(r"^(?:- |\d+\. )", lines[index]):
            index += 1
            continue

        block = [lines[index]]
        index += 1
        while index < len(lines):
            line = lines[index]
            if not line:
                break
            if re.match(r"^(?:- |\d+\. |#{1,6} |---$)", line):
                break
            block.append(line)
            index += 1

        flattened = " ".join(part.strip() for part in block)
        flattened = re.sub(r"\s+", " ", flattened).strip()
        blocks.append(flattened)

    return blocks


def is_bibliographic_block(block: str) -> bool:
    has_url = "http://" in block or "https://" in block
    if has_url:
        return True

    item = re.sub(r"^(?:- |\d+\. )", "", block).strip()
    has_italic_title = bool(re.search(r",.{0,100}(?<!\*)\*[^*]{3,}\*(?!\*)", item))
    quoted_title = bool(re.search(r",.{0,100}«[^»]{3,}»", item))
    author_lead = item.lstrip("*")
    has_quoted_title = quoted_title and bool(re.match(r"[A-ZА-ЯЁ]", author_lead))
    starts_standard = bool(
        re.match(
            r"(?:ISO|IEC|ASTM|ASME|IEEE|JCGM|NASA-(?:STD|HDBK)|NFPA|IMO|ITTC|"
            r"ГОСТ|РМГ)\b",
            item,
        )
    )
    return has_italic_title or has_quoted_title or starts_standard


def clean_reference(block: str) -> str:
    item = re.sub(r"^(?:- |\d+\. )", "", block).strip()
    return re.sub(r"\s+", " ", item)


def collect_sources(text: str) -> tuple[list[str], list[str]]:
    """Return (print/standards, online/data) references in first-citation order."""

    print_refs: list[str] = []
    online_refs: list[str] = []
    seen: set[str] = set()

    for block in iter_list_blocks(text):
        if not is_bibliographic_block(block):
            continue
        item = clean_reference(block)
        key = item.casefold().rstrip(".;")
        if key in seen:
            continue
        seen.add(key)
        if "http://" in item or "https://" in item:
            online_refs.append(item)
        else:
            print_refs.append(item)

    all_urls = list(dict.fromkeys(re.findall(r"https?://[^\s)>|]+", text)))
    captured_urls = {
        url.rstrip(".,;")
        for item in online_refs
        for url in re.findall(r"https?://[^\s)>|]+", item)
    }
    for url in all_urls:
        clean_url = url.rstrip(".,;")
        if clean_url not in captured_urls:
            online_refs.append(clean_url)
            captured_urls.add(clean_url)

    return print_refs, online_refs


def bullet(item: str, width: int = 100) -> str:
    return textwrap.fill(
        item,
        width=width,
        initial_indent="- ",
        subsequent_indent="  ",
        break_long_words=False,
        break_on_hyphens=False,
    )


def source_appendix(print_refs: list[str], online_refs: list[str]) -> str:
    parts = [
        SOURCES_START,
        "## Сводный список источников",
        "",
        "Этот раздел намеренно дублирует источники, приведённые рядом с темами. "
        "Список собран в одном месте для последовательного чтения и аудита ссылок.",
        "",
        "### Книги, отчёты и стандарты",
        "",
    ]
    parts.extend(bullet(item) for item in print_refs)
    parts.extend(["", "### Онлайн-курсы, базы данных и официальные страницы", ""])
    parts.extend(bullet(item) for item in online_refs)
    parts.extend([SOURCES_END, ""])
    return "\n".join(parts)


def subject_source_link(guide: Guide) -> str:
    return f"[{guide.catalog_title}]({guide.path.name}#{guide.prefix}-sources)"


def coverage_appendix() -> str:
    parts = [
        SOURCES_START,
        "## Реестр полных списков источников",
        "",
        "Карта не повторяет тысячи библиографических строк внутри тематических разделов. "
        "Полные списки, уже продублированные в конце каждого маршрута:",
        "",
    ]
    parts.extend(f"- {subject_source_link(guide)}" for guide in SUBJECT_GUIDES)
    parts.extend(
        [
            "- [Единый каталог всех дисциплин](INTEGRATED_SCIENCE_CURRICULUM_RU.md#curriculum-sources)",
            SOURCES_END,
            "",
        ]
    )
    return "\n".join(parts)


def global_source_appendix(
    source_sets: dict[Path, tuple[list[str], list[str]]],
) -> str:
    parts = [
        SOURCES_START,
        "## Полный сводный каталог источников",
        "",
        "Ниже намеренно повторены источники всех предметных маршрутов. Для контекста, "
        "уровня сложности и области применимости следует переходить в соответствующий файл.",
        "",
        "### Быстрые ссылки на предметные списки",
        "",
    ]
    parts.extend(f"- {subject_source_link(guide)}" for guide in SUBJECT_GUIDES)

    for guide in SUBJECT_GUIDES:
        print_refs, online_refs = source_sets[guide.path]
        parts.extend(["", f"### {guide.catalog_title}: книги, отчёты и стандарты", ""])
        parts.extend(bullet(item) for item in print_refs)
        parts.extend(["", f"### {guide.catalog_title}: онлайн-источники и данные", ""])
        parts.extend(bullet(item) for item in online_refs)

    parts.extend([SOURCES_END, ""])
    return "\n".join(parts)


def section_key(title: str) -> str:
    lowered = title.casefold()
    if "сводный список источников" in lowered or "полный сводный каталог" in lowered:
        return "sources"
    if "реестр полных списков" in lowered:
        return "sources"
    if lowered.startswith("итог"):
        return "summary"
    if lowered.startswith("как пользоваться"):
        return "intro"

    match = re.match(r"(\d+)([a-zа-я]?)\.", lowered)
    if match:
        return f"{match.group(1)}{match.group(2)}"

    digest = hashlib.sha1(title.encode("utf-8")).hexdigest()[:8]
    return f"sec-{digest}"


def add_anchors_and_toc(text: str, prefix: str) -> str:
    headings: list[tuple[str, str]] = []
    used: set[str] = set()

    for match in re.finditer(r"^## (.+)$", text, flags=re.M):
        title = match.group(1).strip()
        if title == "Оглавление":
            continue
        key = section_key(title)
        anchor = f"{prefix}-{key}"
        if anchor in used:
            suffix = 2
            while f"{anchor}-{suffix}" in used:
                suffix += 1
            anchor = f"{anchor}-{suffix}"
        used.add(anchor)
        headings.append((title, anchor))

    anchor_by_title: dict[str, list[str]] = {}
    for title, anchor in headings:
        anchor_by_title.setdefault(title, []).append(anchor)

    def insert_anchor(match: re.Match[str]) -> str:
        title = match.group(1).strip()
        if title == "Оглавление":
            return match.group(0)
        anchor = anchor_by_title[title].pop(0)
        return f'<a id="{anchor}"></a>\n## {title}'

    anchored = re.sub(r"^## (.+)$", insert_anchor, text, flags=re.M)
    toc = [TOC_START, "## Оглавление", ""]
    toc.extend(f"- [{title}](#{anchor})" for title, anchor in headings)
    if prefix == "curriculum":
        navigation = (
            "> [Карта покрытия](SCIENCE_COVERAGE_MAP_RU.md) · "
            "[Общий каталог источников](#curriculum-sources)"
        )
    elif prefix == "coverage":
        navigation = (
            "> [Единая программа](INTEGRATED_SCIENCE_CURRICULUM_RU.md) · "
            "[Реестр источников](#coverage-sources)"
        )
    else:
        navigation = (
            "> [Единая программа](INTEGRATED_SCIENCE_CURRICULUM_RU.md) · "
            "[Карта покрытия](SCIENCE_COVERAGE_MAP_RU.md) · "
            f"[Источники этого файла](#{prefix}-sources)"
        )
    toc.extend(["", navigation, TOC_END, ""])
    toc_text = "\n".join(toc)

    lines = anchored.splitlines()
    if not lines or not lines[0].startswith("# "):
        raise ValueError(f"Expected H1 at start for prefix {prefix}")
    insertion = 1
    while insertion < len(lines) and not lines[insertion].strip():
        insertion += 1
    lines[insertion:insertion] = [toc_text, ""]
    return "\n".join(lines).rstrip() + "\n"


def main() -> None:
    for guide in GUIDES:
        if not guide.path.exists():
            raise FileNotFoundError(guide.path)

    clean_text: dict[Path, str] = {
        guide.path: strip_generated(guide.path.read_text(encoding="utf-8"), guide.prefix)
        for guide in GUIDES
    }

    source_sets = {
        guide.path: collect_sources(clean_text[guide.path]) for guide in SUBJECT_GUIDES
    }

    for guide in SUBJECT_GUIDES:
        clean_text[guide.path] = (
            clean_text[guide.path].rstrip()
            + "\n\n---\n\n"
            + source_appendix(*source_sets[guide.path])
        )

    coverage = next(guide for guide in GUIDES if guide.prefix == "coverage")
    clean_text[coverage.path] = (
        clean_text[coverage.path].rstrip() + "\n\n---\n\n" + coverage_appendix()
    )

    curriculum = next(guide for guide in GUIDES if guide.prefix == "curriculum")
    clean_text[curriculum.path] = (
        clean_text[curriculum.path].rstrip()
        + "\n\n---\n\n"
        + global_source_appendix(source_sets)
    )

    for guide in GUIDES:
        rendered = add_anchors_and_toc(clean_text[guide.path], guide.prefix)
        guide.path.write_text(rendered, encoding="utf-8")
        print(f"updated {guide.path.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
