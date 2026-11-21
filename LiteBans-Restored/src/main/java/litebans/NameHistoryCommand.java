package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public final class NameHistoryCommand extends AbstractCommand {
   public NameHistoryCommand(@NotNull Platform platform) {
      super("namehistory", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         String name = AltAccountPunisher.resolveName(commandTask, (String)ArraysKt.first(texts));
         int value = StringMatching.getInt(texts, 1, 10);
         CommandTask commandTask2 = this;
         DatabaseModule module = (DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               List loginRecordsByTarget = session.findLoginRecordsByTarget(name);
               CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
               boolean flag = loginRecordsByTarget.isEmpty();
               CharSequence message = Message.ERROR_NO_HISTORY;
               if (flag) {
                  commandOutput.returnResult(message);
                  throw new CommandAbortException();
               }

               AltAccountPunisher.sendMessage(this, session.replacePlaceholders(Placeholders.replacePlaceholder(Message.START_6.computeText("target", name), "limit", value)));
               Iterable iterable = loginRecordsByTarget;
               int value2 = KotlinRangesKtSub.coerceAtLeast(KotlinStub15.computeIntValue(CollectionsKt.computeIntValue((Iterable)iterable, 10)), 16);
               Map linkedHashMap = new LinkedHashMap(value2);

               for (Object obj : iterable) {
                  LoginRecord loginRecord = (LoginRecord)obj;
                  linkedHashMap.put(loginRecord.getName(), obj);
               }

               Iterable take2 = CollectionsKt.take(linkedHashMap.values(), value);

               for (Object obj2 : take2) {
                  LoginRecord loginRecord2 = (LoginRecord)obj2;
                  AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.ENTRY_2, loginRecord2, false, 2, null));
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
