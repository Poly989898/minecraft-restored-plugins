package litebans;

import java.util.RandomAccess;
import org.jetbrains.annotations.NotNull;

final class KotlinRandomAccessImpl extends KotlinListImpl3 implements RandomAccess {
   private final KotlinListImpl3 kotlinList;
   private final int intValue4;
   private int intValue5;

   public KotlinRandomAccessImpl(@NotNull KotlinListImpl3 kotlinList, int value, int value2) {
      Intrinsics.checkParameterIsNotNull(kotlinList, "list");
      super();
      this.kotlinList = kotlinList;
      this.intValue4 = value;
      KotlinListImpl3.KOTLIN_LIST_IMPL3_COMPANION.handleIntValue(this.intValue4, value2, this.kotlinList.size());
      this.intValue5 = value2 - this.intValue4;
   }

   @Override
   public Object get(int value) {
      KotlinListImpl3.KOTLIN_LIST_IMPL3_COMPANION.handleIntValue(value, this.intValue5);
      return this.kotlinList.get(this.intValue4 + value);
   }

   @Override
   public int obtainIntValue() {
      return this.intValue5;
   }
}
