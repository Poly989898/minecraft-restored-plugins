package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import org.jetbrains.annotations.NotNull;

public final class BanHammerImport extends ImportSource {
   public BanHammerImport(@NotNull Platform platform) {
      super(platform, "BanHammer", "banhammer_", 0, 0L, 24, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      try {
         PreparedStatement prepareStatement2 = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "bans WHERE state=0");
         Throwable error = null;

         try {
            if (prepareStatement2.execute()) {
               ResultSet resultSet = prepareStatement2.getResultSet();
               Throwable error2 = null;

               try {

                  while (resultSet.next()) {
                     PreparedStatement prepareStatement3 = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "players WHERE id=?");
                     Throwable error3 = null;

                     try {
                        prepareStatement3.setInt(1, resultSet.getInt("player_id"));
                        prepareStatement3.execute();
                        ResultSet resultSet2 = prepareStatement3.getResultSet();
                        if (resultSet2 != null && resultSet2.next()) {
                           String string = resultSet2.getString("name");
                           String string2 = resultSet.getString("reason");
                           CommandTask commandTask = this;
                           BroadcastModule module = (BroadcastModule)commandTask.getPlatform().getModule(BroadcastModule.class);
                           Intrinsics.checkNotNull(resultSet);
                           CommandTask commandTask2 = this;
                           Date date = module.computeDate(resultSet, "created_at", false, ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getSqlDriver());
                           long time = date.getTime();
                           CommandTask commandTask3 = this;
                           module = (BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class);
                           CommandTask commandTask4 = this;
                           Date date2 = module.computeDate(resultSet, "expires_at", false, ((ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class)).getSettings().getSqlDriver());
                           long time2 = date2.getTime();
                           ImportSource punishCommandBase = this;
                           Intrinsics.checkNotNull(string2);
                           ImportSource.importPunishmentWithDefaults(punishCommandBase, databaseSession, string, string2, time, time2, null, null, null, null, null, null, 1984, null);
                        }

                     } catch (Throwable e2) {
                        error3 = e2;
                        throw SneakyThrow.<RuntimeException>rethrow(e2);
                     } finally {
                        KotlinUtil15.handleAutoCloseable(prepareStatement3, error3);
                     }
                  }

               } catch (Throwable e3) {
                  error2 = e3;
                  throw SneakyThrow.<RuntimeException>rethrow(e3);
               } finally {
                  KotlinUtil15.handleAutoCloseable(resultSet, error2);
               }
            }

         } catch (Throwable e4) {
            error = e4;
            throw SneakyThrow.<RuntimeException>rethrow(e4);
         } finally {
            KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
