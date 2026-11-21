package team.unnamed.creative.item;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

record ItemImpl(Key key, ItemModel model, boolean handAnimationOnSwap, boolean oversizedInGui, float swapAnimationScale) implements Item {
   ItemImpl(final @NotNull Key key, final @NotNull ItemModel model, final boolean handAnimationOnSwap, final boolean oversizedInGui, final float swapAnimationScale) {
      this.key = (Key)Objects.requireNonNull(key, "key");
      this.model = (ItemModel)Objects.requireNonNull(model, "model");
      this.handAnimationOnSwap = handAnimationOnSwap;
      this.oversizedInGui = oversizedInGui;
      this.swapAnimationScale = swapAnimationScale;
   }

   public @NotNull Key key() {
      return this.key;
   }

   public @NotNull ItemModel model() {
      return this.model;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{key=" + String.valueOf(this.key) + ", model=" + String.valueOf(this.model) + ", handAnimationOnSwap=" + this.handAnimationOnSwap + ", oversized_in_gui=" + this.oversizedInGui + ", swap_animation_scale=" + this.swapAnimationScale + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ItemImpl item = (ItemImpl)o;
         return this.key.equals(item.key) && this.model.equals(item.model) && this.handAnimationOnSwap == item.handAnimationOnSwap && this.oversizedInGui == item.oversizedInGui && this.swapAnimationScale == item.swapAnimationScale;
      } else {
         return false;
      }
   }
}
