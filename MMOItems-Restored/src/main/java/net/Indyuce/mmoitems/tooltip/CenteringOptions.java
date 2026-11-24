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

   public CenteringOptions(@NotNull ConfigurationSection var1) {
      this.name = var1.getBoolean("display_name.enabled");
      this.nameFontSize = var1.getDouble("display_name.font_size");
      this.nameSpan = var1.getInt("display_name.span");
      this.namePrefix = var1.getString("display_name.prefix", "");
      this.loreLines = var1.getInt("lore.lines");
      this.loreFontSize = var1.getDoubleList("lore.font_size");
      this.loreSpan = var1.getIntegerList("lore.span");
      Validate.isTrue(this.name || this.loreLines > 0, "Centering must be enabled for at least one lore line or the display name");
      Validate.isTrue(this.loreLines >= 0, "Lore line count must be positive");
      if (this.loreLines > 0) {
         Validate.isTrue(!this.loreFontSize.isEmpty(), "You must provide at least one lore font size");
         Validate.isTrue(!this.loreSpan.isEmpty(), "You must provide at least one lore span");

         for (double var3 : this.loreFontSize) {
            Validate.isTrue(var3 > 0.0, "Font size must be positive");
         }

         Iterator var5 = this.loreSpan.iterator();

         while (var5.hasNext()) {
            double var6 = ((Integer)var5.next()).intValue();
            Validate.isTrue(var6 > 0.0, "Font size must be positive");
         }
      }

      if (this.name) {
         Validate.isTrue(this.nameFontSize > 0.0, "Font size must be positive");
         Validate.isTrue(this.nameSpan > 0, "Name must be positive");
      }

      this.regex = Pattern.compile(var1.getString("regex", "(?i)[^&§][a-z][a-z ]*[a-z]"));
   }

   public boolean displayName() {
      return this.name;
   }

   public int getLoreLines() {
      return this.loreLines;
   }

   @NotNull
   public String centerLore(int var1, String var2) {
      return this.center(
         var2, "", this.loreFontSize.get(Math.min(var1, this.loreFontSize.size() - 1)), this.loreSpan.get(Math.min(var1, this.loreSpan.size() - 1))
      );
   }

   @NotNull
   public String centerName(@NotNull String var1) {
      return this.center(var1, this.namePrefix, this.nameFontSize, this.nameSpan);
   }

   @NotNull
   private String center(@NotNull String var1, @NotNull String var2, double var3, int var5) {
      Matcher var6 = this.regex.matcher(var1);
      if (!var6.find()) {
         return var1;
      }

      int var7 = var6.start();
      int var8 = var6.end();
      int var9 = (int)(var3 * (var8 - var7) + 1.0 * this.countSeparators(var1.substring(var7, var8)));
      if (var9 >= var5) {
         return var1;
      }

      int var10 = (var5 - var9) / 2;
      return var1.substring(0, var7) + UtilityMethods.getFontSpace(var10) + var2 + var1.substring(var7);
   }

   private int countSeparators(@NotNull String var1) {
      int var2 = 0;
      boolean var3 = true;

      for (int var4 = 0; var4 < var1.length(); var4++) {
         boolean var5 = var1.charAt(var4) == ' ';
         if (!var3 && !var5) {
            var2++;
         }

         var3 = var5;
      }

      return var2;
   }
}
