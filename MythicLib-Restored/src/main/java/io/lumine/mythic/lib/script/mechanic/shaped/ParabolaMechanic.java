package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.SourceLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.TargetLocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParabolaMechanic extends Mechanic {
   private final double height;
   private final double speed;
   private final LocationTargeter sourceLocation;
   private final LocationTargeter targetLocation;
   private final Script onStart;
   private final Script onTick;
   private final Script onEnd;

   public ParabolaMechanic(ConfigObject config) {
      this.sourceLocation = config.contains("source") ? config.getLocationTargeter("source") : new SourceLocationTargeter();
      this.targetLocation = config.contains("target") ? config.getLocationTargeter("target") : new TargetLocationTargeter();
      config.validateKeys("tick");
      this.onStart = config.contains("start") ? config.getScript("start") : null;
      this.onTick = config.getScript("tick");
      this.onEnd = config.contains("end") ? config.getScript("end") : null;
      this.height = config.getDouble("height");
      this.speed = config.getDouble("speed", 1.0);
      Validate.isTrue(this.speed > 0.0, "Speed must be strictly positive");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Location source = this.sourceLocation.findTargets(meta).get(0);

      for (Location loc : this.targetLocation.findTargets(meta)) {
         this.cast(meta, source, loc.clone().subtract(source).toVector());
      }
   }

   public void cast(final SkillMetadata meta, final Location source, final Vector dir) {
      Validate.isTrue(dir.lengthSquared() > 0.0, "Direction cannot be zero");
      final double xzLength = dir.clone().setY(0).length();
      double height = this.height + Math.max(0.0, dir.getY());
      final double a = (2.0 * dir.getY() - 4.0 * height) / (xzLength * xzLength);
      final double b = xzLength - dir.getY() / (a * xzLength);
      (new BukkitRunnable() {
            double x = 0.0;
            private static final double DT = 0.05;
            private static final double STEP = 0.3;
            private final Vector axis = dir.clone().setY(0).normalize();

            public void run() {
               double dx = ParabolaMechanic.this.speed * 0.05;
               if (this.x >= xzLength) {
                  this.cancel();
               } else {
                  double dy = ParabolaMechanic.this.getLength(a, b, this.x, this.x + dx);
                  this.x += dx;
                  int displayed = Math.max(1, (int)(dy / 0.3));
                  double xStep = dx / displayed;
                  Script cast = ParabolaMechanic.this.onStart != null && this.x == dx
                     ? ParabolaMechanic.this.onStart
                     : (ParabolaMechanic.this.onEnd != null && this.x >= xzLength ? ParabolaMechanic.this.onEnd : ParabolaMechanic.this.onTick);

                  for (int i = 0; i < displayed; i++) {
                     double x_i = this.x + i * xStep;
                     Location loc_i = source.clone().add(this.axis.clone().multiply(x_i)).add(0.0, ParabolaMechanic.this.y(a, b, x_i), 0.0);
                     cast.cast(meta.clone(source, loc_i, null, null));
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, 1L);
   }

   private double y(double a, double b, double x) {
      return a * x * (x - b);
   }

   private double getLength(double a, double b, double xStart, double xEnd) {
      double uStart = a * (2.0 * xStart - b);
      double uEnd = a * (2.0 * xEnd - b);
      return (this.primitive(uEnd) - this.primitive(uStart)) / (2.0 * a);
   }

   private double primitive(double x) {
      return 0.5 * x + 0.25 * Math.sinh(2.0 * x);
   }
}
