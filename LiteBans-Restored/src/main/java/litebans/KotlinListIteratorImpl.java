package litebans;

import java.util.ListIterator;
import java.util.NoSuchElementException;

class KotlinListIteratorImpl extends KotlinIteratorImpl implements ListIterator, KMappedMarker {
   final KotlinListImpl3 kotlinListImpl;

   public KotlinListIteratorImpl(KotlinListImpl3 kotlinList, int value) {
      super(kotlinList);
      this.kotlinListImpl = kotlinList;
      KotlinListImpl3.KOTLIN_LIST_IMPL3_COMPANION.handleIntValue2(value, this.kotlinListImpl.size());
      this.handleIntValue(value);
   }

   @Override
   public boolean hasPrevious() {
      return this.obtainIntValue5() > 0;
   }

   @Override
   public int nextIndex() {
      return this.obtainIntValue5();
   }

   @Override
   public Object previous() {
      if (!this.hasPrevious()) {
         throw new NoSuchElementException();
      }

      KotlinListImpl3 kotlinList = this.kotlinListImpl;
      this.handleIntValue(this.obtainIntValue5() + -1);
      return kotlinList.get(this.obtainIntValue5());
   }

   @Override
   public int previousIndex() {
      return this.obtainIntValue5() - 1;
   }

   @Override
   public void set(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void add(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
