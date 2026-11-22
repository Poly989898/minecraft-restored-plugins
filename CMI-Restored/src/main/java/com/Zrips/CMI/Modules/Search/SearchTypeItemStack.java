package com.Zrips.CMI.Modules.Search;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import net.Zrips.CMILib.Items.CMIItemSerializer;
import net.Zrips.CMILib.Items.CMIItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class SearchTypeItemStack extends SearchTypeItem {
  ItemStack searchedItem = null;
  Set<SearchTypeItem> searchTypes = new LinkedHashSet<>();
  Set<ItemSearchCriteria> criteria = new HashSet<>();

  public SearchTypeItemStack(ItemStack itemStack, Set<ItemSearchCriteria> set) {
    super(null);
    this.searchedItem = itemStack;
    this.criteria = set;

    label36:
    for (ItemSearchCriteria itemSearchCriteria : set) {
      switch (itemSearchCriteria) {
        case MATERIAL:
          this.searchTypes.add(new SearchTypeItemMaterial(this, itemStack.getType()));
          continue;
        case NAME:
          String string = new CMIItemStack(itemStack).getDisplayName();
          if (!string.isEmpty()) {
            this.searchTypes.add(new SearchTypeItemName(this, string));
          }
          continue;
        case LORE:
          var list = new CMIItemStack(itemStack).getLore();
          if (!list.isEmpty()) {
            this.searchTypes.add(new SearchTypeItemLore(this, (String) list.get(0)));
          }
          continue;
        case ENCHANT:
          var iterator = itemStack.getEnchantments().entrySet().iterator();

          while (true) {
            if (!iterator.hasNext()) {
              continue label36;
            }

            Entry entry = (Entry) iterator.next();
            this.searchTypes.add(
                new SearchTypeItemEnchant(
                    this, (Enchantment) entry.getKey(), (Integer) entry.getValue()));
          }
        case MODELDATA:
          this.searchTypes.add(
              new SearchTypeItemModelData(this, CMIItemStack.getCustomModelData(itemStack)));
        case POTION:
        default:
          continue;
        case AMOUNT:
          this.searchTypes.add(new SearchTypeItemAmount(this, itemStack.getAmount()));
          continue;
        case EXACT:
          this.searchTypes.add(new SearchTypeItemExact(this, itemStack));
          continue;
        case OVERSIZE:
      }

      this.searchTypes.add(new SearchTypeItemAmount(this, 0));
    }
  }

  public ItemStack getSearchedItem() {
    return this.searchedItem;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    for (SearchTypeItem searchTypeItem : this.searchTypes) {
      boolean flag = searchTypeItem.isValid();
      if (!flag) {
        return false;
      }
    }

    return true;
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item "
            + CMIItemSerializer.serialize(this.getSearchedItem())
            + " By criteria "
            + this.criteria;
  }
}
