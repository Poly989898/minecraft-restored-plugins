package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public final class StaffRollbackCommand extends AbstractCommand {
   public StaffRollbackCommand(@NotNull Platform platform) {
      super("staffrollback", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isSecurityStaffrollback()) {
            CommandTask commandTask2 = this;
            CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
            boolean isConsole2 = commandTask2.getSender().isConsole();
            CharSequence message = Message.ERROR_CONSOLE_ONLY;
            boolean flag = !isConsole2;
            if (flag) {
               commandOutput.returnResult(message);
               throw new CommandAbortException();
            }
         }

         CommandTask commandTask3 = this;
         String name = AltAccountPunisher.resolveName(commandTask3, (String)ArraysKt.first(texts));
         String text = AltAccountPunisher.getReasonArgumentWithDefaults(this, liteSender, texts, null, 4, null);
         CommandTask commandTask4 = this;
         DatabaseModule module = (DatabaseModule)commandTask4.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               LoginRecord loginRecord = AltAccountPunisher.parseTargetRecordWithDefaults(session, name, false, 2, null);
               StringBuilder stringBuilder = new StringBuilder();
               String[] texts2 = texts;
               byte b = 1;
               String text2;
               if (b < texts2.length) {
                  text2 = texts2[b];
               } else {
                  StringBuilder builder = stringBuilder;
                  text2 = "";
                  stringBuilder = builder;
               }

               stringBuilder = stringBuilder.append(text2);
               String[] texts3 = texts;
               byte b2 = 2;
               if (b2 < texts3.length) {
                  text2 = texts3[b2];
               } else {
                  StringBuilder builder2 = stringBuilder;
                  text2 = "";
                  stringBuilder = builder2;
               }

               String string = stringBuilder.append(text2).toString();
               double duration = DurationUnit.DURATIONS.parseDuration(string, Double.MIN_VALUE);
               CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
               boolean flag2 = duration == Double.MIN_VALUE && !StringsKt.isBlank(string);
               CharSequence message2 = Message.ERROR_NO_DURATION;
               if (flag2) {
                  commandOutput2.returnResult(message2);
                  throw new CommandAbortException();
               }

               String uuid = loginRecord.getUuid();
               if (uuid == null) {
                  CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NO_LOGINRECORD);
                  throw new CommandAbortException();
               }

               long value = duration == Double.MIN_VALUE ? 0L : (long)(DatabaseModule.getCurrentTimeWithDefaults(session.getDatabaseModule(), false, 1, null) - duration);
               long value2 = session.computeLongValue(uuid, value, text);
               AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.MESSAGE_2.computeText("amount", value2), loginRecord, false, 2, null));
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
}
