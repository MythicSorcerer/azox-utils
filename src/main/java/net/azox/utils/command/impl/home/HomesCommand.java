package net.azox.utils.command.impl.home;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class HomesCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (plugin.getPlayerStorage().isGuiEnabled(player) && args.length == 0) {
            plugin.getGuiManager().openHomesGui(player);
            return;
        }

        OfflinePlayer target = player;
        String targetName = player.getName();
        int page = 1;

        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                if (player.hasPermission("azox.util.home.others")) {
                    target = Bukkit.getOfflinePlayer(args[0]);
                    targetName = target.getName();
                    if (args.length > 1) {
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }

        final Map<String, Home> homesMap = plugin.getPlayerStorage().getHomes(target);
        final List<Home> homes = new ArrayList<>(homesMap.values());

        if (homes.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>" + targetName + " has no homes!");
            return;
        }

        final int pageSize = 10;
        final int totalPages = (int) Math.ceil((double) homes.size() / pageSize);
        if (page > totalPages) page = totalPages;
        if (page < 1) page = 1;

        MessageUtil.sendMessage(player, "<gold>" + MessageUtil.ICON_HOME + " " + targetName + "'s Homes:");
        MessageUtil.sendMessage(player, "<gray>Page " + page + " of " + totalPages);

        final int start = (page - 1) * pageSize;
        final int end = Math.min(start + pageSize, homes.size());

        for (int i = start; i < end; i++) {
            final Home home = homes.get(i);
            final Component homeComp = Component.text(MessageUtil.ICON_HOME + " [" + home.getName() + "] ", NamedTextColor.YELLOW)
                    .hoverEvent(HoverEvent.showText(Component.text(MessageUtil.ICON_INFO + " Description: " + (home.getDescription().isEmpty() ? "None" : home.getDescription()) + "\n" +
                            MessageUtil.ICON_ARROW + " Type: " + (home.isPublic() ? "Public" : "Private"), NamedTextColor.GRAY)))
                    .clickEvent(ClickEvent.runCommand("/edithome " + home.getName()));
            player.sendMessage(homeComp);
        }

        final Component pagination = Component.empty();
        if (page > 1) {
            pagination.append(Component.text(MessageUtil.ICON_PREV + " [Previous] ", NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/homes " + (page - 1))));
        }
        if (page < totalPages) {
            pagination.append(Component.text("[Next] " + MessageUtil.ICON_NEXT, NamedTextColor.AQUA)
                    .clickEvent(ClickEvent.runCommand("/homes " + (page + 1))));
        }
        if (!pagination.equals(Component.empty())) {
            player.sendMessage(pagination);
        }
    }
}
