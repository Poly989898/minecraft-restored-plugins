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
   name = "effect:thunderlevel",
   aliases = {"thunderlevel", "e:thunderlevel"},
   description = "Modifies the skybox for the target player"
)
public class ThunderLevelEffect extends SkillMechanic implements ITargetedEntitySkill {
   protected PlaceholderInt type;

   public ThunderLevelEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.type = PlaceholderInt.of(mlc.getString(new String[]{"level", "lvl", "l", "type", "t", "environment", "env", "e"}, "0"));
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().sendGameStateChange(target.asPlayer(), 8, this.type.get(data, target));
         return SkillResult.SUCCESS;
      }
   }
}
