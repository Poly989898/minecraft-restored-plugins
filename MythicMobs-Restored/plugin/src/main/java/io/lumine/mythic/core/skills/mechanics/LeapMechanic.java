package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.MythicUtil;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicFields;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "leap",
   description = "Causes the caster to leap to the target location"
)
public class LeapMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicFields({@MythicField(
         name = "velocity",
         aliases = {"v"},
         description = "The velocity of the leap",
         defValue = "100"
      ), @MythicField(
         name = "noise",
         aliases = {"n"},
         description = "The noise/randomness of the leap",
         defValue = "1"
      )})
   protected PlaceholderDouble velocity;
   @MythicFields({@MythicField(
         name = "velocity",
         aliases = {"v"},
         description = "The velocity of the leap",
         defValue = "100"
      ), @MythicField(
         name = "noise",
         aliases = {"n"},
         description = "The noise/randomness of the leap",
         defValue = "1"
      )})
   protected PlaceholderDouble noise;

   public LeapMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.velocity = mlc.getPlaceholderDouble(new String[]{"velocity", "v"}, 100.0);
      this.noise = mlc.getPlaceholderDouble(new String[]{"noise", "n"}, 0.0);
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      this.Leap(data, data.getCaster(), target.getLocation(), data.getPower());
      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      this.Leap(data, data.getCaster(), target, data.getPower());
      return SkillResult.SUCCESS;
   }

   protected void Leap(SkillMetadata data, SkillCaster am, AbstractLocation location, float power) {
      int gravity = 20;
      double velocity = this.velocity.get(data) / 10.0 * (1.0 + power * 0.1);
      Location from = BukkitAdapter.adapt(am.getEntity().getLocation());
      Location to = BukkitAdapter.adapt(location);
      if (from.getWorld().equals(to.getWorld())) {
         Vector v = to.clone().subtract(from).toVector();
         Double elevation = v.getY();
         Double launchAngle = MythicUtil.calculateLaunchAngle(from, to, velocity, elevation, 20.0);
         Double distance = Math.sqrt(Math.pow(v.getX(), 2.0) + Math.pow(v.getZ(), 2.0));
         if (distance != 0.0) {
            if (launchAngle == null) {
               launchAngle = Math.atan((40.0 * elevation + Math.pow(velocity, 2.0)) / (40.0 * elevation + 2.0 * Math.pow(velocity, 2.0)));
            }

            Double hangtime = MythicUtil.calculateHangtime(launchAngle, velocity, elevation, 20.0);
            v.setY(Math.tan(launchAngle) * distance);
            v = MythicUtil.normalizeVector(v);
            Vector noise = Vector.getRandom();
            noise = noise.multiply(this.noise.get(data) / 10.0);
            v.add(noise);
            velocity = velocity + 1.188 * Math.pow(hangtime, 2.0) + (Numbers.randomDouble() - 0.8) / 2.0;
            v = v.multiply(velocity / 20.0);
            if (v.length() > 4.0) {
               v = v.normalize().multiply(4);
            }

            if (Double.isNaN(v.getX())) {
               v.setX(0);
            }

            if (Double.isNaN(v.getY())) {
               v.setY(0);
            }

            if (Double.isNaN(v.getZ())) {
               v.setZ(0);
            }

            am.getEntity().getBukkitEntity().setVelocity(v);
         }
      }
   }
}
