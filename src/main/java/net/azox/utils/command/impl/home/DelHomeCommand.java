package net.azox.utils.command.impl.home;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DelHomeCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /delhome <name|all>");
            return;
        }

        final String name = args[0].toLowerCase();

        if (name.equals("all")) {
            if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                plugin.getHomeManager().deleteAllHomes(player);
                MessageUtil.sendMessage(player, "<green>Successfully deleted all homes!");
            } else {
                final Component message = Component.text("Are you sure you want to delete all homes? ", NamedTextColor.RED)
                        .append(Component.text("[YES]", NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/delhome all confirm")))
                        .append(Component.text(" / ", NamedTextColor.GRAY))
                        .append(Component.text("[NO]", NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("/homes")));
                player.sendMessage(message);
            }
            return;
        }

        final Optional<Home> home = plugin.getHomeManager().getHome(player, name);
        if (home.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>Home '" + name + "' not found!");
            return;
        }

        plugin.getHomeManager().deleteHome(player, name);
        MessageUtil.sendMessage(player, "<green>Successfully deleted home '" + name + "'!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (sender instanceof Player && args.length == 1) {
            final Player player = (Player) sender;
            final List<String> homes = new ArrayList<>(plugin.getHomeManager().getHomes(player).keySet());
            homes.add("all");
            return homes.stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
