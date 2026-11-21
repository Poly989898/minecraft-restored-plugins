package litebans;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class StringMatching {
   private static final byte[] return2 = new byte[0];

   public static final byte[] obtainBytes() {
      return return2;
   }

   public static final List computeList(@NotNull String[] texts) {
      List list;
      if (texts.length == 0) {
         list = CollectionsKt.emptyList();
      } else {
         String[] texts2 = texts;
         var arrayList = new ArrayList(texts2.length);
         int value = 0;

         for (int i = texts2.length; value < i; value++) {
            String text = texts2[value];
            arrayList.add(text.toLowerCase(Locale.ENGLISH));
         }

         list = arrayList;
      }

      return list;
   }

   public static final Integer getIntOrNull(@NotNull String[] texts, int value) {
      String orNull = (String)ArraysKt.getOrNull(texts, value);
      return orNull != null ? StringsKt.computeInteger(orNull) : null;
   }

   public static final int getInt(@NotNull String[] texts, int value, int value2) {
      Integer boxed = getIntOrNull(texts, value);
      return boxed != null ? boxed : value2;
   }

   public static final int computeIntValue(@NotNull String[] texts, int value) {
      int value2 = texts.length + -1;
      if (0 <= value2) {
         do {
            int value3 = value2--;
            Integer boxed = StringsKt.computeInteger(texts[value3]);
            if (boxed != null) {
               return boxed;
            }
         } while (0 <= value2);
      }

      return value;
   }

   public static final double getDouble(@NotNull String[] texts, int value, double number) {
      String orNull = (String)ArraysKt.getOrNull(texts, value);
      if (orNull != null) {
         Double double2 = StringsKt.computeDouble(orNull);
         if (double2 != null) {
            return double2;
         }
      }

      return number;
   }

   public static final boolean bytesEqual(@NotNull byte[] bytes, @NotNull byte[] bytes2, int value, int value2) {
      int value3 = value2 - value;
      int value4 = 0;
      int value5 = value;

      while (value4 < value3) {
         if (bytes[value4++] != bytes2[value5++]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean regionEquals(@NotNull byte[] bytes, @NotNull byte[] bytes2, int value, int value2, int value3) {
      int value4 = value + (value3 - value2);
      int value5 = value;
      int value6 = value2;

      while (value5 < value4) {
         if (bytes[value5++] != bytes2[value6++]) {
            return false;
         }
      }

      return true;
   }

   public static final void handleTriple(@NotNull Triple triple, @NotNull Function1 callback) {
      callback.computeObj(triple.getFirst());
      callback.computeObj(triple.getSecond());
      callback.computeObj(triple.getThird());
   }
}
