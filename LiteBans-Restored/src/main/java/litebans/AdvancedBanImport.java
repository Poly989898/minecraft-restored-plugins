package litebans;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class AdvancedBanImport extends ImportSource {
   public AdvancedBanImport(@NotNull Platform platform) {
      super(platform, "AdvancedBan", null, 0, 0L, 28, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      this.handleText("ban", PunishmentType.BAN, connection, databaseSession);
      this.handleText("mute", PunishmentType.MUTE, connection, databaseSession);
   }

   private final void handleText(String text, PunishmentType punishmentType, Connection connection, DatabaseSession databaseSession) {
      try {
         String text2 = StringsKt.computeText(text, Locale.ENGLISH);
         String text3 = "WHERE punishmentType='"
            + text2
            + "' OR punishmentType='Temp"
            + text
            + "' OR punishmentType='TEMP_"
            + text2
            + "' OR punishmentType='Ip"
            + text
            + "' OR punishmentType='IP_"
            + text2
            + "' OR punishmentType='Tempip"
            + text
            + "' OR punishmentType='TEMP_IP_"
            + text2
            + '\'';
         handleKotlinPunishCommandBaseSub(this, connection, "SELECT * FROM Punishments " + text3, text, databaseSession, punishmentType, false, 32, null);
         this.handleConnection(connection, "SELECT * FROM PunishmentHistory " + text3, text, databaseSession, punishmentType, false);
      } catch (Exception e) {
         CommandTask commandTask = this;
         ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e);
      }
   }

   private final void handleConnection(Connection connection, String text, String text2, DatabaseSession databaseSession, PunishmentType punishmentType, boolean flag) {
      try {
         PreparedStatement prepareStatement2 = connection.prepareStatement(text);
         Throwable error = null;

         try {
            ResultSet executeQuery2 = prepareStatement2.executeQuery();
            Throwable error2 = null;

            try {

               while (executeQuery2.next()) {
                  String string = executeQuery2.getString("name");
                  String text3 = UsernameValidator.KOTLIN_DATA.computeText3(executeQuery2.getString("uuid"));
                  String string2 = executeQuery2.getString("operator");
                  String string3 = executeQuery2.getString("reason");
                  String string4 = executeQuery2.getString("punishmentType");
                  Intrinsics.checkNotNull(string4);
                  String lowerCase = string4.toLowerCase(Locale.ENGLISH);
                  String text4 = null;
                  boolean contains = StringsKt.containsWithDefaults((CharSequence)lowerCase, (CharSequence)("ip" + text2), false, 2, null);
                  String text5 = text3;
                  if (TargetParser.TARGET_PARSER.checkText(text5) && !contains) {
                     this.getLogger().warning("IP was stored in UUID column for " + string4 + '!');
                     contains = true;
                  }

                  if (contains) {
                     text4 = text3;
                     text3 = this.computeText(string);
                  }

                  String text6 = text3;
                  if (!TargetParser.isUuid(text6)) {
                     this.getLogger().warning("Username ('" + text3 + "') was stored in UUID column for " + string4 + '!');
                     text3 = this.computeText(text3);
                  }

                  String text7 = text3;
                  if (!TargetParser.isUuid(text7)) {
                     this.getLogger().warning("Failed to import " + string4 + " for " + text3);
                     text3 = null;
                  }

                  long long2 = executeQuery2.getLong("start");
                  long value = Math.max(executeQuery2.getLong("end"), 0L);
                  CommandTask commandTask = this;
                  String text8 = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName();
                  CommandTask commandTask2 = this;
                  String scope = ServerScopes.SERVER_SCOPE.resolveScope(((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getDefaultServerScope(), text8, this.getPlatform().getConsole());
                  if (flag && PunishmentQueries.findPunishmentWithDefaults(databaseSession, text3, null, (PunishmentType)null, false, false, 30, null) != null) {
                     this.getLogger().warning("Ignoring duplicate " + text2 + " for " + text3);
                  } else {
                     Intrinsics.checkNotNull(string3);
                     Punishment punishment = new Punishment(
                        punishmentType, text3, text4, string3, this.computeText(string2), string2, scope, text8, long2, value, (byte)0, false, contains, flag, 0L, 19456, null
                     );
                     databaseSession.insertPunishment(punishment);
                     if (punishmentType == PunishmentType.BAN) {
                        this.obtainAtomicLong().incrementAndGet();
                     }
                  }

                  if (text3 != null) {
                     Platform platform = this.getPlatform();
                     Intrinsics.checkNotNull(string);
                     new QueueCommandTask(platform, string, text3, text4).run();
                  }
               }

            } catch (Throwable e2) {
               error2 = e2;
               throw SneakyThrow.<RuntimeException>rethrow(e2);
            } finally {
               KotlinUtil15.handleAutoCloseable(executeQuery2, error2);
            }

         } catch (Throwable e3) {
            error = e3;
            throw SneakyThrow.<RuntimeException>rethrow(e3);
         } finally {
            KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   static void handleKotlinPunishCommandBaseSub(AdvancedBanImport kotlinPunishCommandBaseSub, Connection connection, String text, String text2, DatabaseSession databaseSession, PunishmentType punishmentType, boolean flag, int value, Object obj) {
      if ((value & 32) != 0) {
         flag = true;
      }

      kotlinPunishCommandBaseSub.handleConnection(connection, text, text2, databaseSession, punishmentType, flag);
   }
}
