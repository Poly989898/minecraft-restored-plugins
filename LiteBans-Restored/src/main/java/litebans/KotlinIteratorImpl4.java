package litebans;

import java.util.Iterator;

public abstract class KotlinIteratorImpl4 implements Iterator, KMappedMarker {
   public abstract int obtainIntValue4();

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object next() {
      return this.obtainIntValue4();
   }
}
