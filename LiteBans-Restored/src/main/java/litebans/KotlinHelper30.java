package litebans;

import java.util.NoSuchElementException;

public final class KotlinHelper30 extends KotlinIteratorImpl4 {
   private final int intValue12;
   private final int intValue13;
   private boolean flag4;
   private int intValue14;

   public KotlinHelper30(int value, int value2, int value3) {
      this.intValue12 = value3;
      this.intValue13 = value2;
      this.flag4 = this.intValue12 > 0 ? value <= value2 : value >= value2;
      this.intValue14 = this.flag4 ? value : this.intValue13;
   }

   @Override
   public boolean hasNext() {
      return this.flag4;
   }

   @Override
   public int obtainIntValue4() {
      int value = this.intValue14;
      if (value == this.intValue13) {
         if (!this.flag4) {
            throw new NoSuchElementException();
         }

         this.flag4 = false;
      } else {
         this.intValue14 = this.intValue14 + this.intValue12;
      }

      return value;
   }
}
