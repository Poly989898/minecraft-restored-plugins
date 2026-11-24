package net.Indyuce.mmocore.loot.chest.particle;

import java.util.function.BiConsumer;
import net.Indyuce.mmocore.MMOCore;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public enum ChestParticleEffect {
   HELIX(
      (loc, particle) -> (new BukkitRunnable() {
            double ti = 0.0;

            public void run() {
               if ((this.ti += Math.PI / 16) > Math.PI * 2) {
                  this.cancel();
               }

               for (double j = 0.0; j < Math.PI * 2; j += Math.PI * 2.0 / 5.0) {
                  loc.getWorld()
                     .spawnParticle(particle, loc.clone().add(Math.cos(j + this.ti / 2.0), -0.5 + this.ti / Math.PI / 2.0, Math.sin(j + this.ti / 2.0)), 0, CastingParticle.defaultData(particle));
               }
            }
         })
         .runTaskTimer(MMOCore.plugin, 0L, 1L)
   ),
   OFFSET((loc, particle) -> (new BukkitRunnable() {
      int ti = 0;

      public void run() {
         if (this.ti++ > 20) {
            this.cancel();
         }

         for (double j = 0.0; j < Math.PI * 2; j += Math.PI * 2.0 / 5.0) {
            loc.getWorld().spawnParticle(particle, loc.clone(), 1, 0.5, 0.5, 0.5, 0.0, CastingParticle.defaultData(particle));
         }
      }
   }).runTaskTimer(MMOCore.plugin, 0L, 1L)),
   GALAXY((loc, particle) -> (new BukkitRunnable() {
      double ti = 0.0;

      public void run() {
         if ((this.ti += Math.PI / 16) > Math.PI * 2) {
            this.cancel();
         }

         for (double j = 0.0; j < Math.PI * 2; j += Math.PI * 2.0 / 5.0) {
            loc.getWorld().spawnParticle(particle, loc.clone().add(0.0, -0.1, 0.0), 0, Math.cos(j + this.ti / 2.0), 0.0, Math.sin(j + this.ti / 2.0), 0.13, CastingParticle.defaultData(particle));
         }
      }
   }).runTaskTimer(MMOCore.plugin, 0L, 1L));

   private final BiConsumer<Location, Particle> func;

   ChestParticleEffect(BiConsumer<Location, Particle> func) {
      this.func = func;
   }

   public void play(Location loc, Particle particle) {
      this.func.accept(loc, particle);
   }
}
