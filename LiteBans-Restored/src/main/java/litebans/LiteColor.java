package litebans;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public enum LiteColor implements CharSequence {
   BLACK('0', "black"),
   DARK_BLUE('1', "dark_blue"),
   DARK_GREEN('2', "dark_green"),
   DARK_AQUA('3', "dark_aqua"),
   DARK_RED('4', "dark_red"),
   DARK_PURPLE('5', "dark_purple"),
   GOLD('6', "gold"),
   GRAY('7', "gray"),
   DARK_GRAY('8', "dark_gray"),
   BLUE('9', "blue"),
   GREEN('a', "green"),
   AQUA('b', "aqua"),
   RED('c', "red"),
   LIGHT_PURPLE('d', "light_purple"),
   YELLOW('e', "yellow"),
   WHITE('f', "white"),
   OBFUSCATED('k', "obfuscated"),
   BOLD('l', "bold"),
   STRIKETHROUGH('m', "strikethrough"),
   UNDERLINE('n', "underline"),
   ITALIC('o', "italic"),
   RESET('r', "reset"),
   MAGIC('x', "magic");

   public static final char VALUE_167 = 167;
   public static final String TEXT = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
   public static final Pattern PATTERN = Pattern.compile("(?i)(?<!<@)[&§][0-9A-FK-ORX]");
   private static final Map MAP = new HashMap();
   private final char charValue;
   private final String text2;
   private final String text3;

   public static LiteColor valueOfName(String text) {
      return Enum.valueOf(LiteColor.class, text);
   }

   LiteColor(char c, String text) {
      this.charValue = c;
      this.text3 = text;
      this.text2 = new String(new char[]{'§', c});
   }

   public static String stripColor(CharSequence message) {
      return message == null ? null : PATTERN.matcher(message).replaceAll("");
   }

   public static String translateAlternateColorCodes(char c, String text) {
      char[] charArray = text.toCharArray();

      for (int i = 0; i < charArray.length - 1; i++) {
         if (charArray[i] == c && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(charArray[i + 1]) > -1) {
            charArray[i] = 167;
            charArray[i + 1] = Character.toLowerCase(charArray[i + 1]);
         }
      }

      return new String(charArray);
   }

   @Override
   public char charAt(int value) {
      return this.toString().charAt(value);
   }

   @Override
   public int length() {
      return this.toString().length();
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return this.toString().subSequence(value, value2);
   }

   @Override
   public @NotNull String toString() {
      return this.text2;
   }

   public static String gold(String text) {
      return GOLD + text;
   }

   public static String green(String text) {
      return GREEN + text;
   }

   public static String red(String text) {
      return RED + text;
   }

   public static String lightPurple(String text) {
      return LIGHT_PURPLE + text;
   }

   public static String yellow(String text) {
      return YELLOW + text;
   }

   public static String underline(String text) {
      return UNDERLINE + text;
   }

   private static final LiteColor[] createValues() {
      return new LiteColor[]{
         BLACK,
         DARK_BLUE,
         DARK_GREEN,
         DARK_AQUA,
         DARK_RED,
         DARK_PURPLE,
         GOLD,
         GRAY,
         DARK_GRAY,
         BLUE,
         GREEN,
         AQUA,
         RED,
         LIGHT_PURPLE,
         YELLOW,
         WHITE,
         OBFUSCATED,
         BOLD,
         STRIKETHROUGH,
         UNDERLINE,
         ITALIC,
         RESET,
         MAGIC
      };
   }

   static {
      for (LiteColor liteColor : values()) {
         MAP.put(liteColor.charValue, liteColor);
      }
   }
}
