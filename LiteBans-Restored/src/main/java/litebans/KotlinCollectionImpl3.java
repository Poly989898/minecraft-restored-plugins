package litebans;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

final class KotlinCollectionImpl3 implements Collection, KMappedMarker {
   private final Object[] objs;
   private final boolean flag5;

   public KotlinCollectionImpl3(@NotNull Object[] objs, boolean flag) {
      Intrinsics.checkParameterIsNotNull(objs, "values");
      super();
      this.objs = objs;
      this.flag5 = flag;
   }

   public int obtainIntValue8() {
      return this.objs.length;
   }

   @Override
   public boolean isEmpty() {
      return this.objs.length == 0;
   }

   @Override
   public boolean contains(Object obj) {
      return ArraysKt.contains(this.objs, obj);
   }

   @Override
   public boolean containsAll(@NotNull Collection collection) {
      Intrinsics.checkParameterIsNotNull(collection, "elements");
      var collection2 = collection;
      boolean flag;
      if (((Collection)collection2).isEmpty()) {
         flag = true;
      } else {
         Iterator iterator2 = collection2.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = true;
               break;
            }

            Object obj = iterator2.next();
            if (!this.contains(obj)) {
               flag = false;
               break;
            }
         }
      }

      return flag;
   }

   @Override
   public Iterator iterator() {
      return KotlinUtil12.computeIterator(this.objs);
   }

   @Override
   public final Object[] toArray() {
      return CollectionsKt.computeObjArray(this.objs, this.flag5);
   }

   @Override
   public boolean add(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
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
      return this.obtainIntValue8();
   }

   @Override
   public Object[] toArray(Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "array");
      return KotlinHelper12.computeObjArray(this, objs);
   }
}
