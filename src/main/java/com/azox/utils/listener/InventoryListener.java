package com.azox.utils.listener;

import com.azox.utils.AzoxUtils;
import com.azox.utils.command.impl.util.SeeCommand;
import com.azox.utils.manager.GuiManager;
import com.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class InventoryListener implements Listener {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final Component title = event.getView().title();
        final String plainTitle = PLAIN_SERIALIZER.serialize(title);

        if (plainTitle.startsWith("Inspecting: ")) {
            handleInspectClick(event);
            return;
        }

        if (plainTitle.contains("Your Homes")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String homeName = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.HOME_KEY, PersistentDataType.STRING);
            if (homeName == null) return;

            final Player player = (Player) event.getWhoClicked();

            if (event.isLeftClick()) {
                player.closeInventory();
                player.performCommand("home " + homeName);
            } else if (event.isRightClick()) {
                player.closeInventory();
                player.performCommand("edithome " + homeName);
            }
        } else if (plainTitle.contains("Server Utilities")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String type = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.UTILITY_KEY, PersistentDataType.STRING);
            if (type == null) return;

            final Player player = (Player) event.getWhoClicked();

            player.closeInventory();
            switch (type) {
                case "craft": player.performCommand("craft"); break;
                case "grindstone": player.performCommand("grindstone"); break;
                case "stonecutter": player.performCommand("stonecutter"); break;
                case "ec": player.performCommand("ec"); break;
                case "anvil": player.performCommand("anvil"); break;
                case "carttable": player.performCommand("carttable"); break;
            }
        } else if (plainTitle.contains("Admin Configuration")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String setting = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
            if (setting == null) return;

            final Player player = (Player) event.getWhoClicked();

            switch (setting) {
                case "vanish_settings":
                    plugin.getGuiManager().openVanishGui(player);
                    break;
                case "teleport_menu":
                    plugin.getGuiManager().openTeleportMenu(player, 1);
                    break;
            }
        } else if (plainTitle.contains("Configuration")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String setting = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
            if (setting == null) return;

            final Player player = (Player) event.getWhoClicked();

            switch (setting) {
                case "toggle_gui":
                    boolean currentGui = plugin.getPlayerStorage().isGuiEnabled(player);
                    plugin.getPlayerStorage().setGuiEnabled(player, !currentGui);
                    plugin.getGuiManager().openConfigGui(player);
                    break;
                case "toggle_particles":
                    boolean currentParticles = plugin.getPlayerStorage().areParticlesEnabled(player);
                    plugin.getPlayerStorage().setParticlesEnabled(player, !currentParticles);
                    plugin.getGuiManager().openConfigGui(player);
                    break;
                case "vanish_settings":
                    plugin.getGuiManager().openVanishGui(player);
                    break;
            }
        } else if (plainTitle.contains("Vanish Settings")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String setting = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
            if (setting == null) return;

            final Player player = (Player) event.getWhoClicked();

            switch (setting) {
                case "v_fake_msg":
                    plugin.getPlayerStorage().setVanishFakeMessages(player, !plugin.getPlayerStorage().isVanishFakeMessages(player));
                    break;
                case "v_auto_fly":
                    plugin.getPlayerStorage().setVanishAutoFly(player, !plugin.getPlayerStorage().isVanishAutoFly(player));
                    break;
                case "v_auto_god":
                    plugin.getPlayerStorage().setVanishAutoGod(player, !plugin.getPlayerStorage().isVanishAutoGod(player));
                    break;
                case "v_pickup":
                    plugin.getPlayerStorage().setVanishPickupDisabled(player, !plugin.getPlayerStorage().isVanishPickupDisabled(player));
                    break;
            }
            plugin.getGuiManager().openVanishGui(player);
        } else if (plainTitle.contains("World Selector")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String worldName = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.WORLD_KEY, PersistentDataType.STRING);
            if (worldName == null) return;

            final Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            player.performCommand("tp " + worldName);
        } else if (plainTitle.equals("Ender Chest Pages")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final Integer page = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.EC_PAGE_KEY, PersistentDataType.INTEGER);
            if (page == null) return;

            final Player player = (Player) event.getWhoClicked();
            plugin.getGuiManager().openEnderChestPage(player, page);
        } else if (plainTitle.contains("Teleport Menu")) {
            event.setCancelled(true);
            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getItemMeta() == null) return;

            final String action = clickedItem.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
            if (action == null) return;

            final Player player = (Player) event.getWhoClicked();

            if (action.startsWith("tp_player:")) {
                final String targetName = action.substring(10);
                player.closeInventory();
                player.performCommand("tp " + targetName);
            } else if (action.startsWith("tp_offline:")) {
                final String uuidStr = action.substring(11);
                player.closeInventory();
                player.performCommand("tpo " + uuidStr);
            } else if (action.startsWith("tp_page:")) {
                final int page = Integer.parseInt(action.substring(8));
                plugin.getGuiManager().openTeleportMenu(player, page);
            } else if (action.startsWith("tp_world_")) {
                final String world = action.substring(9);
                player.closeInventory();
                player.performCommand("tp " + world);
            } else if (action.equals("back_to:admin")) {
                plugin.getGuiManager().openAdminGui(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(final org.bukkit.event.inventory.InventoryCloseEvent event) {
        final String plainTitle = PLAIN_SERIALIZER.serialize(event.getView().title());
        if (plainTitle.startsWith("Ender Chest - Page ")) {
            try {
                final int page = Integer.parseInt(plainTitle.substring(plainTitle.lastIndexOf(' ') + 1));
                final Player player = (Player) event.getPlayer();
                plugin.getPlayerStorage().saveEnderChestPage(player, page, event.getInventory().getContents());
                MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Ender Chest Page " + page + " saved!");
            } catch (NumberFormatException ignored) {}
        }
    }

    private void handleInspectClick(final InventoryClickEvent event) {
        final Inventory inv = event.getInventory();
        final ItemStack infoItem = inv.getItem(42);
        if (infoItem == null || infoItem.getItemMeta() == null) return;

        final String uuidStr = infoItem.getItemMeta().getPersistentDataContainer().get(SeeCommand.INSPECT_TARGET_KEY, PersistentDataType.STRING);
        if (uuidStr == null) return;

        final Player target = Bukkit.getPlayer(UUID.fromString(uuidStr));
        if (target == null) {
            event.getWhoClicked().closeInventory();
            return;
        }

        final int slot = event.getRawSlot();
        
        final ItemStack clickedItem = event.getCurrentItem();
        if (slot >= 42 && slot <= 44 || (slot >= 45 && target.getOpenInventory().getTopInventory() == null) || (clickedItem != null && clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            final ItemStack[] contents = target.getInventory().getContents();
            for (int i = 0; i < 36; i++) contents[i] = filterPlaceholder(inv.getItem(i));
            target.getInventory().setContents(contents);

            final ItemStack[] armor = new ItemStack[4];
            armor[3] = filterPlaceholder(inv.getItem(36));
            armor[2] = filterPlaceholder(inv.getItem(37));
            armor[1] = filterPlaceholder(inv.getItem(38));
            armor[0] = filterPlaceholder(inv.getItem(39));
            target.getInventory().setArmorContents(armor);

            target.getInventory().setItemInOffHand(filterPlaceholder(inv.getItem(40)));
            target.setItemOnCursor(filterPlaceholder(inv.getItem(50)));

            final Inventory topInv = target.getOpenInventory().getTopInventory();
            if (topInv != null && topInv.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING && topInv.getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
                for (int i = 0; i < 9; i++) {
                    if (i < topInv.getSize()) {
                        topInv.setItem(i, filterPlaceholder(inv.getItem(45 + i)));
                    }
                }
            }
        });
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        final String plainTitle = PLAIN_SERIALIZER.serialize(event.getView().title());
        if (plainTitle.startsWith("Inspecting: ")) {
            event.setCancelled(true);
        }
    }

    private ItemStack filterPlaceholder(ItemStack item) {
        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) return null;
        return item;
    }
}
