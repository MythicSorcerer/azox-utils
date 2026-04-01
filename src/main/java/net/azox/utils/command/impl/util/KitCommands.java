package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.model.Kit;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class KitCommands extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (label.equalsIgnoreCase("createkit") || label.equalsIgnoreCase("ck")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /createkit <name> [cooldown]");
                return;
            }
            final String name = args[0];
            long cooldown = 0;
            if (args.length > 1) {
                try {
                    cooldown = Long.parseLong(args[1]);
                } catch (NumberFormatException ignored) {}
            }
            
            final ItemStack[] contents = player.getInventory().getContents();
            plugin.getKitManager().createKit(name, contents, cooldown);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Kit '" + name + "' created with " + cooldown + "s cooldown!");
            return;
        }

        if (label.equalsIgnoreCase("delkit") || label.equalsIgnoreCase("rmkit")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /delkit <name>");
                return;
            }
            if (plugin.getKitManager().getKit(args[0]).isEmpty()) {
                MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit not found!");
                return;
            }
            plugin.getKitManager().deleteKit(args[0]);
            MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Kit deleted!");
            return;
        }

        if (label.equalsIgnoreCase("kit")) {
            if (args.length == 0) {
                final String kits = String.join(", ", plugin.getKitManager().getKits().keySet());
                MessageUtil.sendMessage(player, "<gold>Kits: <yellow>" + (kits.isEmpty() ? "None" : kits));
                return;
            }
            plugin.getKitManager().getKit(args[0]).ifPresentOrElse(
                    kit -> plugin.getKitManager().giveKit(player, kit),
                    () -> MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit not found!")
            );
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return plugin.getKitManager().getKits().keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
