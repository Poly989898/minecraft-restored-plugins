package litebans;

// $VF: synthetic class
public final class PunishmentTypeSwitchMap2 {
   public static final int[] value;

   static {
      int[] ints = new int[PunishmentType.values().length];

      try {
         ints[PunishmentType.KICK.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[PunishmentType.WARN.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      value = ints;
   }
}
