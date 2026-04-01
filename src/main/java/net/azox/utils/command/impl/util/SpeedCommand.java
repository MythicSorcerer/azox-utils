package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class SpeedCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /speed <1-10>");
            return;
        }

        try {
            float speed = Float.parseFloat(args[0]);
            if (speed < 1) speed = 1;
            if (speed > 10) speed = 10;
            
            float finalSpeed = speed / 10f;
            if (player.isFlying()) {
                player.setFlySpeed(finalSpeed);
                MessageUtil.sendMessage(player, "<green>Fly speed set to " + speed + "!");
            } else {
                player.setWalkSpeed(finalSpeed);
                MessageUtil.sendMessage(player, "<green>Walk speed set to " + speed + "!");
            }
        } catch (NumberFormatException e) {
            MessageUtil.sendMessage(player, "<red>Invalid number!");
        }
    }
}
