package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "projectileHasEnded",
   description = "Checks if the calling projectile has ended"
)
public class ProjectileHasEndedCondition extends SkillCondition implements ISkillMetaCondition {
   public ProjectileHasEndedCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(SkillMetadata meta) {
      return meta.getCallingEvent() instanceof Projectile.ProjectileTracker projectileTracker ? projectileTracker.getCancelled() : false;
   }
}
