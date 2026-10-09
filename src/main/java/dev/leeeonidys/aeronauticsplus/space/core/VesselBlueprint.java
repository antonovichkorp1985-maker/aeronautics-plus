package dev.leeeonidys.aeronauticsplus.space.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
        Map<String, List<VesselComponent>> byStage = new LinkedHashMap<>();
        for (VesselComponent component : components) {
            byStage.computeIfAbsent(component.stageId(), ignored -> new ArrayList<>()).add(component);
        }
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
                StageState stage = new StageState(
                        stageId,
                        stageComponents.stream()
                                .filter(component -> component.kind() != VesselComponent.ComponentKind.TANK
                                        && component.kind() != VesselComponent.ComponentKind.ENGINE)
                                .filter(component -> component.structuralMassKg() > 0.0)
                                .map(component -> new MassElement(
                                        component.id(),
                                        component.structuralMassKg(),
                                        component.localPositionMeters(),
                                        roleOf(component.kind())))
                                .toList(),
                        tanks.stream().map(VesselComponent::tank).toList(),
                        engines.stream().map(VesselComponent::engine).toList(),
                        true);
                addThrustGeometryDiagnostics(stage, diagnostics);
                stages.add(stage);
            }
        }
        addPayloadDiagnostics(diagnostics);
        return new VesselCompilation(diagnostics, stages);
    }

    private void addPayloadDiagnostics(List<VesselDiagnostic> diagnostics) {
        boolean hasPayload = components.stream()
                .anyMatch(component -> component.kind() == VesselComponent.ComponentKind.PAYLOAD);
        boolean hasFairing = components.stream()
                .anyMatch(component -> component.kind() == VesselComponent.ComponentKind.FAIRING);
        if (!hasPayload) {
            diagnostics.add(warning("NO_PAYLOAD",
                    "Нет полезной нагрузки: орбитальный запуск без аппарата"));
        }
        if (hasPayload && !hasFairing) {
            diagnostics.add(warning("PAYLOAD_WITHOUT_FAIRING",
                    "Полезная нагрузка без обтекателя: нет защиты на участке атмосферы"));
        }
        if (hasFairing && !hasPayload) {
            diagnostics.add(warning("FAIRING_WITHOUT_PAYLOAD",
                    "Обтекатель без аппарата: сбрасывать нечего"));
        }
    }

    private static MassElement.Role roleOf(VesselComponent.ComponentKind kind) {
        return switch (kind) {
            case FAIRING -> MassElement.Role.FAIRING;
            case PAYLOAD -> MassElement.Role.PAYLOAD;
            default -> MassElement.Role.STRUCTURE;
        };
    }

    private static void addThrustGeometryDiagnostics(StageState stage, List<VesselDiagnostic> diagnostics) {
        ThrustGeometry geometry = stage.thrustGeometry();
        if (!geometry.hasNetThrust()) {
            diagnostics.add(error("ZERO_NET_THRUST",
                    "Ступень " + stage.id()
                            + " имеет нулевой суммарный вектор тяги — двигатели выключены или взаимно компенсируются"));
            return;
        }
        if (geometry.hasMaterialOffset()) {
            diagnostics.add(warning("THRUST_OFFSET",
                    "Ступень " + stage.id() + ": центр тяги смещён относительно центра масс на "
                            + String.format(Locale.ROOT, "%.3f", geometry.perpendicularOffsetMeters())
                            + " м; появится вращательный момент"));
        }
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

    private static VesselDiagnostic warning(String code, String message) {
        return new VesselDiagnostic(VesselDiagnostic.Severity.WARNING, code, message);
    }
}
