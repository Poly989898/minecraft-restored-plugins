package io.lumine.mythic.lib.api;

import io.lumine.mythic.lib.version.VParticle;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

@Deprecated
public class MMORayTraceResult {
   private final LivingEntity entity;
   private final double range;

   public MMORayTraceResult(LivingEntity entity, double range) {
      this.range = range;
      this.entity = entity;
   }

   public boolean hasHit() {
      return this.entity != null;
   }

   public LivingEntity getHit() {
      return this.entity;
   }

   public double getRange() {
      return this.range;
   }

   public void draw(Location source, Vector vec, double c, Color color) {
      this.draw(source, vec, c, loc -> loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), loc, 0, new DustOptions(Color.RED, 1.0F)));
   }

   public void draw(Location loc, Vector vec, double c, Consumer<Location> tick) {
      vec = vec.multiply(1.0 / c);

      for (int j = 0; j < this.range * c; j++) {
         tick.accept(loc.add(vec));
      }
   }
}
