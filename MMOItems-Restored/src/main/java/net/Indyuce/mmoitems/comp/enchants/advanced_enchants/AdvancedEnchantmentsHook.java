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
   public void onEnchantApply(EnchantApplyEvent event) {
      NBTItem nBTItem = MythicLib.plugin.getVersion().getWrapper().getNBTItem(event.getItem());
      if (nBTItem.getBoolean("MMOITEMS_DISABLE_ADVANCED_ENCHANTS")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onReforge(MMOItemReforgeEvent event) {
      if (event.getOptions().shouldKeepAdvancedEnchants()) {
         StatData data = event.getOldMMOItem().getData(ADVANCED_ENCHANTMENTS);
         if (data != null) {
            event.getNewMMOItem().setData(ADVANCED_ENCHANTMENTS, data);
         }
      }
   }
}
