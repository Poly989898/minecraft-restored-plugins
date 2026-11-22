package litebans;

import java.util.Date;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class AllowTask extends CommandTask {
   private final String[] args;
   private final boolean checkAltAccounts;

   public AllowTask(@NotNull String[] texts, @NotNull Platform platform, boolean flag) {
      super(platform);
      this.args = texts;
      this.checkAltAccounts = flag;
   }

   @Override
   public void run() {
      if (this.checkAltAccounts) {
         CommandTask commandTask = this;
         String[] texts = new String[]{"litebans.admin", "litebans.allow"};
         AltAccountPunisher.handleCommandTask(commandTask, texts);
      }

      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean flag = this.args.length < 2;
      CharSequence message = Message.ALLOW_USAGE;
      if (flag) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }

      CommandTask commandTask2 = this;
      ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
         boolean isClosed2 = databaseSession.isClosed();
         CharSequence message2 = Message.ERROR_NO_SQL_CONNECTION;
         if (isClosed2) {
            commandOutput2.returnResult(message2);
            throw new CommandAbortException();
         }

         String orNull = (String)ArraysKt.getOrNull(this.args, 2);
         if (orNull == null) {
            CommandTask.COMMAND_OUTPUT.returnResult(Message.ALLOW_USAGE);
            throw new CommandAbortException();
         }

         String text = orNull;
         CommandOutput commandOutput22 = CommandTask.COMMAND_OUTPUT;
         boolean flag2 = TargetParser.TARGET_PARSER.checkText(text);
         CharSequence message22 = Message.ALLOW_USAGE;
         if (flag2) {
            commandOutput22.returnResult(message22);
            throw new CommandAbortException();
         }

         LoginRecord loginRecord = databaseSession.findLoginRecord(text);
         byte b = TargetParser.TARGET_PARSER.parseTarget(text).getByteValue();
         LoginRecord loginRecord2;
         if ((loginRecord != null ? loginRecord.getUuid() : null) != null) {
            loginRecord2 = loginRecord;
         } else if (b == 1) {
            loginRecord2 = new LoginRecord(text, text, null, null, 12, null);
         } else {
            CommandTask commandTask3 = this;
            if (((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().isOnlineMode()) {
               Profile profile = UsernameValidator.KOTLIN_DATA.computeProfile2(text);
               UUID uuid = profile.getUUID();
               loginRecord2 = PunishmentQueries.saveLoginRecordWithDefaults(databaseSession, new LoginRecord(profile.getName(), uuid != null ? uuid.toString() : null, null, new Date()), false, 2, null);
            } else {
               if (b != 0) {
                  CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NO_UUID_FOUND);
                  throw new CommandAbortException();
               }

               loginRecord2 = PunishmentQueries.saveLoginRecordWithDefaults(databaseSession, new LoginRecord(text, TextUtil.offlineUuid(text), null, null, 12, null), false, 2, null);
            }
         }

         LoginRecord loginRecord3 = loginRecord2;
         CommandOutput commandOutput3 = CommandTask.COMMAND_OUTPUT;
         boolean flag22 = loginRecord3.getUuid() == null;
         CharSequence message3 = Message.ERROR_NO_UUID_FOUND;
         if (flag22) {
            commandOutput3.returnResult(message3);
            throw new CommandAbortException();
         }

         orNull = loginRecord3.getUuid();
         Intrinsics.checkNotNull(orNull);
         UUID fromString2 = UUID.fromString(orNull);
         Intrinsics.checkNotNull(fromString2);
         boolean hasAllowEntry = PunishmentQueries.hasAllowEntryWithDefaults(databaseSession, fromString2, null, 2, null);
         String text2 = this.args[1];
         switch (text2.hashCode()) {
            case -934610812:
               if (text2.equals("remove")) {
                  CommandOutput commandOutput4 = CommandTask.COMMAND_OUTPUT;
                  CharSequence message4 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.ALLOW_ERROR_REMOVE, loginRecord3, false, 2, null);
                  boolean flag3 = !hasAllowEntry;
                  if (flag3) {
                     commandOutput4.returnResult(message4);
                     throw new CommandAbortException();
                  }

                  databaseSession.deleteAllowEntries(fromString2);
                  AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.ALLOW_REMOVED, loginRecord3, false, 2, null));
               }
               break;
            case 96417:
               if (text2.equals("add")) {
                  CommandOutput commandOutput5 = CommandTask.COMMAND_OUTPUT;
                  CharSequence message5 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.ALLOW_ERROR_ADD, loginRecord3, false, 2, null);
                  if (hasAllowEntry) {
                     commandOutput5.returnResult(message5);
                     throw new CommandAbortException();
                  }

                  PunishmentQueries.getAllowEntriesWithDefaults(databaseSession, fromString2, 0, 2, null);
                  AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.ALLOW_ADDED, loginRecord3, false, 2, null));
               }
               break;
            case 94627080:
               if (text2.equals("check")) {
                  Message message6 = Message.computeMessage2("ALLOW_CHECK_" + hasAllowEntry);
                  CommandTask commandTask22 = this;
                  Intrinsics.checkNotNull(message6);
                  AltAccountPunisher.sendMessage(commandTask22, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message6, loginRecord3, false, 2, null));
               }
         }

         return Unit.UNIT;
      });
   }

}
