package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

public final class KotlinLazyImpl implements Lazy, Serializable {
   private Function0 function4;
   private Object obj8;

   public KotlinLazyImpl(@NotNull Function0 supplier) {
      Intrinsics.checkParameterIsNotNull(supplier, "initializer");
      super();
      this.function4 = supplier;
      this.obj8 = KotlinHelper38.KOTLIN_HELPER;
   }

   @Override
   public Object getValue() {
      if (this.obj8 == KotlinHelper38.KOTLIN_HELPER) {
         Function0 supplier = this.function4;
         Intrinsics.checkNotNull(this.function4);
         this.obj8 = supplier.obtainObj2();
         this.function4 = null;
      }

      return this.obj8;
   }

   @Override
   public boolean isInitialized() {
      return this.obj8 != KotlinHelper38.KOTLIN_HELPER;
   }

   @Override
   public String toString() {
      return this.isInitialized() ? String.valueOf(this.getValue()) : "Lazy value not initialized yet.";
   }

   private final Object obtainObj4() {
      return new InitializedLazyImpl(this.getValue());
   }
}
