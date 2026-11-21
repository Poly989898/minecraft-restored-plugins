package team.unnamed.creative.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.item.property.ItemNumericProperty;

record RangeDispatchItemModelImpl(ItemNumericProperty property, float scale, List<RangeDispatchItemModel.Entry> entries, ItemModel fallback, Transformation transformation) implements RangeDispatchItemModel {
   RangeDispatchItemModelImpl(final @NotNull ItemNumericProperty property, final float scale, final @NotNull List<RangeDispatchItemModel.Entry> entries, final @Nullable ItemModel fallback, final @Nullable Transformation transformation) {
      this.property = (ItemNumericProperty)Objects.requireNonNull(property, "property");
      this.scale = scale;
      this.entries = (List)Objects.requireNonNull(entries, "entries");
      this.fallback = fallback;
      this.transformation = transformation;
   }

   public @NotNull ItemNumericProperty property() {
      return this.property;
   }

   public @NotNull List<RangeDispatchItemModel.Entry> entries() {
      return this.entries;
   }

   public @Nullable ItemModel fallback() {
      return this.fallback;
   }

   public @NotNull String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "{property=" + String.valueOf(this.property) + ", scale=" + this.scale + ", entries=" + String.valueOf(this.entries) + ", fallback=" + String.valueOf(this.fallback) + ", transformation=" + String.valueOf(this.transformation) + "}";
   }

   public boolean equals(Object o) {
      if (o != null && this.getClass() == o.getClass()) {
         RangeDispatchItemModelImpl that = (RangeDispatchItemModelImpl)o;
         return Float.compare(that.scale, this.scale) == 0 && this.property.equals(that.property) && this.entries.equals(that.entries) && Objects.equals(this.transformation, that.transformation) && Objects.equals(this.fallback, that.fallback);
      } else {
         return false;
      }
   }

   static record EntryImpl(float threshold, ItemModel model, Transformation transformation) implements RangeDispatchItemModel.Entry {
      EntryImpl(final float threshold, final @NotNull ItemModel model, final @Nullable Transformation transformation) {
         this.threshold = threshold;
         this.model = (ItemModel)Objects.requireNonNull(model, "model");
         this.transformation = transformation;
      }

      public @NotNull ItemModel model() {
         return this.model;
      }

      public @NotNull String toString() {
         String var10000 = this.getClass().getSimpleName();
         return var10000 + "{threshold=" + this.threshold + ", model=" + String.valueOf(this.model) + ", transformation=" + String.valueOf(this.transformation) + "}";
      }

      public boolean equals(Object o) {
         if (o != null && this.getClass() == o.getClass()) {
            EntryImpl entry = (EntryImpl)o;
            return Float.compare(entry.threshold, this.threshold) == 0 && this.model.equals(entry.model) && Objects.equals(this.transformation, entry.transformation);
         } else {
            return false;
         }
      }
   }

   static final class BuilderImpl implements RangeDispatchItemModel.Builder {
      private ItemNumericProperty property;
      private float scale = 1.0F;
      private final List<RangeDispatchItemModel.Entry> entries = new ArrayList();
      private ItemModel fallback;
      private Transformation transformation;

      public RangeDispatchItemModel.@NotNull Builder transformation(@Nullable Transformation transformation) {
         this.transformation = transformation;
         return this;
      }

      public RangeDispatchItemModel.@NotNull Builder property(@NotNull ItemNumericProperty property) {
         this.property = (ItemNumericProperty)Objects.requireNonNull(property, "property");
         return this;
      }

      public RangeDispatchItemModel.@NotNull Builder scale(float scale) {
         this.scale = scale;
         return this;
      }

      public RangeDispatchItemModel.@NotNull Builder addEntry(RangeDispatchItemModel.@NotNull Entry entry) {
         this.entries.add((RangeDispatchItemModel.Entry)Objects.requireNonNull(entry, "entry"));
         return this;
      }

      public RangeDispatchItemModel.@NotNull Builder fallback(@Nullable ItemModel fallback) {
         this.fallback = fallback;
         return this;
      }

      public @NotNull RangeDispatchItemModel build() {
         return new RangeDispatchItemModelImpl(this.property, this.scale, this.entries, this.fallback, this.transformation);
      }
   }
}
