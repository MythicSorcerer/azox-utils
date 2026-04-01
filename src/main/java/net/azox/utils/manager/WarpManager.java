package net.azox.utils.manager;

import net.azox.utils.model.Warp;
import net.azox.utils.storage.WarpStorage;
import lombok.Getter;
import org.bukkit.Location;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class WarpManager {

    @Getter
    private final WarpStorage storage;
    private final Map<String, Warp> cachedWarps;

    public WarpManager() {
        this.storage = new WarpStorage();
        this.cachedWarps = new ConcurrentHashMap<>(storage.getWarps());
    }

    public Optional<Warp> getWarp(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cachedWarps.get(name.toLowerCase()));
    }

    public void setWarp(final String name, final Location location, final int level) {
        if (name == null || location == null) {
            return;
        }
        final String warpName = name.toLowerCase();
        final Warp warp = cachedWarps.getOrDefault(warpName, new Warp());
        warp.setName(name);
        warp.setLocation(location);
        warp.setLevel(level);

        cachedWarps.put(warpName, warp);
        storage.saveWarp(warp);
    }

    public void deleteWarp(final String name) {
        if (name == null) {
            return;
        }
        cachedWarps.remove(name.toLowerCase());
        storage.deleteWarp(name.toLowerCase());
    }

    public Map<String, Warp> getWarps() {
        return cachedWarps;
    }
}
