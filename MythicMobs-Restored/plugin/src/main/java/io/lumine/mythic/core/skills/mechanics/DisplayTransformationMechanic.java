package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@MythicMechanic(
   name = "displaytransformation",
   aliases = {"settransformation", "transformation"},
   description = "Sets the display entity's transformations"
)
public class DisplayTransformationMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
   @MythicField(
      name = "action",
      aliases = {"a"},
      defValue = "SET",
      description = "The action to perform for the display entity's transformations. SET/ADD/MULTIPLY/DIVIDE"
   )
   private final DisplayTransformationMechanic.Action action;
   @MythicField(
      name = "transformationType",
      aliases = {"transformation", "type", "tt"},
      defValue = "TRANSLATION",
      description = "The type of transformation to apply to the display entity. TRANSLATION/SCALE/RIGHT_ROTATION/LEFT_ROTATION"
   )
   private final DisplayTransformationMechanic.TransformationType transformationType;
   @MythicField(
      name = "values",
      aliases = {"value", "val"},
      defValue = "0,0,0",
      description = "The values for the transformation."
   )
   private final List<PlaceholderFloat> values;

   public DisplayTransformationMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
      super(manager, file, line, mlc);
      this.action = mlc.getEnum(new String[]{"action", "a"}, DisplayTransformationMechanic.Action.class, DisplayTransformationMechanic.Action.SET);
      this.transformationType = mlc.getEnum(
         new String[]{"transformationtype", "transformation", "type", "tt"},
         DisplayTransformationMechanic.TransformationType.class,
         DisplayTransformationMechanic.TransformationType.TRANSLATION
      );
      String[] values = mlc.getString(new String[]{"value", "val"}, "0,0,0").split(",");
      this.values = Arrays.stream(values).map(PlaceholderFloat::of).toList();
   }

   @Override
   public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
      if (!(target.getBukkitEntity() instanceof Display)) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.applyTransformation(data, target);
         return SkillResult.SUCCESS;
      }
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      if (!(data.getCaster().getEntity().getBukkitEntity() instanceof Display)) {
         return SkillResult.INVALID_TARGET;
      } else {
         this.applyTransformation(data, data.getCaster().getEntity());
         return SkillResult.SUCCESS;
      }
   }

   private void applyTransformation(SkillMetadata data, AbstractEntity target) {
      Display display = (Display)target.getBukkitEntity();
      List<Float> vals = this.values.stream().map(a -> a.get(data, target)).toList();
      Runnable applyRunnable = () -> {
         Transformation current = display.getTransformation();

         Transformation transformation = switch (this.action) {
            case SET -> {
               switch (this.transformationType) {
                  case TRANSLATION:
                     yield new Transformation(
                        new Vector3f(vals.get(0), vals.get(1), vals.get(2)), current.getLeftRotation(), current.getScale(), current.getRightRotation()
                     );
                  case SCALE:
                     yield new Transformation(
                        current.getTranslation(), current.getLeftRotation(), new Vector3f(vals.get(0), vals.get(1), vals.get(2)), current.getRightRotation()
                     );
                  case RIGHT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale(),
                        new Quaternionf(vals.get(0), vals.get(1), vals.get(2), vals.get(3))
                     );
                  case LEFT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        new Quaternionf(vals.get(0), vals.get(1), vals.get(2), vals.get(3)),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  default:
                     throw new MatchException(null, null);
               }
            }
            case ADD -> {
               switch (this.transformationType) {
                  case TRANSLATION:
                     yield new Transformation(
                        current.getTranslation().add(vals.get(0), vals.get(1), vals.get(2)),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  case SCALE:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale().add(vals.get(0), vals.get(1), vals.get(2)),
                        current.getRightRotation()
                     );
                  case RIGHT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation().add(vals.get(0), vals.get(1), vals.get(2), vals.get(3))
                     );
                  case LEFT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation().add(vals.get(0), vals.get(1), vals.get(2), vals.get(3)),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  default:
                     throw new MatchException(null, null);
               }
            }
            case MULTIPLY -> {
               switch (this.transformationType) {
                  case TRANSLATION:
                     yield new Transformation(
                        current.getTranslation().mul(vals.get(0), vals.get(1), vals.get(2)),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  case SCALE:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale().mul(vals.get(0), vals.get(1), vals.get(2)),
                        current.getRightRotation()
                     );
                  case RIGHT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation().mul(vals.get(0), vals.get(1), vals.get(2), vals.get(3))
                     );
                  case LEFT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation().mul(vals.get(0), vals.get(1), vals.get(2), vals.get(3)),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  default:
                     throw new MatchException(null, null);
               }
            }
            case DIVIDE -> {
               switch (this.transformationType) {
                  case TRANSLATION:
                     yield new Transformation(
                        current.getTranslation().div(vals.get(0), vals.get(1), vals.get(2)),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  case SCALE:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale().div(vals.get(0), vals.get(1), vals.get(2)),
                        current.getRightRotation()
                     );
                  case RIGHT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation(),
                        current.getScale(),
                        current.getRightRotation().div(new Quaternionf(vals.get(0), vals.get(1), vals.get(2), vals.get(3)))
                     );
                  case LEFT_ROTATION:
                     yield new Transformation(
                        current.getTranslation(),
                        current.getLeftRotation().div(new Quaternionf(vals.get(0), vals.get(1), vals.get(2), vals.get(3))),
                        current.getScale(),
                        current.getRightRotation()
                     );
                  default:
                     throw new MatchException(null, null);
               }
            }
         };
         display.setInterpolationDelay(display.getInterpolationDelay());
         display.setTransformation(transformation);
      };
      Schedulers.runOnRegion(display, applyRunnable);
   }

   public static enum Action {
      SET,
      ADD,
      MULTIPLY,
      DIVIDE;
   }

   public static enum TransformationType {
      TRANSLATION,
      SCALE,
      RIGHT_ROTATION,
      LEFT_ROTATION;
   }
}
