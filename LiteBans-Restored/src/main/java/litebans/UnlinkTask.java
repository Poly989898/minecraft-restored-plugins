package litebans;

import org.jetbrains.annotations.NotNull;

public final class UnlinkTask extends CommandTask {
   private final String[] args;

   public UnlinkTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.args = texts;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      String[] texts = new String[]{"litebans.admin", "litebans.unlink"};
      AltAccountPunisher.handleCommandTask(commandTask, texts);
      CommandTask commandTask2 = this;
      ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         boolean isClosed2 = databaseSession.isClosed();
         CharSequence message = Message.ERROR_NO_SQL_CONNECTION;
         if (isClosed2) {
            commandOutput.returnResult(message);
            throw new CommandAbortException();
         } else {
            String text = this.args[1];
            LoginRecord loginRecord = databaseSession.findLoginRecord(text);
            if (loginRecord == null) {
               CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NO_UUID_FOUND);
               throw new CommandAbortException();
            } else {
               databaseSession.clearLoginRecord(loginRecord);
               AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (LiteColor.green("Player ") + LiteColor.gold(loginRecord.getName()) + LiteColor.green(" has been unlinked successfully.")));
               return Unit.UNIT;
            }
         }
      });
   }

}
