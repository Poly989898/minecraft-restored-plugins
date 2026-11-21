package team.unnamed.creative.item;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

record CompositeItemModelImpl(List<ItemModel> models, @Nullable Transformation transformation) implements CompositeItemModel {
   CompositeItemModelImpl(final @NotNull List<ItemModel> models, final @Nullable Transformation transformation) {
      this.models = (List)Objects.requireNonNull(models, "models");
      this.transformation = transformation;
   }

   public @NotNull List<ItemModel> models() {
      return this.models;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{models=" + String.valueOf(this.models) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(@Nullable Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         CompositeItemModelImpl that = (CompositeItemModelImpl)o;
         return Objects.equals(this.models, that.models) && Objects.equals(this.transformation, that.transformation);
      } else {
         return false;
      }
   }
}
