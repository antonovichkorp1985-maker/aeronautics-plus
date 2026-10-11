package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.FlightReadout;
import dev.leeeonidys.aeronauticsplus.space.world.VesselEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Seated strip: altitude, speed, throttle, warp, hold. Not the orientation GUI.
 */
public final class FlightHud {
    private FlightHud() {
    }

    public static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        if (!(minecraft.player.getVehicle() instanceof VesselEntity vessel)) {
            return;
        }
        String[] lines = FlightReadout.lines(
                vessel.hudAltitudeMeters(),
                vessel.hudHorizontalMetersPerSecond(),
                vessel.hudRadialMetersPerSecond(),
                vessel.hudThrottle(),
                vessel.hudWarp(),
                vessel.hudHoldLabel());
        int y = 8;
        for (String line : lines) {
            graphics.drawString(minecraft.font, line, 8, y, 0xFFEAECEE, true);
            y += 10;
        }
    }
}
