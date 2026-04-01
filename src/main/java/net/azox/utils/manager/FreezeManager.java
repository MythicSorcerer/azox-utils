package net.azox.utils.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class FreezeManager {
    private final Set<UUID> frozenPlayers = new HashSet<>();

    public void freeze(final UUID uuid) {
        frozenPlayers.add(uuid);
    }

    public void unfreeze(final UUID uuid) {
        frozenPlayers.remove(uuid);
    }

    public boolean isFrozen(final UUID uuid) {
        return frozenPlayers.contains(uuid);
    }

    public void toggleFreeze(final UUID uuid) {
        if (isFrozen(uuid)) unfreeze(uuid);
        else freeze(uuid);
    }
}
