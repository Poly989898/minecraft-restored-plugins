package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public final class DupeIpCommand extends AbstractCommand {
   public DupeIpCommand(@NotNull Platform platform) {
      super("dupeip", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         String name = AltAccountPunisher.resolveName(commandTask, (String)ArraysKt.first(texts));
         CommandTask commandTask2 = this;
         int value = StringMatching.getInt(texts, 1, ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getNotifyDupeipScanLimit());
         CommandTask commandTask3 = this;
         DatabaseModule module = (DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               DupeIpScanTask punishmentApplyTask = new DupeIpScanTask(name, session, liteSender, false, value).obtainPunishmentApplyTask();
               if (!punishmentApplyTask.isFailed()) {
                  boolean flag;
                  label85: {
                     label101: {
                        if (!liteSender.isConsole()) {
                           CommandTask commandTask4 = this;
                           if (((ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class)).getSettings().isSecurityDupeip()) {
                              break label101;
                           }
                        }

                        LiteSender liteSender2 = liteSender;
                        String text = "litebans.dupeip.viewip";
                        if (liteSender2.hasPermission(text)) {
                           flag = false;
                           break label85;
                        }
                     }

                     flag = true;
                  }

                  boolean flag2 = flag;
                  String text2 = punishmentApplyTask.hasMultipleAddresses() ? Message.DUPEIP_MULTIPLE_ADDRESSES.computeText("num", punishmentApplyTask.getAddressCount()) : punishmentApplyTask.getText43();
                  CharSequence message = flag2 ? Message.START_NO_IP : Message.DUPEIP_START;
                  CharSequence replacePlaceholder2 = Placeholders.replacePlaceholder(message, "ip", text2);
                  LoginRecord loginRecord = punishmentApplyTask.getLoginRecord();
                  Intrinsics.checkNotNull(loginRecord);
                  CharSequence message2 = PunishmentQueries.replacePlaceholdersWithDefaults(session, replacePlaceholder2, loginRecord, false, 2, null);
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, message2, null, 4, null);
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, punishmentApplyTask.getResultText(), null, 4, null);
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, Message.DUPEIP_END, null, 4, null);
               }

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
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
