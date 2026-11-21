package team.unnamed.creative.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.Vector3Float;

public interface ItemTransform {
   float MIN_TRANSLATION = -80.0F;
   float MAX_TRANSLATION = 80.0F;
   float MIN_SCALE = -4.0F;
   float MAX_SCALE = 4.0F;
   Vector3Float DEFAULT_ROTATION = Vector3Float.ZERO;
   Vector3Float DEFAULT_TRANSLATION = Vector3Float.ZERO;
   Vector3Float DEFAULT_SCALE = Vector3Float.ONE;

   @NotNull Vector3Float rotation();

   @NotNull Vector3Float translation();

   @NotNull Vector3Float scale();

   static @NotNull ItemTransform transform(@NotNull Vector3Float rotation, @NotNull Vector3Float translation, @NotNull Vector3Float scale) {
      return new ItemTransformImpl(rotation, translation, scale);
   }

   static @NotNull Builder transform() {
      return new ItemTransformImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull ItemTransform of(@NotNull Vector3Float rotation, @NotNull Vector3Float translation, @NotNull Vector3Float scale) {
      return new ItemTransformImpl(rotation, translation, scale);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return new ItemTransformImpl.BuilderImpl();
   }

   public static enum Type {
      THIRDPERSON_RIGHTHAND,
      THIRDPERSON_LEFTHAND,
      FIRSTPERSON_RIGHTHAND,
      FIRSTPERSON_LEFTHAND,
      GUI,
      HEAD,
      GROUND,
      FIXED,
      ON_SHELF;
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder rotation(@NotNull Vector3Float var1);

      @Contract("_ -> this")
      @NotNull Builder translation(@NotNull Vector3Float var1);

      @Contract("_ -> this")
      @NotNull Builder scale(@NotNull Vector3Float var1);

      @Contract("-> this")
      @NotNull ItemTransform build();
   }
}
