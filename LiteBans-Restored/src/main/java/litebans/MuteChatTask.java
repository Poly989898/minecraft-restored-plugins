package litebans;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MuteChatTask extends CommandTask {
   public static final MuteChatTaskCompanion MUTE_CHAT_TASK_COMPANION = new MuteChatTaskCompanion(null);
   private ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();

   public MuteChatTask(@NotNull Platform platform) {
      super(platform);
   }

   public final ConcurrentHashMap getMutedPlayers() {
      return this.concurrentHashMap;
   }

   public final void setMutedPlayers(@NotNull ConcurrentHashMap concurrentHashMap) {
      this.concurrentHashMap = concurrentHashMap;
   }

   public final void handleObj(@NotNull Object obj, @NotNull String text, @NotNull LiteSender liteSender, boolean flag) {
      UUID uuid = liteSender.getUuid();
      CommandTask commandTask = this;
      String senderAddress = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSenderAddress(liteSender, obj);
      CommandTask commandTask2 = this;
      PunishmentLookupModule module = (PunishmentLookupModule)commandTask2.getPlatform().getModule(PunishmentLookupModule.class);
      String serverName = liteSender.getServerName();
      if (serverName == null) {
         CommandTask commandTask3 = this;
         serverName = ((DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class)).getServerName();
      }

      Punishment punishment = module.findPunishment(uuid, senderAddress, serverName);
      CommandTask commandTask4 = this;
      ConfigModule module2 = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
      byte b2 = 100;
      if (module2.isDebugAtLeast(b2)) {
         module2.logDebug("[+] Mute for " + liteSender.getName() + " is " + punishment);
      }

      if (punishment != null) {
         if (!flag && text.length() != 0 && text.charAt(0) == '/') {
            CharSequence message = text;
            char[] chars = new char[]{' '};
            String string = (String)StringsKt.splitWithDefaults(message, chars, false, 0, 6, null).get(0);
            CommandTask commandTask5 = this;
            Settings settings = ((ConfigModule)commandTask5.getPlatform().getModule(ConfigModule.class)).getSettings();
            String text2 = text;
            String lowerCase = text2.toLowerCase(Locale.ROOT);
            if (TextUtil.hasDoubleSpace(lowerCase)) {
               lowerCase = TextUtil.collapseWhitespace(lowerCase);
            }

            if (StringsKt.containsWithDefaults(string, ':', false, 2, null) && settings.isMutesPreventBypass()) {
               lowerCase = this.stripNamespace(lowerCase);
            }

            var list = settings.getMutesCommandPrefixes();
            boolean flag2;
            if (list instanceof Collection && ((Collection)list).isEmpty()) {
               flag2 = false;
            } else {
               Iterator iterator2 = list.iterator();

               while (true) {
                  if (!iterator2.hasNext()) {
                     flag2 = false;
                     break;
                  }

                  Object obj2 = iterator2.next();
                  String string2 = (String)obj2;
                  if (StringsKt.checkText(lowerCase, string2, false, 2, null)) {
                     flag2 = true;
                     break;
                  }
               }
            }

            if (flag2 || settings.getMutesCommandBlacklist().contains(lowerCase)) {
               this.handleObj(obj, liteSender, text, punishment);
            }
         } else {
            this.handleObj(obj, liteSender, text, punishment);
         }
      }
   }

   public static void handleMuteChatTask(MuteChatTask muteChatTask, Object obj, String text, LiteSender liteSender, boolean flag, int value, Object obj2) {
      if ((value & 8) != 0) {
         flag = false;
      }

      muteChatTask.handleObj(obj, text, liteSender, flag);
   }

   private final String stripNamespace(String text) {
      CharSequence message = text;
      Regex regex = new Regex("^/[^ ]+:");
      String text2 = "/";
      return regex.replaceFirst(message, text2);
   }

   public final void handleObj(@NotNull Object obj, @NotNull LiteSender liteSender, @Nullable String text, @Nullable Punishment punishment) {
      PlayerKicker.disallowLoginWithDefaults(this.getPlatform().createBridge(), obj, null, null, false, null, 30, null);
      if (punishment != null) {
         CommandTask commandTask = this;
         PunishmentLookupModule module = (PunishmentLookupModule)commandTask.getPlatform().getModule(PunishmentLookupModule.class);
         CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
         LiteSender liteSender2 = liteSender;

         try {
            commandOutput.getSenderHolder().set(liteSender2);
            Platform platform = module.platform;
            CharSequence message = text;
            Intrinsics.checkNotNull(text);
            new MuteNotifyTask(platform, punishment, message).run();
         } finally {
            commandOutput.getSenderHolder().remove();
         }
      }
   }
}
