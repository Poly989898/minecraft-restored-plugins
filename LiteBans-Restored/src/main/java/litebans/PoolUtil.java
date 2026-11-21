package litebans;

import org.jetbrains.annotations.NotNull;

class PoolUtil extends PoolStub2 {
   public static final StringBuilder computeStringBuilder(@NotNull StringBuilder builder) {
      Intrinsics.checkParameterIsNotNull(builder, "<this>");
      StringBuilder builder2 = builder;
      builder2.setLength(0);
      return builder2;
   }
}
