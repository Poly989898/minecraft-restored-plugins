package litebans;

import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TargetParser {
   public static final TargetParser TARGET_PARSER = new TargetParser();
   private static final HashSet HASH_SET = ArraysKt.toHashSet("0123456789.*%_".toCharArray());
   private static final HashSet HASH_SET_2 = ArraysKt.toHashSet("0123456789ABCDEFMVZabcdefmvz".toCharArray());
   private static final HashSet HASH_SET_3 = ArraysKt.toHashSet("MVZmvz".toCharArray());

   private TargetParser() {
   }

   public final String normalizeNullable(@Nullable String text) {
      return text == null ? null : normalize(text);
   }

   public static final String normalize(@NotNull String text) {
      String text2 = text;
      if (isUuid(text2)) {
         String text3 = UsernameValidator.KOTLIN_DATA.computeText3(text2);
         text2 = text3.toLowerCase(Locale.ENGLISH);
      }

      return text2;
   }

   public final UUID parseUuid(@NotNull String text) {
      String normalize2 = normalize(text);
      return isUuid(normalize2) ? UUID.fromString(normalize2) : null;
   }

   public final boolean isNumeric(@Nullable String text) {
      boolean flag;
      if (text != null && text.length() != 0) {
         boolean flag2;
         try {
            Double.parseDouble(text);
            flag2 = true;
         } catch (NumberFormatException e) {
            flag2 = false;
         }

         flag = flag2;
      } else {
         flag = false;
      }

      return flag;
   }

   public final boolean checkText(@Nullable String text) {
      return text != null && !StringsKt.startsWithWithDefaults(text, '.', false, 2, null) && (StringsKt.containsWithDefaults(text, '.', false, 2, null) && this.isUsername(text) || StringsKt.containsWithDefaults(text, ':', false, 2, null));
   }

   public final boolean isUsername(@NotNull String text) {
      if (text.length() <= 15) {
         CharSequence message = text;
         int value = 0;

         boolean flag;
         while (true) {
            if (value >= message.length()) {
               flag = true;
               break;
            }

            char charAt2 = message.charAt(value);
            if (!HASH_SET.contains(charAt2)) {
               flag = false;
               break;
            }

            value++;
         }

         if (flag) {
            return true;
         }
      }

      return false;
   }

   public final boolean isIpv4(@Nullable String text) {
      CharSequence message = text;
      boolean flag;
      if (message == null || message.length() == 0) {
         flag = false;
      } else {
         boolean flag2;
         try {
            Long.parseLong(text);
            flag2 = true;
         } catch (NumberFormatException e) {
            flag2 = false;
         }

         flag = flag2;
      }

      return flag;
   }

   public final boolean isIpAddress(@Nullable String text) {
      return this.isIpv4(text) || this.isIpv6(text);
   }

   public final boolean isIpv6(@Nullable String text) {
      if (text != null && text.length() >= 6 && text.length() <= 21) {
         if (text.length() % 3 != 0) {
            CharSequence message = text;
            int value = 0;

            boolean flag;
            while (true) {
               if (value >= message.length()) {
                  flag = true;
                  break;
               }

               char charAt2 = message.charAt(value);
               if (HASH_SET_3.contains(charAt2)) {
                  flag = false;
                  break;
               }

               value++;
            }

            if (flag) {
               return false;
            }
         }

         CharSequence message2 = text;

         for (int i = 0; i < message2.length(); i++) {
            char charAt3 = message2.charAt(i);
            if (!HASH_SET_2.contains(charAt3)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static final boolean isUuid(@Nullable String text) {
      if (text == null) {
         return false;
      }

      int value = text.length();
      if (value == 36 || value == 32) {
         CharSequence message = text;
         int value2 = 0;

         boolean flag;
         while (true) {
            if (value2 >= message.length()) {
               flag = true;
               break;
            }

            char charAt2 = message.charAt(value2);
            if (!Character.isLetterOrDigit(charAt2) && charAt2 != '-') {
               flag = false;
               break;
            }

            value2++;
         }

         if (flag) {
            return true;
         }
      }

      return false;
   }

   public final PunishmentTarget parseTarget(@NotNull String text) {
      String text2 = text;
      byte b = 0;
      if (this.checkText(text2)) {
         b = 2;
         String text3 = text2;
         text2 = text3.toLowerCase(Locale.ENGLISH);
      } else if (isUuid(text2)) {
         text2 = normalize(text2);
         b = 1;
      }

      return new PunishmentTarget(b, text2);
   }
}
