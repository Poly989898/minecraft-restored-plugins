package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Items.CMIItemSerializer;
import org.bukkit.inventory.ItemStack;

public class SearchTypeItemExact extends SearchTypeItem {
  ItemStack searchedItem = null;

  public SearchTypeItemExact(ItemStack itemStack) {
    this(null, itemStack);
  }

  public SearchTypeItemExact(SearchTypeItem searchTypeItem, ItemStack itemStack) {
    super(searchTypeItem);
    this.searchedItem = itemStack;
  }

  public ItemStack getSearchedItem() {
    return this.searchedItem;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    this.checkShulkerBundle();
    return this.getSearchedItem().isSimilar(this.getItemToCompare());
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Exact Item " + CMIItemSerializer.serialize(this.getSearchedItem());
  }
}
