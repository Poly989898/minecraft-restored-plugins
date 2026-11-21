package io.lumine.mythic.core.skills.targeters;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.projectiles.Projectile;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "projectileforward",
   aliases = {""},
   description = "Targets a point in front of the casting projectile"
)
public class ProjectileForwardTargeter extends ILocationSelector {
   protected PlaceholderDouble forward;
   protected PlaceholderFloat rotate;
   protected boolean rotated = false;

   public ProjectileForwardTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.forward = mlc.getPlaceholderDouble(new String[]{"forward", "f", "amount", "a"}, 1.0);
      this.rotate = mlc.getPlaceholderFloat(new String[]{"rotate", "rot"}, 0.0F);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      if (data.getCallingEvent() instanceof Projectile.ProjectileTracker tracker) {
         AbstractLocation location = tracker.getCurrentLocation().clone();
         AbstractVector direction = tracker.getCurrentVelocity().clone();
         float value = this.rotate.get(data);
         double forward = this.forward.get(data);
         if (value != 0.0F) {
            location.add(direction.rotate(value).normalize().multiply(forward));
         } else {
            location.add(direction.normalize().multiply(forward));
         }

         Collection<AbstractLocation> targets = Lists.newArrayListWithCapacity(1);
         targets.add(this.mutate(data, location));
         return targets;
      } else {
         MythicLogger.debug(MythicLogger.DebugLevel.SKILL_INFO, "ProjectileForwardTargeter called by non-projectile, skipping");
         return Lists.newArrayList();
      }
   }
}
