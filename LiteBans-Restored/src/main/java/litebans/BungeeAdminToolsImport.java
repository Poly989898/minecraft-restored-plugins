package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class BungeeAdminToolsImport extends ImportSource {
   public BungeeAdminToolsImport(@NotNull Platform platform) {
      super(platform, "BungeeAdminTools", "BAT_", 0, 0L, 24, null);
   }

   @Override
   public boolean isSource(@NotNull String text) {
      return super.isSource(text) || StringsKt.checkText(text, "BAT", true);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      try {
         PreparedStatement prepareStatement2 = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "ban WHERE ban_state=1");
         Throwable error = null;

         try {
            ResultSet executeQuery2 = prepareStatement2.executeQuery();
            Throwable error2 = null;

            try {

               while (executeQuery2.next()) {
                  try {
                     String string = executeQuery2.getString("UUID");
                     String text2 = null;
                     String text3 = null;
                     String string2 = executeQuery2.getString("ban_ip");
                     boolean flag2 = string2 != null && string2.length() > 0;
                     if (string != null) {
                        text2 = UsernameValidator.KOTLIN_DATA.computeText3(string);

                        try {
                           PreparedStatement prepareStatement3 = connection.prepareStatement(
                              "SELECT " + this.getTablePrefix() + "player,lastip FROM " + this.getTablePrefix() + "players WHERE UUID=? ORDER BY lastlogin DESC LIMIT 1"
                           );
                           Throwable error3 = null;

                           try {
                              prepareStatement3.setString(1, string);
                              ResultSet executeQuery3 = prepareStatement3.executeQuery();
                              Throwable error4 = null;

                              try {
                                 if (executeQuery3.next()) {
                                    text3 = executeQuery3.getString(this.getTablePrefix() + "player");
                                    string2 = executeQuery3.getString("lastip");
                                 }

                              } catch (Throwable e2) {
                                 error4 = e2;
                                 throw SneakyThrow.<RuntimeException>rethrow(e2);
                              } finally {
                                 KotlinUtil15.handleAutoCloseable(executeQuery3, error4);
                              }

                           } catch (Throwable e3) {
                              error3 = e3;
                              throw SneakyThrow.<RuntimeException>rethrow(e3);
                           } finally {
                              KotlinUtil15.handleAutoCloseable(prepareStatement3, error3);
                           }
                        } catch (Exception e4) {
                           CommandTask commandTask = this;
                           ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e4);
                        }
                     }

                     if (text3 == null && text2 != null) {
                        text3 = this.computeText(UUID.fromString(text2));
                     }

                     String string3 = executeQuery2.getString("ban_staff");
                     String text4 = null;
                     if (string3 != null) {
                        try {
                           PreparedStatement prepareStatement4 = connection.prepareStatement(
                              "SELECT UUID FROM " + this.getTablePrefix() + "players WHERE " + this.getTablePrefix() + "player=? ORDER BY lastlogin DESC LIMIT 1"
                           );
                           Throwable error5 = null;

                           try {
                              prepareStatement4.setString(1, string3);
                              ResultSet executeQuery4 = prepareStatement4.executeQuery();
                              Throwable error6 = null;

                              try {
                                 if (executeQuery4.next()) {
                                    text4 = UsernameValidator.KOTLIN_DATA.computeText3(executeQuery4.getString("UUID"));
                                 }

                              } catch (Throwable e5) {
                                 error6 = e5;
                                 throw SneakyThrow.<RuntimeException>rethrow(e5);
                              } finally {
                                 KotlinUtil15.handleAutoCloseable(executeQuery4, error6);
                              }

                           } catch (Throwable e6) {
                              error5 = e6;
                              throw SneakyThrow.<RuntimeException>rethrow(e6);
                           } finally {
                              KotlinUtil15.handleAutoCloseable(prepareStatement4, error5);
                           }
                        } catch (Exception e7) {
                           CommandTask commandTask2 = this;
                           ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e7);
                        }
                     }

                     String string4 = executeQuery2.getString("ban_reason");
                     if (string4 == null) {
                        string4 = "";
                     }

                     String text5 = string4;
                     CommandTask commandTask3 = this;
                     BroadcastModule module = (BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class);
                     Intrinsics.checkNotNull(executeQuery2);
                     CommandTask commandTask4 = this;
                     Date date = module.computeDate(executeQuery2, "ban_begin", false, ((ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class)).getSettings().getSqlDriver());
                     long time = date.getTime();
                     CommandTask commandTask5 = this;
                     BroadcastModule module2 = (BroadcastModule)commandTask5.getPlatform().getModule(BroadcastModule.class);
                     CommandTask commandTask6 = this;
                     Date date2 = module2.computeDate(executeQuery2, "ban_end", false, ((ConfigModule)commandTask6.getPlatform().getModule(ConfigModule.class)).getSettings().getSqlDriver());
                     long time2 = date2.getTime();
                     if (executeQuery2.getTimestamp("ban_end") == null) {
                        time2 = -1L;
                     }

                     if (!flag2 && PunishmentQueries.findPunishmentWithDefaults(databaseSession, text2, null, (PunishmentType)null, false, false, 30, null) != null) {
                        this.getLogger().warning("Ignoring duplicate ban for " + text2);
                     } else {
                        Punishment punishment;
                        CharSequence message = text5;
                        String text6 = text4;
                        if (text4 == null) {
                           text6 = "#";
                        }

                        punishment = new Punishment(
                           PunishmentType.BAN, text2, string2, message, text6, string3, ServerScopes.SERVER_SCOPE.getLocalScope(), null, time, time2, (byte)0, false, flag2, false, 0L, 27648, null
                        );
                        Punishment punishment2 = punishment;
                        databaseSession.insertPunishment(punishment2);
                        Long.valueOf(flag2 ? this.obtainAtomicLong2().incrementAndGet() : this.obtainAtomicLong().incrementAndGet());
                     }

                     if (text3 != null && text2 != null) {
                        new QueueCommandTask(this.getPlatform(), text3, text2, "#").run();
                     }
                  } catch (Exception e8) {
                     CommandTask commandTask7 = this;
                     ((DatabaseModule)commandTask7.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e8);
                  }
               }

            } catch (Throwable e9) {
               error2 = e9;
               throw SneakyThrow.<RuntimeException>rethrow(e9);
            } finally {
               KotlinUtil15.handleAutoCloseable(executeQuery2, error2);
            }

         } catch (Throwable e10) {
            error = e10;
            throw SneakyThrow.<RuntimeException>rethrow(e10);
         } finally {
            KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
