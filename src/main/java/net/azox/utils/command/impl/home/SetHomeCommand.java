package net.azox.utils.command.impl.home;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public final class SetHomeCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }

        final Map<String, Home> homes = plugin.getHomeManager().getHomes(player);
        if (!homes.containsKey(homeName.toLowerCase())) {
            final int limit = plugin.getHomeManager().getHomeLimit(player);
            if (homes.size() >= limit) {
                MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " You have reached your home limit of " + limit + "!");
                return;
            }
        }

        plugin.getHomeManager().setHome(player, homeName, player.getLocation());
        final String displayName = homeName.equalsIgnoreCase("home") ? "" : " called " + homeName;
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Successfully set a new home" + displayName + "!");
    }
}
