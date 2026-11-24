package me.TechsCode.UltraPermissions.base.visual;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {
   private static boolean gradientEnabled = false;
   private static boolean isBuneecord = false;
   private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("\\{#(([0-9A-Fa-f]{6})|([0-9A-Fa-f]{3}))}");
   private static final Pattern HEX_GRADIENT_COLOR_PATTERN = Pattern.compile("((?:[&\u00a7][uUlLoOkKrR])*)(\\{(#[^{}>]+)>})(.*?)(\\{(#.*?)<(>?)})");
   private static final Pattern STRIP_CHAT_COLOR_PATTERN = Pattern.compile("(\u00a7[0-9A-FK-ORk-orlumkr]|&[0-9A-FK-ORk-orlumkr])");

   public Text() {
   }

   public static String color(String text) {
      if (isGradientEnabled()) {
         text = gradientColor(text);
      }

      return chatColor(text);
   }

   public static String stripColor(String text) {
      return stripHexColor(stripChatColor(text));
   }

   public static String stripChatColor(String text) {
      return text.replaceAll(STRIP_CHAT_COLOR_PATTERN.pattern(), "");
   }

   public static String stripHexColor(String text) {
      return text.replaceAll(HEX_COLOR_PATTERN.pattern(), "");
   }

   public static String chatColor(String text) {
      Preconditions.checkArgument(text != null, "Text must be defined.");
      char[] chars = text.toCharArray();

      for (int i = 0; i < chars.length - 1; i++) {
         if (chars[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1) {
            chars[i] = 167;
            chars[i + 1] = Character.toLowerCase(chars[i + 1]);
         }
      }

      return new String(chars);
   }

   public static String gradientColor(String text) {
      Matcher matcher = HEX_GRADIENT_COLOR_PATTERN.matcher(text);

      while (matcher.find()) {
         String match = matcher.group(0);
         String startFormats = matcher.group(1);
         String startHex = matcher.group(3);
         String content = matcher.group(4);
         String endHex = matcher.group(6);
         boolean hasMore = !matcher.group(7).isEmpty();
         Color startColor = Color.from(startHex.replace("#", ""));
         Color endColor = Color.from(endHex.replace("#", ""));
         if (startColor.isValid() && endColor.isValid()) {
            LinkedHashSet<String> formats = new LinkedHashSet<>(getFormats(startFormats));
            formats.addAll(ColorCalculations.getFormats(content));
            content = stripColor(content);
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < content.length(); i++) {
               char c = content.charAt(i);
               double percent = i * 100.0 / Math.max(1, content.length() - 1);
               Color stepColor = ColorCalculations.getColorInBetweenPercent(startColor, endColor, percent);
               builder.append("{#").append(stepColor.getColorCode()).append("}");

               for (String format : formats) {
                  builder.append(format);
               }

               builder.append(c);
            }

            if (hasMore) {
               builder.append("{#").append(endHex.replace("#", "")).append(">").append("}");
            }

            text = text.replace(match, builder.toString());
            if (hasMore) {
               text = gradientColor(text);
            }
         }
      }

      return hexColor(chatColor(text));
   }

   public static String gradient(String text, boolean bold, Color... colors) {
      Preconditions.checkArgument(colors.length > 1, "Define 2 or more colors.");
      int steps = Math.max(1, text.length() / colors.length);
      ArrayList gradientColors = new ArrayList();
      int colorIndex = 0;

      for (Color color : colors) {
         Color nextColor = colors.length == colorIndex + 1 ? null : colors[colorIndex + 1];
         if (nextColor != null) {
            gradientColors.addAll(ColorCalculations.getColorsInBetween(color, nextColor, steps));
         }

         colorIndex++;
      }

      StringBuilder stringBuilder = new StringBuilder();
      colorIndex = 0;

      for (char c : text.toCharArray()) {
         Color stepColor = gradientColors.size() <= colorIndex ? (Color)gradientColors.get(gradientColors.size() - 1) : (Color)gradientColors.get(colorIndex);
         stringBuilder.append(stepColor.getAppliedTag()).append(bold ? "\u00a7l" : "").append(c);
         colorIndex++;
      }

      return stringBuilder.toString();
   }

   private static Set<String> getFormats(String text) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      if (text == null) {
         return linkedHashSet;
      }

      Matcher matcher = Pattern.compile("[&\u00a7]([uUlLoOkKrR])").matcher(text);

      while (matcher.find()) {
         linkedHashSet.add(matcher.group(1).toLowerCase());
      }

      return linkedHashSet;
   }

   public static String hexColor(String text) {
      Preconditions.checkArgument(text != null, "Text must be defined.");
      Matcher matcher = HEX_COLOR_PATTERN.matcher(text);

      while (matcher.find()) {
         String hexMatch = matcher.group();
         String hexCode = hexMatch.replace("{", "").replace("}", "");
         if (isBungeeCord()) {
            text = text.replace(hexMatch, BungeeColor.colorOf(hexCode));
         } else {
            hexCode = hexCode.replace("#", "");
            text = text.replace(hexMatch, Color.from(hexCode).getAppliedTag());
         }
      }

      return text;
   }

   public static String firstUpperCase(String text) {
      return text != null && !text.isEmpty() ? text.substring(0, 1).toUpperCase() + text.substring(1) : text;
   }

   public static void setEnableGradient(boolean enableGradient) {
      gradientEnabled = enableGradient;
   }

   public static boolean isGradientEnabled() {
      return gradientEnabled;
   }

   public static void setIsBungeeCord(boolean isBungeeCord) {
      isBuneecord = isBungeeCord;
   }

   public static boolean isBungeeCord() {
      return isBuneecord;
   }

   public static String capitalizeFirstLetter(String text) {
      return text != null && !text.isEmpty() ? text.substring(0, 1).toUpperCase() + text.substring(1) : text;
   }
}
