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
   public void OnLootBeLooted(MobDropLootEvent var1) {
      this.handle(var1);
   }

   @EventHandler
   public void OnLootBeLooted(PlayerLootEvent var1) {
      this.handle(var1);
   }

   public void handle(LootEvent var1) {
      for (ItemStack var3 : var1.getItemList()) {
         if (!SilentNumbers.isAir(var3)) {
            MMOItemReforger var4 = new MMOItemReforger(var3);
            if (var4.reforge(MMOItems.plugin.getLanguage().phatLootsOptions) && var4.hasChanges()) {
               ItemStack var5 = var4.toStack();
               var5.setAmount(var3.getAmount());
               ItemMeta var6 = var5.getItemMeta();
               var3.setType(var5.getType());
               var3.setItemMeta(var6);
               var3.setData(var5.getData());
            }
         }
      }
   }
}
