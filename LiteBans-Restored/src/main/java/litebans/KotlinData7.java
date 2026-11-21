package litebans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinData7 implements Set, Serializable, KMappedMarker {
   public static final KotlinData7 return4 = new KotlinData7();
   private static final long VALUE_3406603774387020532 = 3406603774387020532L;

   private KotlinData7() {
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return obj instanceof Set && ((Set)obj).isEmpty();
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "[]";
   }

   public int obtainIntValue6() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   public boolean checkVoid2(@NotNull Void void2) {
      Intrinsics.checkParameterIsNotNull(void2, "element");
      return false;
   }

   @Override
   public boolean containsAll(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "elements");
      return collection.isEmpty();
   }

   @Override
   public Iterator iterator() {
      return KotlinListIteratorImpl2.KOTLIN_LIST_ITERATOR_IMPL;
   }

   private final Object obtainObj3() {
      return return4;
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
   public final int size() {
      return this.obtainIntValue6();
   }

   @Override
   public final boolean contains(Object obj) {
      return !(obj instanceof Void) ? false : this.checkVoid2((Void)obj);
   }

   @Override
   public boolean add(Object obj) {
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
