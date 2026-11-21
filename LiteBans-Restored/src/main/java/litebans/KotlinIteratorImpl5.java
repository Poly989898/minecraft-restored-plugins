package litebans;

import java.util.Iterator;
import java.util.NoSuchElementException;

class KotlinIteratorImpl5 implements Iterator {
   private int intValue;
   final FastList fastList;

   KotlinIteratorImpl5(FastList fastList) {
      this.fastList = fastList;
   }

   @Override
   public boolean hasNext() {
      return this.intValue < FastList.access$intValue(this.fastList);
   }

   @Override
   public Object next() {
      if (this.intValue < FastList.access$intValue(this.fastList)) {
         return FastList.access$objArray(this.fastList)[this.intValue++];
      } else {
         throw new NoSuchElementException("No more elements in FastList");
      }
   }
}
