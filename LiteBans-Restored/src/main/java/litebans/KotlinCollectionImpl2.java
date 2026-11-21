package litebans;

import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public abstract class KotlinCollectionImpl2 implements Collection, KMappedMarker {
   protected KotlinCollectionImpl2() {
   }

   public abstract int obtainIntValue();

   @Override
   public abstract Iterator iterator();

   @Override
   public boolean contains(Object obj) {
      boolean flag;
      if (this instanceof Collection && this.isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = this.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj2 = iterator2.next();
            if (Intrinsics.areEqual(obj2, obj)) {
               flag = true;
               break;
            }
         }
      }

      return flag;
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
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public String toString() {
      return CollectionsKt.joinToStringWithDefaults(this, ", ", "[", "]", 0, null, obj -> obj == this ? "(this Collection)" : String.valueOf(obj), 24, null);
   }

   @Override
   public Object[] toArray() {
      return KotlinHelper12.computeObjArray(this);
   }

   @Override
   public Object[] toArray(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "array");
      return KotlinHelper12.computeObjArray(this, objs);
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
      return this.obtainIntValue();
   }
}
