package litebans;

// $VF: synthetic class
public final class PunishmentSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[PunishmentType.values().length];

      try {
         ints[PunishmentType.UNBAN.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[PunishmentType.UNMUTE.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[PunishmentType.UNWARN.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[PunishmentType.WARN.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      try {
         ints[PunishmentType.KICK.ordinal()] = 5;
      } catch (NoSuchFieldError e5) {
      }

      value = ints;
   }
}
