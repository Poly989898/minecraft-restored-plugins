package team.unnamed.creative.item;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemBooleanProperty;

record ConditionItemModelImpl(ItemBooleanProperty condition, ItemModel onTrue, ItemModel onFalse, Transformation transformation) implements ConditionItemModel {
   ConditionItemModelImpl(final @NotNull ItemBooleanProperty condition, final @NotNull ItemModel onTrue, final @NotNull ItemModel onFalse, final @Nullable Transformation transformation) {
      this.condition = (ItemBooleanProperty)Objects.requireNonNull(condition, "condition");
      this.onTrue = (ItemModel)Objects.requireNonNull(onTrue, "onTrue");
      this.onFalse = (ItemModel)Objects.requireNonNull(onFalse, "onFalse");
      this.transformation = transformation;
   }

   public @NotNull ItemBooleanProperty condition() {
      return this.condition;
   }

   public @NotNull ItemModel onTrue() {
      return this.onTrue;
   }

   public @NotNull ItemModel onFalse() {
      return this.onFalse;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{condition=" + String.valueOf(this.condition) + ", onTrue=" + String.valueOf(this.onTrue) + ", onFalse=" + String.valueOf(this.onFalse) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ConditionItemModelImpl that = (ConditionItemModelImpl)o;
         return this.condition.equals(that.condition) && this.onTrue.equals(that.onTrue) && this.onFalse.equals(that.onFalse) && Objects.equals(this.transformation, that.transformation);
      } else {
         return false;
      }
   }
}
