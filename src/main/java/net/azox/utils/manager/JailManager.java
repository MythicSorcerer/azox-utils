package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import net.azox.utils.storage.JailStorage;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class JailManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    @Getter
    private final JailStorage storage;
    private final Map<String, Location> cachedJails;

    public JailManager() {
        this.storage = new JailStorage();
        this.cachedJails = new ConcurrentHashMap<>(storage.getJails());
    }

    public Optional<Location> getJail(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cachedJails.get(name.toLowerCase()));
    }

    public void setJail(final String name, final Location location) {
        if (name == null || location == null) {
            return;
        }
        cachedJails.put(name.toLowerCase(), location);
        storage.saveJail(name, location);
    }

    public void deleteJail(final String name) {
        if (name == null) {
            return;
        }
        cachedJails.remove(name.toLowerCase());
        storage.deleteJail(name);
    }

    public void jailPlayer(final Player player, final String jailName, final boolean inescapable) {
        if (player == null || jailName == null) {
            return;
        }
        getJail(jailName).ifPresent(player::teleport);
        plugin.getPlayerStorage().setJailed(player, jailName, inescapable);
    }

    public void unjailPlayer(final Player player) {
        if (player == null) {
            return;
        }
        plugin.getPlayerStorage().setUnjailed(player);
    }

    public Map<String, Location> getJails() {
        return cachedJails;
    }
}
