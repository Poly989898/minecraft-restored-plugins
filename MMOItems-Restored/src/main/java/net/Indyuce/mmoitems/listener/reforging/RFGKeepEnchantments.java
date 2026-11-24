package net.Indyuce.mmoitems.listener.reforging;

import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.stat.Enchants;
import net.Indyuce.mmoitems.stat.data.EnchantListData;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.StatHistory;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class RFGKeepEnchantments implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepEnchantments()) {
         MMOItem mmoItem = event.getOldMMOItem().clone();
         if (!mmoItem.hasData(ItemStats.ENCHANTS)) {
            mmoItem.setData(ItemStats.ENCHANTS, new EnchantListData());
         }

         Enchants.separateEnchantments(mmoItem);
         StatHistory statHistory = mmoItem.computeStatHistory(ItemStats.ENCHANTS);
         EnchantListData enchantListData = ((EnchantListData)statHistory.getOriginalData()).clone();
         StatHistory newHistory = event.getNewMMOItem().computeStatHistory(ItemStats.ENCHANTS);

         for (StatData statData : statHistory.getExternalData()) {
            newHistory.registerExternalData(statData);
         }

         enchantListData.identifyTrueOriginalEnchantments(event.getNewMMOItem());
      }
   }

   void log(@NotNull EnchantListData enchantListData, @NotNull String text) {
      MMOItems.print(null, "  §3> §7" + text + ":", null);

      for (Enchantment enchant : enchantListData.getEnchants()) {
         MMOItems.print(null, "  §b * §7" + enchant.getName() + " §f" + enchantListData.getLevel(enchant), null);
      }
   }
}
