package litebans;

// $VF: synthetic class
public final class SyncMessageTypeSwitchMap {
   public static final int[] value;

   static {
      int[] ints = new int[SyncMessageType.values().length];

      try {
         ints[SyncMessageType.BROADCAST.ordinal()] = 1;
      } catch (NoSuchFieldError e) {
      }

      try {
         ints[SyncMessageType.BROADCAST_SILENT.ordinal()] = 2;
      } catch (NoSuchFieldError e2) {
      }

      try {
         ints[SyncMessageType.BROADCAST_EXTRA_SILENT.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[SyncMessageType.NOTIFY_DUPEIP_JOIN.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      try {
         ints[SyncMessageType.NOTIFY_MUTE.ordinal()] = 5;
      } catch (NoSuchFieldError e5) {
      }

      try {
         ints[SyncMessageType.BAN.ordinal()] = 6;
      } catch (NoSuchFieldError e6) {
      }

      try {
         ints[SyncMessageType.MUTE.ordinal()] = 7;
      } catch (NoSuchFieldError e7) {
      }

      try {
         ints[SyncMessageType.WARN.ordinal()] = 8;
      } catch (NoSuchFieldError e8) {
      }

      try {
         ints[SyncMessageType.KICK_REQUEST.ordinal()] = 9;
      } catch (NoSuchFieldError e9) {
      }

      try {
         ints[SyncMessageType.KICK_SUCCESS.ordinal()] = 10;
      } catch (NoSuchFieldError e10) {
      }

      try {
         ints[SyncMessageType.UNBAN.ordinal()] = 11;
      } catch (NoSuchFieldError e11) {
      }

      try {
         ints[SyncMessageType.UNMUTE.ordinal()] = 12;
      } catch (NoSuchFieldError e12) {
      }

      value = ints;
   }
}
