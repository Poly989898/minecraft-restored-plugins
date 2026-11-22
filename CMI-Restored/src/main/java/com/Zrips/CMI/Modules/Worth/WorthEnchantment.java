package com.Zrips.CMI.Modules.Worth;

import org.bukkit.enchantments.Enchantment;

public class WorthEnchantment {
  private Double sellPrice = 0.0;
  private Double buyPrice = null;
  private Integer level = null;
  private Enchantment enchant;

  public WorthEnchantment(Enchantment enchantment, int value, double number, Double newBuyPrice) {
    this.enchant = enchantment;
    this.level = value;
    this.sellPrice = number;
    this.buyPrice = newBuyPrice;
  }

  public Double getSellPrice() {
    return this.sellPrice;
  }

  public void setSellPrice(double number) {
    this.sellPrice = number;
    if (this.sellPrice < 0.0) {
      this.sellPrice = 0.0;
    }
  }

  public Double getBuyPrice() {
    return this.buyPrice == null ? this.sellPrice : this.buyPrice;
  }

  public boolean isBuyPriceSet() {
    return this.buyPrice != null;
  }

  public void setBuyPrice(Double newBuyPrice) {
    this.buyPrice = newBuyPrice;
    if (this.buyPrice != null && this.buyPrice < 0.0) {
      this.buyPrice = null;
    }
  }

  public Enchantment getEnchant() {
    return this.enchant;
  }

  public Integer getLevel() {
    return this.level;
  }
}
