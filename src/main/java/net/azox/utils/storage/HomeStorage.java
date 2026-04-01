package net.azox.utils.storage;

import net.azox.utils.model.Home;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class HomeStorage extends BaseStorage {

    public HomeStorage() {
        super("homes.yml");
    }

    public void saveHome(final Home home) {
        if (home == null || home.getOwnerUuid() == null) {
            return;
        }
        final String path = home.getOwnerUuid().toString() + "." + home.getName();
        this.config.set(path + ".world", home.getWorldName());
        this.config.set(path + ".x", home.getX());
        this.config.set(path + ".y", home.getY());
        this.config.set(path + ".z", home.getZ());
        this.config.set(path + ".yaw", home.getYaw());
        this.config.set(path + ".pitch", home.getPitch());
        this.config.set(path + ".isPublic", home.isPublic());
        this.config.set(path + ".description", home.getDescription());
        this.config.set(path + ".creationDate", home.getCreationDate());
        this.save();
    }

    public void deleteHome(final UUID ownerUuid, final String name) {
        if (ownerUuid == null || name == null) {
            return;
        }
        this.config.set(ownerUuid.toString() + "." + name, null);
        this.save();
    }

    public void deleteAllHomes(final UUID ownerUuid) {
        if (ownerUuid == null) {
            return;
        }
        this.config.set(ownerUuid.toString(), null);
        this.save();
    }

    public Map<String, Home> getHomes(final UUID ownerUuid) {
        final Map<String, Home> homes = new HashMap<>();
        if (ownerUuid == null) {
            return homes;
        }
        final ConfigurationSection section = this.config.getConfigurationSection(ownerUuid.toString());
        if (section == null) {
            return homes;
        }

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection homeSection = section.getConfigurationSection(key);
            if (homeSection == null) {
                continue;
            }

            final Home home = new Home(
                    ownerUuid,
                    key,
                    homeSection.getString("world"),
                    homeSection.getDouble("x"),
                    homeSection.getDouble("y"),
                    homeSection.getDouble("z"),
                    (float) homeSection.getDouble("yaw"),
                    (float) homeSection.getDouble("pitch"),
                    homeSection.getBoolean("isPublic"),
                    homeSection.getString("description"),
                    homeSection.getLong("creationDate")
            );
            homes.put(key, home);
        }
        return homes;
    }

    public List<Home> getPublicHomes() {
        final List<Home> publicHomes = new ArrayList<>();
        for (final String uuidStr : this.config.getKeys(false)) {
            final UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (final IllegalArgumentException exception) {
                continue;
            }
            final ConfigurationSection userSection = this.config.getConfigurationSection(uuidStr);
            if (userSection == null) {
                continue;
            }

            for (final String homeName : userSection.getKeys(false)) {
                final ConfigurationSection homeSection = userSection.getConfigurationSection(homeName);
                if (homeSection != null && homeSection.getBoolean("isPublic")) {
                    publicHomes.add(new Home(
                            uuid,
                            homeName,
                            homeSection.getString("world"),
                            homeSection.getDouble("x"),
                            homeSection.getDouble("y"),
                            homeSection.getDouble("z"),
                            (float) homeSection.getDouble("yaw"),
                            (float) homeSection.getDouble("pitch"),
                            true,
                            homeSection.getString("description"),
                            homeSection.getLong("creationDate")
                    ));
                }
            }
        }
        return publicHomes;
    }
}
