package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class KotlinData9 {
   private KotlinData9() {
   }

   public final String computeText(@NotNull String text) {
      if (!StringsKt.containsWithDefaults(text, '#', false, 2, null)) {
         return text;
      }

      StringBuilder stringBuilder = new StringBuilder();
      ArrayList arrayList = new ArrayList();
      CharSequence message = Message.HEX_FORMATS;
      char[] chars = new char[]{'\n'};
      Iterable split = StringsKt.splitWithDefaults(message, chars, false, 0, 6, null);
      var arrayList2 = new ArrayList();

      for (Object obj : split) {
         String string = (String)obj;
         if (StringsKt.containsWithDefaults((CharSequence)string, (CharSequence)"%s", false, 2, null)) {
            arrayList2.add(obj);
         }
      }

      List list = arrayList2;
      boolean flag;
      if (list instanceof Collection && ((Collection)list).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = list.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj2 = iterator2.next();
            String string2 = (String)obj2;
            message = text;
            char c = StringsKt.computeCharValue(string2);
            String text2 = "#";
            if (StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)(c + text2), false, 2, null)) {
               flag = true;
               break;
            }
         }
      }

      boolean flag2 = flag;
      char[] charArray = text.toCharArray();
      int value = 0;
      int value2 = 0;

      for (int i = charArray.length; value2 < i; value2++) {
         char c2 = charArray[value2];
         int value3 = value++;
         if (c2 == '#' && charArray.length > value3 + 6) {
            StringsKt.computeStringBuilder(stringBuilder);
            stringBuilder.append('#');
            boolean flag3 = true;

            for (int i2 = 1; i2 < 7; i2++) {
               char c3 = charArray[value3 + i2];
               if (!Character.isDigit(c3) && ('a' <= c3 ? c3 >= 'g' : true) && ('A' <= c3 ? c3 >= 'G' : true)) {
                  flag3 = false;
                  break;
               }

               stringBuilder.append(c3);
            }

            if (flag3) {
               String string3 = stringBuilder.toString();
               String text3 = JsonMessageModule.KOTLIN_DATA.computeText2(string3);
               if (flag2) {
                  Iterable iterable = list;

                  for (Object obj3 : iterable) {
                     String string4 = (String)obj3;
                     Collection collection = arrayList;
                     Object[] objs = new Object[]{string3};
                     collection.add(TuplesKt.computePair(String.format(string4, Arrays.copyOf(objs, objs.length)), text3));
                  }
               }

               arrayList.add(TuplesKt.computePair(string3, text3));
            }
         }
      }

      String text4 = text;
      String text5 = text4;

      for (Object obj4 : arrayList) {
         Pair pair = (Pair)obj4;
         String string5 = (String)pair.component1();
         String string6 = (String)pair.component2();
         text5 = StringsKt.computeText(text5, string5, string6, false, 4, null);
      }

      return text5;
   }

   private final String computeText2(String text) {
      StringBuilder builder = new StringBuilder(22).append('§').append('x');
      CharSequence substring2 = text.substring(1);

      for (int i = 0; i < substring2.length(); i++) {
         char charAt2 = substring2.charAt(i);
         builder.append('§').append(charAt2);
      }

      return builder.toString();
   }

   public KotlinData9(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
