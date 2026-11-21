package litebans;

public final class GeneralPurposeBit implements Cloneable {
   private static final int VALUE_1 = 1;
   private static final int VALUE_2 = 2;
   private static final int VALUE_4 = 4;
   private static final int VALUE_8 = 8;
   private static final int VALUE_64 = 64;
   public static final int VALUE_2048 = 2048;
   private boolean flag = false;
   private boolean flag2 = false;
   private boolean flag3 = false;
   private boolean flag4 = false;

   public boolean obtainFlag() {
      return this.flag2;
   }

   public boolean obtainFlag2() {
      return this.flag3;
   }

   @Override
   public int hashCode() {
      return 3 * (7 * (13 * (17 * (this.flag3 ? 1 : 0) + (this.flag4 ? 1 : 0)) + (this.flag ? 1 : 0)) + (this.flag2 ? 1 : 0));
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof GeneralPurposeBit)) {
         return false;
      }

      GeneralPurposeBit generalPurposeBit = (GeneralPurposeBit)obj;
      return generalPurposeBit.flag3 == this.flag3 && generalPurposeBit.flag4 == this.flag4 && generalPurposeBit.flag == this.flag && generalPurposeBit.flag2 == this.flag2;
   }

   @Override
   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException e) {
         throw new RuntimeException("GeneralPurposeBit is not Cloneable?", e);
      }
   }
}
