package litebans;

// $VF: synthetic class
class MessageSwitchMap {
   static final int[] INTS = new int[PunishmentType.values().length];

   static {
      try {
         INTS[PunishmentType.BAN.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         INTS[PunishmentType.MUTE.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         INTS[PunishmentType.WARN.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         INTS[PunishmentType.KICK.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }
   }
}
