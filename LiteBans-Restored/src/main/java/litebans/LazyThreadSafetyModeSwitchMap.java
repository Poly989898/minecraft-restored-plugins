package litebans;

// $VF: synthetic class
public final class LazyThreadSafetyModeSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[LazyThreadSafetyMode.values().length];

      try {
         ints[LazyThreadSafetyMode.SYNCHRONIZED.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[LazyThreadSafetyMode.PUBLICATION.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[LazyThreadSafetyMode.NONE.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      value = ints;
   }
}
