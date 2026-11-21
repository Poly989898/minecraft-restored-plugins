package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.placeholders.PlaceholderBoolean;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "fovoffset"
)
public class FOVOffsetPlaceholder extends NumericEntityScopedPlaceholder {
   @MythicField(
      name = "rotation",
      aliases = {"r"},
      description = "Rotates the FOV to check in"
   )
   private PlaceholderDouble rotation = PlaceholderDouble.of("0");
   @MythicField(
      name = "absolute",
      aliases = {"abs"},
      description = "If true, returns the absolute angle. If false, returns signed angle."
   )
   private PlaceholderBoolean absolute;

   public FOVOffsetPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      if (context.config != null) {
         this.rotation = context.config.getPlaceholderDouble(new String[]{"rotation", "r"}, 0.0);
         this.absolute = context.config.getPlaceholderBoolean(new String[]{"absolute", "abs"}, true);
      }
   }

   private AbstractVector rotateAroundY(AbstractVector v, double degrees) {
      double radians = Math.toRadians(degrees);
      double sin = Math.sin(radians);
      double cos = Math.cos(radians);
      double x = v.getX() * cos + v.getZ() * sin;
      double z = v.getX() * -sin + v.getZ() * cos;
      return new AbstractVector(x, v.getY(), z);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity casterEntity = placeholderContext.meta().getCaster().getEntity();
      AbstractEntity targetEntity = this.getEntity.get(placeholderContext);
      AbstractVector casterVector = casterEntity.getEyeLocation().toVector();
      AbstractVector headDirection = casterEntity.getEyeLocation().getDirection();
      AbstractVector targetVector;
      if (targetEntity == null) {
         AbstractLocation targetLocation = this.getLocation.get(placeholderContext);
         targetVector = targetLocation.toVector();
      } else {
         targetVector = targetEntity.getEyeLocation().toVector();
      }

      headDirection = this.rotateAroundY(headDirection, this.rotation.get(placeholderContext));
      AbstractVector targetDirection = targetVector.subtract(casterVector).normalize();
      double offsetAngle = Math.toDegrees(targetDirection.angle(headDirection));
      double signedAngle = offsetAngle;
      if (!this.absolute.get(placeholderContext)) {
         AbstractVector crossProduct = headDirection.crossProduct(targetDirection);
         if (crossProduct.getY() < 0.0) {
            signedAngle = -offsetAngle;
         }
      }

      return signedAngle;
   }
}
