package litebans;

import java.sql.SQLException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class LibertyBansImport extends ImportSource {
   private final byte[] bytes7 = KotlinUtil22.computeBytes(LiteSender.uuid3);

   public LibertyBansImport(@NotNull Platform platform) {
      super(platform, "LibertyBans", "libertybans_", 0, 0L, 24, null);
   }

   public final byte[] getBytes7() {
      return this.bytes7;
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      handleKotlinPunishCommandBaseSub(this, connection, flag, databaseSession, PunishmentType.BAN, null, 16, null);
      handleKotlinPunishCommandBaseSub(this, connection, flag, databaseSession, PunishmentType.MUTE, null, 16, null);
      this.handleConnection(connection, flag, databaseSession, PunishmentType.WARN, "warns");
   }

   private final void handleConnection(Connection connection, boolean flag, DatabaseSession databaseSession, PunishmentType punishmentType, String text) {
      try {
         String text2 = "SELECT * FROM " + this.getTablePrefix() + "applicable_" + text;
         CommandTask commandTask = this;
         ConfigModule module = (ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class);
         if (module.isVerbose()) {
            module.logDebug(text2);
         }

         PreparedStatement prepareStatement2 = connection.prepareStatement(text2);
         Throwable error = null;

         try {
            if (prepareStatement2.execute()) {
               ResultSet resultSet = prepareStatement2.getResultSet();
               Throwable error2 = null;

               try {

                  while (resultSet.next()) {
                     int int2 = resultSet.getInt("victim_type");
                     Intrinsics.checkNotNull(resultSet);
                     byte[] bytes = resultSet.getBytes("victim_uuid");
                     UUID uuid = null;
                     if (bytes != null && bytes.length == 16) {
                        Intrinsics.checkNotNull(bytes);
                        uuid = KotlinUtil22.computeUuid(bytes);
                     }

                     if (uuid == null || Intrinsics.areEqual(uuid, KotlinUtil11.obtainUuid())) {
                        byte[] bytes2 = resultSet.getBytes("uuid");
                        if (bytes2 != null && bytes2.length == 16) {
                           Intrinsics.checkNotNull(bytes2);
                           uuid = KotlinUtil22.computeUuid(bytes2);
                        }
                     }

                     CommandTask commandTask2 = this;
                     ConfigModule module2 = (ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class);
                     if (module2.isVerbose()) {
                        module2.logDebug("victim: " + uuid);
                     }

                     UUID uuid2 = uuid;
                     byte[] bytes3 = resultSet.getBytes("operator");
                     byte[] bytes4 = bytes3;
                     if (bytes4 == null || bytes4.length != 16) {
                        bytes3 = this.getBytes7();
                     }

                     byte[] bytes5 = bytes3;
                     Intrinsics.checkNotNull(bytes5);
                     UUID uuid3 = KotlinUtil22.computeUuid(bytes5);
                     String text3 = null;
                     if (flag) {
                        String text4 = null;
                        if (int2 != 0) {
                           byte[] bytes6 = resultSet.getBytes("address");
                           if (bytes6.length >= 4) {
                              Intrinsics.checkNotNull(bytes6);
                              text4 = InetAddress.getByAddress(bytes6).getHostAddress().toString();
                           }
                        }

                        text3 = text4;
                     }

                     String string = resultSet.getString("reason");
                     long value = resultSet.getLong("start") * 1000;
                     long value2 = resultSet.getLong("end") * 1000;
                     String text5;
                     if (uuid2 == null) {
                        text5 = null;
                     } else {
                        label396: {
                           PreparedStatement prepareStatement3 = connection.prepareStatement("SELECT name FROM " + this.getTablePrefix() + "latest_names WHERE uuid=?");
                           Throwable error3 = null;

                           label338: {
                              String text6;
                              try {
                                 prepareStatement3.setBytes(1, KotlinUtil22.computeBytes(uuid2));
                                 prepareStatement3.execute();
                                 ResultSet resultSet2 = prepareStatement3.getResultSet();
                                 if (resultSet2 == null || !resultSet2.next()) {
                                    break label338;
                                 }

                                 text6 = resultSet2.getString(1);
                              } catch (Throwable e2) {
                                 error3 = e2;
                                 throw SneakyThrow.<RuntimeException>rethrow(e2);
                              } finally {
                                 KotlinUtil15.handleAutoCloseable(prepareStatement3, error3);
                              }

                              text5 = text6;
                              break label396;
                           }

                           text5 = null;
                        }
                     }

                     String text7 = text5;
                     Intrinsics.checkNotNull(string);
                     String text8 = this.computeText(uuid3);
                     String text9 = text3;
                     ImportSource.importPunishmentWithDefaults(this, databaseSession, text7, string, value, value2, text8, uuid2, uuid3, text9, null, punishmentType, 512, null);
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

   static void handleKotlinPunishCommandBaseSub(LibertyBansImport kotlinPunishCommandBaseSub, Connection connection, boolean flag, DatabaseSession databaseSession, PunishmentType punishmentType, String text, int value, Object obj) {
      if ((value & 16) != 0) {
         String text2 = punishmentType.getPunishmentTable().name();
         text = text2.toLowerCase(Locale.ENGLISH);
      }

      kotlinPunishCommandBaseSub.handleConnection(connection, flag, databaseSession, punishmentType, text);
   }
}
