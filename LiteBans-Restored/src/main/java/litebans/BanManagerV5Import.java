package litebans;

import java.sql.SQLException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class BanManagerV5Import extends ImportSource {
   public BanManagerV5Import(@NotNull Platform platform) {
      super(platform, "BanManagerV5", "bm_", 0, 0L, 24, null);
   }

   @Override
   public boolean isSource(@NotNull String text) {
      if (!super.isSource(text)) {
         String[] texts = new String[]{"banmanager5", "banmanager7", "banmanagerv7"};
         String text2 = text;
         if (!ArraysKt.contains(texts, text2.toLowerCase(Locale.ENGLISH))) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      boolean endsWith = StringsKt.endsWithWithDefaults(text, '7', false, 2, null);
      this.handleText("bans", PunishmentType.BAN, connection, databaseSession, flag, endsWith);
      this.handleText("mutes", PunishmentType.MUTE, connection, databaseSession, flag, endsWith);
   }

   private final void handleText(String text, PunishmentType punishmentType, Connection connection, DatabaseSession databaseSession, boolean flag, boolean flag2) {
      try {
         PreparedStatement prepareStatement2 = connection.prepareStatement(
            "SELECT * FROM "
               + this.getTablePrefix()
               + "player_"
               + text
               + " INNER JOIN "
               + this.getTablePrefix()
               + "players ON "
               + this.getTablePrefix()
               + "player_"
               + text
               + ".player_id="
               + this.getTablePrefix()
               + "players.id"
         );
         Throwable error = null;

         try {
            if (prepareStatement2.execute()) {
               ResultSet resultSet = prepareStatement2.getResultSet();
               Throwable error2 = null;

               try {

                  while (resultSet.next()) {
                     String string = resultSet.getString("name");
                     UUID uuid = KotlinUtil22.computeUuid(resultSet.getBytes("player_id"));
                     String string2 = resultSet.getString("reason");
                     UUID uuid2 = KotlinUtil22.computeUuid(resultSet.getBytes("actor_id"));
                     long value = resultSet.getLong("created") * 1000L;
                     long value2 = resultSet.getLong("expires") * 1000L;
                     String string3 = uuid.toString();
                     String text2 = this.computeText(uuid2);
                     Intrinsics.checkNotNull(string2);
                     Punishment punishment = new Punishment(
                        punishmentType, string3, null, string2, uuid2.toString(), text2, ServerScopes.SERVER_SCOPE.getLocalScope(), null, value, value2, (byte)0, false, false, false, 0L, 31744, null
                     );
                     if (PunishmentQueries.findPunishmentWithDefaults(databaseSession, string3, null, (PunishmentType)null, false, false, 30, null) == null) {
                        databaseSession.insertPunishment(punishment);
                        if (punishmentType == PunishmentType.BAN) {
                           this.obtainAtomicLong().incrementAndGet();
                        }
                     } else {
                        this.getLogger().warning("Ignoring duplicate ban for " + string3);
                     }

                     Platform platform = this.getPlatform();
                     Intrinsics.checkNotNull(string);
                     new QueueCommandTask(platform, string, string3, "#").run();
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
                           String text3;
                           if (flag2) {
                              byte[] bytes = resultSet2.getBytes("ip");
                              text3 = InetAddress.getByAddress(bytes).getHostAddress().toString();
                           } else {
                              long long2 = resultSet2.getLong("ip");
                              text3 = "" + (long2 >> 24 & 255L) + '.' + (long2 >> 16 & 255L) + '.' + (long2 >> 8 & 255L) + '.' + (long2 & 255L);
                           }

                           String text4 = text3;
                           String string4 = resultSet2.getString("reason");
                           UUID uuid3 = KotlinUtil22.computeUuid(resultSet2.getBytes("actor_id"));
                           long long3 = resultSet2.getLong("created");
                           long value3 = long3 * 1000L;
                           long long4 = resultSet2.getLong("expires");
                           long value4 = long4 * 1000L;
                           String text5 = this.computeText(uuid3);
                           Intrinsics.checkNotNull(string4);
                           Punishment punishment2 = new Punishment(
                              punishmentType, null, text4, string4, uuid3.toString(), text5, ServerScopes.SERVER_SCOPE.getLocalScope(), null, value3, value4, (byte)0, false, true, false, 0L, 27648, null
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
