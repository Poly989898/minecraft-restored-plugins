package net.Indyuce.mmoitems.api.player.inventory;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public abstract class EquippedItem {
   public EquippedItem(ItemStack item, EquipmentSlot slot) { }
   public abstract void setItem(ItemStack item);
}
