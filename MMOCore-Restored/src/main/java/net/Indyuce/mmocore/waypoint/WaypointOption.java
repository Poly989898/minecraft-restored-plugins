package net.Indyuce.mmocore.waypoint;

public enum WaypointOption {
   DEFAULT(false),
   UNLOCKABLE(true),
   ENABLE_MENU(true),
   DYNAMIC(false);

   private final String path = this.name().toLowerCase().replace("_", "-");
   private final boolean defaultValue;

   WaypointOption(boolean defaultValue) {
      this.defaultValue = defaultValue;
   }

   public String getPath() {
      return this.path;
   }

   public boolean getDefaultValue() {
      return this.defaultValue;
   }
}
