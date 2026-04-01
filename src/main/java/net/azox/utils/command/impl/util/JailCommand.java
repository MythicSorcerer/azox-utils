package net.azox.utils.command.impl.util;

import net.azox.utils.AzoxUtils;
import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class JailCommand extends BaseCommand {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?");

    @Override
    public void execute(final CommandSender sender, final String label, final String[] args) {
        if (label.equalsIgnoreCase("setjail")) {
            if (!isPlayer(sender)) {
                return;
            }
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /setjail <name>");
                return;
            }
            plugin.getJailManager().setJail(args[0], ((Player) sender).getLocation());
            MessageUtil.sendMessage(sender, "<green>Jail '" + args[0] + "' set!");
            return;
        }

        if (label.equalsIgnoreCase("deljail")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /deljail <name>");
                return;
            }
            plugin.getJailManager().deleteJail(args[0]);
            MessageUtil.sendMessage(sender, "<green>Jail '" + args[0] + "' deleted!");
            return;
        }

        if (label.equalsIgnoreCase("unjail")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(sender, "<red>Usage: /unjail <player>");
                return;
            }
            final Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageUtil.sendMessage(sender, "<red>Player not found!");
                return;
            }
            plugin.getPlayerStorage().setUnjailed(target);
            MessageUtil.sendMessage(sender, "<green>Player " + target.getName() + " unjailed!");
            return;
        }

        if (args.length < 2) {
            MessageUtil.sendMessage(sender, "<red>Usage: /jail <player> <jailname> [escapable|not] [dramatic] [time]");
            return;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageUtil.sendMessage(sender, "<red>Player not found!");
            return;
        }

        final Optional<Location> jailLoc = plugin.getJailManager().getJail(args[1]);
        if (jailLoc.isEmpty()) {
            MessageUtil.sendMessage(sender, "<red>Jail not found!");
            return;
        }

        final boolean inescapable = args.length > 2 && args[2].equalsIgnoreCase("not");
        final boolean dramatic = args.length > 3 && args[3].equalsIgnoreCase("dramatic");
        final Long durationMillis = args.length > 4 ? parseDuration(args[4]) : null;

        if (dramatic) {
            applyDramaticJail(target, jailLoc.get(), inescapable, durationMillis, sender);
        } else {
            jailPlayer(target, args[1], inescapable, durationMillis, sender);
        }
    }

    private void jailPlayer(final Player target, final String jailName, final boolean inescapable, final Long durationMillis, final CommandSender sender) {
        final Optional<Location> jailLoc = plugin.getJailManager().getJail(jailName);
        if (jailLoc.isEmpty()) {
            return;
        }

        target.teleport(jailLoc.get());
        plugin.getPlayerStorage().setJailed(target, jailName, inescapable, durationMillis);

        if (inescapable) {
            applyInescapableJailEffects(target);
        }

        final String timeMessage = durationMillis != null ? formatDuration(durationMillis) : "indefinitely";
        MessageUtil.sendMessage(target, "<red>You have been sentenced to solitary confinement!");
        MessageUtil.sendMessage(target, "<gray>You will be released " + timeMessage + ", unless you escape before then.");
        MessageUtil.sendMessage(sender, "<green>Player " + target.getName() + " jailed in " + jailName + " (" + (inescapable ? "inescapable" : "escapable") + ") for " + timeMessage);

        Bukkit.broadcast(MessageUtil.parse("<yellow>" + target.getName() + " has been sentenced to solitary confinement!"));
    }

    private void applyInescapableJailEffects(final Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 254, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, Integer.MAX_VALUE, 254, false, false));
    }

    private void applyDramaticJail(final Player target, final Location jailLoc, final boolean inescapable, final Long durationMillis, final CommandSender sender) {
        boolean canLevitate = true;
        for (int i = 1; i <= 15; i++) {
            if (target.getLocation().add(0, i, 0).getBlock().getType() != Material.AIR) {
                canLevitate = false;
                break;
            }
        }

        if (canLevitate) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 40, 2));
        }

        target.setGlowing(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.teleport(jailLoc);
                target.setGlowing(false);
                target.removePotionEffect(PotionEffectType.LEVITATION);

                plugin.getPlayerStorage().setJailed(target, "dramatic_jail", inescapable, durationMillis);

                if (inescapable) {
                    applyInescapableJailEffects(target);
                }

                final String timeMessage = durationMillis != null ? formatDuration(durationMillis) : "indefinitely";
                MessageUtil.sendMessage(target, "<red><bold>You have been sentenced to solitary confinement!");
                MessageUtil.sendMessage(target, "<gray>You will be released " + timeMessage + ", unless you escape before then.");
                MessageUtil.sendMessage(sender, "<green>Player " + target.getName() + " dramatically jailed for " + timeMessage);

                Bukkit.broadcast(MessageUtil.parse("<yellow>" + target.getName() + " has been sentenced to solitary confinement!"));
            }
        }.runTaskLater(plugin, 40L);
    }

    private Long parseDuration(final String duration) {
        final Matcher matcher = TIME_PATTERN.matcher(duration.toLowerCase());
        if (!matcher.matches()) {
            return null;
        }

        long days = matcher.group(1) != null ? Long.parseLong(matcher.group(1)) : 0;
        long hours = matcher.group(2) != null ? Long.parseLong(matcher.group(2)) : 0;
        long minutes = matcher.group(3) != null ? Long.parseLong(matcher.group(3)) : 0;
        long seconds = matcher.group(4) != null ? Long.parseLong(matcher.group(4)) : 0;

        return ((days * 24 + hours) * 60 + minutes) * 60000 + seconds * 1000;
    }

    private String formatDuration(final long millis) {
        final long seconds = millis / 1000;
        final long minutes = seconds / 60;
        final long hours = minutes / 60;
        final long days = hours / 24;

        final long remainingHours = hours % 24;
        final long remainingMinutes = minutes % 60;
        final long remainingSeconds = seconds % 60;

        final StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("d");
        }
        if (remainingHours > 0) {
            sb.append(remainingHours).append("h");
        }
        if (remainingMinutes > 0) {
            sb.append(remainingMinutes).append("m");
        }
        if (remainingSeconds > 0 || sb.length() == 0) {
            sb.append(remainingSeconds).append("s");
        }

        final double totalDays = millis / 86400000.0;
        if (days > 0) {
            return String.format("%.1f days", totalDays);
        } else if (hours > 0) {
            return String.format("%.1f hours", millis / 3600000.0);
        } else if (minutes > 0) {
            return String.format("%.1f minutes", millis / 60000.0);
        } else {
            return seconds + " seconds";
        }
    }

    @Override
    public List<String> complete(final CommandSender sender, final String[] args) {
        if (args.length == 1) {
            return getVisiblePlayerNames(sender, args[0]);
        }
        if (args.length == 2) {
            return plugin.getJailManager().getJails().keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 3) {
            return java.util.Arrays.asList("escapable", "not");
        }
        if (args.length == 4) {
            return java.util.Arrays.asList("dramatic");
        }
        return new ArrayList<>();
    }
}
