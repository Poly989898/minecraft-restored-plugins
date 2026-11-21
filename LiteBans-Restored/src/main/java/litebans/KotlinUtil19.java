package litebans;

import org.jetbrains.annotations.NotNull;

class KotlinUtil19 extends KotlinStub7 {
   public static final KotlinAInterface3 computeKotlinAInterface(@NotNull KotlinAInterface3 kotlinAInterface) {
      Intrinsics.checkParameterIsNotNull(kotlinAInterface, "<this>");
      return kotlinAInterface instanceof KotlinHelper19 ? kotlinAInterface : new KotlinHelper19(kotlinAInterface);
   }
}
