package dev.leeeonidys.aeronauticsplus.space.compile;

import dev.leeeonidys.aeronauticsplus.space.core.EngineState;
import dev.leeeonidys.aeronauticsplus.space.core.TankState;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.core.VesselBlueprint;
import dev.leeeonidys.aeronauticsplus.space.core.VesselComponent;
import dev.leeeonidys.aeronauticsplus.space.core.VesselConnection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
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

    public static VesselBlueprint compile(VesselBlockGrid grid) {
        if (grid == null || grid.isEmpty()) {
            return new VesselBlueprint(List.of(), List.of());
        }
        GridPos origin = grid.origin();
        Map<GridPos, VesselBlockOccupant> byPos = new HashMap<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            byPos.put(occupant.pos(), occupant);
        }
        Map<GridPos, String> stageByPos = assignStages(grid.occupants(), byPos);
        List<VesselComponent> components = new ArrayList<>();
        for (VesselBlockOccupant occupant : grid.occupants()) {
            components.add(toComponent(
                    occupant,
                    stageByPos.get(occupant.pos()),
                    occupant.pos().occupancyCentroidMeters(origin, occupant.spec().occupancy())));
        }
        return new VesselBlueprint(components, buildConnections(grid.occupants(), byPos));
    }

    private static Map<GridPos, String> assignStages(
            List<VesselBlockOccupant> occupants, Map<GridPos, VesselBlockOccupant> byPos) {
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
                VesselBlockOccupant here = byPos.get(current);
                for (BlockFace face : BlockFace.values()) {
                    GridPos neighbourPos = current.offset(face);
                    VesselBlockOccupant neighbour = byPos.get(neighbourPos);
                    if (neighbour == null || visited.contains(neighbourPos)) {
                        continue;
                    }
                    if (shares(here, neighbour, face, VesselConnection.ConnectionKind.STRUCTURAL)) {
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

    private static List<VesselConnection> buildConnections(
            List<VesselBlockOccupant> occupants, Map<GridPos, VesselBlockOccupant> byPos) {
        List<VesselConnection> connections = new ArrayList<>();
        for (VesselBlockOccupant occupant : occupants) {
            for (BlockFace face : BlockFace.values()) {
                GridPos neighbourPos = occupant.pos().offset(face);
                if (occupant.pos().compareTo(neighbourPos) >= 0) {
                    continue;
                }
                VesselBlockOccupant neighbour = byPos.get(neighbourPos);
                if (neighbour == null) {
                    continue;
                }
                for (VesselConnection.ConnectionKind kind : sharedKinds(occupant, neighbour, face)) {
                    connections.add(new VesselConnection(occupant.componentId(), neighbour.componentId(), kind));
                }
            }
        }
        return connections;
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
            case STRUCTURE, SEPARATOR -> VesselComponent.structure(
                    id, stageId, VesselComponent.ComponentKind.STRUCTURE, spec.massKg(), position);
            case TANK -> VesselComponent.tank(id, stageId, new TankState(
                    id, spec.massKg(), spec.tankCapacityKg(), spec.defaultPropellant(), position));
            case ENGINE -> VesselComponent.engine(id, stageId, new EngineState(
                    id, spec.fuelId(), spec.oxidizerId(), spec.massKg(), spec.thrustNewtons(),
                    spec.specificImpulseSeconds(), spec.mixtureRatio(), 1.0, 0.0, true, position,
                    occupant.facing().opposite().vector()));
        };
    }
}
