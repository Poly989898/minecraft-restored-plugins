package litebans;

public final class StateFlags {
   private boolean flag3;
   private boolean flag4;

   public StateFlags(boolean flag, boolean flag2) {
      this.flag3 = flag;
      this.flag4 = flag2;
   }

   public StateFlags(boolean flag, boolean flag2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         flag = false;
      }

      if ((value & 2) != 0) {
         flag2 = false;
      }

      this(flag, flag2);
   }

   public final boolean isFlag3() {
      return this.flag3;
   }

   public final void setFlag3(boolean flag) {
      this.flag3 = flag;
   }

   public final boolean isFlag4() {
      return this.flag4;
   }

   public StateFlags() {
      this(false, false, 3, null);
   }
}
