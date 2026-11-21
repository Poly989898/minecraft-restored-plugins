package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "shield",
   description = "Shields the target entity for a given number of hearts"
)
public class ShieldMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected static boolean noloop = false;
   protected PlaceholderFloat amount;
   protected PlaceholderFloat maxShield;

   public ShieldMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderFloat(new String[]{"amount", "a"}, 1.0F);
      this.maxShield = mlc.getPlaceholderFloat(new String[]{"maxabsorb", "maxshield", "ma", "ms"}, -1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isDead()) {
         return SkillResult.INVALID_TARGET;
      } else {
         float newAbsorb = MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getEntityAbsorptionHearts(target) + this.amount.get(data, target);
         float maxShield = this.maxShield.get(data, target);
         if (maxShield > 0.0F) {
            newAbsorb = Math.min(newAbsorb, maxShield);
         }

         MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().setEntityAbsorptionHearts(target, newAbsorb);
         return SkillResult.SUCCESS;
      }
   }
}
