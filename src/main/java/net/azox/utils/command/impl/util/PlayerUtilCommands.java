package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class PlayerUtilCommands extends BaseCommand {

    private final String type;
    private final java.util.Map<java.util.UUID, Long> feedCooldowns = new java.util.HashMap<>();

    public PlayerUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(org.bukkit.command.CommandSender sender, String label, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;
        if (args.length > 0 && sender.hasPermission("azox.util." + type + ".others")) {
            target = org.bukkit.Bukkit.getPlayer(args[0]);
        }

        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        final Player finalTarget = target;
        switch (type.toLowerCase()) {
            case "feed":
                if (sender instanceof Player && sender.equals(finalTarget)) {
                    final long now = System.currentTimeMillis();
                    final long lastUsed = feedCooldowns.getOrDefault(finalTarget.getUniqueId(), 0L);
                    int cooldown = 300; // Default 5 mins
                    for (int i = 3600; i >= 0; i -= 60) {
                        if (finalTarget.hasPermission("azox.util.feed.cooldown." + i)) {
                            cooldown = i;
                            break;
                        }
                    }
                    if (now - lastUsed < cooldown * 1000L && !finalTarget.hasPermission("azox.util.feed.bypass")) {
                        long remaining = (cooldown * 1000L - (now - lastUsed)) / 1000;
                        MessageUtil.sendMessage(finalTarget, "<red>" + MessageUtil.ICON_ERROR + " You must wait " + remaining + "s to use feed again!");
                        return;
                    }
                    feedCooldowns.put(finalTarget.getUniqueId(), now);
                }
                finalTarget.setFoodLevel(20);
                finalTarget.setSaturation(20);
                MessageUtil.sendMessage(finalTarget, "<green>" + MessageUtil.ICON_SUCCESS + " You have been fed!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>" + MessageUtil.ICON_SUCCESS + " Fed " + finalTarget.getName() + "!");
                break;

            case "heal":
                finalTarget.setHealth(finalTarget.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
                finalTarget.setFoodLevel(20);
                finalTarget.setFireTicks(0);
                MessageUtil.sendMessage(finalTarget, "<green>You have been healed!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>Healed " + finalTarget.getName() + "!");
                break;
            case "fly":
                finalTarget.setAllowFlight(!finalTarget.getAllowFlight());
                MessageUtil.sendMessage(finalTarget, "<green>Flight " + (finalTarget.getAllowFlight() ? "enabled" : "disabled") + "!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>Flight " + (finalTarget.getAllowFlight() ? "enabled" : "disabled") + " for " + finalTarget.getName() + "!");
                break;
            case "god":
                finalTarget.setInvulnerable(!finalTarget.isInvulnerable());
                MessageUtil.sendMessage(finalTarget, "<green>God mode " + (finalTarget.isInvulnerable() ? "enabled" : "disabled") + "!");
                if (!finalTarget.equals(sender)) MessageUtil.sendMessage(sender, "<green>God mode " + (finalTarget.isInvulnerable() ? "enabled" : "disabled") + " for " + finalTarget.getName() + "!");
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
