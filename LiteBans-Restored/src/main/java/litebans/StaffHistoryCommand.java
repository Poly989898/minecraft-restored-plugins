package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class StaffHistoryCommand extends AbstractCommand implements LiteAInterface3 {
   public StaffHistoryCommand(@NotNull Platform platform) {
      super("staffhistory", platform);
   }

   @Override
   public CharSequence obtainMessage() {
      return Message.START_3;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         int value = LiteHelper8.computeIntValue(this, this.getPlatform(), texts, CollectionsKt.emptyList(), 0, 8, null);
         CommandTask commandTask = this;
         String name = AltAccountPunisher.resolveName(commandTask, (String)ArraysKt.first(texts));
         CommandTask commandTask2 = this;
         DatabaseModule module = (DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               LoginRecord targetRecord = AltAccountPunisher.parseTargetRecord(session, name, false);
               String orNull = (String)ArraysKt.getOrNull(texts, 1);
               PunishmentType punishmentType = PunishmentType.PUNISHMENT_TYPE_COMPANION.computePunishmentType(orNull);
               String uuid = targetRecord.getUuid();
               if (uuid == null) {
                  CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NO_LOGINRECORD);
                  throw new CommandAbortException();
               }

               String text = AltAccountPunisher.getReasonArgumentWithDefaults(this, liteSender, texts, null, 4, null);
               List list = punishmentType != null ? CollectionsKt.computeList(punishmentType) : PunishmentType.PUNISHMENT_TYPE_COMPANION.obtainList();
               Iterable iterable = list;
               var arrayList = new ArrayList();

               for (Object obj : iterable) {
                  PunishmentType punishmentType2 = (PunishmentType)obj;
                  Iterable history = session.getHistory(uuid, punishmentType2, text, value);
                  CollectionsKt.checkCollection(arrayList, history);
               }

               List sorted2 = CollectionsKt.sorted(arrayList);
               LiteHelper8.handleLiteAInterface(this, session, sorted2, targetRecord, texts, null, 0, 24, null);
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

   @Override
   public int computeIntValue(@NotNull Platform platform, @Nullable String[] texts, @NotNull Collection collection, int value) {
      return LiteHelper8.computeIntValue(this, platform, texts, collection, value);
   }

   @Override
   public void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Collection collection, @Nullable LoginRecord loginRecord, @Nullable String[] texts, @NotNull AbstractCommand abstractCommand, int value) {
      LiteHelper8.handleLiteAInterface(this, databaseSession, collection, loginRecord, texts, abstractCommand, value);
   }

   @Override
   public LoginRecord computeLoginRecord(@NotNull DatabaseSession databaseSession, @NotNull HashMap map, @Nullable String text) {
      return LiteHelper8.computeLoginRecord(this, databaseSession, map, text);
   }
}
