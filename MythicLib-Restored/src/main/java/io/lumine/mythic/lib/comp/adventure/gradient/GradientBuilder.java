package io.lumine.mythic.lib.comp.adventure.gradient;

import com.google.common.base.Preconditions;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Nullable;

public class GradientBuilder {
   public static String rgbGradient(String str, Color from, Color to, double phase, Interpolator interpolator) {
      return rgbGradient(str, from, to, phase, interpolator, new ArrayList<>());
   }

   public static String rgbGradient(String str, Color from, Color to, double phase, Interpolator interpolator, List<String> decorations) {
      double[] red = interpolator.interpolate(from.getRed(), to.getRed(), str.length());
      double[] green = interpolator.interpolate(from.getGreen(), to.getGreen(), str.length());
      double[] blue = interpolator.interpolate(from.getBlue(), to.getBlue(), str.length());
      StringBuilder builder = new StringBuilder();
      int start = str.length() - (int)(str.length() * phase);
      String decoration = String.join("", decorations);
      int charIndex = 0;

      for (int i = start; i < str.length(); i++) {
         builder.append(ChatColor.of(new Color((int)Math.round(red[i]), (int)Math.round(green[i]), (int)Math.round(blue[i]))))
            .append(decoration)
            .append(str.charAt(charIndex++));
      }

      for (int i = 0; i < start; i++) {
         builder.append(ChatColor.of(new Color((int)Math.round(red[i]), (int)Math.round(green[i]), (int)Math.round(blue[i]))))
            .append(decoration)
            .append(str.charAt(charIndex++));
      }

      return builder.toString();
   }

   public static String multiRgbGradient(String str, Color[] colors, double @Nullable [] portions, Interpolator interpolator, List<String> decorations) {
      double[] p;
      if (portions == null) {
         p = new double[colors.length - 1];
         Arrays.fill(p, 1.0 / p.length);
      } else {
         p = portions;
      }

      Preconditions.checkArgument(colors.length >= 2);
      Preconditions.checkArgument(p.length == colors.length - 1);
      StringBuilder builder = new StringBuilder();
      int stringIndex = 0;

      for (double portion : p) {
         int length = (int)(portion * str.length());
         String substring = str.substring(stringIndex, stringIndex + length);
         builder.append(rgbGradient(substring, colors[0], colors[1], 0.0, interpolator, decorations));
         colors = Arrays.copyOfRange(colors, 1, colors.length);
         stringIndex += length;
      }

      if (stringIndex < str.length()) {
         builder.append(ChatColor.of(colors[colors.length - 1])).append(String.join("", decorations)).append(str.substring(stringIndex));
      }

      return builder.toString();
   }

   public static String multiRgbGradient(String str, Color[] colors, double phase, Interpolator interpolator, List<String> decorations) {
      Color[] c = new Color[colors.length];

      for (int i = 0; i < colors.length; i++) {
         c[i] = colors[(i + (int)(colors.length * phase)) % colors.length];
      }

      return multiRgbGradient(str, c, null, interpolator, decorations);
   }
}
