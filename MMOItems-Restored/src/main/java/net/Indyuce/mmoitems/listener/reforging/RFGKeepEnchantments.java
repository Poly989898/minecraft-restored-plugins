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
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepEnchantments()) {
         MMOItem var2 = var1.getOldMMOItem().clone();
         if (!var2.hasData(ItemStats.ENCHANTS)) {
            var2.setData(ItemStats.ENCHANTS, new EnchantListData());
         }

         Enchants.separateEnchantments(var2);
         StatHistory var3 = var2.computeStatHistory(ItemStats.ENCHANTS);
         EnchantListData var4 = ((EnchantListData)var3.getOriginalData()).clone();
         StatHistory var5 = var1.getNewMMOItem().computeStatHistory(ItemStats.ENCHANTS);

         for (StatData var7 : var3.getExternalData()) {
            var5.registerExternalData(var7);
         }

         var4.identifyTrueOriginalEnchantments(var1.getNewMMOItem());
      }
   }

   void log(@NotNull EnchantListData var1, @NotNull String var2) {
      MMOItems.print(null, "  §3> §7" + var2 + ":", null);

      for (Enchantment var4 : var1.getEnchants()) {
         MMOItems.print(null, "  §b * §7" + var4.getName() + " §f" + var1.getLevel(var4), null);
      }
   }
}
