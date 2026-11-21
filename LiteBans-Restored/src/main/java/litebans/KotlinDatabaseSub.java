package litebans;

import java.io.Closeable;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import litebans.api.Database;
import litebans.api.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinDatabaseSub extends Database {
   private final Platform platform;
   private int intValue;

   public KotlinDatabaseSub(@NotNull Platform platform) {
      this.platform = platform;
   }

   public final int obtainIntValue() {
      return this.intValue;
   }

   @Override
   public boolean isPlayerBanned(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.BAN) != null;
   }

   @Override
   public boolean isPlayerMuted(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.MUTE) != null;
   }

   private final Entry computeEntry(UUID uuid, String text, String text2, PunishmentType punishmentType) {
      this.perform();
      if (uuid == null && text == null) {
         return null;
      }

      DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      String text3;
      if (Intrinsics.areEqual(text2, "__DEFAULT__")) {
         text3 = module.getServerName();
      } else {
         text3 = text2;
         if (text2 == null) {
            text3 = module.getServerName();
         }
      }

      String text4 = text3;
      if (uuid != null && punishmentType == PunishmentType.MUTE) {
         PunishmentLookupModule module2 = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
         Punishment punishment = module2.findPunishment(uuid, text, text2);
         if (punishment != null) {
            return KotlinHelper29.KOTLIN_HELPER.computeEntry(this.platform, punishment);
         }
      }

      var session = module.openSession();
      Throwable error = null;

      try {
         DatabaseSession databaseSession = (DatabaseSession)session;
         Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(databaseSession, Objects.toString(uuid), text, punishmentType, text4, false, false, 48, null);
         if (punishment2 == null) {
            return null;
         }

         return KotlinHelper29.KOTLIN_HELPER.computeEntry(this.platform, punishment2);
      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(session, error);
      }
   }

   private final Entry computeEntry(long value, String text, PunishmentType punishmentType) {
      this.perform();
      if (value < 0L) {
         String text2 = "Negative ID: " + value;
         throw new IllegalArgumentException(text2.toString());
      }

      DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      String text3 = Intrinsics.areEqual(text, "__DEFAULT__") ? module.getServerName() : text;
      var session = module.openSession();
      Throwable error = null;

      try {
         DatabaseSession databaseSession = (DatabaseSession)session;
         String text4 = text3;
         if (text3 == null) {
            text4 = module.getServerName();
         }

         Punishment punishmentById = PunishmentQueries.findPunishmentByIdWithDefaults(databaseSession, value, punishmentType, text4, false, 8, null);
         if (punishmentById == null) {
            return null;
         }

         return KotlinHelper29.KOTLIN_HELPER.computeEntry(this.platform, punishmentById);
      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(session, error);
      }
   }

   @Override
   public boolean isPlayerBanned(@Nullable UUID uuid, @Nullable String text) {
      return this.isPlayerBanned(uuid, text, "__DEFAULT__");
   }

   @Override
   public boolean isPlayerMuted(@Nullable UUID uuid, @Nullable String text) {
      return this.isPlayerMuted(uuid, text, "__DEFAULT__");
   }

   @Override
   public Entry getBan(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.BAN);
   }

   @Override
   public Entry getBan(long value, @Nullable String text) {
      return this.computeEntry(value, text, PunishmentType.BAN);
   }

   @Override
   public Entry getMute(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.MUTE);
   }

   @Override
   public Entry getMute(long value, @Nullable String text) {
      return this.computeEntry(value, text, PunishmentType.MUTE);
   }

   @Override
   public Entry getWarning(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.WARN);
   }

   @Override
   public Entry getWarning(long value, @Nullable String text) {
      return this.computeEntry(value, text, PunishmentType.WARN);
   }

   @Override
   public Entry getKick(@Nullable UUID uuid, @Nullable String text, @Nullable String text2) {
      return this.computeEntry(uuid, text, text2, PunishmentType.KICK);
   }

   @Override
   public Entry getKick(long value, @Nullable String text) {
      return this.computeEntry(value, text, PunishmentType.KICK);
   }

   @Override
   public PreparedStatement prepareStatement(@NotNull String text) {
      String text2 = text;
      this.perform();
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      DatabaseModule module2 = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      DatabaseSession session = module2.openSession();
      if (session.isClosed()) {
         throw new IllegalStateException("Database connection not available");
      }

      if (StringsKt.containsWithDefaults(text2, '{', false, 2, null)) {
         text2 = StringsKt.computeText(text2, "{table_prefix}", module.getSettings().getSqlTablePrefix(), false, 4, null);
         PunishmentTable[] punishmentTables = PunishmentTable.values();
         int value = 0;

         for (int i = punishmentTables.length; value < i; value++) {
            PunishmentTable punishmentTable = punishmentTables[value];
            StringBuilder builder = new StringBuilder().append('{');
            String text3 = punishmentTable.name();
            text2 = StringsKt.computeText(text2, builder.append(text3.toLowerCase(Locale.ENGLISH)).append('}').toString(), punishmentTable.toString(), false, 4, null);
         }
      }

      int value2 = this.intValue++;
      return new PoolStatementWrapperSub(session.prepare(text2).getStatement(), session);
   }

   @Override
   public Collection getUsersByIP(@Nullable String text) {
      this.perform();
      if (text != null) {
         String text2 = text;
         if (TargetParser.TARGET_PARSER.checkText(text2) && !TargetParser.isUuid(text)) {
            DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
            var session = module.openSession();
            Throwable error = null;

            try {
               DatabaseSession databaseSession = (DatabaseSession)session;
               List loginRecords = PunishmentQueries.findLoginRecordsWithDefaults(databaseSession, text, 0, 2, null);
               if (loginRecords.isEmpty()) {
                  return CollectionsKt.emptyList();
               }

               Iterable iterable = loginRecords;
               var arrayList = new ArrayList();

               for (Object obj : iterable) {
                  LoginRecord loginRecord = (LoginRecord)obj;
                  if (TargetParser.isUuid(loginRecord.getUuid())) {
                     arrayList.add(obj);
                  }
               }

               Iterable iterable2 = arrayList;
               var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList, 10));

               for (Object obj2 : iterable2) {
                  LoginRecord loginRecord2 = (LoginRecord)obj2;
                  arrayList2.add(UUID.fromString(loginRecord2.getUuid()));
               }

               return CollectionsKt.toSet(arrayList2);
            } catch (Throwable e) {
               error = e;
               throw SneakyThrow.<RuntimeException>rethrow(e);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         }
      }

      throw new IllegalArgumentException("Target is not an IP address: " + text);
   }

   @Override
   public String getPlayerName(@NotNull UUID uuid) {
      this.perform();
      DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      var session = module.openSession();
      Throwable error = null;

      try {
         DatabaseSession databaseSession = (DatabaseSession)session;
         LoginRecord loginRecord = databaseSession.findLoginRecord(uuid.toString());
         return loginRecord != null ? loginRecord.getName() : null;
      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(session, error);
      }
   }

   @Override
   public UUID getPlayerUUID(@NotNull String text) {
      this.perform();
      DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
      var session = module.openSession();
      Throwable error = null;

      try {
         DatabaseSession databaseSession = (DatabaseSession)session;
         TargetParser targetParser = TargetParser.TARGET_PARSER;
         LoginRecord loginRecord = databaseSession.findLoginRecord(text);
         String normalizeNullable2 = targetParser.normalizeNullable(loginRecord != null ? loginRecord.getUuid() : null);
         return TargetParser.isUuid(normalizeNullable2) ? UUID.fromString(normalizeNullable2) : null;
      } catch (Throwable e) {
         error = e;
         throw SneakyThrow.<RuntimeException>rethrow(e);
      } finally {
         CloseableKt.handleCloseable(session, error);
      }
   }

   @Override
   public String toString() {
      return "Database";
   }

   private final void perform() {
      if (this.platform.isPrimaryThread()) {
         throw new IllegalStateException("Database queries cannot be executed on the main server thread!");
      }
   }
}
