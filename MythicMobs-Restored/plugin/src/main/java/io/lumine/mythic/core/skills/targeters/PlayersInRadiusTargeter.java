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
   name = "playersInRadius",
   aliases = {"PIR"},
   description = "Targets the players in a radius around the caster"
)
public class PlayersInRadiusTargeter extends IEntitySelector {
   private PlaceholderDouble radius;

   public PlayersInRadiusTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = PlaceholderDouble.of(mlc.getString(new String[]{"radius", "r"}, "5"));
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      SkillCaster am = data.getCaster();
      HashSet<AbstractEntity> targets = new HashSet<>();
      double radiusSq = this.radius.get(data);
      radiusSq *= radiusSq;

      for (AbstractPlayer p : am.getEntity().getWorld().getPlayers()) {
         if (p.getWorld().equals(am.getEntity().getWorld()) && this.distanceSquared(am.getEntity().getLocation(), p) < radiusSq) {
            targets.add(p);
         }
      }

      return targets;
   }
}
