package net.azox.utils.command.impl.util;

import net.azox.utils.AzoxUtils;
import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.util.concurrent.TimeUnit;

public final class SystemCommands extends BaseCommand {

    private final String type;

    public SystemCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        switch (type.toLowerCase()) {
            case "tps":
                final double[] tps = Bukkit.getTPS();
                final long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
                final long maxMemory = Runtime.getRuntime().maxMemory() / 1024 / 1024;
                
                MessageUtil.sendMessage(sender, "<gold>Server Performance:");
                MessageUtil.sendMessage(sender, "<gray>TPS (1m, 5m, 15m): <green>" + formatTps(tps[0]) + ", " + formatTps(tps[1]) + ", " + formatTps(tps[2]));
                MessageUtil.sendMessage(sender, "<gray>Memory: <green>" + usedMemory + "MB / " + maxMemory + "MB");
                break;
            case "ping":
                Player targetPing = sender instanceof Player ? (Player) sender : null;
                if (args.length > 0) targetPing = Bukkit.getPlayer(args[0]);
                if (targetPing == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                MessageUtil.sendMessage(sender, "<gold>" + targetPing.getName() + "'s Ping: <green>" + targetPing.getPing() + "ms");
                break;
            case "uptime":
                final long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
                MessageUtil.sendMessage(sender, "<gold>Server Uptime: <green>" + formatTime(uptime));
                break;
            case "stats":
                Player targetStats = sender instanceof Player ? (Player) sender : null;
                if (args.length > 0) targetStats = Bukkit.getPlayer(args[0]);
                if (targetStats == null) {
                    MessageUtil.sendMessage(sender, "<red>Player not found!");
                    return;
                }
                MessageUtil.sendMessage(sender, "<gold>Stats for " + targetStats.getName() + ":");
                MessageUtil.sendMessage(sender, "<gray>Kills: <green>" + targetStats.getStatistic(Statistic.PLAYER_KILLS));
                MessageUtil.sendMessage(sender, "<gray>Deaths: <green>" + targetStats.getStatistic(Statistic.DEATHS));
                MessageUtil.sendMessage(sender, "<gray>Playtime: <green>" + formatTime(targetStats.getStatistic(Statistic.PLAY_ONE_MINUTE) * 50L));
                break;
            case "azoxreload":
                // In a real plugin, this would reload configs
                MessageUtil.sendMessage(sender, "<green>AzoxUtils configuration reloaded!");
                break;
        }
    }

    private String formatTps(double tps) {
        return (tps > 18.0 ? "<green>" : (tps > 15.0 ? "<yellow>" : "<red>")) + String.format("%.2f", Math.min(20.0, tps));
    }

    private String formatTime(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }
}
