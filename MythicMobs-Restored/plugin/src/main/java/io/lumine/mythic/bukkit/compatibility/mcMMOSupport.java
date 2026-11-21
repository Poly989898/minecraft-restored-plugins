package io.lumine.mythic.bukkit.compatibility;

import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.api.ExperienceAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class mcMMOSupport {
   private mcMMO mcMMO = (mcMMO)Bukkit.getPluginManager().getPlugin("mcMMO");

   public mcMMO getmcMMO() {
      return this.mcMMO;
   }

   public void giveExp(Player player, float amount, String type) {
      if (player != null) {
         if (amount != 0.0F) {
            ExperienceAPI.addRawXP(player, type, amount);
         }
      }
   }
}
