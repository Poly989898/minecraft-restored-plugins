package me.TechsCode.UltraPermissions.base.visual;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Animation {
   private static final HashMap<String, List<String>> animationCache = new HashMap<>();

   public Animation() {
   }

   public static String wave(String text, Color... colors) {
      return wave(text, true, 5, 10, colors);
   }

   public static String wave(String text, boolean bold, int pause, int steps, Color... colors) {
      Preconditions.checkArgument(colors.length > 1, "Not enough colors provided");
      String cacheKey = "wave-" + text + "-" + bold + "-" + pause + "-" + steps + "-" + Arrays.stream(colors).map(Color::getColorCode).collect(Collectors.joining("-"));
      if (animationCache.containsKey(cacheKey)) {
         return currentFrame(animationCache.get(cacheKey));
      }

      ArrayList frames = new ArrayList();
      int colorIndex = 0;

      for (Color color : colors) {
         Color nextColor = colors[colors.length == colorIndex + 1 ? 0 : colorIndex + 1];
         frames.addAll(Collections.nCopies(pause, color.getAppliedTag() + (bold ? "\u00a7l" : "") + text));
         ArrayList gradientTags = new ArrayList();
         gradientTags.addAll(Collections.nCopies(text.length(), color.getAppliedTag()));
         gradientTags.addAll(ColorCalculations.getColorsInBetween(color, nextColor, steps).stream().map(Color::getAppliedTag).collect(Collectors.toList()));
         gradientTags.addAll(Collections.nCopies(text.length(), nextColor.getAppliedTag()));

         for (int offset = 0; offset <= gradientTags.size() - text.length(); offset++) {
            StringBuilder stringBuilder = new StringBuilder();
            int value = 0;

            for (char c : text.toCharArray()) {
               String tag = (String)gradientTags.get(value + offset);
               stringBuilder.append(tag).append(bold ? "\u00a7l" : "").append(c);
               value++;
            }

            frames.add(stringBuilder.toString());
         }

         frames.addAll(Collections.nCopies(pause, nextColor.getAppliedTag() + (bold ? "\u00a7l" : "") + text));
         colorIndex++;
      }

      animationCache.put(cacheKey, frames);
      return currentFrame(frames);
   }

   public static String fading(String text, Color... colors) {
      return fading(text, true, 10, 20, colors);
   }

   public static String fading(String text, boolean bold, int pause, int steps, Color... colors) {
      Preconditions.checkArgument(colors.length > 1, "Not enough colors provided");
      String cacheKey = "fading-"
         + text
         + "-"
         + bold
         + "-"
         + pause
         + "-"
         + steps
         + "-"
         + Arrays.stream(colors).map(Color::getColorCode).collect(Collectors.joining("-"));
      if (animationCache.containsKey(cacheKey)) {
         return currentFrame(animationCache.get(cacheKey));
      }

      ArrayList frames = new ArrayList();
      int colorIndex = 0;

      for (Color color : colors) {
         Color nextColor = colors[colors.length == colorIndex + 1 ? 0 : colorIndex + 1];
         frames.addAll(Collections.nCopies(pause, color.getAppliedTag() + (bold ? "\u00a7l" : "") + text));

         for (Color gradientColor : ColorCalculations.getColorsInBetween(color, nextColor, steps)) {
            frames.add(gradientColor.getAppliedTag() + (bold ? "\u00a7l" : "") + text);
         }

         colorIndex++;
      }

      animationCache.put(cacheKey, frames);
      return currentFrame(frames);
   }

   private static String currentFrame(List<String> frames) {
      long tick = System.currentTimeMillis() / 50L;
      int index = (int)(tick % frames.size());
      return (String)frames.get(index);
   }
}
