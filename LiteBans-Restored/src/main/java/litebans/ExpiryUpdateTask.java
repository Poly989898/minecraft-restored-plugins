package litebans;

import org.jetbrains.annotations.NotNull;

public final class ExpiryUpdateTask extends CommandTask {
   public ExpiryUpdateTask(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CommandTask commandTask2 = this;
         PunishmentLookupModule module = (PunishmentLookupModule)commandTask2.getPlatform().getModule(PunishmentLookupModule.class);
         DatabaseModule databaseModule = databaseSession.getDatabaseModule();
         long currentTime = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
         int value = 21600000 + new KotlinRandom(0L, 1, null).nextInt(32400000);
         databaseModule.setLastExpiredUpdate(currentTime + value);
         CommandTask commandTask22 = this;
         if (((ConfigModule)commandTask22.getPlatform().getModule(ConfigModule.class)).getSettings().isMutesEnabled()) {
            module.handleDatabaseSession(databaseSession);
         }

         return Unit.UNIT;
      });
   }

}
