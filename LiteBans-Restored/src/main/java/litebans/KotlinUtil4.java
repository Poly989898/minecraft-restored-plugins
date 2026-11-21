package litebans;

import kotlin.internal.PlatformImplementationsKt;
import org.jetbrains.annotations.NotNull;

class KotlinUtil4 {
   public static final void handleThrowable(@NotNull Throwable error, @NotNull Throwable error2) {
      Intrinsics.checkParameterIsNotNull(error, "<this>");
      Intrinsics.checkParameterIsNotNull(error2, "exception");
      if (error != error2) {
         PlatformImplementationsKt.IMPLEMENTATIONS.addSuppressed(error, error2);
      }
   }
}
