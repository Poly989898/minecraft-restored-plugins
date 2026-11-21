package io.lumine.mythic.bukkit.compatibility;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.enums.ExpSource;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkillAPISupport {
   private SkillAPI skillapi = (SkillAPI)Bukkit.getPluginManager().getPlugin("SkillAPI");

   public SkillAPI getSkillAPI() {
      return this.skillapi;
   }

   public void giveExp(Player player, int amount) {
      if (player != null) {
         if (amount != 0) {
            SkillAPI.getPlayerAccountData(player).getActiveData().giveExp(amount, ExpSource.MOB);
         }
      }
   }
}
