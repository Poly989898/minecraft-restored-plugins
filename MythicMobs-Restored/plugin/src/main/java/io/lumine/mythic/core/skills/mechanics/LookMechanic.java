package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableRegistry;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

@MythicMechanic(
   author = "Ashijin",
   name = "look",
   description = "Forces the caster to look at the target location"
)
public class LookMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
   @MythicField(
      name = "headOnly",
      aliases = {"ho"},
      description = "Only rotate the head of the mob",
      defValue = "false"
   )
   protected boolean headOnly;
   @MythicField(
      name = "immediately",
      aliases = {"immediate", "i"},
      description = "Causes the mob to turn towards the target immediate without any animations",
      defValue = "true"
   )
   protected boolean immediately;
   @MythicField(
      name = "force",
      aliases = {"f"},
      description = "Forces the mob to look at the target",
      defValue = "false"
   )
   protected boolean force;
   @MythicField(
      name = "forcePaper",
      aliases = {"fp"},
      description = "Whether to use PaperMC's method to force the mob to look at the target",
      defValue = "false"
   )
   protected boolean forcePaper;
   @MythicField(
      name = "speed",
      aliases = {"s", "duration", "ticks"},
      description = "Ticks to fully rotate toward the target for no-AI entities. 1 or less = instant.",
      defValue = "10"
   )
   protected PlaceholderDuration speedTicks;

   public LookMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
      this.headOnly = mlc.getBoolean(new String[]{"headonly", "ho"}, false);
      this.immediately = mlc.getBoolean(new String[]{"immediately", "immediate", "i"}, true);
      this.force = mlc.getBoolean(new String[]{"force", "f"}, false);
      this.forcePaper = mlc.getBoolean(new String[]{"forcepaper", "fp"}, false);
      this.speedTicks = mlc.getPlaceholderDuration(new String[]{"speed", "s", "duration", "ticks"}, DurationUnit.TICKS, "10");
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (data.getCaster().getEntity().getBukkitEntity() instanceof ArmorStand armorStand) {
         this.point(target.getLocation(), armorStand);
      } else if (this.force) {
         this.forceLook(data, target.getLocation());
      } else if (this.forcePaper) {
         data.getCaster().getEntity().lookAt(target);
      } else if (!data.getCaster().getEntity().hasAI()) {
         this.simulateLookingAtLocation(data.getCaster().getEntity(), target, Math.max(1, this.speedTicks.get(data, target).getTicks()));
      } else {
         this.getPlugin().getVolatileCodeHandler().lookAtEntity(data.getCaster().getEntity(), target, this.headOnly, this.immediately);
      }

      return SkillResult.SUCCESS;
   }

   @Override
   public SkillResult castAtLocation(SkillMetadata data, AbstractLocation target) {
      if (data.getCaster().getEntity().getBukkitEntity() instanceof ArmorStand armorStand) {
         this.point(target, armorStand);
      } else if (this.force) {
         this.forceLook(data, target);
      } else if (this.forcePaper) {
         data.getCaster().getEntity().lookAt(target);
      } else if (!data.getCaster().getEntity().hasAI()) {
         this.simulateLookingAtLocation(data.getCaster().getEntity(), target, Math.max(1, this.speedTicks.get(data).getTicks()));
      } else {
         this.getPlugin().getVolatileCodeHandler().lookAtLocation(data.getCaster().getEntity(), target, this.headOnly, this.immediately);
      }

      return SkillResult.SUCCESS;
   }

   public void forceLook(SkillMetadata data, AbstractLocation target) {
      AbstractLocation fromLocation = data.getCaster().getLocation();
      double xDiff = target.getX() - fromLocation.getX();
      double yDiff = target.getY() - fromLocation.getY();
      double zDiff = target.getZ() - fromLocation.getZ();
      double distanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
      double distanceY = Math.sqrt(distanceXZ * distanceXZ + yDiff * yDiff);
      double yawRads = Math.acos(xDiff / distanceXZ);
      double pitchRads = Math.acos(yDiff / distanceY);
      double yaw = Math.toDegrees(yawRads);
      double pitch = Math.toDegrees(pitchRads) - 90.0;
      if (zDiff < 0.0) {
         yaw += Math.abs(180.0 - yaw) * 2.0;
      }

      yaw -= 90.0;
      if (data.getCaster().getEntity().getBukkitEntity() instanceof ArmorStand as) {
         this.point(target, as);
      } else {
         fromLocation.setYaw((float)yaw);
         fromLocation.setPitch((float)pitch);
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(data.getCaster().getEntity(), (float)pitch, (float)yaw);
      }
   }

   private void point(AbstractLocation target, ArmorStand stand) {
      Location location = BukkitAdapter.adapt(target);
      Vector delta = location.toVector().subtract(stand.getLocation().toVector());
      double yaw = Math.atan2(delta.getX(), delta.getZ());
      double len = delta.getZ() / Math.cos(yaw);
      double pitch = Math.atan2(delta.getY() - 1.4375, len);
      stand.setHeadPose(new EulerAngle(-pitch, 0.0, 0.0));
      Location loc = stand.getLocation();
      loc.setYaw((float)Math.toDegrees(-yaw));
      if (ServerVersion.isFolia()) {
         stand.teleportAsync(loc);
      } else {
         stand.teleport(loc);
      }
   }

   private void simulateLookingAtLocation(AbstractEntity caster, AbstractEntity target, int speedTicks) {
      if (caster != null && target != null) {
         if (!this.immediately && speedTicks > 1) {
            new LookMechanic.Animator(caster, target, speedTicks);
         } else {
            this.aimAndSetRotation(caster, target.getLocation());
         }
      }
   }

   private void simulateLookingAtLocation(AbstractEntity caster, AbstractLocation target, int speedTicks) {
      if (caster != null && target != null) {
         if (!this.immediately && speedTicks > 1) {
            new LookMechanic.Animator(caster, target, speedTicks);
         } else {
            this.aimAndSetRotation(caster, target);
         }
      }
   }

   private void aimAndSetRotation(AbstractEntity caster, AbstractLocation target) {
      Entity be = caster.getBukkitEntity();
      if (be != null) {
         Location cur = be.getLocation();
         double eyeY = cur.getY() + approximateEyeHeight(be);
         double dx = target.getX() - cur.getX();
         double dz = target.getZ() - cur.getZ();
         double dy = target.getY() - eyeY;
         double distXZ = Math.max(1.0E-6, Math.hypot(dx, dz));
         float desiredYaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
         float desiredPitch = clampPitch((float)Math.toDegrees(-Math.atan2(dy, distXZ)));
         this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(caster, desiredPitch, wrapYaw(desiredYaw));
      }
   }

   private static float wrapYaw(float yaw) {
      yaw %= 360.0F;
      if (yaw >= 180.0F) {
         yaw -= 360.0F;
      }

      if (yaw < -180.0F) {
         yaw += 360.0F;
      }

      return yaw;
   }

   private static float deltaAngle(float current, float target) {
      float d = wrapYaw(target) - wrapYaw(current);
      if (d >= 180.0F) {
         d -= 360.0F;
      }

      if (d < -180.0F) {
         d += 360.0F;
      }

      return d;
   }

   private static float clampPitch(float pitch) {
      return Math.max(-89.9F, Math.min(89.9F, pitch));
   }

   private static double approximateEyeHeight(Entity e) {
      if (e instanceof LivingEntity le) {
         try {
            return le.getEyeHeight();
         } catch (Throwable ex) {
         }
      }

      double h = e.getHeight();
      return h > 0.0 ? h * 0.85 : 1.5;
   }

   private final class Animator implements Runnable, Terminable {
      private final TerminableRegistry components = TerminableRegistry.create();
      private final AbstractEntity caster;
      private final AbstractEntity movingTarget;
      private AbstractLocation targetLocation;
      private int ticksRemaining;

      Animator(AbstractEntity caster, AbstractEntity target, int speedTicks) {
         this.caster = caster;
         this.movingTarget = target;
         this.targetLocation = target.getLocation();
         this.ticksRemaining = Math.max(1, speedTicks);
         this.start();
      }

      Animator(AbstractEntity caster, AbstractLocation targetLoc, int speedTicks) {
         this.caster = caster;
         this.movingTarget = null;
         this.targetLocation = targetLoc;
         this.ticksRemaining = Math.max(1, speedTicks);
         this.start();
      }

      private void start() {
         this.components.accept(Schedulers.of(this.caster.getBukkitEntity()).runRepeating(this, 0L, 1L));
      }

      @Override
      public void run() {
         if (this.caster != null && this.caster.getBukkitEntity() != null && !this.caster.isDead()) {
            if (this.movingTarget != null) {
               if (this.movingTarget.isDead() || this.movingTarget.getBukkitEntity() == null) {
                  this.close();
                  return;
               }

               this.targetLocation = this.movingTarget.getLocation();
            }

            if (this.targetLocation == null) {
               this.close();
            } else {
               Location cur = this.caster.getBukkitEntity().getLocation();
               float curYaw = cur.getYaw();
               float curPitch = cur.getPitch();
               double eyeY = cur.getY() + LookMechanic.approximateEyeHeight(this.caster.getBukkitEntity());
               double dx = this.targetLocation.getX() - cur.getX();
               double dz = this.targetLocation.getZ() - cur.getZ();
               double dy = this.targetLocation.getY() - eyeY;
               double distXZ = Math.max(1.0E-6, Math.hypot(dx, dz));
               float desiredYaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
               float desiredPitch = LookMechanic.clampPitch((float)Math.toDegrees(-Math.atan2(dy, distXZ)));
               float dYaw = LookMechanic.deltaAngle(curYaw, desiredYaw);
               float dPitch = desiredPitch - curPitch;
               int stepsLeft = Math.max(1, this.ticksRemaining);
               float stepYaw = dYaw / stepsLeft;
               float stepPitch = dPitch / stepsLeft;
               if (Math.abs(dYaw) < 0.25F) {
                  stepYaw = dYaw;
               }

               if (Math.abs(dPitch) < 0.25F) {
                  stepPitch = dPitch;
               }

               float newYaw = LookMechanic.wrapYaw(curYaw + stepYaw);
               float newPitch = LookMechanic.clampPitch(curPitch + stepPitch);
               LookMechanic.this.getPlugin().getVolatileCodeHandler().getEntityHandler().setEntityRotation(this.caster, newPitch, newYaw);
               this.ticksRemaining--;
               if (this.ticksRemaining <= 0) {
                  LookMechanic.this.getPlugin()
                     .getVolatileCodeHandler()
                     .getEntityHandler()
                     .setEntityRotation(this.caster, desiredPitch, LookMechanic.wrapYaw(desiredYaw));
                  this.close();
               }
            }
         } else {
            this.close();
         }
      }

      @Override
      public void close() {
         this.components.closeAndReportException();
      }
   }
}
