package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import net.azox.utils.model.Home;
import net.azox.utils.util.MessageUtil;
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
import java.util.Objects;

public final class GuiManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    public static final NamespacedKey HOME_KEY = new NamespacedKey(AzoxUtils.getInstance(), "home_name");
    public static final NamespacedKey UTILITY_KEY = new NamespacedKey(AzoxUtils.getInstance(), "utility_type");
    public static final NamespacedKey ADMIN_KEY = new NamespacedKey(AzoxUtils.getInstance(), "admin_setting");
    public static final NamespacedKey WORLD_KEY = new NamespacedKey(AzoxUtils.getInstance(), "world_name");
    public static final NamespacedKey EC_PAGE_KEY = new NamespacedKey(AzoxUtils.getInstance(), "ec_page");
    public static final NamespacedKey CONFIRM_ACTION_KEY = new NamespacedKey(AzoxUtils.getInstance(), "confirm_action");

    public void openHomesGui(final Player player) {
        if (player == null) {
            return;
        }
        final Map<String, net.azox.utils.model.Home> homes = plugin.getHomeManager().getHomes(player);
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>" + MessageUtil.ICON_HOME + " Your Homes"));

        int slot = 0;
        for (final net.azox.utils.model.Home home : homes.values()) {
            if (slot >= 53) {
                break;
            }

            final ItemStack item = new ItemStack(Material.LIME_CONCRETE);
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
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
            inventory.setItem(slot++, item);
        }

        final ItemStack unlock = new ItemStack(Material.GOLD_BLOCK);
        final ItemMeta unlockMeta = unlock.getItemMeta();
        if (unlockMeta != null) {
            unlockMeta.displayName(MessageUtil.parse("<gold><bold>Unlock More Homes"));
            unlockMeta.lore(List.of(MessageUtil.parse("<gray>Get a higher rank to increase your limit!")));
            unlock.setItemMeta(unlockMeta);
        }
        inventory.setItem(53, unlock);

        player.openInventory(inventory);
    }

    public void openManageHomeGui(final Player player, final net.azox.utils.model.Home home) {
        if (player == null || home == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Manage: " + home.getName()));

        final ItemStack info = new ItemStack(Material.BOOK);
        final ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.displayName(MessageUtil.parse("<yellow>" + MessageUtil.ICON_INFO + " Home Info"));
            final List<Component> infoLore = new ArrayList<>();
            infoLore.add(MessageUtil.parse("<gray>World: " + home.getWorldName()));
            infoLore.add(MessageUtil.parse("<gray>Coords: " + (int) home.getX() + ", " + (int) home.getY() + ", " + (int) home.getZ()));
            if (!home.getDescription().isEmpty()) {
                infoLore.add(MessageUtil.parse("<gray>Desc: " + home.getDescription()));
            }
            infoMeta.lore(infoLore);
            info.setItemMeta(infoMeta);
        }
        inventory.setItem(4, info);

        inventory.setItem(10, createHomeActionItem(Material.ENDER_PEARL, "<green>Teleport", "teleport", home.getName()));
        inventory.setItem(12, createHomeActionItem(Material.NAME_TAG, "<yellow>Rename", "rename", home.getName()));
        inventory.setItem(13, createHomeActionItem(Material.WRITABLE_BOOK, "<yellow>Set Description", "description", home.getName()));
        inventory.setItem(14, createHomeActionItem(Material.BEACON, "<aqua>Toggle Public", "public", home.getName()));
        inventory.setItem(15, createHomeActionItem(Material.COMPASS, "<gold>Relocate", "relocate", home.getName()));
        inventory.setItem(16, createHomeActionItem(Material.BARRIER, "<red>Delete", "delete", home.getName()));

        inventory.setItem(22, createBackButton("homes"));

        player.openInventory(inventory);
    }

    public void openConfirmGui(final Player player, final String action, final String targetName) {
        if (player == null || action == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<red>Confirm: " + action));

        final ItemStack confirm = new ItemStack(Material.LIME_CONCRETE);
        final ItemMeta confirmMeta = confirm.getItemMeta();
        if (confirmMeta != null) {
            confirmMeta.displayName(MessageUtil.parse("<green><bold>CONFIRM " + action.toUpperCase()));
            confirmMeta.getPersistentDataContainer().set(CONFIRM_ACTION_KEY, PersistentDataType.STRING, action + ":" + targetName);
            confirm.setItemMeta(confirmMeta);
        }

        final ItemStack cancel = new ItemStack(Material.RED_CONCRETE);
        final ItemMeta cancelMeta = cancel.getItemMeta();
        if (cancelMeta != null) {
            cancelMeta.displayName(MessageUtil.parse("<red><bold>CANCEL"));
            cancel.setItemMeta(cancelMeta);
        }

        inventory.setItem(11, confirm);
        inventory.setItem(15, cancel);

        player.openInventory(inventory);
    }

    public void openUtilitiesGui(final Player player) {
        if (player == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Server Utilities"));

        if (player.hasPermission("azox.util.craft")) {
            inventory.setItem(10, createGuiItem(Material.CRAFTING_TABLE, "<green>Crafting Table", "craft"));
        }
        if (player.hasPermission("azox.util.grindstone")) {
            inventory.setItem(11, createGuiItem(Material.GRINDSTONE, "<green>Grindstone", "grindstone"));
        }
        if (player.hasPermission("azox.util.stonecutter")) {
            inventory.setItem(12, createGuiItem(Material.STONECUTTER, "<green>Stonecutter", "stonecutter"));
        }
        if (player.hasPermission("azox.util.enderchest")) {
            inventory.setItem(13, createGuiItem(Material.ENDER_CHEST, "<green>Ender Chest", "ec"));
        }
        if (player.hasPermission("azox.util.anvil")) {
            inventory.setItem(14, createGuiItem(Material.ANVIL, "<green>Anvil", "anvil"));
        }
        if (player.hasPermission("azox.util.cartographytable")) {
            inventory.setItem(15, createGuiItem(Material.CARTOGRAPHY_TABLE, "<green>Cartography Table", "carttable"));
        }
        if (player.hasPermission("azox.util.loom")) {
            inventory.setItem(16, createGuiItem(Material.LOOM, "<green>Loom", "loom"));
        }

        player.openInventory(inventory);
    }

    public void openAdminGui(final Player player) {
        if (player == null) {
            return;
        }
        // Use 54-slot inventory with items on middle row (row 3, slots 18-26)
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<red>" + MessageUtil.ICON_STAR + " Admin Configuration"));

        // Middle row (row 3): slots 18-26, place items at 20 and 24
        inventory.setItem(20, createAdminItem(Material.ENDER_EYE, "<aqua>Vanish Settings", "vanish_settings", true));
        inventory.setItem(24, createAdminItem(Material.COMPASS, "<green>Teleport Menu", "teleport_menu", true));

        player.openInventory(inventory);
    }

    public void openVanishGui(final Player player) {
        if (player == null) {
            return;
        }
        // Use 54-slot inventory for better spacing
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<aqua>" + MessageUtil.ICON_INFO + " Vanish Settings"));

        final boolean fakeMessage = plugin.getPlayerStorage().isVanishFakeMessages(player);
        final boolean autoFly = plugin.getPlayerStorage().isVanishAutoFly(player);
        final boolean autoGod = plugin.getPlayerStorage().isVanishAutoGod(player);
        final boolean pickup = !plugin.getPlayerStorage().isVanishPickupDisabled(player);

        // Row 2 (slots 9-17): Fake Join/Leave at slot 11, indicator at 20
        inventory.setItem(11, createAdminItem(Material.PAPER, "<yellow>Fake Join/Leave", "v_fake_msg", fakeMessage));
        inventory.setItem(20, createStatusIndicator(fakeMessage));

        // Row 3 (slots 18-26): Auto Fly at slot 20, indicator at 29 - but we use 54 slot so 29 is valid
        inventory.setItem(22, createAdminItem(Material.FEATHER, "<yellow>Auto Fly", "v_auto_fly", autoFly));
        inventory.setItem(31, createStatusIndicator(autoFly));

        // Row 4 (slots 27-35): Auto God at slot 31, indicator at 40
        inventory.setItem(33, createAdminItem(Material.GOLDEN_APPLE, "<yellow>Auto God", "v_auto_god", autoGod));
        inventory.setItem(42, createStatusIndicator(autoGod));

        // Row 5 (slots 36-44): Item Pickup at slot 38, indicator at 47
        inventory.setItem(38, createAdminItem(Material.HOPPER, "<yellow>Item Pickup", "v_pickup", pickup));
        inventory.setItem(47, createStatusIndicator(pickup));

        // Back button in center of last row
        inventory.setItem(49, createBackButton("admin"));

        player.openInventory(inventory);
    }

    public void openConfigGui(final Player player) {
        if (player == null) {
            return;
        }
        // Use 54-slot inventory for better spacing
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<gold>" + MessageUtil.ICON_UTILITY + " Configuration"));

        final boolean guiEnabled = plugin.getPlayerStorage().isGuiEnabled(player);
        final boolean particles = plugin.getPlayerStorage().areParticlesEnabled(player);

        // Row 3 (slots 18-26): GUI Mode at slot 20, indicator at 29
        inventory.setItem(20, createAdminItem(Material.BOOK, "<yellow>GUI Mode", "toggle_gui", guiEnabled));
        inventory.setItem(29, createStatusIndicator(guiEnabled));

        // Row 4 (slots 27-35): Particles at slot 31, indicator at 40 - leave gap
        inventory.setItem(33, createAdminItem(Material.FIREWORK_STAR, "<yellow>Particles", "toggle_particles", particles));
        inventory.setItem(42, createStatusIndicator(particles));

        player.openInventory(inventory);
    }

    public void openWorldSelectorGui(final Player player) {
        if (player == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<green>" + MessageUtil.ICON_WARP + " World Selector"));

        inventory.setItem(11, createWorldItem(Material.GRASS_BLOCK, "<green>Survival", "world"));
        inventory.setItem(13, createWorldItem(Material.BEACON, "<gold>Lobby", "lobby"));

        inventory.setItem(22, createBackButton("admin"));

        player.openInventory(inventory);
    }

    public void openTeleportMenu(final Player player, final int page) {
        if (player == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 54, MessageUtil.parse("<green>" + MessageUtil.ICON_TP + " Teleport Menu - Page " + page));

        // Top row: dimension teleports (no status indicators needed - these are actions, not toggles)
        inventory.setItem(0, createWorldItem(Material.GRASS_BLOCK, "<green>Overworld", "tp_world_overworld"));
        inventory.setItem(1, createWorldItem(Material.NETHERRACK, "<red>Nether", "tp_world_nether"));
        inventory.setItem(2, createWorldItem(Material.END_STONE, "<purple>End", "tp_world_end"));

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
            if (itemsOnPage >= itemsPerPage - 9) {
                break;
            }

            final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            final ItemMeta meta = head.getItemMeta();
            if (meta == null) {
                continue;
            }
            meta.displayName(MessageUtil.parse("<yellow>" + onlinePlayer.getName()));
            meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_player:" + onlinePlayer.getName());
            final List<Component> lore = new ArrayList<>();
            lore.add(MessageUtil.parse("<gray>Click to teleport to " + onlinePlayer.getName()));
            meta.lore(lore);
            head.setItemMeta(meta);
            inventory.setItem(slot++, head);
            itemsOnPage++;
        }

        final File dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (dataFolder.exists()) {
            final File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (final File file : files) {
                    if (itemsOnPage >= itemsPerPage - 9) {
                        break;
                    }

                    final String fileName = file.getName();
                    final String namePart = fileName.substring(0, fileName.lastIndexOf('_'));
                    final String uuidPart = fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));

                    boolean isOnline = false;
                    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (onlinePlayer.getName().equalsIgnoreCase(namePart)) {
                            isOnline = true;
                            break;
                        }
                    }
                    if (isOnline) {
                        continue;
                    }

                    if (skipped < skipItems) {
                        skipped++;
                        continue;
                    }

                    final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                    final ItemMeta meta = head.getItemMeta();
                    if (meta == null) {
                        continue;
                    }
                    meta.displayName(MessageUtil.parse("<gray>" + namePart + " <dark_gray>(Offline)"));
                    meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_offline:" + uuidPart);
                    final List<Component> lore = new ArrayList<>();
                    lore.add(MessageUtil.parse("<gray>Click to teleport to last known location"));
                    meta.lore(lore);
                    head.setItemMeta(meta);
                    inventory.setItem(slot++, head);
                    itemsOnPage++;
                }
            }
        }

        // Navigation buttons (last row)
        if (page > 1) {
            final ItemStack prev = new ItemStack(Material.ARROW);
            final ItemMeta prevMeta = prev.getItemMeta();
            if (prevMeta != null) {
                prevMeta.displayName(MessageUtil.parse("<green>Previous Page"));
                prevMeta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_page:" + (page - 1));
                prev.setItemMeta(prevMeta);
            }
            inventory.setItem(48, prev);
        }

        final ItemStack back = createBackButton("admin");
        final ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.displayName(MessageUtil.parse("<red>Back"));
            back.setItemMeta(backMeta);
        }
        inventory.setItem(49, back);

        if (slot >= 45 + (page * itemsPerPage)) {
            final ItemStack next = new ItemStack(Material.ARROW);
            final ItemMeta nextMeta = next.getItemMeta();
            if (nextMeta != null) {
                nextMeta.displayName(MessageUtil.parse("<green>Next Page"));
                nextMeta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "tp_page:" + (page + 1));
                next.setItemMeta(nextMeta);
            }
            inventory.setItem(50, next);
        }

        player.openInventory(inventory);
    }

    public void openEnderChestPageSelector(final Player player, final int maxPages) {
        if (player == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest Pages"));
        for (int i = 1; i <= maxPages; i++) {
            inventory.setItem(10 + i, createEcPageItem(i));
        }
        player.openInventory(inventory);
    }

    public void openEnderChestPage(final Player player, final int page) {
        if (player == null) {
            return;
        }
        final Inventory inventory = Bukkit.createInventory(null, 27, MessageUtil.parse("<gold>Ender Chest - Page " + page));
        inventory.setContents(plugin.getPlayerStorage().getEnderChestPage(player, page));
        player.openInventory(inventory);
    }

    private ItemStack createGuiItem(final Material material, final String name, final String type) {
        final ItemStack item = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse(Objects.requireNonNull(name, "Name cannot be null")));
        meta.getPersistentDataContainer().set(UTILITY_KEY, PersistentDataType.STRING, Objects.requireNonNull(type, "Type cannot be null"));
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Click to open!"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAdminItem(final Material material, final String name, final String key, final boolean enabled) {
        final ItemStack item = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse(Objects.requireNonNull(name, "Name cannot be null")));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, Objects.requireNonNull(key, "Key cannot be null"));
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Status: " + (enabled ? "<green>Enabled" : "<red>Disabled")));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createHomeActionItem(final Material material, final String name, final String action, final String homeName) {
        final ItemStack item = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse(Objects.requireNonNull(name, "Name cannot be null")));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "home_action:" + action + ":" + homeName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBackButton(final String target) {
        final ItemStack item = new ItemStack(Material.BARRIER);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse("<red>Back"));
        meta.getPersistentDataContainer().set(ADMIN_KEY, PersistentDataType.STRING, "back_to:" + Objects.requireNonNull(target, "Target cannot be null"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createWorldItem(final Material material, final String name, final String worldName) {
        final ItemStack item = new ItemStack(Objects.requireNonNull(material, "Material cannot be null"));
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse(Objects.requireNonNull(name, "Name cannot be null")));
        meta.getPersistentDataContainer().set(WORLD_KEY, PersistentDataType.STRING, Objects.requireNonNull(worldName, "World name cannot be null"));
        final List<Component> lore = new ArrayList<>();
        lore.add(MessageUtil.parse("<gray>Click to teleport!"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createEcPageItem(final int page) {
        final ItemStack item = new ItemStack(Material.ENDER_CHEST);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse("<green>Page " + page));
        meta.getPersistentDataContainer().set(EC_PAGE_KEY, PersistentDataType.INTEGER, page);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createStatusIndicator(final boolean enabled) {
        final ItemStack item = new ItemStack(enabled ? Material.LIME_CONCRETE : Material.GRAY_CONCRETE);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        meta.displayName(MessageUtil.parse(enabled ? "<green>✔ Enabled" : "<red>✘ Disabled"));
        item.setItemMeta(meta);
        return item;
    }
}
