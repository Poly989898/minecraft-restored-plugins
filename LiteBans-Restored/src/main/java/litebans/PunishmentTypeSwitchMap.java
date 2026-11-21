package litebans;

// $VF: synthetic class
public final class PunishmentTypeSwitchMap {
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

      value = ints;
   }
}
