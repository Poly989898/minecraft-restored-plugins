package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setFlying",
   description = "Sets whether the player is flying"
)
public class SetFlyingMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "flying",
      aliases = {"f", "state", "value"},
      description = "Whether the target player should be flying",
      defValue = "false"
   )
   protected boolean state;

   public SetFlyingMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.state = mlc.getBoolean(new String[]{"flying", "f", "state", "value"}, false);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         target.asPlayer().setAllowFlight(this.state);
         target.asPlayer().setFlying(this.state);
         return SkillResult.SUCCESS;
      }
   }
}
