package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
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
   name = "NearestPlayer",
   aliases = {},
   description = "Targets the nearest player in a radius"
)
public class NearestPlayerTargeter extends IEntitySelector {
   private double radius;

   public NearestPlayerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> targets = new HashSet<>();
      AbstractEntity nearest = null;
      double distSqr = Math.pow(this.radius, 2.0);

      for (AbstractPlayer p : am.getEntity().getWorld().getPlayers()) {
         if (!p.getUniqueId().equals(am.getEntity().getUniqueId()) && p.getWorld().equals(am.getEntity().getWorld())) {
            double d = this.distanceSquared(am.getEntity().getLocation(), p);
            if (d < distSqr) {
               nearest = p;
               distSqr = d;
            }
         }
      }

      if (nearest != null) {
         targets.add(nearest);
      }

      return targets;
   }
}
