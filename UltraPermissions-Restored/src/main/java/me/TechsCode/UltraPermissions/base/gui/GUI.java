package me.TechsCode.UltraPermissions.base.gui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.scheduler.RecurringTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public abstract class GUI implements Listener {
   protected Player p;
   private final SpigotTechPlugin plugin;
   private boolean stopped;
   private RecurringTask recurringTask;
   private Inventory inventory;
   private HashMap<Integer, List<Action>> actions;

   public GUI(Player player, SpigotTechPlugin spigotTechPlugin) {
      this.p = player;
      this.plugin = spigotTechPlugin;
      int guiUpdateInterval = spigotTechPlugin.getAppearanceRegistry().getGuiUpdateInterval();
      this.recurringTask = spigotTechPlugin.getScheduler().runTaskTimer(() -> {
         if (this.stopped) {
            this.recurringTask.stop();
            HandlerList.unregisterAll(this);
         } else {
            Model model = new Model();
            model.setTitle(this.getCurrentTitle());
            model.setSlots(this.getCurrentSlots());
            this.construct(model);
            if (this.inventory == null) {
               this.inventory = Bukkit.createInventory(null, model.getSlots(), model.getTitle());
               player.openInventory(this.inventory);
            }

            String text;
            try {
               InventoryView openInventory = player.getOpenInventory();
               Method method = openInventory.getClass().getMethod("getTitle");
               method.setAccessible(true);
               text = (String)method.invoke(openInventory);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException exception) {
               throw new RuntimeException(exception);
            }

            if (!text.equals(model.getTitle())) {
               this.inventory = Bukkit.createInventory(null, model.getSlots(), model.getTitle());
               player.openInventory(this.inventory);
            }

            if (this.inventory.getSize() != model.getSlots()) {
               this.inventory = Bukkit.createInventory(null, model.getSlots(), model.getTitle());
               player.openInventory(this.inventory);
            }

            HashMap<Integer, Entry> entries = model.getEntries();
            IntStream.rangeClosed(1, this.inventory.getSize()).filter(count -> !entries.containsKey(count)).forEach(count -> this.inventory.clear(count - 1));
            this.actions = new HashMap<>();
            entries.forEach((value, entry) -> {
               Button button = new Button();
               entry.button(button);
               ItemStack item = button.item().get();
               ItemMeta itemMeta = item.getItemMeta();
               if (itemMeta != null) {
                  try {
                     if (itemMeta.hasItemFlag(ItemFlag.valueOf("HIDE_LORE"))) {
                        itemMeta.removeItemFlags(new ItemFlag[]{ItemFlag.valueOf("HIDE_LORE")});
                        item.setItemMeta(itemMeta);
                     }
                  } catch (NoSuchFieldError | IllegalArgumentException exception) {
                  }
               }

               this.inventory.setItem(value - 1, item);
               this.actions.put(value - 1, button.getActions());
            });
            Bukkit.getScheduler().runTask((Plugin)spigotTechPlugin.getBootstrap(), player::updateInventory);
         }
      }, 0L, guiUpdateInterval);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)spigotTechPlugin.getBootstrap());
   }

   public void preventOpening() {
      this.stopped = true;
   }

   protected abstract void construct(Model model);

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

   public int getCurrentSlots() {
      return 27;
   }

   public String getCurrentTitle() {
      return "Unnamed Inventory";
   }

   public void onClose() {
   }
}
