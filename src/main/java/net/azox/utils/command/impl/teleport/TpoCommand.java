package net.azox.utils.command.impl.teleport;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class TpoCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (label.equalsIgnoreCase("tpo") || label.equalsIgnoreCase("tpoffline")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /tpo <player>");
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            Location loc = target.getLocation();
            if (loc == null) {
                MessageUtil.sendMessage(player, "<red>Target location not found!");
                return;
            }
            player.teleport(loc);
            MessageUtil.sendMessage(player, "<green>Teleported to " + target.getName() + "!");
        } else if (label.equalsIgnoreCase("tpohere")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /tpohere <player>");
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.isOnline()) {
                plugin.getTeleportManager().addUndoLocation(target.getUniqueId(), target.getPlayer().getLocation());
                target.getPlayer().teleport(player.getLocation());
                MessageUtil.sendMessage(player, "<green>Teleported " + target.getName() + " to you.");
            } else {
                plugin.getTeleportManager().addPendingTeleport(target.getUniqueId(), player.getLocation());
                MessageUtil.sendMessage(player, "<green>Target offline. They will teleport to you on join.");
            }
        } else if (label.equalsIgnoreCase("tpoundo")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /tpoundo <player>");
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            Location oldLoc = plugin.getTeleportManager().getUndoLocation(target.getUniqueId());
            if (oldLoc != null && target.isOnline()) {
                target.getPlayer().teleport(oldLoc);
                MessageUtil.sendMessage(player, "<green>Teleported " + target.getName() + " back.");
            } else {
                MessageUtil.sendMessage(player, "<red>No undo location found or target offline!");
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
