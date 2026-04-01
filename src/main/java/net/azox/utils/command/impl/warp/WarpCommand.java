package net.azox.utils.command.impl.warp;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Warp;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class WarpCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            final List<String> warps = plugin.getWarpManager().getWarps().values().stream()
                    .filter(warp -> player.hasPermission("azox.util.warp." + warp.getLevel()))
                    .map(Warp::getName)
                    .collect(Collectors.toList());

            if (warps.isEmpty()) {
                MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " No warps available!");
                return;
            }

            MessageUtil.sendMessage(player, "<gold>" + MessageUtil.ICON_WARP + " Available Warps: <yellow>" + String.join(", ", warps));
            return;
        }

        final String name = args[0];
        final Optional<Warp> warpOpt = plugin.getWarpManager().getWarp(name);

        if (warpOpt.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Warp '" + name + "' not found!");
            return;
        }

        final Warp warp = warpOpt.get();
        if (!player.hasPermission("azox.util.warp." + warp.getLevel())) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " You do not have permission to use this warp!");
            return;
        }

        plugin.getTeleportManager().teleportWithDelay(player, warp.getLocation());
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            final Player player = (Player) sender;
            return plugin.getWarpManager().getWarps().values().stream()
                    .filter(warp -> player.hasPermission("azox.util.warp." + warp.getLevel()))
                    .map(Warp::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
