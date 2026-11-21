package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.jetbrains.annotations.NotNull;

public final class BanManagerV4Import extends ImportSource {
   public BanManagerV4Import(@NotNull Platform platform) {
      super(platform, "BanManagerV4", "bm_", 0, 0L, 24, null);
   }

   @Override
   public boolean isSource(@NotNull String text) {
      return super.isSource(text) || StringsKt.checkText(text, "banmanager4", true);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      try {
         PreparedStatement prepareStatement2 = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "bans");
         Throwable error = null;

         try {
            if (prepareStatement2.execute()) {
               ResultSet resultSet = prepareStatement2.getResultSet();
               Throwable error2 = null;

               try {

                  while (resultSet.next()) {
                     String string = resultSet.getString("banned");
                     String string2 = resultSet.getString("banned_by");
                     String string3 = resultSet.getString("ban_reason");
                     long value = resultSet.getLong("ban_time") * 1000L;
                     long value2 = resultSet.getLong("ban_expires_on") * 1000L;
                     String text2 = this.computeText(string);
                     PunishmentType punishmentType = PunishmentType.BAN;
                     Intrinsics.checkNotNull(string3);
                     Punishment punishment = new Punishment(
                        punishmentType, text2, null, string3, this.computeText(string2), string2, ServerScopes.SERVER_SCOPE.getLocalScope(), null, value, value2, (byte)0, false, false, false, 0L, 31744, null
                     );
                     if (PunishmentQueries.findPunishmentWithDefaults(databaseSession, text2, null, (PunishmentType)null, false, false, 30, null) == null) {
                        databaseSession.insertPunishment(punishment);
                        this.obtainAtomicLong().incrementAndGet();
                     } else {
                        this.getLogger().warning("Ignoring duplicate ban for " + string);
                     }

                     if (text2 != null) {
                        Platform platform = this.getPlatform();
                        Intrinsics.checkNotNull(string);
                        new QueueCommandTask(platform, string, text2, "#").run();
                     }
                  }

               } catch (Throwable e2) {
                  error2 = e2;
                  throw SneakyThrow.<RuntimeException>rethrow(e2);
               } finally {
                  KotlinUtil15.handleAutoCloseable(resultSet, error2);
               }
            }

            if (flag) {
               this.getLogger().info("Importing IP-bans...");
               PreparedStatement prepareStatement3 = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "ip_bans");
               Throwable error3 = null;

               try {
                  if (prepareStatement3.execute()) {
                     ResultSet resultSet2 = prepareStatement3.getResultSet();
                     Throwable error4 = null;

                     try {

                        while (resultSet2.next()) {
                           String string4 = resultSet2.getString("banned");
                           String string5 = resultSet2.getString("banned_by");
                           String string6 = resultSet2.getString("ban_reason");
                           long long2 = resultSet2.getLong("ban_time");
                           long value3 = long2 * 1000L;
                           long long3 = resultSet2.getLong("ban_expires_on");
                           long value4 = long3 * 1000L;
                           PunishmentType punishmentType2 = PunishmentType.BAN;
                           Intrinsics.checkNotNull(string6);
                           Punishment punishment2 = new Punishment(
                              punishmentType2,
                              null,
                              string4,
                              string6,
                              this.computeText(string5),
                              string5,
                              ServerScopes.SERVER_SCOPE.getLocalScope(),
                              null,
                              value3,
                              value4,
                              (byte)0,
                              false,
                              true,
                              false,
                              0L,
                              27648,
                              null
                           );
                           this.handleDatabaseSession(databaseSession, punishment2, this.obtainAtomicLong(), this.obtainAtomicLong2());
                        }

                     } catch (Throwable e3) {
                        error4 = e3;
                        throw SneakyThrow.<RuntimeException>rethrow(e3);
                     } finally {
                        KotlinUtil15.handleAutoCloseable(resultSet2, error4);
                     }
                  }

               } catch (Throwable e4) {
                  error3 = e4;
                  throw SneakyThrow.<RuntimeException>rethrow(e4);
               } finally {
                  KotlinUtil15.handleAutoCloseable(prepareStatement3, error3);
               }
            }

         } catch (Throwable e5) {
            error = e5;
            throw SneakyThrow.<RuntimeException>rethrow(e5);
         } finally {
            KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
