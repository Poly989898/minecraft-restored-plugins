package litebans;

// $VF: synthetic class
public final class SyncModuleSwitchMap {
   public static final int[] value;
   public static final int[] value2;

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

      try {
         ints[PunishmentType.WARN.ordinal()] = 3;
      } catch (NoSuchFieldError e3) {
      }

      try {
         ints[PunishmentType.KICK.ordinal()] = 4;
      } catch (NoSuchFieldError e4) {
      }

      value = ints;
      int[] ints2 = new int[SyncMessageType.values().length];

      try {
         ints2[SyncMessageType.BAN.ordinal()] = 1;
      } catch (NoSuchFieldError e5) {
      }

      try {
         ints2[SyncMessageType.MUTE.ordinal()] = 2;
      } catch (NoSuchFieldError e6) {
      }

      try {
         ints2[SyncMessageType.WARN.ordinal()] = 3;
      } catch (NoSuchFieldError e7) {
      }

      try {
         ints2[SyncMessageType.UNBAN.ordinal()] = 4;
      } catch (NoSuchFieldError e8) {
      }

      try {
         ints2[SyncMessageType.UNMUTE.ordinal()] = 5;
      } catch (NoSuchFieldError e9) {
      }

      try {
         ints2[SyncMessageType.BROADCAST.ordinal()] = 6;
      } catch (NoSuchFieldError e10) {
      }

      try {
         ints2[SyncMessageType.BROADCAST_SILENT.ordinal()] = 7;
      } catch (NoSuchFieldError e11) {
      }

      try {
         ints2[SyncMessageType.BROADCAST_EXTRA_SILENT.ordinal()] = 8;
      } catch (NoSuchFieldError e12) {
      }

      try {
         ints2[SyncMessageType.BROADCAST_CUSTOM.ordinal()] = 9;
      } catch (NoSuchFieldError e13) {
      }

      try {
         ints2[SyncMessageType.NOTIFY_BANNED_JOIN.ordinal()] = 10;
      } catch (NoSuchFieldError e14) {
      }

      try {
         ints2[SyncMessageType.NOTIFY_DUPEIP_JOIN.ordinal()] = 11;
      } catch (NoSuchFieldError e15) {
      }

      try {
         ints2[SyncMessageType.NOTIFY_MUTE.ordinal()] = 12;
      } catch (NoSuchFieldError e16) {
      }

      try {
         ints2[SyncMessageType.KICK_REQUEST.ordinal()] = 13;
      } catch (NoSuchFieldError e17) {
      }

      try {
         ints2[SyncMessageType.EXPIRED_NOTIFY.ordinal()] = 14;
      } catch (NoSuchFieldError e18) {
      }

      try {
         ints2[SyncMessageType.PING.ordinal()] = 15;
      } catch (NoSuchFieldError e19) {
      }

      try {
         ints2[SyncMessageType.SERVERS_RELOAD.ordinal()] = 16;
      } catch (NoSuchFieldError e20) {
      }

      try {
         ints2[SyncMessageType.CONFIG_RELOAD.ordinal()] = 17;
      } catch (NoSuchFieldError e21) {
      }

      value2 = ints2;
   }
}
