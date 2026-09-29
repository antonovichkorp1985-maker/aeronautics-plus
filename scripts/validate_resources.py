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
GEOMETRY = ASSETS / "models/block/propellers/geometry"
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


def validate_obj(path: Path, expected_blades: int) -> None:
    lines = path.read_text(encoding="utf-8").splitlines()
    current_object = ""
    object_vertices: dict[str, list[tuple[float, float, float]]] = {}
    vertices: list[tuple[float, float, float]] = []
    texture_coordinates = 0
    faces = 0

    for line in lines:
        if line.startswith("o "):
            current_object = line[2:].strip()
            object_vertices.setdefault(current_object, [])
        elif line.startswith("v "):
            try:
                vertex = tuple(float(value) for value in line.split()[1:4])
            except (ValueError, IndexError):
                fail(f"invalid OBJ vertex in {path.relative_to(ROOT)}: {line}")
            if len(vertex) != 3:
                fail(f"invalid OBJ vertex in {path.relative_to(ROOT)}: {line}")
            vertices.append(vertex)
            object_vertices.setdefault(current_object, []).append(vertex)
        elif line.startswith("vt "):
            texture_coordinates += 1
        elif line.startswith("f "):
            references = line.split()[1:]
            if len(references) != 3 or any("/" not in reference for reference in references):
                fail(f"OBJ faces must be textured triangles in {path.relative_to(ROOT)}")
            faces += 1

    if not vertices or texture_coordinates != len(vertices) or faces < 100:
        fail(f"incomplete OBJ mesh in {path.relative_to(ROOT)}")
    if f"mtllib {path.stem}.mtl" not in lines or "usemtl propeller" not in lines:
        fail(f"missing material binding in {path.relative_to(ROOT)}")

    tip_objects = [name for name in object_vertices if re.fullmatch(r"blade_\d+_tip", name)]
    body_objects = [name for name in object_vertices if re.fullmatch(r"blade_\d+_body", name)]
    if len(tip_objects) != expected_blades or len(body_objects) != expected_blades:
        fail(f"wrong blade count in {path.relative_to(ROOT)}")

    blade_vertices = [
        vertex
        for name, values in object_vertices.items()
        if name.startswith("blade_")
        for vertex in values
    ]
    xy = [coordinate for vertex in blade_vertices for coordinate in vertex[:2]]
    if min(xy) >= 0 or max(xy) <= 1:
        fail(f"aircraft blades must extend beyond one block in {path.relative_to(ROOT)}")
    if max(vertex[2] for vertex in blade_vertices) - min(vertex[2] for vertex in blade_vertices) > 0.09:
        fail(f"aircraft blades are too thick in {path.relative_to(ROOT)}")

    shaft_vertices = object_vertices.get("rear_drive_shaft", [])
    if not shaft_vertices or min(vertex[2] for vertex in shaft_vertices) < 0.64:
        fail(f"drive shaft must remain behind the hub in {path.relative_to(ROOT)}")
    if max(vertex[2] for vertex in shaft_vertices) > 1.001:
        fail(f"drive shaft exceeds the block boundary in {path.relative_to(ROOT)}")

    material_path = path.with_suffix(".mtl")
    if not material_path.is_file() or "map_Kd #texture0" not in material_path.read_text(encoding="utf-8"):
        fail(f"invalid OBJ material in {material_path.relative_to(ROOT)}")


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

    for blade_name, blade_count in (("two", 2), ("three", 3), ("four", 4)):
        validate_obj(GEOMETRY / f"propeller_{blade_name}_blade.obj", blade_count)

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
        expected_geometry = (
            f"aeronauticsplus:models/block/propellers/geometry/propeller_{blade_name}_blade.obj"
        )
        if block_model.get("loader") != "neoforge:obj":
            fail(f"{block_id} must use NeoForge's OBJ model loader")
        if block_model.get("model") != expected_geometry:
            fail(f"{block_id} must use {expected_geometry}")
        if block_model.get("automatic_culling") is not False:
            fail(f"{block_id} must disable automatic OBJ culling")
        if block_model.get("ambientocclusion") is not False:
            fail(f"{block_id} must disable model ambient occlusion")
        if "gui" not in block_model.get("display", {}):
            fail(f"{block_id} is missing scaled item display settings")

        item_model = load_json(item_model_path)
        if item_model.get("loader") != "neoforge:obj" or item_model.get("model") != expected_geometry:
            fail(f"{block_id} item must load the same OBJ geometry directly")
        if item_model.get("textures") != block_model.get("textures"):
            fail(f"{block_id} block and item texture bindings differ")

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

        assert_png_dimensions(texture_path, (32, 32))

    assert_png_dimensions(RESOURCES / "icon.png", (32, 32))
    print(
        f"Validated {len(ids)} propellers and {len(json_files)} JSON files: "
        "assets, translations, recipes, advancements, loot tables and PNG dimensions are consistent."
    )


if __name__ == "__main__":
    validate()
