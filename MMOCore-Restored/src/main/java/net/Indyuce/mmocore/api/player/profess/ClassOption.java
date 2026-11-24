package net.Indyuce.mmocore.api.player.profess;

public enum ClassOption {
   DEFAULT,
   NEEDS_PERMISSION,
   DISPLAY(true),
   OFF_COMBAT_HEALTH_REGEN,
   OFF_COMBAT_MANA_REGEN,
   OFF_COMBAT_STAMINA_REGEN,
   OFF_COMBAT_STELLIUM_REGEN;

   private final boolean def;

   ClassOption() {
      this(false);
   }

   ClassOption(boolean def) {
      this.def = def;
   }

   public boolean getDefault() {
      return this.def;
   }

   public String getPath() {
      return this.name().toLowerCase().replace("_", "-");
   }
}
