package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BanListCommand extends AbstractCommand implements LiteAInterface3 {
   public BanListCommand(@NotNull Platform platform) {
      super("banlist", platform);
   }

   @Override
   public boolean obtainFlag() {
      return false;
   }

   @Override
   public CharSequence obtainMessage() {
      return "";
   }

   public PunishmentType obtainPunishmentType() {
      return PunishmentType.BAN;
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
               PunishmentTable punishmentTable = this.obtainPunishmentType().getPunishmentTable();
               CommandTask commandTask2 = this;
               double countEntries = PunishmentQueries.countEntriesWithDefaults(session, punishmentTable, (String) null, ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isBanlistShowActiveOnly(), 2, null);
               CommandTask commandTask3 = this;
               double banlistBansPerPage = ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().getBanlistBansPerPage();
               double floor2 = Math.floor(countEntries / banlistBansPerPage);
               double number = floor2;
               double number2 = StringMatching.getDouble(texts, 0, floor2);
               double number3 = Math.min(floor2, number2);
               if (number3 > 0.0 && number3 == floor2 && (int)countEntries % (int)banlistBansPerPage == 0) {
                  number3 += -1.0;
                  number += -1.0;
               }

               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (Placeholders.replacePlaceholder(Placeholders.replacePlaceholder(session.replacePlaceholders(Message.START_4), "page", (int)number3), "total", (int)number)));
               PunishmentType punishmentType = this.obtainPunishmentType();
               long long2 = (long)number3;
               long long3 = (long)banlistBansPerPage;
               CommandTask commandTask4 = this;
               Collection entriesPage = session.getEntriesPage(punishmentType, long2, long3, ((ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class)).getSettings().isBanlistShowActiveOnly());
               LiteHelper8.handleLiteAInterface(this, session, entriesPage, null, null, null, 0, 30, null);
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
