package io.lumine.mythic.core.skills.placeholders.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.core.players.PlayerManager;
import io.lumine.mythic.core.skills.placeholders.segments.types.GenericPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.EntityScopedPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.NumericEntityScopedPlaceholder;
import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public abstract class VelocityComponentPlaceholder extends NumericEntityScopedPlaceholder {
   protected final VelocityComponentPlaceholder.VelocityComponent component;

   public VelocityComponentPlaceholder(EntityScopedPlaceholder.EntityScopedPlaceholderArguments context) {
      super(context);
      this.manager.getPlugin().getPlayerManager().trackPlayerMovement();
      VelocityComponentPlaceholder.VelocityComponent parsedComponent = VelocityComponentPlaceholder.VelocityComponent.TOTAL;
      GenericPlaceholderSegment maybeArg = this.getPlaceholderSegmentsManager().getExtraArg(0);
      if (maybeArg != null) {
         VelocityComponentPlaceholder.VelocityComponent maybeComponent = VelocityComponentPlaceholder.VelocityComponent.fromKey(maybeArg.key());
         if (maybeComponent != null) {
            parsedComponent = maybeComponent;
            this.usedPlaceholderArguments.set(1);
         }
      }

      this.component = parsedComponent;
      this.initializeMetaKeywords();
   }

   @Nullable
   protected Number getVelocityComponent(@Nullable AbstractVector velocity) {
      if (velocity == null) {
         return null;
      } else {
         double value = switch (this.component) {
            case X -> velocity.getX();
            case Y -> velocity.getY();
            case Z -> velocity.getZ();
            case TOTAL -> velocity.length();
         };
         return Double.isFinite(value) ? value : 0.0;
      }
   }

   @Nullable
   protected AbstractVector getResolvedVelocity(@Nullable AbstractEntity entity) {
      if (entity == null) {
         return null;
      } else if (!entity.isPlayer()) {
         return entity.getVelocity();
      } else {
         PlayerManager.PlayerMovementData data = this.manager.getPlugin().getPlayerManager().getPlayerPositions().get(entity.getUniqueId());
         if (data != null) {
            Location from = data.getFrom();
            Location to = data.getTo();
            if (from != null && to != null) {
               Vector delta = to.toVector().subtract(from.toVector());
               return new AbstractVector(delta.getX(), delta.getY(), delta.getZ());
            }
         }

         return entity.getVelocity();
      }
   }

   protected static enum VelocityComponent {
      X,
      Y,
      Z,
      TOTAL;

      @Nullable
      public static VelocityComponentPlaceholder.VelocityComponent fromKey(@Nullable String key) {
         if (key == null) {
            return null;
         } else {
            String str = key.toLowerCase(Locale.ROOT);

            return switch (str) {
               case "x" -> X;
               case "y" -> Y;
               case "z" -> Z;
               case "total", "t", "length", "len", "magnitude", "mag" -> TOTAL;
               default -> null;
            };
         }
      }
   }
}
