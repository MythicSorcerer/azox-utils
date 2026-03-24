package com.azox.utils.manager;

import com.azox.utils.AzoxUtils;
import com.azox.utils.model.Home;
import com.azox.utils.util.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class GuiManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    public static final NamespacedKey HOME_KEY = new NamespacedKey(AzoxUtils.getInstance(), "home_name");
    public static final NamespacedKey UTILITY_KEY = new NamespacedKey(AzoxUtils.getInstance(), "utility_type");
    public static final NamespacedKey ADMIN_KEY = new NamespacedKey(AzoxUtils.getInstance(), "admin_setting");
    public static final NamespacedKey WORLD_KEY = new NamespacedKey(AzoxUtils.getInstance(), "world_name");
    public static final NamespacedKey EC_PAGE_KEY = new NamespacedKey(AzoxUtils.getInstance(), "ec_page");
    public static final NamespacedKey CONFIRM_ACTION_KEY = new NamespacedKey(AzoxUtils.getInstance(), "confirm_action");

    public void openHomesGui(final Player player) {
        final Map<String, Home> homes = plugin.getHomeManager().getHomes(player);
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>" + MessageUtil.ICON_HOME + " Your Homes"));

        int slot = 0;
        for (final Home home : homes.values()) {
            if (slot >= 53) break;
            
            final ItemStack item = new ItemStack(Material.LIME_CONCRETE);
            final ItemMeta meta = item.getItemMeta();
            meta.displayName(MessageUtil.parse("<green>" + MessageUtil.ICON_HOME + " " + home.getName()));
            meta.getPersistentDataContainer().set(HOME_KEY, PersistentDataType.STRING, home.getName());
            
            final List<Component> lore = new ArrayList<>();
            lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_ARROW + " World: " + home.getWorldName()));
            lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_ARROW + " X: " + (int) home.getX() + ", Y: " + (int) home.getY() + ", Z: " + (int) home.getZ()));
            if (!home.getDescription().isEmpty()) {
                lore.add(MessageUtil.parse("<gray>" + MessageUtil.ICON_INFO + " Description: " + home.getDescription()));
            }
            lore.add(MessageUtil.parse(""));
            lore.add(MessageUtil.parse("<green>" + MessageUtil.ICON_TP + " Left-Click to Teleport"));
            lore.add(MessageUtil.parse("<red>" + MessageUtil.ICON_UTILITY + " Right-Click to Manage"));
            
            meta.lore(lore);
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        final ItemStack unlock = new ItemStack(Material.GOLD_BLOCK);
        final ItemMeta unlockMeta = unlock.getItemMeta();
        unlockMeta.displayName(MessageUtil.parse("<gold><bold>Unlock More Homes"));
        unlockMeta.lore(List.of(MessageUtil.parse("<gray>Get a higher rank to increase your limit!")));
        unlock.setItemMeta(unlockMeta);
        inv.setItem(53, unlock);

        player.openInventory(inv);
    }

    public void openManageHomeGui(final Player player, final Home home) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Manage: " + home.getName()));

        final ItemStack info = new ItemStack(Material.BOOK);
        final ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(MessageUtil.parse("<yellow>" + MessageUtil.ICON_INFO + " Home Info"));
        final List<Component> infoLore = new ArrayList<>();
        infoLore.add(MessageUtil.parse("<gray>World: " + home.getWorldName()));
        infoLore.add(MessageUtil.parse("<gray>Coords: " + (int)home.getX() + ", " + (int)home.getY() + ", " + (int)home.getZ()));
        if (!home.getDescription().isEmpty()) infoLore.add(MessageUtil.parse("<gray>Desc: " + home.getDescription()));
        infoMeta.lore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(4, info);

        inv.setItem(10, createHomeActionItem(Material.ENDER_PEARL, "<green>Teleport", "teleport", home.getName()));
        inv.setItem(12, createHomeActionItem(Material.NAME_TAG, "<yellow>Rename", "rename", home.getName()));
        inv.setItem(13, createHomeActionItem(Material.WRITABLE_BOOK, "<yellow>Set Description", "description", home.getName()));
        inv.setItem(14, createHomeActionItem(Material.BEACON, "<aqua>Toggle Public", "public", home.getName()));
        inv.setItem(15, createHomeActionItem(Material.COMPASS, "<gold>Relocate", "relocate", home.getName()));
        inv.setItem(16, createHomeActionItem(Material.BARRIER, "<red>Delete", "delete", home.getName()));

        inv.setItem(22, createBackButton("homes"));

        player.openInventory(inv);
    }

    public void openConfirmGui(final Player player, String action, String targetName) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>Confirm: " + action));

        final ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        final ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.displayName(MessageUtil.parse("<green><bold>CONFIRM " + action.toUpperCase()));
        confirmMeta.getPersistentDataContainer().set(CONFIRM_ACTION_KEY, PersistentDataType.STRING, action + ":" + targetName);
        confirm.setItemMeta(confirmMeta);

        final ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        final ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.displayName(MessageUtil.parse("<red><bold>CANCEL"));
        cancel.setItemMeta(cancelMeta);

        inv.setItem(11, confirm);
        inv.setItem(15, cancel);

        player.openInventory(inv);
    }

    public void openUtilitiesGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Server Utilities"));

        if (player.hasPermission("azox.util.default.craft")) inv.setItem(10, createGuiItem(Material.CRAFTING_TABLE, "<green>Crafting Table", "craft"));
        if (player.hasPermission("azox.util.player.grindstone")) inv.setItem(11, createGuiItem(Material.GRINDSTONE, "<green>Grindstone", "grindstone"));
        if (player.hasPermission("azox.util.player.stonecutter")) inv.setItem(12, createGuiItem(Material.STONECUTTER, "<green>Stonecutter", "stonecutter"));
        if (player.hasPermission("azox.util.default.enderchest")) inv.setItem(13, createGuiItem(Material.ENDER_CHEST, "<green>Ender Chest", "ec"));
        if (player.hasPermission("azox.util.player.anvil")) inv.setItem(14, createGuiItem(Material.ANVIL, "<green>Anvil", "anvil"));
        if (player.hasPermission("azox.util.player.cartographytable")) inv.setItem(15, createGuiItem(Material.CARTOGRAPHY_TABLE, "<green>Cartography Table", "carttable"));
        if (player.hasPermission("azox.util.player.loom")) inv.setItem(16, createGuiItem(Material.LOOM, "<green>Loom", "loom"));

        player.openInventory(inv);
    }

    public void openAdminGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>" + MessageUtil.ICON_STAR + " Admin Configuration"));

        // Layout: --V---T--
        // Slots: 012345678
        inv.setItem(2, createAdminItem(Material.ENDER_EYE, "<aqua>Vanish Settings", "vanish_settings", true));
        inv.setItem(6, createAdminItem(Material.COMPASS, "<green>Teleport Menu", "teleport_menu", true));

        player.openInventory(inv);
    }

    public void openVanishGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<aqua>" + MessageUtil.ICON_INFO + " Vanish Settings"));

        boolean fakeMsg = plugin.getPlayerStorage().isVanishFakeMessages(player);
        boolean autoFly = plugin.getPlayerStorage().isVanishAutoFly(player);
        boolean autoGod = plugin.getPlayerStorage().isVanishAutoGod(player);
        boolean pickup = !plugin.getPlayerStorage().isVanishPickupDisabled(player);

        inv.setItem(10, createAdminItem(Material.PAPER, "<yellow>Fake Join/Leave", "v_fake_msg", fakeMsg));
        inv.setItem(19, new ItemStack(fakeMsg ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(12, createAdminItem(Material.FEATHER, "<yellow>Auto Fly", "v_auto_fly", autoFly));
        inv.setItem(21, new ItemStack(autoFly ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(14, createAdminItem(Material.GOLDEN_APPLE, "<yellow>Auto God", "v_auto_god", autoGod));
        inv.setItem(23, new ItemStack(autoGod ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(16, createAdminItem(Material.HOPPER, "<yellow>Item Pickup", "v_pickup", pickup));
        inv.setItem(25, new ItemStack(pickup ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(22, createBackButton("config"));

        player.openInventory(inv);
    }

    public void openConfigGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Configuration"));

        boolean guiEnabled = plugin.getPlayerStorage().isGuiEnabled(player);
        inv.setItem(10, createAdminItem(Material.BOOK, "<yellow>GUI Mode", "toggle_gui", guiEnabled));
        inv.setItem(11, new ItemStack(guiEnabled ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        boolean particles = plugin.getPlayerStorage().areParticlesEnabled(player);
        inv.setItem(12, createAdminItem(Material.FIREWORK_STAR, "<yellow>Particles", "toggle_particles", particles));
        inv.setItem(13, new ItemStack(particles ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE));

        inv.setItem(15, createAdminItem(Material.ENDER_EYE, "<aqua>Vanish Settings", "vanish_settings", true));

        player.openInventory(inv);
    }

    public void openWorldSelectorGui(final Player player) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));

        inv.setItem(11, createWorldItem(Material.GRASS_BLOCK, "<green>Survival", "world"));
        inv.setItem(13, createWorldItem(Material.BEACON, "<gold>Lobby", "lobby"));

        inv.setItem(22, createBackButton("admin"));

        player.openInventory(inv);
    }

    public void openTeleportMenu(final Player player, int page) {
        final Inventory inv = Bukkit.createInventory(null, 54, MessageUtil.parse("<green>" + MessageUtil.ICON_TP + " Teleport Menu - Page " + page));

        // Fill with dimensions first (overworld, nether, end)
        inv.setItem(0, createWorldItem(Material.GRASS_BLOCK, "<green>Overworld", "tp_world_overworld"));
        inv.setItem(1, createWorldItem(Material.NETHERRACK, "<red>Nether", "tp_world_nether"));
        inv.setItem(2, createWorldItem(Material.END_STONE, "<purple>End", "tp_world_end"));

        // Online players (player heads)
        int slot = 9;
        int itemsOnPage = 0;
        final int itemsPerPage = 45;
        final int skipItems = (page - 1) * itemsPerPage;
        int skipped = 0;

        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (skipped < skipItems) {
                skipped++;
                continue;
            }
            if (itemsOnPage >= itemsPerPage - 9) break; // Leave last row for navigation

            final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            final ItemMeta meta = head.getItemMeta();
            meta.displayName(MessageUtil.parse("<yellow>" + onlinePlayer.getName()));
            meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_player:" + onlinePlayer.getName());
            final List<Component> lore = new ArrayList<>();
            lore.add(MessageUtil.parse("<gray>Click to teleport to " + onlinePlayer.getName()));
            meta.lore(lore);
            head.setItemMeta(meta);
            inv.setItem(slot++, head);
            itemsOnPage++;
        }

        // Offline players (from stored data)
        final File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (dataFolder.exists()) {
            final File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (final File file : files) {
                    if (itemsOnPage >= itemsPerPage - 9) break;

                    final String fileName = file.getName();
                    final String namePart = fileName.substring(0, fileName.lastIndexOf('_'));
                    final String uuidPart = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));

                    // Skip if player is online (already added)
                    boolean isOnline = false;
                    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.getName().equalsIgnoreCase(namePart)) {
                            isOnline = true;
                            break;
                        }
                    }
                    if (isOnline) continue;

                    if (skipped < skipItems) {
                        skipped++;
                        continue;
                    }

                    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    final ItemMeta meta = head.getItemMeta();
                    meta.displayName(MessageUtil.parse("<gray>" + namePart + " <dark_gray>(Offline)"));
                    meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_offline:" + uuidPart);
                    final List<Component> lore = new ArrayList<>();
                    lore.add(MessageUtil.parse("<gray>Click to teleport to last known location"));
                    meta.lore(lore);
                    head.setItemMeta(meta);
                    inv.setItem(slot++, head);
                    itemsOnPage++;
                }
            }
        }

        // Navigation buttons (last row)
        if (page > 1) {
            final ItemStack prev = new ItemStack(Material.ARROW);
            final ItemMeta prevMeta = prev.getItemMeta();
            prevMeta.displayName(MessageUtil.parse("<green>Previous Page"));
            prevMeta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_page:" + (page - 1));
            prev.setItemMeta(prevMeta);
            inv.setItem(48, prev);
        }

        final ItemStack back = createBackButton("admin");
        final ItemMeta backMeta = back.getItemMeta();
        backMeta.displayName(MessageUtil.parse("<red>Back"));
        back.setItemMeta(backMeta);
        inv.setItem(49, back);

        if (slot >= 45 + (page * itemsPerPage)) {
            final ItemStack next = new ItemStack(Material.ARROW);
            final ItemMeta nextMeta = next.getItemMeta();
            nextMeta.displayName(MessageUtil.parse("<green>Next Page"));
            nextMeta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_page:" + (page + 1));
            next.setItemMeta(nextMeta);
            inv.setItem(50, next);
        }

        player.openInventory(inv);
    }

    public void openEnderChestPageSelector(final Player player, int maxPages) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest Pages"));
        for (int i = 1; i <= maxPages; i++) {
            inv.setItem(10 + i, createEcPageItem(i));
        }
        player.openInventory(inv);
    }

    public void openEnderChestPage(final Player player, int page) {
        final Inventory inv = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest - Page " + page));
        inv.setContents(plugin.getPlayerStorage().getEnderChestPage(player, page));
        player.openInventory(inv);
    }

    private ItemStack createGuiItem(final Material material, final String name, final String type) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(UTILITY_KEY, PersistentDataType.STRING, type);
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Click to open!"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminItem(final Material material, final String name, final String key, final boolean enabled) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, key);
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Status: " + (enabled ? "<green>Enabled" : "<red>Disabled")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createHomeActionItem(Material material, String name, String action, String homeName) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "home_action:" + action + ":" + homeName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackButton(String target) {
        final ItemStack item = new ItemStack(Material.BARRIER);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<red>Back"));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "back_to:" + target);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createWorldItem(final Material material, final String name, final String worldName) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse(name));
        meta.getPersistentDataContainer().set(WORLD_KEY, PersistentDataType.STRING, worldName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEcPageItem(int page) {
        final ItemStack item = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(MessageUtil.parse("<green>Page " + page));
        meta.getPersistentDataContainer().set(EC_PAGE_KEY, PersistentDataType.INTEGER, page);
        item.setItemMeta(meta);
        return item;
    }
}
