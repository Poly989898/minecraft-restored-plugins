package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "effect:skybox",
   aliases = {"skybox", "e:skybox"},
   description = "Modifies the skybox for the target player"
)
public class SkyboxEffect extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderInt type;

   public SkyboxEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.type = PlaceholderInt.of(mlc.getString(new String[]{"skybox", "sky", "s", "type", "t", "environment", "env", "e"}, "0"));
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().sendGameStateChange(target.asPlayer(), 7, this.type.get(data, target));
         return SkillResult.SUCCESS;
      }
   }
}
