package litebans;

import java.util.Iterator;
import java.util.NoSuchElementException;

class KotlinIteratorImpl implements Iterator, KMappedMarker {
   private int intValue11;
   final KotlinListImpl3 kotlinListImpl2;

   public KotlinIteratorImpl(KotlinListImpl3 kotlinList) {
      this.kotlinListImpl2 = kotlinList;
   }

   protected final int obtainIntValue5() {
      return this.intValue11;
   }

   protected final void handleIntValue(int value) {
      this.intValue11 = value;
   }

   @Override
   public boolean hasNext() {
      return this.intValue11 < this.kotlinListImpl2.size();
   }

   @Override
   public Object next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      }

      KotlinListImpl3 kotlinList = this.kotlinListImpl2;
      int value = this.intValue11++;
      return kotlinList.get(value);
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
