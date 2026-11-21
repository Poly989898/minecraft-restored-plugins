package litebans;

import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public final class CommandArguments {
   private final ConfigModule configModule;
   private final CharSequence[] messageArray;

   public CommandArguments(@NotNull ConfigModule configModule2, @NotNull CharSequence[] messages) {
      this.configModule = configModule2;
      this.messageArray = messages;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public final CharSequence[] getMessageArray() {
      return this.messageArray;
   }

   public final int obtainIntValue() {
      CharSequence[] messages = this.messageArray;
      return this.configModule.computeIntValue(Arrays.copyOf(messages, messages.length));
   }
}
