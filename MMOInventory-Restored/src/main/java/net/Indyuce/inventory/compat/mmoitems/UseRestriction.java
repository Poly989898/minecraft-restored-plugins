package net.Indyuce.inventory.compat.mmoitems;

import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.slot.restriction.SlotRestriction;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.inventory.ItemStack;

public class UseRestriction extends SlotRestriction {
   @Override
   public boolean isVerified(InventoryHandler provider, CustomSlot slot, ItemStack item) {
      return PlayerData.get(provider.getPlayer()).getRPG().canUse(NBTItem.get(item), false);
   }
}
