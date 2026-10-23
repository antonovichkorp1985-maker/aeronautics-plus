package dev.leeeonidys.aeronauticsplus.space.world;

import dev.leeeonidys.aeronauticsplus.space.core.OrbitMapTrack;
import java.util.List;
import java.util.function.Consumer;

/**
 * Client screen hook. Dedicated server keeps the no-op. The client assigns this in setup.
 */
public final class OrbitMapClientHooks {
    public static volatile Consumer<List<OrbitMapTrack>> OPEN = tracks -> {
    };

    private OrbitMapClientHooks() {
    }
}
