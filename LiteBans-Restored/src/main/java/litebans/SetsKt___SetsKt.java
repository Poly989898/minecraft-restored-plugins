package litebans;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

class SetsKt___SetsKt extends SetsJVM {
   public static final Set obtainSet() {
      return KotlinData7.return4;
   }

   public static final Set computeSet(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "elements");
      return ArraysKt.toSet(objs);
   }

   public static final Set computeSet(@NotNull Set set) {
      Intrinsics.checkParameterIsNotNull(set, "<this>");
      Set set2;
      switch (set.size()) {
         case 0:
            set2 = KotlinStub.obtainSet();
            break;
         case 1:
            set2 = KotlinStub.computeSet(set.iterator().next());
            break;
         default:
            set2 = set;
      }

      return set2;
   }
}
