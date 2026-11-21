package litebans;

import org.jetbrains.annotations.Nullable;

public final class LiteMessageException extends RuntimeException {
   public LiteMessageException() {
   }

   public LiteMessageException(@Nullable String text) {
      super(text);
   }
}
