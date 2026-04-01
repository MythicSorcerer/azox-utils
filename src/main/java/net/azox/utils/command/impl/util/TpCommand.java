package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class TpCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            MessageUtil.sendMessage(sender, "<red>Usage: /tp <player> | /tp <x> <y> <z> [world] | /tp <player1> <player2>");
            return;
        }

        if (args.length == 1) {
            if (!isPlayer(sender)) return;
            final Player player = (Player) sender;
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtil.sendMessage(player, "<red>Player not found!");
                return;
            }
            player.teleport(target);
            MessageUtil.sendMessage(player, "<green>Teleported to " + target.getName() + "!");
            return;
        }

        if (args.length == 2) {
            final Player p1 = Bukkit.getPlayer(args[0]);
            final Player p2 = Bukkit.getPlayer(args[1]);
            if (p1 == null || p2 == null) {
                MessageUtil.sendMessage(sender, "<red>One or both players not found!");
                return;
            }
            p1.teleport(p2);
            MessageUtil.sendMessage(sender, "<green>Teleported " + p1.getName() + " to " + p2.getName() + "!");
            return;
        }

        if (args.length >= 3) {
            if (!isPlayer(sender) && args.length == 3) return;
            Player target = sender instanceof Player ? (Player) sender : null;
            int offset = 0;
            
            // Check if first arg is player
            Player argTarget = Bukkit.getPlayer(args[0]);
            if (argTarget != null && args.length >= 4) {
                target = argTarget;
                offset = 1;
            }

            if (target == null) {
                MessageUtil.sendMessage(sender, "<red>Target player not found or not specified!");
                return;
            }

            try {
                double x = parseCoord(target.getLocation().getX(), args[offset]);
                double y = parseCoord(target.getLocation().getY(), args[offset + 1]);
                double z = parseCoord(target.getLocation().getZ(), args[offset + 2]);
                World world = target.getWorld();
                
                if (args.length > offset + 3) {
                    world = Bukkit.getWorld(args[offset + 3]);
                    if (world == null) {
                        MessageUtil.sendMessage(sender, "<red>World not found!");
                        return;
                    }
                }

                target.teleport(new Location(world, x, y, z, target.getLocation().getYaw(), target.getLocation().getPitch()));
                MessageUtil.sendMessage(sender, "<green>Teleported " + target.getName() + " to " + x + ", " + y + ", " + z + "!");
            } catch (NumberFormatException e) {
                MessageUtil.sendMessage(sender, "<red>Invalid coordinates!");
            }
        }
    }

    private double parseCoord(double current, String input) {
        if (input.startsWith("~")) {
            if (input.length() == 1) return current;
            return current + Double.parseDouble(input.substring(1));
        }
        return Double.parseDouble(input);
    }
}
