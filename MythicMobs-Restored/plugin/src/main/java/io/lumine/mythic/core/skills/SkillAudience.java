package io.lumine.mythic.core.skills;

import com.google.common.collect.Lists;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public abstract class SkillAudience {
   protected static MythicPlugin getPlugin() {
      return MythicProvider.get();
   }

   public SkillAudience(MythicLineConfig mlc) {
   }

   public abstract Collection<AbstractPlayer> get(SkillMetadata skillMetadata, AbstractEntity abstractEntity);

   public Collection<AbstractPlayer> getEffective(SkillMetadata data, AbstractEntity target) {
      Collection<AbstractPlayer> resolved = this.get(data, target);
      if (!data.hasExecutionAudience()) {
         return resolved;
      } else {
         Set<UUID> execAudience = data.getExecutionAudience().get();
         if (resolved != null && !resolved.isEmpty()) {
            ArrayList<AbstractPlayer> effective = new ArrayList<>(resolved.size());

            for (AbstractPlayer player : resolved) {
               if (execAudience.contains(player.getUniqueId())) {
                  effective.add(player);
               }
            }

            return effective;
         } else {
            return Lists.newArrayList();
         }
      }
   }

   public static Collection<AbstractPlayer> getEffective(@Nullable SkillAudience audience, SkillMetadata data, @Nullable AbstractEntity target) {
      if (audience == null) {
         return (Collection<AbstractPlayer>)(data.hasExecutionAudience() ? resolvePlayersFromUuids(data.getExecutionAudience().get()) : Lists.newArrayList());
      } else {
         return audience.getEffective(data, target);
      }
   }

   private static Collection<AbstractPlayer> resolvePlayersFromUuids(Set<UUID> uuids) {
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
