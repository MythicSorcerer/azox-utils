package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ConfigCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        plugin.getGuiManager().openConfigGui((Player) sender);
    }
}
