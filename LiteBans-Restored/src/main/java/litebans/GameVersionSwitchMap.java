package litebans;

// $VF: synthetic class
class GameVersionSwitchMap {
   static final int[] value;
   static final int[] INTS_2 = new int[GameVersion.values().length];

   static {
      try {
         INTS_2[GameVersion.V1_21_5.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         INTS_2[GameVersion.V1_16.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      value = new int[ClickAction.values().length];

      try {
         value[ClickAction.OPEN_URL.ordinal()] = 1;
      } catch (NoSuchFieldError e3) {
      }

      try {
         value[ClickAction.RUN_COMMAND.ordinal()] = 2;
      } catch (NoSuchFieldError e4) {
      }

      try {
         value[ClickAction.SUGGEST_COMMAND.ordinal()] = 3;
      } catch (NoSuchFieldError e5) {
      }

      try {
         value[ClickAction.CHANGE_PAGE.ordinal()] = 4;
      } catch (NoSuchFieldError e6) {
      }
   }
}
