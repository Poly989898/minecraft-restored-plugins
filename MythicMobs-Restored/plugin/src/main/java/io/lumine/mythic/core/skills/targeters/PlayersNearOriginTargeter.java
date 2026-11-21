package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;

@MythicTargeter(
   author = "Ashijin",
   name = "PlayersNearOrigin",
   aliases = {"PlayersNearSource", "PNO"},
   description = "Targets all players in the given radius around the origin of the metaskill"
)
public class PlayersNearOriginTargeter extends IEntitySelector {
   private double radius;

   public PlayersNearOriginTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
      this.radius = mlc.getDouble(new String[]{"radius", "r"}, 5.0);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      ObjectArrayList<AbstractEntity> targets = new ObjectArrayList();
      double range = Math.pow(this.radius, 2.0);
      AbstractWorld originWorld = data.getCaster().getLocation().getWorld();

      for (AbstractPlayer player : data.getCaster().getEntity().getWorld().getPlayers()) {
         if (originWorld.equals(player.getWorld()) && this.distanceSquared(data.getOrigin(), player) < range) {
            targets.add(player);
         }
      }

      return targets;
   }
}
