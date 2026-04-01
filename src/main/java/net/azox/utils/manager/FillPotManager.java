package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class FillPotManager {

    private static final int REFILL_INTERVAL_TICKS = 200;

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, List<SavedPotionSlot>> savedSlots = new ConcurrentHashMap<>();

    public FillPotManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : savedSlots.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline() && plugin.getPlayerStorage().isFillPotEnabled(player)) {
                        refillPotions(player);
                    }
                }
            }
        }.runTaskTimer(plugin, REFILL_INTERVAL_TICKS, REFILL_INTERVAL_TICKS);
    }

    public void loadPlayerData(Player player) {
        if (plugin.getPlayerStorage().hasSavedPotions(player)) {
            List<String> savedData = plugin.getPlayerStorage().getSavedPotions(player);
            List<SavedPotionSlot> slots = new ArrayList<>();
            for (String entry : savedData) {
                String[] parts = entry.split(":", 2);
                if (parts.length == 2) {
                    try {
                        int slot = Integer.parseInt(parts[0]);
                        ItemStack item = deserializeItemStack(parts[1]);
                        if (item != null) {
                            slots.add(new SavedPotionSlot(slot, item.clone()));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            savedSlots.put(player.getUniqueId(), slots);
        }
    }

    public void unloadPlayerData(UUID uuid) {
        savedSlots.remove(uuid);
    }

    public void savePlayerPotions(Player player) {
        List<SavedPotionSlot> slots = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && isPotion(item.getType())) {
                slots.add(new SavedPotionSlot(i, item.clone()));
            }
        }
        savedSlots.put(player.getUniqueId(), slots);
    }

    private void refillPotions(Player player) {
        List<SavedPotionSlot> slots = savedSlots.get(player.getUniqueId());
        if (slots == null) return;

        for (SavedPotionSlot slot : slots) {
            ItemStack current = player.getInventory().getItem(slot.slot);
            if (current == null || current.getAmount() < slot.item.getAmount()) {
                player.getInventory().setItem(slot.slot, slot.item.clone());
            }
        }
    }

    private boolean isPotion(Material material) {
        return material == Material.POTION ||
               material == Material.SPLASH_POTION ||
               material == Material.LINGERING_POTION;
    }

    private ItemStack deserializeItemStack(String data) {
        try {
            String[] parts = data.split(",");
            Material material = Material.getMaterial(parts[0]);
            if (material == null) return null;

            int amount = Integer.parseInt(parts[1]);
            short durability = Short.parseShort(parts[2]);

            ItemStack item = new ItemStack(material, amount, durability);

            if (parts.length > 3) {
                org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    for (int i = 3; i < parts.length; i++) {
                        if (parts[i].startsWith("name:")) {
                            meta.setDisplayName(parts[i].substring(5).replace(";", ","));
                        } else if (parts[i].startsWith("lore:")) {
                            String loreStr = parts[i].substring(5);
                            List<String> lore = new ArrayList<>();
                            for (String line : loreStr.split("\\|")) {
                                lore.add(line.replace(";", ","));
                            }
                            meta.setLore(lore);
                        }
                    }
                    item.setItemMeta(meta);
                }
            }

            return item;
        } catch (Exception e) {
            return null;
        }
    }

    public static class SavedPotionSlot {
        public final int slot;
        public final ItemStack item;

        public SavedPotionSlot(int slot, ItemStack item) {
            this.slot = slot;
            this.item = item;
        }
    }
}
