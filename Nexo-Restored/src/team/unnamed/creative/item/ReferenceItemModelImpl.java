package team.unnamed.creative.item;

import java.util.List;
import java.util.Objects;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.tint.TintSource;

record ReferenceItemModelImpl(Key model, List<TintSource> tints, Transformation transformation) implements ReferenceItemModel {
   ReferenceItemModelImpl(final @NotNull Key model, final @NotNull List<TintSource> tints, final @Nullable Transformation transformation) {
      this.model = (Key)Objects.requireNonNull(model, "model");
      this.tints = (List)Objects.requireNonNull(tints, "tints");
      this.transformation = transformation;
   }

   public @NotNull Key model() {
      return this.model;
   }

   public @NotNull List<TintSource> tints() {
      return this.tints;
   }

   public @Nullable Transformation transformation() {
      return this.transformation;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{model=" + String.valueOf(this.model) + ", tints=" + String.valueOf(this.tints) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         ReferenceItemModelImpl that = (ReferenceItemModelImpl)o;
         return this.model.equals(that.model) && this.tints.equals(that.tints) && Objects.equals(this.transformation, that.transformation);
      } else {
         return false;
      }
   }
}
