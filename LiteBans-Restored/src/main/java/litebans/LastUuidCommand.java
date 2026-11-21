package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public final class LastUuidCommand extends AbstractCommand {
   public LastUuidCommand(@NotNull Platform platform) {
      super("lastuuid", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         String name = AltAccountPunisher.resolveName(commandTask, (String)ArraysKt.first(texts));
         CommandTask commandTask2 = this;
         DatabaseModule module = (DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               LoginRecord loginRecord = AltAccountPunisher.parseTargetRecordWithDefaults(session, name, false, 2, null);
               AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.MESSAGE_3, loginRecord, false, 2, null));
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
