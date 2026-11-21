package litebans;

// $VF: synthetic class
public final class ConfigModuleSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[PunishmentType.values().length];

      try {
         ints[PunishmentType.KICK.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      value = ints;
   }
}
