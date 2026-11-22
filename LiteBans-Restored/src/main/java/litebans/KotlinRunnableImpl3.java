package litebans;

import java.io.Closeable;
import java.sql.SQLException;

public final class KotlinRunnableImpl3 implements Runnable {
   final DatabaseModule databaseModule;

   public KotlinRunnableImpl3(DatabaseModule databaseModule) {
      this.databaseModule = databaseModule;
   }

   @Override
   public final void run() {
      try {
         DatabaseModule databaseModule = this.databaseModule;

         try {
            DatabaseSession session = databaseModule.openSession();
            Throwable error = null;

            try {
               databaseModule.updateServerRecord(session, true);
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
            if (!databaseModule.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }

         PluginModule pluginModule = this.databaseModule;
         String text = "Server information reset complete.";
         pluginModule.platform.getLogger().severe(text);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
