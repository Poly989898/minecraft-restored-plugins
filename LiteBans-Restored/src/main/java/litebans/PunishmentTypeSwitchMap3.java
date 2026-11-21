package litebans;

// $VF: synthetic class
public final class PunishmentTypeSwitchMap3 {
   public static final int[] value;

   static {
      int[] ints = new int[PunishmentType.values().length];

      try {
         ints[PunishmentType.BAN.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[PunishmentType.MUTE.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[PunishmentType.WARN.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[PunishmentType.KICK.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      value = ints;
   }
}
