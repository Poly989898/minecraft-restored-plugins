package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   version = "4.11",
   name = "bossBorder",
   aliases = {"effect:bossBorder", "e:bossBorder"},
   description = "Draws a world border to create a boss arena"
)
public class BossBorderEffect extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "radius",
      aliases = {"r"},
      version = "4.11",
      description = "Radius of the border"
   )
   private final PlaceholderInt radius;

   public BossBorderEffect(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.radius = mlc.getPlaceholderInteger(new String[]{"radius", "r"}, 32);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!target.isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else if (data.getCaster() instanceof ActiveMob am) {
         this.getPlugin()
            .getVolatileCodeHandler()
            .getEntityHandler()
            .setPlayerWorldBorder(target.asPlayer(), am.getSpawnLocation(), this.radius.get(data, target));
         return SkillResult.SUCCESS;
      } else {
         return SkillResult.INVALID_TARGET;
      }
   }
}
