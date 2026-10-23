package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapView;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Kepler map: Earth disk, 20 km handoff ring, vehicle dots.
 * Altitude is linear to 200 km — not Earth scale. Not an orientation GUI.
 */
public final class OrbitMapScreen extends Screen {
    private static final int EARTH = 0xFF1B4F72;
    private static final int LAND = 0xFF1E8449;
    private static final int RING = 0xFFFFC107;
    private static final int PARKING = 0xFF5D6D7E;
    private static final int MAP_DOT = 0xFF5DADE2;
    private static final int WORLD_DOT = 0xFFE67E22;
    private static final int TEXT = 0xFFEAECEE;

    private List<OrbitMapTrack> tracks;
    private int refresh;

    public OrbitMapScreen(List<OrbitMapTrack> tracks) {
        super(Component.translatable("screen.aeronauticsplus.orbit_map"));
        this.tracks = List.copyOf(tracks == null ? List.of() : tracks);
    }

    public void setTracks(List<OrbitMapTrack> next) {
        this.tracks = List.copyOf(next == null ? List.of() : next);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        refresh++;
        if (refresh % 10 == 0) {
            PacketDistributor.sendToServer(new OrbitMapRequestPayload());
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        int cx = width / 2;
        int cy = height / 2 + 8;
        int radius = Math.max(40, Math.min(width, height) / 3);
        fillCircle(graphics, cx, cy, radius, 0xFF0B1A24);
        fillCircle(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.EARTH_FRACTION), EARTH);
        fillCircle(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.EARTH_FRACTION * 0.55), LAND);
        ring(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.handoffRadius()), RING);
        ring(graphics, cx, cy, radius, PARKING);
        graphics.drawCenteredString(font, title, cx, 12, TEXT);
        graphics.drawCenteredString(
                font, Component.translatable("screen.aeronauticsplus.orbit_map.hint"), cx, 24, 0xFFABB2B9);
        int listY = 40;
        if (tracks.isEmpty()) {
            graphics.drawString(
                    font,
                    Component.translatable("screen.aeronauticsplus.orbit_map.empty"),
                    12,
                    listY,
                    0xFFABB2B9,
                    false);
        }
        List<OrbitMapTrack> drawn = new ArrayList<>(tracks);
        for (OrbitMapTrack track : drawn) {
            OrbitMapView.Plot plot = OrbitMapView.plot(track);
            int x = cx + (int) Math.round(plot.x() * radius);
            int y = cy - (int) Math.round(plot.y() * radius);
            int color = track.presence() == FlightPresence.ORBIT_MAP ? MAP_DOT : WORLD_DOT;
            graphics.fill(x - 2, y - 2, x + 3, y + 3, color);
            String line = String.format(
                    Locale.ROOT,
                    "%s  %.1f km  %s  horiz %.0f m/s",
                    track.id(),
                    track.altitudeMeters() / 1000.0,
                    track.presence() == FlightPresence.ORBIT_MAP ? "MAP" : "WORLD",
                    track.horizontalSpeedMetersPerSecond());
            graphics.drawString(font, line, 12, listY, TEXT, false);
            listY += 12;
        }
        graphics.drawString(
                font,
                Component.translatable("screen.aeronauticsplus.orbit_map.handoff"),
                12,
                height - 18,
                RING,
                false);
    }

    private static void fillCircle(GuiGraphics graphics, int cx, int cy, int r, int color) {
        if (r <= 0) {
            return;
        }
        for (int dy = -r; dy <= r; dy++) {
            int span = (int) Math.round(Math.sqrt((double) r * r - dy * dy));
            graphics.fill(cx - span, cy + dy, cx + span + 1, cy + dy + 1, color);
        }
    }

    private static void ring(GuiGraphics graphics, int cx, int cy, int r, int color) {
        if (r <= 0) {
            return;
        }
        int steps = Math.max(48, r);
        Integer px = null;
        Integer py = null;
        for (int i = 0; i <= steps; i++) {
            double a = 2.0 * Math.PI * i / steps;
            int x = cx + (int) Math.round(r * Math.cos(a));
            int y = cy + (int) Math.round(r * Math.sin(a));
            if (px != null) {
                graphics.fill(Math.min(px, x), Math.min(py, y), Math.max(px, x) + 1, Math.max(py, y) + 1, color);
            }
            px = x;
            py = y;
        }
    }
}
