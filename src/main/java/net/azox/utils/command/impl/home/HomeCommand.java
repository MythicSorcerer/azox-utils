package net.azox.utils.command.impl.home;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class HomeCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        String homeName = "home";
        if (args.length > 0) {
            homeName = args[0];
        }

        final Optional<Home> home = plugin.getHomeManager().getHome(player, homeName);
        if (home.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>Home '" + homeName + "' not found!");
            return;
        }

        plugin.getTeleportManager().teleportWithDelay(player, home.get().getLocation());
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            final Player player = (Player) sender;
            return plugin.getHomeManager().getHomes(player).keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
