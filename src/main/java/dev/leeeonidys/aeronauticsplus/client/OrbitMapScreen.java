package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.ManeuverPreview;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapView;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystem;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * KSP-inspired orbit-map pass: selectable vessel, conic trace and live flight-data panel.
 * Altitude is linear to 200 km; the conic is projected into XZ and omits orbital inclination.
 */
public final class OrbitMapScreen extends Screen {
    private static final int EARTH = 0xFF1B4F72;
    private static final int LAND = 0xFF1E8449;
    private static final int MOON = 0xFFB0B7C3;
    private static final int MOON_LAND = 0xFF7F8C8D;
    private static final int MARS = 0xFFA04000;
    private static final int MARS_LAND = 0xFF6E2C00;
    private static final int RING = 0xFFFFC107;
    private static final int PARKING = 0xFF5D6D7E;
    private static final int MAP_DOT = 0xFF5DADE2;
    private static final int WORLD_DOT = 0xFFE67E22;
    private static final int TEXT = 0xFFEAECEE;

    private List<OrbitMapTrack> tracks;
    private String selectedTrackId;
    private double plannedProgradeDeltaV;
    private int refresh;

    public OrbitMapScreen(List<OrbitMapTrack> tracks) {
        super(Component.translatable("screen.aeronauticsplus.orbit_map"));
        this.tracks = List.copyOf(tracks == null ? List.of() : tracks);
        this.selectedTrackId = this.tracks.isEmpty() ? null : this.tracks.get(0).id();
    }

    public void setTracks(List<OrbitMapTrack> next) {
        this.tracks = List.copyOf(next == null ? List.of() : next);
        if (selectedTrackId == null || this.tracks.stream().noneMatch(t -> t.id().equals(selectedTrackId))) {
            selectedTrackId = this.tracks.isEmpty() ? null : this.tracks.get(0).id();
        }
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
        OrbitMapTrack selected = tracks.stream()
                .filter(track -> track.id().equals(selectedTrackId))
                .findFirst().orElse(null);
        OrbitMapView.Frame frame = selected == null
                ? OrbitMapView.frame(tracks)
                : new OrbitMapView.Frame(selected.bodyId(), OrbitMapView.handoffVisible(selected.bodyId()));
        int[] disk = diskColors(frame.bodyId());
        int cx = width / 2;
        int cy = height / 2 + 8;
        int radius = Math.max(40, Math.min(width, height) / 3);
        graphics.fill(6, 36, Math.min(width / 3, 118), height - 6, 0xCC101820);
        graphics.fill(Math.max(width - 144, width * 2 / 3), 36, width - 6, height - 6, 0xCC101820);
        fillCircle(graphics, cx, cy, radius, 0xFF0B1A24);
        fillCircle(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.EARTH_FRACTION), disk[0]);
        fillCircle(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.EARTH_FRACTION * 0.55), disk[1]);
        if (frame.handoff()) {
            ring(graphics, cx, cy, (int) Math.round(radius * OrbitMapView.handoffRadius()), RING);
        }
        ring(graphics, cx, cy, radius, PARKING);
        for (OrbitMapTrack track : tracks) {
            if (OrbitMapView.inFrame(track, frame.bodyId())) {
                drawOrbit(graphics, track, cx, cy, radius,
                        track.id().equals(selectedTrackId) ? 0xFF66D9EF : 0x885A9EAA);
            }
        }
        graphics.drawCenteredString(font, title, cx, 12, TEXT);
        graphics.drawCenteredString(font, hint(frame), cx, 24, 0xFFABB2B9);
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.vessels"),
                12, 42, 0xFF58C4DD, false);
        int listY = 55;
        if (tracks.isEmpty()) {
            graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.empty"),
                    12, listY, 0xFFABB2B9, false);
        }
        List<OrbitMapTrack> drawn = new ArrayList<>(tracks);
        int row = 0;
        for (OrbitMapTrack track : drawn) {
            if (OrbitMapView.inFrame(track, frame.bodyId())) {
                OrbitMapView.Plot plot = OrbitMapView.plot(track);
                int x = cx + (int) Math.round(plot.x() * radius);
                int y = cy - (int) Math.round(plot.y() * radius);
                int color = track.presence() == FlightPresence.ORBIT_MAP ? MAP_DOT : WORLD_DOT;
                graphics.fill(x - 2, y - 2, x + 3, y + 3, color);
            }
            boolean active = track.id().equals(selectedTrackId);
            int rowTop = listY - 2;
            graphics.fill(9, rowTop, Math.min(width / 3, 115), rowTop + 12,
                    active ? 0xFF24546A : 0x00000000);
            String shortId = track.id().length() > 14 ? track.id().substring(0, 14) : track.id();
            graphics.drawString(font, shortId, 12, listY, active ? 0xFFFFFFFF : TEXT, false);
            graphics.drawString(font,
                    track.presence() == FlightPresence.ORBIT_MAP ? "● ORBIT" : "● WORLD",
                    12, listY + 10, track.presence() == FlightPresence.ORBIT_MAP ? MAP_DOT : WORLD_DOT, false);
            listY += 25;
            row++;
        }
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.flight_data"),
                Math.max(width - 138, width * 2 / 3) + 8, 42, 0xFF58C4DD, false);
        if (selected != null) {
            int x = Math.max(width - 138, width * 2 / 3) + 8;
            int y = 58;
            y = dataLine(graphics, "BODY", selected.bodyId(), x, y);
            y = dataLine(graphics, "ALT", String.format(Locale.ROOT, "%.2f km", selected.altitudeMeters() / 1000.0), x, y);
            y = dataLine(graphics, "H V", String.format(Locale.ROOT, "%.0f m/s", selected.horizontalSpeedMetersPerSecond()), x, y);
            y = dataLine(graphics, "AP", Double.isFinite(selected.apoapsisAltitudeMeters())
                    ? String.format(Locale.ROOT, "%.1f km", selected.apoapsisAltitudeMeters() / 1000.0) : "ESCAPE", x, y);
            y = dataLine(graphics, "PE", String.format(Locale.ROOT, "%.1f km", selected.periapsisAltitudeMeters() / 1000.0), x, y);
            y = dataLine(graphics, "Q", String.format(Locale.ROOT, "%.1f Pa", selected.dynamicPressurePascals()), x, y);
            dataLine(graphics, "PRESENCE", selected.presence() == FlightPresence.ORBIT_MAP ? "ORBIT MAP" : "WORLD", x, y);
            drawManeuverPlanner(graphics, selected, x, height - 83);
        }
        graphics.drawString(font, footer(frame), 12, height - 18, frame.handoff() ? RING : PARKING, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int panelX = Math.max(width - 138, width * 2 / 3) + 8;
            int plannerTop = height - 83;
            if (mouseX >= panelX && mouseX < panelX + 88 && mouseY >= plannerTop + 24 && mouseY < plannerTop + 37) {
                int buttonIndex = (int) ((mouseX - panelX) / 22.0);
                plannedProgradeDeltaV = switch (buttonIndex) {
                    case 0 -> plannedProgradeDeltaV - 10.0;
                    case 1 -> plannedProgradeDeltaV - 1.0;
                    case 2 -> plannedProgradeDeltaV + 1.0;
                    case 3 -> plannedProgradeDeltaV + 10.0;
                    default -> plannedProgradeDeltaV;
                };
                plannedProgradeDeltaV = Math.max(-50_000.0, Math.min(50_000.0, plannedProgradeDeltaV));
                return true;
            }
            if (mouseX >= panelX && mouseX < panelX + 130 && mouseY >= plannerTop + 10 && mouseY < plannerTop + 22) {
                plannedProgradeDeltaV = 0.0;
                return true;
            }
        }
        if (button == 0 && mouseX >= 8 && mouseX <= Math.min(width / 3, 118) && mouseY >= 52) {
            int index = (int) ((mouseY - 52) / 25.0);
            if (index >= 0 && index < tracks.size()) {
                selectedTrackId = tracks.get(index).id();
                plannedProgradeDeltaV = 0.0;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /** Draw the two-body conic in its XZ projection; this UI pass intentionally omits inclination. */
    private void drawManeuverPlanner(GuiGraphics graphics, OrbitMapTrack track, int x, int y) {
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.maneuver_preview"),
                x, y, 0xFFFFC857, false);
        graphics.drawString(font, String.format(Locale.ROOT, "Δv %+,.1f m/s", plannedProgradeDeltaV),
                x, y + 11, TEXT, false);
        int buttonY = y + 24;
        String[] labels = {"-10", "-1", "+1", "+10"};
        for (int i = 0; i < labels.length; i++) {
            int bx = x + i * 22;
            graphics.fill(bx, buttonY, bx + 20, buttonY + 13, 0xFF263746);
            graphics.drawCenteredString(font, labels[i], bx + 10, buttonY + 3, 0xFFBDEBFA);
        }
        try {
            ManeuverPreview.Result preview = ManeuverPreview.progradeImpulse(
                    SolarSystem.body(track.bodyId()),
                    track.altitudeMeters(),
                    track.radialSpeedMetersPerSecond(),
                    track.horizontalSpeedMetersPerSecond(),
                    plannedProgradeDeltaV);
            String ap = preview.escape()
                    ? "Ap: escape"
                    : String.format(Locale.ROOT, "Ap %.0f km", preview.apoapsisAltitudeMeters() / 1000.0);
            String pe = String.format(Locale.ROOT, "Pe %.0f km", preview.periapsisAltitudeMeters() / 1000.0);
            graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.plan_only"),
                    x, y + 39, 0xFFFFC857, false);
            graphics.drawString(font, ap + "   " + pe, x, y + 50, TEXT, false);
        } catch (IllegalArgumentException invalid) {
            graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.invalid_impulse"),
                    x, y + 39, 0xFFFF7777, false);
        }
    }

    private static void drawOrbit(GuiGraphics graphics, OrbitMapTrack track, int cx, int cy, int mapRadius, int color) {
        if (!Double.isFinite(track.apoapsisAltitudeMeters())) {
            return; // Hyperbolic tracks have no closed ellipse; their live position remains plotted.
        }
        double bodyRadius;
        try {
            bodyRadius = SolarSystem.body(track.bodyId()).radiusMeters();
        } catch (IllegalArgumentException unknownBody) {
            return;
        }
        double rp = bodyRadius + track.periapsisAltitudeMeters();
        double ra = bodyRadius + track.apoapsisAltitudeMeters();
        if (!(rp > bodyRadius) || !(ra >= rp)) {
            return; // Skip impact trajectories and degenerate conics.
        }
        double eccentricity = (ra - rp) / (ra + rp);
        double semilatus = rp * (1.0 + eccentricity);
        int segments = 160;
        Integer previousX = null;
        Integer previousY = null;
        for (int i = 0; i <= segments; i++) {
            double anomaly = 2.0 * Math.PI * i / segments;
            double radiusMeters = semilatus / (1.0 + eccentricity * Math.cos(anomaly));
            double angle = track.periapsisAngleRadians() + anomaly;
            double altitude = radiusMeters - bodyRadius;
            OrbitMapView.Plot point = OrbitMapView.plot(altitude, angle);
            int x = cx + (int) Math.round(point.x() * mapRadius);
            int y = cy - (int) Math.round(point.y() * mapRadius);
            if (previousX != null) {
                graphics.fill(Math.min(previousX, x), Math.min(previousY, y),
                        Math.max(previousX, x) + 1, Math.max(previousY, y) + 1, color);
            }
            previousX = x;
            previousY = y;
        }
    }

    private int dataLine(GuiGraphics graphics, String label, String value, int x, int y) {
        graphics.drawString(font, label, x, y, 0xFF879BA7, false);
        graphics.drawString(font, value, x + 54, y, TEXT, false);
        return y + 14;
    }

    private static int[] diskColors(String bodyId) {
        return switch (bodyId) {
            case "moon" -> new int[] {MOON, MOON_LAND};
            case "mars" -> new int[] {MARS, MARS_LAND};
            default -> new int[] {EARTH, LAND};
        };
    }

    private static Component hint(OrbitMapView.Frame frame) {
        if ("moon".equals(frame.bodyId())) {
            return Component.translatable("screen.aeronauticsplus.orbit_map.hint_moon");
        }
        if (!frame.handoff()) {
            return Component.translatable("screen.aeronauticsplus.orbit_map.hint_vacuum");
        }
        return Component.translatable("screen.aeronauticsplus.orbit_map.hint");
    }

    private static Component footer(OrbitMapView.Frame frame) {
        if ("moon".equals(frame.bodyId())) {
            return Component.translatable(
                    "screen.aeronauticsplus.orbit_map.moon",
                    String.format(Locale.ROOT, "%.0f", OrbitMapView.moonSoiAltitudeKm()));
        }
        if (!frame.handoff()) {
            return Component.translatable("screen.aeronauticsplus.orbit_map.vacuum", frame.bodyId());
        }
        return Component.translatable("screen.aeronauticsplus.orbit_map.handoff");
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
