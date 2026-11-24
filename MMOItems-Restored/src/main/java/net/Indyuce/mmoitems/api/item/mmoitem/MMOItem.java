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

   public MMOItem(Type var1, String var2) {
      this.type = var1;
      this.id = var2;
   }

   @Override
   public Type getType() {
      return this.type;
   }

   @Override
   public String getId() {
      return this.id;
   }

   public void mergeData(@NotNull ItemStat var1, @NotNull StatData var2, @Nullable UUID var3) {
      if (var2 instanceof Mergeable) {
         StatHistory var4 = this.computeStatHistory(var1);
         if (var3 != null) {
            var4.registerGemstoneData(var3, var2);
         } else {
            var4.registerExternalData(var2);
         }

         this.setData(var1, var4.recalculate(this.getUpgradeLevel()));
      } else {
         this.setData(var1, var2);
      }
   }

   public void setData(@NotNull ItemStat var1, @NotNull StatData var2) {
      this.stats.put(var1, var2);
   }

   public void replaceData(@NotNull ItemStat var1, @NotNull StatData var2) {
      this.stats.replace(var1, var2);
   }

   public void removeData(@NotNull ItemStat var1) {
      this.stats.remove(var1);
   }

   @Nullable
   public StatData getData(@NotNull ItemStat var1) {
      return this.stats.get(var1);
   }

   @NotNull
   public StatData computeData(@NotNull ItemStat<?, ?> var1) {
      return this.stats.computeIfAbsent(var1, ItemStat::getClearStatData);
   }

   public boolean hasData(@NotNull ItemStat var1) {
      return this.stats.get(var1) != null;
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
      MMOItem var1 = new MMOItem(this.type, this.id);
      var1.stats.putAll(this.stats);
      this.mergeableStatHistory.forEach((var1x, var2) -> var1.mergeableStatHistory.put((ItemStat<?, ?>)var1x, var2.clone().setParent(var1)));
      return var1;
   }

   public boolean hasStatHistory(@NotNull ItemStat<?, ?> var1) {
      return var1 instanceof Mergeable && this.mergeableStatHistory.containsKey(var1);
   }

   @Nullable
   public StatHistory getStatHistory(@NotNull ItemStat<?, ?> var1) {
      return var1.equals(ItemStats.ENCHANTS) ? this.computeStatHistory(var1) : this.mergeableStatHistory.get(var1);
   }

   @NotNull
   public StatHistory computeStatHistory(@NotNull ItemStat<?, ?> var1) {
      return this.mergeableStatHistory.computeIfAbsent(var1, var1x -> {
         StatData var2 = this.computeData((ItemStat<?, ?>)var1x);
         Validate.isTrue(var2 instanceof Mergeable, "Stat " + var1x.getId() + " is not mergeable");
         return new StatHistory(this, var1x, var2);
      });
   }

   @NotNull
   public ArrayList<StatHistory> getStatHistories() {
      return new ArrayList<>(this.mergeableStatHistory.values());
   }

   public void setStatHistory(@NotNull ItemStat var1, @NotNull StatHistory var2) {
      this.mergeableStatHistory.put(var1, var2);
   }

   @Nullable
   public ItemTier getTier() {
      StatData var1 = this.stats.get(ItemStats.TIER);
      return var1 != null ? MMOItems.plugin.getTiers().get(var1.toString()) : null;
   }

   @Nullable
   public TooltipTexture getTooltip() {
      StatData var1 = this.stats.get(ItemStats.TOOLTIP);
      return var1 != null ? MMOItems.plugin.getLore().getTooltip(var1.toString()) : null;
   }

   @Deprecated
   public int getDamage() {
      int var1 = this.hasData(ItemStats.MAX_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.MAX_DURABILITY)).getValue()) : -1;
      if (var1 > 0) {
         NBTItem var2 = this.newBuilder().buildNBT();
         int var3 = this.hasData(ItemStats.CUSTOM_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.CUSTOM_DURABILITY)).getValue()) : var1;
         return var1 - var3;
      } else {
         return this.hasData(ItemStats.ITEM_DAMAGE) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.ITEM_DAMAGE)).getValue()) : 0;
      }
   }

   @Deprecated
   public void setDamage(int var1) {
      if (!this.hasData(ItemStats.UNBREAKABLE)) {
         int var2 = this.hasData(ItemStats.MAX_DURABILITY) ? SilentNumbers.round(((DoubleData)this.getData(ItemStats.MAX_DURABILITY)).getValue()) : -1;
         if (var2 > 0) {
            NBTItem var3 = this.newBuilder().buildNBT();
            this.setData(ItemStats.CUSTOM_DURABILITY, new DoubleData(var2 - var1));
            Material var4 = this.hasData(ItemStats.MATERIAL) ? ((MaterialData)this.getData(ItemStats.MATERIAL)).getMaterial() : Material.GOLD_INGOT;
            double var5 = (double)var1 * var4.getMaxDurability() / var2;
            if (var5 == 0.0) {
               return;
            }

            this.setData(ItemStats.ITEM_DAMAGE, new DoubleData(var5));
         } else {
            this.setData(ItemStats.ITEM_DAMAGE, new DoubleData(var1));
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
      UpgradeData var1 = (UpgradeData)this.getData(ItemStats.UPGRADE);
      return var1.getTemplate();
   }

   @NotNull
   public List<GemstoneData> getGemstones() {
      StatData var1 = this.getData(ItemStats.GEM_SOCKETS);
      return var1 == null ? new ArrayList<>() : ((GemSocketsData)var1).getGems();
   }

   @Deprecated
   public Set<GemstoneData> getGemStones() {
      return new HashSet<>(this.getGemstones());
   }

   @NotNull
   public List<Pair<GemstoneData, MMOItem>> extractGemstones() {
      GemSocketsData var1 = (GemSocketsData)this.getData(ItemStats.GEM_SOCKETS);
      if (var1 == null) {
         return new ArrayList<>();
      }

      ArrayList<Pair<GemstoneData, MMOItem>> var2 = new ArrayList<>();

      for (GemstoneData var4 : var1.getGemstones()) {
         MMOItem var5 = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(var4.getMMOItemType()), var4.getMMOItemID());
         if (var5 != null) {
            var2.add(Pair.of(var4, var5));
         }
      }

      if (MMOItemReforger.gemstonesRevIDWhenUnsocket) {
         return var2;
      }

      for (StatHistory var9 : this.mergeableStatHistory.values()) {
         for (Pair<GemstoneData, MMOItem> var6 : var2) {
            StatData var7 = var9.getGemstoneData(((GemstoneData)var6.getKey()).getHistoricUUID());
            if (var7 != null) {
               ((MMOItem)var6.getValue()).setData(var9.getItemStat(), var7);
            }
         }
      }

      return var2;
   }

   @Nullable
   public MMOItem extractGemstone(@NotNull GemstoneData var1) {
      MMOItem var2 = MMOItems.plugin.getMMOItem(MMOItems.plugin.getTypes().get(var1.getMMOItemType()), var1.getMMOItemID());
      if (var2 == null) {
         return null;
      }

      if (MMOItemReforger.gemstonesRevIDWhenUnsocket) {
         return var2;
      }

      for (StatHistory var4 : this.mergeableStatHistory.values()) {
         StatData var5 = var4.getGemstoneData(var1.getHistoricUUID());
         if (var5 != null) {
            var2.setData(var4.getItemStat(), var5);
         }
      }

      return var2;
   }

   public void removeGemStone(@NotNull UUID var1, @Nullable String var2) {
      if (this.hasData(ItemStats.GEM_SOCKETS)) {
         StatHistory var3 = this.computeStatHistory(ItemStats.GEM_SOCKETS);
         if (!((GemSocketsData)var3.getOriginalData()).removeGem(var1, var2)) {
            for (UUID var5 : var3.getAllGemstones()) {
               if (((GemSocketsData)var3.getGemstoneData(var5)).removeGem(var1, var2)) {
                  return;
               }
            }

            for (StatData var8 : var3.getExternalData()) {
               if (((GemSocketsData)var8).removeGem(var1, var2)) {
                  return;
               }
            }

            for (UUID var9 : var3.getAllModifiers()) {
               if (((GemSocketsData)var3.getModifiersBonus(var9)).removeGem(var1, var2)) {
                  return;
               }
            }
         }
      }
   }
}
