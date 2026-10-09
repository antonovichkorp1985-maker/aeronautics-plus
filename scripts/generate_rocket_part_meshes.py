#!/usr/bin/env python3
"""Generate original Minecraft-scale OBJ meshes for rocket parts.

Silhouettes follow public-domain NASA RS-25 / Saturn V stage geometry
(bell nozzle, cylindrical tank, isogrid barrel, clamp-band decoupler).
Meshes, UVs and textures are authored here; they are not copies of
NASA high-poly files or other Minecraft mods.

Face winding is outward (CCW when viewed from outside) so Minecraft's
back-face culling shows the outer skin, not the hollow interior.
"""

from __future__ import annotations

import json
import math
import struct
import zlib
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
GEO = ROOT / "src/main/resources/assets/aeronauticsplus/models/block/rockets/geometry"
TEX = ROOT / "src/main/resources/assets/aeronauticsplus/textures/block"
ASSETS = ROOT / "src/main/resources/assets/aeronauticsplus"

MTL = """newmtl propeller
Ka 1.0 1.0 1.0
Kd 1.0 1.0 1.0
Ks 0.0 0.0 0.0
d 1.0
illum 1
map_Kd #texture0
"""


def normal(a, b, c) -> tuple[float, float, float]:
    ux, uy, uz = b[0] - a[0], b[1] - a[1], b[2] - a[2]
    vx, vy, vz = c[0] - a[0], c[1] - a[1], c[2] - a[2]
    nx = uy * vz - uz * vy
    ny = uz * vx - ux * vz
    nz = ux * vy - uy * vx
    length = math.hypot(nx, ny, nz) or 1.0
    return (nx / length, ny / length, nz / length)


class Mesh:
    def __init__(self) -> None:
        self.objects: list[tuple[str, list[tuple]]] = []

    def add(self, name: str, triangles: list[tuple]) -> None:
        if triangles:
            self.objects.append((name, triangles))

    def write(self, path: Path) -> None:
        lines = ["mtllib " + path.with_suffix(".mtl").name, "usemtl propeller", "s off"]
        index = 1
        for name, triangles in self.objects:
            lines.append(f"o {name}")
            for a, b, c, uvs in triangles:
                nx, ny, nz = normal(a, b, c)
                for vertex, uv in zip((a, b, c), uvs):
                    lines.append(f"v {vertex[0]:.6f} {vertex[1]:.6f} {vertex[2]:.6f}")
                    lines.append(f"vt {uv[0]:.6f} {uv[1]:.6f}")
                    lines.append(f"vn {nx:.6f} {ny:.6f} {nz:.6f}")
                lines.append(
                    f"f {index}/{index}/{index} {index + 1}/{index + 1}/{index + 1} "
                    f"{index + 2}/{index + 2}/{index + 2}"
                )
                index += 3
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        path.with_suffix(".mtl").write_text(MTL, encoding="utf-8")


def tri(a, b, c, ua, ub, uc) -> tuple:
    return (a, b, c, (ua, ub, uc))


def circle(cx: float, cy: float, cz: float, radius: float, axis: str, i: int, n: int) -> tuple[float, float, float]:
    ang = 2.0 * math.pi * i / n
    c, s = math.cos(ang), math.sin(ang)
    if axis == "y":
        return (cx + radius * c, cy, cz + radius * s)
    if axis == "x":
        return (cx, cy + radius * c, cz + radius * s)
    return (cx + radius * c, cy + radius * s, cz)


def frustum(y0: float, r0: float, y1: float, r1: float, n: int, u0: float, u1: float, invert: bool = False) -> list:
    """Side of a cone/cylinder. invert=True for inner walls (normals toward axis)."""
    tris = []
    for i in range(n):
        j = (i + 1) % n
        a = circle(0.5, y0, 0.5, r0, "y", i, n)
        b = circle(0.5, y0, 0.5, r0, "y", j, n)
        c = circle(0.5, y1, 0.5, r1, "y", j, n)
        d = circle(0.5, y1, 0.5, r1, "y", i, n)
        ui = i / n
        uj = (i + 1) / n
        if invert:
            tris.append(tri(a, b, c, (ui, u0), (uj, u0), (uj, u1)))
            tris.append(tri(a, c, d, (ui, u0), (uj, u1), (ui, u1)))
        else:
            tris.append(tri(a, d, c, (ui, u0), (ui, u1), (uj, u1)))
            tris.append(tri(a, c, b, (ui, u0), (uj, u1), (uj, u0)))
    return tris


def disk(y: float, radius: float, n: int, up: bool, u_center: tuple[float, float] = (0.5, 0.5)) -> list:
    """Cap. up=True faces +Y (top), up=False faces -Y (bottom)."""
    tris = []
    center = (0.5, y, 0.5)
    for i in range(n):
        j = (i + 1) % n
        a = circle(0.5, y, 0.5, radius, "y", i, n)
        b = circle(0.5, y, 0.5, radius, "y", j, n)
        ua = (u_center[0] + 0.2 * math.cos(2 * math.pi * i / n), u_center[1] + 0.2 * math.sin(2 * math.pi * i / n))
        ub = (u_center[0] + 0.2 * math.cos(2 * math.pi * j / n), u_center[1] + 0.2 * math.sin(2 * math.pi * j / n))
        if up:
            tris.append(tri(center, b, a, u_center, ub, ua))
        else:
            tris.append(tri(center, a, b, u_center, ua, ub))
    return tris


def box(x0, y0, z0, x1, y1, z1, u0=0.05, v0=0.05, u1=0.45, v1=0.45) -> list:
    p = [
        (x0, y0, z0), (x1, y0, z0), (x1, y1, z0), (x0, y1, z0),
        (x0, y0, z1), (x1, y0, z1), (x1, y1, z1), (x0, y1, z1),
    ]
    faces = (
        (0, 3, 2, 1),
        (4, 5, 6, 7),
        (0, 4, 7, 3),
        (1, 2, 6, 5),
        (0, 1, 5, 4),
        (3, 7, 6, 2),
    )
    uv = ((u0, v0), (u1, v0), (u1, v1), (u0, v1))
    tris = []
    for a, b, c, d in faces:
        tris.append(tri(p[a], p[b], p[c], uv[0], uv[1], uv[2]))
        tris.append(tri(p[a], p[c], p[d], uv[0], uv[2], uv[3]))
    return tris


def cylinder_x(x0, x1, cy, cz, radius, n, u0, u1) -> list:
    tris = []
    for i in range(n):
        j = (i + 1) % n
        a = circle(x0, cy, cz, radius, "x", i, n)
        b = circle(x0, cy, cz, radius, "x", j, n)
        c = circle(x1, cy, cz, radius, "x", j, n)
        d = circle(x1, cy, cz, radius, "x", i, n)
        ui, uj = i / n, (i + 1) / n
        tris.append(tri(a, d, c, (ui, u0), (ui, u1), (uj, u1)))
        tris.append(tri(a, c, b, (ui, u0), (uj, u1), (uj, u0)))
    return tris


def engine_mesh() -> Mesh:
    mesh = Mesh()
    n = 24
    # RS-25 silhouette scaled into one metre cell. No cooling tubes through the bell.
    mesh.add(
        "bell_outer",
        frustum(0.02, 0.47, 0.30, 0.20, n, 0.02, 0.42)
        + frustum(0.30, 0.20, 0.42, 0.10, n, 0.42, 0.55)
        + frustum(0.42, 0.10, 0.50, 0.12, n, 0.55, 0.62),
    )
    mesh.add(
        "bell_inner",
        frustum(0.04, 0.43, 0.30, 0.17, n, 0.78, 0.90, invert=True)
        + frustum(0.30, 0.17, 0.42, 0.08, n, 0.90, 0.97, invert=True)
        + disk(0.04, 0.43, n, False, (0.80, 0.22)),
    )
    mesh.add(
        "chamber",
        frustum(0.50, 0.17, 0.78, 0.17, n, 0.05, 0.28)
        + disk(0.78, 0.17, n, True, (0.22, 0.78)),
    )
    mesh.add(
        "gimbal",
        frustum(0.78, 0.20, 0.90, 0.16, n, 0.30, 0.40)
        + frustum(0.90, 0.16, 0.98, 0.10, n, 0.40, 0.48)
        + disk(0.98, 0.10, n, True, (0.22, 0.22)),
    )
    pumps = []
    pumps.extend(cylinder_x(0.06, 0.30, 0.68, 0.50, 0.07, 12, 0.50, 0.70))
    pumps.extend(cylinder_x(0.70, 0.94, 0.68, 0.50, 0.07, 12, 0.50, 0.70))
    pumps.extend(box(0.28, 0.62, 0.46, 0.36, 0.74, 0.54, 0.72, 0.55, 0.88, 0.72))
    pumps.extend(box(0.64, 0.62, 0.46, 0.72, 0.74, 0.54, 0.72, 0.55, 0.88, 0.72))
    mesh.add("turbopumps", pumps)
    return mesh


def tank_mesh() -> Mesh:
    mesh = Mesh()
    n = 24
    # Closed barrel: outer skin + full bulkheads. No sight-line through the tank.
    mesh.add("barrel", frustum(0.12, 0.44, 0.88, 0.44, n, 0.02, 0.50))
    mesh.add(
        "lower_dome",
        frustum(0.02, 0.28, 0.12, 0.44, n, 0.50, 0.64) + disk(0.02, 0.28, n, False, (0.22, 0.22)),
    )
    mesh.add(
        "upper_dome",
        frustum(0.88, 0.44, 0.98, 0.28, n, 0.64, 0.78) + disk(0.98, 0.28, n, True, (0.22, 0.78)),
    )
    mesh.add("band", frustum(0.44, 0.455, 0.56, 0.455, n, 0.82, 0.96))
    stringers = []
    for i in range(8):
        ang = 2 * math.pi * i / 8
        dx, dz = 0.45 * math.cos(ang), 0.45 * math.sin(ang)
        stringers.extend(
            box(
                0.5 + dx - 0.014,
                0.14,
                0.5 + dz - 0.014,
                0.5 + dx + 0.014,
                0.86,
                0.5 + dz + 0.014,
                0.40,
                0.05,
                0.52,
                0.40,
            )
        )
    mesh.add("stringers", stringers)
    mesh.add("feed", box(0.46, 0.00, 0.46, 0.54, 0.04, 0.54, 0.10, 0.80, 0.25, 0.95))
    return mesh


def structure_mesh() -> Mesh:
    mesh = Mesh()
    n = 16
    mesh.add(
        "skin",
        frustum(0.02, 0.36, 0.98, 0.36, n, 0.05, 0.70, invert=True)
        + frustum(0.02, 0.40, 0.98, 0.40, n, 0.05, 0.70),
    )
    rings = []
    for y in (0.08, 0.50, 0.92):
        rings.extend(frustum(y - 0.03, 0.42, y + 0.03, 0.42, n, 0.72, 0.85))
    mesh.add("rings", rings)
    longerons = []
    for i in range(4):
        ang = math.pi / 4 + i * math.pi / 2
        dx, dz = 0.41 * math.cos(ang), 0.41 * math.sin(ang)
        longerons.extend(
            box(
                0.5 + dx - 0.025,
                0.02,
                0.5 + dz - 0.025,
                0.5 + dx + 0.025,
                0.98,
                0.5 + dz + 0.025,
                0.40,
                0.05,
                0.55,
                0.45,
            )
        )
    mesh.add("longerons", longerons)
    braces = []
    for y0 in (0.10, 0.52):
        braces.extend(box(0.12, y0, 0.48, 0.88, y0 + 0.03, 0.52, 0.60, 0.70, 0.80, 0.90))
        braces.extend(box(0.48, y0, 0.12, 0.52, y0 + 0.03, 0.88, 0.60, 0.70, 0.80, 0.90))
    mesh.add("braces", braces)
    return mesh


def separator_mesh() -> Mesh:
    mesh = Mesh()
    n = 16
    mesh.add(
        "clamp",
        frustum(0.42, 0.40, 0.58, 0.40, n, 0.05, 0.40)
        + frustum(0.42, 0.32, 0.58, 0.32, n, 0.05, 0.40, invert=True),
    )
    petals = []
    for i in range(8):
        ang = 2 * math.pi * i / 8
        c, s = math.cos(ang), math.sin(ang)
        x, z = 0.5 + 0.30 * c, 0.5 + 0.30 * s
        petals.extend(box(x - 0.04, 0.58, z - 0.04, x + 0.04, 0.92, z + 0.04, 0.70, 0.10, 0.90, 0.45))
        petals.extend(box(x - 0.04, 0.08, z - 0.04, x + 0.04, 0.42, z + 0.04, 0.70, 0.10, 0.90, 0.45))
    mesh.add("petals", petals)
    mesh.add("bolts", box(0.46, 0.36, 0.46, 0.54, 0.64, 0.54, 0.20, 0.70, 0.40, 0.90))
    return mesh


def fairing_mesh() -> Mesh:
    """Ogive shroud: closed nose, open clamp ring, inner skin. Not a copied NASA fairing."""
    mesh = Mesh()
    n = 24
    mesh.add(
        "ogive",
        frustum(0.08, 0.42, 0.55, 0.34, n, 0.02, 0.40)
        + frustum(0.55, 0.34, 0.88, 0.14, n, 0.40, 0.70)
        + frustum(0.88, 0.14, 0.98, 0.03, n, 0.70, 0.86)
        + disk(0.98, 0.03, n, True, (0.22, 0.78)),
    )
    mesh.add(
        "inner",
        frustum(0.12, 0.38, 0.55, 0.30, n, 0.78, 0.88, invert=True)
        + frustum(0.55, 0.30, 0.86, 0.12, n, 0.88, 0.96, invert=True),
    )
    mesh.add(
        "clamp",
        frustum(0.00, 0.44, 0.08, 0.44, n, 0.05, 0.22)
        + frustum(0.00, 0.32, 0.08, 0.32, n, 0.05, 0.22, invert=True)
        + frustum(0.08, 0.42, 0.12, 0.42, n, 0.22, 0.30),
    )
    seam = []
    seam.extend(box(0.48, 0.10, 0.08, 0.52, 0.90, 0.12, 0.70, 0.10, 0.90, 0.55))
    seam.extend(box(0.48, 0.10, 0.88, 0.52, 0.90, 0.92, 0.70, 0.10, 0.90, 0.55))
    mesh.add("seam", seam)
    return mesh


def habitat_mesh() -> Mesh:
    """Pressurized cabin: barrel, windows, docking ring. Original geometry."""
    mesh = Mesh()
    n = 20
    mesh.add("barrel", frustum(0.08, 0.40, 0.92, 0.40, n, 0.05, 0.55))
    mesh.add(
        "domes",
        frustum(0.00, 0.28, 0.08, 0.40, n, 0.55, 0.68) + disk(0.00, 0.28, n, False, (0.22, 0.22))
        + frustum(0.92, 0.40, 1.00, 0.28, n, 0.68, 0.82) + disk(1.00, 0.28, n, True, (0.22, 0.78)),
    )
    windows = []
    windows.extend(box(0.18, 0.38, 0.72, 0.38, 0.62, 0.82, 0.70, 0.10, 0.90, 0.40))
    windows.extend(box(0.62, 0.38, 0.72, 0.82, 0.62, 0.82, 0.70, 0.10, 0.90, 0.40))
    mesh.add("windows", windows)
    mesh.add("hatch", frustum(0.44, 0.42, 0.56, 0.42, n, 0.82, 0.95))
    return mesh


def solar_mesh() -> Mesh:
    """Thin photovoltaic wing with a boom. Not a full cube."""
    mesh = Mesh()
    mesh.add("boom", box(0.46, 0.46, 0.10, 0.54, 0.54, 0.36, 0.70, 0.70, 0.88, 0.90))
    mesh.add("panel", box(0.02, 0.36, 0.36, 0.98, 0.64, 0.78, 0.08, 0.08, 0.55, 0.55))
    cells = []
    for i in range(4):
        x0 = 0.08 + i * 0.22
        cells.extend(box(x0, 0.40, 0.40, x0 + 0.16, 0.60, 0.74, 0.10, 0.58, 0.40, 0.88))
    mesh.add("cells", cells)
    return mesh


def gyro_mesh() -> Mesh:
    """Control-moment gyro: rotor disk in a gimbal ring."""
    mesh = Mesh()
    n = 20
    mesh.add(
        "rotor",
        frustum(0.42, 0.28, 0.58, 0.28, n, 0.05, 0.40)
        + disk(0.42, 0.28, n, False, (0.22, 0.22))
        + disk(0.58, 0.28, n, True, (0.22, 0.78)),
    )
    mesh.add(
        "gimbal",
        frustum(0.30, 0.34, 0.34, 0.34, n, 0.50, 0.65)
        + frustum(0.66, 0.34, 0.70, 0.34, n, 0.50, 0.65),
    )
    mesh.add(
        "case",
        frustum(0.22, 0.22, 0.78, 0.22, n, 0.70, 0.90)
        + disk(0.22, 0.22, n, False, (0.80, 0.22))
        + disk(0.78, 0.22, n, True, (0.80, 0.78)),
    )
    return mesh


def rcs_mesh() -> Mesh:
    """Four small RCS bells on a pod. Not a main engine."""
    mesh = Mesh()
    n = 12
    mesh.add("pod", box(0.36, 0.20, 0.36, 0.64, 0.80, 0.64, 0.40, 0.10, 0.62, 0.50))
    bells = []
    for dx, dz in ((0.0, -0.22), (0.0, 0.22), (-0.22, 0.0), (0.22, 0.0)):
        bells.extend(frustum(0.08, 0.10, 0.28, 0.06, n, 0.70, 0.90))
        # offset copies: shift by rewriting via boxes for side bells
        bells.extend(box(0.46 + dx, 0.04, 0.46 + dz, 0.54 + dx, 0.22, 0.54 + dz, 0.70, 0.55, 0.90, 0.80))
    mesh.add("bells", bells)
    return mesh


def payload_mesh() -> Mesh:
    """Satellite bus with panel stubs and a dish. Original Minecraft-scale geometry."""
    mesh = Mesh()
    n = 16
    mesh.add(
        "bus",
        box(0.32, 0.22, 0.32, 0.68, 0.70, 0.68, 0.08, 0.08, 0.42, 0.55),
    )
    mesh.add(
        "radiator",
        box(0.36, 0.70, 0.36, 0.64, 0.74, 0.64, 0.45, 0.08, 0.62, 0.22),
    )
    panels = []
    panels.extend(box(0.02, 0.38, 0.46, 0.30, 0.62, 0.54, 0.10, 0.60, 0.40, 0.88))
    panels.extend(box(0.70, 0.38, 0.46, 0.98, 0.62, 0.54, 0.10, 0.60, 0.40, 0.88))
    mesh.add("panels", panels)
    mesh.add(
        "dish",
        frustum(0.74, 0.06, 0.88, 0.16, n, 0.70, 0.90)
        + frustum(0.74, 0.04, 0.86, 0.13, n, 0.70, 0.90, invert=True)
        + disk(0.74, 0.06, n, False, (0.80, 0.22)),
    )
    mesh.add("boom", box(0.48, 0.70, 0.48, 0.52, 0.78, 0.52, 0.72, 0.70, 0.88, 0.90))
    return mesh


def mount_mesh() -> Mesh:
    """Train-car cradle: rails, ring, hold-down clamps. Not a homemade crawler."""
    mesh = Mesh()
    n = 16
    rails = []
    rails.extend(box(0.06, 0.00, 0.06, 0.20, 0.08, 0.94, 0.05, 0.05, 0.40, 0.35))
    rails.extend(box(0.80, 0.00, 0.06, 0.94, 0.08, 0.94, 0.05, 0.05, 0.40, 0.35))
    rails.extend(box(0.06, 0.00, 0.06, 0.94, 0.08, 0.20, 0.05, 0.05, 0.40, 0.35))
    rails.extend(box(0.06, 0.00, 0.80, 0.94, 0.08, 0.94, 0.05, 0.05, 0.40, 0.35))
    mesh.add("rails", rails)
    mesh.add(
        "ring",
        frustum(0.08, 0.42, 0.16, 0.42, n, 0.45, 0.70)
        + frustum(0.08, 0.28, 0.16, 0.28, n, 0.45, 0.70, invert=True),
    )
    clamps = []
    for i in range(4):
        ang = math.pi / 4 + i * math.pi / 2
        dx, dz = 0.38 * math.cos(ang), 0.38 * math.sin(ang)
        clamps.extend(
            box(
                0.5 + dx - 0.04,
                0.08,
                0.5 + dz - 0.04,
                0.5 + dx + 0.04,
                0.42,
                0.5 + dz + 0.04,
                0.72,
                0.10,
                0.95,
                0.55,
            )
        )
        clamps.extend(
            box(
                0.5 + dx - 0.06,
                0.38,
                0.5 + dz - 0.06,
                0.5 + dx + 0.06,
                0.46,
                0.5 + dz + 0.06,
                0.72,
                0.55,
                0.95,
                0.80,
            )
        )
    mesh.add("clamps", clamps)
    return mesh


def chunk(tag: bytes, data: bytes) -> bytes:
    return struct.pack(">I", len(data)) + tag + data + struct.pack(">I", zlib.crc32(tag + data) & 0xFFFFFFFF)


def write_png(path: Path, pixels: list[list[tuple[int, int, int]]]) -> None:
    height, width = len(pixels), len(pixels[0])
    raw = b"".join(b"\x00" + bytes(c for px in row for c in px) for row in pixels)
    png = b"\x89PNG\r\n\x1a\n"
    png += chunk(b"IHDR", struct.pack(">IIBBBBB", width, height, 8, 2, 0, 0, 0))
    png += chunk(b"IDAT", zlib.compress(raw, 9))
    png += chunk(b"IEND", b"")
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(png)


def paint(size: int, fn) -> list[list[tuple[int, int, int]]]:
    return [[fn(x, y, size) for x in range(size)] for y in range(size)]


def engine_tex(x, y, s):
    nx, ny = x / (s - 1), y / (s - 1)
    cx, cy = nx - 0.5, ny - 0.5
    r = math.hypot(cx, cy)
    if r < 0.14:
        return (18, 14, 12)
    if r < 0.46:
        t = (r - 0.14) / 0.32
        return (int(168 + 40 * t), int(86 + 18 * t), int(42 + 10 * t))
    if ny > 0.70:
        return (118, 122, 130)
    if (x + y) % 5 == 0:
        return (72, 48, 32)
    return (64, 56, 50)


def tank_tex(x, y, s):
    ny = y / (s - 1)
    if 0.40 <= ny <= 0.58:
        return (198, 92, 28) if (x + y) % 4 else (184, 78, 22)
    if x % 8 == 0 or y in (0, s - 1):
        return (132, 136, 142)
    return (220, 222, 226) if (x + y) % 3 else (206, 210, 216)


def fuel_tank_tex(x, y, s):
    ny = y / (s - 1)
    if 0.40 <= ny <= 0.58:
        return (48, 36, 28) if (x + y) % 4 else (36, 28, 22)
    if x % 8 == 0 or y in (0, s - 1):
        return (118, 96, 64)
    return (168, 92, 36) if (x + y) % 3 else (148, 78, 28)


def oxidizer_tank_tex(x, y, s):
    ny = y / (s - 1)
    if 0.40 <= ny <= 0.58:
        return (210, 232, 240) if (x + y) % 4 else (186, 214, 226)
    if x % 8 == 0 or y in (0, s - 1):
        return (148, 168, 184)
    if (x + y) % 11 == 0:
        return (236, 246, 252)
    return (198, 220, 232) if (x + y) % 3 else (176, 204, 220)


def structure_tex(x, y, s):
    if x % 8 == 0 or y % 8 == 0:
        return (176, 182, 192)
    if (x // 4 + y // 4) % 2 == 0:
        return (58, 62, 70)
    return (44, 48, 54)


def separator_tex(x, y, s):
    ny = y / (s - 1)
    if 0.40 <= ny <= 0.60:
        return (24, 24, 26)
    if (x + y) % 5 == 0:
        return (232, 198, 64)
    return (214, 176, 36)


def fairing_tex(x, y, s):
    ny = y / (s - 1)
    if x in (s // 2, s // 2 - 1):
        return (36, 38, 42)
    if 0.18 <= ny <= 0.24 or 0.72 <= ny <= 0.78:
        return (168, 172, 178)
    return (232, 234, 238) if (x + y) % 5 else (218, 222, 228)


def habitat_tex(x, y, s):
    ny = y / (s - 1)
    if 0.30 <= ny <= 0.46 or 0.58 <= ny <= 0.70:
        return (40, 80, 140) if (x // 6 + y // 6) % 2 else (30, 60, 110)
    if x % 8 == 0:
        return (176, 180, 188)
    return (214, 216, 220) if (x + y) % 4 else (198, 200, 206)


def solar_tex(x, y, s):
    if (x // 8 + y // 8) % 2 == 0:
        return (18, 32, 96)
    return (28, 48, 140)


def gyro_tex(x, y, s):
    nx, ny = x / (s - 1), y / (s - 1)
    r = math.hypot(nx - 0.5, ny - 0.5)
    if r < 0.22:
        return (48, 52, 58)
    if r < 0.38:
        return (168, 172, 180)
    return (88, 92, 98) if (x + y) % 5 else (72, 76, 82)


def rcs_tex(x, y, s):
    ny = y / (s - 1)
    if ny < 0.28:
        return (168, 86, 42)
    if (x + y) % 6 == 0:
        return (90, 94, 100)
    return (118, 122, 130)


def payload_tex(x, y, s):
    nx, ny = x / (s - 1), y / (s - 1)
    if 0.42 <= ny <= 0.58 and (nx < 0.22 or nx > 0.78):
        return (28, 36, 64) if (x // 3 + y // 3) % 2 else (18, 24, 48)
    if (x + y) % 7 == 0:
        return (168, 124, 42)
    return (198, 156, 64) if (x + y) % 3 else (184, 140, 52)


def mount_tex(x, y, s):
    ny = y / (s - 1)
    if 0.18 <= ny <= 0.28 or 0.72 <= ny <= 0.82:
        return (214, 176, 36)
    if x % 8 == 0 or y % 8 == 0:
        return (168, 172, 178)
    return (78, 82, 90) if (x // 4 + y // 4) % 2 == 0 else (58, 62, 70)


JSON_MODEL = """{{
  "loader": "neoforge:obj",
  "model": "aeronauticsplus:models/block/rockets/geometry/{obj_id}.obj",
  "automatic_culling": false,
  "shade_quads": true,
  "flip_v": false,
  "emissive_ambient": false,
  "ambientocclusion": false,
  "textures": {{
    "texture0": "aeronauticsplus:block/{block_id}",
    "particle": "aeronauticsplus:block/{block_id}"
  }},
  "display": {{
    "gui": {{ "rotation": [30, 225, 0], "translation": [0, 0, 0], "scale": [0.72, 0.72, 0.72] }},
    "ground": {{ "rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.46, 0.46, 0.46] }},
    "fixed": {{ "rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [0.68, 0.68, 0.68] }},
    "thirdperson_righthand": {{ "rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.56, 0.56, 0.56] }},
    "thirdperson_lefthand": {{ "rotation": [75, 225, 0], "translation": [0, 2.5, 0], "scale": [0.56, 0.56, 0.56] }},
    "firstperson_righthand": {{ "rotation": [0, 45, 0], "translation": [0, 0, 0], "scale": [0.62, 0.62, 0.62] }},
    "firstperson_lefthand": {{ "rotation": [0, 225, 0], "translation": [0, 0, 0], "scale": [0.62, 0.62, 0.62] }}
  }}
}}
"""

ENGINE_STATES = {
    "facing=down": {},
    "facing=up": {"x": 180},
    "facing=north": {"x": 90, "y": 180},
    "facing=south": {"x": 90},
    "facing=west": {"x": 90, "y": 270},
    "facing=east": {"x": 90, "y": 90},
}

UP_STATES = {
    "facing=up": {},
    "facing=down": {"x": 180},
    "facing=north": {"x": 90},
    "facing=south": {"x": 270},
    "facing=west": {"x": 90, "y": 270},
    "facing=east": {"x": 90, "y": 90},
}


def write_part_models(block_id: str, obj_id: str | None = None) -> None:
    body = JSON_MODEL.format(block_id=block_id, obj_id=obj_id or block_id)
    (ASSETS / "models/block" / f"{block_id}.json").write_text(body, encoding="utf-8")
    (ASSETS / "models/item" / f"{block_id}.json").write_text(body, encoding="utf-8")


def write_blockstate(block_id: str, mapping: dict) -> None:
    variants = {}
    for key, extra in mapping.items():
        entry = {"model": f"aeronauticsplus:block/{block_id}"}
        entry.update(extra)
        variants[key] = entry
    path = ASSETS / "blockstates" / f"{block_id}.json"
    path.write_text(json.dumps({"variants": variants}, indent=2) + "\n", encoding="utf-8")


def assert_outward() -> None:
    sample = frustum(0.0, 0.4, 1.0, 0.4, 16, 0.0, 1.0)[0]
    a, b, c, _ = sample
    nx, ny, nz = normal(a, b, c)
    # Angle-0 station sits on +X; outer normal must point away from the axis.
    if nx <= 0.2:
        raise SystemExit(f"outer frustum winding still inward: n=({nx:.3f},{ny:.3f},{nz:.3f})")
    top = disk(1.0, 0.4, 16, True)[0]
    a, b, c, _ = top
    nx, ny, nz = normal(a, b, c)
    if ny <= 0.2:
        raise SystemExit(f"top disk does not face +Y: n=({nx:.3f},{ny:.3f},{nz:.3f})")


def main() -> None:
    assert_outward()
    engine_mesh().write(GEO / "rocket_engine.obj")
    tank_mesh().write(GEO / "rocket_tank.obj")
    structure_mesh().write(GEO / "rocket_structure.obj")
    separator_mesh().write(GEO / "stage_separator.obj")
    mount_mesh().write(GEO / "rocket_mount.obj")
    fairing_mesh().write(GEO / "rocket_fairing.obj")
    payload_mesh().write(GEO / "rocket_payload.obj")
    habitat_mesh().write(GEO / "crew_habitat.obj")
    solar_mesh().write(GEO / "solar_panel.obj")
    gyro_mesh().write(GEO / "control_gyro.obj")
    rcs_mesh().write(GEO / "rcs_thruster.obj")

    write_png(TEX / "rocket_engine.png", paint(64, engine_tex))
    write_png(TEX / "rocket_tank.png", paint(64, tank_tex))
    write_png(TEX / "rocket_fuel_tank.png", paint(64, fuel_tank_tex))
    write_png(TEX / "rocket_oxidizer_tank.png", paint(64, oxidizer_tank_tex))
    write_png(TEX / "rocket_structure.png", paint(64, structure_tex))
    write_png(TEX / "stage_separator.png", paint(64, separator_tex))
    write_png(TEX / "rocket_mount.png", paint(64, mount_tex))
    write_png(TEX / "rocket_fairing.png", paint(64, fairing_tex))
    write_png(TEX / "rocket_payload.png", paint(64, payload_tex))
    write_png(TEX / "crew_habitat.png", paint(64, habitat_tex))
    write_png(TEX / "solar_panel.png", paint(64, solar_tex))
    write_png(TEX / "control_gyro.png", paint(64, gyro_tex))
    write_png(TEX / "rcs_thruster.png", paint(64, rcs_tex))

    for block_id in (
            "rocket_engine", "rocket_tank", "rocket_structure", "stage_separator",
            "rocket_mount", "rocket_fairing", "rocket_payload",
            "crew_habitat", "solar_panel", "control_gyro", "rcs_thruster"):
        write_part_models(block_id)
    write_part_models("rocket_fuel_tank", "rocket_tank")
    write_part_models("rocket_oxidizer_tank", "rocket_tank")

    write_blockstate("rocket_engine", ENGINE_STATES)
    write_blockstate("rocket_tank", UP_STATES)
    write_blockstate("rocket_fuel_tank", UP_STATES)
    write_blockstate("rocket_oxidizer_tank", UP_STATES)
    write_blockstate("rocket_structure", UP_STATES)
    write_blockstate("stage_separator", UP_STATES)
    write_blockstate("rocket_mount", UP_STATES)
    write_blockstate("rocket_fairing", UP_STATES)
    write_blockstate("rocket_payload", UP_STATES)
    write_blockstate("crew_habitat", UP_STATES)
    write_blockstate("solar_panel", UP_STATES)
    write_blockstate("control_gyro", UP_STATES)
    write_blockstate("rcs_thruster", ENGINE_STATES)
    print("rocket part meshes, textures and models written")


if __name__ == "__main__":
    main()
