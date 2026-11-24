package net.Indyuce.inventory.slot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface VanillaSlotHandler {
   void equip(Player var1, ItemStack var2);

   boolean canEquip(ItemStack var1);

   ItemStack retrieveItem(Player var1);

   boolean supportsShiftClick();
}
