package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DupeIpAlertTask extends CommandTask {
   private final String text55;
   private final String text56;
   private final String text57;

   public DupeIpAlertTask(@NotNull Platform platform, @NotNull String text, @NotNull String text2, @Nullable String text3) {
      super(platform);
      this.text55 = text;
      this.text56 = text2;
      this.text57 = text3;
   }

   public final String getText55() {
      return this.text55;
   }

   public final String getText56() {
      return this.text56;
   }

   public final String getText57() {
      return this.text57;
   }

   @Override
   public void run() {
      try {
         Object obj = null;
         CommandTask commandTask = this;
         DatabaseModule module = (DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               CommandTask commandTask2 = this;
               Settings settings = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings();
               DupeIpScanTask punishmentApplyTask = new DupeIpScanTask(this.text56, session, null, false, 0, 28, null).obtainPunishmentApplyTask();
               CommandTask commandTask3 = this;
               Settings settings2 = ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings();
               UUID uuid = TargetParser.TARGET_PARSER.parseUuid(this.getText56());
               if (uuid != null) {
                  UUID uuid2 = uuid;
                  if (settings2.getByteValue() > 1 && session.getDatabaseModule().isDisclaimerAccepted() && punishmentApplyTask.isBannedFound() && !PunishmentQueries.hasAllowEntryWithDefaults(session, uuid2, null, 2, null)) {
                     Iterable iterable = punishmentApplyTask.getResults();
                     Iterator iterator2 = iterable.iterator();

                     while (true) {
                        if (!iterator2.hasNext()) {
                           obj = null;
                           break;
                        }

                        Object obj2 = iterator2.next();
                        DupeIpResult dupeIpResult = (DupeIpResult)obj2;
                        if (dupeIpResult.getPunishment() != null && (dupeIpResult.getPunishment().isIpBan() || settings2.getByteValue() > 2)) {
                           obj = obj2;
                           break;
                        }
                     }

                     DupeIpResult dupeIpResult2 = (DupeIpResult)obj;
                     if ((DupeIpResult)obj != null) {
                        Punishment punishment = dupeIpResult2.getPunishment();
                        Intrinsics.checkNotNull(punishment);
                        AltAccountPunisher.computePunishment(session, dupeIpResult2, punishment, this.getText55(), this.getText57(), this.getText56(), this.getText57(), false, 64, null);
                        if (settings2.getByteValue() > 2) {
                           Iterable iterable2 = punishmentApplyTask.getResults();
                           var arrayList = new ArrayList();

                           for (Object obj3 : iterable2) {
                              DupeIpResult dupeIpResult3 = (DupeIpResult)obj3;
                              if (!Intrinsics.areEqual(dupeIpResult3.getText4(), this.getText56()) && dupeIpResult3.getPunishment() == null) {
                                 arrayList.add(obj3);
                              }
                           }

                           Iterable iterable3 = arrayList;
                           var arrayList2 = new ArrayList();

                           for (Object obj4 : iterable3) {
                              DupeIpResult dupeIpResult4 = (DupeIpResult)obj4;
                              uuid = TargetParser.TARGET_PARSER.parseUuid(dupeIpResult4.getText4());
                              boolean flag;
                              if (uuid == null) {
                                 flag = false;
                              } else {
                                 UUID uuid3 = uuid;
                                 flag = !PunishmentQueries.hasAllowEntryWithDefaults(session, uuid3, null, 2, null);
                              }

                              if (flag) {
                                 arrayList2.add(obj4);
                              }
                           }

                           List list = arrayList2;
                           Iterable iterable4 = list;

                           for (Object obj5 : iterable4) {
                              DupeIpResult dupeIpResult5 = (DupeIpResult)obj5;
                              AltAccountPunisher.createDupePunishment(session, dupeIpResult5, dupeIpResult2.getPunishment(), dupeIpResult5.getText3(), this.getText57(), dupeIpResult5.getText4(), this.getText57(), true);
                           }
                        }
                     }
                  }
               }

               if (!punishmentApplyTask.isFailed() && (punishmentApplyTask.isBannedFound() && settings.isNotifyDupeipOnBannedAccount() || punishmentApplyTask.isMutedFound() && settings.isNotifyDupeipOnMutedAccount() || punishmentApplyTask.getResultCount() >= settings.getNotifyDupeipOnJoinThreshold())) {
                  LoginRecord loginRecord = punishmentApplyTask.getLoginRecord();
                  if (loginRecord == null) {
                     StandardKt.notImplemented();
                     throw new CommandAbortException();
                  }

                  CharSequence message = this.computeMessage(session, loginRecord, punishmentApplyTask);
                  CommandTask commandTask4 = this;
                  BroadcastModule module2 = (BroadcastModule)commandTask4.getPlatform().getModule(BroadcastModule.class);
                  BroadcastModule.handleBroadcastModule(module2, session, message, SyncMessageType.NOTIFY_DUPEIP_JOIN, null, null, false, false, null, 124, null);
                  if (settings.isSyncDupeipNotifications() && session.getDatabaseModule().isSyncEnabled()) {
                     session.getDatabaseModule().handleFunction(obj6 -> {
                        AltAccountPunisher.sendSyncWithDefaults(((DatabaseSession) obj6), SyncMessageType.NOTIFY_DUPEIP_JOIN, message, null, null, 12, null);
                        return Unit.UNIT;
                     });
                  }

                  CommandTask commandTask5 = punishmentApplyTask;
                  String text = ((ConfigModule)commandTask5.getPlatform().getModule(ConfigModule.class)).obtainKotlinData().computeText("alts");
                  if (text.length() > 0) {
                     String string = session.replacePlaceholders(Message.WEB_ALTS_THUMB, punishmentApplyTask.getLoginRecord(), false).toString();
                     CharSequence replacePlaceholders2 = session.replacePlaceholders(TextUtil.computeMessage(punishmentApplyTask.getResultText(), Message.DUPEIP_END), punishmentApplyTask.getLoginRecord(), false);
                     CharSequence replacePlaceholders3 = session.replacePlaceholders(Message.WEB_ALTS_TITLE, punishmentApplyTask.getLoginRecord(), false);
                     DiscordWebhook.KOTLIN_DATA.sendWebhook(session, new DiscordWebhook(text), replacePlaceholders3, replacePlaceholders2, DiscordWebhook.KOTLIN_DATA.computeIntValue(session, Message.WEB_ALTS_COLOR), string, null, false);
                  }
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

   public final CharSequence computeMessage(@NotNull DatabaseSession databaseSession, @NotNull LoginRecord loginRecord, @NotNull DupeIpScanTask punishmentApplyTask) {
      CharSequence message = punishmentApplyTask.hasMultipleAddresses() ? Message.DUPEIP_MULTIPLE_ADDRESSES.computeText("num", punishmentApplyTask.getAddressCount()) : this.text57;
      return TextUtil.computeMessage(TextUtil.computeMessage(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.START_NO_IP.computeText("ip", message), loginRecord, false, 2, null), punishmentApplyTask.getResultText()), Message.DUPEIP_END);
   }

}
