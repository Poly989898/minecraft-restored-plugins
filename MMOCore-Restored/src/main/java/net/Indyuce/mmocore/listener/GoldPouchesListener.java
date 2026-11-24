package net.Indyuce.mmocore.listener;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.version.VersionUtils;
import net.Indyuce.mmocore.gui.eco.GoldPouch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GoldPouchesListener implements Listener {
   @EventHandler
   public void a(PlayerInteractEvent event) {
      if (event.getAction().name().startsWith("RIGHT") && event.getHand() == EquipmentSlot.HAND) {
         NBTItem nbt = NBTItem.get(event.getItem());
         if (nbt.hasTag("RpgPouchInventory")) {
            event.setCancelled(true);
            if (event.getItem().getAmount() <= 1) {
               new GoldPouch(event.getPlayer(), nbt).open();
            }
         }
      }
   }

   @EventHandler
   public void b(EntityPickupItemEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (VersionUtils.getOpen(player).getTopInventory().getHolder() instanceof GoldPouch) {
            ItemStack item = event.getItem().getItemStack();
            if (NBTItem.get(item).hasTag("RpgPouchInventory")) {
               event.setCancelled(true);
            }
         }
      }
   }
}
