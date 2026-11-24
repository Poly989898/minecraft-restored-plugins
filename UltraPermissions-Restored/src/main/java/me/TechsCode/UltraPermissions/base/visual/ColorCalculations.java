package me.TechsCode.UltraPermissions.base.visual;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorCalculations {
   private static final Pattern FORMAT_PATTERN = Pattern.compile("(&[klmnorKLMNOR])");

   public ColorCalculations() {
   }

   public static List<Color> getColorsInBetween(Color color, Color end, int count) {
      double value = (double)(end.getRed() - color.getRed()) / count;
      double greenStep = (double)(end.getGreen() - color.getGreen()) / count;
      double blueStep = (double)(end.getBlue() - color.getBlue()) / count;
      ArrayList list = new ArrayList();

      for (int i = 1; i <= count; i++) {
         int red = (int)Math.round(color.getRed() + value * i);
         int green = (int)Math.round(color.getGreen() + greenStep * i);
         int blue = (int)Math.round(color.getBlue() + blueStep * i);
         Color interpolated = Color.from(red, green, blue);
         list.add(interpolated);
      }

      return list;
   }

   public static Color getColorInBetweenPercent(Color color, Color end, double value) {
      value /= 100.0;
      double inverse = 1.0 - value;
      int count = (int)(end.getRed() * value + color.getRed() * inverse);
      int green = (int)(end.getGreen() * value + color.getGreen() * inverse);
      int blue = (int)(end.getBlue() * value + color.getBlue() * inverse);
      return Color.from(count, green, blue);
   }

   public static Set<String> getFormats(String text) {
      text = text.replace("\u00a7", "&");
      HashSet set = new HashSet();
      Matcher matcher = FORMAT_PATTERN.matcher(text);

      while (matcher.find()) {
         set.add(matcher.group());
      }

      return set;
   }
}
