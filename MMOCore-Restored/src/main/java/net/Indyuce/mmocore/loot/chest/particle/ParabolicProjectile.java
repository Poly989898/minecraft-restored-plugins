package net.Indyuce.mmocore.loot.chest.particle;

import io.lumine.mythic.lib.version.VParticle;
import java.util.function.Consumer;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParabolicProjectile extends BukkitRunnable {
   private final Location target;
   private final Consumer<Location> display;
   private final Vector vec;
   private final Runnable end;
   private final int speed;
   private final Location loc;
   private int j;

   public ParabolicProjectile(Location source, Location target, Color color) {
      this(source, target, VParticle.REDSTONE.get(), () -> {}, 1, color, 1.0F);
   }

   public ParabolicProjectile(Location source, Location target, Particle particle, Runnable end, int speed, Color color, float size) {
      this(
         source,
         target,
         target.clone().subtract(source).toVector().multiply(0.1).setY(6).normalize().multiply(0.3),
         end,
         speed,
         loc -> loc.getWorld().spawnParticle(particle, loc, 1, new DustOptions(color, size))
      );
   }

   public ParabolicProjectile(Location source, Location target, Runnable end, Color color) {
      this(
         source,
         target,
         target.clone().subtract(source).toVector().multiply(0.1).setY(6).normalize().multiply(0.3),
         end,
         1,
         loc -> loc.getWorld().spawnParticle(VParticle.REDSTONE.get(), loc, 1, new DustOptions(color, 1.0F))
      );
   }

   public ParabolicProjectile(Location source, Location target, Particle particle) {
      this(
         source,
         target,
         target.clone().subtract(source).toVector().multiply(0.1).setY(6).normalize().multiply(0.3),
         () -> {},
         1,
         loc -> loc.getWorld().spawnParticle(particle, loc, 0, CastingParticle.defaultData(particle))
      );
   }

   public ParabolicProjectile(Location source, Location target, Runnable end, int speed, Particle particle) {
      this(
         source,
         target,
         target.clone().subtract(source).toVector().multiply(0.1).setY(6).normalize().multiply(0.3),
         end,
         speed,
         loc -> loc.getWorld().spawnParticle(particle, loc, 0, CastingParticle.defaultData(particle))
      );
   }

   public ParabolicProjectile(Location source, Location target, Vector vec, Runnable end, int speed, Particle particle) {
      this(source, target, vec, end, speed, loc -> loc.getWorld().spawnParticle(particle, loc, 0, CastingParticle.defaultData(particle)));
   }

   private ParabolicProjectile(Location source, Location target, Vector vec, Runnable end, int speed, Consumer<Location> display) {
      this.loc = source.clone();
      this.target = target;
      this.display = display;
      this.end = end;
      this.vec = vec;
      this.speed = Math.max(1, speed);
      this.runTaskTimer(MMOCore.plugin, 0L, 1L);
   }

   public void run() {
      for (int k = 0; k < this.speed; k++) {
         if (this.j++ > 100 || this.loc.distanceSquared(this.target) < 0.8) {
            this.end.run();
            this.cancel();
         }

         double c = Math.min(1.0, this.j / 40.0);
         this.display.accept(this.loc.add(this.target.clone().subtract(this.loc).toVector().normalize().multiply(c).add(this.vec.clone().multiply(1.0 - c))));
      }
   }
}
