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
   public GemStone(Player var1, NBTItem var2) {
      super(var1, var2);
   }

   public GemStone(PlayerData var1, NBTItem var2) {
      super(var1, var2);
   }

   @NotNull
   public GemStone.ApplyResult applyOntoItem(@NotNull NBTItem var1, @NotNull Type var2) {
      return this.applyOntoItem(new LiveMMOItem(var1), var2, MMOUtils.getDisplayName(var1.getItem()), true, false);
   }

   @NotNull
   public GemStone.ApplyResult applyOntoItem(@NotNull MMOItem var1, @NotNull Type var2, @NotNull String var3, boolean var4, boolean var5) {
      if (!var1.hasData(ItemStats.GEM_SOCKETS)) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      String var6 = this.getNBTItem().getString(ItemStats.GEM_COLOR.getNBTPath());
      GemSocketsData var7 = (GemSocketsData)var1.getData(ItemStats.GEM_SOCKETS);
      String var8 = var7.getEmptySocket(var6);
      if (var8 == null) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      String var9 = this.getNBTItem().getString(ItemStats.ITEM_TYPE_RESTRICTION.getNBTPath());
      if (!var9.isEmpty() && (!var2.isWeapon() || !var9.contains("WEAPON")) && !var9.contains(var2.getId())) {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }

      double var10 = this.getNBTItem().getStat(ItemStats.SUCCESS_RATE.getId());
      if (var10 == 0.0) {
         var10 = 100.0;
      }

      ApplyGemStoneEvent var12 = new ApplyGemStoneEvent(
         this.playerData, this.mmoitem, var1, RANDOM.nextDouble() > var10 / 100.0 ? GemStone.ResultType.FAILURE : GemStone.ResultType.SUCCESS
      );
      Bukkit.getPluginManager().callEvent(var12);
      if (!var12.isCancelled() && var12.getResult() != GemStone.ResultType.NONE) {
         if (var12.getResult() == GemStone.ResultType.FAILURE) {
            if (!var5) {
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
               Message.GEM_STONE_BROKE.format(ChatColor.RED, "#gem#", MMOUtils.getDisplayName(this.getItem()), "#item#", var3).send(this.player);
            }

            return new GemStone.ApplyResult(GemStone.ResultType.FAILURE);
         } else {
            Enchants.separateEnchantments(var1);
            LiveMMOItem var13 = new LiveMMOItem(this.getNBTItem());
            GemstoneData var14 = new GemstoneData(var13, var8);
            StatHistory var15 = var1.computeStatHistory(ItemStats.GEM_SOCKETS);
            this.findEmptySocket(var15, var6, var14);
            var1.setData(ItemStats.GEM_SOCKETS, var15.recalculate(var1.getUpgradeLevel()));
            Integer var16 = null;
            String var17 = var13.hasData(ItemStats.GEM_UPGRADE_SCALING)
               ? var13.getData(ItemStats.GEM_UPGRADE_SCALING).toString()
               : GemUpgradeScaling.defaultValue;
            switch (var17) {
               case "HISTORIC":
                  var16 = 0;
                  break;
               case "SUBSEQUENT":
                  var16 = var1.getUpgradeLevel();
            }

            var14.setLevel(var16);

            for (ItemStat var22 : var13.getStats()) {
               if (!(var22 instanceof GemStoneStat)) {
                  StatData var20 = var13.getData(var22);
                  if (var20 instanceof Mergeable) {
                     var1.mergeData(var22, var20, var14.getHistoricUUID());
                  }
               }
            }

            if (!var5) {
               this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 2.0F);
               Message.GEM_STONE_APPLIED.format(ChatColor.YELLOW, "#gem#", MMOUtils.getDisplayName(this.getItem()), "#item#", var3).send(this.player);
            }

            return var4 ? new GemStone.ApplyResult(var1.newBuilder().build()) : new GemStone.ApplyResult(var1, GemStone.ResultType.SUCCESS);
         }
      } else {
         return new GemStone.ApplyResult(GemStone.ResultType.NONE);
      }
   }

   private void findEmptySocket(StatHistory var1, String var2, GemstoneData var3) {
      GemSocketsData var4 = (GemSocketsData)var1.getOriginalData();
      if (!var4.apply(var2, var3)) {
         for (UUID var6 : var1.getAllModifiers()) {
            var4 = (GemSocketsData)var1.getModifiersBonus(var6);
            if (var4.apply(var2, var3)) {
               return;
            }
         }

         for (StatData var12 : var1.getExternalData()) {
            var4 = (GemSocketsData)var12;
            if (var4.apply(var2, var3)) {
               return;
            }
         }

         for (UUID var13 : var1.getAllGemstones()) {
            var4 = (GemSocketsData)var1.getGemstoneData(var13);
            if (var4.apply(var2, var3)) {
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

      public ApplyResult(@NotNull GemStone.ResultType var1) {
         this((ItemStack)null, var1);
      }

      public ApplyResult(@Nullable ItemStack var1) {
         this(var1, GemStone.ResultType.SUCCESS);
      }

      public ApplyResult(@Nullable ItemStack var1, @NotNull GemStone.ResultType var2) {
         this.type = var2;
         this.result = var1;
         this.resultAsMMOItem = null;
      }

      public ApplyResult(@Nullable MMOItem var1, @NotNull GemStone.ResultType var2) {
         this.type = var2;
         this.result = null;
         this.resultAsMMOItem = var1;
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
