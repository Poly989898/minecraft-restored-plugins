package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.Creature;

@MythicCondition(
   author = "Ashijin",
   name = "targetNotInLineOfSight",
   aliases = {},
   description = "Tests if the target doesn't have line of sight to their target"
)
public class TargetNotInLineOfSightCondition extends SkillCondition implements IEntityCondition {
   public TargetNotInLineOfSightCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (!entity.isCreature()) {
         return true;
      } else {
         Creature c = (Creature)entity.getBukkitEntity();
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(c)) {
            Boolean result = Schedulers.of(c).supply(() -> this.checkNotInLineOfSight(c)).join();
            return result != null ? result : false;
         } else {
            return this.checkNotInLineOfSight(c);
         }
      }
   }

   private boolean checkNotInLineOfSight(Creature c) {
      return c.getTarget() == null ? false : !c.hasLineOfSight(c.getTarget());
   }
}
