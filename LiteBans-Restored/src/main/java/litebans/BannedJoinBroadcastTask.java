package litebans;

import org.jetbrains.annotations.NotNull;

public final class BannedJoinBroadcastTask extends CommandTask {
   private final String target;
   private final CharSequence joinMessage;
   private final boolean syncRequested;

   public BannedJoinBroadcastTask(@NotNull Platform platform, @NotNull String text, @NotNull CharSequence message, boolean flag) {
      super(platform);
      this.target = text;
      this.joinMessage = message;
      this.syncRequested = flag;
   }

   @Override
   public void run() {
      ThrottleModule module = (ThrottleModule)this.getPlatform().getModule(ThrottleModule.class);
      if (!module.checkByteValue((byte)2, TargetParser.normalize(this.target))) {
         CommandTask commandTask = this;
         ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
            DatabaseSession databaseSession = (DatabaseSession) obj;
            CommandTask commandTask2 = this;
            BroadcastModule module2 = (BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class);
            BroadcastModule.handleBroadcastModule(module2, databaseSession, this.joinMessage, "banned_join", null, null, false, false, null, 124, null);
            if (this.syncRequested && databaseSession.getDatabaseModule().isSyncEnabled()) {
               SyncMessageType syncMessageType = SyncMessageType.NOTIFY_BANNED_JOIN;
               CharSequence[] messages = new CharSequence[]{this.target, this.joinMessage};
               AltAccountPunisher.sendSyncWithDefaults(databaseSession, syncMessageType, ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null), null, null, 12, null);
            }

            return Unit.UNIT;
         });
      }
   }

}
