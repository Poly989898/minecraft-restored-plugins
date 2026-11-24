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

   public TooltipTexture(@NotNull ConfigurationSection section) {
      this.id = UtilityMethods.enumName(section.getName());
      String fontSpace = UtilityMethods.getFontSpace(section.getInt("align_texture"));
      this.alignText = UtilityMethods.getFontSpace(section.getInt("align_text"));
      this.hideVanilla = UtilityMethods.getFontSpace(section.getInt("hide_texture"));
      this.top = fontSpace + section.getString("top") + this.alignText;
      this.loreHeader = section.getStringList("lore_header");
      this.middle = fontSpace + section.getString("middle") + this.alignText;
      this.bar = section.get("bar") != null ? fontSpace + section.getString("bar") : null;
      this.bottom = fontSpace + section.getString("bottom");
      this.ignoreFirst = section.getInt("ignore_first");

      try {
         this.centering = section.isConfigurationSection("center") ? new CenteringOptions(section.getConfigurationSection("center")) : null;
      } catch (RuntimeException exception) {
         throw new RuntimeException("Could not load centering options: " + exception.getMessage());
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
