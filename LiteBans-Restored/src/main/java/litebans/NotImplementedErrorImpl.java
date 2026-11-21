package litebans;

import org.jetbrains.annotations.NotNull;

public final class NotImplementedErrorImpl extends Error {
   public NotImplementedErrorImpl(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "message");
      super(text);
   }

   public NotImplementedErrorImpl(String text, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         text = "An operation is not implemented.";
      }

      this(text);
   }

   public NotImplementedErrorImpl() {
      this(null, 1, null);
   }
}
