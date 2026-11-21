package io.lumine.mythic.core.menus.mobs;

public enum MobBrowseMode {
   SHOW_ALL,
   PACKS;

   public MobBrowseMode next() {
      return this.ordinal() >= values().length - 1 ? values()[0] : values()[this.ordinal() + 1];
   }
}
