package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Colors.CMIChatColor;

public class SearchTypeItemLore extends SearchTypeItem {
  String lore = null;

  public SearchTypeItemLore(String string) {
    this(null, string);
  }

  public SearchTypeItemLore(SearchTypeItem searchTypeItem, String string) {
    super(searchTypeItem);
    this.lore = string.replace(" ", "").toLowerCase();
  }

  public String getLore() {
    return this.lore;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    this.checkShulkerBundle();
    if (this.getItemToCompare().hasItemMeta() && this.getItemToCompare().getItemMeta().hasLore()) {
      for (String string : this.getItemToCompare().getItemMeta().getLore()) {
        if (string != null
            && CMIChatColor.stripColor(string).replace(" ", "").toLowerCase().contains(this.lore)) {
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item lore |" + this.getLore() + "|";
  }
}
