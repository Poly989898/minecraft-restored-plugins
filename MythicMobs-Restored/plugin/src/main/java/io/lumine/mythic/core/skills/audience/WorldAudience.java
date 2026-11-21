package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.adapters.AbstractWorld;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.utils.annotations.MythicAudience;
import java.util.Collection;
import java.util.List;

@MythicAudience(
   name = "world",
   aliases = {},
   version = "4.11",
   description = "All players in the world"
)
public class WorldAudience extends SkillAudience {
   public WorldAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> targets = Lists.newArrayList();
      AbstractWorld world;
      if (data == null) {
         world = target.getWorld();
      } else {
         world = data.getCaster().getEntity().getWorld();
      }

      if (world == null) {
         return targets;
      } else {
         List<AbstractPlayer> players = world.getPlayers();
         if (players == null) {
            return targets;
         } else {
            targets.addAll(players);
            return targets;
         }
      }
   }
}
