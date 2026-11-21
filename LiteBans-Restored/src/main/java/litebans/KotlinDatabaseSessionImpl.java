package litebans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinDatabaseSessionImpl implements DatabaseSession {
   private final Platform platform;

   public KotlinDatabaseSessionImpl(@NotNull Platform platform) {
      this.platform = platform;
   }

   @Override
   public Platform getPlatform() {
      return this.platform;
   }

   @Override
   public boolean isClosed() {
      return true;
   }

   @Override
   public void handleText(@Nullable String text, @Nullable String text2, @NotNull CharSequence message, @NotNull Punishment punishment, boolean flag) {
   }

   @Override
   public void handlePunishment(@NotNull Punishment punishment, @Nullable String text, @Nullable String text2, @NotNull CharSequence message, boolean flag) {
   }

   @Override
   public long getTotalEntryCount() {
      return 0L;
   }

   @Override
   public int countTemplateUses(@NotNull Template template, @Nullable String text, @Nullable String text2) {
      return 0;
   }

   @Override
   public Pair computePair(@Nullable String text, @Nullable String text2, @NotNull TemplateGroup templateGroup, @NotNull Template template) {
      return TuplesKt.computePair(0.0, 0.0);
   }

   @Override
   public void insertPunishment(@NotNull Punishment punishment) {
   }

   @Override
   public void saveLogin(@Nullable String text, @Nullable String text2, @Nullable String text3, boolean flag, boolean flag2) {
   }

   @Override
   public LoginRecord saveLoginRecord(@NotNull LoginRecord loginRecord, boolean flag) {
      return new LoginRecord(null, null, null, new Date());
   }

   @Override
   public int clearLoginRecord(@NotNull LoginRecord loginRecord) {
      return 0;
   }

   @Override
   public void close() {
   }

   @Override
   public List getHistory(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public List getHistory(@NotNull Punishment punishment, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public List getHistory(@NotNull LiteSender liteSender, @NotNull PunishmentType punishmentType, boolean flag, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public int countEntries(@NotNull HistoryQuery historyQuery, @NotNull PunishmentType punishmentType) {
      return 0;
   }

   @Override
   public List getHistory(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public List findLoginRecords(@Nullable String text, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public Collection getEntriesPage(@NotNull PunishmentType punishmentType, long value, long value2, boolean flag) {
      return CollectionsKt.emptyList();
   }

   @Override
   public Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, boolean flag2) {
      return null;
   }

   @Override
   public Punishment findPunishment(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType) {
      return null;
   }

   @Override
   public List findPunishments(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType, boolean flag) {
      return CollectionsKt.emptyList();
   }

   @Override
   public Punishment findPunishmentById(long value, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag) {
      return null;
   }

   @Override
   public Punishment findPunishment(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, boolean flag) {
      return null;
   }

   @Override
   public Punishment findExistingPunishment(@NotNull Punishment punishment, boolean flag) {
      return null;
   }

   @Override
   public Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, boolean flag, boolean flag2) {
      return null;
   }

   @Override
   public long countEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag) {
      return 0L;
   }

   @Override
   public boolean hasAllowEntry(@NotNull UUID uuid, @NotNull PunishmentType punishmentType) {
      return false;
   }

   public ProxyResultSet computeProxyResultSet(@NotNull UUID uuid, int value) {
      return ResultSetUtil.obtainProxyResultSet();
   }

   public ProxyResultSet computeProxyResultSet(@NotNull UUID uuid) {
      return ResultSetUtil.obtainProxyResultSet();
   }

   @Override
   public List findLoginRecordsByTarget(@NotNull String text) {
      return CollectionsKt.emptyList();
   }

   @Override
   public LoginRecord findLoginRecord(@Nullable String text) {
      return null;
   }

   @Override
   public List findLoginRecordsLimit(@Nullable String text, int value) {
      return CollectionsKt.emptyList();
   }

   @Override
   public long getPunishmentId(@NotNull Punishment punishment) {
      return 0L;
   }

   @Override
   public void handleConnection(@Nullable Connection connection, @NotNull PunishmentTable punishmentTable, boolean flag) {
   }

   @Override
   public Punishment readPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public PreparedQuery prepare(@NotNull CharSequence message) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void expireEntries(@Nullable String text, long value) {
   }

   @Override
   public ResultSet getEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, int value) {
      return ResultSetUtil.obtainProxyResultSet();
   }

   @Override
   public ResultSet resetTemplates(@NotNull PunishmentTable punishmentTable) {
      return ResultSetUtil.obtainProxyResultSet();
   }

   @Override
   public void updateExpiry(@NotNull CharSequence message, long value, @NotNull Punishment punishment) {
   }

   @Override
   public long computeLongValue(@NotNull String text, long value, @NotNull String text2) {
      return -1L;
   }

   @Override
   public ResultSet markWarned(@NotNull String text) {
      return new ProxyResultSet();
   }

   @Override
   public void updateExpired(boolean flag) {
   }

   @Override
   public boolean columnExists(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag) {
      return false;
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

   @Override
   public ResultSet getAllowEntries(UUID uuid, int value) {
      return this.computeProxyResultSet(uuid, value);
   }

   @Override
   public ResultSet deleteAllowEntries(UUID uuid) {
      return this.computeProxyResultSet(uuid);
   }

   @Override
   public boolean tableExists(@NotNull CharSequence message) {
      return false;
   }

   @Override
   public boolean checkText(@Nullable String text, @Nullable String text2, @NotNull String text3) {
      return false;
   }

   @Override
   public LoginRecord readLoginRecord(@NotNull ResultSet resultSet, boolean flag, @Nullable String text) {
      return null;
   }

   @Override
   public Punishment readNextPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType) {
      return null;
   }
}
