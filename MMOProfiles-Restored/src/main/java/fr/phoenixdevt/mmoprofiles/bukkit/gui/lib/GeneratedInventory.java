package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/** An EditableInventory instance opened for one player. */
public abstract class GeneratedInventory implements InventoryHolder {
   private final Navigator navigator;
   private final EditableInventory editable;
   private Inventory inventory;
   private final Map<Integer, SlotEntry> slotMap = new HashMap<>();
   protected Player player;
   public int page = 1;
   public String guiName;

   public GeneratedInventory(Navigator navigator, EditableInventory editable) {
      this.navigator = navigator;
      this.editable = editable;
      this.player = navigator.getPlayerData().getPlayer();
      this.guiName = editable.getName();
   }

   public Navigator getNavigator() {
      return this.navigator;
   }

   public EditableInventory getEditable() {
      return this.editable;
   }

   public Player getPlayer() {
      return this.player;
   }

   /** Ticks to wait before the GUI is shown again after the player closed it. */
   public long getCloseTimeOut() {
      return 1L;
   }

   public int getMaxPage() {
      return 1;
   }

   public String getRawName() {
      return this.guiName;
   }

   @Override
   public Inventory getInventory() {
      return this.inventory;
   }

   /** (Re)builds the inventory and shows it to the player. */
   @SuppressWarnings("unchecked")
   public void open() {
      this.slotMap.clear();
      Component title = InventoryItem.colored(this.getRawName());
      Inventory built = Bukkit.createInventory(this, this.editable.getSlots(), title);
      for (InventoryItem<?> wildcard : this.editable.getItems().values()) {
         InventoryItem<GeneratedInventory> item = (InventoryItem<GeneratedInventory>) wildcard;
         int n = 0;
         for (int slot : item.getSlots()) {
            if (slot >= 0 && slot < built.getSize()) {
               ItemStack stack = item.getDisplayedItem(this, n);
               if (stack != null) {
                  built.setItem(slot, stack);
                  this.slotMap.put(slot, new SlotEntry(item, n));
               }
            }
            n++;
         }
      }

      this.navigator.push(this);
      this.inventory = built;
      this.navigator.setLastOpened(built);
      this.player.openInventory(built);
   }

   void click(org.bukkit.event.inventory.InventoryClickEvent event) {
      SlotEntry entry = this.slotMap.get(event.getRawSlot());
      if (entry != null && event.getCurrentItem() != null) {
         entry.item().onClick(this, event);
      }
   }

   private record SlotEntry(InventoryItem<GeneratedInventory> item, int n) {
   }
}
