package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class MiscUtilCommands extends BaseCommand {

    private final String type;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    public MiscUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        switch (type.toLowerCase()) {
            case "getpos":
                if (!isPlayer(sender) && args.length == 0) return;
                Player targetPos = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) sender;
                if (targetPos == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                Location loc = targetPos.getLocation();
                MessageUtil.sendMessage(sender, "<gold>" + targetPos.getName() + "'s Coordinates: <yellow>X: " + loc.getBlockX() + ", Y: " + loc.getBlockY() + ", Z: " + loc.getBlockZ() + " (" + loc.getWorld().getName() + ")");
                break;
            case "whois":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /whois <player>");
                    return;
                }
                OfflinePlayer who = Bukkit.getOfflinePlayer(args[0]);
                MessageUtil.sendMessage(sender, "<gold>Whois: <yellow>" + who.getName());
                MessageUtil.sendMessage(sender, "<gray>UUID: " + who.getUniqueId());
                MessageUtil.sendMessage(sender, "<gray>Online: " + (who.isOnline() ? "<green>Yes" : "<red>No"));
                if (who.getLastPlayed() > 0) MessageUtil.sendMessage(sender, "<gray>Last Seen: " + DATE_FORMAT.format(new Date(who.getLastPlayed())));
                break;
            case "broadcast":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /broadcast <message>");
                    return;
                }
                String msg = String.join(" ", args);
                Bukkit.broadcast(MessageUtil.parse("<red><bold>[Broadcast]</bold> <white>" + msg));
                break;
            case "suicide":
                if (!isPlayer(sender)) return;
                ((Player) sender).setHealth(0);
                MessageUtil.sendMessage(sender, "<red>You took your own life.");
                break;
            case "break":
                if (!isPlayer(sender)) return;
                Player p = (Player) sender;
                Block block = p.getTargetBlockExact(5);
                if (block == null || block.getType() == Material.AIR) {
                    MessageUtil.sendMessage(p, "<red>No block in range!");
                    return;
                }
                block.setType(Material.AIR);
                MessageUtil.sendMessage(p, "<green>Block broken!");
                break;
            case "compass":
                if (!isPlayer(sender)) return;
                Player pc = (Player) sender;
                float yaw = pc.getLocation().getYaw();
                String dir = "North";
                if (yaw < 0) yaw += 360;
                if (yaw >= 315 || yaw < 45) dir = "South";
                else if (yaw >= 45 && yaw < 135) dir = "West";
                else if (yaw >= 135 && yaw < 225) dir = "North";
                else if (yaw >= 225 && yaw < 315) dir = "East";
                MessageUtil.sendMessage(pc, "<gold>You are facing: <yellow>" + dir);
                break;
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (type.equalsIgnoreCase("getpos") || type.equalsIgnoreCase("whois")) {
                return getVisiblePlayerNames(sender, args[0]);
            }
        }
        return new ArrayList<>();
    }
}
