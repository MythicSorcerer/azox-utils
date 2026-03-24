package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishManager {

    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<UUID> noPickupPlayers = new HashSet<>();
    private final AzoxUtils plugin = AzoxUtils.getInstance();

    public void toggleVanish(final Player player) {
        if (vanishedPlayers.contains(player.getUniqueId())) {
            unvanish(player);
        } else {
            vanish(player);
        }
    }

    public void vanish(final Player player) {
        vanishedPlayers.add(player.getUniqueId());

        // Apply Preferences
        if (plugin.getPlayerStorage().isVanishPickupDisabled(player)) {
            noPickupPlayers.add(player.getUniqueId());
        }

        if (plugin.getPlayerStorage().isVanishFakeMessages(player)) {
            fakeQuit(player);
        }

        if (plugin.getPlayerStorage().isVanishAutoFly(player)) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (plugin.getPlayerStorage().isVanishAutoGod(player)) {
            player.setInvulnerable(true);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            if (!canSee(other, player)) {
                other.hidePlayer(plugin, player);
            }
        }
        player.setMetadata("vanished", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are now vanished (Level " + getVanishLevel(player) + ")!");
    }

    public void unvanish(final Player player) {
        vanishedPlayers.remove(player.getUniqueId());
        noPickupPlayers.remove(player.getUniqueId());

        if (plugin.getPlayerStorage().isVanishFakeMessages(player)) {
            fakeJoin(player);
        }

        for (final Player other : Bukkit.getOnlinePlayers()) {
            other.showPlayer(plugin, player);
        }
        player.removeMetadata("vanished", plugin);
        if (player.getGameMode() != org.bukkit.GameMode.CREATIVE && player.getGameMode() != org.bukkit.GameMode.SPECTATOR) {
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setInvulnerable(false);
        }
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " You are no longer vanished!");
    }
    public boolean canSee(final Player viewer, final Player target) {
        if (!isVanished(target.getUniqueId())) return true;
        if (viewer.getUniqueId().equals(target.getUniqueId())) return true;
        if (!viewer.hasPermission("azox.utils.vanish.see")) return false;

        return getVanishLevel(viewer) >= getVanishLevel(target);
    }

    public int getVanishLevel(final Player player) {
        for (int i = 100; i > 0; i--) {
            if (player.hasPermission("azox.utils.vanish.level." + i)) {
                // If it's just the OP default permission, only return 3
                if (i > 3 && !player.isPermissionSet("azox.utils.vanish.level." + i) && player.isOp()) {
                    continue;
                }
                return i;
            }
        }
        return player.isOp() ? 3 : 1;
    }

    public void toggleItemPickup(final Player player) {
        final boolean currentlyDisabled = plugin.getPlayerStorage().isVanishPickupDisabled(player);
        plugin.getPlayerStorage().setVanishPickupDisabled(player, !currentlyDisabled);

        if (!currentlyDisabled) {
            noPickupPlayers.add(player.getUniqueId());
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Item pickup disabled.");
        } else {
            noPickupPlayers.remove(player.getUniqueId());
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Item pickup enabled.");
        }
    }

    public boolean canPickup(final UUID uuid) {
        return !noPickupPlayers.contains(uuid);
    }

    public void fakeJoin(final Player player) {
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " joined the game"));
    }

    public void fakeQuit(final Player player) {
        Bukkit.broadcast(MessageUtil.parse("<yellow>" + player.getName() + " left the game"));
    }

    public boolean isVanished(final UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

    public void handleJoin(final Player joiningPlayer) {
        for (final UUID uuid : vanishedPlayers) {
            final Player vanished = Bukkit.getPlayer(uuid);
            if (vanished != null && !joiningPlayer.hasPermission("azox.utils.vanish.see")) {
                joiningPlayer.hidePlayer(plugin, vanished);
            }
        }
    }
}
