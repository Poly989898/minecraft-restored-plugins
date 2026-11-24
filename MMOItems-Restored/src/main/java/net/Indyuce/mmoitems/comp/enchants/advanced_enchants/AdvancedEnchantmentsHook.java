package net.Indyuce.mmoitems.comp.enchants.advanced_enchants;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeEvent;
import net.Indyuce.mmoitems.comp.enchants.DisableAdvancedEnchantments;
import net.Indyuce.mmoitems.stat.data.type.StatData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.advancedplugins.ae.api.EnchantApplyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AdvancedEnchantmentsHook implements Listener {
   public static final ItemStat ADVANCED_ENCHANTMENTS = new AdvancedEnchantsStat();
   public static final ItemStat DISABLE_ADVANCED_ENCHANTMENTS = new DisableAdvancedEnchantments();

   @EventHandler
   public void onEnchantApply(EnchantApplyEvent var1) {
      NBTItem var2 = MythicLib.plugin.getVersion().getWrapper().getNBTItem(var1.getItem());
      if (var2.getBoolean("MMOITEMS_DISABLE_ADVANCED_ENCHANTS")) {
         var1.setCancelled(true);
      }
   }

   @EventHandler
   public void onReforge(MMOItemReforgeEvent var1) {
      if (var1.getOptions().shouldKeepAdvancedEnchants()) {
         StatData var2 = var1.getOldMMOItem().getData(ADVANCED_ENCHANTMENTS);
         if (var2 != null) {
            var1.getNewMMOItem().setData(ADVANCED_ENCHANTMENTS, var2);
         }
      }
   }
}
