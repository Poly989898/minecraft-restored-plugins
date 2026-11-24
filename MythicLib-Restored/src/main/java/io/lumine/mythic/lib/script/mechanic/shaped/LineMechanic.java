package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LineMechanic extends Mechanic {
   private final DoubleFormula step;
   private final LocationTargeter source;
   private final LocationTargeter target;
   private final Script onStart;
   private final Script onTick;
   private final Script onEnd;
   private final boolean instant;
   private final int pointsPerTick;

   public LineMechanic(ConfigObject config) {
      config.validateKeys("source", "target");
      this.source = config.getLocationTargeter("source");
      this.target = config.getLocationTargeter("target");
      this.step = config.getDoubleFormula("step", DoubleFormula.constant(0.5));
      this.instant = config.getBoolean("instant", false);
      this.pointsPerTick = config.getInt("points_per_tick", 1);
      Validate.isTrue(this.pointsPerTick > 0, "PPT must be strictly positive");
      this.onStart = config.getScriptOrNull("start");
      this.onTick = config.getScriptOrNull("tick");
      this.onEnd = config.getScriptOrNull("end");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Location source = this.source.findTargets(meta).get(0);

      for (Location loc : this.target.findTargets(meta)) {
         this.cast(meta, source, loc);
      }
   }

   public void cast(final SkillMetadata meta, final Location source, final Location target) {
      if (this.onStart != null) {
         this.onStart.cast(meta.clone(source.clone()));
      }

      final double step = this.step.evaluate(meta);
      final Vector diff = target.clone().subtract(source).toVector();
      final double maxDist = diff.length();
      if (!this.instant) {
         (new BukkitRunnable() {
            double dist = 0.0;

            public void run() {
               for (int i = 0; i < LineMechanic.this.pointsPerTick; i++) {
                  if (this.dist > maxDist) {
                     this.cancel();
                     if (LineMechanic.this.onEnd != null) {
                        LineMechanic.this.onEnd.cast(meta.clone(target.clone()));
                     }

                     return;
                  }

                  Location inter = source.clone().add(diff.clone().multiply(this.dist / maxDist));
                  LineMechanic.this.onTick.cast(meta.clone(inter));
                  this.dist = this.dist + step;
               }
            }
         }).runTaskTimer(MythicLib.plugin, 0L, 1L);
      } else {
         for (double dist = 0.0; dist < maxDist; dist += step) {
            Location inter = source.clone().add(diff.clone().multiply(dist / maxDist));
            this.onTick.cast(meta.clone(inter));
         }

         if (this.onEnd != null) {
            this.onEnd.cast(meta.clone(target.clone()));
         }
      }
   }
}
