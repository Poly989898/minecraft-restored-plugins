package com.Zrips.CMI.Modules.Anvil;

import com.Zrips.CMI.CMI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AnvilManager {
  CMI plugin;

  public AnvilManager(CMI cmi) {
    this.plugin = cmi;
  }

  public int getRepairCost(ItemStack itemStack, ItemStack secondItem, ItemStack thirdItem) {
    if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.hasItemMeta()) {
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (!(itemMeta instanceof Repairable)) {
        return 0;
      }

      Repairable repairable = (Repairable) itemMeta;
      int value = 0;
      if (secondItem != null && secondItem.hasItemMeta()) {
        ItemMeta secondItemMeta = secondItem.getItemMeta();
        if (secondItemMeta instanceof Repairable) {
          Repairable secondRepairable = (Repairable) secondItemMeta;
          value = secondRepairable.getRepairCost() + 1;
        }
      }

      int repairCost = repairable.getRepairCost() + value;
      if (thirdItem != null && thirdItem.getType() != Material.AIR) {
        if (thirdItem.hasItemMeta() != itemStack.hasItemMeta()
            || (thirdItem.hasItemMeta() && thirdItem.getItemMeta().hasDisplayName())
                != itemMeta.hasDisplayName()
            || thirdItem.hasItemMeta()
                && thirdItem.getItemMeta().hasDisplayName()
                && itemMeta.hasDisplayName()
                && !thirdItem
                    .getItemMeta()
                    .getDisplayName()
                    .equalsIgnoreCase(itemMeta.getDisplayName())) {
          repairCost++;
        }

        if (secondItem != null && thirdItem.getType() != Material.AIR) {
          repairCost++;
        }

        return repairCost;
      } else {
        return repairCost;
      }
    } else {
      return 0;
    }
  }
}
