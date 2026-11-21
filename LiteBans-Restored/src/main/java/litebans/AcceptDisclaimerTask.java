package litebans;

import org.jetbrains.annotations.NotNull;

public final class AcceptDisclaimerTask extends CommandTask {
   public AcceptDisclaimerTask(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         databaseSession.getDatabaseModule().setDisclaimerAccepted(true);
         PunishmentTable punishmentTable = PunishmentTable.CONFIG;
         SqlUtil.computeResultSet(databaseSession.prepare(SqlText.box(SqlText.set(SqlText.constructorImpl("UPDATE " + punishmentTable), "accept=1"))));
         AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (LiteColor.green("Disclaimer accepted!")));
         SyncModule module = (SyncModule)databaseSession.getPlatform().getModule(SyncModule.class);
         SyncModule.sendSyncMessageWithDefaults(module, SyncMessageType.CONFIG_RELOAD, databaseSession, null, 0, 12, null);
         return Unit.UNIT;
      });
   }

}
