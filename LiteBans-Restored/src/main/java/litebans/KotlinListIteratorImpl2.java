package litebans;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public final class KotlinListIteratorImpl2 implements ListIterator, KMappedMarker {
   public static final KotlinListIteratorImpl2 KOTLIN_LIST_ITERATOR_IMPL = new KotlinListIteratorImpl2();

   private KotlinListIteratorImpl2() {
   }

   @Override
   public boolean hasNext() {
      return false;
   }

   @Override
   public boolean hasPrevious() {
      return false;
   }

   @Override
   public int nextIndex() {
      return 0;
   }

   @Override
   public int previousIndex() {
      return -1;
   }

   public Void obtainVoid() {
      throw new NoSuchElementException();
   }

   public Void obtainVoid2() {
      throw new NoSuchElementException();
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object next() {
      return this.obtainVoid();
   }

   @Override
   public Object previous() {
      return this.obtainVoid2();
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
