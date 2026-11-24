package net.Indyuce.mmoitems.tooltip;

import io.lumine.mythic.lib.UtilityMethods;
import java.util.List;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TooltipTexture {
   private final String id;
   @NotNull
   private final String top;
   @NotNull
   private final String middle;
   @NotNull
   private final String bottom;
   @Nullable
   private final String bar;
   @Nullable
   private final String hideVanilla;
   @Nullable
   private final String alignText;
   private final List<String> loreHeader;
   private final int ignoreFirst;
   private final CenteringOptions centering;

   public TooltipTexture(@NotNull ConfigurationSection var1) {
      this.id = UtilityMethods.enumName(var1.getName());
      String var2 = UtilityMethods.getFontSpace(var1.getInt("align_texture"));
      this.alignText = UtilityMethods.getFontSpace(var1.getInt("align_text"));
      this.hideVanilla = UtilityMethods.getFontSpace(var1.getInt("hide_texture"));
      this.top = var2 + var1.getString("top") + this.alignText;
      this.loreHeader = var1.getStringList("lore_header");
      this.middle = var2 + var1.getString("middle") + this.alignText;
      this.bar = var1.get("bar") != null ? var2 + var1.getString("bar") : null;
      this.bottom = var2 + var1.getString("bottom");
      this.ignoreFirst = var1.getInt("ignore_first");

      try {
         this.centering = var1.isConfigurationSection("center") ? new CenteringOptions(var1.getConfigurationSection("center")) : null;
      } catch (RuntimeException var4) {
         throw new RuntimeException("Could not load centering options: " + var4.getMessage());
      }

      Validate.notNull(this.top, "Tooltip top portion cannot be null");
      Validate.notNull(this.middle, "Tooltip middle portion cannot be null");
      Validate.notNull(this.bottom, "Tooltip bottom portion cannot be null");
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String getTop() {
      return this.top;
   }

   @NotNull
   public String getMiddle() {
      return this.middle;
   }

   @NotNull
   public String getAlignText() {
      return this.alignText;
   }

   @NotNull
   public String getBottom() {
      return this.bottom;
   }

   @NotNull
   public String getBar() {
      return this.bar == null ? this.middle : this.bar;
   }

   @NotNull
   public String getSuffix() {
      return this.hideVanilla;
   }

   public int getFirstIgnored() {
      return this.ignoreFirst;
   }

   @Nullable
   public CenteringOptions getCenteringOptions() {
      return this.centering;
   }

   @Nullable
   public List<String> getLoreHeader() {
      return this.loreHeader;
   }
}
