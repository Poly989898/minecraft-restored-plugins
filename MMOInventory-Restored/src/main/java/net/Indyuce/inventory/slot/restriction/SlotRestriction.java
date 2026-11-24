package net.Indyuce.inventory.slot.restriction;

import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import org.bukkit.inventory.ItemStack;

public abstract class SlotRestriction {
   public abstract boolean isVerified(InventoryHandler var1, CustomSlot var2, ItemStack var3);
}
