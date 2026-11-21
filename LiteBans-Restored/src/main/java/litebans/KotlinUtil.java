package litebans;

import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class KotlinUtil {
   public static final void handleAppendable(@NotNull Appendable appendable, Object obj, @Nullable Function1 callback) {
      try {
         Intrinsics.checkParameterIsNotNull(appendable, "<this>");
         if (callback != null) {
            appendable.append((CharSequence)callback.computeObj(obj));
         } else if (obj == null ? true : obj instanceof CharSequence) {
            appendable.append((CharSequence)obj);
         } else if (obj instanceof Character) {
            appendable.append((Character)obj);
         } else {
            appendable.append(obj.toString());
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
