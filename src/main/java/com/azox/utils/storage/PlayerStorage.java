package com.azox.utils.storage;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public final class PlayerStorage {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    private FileConfiguration getConfig(final OfflinePlayer player) {
        return plugin.getPlayerDataManager().getConfig(player);
    }

    private void save(final OfflinePlayer player) {
        plugin.getPlayerDataManager().saveConfig(player.getUniqueId(), player.getName());
    }

    // Homes
    public void saveHome(final OfflinePlayer player, final Home home) {
        final FileConfiguration config = getConfig(player);
        final String path = "homes." + home.getName().toLowerCase();
        config.set(path + ".name", home.getName());
        config.set(path + ".world", home.getWorldName());
        config.set(path + ".x", home.getX());
        config.set(path + ".y", home.getY());
        config.set(path + ".z", home.getZ());
        config.set(path + ".yaw", home.getYaw());
        config.set(path + ".pitch", home.getPitch());
        config.set(path + ".isPublic", home.isPublic());
        config.set(path + ".description", home.getDescription());
        config.set(path + ".creationDate", home.getCreationDate());
        save(player);
    }

    public void deleteHome(final OfflinePlayer player, final String name) {
        getConfig(player).set("homes." + name.toLowerCase(), null);
        save(player);
    }

    public Map<String, Home> getHomes(final OfflinePlayer player) {
        final Map<String, Home> homes = new HashMap<>();
        final FileConfiguration config = getConfig(player);
        if (config == null) return homes;
        final ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) return homes;

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection h = section.getConfigurationSection(key);
            homes.put(key, new Home(
                    player.getUniqueId(),
                    h.getString("name"),
                    h.getString("world"),
                    h.getDouble("x"),
                    h.getDouble("y"),
                    h.getDouble("z"),
                    (float) h.getDouble("yaw"),
                    (float) h.getDouble("pitch"),
                    h.getBoolean("isPublic"),
                    h.getString("description"),
                    h.getLong("creationDate")
            ));
        }
        return homes;
    }

    public List<Home> getPublicHomes() {
        final List<Home> publicHomes = new ArrayList<>();
        final File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) return publicHomes;

        final File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return publicHomes;

        for (final File file : files) {
            final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection homesSection = config.getConfigurationSection("homes");
            if (homesSection == null) continue;

            final String fileName = file.getName();
            final String uuidStr = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));
            final UUID uuid = UUID.fromString(uuidStr);

            for (final String key : homesSection.getKeys(false)) {
                final ConfigurationSection h = homesSection.getConfigurationSection(key);
                if (h != null && h.getBoolean("isPublic")) {
                    publicHomes.add(new Home(
                            uuid,
                            h.getString("name"),
                            h.getString("world"),
                            h.getDouble("x"),
                            h.getDouble("y"),
                            h.getDouble("z"),
                            (float) h.getDouble("yaw"),
                            (float) h.getDouble("pitch"),
                            true,
                            h.getString("description"),
                            h.getLong("creationDate")
                    ));
                }
            }
        }
        return publicHomes;
    }

    // Back Location
    public void setBackLocation(final OfflinePlayer player, final Location loc) {
        final FileConfiguration config = getConfig(player);
        if (loc == null) {
            config.set("back", null);
        } else {
            config.set("back.world", loc.getWorld().getName());
            config.set("back.x", Math.round(loc.getX() * 100.0) / 100.0);
            config.set("back.y", Math.round(loc.getY() * 100.0) / 100.0);
            config.set("back.z", Math.round(loc.getZ() * 100.0) / 100.0);
            config.set("back.yaw", Math.round(loc.getYaw() * 100.0) / 100.0);
            config.set("back.pitch", Math.round(loc.getPitch() * 100.0) / 100.0);
        }
        save(player);
    }

    public Location getBackLocation(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return null;
        final ConfigurationSection s = config.getConfigurationSection("back");
        if (s == null) return null;
        final World w = Bukkit.getWorld(s.getString("world", ""));
        if (w == null) return null;
        return new Location(w, s.getDouble("x"), s.getDouble("y"), s.getDouble("z"), (float) s.getDouble("yaw"), (float) s.getDouble("pitch"));
    }

    // Preferences
    public boolean isGuiEnabled(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.gui", true); }
    public void setGuiEnabled(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.gui", v); save(player); }

    public boolean areParticlesEnabled(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.particles", true); }
    public void setParticlesEnabled(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.particles", v); save(player); }

    public boolean isVanishFakeMessages(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.vanish.fake_messages", true); }
    public void setVanishFakeMessages(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.vanish.fake_messages", v); save(player); }

    public boolean isVanishAutoFly(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.vanish.auto_fly", true); }
    public void setVanishAutoFly(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.vanish.auto_fly", v); save(player); }

    public boolean isVanishAutoGod(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.vanish.auto_god", true); }
    public void setVanishAutoGod(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.vanish.auto_god", v); save(player); }

    public boolean isVanishPickupDisabled(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.vanish.pickup_disabled", true); }
    public void setVanishPickupDisabled(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.vanish.pickup_disabled", v); save(player); }

    public boolean isGodMobsIgnore(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.god.mobs_ignore", true); }
    public void setGodMobsIgnore(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.god.mobs_ignore", v); save(player); }

    public boolean isTpIgnore(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.tp_ignore", false); }
    public void setTpIgnore(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.tp_ignore", v); save(player); }

    public boolean isNightVisionEnabled(final OfflinePlayer player) { return getConfig(player).getBoolean("prefs.night_vision", false); }
    public void setNightVisionEnabled(final OfflinePlayer player, boolean v) { getConfig(player).set("prefs.night_vision", v); save(player); }

    // Jail
    public void setJailed(final OfflinePlayer player, String name, boolean inescapable) {
        getConfig(player).set("jail.name", name);
        getConfig(player).set("jail.inescapable", inescapable);
        save(player);
    }
    public void setUnjailed(final OfflinePlayer player) { getConfig(player).set("jail", null); save(player); }
    public String getJailName(final OfflinePlayer player) { 
        FileConfiguration cfg = getConfig(player);
        return cfg != null ? cfg.getString("jail.name") : null;
    }
    public boolean isJailInescapable(final OfflinePlayer player) { 
        FileConfiguration cfg = getConfig(player);
        return cfg != null && cfg.getBoolean("jail.inescapable", false);
    }

    // Enderchest Pages
    public void saveEnderChestPage(final OfflinePlayer player, int page, ItemStack[] items) {
        getConfig(player).set("enderchest.page_" + page, items);
        save(player);
    }

    public ItemStack[] getEnderChestPage(final OfflinePlayer player, int page) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return new ItemStack[27];
        final List<?> list = config.getList("enderchest.page_" + page);
        if (list == null) return new ItemStack[27];
        return list.toArray(new ItemStack[0]);
    }
}
