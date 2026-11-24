package net.Indyuce.mmocore.loot.chest.particle;

import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class SmallParticleEffect extends BukkitRunnable {
   private final Location loc;
   private final Particle particle;
   private final double r;
   private double t;

   public SmallParticleEffect(Entity entity, Particle particle) {
      this(entity, particle, 0.7);
   }

   public SmallParticleEffect(Entity entity, Particle particle, double r) {
      this.loc = entity.getLocation().add(0.0, 0.5, 0.0);
      this.particle = particle;
      this.r = r;
      this.runTaskTimer(MMOCore.plugin, 0L, 1L);
   }

   public void run() {
      if (this.t > Math.PI * 2) {
         this.cancel();
      }

      for (int k = 0; k < 3; k++) {
         this.t += Math.PI / 10;
         this.loc
            .getWorld()
            .spawnParticle(this.particle, this.loc.clone().add(this.r * Math.cos(this.t), this.t / Math.PI / 2.0, this.r * Math.sin(this.t)), 0);
      }
   }
}
