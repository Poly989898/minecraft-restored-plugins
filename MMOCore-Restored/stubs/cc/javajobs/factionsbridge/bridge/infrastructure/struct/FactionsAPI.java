package cc.javajobs.factionsbridge.bridge.infrastructure.struct;

import org.bukkit.entity.Player;

public interface FactionsAPI {
    Faction getFaction(Player player);
    FPlayer getFPlayer(Player player);
}
