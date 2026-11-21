package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDuration;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.numbers.DurationUnit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

@MythicMechanic(
   author = "jaylawl",
   name = "animatearmorstand",
   aliases = {"animateas", "animas"},
   description = "Makes an armor stand assume a pose over a specified time"
)
public class AnimateArmorStandMechanic extends SkillMechanic implements ITargetedEntitySkill {
   @MythicField(
      name = "duration",
      aliases = {"d"},
      defValue = "1",
      description = "The duration of the animation in ticks."
   )
   protected PlaceholderDuration duration;
   @MythicField(
      name = "smart",
      defValue = "true",
      description = "Whether to apply smart angle conversion."
   )
   boolean smart;
   @MythicField(
      name = "ignoreEmpty",
      aliases = {"ie"},
      defValue = "true",
      description = "Whether to ignore empty pose values."
   )
   boolean ignoreEmpty;
   @MythicField(
      name = "useDegrees",
      aliases = {"ud"},
      defValue = "true",
      description = "Whether to use degrees for pose values."
   )
   boolean useDegrees;
   @MythicField(
      name = "head",
      aliases = {"h"},
      description = "The pose for the armor stand's head."
   )
   PlaceholderString head;
   @MythicField(
      name = "body",
      aliases = {"b"},
      description = "The pose for the armor stand's body."
   )
   PlaceholderString body;
   @MythicField(
      name = "leftArm",
      aliases = {"la"},
      description = "The pose for the armor stand's left arm."
   )
   PlaceholderString leftArm;
   @MythicField(
      name = "rightArm",
      aliases = {"ra"},
      description = "The pose for the armor stand's right arm."
   )
   PlaceholderString rightArm;
   @MythicField(
      name = "leftLeg",
      aliases = {"ll"},
      description = "The pose for the armor stand's left leg."
   )
   PlaceholderString leftLeg;
   @MythicField(
      name = "rightLeg",
      aliases = {"rl"},
      description = "The pose for the armor stand's right leg."
   )
   PlaceholderString rightLeg;
   private EulerAngle h;
   private EulerAngle b;
   private EulerAngle la;
   private EulerAngle ra;
   private EulerAngle ll;
   private EulerAngle rl;

   public AnimateArmorStandMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.duration = mlc.getPlaceholderDuration(new String[]{"duration", "d"}, DurationUnit.TICKS, "1");
      this.smart = mlc.getBoolean(new String[]{"smart"}, true);
      this.ignoreEmpty = mlc.getBoolean(new String[]{"ignoreempty", "ie"}, true);
      this.useDegrees = mlc.getBoolean(new String[]{"usedegrees", "ud"}, true);
      this.head = mlc.getPlaceholderString(new String[]{"head", "h"}, null);
      this.body = mlc.getPlaceholderString(new String[]{"body", "b"}, null);
      this.leftArm = mlc.getPlaceholderString(new String[]{"leftarm", "la"}, null);
      this.rightArm = mlc.getPlaceholderString(new String[]{"rightarm", "ra"}, null);
      this.leftLeg = mlc.getPlaceholderString(new String[]{"leftleg", "ll"}, null);
      this.rightLeg = mlc.getPlaceholderString(new String[]{"rightleg", "rl"}, null);
      if (this.head != null) {
         String[] split = this.head.get().split(",");
         double x = Double.valueOf(split[0]);
         double y = Double.valueOf(split[1]);
         double z = Double.valueOf(split[2]);
         if (this.smart) {
            if (x > 180.0) {
               x -= 360.0;
            }

            if (y > 180.0) {
               y -= 360.0;
            }

            if (z > 180.0) {
               z -= 360.0;
            }
         }

         if (this.useDegrees) {
            x = Math.toRadians(x);
            y = Math.toRadians(y);
            z = Math.toRadians(z);
         }

         this.h = new EulerAngle(x, y, z);
      } else if (!this.ignoreEmpty) {
         this.h = EulerAngle.ZERO;
      }

      if (this.body != null) {
         String[] splitx = this.body.get().split(",");
         double xx = Double.valueOf(splitx[0]);
         double yx = Double.valueOf(splitx[1]);
         double zx = Double.valueOf(splitx[2]);
         if (this.smart) {
            if (xx > 180.0) {
               xx -= 360.0;
            }

            if (yx > 180.0) {
               yx -= 360.0;
            }

            if (zx > 180.0) {
               zx -= 360.0;
            }
         }

         if (this.useDegrees) {
            xx = Math.toRadians(xx);
            yx = Math.toRadians(yx);
            zx = Math.toRadians(zx);
         }

         this.b = new EulerAngle(xx, yx, zx);
      } else if (!this.ignoreEmpty) {
         this.b = EulerAngle.ZERO;
      }

      if (this.leftArm != null) {
         String[] splitxx = this.leftArm.get().split(",");
         double xxx = Double.valueOf(splitxx[0]);
         double yxx = Double.valueOf(splitxx[1]);
         double zxx = Double.valueOf(splitxx[2]);
         if (this.smart) {
            if (xxx > 180.0) {
               xxx -= 360.0;
            }

            if (yxx > 180.0) {
               yxx -= 360.0;
            }

            if (zxx > 180.0) {
               zxx -= 360.0;
            }
         }

         if (this.useDegrees) {
            xxx = Math.toRadians(xxx);
            yxx = Math.toRadians(yxx);
            zxx = Math.toRadians(zxx);
         }

         this.la = new EulerAngle(xxx, yxx, zxx);
      } else if (!this.ignoreEmpty) {
         this.la = EulerAngle.ZERO;
      }

      if (this.rightArm != null) {
         String[] splitxxx = this.rightArm.get().split(",");
         double xxxx = Double.valueOf(splitxxx[0]);
         double yxxx = Double.valueOf(splitxxx[1]);
         double zxxx = Double.valueOf(splitxxx[2]);
         if (this.smart) {
            if (xxxx > 180.0) {
               xxxx -= 360.0;
            }

            if (yxxx > 180.0) {
               yxxx -= 360.0;
            }

            if (zxxx > 180.0) {
               zxxx -= 360.0;
            }
         }

         if (this.useDegrees) {
            xxxx = Math.toRadians(xxxx);
            yxxx = Math.toRadians(yxxx);
            zxxx = Math.toRadians(zxxx);
         }

         this.ra = new EulerAngle(xxxx, yxxx, zxxx);
      } else if (!this.ignoreEmpty) {
         this.ra = EulerAngle.ZERO;
      }

      if (this.leftLeg != null) {
         String[] splitxxxx = this.leftLeg.get().split(",");
         double xxxxx = Double.valueOf(splitxxxx[0]);
         double yxxxx = Double.valueOf(splitxxxx[1]);
         double zxxxx = Double.valueOf(splitxxxx[2]);
         if (this.smart) {
            if (xxxxx > 180.0) {
               xxxxx -= 360.0;
            }

            if (yxxxx > 180.0) {
               yxxxx -= 360.0;
            }

            if (zxxxx > 180.0) {
               zxxxx -= 360.0;
            }
         }

         if (this.useDegrees) {
            xxxxx = Math.toRadians(xxxxx);
            yxxxx = Math.toRadians(yxxxx);
            zxxxx = Math.toRadians(zxxxx);
         }

         this.ll = new EulerAngle(xxxxx, yxxxx, zxxxx);
      } else if (!this.ignoreEmpty) {
         this.ll = EulerAngle.ZERO;
      }

      if (this.rightLeg != null) {
         String[] splitxxxxx = this.rightLeg.get().split(",");
         double xxxxxx = Double.valueOf(splitxxxxx[0]);
         double yxxxxx = Double.valueOf(splitxxxxx[1]);
         double zxxxxx = Double.valueOf(splitxxxxx[2]);
         if (this.smart) {
            if (xxxxxx > 180.0) {
               xxxxxx -= 360.0;
            }

            if (yxxxxx > 180.0) {
               yxxxxx -= 360.0;
            }

            if (zxxxxx > 180.0) {
               zxxxxx -= 360.0;
            }
         }

         if (this.useDegrees) {
            xxxxxx = Math.toRadians(xxxxxx);
            yxxxxx = Math.toRadians(yxxxxx);
            zxxxxx = Math.toRadians(zxxxxx);
         }

         this.rl = new EulerAngle(xxxxxx, yxxxxx, zxxxxx);
      } else if (!this.ignoreEmpty) {
         this.rl = EulerAngle.ZERO;
      }
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(target.getBukkitEntity() instanceof ArmorStand as)) {
         return SkillResult.INVALID_TARGET;
      } else {
         int duration = this.duration.get(data, target).getTicks();
         Schedulers.runOnRegion(as, () -> {
            double hxD = this.h != null ? (this.h.getX() - as.getHeadPose().getX()) / duration : 0.0;
            double hyD = this.h != null ? (this.h.getY() - as.getHeadPose().getY()) / duration : 0.0;
            double hzD = this.h != null ? (this.h.getZ() - as.getHeadPose().getZ()) / duration : 0.0;
            double bxD = this.b != null ? (this.b.getX() - as.getBodyPose().getX()) / duration : 0.0;
            double byD = this.b != null ? (this.b.getY() - as.getBodyPose().getY()) / duration : 0.0;
            double bzD = this.b != null ? (this.b.getZ() - as.getBodyPose().getZ()) / duration : 0.0;
            double laxD = this.la != null ? (this.la.getX() - as.getLeftArmPose().getX()) / duration : 0.0;
            double layD = this.la != null ? (this.la.getY() - as.getLeftArmPose().getY()) / duration : 0.0;
            double lazD = this.la != null ? (this.la.getZ() - as.getLeftArmPose().getZ()) / duration : 0.0;
            double raxD = this.ra != null ? (this.ra.getX() - as.getRightArmPose().getX()) / duration : 0.0;
            double rayD = this.ra != null ? (this.ra.getY() - as.getRightArmPose().getY()) / duration : 0.0;
            double razD = this.ra != null ? (this.ra.getZ() - as.getRightArmPose().getZ()) / duration : 0.0;
            double llxD = this.ll != null ? (this.ll.getX() - as.getLeftLegPose().getX()) / duration : 0.0;
            double llyD = this.ll != null ? (this.ll.getY() - as.getLeftLegPose().getY()) / duration : 0.0;
            double llzD = this.ll != null ? (this.ll.getZ() - as.getLeftLegPose().getZ()) / duration : 0.0;
            double rlxD = this.rl != null ? (this.rl.getX() - as.getRightLegPose().getX()) / duration : 0.0;
            double rlyD = this.rl != null ? (this.rl.getY() - as.getRightLegPose().getY()) / duration : 0.0;
            double rlzD = this.rl != null ? (this.rl.getZ() - as.getRightLegPose().getZ()) / duration : 0.0;

            for (int i = 0; i < duration; i++) {
               Schedulers.of(as).runLater(() -> {
                  if (this.h != null) {
                     as.setHeadPose(as.getHeadPose().add(hxD, hyD, hzD));
                  }

                  if (this.b != null) {
                     as.setBodyPose(as.getBodyPose().add(bxD, byD, bzD));
                  }

                  if (this.la != null) {
                     as.setLeftArmPose(as.getLeftArmPose().add(laxD, layD, lazD));
                  }

                  if (this.ra != null) {
                     as.setRightArmPose(as.getRightArmPose().add(raxD, rayD, razD));
                  }

                  if (this.ll != null) {
                     as.setLeftLegPose(as.getLeftLegPose().add(llxD, llyD, llzD));
                  }

                  if (this.rl != null) {
                     as.setRightLegPose(as.getRightLegPose().add(rlxD, rlyD, rlzD));
                  }
               }, i + 1);
            }
         });
         return SkillResult.SUCCESS;
      }
   }
}
