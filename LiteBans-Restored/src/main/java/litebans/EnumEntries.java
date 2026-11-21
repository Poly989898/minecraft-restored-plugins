package litebans;

import org.jetbrains.annotations.NotNull;

public final class EnumEntries {
   public static final KotlinListSub computeKotlinListSub(@NotNull Enum[] enums) {
      Intrinsics.checkParameterIsNotNull(enums, "entries");
      return new KotlinHelper26(enums);
   }
}
