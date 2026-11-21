package team.unnamed.creative.model;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.texture.TextureUV;

record ElementFaceImpl(@Nullable TextureUV uv, String texture, @Nullable CubeFace cullFace, int rotation, int tintIndex) implements ElementFace {
   ElementFaceImpl(final @Nullable TextureUV uv, final @NotNull String texture, final @Nullable CubeFace cullFace, final int rotation, final int tintIndex) {
      this.uv = uv;
      this.texture = (String)Objects.requireNonNull(texture, "texture");
      this.cullFace = cullFace;
      this.rotation = rotation;
      this.tintIndex = tintIndex;
      this.validate();
   }

   private void validate() {
      if (this.rotation % 90 != 0 || this.rotation < 0 || this.rotation > 270) {
         throw new IllegalArgumentException("Rotation must be a positive multiple of 90");
      }
   }

   public @NotNull String texture() {
      return this.texture;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{uv=" + String.valueOf(this.uv) + ", texture=" + this.texture + ", cullFace=" + String.valueOf(this.cullFace) + ", rotation=" + this.rotation + ", tintIndex=" + this.tintIndex + "}";
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ElementFaceImpl that = (ElementFaceImpl)o;
         return this.rotation == that.rotation && Objects.equals(this.uv, that.uv) && this.texture.equals(that.texture) && this.cullFace == that.cullFace && this.tintIndex == that.tintIndex;
      } else {
         return false;
      }
   }

   static final class BuilderImpl implements ElementFace.Builder {
      private TextureUV uv;
      private String texture;
      private CubeFace cullFace;
      private int rotation = 0;
      private int tintIndex = -1;

      public ElementFace.@NotNull Builder uv(@Nullable TextureUV uv) {
         this.uv = uv;
         return this;
      }

      public ElementFace.@NotNull Builder texture(@NotNull String texture) {
         this.texture = (String)Objects.requireNonNull(texture, "texture");
         return this;
      }

      public ElementFace.@NotNull Builder cullFace(@Nullable CubeFace cullFace) {
         this.cullFace = cullFace;
         return this;
      }

      public ElementFace.@NotNull Builder rotation(int rotation) {
         this.rotation = rotation;
         return this;
      }

      public ElementFace.@NotNull Builder tintIndex(int tintIndex) {
         this.tintIndex = tintIndex;
         return this;
      }

      public @NotNull ElementFace build() {
         return new ElementFaceImpl(this.uv, this.texture, this.cullFace, this.rotation, this.tintIndex);
      }
   }
}
