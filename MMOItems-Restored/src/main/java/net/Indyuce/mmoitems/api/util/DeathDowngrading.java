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

   public static void playerDeathDowngrade(@NotNull Player player) {
      PlayerData playerData = PlayerData.get(player);
      double stat = playerData.getStats().getStat(ItemStats.DOWNGRADE_ON_DEATH_CHANCE);
      if (!(stat <= 0.0)) {
         playerData.updateInventory();
         List equipped = playerData.getInventory().getEquipped();
         Iterator iterator = equipped.iterator();

         while (iterator.hasNext()) {
            EquippedItem equippedItem = (EquippedItem)iterator.next();
            if (equippedItem == null || !canDeathDowngrade(equippedItem.getCached())) {
               iterator.remove();
            }
         }

         if (equipped.size() != 0) {
            while (stat >= 100.0 && equipped.size() > 0) {
               stat -= 100.0;
               int index = RANDOM.nextInt(equipped.size());
               EquippedItem equippedItem = (EquippedItem)equipped.get(index);
               equippedItem.setItem(downgrade(new LiveMMOItem(equippedItem.getNBT()), player));
               equipped.remove(index);
            }

            if (stat > 0.0 && equipped.size() > 0 && RANDOM.nextInt(100) < stat) {
               int index = RANDOM.nextInt(equipped.size());
               EquippedItem equippedItem = (EquippedItem)equipped.get(index);
               equippedItem.setItem(downgrade(new LiveMMOItem(equippedItem.getNBT()), player));
               equipped.remove(index);
            }
         }
      }
   }

   @NotNull
   public static ArrayList<ItemStack> downgradeItems(@NotNull List<ItemStack> items, @NotNull Player player, double value) {
      ArrayList list = new ArrayList();
      ArrayList list2 = new ArrayList();

      for (ItemStack item : items) {
         if (!SilentNumbers.isAir(item)) {
            if (canDeathDowngrade(item)) {
               list2.add(item);
            } else {
               list.add(item);
            }
         }
      }

      while (value >= 100.0 && list2.size() > 0) {
         value -= 100.0;
         int index = RANDOM.nextInt(list2.size());
         ItemStack item = (ItemStack)list2.get(index);
         list.add(downgrade(new LiveMMOItem(item), player));
         list2.remove(index);
      }

      if (value > 0.0 && list2.size() > 0 && RANDOM.nextInt(100) < value) {
         int index = RANDOM.nextInt(list2.size());
         ItemStack item = (ItemStack)list2.get(index);
         list.add(downgrade(new LiveMMOItem(item), player));
         list2.remove(index);
      }

      list.addAll(list2);
      return list;
   }

   @NotNull
   public static ItemStack downgrade(@NotNull ItemStack item, @NotNull Player player) {
      if (!SilentNumbers.isAir(item) && item.getType().isItem()) {
         NBTItem nbtItem = NBTItem.get(item);
         return !nbtItem.hasType() ? item : downgrade(new LiveMMOItem(nbtItem), player);
      } else {
         return item;
      }
   }

   @NotNull
   public static ItemStack downgrade(@NotNull LiveMMOItem liveMMOItem, @NotNull Player player) {
      liveMMOItem.getUpgradeTemplate().upgradeTo(liveMMOItem, liveMMOItem.getUpgradeLevel() - 1);
      ItemStack item = liveMMOItem.newBuilder().build();
      DurabilityItem durabilityItem = new DurabilityItem(player, liveMMOItem.newBuilder().buildNBT());
      if (durabilityItem.getDurability() != durabilityItem.getMaxDurability()) {
         durabilityItem.addDurability(durabilityItem.getMaxDurability());
         item.setItemMeta(durabilityItem.toItem().getItemMeta());
      }

      Message.DEATH_DOWNGRADING.format(ChatColor.RED, "#item#", MMOUtils.getDisplayName(liveMMOItem.getNBT().getItem())).send(player);
      return item;
   }

   public static double getDeathDowngradeChance(@NotNull Player player) {
      PlayerData playerData = PlayerData.get(player);
      return playerData.getStats().getStat(ItemStats.DOWNGRADE_ON_DEATH_CHANCE);
   }

   @Contract("null->false")
   public static boolean canDeathDowngrade(@Nullable ItemStack item) {
      if (!SilentNumbers.isAir(item) && item.getType().isItem()) {
         NBTItem nbtItem = NBTItem.get(item);
         return !nbtItem.hasType() ? false : canDeathDowngrade(new VolatileMMOItem(nbtItem));
      } else {
         return false;
      }
   }

   @Contract("null->false")
   public static boolean canDeathDowngrade(@Nullable MMOItem mmoItem) {
      if (mmoItem == null) {
         return false;
      }

      if (!mmoItem.hasData(ItemStats.DOWNGRADE_ON_DEATH)) {
         return false;
      }

      if (!mmoItem.hasData(ItemStats.UPGRADE)) {
         return false;
      }

      if (!mmoItem.hasUpgradeTemplate()) {
         return false;
      }

      UpgradeData data = (UpgradeData)mmoItem.getData(ItemStats.UPGRADE);
      return data.getLevel() > data.getMin();
   }
}
