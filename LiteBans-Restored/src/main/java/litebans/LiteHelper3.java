package litebans;

import org.jetbrains.annotations.NotNull;

public final class LiteHelper3 {
   private final Object obj2;
   private boolean flag2;

   public LiteHelper3(@NotNull Object obj, boolean flag) {
      this.obj2 = obj;
      this.flag2 = flag;
   }

   public LiteHelper3(Object obj, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         flag = false;
      }

      this(obj, flag);
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final void setFlag2(boolean flag) {
      this.flag2 = flag;
   }
}
