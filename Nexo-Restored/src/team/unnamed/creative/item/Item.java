package team.unnamed.creative.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.part.ResourcePackPart;

public interface Item extends Keyed, ResourcePackPart {
   @Internal
   boolean DEFAULT_HAND_ANIMATION_ON_SWAP = true;
   @Internal
   boolean DEFAULT_OVERSIZED_IN_GUI = false;
   @Internal
   float DEFAULT_SWAP_ANIMATION_SCALE = 1.0F;
   @Internal
   Transformation DEFAULT_TRANSFORMATION = Transformation.DEFAULT;

   boolean handAnimationOnSwap();

   boolean oversizedInGui();

   float swapAnimationScale();

   @NotNull ItemModel model();

   default void addTo(@NotNull ResourceContainer resourceContainer) {
      resourceContainer.item(this);
   }

   @Contract(
      value = "_, _, _, _ -> new",
      pure = true
   )
   static @NotNull Item item(@NotNull Key key, @NotNull ItemModel model, boolean handAnimationOnSwap, boolean oversizedInGui, float swapAnimationScale) {
      return new ItemImpl(key, model, handAnimationOnSwap, oversizedInGui, swapAnimationScale);
   }

   @Contract(
      value = "_, _, _, _ -> new",
      pure = true
   )
   static @NotNull Item item(@NotNull Key key, @NotNull ItemModel model, boolean handAnimationOnSwap, boolean oversizedInGui) {
      return new ItemImpl(key, model, handAnimationOnSwap, oversizedInGui, 1.0F);
   }

   @Contract(
      value = "_, _, _ -> new",
      pure = true
   )
   static @NotNull Item item(@NotNull Key key, @NotNull ItemModel model, boolean handAnimationOnSwap) {
      return new ItemImpl(key, model, handAnimationOnSwap, false, 1.0F);
   }

   static @NotNull Item item(@NotNull Key key, @NotNull ItemModel model) {
      return item(key, model, true);
   }
}
