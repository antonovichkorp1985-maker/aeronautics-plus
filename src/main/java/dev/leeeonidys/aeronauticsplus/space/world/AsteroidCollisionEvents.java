package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Asteroid belt hazard: a vessel that runs into rock is destroyed, crew takes
 * the hit. Not ChemMod, not n-body.
 */
@EventBusSubscriber(modid = AeronauticsPlus.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class AsteroidCollisionEvents {
    private AsteroidCollisionEvents() {
    }

    @SubscribeEvent
    public static void onTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) {
            return;
        }
        if (!AsteroidWorld.isBelt(level)) {
            return;
        }
        if (!isVessel(entity)) {
            return;
        }
        int minX = (int) Math.floor(entity.getX() - 1.0);
        int maxX = (int) Math.floor(entity.getX() + 1.0);
        int minY = (int) Math.floor(entity.getY() - 1.0);
        int maxY = (int) Math.floor(entity.getY() + 1.0);
        int minZ = (int) Math.floor(entity.getZ() - 1.0);
        int maxZ = (int) Math.floor(entity.getZ() + 1.0);
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (level.getBlockState(new BlockPos(x, y, z)).is(AeronauticsPlus.ASTEROID_ROCK)) {
                        destroyVessel(server, entity);
                        return;
                    }
                }
            }
        }
    }

    private static boolean isVessel(Entity entity) {
        return entity.getType() == AeronauticsPlus.VESSEL.get();
    }

    private static void destroyVessel(net.minecraft.server.level.ServerLevel server, Entity vessel) {
        vessel.discard();
    }
}
