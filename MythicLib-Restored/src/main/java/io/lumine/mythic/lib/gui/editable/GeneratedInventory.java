package io.lumine.mythic.lib.gui.editable;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.PluginInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.util.Tasks;
import io.lumine.mythic.lib.util.lang3.Validate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/** An {@link EditableInventory} opened by one player. */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class GeneratedInventory extends PluginInventory {
   private final EditableInventory editable;
   protected final String guiName;
   private final List<InventoryItem<?>> loaded = new ArrayList<>();
   protected Inventory lastOpened;
   private int perPage = -1;

   public GeneratedInventory(Navigator navigator, EditableInventory editable) {
      super(navigator);
      this.editable = editable;
      this.guiName = editable.getName();
      Validate.notNull(this.guiName, "guiName is null");
   }

   public List<InventoryItem<?>> getLoaded() {
      return this.loaded;
   }

   public EditableInventory getEditable() {
      return this.editable;
   }

   /** Enables pages; each page displays the given number of entries of the paginated items. */
   protected void enablePagination(int perPage) {
      this.perPage = perPage;
   }

   public boolean hasPagination() {
      return this.perPage >= 0;
   }

   /** Number of pages, to be overridden by GUIs that use pagination. */
   public int getMaxPage() {
      return 1;
   }

   /** Index of the first entry displayed on the current page, plus {@code n}. */
   public int getPageIndex(int n) {
      return (this.page - 1) * this.perPage + n;
   }

   public int computeMaxPage(int totalEntries) {
      return Math.ceilDiv(Math.max(1, totalEntries), this.perPage);
   }

   public InventoryItem<?> getByFunction(String function) {
      for (InventoryItem<?> item : this.loaded) {
         if (item.getFunction().equals(function)) {
            return item;
         }
      }
      return null;
   }

   public InventoryItem<?> getBySlot(int slot) {
      for (InventoryItem<?> item : this.loaded) {
         if (item.getSlots().contains(slot)) {
            return item;
         }
      }
      return null;
   }

   public void addLoaded(InventoryItem<?> item) {
      this.loaded.add(0, item);
   }

   @Override
   public Inventory getInventory() {
      this.loaded.clear();
      this.lastOpened = Bukkit.createInventory(this, this.editable.getVanillaSlots(), this.bakeName());
      for (InventoryItem item : this.editable.getItems()) {
         if (item.isDisplayed(this)) {
            this.addLoaded(item);
            this.displayItem(this.lastOpened, item);
         }
      }

      return this.lastOpened;
   }

   public void displayItem(Inventory inventory, InventoryItem item) {
      if (!item.hasDifferentDisplay()) {
         ItemStack display = item.getDisplayedItem(this, 0);
         if (display != null) {
            for (Object slot : item.getSlots()) {
               inventory.setItem((Integer)slot, display);
            }
         }
      } else {
         List<Integer> slots = item.getSlots();
         for (int n = 0; n < slots.size(); n++) {
            ItemStack display = item.getDisplayedItem(this, n);
            if (display != null) {
               inventory.setItem(slots.get(n), display);
            }
         }
      }
   }

   @Override
   public void onClick(InventoryClickEvent event) {
      event.setCancelled(true);
      if (event.getInventory().equals(event.getClickedInventory()) && event.getCurrentItem() != null) {
         InventoryItem item = this.getBySlot(event.getSlot());
         if (item != null) {
            item.onClick(this, event);
         }
      }
   }

   @Override
   public void onClose() {
      this.lastOpened = null;
   }

   public String getRawName() {
      return this.guiName;
   }

   /** The title shown to the player. */
   public String bakeName() {
      return MythicLib.plugin.getPlaceholderParser().parse(this.player, this.applyNamePlaceholders(this.getRawName()));
   }

   public String applyNamePlaceholders(String name) {
      return name;
   }

   /** Replaces the n-th slot of an item on the main thread, after the consumer edited the given item. */
   public void liveUpdate(InventoryItem<?> item, int n, ItemStack display, Consumer<ItemStack> update) {
      Tasks.runSync(MythicLib.plugin, () -> {
         if (this.lastOpened == null) {
            return;
         }

         update.accept(display);
         this.lastOpened.setItem(item.getSlots().get(n), display);
      });
   }

   public void liveUpdate(InventoryItem item, int n) {
      this.liveUpdate(item, n, item.getDisplayedItem(this, n), UtilityMethods.dummyConsume());
   }

   /**
    * Edits a displayed item asynchronously (e.g. to load a skull texture) and puts it back in the GUI.
    * The inventory is only touched from the main thread, and only if it is still open.
    */
   public void asyncUpdate(InventoryItem<?> item, int n, ItemStack display, Consumer<ItemStack> update) {
      Bukkit.getScheduler().runTaskAsynchronously(MythicLib.plugin, () -> {
         if (this.lastOpened == null) {
            return;
         }

         update.accept(display);
         this.setSlotSync(item, n, display);
      });
   }

   public void asyncUpdate(InventoryItem item, int n) {
      Bukkit.getScheduler().runTaskAsynchronously(MythicLib.plugin, () -> {
         if (this.lastOpened == null) {
            return;
         }

         this.setSlotSync(item, n, item.getDisplayedItem(this, n));
      });
   }

   public <T> void asyncUpdate(CompletableFuture<T> future, InventoryItem<?> item, int n, ItemStack display, BiConsumer<T, ItemStack> update) {
      future.thenAccept(value -> {
         if (this.lastOpened == null) {
            return;
         }

         update.accept(value, display);
         this.setSlotSync(item, n, display);
      });
   }

   private void setSlotSync(InventoryItem<?> item, int n, ItemStack display) {
      Tasks.runSync(MythicLib.plugin, () -> {
         Inventory inventory = this.lastOpened;
         if (inventory != null && n < item.getSlots().size()) {
            inventory.setItem(item.getSlots().get(n), display);
         }
      });
   }
}
