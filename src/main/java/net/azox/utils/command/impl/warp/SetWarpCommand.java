package net.azox.utils.command.impl.warp;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SetWarpCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /setwarp <name> [level 1-10]");
            return;
        }

        final String name = args[0];
        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
                if (level < 1) level = 1;
                if (level > 10) level = 10;
            } catch (NumberFormatException ignored) {}
        }

        plugin.getWarpManager().setWarp(name, player.getLocation(), level);
        MessageUtil.sendMessage(player, "<green>Successfully set warp '" + name + "' with level " + level + "!");
    }
}
