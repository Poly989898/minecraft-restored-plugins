package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setMaxHealth",
   aliases = {"setMaxHp"},
   description = "Sets the target's max health"
)
public class SetMaxHealthMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The max health value used by the mechanic",
      defValue = "1"
   )
   private PlaceholderDouble amount;
   @MythicField(
      name = "mode",
      aliases = {"m"},
      description = "How to apply max health: STATIC or SCALE",
      defValue = "STATIC"
   )
   private SetMaxHealthMechanic.Mode mode;

   public SetMaxHealthMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderDouble(new String[]{"amount", "a"}, 1.0);
      String mode = mlc.getString(new String[]{"mode", "m"}, "STATIC");

      try {
         this.mode = SetMaxHealthMechanic.Mode.valueOf(mode.toUpperCase());
      } catch (Error | Exception e) {
         this.mode = SetMaxHealthMechanic.Mode.STATIC;
         MythicLogger.errorMechanicConfig(this, mlc, "Invalid mode");
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         switch (this.mode) {
            case STATIC:
               target.setMaxHealth(this.amount.get(data, target));
               break;
            case SCALE:
               double newMaxHealth = this.amount.get(data, target);
               double oldHealth = target.getHealth();
               double oldMaxHealth = target.getMaxHealth();
               double percent = oldHealth / oldMaxHealth;
               double newHealth = newMaxHealth * percent;
               target.setMaxHealth(newMaxHealth);
               target.setHealth(newHealth);
         }
      }

      return SkillResult.SUCCESS;
   }

   private static enum Mode {
      STATIC,
      SCALE;
   }
}
