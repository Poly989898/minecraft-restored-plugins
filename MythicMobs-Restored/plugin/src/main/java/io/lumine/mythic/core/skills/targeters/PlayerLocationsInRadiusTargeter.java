package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "PlayerLocationsInRadius",
   aliases = {"LocationRadius", "PLIR", "PLR"},
   description = "Targets the locations of all players in a given radius around the caster"
)
public class PlayerLocationsInRadiusTargeter extends ILocationSelector {
   double radius;
   double yOffset;

   public PlayerLocationsInRadiusTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble("radius", 5.0);
      this.radius = mlc.getDouble("r", this.radius);
      this.yOffset = mlc.getDouble("yoffset", 0.0);
      this.yOffset = mlc.getDouble("y", this.yOffset);
   }

   @Override
   public Collection<AbstractLocation> getLocations(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractLocation> targets = new HashSet<>();
      double radiusSq = this.radius * this.radius;

      for (AbstractPlayer p : am.getEntity().getWorld().getPlayers()) {
         if (am.getLocation().getWorld().equals(p.getWorld()) && am.getEntity().getLocation().distanceSquared(p.getLocation()) < radiusSq) {
            AbstractLocation l = p.getLocation();
            l.add(0.0, this.yOffset, 0.0);
            targets.add(l);
         }
      }

      return targets;
   }
}
