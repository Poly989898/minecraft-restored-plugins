package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Merchant;

@MythicMechanic(
   author = "Seyarada",
   name = "opentrades",
   aliases = {"opentrade", "trade"},
   description = "Delays the execution of the next mechanic"
)
public class OpenTradesMechanic extends SkillMechanic implements ITargetedEntitySkill {
   boolean realTrade;

   public OpenTradesMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.realTrade = mlc.getBoolean(new String[]{"realTrade", "real"}, true);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.getBukkitEntity() instanceof Player player && data.getCaster().getEntity().getBukkitEntity() instanceof Villager villager) {
         Schedulers.runOnRegion(target.getBukkitEntity(), () -> {
            if (this.realTrade) {
               player.openMerchant(villager, true);
            } else {
               Merchant merchant = Bukkit.createMerchant(villager.customName());
               merchant.setRecipes(villager.getRecipes());
               player.openMerchant(merchant, true);
            }
         });
      }

      return SkillResult.SUCCESS;
   }
}
