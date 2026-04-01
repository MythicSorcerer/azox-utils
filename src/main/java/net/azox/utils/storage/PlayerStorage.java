package net.azox.utils.storage;

import net.azox.utils.AzoxUtils;
import net.azox.utils.model.Home;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public final class PlayerStorage {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    private FileConfiguration getConfig(final OfflinePlayer player) {
        return player != null ? plugin.getPlayerDataManager().getConfig(player) : null;
    }

    private void save(final OfflinePlayer player) {
        if (player != null) {
            plugin.getPlayerDataManager().saveConfig(player.getUniqueId(), player.getName());
        }
    }

    private boolean getBoolean(final OfflinePlayer player, final String path, final boolean defaultValue) {
        final FileConfiguration config = getConfig(player);
        return config != null ? config.getBoolean(path, defaultValue) : defaultValue;
    }

    private void setBoolean(final OfflinePlayer player, final String path, final boolean value) {
        if (player != null) {
            getConfig(player).set(path, value);
            save(player);
        }
    }

    public void saveHome(final OfflinePlayer player, final Home home) {
        if (player == null || home == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
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
        if (player == null || name == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config != null) {
            config.set("homes." + name.toLowerCase(), null);
            save(player);
        }
    }

    public Map<String, Home> getHomes(final OfflinePlayer player) {
        final Map<String, Home> homes = new HashMap<>();
        if (player == null) {
            return homes;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return homes;
        }
        final ConfigurationSection section = config.getConfigurationSection("homes");
        if (section == null) {
            return homes;
        }

        for (final String key : section.getKeys(false)) {
            final ConfigurationSection homeSection = section.getConfigurationSection(key);
            if (homeSection != null) {
                homes.put(key, createHomeFromSection(player.getUniqueId(), homeSection));
            }
        }
        return homes;
    }

    public List<Home> getPublicHomes() {
        final List<Home> publicHomes = new ArrayList<>();
        final File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            return publicHomes;
        }

        final File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return publicHomes;
        }

        for (final File file : files) {
            final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection homesSection = config.getConfigurationSection("homes");
            if (homesSection == null) {
                continue;
            }

            final String fileName = file.getName();
            final String uuidStr = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));
            final UUID uuid;
            try {
                uuid = UUID.fromString(uuidStr);
            } catch (final IllegalArgumentException exception) {
                continue;
            }

            for (final String key : homesSection.getKeys(false)) {
                final ConfigurationSection homeSection = homesSection.getConfigurationSection(key);
                if (homeSection != null && homeSection.getBoolean("isPublic")) {
                    publicHomes.add(createHomeFromSection(uuid, homeSection));
                }
            }
        }
        return publicHomes;
    }

    private Home createHomeFromSection(final UUID ownerUuid, final ConfigurationSection section) {
        return new Home(
                ownerUuid,
                section.getString("name"),
                section.getString("world"),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"),
                section.getBoolean("isPublic"),
                section.getString("description"),
                section.getLong("creationDate")
        );
    }

    public void setBackLocation(final OfflinePlayer player, final Location location) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        if (location == null) {
            config.set("back", null);
        } else {
            config.set("back.world", location.getWorld() != null ? location.getWorld().getName() : null);
            config.set("back.x", Math.round(location.getX() * 100.0) / 100.0);
            config.set("back.y", Math.round(location.getY() * 100.0) / 100.0);
            config.set("back.z", Math.round(location.getZ() * 100.0) / 100.0);
            config.set("back.yaw", Math.round(location.getYaw() * 100.0) / 100.0);
            config.set("back.pitch", Math.round(location.getPitch() * 100.0) / 100.0);
        }
        save(player);
    }

    public Location getBackLocation(final OfflinePlayer player) {
        if (player == null) {
            return null;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return null;
        }
        final ConfigurationSection section = config.getConfigurationSection("back");
        if (section == null) {
            return null;
        }
        final String worldName = section.getString("world", "");
        if (worldName == null || worldName.isEmpty()) {
            return null;
        }
        final World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, section.getDouble("x"), section.getDouble("y"), section.getDouble("z"), (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }

    public boolean isGuiEnabled(final OfflinePlayer player) {
        return getBoolean(player, "prefs.gui", true);
    }

    public void setGuiEnabled(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.gui", enabled);
    }

    public boolean areParticlesEnabled(final OfflinePlayer player) {
        return getBoolean(player, "prefs.particles", true);
    }

    public void setParticlesEnabled(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.particles", enabled);
    }

    public boolean isVanishFakeMessages(final OfflinePlayer player) {
        return getBoolean(player, "prefs.vanish.fake_messages", true);
    }

    public void setVanishFakeMessages(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.vanish.fake_messages", enabled);
    }

    public boolean isVanishAutoFly(final OfflinePlayer player) {
        return getBoolean(player, "prefs.vanish.auto_fly", true);
    }

    public void setVanishAutoFly(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.vanish.auto_fly", enabled);
    }

    public boolean isVanishAutoGod(final OfflinePlayer player) {
        return getBoolean(player, "prefs.vanish.auto_god", true);
    }

    public void setVanishAutoGod(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.vanish.auto_god", enabled);
    }

    public boolean isVanishPickupDisabled(final OfflinePlayer player) {
        return getBoolean(player, "prefs.vanish.pickup_disabled", true);
    }

    public void setVanishPickupDisabled(final OfflinePlayer player, final boolean disabled) {
        setBoolean(player, "prefs.vanish.pickup_disabled", disabled);
    }

    public boolean isGodMobsIgnore(final OfflinePlayer player) {
        return getBoolean(player, "prefs.god.mobs_ignore", true);
    }

    public void setGodMobsIgnore(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.god.mobs_ignore", enabled);
    }

    public boolean isTpIgnore(final OfflinePlayer player) {
        return getBoolean(player, "prefs.tp_ignore", false);
    }

    public void setTpIgnore(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.tp_ignore", enabled);
    }

    public boolean isNightVisionEnabled(final OfflinePlayer player) {
        return getBoolean(player, "prefs.night_vision", false);
    }

    public void setNightVisionEnabled(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "prefs.night_vision", enabled);
    }

    public void setJailed(final OfflinePlayer player, final String name, final boolean inescapable) {
        setJailed(player, name, inescapable, null);
    }

    public void setJailed(final OfflinePlayer player, final String name, final boolean inescapable, final Long durationMillis) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return;
        }
        config.set("jail.name", name);
        config.set("jail.inescapable", inescapable);
        if (durationMillis != null) {
            config.set("jail.releaseTime", System.currentTimeMillis() + durationMillis);
        } else {
            config.set("jail.releaseTime", null);
        }
        save(player);
    }

    public void setUnjailed(final OfflinePlayer player) {
        if (player == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config != null) {
            config.set("jail", null);
            save(player);
        }
    }

    public String getJailName(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        return config != null ? config.getString("jail.name") : null;
    }

    public boolean isJailInescapable(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        return config != null && config.getBoolean("jail.inescapable", false);
    }

    public Long getJailReleaseTime(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return null;
        }
        final long releaseTime = config.getLong("jail.releaseTime", -1L);
        return releaseTime > 0 ? releaseTime : null;
    }

    public boolean shouldReleaseFromJail(final OfflinePlayer player) {
        final Long releaseTime = getJailReleaseTime(player);
        return releaseTime != null && System.currentTimeMillis() >= releaseTime;
    }

    public void saveEnderChestPage(final OfflinePlayer player, final int page, final ItemStack[] items) {
        if (player == null || items == null) {
            return;
        }
        final FileConfiguration config = getConfig(player);
        if (config != null) {
            config.set("enderchest.page_" + page, items);
            save(player);
        }
    }

    public ItemStack[] getEnderChestPage(final OfflinePlayer player, final int page) {
        if (player == null) {
            return new ItemStack[27];
        }
        final FileConfiguration config = getConfig(player);
        if (config == null) {
            return new ItemStack[27];
        }
        final List<?> list = config.getList("enderchest.page_" + page);
        if (list == null) {
            return new ItemStack[27];
        }
        return list.toArray(new ItemStack[0]);
    }

    public boolean hasPermEffects(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return false;
        return config.contains("permeffects");
    }

    public Integer getPermEffectLevel(final OfflinePlayer player, final String effectName) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return null;
        return config.contains("permeffects." + effectName) ? config.getInt("permeffects." + effectName) : null;
    }

    public void setPermEffect(final OfflinePlayer player, final String effectName, final int level) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return;
        config.set("permeffects." + effectName, level);
        save(player);
    }

    public void clearPermEffects(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return;
        config.set("permeffects", null);
        save(player);
    }

    public boolean isILiveEnabled(final OfflinePlayer player) {
        return getBoolean(player, "ilive.enabled", false);
    }

    public void setILiveEnabled(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "ilive.enabled", enabled);
    }

    public String getILiveMode(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        return config != null ? config.getString("ilive.mode", "nototem") : "nototem";
    }

    public void setILiveMode(final OfflinePlayer player, final String mode) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return;
        config.set("ilive.mode", mode);
        save(player);
    }

    public int getILiveDamageReduction(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        return config != null ? config.getInt("ilive.damageReduction", 1) : 1;
    }

    public void setILiveDamageReduction(final OfflinePlayer player, final int reduction) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return;
        config.set("ilive.damageReduction", reduction);
        save(player);
    }

    public boolean isILiveFoodHealEnabled(final OfflinePlayer player) {
        return getBoolean(player, "ilive.foodHeal", true);
    }

    public void setILiveFoodHeal(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "ilive.foodHeal", enabled);
    }

    public boolean isSilenced(final OfflinePlayer player) {
        return getBoolean(player, "prefs.silenced", false);
    }

    public void setSilenced(final OfflinePlayer player, final boolean silenced) {
        setBoolean(player, "prefs.silenced", silenced);
    }

    public boolean hasSavedPotions(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return false;
        return config.contains("fillpot.saved");
    }

    public List<String> getSavedPotions(final OfflinePlayer player) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return new ArrayList<>();
        return config.getStringList("fillpot.saved");
    }

    public void savePotions(final OfflinePlayer player, final List<String> potions) {
        final FileConfiguration config = getConfig(player);
        if (config == null) return;
        config.set("fillpot.saved", potions);
        save(player);
    }

    public boolean isFillPotEnabled(final OfflinePlayer player) {
        return getBoolean(player, "fillpot.enabled", false);
    }

    public void setFillPotEnabled(final OfflinePlayer player, final boolean enabled) {
        setBoolean(player, "fillpot.enabled", enabled);
    }
}
