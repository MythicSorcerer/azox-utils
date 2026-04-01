package net.azox.utils.listener;

import net.azox.utils.AzoxUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public final class TeleportListener implements Listener {

    private final AzoxUtils plugin = AzoxUtils.getInstance();

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        plugin.getTeleportManager().setLastLocation(event.getEntity(), event.getEntity().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND || 
            event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            plugin.getTeleportManager().setLastLocation(event.getPlayer(), event.getFrom());
        }
    }
}
