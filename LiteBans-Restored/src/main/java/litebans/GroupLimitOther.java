package litebans;

import org.jetbrains.annotations.Nullable;

public final class GroupLimitOther {
   private final boolean templateOnly;

   public GroupLimitOther(boolean flag) {
      this.templateOnly = flag;
   }

   public final boolean isTemplateOnly() {
      return this.templateOnly;
   }

   @Override
   public String toString() {
      return "GroupLimitOther(templateOnly=" + this.templateOnly + ')';
   }

   @Override
   public int hashCode() {
      return Boolean.hashCode(this.templateOnly);
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
      return this.templateOnly == groupLimitOther.templateOnly;
   }
}
