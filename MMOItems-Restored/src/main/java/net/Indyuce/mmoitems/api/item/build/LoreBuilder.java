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
   public LoreBuilder(@NotNull Collection<String> texts) {
      this.lore.addAll(texts);
      this.tooltip = null;
   }

   public LoreBuilder(@NotNull MMOItem mmoItem) {
      this.lore.addAll(MMOItems.plugin.getLore().getFormat(mmoItem));
      this.tooltip = mmoItem.getTooltip();
      this.registerPlaceholder("type", mmoItem.getType().getName());
      ItemTier tier = mmoItem.getTier();
      this.registerPlaceholder("tier", tier != null ? tier.getName() : MMOItems.plugin.getLanguage().defaultTierName);
   }

   public void insert(int count, @NotNull String text) {
      this.lore.add(count, text);
   }

   public void insert(int count, @NotNull Collection<String> texts) {
      this.lore.addAll(count, texts);
   }

   public void insert(String text, String... texts) {
      int count = this.lore.indexOf("#" + text + "#");
      if (count >= 0) {
         for (int i = 0; i < texts.length; i++) {
            this.lore.add(count + 1, texts[texts.length - i - 1]);
         }

         this.lore.remove(count);
      }
   }

   public void insert(@NotNull String text, @NotNull List<String> texts) {
      int count = this.lore.indexOf("#" + text + "#");
      if (count >= 0) {
         Lists.reverse(texts).forEach(var2x -> this.lore.add(count + 1, var2x));
         this.lore.remove(count);
      }
   }

   public void registerPlaceholder(@NotNull String text, @Nullable Object obj) {
      this.placeholders.put(text, String.valueOf(obj));
   }

   @NotNull
   public String applySpecialPlaceholders(String text) {
      int count = text.indexOf("{");

      while (count >= 0) {
         int count2 = text.indexOf("}", count);
         if (count2 < 0) {
            break;
         }

         String text2 = text.substring(count + 1, count2);
         String text3 = this.placeholders.get(text2);
         if (text3 != null) {
            text = text.substring(0, count) + text3 + text.substring(count2 + 1);
            count2 += text3.length() - text2.length() - 2;
         }

         count = text.indexOf("{", count2 + 1);
      }

      return text;
   }

   @Deprecated
   public void end(@NotNull String text) {
      this.end.add(text);
   }

   protected List<String> whenBuilt() {
      int count = 0;

      while (count < this.lore.size()) {
         int index = this.lore.size() - count - 1;
         String text = this.lore.get(index);
         if (text.startsWith("#")) {
            this.lore.remove(index);
         } else if (!text.startsWith("{bar}") || count != 0 && !this.getType(index + 1).isBar()) {
            count++;
         } else {
            this.lore.remove(index);
         }
      }

      String text = this.tooltip != null ? this.tooltip.getSuffix() : "";
      if (this.tooltip != null) {
         this.lore.add(this.tooltip.getBottom() + text);
         if (this.tooltip.getLoreHeader() != null) {
            this.lore.addAll(0, this.tooltip.getLoreHeader());
         }
      }

      int count2 = this.tooltip != null ? this.tooltip.getFirstIgnored() : 0;
      int index = 0;

      while (index < this.lore.size()) {
         String text2 = this.lore.get(index);
         LoreBuilder.LineType type = this.getType(index, text2);
         if (type.isNormalBar()) {
            text2 = text2.substring(5);
         }

         if (type.isSuperBar()) {
            text2 = text2.substring(6);
         }

         if (this.tooltip != null && !type.isBottom()) {
            text2 = (index < count2 ? this.tooltip.getAlignText() : (type.isBar() ? this.tooltip.getBar() : this.tooltip.getMiddle())) + text2;
         }

         String text3 = StringUtils.substringBetween(text2, "MATH%", "%");
         if (text3 != null) {
            text2 = text2.replaceFirst("MATH\\%[^%]*\\%", this.evaluate(text3));
         }

         text2 = MythicLib.plugin.getPlaceholderParser().parse(null, text2);
         text2 = this.applySpecialPlaceholders(text2);
         String[] texts = text2.split("\n", -1);
         if (texts.length > 1) {
            for (int i = texts.length - 1; i >= 0; i--) {
               this.lore.add(index, LINE_PREFIX + texts[i] + (index < count2 ? "" : text));
            }

            this.lore.remove(index + texts.length);
            index += texts.length;
         } else {
            this.lore.set(index++, LINE_PREFIX + text2 + text);
         }
      }

      if (this.tooltip != null && this.tooltip.getCenteringOptions() != null) {
         for (int i = 0; i < this.tooltip.getCenteringOptions().getLoreLines(); i++) {
            this.lore.set(i, this.tooltip.getCenteringOptions().centerLore(i, this.lore.get(i)));
         }
      }

      this.lore.addAll(this.end);
      return this.lore;
   }

   @NotNull
   private LoreBuilder.LineType getType(int index) {
      return this.getType(index, this.lore.get(index));
   }

   @NotNull
   private LoreBuilder.LineType getType(int count, String text) {
      if (count == this.lore.size() - 1) {
         if (text.startsWith("{bar}")) {
            return LoreBuilder.LineType.BAR;
         } else {
            return text.startsWith("{sbar}") ? LoreBuilder.LineType.SUPERBAR : LoreBuilder.LineType.BOTTOM;
         }
      } else if (text.startsWith("{bar}")) {
         return LoreBuilder.LineType.BAR;
      } else {
         return text.startsWith("{sbar}") ? LoreBuilder.LineType.SUPERBAR : LoreBuilder.LineType.MIDDLE;
      }
   }

   @Deprecated
   private String evaluate(String text) {
      try {
         return String.valueOf(NumericalExpression.eval(text));
      } catch (Throwable exception) {
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

   public void setLore(List<String> texts) {
      this.lore.clear();
      this.lore.addAll(texts);
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
