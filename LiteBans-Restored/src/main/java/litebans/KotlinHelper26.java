package litebans;

import java.io.Serializable;
import org.jetbrains.annotations.NotNull;

final class KotlinHelper26 extends KotlinListImpl3 implements KotlinListSub, Serializable {
   private final Enum[] enums;

   public KotlinHelper26(@NotNull Enum[] enums) {
      Intrinsics.checkParameterIsNotNull(enums, "entries");
      super();
      this.enums = enums;
   }

   @Override
   public int obtainIntValue() {
      return this.enums.length;
   }

   public Enum computeEnum(int value) {
      KotlinListImpl3.KOTLIN_LIST_IMPL3_COMPANION.handleIntValue(value, this.enums.length);
      return this.enums[value];
   }

   public boolean checkEnum(@NotNull Enum enum2) {
      Intrinsics.checkParameterIsNotNull(enum2, "element");
      Enum orNull = (Enum)ArraysKt.getOrNull(this.enums, enum2.ordinal());
      return orNull == enum2;
   }

   public int computeIntValue(@NotNull Enum enum2) {
      Intrinsics.checkParameterIsNotNull(enum2, "element");
      int value = enum2.ordinal();
      Enum orNull = (Enum)ArraysKt.getOrNull(this.enums, value);
      return orNull == enum2 ? value : -1;
   }

   public int computeIntValue2(@NotNull Enum enum2) {
      Intrinsics.checkParameterIsNotNull(enum2, "element");
      return this.indexOf(enum2);
   }

   private final Object obtainObj2() {
      return new EnumEntriesList(this.enums);
   }

   @Override
   public Object get(int value) {
      return this.computeEnum(value);
   }

   @Override
   public final boolean contains(Object obj) {
      return !(obj instanceof Enum) ? false : this.checkEnum((Enum)obj);
   }

   @Override
   public final int indexOf(Object obj) {
      return !(obj instanceof Enum) ? -1 : this.computeIntValue((Enum)obj);
   }

   @Override
   public final int lastIndexOf(Object obj) {
      return !(obj instanceof Enum) ? -1 : this.computeIntValue2((Enum)obj);
   }
}
