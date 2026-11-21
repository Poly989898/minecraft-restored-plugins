package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WarningsCommand extends AbstractCommand implements LiteAInterface3 {
   public WarningsCommand(@NotNull Platform platform) {
      super("warnings", platform);
   }

   @Override
   public String getPermission() {
      return "litebans.warnings.self";
   }

   @Override
   public boolean obtainFlag() {
      return false;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         String uuidString = liteSender.getUuidString();
         CommandTask commandTask = this;
         String firstOrNull2 = (String)ArraysKt.firstOrNull(texts);
         if (firstOrNull2 == null) {
            firstOrNull2 = uuidString;
         }

         String name = AltAccountPunisher.resolveName(commandTask, firstOrNull2);
         if (!StringsKt.checkText(name, liteSender.getName(), true) && !StringsKt.checkText(name, uuidString, true)) {
            CommandTask commandTask2 = this;
            String text = "litebans.warnings";
            LiteSender sender = commandTask2.getSender();
            CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
            boolean flag = !sender.hasPermission(text);
            CharSequence message = Message.PERMISSION_ERROR;
            if (flag) {
               commandOutput.returnResult(message);
               throw new CommandAbortException();
            }
         } else {
            CommandTask commandTask3 = this;
            String text2 = "litebans.warnings.self";
            LiteSender sender2 = commandTask3.getSender();
            CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
            boolean flag2 = !sender2.hasPermission(text2);
            CharSequence message2 = Message.PERMISSION_ERROR;
            if (flag2) {
               commandOutput2.returnResult(message2);
               throw new CommandAbortException();
            }

            CommandOutput commandOutput3 = CommandTask.COMMAND_OUTPUT;
            boolean isPlayer2 = liteSender.isPlayer();
            CharSequence message3 = Message.WARNINGS_USAGE;
            boolean flag3 = !isPlayer2;
            if (flag3) {
               commandOutput3.returnResult(message3);
               throw new CommandAbortException();
            }
         }

         String text3 = AltAccountPunisher.getReasonArgumentWithDefaults(this, liteSender, texts, null, 4, null);
         int value = this.computeIntValue(this.getPlatform(), texts, CollectionsKt.emptyList(), 512);
         CommandTask commandTask4 = this;
         DatabaseModule module = (DatabaseModule)commandTask4.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               LoginRecord loginRecord = AltAccountPunisher.parseTargetRecordWithDefaults(session, name, false, 2, null);
               List sorted2 = CollectionsKt.sorted(session.getHistory(loginRecord, PunishmentType.WARN, text3, true, value));
               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (Placeholders.replacePlaceholder(PunishmentQueries.replacePlaceholdersWithDefaults(session, this.obtainMessage(), loginRecord, false, 2, null), "limit", value)));
               Iterable iterable = sorted2;

               for (Object obj : iterable) {
                  Punishment punishment = (Punishment)obj;
                  CharSequence message4 = Message.computeMessage(punishment.getType());
                  Message message5 = punishment.isPermanent() ? Message.ACTIVE_SUFFIX : Message.ACTIVE_SUFFIX_TEMP;
                  if (session.isAffecting(punishment)) {
                     CharSequence message6 = message4;
                     message4 = "" + message6 + message5;
                  }

                  AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, message4, punishment, loginRecord, false, 4, null));
               }

            } catch (Throwable e2) {
               error = e2;
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         } catch (Throwable e3) {
            if (!(e3 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            }
            SQLException sqlException = (SQLException) e3;
            if (!module.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public CharSequence obtainMessage() {
      return Message.START_2;
   }

   @Override
   public int computeIntValue(@NotNull Platform platform, @Nullable String[] texts, @NotNull Collection collection, int value) {
      return LiteHelper8.computeIntValue(this, platform, texts, collection, value);
   }

   @Override
   public void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Collection collection, @Nullable LoginRecord loginRecord, @Nullable String[] texts, @NotNull AbstractCommand abstractCommand, int value) {
      LiteHelper8.handleLiteAInterface(this, databaseSession, collection, loginRecord, texts, abstractCommand, value);
   }

   @Override
   public LoginRecord computeLoginRecord(@NotNull DatabaseSession databaseSession, @NotNull HashMap map, @Nullable String text) {
      return LiteHelper8.computeLoginRecord(this, databaseSession, map, text);
   }
}
