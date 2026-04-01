package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class CondenseCommand extends BaseCommand {

    private static final Map<Material, Material> CONDENSE_MAP = new HashMap<>();

    static {
        CONDENSE_MAP.put(Material.IRON_INGOT, Material.IRON_BLOCK);
        CONDENSE_MAP.put(Material.GOLD_INGOT, Material.GOLD_BLOCK);
        CONDENSE_MAP.put(Material.DIAMOND, Material.DIAMOND_BLOCK);
        CONDENSE_MAP.put(Material.EMERALD, Material.EMERALD_BLOCK);
        CONDENSE_MAP.put(Material.COAL, Material.COAL_BLOCK);
        CONDENSE_MAP.put(Material.REDSTONE, Material.REDSTONE_BLOCK);
        CONDENSE_MAP.put(Material.LAPIS_LAZULI, Material.LAPIS_BLOCK);
        CONDENSE_MAP.put(Material.RAW_IRON, Material.RAW_IRON_BLOCK);
        CONDENSE_MAP.put(Material.RAW_GOLD, Material.RAW_GOLD_BLOCK);
        CONDENSE_MAP.put(Material.RAW_COPPER, Material.RAW_COPPER_BLOCK);
        CONDENSE_MAP.put(Material.COPPER_INGOT, Material.COPPER_BLOCK);
        CONDENSE_MAP.put(Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        boolean all = args.length > 0 && args[0].equalsIgnoreCase("all");

        if (all) {
            int totalCondensed = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) continue;
                totalCondensed += condenseItem(player, item);
            }
            MessageUtil.sendMessage(player, "<green>Condensed all possible items!");
        } else {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR) {
                MessageUtil.sendMessage(player, "<red>You are not holding anything!");
                return;
            }
            if (condenseItem(player, item) > 0) {
                MessageUtil.sendMessage(player, "<green>Items condensed!");
            } else {
                MessageUtil.sendMessage(player, "<red>This item cannot be condensed or you don't have enough!");
            }
        }
    }

    private int condenseItem(Player player, ItemStack item) {
        Material target = CONDENSE_MAP.get(item.getType());
        if (target == null) return 0;

        int amount = item.getAmount();
        int blocks = amount / 9;
        if (blocks <= 0) return 0;

        int remainder = amount % 9;
        item.setAmount(remainder);
        
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(new ItemStack(target, blocks));
        for (ItemStack lo : leftover.values()) {
            player.getWorld().dropItem(player.getLocation(), lo);
        }
        
        return blocks;
    }
}
