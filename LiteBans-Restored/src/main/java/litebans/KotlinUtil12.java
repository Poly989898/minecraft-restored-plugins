package litebans;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public final class KotlinUtil12 {
   public static final Iterator computeIterator(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "array");
      return new KotlinIteratorImpl3(objs);
   }
}
