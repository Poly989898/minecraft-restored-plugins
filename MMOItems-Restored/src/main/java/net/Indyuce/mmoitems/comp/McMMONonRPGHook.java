package net.Indyuce.mmoitems.comp;

import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMONonRPGHook implements Listener {
   @EventHandler(ignoreCancelled = true)
   public void handleNoMcMMORepair(McMMOPlayerRepairCheckEvent var1) {
      NBTItem var2 = NBTItem.get(var1.getRepairedObject());
      if (var2.hasType() && var2.getBoolean("MMOITEMS_DISABLE_MCMMO_REPAIR")) {
         var1.setCancelled(true);
      }
   }
}
