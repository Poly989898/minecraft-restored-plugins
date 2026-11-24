package me.TechsCode.UltraPermissions.base.gui.Furnace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Action;
import me.TechsCode.UltraPermissions.base.gui.ActionType;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.Entry;
import me.TechsCode.UltraPermissions.base.scheduler.RecurringTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public abstract class Furnace implements Listener {
   protected Player p;
   private final SpigotTechPlugin plugin;
   private boolean stopped;
   private RecurringTask recurringTask;
   private Inventory inventory;
   private HashMap<Integer, List<Action>> actions;

   public Furnace(Player player, SpigotTechPlugin spigotTechPlugin) {
      this.p = player;
      this.plugin = spigotTechPlugin;
      this.recurringTask = spigotTechPlugin.getScheduler().runTaskTimer(() -> {
         if (this.stopped) {
            this.recurringTask.stop();
            HandlerList.unregisterAll(this);
         } else {
            FurnaceModel furnaceModel = new FurnaceModel();
            furnaceModel.setTitle(this.getCurrentTitle());
            furnaceModel.setSlots(3);
            this.construct(furnaceModel);

            String text;
            try {
               InventoryView openInventory = player.getOpenInventory();
               Method method = openInventory.getClass().getMethod("getTitle");
               method.setAccessible(true);
               text = (String)method.invoke(openInventory);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
               throw new RuntimeException(exception);
            }

            if (this.inventory == null || !text.equals(furnaceModel.getTitle()) || this.inventory.getSize() != furnaceModel.getSlots()) {
               this.inventory = Bukkit.createInventory(null, InventoryType.FURNACE, furnaceModel.getTitle());
               player.openInventory(this.inventory);
            }

            HashMap<Integer, Entry> entries = furnaceModel.getEntries();
            IntStream.rangeClosed(1, this.inventory.getSize()).filter(count -> !entries.containsKey(count)).forEach(count -> this.inventory.clear(count - 1));
            this.actions = new HashMap<>();
            entries.forEach((value, entry) -> {
               Button button = new Button();
               entry.button(button);
               this.inventory.setItem(value, button.item().get());
               this.actions.put(value, button.getActions());
            });
            player.updateInventory();
         }
      }, 0L, 2L);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)spigotTechPlugin.getBootstrap());
   }

   public void preventOpening() {
      this.stopped = true;
   }

   protected abstract void construct(FurnaceModel furnaceModel);

   public void reopen() {
      if (this.inventory == null) {
         this.recurringTask.start();
         Bukkit.getPluginManager().registerEvents(this, (Plugin)this.plugin.getBootstrap());
      }
   }

   private void close() {
      this.inventory = null;
      this.recurringTask.stop();
      HandlerList.unregisterAll(this);
      this.onClose();
   }

   @EventHandler
   public void click(InventoryClickEvent event) {
      if (event.getWhoClicked().equals(this.p)) {
         if (event.getSlotType() == SlotType.OUTSIDE) {
            event.setCancelled(true);
         } else {
            event.setCancelled(true);
            if (this.actions != null) {
               if (!(event.getClickedInventory() instanceof PlayerInventory) && this.actions.containsKey(event.getSlot())) {
                  ActionType actionType = ActionType.fromClickType(event.getClick());
                  this.actions.get(event.getSlot()).forEach(action -> action.onClick(actionType));
               }
            }
         }
      }
   }

   @EventHandler
   public void drag(InventoryDragEvent event) {
      if (event.getWhoClicked().equals(this.p)) {
         if (!(event.getInventory() instanceof PlayerInventory)) {
            if (event.getInventorySlots().size() > 1) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void leave(PlayerQuitEvent event) {
      if (event.getPlayer().equals(this.p)) {
         this.close();
      }
   }

   @EventHandler
   public void close(InventoryCloseEvent event) {
      if (this.inventory != null && this.inventory.equals(event.getInventory())) {
         this.close();
      }
   }

   public String getCurrentTitle() {
      return "Unnamed Inventory";
   }

   public void onClose() {
   }
}
