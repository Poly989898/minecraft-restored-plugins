package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class HistoryCommand extends AbstractCommand implements LiteAInterface3 {
   public HistoryCommand(@NotNull Platform platform) {
      super("history", platform);
   }

   @Override
   public CharSequence obtainMessage() {
      return Message.START;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         DatabaseModule module = (DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               CommandTask commandTask2 = this;
               String name = AltAccountPunisher.resolveName(commandTask2, (String)ArraysKt.first(texts));
               LoginRecord targetRecord = AltAccountPunisher.parseTargetRecord(session, name, false);
               String orNull = (String)ArraysKt.getOrNull(texts, 1);
               PunishmentType punishmentType = PunishmentType.PUNISHMENT_TYPE_COMPANION.computePunishmentType(orNull);
               String text = AltAccountPunisher.getReasonArgumentWithDefaults(this, liteSender, texts, null, 4, null);
               if (targetRecord.getUuid() == null) {
                  CommandTask.COMMAND_OUTPUT.returnResult(name + "'s uuid is null");
                  throw new CommandAbortException();
               }

               int value = LiteHelper8.computeIntValue(this, session.getPlatform(), texts, CollectionsKt.emptyList(), 0, 8, null);
               List list = punishmentType != null ? CollectionsKt.computeList(punishmentType) : PunishmentType.PUNISHMENT_TYPE_COMPANION.obtainList();
               Iterable iterable = list;
               var arrayList = new ArrayList();

               for (Object obj : iterable) {
                  PunishmentType punishmentType2 = (PunishmentType)obj;
                  Iterable history = session.getHistory(targetRecord, punishmentType2, text, false, value);
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
