package net.Indyuce.inventory.inventory;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.inventory.util.Utils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleInventoryHandler extends InventoryHandler {
   public SimpleInventoryHandler(MMOPlayerData playerData) {
      super(playerData);
   }

   @Override
   protected void insertItemAtSlot(@NotNull CustomSlot slot, @Nullable ItemStack item) {
      this.getPlayer().getInventory().setItem(slot.getIndex(), item);
   }

   @Override
   public Collection<InventoryItem> retrieveItems() {
      Set<InventoryItem> set = new HashSet<>();

      for (CustomSlot slot : MMOInventory.plugin.getSlotManager().getCustomSlots()) {
         ItemStack item = this.getPlayer().getInventory().getItem(slot.getIndex());
         if (!UtilityMethods.isAir(item) && !Utils.isGuiItem(item)) {
            set.add(new InventoryItem(item, slot));
         }
      }

      return set;
   }
}
