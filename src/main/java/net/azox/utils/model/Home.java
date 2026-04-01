package net.azox.utils.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public final class Home {
    private UUID ownerUuid;
    private String name;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private boolean isPublic;
    private String description;
    private long creationDate;

    public Location getLocation() {
        if (this.worldName == null) {
            return null;
        }
        final World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public void setLocation(final Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }
        this.worldName = location.getWorld().getName();
        this.x = Math.round(location.getX() * 100.0) / 100.0;
        this.y = Math.round(location.getY() * 100.0) / 100.0;
        this.z = Math.round(location.getZ() * 100.0) / 100.0;
        this.yaw = (float) (Math.round(location.getYaw() * 100.0) / 100.0);
        this.pitch = (float) (Math.round(location.getPitch() * 100.0) / 100.0);
    }
}
