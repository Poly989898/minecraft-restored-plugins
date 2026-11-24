package net.playavalon.mythicdungeons.api.party;

import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface IDungeonParty {
    default void initDungeonParty(Plugin plugin) { throw new UnsupportedOperationException("compile-only stub"); }
    void addPlayer(Player player);
    void removePlayer(Player player);
    List<Player> getPlayers();
    OfflinePlayer getLeader();
}
