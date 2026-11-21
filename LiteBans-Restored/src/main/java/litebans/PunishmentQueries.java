package litebans;

import java.sql.SQLException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentQueries {
   public static boolean isOpen(@NotNull DatabaseSession databaseSession) {
      return !databaseSession.isClosed();
   }

   public static DatabaseModule access$getPlatform(@NotNull DatabaseSession databaseSession) {
      return (DatabaseModule)databaseSession.getPlatform().getModule(DatabaseModule.class);
   }

   public static LoginRecord saveLoginRecordWithDefaults(DatabaseSession databaseSession, LoginRecord loginRecord, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      return databaseSession.saveLoginRecord(loginRecord, flag);
   }

   public static void saveLoginWithDefaults(DatabaseSession databaseSession, String text, String text2, String text3, boolean flag, boolean flag2, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 8) != 0) {
         flag = true;
      }

      if ((value & 16) != 0) {
         flag2 = true;
      }

      databaseSession.saveLogin(text, text2, text3, flag, flag2);
   }

   public static ResultSet getAllowEntriesWithDefaults(DatabaseSession databaseSession, UUID uuid, int value, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 2) != 0) {
         value = 0;
      }

      return databaseSession.getAllowEntries(uuid, value);
   }

   public static boolean hasAllowEntryWithDefaults(DatabaseSession databaseSession, UUID uuid, PunishmentType punishmentType, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         punishmentType = PunishmentType.BAN;
      }

      return databaseSession.hasAllowEntry(uuid, punishmentType);
   }

   public static List findLoginRecordsWithDefaults(DatabaseSession databaseSession, String text, int value, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 2) != 0) {
         value = 256;
      }

      return databaseSession.findLoginRecords(text, value);
   }

   public static Punishment findExistingPunishmentWithDefaults(DatabaseSession databaseSession, Punishment punishment, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         flag = false;
      }

      return databaseSession.findExistingPunishment(punishment, flag);
   }

   public static Punishment findPunishmentWithDefaults(DatabaseSession databaseSession, String text, String text2, PunishmentType punishmentType, boolean flag, boolean flag2, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         text2 = null;
      }

      if ((value & 4) != 0) {
         punishmentType = PunishmentType.BAN;
      }

      if ((value & 8) != 0) {
         flag = false;
      }

      if ((value & 16) != 0) {
         flag2 = false;
      }

      return databaseSession.findPunishment(text, text2, punishmentType, flag, flag2);
   }

   public static Punishment findPunishmentWithDefaults(DatabaseSession databaseSession, String text, String text2, PunishmentType punishmentType, String text3, boolean flag, boolean flag2, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         text2 = null;
      }

      if ((value & 4) != 0) {
         punishmentType = PunishmentType.BAN;
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      if ((value & 32) != 0) {
         flag2 = false;
      }

      return databaseSession.findPunishment(text, text2, punishmentType, text3, flag, flag2);
   }

   public static Punishment findPunishmentWithDefaults(DatabaseSession databaseSession, String text, PunishmentType punishmentType, String text2, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 4) != 0) {
         text2 = "__ALL__";
      }

      if ((value & 8) != 0) {
         flag = true;
      }

      return databaseSession.findPunishment(text, punishmentType, text2, flag);
   }

   public static Punishment findPunishmentByIdWithDefaults(DatabaseSession databaseSession, long value, PunishmentType punishmentType, String text, boolean flag, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 8) != 0) {
         flag = true;
      }

      return databaseSession.findPunishmentById(value, punishmentType, text, flag);
   }

   public static long countEntriesWithDefaults(DatabaseSession databaseSession, PunishmentTable punishmentTable, String text, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         text = "__ALL__";
      }

      if ((value & 4) != 0) {
         flag = false;
      }

      return databaseSession.countEntries(punishmentTable, text, flag);
   }

   public static Punishment findPunishmentWithDefaults(DatabaseSession databaseSession, String text, String text2, String text3, PunishmentType punishmentType, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 8) != 0) {
         punishmentType = PunishmentType.BAN;
      }

      return databaseSession.findPunishment(text, text2, text3, punishmentType);
   }

   public static List findLoginRecordsLimitWithDefaults(DatabaseSession databaseSession, String text, int value, int value2, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value2 & 2) != 0) {
         value = 8;
      }

      return databaseSession.findLoginRecordsLimit(text, value);
   }

   public static void handleDatabaseSession(DatabaseSession databaseSession, String text, String text2, CharSequence message, Punishment punishment, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      databaseSession.handleText(text, text2, message, punishment, flag);
   }

   public static void handleDatabaseSession(DatabaseSession databaseSession, Punishment punishment, String text, String text2, CharSequence message, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         text = null;
      }

      if ((value & 4) != 0) {
         text2 = null;
      }

      if ((value & 8) != 0) {
         message = "";
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      databaseSession.handlePunishment(punishment, text, text2, message, flag);
   }

   public static void updateExpiredWithDefaults(DatabaseSession databaseSession, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 1) != 0) {
         flag = false;
      }

      databaseSession.updateExpired(flag);
   }

   public static List computeList(@NotNull DatabaseSession databaseSession, @NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      try {
         if (!resultSet.next()) {
            return CollectionsKt.emptyList();
         }

         ArrayList arrayList = new ArrayList();

         do {
            arrayList.add(databaseSession.readPunishment(resultSet, punishmentType));
         } while (resultSet.next());

         return arrayList;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public static List access$getHistory(@NotNull DatabaseSession databaseSession, @NotNull LoginRecord loginRecord, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag, int value) {
      return databaseSession.getHistory(loginRecord.getUuid(), loginRecord.getIp(), punishmentType, text, flag, value);
   }

   public static List computeList(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, int value) {
      Punishment punishment2 = punishment;
      return databaseSession.getHistory(punishment2.getUuid(), punishment2.getIp(), punishment2.getType(), punishment2.getServerScope(), punishment2.isActive(), value);
   }

   public static List computeList(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender, @NotNull PunishmentType punishmentType, boolean flag, int value) {
      LiteSender liteSender2 = liteSender;
      String uuidString = liteSender2.getUuidString();
      String ipAddress = liteSender2.getIpAddress();
      String serverName = liteSender2.getServerName();
      if (serverName == null) {
         serverName = databaseSession.getDatabaseModule().getServerName();
      }

      return databaseSession.getHistory(uuidString, ipAddress, punishmentType, serverName, flag, value);
   }

   public static String computeText(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender) {
      Settings settings = ((ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class)).getSettings();
      return liteSender.isConsole() ? settings.getConsoleSenderName() : (settings.isUseDisplayNames() ? liteSender.getDisplayName() : liteSender.getName());
   }

   public static CharSequence replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull LiteSender liteSender) {
      return replacePlaceholdersWithDefaults(databaseSession, message, databaseSession.getStaffName(liteSender), liteSender.getUuidString(), false, 4, null);
   }

   public static CharSequence replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @Nullable String text, @Nullable String text2, boolean flag) {
      if (message.length() == 0) {
         return message;
      }

      CharSequence message2 = message;
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      ConfigModule module2 = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      Serializable serializable = text;
      String text3 = "";
      String text4 = "";
      if (text2 != null && (Placeholders.containsPlaceholder(message2, "executorPrefix") || Placeholders.containsPlaceholder(message2, "executorSuffix"))) {
         byte platformId = databaseSession.getPlatform().getPlatformId();
         if (platformId == 0) {
            PlatformBridge bridge = databaseSession.getPlatform().createBridge();
            Intrinsics.checkNotNullExpressionValue(bridge, "");
            VaultBridge vault = ((BukkitBridge)bridge).getVault();
            LiteSender onlineSender = module.findOnlineSender(text2);
            if (onlineSender != null) {
               text3 = vault.getPrefix(onlineSender.getHandle());
               text4 = vault.getSuffix(onlineSender.getHandle());
            } else if (TargetParser.isUuid(text2)) {
               try {
                  UUID fromString2 = UUID.fromString(text2);
                  text3 = vault.getPrefix(fromString2);
                  text4 = vault.getSuffix(fromString2);
               } catch (IllegalArgumentException e) {
                  module2.handleObj(e, 0);
               }
            }
         } else {
            Pair luckPermsPrefixSuffix = Placeholders.getLuckPermsPrefixSuffix(databaseSession, text2);
            text3 = (String)luckPermsPrefixSuffix.getFirst();
            text4 = (String)luckPermsPrefixSuffix.getSecond();
         }
      }

      if (Intrinsics.areEqual(text3, "null")) {
         text3 = "";
      }

      if (Intrinsics.areEqual(text4, "null")) {
         text4 = "";
      }

      if (flag) {
         text3 = Message.computeText(text3, true);
         text4 = Message.computeText(text4, true);
         serializable = String.valueOf(serializable) + LiteColor.RESET;
      }

      return Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(message2, "executorUUID", text2), "executorPrefix", text3), "executorSuffix", text4), "executor", serializable);
   }

   public static CharSequence replacePlaceholdersWithDefaults(DatabaseSession databaseSession, CharSequence message, String text, String text2, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 4) != 0) {
         flag = true;
      }

      return databaseSession.replacePlaceholders(message, text, text2, flag);
   }

   public static CharSequence replaceStatistics(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message) {
      return Placeholders.replaceStatistics(databaseSession, message);
   }

   public static CharSequence replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @Nullable LoginRecord loginRecord, boolean flag) {
      return loginRecord == null ? message : Placeholders.replacePlaceholders(databaseSession, loginRecord, message, flag);
   }

   public static CharSequence replacePlaceholdersWithDefaults(DatabaseSession databaseSession, CharSequence message, LoginRecord loginRecord, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      return databaseSession.replacePlaceholders(message, loginRecord, flag);
   }

   public static CharSequence replacePlaceholdersNullable(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @Nullable LiteSender liteSender) {
      return liteSender != null ? Placeholders.replacePlaceholdersWithDefaults(databaseSession, liteSender, message, false, 4, null) : message;
   }

   public static CharSequence replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull KotlinHelper13 kotlinHelper) {
      return kotlinHelper.getLiteSender() == null ? databaseSession.replacePlaceholders(message, kotlinHelper.getTarget(), kotlinHelper.getExecutor(), kotlinHelper.getText8(), kotlinHelper.getText9(), null) : databaseSession.replacePlaceholders(message, kotlinHelper.getLiteSender());
   }

   public static CharSequence replacePlaceholders(
      @NotNull DatabaseSession databaseSession,
   @NotNull CharSequence message,
   @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   @Nullable String text4,
   @Nullable String text5
   ) {
      return Placeholders.replaceDetailPlaceholdersWithDefaults(databaseSession, text, text2, text3, text4, text5, new Date(), message, false, 128, null);
   }

   public static CharSequence replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @Nullable Punishment punishment, boolean flag) {
      return punishment != null && !StringsKt.isBlank(message) ? Placeholders.replacePunishmentPlaceholdersWithDefaults(databaseSession, punishment, message, 0L, flag, 4, null) : message;
   }

   public static CharSequence replacePlaceholdersWithDefaults(DatabaseSession databaseSession, CharSequence message, Punishment punishment, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      return databaseSession.replacePlaceholders(message, punishment, flag);
   }

   public static CharSequence access$replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @Nullable Punishment punishment, @Nullable LoginRecord loginRecord, boolean flag) {
      return databaseSession.replacePlaceholders(databaseSession.replacePlaceholders(message, punishment, false), loginRecord, flag);
   }

   public static CharSequence replacePlaceholdersWithDefaults(DatabaseSession databaseSession, CharSequence message, Punishment punishment, LoginRecord loginRecord, boolean flag, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 4) != 0) {
         flag = true;
      }

      return databaseSession.replacePlaceholders(message, punishment, loginRecord, flag);
   }

   public static boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull LiteSender liteSender) {
      DatabaseModule module = (DatabaseModule)databaseSession.getPlatform().getModule(DatabaseModule.class);
      if (punishment.checkText(liteSender.getUuidString(), liteSender.getIpAddress(), DatabaseModule.getCurrentTimeWithDefaults(module, false, 1, null))) {
         ServerScope serverScope = ServerScopes.SERVER_SCOPE;
         String serverScope2 = punishment.getServerScope();
         if (serverScope2 == null) {
            serverScope2 = ServerScopes.SERVER_SCOPE.getLocalScope();
         }

         String serverName = liteSender.getServerName();
         if (serverName == null) {
            serverName = module.getServerName();
         }

         if (serverScope.scopesMatch(serverScope2, serverName)) {
            return true;
         }
      }

      return false;
   }

   public static boolean checkDatabaseSession(@NotNull DatabaseSession databaseSession, @Nullable Punishment punishment) {
      return punishment != null ? punishment.checkText(punishment.getUuid(), punishment.getIp(), DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null)) : false;
   }
}
