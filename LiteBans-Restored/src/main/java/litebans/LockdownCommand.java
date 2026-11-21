package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class LockdownCommand extends AbstractCommand {
   public LockdownCommand(@NotNull Platform platform) {
      super("lockdown", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      ArrayList list = CollectionsKt.arrayListOf(Arrays.copyOf(texts, texts.length));
      List list2 = list;
      CommandTask commandTask = this;
      ServerScopes scopes = ServerScope.parseScopesWithDefaults(ServerScopes.SERVER_SCOPE, liteSender, list2, ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).getServerName(), false, 8, null);
      String text = "local";
      CommandTask commandTask2 = this;
      String serverName = ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).getServerName();
      CharSequence message = scopes.getScope();
      String text2 = message == null || message.length() == 0 ? ServerScopes.SERVER_SCOPE.resolveScope(text, serverName, liteSender) : scopes.getScope();
      String orNull = (String)CollectionsKt.getOrNull(list, 0);
      if (orNull != null) {
         String text3 = orNull;
         orNull = text3.toLowerCase(Locale.ENGLISH);
         if (orNull != null) {
            String text4 = orNull;
            if (list.size() == 1) {
               String[] texts2 = new String[]{"stop", "end", "finish", "off", "disable"};
               if (KotlinStub.computeSet(texts2).contains(text4)) {
                  CommandTask commandTask3 = this;
                  LockdownHandler lockdownHandler = ((BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class)).getLockdownHandler();
                  if (lockdownHandler != null) {
                     orNull = (String) lockdownHandler.getKickMessage();
                     if (orNull != null) {
                        CharSequence message2 = orNull;
                        CommandTask commandTask4 = this;
                        ConfigModule module = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
                        if (module.isVerbose()) {
                           module.logDebug(message2);
                        }

                        CommandTask commandTask5 = this;
                        ((BroadcastModule)commandTask5.getPlatform().getModule(BroadcastModule.class)).handleText(text2, null, true);
                        return;
                     }
                  }

                  CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NOT_ACTIVE);
                  throw new CommandAbortException();
               }
            }

            String text5 = Message.computeText(CollectionsKt.joinToStringWithDefaults(list, " ", null, null, 0, null, null, 62, null), true);
            CommandTask commandTask6 = this;
            ((BroadcastModule)commandTask6.getPlatform().getModule(BroadcastModule.class)).handleText(text2, text5, true);
            return;
         }
      }

      CommandTask.COMMAND_OUTPUT.returnResult(Message.LOCKDOWN_USAGE);
      throw new CommandAbortException();
   }
}
