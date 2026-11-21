package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@MythicPlaceholder(
   placeholder = "velocity.relative",
   usedPlaceholderArguments = -1
)
public class VelocityRelativePlaceholder extends VelocityComponentPlaceholder {
   public VelocityRelativePlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
   }

   @Nullable
   @Override
   public Number applyToScopeWithNumericFormatting(PlaceholderContext placeholderContext) {
      AbstractEntity entity = this.getEntity.get(placeholderContext);
      return entity == null ? null : this.getVelocityComponent(this.getRelativeVelocity(entity));
   }

   @Nullable
   private AbstractVector getRelativeVelocity(@NotNull AbstractEntity entity) {
      AbstractVector velocity = this.getResolvedVelocity(entity);
      if (velocity == null) {
         return null;
      } else {
         AbstractVector forward = entity.getLocation().getDirection();
         if (forward != null && forward.isFinite() && forward.lengthSquared() != 0.0) {
            forward = forward.clone().normalize();
            AbstractVector up = new AbstractVector(0, 1, 0);
            AbstractVector right = forward.crossProduct(up);
            if (right.isFinite() && right.lengthSquared() != 0.0) {
               right.normalize();
            } else {
               right = new AbstractVector(1, 0, 0);
            }

            up = right.crossProduct(forward);
            if (up.isFinite() && up.lengthSquared() != 0.0) {
               up.normalize();
            } else {
               up = new AbstractVector(0, 1, 0);
            }

            AbstractVector localVelocity = new AbstractVector(velocity.dot(right), velocity.dot(up), velocity.dot(forward));
            return !localVelocity.isFinite() ? new AbstractVector(0, 0, 0) : localVelocity;
         } else {
            return velocity.clone();
         }
      }
   }
}
