package dev.leeeonidys.aeronauticsplus.client;

import dev.leeeonidys.aeronauticsplus.space.core.FlightPresence;
import dev.leeeonidys.aeronauticsplus.space.core.ManeuverPreview;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapView;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystem;
import dev.leeeonidys.aeronauticsplus.space.core.SolarSystemMap;
import dev.leeeonidys.aeronauticsplus.space.core.Vector3d;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapControlPayload;
import dev.leeeonidys.aeronauticsplus.space.world.OrbitMapRequestPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

/**
 * KSP-inspired orbit map with selectable vessel, live conic, rotatable 3D orthographic mode and 2D fallback.
 * Altitude is intentionally compressed linearly to 200 km so low orbits remain visible.
 */
public final class OrbitMapScreen extends Screen {
    private static final int BG_DEEP = 0xFF020408;
    private static final int STAR = 0xFFFFFFFF;
    private static final int PANEL_BG = 0xCC101820;
    private static final int PANEL_ACTIVE = 0xFF24546A;
    private static final int TEXT = 0xFFEAECEE;
    private static final int TEXT_DIM = 0xFF8F9AA5;
    private static final int MAP_DOT = 0xFF5DADE2;
    private static final int WORLD_DOT = 0xFFE67E22;
    private static final int AP_MARKER = 0xFFFF5555;
    private static final int PE_MARKER = 0xFF55FF55;
    /** Body colors for SYSTEM and local maps. */
    private static final int EARTH = 0xFF1B4F72;
    private static final int LAND = 0xFF1E8449;
    private static final int MOON = 0xFFB0B7C3;
    private static final int MOON_LAND = 0xFF7F8C8D;
    private static final int MARS = 0xFFA04000;
    private static final int MARS_LAND = 0xFF6E2C00;
    private static final int RING = 0xFFFFC107;
    private static final int PARKING = 0xFF5D6D7E;

    private enum Panel { FLIGHT, PLAN, CONTROL }
    private enum MapMode { SYSTEM, THREE_D, TWO_D }

    private List<OrbitMapTrack> tracks;
    private String selectedTrackId;
    private Panel panel = Panel.FLIGHT;
    private double plannedProgradeDeltaV;
    private MapMode mapMode = MapMode.SYSTEM;
    private String focusedBodyId;
    private final Map<String, List<int[]>> systemBodyHits = new HashMap<>();
    private double cameraYaw = 0.45;
    private double cameraPitch = 0.38;
    private double cameraZoom = 1.0;
    private boolean draggingMap;
    private double lastDragX;
    private double lastDragY;
    private boolean leftPanelOpen = true;
    private boolean rightPanelOpen = true;
    private int refresh;
    private int hoverBody = -1;

    public OrbitMapScreen(List<OrbitMapTrack> tracks) {
        super(Component.translatable("screen.aeronauticsplus.orbit_map"));
        this.tracks = List.copyOf(tracks == null ? List.of() : tracks);
        this.selectedTrackId = this.tracks.isEmpty() ? null : this.tracks.get(0).id();
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
    public void onClose() {
        super.onClose();
        OrbitMapClient.onScreenClosed();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.fill(0, 0, width, height, BG_DEEP);
        super.render(graphics, mouseX, mouseY, partialTick);
        OrbitMapTrack selected = tracks.stream()
                .filter(track -> track.id().equals(selectedTrackId))
                .findFirst().orElse(null);
        String localBody = focusedBodyId != null ? focusedBodyId
                : selected != null ? selected.bodyId() : OrbitMapView.frame(tracks).bodyId();
        OrbitMapView.Frame frame = new OrbitMapView.Frame(localBody, OrbitMapView.handoffVisible(localBody));
        int cx = width / 2;
        int cy = height / 2 + 8;
        int radius = Math.max(40, Math.min(width, height) / 2 - 20);
        int[] disk = diskColors(frame.bodyId());
        if (mapMode == MapMode.SYSTEM) {
            drawSystemMap(graphics, cx, cy, radius, mouseX, mouseY);
        } else if (mapMode == MapMode.THREE_D) {
            drawThreeDimensionalMap(graphics, frame, disk, cx, cy, radius, mouseX, mouseY);
        } else {
            drawTwoDimensionalMap(graphics, frame, disk, cx, cy, radius);
        }
        drawModeToggle(graphics, cx);
        drawTitle(graphics, cx, frame);
        if (leftPanelOpen) {
            drawVesselList(graphics, selected, cx, cy, radius);
        }
        if (rightPanelOpen && selected != null) {
            int panelX = width - 150;
            drawPanelTabs(graphics, panelX);
            if (panel == Panel.FLIGHT) {
                drawFlightData(graphics, selected, panelX);
            } else if (panel == Panel.PLAN) {
                drawManeuverPlanner(graphics, selected, panelX, height - 140);
            } else {
                drawFlightControls(graphics, selected, panelX);
            }
        }
        drawFooter(graphics, cx, frame);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        OrbitMapTrack selected = tracks.stream().filter(track -> track.id().equals(selectedTrackId)).findFirst().orElse(null);
        int panelX = width - 150;
        int mapCenterX = width / 2;
        int mapCenterY = height / 2 + 8;
        int mapRadius = Math.max(40, Math.min(width, height) / 2 - 20);
        if (button == 0 && mouseY >= 37 && mouseY < 52
                && mouseX >= mapCenterX - 44 && mouseX < mapCenterX + 44) {
            if (mapMode == MapMode.SYSTEM) {
                focusedBodyId = selected != null ? selected.bodyId()
                        : focusedBodyId != null ? focusedBodyId : "earth";
                mapMode = MapMode.THREE_D;
            } else if (mapMode == MapMode.THREE_D) {
                mapMode = MapMode.TWO_D;
            } else {
                mapMode = MapMode.SYSTEM;
            }
            return true;
        }
        if (button == 0 && mapMode == MapMode.SYSTEM) {
            String hitBody = null;
            double nearestHitSquared = 9.0 * 9.0;
            for (Map.Entry<String, List<int[]>> entry : systemBodyHits.entrySet()) {
                for (int[] hit : entry.getValue()) {
                    double dx = mouseX - hit[0];
                    double dy = mouseY - hit[1];
                    double distanceSquared = dx * dx + dy * dy;
                    if (distanceSquared < nearestHitSquared) {
                        nearestHitSquared = distanceSquared;
                        hitBody = entry.getKey();
                    }
                }
            }
            if ("sun".equals(hitBody)) {
                return true;
            }
            if (hitBody != null) {
                focusedBodyId = hitBody;
                mapMode = MapMode.THREE_D;
                return true;
            }
        }
        if ((mapMode == MapMode.THREE_D || mapMode == MapMode.SYSTEM) && button == 1
                && Math.hypot(mouseX - mapCenterX, mouseY - mapCenterY) <= mapRadius * cameraZoom) {
            draggingMap = true;
            lastDragX = mouseX;
            lastDragY = mouseY;
            return true;
        }
        if (rightPanelOpen && button == 0 && mouseX >= panelX && mouseY >= 38 && mouseY < 52) {
            if (mouseX < panelX + 30 && mouseY < 46) {
                leftPanelOpen = !leftPanelOpen;
                return true;
            }
            if (mouseX < panelX + 60 && mouseY < 46) {
                rightPanelOpen = !rightPanelOpen;
                return true;
            }
            int tab = (int) ((mouseX - panelX) / 38.0);
            if (tab >= 0 && tab < Panel.values().length) {
                panel = Panel.values()[tab];
                return true;
            }
        }
        if (button == 0 && selected != null && panel == Panel.PLAN) {
            int plannerTop = height - 140;
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
        if (button == 0 && selected != null && panel == Panel.CONTROL) {
            if (mouseY >= 150 && mouseY < 164 && mouseX >= panelX && mouseX < panelX + 92) {
                int index = (int) ((mouseX - panelX) / 23.0);
                int[] actions = {OrbitMapControlPayload.THROTTLE_CUT, OrbitMapControlPayload.THROTTLE_DOWN,
                        OrbitMapControlPayload.THROTTLE_UP, OrbitMapControlPayload.THROTTLE_FULL};
                if (index < actions.length) {
                    sendControl(selected, actions[index]);
                    return true;
                }
            }
            if (mouseY >= 185 && mouseY < 199 && mouseX >= panelX && mouseX < panelX + 92) {
                int index = (int) ((mouseX - panelX) / 23.0);
                int[] actions = {OrbitMapControlPayload.PITCH_DOWN, OrbitMapControlPayload.PITCH_UP,
                        OrbitMapControlPayload.YAW_LEFT, OrbitMapControlPayload.YAW_RIGHT};
                if (index < actions.length) {
                    sendControl(selected, actions[index]);
                    return true;
                }
            }
            if (mouseY >= 203 && mouseY < 217 && mouseX >= panelX && mouseX < panelX + 92) {
                int index = (int) ((mouseX - panelX) / 23.0);
                int[] actions = {OrbitMapControlPayload.ROLL_LEFT, OrbitMapControlPayload.ROLL_RIGHT,
                        OrbitMapControlPayload.ZERO_ROTATION, OrbitMapControlPayload.RCS_PULSE};
                if (index < actions.length) {
                    sendControl(selected, actions[index]);
                    return true;
                }
            }
        }
        if (leftPanelOpen && button == 0 && mouseX >= 10 && mouseX <= 160 && mouseY >= 60) {
            int index = (int) ((mouseY - 60) / 26.0);
            if (index >= 0 && index < tracks.size()) {
                selectedTrackId = tracks.get(index).id();
                if (mapMode != MapMode.SYSTEM) {
                    focusedBodyId = tracks.get(index).bodyId();
                }
                plannedProgradeDeltaV = 0.0;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingMap && button == 1) {
            cameraYaw += (mouseX - lastDragX) * 0.008;
            cameraPitch = Math.max(-1.4, Math.min(1.4, cameraPitch + (mouseY - lastDragY) * 0.008));
            lastDragX = mouseX;
            lastDragY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1 && draggingMap) {
            draggingMap = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int cx = width / 2;
        int cy = height / 2 + 8;
        int radius = Math.max(40, Math.min(width, height) / 2 - 20);
        if ((mapMode == MapMode.THREE_D || mapMode == MapMode.SYSTEM)
                && Math.hypot(mouseX - cx, mouseY - cy) <= radius * cameraZoom + 40) {
            double factor = scrollY > 0.0 ? 0.9 : 1.0 / 0.9;
            cameraZoom = Math.max(0.25, Math.min(4.0, cameraZoom * factor));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void drawModeToggle(GuiGraphics graphics, int cx) {
        int y = 37;
        String label = switch (mapMode) {
            case SYSTEM -> "SYSTEM";
            case THREE_D -> "LOCAL 3D";
            case TWO_D -> "LOCAL 2D";
        };
        graphics.fill(cx - 50, y, cx + 50, y + 14, 0xDD182028);
        graphics.drawCenteredString(font, label, cx, y + 3, 0xFFBDEBFA);
    }

    private void drawTitle(GuiGraphics graphics, int cx, OrbitMapView.Frame frame) {
        graphics.drawCenteredString(font, title, cx, 12, TEXT);
        Component hint = mapMode == MapMode.SYSTEM
                ? Component.literal("Click planet to focus • RMB drag to rotate • wheel to zoom")
                : hint(frame);
        graphics.drawCenteredString(font, hint, cx, 24, TEXT_DIM);
    }

    private void drawVesselList(GuiGraphics graphics, OrbitMapTrack selected, int cx, int cy, int radius) {
        int listX = 10;
        int listY = 55;
        int listWidth = 150;
        graphics.fill(listX - 4, listY - 4, listX + listWidth + 4, height - 4, PANEL_BG);
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.vessels"),
                listX + 2, listY - 2, 0xFF58C4DD, false);
        if (tracks.isEmpty()) {
            graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.empty"),
                    listX + 2, listY + 16, TEXT_DIM, false);
        }
        for (int i = 0; i < tracks.size(); i++) {
            OrbitMapTrack track = tracks.get(i);
            boolean active = track.id().equals(selectedTrackId);
            int rowY = listY + 16 + i * 26;
            if (rowY > height - 30) break;
            if (active) {
                graphics.fill(listX, rowY - 2, listX + listWidth, rowY + 22, PANEL_ACTIVE);
            }
            String shortId = track.id().length() > 18 ? track.id().substring(0, 18) + ".." : track.id();
            graphics.drawString(font, shortId, listX + 4, rowY, active ? TEXT : TEXT, false);
            String status = track.presence() == FlightPresence.ORBIT_MAP ? "● ORBIT"
                    : track.presence() == FlightPresence.STATION_BLOCKS ? "● STATION" : "● WORLD";
            int statusColor = track.presence() == FlightPresence.ORBIT_MAP ? MAP_DOT
                    : track.presence() == FlightPresence.STATION_BLOCKS ? 0xFFFFC107 : WORLD_DOT;
            graphics.drawString(font, status, listX + 4, rowY + 12, statusColor, false);
        }
    }

    private void drawSystemMap(GuiGraphics graphics, int cx, int cy, int radius,
                                 int mouseX, int mouseY) {
        systemBodyHits.clear();
        fillCircle(graphics, cx, cy, radius, 0xFF050D18);
        drawSystemRing(graphics, cx, cy, radius, 1.0, 0x334A667F);
        drawSystemRing(graphics, cx, cy, radius, 5.0, 0x334A667F);
        drawSystemRing(graphics, cx, cy, radius, 10.0, 0x334A667F);
        drawSystemRing(graphics, cx, cy, radius, 20.0, 0x334A667F);
        drawSystemRing(graphics, cx, cy, radius, SolarSystemMap.rangeAu(), 0x554A667F);
        for (String bodyId : SolarSystemMap.planetIds()) {
            drawSystemOrbit(graphics, bodyId, cx, cy, radius, systemColor(bodyId));
        }
        for (String bodyId : SolarSystemMap.dwarfPlanetIds()) {
            drawSystemOrbit(graphics, bodyId, cx, cy, radius, 0x554C5967);
        }
        drawSystemOrbit(graphics, "moon", cx, cy, radius, 0x447E8791);
        int[] sun = {cx, cy};
        drawSystemBody(graphics, "sun", "SUN", sun, 7, -5, 5, 0xFFFFD54F);
        for (String bodyId : SolarSystemMap.planetIds()) {
            Vector3d position = SolarSystemMap.position(bodyId, mapEpochSeconds());
            int[] point = projectSystem(position, cx, cy, radius);
            int[] labelOffset = systemLabelOffset(bodyId);
            drawSystemBody(graphics, bodyId, bodyId.toUpperCase(Locale.ROOT), point,
                    labelOffset[0], labelOffset[1], bodyId.equals("earth") ? 5 : 4, systemColor(bodyId));
        }
        Vector3d lunarPosition = SolarSystemMap.position("moon", mapEpochSeconds());
        int[] moon = projectSystem(lunarPosition, cx, cy, radius);
        int[] earth = projectSystem(SolarSystemMap.position("earth", mapEpochSeconds()), cx, cy, radius);
        int[] moonCallout = new int[] {
                clamp(earth[0] - 18, cx - radius + 7, cx + radius - 7),
                clamp(earth[1] + 18, cy - radius + 7, cy + radius - 7)};
        drawSystemLeader(graphics, moon[0], moon[1], moonCallout[0], moonCallout[1], 0xFFCFD6DF);
        drawSystemBody(graphics, "moon", "MOON (UI OFFSET)", moonCallout, -1, -12, 3, MOON);
        for (String bodyId : SolarSystemMap.dwarfPlanetIds()) {
            int[] point = projectSystem(SolarSystemMap.position(bodyId, mapEpochSeconds()), cx, cy, radius);
            int[] labelOffset = systemLabelOffset(bodyId);
            drawSystemBody(graphics, bodyId, bodyId.toUpperCase(Locale.ROOT), point,
                    labelOffset[0], labelOffset[1], 3, 0xFFB59B78);
        }
        for (OrbitMapTrack track : tracks) {
            try {
                Vector3d position = systemPosition(track);
                int[] point = projectSystem(position, cx, cy, radius);
                int color = track.presence() == FlightPresence.STATION_BLOCKS ? 0xFFFFC107
                        : track.id().equals(selectedTrackId) ? 0xFF66D9EF : MAP_DOT;
                fillCircle(graphics, point[0], point[1], track.presence() == FlightPresence.STATION_BLOCKS ? 4 : 3, color);
            } catch (IllegalArgumentException unknownBody) {
                // Keep a stale or future body catalogue entry from taking down the map screen.
            }
        }
        graphics.drawString(font, "AU rings: 1 / 5 / 10 / 20 / 40", cx - radius, cy + radius + 3,
                0xFF8196A5, false);
    }

    private void drawSystemBody(GuiGraphics graphics, String bodyId, String label, int[] point,
                                int labelOffsetX, int labelOffsetY, int dotRadius, int color) {
        int labelX = point[0] + labelOffsetX;
        int labelY = point[1] + labelOffsetY;
        if (labelOffsetX != 0 || labelOffsetY != 0) {
            drawSystemLeader(graphics, point[0], point[1], labelX, labelY, 0x668F9BA8);
        }
        fillCircle(graphics, point[0], point[1], dotRadius, color);
        graphics.drawString(font, label, labelX, labelY, color, false);
        addSystemHit(bodyId, point[0], point[1]);
        addSystemHit(bodyId, labelX + font.width(label) / 2, labelY + font.lineHeight / 2);
    }

    private void addSystemHit(String bodyId, int x, int y) {
        systemBodyHits.computeIfAbsent(bodyId, ignored -> new ArrayList<>()).add(new int[] {x, y});
    }

    private static int[] systemLabelOffset(String bodyId) {
        return switch (bodyId) {
            case "mercury" -> new int[] {-27, -13};
            case "venus" -> new int[] {-27, 2};
            case "earth" -> new int[] {8, -13};
            case "mars" -> new int[] {8, 2};
            case "jupiter" -> new int[] {9, -10};
            case "saturn" -> new int[] {9, 3};
            case "uranus" -> new int[] {-25, -10};
            case "neptune" -> new int[] {-25, 3};
            case "ceres" -> new int[] {-28, -12};
            case "vesta" -> new int[] {8, -12};
            case "pallas" -> new int[] {-28, 2};
            case "hygiea" -> new int[] {8, 2};
            default -> new int[] {6, -4};
        };
    }

    private static void drawSystemLeader(GuiGraphics graphics, int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0);
        int sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0);
        int sy = y0 < y1 ? 1 : -1;
        int error = dx + dy;
        while (true) {
            graphics.fill(x0, y0, x0 + 1, y0 + 1, color);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int twiceError = 2 * error;
            if (twiceError >= dy) {
                error += dy;
                x0 += sx;
            }
            if (twiceError <= dx) {
                error += dx;
                y0 += sy;
            }
        }
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private void drawSystemOrbit(GuiGraphics graphics, String bodyId, int cx, int cy, int radius, int color) {
        Integer px = null;
        Integer py = null;
        for (int i = 0; i <= 96; i++) {
            double offset = 2.0 * Math.PI * i / 96.0;
            Vector3d position = SolarSystemMap.orbitPoint(bodyId, mapEpochSeconds(), offset);
            int[] point = projectSystem(position, cx, cy, radius);
            if (px != null) {
                graphics.fill(Math.min(px, point[0]), Math.min(py, point[1]),
                        Math.max(px, point[0]) + 1, Math.max(py, point[1]) + 1, color);
            }
            px = point[0];
            py = point[1];
        }
    }

    private void drawSystemRing(GuiGraphics graphics, int cx, int cy, int radius, double ringAu, int color) {
        Integer px = null;
        Integer py = null;
        for (int i = 0; i <= 120; i++) {
            double angle = 2.0 * Math.PI * i / 120.0;
            Vector3d position = new Vector3d(ringAu * SolarSystem.AU_METERS * Math.cos(angle),
                    0.0, ringAu * SolarSystem.AU_METERS * Math.sin(angle));
            int[] point = projectSystem(position, cx, cy, radius);
            if (px != null) {
                graphics.fill(Math.min(px, point[0]), Math.min(py, point[1]),
                        Math.max(px, point[0]) + 1, Math.max(py, point[1]) + 1, color);
            }
            px = point[0];
            py = point[1];
        }
    }

    private int[] projectSystem(Vector3d position, int cx, int cy, int radius) {
        OrbitMapView.Projected point = OrbitMapView.projectSystem(position, cameraYaw, cameraPitch, cameraZoom);
        return new int[] {cx + (int) Math.round(point.x() * radius), cy - (int) Math.round(point.y() * radius)};
    }

    private Vector3d systemPosition(OrbitMapTrack track) {
        Vector3d body = SolarSystemMap.position(track.bodyId(), track.epochSeconds());
        double localRadius = SolarSystem.body(track.bodyId()).radiusMeters() + track.altitudeMeters();
        return body.add(track.positionDirection().multiply(localRadius));
    }

    private double mapEpochSeconds() {
        return tracks.stream().filter(track -> track.id().equals(selectedTrackId))
                .mapToDouble(OrbitMapTrack::epochSeconds).findFirst().orElse(0.0);
    }

    private static int systemColor(String bodyId) {
        return switch (bodyId) {
            case "mercury" -> 0xFFAAA49A;
            case "venus" -> 0xFFE5B36A;
            case "earth" -> 0xFF58A6E7;
            case "mars" -> 0xFFE07152;
            case "jupiter" -> 0xFFD6A66C;
            case "saturn" -> 0xFFE9D28E;
            case "uranus" -> 0xFF75D1D4;
            case "neptune" -> 0xFF5577EE;
            case "ceres", "vesta", "pallas", "hygiea" -> 0xFFB59B78;
            default -> 0xFFB0B7C3;
        };
    }

private void drawTwoDimensionalMap(GuiGraphics graphics, OrbitMapView.Frame frame, int[] disk,
                                        int cx, int cy, int radius) {
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
    }

private void drawThreeDimensionalMap(GuiGraphics graphics, OrbitMapView.Frame frame, int[] disk,
                                          int cx, int cy, int radius, int mouseX, int mouseY) {
        fillCircle(graphics, cx, cy, radius, 0xFF07111B);
        int bodyRadius = (int) Math.round(radius * OrbitMapView.EARTH_FRACTION * cameraZoom);
        fillSphere(graphics, cx, cy, bodyRadius, disk[0]);
        drawGlobeGrid(graphics, cx, cy, radius, disk[1]);
        if (frame.handoff()) {
            drawSpatialRing(graphics, cx, cy, radius, 20_000.0, RING);
        }
        drawSpatialRing(graphics, cx, cy, radius, OrbitMapView.VIEW_ALTITUDE_METERS, PARKING);
        for (OrbitMapTrack track : tracks) {
            if (OrbitMapView.inFrame(track, frame.bodyId())) {
                drawOrbit3d(graphics, track, cx, cy, radius,
                        track.id().equals(selectedTrackId) ? 0xFF66D9EF : 0x885A9EAA);
            }
        }
    }

    private int[] mapPoint(OrbitMapTrack track, int cx, int cy, int radius) {
        if (mapMode == MapMode.SYSTEM) {
            return projectSystem(systemPosition(track), cx, cy, radius);
        }
        if (mapMode == MapMode.THREE_D) {
            OrbitMapView.Projected point = OrbitMapView.project3d(
                    track.altitudeMeters(), track.positionDirection(), cameraYaw, cameraPitch, cameraZoom);
            return new int[] {cx + (int) Math.round(point.x() * radius), cy - (int) Math.round(point.y() * radius)};
        }
        OrbitMapView.Plot point = OrbitMapView.plot(track);
        return new int[] {cx + (int) Math.round(point.x() * radius), cy - (int) Math.round(point.y() * radius)};
    }

    private void fillSphere(GuiGraphics graphics, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            double y = dy / (double) Math.max(1, radius);
            int span = (int) Math.round(Math.sqrt(Math.max(0.0, 1.0 - y * y)) * radius);
            double light = 0.35 + 0.65 * Math.sqrt(Math.max(0.0, 1.0 - y * y));
            graphics.fill(cx - span, cy + dy, cx + span + 1, cy + dy + 1, shade(color, light));
        }
        ring(graphics, cx, cy, radius, 0x99D6EAF8);
    }

    private static int shade(int color, double factor) {
        int alpha = (color >>> 24) & 0xFF;
        int red = Math.min(255, (int) (((color >>> 16) & 0xFF) * factor));
        int green = Math.min(255, (int) (((color >>> 8) & 0xFF) * factor));
        int blue = Math.min(255, (int) ((color & 0xFF) * factor));
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    private void drawGlobeGrid(GuiGraphics graphics, int cx, int cy, int radius, int gridColor) {
        for (int latitude = -60; latitude <= 60; latitude += 30) {
            double lat = Math.toRadians(latitude);
            Integer px = null;
            Integer py = null;
            double oldDepth = -1.0;
            for (int i = 0; i <= 96; i++) {
                double lon = 2.0 * Math.PI * i / 96.0;
                Vector3d direction = new Vector3d(Math.cos(lat) * Math.cos(lon), Math.sin(lat), Math.cos(lat) * Math.sin(lon));
                OrbitMapView.Projected point = OrbitMapView.project3d(0.0, direction, cameraYaw, cameraPitch, cameraZoom);
                int x = cx + (int) Math.round(point.x() * radius);
                int y = cy - (int) Math.round(point.y() * radius);
                if (px != null && point.depth() >= 0.0 && oldDepth >= 0.0) {
                    graphics.fill(Math.min(px, x), Math.min(py, y), Math.max(px, x) + 1, Math.max(py, y) + 1, gridColor);
                }
                px = point.depth() >= 0.0 ? x : null;
                py = point.depth() >= 0.0 ? y : null;
                oldDepth = point.depth();
            }
        }
        for (int longitude = 0; longitude < 360; longitude += 60) {
            double lon = Math.toRadians(longitude);
            Integer px = null;
            Integer py = null;
            double oldDepth = -1.0;
            for (int i = 0; i <= 64; i++) {
                double lat = -Math.PI / 2.0 + Math.PI * i / 64.0;
                Vector3d direction = new Vector3d(Math.cos(lat) * Math.cos(lon), Math.sin(lat), Math.cos(lat) * Math.sin(lon));
                OrbitMapView.Projected point = OrbitMapView.project3d(0.0, direction, cameraYaw, cameraPitch, cameraZoom);
                int x = cx + (int) Math.round(point.x() * radius);
                int y = cy - (int) Math.round(point.y() * radius);
                if (px != null && point.depth() >= 0.0 && oldDepth >= 0.0) {
                    graphics.fill(Math.min(px, x), Math.min(py, y), Math.max(px, x) + 1, Math.max(py, y) + 1, gridColor);
                }
                px = point.depth() >= 0.0 ? x : null;
                py = point.depth() >= 0.0 ? y : null;
                oldDepth = point.depth();
            }
        }
    }

    private void drawSpatialRing(GuiGraphics graphics, int cx, int cy, int radius, double altitude, int color) {
        Integer px = null;
        Integer py = null;
        double oldDepth = -1.0;
        for (int i = 0; i <= 160; i++) {
            double angle = 2.0 * Math.PI * i / 160.0;
            Vector3d direction = new Vector3d(Math.cos(angle), 0.0, Math.sin(angle));
            OrbitMapView.Projected point = OrbitMapView.project3d(altitude, direction, cameraYaw, cameraPitch, cameraZoom);
            int x = cx + (int) Math.round(point.x() * radius);
            int y = cy - (int) Math.round(point.y() * radius);
            if (px != null && point.depth() >= 0.0 && oldDepth >= 0.0) {
                graphics.fill(Math.min(px, x), Math.min(py, y), Math.max(px, x) + 1, Math.max(py, y) + 1, color);
            }
            px = point.depth() >= 0.0 ? x : null;
            py = point.depth() >= 0.0 ? y : null;
            oldDepth = point.depth();
        }
    }

    private void drawOrbit3d(GuiGraphics graphics, OrbitMapTrack track, int cx, int cy, int radius, int color) {
        if (!Double.isFinite(track.apoapsisAltitudeMeters())) {
            return;
        }
        double bodyRadius;
        try {
            bodyRadius = SolarSystem.body(track.bodyId()).radiusMeters();
        } catch (IllegalArgumentException unknownBody) {
            return;
        }
        if (!(bodyRadius + track.periapsisAltitudeMeters() > bodyRadius)
                || !(bodyRadius + track.apoapsisAltitudeMeters() >= bodyRadius + track.periapsisAltitudeMeters())) {
            return;
        }
        Integer px = null;
        Integer py = null;
        double previousDepth = 0.0;
        for (int i = 0; i <= 192; i++) {
            double anomaly = 2.0 * Math.PI * i / 192.0;
            Vector3d position = OrbitMapView.conicPosition(bodyRadius, track.periapsisAltitudeMeters(),
                    track.apoapsisAltitudeMeters(), track.periapsisDirection(), track.orbitNormal(), anomaly);
            double altitude = position.magnitude() - bodyRadius;
            OrbitMapView.Projected point = OrbitMapView.project3d(
                    altitude, position.normalized(), cameraYaw, cameraPitch, cameraZoom);
            int x = cx + (int) Math.round(point.x() * radius);
            int y = cy - (int) Math.round(point.y() * radius);
            boolean visible = point.depth() >= 0.0
                    || Math.hypot(point.x(), point.y()) > OrbitMapView.EARTH_FRACTION * cameraZoom;
            if (px != null && visible && (previousDepth >= 0.0
                    || Math.hypot(point.x(), point.y()) > OrbitMapView.EARTH_FRACTION * cameraZoom)) {
                graphics.fill(Math.min(px, x), Math.min(py, y), Math.max(px, x) + 1, Math.max(py, y) + 1, color);
            }
            px = visible ? x : null;
            py = visible ? y : null;
            previousDepth = point.depth();
        }
    }

    private void drawPanelTabs(GuiGraphics graphics, int x) {
        String[] keys = {"tab_flight", "tab_plan", "tab_control"};
        Panel[] panels = Panel.values();
        for (int i = 0; i < panels.length; i++) {
            int bx = x + i * 43;
            boolean active = panel == panels[i];
            graphics.fill(bx, 40, bx + 40, 53, active ? 0xFF24546A : 0xFF263746);
            graphics.drawCenteredString(font,
                    Component.translatable("screen.aeronauticsplus.orbit_map." + keys[i]),
                    bx + 20, 43, active ? 0xFFFFFFFF : 0xFFBDEBFA);
        }
    }

    private void drawFlightData(GuiGraphics graphics, OrbitMapTrack track, int x) {
        int y = 59;
        y = dataLine(graphics, "BODY", track.bodyId(), x, y);
        y = dataLine(graphics, "ALT", String.format(Locale.ROOT, "%.2f km", track.altitudeMeters() / 1000.0), x, y);
        y = dataLine(graphics, "SPEED", String.format(Locale.ROOT, "%.1f m/s", track.totalSpeedMetersPerSecond()), x, y);
        y = dataLine(graphics, "RADIAL", String.format(Locale.ROOT, "%+.1f m/s", track.radialSpeedMetersPerSecond()), x, y);
        y = dataLine(graphics, "HORIZ", String.format(Locale.ROOT, "%.1f m/s", track.horizontalSpeedMetersPerSecond()), x, y);
        y = dataLine(graphics, "AP", Double.isFinite(track.apoapsisAltitudeMeters())
                ? String.format(Locale.ROOT, "%.1f km", track.apoapsisAltitudeMeters() / 1000.0) : "ESCAPE", x, y);
        y = dataLine(graphics, "PE", String.format(Locale.ROOT, "%.1f km", track.periapsisAltitudeMeters() / 1000.0), x, y);
        y = dataLine(graphics, "INC (+Y)", Double.isFinite(track.inclinationDegrees())
                ? String.format(Locale.ROOT, "%.2f°", track.inclinationDegrees()) : "N/A (radial)", x, y);
        y = dataLine(graphics, "THROTTLE", String.format(Locale.ROOT, "%.0f%%", track.throttle() * 100.0), x, y);
        y = dataLine(graphics, "THRUST", String.format(Locale.ROOT, "%.0f N", track.availableThrustNewtons()), x, y);
        y = dataLine(graphics, "FUEL", String.format(Locale.ROOT, "%.1f kg", track.propellantMassKg()), x, y);
        dataLine(graphics, "Q", String.format(Locale.ROOT, "%.1f Pa", track.dynamicPressurePascals()), x, y);
    }

    private void drawFlightControls(GuiGraphics graphics, OrbitMapTrack track, int x) {
        if (track.presence() != FlightPresence.ORBIT_MAP) {
            graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.seated_controls"),
                    x, 61, TEXT, false);
            return;
        }
        int y = 59;
        y = dataLine(graphics, "SPEED", String.format(Locale.ROOT, "%.1f m/s", track.totalSpeedMetersPerSecond()), x, y);
        y = dataLine(graphics, "THROTTLE", String.format(Locale.ROOT, "%.0f%%", track.throttle() * 100.0), x, y);
        y = dataLine(graphics, "THRUST", String.format(Locale.ROOT, "%.0f N", track.availableThrustNewtons()), x, y);
        y = dataLine(graphics, "FUEL", String.format(Locale.ROOT, "%.1f kg", track.propellantMassKg()), x, y);
        Vector3d rates = track.angularVelocityBodyRadiansPerSecond();
        graphics.drawString(font, "P/Y/R", x, y, 0xFF879BA7, false);
        graphics.drawString(font, String.format(Locale.ROOT, "%+.1f %+.1f %+.1f°/s",
                Math.toDegrees(rates.x()), Math.toDegrees(rates.y()), Math.toDegrees(rates.z())),
                x + 35, y, TEXT, false);
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.engine_controls"),
                x, 137, 0xFFFFC857, false);
        String[] engine = {"CUT", "-10%", "+10%", "MAX"};
        for (int i = 0; i < engine.length; i++) {
            drawActionButton(graphics, x + i * 23, 150, 21, engine[i]);
        }
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.point_controls"),
                x, 172, 0xFFFFC857, false);
        String[] axes = {"P-", "P+", "Y-", "Y+"};
        for (int i = 0; i < axes.length; i++) {
            drawActionButton(graphics, x + i * 23, 185, 21, axes[i]);
        }
        String[] roll = {"R-", "R+", "STOP", "RCS"};
        for (int i = 0; i < roll.length; i++) {
            drawActionButton(graphics, x + i * 23, 203, 21, roll[i]);
        }
        graphics.drawString(font, Component.translatable("screen.aeronauticsplus.orbit_map.rcs_hint"),
                x, 220, 0xFF879BA7, false);
    }

    private void drawActionButton(GuiGraphics graphics, int x, int y, int buttonWidth, String label) {
        graphics.fill(x, y, x + buttonWidth, y + 13, 0xFF263746);
        graphics.drawCenteredString(font, label, x + buttonWidth / 2, y + 3, 0xFFBDEBFA);
    }

    private void sendControl(OrbitMapTrack track, int action) {
        if (track != null && track.presence() == FlightPresence.ORBIT_MAP) {
            PacketDistributor.sendToServer(new OrbitMapControlPayload(track.id(), action));
        }
    }

    /** Draw the two-body conic projected from its actual orbital plane into the XZ map. */
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
        if (!(bodyRadius + track.periapsisAltitudeMeters() > bodyRadius)
                || !(bodyRadius + track.apoapsisAltitudeMeters() >= bodyRadius + track.periapsisAltitudeMeters())) {
            return; // Skip impact trajectories and degenerate conics.
        }
        int segments = 160;
        Integer previousX = null;
        Integer previousY = null;
        for (int i = 0; i <= segments; i++) {
            double anomaly = 2.0 * Math.PI * i / segments;
            OrbitMapView.Plot point = OrbitMapView.conicPoint(
                    bodyRadius,
                    track.periapsisAltitudeMeters(),
                    track.apoapsisAltitudeMeters(),
                    track.periapsisDirection(),
                    track.orbitNormal(),
                    anomaly);
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
            case "ceres", "vesta", "pallas", "hygiea" -> new int[] {0xFF9A8B7C, 0xFF6E5E4C};
            default -> new int[] {EARTH, LAND};
        };
    }

    private void drawFooter(GuiGraphics graphics, int cx, OrbitMapView.Frame frame) {
        Component footerText = mapMode == MapMode.SYSTEM
                ? Component.literal("SOLAR SYSTEM • LOG AU • approximate two-body ephemeris") : footer(frame);
        graphics.drawString(font, footerText, 12, height - 18,
                mapMode == MapMode.SYSTEM ? 0xFF91C8E8 : frame.handoff() ? RING : PARKING);
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
