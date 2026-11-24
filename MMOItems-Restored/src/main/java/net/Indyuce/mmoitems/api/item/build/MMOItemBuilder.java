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

   public MMOItemBuilder(MMOItemTemplate mmoItemTemplate, int count, @Nullable ItemTier itemTier) {
      this(mmoItemTemplate, count, itemTier, false);
   }

   public MMOItemBuilder(@NotNull MMOItemTemplate mmoItemTemplate, int count, @Nullable ItemTier itemTier, boolean flag) {
      this.template = mmoItemTemplate;
      this.level = count;
      this.tier = itemTier;
      itemTier = itemTier != null
         ? itemTier
         : (mmoItemTemplate.getBaseItemData().containsKey(ItemStats.TIER) ? MMOItems.plugin.getTiers().get(mmoItemTemplate.getBaseItemData().get(ItemStats.TIER).toString()) : null);
      this.capacity = (mmoItemTemplate.hasModifierCapacity()
            ? mmoItemTemplate.getModifierCapacity()
            : (itemTier != null && itemTier.hasCapacity() ? itemTier.getModifierCapacity() : MMOItems.plugin.getLanguage().defaultItemCapacity))
         .calculate(count);
      this.mmoitem = new MMOItem(mmoItemTemplate.getType(), mmoItemTemplate.getId());
      mmoItemTemplate.getBaseItemData().forEach((var1x, var2x) -> this.applyData(var1x, var2x.randomize(this)));
      if (itemTier != null) {
         this.mmoitem.setData(ItemStats.TIER, new StringData(itemTier.getId()));
      }

      if (count > 0) {
         this.mmoitem.setData(ItemStats.ITEM_LEVEL, new DoubleData(count));
      }

      if (itemTier != null && itemTier.getTooltip() != null && !this.mmoitem.hasData(ItemStats.TOOLTIP)) {
         this.mmoitem.setData(ItemStats.TOOLTIP, new StringData(itemTier.getTooltip().getId()));
      }

      if (!flag && mmoItemTemplate.hasModifierGroup()) {
         mmoItemTemplate.getModifierGroup().collect(this);
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

   public void reduceCapacity(double value) {
      this.capacity -= value;
   }

   protected MMOItem whenBuilt() {
      if (!this.nameModifiers.isEmpty()) {
         StatHistory statHistory = this.mmoitem.computeStatHistory(ItemStats.NAME);
         this.nameModifiers.forEach((var1x, nameModifier) -> {
            NameData nameData = new NameData(null);
            if (nameModifier.getType() == NameModifier.ModifierType.PREFIX) {
               nameData.addPrefix(nameModifier.getFormat());
            } else if (nameModifier.getType() == NameModifier.ModifierType.SUFFIX) {
               nameData.addSuffix(nameModifier.getFormat());
            }

            statHistory.registerModifierBonus(var1x, nameData);
         });
         this.mmoitem.setData(ItemStats.NAME, statHistory.recalculate(this.mmoitem.getUpgradeLevel()));
      }

      return this.mmoitem;
   }

   public void applyData(@NotNull ItemStat itemStat, @NotNull StatData statData) {
      StatData data = this.mmoitem.getData(itemStat);
      if (data != null && data instanceof Mergeable) {
         ((Mergeable)data).mergeWith((Mergeable)statData);
      } else {
         this.mmoitem.setData(itemStat, statData);
      }
   }

   public void addModifierData(@NotNull ItemStat itemStat, @NotNull StatData statData, @NotNull UUID uuid) {
      if (itemStat.getClearStatData() instanceof Mergeable) {
         this.mmoitem.computeStatHistory(itemStat).registerModifierBonus(uuid, statData);
      } else {
         this.mmoitem.setData(itemStat, statData);
      }
   }

   public void addModifier(@NotNull NameModifier nameModifier, @NotNull UUID uuid) {
      Iterator iterator = this.nameModifiers.values().iterator();

      while (iterator.hasNext()) {
         NameModifier nameModifier2 = (NameModifier)iterator.next();
         if (nameModifier2.getType() == nameModifier.getType()) {
            if (nameModifier2.getPriority() > nameModifier.getPriority()) {
               return;
            }

            if (nameModifier2.getPriority() < nameModifier.getPriority()) {
               iterator.remove();
            }
         }
      }

      this.nameModifiers.put(uuid, nameModifier);
   }

   @Deprecated
   @NotNull
   public static Collection<ModifierNode> rollModifiers(@NotNull MMOItemTemplate mmoItemTemplate) {
      if (!mmoItemTemplate.hasOption(MMOItemTemplate.TemplateOption.ROLL_MODIFIER_CHECK_ORDER)) {
         return mmoItemTemplate.getModifiers().values();
      }

      ArrayList list = new ArrayList<>(mmoItemTemplate.getModifiers().values());
      Collections.shuffle(list);
      return list;
   }

   @NotNull
   public MMOItem build() {
      return (MMOItem)super.build();
   }
}
