package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.meta.BookMeta;

public class SearchTypeItemName extends SearchTypeItem {
  String name = null;

  public SearchTypeItemName(String string) {
    this(null, string);
  }

  public SearchTypeItemName(SearchTypeItem searchTypeItem, String string) {
    super(searchTypeItem);
    this.name = CMIChatColor.stripColor(string).replace(" ", "").toLowerCase();
  }

  public String getName() {
    return this.name;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    }

    this.checkShulkerBundle();
    if (this.getItemToCompare().getType() != Material.WRITTEN_BOOK) {
      if (!this.getItemToCompare().hasItemMeta()
          || !this.getItemToCompare().getItemMeta().hasDisplayName()) {
        return false;
      }

      if (CMIChatColor.stripColor(this.getItemToCompare().getItemMeta().getDisplayName())
          .replace(" ", "")
          .toLowerCase()
          .contains(this.name)) {
        return true;
      }
    } else {
      BookMeta bookMeta = (BookMeta) this.getItemToCompare().getItemMeta();
      String string = bookMeta.getTitle();
      if (this.getItemToCompare().hasItemMeta()
          && this.getItemToCompare().getItemMeta().hasDisplayName()) {
        string = this.getItemToCompare().getItemMeta().getDisplayName();
      }

      if (string != null
          && CMIChatColor.stripColor(string).replace(" ", "").toLowerCase().contains(this.name)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null
        ? this.parent.getSearchTypeName()
        : "Item name |" + this.getName() + "|";
  }
}
