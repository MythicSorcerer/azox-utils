package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class VanishCommand extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (args.length > 0) {
            final String sub = args[0].toLowerCase();
            switch (sub) {
                case "tipu":
                    plugin.getVanishManager().toggleItemPickup(player);
                    return;
                case "fakejoin":
                case "fj":
                    plugin.getVanishManager().fakeJoin(player);
                    return;
                case "fakeleave":
                case "fakequit":
                case "fl":
                case "fq":
                    plugin.getVanishManager().fakeQuit(player);
                    return;
                case "gui":
                    plugin.getGuiManager().openVanishGui(player);
                    return;
                case "status":
                    final boolean vanished = plugin.getVanishManager().isVanished(player.getUniqueId());
                    final int level = plugin.getVanishManager().getVanishLevel(player);
                    MessageUtil.sendMessage(player, "<gold>Vanish Status:");
                    MessageUtil.sendMessage(player, "<gray>Status: " + (vanished ? "<green>Vanished" : "<red>Visible"));
                    MessageUtil.sendMessage(player, "<gray>Vanish Level: <yellow>" + level);
                    MessageUtil.sendMessage(player, "<gray>Auto Fly: " + (plugin.getPlayerStorage().isVanishAutoFly(player) ? "<green>On" : "<red>Off"));
                    MessageUtil.sendMessage(player, "<gray>Auto God: " + (plugin.getPlayerStorage().isVanishAutoGod(player) ? "<green>On" : "<red>Off"));
                    MessageUtil.sendMessage(player, "<gray>Fake Msgs: " + (plugin.getPlayerStorage().isVanishFakeMessages(player) ? "<green>On" : "<red>Off"));
                    return;
            }
        }

        plugin.getVanishManager().toggleVanish(player);
    }
}
