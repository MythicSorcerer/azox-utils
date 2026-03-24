package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SettingsCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            boolean current = plugin.getPlayerStorage().isGuiEnabled(player);
            plugin.getPlayerStorage().setGuiEnabled(player, !current);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " GUI menus are now " + (!current ? "<green>enabled" : "<red>disabled") + "!");
            return;
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("particles") || args[0].equalsIgnoreCase("particle"))) {
            boolean current = plugin.getPlayerStorage().areParticlesEnabled(player);
            plugin.getPlayerStorage().setParticlesEnabled(player, !current);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Particles are now " + (!current ? "<green>enabled" : "<red>disabled") + "!");
            return;
        }

        plugin.getGuiManager().openConfigGui(player);
    }
}
