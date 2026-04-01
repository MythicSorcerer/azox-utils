package net.azox.utils.manager;

import net.azox.utils.AzoxUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class ParticleManager {

    private final AzoxUtils plugin = AzoxUtils.getInstance();
    private final Map<UUID, Particle> activeParticles = new HashMap<>();

    public ParticleManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (player == null) {
                        continue;
                    }
                    if (!plugin.getPlayerStorage().areParticlesEnabled(player)) {
                        continue;
                    }

                    final Particle particle = getPlayerParticle(player);
                    if (particle != null) {
                        // Use particles that don't require data
                        player.getWorld().spawnParticle(particle, player.getLocation().add(0, 3.2, 0), 1, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
        }.runTaskTimer(AzoxUtils.getInstance(), 0L, 20L);
    }

    public void setParticle(final UUID uuid, final Particle particle) {
        if (uuid == null) {
            return;
        }
        if (particle == null) {
            activeParticles.remove(uuid);
        } else {
            activeParticles.put(uuid, particle);
        }
    }

    public Particle getPlayerParticle(final Player player) {
        if (player == null) {
            return null;
        }
        for (int i = 100; i > 0; i--) {
            if (player.hasPermission("azox.util.particles." + i)) {
                return getParticleForLevel(i);
            }
        }
        return null;
    }

    private Particle getParticleForLevel(final int level) {
        // Use only particles that don't require data
        if (level >= 100) {
            return Particle.END_ROD;
        } else if (level >= 75) {
            return Particle.HAPPY_VILLAGER;
        } else if (level >= 50) {
            return Particle.FLAME;
        } else if (level >= 25) {
            return Particle.HEART;
        }
        return Particle.SMOKE;
    }

    public boolean hasParticle(final UUID uuid) {
        return uuid != null && activeParticles.containsKey(uuid);
    }

    public void removeParticle(final UUID uuid) {
        if (uuid == null) {
            return;
        }
        activeParticles.remove(uuid);
    }
}
