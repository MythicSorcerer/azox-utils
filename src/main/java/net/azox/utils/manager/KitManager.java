package net.azox.utils.manager;

import net.azox.utils.model.Kit;
import net.azox.utils.storage.KitStorage;
import net.azox.utils.util.MessageUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KitManager {

    @Getter
    private final KitStorage storage;
    private final Map<String, Kit> cachedKits;
    private final Map<UUID, Map<String, Long>> kitCooldowns = new ConcurrentHashMap<>();

    public KitManager() {
        this.storage = new KitStorage();
        this.cachedKits = new ConcurrentHashMap<>(storage.getKits());
    }

    public Optional<Kit> getKit(final String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cachedKits.get(name.toLowerCase()));
    }

    public void createKit(final String name, final ItemStack[] contents, final long cooldown) {
        if (name == null || contents == null) {
            return;
        }
        final Kit kit = new Kit(name.toLowerCase(), contents, cooldown);
        cachedKits.put(name.toLowerCase(), kit);
        storage.saveKit(kit);
    }

    public void deleteKit(final String name) {
        if (name == null) {
            return;
        }
        cachedKits.remove(name.toLowerCase());
        storage.deleteKit(name);
    }

    public void giveKit(final Player player, final Kit kit) {
        if (player == null || kit == null) {
            return;
        }
        if (!player.hasPermission("azox.util.kit." + kit.getName()) && !player.hasPermission("azox.util.kit.*")) {
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " You don't have permission for this kit!");
            return;
        }

        final long now = System.currentTimeMillis();
        final Map<String, Long> playerCooldowns = kitCooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        final long lastUsed = playerCooldowns.getOrDefault(kit.getName(), 0L);
        final long cooldownMillis = kit.getCooldown() * 1000;

        if (now - lastUsed < cooldownMillis && !player.hasPermission("azox.util.kit.bypass")) {
            final long remaining = (cooldownMillis - (now - lastUsed)) / 1000;
            MessageUtil.sendMessage(player, "<red>" + MessageUtil.ICON_ERROR + " Kit on cooldown! Wait " + remaining + "s.");
            return;
        }

        for (final ItemStack item : kit.getContents()) {
            if (item != null) {
                player.getInventory().addItem(item.clone());
            }
        }

        playerCooldowns.put(kit.getName(), now);
        MessageUtil.sendMessage(player, "<green>" + MessageUtil.ICON_SUCCESS + " Received kit " + kit.getName() + "!");
    }

    public Map<String, Kit> getKits() {
        return cachedKits;
    }
}
