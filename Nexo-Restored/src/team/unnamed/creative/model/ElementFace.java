package team.unnamed.creative.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector4Float;
import team.unnamed.creative.texture.TextureUV;

public interface ElementFace {
   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   float MINECRAFT_UV_UNIT = 16.0F;
   int DEFAULT_ROTATION = 0;
   int DEFAULT_TINT_INDEX = -1;

   static @NotNull ElementFace face(@Nullable TextureUV uv, @NotNull String texture, @Nullable CubeFace cullFace, int rotation, int tintIndex) {
      return new ElementFaceImpl(uv, texture, cullFace, rotation, tintIndex);
   }

   static @NotNull Builder face() {
      return new ElementFaceImpl.BuilderImpl();
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull ElementFace of(@Nullable Vector4Float uv, @NotNull String texture, @Nullable CubeFace cullFace, int rotation, int tintIndex) {
      return face(uv == null ? null : TextureUV.uv(uv.x(), uv.y(), uv.x2(), uv.y2()), texture, cullFace, rotation, tintIndex);
   }

   /** @deprecated */
   @Deprecated
   @ScheduledForRemoval(
      inVersion = "2.0.0"
   )
   static @NotNull Builder builder() {
      return new ElementFaceImpl.BuilderImpl();
   }

   @Nullable TextureUV uv();

   @NotNull String texture();

   @Nullable CubeFace cullFace();

   int rotation();

   int tintIndex();

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder uv(@Nullable TextureUV var1);

      /** @deprecated */
      @Deprecated
      @ScheduledForRemoval(
         inVersion = "2.0.0"
      )
      @Contract("_ -> this")
      default @NotNull Builder uv(@Nullable Vector4Float uv) {
         return this.uv(uv == null ? null : TextureUV.uv(new Vector2Float(uv.x(), uv.y()), new Vector2Float(uv.x2(), uv.y2())));
      }

      @Contract("_ -> this")
      @NotNull Builder texture(@NotNull String var1);

      @Contract("_ -> this")
      @NotNull Builder cullFace(@Nullable CubeFace var1);

      @Contract("_ -> this")
      @NotNull Builder rotation(int var1);

      @Contract("_ -> this")
      @NotNull Builder tintIndex(int var1);

      @NotNull ElementFace build();
   }
}
