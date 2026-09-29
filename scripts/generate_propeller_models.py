#!/usr/bin/env python3
"""Generate the original Aeronautics Plus propeller models and palette textures.

The models intentionally use only vanilla cuboids so they work with the Create
Aeronautics kinetic renderer.  Three-blade rotors use a stepped 120-degree
silhouette instead of the previous cross-like shaft/blade arrangement.
"""

from __future__ import annotations

import json
import struct
import zlib
from pathlib import Path
from typing import Iterable

ROOT = Path(__file__).resolve().parents[1]
MODELS = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/propellers"
TEMPLATES = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/templates"
TEXTURES = ROOT / "src/main/resources/assets/aeronauticsplus/textures/block/propellers"
PROTOTYPE_MODEL = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/prototype_propeller.json"
PROTOTYPE_TEXTURE = ROOT / "src/main/resources/assets/aeronauticsplus/textures/block/prototype_propeller.png"


PALETTES = {
    "wooden": {
        "blade_dark": (74, 42, 20, 255),
        "blade_mid": (132, 79, 34, 255),
        "blade_light": (181, 119, 58, 255),
        "edge": (55, 31, 18, 255),
        "hub": (83, 91, 94, 255),
        "hub_light": (145, 151, 150, 255),
        "shaft": (46, 51, 54, 255),
    },
    "aluminum": {
        "blade_dark": (82, 99, 108, 255),
        "blade_mid": (145, 164, 171, 255),
        "blade_light": (210, 225, 226, 255),
        "edge": (61, 73, 80, 255),
        "hub": (91, 105, 113, 255),
        "hub_light": (184, 199, 202, 255),
        "shaft": (50, 59, 64, 255),
    },
    "steel": {
        "blade_dark": (45, 51, 57, 255),
        "blade_mid": (82, 92, 101, 255),
        "blade_light": (135, 146, 153, 255),
        "edge": (31, 35, 40, 255),
        "hub": (52, 59, 66, 255),
        "hub_light": (112, 122, 129, 255),
        "shaft": (24, 28, 32, 255),
    },
    "prototype": {
        "blade_dark": (73, 83, 88, 255),
        "blade_mid": (126, 139, 143, 255),
        "blade_light": (190, 203, 204, 255),
        "edge": (48, 55, 59, 255),
        "hub": (79, 90, 95, 255),
        "hub_light": (162, 174, 175, 255),
        "shaft": (38, 44, 48, 255),
    },
}


def png_chunk(kind: bytes, payload: bytes) -> bytes:
    return struct.pack(">I", len(payload)) + kind + payload + struct.pack(">I", zlib.crc32(kind + payload) & 0xFFFFFFFF)


def write_png(path: Path, pixels: list[list[tuple[int, int, int, int]]]) -> None:
    height = len(pixels)
    width = len(pixels[0])
    raw = b"".join(b"\x00" + b"".join(bytes(pixel) for pixel in row) for row in pixels)
    header = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    path.write_bytes(
        b"\x89PNG\r\n\x1a\n"
        + png_chunk(b"IHDR", header)
        + png_chunk(b"IDAT", zlib.compress(raw, 9))
        + png_chunk(b"IEND", b"")
    )


def palette_texture(palette: dict[str, tuple[int, int, int, int]], *, wood_grain: bool) -> list[list[tuple[int, int, int, int]]]:
    pixels = [[palette["blade_mid"] for _ in range(16)] for _ in range(16)]

    # Blade face: upper-left 8x8, light centre and dark leading edge.
    for y in range(8):
        for x in range(8):
            if x in (0, 7) or y in (0, 7):
                color = palette["blade_dark"]
            elif (x + 2 * y) % 7 == 0:
                color = palette["blade_light"]
            else:
                color = palette["blade_mid"]
            if wood_grain and y in (2, 5) and 1 < x < 7:
                color = palette["blade_dark"] if x % 3 == 0 else color
            pixels[y][x] = color

    # Blade edge: upper-right 8x8.
    for y in range(8):
        for x in range(8, 16):
            pixels[y][x] = palette["edge"] if y in (0, 7) or x in (8, 15) else palette["blade_dark"]

    # Hub: lower-left 8x8 with a simple machined/riveted highlight.
    for y in range(8, 16):
        for x in range(8):
            border = x in (0, 7) or y in (8, 15)
            diagonal = (x + y) % 6 == 0
            pixels[y][x] = palette["shaft"] if border else palette["hub_light"] if diagonal else palette["hub"]
    for x, y in ((1, 9), (6, 9), (1, 14), (6, 14)):
        pixels[y][x] = palette["hub_light"]

    # Shaft: lower-right 8x8.
    for y in range(8, 16):
        for x in range(8, 16):
            pixels[y][x] = palette["edge"] if x in (8, 15) else palette["shaft"]
    return pixels


def faces(kind: str) -> dict[str, dict[str, object]]:
    if kind == "blade":
        broad_uv = [0, 0, 8, 8]
        edge_uv = [8, 0, 16, 8]
        texture = "#0"
    elif kind == "hub":
        broad_uv = edge_uv = [0, 8, 8, 16]
        texture = "#0"
    elif kind == "shaft":
        broad_uv = edge_uv = [8, 8, 16, 16]
        texture = "#0"
    else:
        raise ValueError(kind)

    return {
        "north": {"uv": broad_uv, "texture": texture},
        "south": {"uv": broad_uv, "texture": texture},
        "east": {"uv": edge_uv, "texture": texture},
        "west": {"uv": edge_uv, "texture": texture},
        "up": {"uv": edge_uv, "texture": texture},
        "down": {"uv": edge_uv, "texture": texture},
    }


def element(start: Iterable[float], end: Iterable[float], kind: str, *, name: str) -> dict[str, object]:
    return {
        "name": name,
        "from": list(start),
        "to": list(end),
        "faces": faces(kind),
    }


def common_hub() -> list[dict[str, object]]:
    # Layered cuboids suggest a machined collar and spinner without relying on
    # non-vanilla mesh loaders. The narrow dark shaft remains visually distinct
    # from every blade, including when the block faces vertically.
    return [
        element((7.25, 7.25, 0), (8.75, 8.75, 16), "shaft", name="drive_shaft"),
        element((6.0, 6.0, 9.5), (10.0, 10.0, 11.0), "shaft", name="rear_collar"),
        element((5.4, 5.4, 7.0), (10.6, 10.6, 9.5), "hub", name="hub_body"),
        element((6.3, 6.3, 5.4), (9.7, 9.7, 7.0), "hub", name="spinner"),
        element((7.1, 7.1, 4.4), (8.9, 8.9, 5.4), "hub", name="spinner_tip"),
    ]


def two_blades() -> list[dict[str, object]]:
    return [
        element((6.2, 9.0, 6.4), (9.8, 11.5, 9.2), "blade", name="blade_top_root"),
        element((6.7, 11.5, 5.9), (9.3, 14.0, 8.5), "blade", name="blade_top_mid"),
        element((7.15, 14.0, 5.5), (8.85, 16.0, 7.7), "blade", name="blade_top_tip"),
        element((6.2, 4.5, 6.8), (9.8, 7.0, 9.6), "blade", name="blade_bottom_root"),
        element((6.7, 2.0, 7.5), (9.3, 4.5, 10.1), "blade", name="blade_bottom_mid"),
        element((7.15, 0.0, 8.3), (8.85, 2.0, 10.5), "blade", name="blade_bottom_tip"),
    ]


def three_blades() -> list[dict[str, object]]:
    # One blade points up. Two stepped blades approximate 120°/240° without a
    # custom renderer, avoiding the old cross-shaped silhouette.
    return [
        element((6.2, 9.0, 6.4), (9.8, 11.5, 9.2), "blade", name="blade_top_root"),
        element((6.7, 11.5, 5.9), (9.3, 14.0, 8.5), "blade", name="blade_top_mid"),
        element((7.15, 14.0, 5.5), (8.85, 16.0, 7.7), "blade", name="blade_top_tip"),
        element((4.6, 5.6, 6.8), (7.0, 8.1, 9.6), "blade", name="blade_left_root"),
        element((2.25, 3.5, 7.4), (5.2, 5.9, 10.0), "blade", name="blade_left_mid"),
        element((0.0, 1.8, 8.1), (2.8, 3.8, 10.3), "blade", name="blade_left_tip"),
        element((9.0, 5.6, 6.8), (11.4, 8.1, 9.6), "blade", name="blade_right_root"),
        element((10.8, 3.5, 7.4), (13.75, 5.9, 10.0), "blade", name="blade_right_mid"),
        element((13.2, 1.8, 8.1), (16.0, 3.8, 10.3), "blade", name="blade_right_tip"),
    ]


def four_blades() -> list[dict[str, object]]:
    return two_blades() + [
        element((9.0, 6.2, 6.4), (11.5, 9.8, 9.2), "blade", name="blade_right_root"),
        element((11.5, 6.7, 5.9), (14.0, 9.3, 8.5), "blade", name="blade_right_mid"),
        element((14.0, 7.15, 5.5), (16.0, 8.85, 7.7), "blade", name="blade_right_tip"),
        element((4.5, 6.2, 6.8), (7.0, 9.8, 9.6), "blade", name="blade_left_root"),
        element((2.0, 6.7, 7.5), (4.5, 9.3, 10.1), "blade", name="blade_left_mid"),
        element((0.0, 7.15, 8.3), (2.0, 8.85, 10.5), "blade", name="blade_left_tip"),
    ]


def template_model(blades: int) -> dict[str, object]:
    blade_elements = {2: two_blades, 3: three_blades, 4: four_blades}[blades]()
    return {
        "credit": "Original Aeronautics Plus model",
        "ambientocclusion": False,
        "parent": "block/block",
        "textures": {"particle": "#0"},
        "elements": common_hub() + blade_elements,
    }


def material_model(texture: str, blades: int) -> dict[str, object]:
    blade_name = {2: "two", 3: "three", 4: "four"}[blades]
    return {
        "parent": f"aeronauticsplus:block/templates/propeller_{blade_name}_blade",
        "textures": {"0": texture},
    }


def write_model(path: Path, data: dict[str, object]) -> None:
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    MODELS.mkdir(parents=True, exist_ok=True)
    TEMPLATES.mkdir(parents=True, exist_ok=True)
    TEXTURES.mkdir(parents=True, exist_ok=True)

    for blades, blade_name in ((2, "two"), (3, "three"), (4, "four")):
        write_model(
            TEMPLATES / f"propeller_{blade_name}_blade.json",
            template_model(blades),
        )

    for material in ("wooden", "aluminum", "steel"):
        texture = palette_texture(PALETTES[material], wood_grain=material == "wooden")
        for blades, blade_name in ((2, "two"), (3, "three"), (4, "four")):
            block_id = f"{material}_{blade_name}_blade_propeller"
            texture_id = f"aeronauticsplus:block/propellers/{block_id}"
            write_model(MODELS / f"{block_id}.json", material_model(texture_id, blades))
            write_png(TEXTURES / f"{block_id}.png", texture)

    prototype_texture = palette_texture(PALETTES["prototype"], wood_grain=False)
    write_model(
        PROTOTYPE_MODEL,
        material_model("aeronauticsplus:block/prototype_propeller", 4),
    )
    write_png(PROTOTYPE_TEXTURE, prototype_texture)

    print("Generated 10 original tapered propeller models and palette textures.")


if __name__ == "__main__":
    main()
