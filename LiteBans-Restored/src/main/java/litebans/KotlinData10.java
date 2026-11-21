package litebans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class KotlinData10 implements Map, Serializable, KMappedMarker {
   public static final KotlinData10 return5 = new KotlinData10();
   private static final long VALUE_8246714829545688274 = 8246714829545688274L;

   private KotlinData10() {
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return obj instanceof Map && ((Map)obj).isEmpty();
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public String toString() {
      return "{}";
   }

   public int obtainIntValue7() {
      return 0;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public boolean containsKey(@Nullable Object obj) {
      return false;
   }

   public boolean checkVoid3(@NotNull Void void2) {
      Intrinsics.checkParameterIsNotNull(void2, "value");
      return false;
   }

   public Void computeVoid(@Nullable Object obj) {
      return null;
   }

   public Set obtainSet() {
      return KotlinData7.return4;
   }

   public Set obtainSet2() {
      return KotlinData7.return4;
   }

   public Collection obtainCollection() {
      return KotlinListImpl.return2;
   }

   private final Object obtainObj4() {
      return return5;
   }

   public Void computeVoid2(Object obj) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void putAll(Map map) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public final int size() {
      return this.obtainIntValue7();
   }

   @Override
   public final boolean containsValue(Object obj) {
      return !(obj instanceof Void) ? false : this.checkVoid3((Void)obj);
   }

   @Override
   public Object get(Object obj) {
      return this.computeVoid(obj);
   }

   @Override
   public final Set entrySet() {
      return this.obtainSet();
   }

   @Override
   public final Set keySet() {
      return this.obtainSet2();
   }

   @Override
   public final Collection values() {
      return this.obtainCollection();
   }

   @Override
   public Object put(Object obj, Object obj2) {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }

   @Override
   public Object remove(Object obj) {
      return this.computeVoid2(obj);
   }
}
