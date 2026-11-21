package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

@MythicCondition(
   author = "Ashijin",
   name = "targetInLineOfSight",
   aliases = {},
   description = "Tests if the target has line of sight to their target"
)
public class TargetInLineOfSightCondition extends SkillCondition implements IEntityCondition {
   public TargetInLineOfSightCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof Mob mob) {
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(mob)) {
            Boolean result = Schedulers.of(mob).supply(() -> this.checkTargetLineOfSight(entity, mob)).join();
            return result != null ? result : false;
         } else {
            return this.checkTargetLineOfSight(entity, mob);
         }
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Entity is not a mob");
         return true;
      }
   }

   private boolean checkTargetLineOfSight(AbstractEntity entity, Mob mob) {
      LivingEntity target = mob.getTarget();
      if (target == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "Entity does not have a target");
         return false;
      } else {
         boolean bool = mob.hasLineOfSight(target);
         MythicLogger.debug(MythicLogger.DebugLevel.CONDITION, "{0} LOS to {1} == {2}", entity.getUniqueId(), target.getUniqueId(), bool);
         return bool;
      }
   }
}
