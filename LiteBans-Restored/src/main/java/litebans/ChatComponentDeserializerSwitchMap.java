package litebans;

// $VF: synthetic class
class ChatComponentDeserializerSwitchMap {
   static final int[] INTS = new int[GameVersion.values().length];

   static {
      try {
         INTS[GameVersion.V1_16.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         INTS[GameVersion.V1_21_5.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }
   }
}
