package litebans;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DatabaseSessionImpl implements DatabaseSession {
   public static final DatabaseSessionImplCompanion DATABASE_SESSION_IMPL_COMPANION = new DatabaseSessionImplCompanion(null);
   private final Platform platform;
   private final Connection connection;
   private final PunishmentLookupModule punishmentLookupModule;
   private final BroadcastModule broadcastModule;
   private final ConfigModule configModule;
   private static final Function1 return2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction(obj -> ((PreparedQuery) obj));
   public static final int DURATION_20_HOURS_MILLIS = 72000000;
   public static final int DURATION_16_HOURS_MILLIS = 57600000;
   public static final int DURATION_8_HOURS_MILLIS = 28800000;
   public static final int VALUE_64 = 64;
   private static int value12 = 64;

   public DatabaseSessionImpl(@NotNull Platform platform, @NotNull Connection connection) {
      this.platform = platform;
      this.connection = connection;
      this.punishmentLookupModule = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
   }

   @Override
   public final Platform getPlatform() {
      return this.platform;
   }

   public final BroadcastModule getBroadcastModule() {
      return this.broadcastModule;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public DatabaseMetaData getMetaData() {
      try {
         return this.connection.getMetaData();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isClosed() {
      try {
         return this.connection.isClosed();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getTotalEntryCount() {
      Iterable iterable = PunishmentTable.PUNISHMENT_TABLE_COMPANION.obtainList();
      long value = 0L;

      for (Object obj : iterable) {
         PunishmentTable punishmentTable = (PunishmentTable)obj;
         long value2 = value;
         long countEntries = PunishmentQueries.countEntriesWithDefaults(this, punishmentTable, (String) null, false, 6, null);
         value = value2 + countEntries;
      }

      return value;
   }

   public final Pair buildInsertStatement(@NotNull Punishment punishment, boolean flag) {
      String text = "uuid,ip,reason,banned_by_uuid,banned_by_name,time,until,template,server_scope,server_origin,silent,ipban,ipban_wildcard,active"
         + punishment.getWarnedSqlSuffix();
      CharSequence message = text;
      byte b = 44;
      int value = 0;

      for (int i = 0; i < message.length(); i++) {
         char charAt2 = message.charAt(i);
         if (charAt2 == b) {
            value++;
         }
      }

      int value2 = value + 1;
      StringBuilder stringBuilder = new StringBuilder((value2 + 1) * 2);

      for (int i2 = 0; i2 < value2; i2++) {
         if (flag && i2 > 9) {
            stringBuilder.append("?::bit,");
         } else {
            stringBuilder.append('?').append(',');
         }
      }

      String substring2 = stringBuilder.substring(0, stringBuilder.length() - 1);
      return TuplesKt.computePair(text, substring2);
   }

   @Override
   public void insertPunishment(@NotNull Punishment punishment) {
      Pair insertStatement = this.buildInsertStatement(punishment, Intrinsics.areEqual(this.configModule.getSettings().getSqlDriver(), "pgsql"));
      String string = (String)insertStatement.component1();
      String string2 = (String)insertStatement.component2();
      ConfigModule configModule = this.configModule;
      byte b = 1;
      if (configModule.isDebugAtLeast(b)) {
         configModule.logDebug("Created new " + punishment.getType() + " for server:" + ServerScopes.SERVER_SCOPE.normalize(punishment.getServerScope()));
      }

      ConfigModule configModule2 = this.configModule;
      byte b2 = 1;
      if (configModule2.isDebugAtLeast(b2)) {
         configModule2.logDebug("Adding entry: " + punishment);
      }

      StringBuilder builder = new StringBuilder().append("INSERT INTO ");
      Punishment punishment2 = punishment;
      String string3 = builder.append(punishment2.getType().getPunishmentTable()).append('(').append(string).append(")VALUES(").append(string2).append(')').toString();
      PreparedQuery prepare2 = this.prepare(string3);
      Punishment punishment3 = punishment;
      String[] texts = new String[5];
      String uuid = punishment3.getUuid();
      texts[0] = uuid != null ? TextUtil.access$length(uuid, 36) : null;
      uuid = punishment3.getIp();
      texts[1] = uuid != null ? TextUtil.access$length(uuid, 45) : null;
      CharSequence reason = punishment3.getReason();
      short value = 2048;
      texts[2] = TextUtil.access$length(reason.toString(), value);
      uuid = punishment3.getBannedByUuid();
      texts[3] = uuid != null ? TextUtil.access$length(uuid, 36) : null;
      uuid = punishment3.getBannedByName();
      texts[4] = uuid != null ? TextUtil.access$length(uuid, 128) : null;
      int value2 = 0;

      for (int i = texts.length; value2 < i; value2++) {
         String text = texts[value2];
         prepare2.bindString(text);
      }

      long[] values = new long[]{punishment3.getTime(), punishment3.getUntil()};
      int value3 = 0;

      for (int i2 = values.length; value3 < i2; value3++) {
         long value4 = values[value3];
         prepare2.bindLong(value4);
      }

      PreparedQuery bindInt2 = prepare2.bindInt(punishment3.getTemplate());
      String[] texts2 = new String[]{ServerScopes.SERVER_SCOPE.normalize(punishment3.getServerScope()), ServerScopes.SERVER_SCOPE.normalize(punishment3.getServerOrigin())};
      int value5 = 0;

      for (int i3 = texts2.length; value5 < i3; value5++) {
         String text2 = texts2[value5];
         bindInt2.bindString(text2);
      }

      boolean[] flags = new boolean[]{punishment3.isSilent(), punishment3.isIpBan(), punishment3.obtainFlag(), punishment3.isActive()};
      int value6 = 0;

      for (int i4 = flags.length; value6 < i4; value6++) {
         boolean flag = flags[value6];
         bindInt2.bindBoolean(flag);
      }

      if (punishment3.getType() == PunishmentType.WARN) {
         prepare2.bindBoolean(punishment3.isWarned());
      }

      SqlUtil.computeIntValue(bindInt2);
   }

   @Override
   public LoginRecord saveLoginRecord(@NotNull LoginRecord loginRecord, boolean flag) {
      LoginRecord loginRecord2 = loginRecord;
      PunishmentQueries.saveLoginWithDefaults(this, loginRecord2.getName(), loginRecord2.getUuid(), loginRecord2.getIp(), flag, false, 16, null);
      return loginRecord2;
   }

   @Override
   public void saveLogin(@Nullable String text, @Nullable String text2, @Nullable String text3, boolean flag, boolean flag2) {
      String text4 = text;
      if (text == null) {
         text4 = "#";
      }

      String text5 = text4;
      text4 = text3;
      if (text3 == null) {
         text4 = "#";
      }

      String text6 = text4;
      int value = text5.length();
      byte usernameMaxLength = this.getConfigModule().getSettings().getUsernameMaxLength();
      if (Intrinsics.compare(value, usernameMaxLength & 255) > 0) {
         String text7 = "Player name \"" + text5 + "\" length > " + UByte.toStringImpl(this.getConfigModule().getSettings().getUsernameMaxLength());
         throw new IllegalArgumentException(text7.toString());
      }

      if (text2 != null && text2.length() > 36) {
         String text8 = "UUID \"" + text2 + "\" length > 36";
         throw new IllegalArgumentException(text8.toString());
      }

      try {
         if (this.checkText(text5, text2, text6)) {
            if (flag && !StringsKt.containsWithDefaults(text6, '#', false, 2, null) && text2 != null) {
               PunishmentTable punishmentTable = PunishmentTable.HISTORY;
               String text9 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable), "date=CURRENT_TIMESTAMP");
               CharSequence message = "ip=?";
               CharSequence[] messages = new CharSequence[]{SqlText.box(text9), message};
               int value2 = 0;

               for (int i = messages.length; value2 < i; value2++) {
                  CharSequence message2 = messages[value2];
                  if (message2.length() <= 0) {
                     String text10 = "Failed requirement.";
                     throw new IllegalArgumentException(text10.toString());
                  }
               }

               PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.and(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(text9) + " WHERE " + message), "uuid=?"), "name=?")));
               String[] texts = new String[]{text6, text2, text5};
               int value3 = 0;

               for (int i2 = texts.length; value3 < i2; value3++) {
                  String text11 = texts[value3];
                  prepare2.bindString(text11);
               }

               SqlUtil.computeIntValue(prepare2);
            }

            return;
         }

         if (this.configModule.getSettings().isDeletePreviousHistory() && flag2) {
            PunishmentTable punishmentTable2 = PunishmentTable.HISTORY;
            String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable2);
            CharSequence message3 = "name{=?}";
            CharSequence[] messages2 = new CharSequence[]{SqlText.box(constructor), message3};
            int value4 = 0;

            for (int i3 = messages2.length; value4 < i3; value4++) {
               CharSequence message4 = messages2[value4];
               if (message4.length() <= 0) {
                  String text12 = "Failed requirement.";
                  throw new IllegalArgumentException(text12.toString());
               }
            }

            PreparedQuery prepare3 = this.prepare(SqlText.box(SqlText.computeText(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message3), "uuid=?")));
            String[] texts2 = new String[]{text5, text2};
            int value5 = 0;

            for (int i4 = texts2.length; value5 < i4; value5++) {
               String text13 = texts2[value5];
               prepare3.bindString(text13);
            }

            SqlUtil.computeIntValue(prepare3);
         }

         PunishmentTable punishmentTable3 = PunishmentTable.HISTORY;
         CharSequence message5 = "name,uuid,ip";
         byte b = 44;
         int value6 = 0;

         for (int i5 = 0; i5 < message5.length(); i5++) {
            char charAt2 = message5.charAt(i5);
            if (charAt2 == b) {
               value6++;
            }
         }

         int value7 = value6;
         String text14 = "?";
         if (value7 > 0) {
            StringBuilder stringBuilder = new StringBuilder(value7 * 2);
            Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, value7);
            Iterator iterator2 = kotlinIntProgressionSub.iterator();

            while (iterator2.hasNext()) {
               int value8 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
               stringBuilder.append("?,");
            }

            text14 = StringsKt.computeText(stringBuilder.toString(), 1);
         }

         PreparedQuery prepare4 = this.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable3 + '(' + message5 + ")VALUES(" + text14 + ')')));
         String[] texts3 = new String[]{text5, text2, text6};
         int value9 = 0;

         for (int i6 = texts3.length; value9 < i6; value9++) {
            String text15 = texts3[value9];
            prepare4.bindString(text15);
         }

         SqlUtil.computeResultSet(prepare4);
         ConfigModule configModule = this.configModule;
         byte b2 = 1;
         if (configModule.isDebugAtLeast(b2)) {
            configModule.logDebug("New login record: [" + text5 + ':' + text2 + ':' + text6 + ']');
         }
      } catch (Throwable e) {
         if (!(e instanceof SQLException)) {
            throw SneakyThrow.<RuntimeException>rethrow(e);
         }
         SQLException sqlException = (SQLException) e;
         text4 = sqlException.getMessage();
         if (text4 != null ? StringsKt.containsWithDefaults((CharSequence)text4, (CharSequence)"Duplicate entry", false, 2, null) : false) {
            return;
         }

         this.getDatabaseModule().logConnectionError(sqlException);
      }
   }

   @Override
   public void close() {
      try {
         this.connection.close();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final Statement createStatement() {
      try {
         return this.connection.createStatement();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int countEntries(@NotNull HistoryQuery historyQuery, @NotNull PunishmentType punishmentType) {
      try {
         ResultSet resultSet = historyQuery.computeResultSet(punishmentType, "COUNT(*)");
         byte b = 1;
         Integer boxed = resultSet.next() ? resultSet.getInt(b) : null;
         return boxed != null ? boxed : 0;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public List getHistory(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, int value) {
      KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
      kotlinSerializable.obj = DATABASE_SESSION_IMPL_COMPANION.obtainFunction2();
      long value2 = this.configModule.getSettings().getDurationsEffectiveIpbanDuration();
      String text4 = "uuid=?";
      if (text2 != null && !StringsKt.startsWithWithDefaults(text2, '#', false, 2, null)) {
         String text5 = " OR(ipban_wildcard={true} AND active={true} AND ? LIKE ip)";
         String text6 = " OR(ipban_wildcard={true})";
         if (value2 > 0L) {
            text4 = text4 + " OR(ipban={true} AND(ip=?" + text5 + ")AND((time+" + value2 + ")>?" + text6 + "))";
            kotlinSerializable.obj = DATABASE_SESSION_IMPL_COMPANION.computeFunction2((Function1)kotlinSerializable.obj, obj -> {
               PreparedQuery preparedQuery = (PreparedQuery) obj;
               String[] texts = new String[]{text, text2, text2};
               int value3 = 0;

               for (int i = texts.length; value3 < i; value3++) {
                  String text32 = texts[value3];
                  preparedQuery.bindString(text32);
               }

               return SqlUtil.access$bindLong(preparedQuery);
            });
         } else {
            text4 = text4 + " OR(ipban={true} AND(ip=?" + text5 + "))";
            kotlinSerializable.obj = DATABASE_SESSION_IMPL_COMPANION.computeFunction2((Function1)kotlinSerializable.obj, obj2 -> {
               PreparedQuery preparedQuery = (PreparedQuery) obj2;
               String[] texts = new String[]{text, text2, text2};
               int value3 = 0;

               for (int i = texts.length; value3 < i; value3++) {
                  String text32 = texts[value3];
                  preparedQuery.bindString(text32);
               }

               return preparedQuery;
            });
         }
      } else {
         kotlinSerializable.obj = DATABASE_SESSION_IMPL_COMPANION.computeFunction2((Function1)kotlinSerializable.obj, obj3 -> {
            PreparedQuery preparedQuery = (PreparedQuery) obj3;
            String[] texts = new String[]{text};
            int value3 = 0;

            for (int i = texts.length; value3 < i; value3++) {
               String text22 = texts[value3];
               preparedQuery.bindString(text22);
            }

            return preparedQuery;
         });
      }

      List templatesFor = this.configModule.getTemplateManager().getTemplatesFor(punishmentType);
      if (!templatesFor.isEmpty()) {
         text4 = '(' + text4 + ")AND template NOT IN(" + CollectionsKt.joinToStringWithDefaults(templatesFor, ",", null, null, 0, null, null, 62, null) + ')';
      }

      String text7 = text4;
      ExecutorSource executorSource = ExecutorSource.CUSTOM;
      DatabaseSession databaseSession = this;
      ServerScopeClause serverScopeClause = new ServerScopeClause(databaseSession.getPlatform(), text3);
      KotlinHistoryQuerySub kotlinHistoryQuerySub = new KotlinHistoryQuerySub(this, flag, value, kotlinSerializable, text7, executorSource, serverScopeClause);
      return this.readPunishments(HistoryQuery.computeResultSet(kotlinHistoryQuerySub, punishmentType, null, 2, null), punishmentType);
   }

   @Override
   public List getHistory(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, int value) {
      HistoryQueryFactory historyQueryFactory = HistoryQuery.HISTORY_QUERY_FACTORY;
      ExecutorSource executorSource = ExecutorSource.EXECUTOR_UUID;
      HistoryQuery historyQuery = HistoryQueryFactory.createQueryWithDefaults(historyQueryFactory, this, text, text2, executorSource, false, "time", false, value, null, 168, null);
      return this.readPunishments(HistoryQuery.computeResultSet(historyQuery, punishmentType, null, 2, null), punishmentType);
   }

   @Override
   public boolean columnExists(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag) {
      try {
         boolean flag2;
         if (flag) {
            flag2 = SqlUtil.computeResultSet(this.prepare("SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME LIKE '" + punishmentTable + "' AND COLUMN_NAME='" + text + "' LIMIT 1"))
               .next();
         } else {
            DatabaseMetaData metaData = this.getMetaData();
            String text2 = PunishmentTable.computeText(punishmentTable, null, false, 3, null);
            DatabaseModule databaseModule = this.getDatabaseModule();
            ConfigModule module = (ConfigModule)databaseModule.platform.getModule(ConfigModule.class);
            String text3;
            if (Intrinsics.areEqual(module.getSettings().getSqlDriver(), "h2")) {
               String text4 = text;
               text3 = text4.toUpperCase(Locale.ENGLISH);
            } else {
               text3 = text;
            }

            flag2 = metaData.getColumns(null, null, text2, text3).next();
         }

         return flag2;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean tableExists(@NotNull CharSequence message) {
      try {
         return this.getMetaData().getTables(null, null, message.toString(), null).next();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public List findLoginRecords(@Nullable String text, int value) {
      try {
         String text2 = text;
         if (text2 != null && !StringsKt.startsWithWithDefaults(text2, '#', false, 2, null)) {
            PunishmentTarget target = TargetParser.TARGET_PARSER.parseTarget(text2);
            String text3 = target.obtainText();
            String text4 = DATABASE_SESSION_IMPL_COMPANION.computeText(this, text3);
            if (target.getByteValue() == 2 && StringsKt.containsWithDefaults(text2, '*', false, 2, null)) {
               text2 = StringsKt.computeText(text2, '*', '%', false, 4, null);
               text4 = "ip LIKE ?";
            }

            PunishmentTable punishmentTable = PunishmentTable.HISTORY;
            CharSequence message = this.broadcastModule.getHistoryColumns();
            String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
            CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), text4};
            int value2 = 0;

            for (int i = messages.length; value2 < i; value2++) {
               CharSequence message2 = messages[value2];
               if (message2.length() <= 0) {
                  String text5 = "Failed requirement.";
                  throw new IllegalArgumentException(text5.toString());
               }
            }

            ResultSet resultSet = SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.limit(SqlText.orderByDesc(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + text4), "date"), value))).bindString(text2));
            ArrayList arrayList = new ArrayList(4);

            while (resultSet.next()) {
               arrayList.add(this.readLoginRecord(resultSet, true, this.configModule.getSettings().getSqlDriver()));
            }

            return arrayList;
         } else {
            return CollectionsKt.emptyList();
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public LoginRecord readLoginRecord(@NotNull ResultSet resultSet, boolean flag, @Nullable String text) {
      try {
         return new LoginRecord(resultSet.getString("name"), TargetParser.normalize(resultSet.getString("uuid")), resultSet.getString("ip"), this.broadcastModule.computeDate(resultSet, "date", flag, text));
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Collection getEntriesPage(@NotNull PunishmentType punishmentType, long value, long value2, boolean flag) {
      long value3 = Math.max(0L, value);
      long value4 = Math.max(0L, value3 * value2);
      HistoryQuery historyQuery = HistoryQuery.HISTORY_QUERY_FACTORY.createQuery(this, "uuid IS NOT NULL", "__ALL__", ExecutorSource.CUSTOM, flag, "time", false, value2, value4);
      return this.readPunishments(HistoryQuery.computeResultSet(historyQuery, punishmentType, null, 2, null), punishmentType);
   }

   @Override
   public Punishment findExistingPunishment(@NotNull Punishment punishment, boolean flag) {
      return PunishmentQueries.findPunishmentWithDefaults(this, punishment.getUuid(), punishment.isIpBan() ? punishment.getIp() : null, punishment.getType(), punishment.getServerScope(), flag, false, 32, null);
   }

   @Override
   public Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, boolean flag, boolean flag2) {
      return this.findPunishment(text, text2, punishmentType, this.getDatabaseModule().getServerName(), flag, flag2);
   }

   @Override
   public Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, boolean flag2) {
      try {
         String normalizeNullable2 = TargetParser.TARGET_PARSER.normalizeNullable(text);
         PunishmentQueries.updateExpiredWithDefaults(this, false, 1, null);
         ServerScopeClause serverScopeClause;
         if (flag2) {
            DatabaseSession databaseSession = this;
            boolean flag3 = true;
            serverScopeClause = new ServerScopeClauseImpl(databaseSession.getPlatform(), text3, flag3);
         } else {
            DatabaseSession databaseSession2 = this;
            serverScopeClause = new ServerScopeClause(databaseSession2.getPlatform(), text3);
         }

         ServerScopeClause serverScopeClause2 = serverScopeClause;
         StringBuilder builder = new StringBuilder().append("AND ");
         String string = builder.append("active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)").toString();
         String text4 = "";
         String text5 = "";
         if (punishmentType != PunishmentType.WARN && punishmentType != PunishmentType.KICK && !this.configModule.getSettings().isDisableWildcardBans()) {
            text4 = " OR(ipban_wildcard=" + this.broadcastModule.getSqlTrue() + " AND active=" + this.broadcastModule.getSqlTrue() + " AND ? LIKE ip)";
            text5 = " OR(ipban_wildcard=" + this.broadcastModule.getSqlTrue() + ')';
         }

         boolean flag4 = normalizeNullable2 == null || StringsKt.startsWithWithDefaults(normalizeNullable2, '#', false, 2, null);
         boolean flag5 = text2 == null || StringsKt.startsWithWithDefaults(text2, '#', false, 2, null);
         if (flag5 && flag4) {
            return null;
         }

         String text6 = flag ? "ORDER BY time DESC LIMIT 1" : "LIMIT 1";
         long value = this.configModule.getSettings().getDurationsEffectiveIpbanDuration();
         String text7 = value > 0L
            ? "(ipban=" + this.broadcastModule.getSqlTrue() + " AND(ip=?" + text4 + ")AND((time+" + value + ")>?" + text5 + "))"
            : "(ipban=" + this.broadcastModule.getSqlTrue() + " AND(ip=?" + text4 + "))";
         String text8 = (
               flag5
                  ? "SELECT * FROM " + punishmentType.getPunishmentTable() + " WHERE(uuid=?" + serverScopeClause2 + ')' + string
                  : (
                     flag4
                        ? "SELECT * FROM " + punishmentType.getPunishmentTable() + " WHERE(" + text7 + serverScopeClause2 + ')' + string
                        : "SELECT * FROM " + punishmentType.getPunishmentTable() + " WHERE((uuid=? OR" + text7 + ')' + serverScopeClause2 + ')' + string
                  )
            )
            + text6;
         ConfigModule configModule = this.configModule;
         if (configModule.isVerbose()) {
            configModule.logDebug(text8);
         }

         PreparedQuery prepare2 = this.prepare(text8);
         if (!flag4) {
            prepare2.bindString(normalizeNullable2);
         }

         if (!flag5) {
            prepare2.bindString(text2);
            if (!StringsKt.isBlank(text4)) {
               prepare2.bindString(text2);
            }

            if (value > 0L) {
               SqlUtil.access$bindLong(prepare2);
            }
         }

         serverScopeClause2.handlePreparedQuery(prepare2);
         SqlUtil.access$bindLong(prepare2);
         Punishment punishment = null;
         long value2 = System.nanoTime();

         try {
            ResultSet resultSet = SqlUtil.computeResultSet(prepare2);
            if (resultSet.next()) {
               Punishment punishment2 = this.readPunishment(resultSet, punishmentType);
               if (punishment2.checkText(normalizeNullable2, text2, DatabaseModule.getCurrentTimeWithDefaults(this.getDatabaseModule(), false, 1, null))) {
                  punishment = punishment2;
               }
            }
         } finally {
            long value3 = System.nanoTime() - value2;
            double number = value3 / 1.0E9;
            if (number > 4.0) {
               this.platform.getLogger().warning("Database is slow! Pooled connection took " + NumberFormats.computeText2(number) + " seconds to retrieve result from database.");
            }
         }

         return punishment;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Punishment findPunishment(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType) {
      List loginRecordsLimit = PunishmentQueries.findLoginRecordsLimitWithDefaults(this, text, 0, 2, null);
      if (loginRecordsLimit.isEmpty()) {
         return null;
      }

      for (LoginRecord loginRecord : (Iterable<LoginRecord>) loginRecordsLimit) {
         if (loginRecord.getUuid() != null && !Intrinsics.areEqual(loginRecord.getUuid(), text2)) {
            this.configModule.debug(10, "UUID: " + loginRecord.getUuid());
            Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(this, loginRecord.getUuid(), null, punishmentType, text3, false, false, 32, null);
            ConfigModule configModule = this.configModule;
            StringBuilder stringBuilder = new StringBuilder();
            PunishmentType punishmentType2 = punishmentType;
            String string = punishmentType2.toString();
            String text4;
            if (string.length() > 0) {
               StringBuilder stringBuilder2 = new StringBuilder();
               char charAt2 = string.charAt(0);
               StringBuilder builder = stringBuilder2.append((Object)(Character.isLowerCase(charAt2) ? KotlinStub13.computeText2(charAt2, Locale.getDefault()) : String.valueOf(charAt2)));
               byte b = 1;
               text4 = builder.append(string.substring(b)).toString();
            } else {
               text4 = string;
            }

            configModule.debug(10, stringBuilder.append(text4).append(": ").append(punishment).toString());
            if (punishment != null) {
               this.platform.getLogger().info("Player is name-banned: " + text + " - " + punishmentType + " ID is " + punishment.getId());
               return punishment;
            }
         }
      }

      return null;
   }

   @Override
   public List findPunishments(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType, boolean flag) {
      List loginRecordsLimit = PunishmentQueries.findLoginRecordsLimitWithDefaults(this, text, 0, 2, null);
      if (loginRecordsLimit.isEmpty()) {
         return CollectionsKt.emptyList();
      }

      List arrayList = new ArrayList();

      for (LoginRecord loginRecord : (Iterable<LoginRecord>) loginRecordsLimit) {
         if (loginRecord.getUuid() != null && !Intrinsics.areEqual(loginRecord.getUuid(), text2)) {
            List history = this.getHistory(loginRecord.getUuid(), null, punishmentType, text3, true, 0);
            if (!history.isEmpty()) {
               arrayList.addAll(history);
            }
         }
      }

      if (!flag) {
         return arrayList;
      }

      Iterable iterable = arrayList;
      var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         Punishment punishment = (Punishment)obj;
         arrayList2.add(
            new Punishment(
               punishmentType,
               text2,
               punishment.getIp(),
               punishment.getReason(),
               punishment.getBannedByUuid(),
               punishment.getBannedByName(),
               punishment.getServerScope(),
               punishment.getServerOrigin(),
               punishment.getTime(),
               punishment.getUntil(),
               punishment.getTemplate(),
               punishment.isSilent(),
               punishment.isIpBan(),
               punishment.isActive(),
               punishment.getId(),
               null
            )
         );
      }

      return CollectionsKt.toMutableList(arrayList2);
   }

   @Override
   public Punishment findPunishment(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, boolean flag) {
      if (!TargetParser.TARGET_PARSER.isIpv4(text)) {
         return null;
      }

      HistoryQuery historyQuery = HistoryQueryFactory.createQueryWithDefaults(HistoryQuery.HISTORY_QUERY_FACTORY, this, text, text2, ExecutorSource.ID, flag, null, false, null, null, 240, null);
      ResultSet resultSet = HistoryQuery.computeResultSet(historyQuery, punishmentType, null, 2, null);
      Punishment nextPunishment = this.readNextPunishment(resultSet, punishmentType);
      return flag && !this.isAffecting(nextPunishment) ? null : nextPunishment;
   }

   @Override
   public Punishment findPunishmentById(long value, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag) {
      return this.findPunishment(String.valueOf(value), punishmentType, text, flag);
   }

   @Override
   public long countEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag) {
      try {
         CharSequence message = "SELECT COUNT(*) FROM " + punishmentTable;
         DatabaseSession databaseSession = this;
         boolean flag2 = true;
         ServerScopeClauseImpl serverScopeClause = new ServerScopeClauseImpl(databaseSession.getPlatform(), text, flag2);
         String text2 = "";
         if (flag) {
            StringBuilder builder = new StringBuilder().append("WHERE ");
            text2 = builder.append("active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)").toString();
         }

         Function1 callback = text2.length() == 0 ? DATABASE_SESSION_IMPL_COMPANION.obtainFunction2() : DATABASE_SESSION_IMPL_COMPANION.computeFunction(obj -> SqlUtil.access$bindLong(((PreparedQuery) obj)));
         String text3 = "uuid IS NOT NULL";
         String text4 = "AND";
         String text5 = text2.length() == 0
            ? " WHERE " + text3
            : (!StringsKt.containsWithDefaults((CharSequence)text2, (CharSequence)"WHERE", false, 2, null) ? " WHERE " + text2 + ' ' + text4 + ' ' + text3 : text2 + ' ' + text4 + ' ' + text3);
         if (text5.length() > 0) {
            message = String.valueOf(message) + ' ' + text5;
         }

         String text6 = String.valueOf(message) + serverScopeClause;
         Function1 callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj2 -> {
            PreparedQuery preparedQuery = (PreparedQuery) obj2;
            serverScopeClause.handlePreparedQuery(preparedQuery);
            return preparedQuery;
         });
         ResultSet resultSet = SqlUtil.computeResultSet2(this.prepare(text6), callback2);
         byte b = 1;
         Long boxed = resultSet.next() ? resultSet.getLong(b) : null;
         return boxed != null ? boxed : 0L;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean hasAllowEntry(@NotNull UUID uuid, @NotNull PunishmentType punishmentType) {
      try {
         PunishmentTable punishmentTable = PunishmentTable.ALLOW;
         CharSequence message = "1";
         String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
         CharSequence message2 = "uuid=?";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
         int value = 0;

         for (int i = messages.length; value < i; value++) {
            CharSequence message3 = messages[value];
            if (message3.length() <= 0) {
               String text = "Failed requirement.";
               throw new IllegalArgumentException(text.toString());
            }
         }

         return SqlUtil.computeResultSet2(this.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2))).bindUuid(uuid)).next();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getAllowEntries(@NotNull UUID uuid, int value) {
      PunishmentTable punishmentTable = PunishmentTable.ALLOW;
      CharSequence message = "uuid,type";
      byte b = 44;
      int value2 = 0;

      for (int i = 0; i < message.length(); i++) {
         char charAt2 = message.charAt(i);
         if (charAt2 == b) {
            value2++;
         }
      }

      int value3 = value2;
      String text = "?";
      if (value3 > 0) {
         StringBuilder stringBuilder = new StringBuilder(value3 * 2);
         Iterable kotlinIntProgressionSub = new KotlinIntProgressionSub(0, value3);
         Iterator iterator2 = kotlinIntProgressionSub.iterator();

         while (iterator2.hasNext()) {
            int value4 = ((KotlinIteratorImpl4)iterator2).obtainIntValue4();
            stringBuilder.append("?,");
         }

         text = StringsKt.computeText(stringBuilder.toString(), 1);
      }

      return SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.constructorImpl("INSERT INTO " + punishmentTable + '(' + message + ")VALUES(" + text + ')'))).bindUuid(uuid).bindInt(value));
   }

   @Override
   public ResultSet deleteAllowEntries(@NotNull UUID uuid) {
      PunishmentTable punishmentTable = PunishmentTable.ALLOW;
      String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable);
      CharSequence message = "uuid=?";
      CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text = "Failed requirement.";
            throw new IllegalArgumentException(text.toString());
         }
      }

      return SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message))).bindUuid(uuid));
   }

   @Override
   public List findLoginRecordsByTarget(@NotNull String text) {
      String text2 = text;
      if (TargetParser.TARGET_PARSER.checkText(text2)) {
         return PunishmentQueries.findLoginRecordsWithDefaults(this, text, 0, 2, null);
      }

      LoginRecord loginRecord = this.findLoginRecord(text);
      return loginRecord != null ? PunishmentQueries.findLoginRecordsWithDefaults(this, loginRecord.getUuid(), 0, 2, null) : CollectionsKt.emptyList();
   }

   @Override
   public LoginRecord findLoginRecord(@Nullable String text) {
      try {
         if (text == null) {
            return null;
         }

         if (Intrinsics.areEqual(text, "[CONSOLE]")) {
            return new LoginRecord("[CONSOLE]", "CONSOLE", "#", null, 8, null);
         }

         PunishmentTarget target = TargetParser.TARGET_PARSER.parseTarget(text);
         String text2 = target.obtainText();
         String text3 = DATABASE_SESSION_IMPL_COMPANION.computeText(this, text2);
         CommandQueueModule module = (CommandQueueModule)this.platform.getModule(CommandQueueModule.class);
         if (!module.getArrayBlockingQueue().isEmpty() && target.getByteValue() == 0 || target.getByteValue() == 1) {
            Iterable arrayBlockingQueue = module.getArrayBlockingQueue();
            Iterator iterator2 = arrayBlockingQueue.iterator();

            Object obj;
            while (true) {
               if (!iterator2.hasNext()) {
                  obj = null;
                  break;
               }

               Object obj2 = iterator2.next();
               PlayerInfo playerInfo = (PlayerInfo)obj2;
               if (Intrinsics.areEqual(playerInfo.getName(), text) || Intrinsics.areEqual(playerInfo.getUuid(), text)) {
                  obj = obj2;
                  break;
               }
            }

            PlayerInfo playerInfo2 = (PlayerInfo)obj;
            if (playerInfo2 != null) {
               return new LoginRecord(playerInfo2.getName(), playerInfo2.getUuid(), playerInfo2.getIp(), null, 8, null);
            }
         }

         PreparedQuery prepare2 = this.prepare("SELECT " + this.broadcastModule.getHistoryColumns() + " FROM " + PunishmentTable.HISTORY + " WHERE " + text3 + " ORDER BY date DESC LIMIT 1");
         String text4 = target.getTarget();
         prepare2.bindString(text4);
         ResultSet resultSet = SqlUtil.computeResultSet(prepare2);
         return resultSet.next() ? this.readLoginRecord(resultSet, true, this.configModule.getSettings().getSqlDriver()) : null;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public List findLoginRecordsLimit(@Nullable String text, int value) {
      try {
         if (text == null) {
            return CollectionsKt.emptyList();
         }

         if (Intrinsics.areEqual(text, "[CONSOLE]")) {
            return CollectionsKt.computeList(new LoginRecord("[CONSOLE]", "CONSOLE", "#", null, 8, null));
         }

         PunishmentTarget target = TargetParser.TARGET_PARSER.parseTarget(text);
         String text2 = target.obtainText();
         String text3 = DATABASE_SESSION_IMPL_COMPANION.computeText(this, text2);
         PreparedQuery prepare2 = this.prepare("SELECT " + this.broadcastModule.getHistoryColumns() + " FROM " + PunishmentTable.HISTORY + " WHERE " + text3 + " ORDER BY date DESC LIMIT " + value);
         String text4 = target.getTarget();
         prepare2.bindString(text4);
         ResultSet resultSet = SqlUtil.computeResultSet(prepare2);
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();

         while (resultSet.next()) {
            LoginRecord loginRecord = this.readLoginRecord(resultSet, true, this.configModule.getSettings().getSqlDriver());
            if (loginRecord.getUuid() != null && !arrayList.contains(loginRecord.getUuid())) {
               arrayList.add(loginRecord.getUuid());
               arrayList2.add(loginRecord);
            }
         }

         return arrayList2;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public long getPunishmentId(@NotNull Punishment punishment) {
      try {
         Punishment punishment2 = punishment;
         PunishmentTable punishmentTable = punishment2.getType().getPunishmentTable();
         CharSequence message = "{column.id}";
         String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
         CharSequence message2 = "uuid=?";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
         int value = 0;

         for (int i = messages.length; value < i; value++) {
            CharSequence message3 = messages[value];
            if (message3.length() <= 0) {
               String text = "Failed requirement.";
               throw new IllegalArgumentException(text.toString());
            }
         }

         ResultSet resultSet = SqlUtil.computeResultSet(
            this.prepare(SqlText.box(SqlText.limit(SqlText.orderByDesc(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2), "time=?"), "id"), 1))).bindString(punishment2.getUuid()).bindLong(punishment2.getTime())
         );
         String text2 = "id";
         Long boxed = resultSet.next() ? resultSet.getLong(text2) : null;
         return boxed != null ? boxed : 0L;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean checkText(@Nullable String text, @Nullable String text2, @NotNull String text3) {
      try {
         DatabaseSessionImpl databaseSession;
         boolean flag;
         label56: {
            databaseSession = this;
            if (text != null) {
               int value = text.length();
               byte usernameMaxLength = databaseSession.getConfigModule().getSettings().getUsernameMaxLength();
               if (Intrinsics.compare(value, usernameMaxLength & 255) > 0) {
                  flag = false;
                  break label56;
               }
            }

            flag = true;
         }

         if (!flag) {
            String text4 = "Player name \"" + text + "\" length > " + UByte.toStringImpl(databaseSession.getConfigModule().getSettings().getUsernameMaxLength());
            throw new IllegalArgumentException(text4.toString());
         }

         if (text2 != null && text2.length() > 36) {
            String text5 = "UUID \"" + text2 + "\" length > 36";
            throw new IllegalArgumentException(text5.toString());
         }

         PunishmentTable punishmentTable = PunishmentTable.HISTORY;
         CharSequence message = "*";
         String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
         CharSequence message2 = "name{=?}";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
         int value2 = 0;

         for (int i = messages.length; value2 < i; value2++) {
            CharSequence message3 = messages[value2];
            if (message3.length() <= 0) {
               String text6 = "Failed requirement.";
               throw new IllegalArgumentException(text6.toString());
            }
         }

         PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.limit(SqlText.and(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2), "uuid=?"), "ip=?"), 1)));
         String normalizeNullable2 = TargetParser.TARGET_PARSER.normalizeNullable(text2);
         String[] texts = new String[]{text, normalizeNullable2, text3};
         int value3 = 0;

         for (int i2 = texts.length; value3 < i2; value3++) {
            String text7 = texts[value3];
            prepare2.bindString(text7);
         }

         return SqlUtil.computeResultSet(prepare2).next();
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void handleConnection(@Nullable Connection connection, @NotNull PunishmentTable punishmentTable, boolean flag) {
      try {
         if (connection != null) {
            boolean flag2 = punishmentTable.obtainFlag();
            PunishmentTable punishmentTable2 = punishmentTable;
            boolean flag3 = punishmentTable2 == PunishmentTable.HISTORY;
            if (flag) {
               PunishmentTable punishmentTable3 = punishmentTable;
               if (punishmentTable3 == PunishmentTable.SYNC) {
                  return;
               }

               PunishmentTable punishmentTable4 = punishmentTable;
               if (punishmentTable4 == PunishmentTable.SERVERS || punishmentTable == PunishmentTable.CONFIG) {
                  return;
               }
            }

            String text = flag ? "h2" : "mysql";
            String text2 = PunishmentTable.computeText(punishmentTable, null, flag, 1, null);
            var prepareStatement2 = connection.prepareStatement("SELECT * FROM " + text2);
            Throwable error = null;

            try {
               PreparedStatement preparedStatement = (PreparedStatement)prepareStatement2;
               var executeQuery2 = preparedStatement.executeQuery();
               Throwable error2 = null;

               try {
                  ResultSet resultSet = (ResultSet)executeQuery2;
                  if (!flag2) {
                     if (flag3) {
                        while (resultSet.next()) {
                           Intrinsics.checkNotNull(resultSet);
                           LoginRecord loginRecord = this.readLoginRecord(resultSet, false, text);
                           String component12 = loginRecord.component1();
                           String component22 = loginRecord.component2();
                           String component32 = loginRecord.component3();
                           PunishmentQueries.saveLoginWithDefaults(this, component12, component22, component32, false, false, 16, null);
                        }
                     }
                  } else {
                     while (resultSet.next()) {
                        Intrinsics.checkNotNull(resultSet);
                        Punishment punishment = this.readPunishment(resultSet, punishmentTable.obtainPunishmentType());
                        Punishment existingPunishment = PunishmentQueries.findExistingPunishmentWithDefaults(this, punishment, false, 2, null);
                        if (existingPunishment == null || existingPunishment.isIpBan() && !Intrinsics.areEqual(existingPunishment.getUuid(), punishment.getUuid())) {
                           try {
                              this.insertPunishment(punishment);
                           } catch (Throwable e2) {
                              if (!(e2 instanceof SQLException)) {
                                 throw SneakyThrow.<RuntimeException>rethrow(e2);
                              }
                              SQLException sqlException = (SQLException) e2;
                              this.platform.getLogger().severe("Failed to add " + punishment.getType() + ": " + sqlException.getMessage() + " - " + punishment);
                              if (this.configModule.isDebugAtLeast(1)) {
                                 sqlException.printStackTrace();
                              }
                           }
                        }
                     }
                  }

               } catch (Throwable e3) {
                  error2 = e3;
                  throw SneakyThrow.<RuntimeException>rethrow(e3);
               } finally {
                  KotlinUtil15.handleAutoCloseable(executeQuery2, error2);
               }

            } catch (Throwable e4) {
               error = e4;
               throw SneakyThrow.<RuntimeException>rethrow(e4);
            } finally {
               KotlinUtil15.handleAutoCloseable(prepareStatement2, error);
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Punishment readPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      try {
         PunishmentType punishmentType2 = punishmentType.obtainPunishmentType();
         String normalize2 = TargetParser.normalize(resultSet.getString("uuid"));
         String string = resultSet.getString("ip");
         CharSequence string2 = resultSet.getString("reason");
         String string3 = resultSet.getString("banned_by_uuid");
         if (string3 == null) {
            string3 = "#";
         }

         String string4 = resultSet.getString("banned_by_name");
         ResultSet resultSet2 = resultSet;
         String text = "server_scope";
         String text2 = string3;

         String text3;
         try {
            text3 = resultSet2.getString(text);
         } catch (SQLException e2) {
            text3 = null;
         }

         String text4 = text3;
         ResultSet resultSet3 = resultSet;
         String text5 = "server_origin";

         String text6;
         try {
            text6 = resultSet3.getString(text5);
         } catch (SQLException e3) {
            text6 = null;
         }

         String text7 = text6;
         long long2 = resultSet.getLong("time");
         long long3 = resultSet.getLong("until");
         byte constructor = UByte.constructorImpl((byte)resultSet.getInt("template"));
         boolean boolean2 = resultSet.getBoolean("silent");
         boolean boolean3 = resultSet.getBoolean("ipban");
         boolean boolean4 = resultSet.getBoolean("active");
         Object obj = null;
         short value = 16384;
         long value2 = 0L;
         Punishment punishment = new Punishment(punishmentType2, normalize2, string, string2, text2, string4, text4, text7, long2, long3, constructor, boolean2, boolean3, boolean4, value2, value, (DefaultConstructorMarker)obj);
         if (punishmentType == PunishmentType.WARN) {
            punishment.setWarned(resultSet.getBoolean("warned"));
         }

         if (!this.broadcastModule.isSqliteResult(resultSet)) {
            punishment.setId(resultSet.getLong("id"));
         }

         if (punishmentType != PunishmentType.KICK) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeZone(TimeZone.getTimeZone("UTC"));
            Timestamp timestamp = resultSet.getTimestamp("removed_by_date");
            if (timestamp != null) {
               instance.setTime(timestamp);
               punishment.setRemovedByDate(instance.getTimeInMillis() + this.getDatabaseModule().getDatabaseTimeOffset());
            } else {
               punishment.setRemovedByDate(0L);
            }

            ResultSet resultSet4 = resultSet;
            String text8 = "removed_by_uuid";
            TargetParser targetParser = TargetParser.TARGET_PARSER;

            String text9;
            try {
               text9 = resultSet4.getString(text8);
            } catch (SQLException e4) {
               text9 = null;
            }

            String text10 = text9;
            punishment.setRemovedByUuid(targetParser.normalizeNullable(text10));
            ResultSet resultSet5 = resultSet;
            String text11 = "removed_by_name";

            String text12;
            try {
               text12 = resultSet5.getString(text11);
            } catch (SQLException e5) {
               text12 = null;
            }

            punishment.setRemovedByName(text12);
            ResultSet resultSet6 = resultSet;
            String text13 = "removed_by_reason";

            String text14;
            try {
               text14 = resultSet6.getString(text13);
            } catch (SQLException e6) {
               text14 = null;
            }

            punishment.setRemovedByReason(text14);
         }

         return punishment;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Punishment readNextPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      try {
         return resultSet.next() ? this.readPunishment(resultSet, punishmentType) : null;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PreparedQuery prepare(@NotNull CharSequence message) {
      try {
         ConfigModule configModule = this.configModule;
         if (configModule.isVerbose()) {
            configModule.logDebug(message);
         }

         PreparedStatement prepareStatement2 = this.connection.prepareStatement(message.toString());
         if (prepareStatement2 == null) {
            StandardKt.notImplemented();
            throw new CommandAbortException();
         } else {
            String text = message instanceof SqlText ? ((SqlText)message).unbox() : null;
            return Intrinsics.areEqual(this.configModule.getSettings().getSqlDriver(), "pgsql") ? new PreparedQueryImpl(this, prepareStatement2, text, null) : new PreparedQuery(this, prepareStatement2, text, null);
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void expireEntries(@Nullable String text, long value) {
      String normalizeNullable2 = TargetParser.TARGET_PARSER.normalizeNullable(text);
      String text2 = "(active={false} OR(until>0 AND " + DatabaseModule.getCurrentTimeWithDefaults(this.getDatabaseModule(), false, 1, null) + ">until))";
      PunishmentTable[] punishmentTables = PunishmentTable.values();
      var arrayList = new ArrayList();
      int value2 = 0;

      for (int i = punishmentTables.length; value2 < i; value2++) {
         PunishmentTable punishmentTable = punishmentTables[value2];
         if (punishmentTable.obtainFlag()) {
            arrayList.add(punishmentTable);
         }
      }


      for (Object obj : arrayList) {
         PunishmentTable punishmentTable2 = (PunishmentTable)obj;
         String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable2);
         CharSequence message = "uuid=?";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message};
         int value3 = 0;

         for (int i2 = messages.length; value3 < i2; value3++) {
            CharSequence message2 = messages[value3];
            if (message2.length() <= 0) {
               String text3 = "Failed requirement.";
               throw new IllegalArgumentException(text3.toString());
            }
         }

         String text4 = SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message), "time>?");
         if (punishmentTable2 != PunishmentTable.KICKS) {
            text4 = SqlText.and(text4, text2);
         }

         PreparedQuery prepare2 = this.prepare(SqlText.box(text4));
         prepare2.bindString(normalizeNullable2);
         SqlUtil.computeResultSet(prepare2.bindLong(value));
      }
   }

   @Override
   public int countTemplateUses(@NotNull Template template, @Nullable String text, @Nullable String text2) {
      try {
         if (text == null) {
            return 0;
         }

         boolean flag = template.isFlag2() && text2 != null && !StringsKt.containsWithDefaults(text2, '#', false, 2, null);
         PunishmentTable punishmentTable = template.getType().getPunishmentTable();
         Function1 callback = DATABASE_SESSION_IMPL_COMPANION.obtainFunction2();
         CharSequence message = "COUNT(*)";
         String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
         Function1 callback2;
         String text3;
         if (flag) {
            CharSequence message2 = "(uuid=? OR ipban={true} AND ip=?)";
            CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
            int value = 0;

            for (int i = messages.length; value < i; value++) {
               CharSequence message3 = messages[value];
               if (message3.length() <= 0) {
                  String text4 = "Failed requirement.";
                  throw new IllegalArgumentException(text4.toString());
               }
            }

            text3 = SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2), "template=?");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj -> {
               PreparedQuery preparedQuery = (PreparedQuery) obj;
               String[] texts = new String[]{text, text2};
               int value4 = 0;

               for (int i3 = texts.length; value4 < i3; value4++) {
                  String text32 = texts[value4];
                  preparedQuery.bindString(text32);
               }

               return preparedQuery.bindInt(template.getId());
            });
         } else {
            CharSequence message4 = "uuid=?";
            CharSequence[] messages2 = new CharSequence[]{SqlText.box(constructor), message4};
            int value2 = 0;

            for (int i2 = messages2.length; value2 < i2; value2++) {
               CharSequence message5 = messages2[value2];
               if (message5.length() <= 0) {
                  String text5 = "Failed requirement.";
                  throw new IllegalArgumentException(text5.toString());
               }
            }

            text3 = SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message4), "template=?");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj2 -> {
               PreparedQuery bindString2 = ((PreparedQuery) obj2).bindString(text);
               return bindString2.bindInt(template.getId());
            });
         }

         if (template.getType() == PunishmentType.WARN) {
            text3 = SqlText.and(text3, "active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback2, obj3 -> SqlUtil.access$bindLong(((PreparedQuery) obj3)));
         }

         if (template.getEscalationWindow() > 0L) {
            long value3 = DatabaseModule.getCurrentTimeWithDefaults(this.getDatabaseModule(), false, 1, null) - template.getEscalationWindow();
            text3 = SqlText.and(text3, "time>?");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback2, obj4 -> ((PreparedQuery) obj4).bindLong(value3));
         }

         ResultSet resultSet = SqlUtil.computeResultSet(this.prepare(SqlText.box(text3)), callback2);
         byte b2 = 1;
         Integer boxed = resultSet.next() ? resultSet.getInt(b2) : null;
         return boxed != null ? boxed : 0;
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Pair computePair(@Nullable String text, @Nullable String text2, @NotNull TemplateGroup templateGroup, @NotNull Template template) {
      try {
         if (text == null) {
            return TuplesKt.computePair(0.0, 0.0);
         }

         boolean flag = template.isFlag2() && text2 != null && !StringsKt.containsWithDefaults(text2, '#', false, 2, null);
         PunishmentTable punishmentTable = templateGroup.getPunishmentType().getPunishmentTable();
         CharSequence message = "template";
         String constructor = SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable);
         Function1 callback = DATABASE_SESSION_IMPL_COMPANION.obtainFunction2();
         String text3;
         Function1 callback2;
         if (flag) {
            CharSequence message2 = "(uuid=? OR ipban={true} AND ip=?)";
            CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
            int value = 0;

            for (int i = messages.length; value < i; value++) {
               CharSequence message3 = messages[value];
               if (message3.length() <= 0) {
                  String text4 = "Failed requirement.";
                  throw new IllegalArgumentException(text4.toString());
               }
            }

            text3 = SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2);
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj -> {
               PreparedQuery preparedQuery = (PreparedQuery) obj;
               String[] texts5 = new String[]{text, text2};
               int value4 = 0;

               for (int i3 = texts5.length; value4 < i3; value4++) {
                  String text32 = texts5[value4];
                  preparedQuery.bindString(text32);
               }

               return preparedQuery;
            });
         } else {
            CharSequence message4 = "uuid=?";
            CharSequence[] messages2 = new CharSequence[]{SqlText.box(constructor), message4};
            int value2 = 0;

            for (int i2 = messages2.length; value2 < i2; value2++) {
               CharSequence message5 = messages2[value2];
               if (message5.length() <= 0) {
                  String text5 = "Failed requirement.";
                  throw new IllegalArgumentException(text5.toString());
               }
            }

            text3 = SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message4);
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj2 -> ((PreparedQuery) obj2).bindString(text));
         }

         if (templateGroup.getPunishmentType() == PunishmentType.WARN) {
            text3 = SqlText.and(text3, "active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback2, obj3 -> SqlUtil.access$bindLong(((PreparedQuery) obj3)));
         }

         if (templateGroup.getEscalationWindow() > 0L) {
            long value3 = DatabaseModule.getCurrentTimeWithDefaults(this.getDatabaseModule(), false, 1, null) - templateGroup.getEscalationWindow();
            text3 = SqlText.and(text3, "time>?");
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback2, obj4 -> ((PreparedQuery) obj4).bindLong(value3));
         }

         String text6 = "";
         Map map = templateGroup.getMap3();

         for (Entry entry : (Iterable<Entry>) map.entrySet()) {
            Template key = (Template)entry.getKey();
            text6 = text6 + "template=? OR ";
            callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback2, obj5 -> ((PreparedQuery) obj5).bindInt(key.getId()));
         }

         String substring2 = text6.substring(0, text6.length() - 4);
         String text7 = SqlText.and(text3, '(' + substring2 + ')');
         ResultSet resultSet = SqlUtil.computeResultSet(this.prepare(SqlText.box(text7)), callback2);
         double number = 0.0;

         while (true) {
            byte b2 = 1;
            Integer boxed = resultSet.next() ? resultSet.getInt(b2) : null;
            if (boxed == null) {
               break;
            }

            byte constructor2 = UByte.constructorImpl((byte)boxed.intValue());
            Template template2 = (Template)templateGroup.getMap4().get(UByte.box(constructor2));
            if (template2 == null) {
               break;
            } else {
               Object obj6 = templateGroup.getMap3().get(template2);
               Intrinsics.checkNotNull(obj6);
               float floatValue2 = ((Number)obj6).floatValue();
               number += floatValue2;
               ConfigModule configModule = this.configModule;
               byte b3 = 10;
               if (configModule.isDebugAtLeast(b3)) {
                  configModule.logDebug('[' + templateGroup.getName() + "] Add " + NumberFormats.computeText2(floatValue2) + " from " + template2.getName() + " = " + NumberFormats.computeText2(number));
               }

               ConfigModule configModule2 = this.configModule;
               if (configModule2.isVerbose()) {
                  CommandArguments commandArguments;
                  ConfigModule configModule3 = this.configModule;
                  String text8 = "a:" + floatValue2;
                  String[] texts;
                  if (Intrinsics.areEqual(text8, "sort")) {
                     String[] texts2 = new String[]{"00"};
                     texts = texts2;
                  } else {
                     String[] texts3 = new String[]{"01:" + text8};
                     texts = texts3;
                  }

                  String[] texts4 = texts;
                  commandArguments = new CommandArguments(configModule3, Arrays.copyOf(texts4, texts4.length));
                  configModule2.logDebug(commandArguments);
               }
            }
         }

         Object obj7 = templateGroup.getMap3().get(template);
         Intrinsics.checkNotNull(obj7);
         float floatValue3 = ((Number)obj7).floatValue();
         double number2 = number + floatValue3;
         return TuplesKt.computePair(number, number2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public ResultSet getEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, int value) {
      DatabaseSession databaseSession = this;
      boolean flag = false;
      ServerScopeClauseImpl serverScopeClause = new ServerScopeClauseImpl(databaseSession.getPlatform(), text, flag);
      if (value == 1) {
         if (!punishmentTable.obtainFlag()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         PunishmentTable punishmentTable2 = punishmentTable;
         return SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable2), "active={false}"))));
      } else if (value == 2) {
         if (!punishmentTable.obtainFlag()) {
            return ResultSetUtil.obtainProxyResultSet();
         }

         PunishmentTable punishmentTable3 = punishmentTable;
         String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable3);
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), serverScopeClause};
         int value2 = 0;

         for (int i = messages.length; value2 < i; value2++) {
            CharSequence message = messages[value2];
            if (message.length() <= 0) {
               String text2 = "Failed requirement.";
               throw new IllegalArgumentException(text2.toString());
            }
         }

         PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + serverScopeClause), "active={false}")));
         serverScopeClause.handlePreparedQuery(prepare2);
         return SqlUtil.computeResultSet(prepare2);
      } else {
         ResultSet resultSet;
         if (ServerScopes.SERVER_SCOPE.isGlobal(text)) {
            PunishmentTable punishmentTable4 = punishmentTable;
            resultSet = SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.constructorImpl("DROP TABLE " + punishmentTable4))));
         } else {
            PunishmentTable punishmentTable5 = punishmentTable;
            String constructor2 = SqlText.constructorImpl("DELETE FROM " + punishmentTable5);
            CharSequence[] messages2 = new CharSequence[]{SqlText.box(constructor2), serverScopeClause};
            int value3 = 0;

            for (int i2 = messages2.length; value3 < i2; value3++) {
               CharSequence message2 = messages2[value3];
               if (message2.length() <= 0) {
                  String text3 = "Failed requirement.";
                  throw new IllegalArgumentException(text3.toString());
               }
            }

            PreparedQuery prepare3 = this.prepare(SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor2) + " WHERE " + serverScopeClause)));
            serverScopeClause.handlePreparedQuery(prepare3);
            resultSet = SqlUtil.computeResultSet(prepare3);
         }

         return resultSet;
      }
   }

   @Override
   public ResultSet resetTemplates(@NotNull PunishmentTable punishmentTable) {
      PunishmentTable punishmentTable2 = punishmentTable;
      return SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable2), "template=255"))));
   }

   @Override
   public void updateExpiry(@NotNull CharSequence message, long value, @NotNull Punishment punishment) {
      DatabaseSession databaseSession = this;
      String serverScope = punishment.getServerScope();
      boolean flag = true;
      ServerScopeClauseImpl serverScopeClause = new ServerScopeClauseImpl(databaseSession.getPlatform(), serverScope, flag);
      Punishment punishment2 = punishment;
      PunishmentTable punishmentTable = punishment2.getType().getPunishmentTable();
      String text = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable), "until=?,reason=?");
      CharSequence message2 = "id=?";
      CharSequence[] messages = new CharSequence[]{SqlText.box(text), message2};
      int value2 = 0;

      for (int i = messages.length; value2 < i; value2++) {
         CharSequence message3 = messages[value2];
         if (message3.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      String constructor = SqlText.constructorImpl(SqlText.expandImpl(text) + " WHERE " + message2);
      String text3 = SqlText.and(constructor, "active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)");
      String constructor2 = SqlText.constructorImpl(SqlText.expandImpl(text3) + serverScopeClause);
      PreparedQuery bindLong2 = this.prepare(SqlText.box(constructor2)).bindLong(value);
      bindLong2.bindString(String.valueOf(message));
      PreparedQuery preparedQuery = SqlUtil.access$bindLong(bindLong2.bindLong(punishment.getId()));
      serverScopeClause.handlePreparedQuery(preparedQuery);
      SqlUtil.computeResultSet(preparedQuery);
   }

   @Override
   public void handleText(@Nullable String text, @Nullable String text2, @NotNull CharSequence message, @NotNull Punishment punishment, boolean flag) {
      String text3 = text2 != null ? TextUtil.access$length(text2, 128) : null;
      DatabaseSession databaseSession = this;
      String serverScope = punishment.getServerScope();
      boolean flag2 = true;
      ServerScopeClauseImpl serverScopeClause = new ServerScopeClauseImpl(databaseSession.getPlatform(), serverScope, flag2);
      boolean flag3 = punishment.isIpBan() && punishment.getIp() != null && !StringsKt.containsWithDefaults(punishment.getIp(), '#', false, 2, null);
      Pair pair;
      if (flag) {
         Punishment punishment2 = punishment;
         pair = TuplesKt.computePair("id", String.valueOf(punishment2.getId()));
      } else {
         pair = flag3 ? TuplesKt.computePair("ip", punishment.getIp()) : TuplesKt.computePair("uuid", punishment.getUuid());
      }

      Pair pair2 = pair;
      Punishment punishment3 = punishment;
      PunishmentTable punishmentTable = punishment3.getType().getPunishmentTable();
      String text4 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable), "active={false},removed_by_date=NOW(),removed_by_uuid=?,removed_by_name=?,removed_by_reason=?");
      CharSequence message2 = (String)pair2.getFirst() + "=?";
      CharSequence[] messages = new CharSequence[]{SqlText.box(text4), message2};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message3 = messages[value];
         if (message3.length() <= 0) {
            String text5 = "Failed requirement.";
            throw new IllegalArgumentException(text5.toString());
         }
      }

      String constructor = SqlText.constructorImpl(SqlText.expandImpl(text4) + " WHERE " + message2);
      String text6 = SqlText.and(constructor, "active=" + this.getBroadcastModule().getSqlTrue() + " AND(until<1 OR until>?)");
      String constructor2 = SqlText.constructorImpl(SqlText.expandImpl(text6) + serverScopeClause);
      ConfigModule configModule = this.configModule;
      if (configModule.isVerbose()) {
         configModule.logDebug(SqlText.expandImpl(constructor2) + ' ' + punishment.getServerScope() + ' ' + pair2);
      }

      PreparedQuery prepare2 = this.prepare(SqlText.box(constructor2));
      String[] texts = new String[]{text, text3, message.toString(), (String)pair2.getSecond()};
      int value2 = 0;

      for (int i2 = texts.length; value2 < i2; value2++) {
         String text7 = texts[value2];
         prepare2.bindString(text7);
      }

      PreparedQuery preparedQuery = SqlUtil.access$bindLong(prepare2);
      serverScopeClause.handlePreparedQuery(preparedQuery);
      SqlUtil.computeResultSet(preparedQuery);
      punishment.handleText(text3, text, message);
   }

   @Override
   public void handlePunishment(@NotNull Punishment punishment, @Nullable String text, @Nullable String text2, @NotNull CharSequence message, boolean flag) {
      if (flag) {
         Punishment punishment2 = punishment;
         PunishmentTable punishmentTable = punishment2.getType().getPunishmentTable();
         String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable);
         CharSequence message2 = "id=?";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message2};
         int value = 0;

         for (int i = messages.length; value < i; value++) {
            CharSequence message3 = messages[value];
            if (message3.length() <= 0) {
               String text3 = "Failed requirement.";
               throw new IllegalArgumentException(text3.toString());
            }
         }

         String constructor2 = SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message2);
         SqlUtil.computeResultSet(this.prepare(SqlText.box(constructor2)).bindLong(punishment.getId()));
      } else {
         Punishment punishment3 = punishment;
         PunishmentTable punishmentTable2 = punishment3.getType().getPunishmentTable();
         String text4 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable2), "active={false},removed_by_date=NOW(),removed_by_uuid=?,removed_by_name=?,removed_by_reason=?");
         CharSequence message4 = "id=?";
         CharSequence[] messages2 = new CharSequence[]{SqlText.box(text4), message4};
         int value2 = 0;

         for (int i2 = messages2.length; value2 < i2; value2++) {
            CharSequence message5 = messages2[value2];
            if (message5.length() <= 0) {
               String text5 = "Failed requirement.";
               throw new IllegalArgumentException(text5.toString());
            }
         }

         String constructor3 = SqlText.constructorImpl(SqlText.expandImpl(text4) + " WHERE " + message4);
         ConfigModule configModule = this.configModule;
         if (configModule.isVerbose()) {
            SqlText sqlText = SqlText.box(constructor3);
            CharSequence message6 = " id=" + punishment.getId();
            configModule.logDebug("" + sqlText + message6);
         }

         PreparedQuery prepare2 = this.prepare(SqlText.box(constructor3));
         prepare2.bindString(text);
         String text6 = text2 != null ? TextUtil.access$length(text2, 128) : null;
         prepare2.bindString(text6);
         CharSequence message7 = message;
         CharSequence message8;
         if (StringsKt.isBlank(message7)) {
            message8 = null;
         } else {
            message8 = message7;
         }

         CharSequence message9 = message8;
         prepare2.bindString(String.valueOf(message9));
         SqlUtil.computeResultSet(prepare2.bindLong(punishment.getId()));
         punishment.handleText(text2, text, message);
      }
   }

   @Override
   public long computeLongValue(@NotNull String text, long value, @NotNull String text2) {
      DatabaseSession databaseSession = this;
      ServerScopeClause serverScopeClause = new ServerScopeClause(databaseSession.getPlatform(), text2);
      PunishmentTable[] punishmentTables = PunishmentTable.values();
      var arrayList = new ArrayList();
      int value2 = 0;

      for (int i = punishmentTables.length; value2 < i; value2++) {
         PunishmentTable punishmentTable = punishmentTables[value2];
         if (punishmentTable.obtainFlag()) {
            arrayList.add(punishmentTable);
         }
      }

      int value3 = 0;

      for (Object obj : arrayList) {
         PunishmentTable punishmentTable2 = (PunishmentTable)obj;
         int value4 = value3;
         String constructor = SqlText.constructorImpl("DELETE FROM " + punishmentTable2);
         CharSequence message = "banned_by_uuid=?";
         CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message};
         int value5 = 0;

         for (int i2 = messages.length; value5 < i2; value5++) {
            CharSequence message2 = messages[value5];
            if (message2.length() <= 0) {
               String text3 = "Failed requirement.";
               throw new IllegalArgumentException(text3.toString());
            }
         }

         PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message), "time>?" + serverScopeClause)));
         prepare2.bindString(text);
         PreparedQuery bindLong2 = prepare2.bindLong(value);
         serverScopeClause.handlePreparedQuery(bindLong2);
         int value6 = SqlUtil.computeIntValue(bindLong2);
         value3 = value4 + value6;
      }

      long value7 = value3;
      Function1 callback = DATABASE_SESSION_IMPL_COMPANION.computeFunction(obj2 -> {
         PreparedQuery preparedQuery = (PreparedQuery) obj2;
         preparedQuery.bindString(text);
         return preparedQuery;
      });
      if (value > 0L) {
         Timestamp timestamp = new Timestamp(value);
         callback = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj3 -> {
            PreparedQuery preparedQuery = (PreparedQuery) obj3;
            preparedQuery.bindTimestamp(timestamp);
            return preparedQuery;
         });
      }

      Function1 callback2 = DATABASE_SESSION_IMPL_COMPANION.computeFunction2(callback, obj4 -> {
         PreparedQuery preparedQuery = (PreparedQuery) obj4;
         serverScopeClause.handlePreparedQuery(preparedQuery);
         return preparedQuery;
      });
      PunishmentTable[] punishmentTables2 = PunishmentTable.values();
      var arrayList2 = new ArrayList();
      int value8 = 0;

      for (int i3 = punishmentTables2.length; value8 < i3; value8++) {
         PunishmentTable punishmentTable3 = punishmentTables2[value8];
         if (punishmentTable3.obtainFlag() && punishmentTable3 != PunishmentTable.KICKS) {
            arrayList2.add(punishmentTable3);
         }
      }


      for (Object obj5 : arrayList2) {
         PunishmentTable punishmentTable4 = (PunishmentTable)obj5;
         String text4 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable4), "active={true},removed_by_name=NULL,removed_by_uuid=NULL,removed_by_reason=NULL");
         SqlText sqlText = SqlText.box(SqlValidation.computeText("active"));
         CharSequence[] messages2 = new CharSequence[]{SqlText.box(text4), sqlText};
         int value9 = 0;

         for (int i4 = messages2.length; value9 < i4; value9++) {
            CharSequence message3 = messages2[value9];
            if (message3.length() <= 0) {
               String text5 = "Failed requirement.";
               throw new IllegalArgumentException(text5.toString());
            }
         }

         String text6 = SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(text4) + " WHERE " + sqlText), "removed_by_uuid=?" + serverScopeClause);
         if (value > 0L) {
            text6 = SqlText.and(text6, "removed_by_date>?");
         }

         SqlUtil.computeResultSet(this.prepare(SqlText.box(text6)), callback2);
      }

      if (this.configModule.getSettings().isMutesEnabled()) {
         PunishmentLookupModule punishmentLookupModule = this.punishmentLookupModule;
         punishmentLookupModule.handleDatabaseSession(this);
      }

      return value7;
   }

   @Override
   public ResultSet markWarned(@NotNull String text) {
      PunishmentTable punishmentTable = PunishmentTable.WARNINGS;
      String text2 = SqlBooleanFormat.computeText(SqlText.constructorImpl("UPDATE " + punishmentTable), "warned");
      CharSequence message = "uuid=?";
      CharSequence[] messages = new CharSequence[]{SqlText.box(text2), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text3 = "Failed requirement.";
            throw new IllegalArgumentException(text3.toString());
         }
      }

      PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(text2) + " WHERE " + message), SqlText.box(SqlValidation.computeText("warned")))));
      prepare2.bindString(text);
      return SqlUtil.computeResultSet(prepare2);
   }

   @Override
   public void updateExpired(boolean flag) {
      try {
         try {
            if (!this.configModule.getSettings().isDisableExpiryUpdate()) {
               long currentTime = DatabaseModule.getCurrentTimeWithDefaults(this.getDatabaseModule(), false, 1, null);
               if (currentTime - this.getDatabaseModule().getLastExpiredUpdate() > 72000000L || flag) {
                  SyncModule module = (SyncModule)this.platform.getModule(SyncModule.class);
                  DatabaseModule databaseModule = this.getDatabaseModule();
                  byte b2 = 0;
                  databaseModule.setLastExpiredUpdate(currentTime + b2);
                  SyncModule.sendSyncMessageWithDefaults(module, SyncMessageType.EXPIRED_NOTIFY, this, null, 0, 12, null);
                  var statement = this.connection.createStatement();
                  Throwable error = null;

                  try {
                     Statement statement2 = (Statement)statement;
                     PunishmentTable[] punishmentTables = PunishmentTable.values();
                     var arrayList = new ArrayList();
                     int value = 0;

                     for (int i = punishmentTables.length; value < i; value++) {
                        PunishmentTable punishmentTable = punishmentTables[value];
                        if (punishmentTable.obtainFlag() && punishmentTable != PunishmentTable.KICKS) {
                           arrayList.add(punishmentTable);
                        }
                     }


                     for (Object obj : arrayList) {
                        PunishmentTable punishmentTable2 = (PunishmentTable)obj;
                        Intrinsics.checkNotNull(statement2);
                        String text = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable2), "active={false},removed_by_name='#expired',removed_by_date=NOW()");
                        CharSequence message = "active={true}";
                        CharSequence[] messages = new CharSequence[]{SqlText.box(text), message};
                        int value2 = 0;

                        for (int i2 = messages.length; value2 < i2; value2++) {
                           CharSequence message2 = messages[value2];
                           if (message2.length() <= 0) {
                              String text2 = "Failed requirement.";
                              throw new IllegalArgumentException(text2.toString());
                           }
                        }

                        SqlText sqlText = SqlText.box(SqlText.and(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(text) + " WHERE " + message), "until>0"), "until<" + currentTime));
                        statement2.addBatch(sqlText.toString());
                     }

                     long value3 = this.configModule.getSettings().getDurationsEffectiveIpbanDuration();
                     if (value3 > 0L) {
                        PunishmentTable[] punishmentTables2 = PunishmentTable.values();
                        var arrayList2 = new ArrayList();
                        int value4 = 0;

                        for (int i3 = punishmentTables2.length; value4 < i3; value4++) {
                           PunishmentTable punishmentTable3 = punishmentTables2[value4];
                           if (punishmentTable3.obtainFlag() && punishmentTable3 != PunishmentTable.KICKS) {
                              arrayList2.add(punishmentTable3);
                           }
                        }


                        for (Object obj2 : arrayList2) {
                           PunishmentTable punishmentTable4 = (PunishmentTable)obj2;
                           Intrinsics.checkNotNull(statement2);
                           String text3 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable4), "ipban={false}");
                           CharSequence message3 = "ipban={true}";
                           CharSequence[] messages2 = new CharSequence[]{SqlText.box(text3), message3};
                           int value5 = 0;

                           for (int i4 = messages2.length; value5 < i4; value5++) {
                              CharSequence message4 = messages2[value5];
                              if (message4.length() <= 0) {
                                 String text4 = "Failed requirement.";
                                 throw new IllegalArgumentException(text4.toString());
                              }
                           }

                           SqlText sqlText2 = SqlText.box(SqlText.and(SqlText.and(SqlText.constructorImpl(SqlText.expandImpl(text3) + " WHERE " + message3), "ipban_wildcard={false}"), "(time+" + value3 + ")<" + currentTime));
                           statement2.addBatch(sqlText2.toString());
                        }
                     }

                     DatabaseModule databaseModule2 = this.getDatabaseModule();
                     Intrinsics.checkNotNull(statement2);
                     int[] executeBatch2 = databaseModule2.executeBatch(statement2);
                     if (this.configModule.isDebugAtLeast(100)) {
                        int value6 = 0;
                        int value7 = 0;

                        for (int i5 = executeBatch2.length; value7 < i5; value7++) {
                           int value8 = executeBatch2[value7];
                           int value9 = value6;
                           int value10 = Math.max(value8, 0);
                           value6 = value9 + value10;
                        }

                        int value11 = value6;
                        if (value11 > 0) {
                           this.configModule.logDebug("Expired " + value11 + " entries(until<" + currentTime + ')');
                        }
                     }

                  } catch (Throwable e2) {
                     error = e2;
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  } finally {
                     KotlinUtil15.handleAutoCloseable(statement, error);
                  }
               }
            }
         } catch (SQLException e3) {
            String message5 = e3.getMessage();
            if (message5 != null ? !StringsKt.checkText(message5, "Deadlock ", false, 2, null) : true) {
               throw e3;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int clearLoginRecord(@NotNull LoginRecord loginRecord) {
      PunishmentTable punishmentTable = PunishmentTable.HISTORY;
      String text = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable), "ip=?");
      CharSequence message = "uuid=?";
      CharSequence[] messages = new CharSequence[]{SqlText.box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      PreparedQuery prepare2 = this.prepare(SqlText.box(SqlText.computeText(SqlText.constructorImpl(SqlText.expandImpl(text) + " WHERE " + message), "ip=?")));
      String[] texts = new String[]{"#", loginRecord.getUuid(), loginRecord.getIp()};
      int value2 = 0;

      for (int i2 = texts.length; value2 < i2; value2++) {
         String text3 = texts[value2];
         prepare2.bindString(text3);
      }

      return SqlUtil.computeIntValue(prepare2);
   }

   private final void handleStatement(Statement statement) {
      try {
         PunishmentTable[] punishmentTables = PunishmentTable.values();
         int value = 0;

         for (int i = punishmentTables.length; value < i; value++) {
            PunishmentTable punishmentTable = punishmentTables[value];
            String text = PunishmentTable.computeText(punishmentTable, Locale.getDefault(), false, 2, null);
            String text2 = PunishmentTable.computeText(punishmentTable, null, false, 3, null);
            if (!Intrinsics.areEqual(text, text2) && !this.tableExists(text2) && this.tableExists(text)) {
               this.platform.getLogger().info("Renaming table \"" + text + "\" to \"" + text2 + '"');
               statement.execute("RENAME TABLE " + text + " TO " + text2);
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final SchemaBuilder createSchemaBuilder() {
      Settings settings = this.configModule.getSettings();
      return new SchemaBuilder(ColumnDefinition.SQL_TYPES.computeColumnDefinition(settings.getSqlDriver(), settings.a_()));
   }

   public final CharSequence computeMessage(@NotNull PunishmentTable punishmentTable, @NotNull SchemaBuilder schemaBuilder) {
      String text = "";
      String text2 = "";
      SchemaBuilder schemaBuilder2 = schemaBuilder;
      if (punishmentTable == PunishmentTable.WARNINGS) {
         String text3 = "warned";
         String text4 = " NOT NULL";
         text2 = text3 + " BIT" + text4;
      }

      if (punishmentTable != PunishmentTable.KICKS) {
         StringBuilder stringBuilder = new StringBuilder();
         String text5 = "removed_by_uuid";
         byte b = 36;
         String text6 = "";
         String text7 = schemaBuilder2.getColumnDefinition().getText7();
         stringBuilder = stringBuilder.append(
               DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text5 + " VARCHAR(" + b + ')' + text7 + text6 : text5 + " VARCHAR(" + b + ')' + text6
            )
            .append(',');
         String text8 = "removed_by_name";
         short value = 128;
         String text9 = "";
         String text10 = schemaBuilder2.getColumnDefinition().getText7();
         stringBuilder = stringBuilder.append(
               DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text8 + " VARCHAR(" + value + ')' + text10 + text9 : text8 + " VARCHAR(" + value + ')' + text9
            )
            .append(',');
         String text11 = "removed_by_reason";
         short value2 = 2048;
         String text12 = "";
         String text13 = schemaBuilder2.getColumnDefinition().getText7();
         stringBuilder = stringBuilder.append(
               DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text11 + " VARCHAR(" + value2 + ')' + text13 + text12 : text11 + " VARCHAR(" + value2 + ')' + text12
            )
            .append(',');
         String text14 = "removed_by_date";
         String text15 = "";
         text = stringBuilder.append(text14 + " TIMESTAMP" + text15).toString();
      }

      String[] texts = new String[18];
      String text16 = "id";
      texts[0] = Intrinsics.areEqual(schemaBuilder2.getColumnDefinition().getText5(), "sqlite") ? text16 + " INTEGER PRIMARY KEY AUTOINCREMENT" : text16 + " SERIAL";
      String text17 = "uuid";
      byte b2 = 36;
      String text18 = " CHARACTER SET ascii COLLATE ascii_general_ci";
      String text19 = "";
      texts[1] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text17 + " VARCHAR(" + b2 + ')' + text18 + text19 : text17 + " VARCHAR(" + b2 + ')' + text19;
      String text20 = "ip";
      byte b3 = 45;
      String text21 = " CHARACTER SET ascii COLLATE ascii_general_ci";
      String text22 = "";
      texts[2] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text20 + " VARCHAR(" + b3 + ')' + text21 + text22 : text20 + " VARCHAR(" + b3 + ')' + text22;
      String text23 = "reason";
      short value3 = 2048;
      String text24 = "";
      String text25 = schemaBuilder2.getColumnDefinition().getText7();
      texts[3] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text23 + " VARCHAR(" + value3 + ')' + text25 + text24 : text23 + " VARCHAR(" + value3 + ')' + text24;
      String text26 = "banned_by_uuid";
      byte b4 = 36;
      String text27 = "";
      String text28 = schemaBuilder2.getColumnDefinition().getText7();
      String text29 = text27 + " NOT NULL";
      texts[4] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text26 + " VARCHAR(" + b4 + ')' + text28 + text29 : text26 + " VARCHAR(" + b4 + ')' + text29;
      String text30 = "banned_by_name";
      short value4 = 128;
      String text31 = "";
      String text32 = schemaBuilder2.getColumnDefinition().getText7();
      texts[5] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text30 + " VARCHAR(" + value4 + ')' + text32 + text31 : text30 + " VARCHAR(" + value4 + ')' + text31;
      texts[6] = text;
      String text33 = "time";
      texts[7] = text33 + " BIGINT NOT NULL";
      String text34 = "until";
      texts[8] = text34 + " BIGINT NOT NULL";
      String text35 = "template";
      String text36 = " DEFAULT 255";
      String text37 = schemaBuilder2.getColumnDefinition().getText5();
      texts[9] = !Intrinsics.areEqual(text37, "pgsql") && !Intrinsics.areEqual(text37, "h2")
         ? text35 + " TINYINT" + schemaBuilder2.getColumnDefinition().getText8() + " NOT NULL" + text36
         : text35 + " SMALLINT NOT NULL" + text36;
      String text38 = "server_scope";
      byte b5 = 32;
      String text39 = "";
      String text40 = schemaBuilder2.getColumnDefinition().getText7();
      texts[10] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text38 + " VARCHAR(" + b5 + ')' + text40 + text39 : text38 + " VARCHAR(" + b5 + ')' + text39;
      String text41 = "server_origin";
      byte b6 = 32;
      String text42 = "";
      String text43 = schemaBuilder2.getColumnDefinition().getText7();
      texts[11] = DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text41 + " VARCHAR(" + b6 + ')' + text43 + text42 : text41 + " VARCHAR(" + b6 + ')' + text42;
      String text44 = "silent";
      String text45 = " NOT NULL";
      texts[12] = text44 + " BIT" + text45;
      String text46 = "ipban";
      String text47 = " NOT NULL";
      texts[13] = text46 + " BIT" + text47;
      String text48 = "ipban_wildcard";
      String text49 = " NOT NULL DEFAULT {false}";
      texts[14] = text48 + " BIT" + text49;
      String text50 = "active";
      String text51 = " NOT NULL";
      texts[15] = text50 + " BIT" + text51;
      texts[16] = text2;
      String text52 = "id";
      texts[17] = "PRIMARY KEY(" + text52 + ')';
      return SqlText.box(schemaBuilder2.computeText(punishmentTable, texts));
   }

   private final void handleKotlinListImpl(KotlinListImpl2 kotlinList) {
      var statement = this.createStatement();
      Throwable error = null;

      try {
         Statement statement2 = (Statement)statement;
         if (kotlinList.obtainFlag()) {
            Iterable iterable = kotlinList.obtainArrayList();

            for (Object obj : iterable) {
               String unbox2 = ((SqlText)obj).unbox();

               try {
                  SqlText sqlText = SqlText.box(unbox2);
                  statement2.execute(sqlText.toString());
               } catch (SQLException e) {
                  ConfigModule.handleConfigModule(kotlinList.getConfigModule(), e, 0, 2, null);
               }
            }
         } else {
            Iterable iterable2 = kotlinList.obtainArrayList();

            for (Object obj2 : iterable2) {
               CharSequence charSequence = (CharSequence)obj2;
               statement2.addBatch(charSequence.toString());
            }

            if (!kotlinList.obtainArrayList().isEmpty()) {
               statement2.executeBatch();
            }
         }

      } catch (Throwable e2) {
         error = e2;
         throw SneakyThrow.<RuntimeException>rethrow(e2);
      } finally {
         KotlinUtil15.handleAutoCloseable(statement, error);
      }
   }

   public final void handleFlag(boolean flag, @NotNull ConfigModule configModule, @NotNull SchemaBuilder schemaBuilder, boolean flag2) {
      KotlinListImpl2 kotlinList = new KotlinListImpl2(this.platform, this, flag, null, 8, null);
      int value;
      if (flag2) {
         value = 0;
      } else {
         int value2;
         try {
            PunishmentTable punishmentTable = PunishmentTable.CONFIG;
            CharSequence message = "build";
            ResultSet resultSet = SqlUtil.computeResultSet(this.prepare(SqlText.box(SqlText.limit(SqlText.constructorImpl("SELECT " + message + " FROM " + punishmentTable), 1))));
            byte b2 = 1;
            String text;
            if (resultSet.next()) {

               String text2;
               try {
                  text2 = resultSet.getString(b2);
               } catch (SQLException e) {
                  text2 = null;
               }

               text = text2;
            } else {
               text = null;
            }

            if (text == null) {
               text = BuildInfoUtil.obtainText3();
            }

            String text3 = text;
            Intrinsics.checkNotNull(text3);
            value2 = Integer.parseInt(text3);
         } catch (SQLException e2) {
            ConfigModule.handleConfigModule(configModule, e2, 0, 2, null);
            value2 = 0;
         }

         value = value2;
      }

      int value3 = value;
      ConfigModule configModule2 = configModule;
      if (configModule2.isVerbose()) {
         configModule2.logDebug("Database build " + value3 + " => " + BuildInfoUtil.obtainText3());
      }

      if (value3 <= 1168) {
         SyncModule module = (SyncModule)this.platform.getModule(SyncModule.class);
         module.clearCacheEntries(this);
      }

      var statement = this.createStatement();
      Throwable error = null;

      try {
         Statement statement2 = (Statement)statement;
         this.handleStatement(statement2);
         boolean areEqual2 = Intrinsics.areEqual(configModule.getSettings().getSqlDriver(), "mysql");
         boolean tableExists2 = this.tableExists(PunishmentTable.BANS);
         boolean tableExists3 = this.tableExists(PunishmentTable.MUTES);
         boolean tableExists4 = this.tableExists(PunishmentTable.WARNINGS);
         boolean columnExists2 = this.columnExists(PunishmentTable.BANS, "mode", areEqual2);
         boolean columnExists3 = this.columnExists(PunishmentTable.WARNINGS, "warned", areEqual2);
         if (!tableExists3 && tableExists2 && columnExists2) {
            CharSequence message2 = this.computeMessage(PunishmentTable.MUTES, schemaBuilder);
            statement2.execute(message2.toString());
            CharSequence message3 = this.computeMessage(PunishmentTable.WARNINGS, schemaBuilder);
            statement2.execute(message3.toString());
            PunishmentTable punishmentTable2 = PunishmentTable.BANS;
            CharSequence message4 = "*";
            String constructor = SqlText.constructorImpl("SELECT " + message4 + " FROM " + punishmentTable2);
            CharSequence message5 = "mode<>0";
            CharSequence[] messages = new CharSequence[]{SqlText.box(constructor), message5};
            int value4 = 0;

            for (int i = messages.length; value4 < i; value4++) {
               CharSequence message6 = messages[value4];
               if (message6.length() <= 0) {
                  String text4 = "Failed requirement.";
                  throw new IllegalArgumentException(text4.toString());
               }
            }

            SqlText sqlText = SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor) + " WHERE " + message5));
            ResultSet executeQuery2 = statement2.executeQuery(sqlText.toString());
            Throwable error2 = null;

            try {

               while (executeQuery2.next()) {
                  this.insertPunishment(this.readPunishment(executeQuery2, PunishmentType.values()[executeQuery2.getInt("mode")]));
               }

            } catch (Throwable e3) {
               error2 = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               KotlinUtil15.handleAutoCloseable(executeQuery2, error2);
            }

            PunishmentTable punishmentTable3 = PunishmentTable.BANS;
            String constructor2 = SqlText.constructorImpl("DELETE FROM " + punishmentTable3);
            CharSequence message7 = "mode<>0";
            CharSequence[] messages2 = new CharSequence[]{SqlText.box(constructor2), message7};
            int value5 = 0;

            for (int i2 = messages2.length; value5 < i2; value5++) {
               CharSequence message8 = messages2[value5];
               if (message8.length() <= 0) {
                  String text5 = "Failed requirement.";
                  throw new IllegalArgumentException(text5.toString());
               }
            }

            SqlText sqlText2 = SqlText.box(SqlText.constructorImpl(SqlText.expandImpl(constructor2) + " WHERE " + message7));
            statement2.executeUpdate(sqlText2.toString());
         }

         if (!Intrinsics.areEqual(configModule.getSettings().getSqlDriver(), "sqlite")) {
            SchemaBuilder schemaBuilder2 = this.createSchemaBuilder();
            if (columnExists2) {
               PunishmentTable punishmentTable4 = PunishmentTable.BANS;
               SqlText sqlText3 = SqlText.box(SqlText.dropColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable4), "mode"));
               statement2.execute(sqlText3.toString());
            }

            if (tableExists4 && !columnExists3) {
               PunishmentTable punishmentTable5 = PunishmentTable.WARNINGS;
               SqlText sqlText4 = SqlText.box(SqlText.addColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable5), "warned BIT"));
               statement2.execute(sqlText4.toString());
               PunishmentTable punishmentTable6 = PunishmentTable.WARNINGS;
               SqlText sqlText5 = SqlText.box(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable6), "warned={true}"));
               statement2.executeUpdate(sqlText5.toString());
            }

            if (flag || tableExists2) {
               PunishmentTable[] punishmentTables = PunishmentTable.values();
               int value6 = 0;

               for (int i3 = punishmentTables.length; value6 < i3; value6++) {
                  PunishmentTable punishmentTable7 = punishmentTables[value6];
                  if (punishmentTable7.obtainFlag()) {
                     kotlinList.setPunishmentTable(punishmentTable7);
                     if (punishmentTable7 != PunishmentTable.KICKS) {
                        kotlinList.handlePair(TuplesKt.computePair("removed_by_uuid", KotlinFunction1Impl3.KOTLIN_FUNCTION1_IMPL_3));
                        kotlinList.handlePair(TuplesKt.computePair("removed_by_name", KotlinFunction1Impl10.KOTLIN_FUNCTION1_IMPL_5));
                        kotlinList.handlePair(TuplesKt.computePair("removed_by_date", KotlinFunction1Impl12.KOTLIN_FUNCTION1_IMPL_4));
                        kotlinList.handlePair(TuplesKt.computePair("removed_by_reason", KotlinFunction1Impl5.KOTLIN_FUNCTION1_IMPL_2));
                     }

                     kotlinList.handlePair(TuplesKt.computePair("template", new KotlinFunction1Impl7(schemaBuilder2)));
                     kotlinList.handlePair(TuplesKt.computePair("server_origin", new KotlinFunction1Impl(schemaBuilder2)));
                     kotlinList.handlePair(TuplesKt.computePair("server_scope", (Function1)obj -> {
                        PunishmentTable punishmentTable8 = (PunishmentTable) obj;
                        SqlText[] sqlTexts = new SqlText[2];
                        String constructor3 = SqlText.constructorImpl("ALTER TABLE " + punishmentTable8);
                        String text6 = "server_scope";
                        byte b = 32;
                        String text22 = "";
                        String text32 = schemaBuilder2.getColumnDefinition().getText7();
                        sqlTexts[0] = SqlText.box(
                           SqlText.addColumn(constructor3, DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder2.getColumnDefinition().getText5()) ? text6 + " VARCHAR(" + b + ')' + text32 + text22 : text6 + " VARCHAR(" + b + ')' + text22)
                        );
                        sqlTexts[1] = SqlText.box(SqlText.whereIsNull(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable8), "server_scope='*'"), "server_scope"));
                        return sqlTexts;
                     }));
                     kotlinList.handlePair(TuplesKt.computePair("ipban_wildcard", (Function1)obj2 -> {
                        PunishmentTable punishmentTable8 = (PunishmentTable) obj2;
                        SqlText[] sqlTexts = new SqlText[2];
                        String constructor3 = SqlText.constructorImpl("ALTER TABLE " + punishmentTable8);
                        String text6 = "ipban_wildcard";
                        String text22 = " NOT NULL DEFAULT {false}";
                        sqlTexts[0] = SqlText.box(SqlText.addColumn(constructor3, text6 + " BIT" + text22));
                        String text32 = SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable8), "ipban_wildcard={true}");
                        CharSequence message9 = "ip like '%\\%%'";
                        byte b = 1;
                        CharSequence[] messages3 = new CharSequence[]{SqlText.box(text32), message9};
                        int value7 = 0;

                        for (int i4 = messages3.length; value7 < i4; value7++) {
                           CharSequence message22 = messages3[value7];
                           if (message22.length() <= 0) {
                              String text42 = "Failed requirement.";
                              throw new IllegalArgumentException(text42.toString());
                           }
                        }

                        String constructor22 = SqlText.constructorImpl(SqlText.expandImpl(text32) + " WHERE " + message9);
                        sqlTexts[b] = SqlText.box(constructor22);
                        return sqlTexts;
                     }));
                  }

                  if (punishmentTable7 != PunishmentTable.HISTORY && DatabaseModule.DATABASE_TYPE.checkText(configModule.getSettings().getSqlDriver()) && value3 <= 680) {
                     SqlText.convertTo(SqlText.constructorImpl("ALTER TABLE " + punishmentTable7), schemaBuilder2.getColumnDefinition().getText7());
                  }
               }

               if (flag || value3 < 883) {
                  kotlinList.setPunishmentTable(PunishmentTable.CONFIG);
                  kotlinList.handlePair(TuplesKt.computePair("accept", new KotlinFunction1Impl8(schemaBuilder2)));
               }

               if (!kotlinList.isEmpty()) {
                  this.platform.getLogger().info("Upgrading database tables...");
                  this.handleKotlinListImpl(kotlinList);
                  this.platform.getLogger().info("Upgrade complete.");
               }
            }
         }

      } catch (Throwable e4) {
         error = e4;
         throw SneakyThrow.<RuntimeException>rethrow(e4);
      } finally {
         KotlinUtil15.handleAutoCloseable(statement, error);
      }
   }

   @Override
   public boolean isOpen() {
      return PunishmentQueries.isOpen(this);
   }

   @Override
   public DatabaseModule getDatabaseModule() {
      return PunishmentQueries.access$getPlatform(this);
   }

   @Override
   public List getHistory(@NotNull LoginRecord loginRecord, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag, int value) {
      return PunishmentQueries.access$getHistory(this, loginRecord, punishmentType, text, flag, value);
   }

   @Override
   public List getHistory(@NotNull Punishment punishment, int value) {
      return PunishmentQueries.computeList(this, punishment, value);
   }

   @Override
   public List getHistory(@NotNull LiteSender liteSender, @NotNull PunishmentType punishmentType, boolean flag, int value) {
      return PunishmentQueries.computeList(this, liteSender, punishmentType, flag, value);
   }

   @Override
   public List readPunishments(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      return PunishmentQueries.computeList(this, resultSet, punishmentType);
   }

   @Override
   public String getStaffName(@NotNull LiteSender liteSender) {
      return PunishmentQueries.computeText(this, liteSender);
   }

   @Override
   public CharSequence replacePlaceholdersNullable(@NotNull CharSequence message, @NotNull LiteSender liteSender) {
      return PunishmentQueries.replacePlaceholders(this, message, liteSender);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable String text, @Nullable String text2, boolean flag) {
      return PunishmentQueries.replacePlaceholders(this, message, text, text2, flag);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message) {
      return PunishmentQueries.replaceStatistics(this, message);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable LoginRecord loginRecord, boolean flag) {
      return PunishmentQueries.replacePlaceholders(this, message, loginRecord, flag);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable LiteSender liteSender) {
      return PunishmentQueries.replacePlaceholdersNullable(this, message, liteSender);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @NotNull KotlinHelper13 kotlinHelper) {
      return PunishmentQueries.replacePlaceholders(this, message, kotlinHelper);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable Punishment punishment, boolean flag) {
      return PunishmentQueries.replacePlaceholders(this, message, punishment, flag);
   }

   @Override
   public CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable Punishment punishment, @Nullable LoginRecord loginRecord, boolean flag) {
      return PunishmentQueries.access$replacePlaceholders(this, message, punishment, loginRecord, flag);
   }

   @Override
   public CharSequence replacePlaceholders(
      @NotNull CharSequence message, @Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, @Nullable String text5
   ) {
      return PunishmentQueries.replacePlaceholders(this, message, text, text2, text3, text4, text5);
   }

   @Override
   public boolean isAffecting(@NotNull Punishment punishment, @NotNull LiteSender liteSender) {
      return PunishmentQueries.checkDatabaseSession(this, punishment, liteSender);
   }

   @Override
   public boolean isAffecting(@Nullable Punishment punishment) {
      return PunishmentQueries.checkDatabaseSession(this, punishment);
   }

   public static final Function1 obtainFunction() {
      return return2;
   }

   public static final int obtainIntValue() {
      return value12;
   }

   public static final void handleIntValue(int value) {
      value12 = value;
   }
}
