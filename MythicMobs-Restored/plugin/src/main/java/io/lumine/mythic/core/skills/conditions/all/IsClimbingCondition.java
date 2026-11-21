package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import org.bukkit.entity.LivingEntity;

@MythicCondition(
   author = "Ticxo",
   name = "isClimbing",
   aliases = {"climbing"},
   description = "If the target is climbing"
)
public class IsClimbingCondition extends SkillCondition implements IEntityCondition {
   public IsClimbingCondition(String line, MythicLineConfig mlc) {
      super(line);
   }

   @Override
   public boolean check(AbstractEntity entity) {
      if (entity.getBukkitEntity() instanceof LivingEntity living) {
         return ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(living)
            ? Schedulers.of(living).<Boolean>supply(living::isClimbing).join()
            : living.isClimbing();
      } else {
         return false;
      }
   }
}
