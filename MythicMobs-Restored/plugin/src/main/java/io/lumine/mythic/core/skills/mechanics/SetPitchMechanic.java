package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setpitch",
   aliases = {},
   description = "Modifies the head pitch of the target entity"
)
public class SetPitchMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "amount",
      aliases = {"a", "pitch", "p"},
      description = "Pitch value to set on the target",
      defValue = "0"
   )
   private final PlaceholderFloat amount;

   public SetPitchMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.amount = mlc.getPlaceholderFloat(new String[]{"amount", "a", "pitch", "p"}, 0.0F);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (target.isLiving()) {
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityPitch(target, this.amount.get(data));
      }

      return SkillResult.SUCCESS;
   }
}
