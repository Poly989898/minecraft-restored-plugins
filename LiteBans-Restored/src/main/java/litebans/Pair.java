package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

public final class Pair implements Serializable {
   private final Object first;
   private final Object second;

   public Pair(Object obj, Object obj2) {
      this.first = obj;
      this.second = obj2;
   }

   public final Object getFirst() {
      return this.first;
   }

   public final Object getSecond() {
      return this.second;
   }

   @Override
   public String toString() {
      return "" + '(' + this.first + ", " + this.second + ')';
   }

   public final Object component1() {
      return this.first;
   }

   public final Object component2() {
      return this.second;
   }

   @Override
   public int hashCode() {
      int value = this.first == null ? 0 : this.first.hashCode();
      return value * 31 + (this.second == null ? 0 : this.second.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Pair)) {
         return false;
      }

      Pair pair = (Pair)obj;
      return !Intrinsics.areEqual(this.first, pair.first) ? false : Intrinsics.areEqual(this.second, pair.second);
   }
}
