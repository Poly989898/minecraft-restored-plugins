package litebans;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import org.jetbrains.annotations.NotNull;

public final class UnsafeLazyImpl implements Lazy, Serializable {
   private final Function0 function;
   private volatile Reference reference;

   public UnsafeLazyImpl(@NotNull Function0 supplier) {
      this.function = supplier;
      this.reference = new SoftReference(null);
   }

   // $VF: Extended synchronized range to monitorexit
   @Override
   public Object getValue() {
      Object obj = this.reference.get();
      if (obj != null) {
         return obj;
      }

      synchronized (this) {
         Object obj2 = this.reference.get();
         Object obj3;
         if (obj2 != null) {
            obj3 = obj2;
         } else {
            Object obj4 = this.function.obtainObj2();
            this.reference = new SoftReference<>(obj4);
            obj3 = obj4;
         }

         return obj3;
      }
   }

   @Override
   public boolean isInitialized() {
      return this.reference.get() != null;
   }

   @Override
   public String toString() {
      return String.valueOf(this.isInitialized() ? this.getValue() : null);
   }

   private final Object obtainObj() {
      return KotlinStub5.computeLazy(this.getValue());
   }
}
