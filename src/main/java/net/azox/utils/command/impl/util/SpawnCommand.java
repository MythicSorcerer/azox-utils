package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SpawnCommand extends BaseCommand {

    private final boolean set;

    public SpawnCommand(final boolean set) {
        this.set = set;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (set) {
            player.getWorld().setSpawnLocation(player.getLocation());
            MessageUtil.sendMessage(player, "<green>Spawn location set!");
        } else {
            plugin.getTeleportManager().teleportWithDelay(player, player.getWorld().getSpawnLocation());
        }
    }
}
