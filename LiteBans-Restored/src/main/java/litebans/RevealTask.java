package litebans;

import org.jetbrains.annotations.NotNull;

public final class RevealTask extends CommandTask {
   private final String[] args;

   public RevealTask(@NotNull String[] texts, @NotNull Platform platform) {
      super(platform);
      this.args = texts;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      String[] texts = new String[]{"litebans.admin", "litebans.reveal"};
      AltAccountPunisher.handleCommandTask(commandTask, texts);
      if (Intrinsics.areEqual(this.args[1], "seed") && this.getSender().isConsole()) {
         CommandTask commandTask2 = this;
         CommandTask.COMMAND_OUTPUT.returnResult(LiteColor.green(String.valueOf(((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getSecurityObscureSeed())));
         throw new CommandAbortException();
      }

      CommandTask commandTask3 = this;
      KotlinData12 kotlinData = ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).obtainKotlinData2();
      if (Intrinsics.areEqual(this.args[1], "web") && this.getSender().isConsole()) {
         StringBuilder stringBuilder = new StringBuilder();

         for (int i = 0; i < 1000; i++) {
            stringBuilder.append((String)kotlinData.getHashMap().get(i));
         }

         CommandTask.COMMAND_OUTPUT.returnResult(LiteColor.green("" + kotlinData.getIntValue() + ':' + stringBuilder));
         throw new CommandAbortException();
      } else {
         long value = kotlinData.computeLongValue(this.args[1]);
         if (value == -1L) {
            CommandTask.COMMAND_OUTPUT.returnResult(LiteColor.red("Invalid ID."));
            throw new CommandAbortException();
         }

         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.getSender(), LiteColor.green(String.valueOf(value)), null, 4, null);
      }
   }
}
