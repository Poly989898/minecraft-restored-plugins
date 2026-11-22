package com.Zrips.CMI.Modules.Worth;

import com.Zrips.CMI.CMI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class WorthItemCheck {
  private WorthItem worth = null;
  private ItemStack item;

  public WorthItemCheck(ItemStack itemStack) {
    this.item = itemStack.clone();
    this.worth = CMI.getInstance().getWorthManager().getWorth(itemStack);
  }

  public Double getSellPrice() {
    return this.getSellPrice(true);
  }

  public Double getSellPrice(boolean flag) {
    return this.getSellPrice(flag, true);
  }

  public Double getSellPrice(boolean flag, boolean includeEnchants) {
    return this.item == null
        ? this.worth.getSellPrice()
        : this.worth.getPlayerSellPrice(this.item, flag, includeEnchants);
  }

  public Double getEnchantSellPrice() {
    if (this.item == null) {
      return 0.0;
    }

    Double sellPrice = 0.0;

    for (var entry : this.item.getEnchantments().entrySet()) {
      if (entry != null) {
        sellPrice =
            sellPrice
                + CMI.getInstance()
                    .getWorthManager()
                    .getEnchantSellPrice((Enchantment) entry.getKey(), (Integer) entry.getValue());
      }
    }

    return sellPrice;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public Double getBuyPrice() {
    return this.worth.getBuyPrice();
  }

  public boolean isBuyPriceSet() {
    return this.worth.getBuyPrice() != null;
  }

  public WorthItem getWorth() {
    return this.worth;
  }

  public void setWorth(WorthItem worthItem) {
    this.worth = worthItem;
  }
}
