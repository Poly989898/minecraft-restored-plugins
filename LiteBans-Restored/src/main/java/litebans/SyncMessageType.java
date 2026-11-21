package litebans;

public enum SyncMessageType {
   PING,
   PONG,
   BROADCAST,
   BROADCAST_SILENT,
   BROADCAST_EXTRA_SILENT,
   NOTIFY_BANNED_JOIN,
   NOTIFY_DUPEIP_JOIN,
   NOTIFY_MUTE,
   BAN,
   MUTE,
   WARN,
   KICK_REQUEST,
   KICK_SUCCESS,
   UNBAN,
   UNMUTE,
   UNWARN,
   EXPIRED_NOTIFY,
   BROADCAST_CUSTOM,
   SERVERS_RELOAD,
   CONFIG_RELOAD;

   public static final SyncMessageTypeCompanion SYNC_MESSAGE_TYPE_COMPANION = new SyncMessageTypeCompanion(null);
   private static final SyncMessageType[] return2 = values();
   private static final KotlinListSub KOTLIN_LIST_SUB = EnumEntries.computeKotlinListSub(values());

   public final int obtainIntValue() {
      return this.ordinal();
   }

   public final String obtainText() {
      String text;
      switch (SyncMessageTypeSwitchMap.value[this.ordinal()]) {
         case 1:
            text = "broadcast";
            break;
         case 2:
            text = "silent";
            break;
         case 3:
            text = "console";
            break;
         case 4:
            text = "dupeip_join";
            break;
         case 5:
            text = "mute";
            break;
         default:
            throw new UnsupportedOperationException(this.toString());
      }

      return text;
   }

   public final boolean obtainFlag() {
      return this == UNBAN || this == UNMUTE || this == UNWARN;
   }

   public static SyncMessageType computeSyncMessageType(String text) {
      return Enum.valueOf(SyncMessageType.class, text);
   }

   private static final SyncMessageType[] obtainSyncMessageTypeArray() {
      return new SyncMessageType[]{
         PING,
         PONG,
         BROADCAST,
         BROADCAST_SILENT,
         BROADCAST_EXTRA_SILENT,
         NOTIFY_BANNED_JOIN,
         NOTIFY_DUPEIP_JOIN,
         NOTIFY_MUTE,
         BAN,
         MUTE,
         WARN,
         KICK_REQUEST,
         KICK_SUCCESS,
         UNBAN,
         UNMUTE,
         UNWARN,
         EXPIRED_NOTIFY,
         BROADCAST_CUSTOM,
         SERVERS_RELOAD,
         CONFIG_RELOAD
      };
   }

   public static final SyncMessageType[] obtainSyncMessageTypeArray2() {
      return return2;
   }
}
