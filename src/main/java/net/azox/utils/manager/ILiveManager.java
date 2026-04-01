package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ILiveManager {

    private static final Set<Material> HEALING_FOODS = new HashSet<>(Arrays.asList(
            Material.COOKED_COD,
            Material.BREAD,
            Material.BAKED_POTATO,
            Material.COOKED_MUTTON,
            Material.COOKED_SALMON,
            Material.COOKED_CHICKEN,
            Material.PUMPKIN_PIE,
            Material.COOKED_PORKCHOP,
            Material.COOKED_BEEF,
            Material.GOLDEN_CARROT,
            Material.GOLDEN_APPLE,
            Material.ENCHANTED_GOLDEN_APPLE
    ));

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, ILiveData> ilivePlayers = new ConcurrentHashMap<>();

    public ILiveManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : ilivePlayers.keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null && player.isOnline()) {
                        processILivePlayer(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 100L);
    }

    public void enableILive(Player player, String mode, int damageReduction, boolean foodHeal) {
        ilivePlayers.put(player.getUniqueId(), new ILiveData(mode, damageReduction, foodHeal));
        plugin.getPlayerStorage().setILiveEnabled(player, true);
        plugin.getPlayerStorage().setILiveMode(player, mode);
        plugin.getPlayerStorage().setILiveDamageReduction(player, damageReduction);
        plugin.getPlayerStorage().setILiveFoodHeal(player, foodHeal);
    }

    public void disableILive(Player player) {
        ilivePlayers.remove(player.getUniqueId());
        plugin.getPlayerStorage().setILiveEnabled(player, false);
    }

    public boolean isEnabled(Player player) {
        return ilivePlayers.containsKey(player.getUniqueId());
    }

    public ILiveData getData(Player player) {
        return ilivePlayers.get(player.getUniqueId());
    }

    public void loadPlayerData(Player player) {
        if (plugin.getPlayerStorage().isILiveEnabled(player)) {
            String mode = plugin.getPlayerStorage().getILiveMode(player);
            int damageReduction = plugin.getPlayerStorage().getILiveDamageReduction(player);
            boolean foodHeal = plugin.getPlayerStorage().isILiveFoodHealEnabled(player);
            if (mode == null) mode = "nototem";
            if (damageReduction < 0) damageReduction = 1;
            ilivePlayers.put(player.getUniqueId(), new ILiveData(mode, damageReduction, foodHeal));
        }
    }

    private void processILivePlayer(Player player) {
        ILiveData data = getData(player);
        if (data == null) return;

        if (data.foodHeal && player.getHealth() < 6.0 && player.getFoodLevel() > 0) {
            healWithFood(player);
        }
    }

    private void healWithFood(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && HEALING_FOODS.contains(item.getType())) {
                player.getInventory().setItem(i, item.getAmount() > 1 ? 
                        new ItemStack(item.getType(), item.getAmount() - 1) : null);
                int newFood = Math.min(20, player.getFoodLevel() + 3);
                player.setFoodLevel(newFood);
                break;
            }
        }
    }

    public double getDamageMultiplier(Player player) {
        ILiveData data = getData(player);
        if (data == null) return 1.0;
        int reduction = data.damageReduction;
        return 1.0 - (reduction * 0.2);
    }

    public boolean isNoTotemMode(Player player) {
        ILiveData data = getData(player);
        return data != null && "nototem".equals(data.mode);
    }

    public static class ILiveData {
        public final String mode;
        public final int damageReduction;
        public final boolean foodHeal;

        public ILiveData(String mode, int damageReduction, boolean foodHeal) {
            this.mode = mode;
            this.damageReduction = Math.max(0, Math.min(5, damageReduction));
            this.foodHeal = foodHeal;
        }
    }
}
