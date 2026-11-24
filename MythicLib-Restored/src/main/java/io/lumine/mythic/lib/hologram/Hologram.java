package io.lumine.mythic.lib.hologram;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.listener.option.GameIndicators;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class Hologram {
   public abstract void despawn();

   public abstract boolean isSpawned();

   public abstract void updateLocation(@NotNull Location location);

   public void flyOut(@NotNull final GameIndicators settings, @NotNull final Vector dir) {
      (new BukkitRunnable() {
         final Location loc = Hologram.this.getLocation().clone();
         double v = 6.0 * settings.initialUpwardVelocity;
         int i = 0;
         private final double acc = -10.0 * settings.gravity;
         private static final double DT = 0.15;

         public void run() {
            if (this.i == 0) {
               dir.multiply(2.0 * settings.radialVelocity);
            }

            if (this.i++ >= settings.lifespan) {
               Hologram.this.despawn();
               this.cancel();
            } else {
               this.v = this.v + this.acc * 0.15;
               this.loc.add(dir.getX() * 0.15, this.v * 0.15, dir.getZ() * 0.15);
               Hologram.this.updateLocation(this.loc);
            }
         }
      }).runTaskTimer(MythicLib.plugin, 0L, settings.tickPeriod);
   }

   public abstract void updateLines(List<String> texts);

   public abstract List<String> getLines();

   public abstract Location getLocation();

   public static Hologram create(Location loc, List<String> lines) {
      return MythicLib.plugin.getHologramFactory().newHologram(loc, lines);
   }
}
