package net.Indyuce.mmoitems.api.item.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import net.Indyuce.mmoitems.api.item.template.ModifierNode;
import net.Indyuce.mmoitems.api.item.template.NameModifier;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.StringData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.NameData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.util.Buildable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemBuilder extends Buildable<MMOItem> {
   private final MMOItemTemplate template;
   private final MMOItem mmoitem;
   private final int level;
   private final ItemTier tier;
   private double capacity;
   private final Map<UUID, NameModifier> nameModifiers = new HashMap<>();

   public MMOItemBuilder(MMOItemTemplate var1, int var2, @Nullable ItemTier var3) {
      this(var1, var2, var3, false);
   }

   public MMOItemBuilder(@NotNull MMOItemTemplate var1, int var2, @Nullable ItemTier var3, boolean var4) {
      this.template = var1;
      this.level = var2;
      this.tier = var3;
      var3 = var3 != null
         ? var3
         : (var1.getBaseItemData().containsKey(ItemStats.TIER) ? MMOItems.plugin.getTiers().get(var1.getBaseItemData().get(ItemStats.TIER).toString()) : null);
      this.capacity = (var1.hasModifierCapacity()
            ? var1.getModifierCapacity()
            : (var3 != null && var3.hasCapacity() ? var3.getModifierCapacity() : MMOItems.plugin.getLanguage().defaultItemCapacity))
         .calculate(var2);
      this.mmoitem = new MMOItem(var1.getType(), var1.getId());
      var1.getBaseItemData().forEach((var1x, var2x) -> this.applyData(var1x, var2x.randomize(this)));
      if (var3 != null) {
         this.mmoitem.setData(ItemStats.TIER, new StringData(var3.getId()));
      }

      if (var2 > 0) {
         this.mmoitem.setData(ItemStats.ITEM_LEVEL, new DoubleData(var2));
      }

      if (var3 != null && var3.getTooltip() != null && !this.mmoitem.hasData(ItemStats.TOOLTIP)) {
         this.mmoitem.setData(ItemStats.TOOLTIP, new StringData(var3.getTooltip().getId()));
      }

      if (!var4 && var1.hasModifierGroup()) {
         var1.getModifierGroup().collect(this);
      }
   }

   @NotNull
   public MMOItemTemplate getTemplate() {
      return this.template;
   }

   public int getLevel() {
      return this.level;
   }

   @Nullable
   public ItemTier getTier() {
      return this.tier;
   }

   public double getCapacity() {
      return this.capacity;
   }

   public void reduceCapacity(double var1) {
      this.capacity -= var1;
   }

   protected MMOItem whenBuilt() {
      if (!this.nameModifiers.isEmpty()) {
         StatHistory var1 = this.mmoitem.computeStatHistory(ItemStats.NAME);
         this.nameModifiers.forEach((var1x, var2) -> {
            NameData var3 = new NameData(null);
            if (var2.getType() == NameModifier.ModifierType.PREFIX) {
               var3.addPrefix(var2.getFormat());
            } else if (var2.getType() == NameModifier.ModifierType.SUFFIX) {
               var3.addSuffix(var2.getFormat());
            }

            var1.registerModifierBonus(var1x, var3);
         });
         this.mmoitem.setData(ItemStats.NAME, var1.recalculate(this.mmoitem.getUpgradeLevel()));
      }

      return this.mmoitem;
   }

   public void applyData(@NotNull ItemStat var1, @NotNull StatData var2) {
      StatData var3 = this.mmoitem.getData(var1);
      if (var3 != null && var3 instanceof Mergeable) {
         ((Mergeable)var3).mergeWith((Mergeable)var2);
      } else {
         this.mmoitem.setData(var1, var2);
      }
   }

   public void addModifierData(@NotNull ItemStat var1, @NotNull StatData var2, @NotNull UUID var3) {
      if (var1.getClearStatData() instanceof Mergeable) {
         this.mmoitem.computeStatHistory(var1).registerModifierBonus(var3, var2);
      } else {
         this.mmoitem.setData(var1, var2);
      }
   }

   public void addModifier(@NotNull NameModifier var1, @NotNull UUID var2) {
      Iterator var3 = this.nameModifiers.values().iterator();

      while (var3.hasNext()) {
         NameModifier var4 = (NameModifier)var3.next();
         if (var4.getType() == var1.getType()) {
            if (var4.getPriority() > var1.getPriority()) {
               return;
            }

            if (var4.getPriority() < var1.getPriority()) {
               var3.remove();
            }
         }
      }

      this.nameModifiers.put(var2, var1);
   }

   @Deprecated
   @NotNull
   public static Collection<ModifierNode> rollModifiers(@NotNull MMOItemTemplate var0) {
      if (!var0.hasOption(MMOItemTemplate.TemplateOption.ROLL_MODIFIER_CHECK_ORDER)) {
         return var0.getModifiers().values();
      }

      ArrayList var1 = new ArrayList<>(var0.getModifiers().values());
      Collections.shuffle(var1);
      return var1;
   }

   @NotNull
   public MMOItem build() {
      return (MMOItem)super.build();
   }
}
