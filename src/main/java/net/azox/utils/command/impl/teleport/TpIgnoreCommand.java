package net.azox.utils.command.impl.teleport;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpIgnoreCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final boolean current = plugin.getPlayerStorage().isTpIgnore(player);
        final boolean next = !current;
        
        plugin.getPlayerStorage().setTpIgnore(player, next);
        MessageUtil.sendMessage(player, "<green>Teleport requests are now " + (next ? "<red>ignored" : "<green>accepted") + "!");
    }
}
