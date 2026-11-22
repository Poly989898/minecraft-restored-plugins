package com.Zrips.CMI.Modules.Search;

public class SearchTypeItemAmount extends SearchTypeItem {
  int stackSize = -1;

  public SearchTypeItemAmount(int value) {
    this(null, value);
  }

  public SearchTypeItemAmount(SearchTypeItem searchTypeItem, int value) {
    super(searchTypeItem);
    this.stackSize = value;
    if (value > 0) {
      if (searchTypeItem == null) {
        this.checkTotalAmount = value;
      } else {
        searchTypeItem.checkTotalAmount = value;
      }
    }
  }

  public int getStackSize() {
    return this.stackSize;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    this.checkShulkerBundle();
    return this.stackSize <= 0
        ? this.getItemToCompare().getAmount() > this.getItemToCompare().getType().getMaxStackSize()
        : true;
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item stack size " + this.getStackSize();
  }
}
