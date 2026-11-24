package de.erethon.dungeonsxl.api.player;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;

public interface PlayerCollection {
    int size();
    boolean contains(Object o);
    Collection<Player> getOnlinePlayers();
    Set<UUID> getUniqueIds();
}
