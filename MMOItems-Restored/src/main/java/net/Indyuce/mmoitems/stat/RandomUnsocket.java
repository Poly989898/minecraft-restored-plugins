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
      @NotNull InventoryClickEvent var1, @NotNull PlayerData var2, @NotNull Consumable var3, @NotNull NBTItem var4, Type var5
   ) {
      VolatileMMOItem var6 = var3.getMMOItem();
      if (!var6.hasData(ItemStats.RANDOM_UNSOCKET)) {
         return false;
      }

      if (var5 == null) {
         return false;
      }

      VolatileMMOItem var7 = new VolatileMMOItem(var4);
      if (!var7.hasData(ItemStats.GEM_SOCKETS)) {
         return false;
      }

      GemSocketsData var8 = (GemSocketsData)var7.getData(ItemStats.GEM_SOCKETS);
      if (var8 != null && var8.getGemstones().size() != 0) {
         Player var9 = var2.getPlayer();
         LiveMMOItem var10 = new LiveMMOItem(var4);
         List var11 = var10.extractGemstones();
         if (var11.isEmpty()) {
            Message.RANDOM_UNSOCKET_GEM_TOO_OLD.format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getCurrentItem())).send(var9);
            return false;
         }

         DoubleData var12 = (DoubleData)var3.getMMOItem().getData(ItemStats.RANDOM_UNSOCKET);
         int var13 = 1;
         if (var12 != null) {
            var13 = SilentNumbers.floor(var12.getValue());
         }

         UnsocketGemStoneEvent var14 = new UnsocketGemStoneEvent(var2, var6, var10);
         Bukkit.getServer().getPluginManager().callEvent(var14);
         if (var14.isCancelled()) {
            return false;
         }

         ArrayList<ItemStack> var15 = new ArrayList<>();

         while (var13 > 0 && !var11.isEmpty()) {
            int var16 = SilentNumbers.floor(SilentNumbers.randomRange(0.0, var11.size()));
            if (var16 >= var11.size()) {
               var16 = var11.size() - 1;
            }

            Pair var17 = (Pair)var11.get(var16);
            MMOItem var18 = (MMOItem)var17.getValue();
            GemstoneData var19 = (GemstoneData)var17.getKey();
            var11.remove(var16);

            try {
               ItemStack var20 = var18.newBuilder().build();
               if (!SilentNumbers.isAir(var20)) {
                  var15.add(var20);
                  String var21;
                  if (var19.getSocketColor() != null) {
                     var21 = var19.getSocketColor();
                  } else {
                     var21 = GemSocketsData.getUncoloredGemSlot();
                  }

                  var10.removeGemStone(var19.getHistoricUUID(), var21);
                  var13--;
                  Message.RANDOM_UNSOCKET_SUCCESS
                     .format(ChatColor.YELLOW, "#item#", MMOUtils.getDisplayName(var1.getCurrentItem()), "#gem#", MMOUtils.getDisplayName(var20))
                     .send(var9);
               }
            } catch (Throwable var22) {
               MMOItems.print(
                  Level.SEVERE,
                  "Could not unsocket gem from item $u{0}$b: $f{1}",
                  "Stat §eRandom Unsocket",
                  SilentNumbers.getItemName(var1.getCurrentItem()),
                  var22.getMessage()
               );
            }
         }

         var10.setData(ItemStats.GEM_SOCKETS, var10.computeStatHistory(ItemStats.GEM_SOCKETS).recalculate(var10.getUpgradeLevel()));
         var1.setCurrentItem(var10.newBuilder().build());

         for (ItemStack var24 : var9.getInventory().addItem(var15.toArray(new ItemStack[0])).values()) {
            var9.getWorld().dropItem(var9.getLocation(), var24);
         }

         var9.playSound(var9.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1.0F, 2.0F);
         return true;
      } else {
         return false;
      }
   }
}
