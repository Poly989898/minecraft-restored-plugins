package litebans;

import org.jetbrains.annotations.NotNull;

public final class MuteNotifyTask extends CommandTask {
   private final Punishment punishment;
   private final CharSequence message;

   public MuteNotifyTask(@NotNull Platform platform, @NotNull Punishment punishment, @NotNull CharSequence message) {
      super(platform);
      this.punishment = punishment;
      this.message = message;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isLocalMessages()) {
         CommandTask commandTask2 = this;
         ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
            DatabaseSession databaseSession = (DatabaseSession) obj;
            ThrottleModule module = (ThrottleModule)databaseSession.getPlatform().getModule(ThrottleModule.class);
            CharSequence message = Placeholders.getBanScreenMessageWithDefaults(databaseSession, this.punishment, 0, 0, 6, null);
            if (message == null) {
               message = this.obtainMessage2();
            }

            CharSequence message2 = message;
            AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (Placeholders.replacePunishmentPlaceholdersForWithDefaults(databaseSession, this.punishment, this.getSender(), message2, 0L, 8, null)));
            if (!module.checkByteValue((byte)1, this.getSender().getUuidString())) {
               CommandTask commandTask3 = this;
               if (((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().isNotifyMutedPlayerChat()) {
                  Punishment punishment = this.punishment;
                  LiteSender sender = this.getSender();
                  CommandTask commandTask22 = this;
                  String text = Placeholders.replacePunishmentPlaceholdersForWithDefaults(
                     databaseSession,
                     punishment,
                     sender,
                     Placeholders.replacePlaceholder(Message.NOTIFICATION.computeText("mutedPlayer", ((ConfigModule)commandTask22.getPlatform().getModule(ConfigModule.class)).getSettings().isUseDisplayNames() ? this.getSender().getDisplayName() : this.getSender().getName()), "message", this.message),
                     0L,
                     8,
                     null
                  );
                  CommandTask commandTask32 = this;
                  BroadcastModule module2 = (BroadcastModule)commandTask32.getPlatform().getModule(BroadcastModule.class);
                  BroadcastModule.handleBroadcastModule(module2, databaseSession, text, "mute", PunishmentType.MUTE, this.punishment.getServerOrigin(), false, false, null, 112, null);
                  AltAccountPunisher.sendSyncWithDefaults(databaseSession, SyncMessageType.NOTIFY_MUTE, text, null, null, 12, null);
               }
            }

            return Unit.UNIT;
         });
      }
   }

   private final Message obtainMessage2() {
      return this.punishment.isPermanent() ? Message.MUTED_PERMANENT : Message.MUTED;
   }

}
