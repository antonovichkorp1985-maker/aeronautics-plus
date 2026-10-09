package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.EngineState;
import dev.leeeonidys.aeronauticsplus.space.core.TankState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselComponent;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import dev.leeeonidys.aeronauticsplus.space.core.VesselCompilation;
import dev.leeeonidys.aeronauticsplus.space.core.VesselDiagnostic;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts a freeform block grid into a {@link VesselBlueprint}.
 * Stage islands are flood-filled through structural ports; separators cut that graph.
 */
public final class VesselBlockCompiler {
    private VesselBlockCompiler() {
    }

    public static VesselCompilation analyze(VesselBlockGrid grid) {
        List<VesselDiagnostic> extra = new ArrayList<>(CellPacking.diagnostics(grid));
        extra.addAll(transporterDiagnostics(grid));
        return compile(grid).analyze().withDiagnostics(extra);
    }

    private static List<VesselDiagnostic> transporterDiagnostics(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            return List.of();
        }
        for (VesselBlockOccupant occupant : grid.occupants()) {
            if (occupant.spec().kind() == VesselPartKind.MOUNT) {
                return List.of(new VesselDiagnostic(
                        VesselDiagnostic.Severity.ERROR,
                        "ON_TRANSPORTER",
                        "Пакет на креплении поезда: старт запрещён, пока крепление не снято"));
            }
        }
        return List.of();
    }

    public static VesselBlueprint compile(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            return new VesselBlueprint(List.of(), List.of());
        }
        GridPos origin = grid.origin();
        Map<GridPos, List<VesselBlockOccupant>> byPos = index(grid.occupants());
        Map<GridPos, String> stageByPos = assignStages(grid.occupants(), byPos);
        List<VesselComponent> components = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            components.add(toComponent(
                    occupant,
                    stageByPos.get(occupant.pos()),
                    occupant.pos().occupancyCentroidMeters(origin, occupant.spec().occupancy())));
        }
        return new VesselBlueprint(components, buildConnections(byPos));
    }

    private static Map<GridPos, List<VesselBlockOccupant>> index(List<VesselBlockOccupant> occupants) {
        Map<GridPos, List<VesselBlockOccupant>> byPos = new LinkedHashMap<>();
        for (VesselBlockOccupant occupant : occupants) {
            byPos.computeIfAbsent(occupant.pos(), ignored -> new ArrayList<>()).add(occupant);
        }
        return byPos;
    }

    private static Map<GridPos, String> assignStages(
            List<VesselBlockOccupant> occupants, Map<GridPos, List<VesselBlockOccupant>> byPos) {
        Set<GridPos> visited = new HashSet<>();
        List<List<GridPos>> islands = new ArrayList<>();
        for (VesselBlockOccupant occupant : occupants) {
            if (!visited.add(occupant.pos())) {
                continue;
            }
            List<GridPos> island = new ArrayList<>();
            ArrayDeque<GridPos> pending = new ArrayDeque<>();
            pending.add(occupant.pos());
            while (!pending.isEmpty()) {
                GridPos current = pending.remove();
                island.add(current);
                List<VesselBlockOccupant> here = byPos.get(current);
                for (BlockFace face : BlockFace.values()) {
                    GridPos neighbourPos = current.offset(face);
                    List<VesselBlockOccupant> neighbours = byPos.get(neighbourPos);
                    if (neighbours == null || visited.contains(neighbourPos)) {
                        continue;
                    }
                    if (structurallyLinked(here, neighbours, face)) {
                        visited.add(neighbourPos);
                        pending.add(neighbourPos);
                    }
                }
            }
            island.sort(Comparator.naturalOrder());
            islands.add(island);
        }
        islands.sort(Comparator.comparing(island -> island.get(0)));
        Map<GridPos, String> stageByPos = new HashMap<>();
        for (int index = 0; index < islands.size(); index++) {
            String stageId = "stage-" + index;
            for (GridPos pos : islands.get(index)) {
                stageByPos.put(pos, stageId);
            }
        }
        return stageByPos;
    }

    private static List<VesselConnection> buildConnections(Map<GridPos, List<VesselBlockOccupant>> byPos) {
        List<VesselConnection> connections = new ArrayList<>();
        for (Map.Entry<GridPos, List<VesselBlockOccupant>> entry : byPos.entrySet()) {
            List<VesselBlockOccupant> cell = entry.getValue();
            for (int i = 0; i < cell.size(); i++) {
                for (int j = i + 1; j < cell.size(); j++) {
                    if (!cell.get(i).spec().occupancy().intersects(cell.get(j).spec().occupancy())) {
                        connections.add(new VesselConnection(
                                cell.get(i).componentId(),
                                cell.get(j).componentId(),
                                VesselConnection.ConnectionKind.STRUCTURAL));
                    }
                }
            }
            for (VesselBlockOccupant occupant : cell) {
                for (BlockFace face : BlockFace.values()) {
                    GridPos neighbourPos = occupant.pos().offset(face);
                    if (occupant.pos().compareTo(neighbourPos) >= 0) {
                        continue;
                    }
                    List<VesselBlockOccupant> neighbours = byPos.get(neighbourPos);
                    if (neighbours == null) {
                        continue;
                    }
                    for (VesselBlockOccupant neighbour : neighbours) {
                        for (VesselConnection.ConnectionKind kind : sharedKinds(occupant, neighbour, face)) {
                            connections.add(new VesselConnection(
                                    occupant.componentId(), neighbour.componentId(), kind));
                        }
                    }
                }
            }
        }
        return connections;
    }

    private static boolean structurallyLinked(
            List<VesselBlockOccupant> here, List<VesselBlockOccupant> neighbours, BlockFace face) {
        for (VesselBlockOccupant from : here) {
            for (VesselBlockOccupant to : neighbours) {
                if (shares(from, to, face, VesselConnection.ConnectionKind.STRUCTURAL)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Set<VesselConnection.ConnectionKind> sharedKinds(
            VesselBlockOccupant from, VesselBlockOccupant to, BlockFace face) {
        Set<VesselConnection.ConnectionKind> shared = EnumSet.noneOf(VesselConnection.ConnectionKind.class);
        Set<VesselConnection.ConnectionKind> outgoing = from.spec().ports(face, from.facing());
        Set<VesselConnection.ConnectionKind> incoming = to.spec().ports(face.opposite(), to.facing());
        for (VesselConnection.ConnectionKind kind : outgoing) {
            if (incoming.contains(kind)) {
                shared.add(kind);
            }
        }
        return shared;
    }

    private static boolean shares(
            VesselBlockOccupant from, VesselBlockOccupant to, BlockFace face,
            VesselConnection.ConnectionKind kind) {
        return from.spec().ports(face, from.facing()).contains(kind)
                && to.spec().ports(face.opposite(), to.facing()).contains(kind);
    }

    private static VesselComponent toComponent(VesselBlockOccupant occupant, String stageId, Vector3d position) {
        String id = occupant.componentId();
        VesselPartSpec spec = occupant.spec();
        return switch (spec.kind()) {
            case STRUCTURE, SEPARATOR, MOUNT -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.STRUCTURE, spec.massKg(), position);
            case FAIRING -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.FAIRING, spec.massKg(), position);
            case PAYLOAD -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.PAYLOAD, spec.massKg(), position);
            case HABITAT -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.HABITAT, spec.massKg(), position);
            case SOLAR -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.SOLAR, spec.massKg(), position);
            case GYRO -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.GYRO, spec.massKg(), position);
            case DOCKING -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.DOCKING, spec.massKg(), position);
            case BATTERY -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.BATTERY, spec.massKg(), position);
            case RADIATOR -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.RADIATOR, spec.massKg(), position);
            case ANTENNA -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.ANTENNA, spec.massKg(), position);
            case LAB -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.LAB, spec.massKg(), position);
            case TANK -> VesselComponent.tank(id, stageId, new TankState(
                    id, spec.massKg(), spec.tankCapacityKg(), spec.defaultPropellant(), position));
            case ENGINE -> VesselComponent.engine(id, stageId, new EngineState(
                    id, spec.fuelId(), spec.oxidizerId(), spec.massKg(), spec.thrustNewtons(),
                    spec.specificImpulseSeconds(), spec.mixtureRatio(), 1.0, 0.0, true, position,
                    occupant.facing().opposite().vector(), false));
            case RCS -> VesselComponent.rcs(id, stageId, new EngineState(
                    id, spec.fuelId(), spec.oxidizerId(), spec.massKg(), spec.thrustNewtons(),
                    spec.specificImpulseSeconds(), spec.mixtureRatio(), 1.0, 0.0, true, position,
                    occupant.facing().opposite().vector(), true));
        };
    }
}
