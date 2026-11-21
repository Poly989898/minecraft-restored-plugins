package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "setpose",
   description = "Sets the entity's pose"
)
public class SetPoseMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "pose",
      aliases = {"p"},
      description = "The pose to set on the target entity"
   )
   private PlaceholderString pose;

   public SetPoseMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.pose = mlc.getPlaceholderString(new String[]{"pose", "p"}, null);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      String pose = this.pose == null ? null : this.pose.get(data, target);
      this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityPose(target, pose);
      return SkillResult.SUCCESS;
   }
}
