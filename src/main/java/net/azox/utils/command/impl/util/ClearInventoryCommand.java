package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ClearInventoryCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;
        if (args.length > 0 && sender.hasPermission("azox.util.clearinventory.others")) {
            target = Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        target.getInventory().clear();
        MessageUtil.sendMessage(target, "<green>Inventory cleared!");
        if (!target.equals(sender)) MessageUtil.sendMessage(sender, "<green>Cleared " + target.getName() + "'s inventory!");
    }
}
