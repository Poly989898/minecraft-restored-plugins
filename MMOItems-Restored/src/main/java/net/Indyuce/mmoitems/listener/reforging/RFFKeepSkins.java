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
   public void onReforge(MMOItemReforgeFinishEvent event) {
      if (event.getOptions().shouldKeepSkins()) {
         String mMOITEMSSKINID = event.getReforger().getNBTItem().getString("MMOITEMS_SKIN_ID");
         if (mMOITEMSSKINID != null && !mMOITEMSSKINID.isEmpty()) {
            NBTItem nbtItem = NBTItem.get(event.getFinishedItem());
            ItemStack item = ItemSkin.applySkin(nbtItem, new VolatileMMOItem(event.getReforger().getNBTItem()));
            event.setFinishedItem(item);
         }
      }
   }
}
