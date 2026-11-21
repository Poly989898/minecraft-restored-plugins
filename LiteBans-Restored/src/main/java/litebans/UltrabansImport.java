package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;

public final class UltrabansImport extends ImportSource {
   public UltrabansImport(@NotNull Platform platform) {
      super(platform, "Ultrabans", null, 0, 0L, 28, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      try {
         var prepareStatement2 = connection.prepareStatement("SELECT * FROM banlist WHERE type=0");
         Throwable error = null;

         try {
            PreparedStatement preparedStatement = (PreparedStatement)prepareStatement2;
            if (preparedStatement.execute()) {
               ResultSet resultSet = preparedStatement.getResultSet();
               Throwable error2 = null;

               try {

                  while (resultSet.next()) {
                     String string = resultSet.getString("name");
                     String string2 = resultSet.getString("reason");
                     String string3 = resultSet.getString("admin");
                     long value = resultSet.getLong("time") * 1000L;
                     long value2 = resultSet.getLong("temptime") * 1000L;
                     ImportSource punishCommandBase = this;
                     Intrinsics.checkNotNull(string2);
                     ImportSource.importPunishmentWithDefaults(punishCommandBase, databaseSession, string, string2, value, value2, string3, null, null, null, null, null, 1984, null);
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

         if (flag) {
            CommandTask commandTask = this;
            long currentTime = DatabaseModule.getCurrentTimeWithDefaults((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class), false, 1, null);
            var prepareStatement3 = connection.prepareStatement("SELECT * FROM banlistip");
            Throwable error3 = null;

            try {
               PreparedStatement preparedStatement2 = (PreparedStatement)prepareStatement3;
               if (preparedStatement2.execute()) {
                  ResultSet resultSet2 = preparedStatement2.getResultSet();
                  Throwable error4 = null;

                  try {

                     while (resultSet2.next()) {
                        String string4 = resultSet2.getString("name");
                        String string5 = resultSet2.getString("lastip");
                        long value3 = -1L;
                        String text2 = "#imported#";
                        String text3 = this.computeText(string4);
                        Punishment punishment = new Punishment(
                           PunishmentType.BAN, text3, string5, "#", this.computeText(text2), text2, ServerScopes.SERVER_SCOPE.getLocalScope(), null, currentTime, value3, (byte)0, false, true, false, 0L, 27648, null
                        );
                        this.handleDatabaseSession(databaseSession, punishment, this.obtainAtomicLong(), this.obtainAtomicLong2());
                        if (text3 != null) {
                           Platform platform = this.getPlatform();
                           Intrinsics.checkNotNull(string4);
                           new QueueCommandTask(platform, string4, text3, "#").run();
                        }
                     }

                  } catch (Throwable e4) {
                     error4 = e4;
                     throw SneakyThrow.<RuntimeException>rethrow(e4);
                  } finally {
                     KotlinUtil15.handleAutoCloseable(resultSet2, error4);
                  }
               }

            } catch (Throwable e5) {
               error3 = e5;
               throw SneakyThrow.<RuntimeException>rethrow(e5);
            } finally {
               KotlinUtil15.handleAutoCloseable(prepareStatement3, error3);
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
