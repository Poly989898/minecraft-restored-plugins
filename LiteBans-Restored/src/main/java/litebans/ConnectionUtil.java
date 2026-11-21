package litebans;

import java.io.Closeable;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public final class ConnectionUtil {
   private ConnectionUtil() {
   }

   public final ThreadLocal obtainThreadLocal() {
      return DateFormats.obtainThreadLocal();
   }

   public final boolean checkDatabaseModule(@NotNull DatabaseModule databaseModule) {
      try {
         Settings settings = databaseModule.getConfigModule().getSettings();
         String sqlAddress = settings.getSqlAddress();
         boolean flag = StringsKt.containsWithDefaults(sqlAddress, ':', false, 2, null) || Intrinsics.areEqual(sqlAddress, "localhost") || Intrinsics.areEqual(sqlAddress, "127.0.0.1");
         if (flag) {
            File dataFolder = databaseModule.platform.getDataFolder();
            String text = "litebans.sqlite";
            sqlAddress = new File(dataFolder, text).getPath();
         }

         File file = new File(sqlAddress);
         if (file.exists()) {
            PluginModule pluginModule = databaseModule;
            String text2 = "Converting SQLite database to H2 database...";
            pluginModule.platform.getLogger().info(text2);
            DatabaseModule databaseModule2 = new DatabaseModule(databaseModule.platform);
            databaseModule2.setConvertSqlite(false);
            databaseModule2.enable();
            settings.setSqlDriver("h2");
            databaseModule.getBroadcastModule().setDatabaseType("h2");
            databaseModule.initialize(databaseModule.getConfigModule());
            DatabaseModule.connectWithDefaults(databaseModule, 0L, false, false, false, 15, null);
            DatabaseModule databaseModule3 = databaseModule;

            try {
               DatabaseSession session = databaseModule3.openSession();
               Throwable error = null;

               try {
                  var rawConnection = databaseModule2.getRawConnection();
                  Throwable error2 = null;

                  try {
                     Connection connection = (Connection)rawConnection;
                     if (connection != null) {
                        PunishmentTable[] punishmentTables = PunishmentTable.values();
                        int value = 0;

                        for (int i = punishmentTables.length; value < i; value++) {
                           PunishmentTable punishmentTable = punishmentTables[value];
                           session.handleConnection(connection, punishmentTable, true);
                        }

                        long totalEntryCount = session.getTotalEntryCount();
                        LiteSender console = session.getPlatform().getConsole();
                        CharSequence message = Message.IMPORT_FINISH_LITEBANS.computeText("amount", totalEntryCount);
                        CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, console, message, null, 4, null);
                        connection.close();
                        databaseModule2.disable();
                        if (file.exists()) {
                           File parentFile = file.getParentFile();
                           String text3 = file.getName() + ".backup";
                           File file2 = new File(parentFile, text3);
                           if (file.renameTo(file2)) {
                              PluginModule pluginModule2 = databaseModule;
                              String text4 = "Renamed SQLite database: " + file.getName() + " => " + file2.getName();
                              pluginModule2.platform.getLogger().info(text4);
                           }
                        }

                        PluginModule pluginModule3 = databaseModule;
                        String text5 = "SQLite -> H2 conversion completed successfully.";
                        pluginModule3.platform.getLogger().info(text5);
                     }
                  } catch (Throwable e2) {
                     error2 = e2;
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  } finally {
                     KotlinUtil15.handleAutoCloseable(rawConnection, error2);
                  }

               } catch (Throwable e3) {
                  error = e3;
                  throw SneakyThrow.<RuntimeException>rethrow(e3);
               } finally {
                  CloseableKt.handleCloseable(session, error);
               }
            } catch (Throwable e4) {
               if (!(e4 instanceof SQLException)) {
                  throw SneakyThrow.<RuntimeException>rethrow(e4);
               }
               SQLException sqlException = (SQLException) e4;
               if (!databaseModule3.isConnectionFailure(sqlException)) {
                  throw sqlException;
               }
            }

            return true;
         } else {
            settings.setSqlDriver("h2");
            databaseModule.getBroadcastModule().setDatabaseType("h2");
            return false;
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public ConnectionUtil(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
