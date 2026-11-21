package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setHealth",
   aliases = {"setHp"},
   description = "Sets the target's health"
)
public class SetHealthMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a"},
      description = "The health value to set on the target",
      defValue = "1"
   )
   private final PlaceholderFloat amount;

   public SetHealthMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.amount = mlc.getPlaceholderFloat(new String[]{"amount", "a"}, 1.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         target.setHealth(this.amount.get(data, target));
      }

      return SkillResult.SUCCESS;
   }

   private static enum Action {
      STATIC,
      PERCENT;
   }
}
