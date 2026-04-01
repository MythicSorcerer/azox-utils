package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class AdminUtilCommands extends BaseCommand {

    private final String type;

    public AdminUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        switch (type.toLowerCase()) {
            case "sudo":
                if (args.length < 2) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /sudo <player> <command>");
                    return;
                }
                Player targetSudo = Bukkit.getPlayer(args[0]);
                if (targetSudo == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String cmd = sb.toString().trim();
                targetSudo.performCommand(cmd);
                MessageUtil.sendMessage(sender, "<green>Forced " + targetSudo.getName() + " to run: " + cmd);
                break;
            case "lightning":
                if (!isPlayer(sender) && args.length == 0) return;
                Player targetLight = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) sender;
                if (targetLight == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                targetLight.getWorld().strikeLightning(targetLight.getLocation());
                MessageUtil.sendMessage(sender, "<green>Struck " + targetLight.getName() + " with lightning!");
                break;
            case "burn":
                if (args.length == 0 && !isPlayer(sender)) return;
                Player targetBurn = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) sender;
                int seconds = 5;
                if (args.length > 1) {
                    try {
                        seconds = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                if (targetBurn == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                targetBurn.setFireTicks(seconds * 20);
                MessageUtil.sendMessage(sender, "<green>Set " + targetBurn.getName() + " on fire for " + seconds + " seconds!");
                break;
            case "extinguish":
                if (args.length == 0 && !isPlayer(sender)) return;
                Player targetExt = args.length > 0 ? Bukkit.getPlayer(args[0]) : (Player) sender;
                if (targetExt == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                targetExt.setFireTicks(0);
                MessageUtil.sendMessage(sender, "<green>Extinguished " + targetExt.getName() + "!");
                break;
            case "freeze":
                if (args.length == 0) {
                    MessageUtil.sendMessage(sender, "<red>Usage: /freeze <player>");
                    return;
                }
                Player targetFreeze = Bukkit.getPlayer(args[0]);
                if (targetFreeze == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                plugin.getFreezeManager().toggleFreeze(targetFreeze.getUniqueId());
                boolean frozen = plugin.getFreezeManager().isFrozen(targetFreeze.getUniqueId());
                MessageUtil.sendMessage(sender, "<green>Player " + targetFreeze.getName() + " is now " + (frozen ? "<red>frozen" : "<green>unfrozen") + "!");
                if (frozen) {
                    MessageUtil.sendMessage(targetFreeze, "<red>You have been frozen by staff!");
                } else {
                    MessageUtil.sendMessage(targetFreeze, "<green>You have been unfrozen!");
                }
                break;
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return getVisiblePlayerNames(sender, args[0]);
        }
        return new ArrayList<>();
    }
}
