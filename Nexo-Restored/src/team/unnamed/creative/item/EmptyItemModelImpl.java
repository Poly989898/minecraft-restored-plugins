package team.unnamed.creative.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EmptyItemModelImpl implements EmptyItemModel {
   static final EmptyItemModel INSTANCE = new EmptyItemModelImpl();

   private EmptyItemModelImpl() {
   }

   public boolean equals(@Nullable Object obj) {
      return obj instanceof EmptyItemModelImpl;
   }

   public @NotNull String toString() {
      return this.getClass().getSimpleName() + "{}";
   }

   public int hashCode() {
      return EmptyItemModelImpl.class.hashCode();
   }

   public @Nullable Transformation transformation() {
      return null;
   }
}
