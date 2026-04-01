package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class NavigationCommands extends BaseCommand {

    private final String type;

    public NavigationCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        switch (type.toLowerCase()) {
            case "top":
                final Location topLoc = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().add(0.5, 1, 0.5);
                player.teleport(topLoc);
                MessageUtil.sendMessage(player, "<green>Teleported to top!");
                break;
            case "jumpto":
                final Block targetBlock = player.getTargetBlockExact(100);
                if (targetBlock == null) {
                    MessageUtil.sendMessage(player, "<red>No block in range!");
                    return;
                }
                player.teleport(targetBlock.getLocation().add(0.5, 1, 0.5).setDirection(player.getLocation().getDirection()));
                MessageUtil.sendMessage(player, "<green>Jumped to block!");
                break;
            case "near":
                int radius = 200;
                if (args.length > 0) {
                    try {
                        radius = Integer.parseInt(args[0]);
                    } catch (NumberFormatException ignored) {}
                }
                final int finalRadius = radius;
                final List<String> nearby = player.getWorld().getNearbyEntities(player.getLocation(), radius, radius, radius).stream()
                        .filter(e -> e instanceof Player && !e.equals(player))
                        .map(e -> e.getName() + " (" + (int) e.getLocation().distance(player.getLocation()) + "m)")
                        .collect(Collectors.toList());

                if (nearby.isEmpty()) {
                    MessageUtil.sendMessage(player, "<red>No players nearby in " + finalRadius + "m radius.");
                } else {
                    MessageUtil.sendMessage(player, "<gold>Nearby players: <yellow>" + String.join(", ", nearby));
                }
                break;
            case "world":
                if (args.length == 0) {
                    MessageUtil.sendMessage(player, "<red>Usage: /world <worldname>");
                    return;
                }
                final World world = Bukkit.getWorld(args[0]);
                if (world == null) {
                    MessageUtil.sendMessage(player, "<red>World not found!");
                    return;
                }
                player.teleport(world.getSpawnLocation());
                MessageUtil.sendMessage(player, "<green>Teleported to world " + world.getName() + "!");
                break;
        }
    }
}
