package team.unnamed.creative.atlas;

import java.util.List;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Vector2Float;

@NonExtendable
public interface UnstitchAtlasSource extends AtlasSource {
   Vector2Float DEFAULT_DIVISOR = Vector2Float.ONE;
   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   double DEFAULT_X_DIVISOR = (double)1.0F;
   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   double DEFAULT_Y_DIVISOR = (double)1.0F;

   @NotNull Key resource();

   @NotNull @Unmodifiable List<Region> regions();

   @NotNull Vector2Float divisor();

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default double xDivisor() {
      return (double)this.divisor().x();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   default double yDivisor() {
      return (double)this.divisor().y();
   }

   @NonExtendable
   public interface Region {
      @NotNull Key sprite();

      @NotNull Vector2Float position();

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      default double x() {
         return (double)this.position().x();
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      default double y() {
         return (double)this.position().y();
      }

      @NotNull Vector2Float dimensions();

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      default double width() {
         return (double)this.dimensions().x();
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      default double height() {
         return (double)this.dimensions().y();
      }

      static @NotNull Region region(@NotNull Key sprite, @NotNull Vector2Float position, @NotNull Vector2Float dimensions) {
         return new UnstitchAtlasSourceImpl.RegionImpl(sprite, position, dimensions);
      }

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      static @NotNull Region of(@NotNull Key sprite, double x, double y, double width, double height) {
         return region(sprite, new Vector2Float((float)x, (float)y), new Vector2Float((float)width, (float)height));
      }
   }
}
