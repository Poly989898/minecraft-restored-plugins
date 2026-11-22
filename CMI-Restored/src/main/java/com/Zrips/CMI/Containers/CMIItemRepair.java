package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Worth.WorthEnchantment;
import com.Zrips.CMI.Modules.Worth.WorthItem;
import java.util.List;
import java.util.Map.Entry;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class CMIItemRepair {
  public static double getRepairCost(List<ItemStack> list) {
    double number = 0.0;

    for (ItemStack itemStack : list) {
      number += getRepairCost(itemStack);
    }

    return format(number);
  }

  public static double getRepairCost(ItemStack itemStack) {
    if (itemStack != null && !CMIMaterial.isAir(itemStack.getType())) {
      var map = itemStack.getEnchantments();
      ItemMeta itemMeta = itemStack.getItemMeta();
      if (itemMeta != null && itemMeta instanceof Repairable) {
        Double repairCost = CMI.getInstance().getRepairManager().getBaseRepairCost();

        for (Entry entry : map.entrySet()) {
          WorthEnchantment worthEnchantment =
              CMI.getInstance()
                  .getWorthManager()
                  .getEnchantWorth((Enchantment) entry.getKey(), (Integer) entry.getValue());
          if (worthEnchantment == null) {
            worthEnchantment =
                CMI.getInstance()
                    .getWorthManager()
                    .getEnchantWorth((Enchantment) entry.getKey(), 0);
          }

          if (worthEnchantment != null && worthEnchantment.getSellPrice() > 0.0) {
            repairCost =
                repairCost
                    + worthEnchantment.getSellPrice()
                        * (CMI.getInstance().getRepairManager().getEnchantWorthPercentage()
                            / 100.0);
          }
        }

        WorthItem worthItem = CMI.getInstance().getWorthManager().getWorth(itemStack);
        if (worthItem != null && worthItem.getSellPrice() > 0.0) {
          repairCost =
              repairCost
                  + worthItem.getSellPrice()
                      * (CMI.getInstance().getRepairManager().getWorthPercentage() / 100.0);
        }

        if (CMI.getInstance().getRepairManager().isCheckDurability()) {
          short shortValue = itemStack.getType().getMaxDurability();
          int value = shortValue - itemStack.getDurability();
          shortValue = shortValue < 1 ? 1 : shortValue;
          value = value < 1 ? 1 : value;
          double number = 100.0 - format(value * 100 / shortValue);
          repairCost = number * repairCost / 100.0;
        }

        return format(repairCost);
      } else {
        return 0.0;
      }
    } else {
      return 0.0;
    }
  }

  private static double format(double number) {
    return (int) (number * 100.0) / 100.0;
  }
}
