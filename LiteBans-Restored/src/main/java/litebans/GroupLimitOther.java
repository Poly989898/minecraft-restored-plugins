package litebans;

import org.jetbrains.annotations.Nullable;

public final class GroupLimitOther {
   private final boolean flag2;

   public GroupLimitOther(boolean flag) {
      this.flag2 = flag;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   @Override
   public String toString() {
      return "GroupLimitOther(templateOnly=" + this.flag2 + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.flag2);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof GroupLimitOther)) {
         return false;
      }

      GroupLimitOther groupLimitOther = (GroupLimitOther)obj;
      return this.flag2 == groupLimitOther.flag2;
   }
}
