package net.Indyuce.mmoitems.listener.reforging;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.api.event.MMOItemReforgeFinishEvent;
import net.Indyuce.mmoitems.api.interaction.ItemSkin;
import net.Indyuce.mmoitems.api.item.mmoitem.VolatileMMOItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class RFFKeepSkins implements Listener {
   @EventHandler
   public void onReforge(MMOItemReforgeFinishEvent var1) {
      if (var1.getOptions().shouldKeepSkins()) {
         String var2 = var1.getReforger().getNBTItem().getString("MMOITEMS_SKIN_ID");
         if (var2 != null && !var2.isEmpty()) {
            NBTItem var3 = NBTItem.get(var1.getFinishedItem());
            ItemStack var4 = ItemSkin.applySkin(var3, new VolatileMMOItem(var1.getReforger().getNBTItem()));
            var1.setFinishedItem(var4);
         }
      }
   }
}
