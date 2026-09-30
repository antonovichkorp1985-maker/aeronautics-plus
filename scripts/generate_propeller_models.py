#!/usr/bin/env python3
"""Generate original low-poly aircraft propellers for Aeronautics Plus.

The previous vanilla cuboid models could only approximate a tapered blade with
visible rectangular steps.  NeoForge's built-in OBJ loader accepts triangulated
arbitrary geometry, so this generator creates continuous pitched airfoils,
low-poly round hubs, front spinners, and rear-only drive shafts.  No third-party
models, textures, or mesh data are used.
"""

from __future__ import annotations

import json
import math
import struct
import zlib
from pathlib import Path
from typing import Iterable

ROOT = Path(__file__).resolve().parents[1]
MODELS = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/propellers"
ITEM_MODELS = ROOT / "src/main/resources/assets/aeronauticsplus/models/item"
BLOCKSTATES = ROOT / "src/main/resources/assets/aeronauticsplus/blockstates"
GEOMETRY = MODELS / "geometry"
TEXTURES = ROOT / "src/main/resources/assets/aeronauticsplus/textures/block/propellers"
PROTOTYPE_MODEL = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/prototype_propeller.json"
PROTOTYPE_STATIC_MODEL = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/prototype_propeller_static.json"
PROTOTYPE_TEXTURE = ROOT / "src/main/resources/assets/aeronauticsplus/textures/block/prototype_propeller.png"

PALETTES = {
    "wooden": {
        "blade_dark": (48, 27, 18, 255),
        "blade_mid": (92, 49, 25, 255),
        "blade_light": (145, 86, 43, 255),
        "edge": (34, 23, 20, 255),
        "hub": (74, 81, 84, 255),
        "hub_light": (139, 146, 145, 255),
        "shaft": (38, 43, 46, 255),
        "tip": (235, 177, 32, 255),
        "tip_light": (255, 220, 71, 255),
        "tip_dark": (123, 82, 15, 255),
    },
    "aluminum": {
        "blade_dark": (65, 79, 87, 255),
        "blade_mid": (126, 145, 153, 255),
        "blade_light": (204, 219, 221, 255),
        "edge": (43, 54, 61, 255),
        "hub": (83, 98, 106, 255),
        "hub_light": (188, 203, 205, 255),
        "shaft": (43, 51, 56, 255),
        "tip": (235, 177, 32, 255),
        "tip_light": (255, 220, 71, 255),
        "tip_dark": (123, 82, 15, 255),
    },
    "steel": {
        "blade_dark": (28, 33, 38, 255),
        "blade_mid": (55, 64, 72, 255),
        "blade_light": (104, 116, 124, 255),
        "edge": (19, 23, 27, 255),
        "hub": (47, 55, 62, 255),
        "hub_light": (116, 128, 135, 255),
        "shaft": (20, 24, 28, 255),
        "tip": (225, 165, 25, 255),
        "tip_light": (255, 214, 60, 255),
        "tip_dark": (111, 72, 12, 255),
    },
    "prototype": {
        "blade_dark": (48, 57, 63, 255),
        "blade_mid": (93, 107, 113, 255),
        "blade_light": (171, 185, 188, 255),
        "edge": (33, 39, 43, 255),
        "hub": (69, 81, 87, 255),
        "hub_light": (153, 166, 169, 255),
        "shaft": (32, 38, 42, 255),
        "tip": (224, 156, 24, 255),
        "tip_light": (255, 207, 63, 255),
        "tip_dark": (111, 67, 12, 255),
    },
}

# Normalized regions of the generated 32x32 atlas.
BLADE_UV = (0.0, 0.0, 0.5, 0.375)
TIP_UV = (0.0, 0.375, 0.5, 0.5625)
HUB_UV = (0.0, 0.5625, 0.5, 1.0)
SHAFT_UV = (0.5, 0.5625, 1.0, 1.0)

DISPLAY = {
    "gui": {"rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.48, 0.48, 0.48]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.32, 0.32, 0.32]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [0.48, 0.48, 0.48]},
    "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.38, 0.38, 0.38]},
    "thirdperson_lefthand": {"rotation": [75, 225, 0], "translation": [0, 2.5, 0], "scale": [0.38, 0.38, 0.38]},
    "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.42, 0.42, 0.42]},
    "firstperson_lefthand": {"rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.42, 0.42, 0.42]},
}

# Tangential/radial coordinates in block units. The body widens through the
# working section before tapering; the high-visibility tip continues the same
# outline without the rectangular staircase of a vanilla cuboid model.
BLADE_BODY = (
    (-0.050, 0.105),
    (-0.105, 0.290),
    (-0.140, 0.520),
    (-0.120, 0.680),
    (-0.086, 0.790),
    (0.074, 0.790),
    (0.100, 0.550),
    (0.076, 0.280),
    (0.050, 0.105),
)
BLADE_TIP = (
    (-0.087, 0.775),
    (-0.066, 0.890),
    (-0.036, 0.950),
    (0.030, 0.950),
    (0.056, 0.900),
    (0.075, 0.775),
)


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


def palette_texture(
    palette: dict[str, tuple[int, int, int, int]], *, wood_grain: bool
) -> list[list[tuple[int, int, int, int]]]:
    pixels = [[palette["edge"] for _ in range(32)] for _ in range(32)]

    # Blade face, UV 0,0 -> 0.5,0.375.
    for y in range(12):
        for x in range(16):
            if x < 2 or x > 14:
                color = palette["blade_dark"]
            elif x in (3, 4):
                color = palette["blade_light"]
            elif (x + y * 3) % 17 == 0:
                color = palette["blade_light"]
            else:
                color = palette["blade_mid"]
            if wood_grain and y in (3, 8) and 2 < x < 15:
                color = palette["blade_dark"] if (x + y) % 4 < 2 else color
            pixels[y][x] = color

    # Thin blade edge, top-right.
    for y in range(12):
        for x in range(16, 32):
            pixels[y][x] = palette["edge"] if x < 19 or y in (0, 11) else palette["blade_dark"]

    # High-visibility tip, UV 0,0.375 -> 0.5,0.5625.
    for y in range(12, 18):
        for x in range(16):
            if x < 2 or x > 14 or y in (12, 17):
                color = palette["tip_dark"]
            elif x in (4, 5):
                color = palette["tip_light"]
            else:
                color = palette["tip"]
            pixels[y][x] = color
        for x in range(16, 32):
            pixels[y][x] = palette["tip_dark"] if x < 20 or y in (12, 17) else palette["tip"]

    # Hub/spinner, lower-left.
    for y in range(18, 32):
        for x in range(16):
            border = x in (0, 1, 14, 15) or y in (18, 19, 30, 31)
            highlight = (x - 5) ** 2 + (y - 24) ** 2 < 10
            pixels[y][x] = palette["shaft"] if border else palette["hub_light"] if highlight else palette["hub"]
    for x, y in ((3, 21), (12, 21), (3, 28), (12, 28)):
        pixels[y][x] = palette["hub_light"]

    # Rear shaft/collar, lower-right.
    for y in range(18, 32):
        for x in range(16, 32):
            pixels[y][x] = palette["edge"] if x in (16, 17, 30, 31) else palette["shaft"]
    return pixels


class ObjMesh:
    """Minimal triangulated Wavefront OBJ writer with per-face UVs."""

    def __init__(self, mtl_name: str) -> None:
        self.lines = [f"mtllib {mtl_name}", "usemtl propeller", "s off"]
        self.vertex_count = 0
        self.uv_count = 0

    def object(self, name: str) -> None:
        self.lines.append(f"o {name}")

    def triangle(
        self,
        points: Iterable[tuple[float, float, float]],
        uvs: Iterable[tuple[float, float]],
    ) -> None:
        point_list = list(points)
        uv_list = list(uvs)
        if len(point_list) != 3 or len(uv_list) != 3:
            raise ValueError("OBJ faces must be triangulated")
        vertex_indices: list[int] = []
        uv_indices: list[int] = []
        for x, y, z in point_list:
            self.lines.append(f"v {x:.6f} {y:.6f} {z:.6f}")
            self.vertex_count += 1
            vertex_indices.append(self.vertex_count)
        for u, v in uv_list:
            self.lines.append(f"vt {u:.6f} {v:.6f}")
            self.uv_count += 1
            uv_indices.append(self.uv_count)
        refs = [f"{vertex_indices[i]}/{uv_indices[i]}" for i in range(3)]
        self.lines.append("f " + " ".join(refs))

    def text(self) -> str:
        return "\n".join(self.lines) + "\n"


def map_uv(
    tangent: float,
    radial: float,
    points: tuple[tuple[float, float], ...],
    region: tuple[float, float, float, float],
) -> tuple[float, float]:
    tangents = [point[0] for point in points]
    radials = [point[1] for point in points]
    u0, v0, u1, v1 = region
    u = u0 + (tangent - min(tangents)) / (max(tangents) - min(tangents)) * (u1 - u0)
    v = v0 + (radial - min(radials)) / (max(radials) - min(radials)) * (v1 - v0)
    return u, v


def blade_point(tangent: float, radial: float, angle: float, z: float) -> tuple[float, float, float]:
    theta = math.radians(angle)
    x = 0.5 + tangent * math.cos(theta) + radial * math.sin(theta)
    y = 0.5 - tangent * math.sin(theta) + radial * math.cos(theta)
    return x, y, z


def add_blade_prism(
    mesh: ObjMesh,
    points: tuple[tuple[float, float], ...],
    angle: float,
    region: tuple[float, float, float, float],
    *,
    name: str,
) -> None:
    mesh.object(name)
    front: list[tuple[float, float, float]] = []
    back: list[tuple[float, float, float]] = []
    uvs = [map_uv(tangent, radial, points, region) for tangent, radial in points]
    for tangent, radial in points:
        # Pitch the airfoil around its radial axis and reduce twist toward the tip.
        camber = tangent * (0.24 - radial * 0.09) + (radial - 0.1) * 0.008
        half_thickness = 0.018 - radial * 0.006
        front.append(blade_point(tangent, radial, angle, 0.5 + camber - half_thickness))
        back.append(blade_point(tangent, radial, angle, 0.5 + camber + half_thickness))

    # Front and back surfaces. Fan triangulation is valid for these convex profiles.
    for index in range(1, len(points) - 1):
        mesh.triangle(
            (front[0], front[index + 1], front[index]),
            (uvs[0], uvs[index + 1], uvs[index]),
        )
        mesh.triangle(
            (back[0], back[index], back[index + 1]),
            (uvs[0], uvs[index], uvs[index + 1]),
        )

    # Airfoil edge.
    for index in range(len(points)):
        next_index = (index + 1) % len(points)
        mesh.triangle(
            (front[index], back[next_index], front[next_index]),
            (uvs[index], uvs[next_index], uvs[next_index]),
        )
        mesh.triangle(
            (front[index], back[index], back[next_index]),
            (uvs[index], uvs[index], uvs[next_index]),
        )


def radial_uv(
    x: float,
    y: float,
    radius: float,
    region: tuple[float, float, float, float],
) -> tuple[float, float]:
    u0, v0, u1, v1 = region
    return (
        u0 + (x / radius + 1) * 0.5 * (u1 - u0),
        v0 + (y / radius + 1) * 0.5 * (v1 - v0),
    )


def add_frustum(
    mesh: ObjMesh,
    *,
    name: str,
    z_front: float,
    radius_front: float,
    z_back: float,
    radius_back: float,
    sides: int,
    region: tuple[float, float, float, float],
) -> None:
    mesh.object(name)
    front = [
        (
            0.5 + radius_front * math.cos(2 * math.pi * i / sides),
            0.5 + radius_front * math.sin(2 * math.pi * i / sides),
            z_front,
        )
        for i in range(sides)
    ]
    back = [
        (
            0.5 + radius_back * math.cos(2 * math.pi * i / sides),
            0.5 + radius_back * math.sin(2 * math.pi * i / sides),
            z_back,
        )
        for i in range(sides)
    ]
    center_uv = ((region[0] + region[2]) / 2, (region[1] + region[3]) / 2)
    front_center = (0.5, 0.5, z_front)
    back_center = (0.5, 0.5, z_back)

    for index in range(sides):
        next_index = (index + 1) % sides
        angle = 2 * math.pi * index / sides
        next_angle = 2 * math.pi * next_index / sides
        front_uv = radial_uv(math.cos(angle), math.sin(angle), 1, region)
        next_front_uv = radial_uv(math.cos(next_angle), math.sin(next_angle), 1, region)
        mesh.triangle(
            (front_center, front[next_index], front[index]),
            (center_uv, next_front_uv, front_uv),
        )
        mesh.triangle(
            (back_center, back[index], back[next_index]),
            (center_uv, front_uv, next_front_uv),
        )
        mesh.triangle(
            (front[index], front[next_index], back[next_index]),
            (front_uv, next_front_uv, next_front_uv),
        )
        mesh.triangle(
            (front[index], back[next_index], back[index]),
            (front_uv, next_front_uv, front_uv),
        )


def blade_angles(blades: int) -> list[float]:
    start = 22.5 if blades in (2, 4) else 0.0
    return [start + index * 360 / blades for index in range(blades)]


def geometry(blades: int, mtl_name: str) -> ObjMesh:
    mesh = ObjMesh(mtl_name)
    for index, angle in enumerate(blade_angles(blades)):
        add_blade_prism(mesh, BLADE_BODY, angle, BLADE_UV, name=f"blade_{index}_body")
        add_blade_prism(mesh, BLADE_TIP, angle, TIP_UV, name=f"blade_{index}_tip")

    # Rotor plane and spinner. Twelve sides are visually round in Minecraft but
    # retain a deliberate low-poly style consistent with the reference build.
    add_frustum(
        mesh,
        name="hub_body",
        z_front=0.430,
        radius_front=0.170,
        z_back=0.570,
        radius_back=0.170,
        sides=12,
        region=HUB_UV,
    )
    add_frustum(
        mesh,
        name="spinner_base",
        z_front=0.350,
        radius_front=0.105,
        z_back=0.435,
        radius_back=0.150,
        sides=12,
        region=HUB_UV,
    )
    add_frustum(
        mesh,
        name="spinner_nose",
        z_front=0.290,
        radius_front=0.035,
        z_back=0.352,
        radius_back=0.105,
        sides=12,
        region=HUB_UV,
    )
    add_frustum(
        mesh,
        name="rear_collar",
        z_front=0.565,
        radius_front=0.105,
        z_back=0.675,
        radius_back=0.105,
        sides=10,
        region=SHAFT_UV,
    )
    add_frustum(
        mesh,
        name="rear_drive_shaft",
        z_front=0.650,
        radius_front=0.030,
        z_back=1.000,
        radius_back=0.030,
        sides=8,
        region=SHAFT_UV,
    )
    return mesh


def material_model(texture: str, blades: int) -> dict[str, object]:
    blade_name = {2: "two", 3: "three", 4: "four"}[blades]
    return {
        "loader": "neoforge:obj",
        "model": f"aeronauticsplus:models/block/propellers/geometry/propeller_{blade_name}_blade.obj",
        "automatic_culling": False,
        "shade_quads": True,
        "flip_v": False,
        "emissive_ambient": False,
        "ambientocclusion": False,
        "textures": {"texture0": texture, "particle": texture},
        "display": DISPLAY,
    }


def particle_only_model(texture: str) -> dict[str, object]:
    """Chunk model used while the complete OBJ is rendered by the block entity."""
    return {
        "ambientocclusion": False,
        "textures": {"particle": texture},
        "elements": [],
    }


def animated_blockstate(model: str) -> dict[str, object]:
    rotations: tuple[tuple[str, dict[str, int]], ...] = (
        ("north", {}),
        ("south", {"y": 180}),
        ("west", {"y": 270}),
        ("east", {"y": 90}),
        ("down", {"x": 90}),
        ("up", {"x": -90}),
    )
    variants: dict[str, object] = {}
    for facing, rotation in rotations:
        for reversed_value in ("true", "false"):
            variants[f"facing={facing},reversed={reversed_value}"] = {
                "model": model,
                **rotation,
            }
    return {"variants": variants}


def write_model(path: Path, data: dict[str, object]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")


def main() -> None:
    MODELS.mkdir(parents=True, exist_ok=True)
    ITEM_MODELS.mkdir(parents=True, exist_ok=True)
    BLOCKSTATES.mkdir(parents=True, exist_ok=True)
    GEOMETRY.mkdir(parents=True, exist_ok=True)
    TEXTURES.mkdir(parents=True, exist_ok=True)

    for blades, blade_name in ((2, "two"), (3, "three"), (4, "four")):
        stem = f"propeller_{blade_name}_blade"
        mtl_name = f"{stem}.mtl"
        (GEOMETRY / mtl_name).write_text(
            "newmtl propeller\nKa 1.0 1.0 1.0\nKd 1.0 1.0 1.0\nKs 0.0 0.0 0.0\nd 1.0\nillum 1\nmap_Kd #texture0\n",
            encoding="utf-8",
        )
        (GEOMETRY / f"{stem}.obj").write_text(
            geometry(blades, mtl_name).text(), encoding="utf-8"
        )

    for material in ("wooden", "aluminum", "steel"):
        texture = palette_texture(PALETTES[material], wood_grain=material == "wooden")
        for blades, blade_name in ((2, "two"), (3, "three"), (4, "four")):
            block_id = f"{material}_{blade_name}_blade_propeller"
            texture_id = f"aeronauticsplus:block/propellers/{block_id}"
            generated_model = material_model(texture_id, blades)
            write_model(MODELS / f"{block_id}.json", generated_model)
            write_model(MODELS / f"{block_id}_static.json", particle_only_model(texture_id))
            write_model(ITEM_MODELS / f"{block_id}.json", generated_model)
            write_model(
                BLOCKSTATES / f"{block_id}.json",
                animated_blockstate(f"aeronauticsplus:block/propellers/{block_id}_static"),
            )
            write_png(TEXTURES / f"{block_id}.png", texture)

    prototype_texture = palette_texture(PALETTES["prototype"], wood_grain=False)
    prototype_model = material_model("aeronauticsplus:block/prototype_propeller", 4)
    write_model(PROTOTYPE_MODEL, prototype_model)
    write_model(
        PROTOTYPE_STATIC_MODEL,
        particle_only_model("aeronauticsplus:block/prototype_propeller"),
    )
    write_model(ITEM_MODELS / "prototype_propeller.json", prototype_model)
    write_model(
        BLOCKSTATES / "prototype_propeller.json",
        animated_blockstate("aeronauticsplus:block/prototype_propeller_static"),
    )
    write_png(PROTOTYPE_TEXTURE, prototype_texture)

    print("Generated 10 original OBJ propellers with continuous airfoils and rear-only shafts.")


if __name__ == "__main__":
    main()
