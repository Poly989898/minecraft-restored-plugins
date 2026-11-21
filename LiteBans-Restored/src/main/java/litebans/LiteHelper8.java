package litebans;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LiteHelper8 {
   public static int computeIntValue(@NotNull LiteAInterface3 liteAInterface, @NotNull Platform platform, @Nullable String[] texts, @NotNull Collection collection, int value) {
      if (texts == null) {
         return Integer.MAX_VALUE;
      }

      int value2 = StringMatching.computeIntValue(texts, value);
      if (!collection.isEmpty()) {
         value2 = Math.min(value2, collection.size());
      }

      return Math.max(1, value2);
   }

   public static int computeIntValue(LiteAInterface3 liteAInterface, Platform platform, String[] texts, Collection collection, int value, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 8) != 0) {
         value = ((ConfigModule)platform.getModule(ConfigModule.class)).getSettings().getDefaultHistoryLimit();
      }

      return liteAInterface.computeIntValue(platform, texts, collection, value);
   }

   public static void handleLiteAInterface(@NotNull LiteAInterface3 liteAInterface, @NotNull DatabaseSession databaseSession, @NotNull Collection collection, @Nullable LoginRecord loginRecord, @Nullable String[] texts, @NotNull AbstractCommand abstractCommand, int value) {
      boolean flag = false;
      AbstractCommand abstractCommand2 = abstractCommand;
      CommandTask commandTask = abstractCommand2;
      long currentTime = DatabaseModule.getCurrentTimeWithDefaults((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class), false, 1, null);
      HashMap hashMap = new HashMap();
      if (!StringsKt.isBlank(liteAInterface.obtainMessage()) && loginRecord != null) {
         AltAccountPunisher.sendMessage((CommandTask) abstractCommand2, (CharSequence) (Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, liteAInterface.obtainMessage(), loginRecord, false, 2, null), "limit", value), "total", collection.size())));
      }

      for (Punishment punishment : (Iterable<Punishment>) CollectionsKt.take(collection, value)) {
         String uuid = punishment.getUuid();
         LoginRecord loginRecord2 = loginRecord;
         if (loginRecord == null) {
            loginRecord2 = liteAInterface.computeLoginRecord(databaseSession, hashMap, uuid);
         }

         LoginRecord loginRecord3 = loginRecord2;
         if (loginRecord3 != null) {
            label130: {
               if (punishment.isIpBan()) {
                  String ip = punishment.getIp();
                  if ((ip != null ? StringsKt.startsWithWithDefaults(ip, '#', false, 2, null) : false) && !Intrinsics.areEqual(punishment.getUuid(), loginRecord3.getUuid())) {
                     flag = true;
                     break label130;
                  }
               }

               flag = false;
            }

            if (flag) {
               CommandTask commandTask2 = abstractCommand2;
               ConfigModule module = (ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class);
               byte b = 100;
               if (module.isDebugAtLeast(b)) {
                  module.logDebug("r0 " + punishment);
               }
               continue;
            }
         }

         boolean flag2 = uuid == null || StringsKt.startsWithWithDefaults(uuid, '#', false, 2, null);
         String ip2 = punishment.getIp();
         if (flag2 && !abstractCommand2.getSender().isConsole()) {
            CommandTask commandTask3 = abstractCommand2;
            if (((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().isSecurityIpban()) {
               ip2 = Message.BAN_HIDDEN_IP.toString();
            }
         }

         CharSequence message = Message.computeMessage(punishment.getType());
         LoginRecord loginRecord4 = !flag2 ? liteAInterface.computeLoginRecord(databaseSession, hashMap, uuid) : new LoginRecord(ip2, null, ip2, new Date(punishment.getTime()));
         if (loginRecord4 == null) {
            CommandTask commandTask4 = abstractCommand2;
            ConfigModule module2 = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
            byte b2 = 100;
            if (module2.isDebugAtLeast(b2)) {
               module2.logDebug("r2 " + punishment);
            }
         } else {
            boolean isAffecting2 = databaseSession.isAffecting(punishment);
            if (isAffecting2 && punishment.getType() != PunishmentType.KICK) {
               CharSequence message2 = message;
               CharSequence message3 = punishment.isPermanent() ? Message.ACTIVE_SUFFIX : Message.ACTIVE_SUFFIX_TEMP;
               message = "" + message2 + message3;
            }

            String removedByName = punishment.getRemovedByName();
            if (Intrinsics.areEqual("#expired", removedByName) || !isAffecting2 && punishment.isExpiredAt(currentTime)) {
               CharSequence message4 = message;
               CharSequence message5 = Message.EXPIRED_SUFFIX;
               message = "" + message4 + message5;
            }

            CharSequence message6 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message, punishment, loginRecord4, false, 4, null);
            CharSequence message7 = null;
            CharSequence message8 = removedByName;
            if (message8 != null && !StringsKt.isBlank(message8) && !StringsKt.startsWithWithDefaults(removedByName, '#', false, 2, null)) {
               CommandTask commandTask5 = abstractCommand2;
               String removedByName2 = ((BroadcastModule)commandTask5.getPlatform().getModule(BroadcastModule.class)).getRemovedByName(databaseSession, punishment);
               if (removedByName2 != null) {
                  Message message9;
                  switch (PunishmentTypeSwitchMap7.value[punishment.getType().ordinal()]) {
                     case 1:
                        message9 = Message.UNBAN_ENTRY;
                        break;
                     case 2:
                        message9 = Message.UNMUTE_ENTRY;
                        break;
                     case 3:
                        message9 = Message.UNWARN_ENTRY;
                        break;
                     default:
                        message9 = null;
                  }

                  message7 = message9;
                  if (message7 != null) {
                     CommandTask commandTask6 = abstractCommand2;
                     long value2 = ((DatabaseModule)commandTask6.getPlatform().getModule(DatabaseModule.class)).getCurrentTime(true) - punishment.getRemovedByDate();
                     Message message10 = (Message)message7;
                     CharSequence message11 = message10.computeText("timeSince", Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value2, false, 2, null));
                     message7 = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Placeholders.replacePlaceholder(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, message11, removedByName2, punishment.getRemovedByUuid(), false, 4, null), "displayName", removedByName2), punishment, loginRecord4, false, 4, null);
                  }
               }
            }

            AltAccountPunisher.sendMessage(abstractCommand2, message6);
            if (message7 != null) {
               AltAccountPunisher.sendMessage(abstractCommand2, message7);
            }
         }
      }
   }

   public static void handleLiteAInterface(LiteAInterface3 liteAInterface, DatabaseSession databaseSession, Collection collection, LoginRecord loginRecord, String[] texts, AbstractCommand abstractCommand, int value, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 2) != 0) {
         loginRecord = null;
      }

      if ((value2 & 4) != 0) {
         texts = null;
      }

      if ((value2 & 8) != 0) {
         Intrinsics.checkNotNullExpressionValue(liteAInterface, "");
         abstractCommand = (AbstractCommand)liteAInterface;
      }

      if ((value2 & 16) != 0) {
         value = computeIntValue(liteAInterface, databaseSession.getPlatform(), texts, collection, 0, 8, null);
      }

      liteAInterface.handleDatabaseSession(databaseSession, collection, loginRecord, texts, abstractCommand, value);
   }

   public static LoginRecord computeLoginRecord(@NotNull LiteAInterface3 liteAInterface, @NotNull DatabaseSession databaseSession, @NotNull HashMap map, @Nullable String text) {
      Map map2 = map;
      Intrinsics.checkNotNull(text);
      String text2 = text;
      Object obj = map2.get(text2);
      Object obj2;
      if (obj == null) {
         LoginRecord loginRecord = databaseSession.findLoginRecord(text);
         map2.put(text2, loginRecord);
         obj2 = loginRecord;
      } else {
         obj2 = obj;
      }

      return (LoginRecord)obj2;
   }
}
