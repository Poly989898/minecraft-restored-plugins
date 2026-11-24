package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.event.item.UnsocketGemStoneEvent;
import net.Indyuce.mmoitems.api.interaction.Consumable;
import net.Indyuce.mmoitems.api.item.mmoitem.LiveMMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.stat.data.DoubleData;
import net.Indyuce.mmoitems.stat.data.GemSocketsData;
import net.Indyuce.mmoitems.stat.data.GemstoneData;
import net.Indyuce.mmoitems.stat.type.ConsumableItemInteraction;
import net.Indyuce.mmoitems.stat.type.DoubleStat;
import net.Indyuce.mmoitems.util.MMOUtils;
import net.Indyuce.mmoitems.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RandomUnsocket extends DoubleStat implements ConsumableItemInteraction {
   public RandomUnsocket() {
      super(
         "RANDOM_UNSOCKET",
         Material.BOWL,
         "Random Unsocket",
         new String[]{"Number of gems (rounded down)", "that will pop out of an item when", "this is applied."},
         new String[]{"consumable"}
      );
   }

   @Override
   public boolean handleConsumableEffect(
      @NotNull InventoryClickEvent event, @NotNull PlayerData playerData, @NotNull Consumable consumable, @NotNull NBTItem nbtItem, Type type
   ) {
      VolatileMMOItem mMOItem = consumable.getMMOItem();
      if (!mMOItem.hasData(ItemStats.RANDOM_UNSOCKET)) {
         return false;
      }

      if (type == null) {
         return false;
      }

      VolatileMMOItem volatileMMOItem = new VolatileMMOItem(nbtItem);
      if (!volatileMMOItem.hasData(ItemStats.GEM_SOCKETS)) {
         return false;
      }

      GemSocketsData data = (GemSocketsData)volatileMMOItem.getData(ItemStats.GEM_SOCKETS);
      if (data != null && data.getGemstones().size() != 0) {
         Player player = playerData.getPlayer();
         LiveMMOItem liveMMOItem = new LiveMMOItem(nbtItem);
         List list = liveMMOItem.extractGemstones();
         if (list.isEmpty()) {
            Message.RANDOM_UNSOCKET_GEM_TOO_OLD.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem())).send(player);
            return false;
         }

         DoubleData chanceData = (DoubleData)consumable.getMMOItem().getData(ItemStats.RANDOM_UNSOCKET);
         int count = 1;
         if (chanceData != null) {
            count = SilentNumbers.floor(chanceData.getValue());
         }

         UnsocketGemStoneEvent unsocketEvent = new UnsocketGemStoneEvent(playerData, mMOItem, liveMMOItem);
         Bukkit.getServer().getPluginManager().callEvent(unsocketEvent);
         if (unsocketEvent.isCancelled()) {
            return false;
         }

         ArrayList<ItemStack> items = new ArrayList<>();

         while (count > 0 && !list.isEmpty()) {
            int index = SilentNumbers.floor(SilentNumbers.randomRange(0.0, list.size()));
            if (index >= list.size()) {
               index = list.size() - 1;
            }

            Pair pair = (Pair)list.get(index);
            MMOItem mmoItem = (MMOItem)pair.getValue();
            GemstoneData key = (GemstoneData)pair.getKey();
            list.remove(index);

            try {
               ItemStack item = mmoItem.newBuilder().build();
               if (!SilentNumbers.isAir(item)) {
                  items.add(item);
                  String text;
                  if (key.getSocketColor() != null) {
                     text = key.getSocketColor();
                  } else {
                     text = GemSocketsData.getUncoloredGemSlot();
                  }

                  liveMMOItem.removeGemStone(key.getHistoricUUID(), text);
                  count--;
                  Message.RANDOM_UNSOCKET_SUCCESS
                     .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(event.getCurrentItem()), "#gem#", MMOUtils.getDisplayName(item))
                     .send(player);
               }
            } catch (Throwable exception) {
               MMOItems.print(
                  Level.SEVERE,
                  "Could not unsocket gem from item $u{0}$b: $f{1}",
                  "Stat §eRandom Unsocket",
                  SilentNumbers.getItemName(event.getCurrentItem()),
                  exception.getMessage()
               );
            }
         }

         liveMMOItem.setData(ItemStats.GEM_SOCKETS, liveMMOItem.computeStatHistory(ItemStats.GEM_SOCKETS).recalculate(liveMMOItem.getUpgradeLevel()));
         event.setCurrentItem(liveMMOItem.newBuilder().build());

         for (ItemStack item : player.getInventory().addItem(items.toArray(new ItemStack[0])).values()) {
            player.getWorld().dropItem(player.getLocation(), item);
         }

         player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0F, 2.0F);
         return true;
      } else {
         return false;
      }
   }
}
