package io.lumine.mythic.core.menus.spawners;

public enum SpawnerBrowseMode {
   ALPHABETICAL,
   DISTANCE;

   public SpawnerBrowseMode next() {
      return this.ordinal() >= values().length - 1 ? values()[0] : values()[this.ordinal() + 1];
   }
}
