package team.unnamed.creative.model;

import java.util.Map;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector3Float;

public interface Element {
   boolean DEFAULT_SHADE = true;
   float MIN_EXTENT = -16.0F;
   float MAX_EXTENT = 32.0F;

   @Contract("-> new")
   static @NotNull Builder element() {
      return new ElementImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   @Contract("-> new")
   static @NotNull Builder builder() {
      return element();
   }

   @NotNull Vector3Float from();

   @NotNull Vector3Float to();

   @Nullable ElementRotation rotation();

   boolean shade();

   @Unmodifiable @NotNull Map<CubeFace, ElementFace> faces();

   int lightEmission();

   @Contract("-> new")
   @NotNull Builder toBuilder();

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder from(@NotNull Vector3Float var1);

      @Contract("_, _, _ -> this")
      default @NotNull Builder from(float x, float y, float z) {
         return this.from(new Vector3Float(x, y, z));
      }

      @Contract("_ -> this")
      @NotNull Builder to(@NotNull Vector3Float var1);

      default @NotNull Builder to(float x, float y, float z) {
         return this.to(new Vector3Float(x, y, z));
      }

      @Contract("_ -> this")
      @NotNull Builder rotation(@Nullable ElementRotation var1);

      @Contract("_ -> this")
      @NotNull Builder shade(boolean var1);

      @Contract("_ -> this")
      @NotNull Builder faces(@NotNull Map<CubeFace, ElementFace> var1);

      @NotNull Builder addFace(@NotNull CubeFace var1, @NotNull ElementFace var2);

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_, _ -> this")
      default @NotNull Builder face(@NotNull CubeFace type, @NotNull ElementFace face) {
         return this.addFace(type, face);
      }

      @Contract("_ -> this")
      @NotNull Builder lightEmission(int var1);

      @Contract("-> new")
      @NotNull Element build();
   }
}
