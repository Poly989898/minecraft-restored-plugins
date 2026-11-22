package com.Zrips.CMI.Modules.Search;

import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class SearchTypeItemEnchant extends SearchTypeItem {
  Enchantment enchant;
  int level;

  public SearchTypeItemEnchant(Enchantment enchantment, int value) {
    this(null, enchantment, value);
  }

  public SearchTypeItemEnchant(SearchTypeItem searchTypeItem, Enchantment enchantment, int value) {
    super(searchTypeItem);
    this.enchant = enchantment;
    this.level = value;
  }

  public Enchantment getEnchant() {
    return this.enchant;
  }

  public int getLevel() {
    return this.level;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    if (this.checkShulkerBundle()) {
      return false;
    }

    Map map = null;
    if (this.getItemToCompare().hasItemMeta()
        && this.getItemToCompare().getItemMeta().hasEnchants()) {
      map = this.getItemToCompare().getItemMeta().getEnchants();
    }

    if (this.getItemToCompare().hasItemMeta()
        && this.getItemToCompare().getItemMeta() instanceof EnchantmentStorageMeta) {
      EnchantmentStorageMeta enchantmentStorageMeta =
          (EnchantmentStorageMeta) this.getItemToCompare().getItemMeta();
      map = enchantmentStorageMeta.getStoredEnchants();
    }

    if (map == null) {
      return false;
    }

    if (this.getEnchant() != null) {
      Integer integer = (Integer) map.get(this.getEnchant());
      if (integer == null) {
        return false;
      } else {
        return this.getLevel() > -1 ? integer >= this.getLevel() : true;
      }
    } else {
      for (Entry entry : ((java.util.Map<?, ?>) map).entrySet()) {
        if ((Integer) entry.getValue() >= this.getLevel()) {
          return true;
        }
      }

      return false;
    }
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item enchant " + this.enchant + ":" + this.level;
  }
}
