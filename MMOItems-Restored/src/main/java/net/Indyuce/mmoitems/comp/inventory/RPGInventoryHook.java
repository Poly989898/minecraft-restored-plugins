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
   public List<EquippedItem> getInventory(Player var1) {
      ArrayList var2 = new ArrayList();

      for (ItemStack var4 : InventoryAPI.getPassiveItems(var1)) {
         if (var4 != null) {
            var2.add(new RPGInventoryHook.LegacyEquippedItem(var4));
         }
      }

      return var2;
   }

   @EventHandler
   public void a(InventoryCloseEvent var1) {
      if (InventoryAPI.isRPGInventory(var1.getInventory())) {
         PlayerData.get((Player)var1.getPlayer()).updateInventory();
      }
   }

   public class LegacyEquippedItem extends EquippedItem {
      public LegacyEquippedItem(ItemStack nullx) {
         super(nullx, EquipmentSlot.ACCESSORY);
      }

      @Override
      public void setItem(@Nullable ItemStack var1) {
         ItemStack var2 = this.getNBT().getItem();
         var2.setType(var1.getType());
         var2.setAmount(var1.getAmount());
         var2.setItemMeta(var2.getItemMeta());
      }
   }
}
