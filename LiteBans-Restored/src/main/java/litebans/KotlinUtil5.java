package litebans;

import org.jetbrains.annotations.NotNull;

class KotlinUtil5 {
   public static final Lazy computeLazy(@NotNull Function0 supplier) {
      Intrinsics.checkParameterIsNotNull(supplier, "initializer");
      return new SynchronizedLazyImpl(supplier, null, 2, null);
   }

   public static final Lazy computeLazy(@NotNull LazyThreadSafetyMode lazyThreadSafetyMode, @NotNull Function0 supplier) {
      Intrinsics.checkParameterIsNotNull(lazyThreadSafetyMode, "mode");
      Intrinsics.checkParameterIsNotNull(supplier, "initializer");
      Lazy lazy;
      switch (LazyThreadSafetyModeSwitchMap.value[lazyThreadSafetyMode.ordinal()]) {
         case 1:
            lazy = new SynchronizedLazyImpl(supplier, null, 2, null);
            break;
         case 2:
            lazy = new KotlinLazyImpl2(supplier);
            break;
         case 3:
            lazy = new KotlinLazyImpl(supplier);
            break;
         default:
            throw new KotlinRuntimeExceptionSub();
      }

      return lazy;
   }
}
