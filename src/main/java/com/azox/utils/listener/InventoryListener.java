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

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getItemMeta() == null) {
            return;
        }

        if (plainTitle.contains("Your Homes")) {
            handleHomesClick(event, player, clickedItem);
        } else if (plainTitle.contains("Server Utilities")) {
            handleUtilitiesClick(event, player, clickedItem);
        } else if (plainTitle.contains("Admin Configuration")) {
            handleAdminClick(event, player, clickedItem);
        } else if (plainTitle.contains("Vanish Settings")) {
            handleVanishSettingsClick(event, player, clickedItem);
        } else if (plainTitle.contains("Configuration")) {
            handleConfigClick(event, player, clickedItem);
        } else if (plainTitle.contains("World Selector")) {
            handleWorldSelectorClick(event, player, clickedItem);
        } else if (plainTitle.contains("Teleport Menu")) {
            handleTeleportMenuClick(event, player, clickedItem);
        } else if (plainTitle.equals("Ender Chest Pages")) {
            handleEnderChestPageClick(event, player, clickedItem);
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
            } catch (NumberFormatException ignored) {
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        final String plainTitle = PLAIN_SERIALIZER.serialize(event.getView().title());
        if (plainTitle.startsWith("Inspecting: ")) {
            event.setCancelled(true);
        }
    }

    private void handleHomesClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String homeName = item.getItemMeta().getPersistentDataContainer().get(GuiManager.HOME_KEY, PersistentDataType.STRING);
        if (homeName == null) {
            return;
        }

        player.closeInventory();
        if (event.isLeftClick()) {
            player.performCommand("home " + homeName);
        } else if (event.isRightClick()) {
            player.performCommand("edithome " + homeName);
        }
    }

    private void handleUtilitiesClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String type = item.getItemMeta().getPersistentDataContainer().get(GuiManager.UTILITY_KEY, PersistentDataType.STRING);
        if (type == null) {
            return;
        }

        player.closeInventory();
        switch (type) {
            case "craft" -> player.performCommand("craft");
            case "grindstone" -> player.performCommand("grindstone");
            case "stonecutter" -> player.performCommand("stonecutter");
            case "ec" -> player.performCommand("ec");
            case "anvil" -> player.performCommand("anvil");
            case "carttable" -> player.performCommand("carttable");
        }
    }

    private void handleAdminClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String setting = item.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
        if (setting == null) {
            return;
        }

        switch (setting) {
            case "vanish_settings" -> plugin.getGuiManager().openVanishGui(player);
            case "teleport_menu" -> plugin.getGuiManager().openTeleportMenu(player, 1);
        }
    }

    private void handleVanishSettingsClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String setting = item.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
        if (setting == null) {
            return;
        }

        if (setting.equals("back_to:admin")) {
            plugin.getGuiManager().openAdminGui(player);
            return;
        }

        switch (setting) {
            case "v_fake_msg" -> plugin.getPlayerStorage().setVanishFakeMessages(player, !plugin.getPlayerStorage().isVanishFakeMessages(player));
            case "v_auto_fly" -> plugin.getPlayerStorage().setVanishAutoFly(player, !plugin.getPlayerStorage().isVanishAutoFly(player));
            case "v_auto_god" -> plugin.getPlayerStorage().setVanishAutoGod(player, !plugin.getPlayerStorage().isVanishAutoGod(player));
            case "v_pickup" -> plugin.getPlayerStorage().setVanishPickupDisabled(player, !plugin.getPlayerStorage().isVanishPickupDisabled(player));
        }
        plugin.getGuiManager().openVanishGui(player);
    }

    private void handleConfigClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String setting = item.getItemMeta().getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
        if (setting == null) {
            return;
        }

        switch (setting) {
            case "toggle_gui" -> {
                plugin.getPlayerStorage().setGuiEnabled(player, !plugin.getPlayerStorage().isGuiEnabled(player));
                plugin.getGuiManager().openConfigGui(player);
            }
            case "toggle_particles" -> {
                plugin.getPlayerStorage().setParticlesEnabled(player, !plugin.getPlayerStorage().areParticlesEnabled(player));
                plugin.getGuiManager().openConfigGui(player);
            }
        }
    }

    private void handleWorldSelectorClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final String worldName = item.getItemMeta().getPersistentDataContainer().get(GuiManager.WORLD_KEY, PersistentDataType.STRING);
        if (worldName == null) {
            return;
        }

        player.closeInventory();
        player.performCommand("tp " + worldName);
    }

    private void handleTeleportMenuClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final var meta = item.getItemMeta();

        final String worldName = meta.getPersistentDataContainer().get(GuiManager.WORLD_KEY, PersistentDataType.STRING);
        if (worldName != null) {
            player.closeInventory();
            player.performCommand("tp " + worldName);
            return;
        }

        final String action = meta.getPersistentDataContainer().get(GuiManager.ADMIN_KEY, PersistentDataType.STRING);
        if (action == null) {
            return;
        }

        if (action.startsWith("tp_player:")) {
            player.closeInventory();
            player.performCommand("tp " + action.substring(10));
        } else if (action.startsWith("tp_offline:")) {
            player.closeInventory();
            player.performCommand("tpo " + action.substring(11));
        } else if (action.startsWith("tp_page:")) {
            plugin.getGuiManager().openTeleportMenu(player, Integer.parseInt(action.substring(8)));
        } else if (action.equals("back_to:admin")) {
            plugin.getGuiManager().openAdminGui(player);
        } else if (action.equals("back_to:config")) {
            plugin.getGuiManager().openConfigGui(player);
        }
    }

    private void handleEnderChestPageClick(final InventoryClickEvent event, final Player player, final ItemStack item) {
        event.setCancelled(true);
        final Integer page = item.getItemMeta().getPersistentDataContainer().get(GuiManager.EC_PAGE_KEY, PersistentDataType.INTEGER);
        if (page == null) {
            return;
        }

        plugin.getGuiManager().openEnderChestPage(player, page);
    }

    private void handleInspectClick(final InventoryClickEvent event) {
        final var inv = event.getInventory();
        final ItemStack infoItem = inv.getItem(42);
        if (infoItem == null || infoItem.getItemMeta() == null) {
            return;
        }

        final String uuidStr = infoItem.getItemMeta().getPersistentDataContainer().get(SeeCommand.INSPECT_TARGET_KEY, PersistentDataType.STRING);
        if (uuidStr == null) {
            return;
        }

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
            for (int i = 0; i < 36; i++) {
                contents[i] = filterPlaceholder(inv.getItem(i));
            }
            target.getInventory().setContents(contents);

            final ItemStack[] armor = new ItemStack[4];
            armor[3] = filterPlaceholder(inv.getItem(36));
            armor[2] = filterPlaceholder(inv.getItem(37));
            armor[1] = filterPlaceholder(inv.getItem(38));
            armor[0] = filterPlaceholder(inv.getItem(39));
            target.getInventory().setArmorContents(armor);

            target.getInventory().setItemInOffHand(filterPlaceholder(inv.getItem(40)));
            target.setItemOnCursor(filterPlaceholder(inv.getItem(50)));

            final var topInv = target.getOpenInventory().getTopInventory();
            if (topInv != null && topInv.getType() != org.bukkit.event.inventory.InventoryType.CRAFTING && topInv.getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
                for (int i = 0; i < 9 && i < topInv.getSize(); i++) {
                    topInv.setItem(i, filterPlaceholder(inv.getItem(45 + i)));
                }
            }
        });
    }

    private ItemStack filterPlaceholder(final ItemStack item) {
        if (item == null || item.getType() == Material.GRAY_STAINED_GLASS_PANE) {
            return null;
        }
        return item;
    }
}
