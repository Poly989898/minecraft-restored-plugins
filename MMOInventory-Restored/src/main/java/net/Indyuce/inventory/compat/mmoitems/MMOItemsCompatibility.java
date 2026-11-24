package net.Indyuce.inventory.compat.mmoitems;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.inventory.MMOInventory;
import net.Indyuce.inventory.api.event.ItemEquipEvent;
import net.Indyuce.inventory.compat.InventoryUpdater;
import net.Indyuce.inventory.inventory.InventoryHandler;
import net.Indyuce.inventory.inventory.InventoryItem;
import net.Indyuce.inventory.inventory.InventoryLookupMode;
import net.Indyuce.inventory.slot.CustomSlot;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.inventory.EquippedItem;
import net.Indyuce.mmoitems.comp.inventory.PlayerInventory;
import net.Indyuce.mmoitems.comp.inventory.RPGInventoryHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MMOItemsCompatibility implements Listener, InventoryUpdater, PlayerInventory {
   public MMOItemsCompatibility() {
      MMOItems.plugin.getInventory().unregisterIf(inv -> inv instanceof RPGInventoryHook);
      MMOItems.plugin.getInventory().register(this);
      MMOInventory.plugin.registerInventoryUpdater(this);
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void updateWhenEquippingItem(ItemEquipEvent event) {
      Bukkit.getScheduler().runTaskLater(MMOInventory.plugin, () -> PlayerData.get(event.getPlayer()).updateInventory(), 0L);
   }

   @Override
   public void updateInventory(Player player) {
      PlayerData.get(player).updateInventory();
   }

   public List<EquippedItem> getInventory(Player player) {
      List<EquippedItem> list = new ArrayList<>();
      InventoryHandler inv = (InventoryHandler)MMOInventory.plugin.getDataManager().get(player);

      for (InventoryItem invItem : inv.getItems(InventoryLookupMode.NORMAL)) {
         list.add(new MMOItemsCompatibility.CustomEquippedItem(inv, invItem.getSlot(), invItem.getItemStack()));
      }

      return list;
   }

   public class CustomEquippedItem extends EquippedItem {
      private final InventoryHandler holder;
      private final CustomSlot customSlot;

      public CustomEquippedItem(InventoryHandler holder, CustomSlot customSlot, ItemStack item) {
         super(item, EquipmentSlot.ACCESSORY);
         this.holder = holder;
         this.customSlot = customSlot;
      }

      public void setItem(@Nullable ItemStack itemStack) {
         this.holder.setItem(this.customSlot, itemStack);
      }
   }
}
