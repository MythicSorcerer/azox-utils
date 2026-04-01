package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class LobbyCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        World target = Bukkit.getWorld("lobby");
        if (target == null) target = Bukkit.getWorld("hub");
        
        if (target == null) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Lobby/Hub world not found!");
            return;
        }

        plugin.getTeleportManager().teleportWithDelay(player, target.getSpawnLocation());
    }
}
