package litebans;

import java.io.BufferedReader;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

final class KotlinHelper6 implements KotlinAInterface3 {
   private final BufferedReader bufferedReader2;

   public KotlinHelper6(@NotNull BufferedReader bufferedReader) {
      Intrinsics.checkParameterIsNotNull(bufferedReader, "reader");
      super();
      this.bufferedReader2 = bufferedReader;
   }

   @Override
   public Iterator obtainIterator() {
      return new KotlinIteratorImpl2(this);
   }

   public static final BufferedReader access$bufferedReader2(KotlinHelper6 kotlinHelper) {
      return kotlinHelper.bufferedReader2;
   }
}
