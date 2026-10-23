package dev.leeeonidys.aeronauticsplus.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.leeeonidys.aeronauticsplus.AeronauticsPlus;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;

/**
 * Small client-only updater for the separately distributed RU resource pack.
 * The ZIP never lives in GitHub: GitHub contains only this metadata and Drive
 * remains the binary source. All network and file failures are non-fatal.
 * Old AeronauticsPlus-RU-Pack-*.zip files are removed even when the download
 * itself is skipped or fails, so a stale second pack cannot linger in the GUI.
 */
public final class RuPackUpdater {
    private static final String MANIFEST_URL =
            "https://raw.githubusercontent.com/antonovichkorp1985-maker/aeronautics-plus/arena/84c40eba-aeronautics-plus/docs/localization/RU_PACK_RELEASE.json";
    private static final String FILE_PREFIX = "AeronauticsPlus-RU-Pack-";
    private static final Pattern PACK_NAME = Pattern.compile("AeronauticsPlus-RU-Pack-[^/\\\\]+\\.zip");
    private static final int CONNECT_TIMEOUT_SECONDS = 8;

    private RuPackUpdater() {
    }

    public static void start() {
        Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
        Path config = gameDir.resolve("config").resolve("aeronauticsplus-ru-pack.json");
        Settings settings = Settings.load(config);
        Path resourcepacks = gameDir.resolve("resourcepacks");
        CompletableFuture.runAsync(() -> run(gameDir, resourcepacks, settings))
                .exceptionally(error -> {
                    AeronauticsPlus.LOGGER.warn("RU-pack updater failed; keeping local pack", error);
                    return null;
                });
    }

    private static void run(Path gameDir, Path resourcepacks, Settings settings) {
        String current = null;
        try {
            Files.createDirectories(resourcepacks);
            if (settings.download()) {
                current = downloadCurrent(resourcepacks);
            } else {
                AeronauticsPlus.LOGGER.info("RU-pack download disabled by config");
            }
        } catch (Exception error) {
            AeronauticsPlus.LOGGER.warn("RU-pack update skipped; local files will still be cleaned", error);
        }
        try {
            if (settings.cleanup()) {
                cleanup(resourcepacks, current);
            }
            if (settings.enable()) {
                String enable = usablePack(resourcepacks, current);
                if (enable != null) {
                    selectPackOnNextLaunch(gameDir, enable);
                }
            }
        } catch (Exception error) {
            AeronauticsPlus.LOGGER.warn("RU-pack cleanup skipped", error);
        }
    }

    private static String downloadCurrent(Path resourcepacks) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        JsonObject manifest = getJson(client, MANIFEST_URL);
        String version = required(manifest, "version");
        String fileName = required(manifest, "file_name");
        String downloadUrl = required(manifest, "download_url");
        String expectedSha = required(manifest, "sha256").toLowerCase();
        if (!PACK_NAME.matcher(fileName).matches()) {
            throw new IOException("manifest contains an unsafe pack name: " + fileName);
        }

        Path target = resourcepacks.resolve(fileName).normalize();
        if (!target.getParent().equals(resourcepacks)) {
            throw new IOException("pack path escapes resourcepacks");
        }
        if (!Files.isRegularFile(target) || !expectedSha.equals(sha256(target))) {
            Path temporary = resourcepacks.resolve(fileName + ".download");
            Files.deleteIfExists(temporary);
            download(client, downloadUrl, temporary);
            if (!expectedSha.equals(sha256(temporary))) {
                Files.deleteIfExists(temporary);
                throw new IOException("SHA-256 mismatch for " + fileName);
            }
            if (!looksLikeZip(temporary)) {
                Files.deleteIfExists(temporary);
                throw new IOException("downloaded file is not a ZIP: " + fileName);
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            AeronauticsPlus.LOGGER.info("Downloaded RU-pack {} from public Drive link", version);
        } else {
            AeronauticsPlus.LOGGER.info("RU-pack {} is already current", version);
        }
        return fileName;
    }

    private static JsonObject getJson(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(15)).header("Accept", "application/json")
                .header("User-Agent", "AeronauticsPlus-RUPack-Updater/1")
                .GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() / 100 != 2) throw new IOException("HTTP " + response.statusCode() + " for manifest");
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    private static void download(HttpClient client, String url, Path destination) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofMinutes(2))
                .header("User-Agent", "AeronauticsPlus-RUPack-Updater/1").GET().build();
        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(destination));
        if (response.statusCode() / 100 != 2) throw new IOException("HTTP " + response.statusCode() + " for pack");
    }

    private static void selectPackOnNextLaunch(Path gameDir, String fileName) throws IOException {
        Path options = gameDir.resolve("options.txt");
        if (!Files.isRegularFile(options)) return;
        List<String> lines = Files.readAllLines(options, StandardCharsets.UTF_8);
        String wanted = "file/" + fileName;
        boolean changed = false;
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).startsWith("resourcePacks:")) continue;
            String line = lines.get(i);
            int open = line.indexOf('['), close = line.lastIndexOf(']');
            if (open < 0 || close < open) return;
            String inside = line.substring(open + 1, close);
            List<String> packs = new ArrayList<>();
            for (String token : inside.split(",")) {
                String pack = token.trim();
                if (pack.startsWith("\"") && pack.endsWith("\"")) pack = pack.substring(1, pack.length() - 1);
                if (!pack.startsWith("file/" + FILE_PREFIX) && !pack.equals(wanted) && !pack.isBlank()) packs.add(pack);
            }
            packs.add(wanted);
            lines.set(i, "resourcePacks:[\"" + String.join("\",\"", packs) + "\"]");
            changed = true;
        }
        if (changed) Files.write(options, lines, StandardCharsets.UTF_8);
    }

    private static void cleanup(Path directory, String keepName) throws IOException {
        List<Path> packs = listPacks(directory);
        Path keep = keepFile(directory, packs, keepName);
        for (Path pack : packs) {
            if (keep != null && pack.equals(keep)) continue;
            Files.deleteIfExists(pack);
            AeronauticsPlus.LOGGER.info("Removed old RU-pack {}", pack.getFileName());
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, FILE_PREFIX + "*.download")) {
            for (Path leftover : stream) {
                Files.deleteIfExists(leftover);
            }
        }
    }

    private static String usablePack(Path directory, String preferred) throws IOException {
        if (preferred != null && Files.isRegularFile(directory.resolve(preferred))) {
            return preferred;
        }
        Path newest = keepFile(directory, listPacks(directory), null);
        return newest == null ? null : newest.getFileName().toString();
    }

    private static Path keepFile(Path directory, List<Path> packs, String keepName) {
        if (keepName != null) {
            Path named = directory.resolve(keepName).toAbsolutePath().normalize();
            if (Files.isRegularFile(named)) return named;
        }
        return packs.stream().max(Comparator.comparingLong(RuPackUpdater::modified)).orElse(null);
    }

    private static List<Path> listPacks(Path directory) throws IOException {
        List<Path> packs = new ArrayList<>();
        if (!Files.isDirectory(directory)) return packs;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, FILE_PREFIX + "*.zip")) {
            for (Path path : stream) packs.add(path.toAbsolutePath().normalize());
        }
        return packs;
    }

    private static long modified(Path path) {
        try { return Files.getLastModifiedTime(path).toMillis(); }
        catch (IOException ignored) { return 0; }
    }

    private static boolean looksLikeZip(Path path) throws IOException {
        try (var input = Files.newInputStream(path)) {
            return input.read() == 'P' && input.read() == 'K';
        }
    }

    private static String sha256(Path path) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (var input = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192]; int read;
            while ((read = input.read(buffer)) >= 0) if (read > 0) digest.update(buffer, 0, read);
        }
        StringBuilder out = new StringBuilder();
        for (byte value : digest.digest()) out.append(String.format("%02x", value));
        return out.toString();
    }

    private static String required(JsonObject object, String key) throws IOException {
        if (!object.has(key) || object.get(key).isJsonNull()) throw new IOException("manifest lacks " + key);
        return object.get(key).getAsString();
    }

    private record Settings(boolean enable, boolean download, boolean cleanup) {
        static Settings load(Path path) {
            try {
                if (Files.isRegularFile(path)) {
                    JsonObject json = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
                    return new Settings(value(json, "auto_enable_ru_pack", true), value(json, "auto_download_ru_pack", true),
                            value(json, "auto_cleanup_old_ru_packs", true));
                }
                Files.createDirectories(path.getParent());
                Files.writeString(path, "{\n  \"auto_enable_ru_pack\": true,\n  \"auto_download_ru_pack\": true,\n  \"auto_cleanup_old_ru_packs\": true\n}\n");
            } catch (Exception error) {
                AeronauticsPlus.LOGGER.warn("Could not read RU-pack updater config; using defaults", error);
            }
            return new Settings(true, true, true);
        }
        private static boolean value(JsonObject o, String k, boolean d) { return o.has(k) ? o.get(k).getAsBoolean() : d; }
    }
}
