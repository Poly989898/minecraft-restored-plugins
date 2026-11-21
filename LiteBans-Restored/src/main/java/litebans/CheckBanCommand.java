package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public class CheckBanCommand extends AbstractCommand {
   public CheckBanCommand(@NotNull Platform platform) {
      super("checkban", platform);
   }

   public PunishmentType obtainPunishmentType2() {
      return PunishmentType.BAN;
   }

   public Message obtainMessage3() {
      return Message.BANNED;
   }

   public Message obtainMessage4() {
      return Message.NO_BAN;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         String name = AltAccountPunisher.resolveName(commandTask, (String)ArraysKt.first(texts));
         String text = AltAccountPunisher.getReasonArgumentWithDefaults(this, liteSender, texts, null, 4, null);
         CommandTask commandTask2 = this;
         DatabaseModule module = (DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               label80: {
                  if (TargetParser.TARGET_PARSER.isIpAddress(name)) {
                     Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(session, AltAccountPunisher.computeText(this, name), this.obtainPunishmentType2(), null, false, 12, null);
                     if (punishment != null) {
                        String uuid = punishment.getUuid();
                        if (uuid == null) {
                           uuid = punishment.getIp();
                           if (uuid == null) {
                              CommandTask.COMMAND_OUTPUT.returnResult(this.obtainMessage4());
                              throw new CommandAbortException();
                           }
                        }

                        String text2 = uuid;
                        LoginRecord loginRecord = AltAccountPunisher.parseTargetRecordWithDefaults(session, text2, false, 2, null);
                        AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, this.obtainMessage3(), punishment, loginRecord, false, 4, null));
                        this.handleDatabaseSession(session, punishment, loginRecord);
                        break label80;
                     }
                  }

                  LoginRecord loginRecord2 = AltAccountPunisher.parseTargetRecordWithDefaults(session, name, false, 2, null);
                  Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(session, loginRecord2.getUuid(), loginRecord2.getIp(), this.obtainPunishmentType2(), text, false, false, 48, null);
                  if (punishment2 == null) {
                     CommandTask.COMMAND_OUTPUT.returnResult(PunishmentQueries.replacePlaceholdersWithDefaults(session, this.obtainMessage4(), loginRecord2, false, 2, null));
                     throw new CommandAbortException();
                  }

                  AltAccountPunisher.sendMessage(this, PunishmentQueries.replacePlaceholdersWithDefaults(session, this.obtainMessage3(), punishment2, loginRecord2, false, 4, null));
                  this.handleDatabaseSession(session, punishment2, loginRecord2);
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

   private final void handleDatabaseSession(DatabaseSession databaseSession, Punishment punishment, LoginRecord loginRecord) {
      if (this.obtainPunishmentType2() == PunishmentType.BAN && !Intrinsics.areEqual(punishment.getUuid(), loginRecord.getUuid())) {
         AllowTask kotlinCommandTaskSub;
         String[] texts = new String[]{"check", null};
         String uuid = loginRecord.getUuid();
         if (uuid == null) {
            uuid = loginRecord.getName();
            if (uuid == null) {
               return;
            }
         }

         texts[1] = uuid;
         kotlinCommandTaskSub = new AllowTask(texts, databaseSession.getPlatform(), false);
      }
   }
}
