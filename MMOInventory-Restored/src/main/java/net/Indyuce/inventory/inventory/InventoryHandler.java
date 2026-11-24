package net.Indyuce.inventory.inventory;

import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.data.SynchronizedDataHolder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class InventoryHandler extends SynchronizedDataHolder {
   public InventoryHandler(MMOPlayerData playerData) {
      super(MMOInventory.plugin, playerData);
   }

   public void setItem(@NotNull CustomSlot slot, @Nullable ItemStack item) {
      if (slot.getType().isCustom()) {
         this.insertItemAtSlot(slot, item);
      } else {
         slot.getType().getVanillaSlotHandler().equip(this.getPlayer(), item);
      }
   }

   public Collection<InventoryItem> getItems(InventoryLookupMode lookupMode) {
      Set<InventoryItem> items = new HashSet<>();

      for (InventoryItem invItem : this.retrieveItems()) {
         if (lookupMode == InventoryLookupMode.IGNORE_RESTRICTIONS || invItem.getSlot().checkSlotRestrictions(this, invItem.getItemStack())) {
            items.add(invItem);
         }
      }

      return items;
   }

   protected abstract void insertItemAtSlot(@NotNull CustomSlot customSlot, @Nullable ItemStack item);

   protected abstract Collection<InventoryItem> retrieveItems();
}
