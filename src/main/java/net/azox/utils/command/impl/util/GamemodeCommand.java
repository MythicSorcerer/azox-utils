package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GamemodeCommand extends BaseCommand {

    private final GameMode mode;

    public GamemodeCommand(final GameMode mode) {
        this.mode = mode;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;
        GameMode targetMode = mode;

        if (mode == null) {
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /gm <s|c|a|sp> [player]");
                return;
            }
            targetMode = parseMode(args[0]);
            if (targetMode == null) {
                MessageUtil.sendMessage(sender, "<red>Invalid gamemode!");
                return;
            }
            if (args.length > 1 && sender.hasPermission("azox.util.gamemode.others")) {
                target = Bukkit.getPlayer(args[1]);
            }
        } else {
            if (args.length > 0 && sender.hasPermission("azox.util.gamemode.others")) {
                target = Bukkit.getPlayer(args[0]);
            }
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        if (target.getGameMode() == targetMode) {
            return;
        }

        target.setGameMode(targetMode);
        MessageUtil.sendMessage(target, "<green>Gamemode set to " + targetMode.name().toLowerCase() + "!");
        if (!target.equals(sender)) MessageUtil.sendMessage(sender, "<green>Set " + target.getName() + "'s gamemode to " + targetMode.name().toLowerCase() + "!");
    }

    private GameMode parseMode(final String input) {
        switch (input.toLowerCase()) {
            case "s": case "survival": case "0": return GameMode.SURVIVAL;
            case "c": case "creative": case "1": return GameMode.CREATIVE;
            case "a": case "adventure": case "2": return GameMode.ADVENTURE;
            case "sp": case "spectator": case "3": return GameMode.SPECTATOR;
            default: return null;
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (mode == null) { // /gm <mode> [player]
            if (args.length == 2) {
                return getVisiblePlayerNames(sender, args[1]);
            }
            if (args.length == 1) {
                return Arrays.asList("survival", "creative", "adventure", "spectator");
            }
        } else { // /gmc [player]
            if (args.length == 1) {
                return getVisiblePlayerNames(sender, args[0]);
            }
        }
        return new ArrayList<>();
    }
}
