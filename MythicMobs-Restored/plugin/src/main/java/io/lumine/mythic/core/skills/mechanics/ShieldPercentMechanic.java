package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "shieldPercent",
   description = "Shields the target entity for a percent of their health"
)
public class ShieldPercentMechanic extends SkillMechanic implements ITargetedEntitySkill {
   protected static boolean noloop = false;
   protected PlaceholderDouble multiplier;
   protected PlaceholderFloat maxShield;

   public ShieldPercentMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.multiplier = mlc.getPlaceholderDouble(new String[]{"multiplier", "m"}, 0.1);
      this.maxShield = mlc.getPlaceholderFloat(new String[]{"maxabsorb", "maxshield", "ma", "ms"}, (float)this.multiplier.get());
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isDead()) {
         return SkillResult.INVALID_TARGET;
      } else {
         double shield = target.getMaxHealth() * this.multiplier.get(data);
         double max = target.getMaxHealth() * this.maxShield.get(data);
         double absorb = MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().getEntityAbsorptionHearts(target) + shield;
         if (absorb < max) {
            MythicBukkit.inst().getVolatileCodeHandler().getEntityHandler().setEntityAbsorptionHearts(target, (float)absorb);
         }

         return SkillResult.SUCCESS;
      }
   }
}
