package io.lumine.mythic.core.skills.audience;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillAudience;
import io.lumine.mythic.core.utils.annotations.MythicAudience;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@MythicAudience(
   name = "execution",
   aliases = {"exec", "inherited"},
   version = "5.7",
   description = "Players from the execution audience set on the skill metadata"
)
public class ExecutionAudience extends SkillAudience {
   public ExecutionAudience(MythicLineConfig mlc) {
      super(mlc);
   }

   @Override
   public Collection<AbstractPlayer> get(SkillMetadata data, AbstractEntity target) {
      if (!data.hasExecutionAudience()) {
         return Lists.newArrayList();
      } else {
         Set<UUID> uuids = data.getExecutionAudience().get();
         ArrayList<AbstractPlayer> players = new ArrayList<>(uuids.size());

         for (UUID uuid : uuids) {
            AbstractPlayer player = getPlugin().getBootstrap().getPlayer(uuid);
            if (player != null) {
               players.add(player);
            }
         }

         return players;
      }
   }
}
