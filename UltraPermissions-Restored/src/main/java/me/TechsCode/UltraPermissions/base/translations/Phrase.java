package me.TechsCode.UltraPermissions.base.translations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.visual.Color;
import me.TechsCode.UltraPermissions.base.visual.Text;
import org.apache.commons.lang3.StringUtils;

public class Phrase {
   private final String name;
   private final String defaultPhrase;
   private final Future<Color> mainColor;
   private final List<Future<Color>> colors;
   private TranslationManager translationManager;

   public static Phrase create(String text, String defaultPhrase, Future<Color> future, Future<Color>... futures) {
      return new Phrase(text, defaultPhrase, future, Arrays.asList(futures));
   }

   public static Phrase create(String text, String defaultPhrase, Color color, Color... colors) {
      ArrayList list = new ArrayList();

      for (Color extraColor : colors) {
         Future future = () -> extraColor;
         list.add(future);
      }

      return new Phrase(text, defaultPhrase, () -> color, Collections.unmodifiableList(list));
   }

   public static Phrase create(String text, String defaultPhrase) {
      return new Phrase(text, defaultPhrase, null, Collections.emptyList());
   }

   private Phrase(String text, String defaultPhrase, Future<Color> future, List<Future<Color>> futures) {
      this.name = text;
      this.defaultPhrase = defaultPhrase;
      this.mainColor = future;
      this.colors = futures;
      int count = StringUtils.countMatches(defaultPhrase, "**");
      if (count % 2 != 0) {
         throw new IllegalArgumentException("Invalid Coloring of Phrase '" + text + "' (Uneven Markers)");
      }

      if (count / 2 != futures.size()) {
         throw new IllegalArgumentException("Invalid Coloring of Phrase '" + text + "' (Invalid Amount of Markers)");
      }
   }

   public void inject(TranslationManager translationManager) {
      this.translationManager = translationManager;
   }

   public String firstUpper() {
      return Text.firstUpperCase(this.get());
   }

   public String lower() {
      return this.get().toLowerCase();
   }

   public String upper() {
      return this.get().toUpperCase();
   }

   public String get() {
      String text = null;
      if (this.translationManager != null) {
         text = this.translationManager.getTranslation(this.name);
      }

      boolean flag = text != null && StringUtils.countMatches(this.defaultPhrase, "**") != StringUtils.countMatches(text, "**");
      String resolved = text != null && !flag ? text : this.defaultPhrase;
      if (this.mainColor != null) {
         List list = this.colors.stream().map(Future::get).collect(Collectors.toList());
         Pattern pattern = Pattern.compile("\\*\\*");
         Matcher matcher = pattern.matcher(resolved);
         StringBuffer stringBuffer = new StringBuffer();

         for (boolean closingMarker = false; matcher.find(); closingMarker = !closingMarker) {
            if (closingMarker) {
               matcher.appendReplacement(stringBuffer, this.mainColor.get().getAppliedTag());
            } else {
               matcher.appendReplacement(stringBuffer, ((Color)list.get(0)).getAppliedTag());
               list.remove(0);
            }
         }

         matcher.appendTail(stringBuffer);
         resolved = this.mainColor.get().getAppliedTag() + Text.color(stringBuffer.toString());
      }

      return resolved;
   }

   public String[] split(int count) {
      String text = this.get();
      ArrayList<String> texts = new ArrayList<>();
      String currentLine = "";

      for (String word : text.split(" ")) {
         int length = Text.stripColor(currentLine).length();
         if (length > count) {
            texts.add(currentLine);
            currentLine = "";
         }

         currentLine = (currentLine + " " + word).trim();
      }

      if (!currentLine.isEmpty()) {
         texts.add(currentLine);
      }

      return texts.stream()
         .map(line -> this.mainColor != null && !line.startsWith(this.mainColor.get().getAppliedTag()) ? this.mainColor.get().getAppliedTag() + line : line)
         .toArray(String[]::new);
   }

   public String getName() {
      return this.name;
   }

   public String getDefaultPhrase() {
      return this.defaultPhrase;
   }

   @Override
   public String toString() {
      return this.get();
   }
}
