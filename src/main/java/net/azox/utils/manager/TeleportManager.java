package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import net.azox.utils.model.TeleportRequest;
import net.azox.utils.storage.PlayerStorage;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class TeleportManager {

    private static final int TELEPORT_DELAY_SECONDS = 3;
    private static final long TELEPORT_EXPIRY_MILLIS = 60_000;
    private static final double MOVE_THRESHOLD_SQUARED = 0.1;

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, TeleportRequest> pendingRequests = new ConcurrentHashMap<>();
    private final Map<UUID, Location> lastLocations = new ConcurrentHashMap<>();
    private final Map<UUID, Location> pendingOfflineTeleports = new ConcurrentHashMap<>();
    private final Map<UUID, Location> undoLocations = new ConcurrentHashMap<>();

    public void addPendingTeleport(final UUID target, final Location destination) {
        if (target != null && destination != null) {
            pendingOfflineTeleports.put(target, destination);
        }
    }

    public Location getPendingTeleport(final UUID target) {
        return target != null ? pendingOfflineTeleports.remove(target) : null;
    }

    public void addUndoLocation(final UUID target, final Location oldLocation) {
        if (target != null && oldLocation != null) {
            undoLocations.put(target, oldLocation);
        }
    }

    public Location getUndoLocation(final UUID target) {
        return target != null ? undoLocations.remove(target) : null;
    }

    public void requestTeleport(final Player requester, final Player target, final boolean here) {
        if (requester != null && target != null) {
            pendingRequests.put(target.getUniqueId(), new TeleportRequest(requester, target, here, System.currentTimeMillis()));
        }
    }

    public Optional<TeleportRequest> getRequest(final Player target) {
        if (target == null) {
            return Optional.empty();
        }
        final TeleportRequest request = pendingRequests.get(target.getUniqueId());
        if (request != null && request.isExpired()) {
            pendingRequests.remove(target.getUniqueId());
            return Optional.empty();
        }
        return Optional.ofNullable(request);
    }

    public void removeRequest(final Player target) {
        if (target != null) {
            pendingRequests.remove(target.getUniqueId());
        }
    }

    public void teleportWithDelay(final Player player, final Location targetLocation) {
        if (player == null || targetLocation == null) {
            return;
        }

        if (player.hasPermission("azox.util.teleport.instant")) {
            teleportInstantly(player, targetLocation);
            return;
        }

        if (TELEPORT_DELAY_SECONDS <= 0) {
            teleportInstantly(player, targetLocation);
            return;
        }

        scheduleDelayedTeleport(player, targetLocation);
    }

    private void teleportInstantly(final Player player, final Location targetLocation) {
        setLastLocation(player, player.getLocation());
        player.teleport(targetLocation);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
    }

    private void scheduleDelayedTeleport(final Player player, final Location targetLocation) {
        MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_TP + " Teleporting in " + TELEPORT_DELAY_SECONDS + " seconds, please do not move...");
        final Location startLocation = player.getLocation();

        new BukkitRunnable() {
            private int count = TELEPORT_DELAY_SECONDS;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(startLocation) > MOVE_THRESHOLD_SQUARED) {
                    MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Teleport cancelled because you moved!");
                    cancel();
                    return;
                }

                if (count <= 0) {
                    setLastLocation(player, player.getLocation());
                    player.teleport(targetLocation);
                    MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Teleported!");
                    cancel();
                    return;
                }

                count--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void setLastLocation(final Player player, final Location location) {
        if (player != null && location != null) {
            lastLocations.put(player.getUniqueId(), location);
            plugin.getPlayerStorage().setBackLocation(player, location);
        }
    }

    public Optional<Location> getLastLocation(final Player player) {
        if (player == null) {
            return Optional.empty();
        }
        final Location cached = lastLocations.get(player.getUniqueId());
        if (cached != null) {
            return Optional.of(cached);
        }
        return Optional.ofNullable(plugin.getPlayerStorage().getBackLocation(player));
    }
}
