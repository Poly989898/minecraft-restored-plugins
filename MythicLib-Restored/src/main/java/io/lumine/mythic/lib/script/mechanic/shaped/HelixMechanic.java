package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.ConstantLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.DefaultLocationTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class HelixMechanic extends Mechanic {
   private final DoubleFormula radius;
   private final double yawSpread;
   private final double height;
   private final long points;
   private final long timeInterval;
   private final long pointsPerTick;
   private final long helixes;
   private final LocationTargeter direction;
   private final LocationTargeter targetLocation;
   private final Script onStart;
   private final Script onTick;
   private final Script onEnd;

   public HelixMechanic(ConfigObject config) {
      this.targetLocation = config.contains("source") ? config.getLocationTargeter("source") : new DefaultLocationTargeter();
      this.direction = config.contains("direction") ? config.getLocationTargeter("direction") : new ConstantLocationTargeter(1.0, 0.0, 0.0);
      config.validateKeys("tick");
      this.onStart = config.contains("start") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("start")) : null;
      this.onTick = MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick"));
      this.onEnd = config.contains("end") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("end")) : null;
      this.yawSpread = config.getDouble("yaw", 360.0);
      this.height = config.getDouble("height", 3.0);
      this.radius = config.contains("radius") ? new DoubleFormula(config.getString("radius")) : new DoubleFormula(2.0);
      this.points = config.getInt("points", 40);
      this.timeInterval = config.getInt("time_interval", 1);
      this.pointsPerTick = config.getInt("points_per_tick", 3);
      this.helixes = config.getInt("helixes", 3);
      Validate.isTrue(this.yawSpread > 0.0, "Yaw spread must be strictly positive");
      Validate.isTrue(this.height > 0.0, "Height must be strictly positive");
      Validate.isTrue(this.points > 0L, "Points must be strictly positive");
      Validate.isTrue(this.timeInterval > 0L, "Time interval must be strictly positive");
      Validate.isTrue(this.pointsPerTick > 0L, "Points per tick must be strictly positive");
   }

   @Override
   public void cast(SkillMetadata meta) {
      Vector dir = this.direction.findTargets(meta).get(0).toVector();

      for (Location loc : this.targetLocation.findTargets(meta)) {
         this.cast(meta, loc, dir);
      }
   }

   public void cast(final SkillMetadata meta, final Location source, Vector dir) {
      Validate.isTrue(dir.lengthSquared() > 0.0, "Direction cannot be zero");
      final double yaw_i = UtilityMethods.getYawPitch(dir)[0] - this.yawSpread / 2.0;
      (new BukkitRunnable() {
            int counter = 0;

            public void run() {
               for (int i = 0; i < HelixMechanic.this.pointsPerTick; i++) {
                  if (this.counter++ >= HelixMechanic.this.points) {
                     this.cancel();
                     return;
                  }

                  double yaw = Math.toRadians(yaw_i + (double)this.counter / HelixMechanic.this.points * HelixMechanic.this.yawSpread);
                  double y = (double)this.counter / HelixMechanic.this.points * HelixMechanic.this.height;
                  double r = HelixMechanic.this.radius.evaluate(meta);
                  Script cast = HelixMechanic.this.onEnd != null && this.counter >= HelixMechanic.this.points
                     ? HelixMechanic.this.onEnd
                     : (HelixMechanic.this.onStart != null && this.counter == 1 ? HelixMechanic.this.onStart : HelixMechanic.this.onTick);

                  for (int j = 0; j < HelixMechanic.this.helixes; j++) {
                     double angle = yaw + (Math.PI * 2) / HelixMechanic.this.helixes * j;
                     Location loc = source.clone().add(r * Math.cos(angle), y, r * Math.sin(angle));
                     cast.cast(meta.clone(source, loc, null, null));
                  }
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, this.timeInterval);
   }
}
