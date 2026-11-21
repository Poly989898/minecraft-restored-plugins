package litebans;

// $VF: synthetic class
public final class PunishmentTableSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[PunishmentTable.values().length];

      try {
         ints[PunishmentTable.BANS.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[PunishmentTable.MUTES.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[PunishmentTable.WARNINGS.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[PunishmentTable.KICKS.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      value = ints;
   }
}
