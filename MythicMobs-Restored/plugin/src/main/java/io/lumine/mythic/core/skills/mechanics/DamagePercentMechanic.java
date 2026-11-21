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
   name = "damagePercent",
   aliases = {"percentDamage"},
   description = "Deals a percentage of the target's health in damage"
)
public class DamagePercentMechanic extends DamagingMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "percent",
      aliases = {"p"},
      description = "The percentage of the target's health",
      defValue = "0.1"
   )
   protected final PlaceholderDouble percent;
   @MythicField(
      name = "currentHealth",
      aliases = {"current", "ch", "c"},
      description = "Whether to calculate the damage using the target's current health or max health",
      defValue = "false"
   )
   protected boolean currentHealth;

   public DamagePercentMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.percent = mlc.getPlaceholderDouble(new String[]{"percent", "p"}, 0.1);
      this.currentHealth = mlc.getBoolean(new String[]{"currentHealth", "current", "ch", "c"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isDead() && !(target.getHealth() <= 0.0)) {
         double damage;
         if (this.powerAffectsDamage) {
            if (this.currentHealth) {
               damage = target.getHealth() * this.percent.get(data, target) * this.power;
            } else {
               damage = target.getMaxHealth() * this.percent.get(data, target) * this.power;
            }
         } else if (this.currentHealth) {
            damage = target.getHealth() * this.percent.get(data, target);
         } else {
            damage = target.getMaxHealth() * this.percent.get(data, target);
         }

         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ DamagePercentMechanic fired for {0} with {1} power", damage, data.getPower());
         this.doDamage(data, target, damage);
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
