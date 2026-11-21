package litebans;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;

public final class LiteCommandTaskSubX extends CommandTask {
   public LiteCommandTaskSubX(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> computeUnit(this, (DatabaseSession) obj));
   }

   private static final Unit computeUnit(LiteCommandTaskSubX liteCommandTaskSubX, DatabaseSession databaseSession) {
      if (databaseSession.getDatabaseModule().isSyncEnabled()) {
         SyncModule module = (SyncModule)databaseSession.getPlatform().getModule(SyncModule.class);
         CommandTask commandTask = liteCommandTaskSubX;
         String sqlTablePrefix = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getSqlTablePrefix();
         String text = String.valueOf(System.nanoTime() % 1000);
         CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
         copyOnWriteArrayList.add(databaseSession.getDatabaseModule().getServerRecord());
         AltAccountPunisher.sendMessage((CommandTask) liteCommandTaskSubX, (CharSequence) (TextUtil.colorize(LiteColor.GREEN, "Table prefix of this instance: \"") + LiteColor.GOLD + sqlTablePrefix + LiteColor.GREEN + '"'));
         AltAccountPunisher.sendMessage((CommandTask) liteCommandTaskSubX, (CharSequence) (TextUtil.colorize(LiteColor.GREEN, "Checking for other servers connected to the database, please wait...")));
         LiteHelper12 liteHelper12 = new LiteHelper12(text, copyOnWriteArrayList);
         module.addListener(liteHelper12);
         SyncModule.sendSyncMessageWithDefaults(module, SyncMessageType.PING, databaseSession, text, 0, 8, null);
         LiteSender sender = liteCommandTaskSubX.getSender();
         Platform platform = databaseSession.getPlatform();
         Runnable runnable = () -> {
            module.removeListener(liteHelper12);
            int value = copyOnWriteArrayList.size();
            if (value <= 1) {
               CharSequence message = TextUtil.colorize(LiteColor.GREEN, "There are no other servers connected to this database.");
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, sender, message, null, 4, null);
            } else {
               StringBuilder builder = new StringBuilder()
                  .append(TextUtil.colorize(LiteColor.GREEN, "There are "))
                  .append(LiteColor.GOLD)
                  .append(value)
                  .append(LiteColor.GREEN)
                  .append(" servers connected to this database: ");
               Collection collection = copyOnWriteArrayList;
               CharSequence string = builder.append(KotlinUtil32.KOTLIN_UTIL32_COMPANION.computeText(collection.toArray(new Object[0]), ", ")).toString();
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, sender, string, null, 4, null);
            }
         };
         CommandTask commandTask2 = liteCommandTaskSubX;
         long syncPollTicks = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getSyncPollTicks();
         CommandTask commandTask3 = liteCommandTaskSubX;
         platform.runAsyncLater(runnable, syncPollTicks * (((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().getSyncPollSkip() + 1));
      }

      return Unit.UNIT;
   }
}
