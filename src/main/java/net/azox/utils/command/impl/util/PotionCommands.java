package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class PotionCommands extends BaseCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        if (label.equalsIgnoreCase("fillpotsave")) {
            savePotions(player);
        } else if (label.equalsIgnoreCase("fillpot")) {
            if (plugin.getPlayerStorage().hasSavedPotions(player)) {
                plugin.getPlayerStorage().setFillPotEnabled(player, true);
                MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Fillpot enabled! Your potions will be replenished.");
            } else {
                MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " No saved potions found. Use /fillpotsave first.");
            }
        } else if (label.equalsIgnoreCase("unfillpot")) {
            plugin.getPlayerStorage().setFillPotEnabled(player, false);
            MessageUtil.sendMessage(player, "<yellow>" + MessageUtil.ICON_INFO + " Fillpot disabled.");
        }
    }

    private void savePotions(Player player) {
        List<String> savedPotions = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && isPotion(item.getType())) {
                String serialized = serializeItemStack(item);
                savedPotions.add(i + ":" + serialized);
                count++;
            }
        }

        if (count == 0) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " No potions found in your inventory.");
            return;
        }

        plugin.getPlayerStorage().savePotions(player, savedPotions);
        plugin.getPlayerStorage().setFillPotEnabled(player, true);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Saved " + count + " potion slot(s). Fillpot is now enabled!");
    }

    private boolean isPotion(Material material) {
        return material == Material.POTION ||
               material == Material.SPLASH_POTION ||
               material == Material.LINGERING_POTION;
    }

    private String serializeItemStack(ItemStack item) {
        StringBuilder sb = new StringBuilder();
        sb.append(item.getType().name());
        sb.append(",");
        sb.append(item.getAmount());
        sb.append(",");
        sb.append(item.getDurability());
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            sb.append(",name:").append(item.getItemMeta().getDisplayName().replace(",", ";"));
        }
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            sb.append(",lore:");
            List<String> lore = item.getItemMeta().getLore();
            for (int i = 0; i < lore.size(); i++) {
                if (i > 0) sb.append("|");
                sb.append(lore.get(i).replace(",", ";"));
            }
        }
        return sb.toString();
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
