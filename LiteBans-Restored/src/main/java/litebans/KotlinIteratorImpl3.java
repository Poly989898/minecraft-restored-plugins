package litebans;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jetbrains.annotations.NotNull;

final class KotlinIteratorImpl3 implements Iterator, KMappedMarker {
   private final Object[] objArray;
   private int intValue10;

   public KotlinIteratorImpl3(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "array");
      super();
      this.objArray = objs;
   }

   @Override
   public boolean hasNext() {
      return this.intValue10 < this.objArray.length;
   }

   @Override
   public Object next() {
      try {
         Object[] objs = this.objArray;
         int value = this.intValue10++;
         return objs[value];
      } catch (ArrayIndexOutOfBoundsException e) {
         this.intValue10--;
         throw new NoSuchElementException(e.getMessage());
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
