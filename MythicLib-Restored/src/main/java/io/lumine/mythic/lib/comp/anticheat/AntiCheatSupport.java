package io.lumine.mythic.lib.comp.anticheat;

import java.util.Map;
import org.bukkit.entity.Player;

public abstract class AntiCheatSupport {
   public abstract void disableAntiCheat(Player player, Map<CheatType, Integer> map);
}
