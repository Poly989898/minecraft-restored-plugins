package litebans;

import java.sql.Connection;
import org.jetbrains.annotations.NotNull;

public final class RollbackCommandBase extends ImportSource {
   public RollbackCommandBase(@NotNull Platform platform) {
      super(platform, "LiteBans", null, 0, 0L, 28, null);
   }

   @Override
   public void handleText(@NotNull String text, @NotNull Connection connection, @NotNull DatabaseSession databaseSession, boolean flag) {
      long totalEntryCount = databaseSession.getTotalEntryCount();
      PunishmentTable[] punishmentTables = PunishmentTable.values();
      int value = 0;

      for (int i = punishmentTables.length; value < i; value++) {
         PunishmentTable punishmentTable = punishmentTables[value];
         CommandTask commandTask = this;
         databaseSession.handleConnection(connection, punishmentTable, ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isImportSqlite());
      }

      long totalEntryCount2 = databaseSession.getTotalEntryCount();
      this.obtainAtomicLong().set(totalEntryCount2 - totalEntryCount);
   }

   @Override
   public void handleLiteSender(@NotNull LiteSender liteSender) {
      CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, Message.IMPORT_FINISH_LITEBANS.computeText("amount", this.obtainAtomicLong().get()), null, 4, null);
   }
}
