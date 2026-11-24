package io.lumine.mythic.lib.comp.anticheat;

import java.util.Map;
import java.util.Map.Entry;
import me.vagdedes.spartan.api.API;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;

public class SpartanPlugin extends AntiCheatSupport {
   @Override
   public void disableAntiCheat(Player player, Map<CheatType, Integer> map) {
      for (Entry<CheatType, Integer> entry : map.entrySet()) {
         API.cancelCheck(player, this.fromCheatType(entry.getKey()), entry.getValue());
      }
   }

   private HackType fromCheatType(CheatType cheatType) {
      return HackType.valueOf(cheatType.toSpartan());
   }
}
