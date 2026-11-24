package net.Indyuce.mmoitems.api.item.mmoitem;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.UpgradeTemplate;
import net.Indyuce.mmoitems.api.item.ItemReference;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.data.MaterialData;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.tooltip.TooltipTexture;
import net.Indyuce.mmoitems.util.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItem implements ItemReference {
   private final Type type;
   private final String id;
   @NotNull
   private final Map<ItemStat, StatData> stats = new HashMap<>();
   @NotNull
   private final Map<ItemStat<?, ?>, StatHistory> mergeableStatHistory = new HashMap<>();

   public MMOItem(Type type, String text) {
      this.type = type;
      this.id = text;
   }

   @Override
   public Type getType() {
      return this.type;
   }

   @Override
   public String getId() {
      return this.id;
   }

   public void mergeData(@NotNull ItemStat itemStat, @NotNull StatData statData, @Nullable UUID uuid) {
      if (statData instanceof Mergeable) {
         StatHistory statHistory = this.computeStatHistory(itemStat);
         if (uuid != null) {
            statHistory.registerGemstoneData(uuid, statData);
         } else {
            statHistory.registerExternalData(statData);
         }

         this.setData(itemStat, statHistory.recalculate(this.getUpgradeLevel()));
      } else {
         this.setData(itemStat, statData);
      }
   }

   public void setData(@NotNull ItemStat itemStat, @NotNull StatData statData) {
      this.stats.put(itemStat, statData);
   }

   public void replaceData(@NotNull ItemStat itemStat, @NotNull StatData statData) {
      this.stats.replace(itemStat, statData);
   }

   public void removeData(@NotNull ItemStat itemStat) {
      this.stats.remove(itemStat);
   }

   @Nullable
   public StatData getData(@NotNull ItemStat itemStat) {
      return this.stats.get(itemStat);
   }

   @NotNull
   public StatData computeData(@NotNull ItemStat<?, ?> itemStat) {
      return this.stats.computeIfAbsent(itemStat, ItemStat::getClearStatData);
   }

   public boolean hasData(@NotNull ItemStat itemStat) {
      return this.stats.get(itemStat) != null;
   }

   @NotNull
   public Set<ItemStat> getStats() {
      return this.stats.keySet();
   }

   @NotNull
   public ItemStackBuilder newBuilder() {
      return new ItemStackBuilder(this);
   }

   public MMOItem clone() {
      MMOItem mmoItem = new MMOItem(this.type, this.id);
      mmoItem.stats.putAll(this.stats);
      this.mergeableStatHistory.forEach((var1x, statHistory) -> mmoItem.mergeableStatHistory.put((ItemStat<?, ?>)var1x, statHistory.clone().setParent(mmoItem)));
      return mmoItem;
   }

   public boolean hasStatHistory(@NotNull ItemStat<?, ?> itemStat) {
      return itemStat instanceof Mergeable && this.mergeableStatHistory.containsKey(itemStat);
   }

   @Nullable
   public StatHistory getStatHistory(@NotNull ItemStat<?, ?> itemStat) {
      return itemStat.equals(ItemStats.ENCHANTS) ? this.computeStatHistory(itemStat) : this.mergeableStatHistory.get(itemStat);
   }

   @NotNull
   public StatHistory computeStatHistory(@NotNull ItemStat<?, ?> itemStat) {
      return this.mergeableStatHistory.computeIfAbsent(itemStat, var1x -> {
         StatData data = this.computeData((ItemStat<?, ?>)var1x);
         Validate.isTrue(data instanceof Mergeable, "Stat " + var1x.getId() + " is not mergeable");
         return new StatHistory(this, var1x, data);
      });
   }

   @NotNull
   public ArrayList<StatHistory> getStatHistories() {
      return new ArrayList<>(this.mergeableStatHistory.values());
   }

   public void setStatHistory(@NotNull ItemStat itemStat, @NotNull StatHistory statHistory) {
      this.mergeableStatHistory.put(itemStat, statHistory);
   }

   @Nullable
   public ItemTier getTier() {
      StatData statData = this.stats.get(ItemStats.TIER);
      return statData != null ? MMOItems.plugin.getTiers().get(statData.toString()) : null;
   }

   @Nullable
   public TooltipTexture getTooltip() {
      StatData statData = this.stats.get(ItemStats.TOOLTIP);
      return statData != null ? MMOItems.plugin.getLore().getTooltip(statData.toString()) : null;
   }

   @Deprecated
   public int getDamage() {
      int count = this.hasData(ItemStats.MAX_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.MAX_DURABILITY)).getValue()) : -1;
      if (count > 0) {
         NBTItem nBT = this.newBuilder().buildNBT();
         int currentDurability = this.hasData(ItemStats.CUSTOM_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.CUSTOM_DURABILITY)).getValue()) : count;
         return count - currentDurability;
      } else {
         return this.hasData(ItemStats.ITEM_DAMAGE) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.ITEM_DAMAGE)).getValue()) : 0;
      }
   }

   @Deprecated
   public void setDamage(int count) {
      if (!this.hasData(ItemStats.UNBREAKABLE)) {
         int maxDurability = this.hasData(ItemStats.MAX_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.MAX_DURABILITY)).getValue()) : -1;
         if (maxDurability > 0) {
            NBTItem nBT = this.newBuilder().buildNBT();
            this.setData(ItemStats.CUSTOM_DURABILITY, new DoubleData(maxDurability - count));
            Material material = this.hasData(ItemStats.MATERIAL) ? ((MaterialData)this.getData(ItemStats.MATERIAL)).getMaterial() : Material.GOLD_INGOT;
            double value = (double)count * material.getMaxDurability() / maxDurability;
            if (value == 0.0) {
               return;
            }

            this.setData(ItemStats.ITEM_DAMAGE, new DoubleData(value));
         } else {
            this.setData(ItemStats.ITEM_DAMAGE, new DoubleData(count));
         }
      }
   }

   public void upgrade() {
      this.getUpgradeTemplate().upgrade(this);
   }

   public boolean hasUpgradeTemplate() {
      return this.hasData(ItemStats.UPGRADE) && ((UpgradeData)this.getData(ItemStats.UPGRADE)).getTemplate() != null;
   }

   public int getUpgradeLevel() {
      return this.hasData(ItemStats.UPGRADE) ? ((UpgradeData)this.getData(ItemStats.UPGRADE)).getLevel() : 0;
   }

   public int getMaxUpgradeLevel() {
      return this.hasData(ItemStats.UPGRADE) ? ((UpgradeData)this.getData(ItemStats.UPGRADE)).getMax() : 0;
   }

   @NotNull
   public UpgradeTemplate getUpgradeTemplate() {
      Validate.isTrue(this.hasUpgradeTemplate(), "Item has no upgrade information");
      UpgradeData data = (UpgradeData)this.getData(ItemStats.UPGRADE);
      return data.getTemplate();
   }

   @NotNull
   public List<GemstoneData> getGemstones() {
      StatData data = this.getData(ItemStats.GEM_SOCKETS);
      return data == null ? new ArrayList<>() : ((GemSocketsData)data).getGems();
   }

   @Deprecated
   public Set<GemstoneData> getGemStones() {
      return new HashSet<>(this.getGemstones());
   }

   @NotNull
   public List<Pair<GemstoneData, MMOItem>> extractGemstones() {
      GemSocketsData data = (GemSocketsData)this.getData(ItemStats.GEM_SOCKETS);
      if (data == null) {
         return new ArrayList<>();
      }

      ArrayList<Pair<GemstoneData, MMOItem>> pairs = new ArrayList<>();

      for (GemstoneData gemstone : data.getGemstones()) {
         MMOItem mMOItem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(gemstone.getMMOItemType()), gemstone.getMMOItemID());
         if (mMOItem != null) {
            pairs.add(Pair.of(gemstone, mMOItem));
         }
      }

      if (MMOItemReforger.gemstonesRevIDWhenUnsocket) {
         return pairs;
      }

      for (StatHistory statHistory : this.mergeableStatHistory.values()) {
         for (Pair<GemstoneData, MMOItem> pair : pairs) {
            StatData gemstoneData = statHistory.getGemstoneData(((GemstoneData)pair.getKey()).getHistoricUUID());
            if (gemstoneData != null) {
               ((MMOItem)pair.getValue()).setData(statHistory.getItemStat(), gemstoneData);
            }
         }
      }

      return pairs;
   }

   @Nullable
   public MMOItem extractGemstone(@NotNull GemstoneData gemstoneData) {
      MMOItem mMOItem = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(gemstoneData.getMMOItemType()), gemstoneData.getMMOItemID());
      if (mMOItem == null) {
         return null;
      }

      if (MMOItemReforger.gemstonesRevIDWhenUnsocket) {
         return mMOItem;
      }

      for (StatHistory statHistory : this.mergeableStatHistory.values()) {
         StatData oldGemstoneData = statHistory.getGemstoneData(gemstoneData.getHistoricUUID());
         if (oldGemstoneData != null) {
            mMOItem.setData(statHistory.getItemStat(), oldGemstoneData);
         }
      }

      return mMOItem;
   }

   public void removeGemStone(@NotNull UUID uuid, @Nullable String text) {
      if (this.hasData(ItemStats.GEM_SOCKETS)) {
         StatHistory statHistory = this.computeStatHistory(ItemStats.GEM_SOCKETS);
         if (!((GemSocketsData)statHistory.getOriginalData()).removeGem(uuid, text)) {
            for (UUID allGemstone : statHistory.getAllGemstones()) {
               if (((GemSocketsData)statHistory.getGemstoneData(allGemstone)).removeGem(uuid, text)) {
                  return;
               }
            }

            for (StatData statData : statHistory.getExternalData()) {
               if (((GemSocketsData)statData).removeGem(uuid, text)) {
                  return;
               }
            }

            for (UUID allModifier : statHistory.getAllModifiers()) {
               if (((GemSocketsData)statHistory.getModifiersBonus(allModifier)).removeGem(uuid, text)) {
                  return;
               }
            }
         }
      }
   }
}
