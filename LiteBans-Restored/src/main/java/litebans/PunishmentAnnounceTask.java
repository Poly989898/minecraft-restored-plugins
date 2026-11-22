package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentAnnounceTask extends CommandTask {
   private final PunishmentRequest punishmentRequest;
   private final LiteSender liteSender;
   private final LoginRecord loginRecord;
   private final CharSequence announceMessage;
   private final LiteSender announceSender;
   private final boolean broadcast;
   private final boolean quiet;

   public PunishmentAnnounceTask(
      @NotNull PunishmentRequest punishmentRequest2, @Nullable LiteSender liteSender, @Nullable LoginRecord loginRecord2, @NotNull CharSequence message, @NotNull Platform platform, @Nullable LiteSender liteSender2, boolean flag, boolean flag2
   ) {
      super(platform);
      this.punishmentRequest = punishmentRequest2;
      this.liteSender = liteSender;
      this.loginRecord = loginRecord2;
      this.announceMessage = message;
      this.announceSender = liteSender2;
      this.broadcast = flag;
      this.quiet = flag2;
   }

   public PunishmentAnnounceTask(PunishmentRequest punishmentRequest2, LiteSender liteSender, LoginRecord loginRecord2, CharSequence message, Platform platform, LiteSender liteSender2, boolean flag, boolean flag2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 32) != 0) {
         liteSender2 = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
      }

      if ((value & 64) != 0) {
         flag = true;
      }

      if ((value & 128) != 0) {
         Punishment punishment = punishmentRequest2.getPunishment();
         Intrinsics.checkNotNull(punishment);
         flag2 = punishment.isSilent();
      }

      this(punishmentRequest2, liteSender, loginRecord2, message, platform, liteSender2, flag, flag2);
   }

   public final PunishmentRequest getPunishmentRequest() {
      return this.punishmentRequest;
   }

   public final LiteSender getLiteSender() {
      return this.liteSender;
   }

   public final LoginRecord getLoginRecord() {
      return this.loginRecord;
   }

   public final LiteSender getLiteSender15() {
      return this.announceSender;
   }

   public final boolean isBroadcast() {
      return this.broadcast;
   }

   public final boolean isQuiet() {
      return this.quiet;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CharSequence message = this.announceMessage;
         CharSequence message2 = message;
         if (this.isQuiet()) {
            CharSequence message3 = Message.SILENT_PREFIX;
            message2 = "" + message3 + message2;
         }

         if (this.getLoginRecord() != null) {
            message2 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message2, this.getLoginRecord(), false, 2, null);
         }

         CharSequence message4 = Placeholders.replacePlaceholders(databaseSession, message2, this.getPunishmentRequest());
         CharSequence message5 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, databaseSession.replacePlaceholders(message4, this.getLiteSender()), this.getPunishmentRequest().getPunishment(), false, 2, null);
         PunishmentRequest punishmentRequest2 = this.getPunishmentRequest();
         Punishment punishment = punishmentRequest2.getPunishment();
         Intrinsics.checkNotNull(punishment);
         Punishment punishment2 = punishment;
         SyncMessageType syncMessageType = this.getPunishmentRequest().isExtraSilent() ? SyncMessageType.BROADCAST_EXTRA_SILENT : (this.isQuiet() ? SyncMessageType.BROADCAST_SILENT : SyncMessageType.BROADCAST);
         CommandTask commandTask2 = this;
         BroadcastModule module = (BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class);
         BroadcastModule.handleBroadcastModule(module, databaseSession, message5, syncMessageType.obtainText(), punishment2.getType(), punishment2.getServerOrigin(), false, false, this.getLiteSender15(), 48, null);
         AltAccountPunisher.sendSyncWithDefaults(databaseSession, syncMessageType, message5, punishment2.getType(), null, 8, null);
         PunishmentRequest punishmentRequest3 = this.getPunishmentRequest();
         punishment = punishmentRequest3.getPunishment();
         Intrinsics.checkNotNull(punishment);
         Punishment punishment3 = punishment;
         PunishmentType type = punishment3.getType();
         if ((type.obtainPunishmentType() == PunishmentType.MUTE || type.obtainPunishmentType() == PunishmentType.WARN) && this.isBroadcast() && this.getLiteSender() != null) {
            String text;
            switch (PunishmentTypeSwitchMap3.value[punishment3.getType().obtainPunishmentType().ordinal()]) {
               case 1:
                  text = "banned";
                  break;
               case 2:
                  text = "muted";
                  break;
               case 3:
                  text = "warned";
                  break;
               case 4:
                  text = "kicked";
                  break;
               default:
                  StandardKt.notImplemented();
                  throw new CommandAbortException();
            }

            String text2 = text;
            CommandTask commandTask22 = this;
            BroadcastModule module2 = (BroadcastModule)commandTask22.getPlatform().getModule(BroadcastModule.class);
            module2.handleDatabaseSession(databaseSession, this.getLiteSender(), punishment3, punishmentRequest3.getStackIndex(), text2);
         }

         CommandTask commandTask3 = this;
         String text3 = ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).obtainKotlinData().computeText(punishment2.computeText(this.getPunishmentRequest()));
         if (text3.length() > 0 && (syncMessageType == SyncMessageType.BROADCAST || syncMessageType == SyncMessageType.BROADCAST_SILENT)) {
            DiscordWebhook.KOTLIN_DATA.handleDatabaseSession(databaseSession, this.getPunishmentRequest(), text3);
         }

         return Unit.UNIT;
      });
   }

}
