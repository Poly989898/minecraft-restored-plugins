package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;

@MythicTargeter(
   author = "Ashijin",
   name = "PlayersInRing",
   aliases = {"PRING"},
   description = "Targets all players in a ring around the caster"
)
public class PlayersInRingTargeter extends IEntitySelector {
   private final PlaceholderDouble min;
   private final PlaceholderDouble max;

   public PlayersInRingTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.min = mlc.getPlaceholderDouble(new String[]{"min", "minrange"}, 5.0);
      this.max = mlc.getPlaceholderDouble(new String[]{"max", "maxrange"}, 10.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> targets = new HashSet<>();
      double max = Math.pow(this.max.get(data), 2.0);
      double min = Math.pow(this.min.get(data), 2.0);

      for (AbstractPlayer p : am.getEntity().getWorld().getPlayers()) {
         if (am.getLocation().getWorld().equals(p.getWorld())) {
            double d = this.distanceSquared(am.getEntity().getLocation(), p);
            if (d < max && d > min) {
               targets.add(p);
            }
         }
      }

      return targets;
   }
}
