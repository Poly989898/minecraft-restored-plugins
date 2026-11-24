package io.lumine.mythic.lib.comp.flags;

public enum CustomFlag {
   MI_WEAPONS(true),
   MI_COMMANDS(true),
   MI_CONSUMABLES(true),
   MI_TOOLS(true),
   MMO_ABILITIES(true),
   PVP_MODE(false),
   @Deprecated
   ABILITY_PVP(true);

   private final boolean def;

   CustomFlag(boolean def) {
      this.def = def;
   }

   public boolean getDefault() {
      return this.def;
   }

   public String getPath() {
      return this.name().toLowerCase().replace("_", "-");
   }
}
