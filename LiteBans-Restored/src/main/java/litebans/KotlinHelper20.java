package litebans;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

final class KotlinHelper20 implements KotlinAInterface3 {
   private final CharSequence message2;
   private final int intValue;
   private final int intValue2;
   private final KotlinKotlinFunctionSub kotlinKotlinFunctionSub2;

   public KotlinHelper20(@NotNull CharSequence message, int value, int value2, @NotNull KotlinKotlinFunctionSub kotlinKotlinFunctionSub) {
      Intrinsics.checkParameterIsNotNull(message, "input");
      Intrinsics.checkParameterIsNotNull(kotlinKotlinFunctionSub, "getNextMatch");
      super();
      this.message2 = message;
      this.intValue = value;
      this.intValue2 = value2;
      this.kotlinKotlinFunctionSub2 = kotlinKotlinFunctionSub;
   }

   @Override
   public Iterator obtainIterator() {
      return new KotlinIteratorImpl6(this);
   }

   public static final int access$intValue(KotlinHelper20 kotlinHelper) {
      return kotlinHelper.intValue;
   }

   public static final CharSequence access$message2(KotlinHelper20 kotlinHelper) {
      return kotlinHelper.message2;
   }

   public static final int access$intValue2(KotlinHelper20 kotlinHelper) {
      return kotlinHelper.intValue2;
   }

   public static final KotlinKotlinFunctionSub access$kotlinKotlinFunctionSub2(KotlinHelper20 kotlinHelper) {
      return kotlinHelper.kotlinKotlinFunctionSub2;
   }
}
