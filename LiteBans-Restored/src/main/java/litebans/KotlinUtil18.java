package litebans;

import org.jetbrains.annotations.NotNull;

class KotlinUtil18 extends KotlinStub8 {
   public static final Iterable computeIterable(@NotNull KotlinAInterface3 kotlinAInterface) {
      Intrinsics.checkParameterIsNotNull(kotlinAInterface, "<this>");
      return new KotlinIterableImpl(kotlinAInterface);
   }
}
