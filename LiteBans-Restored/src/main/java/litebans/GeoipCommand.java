package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public final class GeoipCommand extends AbstractCommand {
   public GeoipCommand(@NotNull Platform platform) {
      super("geoip", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         CommandTask commandTask = this;
         boolean isGeoipEnable2 = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isGeoipEnable();
         CharSequence message = Message.ERROR_DISABLED;
         boolean flag = !isGeoipEnable2;
         if (flag) {
            commandOutput.returnResult(message);
            throw new CommandAbortException();
         }

         CommandTask commandTask2 = this;
         String name = AltAccountPunisher.resolveName(commandTask2, (String)ArraysKt.first(texts));
         LiteSender targetSender = AltAccountPunisher.findTargetSender(this, name);
         String text = null;
         CharSequence message2 = Message.MESSAGE_4;
         if (targetSender != null) {
            name = targetSender.getName();
            text = targetSender.getIpAddress();
            CommandTask commandTask3 = this;
            DatabaseModule module = (DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class);

            try {
               DatabaseSession session = module.openSession();
               Throwable error = null;

               try {
                  message2 = session.replacePlaceholders(message2, targetSender);
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
               if (!module.isConnectionFailure(sqlException)) {
                  throw sqlException;
               }
            }
         } else {
            String text2 = name;
            if (TargetParser.TARGET_PARSER.checkText(text2)) {
               text = name;
            } else {
               CommandTask commandTask4 = this;
               DatabaseModule module2 = (DatabaseModule)commandTask4.getPlatform().getModule(DatabaseModule.class);

               try {
                  DatabaseSession session2 = module2.openSession();
                  Throwable error2 = null;

                  try {
                     label186: {
                        LoginRecord loginRecord = session2.findLoginRecord(name);
                        if (loginRecord != null) {
                           String ip = loginRecord.getIp();
                           if (ip != null ? !StringsKt.startsWithWithDefaults(ip, '#', false, 2, null) : true) {
                              text = loginRecord.getIp();
                              ip = loginRecord.getName();
                              Intrinsics.checkNotNull(ip);
                              name = ip;
                              message2 = PunishmentQueries.replacePlaceholdersWithDefaults(session2, message2, loginRecord, false, 2, null);
                              break label186;
                           }
                        }

                        throw new EmptyMarker();
                     }
                  } catch (Throwable e4) {
                     error2 = e4;
                     throw SneakyThrow.<RuntimeException>rethrow(e4);
                  } finally {
                     CloseableKt.handleCloseable(session2, error2);
                  }
               } catch (Throwable e5) {
                  if (!(e5 instanceof SQLException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e5);
                  }
                  SQLException sqlException2 = (SQLException) e5;
                  if (!module2.isConnectionFailure(sqlException2)) {
                     throw sqlException2;
                  }
               }
            }
         }

         if (text == null) {
            throw new EmptyMarker();
         }

         CommandTask commandTask5 = this;
         String country = ((ConfigModule)commandTask5.getPlatform().getModule(ConfigModule.class)).getCountry(text);
         CharSequence message3 = Intrinsics.areEqual(country, "geoip.unavailable") ? Message.ERROR_UNAVAILABLE : (Intrinsics.areEqual(country, "geoip.error") ? Message.ERROR_NOT_FOUND : message2);
         CharSequence[] messages = new CharSequence[]{"target", name};
         message3 = TextUtil.replacePairs(message3, messages);
         CharSequence[] messages2 = new CharSequence[]{"result", country};
         String text3 = TextUtil.replacePairs(message3, messages2);
         CommandTask.COMMAND_OUTPUT.returnResult(text3);
         throw new CommandAbortException();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
