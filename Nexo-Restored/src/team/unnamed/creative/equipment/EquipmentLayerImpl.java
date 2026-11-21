package team.unnamed.creative.equipment;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record EquipmentLayerImpl(Key texture, EquipmentLayerDye dye, boolean usePlayerTexture) implements EquipmentLayer {
   EquipmentLayerImpl(final @NotNull Key texture, final @Nullable EquipmentLayerDye dye, final boolean usePlayerTexture) {
      this.texture = (Key)Objects.requireNonNull(texture, "texture");
      this.dye = dye;
      this.usePlayerTexture = usePlayerTexture;
   }

   public @NotNull Key texture() {
      return this.texture;
   }

   public @Nullable EquipmentLayerDye dye() {
      return this.dye;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{texture=" + String.valueOf(this.texture) + ", dye=" + String.valueOf(this.dye) + ", usePlayerTexture=" + this.usePlayerTexture + "}";
   }

   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof EquipmentLayerImpl)) {
         return false;
      } else {
         EquipmentLayerImpl that = (EquipmentLayerImpl)obj;
         return this.texture.equals(that.texture) && Objects.equals(this.dye, that.dye) && this.usePlayerTexture == that.usePlayerTexture;
      }
   }
}
