#!/usr/bin/env python3
"""Validate Aeronautics Plus resource and data files without launching Minecraft."""

from __future__ import annotations

import json
import re
import struct
import sys
from pathlib import Path
from typing import Any

ROOT = Path(__file__).resolve().parents[1]
JAVA_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/AeronauticsPlus.java"
RESOURCES = ROOT / "src/main/resources"
ASSETS = RESOURCES / "assets/aeronauticsplus"
DATA = RESOURCES / "data/aeronauticsplus"


def fail(message: str) -> None:
    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(1)


def load_json(path: Path) -> Any:
    duplicates: list[str] = []

    def reject_duplicates(pairs: list[tuple[str, Any]]) -> dict[str, Any]:
        result: dict[str, Any] = {}
        for key, value in pairs:
            if key in result:
                duplicates.append(key)
            result[key] = value
        return result

    try:
        value = json.loads(path.read_text(encoding="utf-8"), object_pairs_hook=reject_duplicates)
    except (OSError, json.JSONDecodeError) as exc:
        fail(f"cannot parse {path.relative_to(ROOT)}: {exc}")

    if duplicates:
        fail(f"duplicate keys in {path.relative_to(ROOT)}: {', '.join(duplicates)}")
    return value


def registered_block_ids() -> list[str]:
    source = JAVA_SOURCE.read_text(encoding="utf-8")
    family_ids = re.findall(
        r'PropellerSpec\.(?:wooden|aluminum|steel)\("([^"]+)"', source
    )
    ids = ["prototype_propeller", *family_ids]
    if len(ids) != 10 or len(set(ids)) != len(ids):
        fail(f"expected 10 unique registered propellers, found {ids}")
    return ids


def assert_png_dimensions(path: Path, expected: tuple[int, int]) -> None:
    raw = path.read_bytes()
    if raw[:8] != b"\x89PNG\r\n\x1a\n" or raw[12:16] != b"IHDR":
        fail(f"not a valid PNG header: {path.relative_to(ROOT)}")
    width, height = struct.unpack(">II", raw[16:24])
    if (width, height) != expected:
        fail(
            f"unexpected dimensions for {path.relative_to(ROOT)}: "
            f"{width}x{height}, expected {expected[0]}x{expected[1]}"
        )


def expected_material_tag(block_id: str) -> str:
    if block_id == "prototype_propeller":
        return "c:ingots/iron"
    if block_id.startswith("wooden_"):
        return "minecraft:planks"
    if block_id.startswith("aluminum_"):
        return "c:ingots/aluminum"
    if block_id.startswith("steel_"):
        return "c:ingots/steel"
    fail(f"unknown propeller material for {block_id}")
    raise AssertionError("unreachable")


def validate() -> None:
    json_files = sorted(RESOURCES.rglob("*.json"))
    for path in json_files:
        load_json(path)

    ids = registered_block_ids()
    source = JAVA_SOURCE.read_text(encoding="utf-8")
    if source.count(".noOcclusion()") < 2:
        fail("prototype and serial propeller properties must disable full-cube occlusion")

    languages = {
        locale: load_json(ASSETS / "lang" / f"{locale}.json")
        for locale in ("en_us", "ru_ru")
    }

    template_dir = ASSETS / "models/block/templates"
    expected_elements = {"two": 11, "three": 14, "four": 17}
    for blade_name, element_count in expected_elements.items():
        template_path = template_dir / f"propeller_{blade_name}_blade.json"
        template = load_json(template_path)
        elements = template.get("elements", [])
        if template.get("ambientocclusion") is not False:
            fail(f"{template_path.relative_to(ROOT)} must disable ambient occlusion")
        if len(elements) != element_count:
            fail(
                f"{template_path.relative_to(ROOT)} has {len(elements)} elements, "
                f"expected {element_count}"
            )
        names = [element.get("name") for element in elements]
        if len(names) != len(set(names)) or "drive_shaft" not in names:
            fail(f"invalid or duplicate element names in {template_path.relative_to(ROOT)}")
        if any("cullface" in face for element in elements for face in element.get("faces", {}).values()):
            fail(f"custom propeller faces must not cull neighbours: {template_path.relative_to(ROOT)}")

    for block_id in ids:
        blockstate_path = ASSETS / "blockstates" / f"{block_id}.json"
        item_model_path = ASSETS / "models/item" / f"{block_id}.json"
        if block_id == "prototype_propeller":
            block_model_path = ASSETS / "models/block" / f"{block_id}.json"
            texture_path = ASSETS / "textures/block" / f"{block_id}.png"
        else:
            block_model_path = ASSETS / "models/block/propellers" / f"{block_id}.json"
            texture_path = ASSETS / "textures/block/propellers" / f"{block_id}.png"

        recipe_path = DATA / "recipe" / f"{block_id}.json"
        advancement_path = DATA / "advancement/recipes/misc" / f"{block_id}.json"
        loot_path = DATA / "loot_table/blocks" / f"{block_id}.json"
        required_paths = (
            blockstate_path,
            block_model_path,
            item_model_path,
            texture_path,
            recipe_path,
            advancement_path,
            loot_path,
        )
        for path in required_paths:
            if not path.is_file():
                fail(f"missing resource for {block_id}: {path.relative_to(ROOT)}")

        blockstate = load_json(blockstate_path)
        variants = blockstate.get("variants", {})
        if len(variants) != 12:
            fail(f"{block_id} must have 12 facing/reversed variants, found {len(variants)}")

        blade_name = "four" if block_id == "prototype_propeller" else next(
            name for name in ("two", "three", "four") if f"_{name}_blade_" in block_id
        )
        block_model = load_json(block_model_path)
        expected_parent = f"aeronauticsplus:block/templates/propeller_{blade_name}_blade"
        if block_model.get("parent") != expected_parent:
            fail(f"{block_id} must inherit {expected_parent}")

        translation_key = f"block.aeronauticsplus.{block_id}"
        for locale, language in languages.items():
            if translation_key not in language:
                fail(f"missing {locale} translation: {translation_key}")

        recipe = load_json(recipe_path)
        if recipe.get("type") != "minecraft:crafting_shaped":
            fail(f"{block_id} recipe is not crafting_shaped")
        if recipe.get("result", {}).get("id") != f"aeronauticsplus:{block_id}":
            fail(f"{block_id} recipe has the wrong output")
        key = recipe.get("key", {})
        if key.get("M", {}).get("tag") != expected_material_tag(block_id):
            fail(f"{block_id} recipe has the wrong material tag")
        if key.get("S", {}).get("item") != "create:shaft":
            fail(f"{block_id} recipe must use create:shaft")

        advancement = load_json(advancement_path)
        recipe_id = f"aeronauticsplus:{block_id}"
        if advancement.get("rewards", {}).get("recipes") != [recipe_id]:
            fail(f"{block_id} advancement does not unlock its recipe")
        shaft_predicate = (
            advancement.get("criteria", {})
            .get("has_shaft", {})
            .get("conditions", {})
            .get("items", [])
        )
        if not any(item.get("items") == "create:shaft" for item in shaft_predicate):
            fail(f"{block_id} advancement must unlock when a Create shaft is found")

        loot_table = load_json(loot_path)
        entries = [
            entry
            for pool in loot_table.get("pools", [])
            for entry in pool.get("entries", [])
        ]
        if not any(
            entry.get("type") == "minecraft:item"
            and entry.get("name") == f"aeronauticsplus:{block_id}"
            for entry in entries
        ):
            fail(f"{block_id} loot table does not drop itself")

        assert_png_dimensions(texture_path, (16, 16))

    assert_png_dimensions(RESOURCES / "icon.png", (32, 32))
    print(
        f"Validated {len(ids)} propellers and {len(json_files)} JSON files: "
        "assets, translations, recipes, advancements, loot tables and PNG dimensions are consistent."
    )


if __name__ == "__main__":
    validate()
