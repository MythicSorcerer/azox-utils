package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class SilenceCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        boolean currentState = plugin.getPlayerStorage().isSilenced(player);
        boolean newState = !currentState;

        plugin.getPlayerStorage().setSilenced(player, newState);

        if (newState) {
            MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Silence enabled. Command responses are now hidden.");
        } else {
            MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Silence disabled. Command responses are now visible.");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
