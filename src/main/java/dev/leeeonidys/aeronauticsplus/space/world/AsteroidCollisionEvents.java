package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import dev.leeeonidys.aeronauticsplus.space.compile.BlockFace;
import dev.leeeonidys.aeronauticsplus.space.compile.VesselPartCatalog;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Asteroid belt hazard.
 *
 * <p>Damage is local: the block of the ship that actually touches the rock takes the hit,
 * and the hit is the closing speed along that face — not the ship's overall speed.
 * A wing clipped edge-on shears off at walking pace; the same speed on a structural
 * cell only dents it. See {@link dev.leeeonidys.aeronauticsplus.space.core.PartImpact}.
 *
 * <p>No ChemMod gas: depressurisation is reported as a stub until ChemMod publishes
 * tank contents.
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class AsteroidCollisionEvents {
    /** Embedded cells keep grinding even when the ship has stopped moving. */
    private static final double EMBEDDED_GRIND_METERS_PER_SECOND = 6.0;

    private AsteroidCollisionEvents() {
    }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof VesselEntity vessel)) {
            return;
        }
        if (vessel.level().isClientSide || vessel.loop() == null) {
            return;
        }
        if (!AsteroidWorld.isBelt(vessel.level())) {
            return;
        }
        if (!vessel.asteroidImpactReady()) {
            return;
        }
        ImpactContact contact = worstContact(vessel);
        if (contact != null) {
            vessel.applyAsteroidImpact(contact);
        }
    }

    /**
     * The cell that is actually in contact, preferring the deepest penetration along the
     * direction of travel. Glancing side contacts lose to the face the ship is driving into.
     */
    private static ImpactContact worstContact(VesselEntity vessel) {
        Level level = vessel.level();
        VesselEntity.PartView[] parts = VesselEntity.decodeParts(vessel.partsPayload());
        if (parts.length == 0) {
            return null;
        }
        Vec3 velocity = vessel.worldVelocityMetersPerSecond();
        // One tick of travel is up to a few blocks: sample the previous position too so a
        // fast pass cannot tunnel through a thin rock without a contact.
        double backX = velocity.x / 20.0;
        double backY = velocity.y / 20.0;
        double backZ = velocity.z / 20.0;
        ImpactContact best = bestAt(level, parts, velocity, vessel.getX(), vessel.getY(), vessel.getZ());
        ImpactContact previous = bestAt(
                level, parts, velocity,
                vessel.getX() - backX, vessel.getY() - backY, vessel.getZ() - backZ);
        if (previous != null
                && (best == null || previous.closingMetersPerSecond() > best.closingMetersPerSecond())) {
            best = previous;
        }
        return best;
    }

    private static ImpactContact bestAt(
            Level level,
            VesselEntity.PartView[] parts,
            Vec3 velocity,
            double originX,
            double originY,
            double originZ) {
        ImpactContact best = null;
        double bestClosing = -1.0;
        for (VesselEntity.PartView part : parts) {
            if (specOrNull(part) == null) {
                continue;
            }
            int cx = (int) Math.floor(originX + part.x());
            int cy = (int) Math.floor(originY + part.y());
            int cz = (int) Math.floor(originZ + part.z());
            BlockPos cell = new BlockPos(cx, cy, cz);
            boolean embedded = isRock(level, cell);

            ImpactContact contact = embedded
                    ? embeddedContact(part, velocity)
                    : faceContact(level, cell, part, velocity);
            if (contact == null) {
                continue;
            }
            if (contact.closingMetersPerSecond() > bestClosing) {
                bestClosing = contact.closingMetersPerSecond();
                best = contact;
            }
        }
        return best;
    }

    /** A cell that is already inside the rock: the hull is being forced through. */
    private static ImpactContact embeddedContact(VesselEntity.PartView part, Vec3 velocity) {
        double speed = velocity.length();
        double normalX = 0.0;
        double normalY = 1.0;
        double normalZ = 0.0;
        if (speed > 1.0e-6) {
            normalX = -velocity.x / speed;
            normalY = -velocity.y / speed;
            normalZ = -velocity.z / speed;
        }
        double closing = Math.max(speed, EMBEDDED_GRIND_METERS_PER_SECOND);
        return new ImpactContact(
                part.specId(), spec(part).kind(), part.x(), part.y(), part.z(),
                closing, 0.0, dotFacing(part, normalX, normalY, normalZ));
    }

    /** A free cell with rock on one of its six faces: that face is the contact. */
    private static ImpactContact faceContact(
            Level level, BlockPos cell, VesselEntity.PartView part, Vec3 velocity) {
        ImpactContact best = null;
        double bestClosing = 0.0;
        for (BlockFace face : BlockFace.values()) {
            if (!isRock(level, cell.offset(face.dx(), face.dy(), face.dz()))) {
                continue;
            }
            // Normal points from the rock into the ship.
            double nx = -face.dx();
            double ny = -face.dy();
            double nz = -face.dz();
            double along = velocity.x * nx + velocity.y * ny + velocity.z * nz;
            double closing = Math.max(0.0, -along);
            double sx = velocity.x - nx * along;
            double sy = velocity.y - ny * along;
            double sz = velocity.z - nz * along;
            double shear = Math.sqrt(sx * sx + sy * sy + sz * sz);
            if (closing <= 0.0 && shear <= 0.0) {
                continue;
            }
            if (best == null || closing > bestClosing) {
                bestClosing = closing;
                best = new ImpactContact(
                        part.specId(), spec(part).kind(), part.x(), part.y(), part.z(),
                        closing, shear, dotFacing(part, nx, ny, nz));
            }
        }
        return best;
    }

    private static boolean isRock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(AeronauticsPlus.ASTEROID_ROCK.get());
    }

    private static dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec spec(
            VesselEntity.PartView part) {
        return VesselPartCatalog.byId(part.specId());
    }

    private static dev.leeeonidys.aeronauticsplus.space.compile.VesselPartSpec specOrNull(
            VesselEntity.PartView part) {
        try {
            return spec(part);
        } catch (RuntimeException unknownPart) {
            return null;
        }
    }

    /**
     * 1.0 when the rock hits the plate broadside along its mounting normal, 0.0 when the
     * contact slides in the plane of the plate.
     */
    private static double dotFacing(VesselEntity.PartView part, double nx, double ny, double nz) {
        BlockFace facing = part.facing();
        return Math.abs(facing.dx() * nx + facing.dy() * ny + facing.dz() * nz);
    }
}
