package litebans;

import org.jetbrains.annotations.NotNull;

public class LiteRunnableImpl2 implements Runnable {
   private final LiteSender liteSender;
   private final String[] texts;
   private final AbstractCommand abstractCommand;
   private final String command;

   public LiteRunnableImpl2(@NotNull LiteSender liteSender, @NotNull String[] texts, @NotNull AbstractCommand abstractCommand, @NotNull String text) {
      this.liteSender = liteSender;
      this.texts = texts;
      this.abstractCommand = abstractCommand;
      this.command = text;
   }

   @Override
   public void run() {
      this.abstractCommand.handleLiteSender(this.liteSender, this.texts, this.command);
   }
}
