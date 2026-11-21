package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public final class IpHistoryCommand extends AbstractCommand {
   public IpHistoryCommand(@NotNull Platform platform) {
      super("iphistory", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         CommandTask commandTask = this;
         boolean flag = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isSecurityIphistory() && !liteSender.isConsole();
         CharSequence message = Message.ERROR_CONSOLE_ONLY;
         if (flag) {
            commandOutput.returnResult(message);
            throw new CommandAbortException();
         }

         CommandTask commandTask2 = this;
         String name = AltAccountPunisher.resolveName(commandTask2, (String)ArraysKt.first(texts));
         int value = StringMatching.getInt(texts, 1, 10);
         CommandTask commandTask3 = this;
         DatabaseModule module = (DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               List loginRecordsByTarget = session.findLoginRecordsByTarget(name);
               CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
               boolean flag2 = loginRecordsByTarget.isEmpty();
               CharSequence message2 = Message.ERROR_NO_HISTORY.computeText("target", name);
               if (flag2) {
                  commandOutput2.returnResult(message2);
                  throw new CommandAbortException();
               }

               AltAccountPunisher.sendMessage(this, session.replacePlaceholders(Placeholders.replacePlaceholder(Message.START_5.computeText("target", name), "limit", value)));
               int value2 = Math.min(value, loginRecordsByTarget.size());
               int value3 = 0;

               for (int i = value2; value3 < i; value3++) {
                  LoginRecord loginRecord = (LoginRecord)loginRecordsByTarget.get(value3);
                  String ip = loginRecord.getIp();
                  if (ip != null) {
                     if (StringsKt.startsWithWithDefaults(ip, '#', false, 2, null)) {
                        if (value2 < loginRecordsByTarget.size()) {
                           value2++;
                        }
                     } else {
                        AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (Placeholders.replacePlaceholderLazy(PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.ENTRY.computeText("ip", ip), loginRecord, false, 2, null), "date", () -> {
                           CommandTask commandTask4 = this;
                           return ((BroadcastModule)commandTask4.getPlatform().getModule(BroadcastModule.class)).formatDate(loginRecord.getDate().getTime(), true);
                        })));
                     }
                  }
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

}
