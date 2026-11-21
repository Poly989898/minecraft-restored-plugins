package litebans;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public final class KotlinListImpl3Companion {
   private KotlinListImpl3Companion() {
   }

   public final void handleIntValue(int value, int value2) {
      if (value < 0 || value >= value2) {
         throw new IndexOutOfBoundsException("index: " + value + ", size: " + value2);
      }
   }

   public final void handleIntValue2(int value, int value2) {
      if (value < 0 || value > value2) {
         throw new IndexOutOfBoundsException("index: " + value + ", size: " + value2);
      }
   }

   public final void handleIntValue(int value, int value2, int value3) {
      if (value < 0 || value2 > value3) {
         throw new IndexOutOfBoundsException("fromIndex: " + value + ", toIndex: " + value2 + ", size: " + value3);
      }

      if (value > value2) {
         throw new IllegalArgumentException("fromIndex: " + value + " > toIndex: " + value2);
      }
   }

   public final int computeIntValue(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "c");
      int value = 1;

      for (Object obj : collection) {
         value = 31 * value + (obj != null ? obj.hashCode() : 0);
      }

      return value;
   }

   public final boolean checkCollection(@NotNull Collection collection, @NotNull Collection collection2) {
      Intrinsics.checkParameterIsNotNull(collection, "c");
      Intrinsics.checkParameterIsNotNull(collection2, "other");
      if (collection.size() != collection2.size()) {
         return false;
      }

      Iterator iterator2 = collection2.iterator();

      for (Object obj : collection) {
         Object obj2 = iterator2.next();
         if (!Intrinsics.areEqual(obj, obj2)) {
            return false;
         }
      }

      return true;
   }

   public KotlinListImpl3Companion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
