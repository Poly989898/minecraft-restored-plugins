package net.Indyuce.inventory.slot;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface VanillaSlotHandler {
   void equip(Player player, ItemStack item);

   boolean canEquip(ItemStack item);

   ItemStack retrieveItem(Player player);

   boolean supportsShiftClick();
}
