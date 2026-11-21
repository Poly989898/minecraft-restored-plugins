package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import org.jetbrains.annotations.Nullable;

public abstract class KotlinListImpl3 extends KotlinCollectionImpl2 implements List, KMappedMarker {
   public static final KotlinListImpl3Companion KOTLIN_LIST_IMPL3_COMPANION = new KotlinListImpl3Companion(null);
   private static final int VALUE_2147483639 = 2147483639;

   protected KotlinListImpl3() {
   }

   @Override
   public abstract int obtainIntValue();

   @Override
   public abstract Object get(int value);

   @Override
   public Iterator iterator() {
      return new KotlinIteratorImpl(this);
   }

   @Override
   public int indexOf(Object obj) {
      List list = this;
      int value = 0;
      Iterator iterator2 = list.iterator();

      int value2;
      while (true) {
         if (!iterator2.hasNext()) {
            value2 = -1;
            break;
         }

         Object obj2 = iterator2.next();
         if (Intrinsics.areEqual(obj2, obj)) {
            value2 = value;
            break;
         }

         value++;
      }

      return value2;
   }

   @Override
   public int lastIndexOf(Object obj) {
      List list = this;
      ListIterator listIterator2 = list.listIterator(list.size());

      int value;
      while (true) {
         if (listIterator2.hasPrevious()) {
            Object previous2 = listIterator2.previous();
            if (!Intrinsics.areEqual(previous2, obj)) {
               continue;
            }

            value = listIterator2.nextIndex();
            break;
         }

         value = -1;
         break;
      }

      return value;
   }

   @Override
   public ListIterator listIterator() {
      return new KotlinListIteratorImpl(this, 0);
   }

   @Override
   public ListIterator listIterator(int value) {
      return new KotlinListIteratorImpl(this, value);
   }

   @Override
   public List subList(int value, int value2) {
      return new KotlinRandomAccessImpl(this, value, value2);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof List) ? false : KOTLIN_LIST_IMPL3_COMPANION.checkCollection(this, (Collection)obj);
      }
   }

   @Override
   public int hashCode() {
      return KOTLIN_LIST_IMPL3_COMPANION.computeIntValue(this);
   }

   @Override
   public void add(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean addAll(int value, Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object set(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object remove(int value) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
