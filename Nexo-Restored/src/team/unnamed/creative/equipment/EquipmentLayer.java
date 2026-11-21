package team.unnamed.creative.equipment;

import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface EquipmentLayer {
   @Internal
   boolean DEFAULT_USE_PLAYER_TEXTURE = false;

   @NotNull Key texture();

   @Nullable EquipmentLayerDye dye();

   boolean usePlayerTexture();

   @Contract(
      value = "_, _, _ -> new",
      pure = true
   )
   static @NotNull EquipmentLayer layer(@NotNull Key texture, @Nullable EquipmentLayerDye dye, boolean usePlayerTexture) {
      return new EquipmentLayerImpl(texture, dye, usePlayerTexture);
   }

   @Contract(
      value = "_, _ -> new",
      pure = true
   )
   static @NotNull EquipmentLayer layer(@NotNull Key texture, @Nullable EquipmentLayerDye dye) {
      return layer(texture, dye, false);
   }

   @Contract(
      value = "_, _ -> new",
      pure = true
   )
   static @NotNull EquipmentLayer layer(@NotNull Key texture, boolean usePlayerTexture) {
      return layer(texture, (EquipmentLayerDye)null, usePlayerTexture);
   }

   @Contract(
      value = "_ -> new",
      pure = true
   )
   static @NotNull EquipmentLayer layer(@NotNull Key texture) {
      return layer(texture, (EquipmentLayerDye)null, false);
   }
}
