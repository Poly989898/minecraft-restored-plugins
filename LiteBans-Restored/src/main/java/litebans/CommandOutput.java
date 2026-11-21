package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandOutput {
   private CommandOutput() {
   }

   public final ThreadLocal getSenderHolder() {
      return CommandTask.getSenderHolder();
   }

   public final Void returnResult(@Nullable Object obj) {
      throw new ResultException(obj);
   }

   public final void send(@NotNull LiteSender liteSender, @NotNull CharSequence message, @NotNull Platform platform) {
      if (message.length() != 0) {
         JsonMessageModule module = (JsonMessageModule)platform.getModule(JsonMessageModule.class);
         CharSequence message2 = message;
         short value = 4096;
         String text = TextUtil.access$length(message2.toString(), value);
         String processMessage2 = module.processMessage(liteSender, text);
         if (processMessage2 != null) {
            if (!liteSender.isPlayer() && platform.getPlatformId() == 0) {
               Platform platform2 = platform;
               if (platform2.isEnabled() && !platform2.obtainFlag3()) {
                  platform.runSync(() -> {
                     liteSender.sendMessage(processMessage2);
                  });
                  return;
               }
            }

            liteSender.sendMessage(processMessage2);
         }
      }
   }

   public static void sendWithDefaults(CommandOutput commandOutput, LiteSender liteSender, CharSequence message, Platform platform, int value, Object obj) {
      if ((value & 4) != 0) {
         platform = PlatformHolder.PLATFORM_STATE.obtainPlatform();
      }

      commandOutput.send(liteSender, message, platform);
   }

   public CommandOutput(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
