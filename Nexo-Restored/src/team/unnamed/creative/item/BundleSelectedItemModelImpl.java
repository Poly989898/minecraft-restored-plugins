package team.unnamed.creative.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BundleSelectedItemModelImpl implements BundleSelectedItemModel {
   static final BundleSelectedItemModel INSTANCE = new BundleSelectedItemModelImpl();

   private BundleSelectedItemModelImpl() {
   }

   public boolean equals(@Nullable Object obj) {
      return obj instanceof BundleSelectedItemModelImpl;
   }

   public @NotNull String toString() {
      return this.getClass().getSimpleName() + "{}";
   }

   public int hashCode() {
      return BundleSelectedItemModelImpl.class.hashCode();
   }

   public @Nullable Transformation transformation() {
      return null;
   }
}
