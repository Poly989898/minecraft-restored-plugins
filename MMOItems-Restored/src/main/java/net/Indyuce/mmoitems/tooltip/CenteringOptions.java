package net.Indyuce.mmoitems.tooltip;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class CenteringOptions {
   private final boolean name;
   private final double nameFontSize;
   private final int nameSpan;
   private final String namePrefix;
   private final int loreLines;
   private final List<Double> loreFontSize;
   private final List<Integer> loreSpan;
   private final Pattern regex;
   private static final String DEFAULT_REGEX = "(?i)[^&§][a-z][a-z ]*[a-z]";
   private static final double SEPARATOR_SPACE = 1.0;

   public CenteringOptions(@NotNull ConfigurationSection section) {
      this.name = section.getBoolean("display_name.enabled");
      this.nameFontSize = section.getDouble("display_name.font_size");
      this.nameSpan = section.getInt("display_name.span");
      this.namePrefix = section.getString("display_name.prefix", "");
      this.loreLines = section.getInt("lore.lines");
      this.loreFontSize = section.getDoubleList("lore.font_size");
      this.loreSpan = section.getIntegerList("lore.span");
      Validate.isTrue(this.name || this.loreLines > 0, "Centering must be enabled for at least one lore line or the display name");
      Validate.isTrue(this.loreLines >= 0, "Lore line count must be positive");
      if (this.loreLines > 0) {
         Validate.isTrue(!this.loreFontSize.isEmpty(), "You must provide at least one lore font size");
         Validate.isTrue(!this.loreSpan.isEmpty(), "You must provide at least one lore span");

         for (double value : this.loreFontSize) {
            Validate.isTrue(value > 0.0, "Font size must be positive");
         }

         Iterator iterator = this.loreSpan.iterator();

         while (iterator.hasNext()) {
            double value = ((Integer)iterator.next()).intValue();
            Validate.isTrue(value > 0.0, "Font size must be positive");
         }
      }

      if (this.name) {
         Validate.isTrue(this.nameFontSize > 0.0, "Font size must be positive");
         Validate.isTrue(this.nameSpan > 0, "Name must be positive");
      }

      this.regex = Pattern.compile(section.getString("regex", "(?i)[^&§][a-z][a-z ]*[a-z]"));
   }

   public boolean displayName() {
      return this.name;
   }

   public int getLoreLines() {
      return this.loreLines;
   }

   @NotNull
   public String centerLore(int count, String text) {
      return this.center(
         text, "", this.loreFontSize.get(Math.min(count, this.loreFontSize.size() - 1)), this.loreSpan.get(Math.min(count, this.loreSpan.size() - 1))
      );
   }

   @NotNull
   public String centerName(@NotNull String text) {
      return this.center(text, this.namePrefix, this.nameFontSize, this.nameSpan);
   }

   @NotNull
   private String center(@NotNull String text, @NotNull String text2, double value, int count) {
      Matcher matcher = this.regex.matcher(text);
      if (!matcher.find()) {
         return text;
      }

      int matchStart = matcher.start();
      int matchEnd = matcher.end();
      int matchWidth = (int)(value * (matchEnd - matchStart) + 1.0 * this.countSeparators(text.substring(matchStart, matchEnd)));
      if (matchWidth >= count) {
         return text;
      }

      int padding = (count - matchWidth) / 2;
      return text.substring(0, matchStart) + UtilityMethods.getFontSpace(padding) + text2 + text.substring(matchStart);
   }

   private int countSeparators(@NotNull String text) {
      int count = 0;
      boolean flag = true;

      for (int i = 0; i < text.length(); i++) {
         boolean isSpace = text.charAt(i) == ' ';
         if (!flag && !isSpace) {
            count++;
         }

         flag = isSpace;
      }

      return count;
   }
}
