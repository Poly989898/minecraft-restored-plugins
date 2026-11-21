package litebans;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.jetbrains.annotations.NotNull;

final class KotlinLazyImpl2 implements Lazy, Serializable {
   public static final KotlinLazyImpl2Companion KOTLIN_LAZY_IMPL2_COMPANION = new KotlinLazyImpl2Companion(null);
   private volatile Function0 function3;
   private volatile Object _value;
   private final Object obj7;
   private static final AtomicReferenceFieldUpdater ATOMIC_REFERENCE_FIELD_UPDATER = AtomicReferenceFieldUpdater.newUpdater(KotlinLazyImpl2.class, Object.class, "_value");

   public KotlinLazyImpl2(@NotNull Function0 supplier) {
      Intrinsics.checkParameterIsNotNull(supplier, "initializer");
      super();
      this.function3 = supplier;
      this._value = KotlinHelper38.KOTLIN_HELPER;
      this.obj7 = KotlinHelper38.KOTLIN_HELPER;
   }

   @Override
   public Object getValue() {
      Object _value2 = this._value;
      if (_value2 != KotlinHelper38.KOTLIN_HELPER) {
         return _value2;
      }

      Function0 supplier = this.function3;
      if (supplier != null) {
         Object obj = supplier.obtainObj2();
         if (ATOMIC_REFERENCE_FIELD_UPDATER.compareAndSet(this, KotlinHelper38.KOTLIN_HELPER, (KotlinHelper38)obj)) {
            this.function3 = null;
            return obj;
         }
      }

      return this._value;
   }

   @Override
   public boolean isInitialized() {
      return this._value != KotlinHelper38.KOTLIN_HELPER;
   }

   @Override
   public String toString() {
      return this.isInitialized() ? String.valueOf(this.getValue()) : "Lazy value not initialized yet.";
   }

   private final Object obtainObj3() {
      return new InitializedLazyImpl(this.getValue());
   }
}
