package io.lumine.mythic.core.menus.items;

public enum ItemBrowseMode {
   SHOW_ALL,
   PACKS,
   GROUPS;

   public ItemBrowseMode next() {
      return this.ordinal() >= values().length - 1 ? values()[0] : values()[this.ordinal() + 1];
   }
}
