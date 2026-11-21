package litebans;

import java.io.IOException;
import java.io.Closeable;
import org.jetbrains.annotations.Nullable;

public final class CloseableKt {
   public static final void handleCloseable(@Nullable Closeable closeable, @Nullable Throwable error) {
      try {
         if (closeable != null) {
            if (error == null) {
               closeable.close();
            } else {
               try {
                  closeable.close();
               } catch (Throwable e2) {
                  KotlinStub4.handleThrowable(error, e2);
               }
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
