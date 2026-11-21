package litebans;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class KotlinUtil27 extends KotlinUtil29 {
   public static final List computeList(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      return KotlinHelper14.computeList(objs);
   }

   public static final Object[] computeObjArray(@NotNull Object[] objs, @NotNull Object[] objs2, int value, int value2, int value3) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      Intrinsics.checkParameterIsNotNull(objs2, "destination");
      System.arraycopy(objs, value2, objs2, value, value3 - value2);
      return objs2;
   }

   public static Object[] computeObjArray(Object[] objs, Object[] objs2, int value, int value2, int value3, int value4, Object obj) {
      if ((value4 & 2) != 0) {
         value = 0;
      }

      if ((value4 & 4) != 0) {
         value2 = 0;
      }

      if ((value4 & 8) != 0) {
         value3 = objs.length;
      }

      return ArraysKt.computeObjArray((Object[])objs, (Object[])objs2, value, value2, value3);
   }

   public static final byte[] computeBytes(@NotNull byte[] bytes, @NotNull byte[] bytes2, int value, int value2, int value3) {
      Intrinsics.checkParameterIsNotNull(bytes, "<this>");
      Intrinsics.checkParameterIsNotNull(bytes2, "destination");
      System.arraycopy(bytes, value2, bytes2, value, value3 - value2);
      return bytes2;
   }

   public static final byte[] computeBytes(@NotNull byte[] bytes, int value, int value2) {
      Intrinsics.checkParameterIsNotNull(bytes, "<this>");
      ArraysKt.handleIntValue(value2, bytes.length);
      return Arrays.copyOfRange(bytes, value, value2);
   }

   public static final void handleObjArray(@NotNull Object[] objs) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      if (objs.length > 1) {
         Arrays.sort(objs);
      }
   }

   public static final void handleObjArray(@NotNull Object[] objs, @NotNull Comparator comparator) {
      Intrinsics.checkParameterIsNotNull(objs, "<this>");
      Intrinsics.checkParameterIsNotNull(comparator, "comparator");
      if (objs.length > 1) {
         Arrays.sort(objs, comparator);
      }
   }
}
