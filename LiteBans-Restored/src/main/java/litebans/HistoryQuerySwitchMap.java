package litebans;

// $VF: synthetic class
public final class HistoryQuerySwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[ExecutorSource.values().length];

      try {
         ints[ExecutorSource.UUID.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[ExecutorSource.EXECUTOR_UUID.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[ExecutorSource.ID.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      value = ints;
   }
}
