package litebans;

import org.jetbrains.annotations.NotNull;

public final class BannedJoinBroadcastTask extends CommandTask {
   private final String text58;
   private final CharSequence message52;
   private final boolean flag43;

   public BannedJoinBroadcastTask(@NotNull Platform platform, @NotNull String text, @NotNull CharSequence message, boolean flag) {
      super(platform);
      this.text58 = text;
      this.message52 = message;
      this.flag43 = flag;
   }

   @Override
   public void run() {
      ThrottleModule module = (ThrottleModule)this.getPlatform().getModule(ThrottleModule.class);
      if (!module.checkByteValue((byte)2, TargetParser.normalize(this.text58))) {
         CommandTask commandTask = this;
         ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
            DatabaseSession databaseSession = (DatabaseSession) obj;
            CommandTask commandTask2 = this;
            BroadcastModule module2 = (BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class);
            BroadcastModule.handleBroadcastModule(module2, databaseSession, this.message52, "banned_join", null, null, false, false, null, 124, null);
            if (this.flag43 && databaseSession.getDatabaseModule().isSyncEnabled()) {
               SyncMessageType syncMessageType = SyncMessageType.NOTIFY_BANNED_JOIN;
               CharSequence[] messages = new CharSequence[]{this.text58, this.message52};
               AltAccountPunisher.sendSyncWithDefaults(databaseSession, syncMessageType, ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null), null, null, 12, null);
            }

            return Unit.UNIT;
         });
      }
   }

}
