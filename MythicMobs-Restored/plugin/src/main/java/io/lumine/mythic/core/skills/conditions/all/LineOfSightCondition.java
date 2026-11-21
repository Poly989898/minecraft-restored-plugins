package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(
   author = "Ashijin",
   name = "lineOfSight",
   aliases = {"inlineofsight"},
   description = "Tests if the target is within line of sight of the caster"
)
public class LineOfSightCondition extends SkillCondition implements IEntityComparisonCondition {
   public LineOfSightCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity, AbstractEntity target) {
      return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())
         ? Schedulers.of(entity.getBukkitEntity()).supply(() -> entity.hasLineOfSight(target)).join()
         : entity.hasLineOfSight(target);
   }
}
