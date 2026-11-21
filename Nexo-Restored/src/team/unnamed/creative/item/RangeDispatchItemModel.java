package team.unnamed.creative.item;

import java.util.List;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.creative.item.property.ItemNumericProperty;

public interface RangeDispatchItemModel extends ItemModel {
   @Internal
   float DEFAULT_SCALE = 1.0F;

   @NotNull ItemNumericProperty property();

   float scale();

   @NotNull List<Entry> entries();

   @Nullable ItemModel fallback();

   @Contract("-> new")
   default @NotNull Builder toBuilder() {
      return ItemModel.rangeDispatch().property(this.property()).scale(this.scale()).fallback(this.fallback()).addEntries(this.entries());
   }

   public interface Entry {
      float threshold();

      @NotNull ItemModel model();

      static @NotNull Entry entry(float threshold, @NotNull ItemModel model, @Nullable Transformation transformation) {
         return new RangeDispatchItemModelImpl.EntryImpl(threshold, model, transformation);
      }

      static @NotNull Entry entry(float threshold, @NotNull ItemModel model) {
         return new RangeDispatchItemModelImpl.EntryImpl(threshold, model, (Transformation)null);
      }
   }

   public interface Builder {
      @Contract("_ -> this")
      @NotNull Builder transformation(@Nullable Transformation var1);

      @Contract("_ -> this")
      @NotNull Builder property(@NotNull ItemNumericProperty var1);

      @Contract("_ -> this")
      @NotNull Builder scale(float var1);

      @Contract("_ -> this")
      @NotNull Builder addEntry(@NotNull Entry var1);

      @Contract("_, _ -> this")
      default @NotNull Builder addEntry(float threshold, @NotNull ItemModel model) {
         return this.addEntry(RangeDispatchItemModel.Entry.entry(threshold, model));
      }

      @Contract("_ -> this")
      default @NotNull Builder addEntries(@NotNull List<Entry> entries) {
         entries.forEach(this::addEntry);
         return this;
      }

      @Contract("_ -> this")
      @NotNull Builder fallback(@Nullable ItemModel var1);

      @Contract("-> new")
      @NotNull RangeDispatchItemModel build();
   }
}
