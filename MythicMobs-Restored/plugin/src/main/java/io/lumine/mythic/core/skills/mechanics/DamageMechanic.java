package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.damage.DamagingMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "damage",
   aliases = {"d"},
   description = "Deals damage to the target"
)
public class DamageMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The amount of damage to deal",
      defValue = "1"
   )
   protected PlaceholderDouble amount;

   public DamageMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 1.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && (!data.getCaster().isUsingDamageSkill() || this.triggerSkills) && (!target.isLiving() || !(target.getHealth() <= 0.0))) {
         double damage;
         if (this.powerAffectsDamage) {
            damage = this.amount.get(data, target) * data.getPower();
         } else {
            damage = this.amount.get(data, target);
         }

         this.doDamage(data, target, damage);
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamageMechanic fired for {0} with {1} power", damage, data.getPower());
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
