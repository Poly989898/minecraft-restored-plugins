package net.Indyuce.inventory.listener.option;

import io.lumine.mythic.lib.api.item.NBTItem;
import java.util.ArrayList;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.inventory.CustomInventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathDrops implements Listener {
   @EventHandler(priority = EventPriority.LOWEST)
   public void dropItems(PlayerDeathEvent event) {
      if (!event.getKeepInventory()) {
         CustomInventoryHandler data = (CustomInventoryHandler)MMOInventory.plugin.getDataManager().getOrNull(event.getEntity());
         if (data != null) {
            boolean keepSoulbound = Bukkit.getPluginManager().getPlugin("MMOItems") != null && MMOItems.plugin.getLanguage().keepSoulboundOnDeath;

            for (CustomSlot slot : new ArrayList<>(data.getFilledSlots())) {
               ItemStack item = data.getItem(slot);
               NBTItem nbt = NBTItem.get(item);
               if (!nbt.getBoolean("MMOITEMS_DISABLE_DEATH_DROP") && (!keepSoulbound || !MMOUtils.isSoulboundTo(nbt, event.getEntity()))) {
                  data.setItem(slot, null);
                  event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), item);
               }
            }
         }
      }
   }
}
