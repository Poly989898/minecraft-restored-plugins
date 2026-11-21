package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.Nullable;

public final class Triple implements Serializable {
   private final Object first;
   private final Object second;
   private final Object third;

   public Triple(Object obj, Object obj2, Object obj3) {
      this.first = obj;
      this.second = obj2;
      this.third = obj3;
   }

   public final Object getFirst() {
      return this.first;
   }

   public final Object getSecond() {
      return this.second;
   }

   public final Object getThird() {
      return this.third;
   }

   @Override
   public String toString() {
      return "" + '(' + this.first + ", " + this.second + ", " + this.third + ')';
   }

   public final Object component1() {
      return this.first;
   }

   public final Object component2() {
      return this.second;
   }

   public final Object component3() {
      return this.third;
   }

   @Override
   public int hashCode() {
      int value = this.first == null ? 0 : this.first.hashCode();
      int value2 = value * 31 + (this.second == null ? 0 : this.second.hashCode());
      return value2 * 31 + (this.third == null ? 0 : this.third.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Triple)) {
         return false;
      } else {
         Triple triple = (Triple)obj;
         if (!Intrinsics.areEqual(this.first, triple.first)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.second, triple.second) ? false : Intrinsics.areEqual(this.third, triple.third);
         }
      }
   }
}
