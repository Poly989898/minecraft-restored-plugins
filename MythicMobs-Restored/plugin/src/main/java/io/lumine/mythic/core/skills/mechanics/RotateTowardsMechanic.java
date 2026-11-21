package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;

@MythicMechanic(
   author = "Ashijin",
   name = "rotatetowards",
   aliases = {"rotateto"},
   description = "Rotates the caster towards the target location incrementally"
)
public class RotateTowardsMechanic extends SkillMechanic implements ITargetedLocationSkill {
   protected boolean rotateYaw;
   protected boolean rotatePitch;
   protected boolean useEyeLocation;
   @MythicField(
      name = "yaw",
      aliases = {"y"},
      version = "4.9",
      description = "The yaw to be set/add - Either yaw or pitch is required"
   )
   protected PlaceholderDouble maxYaw;
   @MythicField(
      name = "pitch",
      aliases = {"p"},
      version = "4.9",
      description = "The pitch to be set/add - Either yaw or pitch is required"
   )
   protected PlaceholderDouble maxPitch;

   public RotateTowardsMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.rotateYaw = mlc.getBoolean(new String[]{"rotateyaw", "yaw"}, true);
      this.rotatePitch = mlc.getBoolean(new String[]{"rotatepitch", "pitch"}, false);
      this.useEyeLocation = mlc.getBoolean(new String[]{"useEyeLocation", "uel"}, false);
      this.maxYaw = mlc.getPlaceholderDouble(new String[]{"maxyaw", "my", "y"}, 0.0);
      this.maxPitch = mlc.getPlaceholderDouble(new String[]{"maxpitch", "mp", "p"}, 0.0);
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (data.getCaster().getEntity().isPlayer()) {
         return SkillResult.INVALID_TARGET;
      } else {
         float maxYaw = (float)this.maxYaw.get(data);
         float maxPitch = (float)this.maxPitch.get(data);
         AbstractLocation casterLocation = this.useEyeLocation ? data.getCaster().getEntity().getEyeLocation() : data.getCaster().getLocation();
         float yaw;
         if (this.rotateYaw) {
            float casterYaw = casterLocation.getYaw();
            float targetYaw = this.calculateYawToTarget(casterLocation, target);
            float yawDifference = targetYaw - casterYaw;
            yawDifference = this.normalizeAngle(yawDifference);
            yaw = casterYaw + this.clamp(yawDifference, -maxYaw, maxYaw);
         } else {
            yaw = casterLocation.getYaw();
         }

         float pitch;
         if (this.rotatePitch) {
            float casterPitch = casterLocation.getPitch();
            float targetPitch = this.calculatePitchToTarget(casterLocation, target);
            float pitchDifference = targetPitch - casterPitch;
            pitch = casterPitch + this.clamp(pitchDifference, -maxPitch, maxPitch);
         } else {
            pitch = casterLocation.getPitch();
         }

         this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(data.getCaster().getEntity(), pitch, yaw);
         return SkillResult.SUCCESS;
      }
   }

   private float calculateYawToTarget(AbstractLocation from, AbstractLocation to) {
      double dx = to.getX() - from.getX();
      double dz = to.getZ() - from.getZ();
      return (float)Math.toDegrees(Math.atan2(-dx, dz));
   }

   private float calculatePitchToTarget(AbstractLocation from, AbstractLocation to) {
      double dx = to.getX() - from.getX();
      double dy = to.getY() - from.getY();
      double dz = to.getZ() - from.getZ();
      double distance = Math.sqrt(dx * dx + dz * dz);
      return (float)Math.toDegrees(Math.atan2(-dy, distance));
   }

   private float normalizeAngle(float angle) {
      while (angle > 180.0F) {
         angle -= 360.0F;
      }

      while (angle < -180.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   private float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }
}
