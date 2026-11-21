package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaComparisonCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.folia.FoliaHelper;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;
import org.bukkit.entity.Mob;

@MythicCondition(
   author = "Ashijin",
   name = "targetWithin",
   aliases = {},
   description = "Tests if the target's target is within a certain distance"
)
public class TargetWithinCondition extends SkillCondition implements ISkillMetaComparisonCondition {
   @MythicField(
      name = "distance",
      aliases = {"d"},
      description = "Distance to check"
   )
   private PlaceholderDouble distance;

   public TargetWithinCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.distance = mlc.getPlaceholderDouble(new String[]{"distance", "d"}, "0", this.conditionVar);
   }

   @Override
   public boolean check(SkillMetadata meta, AbstractEntity entity) {
      if (!entity.isMob()) {
         return true;
      } else {
         double d = this.distance.get(meta, entity);
         double dSq = d * d;
         if (ServerVersion.isFolia() && !FoliaHelper.isOwnedByCurrentRegion(entity.getBukkitEntity())) {
            return Schedulers.of(entity.getBukkitEntity()).supply(() -> {
               Mob cx = (Mob)entity.getBukkitEntity();
               if (cx.getTarget() == null) {
                  return false;
               } else {
                  return cx.getTarget().getWorld().equals(cx.getWorld()) ? cx.getTarget().getLocation().distanceSquared(cx.getLocation()) < dSq : false;
               }
            }).join();
         } else {
            Mob c = (Mob)entity.getBukkitEntity();
            if (c.getTarget() == null) {
               return false;
            } else {
               return c.getTarget().getWorld().equals(c.getWorld()) ? c.getTarget().getLocation().distanceSquared(c.getLocation()) < dSq : false;
            }
         }
      }
   }
}
