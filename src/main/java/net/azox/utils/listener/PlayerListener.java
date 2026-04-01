package net.azox.utils.listener;

import net.azox.utils.AzoxUtils;
import net.azox.utils.manager.GuiManager;
import net.azox.utils.manager.ILiveManager;
import net.azox.utils.util.MessageUtil;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class PlayerListener implements Listener {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        plugin.getVanishManager().handleJoin(player);
        if (plugin.getVanishManager().isVanished(player.getUniqueId())) {
            event.joinMessage(null);
        }
        checkLobby(player);

        if (plugin.getPlayerStorage().isNightVisionEnabled(player)) {
            net.azox.utils.command.impl.util.NightVisionCommand.applyNightVision(player);
        }

        applyPermEffects(player);
        AzoxUtils.getInstance().getILiveManager().loadPlayerData(player);

        if (!hasPermissionManager()) {
            grantDefaultPermissions(player);
        }

        // Check for jail release
        if (plugin.getPlayerStorage().shouldReleaseFromJail(player)) {
            plugin.getPlayerStorage().setUnjailed(player);
            MessageUtil.sendMessage(player, "<green>Your jail sentence has expired. You are now free!");
        }

        // Re-apply jail if still sentenced
        final String jailName = plugin.getPlayerStorage().getJailName(player);
        if (jailName != null && !plugin.getPlayerStorage().shouldReleaseFromJail(player)) {
            plugin.getJailManager().getJail(jailName).ifPresent(player::teleport);
            if (plugin.getPlayerStorage().isJailInescapable(player)) {
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 254, false, false));
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 254, false, false));
            }
        }

        final Location pendingLoc = plugin.getTeleportManager().getPendingTeleport(player.getUniqueId());
        if (pendingLoc != null) {
            player.teleport(pendingLoc);
            MessageUtil.sendMessage(player, "<green>You have been teleported!");
        }
    }

    private boolean hasPermissionManager() {
        return Bukkit.getPluginManager().isPluginEnabled("LuckPerms") ||
               Bukkit.getPluginManager().isPluginEnabled("PermissionsEx") ||
               Bukkit.getPluginManager().isPluginEnabled("GroupManager") ||
               Bukkit.getPluginManager().isPluginEnabled("UltraPermissions") ||
               Bukkit.getPluginManager().isPluginEnabled("zPermissions");
    }

    private void grantDefaultPermissions(final Player player) {
        if (!player.hasPermission("azox.util.granted")) {
            final org.bukkit.permissions.PermissionAttachment attachment = player.addAttachment(plugin);
            attachment.setPermission("azox.util.*", true);
            attachment.setPermission("azox.util.granted", true);
        }
    }

    private void applyPermEffects(Player player) {
        if (plugin.getPlayerStorage().hasPermEffects(player)) {
            FileConfiguration config = plugin.getPlayerDataManager().getConfig(player);
            if (config != null) {
                ConfigurationSection section = config.getConfigurationSection("permeffects");
                if (section != null) {
                    for (String effectName : section.getKeys(false)) {
                        PotionEffectType effectType = PotionEffectType.getByName(effectName);
                        if (effectType != null) {
                            int level = section.getInt(effectName);
                            player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, level, false, false));
                        }
                    }
                }
            }
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
            net.azox.utils.command.impl.util.NightVisionCommand.applyNightVision(event.getPlayer());
        }
        applyPermEffects(event.getPlayer());
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
        if (jailName != null && !plugin.getPlayerStorage().shouldReleaseFromJail(player)) {
            if (plugin.getPlayerStorage().isJailInescapable(player)) {
                plugin.getJailManager().getJail(jailName).ifPresent(loc -> {
                    if (!player.getLocation().getWorld().equals(loc.getWorld()) || player.getLocation().distanceSquared(loc) > 100) {
                        player.teleport(loc);
                        MessageUtil.sendMessage(player, "<red>You are in an inescapable jail!");
                    }
                });
            }
        } else if (jailName != null && plugin.getPlayerStorage().shouldReleaseFromJail(player)) {
            // Player has escaped after time expired
            plugin.getPlayerStorage().setUnjailed(player);
            MessageUtil.sendMessage(player, "<green>Your jail sentence has expired. You are now free!");
            for (final Player admin : Bukkit.getOnlinePlayers()) {
                if (admin.hasPermission("azox.util.admin.*") || admin.isOp()) {
                    MessageUtil.sendMessage(admin, "<yellow>" + player.getName() + " was released from jail (sentence expired).");
                }
            }
            plugin.getLogger().info("Player " + player.getName() + " was released from jail (sentence expired).");
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
        if (player.hasPermission("azox.util.rank.owner")) prefix = "<red>[Owner] ";
        else if (player.hasPermission("azox.util.rank.admin")) prefix = "<red>[Admin] ";
        else if (player.hasPermission("azox.util.rank.mod")) prefix = "<green>[Mod] ";
        else if (player.hasPermission("azox.util.rank.vip")) prefix = "<gold>[VIP] ";

        if (!prefix.isEmpty()) {
            final String finalPrefix = prefix;
            event.renderer((source, sourceDisplayName, message, viewer) -> 
                MessageUtil.parse(finalPrefix).append(sourceDisplayName).append(MessageUtil.parse(": ")).append(message)
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        ILiveManager iliveManager = AzoxUtils.getInstance().getILiveManager();
        if (iliveManager.isEnabled(player)) {
            double multiplier = iliveManager.getDamageMultiplier(player);
            if (multiplier < 1.0) {
                event.setDamage(event.getFinalDamage() * multiplier);
            }

            if (iliveManager.isNoTotemMode(player)) {
                double healthAfterDamage = player.getHealth() - event.getFinalDamage();
                if (healthAfterDamage <= 0.5 && event.getFinalDamage() > 0) {
                    event.setCancelled(true);
                    player.setHealth(0.5);
                }
            }
        }
    }
}
