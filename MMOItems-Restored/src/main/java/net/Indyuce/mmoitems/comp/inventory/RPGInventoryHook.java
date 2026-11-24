package net.Indyuce.mmoitems.comp.inventory;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import ru.endlesscode.rpginventory.api.InventoryAPI;

public class RPGInventoryHook implements PlayerInventory, Listener {
   @Override
   public List<EquippedItem> getInventory(Player player) {
      ArrayList list = new ArrayList();

      for (ItemStack passiveItem : InventoryAPI.getPassiveItems(player)) {
         if (passiveItem != null) {
            list.add(new RPGInventoryHook.LegacyEquippedItem(passiveItem));
         }
      }

      return list;
   }

   @EventHandler
   public void a(InventoryCloseEvent event) {
      if (InventoryAPI.isRPGInventory(event.getInventory())) {
         PlayerData.get((Player)event.getPlayer()).updateInventory();
      }
   }

   public class LegacyEquippedItem extends EquippedItem {
      public LegacyEquippedItem(ItemStack nullx) {
         super(nullx, EquipmentSlot.ACCESSORY);
      }

      @Override
      public void setItem(@Nullable ItemStack item) {
         ItemStack slotItem = this.getNBT().getItem();
         slotItem.setType(item.getType());
         slotItem.setAmount(item.getAmount());
         slotItem.setItemMeta(slotItem.getItemMeta());
      }
   }
}
