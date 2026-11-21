package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class LiteHelper5 {
   private LiteHelper5() {
   }

   public final LiteData computeLiteData(@NotNull String text, @NotNull Integer[] boxeds) {
      String text2 = text;
      if (StringsKt.checkText(text2, "git:", false, 2, null)) {
         text2 = StringsKt.substringAfterLastWithDefaults(text2, ':', null, 2, null);
      }

      if (StringsKt.containsWithDefaults(text2, '+', false, 2, null)) {
         CharSequence message = text2;
         char[] chars = new char[]{'+'};
         text2 = (String)StringsKt.splitWithDefaults(message, chars, false, 0, 6, null).get(1);
      }

      if (StringsKt.containsWithDefaults(text2, '-', false, 2, null)) {
         CharSequence message2 = text2;
         char[] chars2 = new char[]{'-'};
         text2 = (String)StringsKt.splitWithDefaults(message2, chars2, false, 0, 6, null).get(0);
      }

      int value = boxeds[0];
      int value2 = boxeds[1];
      int value3 = boxeds[2];
      if (StringsKt.containsWithDefaults(text2, '.', false, 2, null)) {
         CharSequence message3 = text2;
         char[] chars3 = new char[]{'.'};
         List split = StringsKt.splitWithDefaults(message3, chars3, false, 0, 6, null);
         if (split.size() >= 3) {
            Integer boxed = StringsKt.computeInteger((String)split.get(0));
            value = boxed != null ? boxed : value;
            Integer boxed2 = StringsKt.computeInteger((String)split.get(1));
            value2 = boxed2 != null ? boxed2 : value2;
            Integer boxed3 = StringsKt.computeInteger((String)split.get(2));
            value3 = boxed3 != null ? boxed3 : value3;
         }
      } else {
         Integer boxed4 = StringsKt.computeInteger(text2);
         value = boxed4 != null ? boxed4 : value;
      }

      return new LiteData(value, value2, value3);
   }

   public static LiteData computeLiteData(LiteHelper5 liteHelper, String text, Integer[] boxeds, int value, Object obj) {
      if ((value & 2) != 0) {
         Integer[] boxeds2 = new Integer[]{1, 21, 5};
         boxeds = boxeds2;
      }

      return liteHelper.computeLiteData(text, boxeds);
   }

   public LiteHelper5(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
