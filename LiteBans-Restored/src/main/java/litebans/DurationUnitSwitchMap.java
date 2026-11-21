package litebans;

// $VF: synthetic class
public final class DurationUnitSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[DurationUnit.values().length];

      try {
         ints[DurationUnit.SECONDS.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[DurationUnit.MINUTES.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[DurationUnit.HOURS.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[DurationUnit.DAYS.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      try {
         ints[DurationUnit.WEEKS.ordinal()] = 5;
      } catch (NoSuchFieldError e5) {
      }

      try {
         ints[DurationUnit.MONTHS.ordinal()] = 6;
      } catch (NoSuchFieldError e6) {
      }

      try {
         ints[DurationUnit.YEARS.ordinal()] = 7;
      } catch (NoSuchFieldError e7) {
      }

      value = ints;
   }
}
