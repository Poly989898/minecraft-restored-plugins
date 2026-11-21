package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Optional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;

@MythicMechanic(
   author = "Ashijin",
   name = "posearmorstand",
   aliases = {"armorstandpose"},
   description = "Poses the target armor stand"
)
public class PoseArmorStandMechanic extends SkillMechanic implements ITargetedEntitySkill {
   private Optional<RandomDouble> headPoseP = Optional.empty();
   private Optional<RandomDouble> headPoseY = Optional.empty();
   private Optional<RandomDouble> headPoseA = Optional.empty();
   private Optional<RandomDouble> bodyPoseP = Optional.empty();
   private Optional<RandomDouble> bodyPoseY = Optional.empty();
   private Optional<RandomDouble> bodyPoseA = Optional.empty();
   private Optional<RandomDouble> lArmPoseP = Optional.empty();
   private Optional<RandomDouble> lArmPoseY = Optional.empty();
   private Optional<RandomDouble> lArmPoseA = Optional.empty();
   private Optional<RandomDouble> rArmPoseP = Optional.empty();
   private Optional<RandomDouble> rArmPoseY = Optional.empty();
   private Optional<RandomDouble> rArmPoseA = Optional.empty();
   private Optional<RandomDouble> lLegPoseP = Optional.empty();
   private Optional<RandomDouble> lLegPoseY = Optional.empty();
   private Optional<RandomDouble> lLegPoseA = Optional.empty();
   private Optional<RandomDouble> rLegPoseP = Optional.empty();
   private Optional<RandomDouble> rLegPoseY = Optional.empty();
   private Optional<RandomDouble> rLegPoseA = Optional.empty();

   public PoseArmorStandMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      String headPose = mlc.getString(new String[]{"head", "h"}, null);
      if (headPose != null) {
         try {
            String[] split = headPose.split(",");
            this.headPoseP = Optional.of(new RandomDouble(split[0]));
            this.headPoseY = Optional.of(new RandomDouble(split[1]));
            this.headPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception e) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(e);
         }
      }

      String bodyPose = mlc.getString(new String[]{"body", "b"}, null);
      if (bodyPose != null) {
         try {
            String[] split = bodyPose.split(",");
            this.bodyPoseP = Optional.of(new RandomDouble(split[0]));
            this.bodyPoseY = Optional.of(new RandomDouble(split[1]));
            this.bodyPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(ex);
         }
      }

      String lArmPose = mlc.getString(new String[]{"leftarm", "la"}, null);
      if (lArmPose != null) {
         try {
            String[] split = lArmPose.split(",");
            this.lArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.lArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.lArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex2) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(ex2);
         }
      }

      String rArmPose = mlc.getString(new String[]{"rightarm", "ra"}, null);
      if (rArmPose != null) {
         try {
            String[] split = rArmPose.split(",");
            this.rArmPoseP = Optional.of(new RandomDouble(split[0]));
            this.rArmPoseY = Optional.of(new RandomDouble(split[1]));
            this.rArmPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex3) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(ex3);
         }
      }

      String lLegPose = mlc.getString(new String[]{"leftleg", "ll"}, null);
      if (lLegPose != null) {
         try {
            String[] split = lLegPose.split(",");
            this.lLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.lLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.lLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex4) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(ex4);
         }
      }

      String rLegPose = mlc.getString(new String[]{"rightleg", "rl"}, null);
      if (rLegPose != null) {
         try {
            String[] split = rLegPose.split(",");
            this.rLegPoseP = Optional.of(new RandomDouble(split[0]));
            this.rLegPoseY = Optional.of(new RandomDouble(split[1]));
            this.rLegPoseA = Optional.of(new RandomDouble(split[2]));
         } catch (Exception ex5) {
            MythicLogger.error("ArmorStandPose Mechanic is configured incorrectly: must be in format: pitch,yaw,angle");
            MythicLogger.handleMinorError(ex5);
         }
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      Entity entity = target.getBukkitEntity();
      if (!(entity instanceof ArmorStand e)) {
         return SkillResult.INVALID_TARGET;
      } else {
         Runnable applyRunnable = () -> {
            if (this.headPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.headPoseP.get().get(), (float)this.headPoseY.get().get(), (float)this.headPoseA.get().get());
               e.setHeadPose(a);
            }

            if (this.bodyPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.bodyPoseP.get().get(), (float)this.bodyPoseY.get().get(), (float)this.bodyPoseA.get().get());
               e.setBodyPose(a);
            }

            if (this.lArmPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.lArmPoseP.get().get(), (float)this.lArmPoseY.get().get(), (float)this.lArmPoseA.get().get());
               e.setLeftArmPose(a);
            }

            if (this.rArmPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.rArmPoseP.get().get(), (float)this.rArmPoseY.get().get(), (float)this.rArmPoseA.get().get());
               e.setRightArmPose(a);
            }

            if (this.lLegPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.lLegPoseP.get().get(), (float)this.lLegPoseY.get().get(), (float)this.lLegPoseA.get().get());
               e.setLeftLegPose(a);
            }

            if (this.rLegPoseP.isPresent()) {
               EulerAngle a = new EulerAngle((float)this.rLegPoseP.get().get(), (float)this.rLegPoseY.get().get(), (float)this.rLegPoseA.get().get());
               e.setRightLegPose(a);
            }
         };
         Schedulers.runOnRegion(entity, applyRunnable);
         return SkillResult.SUCCESS;
      }
   }
}
