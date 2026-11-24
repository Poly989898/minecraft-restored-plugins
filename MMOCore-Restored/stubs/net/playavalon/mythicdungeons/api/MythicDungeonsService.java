package net.playavalon.mythicdungeons.api;

import net.playavalon.mythicdungeons.player.party.partysystem.MythicParty;
import org.bukkit.entity.Player;

public interface MythicDungeonsService {
    MythicParty getParty(Player player);
}
