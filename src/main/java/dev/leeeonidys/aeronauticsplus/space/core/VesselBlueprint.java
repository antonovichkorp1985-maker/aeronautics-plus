package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Freeform logical vessel graph. It deliberately has no canonical rocket template.
 * Physical Minecraft blocks are converted to this graph by a later adapter.
 */
public record VesselBlueprint(List<VesselComponent> components, List<VesselConnection> connections) {
    public VesselBlueprint {
        components = List.copyOf(components == null ? List.of() : components);
        connections = List.copyOf(connections == null ? List.of() : connections);
    }

    public VesselCompilation analyze() {
        List<VesselDiagnostic> diagnostics = new ArrayList<>();
        Map<String, VesselComponent> byId = new HashMap<>();
        for (VesselComponent component : components) {
            if (byId.put(component.id(), component) != null) {
                diagnostics.add(error("DUPLICATE_COMPONENT", "Повторяется компонент " + component.id()));
            }
        }

        for (VesselConnection connection : connections) {
            if (!byId.containsKey(connection.fromId()) || !byId.containsKey(connection.toId())) {
                diagnostics.add(error("BROKEN_CONNECTION", "Связь " + connection.fromId() + " → "
                        + connection.toId() + " ссылается на отсутствующий компонент"));
            }
        }

        List<StageState> stages = new ArrayList<>();
        Map<String, List<VesselComponent>> byStage = components.stream()
                .collect(Collectors.groupingBy(VesselComponent::stageId));
        for (Map.Entry<String, List<VesselComponent>> entry : byStage.entrySet()) {
            String stageId = entry.getKey();
            List<VesselComponent> stageComponents = entry.getValue();
            List<VesselComponent> tanks = stageComponents.stream()
                    .filter(component -> component.kind() == VesselComponent.ComponentKind.TANK).toList();
            List<VesselComponent> engines = stageComponents.stream()
                    .filter(component -> component.kind() == VesselComponent.ComponentKind.ENGINE).toList();
            if (tanks.isEmpty()) {
                diagnostics.add(error("NO_TANK", "Ступень " + stageId + " не содержит баков"));
            }
            if (engines.isEmpty()) {
                diagnostics.add(error("NO_ENGINE", "Ступень " + stageId + " не содержит двигателей"));
            }
            validateFuelConnections(stageId, engines, tanks, diagnostics);
            validateStructuralConnectivity(stageId, stageComponents, diagnostics);
            if (!tanks.isEmpty() && !engines.isEmpty()) {
                stages.add(new StageState(
                        stageId,
                        stageComponents.stream()
                                .filter(component -> component.kind() != VesselComponent.ComponentKind.TANK
                                        && component.kind() != VesselComponent.ComponentKind.ENGINE)
                                .mapToDouble(VesselComponent::structuralMassKg).sum(),
                        tanks.stream().map(VesselComponent::tank).toList(),
                        engines.stream().map(VesselComponent::engine).toList(),
                        true));
            }
        }
        return new VesselCompilation(diagnostics, stages);
    }

    private void validateFuelConnections(String stageId, List<VesselComponent> engines,
                                         List<VesselComponent> tanks, List<VesselDiagnostic> diagnostics) {
        for (VesselComponent engine : engines) {
            EngineState engineState = engine.engine();
            boolean fuelConnected = hasCompatibleConnection(engine.id(), engineState.fuelId(),
                    VesselConnection.ConnectionKind.FUEL, tanks);
            boolean oxidizerConnected = hasCompatibleConnection(engine.id(), engineState.oxidizerId(),
                    VesselConnection.ConnectionKind.OXIDIZER, tanks);
            if (!fuelConnected) {
                diagnostics.add(error("NO_FUEL_PATH", "Двигатель " + engine.id()
                        + " ступени " + stageId + " не подключён к топливу " + engineState.fuelId()));
            }
            if (!oxidizerConnected) {
                diagnostics.add(error("NO_OXIDIZER_PATH", "Двигатель " + engine.id()
                        + " ступени " + stageId + " не подключён к окислителю " + engineState.oxidizerId()));
            }
        }
    }

    private boolean hasCompatibleConnection(String engineId, String resourceId,
                                             VesselConnection.ConnectionKind kind,
                                             List<VesselComponent> tanks) {
        Set<String> tankIds = tanks.stream()
                .filter(tank -> kind == VesselConnection.ConnectionKind.FUEL
                        ? tank.tank().contents().fuelId().equals(resourceId)
                        : tank.tank().contents().oxidizerId().equals(resourceId))
                .map(VesselComponent::id).collect(Collectors.toSet());
        return connections.stream().anyMatch(connection -> connection.kind() == kind
                && ((connection.fromId().equals(engineId) && tankIds.contains(connection.toId()))
                || (connection.toId().equals(engineId) && tankIds.contains(connection.fromId()))));
    }

    private void validateStructuralConnectivity(String stageId, List<VesselComponent> components,
                                                List<VesselDiagnostic> diagnostics) {
        if (components.size() < 2) {
            return;
        }
        Set<String> ids = components.stream().map(VesselComponent::id).collect(Collectors.toSet());
        Set<String> visited = new HashSet<>();
        ArrayDeque<String> pending = new ArrayDeque<>();
        pending.add(components.get(0).id());
        while (!pending.isEmpty()) {
            String current = pending.remove();
            if (!visited.add(current)) {
                continue;
            }
            connections.stream()
                    .filter(connection -> connection.kind() == VesselConnection.ConnectionKind.STRUCTURAL)
                    .filter(connection -> connection.fromId().equals(current) || connection.toId().equals(current))
                    .map(connection -> connection.fromId().equals(current) ? connection.toId() : connection.fromId())
                    .filter(ids::contains).forEach(pending::add);
        }
        if (visited.size() != ids.size()) {
            diagnostics.add(error("STRUCTURAL_DISCONNECT", "В ступени " + stageId
                    + " есть компоненты без силового соединения"));
        }
    }

    private static VesselDiagnostic error(String code, String message) {
        return new VesselDiagnostic(VesselDiagnostic.Severity.ERROR, code, message);
    }
}
