package io.lumine.mythic.api.volatilecode.handlers;

import java.util.Collection;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public interface ParticleBundle extends AutoCloseable {
   void add(Particle particle, Object obj, double value, double value2, double value3, int value4, float value5, float value6, float value7, float value8, boolean flag);

   @Override
   void close();

   static ParticleBundle direct(final VolatileParticleHandler handler, final Collection<? extends Player> audience) {
      return new ParticleBundle() {
         @Override
         public void add(
            Particle particle, Object data, double x, double y, double z, int count, float offsetX, float offsetY, float offsetZ, float speed, boolean force
         ) {
            handler.sendParticle(audience, particle, data, x, y, z, count, offsetX, offsetY, offsetZ, speed, force);
         }

         @Override
         public void close() {
         }
      };
   }
}
