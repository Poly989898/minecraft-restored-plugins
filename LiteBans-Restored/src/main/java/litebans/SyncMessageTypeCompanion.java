package litebans;

import org.jetbrains.annotations.NotNull;

public final class SyncMessageTypeCompanion {
   private SyncMessageTypeCompanion() {
   }

   public final SyncMessageType computeSyncMessageType(int value) {
      SyncMessageType[] syncMessageTypes = SyncMessageType.obtainSyncMessageTypeArray2();
      return syncMessageTypes.length <= value ? null : syncMessageTypes[value];
   }

   public final SyncMessageType computeSyncMessageType(@NotNull PunishmentType punishmentType) {
      SyncMessageType syncMessageType;
      switch (PunishmentTypeSwitchMap6.value[punishmentType.ordinal()]) {
         case 1:
            syncMessageType = SyncMessageType.BAN;
            break;
         case 2:
            syncMessageType = SyncMessageType.MUTE;
            break;
         case 3:
            syncMessageType = SyncMessageType.WARN;
            break;
         case 4:
            syncMessageType = SyncMessageType.UNBAN;
            break;
         case 5:
            syncMessageType = SyncMessageType.UNMUTE;
            break;
         case 6:
            syncMessageType = SyncMessageType.UNWARN;
            break;
         case 7:
            StandardKt.notImplemented();
            throw new CommandAbortException();
         default:
            throw new KotlinRuntimeExceptionSub();
      }

      return syncMessageType;
   }

   public SyncMessageTypeCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
