package io.lumine.mythic.api.volatilecode.handlers;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ParticleVisibility;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.ConfigExecutor;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public interface VolatileParticleHandler {
   void sendParticle(
      Collection<? extends Player> collection,
      Particle particle,
      Object obj,
      double value,
      double value2,
      double value3,
      int value4,
      float value5,
      float value6,
      float value7,
      float value8,
      boolean flag
   );

   ParticleBundle openBundle(Collection<? extends Player> collection, int value);

   default void flushTick() {
   }

   static boolean shouldDispatchTo(Player p) {
      ConfigExecutor cfg = MythicBukkit.inst().getConfiguration();
      if (!cfg.isRespectClientParticleVisibility()) {
         return true;
      } else {
         ParticleVisibility v;
         try {
            v = p.getClientOption(ClientOption.PARTICLE_VISIBILITY);
         } catch (Throwable e) {
            return true;
         }
         int pct = switch (v) {
            case ALL -> cfg.getParticleVisibilityAll();
            case DECREASED -> cfg.getParticleVisibilityDecreased();
            case MINIMAL -> cfg.getParticleVisibilityMinimal();
            default -> throw new MatchException(null, null);
         };
         if (pct >= 100) {
            return true;
         } else {
            return pct <= 0 ? false : ThreadLocalRandom.current().nextInt(100) < pct;
         }
      }
   }
}
