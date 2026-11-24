package net.Indyuce.mmoitems.api.util;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.interaction.util.DurabilityItem;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.UpgradeData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DeathDowngrading {
   private static final Random RANDOM = new Random();

   public static void playerDeathDowngrade(@NotNull Player var0) {
      PlayerData var1 = PlayerData.get(var0);
      double var2 = var1.getStats().getStat(ItemStats.DOWNGRADE_ON_DEATH_CHANCE);
      if (!(var2 <= 0.0)) {
         var1.updateInventory();
         List var4 = var1.getInventory().getEquipped();
         Iterator var5 = var4.iterator();

         while (var5.hasNext()) {
            EquippedItem var6 = (EquippedItem)var5.next();
            if (var6 == null || !canDeathDowngrade(var6.getCached())) {
               var5.remove();
            }
         }

         if (var4.size() != 0) {
            while (var2 >= 100.0 && var4.size() > 0) {
               var2 -= 100.0;
               int var7 = RANDOM.nextInt(var4.size());
               EquippedItem var9 = (EquippedItem)var4.get(var7);
               var9.setItem(downgrade(new LiveMMOItem(var9.getNBT()), var0));
               var4.remove(var7);
            }

            if (var2 > 0.0 && var4.size() > 0 && RANDOM.nextInt(100) < var2) {
               int var8 = RANDOM.nextInt(var4.size());
               EquippedItem var10 = (EquippedItem)var4.get(var8);
               var10.setItem(downgrade(new LiveMMOItem(var10.getNBT()), var0));
               var4.remove(var8);
            }
         }
      }
   }

   @NotNull
   public static ArrayList<ItemStack> downgradeItems(@NotNull List<ItemStack> var0, @NotNull Player var1, double var2) {
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();

      for (ItemStack var7 : var0) {
         if (!SilentNumbers.isAir(var7)) {
            if (canDeathDowngrade(var7)) {
               var5.add(var7);
            } else {
               var4.add(var7);
            }
         }
      }

      while (var2 >= 100.0 && var5.size() > 0) {
         var2 -= 100.0;
         int var8 = RANDOM.nextInt(var5.size());
         ItemStack var10 = (ItemStack)var5.get(var8);
         var4.add(downgrade(new LiveMMOItem(var10), var1));
         var5.remove(var8);
      }

      if (var2 > 0.0 && var5.size() > 0 && RANDOM.nextInt(100) < var2) {
         int var9 = RANDOM.nextInt(var5.size());
         ItemStack var11 = (ItemStack)var5.get(var9);
         var4.add(downgrade(new LiveMMOItem(var11), var1));
         var5.remove(var9);
      }

      var4.addAll(var5);
      return var4;
   }

   @NotNull
   public static ItemStack downgrade(@NotNull ItemStack var0, @NotNull Player var1) {
      if (!SilentNumbers.isAir(var0) && var0.getType().isItem()) {
         NBTItem var2 = NBTItem.get(var0);
         return !var2.hasType() ? var0 : downgrade(new LiveMMOItem(var2), var1);
      } else {
         return var0;
      }
   }

   @NotNull
   public static ItemStack downgrade(@NotNull LiveMMOItem var0, @NotNull Player var1) {
      var0.getUpgradeTemplate().upgradeTo(var0, var0.getUpgradeLevel() - 1);
      ItemStack var2 = var0.newBuilder().build();
      DurabilityItem var3 = new DurabilityItem(var1, var0.newBuilder().buildNBT());
      if (var3.getDurability() != var3.getMaxDurability()) {
         var3.addDurability(var3.getMaxDurability());
         var2.setItemMeta(var3.toItem().getItemMeta());
      }

      Message.DEATH_DOWNGRADING.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(var0.getNBT().getItem())).send(var1);
      return var2;
   }

   public static double getDeathDowngradeChance(@NotNull Player var0) {
      PlayerData var1 = PlayerData.get(var0);
      return var1.getStats().getStat(ItemStats.DOWNGRADE_ON_DEATH_CHANCE);
   }

   @Contract("null->false")
   public static boolean canDeathDowngrade(@Nullable ItemStack var0) {
      if (!SilentNumbers.isAir(var0) && var0.getType().isItem()) {
         NBTItem var1 = NBTItem.get(var0);
         return !var1.hasType() ? false : canDeathDowngrade(new VolatileMMOItem(var1));
      } else {
         return false;
      }
   }

   @Contract("null->false")
   public static boolean canDeathDowngrade(@Nullable MMOItem var0) {
      if (var0 == null) {
         return false;
      }

      if (!var0.hasData(ItemStats.DOWNGRADE_ON_DEATH)) {
         return false;
      }

      if (!var0.hasData(ItemStats.UPGRADE)) {
         return false;
      }

      if (!var0.hasUpgradeTemplate()) {
         return false;
      }

      UpgradeData var1 = (UpgradeData)var0.getData(ItemStats.UPGRADE);
      return var1.getLevel() > var1.getMin();
   }
}
