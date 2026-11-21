package litebans;

import org.jetbrains.annotations.Nullable;

public final class KotlinUtil15 {
   public static final void handleAutoCloseable(@Nullable AutoCloseable autoCloseable, @Nullable Throwable error) {
      try {
         if (autoCloseable != null) {
            if (error == null) {
               autoCloseable.close();
            } else {
               try {
                  autoCloseable.close();
               } catch (Throwable e2) {
                  KotlinStub4.handleThrowable(error, e2);
               }
            }
         }
      } catch (Exception e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
