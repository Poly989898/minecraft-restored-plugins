package io.lumine.mythic.core.volatilecode.disabled;

import io.lumine.mythic.api.volatilecode.handlers.ParticleBundle;
import io.lumine.mythic.api.volatilecode.handlers.VolatileParticleHandler;
import java.util.Collection;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class VolatileParticleHandlerDisabled implements VolatileParticleHandler {
   @Override
   public void sendParticle(
      Collection<? extends Player> audience,
      Particle particle,
      Object data,
      double x,
      double y,
      double z,
      int count,
      float offsetX,
      float offsetY,
      float offsetZ,
      float speed,
      boolean force
   ) {
      if (particle != null) {
         for (Player p : audience) {
            p.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, speed, data, force);
         }
      }
   }

   @Override
   public ParticleBundle openBundle(Collection<? extends Player> audience, int bundleSize) {
      final Collection<? extends Player> ref = audience;
      return new ParticleBundle() {
         @Override
         public void add(
            Particle particle, Object data, double x, double y, double z, int count, float offsetX, float offsetY, float offsetZ, float speed, boolean force
         ) {
            VolatileParticleHandlerDisabled.this.sendParticle(ref, particle, data, x, y, z, count, offsetX, offsetY, offsetZ, speed, force);
         }

         @Override
         public void close() {
         }
      };
   }
}
