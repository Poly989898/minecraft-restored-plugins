package com.Zrips.CMI.Modules.Enchants;

import com.Zrips.CMI.CMI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantListener implements Listener {
  private CMI plugin;

  public EnchantListener(CMI cmi) {
    this.plugin = cmi;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void InventoryCloseEvent(InventoryCloseEvent inventoryCloseEvent) {
    if (!this.plugin.getEnchantManager().getDisabled().isEmpty()) {
      Player player = (Player) inventoryCloseEvent.getPlayer();
      ItemStack[] itemStacks = player.getInventory().getContents();
      boolean flag = false;

      for (int value = 0; value < itemStacks.length; value++) {
        ItemStack itemStack = itemStacks[value];
        if (itemStack != null) {
          try {
            if (itemStack.getEnchantments() == null) {
              continue;
            }
          } catch (Exception exception) {
            continue;
          }

          if (!itemStack.getEnchantments().isEmpty()) {
            for (var entry : this.plugin.getEnchantManager().getDisabled().entrySet()) {
              ItemStack resultItem =
                  this.plugin.getNMS().removeEnchantNbt(itemStack, (Enchantment) entry.getValue());
              if (resultItem != null) {
                player.getInventory().setItem(value, resultItem);
                flag = true;
              }
            }
          }
        }
      }

      if (flag) {
        player.updateInventory();
      }
    }
  }
}
