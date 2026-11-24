package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.CasterLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.DefaultDirectionTargeter;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.EntityLocationType;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SlashMechanic extends Mechanic {
   private final double length;
   private final double angle;
   private final double distance;
   private final long points;
   private final long timeInterval;
   private final long pointsPerTick;
   private final LocationTargeter sourceLocation;
   private final LocationTargeter targetLocation;
   private final Script onStart;
   private final Script onTick;
   private final Script onEnd;

   public SlashMechanic(ConfigObject config) {
      this.sourceLocation = config.contains("source") ? config.getLocationTargeter("source") : new CasterLocationTargeter(EntityLocationType.EYES);
      this.targetLocation = config.contains("target") ? config.getLocationTargeter("target") : new DefaultDirectionTargeter();
      config.validateKeys("tick");
      this.onStart = config.contains("start") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("start")) : null;
      this.onTick = MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("tick"));
      this.onEnd = config.contains("end") ? MythicLib.plugin.getSkills().getScriptOrThrow(config.getString("end")) : null;
      this.distance = config.getDouble("distance", 3.0);
      this.length = config.getDouble("length", 4.0);
      this.angle = config.getDouble("angle", -30.0);
      this.points = config.getInt("points", 20);
      this.timeInterval = config.getInt("time_interval", 1);
      this.pointsPerTick = config.getInt("points_per_tick", 1);
      Validate.isTrue(this.length > 0.0, "Length must be strictly positive");
      Validate.isTrue(this.points > 0L, "Points must be strictly positive");
      Validate.isTrue(this.timeInterval > 0L, "Time interval must be strictly positive");
      Validate.isTrue(this.pointsPerTick > 0L, "Points per tick must be strictly positive");
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
      Vector radialAxis = dir.clone().normalize();
      final Vector slashDirection = dir.clone().setY(0).rotateAroundY(-Math.PI / 2).rotateAroundAxis(radialAxis, Math.toRadians(this.angle)).normalize();
      (new BukkitRunnable() {
            int counter = 0;
            final Location current = source.clone().add(slashDirection.clone().multiply(-SlashMechanic.this.length / 2.0));
            final Vector incremented = slashDirection.clone().multiply(SlashMechanic.this.length / SlashMechanic.this.points);

            public void run() {
               for (int i = 0; i < SlashMechanic.this.pointsPerTick; i++) {
                  if (this.counter++ >= SlashMechanic.this.points) {
                     this.cancel();
                     return;
                  }

                  this.current.add(this.incremented);
                  double x = Math.abs(this.counter - SlashMechanic.this.points / 2L) * 2.0 / SlashMechanic.this.points;
                  Script cast = SlashMechanic.this.onStart != null && this.counter == 1
                     ? SlashMechanic.this.onStart
                     : (SlashMechanic.this.onEnd != null && this.counter >= SlashMechanic.this.points ? SlashMechanic.this.onEnd : SlashMechanic.this.onTick);
                  Location intermediate = this.current.clone().add(dir.clone().multiply(SlashMechanic.this.distance * Math.sqrt(1.0 - x * x)));
                  cast.cast(meta.clone(source, intermediate, null, null));
               }
            }
         })
         .runTaskTimer(MythicLib.plugin, 0L, this.timeInterval);
   }
}
