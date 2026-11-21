package litebans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil40 {
   public static final List computeList(Object obj) {
      return Collections.singletonList(obj);
   }

   public static final Object[] computeObjArray(@NotNull Object[] objs, boolean flag) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return flag && Intrinsics.areEqual(objs.getClass(), Object[].class) ? objs : Arrays.copyOf(objs, objs.length, Object[].class);
   }
}
