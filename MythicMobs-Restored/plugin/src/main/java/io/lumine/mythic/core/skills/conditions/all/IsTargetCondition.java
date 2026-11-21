package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

@MythicCondition(
   author = "Ashijin",
   name = "target",
   aliases = {"isTarget"},
   description = "Checks if the target entity is the current target of the caster mob"
)
public class IsTargetCondition extends SkillCondition implements IEntityComparisonCondition {
   public IsTargetCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      if (entity.getBukkitEntity() instanceof Mob mob) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(mob)) {
            Boolean result = Schedulers.of(mob).supply(() -> this.checkTarget(mob, target)).join();
            return result != null ? result : false;
         } else {
            return this.checkTarget(mob, target);
         }
      } else {
         return false;
      }
   }

   private boolean checkTarget(Mob mob, AbstractEntity target) {
      LivingEntity mobTarget = mob.getTarget();
      return mobTarget != null && mobTarget.getUniqueId().equals(target.getUniqueId());
   }
}
