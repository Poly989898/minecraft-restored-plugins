package com.Zrips.CMI.Modules.Search;

import javax.annotation.Nonnull;

public class SearchTypeBase {
  private SearchType type = SearchType.ITEM;

  public SearchType getType() {
    return this.type;
  }

  public SearchTypeBase setType(@Nonnull SearchType searchType) {
    this.type = searchType;
    return this;
  }

  public String getSearchTypeName() {
    return null;
  }
}
