package it.ytnoos.lpx;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import it.ytnoos.lpx.inventories.smartinvs.InventoryManager;
import it.ytnoos.lpx.inventories.smartinvs.SmartInventory;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class InventoryClickListener implements Listener {
   private static final long a = it.ytnoos.lpx.ClassKeyDerivation.a(-5070142650187420524L, 6326694588622944445L, java.lang.invoke.MethodHandles.lookup().lookupClass()).a(190049456072190L);


   private final InventoryManager this$0;

   public InventoryClickListener(InventoryManager inventoryManager) {
      this.this$0 = inventoryManager;
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
      long longValue = a ^ 100263830262230L;
      long longValue2 = longValue ^ 15670221471375L;
      Player player = (Player)inventoryClickEvent.getWhoClicked();
      if (this.this$0.inventories.containsKey(player)) {
         if (inventoryClickEvent.getAction() != InventoryAction.COLLECT_TO_CURSOR
            && inventoryClickEvent.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY
            && inventoryClickEvent.getAction() != InventoryAction.NOTHING) {
            if (inventoryClickEvent.getClickedInventory() == this.this$0.lpxBukkit.getInventoryViewAdapter(player).getTopInventory()) {
               inventoryClickEvent.setCancelled(true);
               int value = inventoryClickEvent.getSlot() / 9;
               int value2 = inventoryClickEvent.getSlot() % 9;
               if (value < 0 || value2 < 0) {
                  return;
               }

               SmartInventory smartInventory = this.this$0.inventories.get(player);
               if (value >= smartInventory.getRows() || value2 >= smartInventory.getColumns()) {
                  return;
               }

               smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == InventoryClickEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(inventoryClickEvent));
               this.this$0.contents.get(player).get(value, longValue2, value2).ifPresent(clickableItem -> clickableItem.run(inventoryClickEvent));
               player.updateInventory();
            }
         } else {
            inventoryClickEvent.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onInventoryDrag(InventoryDragEvent inventoryDragEvent) {
      Player player = (Player)inventoryDragEvent.getWhoClicked();
      if (this.this$0.inventories.containsKey(player)) {
         SmartInventory smartInventory = this.this$0.inventories.get(player);

         for (int value : inventoryDragEvent.getRawSlots()) {
            if (value < this.this$0.lpxBukkit.getInventoryViewAdapter(player).getTopInventory().getSize()) {
               inventoryDragEvent.setCancelled(true);
               break;
            }
         }

         smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == InventoryDragEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(inventoryDragEvent));
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
      Player player = (Player)inventoryOpenEvent.getPlayer();
      if (this.this$0.inventories.containsKey(player)) {
         SmartInventory smartInventory = this.this$0.inventories.get(player);
         smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == InventoryOpenEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(inventoryOpenEvent));
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
      Player player = (Player)inventoryCloseEvent.getPlayer();
      if (this.this$0.inventories.containsKey(player)) {
         SmartInventory smartInventory = this.this$0.inventories.get(player);
         smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == InventoryCloseEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(inventoryCloseEvent));
         if (smartInventory.isCloseable()) {
            inventoryCloseEvent.getInventory().clear();
            this.this$0.inventories.remove(player);
            this.this$0.contents.remove(player);
         } else {
            FoliaScheduler.getEntityScheduler().run(player, this.this$0.plugin, object -> player.openInventory(inventoryCloseEvent.getInventory()), null);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
      Player player = playerQuitEvent.getPlayer();
      if (this.this$0.inventories.containsKey(player)) {
         SmartInventory smartInventory = this.this$0.inventories.get(player);
         smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == PlayerQuitEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(playerQuitEvent));
         this.this$0.inventories.remove(player);
         this.this$0.contents.remove(player);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onPluginDisable(PluginDisableEvent pluginDisableEvent) {
      new HashMap<>(this.this$0.inventories).forEach((player, smartInventory) -> {
         smartInventory.getListeners().stream().filter(inventoryListener -> inventoryListener.getType() == PluginDisableEvent.class).forEach(inventoryListener2 -> inventoryListener2.accept(pluginDisableEvent));
         smartInventory.close(player);
      });
      this.this$0.inventories.clear();
      this.this$0.contents.clear();
   }
}
