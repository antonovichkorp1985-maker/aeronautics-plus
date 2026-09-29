"""Create the Aeronautics Plus helicopter/coaxial-rotor Blender template.

Run with Blender 4.x:
    blender --background --python scripts/blender/create_helicopter_rotor_template.py -- \
      --output AeronauticsPlus-Helicopter-Rotor-Template.blend

The scene is authored in Blender coordinates (Z up). Export to Minecraft OBJ with
Forward=-Z and Up=Y. One Blender metre is one Minecraft block.
"""

from __future__ import annotations

import argparse
import math
import sys
from pathlib import Path

import bpy


# With OBJ export Forward=-Z / Up=Y, Blender (x, y, z) maps to
# Minecraft OBJ (x, z, -y). Thus Blender Y=-0.5 is Minecraft Z=+0.5.
ROOT = (0.5, -0.5, 0.5)
UPPER_PIVOT = (0.5, -0.5, 0.78)
LOWER_PIVOT = (0.5, -0.5, 0.56)
ROTOR_RADIUS = 2.5
BLADE_ROOT = 0.29
BLADE_TIP_START = 2.20
HUB_RADIUS = 0.22
HUB_HEIGHT = 0.10
BLADE_THICKNESS = 0.055
ROOT_CHORD = 0.34
TIP_CHORD = 0.14
BLADE_COUNT = 4


def parse_args() -> argparse.Namespace:
    argv = sys.argv[sys.argv.index("--") + 1 :] if "--" in sys.argv else []
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--output",
        type=Path,
        default=Path.cwd() / "AeronauticsPlus-Helicopter-Rotor-Template.blend",
    )
    return parser.parse_args(argv)


def clear_scene() -> None:
    bpy.ops.object.select_all(action="SELECT")
    bpy.ops.object.delete(use_global=False)
    for collection in list(bpy.data.collections):
        bpy.data.collections.remove(collection)


def collection(name: str) -> bpy.types.Collection:
    result = bpy.data.collections.new(name)
    bpy.context.scene.collection.children.link(result)
    return result


def material(name: str, color: tuple[float, float, float, float]) -> bpy.types.Material:
    result = bpy.data.materials.new(name)
    result.diffuse_color = color
    result.use_nodes = True
    principled = result.node_tree.nodes.get("Principled BSDF")
    if principled:
        principled.inputs["Base Color"].default_value = color
        principled.inputs["Roughness"].default_value = 0.58
        principled.inputs["Metallic"].default_value = 0.62 if "METAL" in name else 0.08
    return result


def relink(obj: bpy.types.Object, target: bpy.types.Collection) -> None:
    for source in list(obj.users_collection):
        source.objects.unlink(obj)
    target.objects.link(obj)


def add_cylinder(
    name: str,
    target: bpy.types.Collection,
    radius: float,
    depth: float,
    location: tuple[float, float, float],
    mat: bpy.types.Material,
    vertices: int = 24,
) -> bpy.types.Object:
    bpy.ops.mesh.primitive_cylinder_add(vertices=vertices, radius=radius, depth=depth, location=location)
    obj = bpy.context.object
    obj.name = name
    obj.data.name = f"{name}_MESH"
    obj.data.materials.append(mat)
    relink(obj, target)
    bpy.ops.object.shade_smooth()
    return obj


def transform_point(
    radial: float,
    tangent: float,
    height: float,
    blade_angle: float,
    pitch: float,
    sweep: float,
) -> tuple[float, float, float]:
    tangent += radial * math.tan(sweep)
    pitched_tangent = tangent * math.cos(pitch) - height * math.sin(pitch)
    pitched_height = tangent * math.sin(pitch) + height * math.cos(pitch)
    c, s = math.cos(blade_angle), math.sin(blade_angle)
    return (
        radial * c - pitched_tangent * s,
        radial * s + pitched_tangent * c,
        pitched_height,
    )


def append_blade_segment(
    vertices: list[tuple[float, float, float]],
    faces: list[tuple[int, int, int, int]],
    face_materials: list[int],
    r0: float,
    r1: float,
    chord0: float,
    chord1: float,
    blade_angle: float,
    pitch: float,
    sweep: float,
    material_index: int,
) -> None:
    start = len(vertices)
    for height in (-BLADE_THICKNESS / 2, BLADE_THICKNESS / 2):
        vertices.extend(
            [
                transform_point(r0, -chord0 / 2, height, blade_angle, pitch, sweep),
                transform_point(r0, chord0 / 2, height, blade_angle, pitch, sweep),
                transform_point(r1, chord1 / 2, height, blade_angle, pitch, sweep),
                transform_point(r1, -chord1 / 2, height, blade_angle, pitch, sweep),
            ]
        )
    faces.extend(
        [
            (start + 0, start + 3, start + 2, start + 1),
            (start + 4, start + 5, start + 6, start + 7),
            (start + 0, start + 1, start + 5, start + 4),
            (start + 1, start + 2, start + 6, start + 5),
            (start + 2, start + 3, start + 7, start + 6),
            (start + 3, start + 0, start + 4, start + 7),
        ]
    )
    face_materials.extend([material_index] * 6)


def append_hub(
    vertices: list[tuple[float, float, float]],
    faces: list[tuple[int, ...]],
    face_materials: list[int],
    material_index: int,
) -> None:
    segments = 24
    start = len(vertices)
    for z in (-HUB_HEIGHT / 2, HUB_HEIGHT / 2):
        for i in range(segments):
            angle = math.tau * i / segments
            vertices.append((HUB_RADIUS * math.cos(angle), HUB_RADIUS * math.sin(angle), z))
    for i in range(segments):
        nxt = (i + 1) % segments
        faces.append((start + i, start + nxt, start + segments + nxt, start + segments + i))
        face_materials.append(material_index)
    faces.append(tuple(start + i for i in reversed(range(segments))))
    faces.append(tuple(start + segments + i for i in range(segments)))
    face_materials.extend([material_index, material_index])


def create_rotor(
    name: str,
    target: bpy.types.Collection,
    pivot: tuple[float, float, float],
    pitch_degrees: float,
    phase_degrees: float,
    blade_mat: bpy.types.Material,
    tip_mat: bpy.types.Material,
    metal_mat: bpy.types.Material,
    direction: str,
) -> bpy.types.Object:
    vertices: list[tuple[float, float, float]] = []
    faces: list[tuple[int, ...]] = []
    face_materials: list[int] = []
    append_hub(vertices, faces, face_materials, 2)
    pitch = math.radians(pitch_degrees)
    sweep = math.radians(4.0)
    for blade in range(BLADE_COUNT):
        angle = math.radians(phase_degrees + blade * (360.0 / BLADE_COUNT))
        append_blade_segment(
            vertices,
            faces,
            face_materials,
            BLADE_ROOT,
            BLADE_TIP_START,
            ROOT_CHORD,
            TIP_CHORD + 0.035,
            angle,
            pitch,
            sweep,
            0,
        )
        append_blade_segment(
            vertices,
            faces,
            face_materials,
            BLADE_TIP_START,
            ROTOR_RADIUS,
            TIP_CHORD + 0.035,
            TIP_CHORD,
            angle,
            pitch,
            sweep,
            1,
        )
    mesh = bpy.data.meshes.new(f"{name}_MESH")
    mesh.from_pydata(vertices, [], faces)
    mesh.validate(verbose=True)
    mesh.update()
    obj = bpy.data.objects.new(name, mesh)
    target.objects.link(obj)
    obj.location = pivot
    obj.data.materials.append(blade_mat)
    obj.data.materials.append(tip_mat)
    obj.data.materials.append(metal_mat)
    for polygon, index in zip(obj.data.polygons, face_materials):
        polygon.material_index = index
    obj["ap_role"] = "dynamic_rotor"
    obj["ap_pivot_minecraft"] = "0.5,0.78,0.5" if "UPPER" in name else "0.5,0.56,0.5"
    obj["ap_rotation_axis_blender"] = "+Z"
    obj["ap_rotation_axis_minecraft"] = "+Y"
    obj["ap_rotation_direction_top_view"] = direction
    obj["ap_export_separately"] = True
    return obj


def add_axis_guide(
    name: str,
    target: bpy.types.Collection,
    location: tuple[float, float, float],
    size: float,
) -> bpy.types.Object:
    obj = bpy.data.objects.new(name, None)
    obj.empty_display_type = "SINGLE_ARROW"
    obj.empty_display_size = size
    obj.location = location
    target.objects.link(obj)
    obj["ap_no_export"] = True
    return obj


def configure_scene() -> None:
    scene = bpy.context.scene
    scene.unit_settings.system = "METRIC"
    scene.unit_settings.scale_length = 1.0
    scene["ap_coordinate_system"] = "Blender +Z up; export Forward=-Z, Up=Y; Minecraft +Y up"
    scene["ap_scale"] = "1 Blender metre = 1 Minecraft block"
    scene["ap_runtime_pivot"] = "Each dynamic rotor rotates around its own object origin"
    scene["ap_template_version"] = "1.0"


def main() -> None:
    args = parse_args()
    clear_scene()
    configure_scene()

    export = collection("AP_EXPORT")
    guides = collection("AP_GUIDES_NO_EXPORT")

    metal = material("AP_METAL", (0.22, 0.25, 0.28, 1.0))
    blade = material("AP_BLADE", (0.11, 0.13, 0.15, 1.0))
    tip = material("AP_BLADE_TIP", (0.92, 0.72, 0.08, 1.0))
    mechanism = material("AP_MECHANISM", (0.35, 0.18, 0.08, 1.0))

    mount = add_cylinder("STATIC_MOUNT", export, 0.30, 0.18, (0.5, -0.5, 0.22), metal)
    mast = add_cylinder("STATIC_MAST", export, 0.075, 0.48, (0.5, -0.5, 0.51), metal, 16)
    swash = add_cylinder("SWASHPLATE", export, 0.25, 0.075, (0.5, -0.5, 0.40), mechanism)
    swash.scale = (1.0, 1.0, 0.55)
    bpy.context.view_layer.objects.active = swash
    bpy.ops.object.transform_apply(location=False, rotation=False, scale=True)

    for obj, role in ((mount, "static_mount"), (mast, "static_mast"), (swash, "tilting_swashplate")):
        obj["ap_role"] = role
        obj["ap_export_separately"] = obj is swash

    create_rotor(
        "ROTOR_LOWER",
        export,
        LOWER_PIVOT,
        pitch_degrees=-8.0,
        phase_degrees=45.0,
        blade_mat=blade,
        tip_mat=tip,
        metal_mat=metal,
        direction="CW viewed from +Z toward origin",
    )
    create_rotor(
        "ROTOR_UPPER",
        export,
        UPPER_PIVOT,
        pitch_degrees=8.0,
        phase_degrees=0.0,
        blade_mat=blade,
        tip_mat=tip,
        metal_mat=metal,
        direction="CCW viewed from +Z toward origin",
    )

    root = bpy.data.objects.new("AP_ROOT__MC_BLOCK_CENTER", None)
    root.empty_display_type = "PLAIN_AXES"
    root.empty_display_size = 0.3
    root.location = ROOT
    guides.objects.link(root)
    root["ap_no_export"] = True
    add_axis_guide("AXIS_UPPER__NO_EXPORT", guides, UPPER_PIVOT, 0.45)
    add_axis_guide("AXIS_LOWER__NO_EXPORT", guides, LOWER_PIVOT, 0.35)

    args.output.parent.mkdir(parents=True, exist_ok=True)
    bpy.ops.wm.save_as_mainfile(filepath=str(args.output.resolve()))
    print(f"Saved Aeronautics Plus rotor template: {args.output.resolve()}")
    print("Export only objects in AP_EXPORT; export ROTOR_UPPER and ROTOR_LOWER separately.")


if __name__ == "__main__":
    main()
