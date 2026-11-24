package net.Indyuce.mmoitems.comp;

import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMONonRPGHook implements Listener {
   @EventHandler(ignoreCancelled = true)
   public void handleNoMcMMORepair(McMMOPlayerRepairCheckEvent event) {
      NBTItem nbtItem = NBTItem.get(event.getRepairedObject());
      if (nbtItem.hasType() && nbtItem.getBoolean("MMOITEMS_DISABLE_MCMMO_REPAIR")) {
         event.setCancelled(true);
      }
   }
}
