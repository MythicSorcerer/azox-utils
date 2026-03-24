package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class NightVisionCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        final boolean currentlyEnabled = plugin.getPlayerStorage().isNightVisionEnabled(player);
        final boolean next = !currentlyEnabled;
        plugin.getPlayerStorage().setNightVisionEnabled(player, next);

        if (next) {
            applyNightVision(player);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Night vision enabled!");
        } else {
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Night vision disabled!");
        }
    }

    public static void applyNightVision(final Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, true));
    }
}
