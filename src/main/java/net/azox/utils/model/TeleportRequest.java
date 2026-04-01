package net.azox.utils.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public final class TeleportRequest {
    private final Player requester;
    private final Player target;
    private final boolean here; // if true, requester wants target to tp to them. if false, requester wants to tp to target.
    private final long timestamp;

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > 60000; // 60 seconds expiry
    }
}
