package net.Indyuce.mmoitems.api.item.build;

import com.google.common.collect.Lists;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.tooltip.TooltipTexture;
import net.Indyuce.mmoitems.util.Buildable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoreBuilder extends Buildable<List<String>> {
   private final List<String> lore = new ArrayList<>();
   private final List<String> end = new ArrayList<>();
   private final Map<String, String> placeholders = new HashMap<>();
   private final TooltipTexture tooltip;
   private static final String LINE_PREFIX = ChatColor.WHITE.toString();

   @Deprecated
   public LoreBuilder(@NotNull Collection<String> var1) {
      this.lore.addAll(var1);
      this.tooltip = null;
   }

   public LoreBuilder(@NotNull MMOItem var1) {
      this.lore.addAll(MMOItems.plugin.getLore().getFormat(var1));
      this.tooltip = var1.getTooltip();
      this.registerPlaceholder("type", var1.getType().getName());
      ItemTier var2 = var1.getTier();
      this.registerPlaceholder("tier", var2 != null ? var2.getName() : MMOItems.plugin.getLanguage().defaultTierName);
   }

   public void insert(int var1, @NotNull String var2) {
      this.lore.add(var1, var2);
   }

   public void insert(int var1, @NotNull Collection<String> var2) {
      this.lore.addAll(var1, var2);
   }

   public void insert(String var1, String... var2) {
      int var3 = this.lore.indexOf("#" + var1 + "#");
      if (var3 >= 0) {
         for (int var4 = 0; var4 < var2.length; var4++) {
            this.lore.add(var3 + 1, var2[var2.length - var4 - 1]);
         }

         this.lore.remove(var3);
      }
   }

   public void insert(@NotNull String var1, @NotNull List<String> var2) {
      int var3 = this.lore.indexOf("#" + var1 + "#");
      if (var3 >= 0) {
         Lists.reverse(var2).forEach(var2x -> this.lore.add(var3 + 1, var2x));
         this.lore.remove(var3);
      }
   }

   public void registerPlaceholder(@NotNull String var1, @Nullable Object var2) {
      this.placeholders.put(var1, String.valueOf(var2));
   }

   @NotNull
   public String applySpecialPlaceholders(String var1) {
      int var2 = var1.indexOf("{");

      while (var2 >= 0) {
         int var3 = var1.indexOf("}", var2);
         if (var3 < 0) {
            break;
         }

         String var4 = var1.substring(var2 + 1, var3);
         String var5 = this.placeholders.get(var4);
         if (var5 != null) {
            var1 = var1.substring(0, var2) + var5 + var1.substring(var3 + 1);
            var3 += var5.length() - var4.length() - 2;
         }

         var2 = var1.indexOf("{", var3 + 1);
      }

      return var1;
   }

   @Deprecated
   public void end(@NotNull String var1) {
      this.end.add(var1);
   }

   protected List<String> whenBuilt() {
      int var1 = 0;

      while (var1 < this.lore.size()) {
         int var2 = this.lore.size() - var1 - 1;
         String var3 = this.lore.get(var2);
         if (var3.startsWith("#")) {
            this.lore.remove(var2);
         } else if (!var3.startsWith("{bar}") || var1 != 0 && !this.getType(var2 + 1).isBar()) {
            var1++;
         } else {
            this.lore.remove(var2);
         }
      }

      String var9 = this.tooltip != null ? this.tooltip.getSuffix() : "";
      if (this.tooltip != null) {
         this.lore.add(this.tooltip.getBottom() + var9);
         if (this.tooltip.getLoreHeader() != null) {
            this.lore.addAll(0, this.tooltip.getLoreHeader());
         }
      }

      int var10 = this.tooltip != null ? this.tooltip.getFirstIgnored() : 0;
      int var11 = 0;

      while (var11 < this.lore.size()) {
         String var4 = this.lore.get(var11);
         LoreBuilder.LineType var5 = this.getType(var11, var4);
         if (var5.isNormalBar()) {
            var4 = var4.substring(5);
         }

         if (var5.isSuperBar()) {
            var4 = var4.substring(6);
         }

         if (this.tooltip != null && !var5.isBottom()) {
            var4 = (var11 < var10 ? this.tooltip.getAlignText() : (var5.isBar() ? this.tooltip.getBar() : this.tooltip.getMiddle())) + var4;
         }

         String var6 = StringUtils.substringBetween(var4, "MATH%", "%");
         if (var6 != null) {
            var4 = var4.replaceFirst("MATH\\%[^%]*\\%", this.evaluate(var6));
         }

         var4 = MythicLib.plugin.getPlaceholderParser().parse(null, var4);
         var4 = this.applySpecialPlaceholders(var4);
         String[] var7 = var4.split("\n", -1);
         if (var7.length > 1) {
            for (int var8 = var7.length - 1; var8 >= 0; var8--) {
               this.lore.add(var11, LINE_PREFIX + var7[var8] + (var11 < var10 ? "" : var9));
            }

            this.lore.remove(var11 + var7.length);
            var11 += var7.length;
         } else {
            this.lore.set(var11++, LINE_PREFIX + var4 + var9);
         }
      }

      if (this.tooltip != null && this.tooltip.getCenteringOptions() != null) {
         for (int var12 = 0; var12 < this.tooltip.getCenteringOptions().getLoreLines(); var12++) {
            this.lore.set(var12, this.tooltip.getCenteringOptions().centerLore(var12, this.lore.get(var12)));
         }
      }

      this.lore.addAll(this.end);
      return this.lore;
   }

   @NotNull
   private LoreBuilder.LineType getType(int var1) {
      return this.getType(var1, this.lore.get(var1));
   }

   @NotNull
   private LoreBuilder.LineType getType(int var1, String var2) {
      if (var1 == this.lore.size() - 1) {
         if (var2.startsWith("{bar}")) {
            return LoreBuilder.LineType.BAR;
         } else {
            return var2.startsWith("{sbar}") ? LoreBuilder.LineType.SUPERBAR : LoreBuilder.LineType.BOTTOM;
         }
      } else if (var2.startsWith("{bar}")) {
         return LoreBuilder.LineType.BAR;
      } else {
         return var2.startsWith("{sbar}") ? LoreBuilder.LineType.SUPERBAR : LoreBuilder.LineType.MIDDLE;
      }
   }

   @Deprecated
   private String evaluate(String var1) {
      try {
         return String.valueOf(NumericalExpression.eval(var1));
      } catch (Throwable var3) {
         return "<ParsingError>";
      }
   }

   @NotNull
   public List<String> getLore() {
      return this.lore;
   }

   public boolean hasTooltip() {
      return this.tooltip != null;
   }

   @NotNull
   public TooltipTexture getTooltip() {
      return Objects.requireNonNull(this.tooltip);
   }

   public void setLore(List<String> var1) {
      this.lore.clear();
      this.lore.addAll(var1);
   }

   @NotNull
   public List<String> build() {
      return (List<String>)super.build();
   }

   private enum LineType {
      MIDDLE,
      BAR,
      SUPERBAR,
      BOTTOM,
      BOTTOM_BAR,
      BOTTOM_SUPERBAR;

      boolean isBar() {
         return this.isNormalBar() || this.isSuperBar();
      }

      boolean isBottom() {
         return this == BOTTOM || this == BOTTOM_BAR || this == BOTTOM_SUPERBAR;
      }

      boolean isSuperBar() {
         return this == SUPERBAR || this == BOTTOM_SUPERBAR;
      }

      boolean isNormalBar() {
         return this == BAR || this == BOTTOM_BAR;
      }
   }
}
