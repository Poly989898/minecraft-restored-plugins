package litebans;

import org.jetbrains.annotations.Nullable;

public class ResultException extends RuntimeException {
   private final Object obj2;

   public ResultException(@Nullable Object obj) {
      super(String.valueOf(obj));
      this.obj2 = obj;
   }

   public final Object obtainObj() {
      return this.obj2;
   }
}
