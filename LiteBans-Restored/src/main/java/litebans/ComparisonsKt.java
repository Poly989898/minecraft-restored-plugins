package litebans;

import org.jetbrains.annotations.Nullable;

class ComparisonsKt {
   public static final int computeIntValue(@Nullable Comparable comparable, @Nullable Comparable comparable2) {
      if (comparable == comparable2) {
         return 0;
      } else if (comparable == null) {
         return -1;
      } else {
         return comparable2 == null ? 1 : comparable.compareTo(comparable2);
      }
   }
}
