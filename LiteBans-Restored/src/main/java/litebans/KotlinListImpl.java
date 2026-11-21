package litebans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinListImpl implements List, Serializable, RandomAccess, KMappedMarker {
   public static final KotlinListImpl return2 = new KotlinListImpl();
   private static final long VALUE_MINUS_7390468764508069838 = -7390468764508069838L;

   private KotlinListImpl() {
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return obj instanceof List && ((List)obj).isEmpty();
   }

   @Override
   public int hashCode() {
      return 1;
   }

   @Override
   public String toString() {
      return "[]";
   }

   public int obtainIntValue3() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   public boolean checkVoid(@NotNull Void void2) {
      Intrinsics.checkParameterIsNotNull(void2, "element");
      return false;
   }

   @Override
   public boolean containsAll(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "elements");
      return collection.isEmpty();
   }

   public Void computeVoid(int value) {
      throw new IndexOutOfBoundsException("Empty list doesn't contain element at index " + value + '.');
   }

   public int computeIntValue(@NotNull Void void2) {
      Intrinsics.checkParameterIsNotNull(void2, "element");
      return -1;
   }

   public int computeIntValue2(@NotNull Void void2) {
      Intrinsics.checkParameterIsNotNull(void2, "element");
      return -1;
   }

   @Override
   public Iterator iterator() {
      return KotlinListIteratorImpl2.KOTLIN_LIST_ITERATOR_IMPL;
   }

   @Override
   public ListIterator listIterator() {
      return KotlinListIteratorImpl2.KOTLIN_LIST_ITERATOR_IMPL;
   }

   @Override
   public ListIterator listIterator(int value) {
      if (value != 0) {
         throw new IndexOutOfBoundsException("Index: " + value);
      } else {
         return KotlinListIteratorImpl2.KOTLIN_LIST_ITERATOR_IMPL;
      }
   }

   @Override
   public List subList(int value, int value2) {
      if (value == 0 && value2 == 0) {
         return this;
      } else {
         throw new IndexOutOfBoundsException("fromIndex: " + value + ", toIndex: " + value2);
      }
   }

   private final Object obtainObj() {
      return return2;
   }

   @Override
   public boolean remove(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean addAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean addAll(int value, Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean removeAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public boolean retainAll(Collection collection) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object remove(int value) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public final int size() {
      return this.obtainIntValue3();
   }

   @Override
   public final boolean contains(Object obj) {
      return !(obj instanceof Void) ? false : this.checkVoid((Void)obj);
   }

   @Override
   public Object get(int value) {
      return this.computeVoid(value);
   }

   @Override
   public final int indexOf(Object obj) {
      return !(obj instanceof Void) ? -1 : this.computeIntValue((Void)obj);
   }

   @Override
   public final int lastIndexOf(Object obj) {
      return !(obj instanceof Void) ? -1 : this.computeIntValue2((Void)obj);
   }

   @Override
   public boolean add(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void add(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object set(int value, Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object[] toArray(Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "array");
      return KotlinHelper12.computeObjArray(this, objs);
   }

   @Override
   public Object[] toArray() {
      return KotlinHelper12.computeObjArray(this);
   }
}
