package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.List;

@MythicMechanic(
   name = "terminateProjectile",
   aliases = {"endprojectile", "terminateproj", "endproj", "stopprojectile", "stopproj"}
)
public class TerminateProjectile extends SkillMechanic implements IMetaSkill {
   private List<SkillCondition> conditions;

   public TerminateProjectile(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      String conditionStr = mlc.getString(new String[]{"conditions", "condition", "cond", "con"});
      if (conditionStr != null) {
         this.conditions = this.getPlugin().getSkillManager().getConditions(conditionStr);
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (data.getCallingEvent() instanceof Projectile.ProjectileTracker projectile) {
         if (projectile.hasTerminated()) {
            return SkillResult.SUCCESS;
         }

         boolean end = true;
         if (this.conditions != null && !this.conditions.isEmpty()) {
            for (SkillCondition condition : this.conditions) {
               if (!condition.evaluateTargets(data)) {
                  end = false;
               }
            }
         }

         if (end) {
            projectile.terminate();
         }
      }

      return SkillResult.SUCCESS;
   }
}
