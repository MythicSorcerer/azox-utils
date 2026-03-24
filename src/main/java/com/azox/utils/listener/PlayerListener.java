package com.azox.utils.listener;

import com.azox.utils.AzoxUtils;
import com.azox.utils.manager.GuiManager;
import com.azox.utils.util.MessageUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public final class PlayerListener implements Listener {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        plugin.getVanishManager().handleJoin(event.getPlayer());
        if (plugin.getVanishManager().isVanished(event.getPlayer().getUniqueId())) {
            event.joinMessage(null);
        }
        checkLobby(event.getPlayer());

        if (plugin.getPlayerStorage().isNightVisionEnabled(event.getPlayer())) {
            com.azox.utils.command.impl.util.NightVisionCommand.applyNightVision(event.getPlayer());
        }

        Location pendingLoc = plugin.getTeleportManager().getPendingTeleport(event.getPlayer().getUniqueId());
        if (pendingLoc != null) {
            event.getPlayer().teleport(pendingLoc);
            MessageUtil.sendMessage(event.getPlayer(), "<green>You have been teleported!");
        }
    }

    @EventHandler
    public void onWorldChange(final PlayerChangedWorldEvent event) {
        checkLobby(event.getPlayer());
    }

    private void checkLobby(Player player) {
        String world = player.getWorld().getName().toLowerCase();
        if (world.contains("hub") || world.contains("lobby")) {
            if (!player.getInventory().contains(Material.COMPASS)) {
                ItemStack compass = new ItemStack(Material.COMPASS);
                var meta = compass.getItemMeta();
                meta.displayName(MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));
                meta.getPersistentDataContainer().set(GuiManager.UTILITY_KEY, PersistentDataType.STRING, "world_selector_item");
                compass.setItemMeta(meta);
                player.getInventory().setItem(4, compass);
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        if (plugin.getVanishManager().isVanished(event.getPlayer().getUniqueId())) {
            event.quitMessage(null);
        }
        plugin.getPlayerDataManager().unload(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onRespawn(final PlayerRespawnEvent event) {
        if (plugin.getPlayerStorage().isNightVisionEnabled(event.getPlayer())) {
            com.azox.utils.command.impl.util.NightVisionCommand.applyNightVision(event.getPlayer());
        }
    }

    @EventHandler
    public void onPickup(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (plugin.getVanishManager().isVanished(player.getUniqueId()) && plugin.getPlayerStorage().isVanishPickupDisabled(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTarget(final EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player)) return;
        Player player = (Player) event.getTarget();
        
        if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        
        if (player.isInvulnerable() && plugin.getPlayerStorage().isGodMobsIgnore(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        if (plugin.getFreezeManager().isFrozen(player.getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(player, "<red>You are frozen and cannot move!");
            return;
        }

        final String jailName = plugin.getPlayerStorage().getJailName(player);
        if (jailName != null && plugin.getPlayerStorage().isJailInescapable(player)) {
            plugin.getJailManager().getJail(jailName).ifPresent(loc -> {
                if (!player.getLocation().getWorld().equals(loc.getWorld()) || player.getLocation().distanceSquared(loc) > 100) {
                    player.teleport(loc);
                    MessageUtil.sendMessage(player, "<red>You are in an inescapable jail!");
                }
            });
        }
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (plugin.getFreezeManager().isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            MessageUtil.sendMessage(event.getPlayer(), "<red>You are frozen and cannot interact!");
            return;
        }
        
        if (event.getItem() != null && event.getItem().getType() == Material.COMPASS) {
            String type = event.getItem().getItemMeta().getPersistentDataContainer().get(GuiManager.UTILITY_KEY, PersistentDataType.STRING);
            if ("world_selector_item".equals(type)) {
                plugin.getGuiManager().openWorldSelectorGui(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onChat(final AsyncChatEvent event) {
        final Player player = event.getPlayer();
        String prefix = "";
        if (player.hasPermission("azox.utils.rank.owner")) prefix = "<red>[Owner] ";
        else if (player.hasPermission("azox.utils.rank.admin")) prefix = "<red>[Admin] ";
        else if (player.hasPermission("azox.utils.rank.mod")) prefix = "<green>[Mod] ";
        else if (player.hasPermission("azox.utils.rank.vip")) prefix = "<gold>[VIP] ";

        if (!prefix.isEmpty()) {
            final String finalPrefix = prefix;
            event.renderer((source, sourceDisplayName, message, viewer) -> 
                MessageUtil.parse(finalPrefix).append(sourceDisplayName).append(MessageUtil.parse(": ")).append(message)
            );
        }
    }
}
