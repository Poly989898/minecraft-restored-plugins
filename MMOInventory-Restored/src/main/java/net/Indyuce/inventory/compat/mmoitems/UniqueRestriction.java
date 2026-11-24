package net.Indyuce.inventory.compat.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.inventory.util.LineConfig;
import org.bukkit.inventory.ItemStack;

public class UniqueRestriction extends SlotRestriction {
   public UniqueRestriction(LineConfig config) {
   }

   @Override
   public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
      NBTItem nbtItem = NBTItem.get(item);
      String set = nbtItem.getString("MMOITEMS_ACCESSORY_SET");

      for (InventoryItem invItem : provider.getItems(InventoryLookupMode.IGNORE_RESTRICTIONS)) {
         if (slot.getIndex() != invItem.getSlot().getIndex()) {
            NBTItem nbtInvItem = NBTItem.get(invItem.getItemStack());
            boolean sameItem = this.getStringMMOItem(nbtItem).equalsIgnoreCase(this.getStringMMOItem(nbtInvItem));
            boolean sameSet = set != null && set.length() > 0 && set.equalsIgnoreCase(nbtInvItem.getString("MMOITEMS_ACCESSORY_SET"));
            if (sameItem || sameSet) {
               return false;
            }
         }
      }

      return true;
   }

   private String getStringMMOItem(NBTItem item) {
      return item.getString("MMOITEMS_ITEM_ID") + "." + item.getString("MMOITEMS_ITEM_TYPE");
   }
}
