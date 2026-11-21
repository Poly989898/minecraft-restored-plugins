package litebans;

import org.jetbrains.annotations.NotNull;

public class LiteRunnableImpl2 implements Runnable {
   private final LiteSender liteSender;
   private final String[] texts;
   private final AbstractCommand abstractCommand;
   private final String text2;

   public LiteRunnableImpl2(@NotNull LiteSender liteSender, @NotNull String[] texts, @NotNull AbstractCommand abstractCommand, @NotNull String text) {
      this.liteSender = liteSender;
      this.texts = texts;
      this.abstractCommand = abstractCommand;
      this.text2 = text;
   }

   @Override
   public void run() {
      this.abstractCommand.handleLiteSender(this.liteSender, this.texts, this.text2);
   }
}
