package io.lumine.mythic.lib.gui.editable.item;

import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.util.lang3.Validate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/** One configured GUI item: the slots it occupies, how it looks and what happens when it is clicked. */
public abstract class InventoryItem<T extends GeneratedInventory> {
   private final List<Integer> slots = new ArrayList<>();
   private String function;
   protected final DecimalFormat ONE_DIGIT = new DecimalFormat("0.#");

   public InventoryItem(ConfigurationSection config) {
      this(null, config);
   }

   /** Creates an item that inherits the slots and the function of its parent. */
   public InventoryItem(InventoryItem<T> parent, ConfigurationSection config) {
      if (parent != null) {
         this.slots.addAll(parent.slots);
         this.function = parent.function;
      }

      if (config != null) {
         config.getStringList("slots").forEach(slot -> this.slots.add(Integer.parseInt(slot.trim())));
      }
   }

   public void setFunction(String function) {
      Validate.isTrue(this.function == null, "Function already provided");
      this.function = function;
   }

   public String getFunction() {
      return this.function;
   }

   public List<Integer> getSlots() {
      return this.slots;
   }

   /** Whether every slot of this item shows something else (the n-th slot is the n-th entry of a list). */
   public boolean hasDifferentDisplay() {
      return false;
   }

   public boolean isDisplayed(T inv) {
      return true;
   }

   /** The item shown in the n-th slot of this GUI item, or null for an empty slot. */
   public abstract ItemStack getDisplayedItem(T inv, int n);

   /** The player placeholders (PlaceholderAPI) are parsed for. */
   public OfflinePlayer getEffectivePlayer(T inv, int n) {
      return inv.getPlayer();
   }

   public void onClick(T inv, InventoryClickEvent event) {
   }
}
