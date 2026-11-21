package litebans;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class AddLoginTask extends CommandTask {
   private final String[] texts;

   public AddLoginTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.texts = texts;
   }

   @Override
   public void run() {
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean flag = this.texts.length < 2;
      CharSequence message = LiteColor.red("$command add-login <name> <UUID> <IP>");
      if (flag) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }

      CommandTask commandTask = this;
      ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
         DatabaseSession databaseSession = (DatabaseSession) obj;
         CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
         boolean isClosed2 = databaseSession.isClosed();
         CharSequence message2 = Message.ERROR_NO_SQL_CONNECTION;
         if (isClosed2) {
            commandOutput2.returnResult(message2);
            throw new CommandAbortException();
         }

         if (this.texts.length != 2) {
            String text = null;
            if (this.texts.length >= 4) {
               text = this.texts[3];
            }

            databaseSession.saveLogin(this.texts[1], this.texts[2], text, false, true);
            this.perform5();
            return Unit.UNIT;
         } else {
            String text2 = this.texts[1];
            char[] chars = new char[]{','};

            for (String text3 : (Iterable<String>) StringsKt.splitWithDefaults(text2, chars, false, 0, 6, null)) {
               Profile profile = UsernameValidator.KOTLIN_DATA.computeProfile(text3);
               if (profile != null) {
                  UUID uuid = profile.getUUID();
                  if (uuid != null) {
                     text2 = uuid.toString();
                     if (text2 != null) {
                        String text4 = text2;
                        databaseSession.saveLogin(text3, text4, (String)null, false, true);
                     }
                  }
               }
            }

            this.perform5();
            return Unit.UNIT;
         }
      });
   }

   private final void perform5() {
      CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.getSender(), LiteColor.green("Login record(s) added."), null, 4, null);
   }

}
