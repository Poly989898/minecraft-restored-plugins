package net.Indyuce.mmoitems.api.interaction;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.UUID;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.GemUpgradeScaling;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.data.type.Mergeable;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GemStone extends UseItem {
   @Deprecated
   public GemStone(Player player, NBTItem nbtItem) {
      super(player, nbtItem);
   }

   public GemStone(PlayerData playerData, NBTItem nbtItem) {
      super(playerData, nbtItem);
   }

   @NotNull
   public GemStone.ApplyResult applyOntoItem(@NotNull NBTItem nbtItem, @NotNull Type type) {
      return this.applyOntoItem(new LiveMMOItem(nbtItem), type, MMOUtils.getDisplayName(nbtItem.getItem()), true, false);
   }

   @NotNull
   public GemStone.ApplyResult applyOntoItem(@NotNull MMOItem mmoItem, @NotNull Type type, @NotNull String text, boolean flag, boolean dryRun) {
      if (!mmoItem.hasData(ItemStats.GEM_SOCKETS)) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      String gemColor = this.getNBTItem().getString(ItemStats.GEM_COLOR.getNBTPath());
      GemSocketsData data = (GemSocketsData)mmoItem.getData(ItemStats.GEM_SOCKETS);
      String emptySocket = data.getEmptySocket(gemColor);
      if (emptySocket == null) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      String typeRestriction = this.getNBTItem().getString(ItemStats.ITEM_TYPE_RESTRICTION.getNBTPath());
      if (!typeRestriction.isEmpty() && (!type.isWeapon() || !typeRestriction.contains("WEAPON")) && !typeRestriction.contains(type.getId())) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      double stat = this.getNBTItem().getStat(ItemStats.SUCCESS_RATE.getId());
      if (stat == 0.0) {
         stat = 100.0;
      }

      ApplyGemStoneEvent event = new ApplyGemStoneEvent(
         this.playerData, this.mmoitem, mmoItem, RANDOM.nextDouble() > stat / 100.0 ? GemStone.ResultType.FAILURE : GemStone.ResultType.SUCCESS
      );
      Bukkit.getPluginManager().callEvent(event);
      if (!event.isCancelled() && event.getResult() != GemStone.ResultType.NONE) {
         if (event.getResult() == GemStone.ResultType.FAILURE) {
            if (!dryRun) {
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
               Message.GEM_STONE_BROKE.format(ChatColor.RED, "#gem#", MMOUtils.getDisplayName(this.getItem()), "#item#", text).send(this.player);
            }

            return new GemStone.ApplyResult(GemStone.ResultType.FAILURE);
         } else {
            Enchants.separateEnchantments(mmoItem);
            LiveMMOItem liveMMOItem = new LiveMMOItem(this.getNBTItem());
            GemstoneData gemstoneData = new GemstoneData(liveMMOItem, emptySocket);
            StatHistory statHistory = mmoItem.computeStatHistory(ItemStats.GEM_SOCKETS);
            this.findEmptySocket(statHistory, gemColor, gemstoneData);
            mmoItem.setData(ItemStats.GEM_SOCKETS, statHistory.recalculate(mmoItem.getUpgradeLevel()));
            Integer value = null;
            String upgradeScaling = liveMMOItem.hasData(ItemStats.GEM_UPGRADE_SCALING)
               ? liveMMOItem.getData(ItemStats.GEM_UPGRADE_SCALING).toString()
               : GemUpgradeScaling.defaultValue;
            switch (upgradeScaling) {
               case "HISTORIC":
                  value = 0;
                  break;
               case "SUBSEQUENT":
                  value = mmoItem.getUpgradeLevel();
            }

            gemstoneData.setLevel(value);

            for (ItemStat gemStat : liveMMOItem.getStats()) {
               if (!(gemStat instanceof GemStoneStat)) {
                  StatData gemStatData = liveMMOItem.getData(gemStat);
                  if (gemStatData instanceof Mergeable) {
                     mmoItem.mergeData(gemStat, gemStatData, gemstoneData.getHistoricUUID());
                  }
               }
            }

            if (!dryRun) {
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
               Message.GEM_STONE_APPLIED.format(ChatColor.YELLOW, "#gem#", MMOUtils.getDisplayName(this.getItem()), "#item#", text).send(this.player);
            }

            return flag ? new GemStone.ApplyResult(mmoItem.newBuilder().build()) : new GemStone.ApplyResult(mmoItem, GemStone.ResultType.SUCCESS);
         }
      } else {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }
   }

   private void findEmptySocket(StatHistory statHistory, String text, GemstoneData gemstoneData) {
      GemSocketsData gemSocketsData = (GemSocketsData)statHistory.getOriginalData();
      if (!gemSocketsData.apply(text, gemstoneData)) {
         for (UUID allModifier : statHistory.getAllModifiers()) {
            gemSocketsData = (GemSocketsData)statHistory.getModifiersBonus(allModifier);
            if (gemSocketsData.apply(text, gemstoneData)) {
               return;
            }
         }

         for (StatData statData : statHistory.getExternalData()) {
            gemSocketsData = (GemSocketsData)statData;
            if (gemSocketsData.apply(text, gemstoneData)) {
               return;
            }
         }

         for (UUID allGemstone : statHistory.getAllGemstones()) {
            gemSocketsData = (GemSocketsData)statHistory.getGemstoneData(allGemstone);
            if (gemSocketsData.apply(text, gemstoneData)) {
               return;
            }
         }

         throw new RuntimeException("MMOItem contains available socket but not its socket stat history");
      }
   }

   public static class ApplyResult {
      @NotNull
      private final GemStone.ResultType type;
      @Nullable
      private final ItemStack result;
      @Nullable
      private final MMOItem resultAsMMOItem;

      public ApplyResult(@NotNull GemStone.ResultType resultType) {
         this((ItemStack)null, resultType);
      }

      public ApplyResult(@Nullable ItemStack item) {
         this(item, GemStone.ResultType.SUCCESS);
      }

      public ApplyResult(@Nullable ItemStack item, @NotNull GemStone.ResultType resultType) {
         this.type = resultType;
         this.result = item;
         this.resultAsMMOItem = null;
      }

      public ApplyResult(@Nullable MMOItem mmoItem, @NotNull GemStone.ResultType resultType) {
         this.type = resultType;
         this.result = null;
         this.resultAsMMOItem = mmoItem;
      }

      @NotNull
      public GemStone.ResultType getType() {
         return this.type;
      }

      @Nullable
      public ItemStack getResult() {
         return this.result;
      }

      @Nullable
      public MMOItem getResultAsMMOItem() {
         return this.resultAsMMOItem;
      }
   }

   public enum ResultType {
      FAILURE,
      NONE,
      SUCCESS;
   }
}
