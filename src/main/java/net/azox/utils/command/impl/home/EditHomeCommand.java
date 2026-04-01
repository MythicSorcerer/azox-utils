package net.azox.utils.command.impl.home;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class EditHomeCommand extends BaseCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length == 0) {
            MessageUtil.sendMessage(player, "<red>Usage: /edithome <homename>");
            return;
        }

        final String homeName = args[0];
        final Optional<Home> homeOpt = plugin.getHomeManager().getHome(player, homeName);

        if (homeOpt.isEmpty()) {
            MessageUtil.sendMessage(player, "<red>Home '" + homeName + "' not found!");
            return;
        }

        final Home home = homeOpt.get();

        // Subcommands for editing
        if (args.length > 1) {
            final String subCommand = args[1].toLowerCase();
            switch (subCommand) {
                case "delete":
                    player.performCommand("delhome " + homeName);
                    return;
                case "relocate":
                    plugin.getHomeManager().setHome(player, homeName, player.getLocation());
                    MessageUtil.sendMessage(player, "<green>Home relocated!");
                    break;
                case "public":
                    home.setPublic(!home.isPublic());
                    plugin.getPlayerStorage().saveHome(player, home);
                    MessageUtil.sendMessage(player, "<green>Home visibility set to " + (home.isPublic() ? "Public" : "Private") + "!");
                    break;
                case "description":
                    if (args.length < 3) {
                        MessageUtil.sendMessage(player, "<red>Usage: /edithome <name> description <text>");
                        return;
                    }
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 2; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    home.setDescription(sb.toString().trim());
                    plugin.getPlayerStorage().saveHome(player, home);
                    MessageUtil.sendMessage(player, "<green>Home description updated!");
                    break;
                case "rename":
                    if (args.length < 3) {
                        MessageUtil.sendMessage(player, "<red>Usage: /edithome <name> rename <newname>");
                        return;
                    }
                    final String newName = args[2];
                    plugin.getHomeManager().deleteHome(player, homeName);
                    plugin.getHomeManager().setHome(player, newName, home.getLocation());
                    final Home newHome = plugin.getHomeManager().getHome(player, newName).get();
                    newHome.setDescription(home.getDescription());
                    newHome.setPublic(home.isPublic());
                    newHome.setCreationDate(home.getCreationDate());
                    plugin.getPlayerStorage().saveHome(player, newHome);
                    MessageUtil.sendMessage(player, "<green>Home renamed to " + newName + "!");
                    return;
            }
        }

        // Display menu
        MessageUtil.sendMessage(player, "<gold>" + MessageUtil.ICON_HOME + " Details for your home" + (home.getName().equalsIgnoreCase("home") ? "" : ", " + home.getName()));
        if (!home.getDescription().isEmpty()) {
            MessageUtil.sendMessage(player, "<gray>" + MessageUtil.ICON_INFO + " " + home.getDescription());
        }
        MessageUtil.sendMessage(player, "<gray>" + MessageUtil.ICON_INFO + " " + DATE_FORMAT.format(new Date(home.getCreationDate())) + " " + (home.isPublic() ? "Public Home" : "Private Home"));
        MessageUtil.sendMessage(player, "<gray>" + MessageUtil.ICON_ARROW + " " + home.getWorldName());
        MessageUtil.sendMessage(player, "<gray>" + MessageUtil.ICON_ARROW + " X: " + (int)home.getX() + ", Y: " + (int)home.getY() + ", Z: " + (int)home.getZ());

        final Component useRow = Component.text("Use: ", NamedTextColor.GOLD)
                .append(Component.text(MessageUtil.ICON_TP + " [Teleport] ", NamedTextColor.GREEN).clickEvent(ClickEvent.runCommand("/home " + home.getName())));
        
        final Component manageRow = Component.text("Manage: ", NamedTextColor.GOLD)
                .append(Component.text(MessageUtil.ICON_ERROR + " [Delete] ", NamedTextColor.RED).clickEvent(ClickEvent.runCommand("/edithome " + home.getName() + " delete")))
                .append(Component.text(MessageUtil.ICON_WARNING + " [Relocate] ", NamedTextColor.YELLOW).clickEvent(ClickEvent.runCommand("/edithome " + home.getName() + " relocate")))
                .append(Component.text(MessageUtil.ICON_STAR + " [Make " + (home.isPublic() ? "Private" : "Public") + "] ", NamedTextColor.AQUA).clickEvent(ClickEvent.runCommand("/edithome " + home.getName() + " public")));

        final Component editRow = Component.text("Edit: ", NamedTextColor.GOLD)
                .append(Component.text(MessageUtil.ICON_UTILITY + " [Rename] ", NamedTextColor.YELLOW).clickEvent(ClickEvent.suggestCommand("/renamehome " + home.getName() + " ")))
                .append(Component.text(MessageUtil.ICON_INFO + " [Description]", NamedTextColor.YELLOW).clickEvent(ClickEvent.suggestCommand("/edithome " + home.getName() + " description ")));

        player.sendMessage(useRow);
        player.sendMessage(manageRow);
        player.sendMessage(editRow);
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
