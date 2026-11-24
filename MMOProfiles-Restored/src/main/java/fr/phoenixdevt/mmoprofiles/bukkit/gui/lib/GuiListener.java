package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class GuiListener implements Listener {
   @EventHandler
   public void onClick(InventoryClickEvent event) {
      if (event.getView().getTopInventory().getHolder() instanceof GeneratedInventory gui) {
         event.setCancelled(true);
         if (event.getClickedInventory() == event.getView().getTopInventory()) {
            gui.click(event);
         }
      }
   }

   @EventHandler
   public void onDrag(InventoryDragEvent event) {
      if (event.getView().getTopInventory().getHolder() instanceof GeneratedInventory) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent event) {
      if (event.getInventory().getHolder() instanceof GeneratedInventory gui) {
         gui.getNavigator().onClose(event.getInventory());
      }
   }
}
