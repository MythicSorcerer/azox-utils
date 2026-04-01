package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerDataManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final File dataFolder;
    private final Map<UUID, FileConfiguration> configs = new HashMap<>();

    public PlayerDataManager() {
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public FileConfiguration getConfig(final org.bukkit.OfflinePlayer player) {
        if (player == null || player.getUniqueId() == null) {
            return null;
        }
        return configs.computeIfAbsent(player.getUniqueId(), k -> loadConfig(player.getUniqueId(), player.getName()));
    }

    public FileConfiguration getConfig(final Player player) {
        if (player == null) {
            return null;
        }
        return configs.computeIfAbsent(player.getUniqueId(), k -> loadConfig(player));
    }

    public FileConfiguration getConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return null;
        }
        return configs.computeIfAbsent(uuid, k -> loadConfig(uuid, name));
    }

    private FileConfiguration loadConfig(final Player player) {
        if (player == null) {
            return null;
        }
        return loadConfig(player.getUniqueId(), player.getName());
    }

    private FileConfiguration loadConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return null;
        }
        final File file = getFile(uuid, name);
        if (file == null || !file.exists()) {
            try {
                if (file != null && !file.createNewFile()) {
                    plugin.getLogger().warning("Failed to create config file for: " + name);
                }
            } catch (final IOException exception) {
                plugin.getLogger().severe("Failed to create config file for: " + name);
                exception.printStackTrace();
            }
        }
        return file != null ? YamlConfiguration.loadConfiguration(file) : null;
    }

    public void saveConfig(final UUID uuid, final String name) {
        if (uuid == null) {
            return;
        }
        final FileConfiguration config = configs.get(uuid);
        if (config == null) {
            return;
        }
        final File file = getFile(uuid, name);
        if (file == null) {
            return;
        }
        try {
            config.save(file);
        } catch (final IOException exception) {
            plugin.getLogger().severe("Failed to save config for: " + name);
            exception.printStackTrace();
        }
    }

    private File getFile(final UUID uuid, final String name) {
        if (uuid == null || name == null) {
            return null;
        }
        return new File(dataFolder, name + "_" + uuid.toString() + ".yml");
    }

    public void unload(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        configs.remove(uuid);
    }
}
