package litebans;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DatabaseSession extends Closeable {
   boolean isClosed();

   boolean isOpen();

   Platform getPlatform();

   DatabaseModule getDatabaseModule();

   void insertPunishment(@NotNull Punishment punishment);

   LoginRecord saveLoginRecord(@NotNull LoginRecord loginRecord, boolean flag);

   void saveLogin(@Nullable String text, @Nullable String text2, @Nullable String text3, boolean flag, boolean flag2);

   ResultSet getAllowEntries(@NotNull UUID uuid, int value);

   boolean hasAllowEntry(@NotNull UUID uuid, @NotNull PunishmentType punishmentType);

   boolean columnExists(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag);

   Punishment readPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType);

   Punishment readNextPunishment(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType);

   LoginRecord readLoginRecord(@NotNull ResultSet resultSet, boolean flag, @Nullable String text);

   int countEntries(@NotNull HistoryQuery historyQuery, @NotNull PunishmentType punishmentType);

   int countTemplateUses(@NotNull Template template, @Nullable String text, @Nullable String text2);

   List getHistory(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, int value);

   List getHistory(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, int value);

   List findLoginRecords(@Nullable String text, int value);

   Collection getEntriesPage(@NotNull PunishmentType punishmentType, long value, long value2, boolean flag);

   Punishment findExistingPunishment(@NotNull Punishment punishment, boolean flag);

   Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, boolean flag, boolean flag2);

   Punishment findPunishment(@Nullable String text, @Nullable String text2, @NotNull PunishmentType punishmentType, @Nullable String text3, boolean flag, boolean flag2);

   Punishment findPunishment(@NotNull String text, @NotNull PunishmentType punishmentType, @NotNull String text2, boolean flag);

   Punishment findPunishmentById(long value, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag);

   long countEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, boolean flag);

   long getPunishmentId(@NotNull Punishment punishment);

   Punishment findPunishment(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType);

   List findPunishments(@NotNull String text, @Nullable String text2, @Nullable String text3, @NotNull PunishmentType punishmentType, boolean flag);

   List findLoginRecordsByTarget(@NotNull String text);

   LoginRecord findLoginRecord(@Nullable String text);

   List findLoginRecordsLimit(@Nullable String text, int value);

   boolean checkText(@Nullable String text, @Nullable String text2, @NotNull String text3);

   void handleConnection(@Nullable Connection connection, @NotNull PunishmentTable punishmentTable, boolean flag);

   PreparedQuery prepare(@NotNull CharSequence message);

   void expireEntries(@Nullable String text, long value);

   Pair computePair(@Nullable String text, @Nullable String text2, @NotNull TemplateGroup templateGroup, @NotNull Template template);

   ResultSet markWarned(@NotNull String text);

   void updateExpiry(@NotNull CharSequence message, long value, @NotNull Punishment punishment);

   ResultSet deleteAllowEntries(@NotNull UUID uuid);

   ResultSet getEntries(@NotNull PunishmentTable punishmentTable, @NotNull String text, int value);

   ResultSet resetTemplates(@NotNull PunishmentTable punishmentTable);

   void handleText(@Nullable String text, @Nullable String text2, @NotNull CharSequence message, @NotNull Punishment punishment, boolean flag);

   void handlePunishment(@NotNull Punishment punishment, @Nullable String text, @Nullable String text2, @NotNull CharSequence message, boolean flag);

   long computeLongValue(@NotNull String text, long value, @NotNull String text2);

   boolean tableExists(@NotNull CharSequence message);

   long getTotalEntryCount();

   int clearLoginRecord(@NotNull LoginRecord loginRecord);

   void updateExpired(boolean flag);

   List readPunishments(@NotNull ResultSet resultSet, @NotNull PunishmentType punishmentType);

   List getHistory(@NotNull LoginRecord loginRecord, @NotNull PunishmentType punishmentType, @NotNull String text, boolean flag, int value);

   List getHistory(@NotNull Punishment punishment, int value);

   List getHistory(@NotNull LiteSender liteSender, @NotNull PunishmentType punishmentType, boolean flag, int value);

   String getStaffName(@NotNull LiteSender liteSender);

   CharSequence replacePlaceholdersNullable(@NotNull CharSequence message, @NotNull LiteSender liteSender);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable String text, @Nullable String text2, boolean flag);

   CharSequence replacePlaceholders(@NotNull CharSequence message);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable LoginRecord loginRecord, boolean flag);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable LiteSender liteSender);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @NotNull KotlinHelper13 kotlinHelper);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, @Nullable String text5);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable Punishment punishment, boolean flag);

   CharSequence replacePlaceholders(@NotNull CharSequence message, @Nullable Punishment punishment, @Nullable LoginRecord loginRecord, boolean flag);

   boolean isAffecting(@NotNull Punishment punishment, @NotNull LiteSender liteSender);

   boolean isAffecting(@Nullable Punishment punishment);
}
