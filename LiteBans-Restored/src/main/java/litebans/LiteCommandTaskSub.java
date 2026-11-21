package litebans;

import org.jetbrains.annotations.NotNull;

public final class LiteCommandTaskSub extends CommandTask {
   public LiteCommandTaskSub(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean isConsole2 = commandTask.getSender().isConsole();
      CharSequence message = Message.ERROR_CONSOLE_ONLY;
      boolean flag = !isConsole2;
      if (flag) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }

      CommandTask commandTask2 = this;
      ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
         boolean isClosed2 = databaseSession.isClosed();
         CharSequence message2 = Message.ERROR_NO_SQL_CONNECTION;
         if (isClosed2) {
            commandOutput2.returnResult(message2);
            throw new CommandAbortException();
         }

         Iterable iterable = PunishmentTable.PUNISHMENT_TABLE_COMPANION.obtainList();

         for (Object obj2 : iterable) {
            PunishmentTable punishmentTable = (PunishmentTable)obj2;
            databaseSession.resetTemplates(punishmentTable);
         }

         AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) (TextUtil.colorize(LiteColor.YELLOW, "All templates have been reset successfully.")));
         return Unit.UNIT;
      });
   }

}
