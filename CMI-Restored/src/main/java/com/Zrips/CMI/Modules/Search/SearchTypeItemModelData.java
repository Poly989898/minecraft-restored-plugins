package com.Zrips.CMI.Modules.Search;

import java.util.Objects;
import net.Zrips.CMILib.Items.CMIItemStack;

public class SearchTypeItemModelData extends SearchTypeItem {
  int modelData = 0;

  public SearchTypeItemModelData(int value) {
    this(null, value);
  }

  public SearchTypeItemModelData(SearchTypeItem searchTypeItem, int value) {
    super(searchTypeItem);
    this.modelData = value;
  }

  public int getModelData() {
    return this.modelData;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    this.checkShulkerBundle();
    return Objects.equals(this.modelData, CMIItemStack.getCustomModelData(this.getItemToCompare()));
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item model data " + this.getModelData();
  }
}
