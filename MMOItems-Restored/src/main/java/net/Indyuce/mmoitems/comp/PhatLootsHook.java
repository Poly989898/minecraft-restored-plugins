package net.Indyuce.mmoitems.comp;

import com.codisimus.plugins.phatloots.events.LootEvent;
import com.codisimus.plugins.phatloots.events.MobDropLootEvent;
import com.codisimus.plugins.phatloots.events.PlayerLootEvent;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.util.MMOItemReforger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PhatLootsHook implements Listener {
   @EventHandler
   public void OnLootBeLooted(MobDropLootEvent event) {
      this.handle(event);
   }

   @EventHandler
   public void OnLootBeLooted(PlayerLootEvent event) {
      this.handle(event);
   }

   public void handle(LootEvent event) {
      for (ItemStack item : event.getItemList()) {
         if (!SilentNumbers.isAir(item)) {
            MMOItemReforger mmoItemReforger = new MMOItemReforger(item);
            if (mmoItemReforger.reforge(MMOItems.plugin.getLanguage().phatLootsOptions) && mmoItemReforger.hasChanges()) {
               ItemStack item2 = mmoItemReforger.toStack();
               item2.setAmount(item.getAmount());
               ItemMeta itemMeta = item2.getItemMeta();
               item.setType(item2.getType());
               item.setItemMeta(itemMeta);
               item.setData(item2.getData());
            }
         }
      }
   }
}
