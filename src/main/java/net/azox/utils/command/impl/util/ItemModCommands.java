package net.azox.utils.command.impl.util;

import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemModCommands extends BaseCommand {

    private final String type;

    public ItemModCommands(final String type) {
        this.type = type;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;
        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            MessageUtil.sendMessage(player, "<red>You must be holding an item!");
            return;
        }

        switch (type.toLowerCase()) {
            case "itemname":
                if (args.length == 0) {
                    MessageUtil.sendMessage(player, "<red>Usage: /itemname <name>");
                    return;
                }
                final String nameText = String.join(" ", args);
                final ItemMeta nameMeta = item.getItemMeta();
                nameMeta.displayName(MessageUtil.parse(nameText));
                item.setItemMeta(nameMeta);
                MessageUtil.sendMessage(player, "<green>Item name updated!");
                break;
            case "copyitem":
                player.getInventory().addItem(item.clone());
                MessageUtil.sendMessage(player, "<green>Item duplicated!");
                break;
            case "repair":
                final ItemMeta repairMeta = item.getItemMeta();
                if (repairMeta instanceof Damageable) {
                    ((Damageable) repairMeta).setDamage(0);
                    item.setItemMeta(repairMeta);
                    MessageUtil.sendMessage(player, "<green>Item repaired!");
                } else {
                    MessageUtil.sendMessage(player, "<red>This item cannot be repaired!");
                }
                break;
            case "enchant":
                if (args.length < 1) {
                    MessageUtil.sendMessage(player, "<red>Usage: /enchant <enchantment> [level]");
                    return;
                }
                final String enchantName = args[0].toLowerCase();
                final Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantName));
                if (enchantment == null) {
                    MessageUtil.sendMessage(player, "<red>Invalid enchantment!");
                    return;
                }
                int level = 1;
                if (args.length > 1) {
                    try {
                        level = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {}
                }
                item.addUnsafeEnchantment(enchantment, level);
                MessageUtil.sendMessage(player, "<green>Applied " + enchantName + " " + level + "!");
                break;
            case "lore":
                if (args.length == 0) {
                    MessageUtil.sendMessage(player, "<red>Usage: /lore <text>");
                    return;
                }
                final String loreText = String.join(" ", args);
                final ItemMeta meta = item.getItemMeta();
                List<Component> lore = meta.hasLore() ? meta.lore() : new ArrayList<>();
                if (lore == null) lore = new ArrayList<>();
                lore.add(MessageUtil.parse(loreText));
                meta.lore(lore);
                item.setItemMeta(meta);
                MessageUtil.sendMessage(player, "<green>Lore updated!");
                break;
        }
    }
}
