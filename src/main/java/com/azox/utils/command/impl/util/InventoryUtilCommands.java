package com.azox.utils.command.impl.util;

import com.azox.utils.command.BaseCommand;
import com.azox.utils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class InventoryUtilCommands extends BaseCommand {

    private final String type;

    public InventoryUtilCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        switch (type.toLowerCase()) {
            case "enderchest":
                int maxPages = 1;
                for (int i = 5; i > 1; i--) {
                    if (player.hasPermission("azox.utils.enderchest.pages." + i)) {
                        maxPages = i;
                        break;
                    }
                }
                if (maxPages > 1) {
                    plugin.getGuiManager().openEnderChestPageSelector(player, maxPages);
                } else {
                    player.openInventory(player.getEnderChest());
                }
                break;
            case "anvil":
                player.openAnvil(null, true);
                break;
            case "cartographytable":
                player.openCartographyTable(null, true);
                break;
            case "craft":
                player.openWorkbench(null, true);
                break;
            case "grindstone":
                player.openGrindstone(null, true);
                break;
            case "stonecutter":
                player.openStonecutter(null, true);
                break;
            case "loom":
                player.openLoom(null, true);
                break;
            case "trash":
                final Inventory trash = Bukkit.createInventory(null, 54, MessageUtil.parse("<red>Trash Bin"));
                player.openInventory(trash);
                break;
        }
    }
}
