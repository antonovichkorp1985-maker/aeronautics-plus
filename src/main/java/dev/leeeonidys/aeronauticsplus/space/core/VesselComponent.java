package dev.leeeonidys.aeronauticsplus.space.core;

/** One logical component compiled from a freely built Minecraft vessel. */
public record VesselComponent(
        String id,
        String stageId,
        ComponentKind kind,
        double structuralMassKg,
        TankState tank,
        EngineState engine,
        Vector3d localPositionMeters) {
    public enum ComponentKind { STRUCTURE, TANK, ENGINE, AVIONICS, PAYLOAD }

    public VesselComponent {
        if (id == null || id.isBlank() || stageId == null || stageId.isBlank() || kind == null) {
            throw new IllegalArgumentException("Vessel component identity is invalid");
        }
        if (structuralMassKg < 0.0 || !Double.isFinite(structuralMassKg)) {
            throw new IllegalArgumentException("Structural mass must be finite and non-negative");
        }
        if (localPositionMeters == null) {
            throw new IllegalArgumentException("Component position must not be null");
        }
        if (kind == ComponentKind.TANK && tank == null) {
            throw new IllegalArgumentException("Tank component requires TankState");
        }
        if (kind == ComponentKind.ENGINE && engine == null) {
            throw new IllegalArgumentException("Engine component requires EngineState");
        }
        if (kind != ComponentKind.TANK && tank != null || kind != ComponentKind.ENGINE && engine != null) {
            throw new IllegalArgumentException("Component payload does not match component kind");
        }
    }

    public static VesselComponent structure(String id, String stageId, ComponentKind kind, double massKg) {
        return structure(id, stageId, kind, massKg, Vector3d.ZERO);
    }

    public static VesselComponent structure(
            String id, String stageId, ComponentKind kind, double massKg, Vector3d localPositionMeters) {
        if (kind == ComponentKind.TANK || kind == ComponentKind.ENGINE) {
            throw new IllegalArgumentException("Use tank() or engine() for physical components");
        }
        return new VesselComponent(id, stageId, kind, massKg, null, null, localPositionMeters);
    }

    public static VesselComponent tank(String id, String stageId, TankState tank) {
        return new VesselComponent(
                id, stageId, ComponentKind.TANK, 0.0, tank, null, tank.localPositionMeters());
    }

    public static VesselComponent engine(String id, String stageId, EngineState engine) {
        return new VesselComponent(
                id, stageId, ComponentKind.ENGINE, 0.0, null, engine, engine.localPositionMeters());
    }
}
