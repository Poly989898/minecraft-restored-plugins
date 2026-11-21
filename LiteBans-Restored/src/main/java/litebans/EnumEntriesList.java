package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

public final class EnumEntriesList implements Serializable {
   private static final EnumEntriesListCompanion ENUM_ENTRIES_LIST_COMPANION = new EnumEntriesListCompanion(null);
   private final Class class2;
   private static final long VALUE_0 = 0L;

   public EnumEntriesList(@NotNull Enum[] enums) {
      Intrinsics.checkParameterIsNotNull(enums, "entries");
      super();
      Class componentType = enums.getClass().getComponentType();
      Intrinsics.checkNotNull(componentType);
      this.class2 = componentType;
   }

   private final Object obtainObj() {
      return EnumEntries.computeKotlinListSub((Enum[])this.class2.getEnumConstants());
   }
}
