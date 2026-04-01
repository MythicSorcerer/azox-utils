package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import net.azox.utils.model.Home;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HomeManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, Map<String, Home>> cachedHomes = new ConcurrentHashMap<>();

    public HomeManager() {
    }

    public Map<String, Home> getHomes(final Player player) {
        if (player == null) {
            return new HashMap<>();
        }
        return cachedHomes.computeIfAbsent(player.getUniqueId(), k -> plugin.getPlayerStorage().getHomes(player));
    }

    public Optional<Home> getHome(final Player player, final String name) {
        if (player == null || name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(getHomes(player).get(name.toLowerCase()));
    }

    public void setHome(final Player player, final String name, final Location location) {
        if (player == null || name == null || location == null) {
            return;
        }
        final Map<String, Home> homes = getHomes(player);

        final String homeName = name.toLowerCase();
        final Home home = homes.getOrDefault(homeName, new Home());
        home.setOwnerUuid(player.getUniqueId());
        home.setName(name);
        home.setLocation(location);
        if (home.getCreationDate() == 0) {
            home.setCreationDate(System.currentTimeMillis());
            home.setPublic(false);
            home.setDescription("");
        }

        homes.put(homeName, home);
        plugin.getPlayerStorage().saveHome(player, home);
    }

    public void deleteHome(final Player player, final String name) {
        if (player == null || name == null) {
            return;
        }
        final Map<String, Home> homes = getHomes(player);
        homes.remove(name.toLowerCase());
        plugin.getPlayerStorage().deleteHome(player, name.toLowerCase());
    }

    public void deleteAllHomes(final Player player) {
        if (player == null) {
            return;
        }
        cachedHomes.remove(player.getUniqueId());
        plugin.getPlayerStorage().getHomes(player).keySet().forEach(homeName -> plugin.getPlayerStorage().deleteHome(player, homeName));
    }

    public int getHomeLimit(final Player player) {
        if (player == null) {
            return 4;
        }
        if (player.hasPermission("azox.util.sethome.unlimited")) {
            return Integer.MAX_VALUE;
        }

        int limit = 4;
        for (int i = 100; i > limit; i--) {
            if (player.hasPermission("azox.util.homes." + i)) {
                return i;
            }
        }
        return limit;
    }

    public List<Home> getPublicHomes() {
        return plugin.getPlayerStorage().getPublicHomes();
    }
}
