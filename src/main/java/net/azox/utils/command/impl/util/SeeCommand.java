package net.azox.utils.command.impl.util;

import net.azox.utils.AzoxUtils;
import net.azox.utils.command.BaseCommand;
import net.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SeeCommand extends BaseCommand {

    public static final NamespacedKey INSPECT_TARGET_KEY = new NamespacedKey(AzoxUtils.getInstance(), "inspect_target");

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return;
        final Player player = (Player) sender;

        String type = "i"; // default to inventory
        String targetName;

        if (label.equalsIgnoreCase("si")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /si <player>");
                return;
            }
            targetName = args[0];
        } else if (label.equalsIgnoreCase("se")) {
            if (args.length == 0) {
                MessageUtil.sendMessage(player, "<red>Usage: /se <player>");
                return;
            }
            targetName = args[0];
            type = "e";
        } else {
            if (args.length < 2) {
                MessageUtil.sendMessage(player, "<red>Usage: /see <i|e> <player>");
                return;
            }
            type = args[0].toLowerCase();
            targetName = args[1];
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null) { // Should not happen with getOfflinePlayer but good practice
            MessageUtil.sendMessage(player, "<red>Player not found!");
            return;
        }

        if (type.startsWith("i")) {
            openInventoryInspect(player, target);
        } else if (type.startsWith("e")) {
            if (target.isOnline()) {
                player.openInventory(target.getPlayer().getEnderChest());
                MessageUtil.sendMessage(player, "<green>Opening " + target.getName() + "'s enderchest...");
            } else {
                // To inspect offline enderchests, we'd need to load NBT data, which is complex.
                // For now, we'll indicate it's not supported.
                MessageUtil.sendMessage(player, "<red>Cannot inspect offline player's enderchest without NBT parsing.");
            }
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String alias, @NotNull String[] args) {
        if (alias.equalsIgnoreCase("si") || alias.equalsIgnoreCase("se")) {
            if (args.length == 1) {
                return getVisiblePlayerNames(sender, args[0]);
            }
        } else if (alias.equalsIgnoreCase("see")) {
            if (args.length == 1) {
                return Arrays.asList("i", "e").stream()
                        .filter(s -> s.startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                return getVisiblePlayerNames(sender, args[1]);
            }
        }
        return Collections.emptyList();
    }

    private void openInventoryInspect(final Player viewer, final OfflinePlayer target) {
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>Inspecting: " + target.getName() + (target.isOnline() ? "" : " (Offline Snapshot)")));
        updateInspectInventory(inv, target);
        viewer.openInventory(inv);
        MessageUtil.sendMessage(viewer, "<green>Opening " + target.getName() + "'s inventory...");

        if (target.isOnline()) {
            final Player onlineTarget = target.getPlayer();
            // Live update task only for online players
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!viewer.isOnline() || !viewer.getOpenInventory().getTitle().contains("Inspecting: " + target.getName())) {
                        this.cancel();
                        return;
                    }
                    if (!onlineTarget.isOnline()) { // Target went offline
                        viewer.closeInventory();
                        MessageUtil.sendMessage(viewer, "<red>Target went offline, closing inspection.");
                        this.cancel();
                        return;
                    }
                    updateInspectInventory(inv, onlineTarget);
                }
            }.runTaskTimer(plugin, 20L, 20L);
        }
    }

    public static void updateInspectInventory(final Inventory inv, final OfflinePlayer target) {
        if (!target.isOnline()) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i, null);
            }
            inv.setItem(42, createInfoItem(target));
            inv.setItem(51, createActivityItem(target));
            return;
        }
        
        final Player onlineTarget = target.getPlayer();
        final ItemStack[] contents = onlineTarget.getInventory().getContents(); 
        
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, contents[i]);
        }
        
        for (int i = 9; i < 36; i++) {
            inv.setItem(i, contents[i]);
        }

        final ItemStack[] armor = onlineTarget.getInventory().getArmorContents();
        inv.setItem(36, armor[3] != null && armor[3].getType() != Material.AIR ? armor[3] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Helmet Slot"));
        inv.setItem(37, armor[2] != null && armor[2].getType() != Material.AIR ? armor[2] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Chestplate Slot"));
        inv.setItem(38, armor[1] != null && armor[1].getType() != Material.AIR ? armor[1] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Leggings Slot"));
        inv.setItem(39, armor[0] != null && armor[0].getType() != Material.AIR ? armor[0] : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Boots Slot"));
        
        inv.setItem(40, onlineTarget.getInventory().getItemInOffHand() != null && onlineTarget.getInventory().getItemInOffHand().getType() != Material.AIR ? onlineTarget.getInventory().getItemInOffHand() : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Offhand Slot"));
        inv.setItem(41, onlineTarget.getItemOnCursor() != null && onlineTarget.getItemOnCursor().getType() != Material.AIR ? onlineTarget.getItemOnCursor() : createPlaceholder(Material.GRAY_STAINED_GLASS_PANE, "<gray>Cursor Slot"));
        
        final ItemStack spacer = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        final ItemMeta spacerMeta = spacer.getItemMeta();
        spacerMeta.displayName(Component.empty());
        spacer.setItemMeta(spacerMeta);
        inv.setItem(42, createInfoItem(onlineTarget));
        inv.setItem(43, spacer);
        inv.setItem(44, spacer);

        final Inventory topInv = onlineTarget.getOpenInventory().getTopInventory();
        if (topInv != null && topInv.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING && topInv.getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
            for (int i = 0; i < 9; i++) {
                if (i < topInv.getSize()) {
                    inv.setItem(45 + i, topInv.getItem(i));
                } else {
                    inv.setItem(45 + i, spacer);
                }
            }
        } else {
            for (int i = 45; i < 54; i++) {
                inv.setItem(i, spacer);
            }
        }
    }

    private static int countItems(Inventory inv) {
        int count = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) count++;
        }
        return count;
    }

    private static ItemStack createInfoItem(OfflinePlayer target) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<gold>Player: " + target.getName() + (target.isOnline() ? "" : " <red>(Offline)")));
        List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>UUID: " + target.getUniqueId()));
        lore.add(MessageUtil.parse("<gray>Online: " + (target.isOnline() ? "<green>Yes" : "<red>No")));
        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            lore.add(MessageUtil.parse("<gray>Health: <red>" + (int)onlineTarget.getHealth() + "/" + (int)onlineTarget.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue()));
            lore.add(MessageUtil.parse("<gray>Food: <orange>" + onlineTarget.getFoodLevel()));
        } else {
            lore.add(MessageUtil.parse("<gray>Last Seen: " + (target.getLastPlayed() > 0 ? new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(new java.util.Date(target.getLastPlayed())) : "Never")));
        }
        meta.getPersistentDataContainer().set(INSPECT_TARGET_KEY, PersistentDataType.STRING, target.getUniqueId().toString());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createActivityItem(OfflinePlayer target) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<yellow>Current Activity"));
        List<Component> lore = new ArrayList<>();
        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            lore.add(MessageUtil.parse("<gray>Open Inventory: <white>" + onlineTarget.getOpenInventory().getType().name()));
            if (onlineTarget.getOpenInventory().getTopInventory() != null) {
                lore.add(MessageUtil.parse("<gray>Top Items: <white>" + countItems(onlineTarget.getOpenInventory().getTopInventory())));
            }
        } else {
            lore.add(MessageUtil.parse("<gray>Offline. No activity."));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createPlaceholder(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        item.setItemMeta(meta);
        return item;
    }
}
