package litebans;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class KotlinUtil17 {
   public static final void handleReader(@NotNull Reader reader, @NotNull Function1 callback) {
      Intrinsics.checkParameterIsNotNull(reader, "<this>");
      Intrinsics.checkParameterIsNotNull(callback, "action");
      Reader reader2 = reader;
      short value = 8192;
      var bufferedReader = reader2 instanceof BufferedReader ? (BufferedReader)reader2 : new BufferedReader(reader2, value);
      Throwable error = null;

      try {
         KotlinAInterface3 kotlinAInterface = computeKotlinAInterface(bufferedReader);
         Iterator iterator = kotlinAInterface.obtainIterator();

         while (iterator.hasNext()) {
            Object obj = iterator.next();
            callback.computeObj(obj);
         }

      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(bufferedReader, error);
      }
   }

   public static final List computeList(@NotNull Reader reader) {
      Intrinsics.checkParameterIsNotNull(reader, "<this>");
      ArrayList arrayList = new ArrayList();
      handleReader(reader, obj -> {
         String text = (String) obj;
         Intrinsics.checkParameterIsNotNull(text, "it");
         arrayList.add(text);
         return Unit.UNIT;
      });
      return arrayList;
   }

   public static final KotlinAInterface3 computeKotlinAInterface(@NotNull BufferedReader bufferedReader) {
      Intrinsics.checkParameterIsNotNull(bufferedReader, "<this>");
      return KotlinStub9.computeKotlinAInterface(new KotlinHelper6(bufferedReader));
   }

}
