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
PROTOTYPE_BE_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/content/propeller/PrototypePropellerBlockEntity.java"
CLIENT_EVENTS_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/client/AeronauticsPlusClientEvents.java"
RENDERER_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/client/AircraftPropellerRenderer.java"
ADAPTER_BLOCK_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/content/propeller/PropellerShaftAdapterBlock.java"
ADAPTER_RENDERER_SOURCE = ROOT / "src/main/java/dev/leeeonidys/aeronauticsplus/client/PropellerShaftAdapterRenderer.java"
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

    # The renderer treats +Z through (0.5, 0.5) as the canonical shaft line,
    # then partialFacing maps it to the block's FACING direction.  Guard both
    # the drive shaft and the rotationally symmetric hub pieces against an
    # off-centre export that would make the propeller orbit around the shaft.
    axis_objects = ("hub_body", "spinner_base", "spinner_nose", "rear_collar", "rear_drive_shaft")
    for object_name in axis_objects:
        values = object_vertices.get(object_name, [])
        if not values:
            fail(f"missing axis object {object_name} in {path.relative_to(ROOT)}")
        for coordinate in (0, 1):
            bounds_center = (
                min(vertex[coordinate] for vertex in values)
                + max(vertex[coordinate] for vertex in values)
            ) / 2
            if abs(bounds_center - 0.5) > 1e-6:
                fail(
                    f"{object_name} is off the Z shaft axis in {path.relative_to(ROOT)}: "
                    f"coordinate {coordinate} is centred at {bounds_center}"
                )

    blade_centroid = tuple(
        sum(vertex[coordinate] for vertex in blade_vertices) / len(blade_vertices)
        for coordinate in (0, 1)
    )
    if any(abs(value - 0.5) > 1e-6 for value in blade_centroid):
        fail(f"blade assembly is off the Z shaft axis in {path.relative_to(ROOT)}")

    material_path = path.with_suffix(".mtl")
    if not material_path.is_file() or "map_Kd #texture0" not in material_path.read_text(encoding="utf-8"):
        fail(f"invalid OBJ material in {material_path.relative_to(ROOT)}")


def validate_balance_constants(source: str) -> None:
    specs = re.findall(
        r'PropellerSpec\.(?:wooden|aluminum|steel)\('
        r'"([^"]+)",\s*(\d+),\s*([0-9.]+),\s*([0-9.]+),\s*([0-9.]+)f\)',
        source,
    )
    if len(specs) != 9:
        fail(f"expected 9 serial propeller balance specs, found {len(specs)}")
    for block_id, _blades, thrust, airflow, _radius in specs:
        thrust_value = float(thrust)
        airflow_value = float(airflow)
        if not 0 < thrust_value <= 4:
            fail(f"{block_id} thrust must be a per-RPM multiplier, got {thrust_value}")
        if not 0 < airflow_value <= 1:
            fail(f"{block_id} airflow must be a per-RPM multiplier, got {airflow_value}")

    prototype_source = PROTOTYPE_BE_SOURCE.read_text(encoding="utf-8")
    returns = [float(value) for value in re.findall(r"return\s+([0-9.]+);", prototype_source)]
    if len(returns) < 2 or not 0 < returns[0] <= 4 or not 0 < returns[1] <= 1:
        fail("prototype thrust/airflow must use per-RPM multipliers")


def validate_client_animation() -> None:
    if not CLIENT_EVENTS_SOURCE.is_file() or not RENDERER_SOURCE.is_file():
        fail("missing client propeller renderer registration")
    events = CLIENT_EVENTS_SOURCE.read_text(encoding="utf-8")
    renderer = RENDERER_SOURCE.read_text(encoding="utf-8")
    if events.count("registerBlockEntityRenderer(") != 3:
        fail("both propeller types and the shaft adapter must register renderers")
    if "PROTOTYPE_PROPELLER_BE" not in events or "AIRCRAFT_PROPELLER_BE" not in events:
        fail("propeller renderer registration is incomplete")
    if "PROPELLER_SHAFT_ADAPTER_BE" not in events or "PropellerShaftAdapterRenderer" not in events:
        fail("shaft-adapter renderer registration is incomplete")
    if "getAngle(partialTicks" not in renderer or "kineticRotationTransform" not in renderer:
        fail("propeller renderer must use the live kinetic angle")
    if "CachedBuffers.partialFacing" not in renderer or "direction.getAxis()" not in renderer:
        fail("propeller renderer must align the canonical OBJ axis with the shaft")
    if "direction.getOpposite()" not in renderer:
        fail("rear drive shaft must face the kinetic connection opposite FACING")
    after_kinetic_rotation = renderer.split("kineticRotationTransform", 1)[1].split(
        "propeller.renderInto", 1
    )[0]
    if re.search(r"propeller\.(?:rotate|translate|transform|center|uncenter)", after_kinetic_rotation):
        fail("propeller renderer must not tilt or offset the model after shaft rotation")
    if "VisualizationManager.supportsVisualization" in renderer:
        fail("fallback renderer must remain active without a registered Flywheel visual")


def validate_shaft_adapter(languages: dict[str, Any]) -> None:
    block_id = "propeller_shaft_adapter"
    blockstate_path = ASSETS / "blockstates" / f"{block_id}.json"
    block_model_path = ASSETS / "models/block" / f"{block_id}.json"
    item_model_path = ASSETS / "models/item" / f"{block_id}.json"
    geometry_path = GEOMETRY / f"{block_id}.obj"
    material_path = GEOMETRY / f"{block_id}.mtl"
    texture_path = ASSETS / "textures/block" / f"{block_id}.png"
    recipe_path = DATA / "recipe" / f"{block_id}.json"
    advancement_path = DATA / "advancement/recipes/misc" / f"{block_id}.json"
    loot_path = DATA / "loot_table/blocks" / f"{block_id}.json"

    for path in (
        ADAPTER_BLOCK_SOURCE,
        ADAPTER_RENDERER_SOURCE,
        blockstate_path,
        block_model_path,
        item_model_path,
        geometry_path,
        material_path,
        texture_path,
        recipe_path,
        advancement_path,
        loot_path,
    ):
        if not path.is_file():
            fail(f"missing shaft-adapter resource: {path.relative_to(ROOT)}")

    registration = JAVA_SOURCE.read_text(encoding="utf-8")
    if registration.count('"propeller_shaft_adapter"') < 2:
        fail("shaft adapter block and block entity must both be registered")
    if "PROPELLER_SHAFT_ADAPTER_ITEM" not in registration:
        fail("shaft adapter item must be registered and added to the creative tab")

    block_source = ADAPTER_BLOCK_SOURCE.read_text(encoding="utf-8")
    required_block_fragments = (
        "extends DirectionalKineticBlock",
        "implements IBE<SimpleKineticBlockEntity>",
        "face.getAxis() == getRotationAxis(state)",
        "RenderShape.ENTITYBLOCK_ANIMATED",
        "Block.box(0, 4.5, 4.5, 16, 11.5, 11.5)",
        "facing.getOpposite()",
        "PROPELLER_SHAFT_ADAPTER_BE",
    )
    for fragment in required_block_fragments:
        if fragment not in block_source:
            fail(f"shaft adapter is missing kinetic contract: {fragment}")

    adapter_renderer = ADAPTER_RENDERER_SOURCE.read_text(encoding="utf-8")
    if "renderRotatingBuffer(" not in adapter_renderer or "getRotatedModel(" not in adapter_renderer:
        fail("shaft adapter renderer must use live Create kinetic rotation")
    if "VisualizationManager.supportsVisualization" in adapter_renderer:
        fail("shaft adapter fallback renderer must remain active under Flywheel")

    blockstate = load_json(blockstate_path)
    variants = blockstate.get("variants", {})
    expected_variants = {f"facing={facing}" for facing in ("north", "south", "west", "east", "down", "up")}
    if set(variants) != expected_variants:
        fail(f"shaft adapter must have six directional variants, found {sorted(variants)}")
    expected_model = "aeronauticsplus:block/propeller_shaft_adapter"
    if any(variant.get("model") != expected_model for variant in variants.values()):
        fail("shaft adapter blockstate references the wrong model")

    block_model = load_json(block_model_path)
    item_model = load_json(item_model_path)
    expected_geometry = (
        "aeronauticsplus:models/block/propellers/geometry/propeller_shaft_adapter.obj"
    )
    if block_model.get("loader") != "neoforge:obj" or block_model.get("model") != expected_geometry:
        fail("shaft adapter must use its generated OBJ model")
    if block_model.get("ambientocclusion") is not False:
        fail("shaft adapter model must disable ambient occlusion")
    if item_model != block_model:
        fail("shaft adapter item and rotating block models must match")

    objects: dict[str, list[tuple[float, float, float]]] = {}
    current_object = ""
    for line in geometry_path.read_text(encoding="utf-8").splitlines():
        if line.startswith("o "):
            current_object = line[2:].strip()
            objects.setdefault(current_object, [])
        elif line.startswith("v "):
            vertex = tuple(float(value) for value in line.split()[1:4])
            objects.setdefault(current_object, []).append(vertex)
    expected_objects = {
        "thin_propeller_output",
        "forward_sleeve",
        "tapered_reducer",
        "locking_collar",
        "create_shaft_input",
    }
    if set(objects) != expected_objects or any(not vertices for vertices in objects.values()):
        fail(f"shaft adapter OBJ parts are incomplete: {sorted(objects)}")
    all_vertices = [vertex for vertices in objects.values() for vertex in vertices]
    if min(vertex[2] for vertex in all_vertices) < -1e-6 or max(vertex[2] for vertex in all_vertices) > 1.000001:
        fail("shaft adapter must remain inside its one-block axial envelope")
    for object_name, vertices in objects.items():
        for coordinate in (0, 1):
            center = (
                min(vertex[coordinate] for vertex in vertices)
                + max(vertex[coordinate] for vertex in vertices)
            ) / 2
            if abs(center - 0.5) > 1e-6:
                fail(f"shaft adapter part {object_name} is off the Z axis")

    def radial_extent(name: str) -> float:
        return max(
            max(abs(vertex[0] - 0.5), abs(vertex[1] - 0.5))
            for vertex in objects[name]
        )

    propeller_output_radius = radial_extent("thin_propeller_output")
    forward_sleeve_radius = radial_extent("forward_sleeve")
    reducer_radius = radial_extent("tapered_reducer")
    create_input_radius = radial_extent("create_shaft_input")
    locking_collar_radius = radial_extent("locking_collar")
    if not 0.034 <= propeller_output_radius <= 0.036:
        fail("shaft adapter output must remain only slightly wider than the propeller spindle")
    if not 0.044 <= forward_sleeve_radius <= 0.046:
        fail("shaft adapter forward sleeve is no longer a close fit around the propeller spindle")
    if not 0.199 <= reducer_radius <= 0.201:
        fail("shaft adapter reducer must finish only slightly outside the Create shaft")
    if not 0.187 <= create_input_radius <= 0.188:
        fail("shaft adapter input no longer matches Create's six-pixel shaft")
    if not 0.204 <= locking_collar_radius <= 0.206:
        fail("shaft adapter locking collar must remain only slightly wider than the Create shaft")
    if locking_collar_radius <= create_input_radius:
        fail("shaft adapter needs a visible locking collar around the Create shaft")
    if "map_Kd #texture0" not in material_path.read_text(encoding="utf-8"):
        fail("shaft adapter OBJ material is not texture-bound")
    assert_png_dimensions(texture_path, (32, 32))

    translation_key = f"block.aeronauticsplus.{block_id}"
    for locale, catalogue in languages.items():
        if translation_key not in catalogue:
            fail(f"missing {locale} shaft-adapter translation")

    recipe = load_json(recipe_path)
    recipe_items = {
        ingredient.get("item")
        for ingredient in recipe.get("key", {}).values()
        if isinstance(ingredient, dict)
    }
    if recipe_items != {"create:andesite_alloy", "create:shaft"}:
        fail(f"unexpected shaft-adapter recipe ingredients: {sorted(recipe_items)}")
    if recipe.get("result", {}).get("id") != "aeronauticsplus:propeller_shaft_adapter":
        fail("shaft-adapter recipe has the wrong result")


def validate() -> None:
    json_files = sorted(RESOURCES.rglob("*.json"))
    for path in json_files:
        load_json(path)

    ids = registered_block_ids()
    source = JAVA_SOURCE.read_text(encoding="utf-8")
    if source.count(".noOcclusion()") < 2:
        fail("prototype and serial propeller properties must disable full-cube occlusion")
    validate_balance_constants(source)
    validate_client_animation()

    languages = {
        locale: load_json(ASSETS / "lang" / f"{locale}.json")
        for locale in ("en_us", "ru_ru")
    }
    validate_shaft_adapter(languages)

    for blade_name, blade_count in (("two", 2), ("three", 3), ("four", 4)):
        validate_obj(GEOMETRY / f"propeller_{blade_name}_blade.obj", blade_count)

    for block_id in ids:
        blockstate_path = ASSETS / "blockstates" / f"{block_id}.json"
        item_model_path = ASSETS / "models/item" / f"{block_id}.json"
        if block_id == "prototype_propeller":
            block_model_path = ASSETS / "models/block" / f"{block_id}.json"
            static_model_path = ASSETS / "models/block" / f"{block_id}_static.json"
            static_model_id = f"aeronauticsplus:block/{block_id}_static"
            texture_path = ASSETS / "textures/block" / f"{block_id}.png"
        else:
            block_model_path = ASSETS / "models/block/propellers" / f"{block_id}.json"
            static_model_path = ASSETS / "models/block/propellers" / f"{block_id}_static.json"
            static_model_id = f"aeronauticsplus:block/propellers/{block_id}_static"
            texture_path = ASSETS / "textures/block/propellers" / f"{block_id}.png"

        recipe_path = DATA / "recipe" / f"{block_id}.json"
        advancement_path = DATA / "advancement/recipes/misc" / f"{block_id}.json"
        loot_path = DATA / "loot_table/blocks" / f"{block_id}.json"
        required_paths = (
            blockstate_path,
            block_model_path,
            static_model_path,
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
        if any(variant.get("model") != static_model_id for variant in variants.values()):
            fail(f"{block_id} blockstate must use its particle-only static model")

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

        static_model = load_json(static_model_path)
        if static_model.get("elements") != []:
            fail(f"{block_id} static model must not contain a non-animated propeller")
        if static_model.get("textures", {}).get("particle") != block_model.get("textures", {}).get("particle"):
            fail(f"{block_id} static and animated model particle textures differ")

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
        f"Validated {len(ids)} propellers, 1 shaft adapter and {len(json_files)} JSON files: "
        "kinetics, assets, translations, recipes, advancements, loot tables and PNG dimensions are consistent."
    )


if __name__ == "__main__":
    validate()
