package litebans;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ColorTranslator {
   public static final char VALUE_167 = 167;
   public static final String TEXT = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
   public static final Pattern PATTERN = Pattern.compile("(?i)" + String.valueOf('§') + "[0-9A-FK-ORX]");
   private static final Map MAP = new HashMap();
   private static final Map MAP_2 = new HashMap();
   public static final ColorTranslator BLACK = new ColorTranslator('0', "black", new Color(0));
   public static final ColorTranslator DARK_BLUE = new ColorTranslator('1', "dark_blue", new Color(170));
   public static final ColorTranslator DARK_GREEN = new ColorTranslator('2', "dark_green", new Color(43520));
   public static final ColorTranslator DARK_AQUA = new ColorTranslator('3', "dark_aqua", new Color(43690));
   public static final ColorTranslator DARK_RED = new ColorTranslator('4', "dark_red", new Color(11141120));
   public static final ColorTranslator DARK_PURPLE = new ColorTranslator('5', "dark_purple", new Color(11141290));
   public static final ColorTranslator GOLD = new ColorTranslator('6', "gold", new Color(16755200));
   public static final ColorTranslator GRAY = new ColorTranslator('7', "gray", new Color(11184810));
   public static final ColorTranslator DARK_GRAY = new ColorTranslator('8', "dark_gray", new Color(5592405));
   public static final ColorTranslator BLUE = new ColorTranslator('9', "blue", new Color(5592575));
   public static final ColorTranslator GREEN = new ColorTranslator('a', "green", new Color(5635925));
   public static final ColorTranslator AQUA = new ColorTranslator('b', "aqua", new Color(5636095));
   public static final ColorTranslator RED = new ColorTranslator('c', "red", new Color(16733525));
   public static final ColorTranslator LIGHT_PURPLE = new ColorTranslator('d', "light_purple", new Color(16733695));
   public static final ColorTranslator YELLOW = new ColorTranslator('e', "yellow", new Color(16777045));
   public static final ColorTranslator WHITE = new ColorTranslator('f', "white", new Color(16777215));
   public static final ColorTranslator OBFUSCATED = new ColorTranslator('k', "obfuscated");
   public static final ColorTranslator BOLD = new ColorTranslator('l', "bold");
   public static final ColorTranslator STRIKETHROUGH = new ColorTranslator('m', "strikethrough");
   public static final ColorTranslator UNDERLINE = new ColorTranslator('n', "underline");
   public static final ColorTranslator ITALIC = new ColorTranslator('o', "italic");
   public static final ColorTranslator RESET = new ColorTranslator('r', "reset");
   private static int intValue = 0;
   private final String text3;
   private final String text4;
   private final int intValue2;
   private final Color color;

   private ColorTranslator(char c, String text) {
      this(c, text, null);
   }

   private ColorTranslator(char c, String text, Color color2) {
      this.text4 = text;
      this.text3 = new String(new char[]{'§', c});
      this.intValue2 = intValue++;
      this.color = color2;
      MAP.put(c, this);
      MAP_2.put(text.toUpperCase(Locale.ROOT), this);
   }

   private ColorTranslator(String text, String text2, int value) {
      this.text4 = text;
      this.text3 = text2;
      this.intValue2 = -1;
      this.color = new Color(value);
   }

   @Override
   public int hashCode() {
      byte b = 7;
      return 53 * b + Objects.hashCode(this.text3);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         ColorTranslator colorTranslator = (ColorTranslator)obj;
         return Objects.equals(this.text3, colorTranslator.text3);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return this.text3;
   }

   public static ColorTranslator computeColorTranslator(char c) {
      return (ColorTranslator)MAP.get(c);
   }

   public static ColorTranslator computeColorTranslator(String text) {
      Preconditions.checkArgument(text != null, "string cannot be null");
      if (text.length() == 7 && text.charAt(0) == '#') {
         int value;
         try {
            value = Integer.parseInt(text.substring(1), 16);
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Illegal hex string " + text);
         }

         StringBuilder stringBuilder = new StringBuilder("§x");

         for (char c : text.substring(1).toCharArray()) {
            stringBuilder.append('§').append(c);
         }

         return new ColorTranslator(text, stringBuilder.toString(), value);
      } else {
         ColorTranslator colorTranslator = (ColorTranslator)MAP_2.get(text.toUpperCase(Locale.ROOT));
         if (colorTranslator != null) {
            return colorTranslator;
         } else {
            throw new IllegalArgumentException("Could not parse ChatColor " + text);
         }
      }
   }

   public String getText4() {
      return this.text4;
   }

   public Color getColor() {
      return this.color;
   }
}
