package net.azox.utils.storage;

import net.azox.utils.model.Warp;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class WarpStorage extends BaseStorage {

    public WarpStorage() {
        super("warps.yml");
    }

    public void saveWarp(final Warp warp) {
        if (warp == null || warp.getName() == null) {
            return;
        }
        final String path = warp.getName();
        this.config.set(path + ".world", warp.getWorldName());
        this.config.set(path + ".x", warp.getX());
        this.config.set(path + ".y", warp.getY());
        this.config.set(path + ".z", warp.getZ());
        this.config.set(path + ".yaw", warp.getYaw());
        this.config.set(path + ".pitch", warp.getPitch());
        this.config.set(path + ".level", warp.getLevel());
        this.save();
    }

    public void deleteWarp(final String name) {
        if (name == null) {
            return;
        }
        this.config.set(name, null);
        this.save();
    }

    public Map<String, Warp> getWarps() {
        final Map<String, Warp> warps = new HashMap<>();
        for (final String key : this.config.getKeys(false)) {
            final ConfigurationSection section = this.config.getConfigurationSection(key);
            if (section == null) {
                continue;
            }

            final Warp warp = new Warp(
                    key,
                    section.getString("world"),
                    section.getDouble("x"),
                    section.getDouble("y"),
                    section.getDouble("z"),
                    (float) section.getDouble("yaw"),
                    (float) section.getDouble("pitch"),
                    section.getInt("level")
            );
            warps.put(key, warp);
        }
        return warps;
    }
}
