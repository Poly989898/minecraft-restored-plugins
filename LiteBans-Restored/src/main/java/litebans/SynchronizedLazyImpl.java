package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SynchronizedLazyImpl implements Lazy, Serializable {
   private Function0 function2;
   private volatile Object obj5;
   private final Object obj6;

   public SynchronizedLazyImpl(@NotNull Function0 supplier, @Nullable Object obj) {
      Intrinsics.checkParameterIsNotNull(supplier, "initializer");
      super();
      this.function2 = supplier;
      this.obj5 = KotlinHelper38.KOTLIN_HELPER;
      Object obj2 = obj;
      if (obj == null) {
         obj2 = this;
      }

      this.obj6 = obj2;
   }

   public SynchronizedLazyImpl(Function0 supplier, Object obj, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         obj = null;
      }

      this(supplier, obj);
   }

   // $VF: Extended synchronized range to monitorexit
   @Override
   public Object getValue() {
      Object obj = this.obj5;
      if (obj != KotlinHelper38.KOTLIN_HELPER) {
         return obj;
      }

      synchronized (this.obj6) {
         Object obj2 = this.obj5;
         if (obj2 != KotlinHelper38.KOTLIN_HELPER) {
            return obj2;
         }

         Function0 supplier = this.function2;
         Intrinsics.checkNotNull(supplier);
         Object obj3 = supplier.obtainObj2();
         this.obj5 = obj3;
         this.function2 = null;
         return obj3;
      }
   }

   @Override
   public boolean isInitialized() {
      return this.obj5 != KotlinHelper38.KOTLIN_HELPER;
   }

   @Override
   public String toString() {
      return this.isInitialized() ? String.valueOf(this.getValue()) : "Lazy value not initialized yet.";
   }

   private final Object obtainObj2() {
      return new InitializedLazyImpl(this.getValue());
   }
}
