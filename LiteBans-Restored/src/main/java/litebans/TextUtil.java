package litebans;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TextUtil {
   public static final String replacePairs(@NotNull CharSequence message, @NotNull CharSequence[] messages) {
      String string = message.toString();
      int value = 0;
      int length2 = messages.length;
      if (length2 % 2 == 1) {
         throw new ArrayIndexOutOfBoundsException("Odd arg length " + length2);
      }

      while (value < length2) {
         CharSequence message2 = messages[value++];
         CharSequence message3 = messages[value++];
         string = Placeholders.replacePlaceholder(string, message2, message3);
      }

      return string;
   }

   public static final CharSequence computeMessage(@NotNull CharSequence message, boolean flag, @Nullable CharSequence message2) {
      CharSequence message3;
      if (message2 != null && message2.length() > 0 && flag) {
         CharSequence message4 = message;
         CharSequence message5 = "\n";
         CharSequence message6 = "" + message4 + message5;
         message3 = "" + message6 + message2;
      } else {
         message3 = message;
      }

      return message3;
   }

   public static final CharSequence computeMessage(@NotNull CharSequence message, @Nullable CharSequence message2) {
      CharSequence message3;
      if (message2 != null && message2.length() > 0) {
         CharSequence message4 = message;
         CharSequence message5 = "\n";
         CharSequence message6 = "" + message4 + message5;
         message3 = "" + message6 + message2;
      } else {
         message3 = message;
      }

      return message3;
   }

   public static final byte[] access$toString(@NotNull StringBuilder builder, @NotNull Charset charset) {
      return builder.toString().getBytes(charset);
   }

   public static final String prefix(char c, @NotNull CharSequence message) {
      return "" + c + message;
   }

   public static final String colorize(@NotNull LiteColor liteColor, @NotNull String text) {
      return liteColor + text;
   }

   public static final boolean hasDoubleSpace(@NotNull CharSequence message) {
      return StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)"  ", false, 2, null);
   }

   public static final String collapseWhitespace(@NotNull CharSequence message) {
      CharSequence message2 = message;
      Regex regex = new Regex("\\s+");
      String text = " ";
      return regex.replaceAll(message2, text);
   }

   public static final String offlineUuid(@NotNull String text) {
      String text2 = "OfflinePlayer:" + text;
      return UUID.nameUUIDFromBytes(text2.getBytes(KotlinCharsets.CHARSET)).toString();
   }

   public static final String access$length(@NotNull String text, int value) {
      return text.length() <= value ? text : text.substring(0, value);
   }

   public static final List computeList(@NotNull List list) {
      String joinToString = CollectionsKt.joinToStringWithDefaults(list, " ", null, null, 0, null, null, 62, null);
      if (!StringsKt.containsWithDefaults(joinToString, '"', false, 2, null) && !StringsKt.containsWithDefaults(joinToString, '\'', false, 2, null)) {
         return list;
      }

      ArrayList arrayList = new ArrayList();
      StringBuilder stringBuilder = new StringBuilder();
      boolean flag = false;
      char[] charArray = joinToString.toCharArray();
      int value = 0;

      for (int i = charArray.length; value < i; value++) {
         char c = charArray[value];
         if (c == ' ' && !flag) {
            arrayList.add(stringBuilder.toString());
            StringsKt.computeStringBuilder(stringBuilder);
         } else if (c != '"' && c != '\'') {
            stringBuilder.append(c);
         } else {
            flag = !flag;
            if (!flag) {
               arrayList.add(stringBuilder.toString());
               StringsKt.computeStringBuilder(stringBuilder);
            }
         }
      }

      return arrayList;
   }
}
