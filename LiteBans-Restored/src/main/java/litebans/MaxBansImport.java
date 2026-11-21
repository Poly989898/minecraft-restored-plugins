package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;

public final class MaxBansImport extends ImportSource {
   public MaxBansImport(@NotNull Platform platform) {
      super(platform, "MaxBans", null, 0, 0L, 28, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      this.handleConnection(connection, databaseSession);
      this.handleFlag(flag, connection, databaseSession);
   }

   private final void handleConnection(Connection connection, DatabaseSession databaseSession) {
      try {
         int value = 0;

         while (true) {
            var prepareStatement2 = connection.prepareStatement("SELECT * FROM bans ORDER BY time LIMIT " + this.getBatchSize() + " OFFSET " + value);
            Throwable error = null;

            try {
               PreparedStatement preparedStatement = (PreparedStatement)prepareStatement2;
               Intrinsics.checkNotNull(preparedStatement);
               if (this.computeIntValue(preparedStatement, databaseSession) == 0) {
                  return;
               }

            } catch (Throwable e2) {
               error = e2;
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            } finally {
               KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
            }

            value += this.getBatchSize();
            Thread.sleep(this.getBatchDelayMillis());
         }
      } catch (InterruptedException | SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleFlag(boolean flag, Connection connection, DatabaseSession databaseSession) {
      try {
         if (flag) {
            var prepareStatement2 = connection.prepareStatement("SELECT * FROM ipbans");
            Throwable error = null;

            try {
               PreparedStatement preparedStatement = (PreparedStatement)prepareStatement2;
               if (preparedStatement.execute()) {
                  ResultSet resultSet = preparedStatement.getResultSet();
                  Throwable error2 = null;

                  try {

                     while (resultSet.next()) {
                        ImportSource.importPunishmentWithDefaults(
                           this,
                           databaseSession,
                           null,
                           resultSet.getString("reason"),
                           resultSet.getLong("time"),
                           resultSet.getLong("expires"),
                           resultSet.getString("banner"),
                           null,
                           null,
                           resultSet.getString("ip"),
                           null,
                           null,
                           1728,
                           null
                        );
                     }

                  } catch (Throwable e2) {
                     error2 = e2;
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  } finally {
                     KotlinUtil15.handleAutoCloseable(resultSet, error2);
                  }
               }

            } catch (Throwable e3) {
               error = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final int computeIntValue(PreparedStatement preparedStatement, DatabaseSession databaseSession) {
      try {
         int value = 0;
         if (preparedStatement.execute()) {
            ResultSet resultSet = preparedStatement.getResultSet();
            Throwable error = null;

            try {

               while (resultSet.next()) {
                  value++;

                  try {
                     ImportSource.importPunishmentWithDefaults(
                        this,
                        databaseSession,
                        resultSet.getString("name"),
                        resultSet.getString("reason"),
                        resultSet.getLong("time"),
                        resultSet.getLong("expires"),
                        resultSet.getString("banner"),
                        null,
                        null,
                        null,
                        null,
                        null,
                        1984,
                        null
                     );
                  } catch (Exception e2) {
                     this.getLogger().warning("Import failed for ban on '" + resultSet.getString("name") + "'; name changed, perhaps?");
                     CommandTask commandTask = this;
                     ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e2);
                  }
               }

            } catch (Throwable e3) {
               error = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               KotlinUtil15.handleAutoCloseable(resultSet, error);
            }
         }

         return value;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
