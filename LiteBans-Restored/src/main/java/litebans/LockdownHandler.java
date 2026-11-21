package litebans;

import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LockdownHandler extends CommandTask implements Named {
   private final CharSequence kickMessage;
   private final String reason;
   private final boolean flag39;
   private PlatformListener platformListener2;

   public LockdownHandler(@NotNull Platform platform, @Nullable CharSequence message, @NotNull String text, boolean flag) {
      super(platform);
      this.kickMessage = message;
      this.reason = text;
      this.flag39 = flag;
   }

   public final CharSequence getKickMessage() {
      return this.kickMessage;
   }

   public final String getReason() {
      return this.reason;
   }

   @Override
   public void run() {
      boolean flag = false;
      if (this.kickMessage != null && this.getPlatform().getPlatformId() != 3) {
         ListenerModule moduleOrNull = (ListenerModule)this.getPlatform().getModuleOrNull(ListenerModule.class);
         this.platformListener2 = moduleOrNull.computePlatformListener(this);
         if (this.platformListener2 != null) {
            this.platformListener2.obtainPlatformListener();
         }
      }

      if (this.kickMessage != null) {
         String formatKickMessage2 = this.formatKickMessage(this.reason, false);
         Object[] onlineSenders = this.getPlatform().getOnlineSenders();
         var arrayList = new ArrayList();
         int value = 0;

         for (int i = onlineSenders.length; value < i; value++) {
            Object obj = onlineSenders[value];
            LiteSender liteSender = (LiteSender)obj;
            ServerScope serverScope = ServerScopes.SERVER_SCOPE;
            String serverName = liteSender.getServerName();
            if (serverName == null) {
               CommandTask commandTask = this;
               serverName = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName();
            }

            label65: {
               label80: {
                  if (!serverScope.scopesMatch(serverName, this.reason)) {
                     if (this.getPlatform().getPlatformId() == 0) {
                        break label80;
                     }

                     CommandTask commandTask2 = this;
                     if (!ServerScopes.SERVER_SCOPE.scopesMatch(((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName(), this.reason)) {
                        break label80;
                     }
                  }

                  String text = "litebans.lockdown.bypass";
                  if (!liteSender.hasPermission(text)) {
                     flag = true;
                     break label65;
                  }
               }

               flag = false;
            }

            if (flag) {
               arrayList.add(obj);
            }
         }


         for (Object obj2 : arrayList) {
            LiteSender liteSender2 = (LiteSender)obj2;
            CommandTask commandTask3 = this;
            BroadcastModule module = (BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class);
            Intrinsics.checkNotNull(liteSender2);
            module.sendPluginMessage(liteSender2, formatKickMessage2);
         }
      }

      if (this.flag39) {
         CommandTask commandTask4 = this;
         if (((DatabaseModule)commandTask4.getPlatform().getModule(DatabaseModule.class)).isConnected()) {
            LiteSender liteSender3 = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
            if (liteSender3 == null) {
               liteSender3 = this.getPlatform().getConsole();
            }

            LiteSender liteSender4 = liteSender3;
            CommandTask commandTask5 = this;
            ((DatabaseModule)commandTask5.getPlatform().getModule(DatabaseModule.class)).handleFunction(new KotlinFunction1Impl9(this, liteSender4));
         }
      }
   }

   @Override
   public void perform2() {
      this.getPlatform().runSync(this);
   }

   @Override
   public void perform3() {
      PlatformListener platformListener = this.platformListener2;
      if (platformListener != null) {
         Platform platform = this.getPlatform();
         PlatformListener[] platformListeners = new PlatformListener[]{platformListener};
         platform.unregisterListeners(platformListeners);
      }
   }

   public final void handleObj(@NotNull Object obj, @NotNull String text, boolean flag) {
      PlayerKicker.disallowLoginWithDefaults(this.getPlatform().createBridge(), obj, text, null, flag, "KICK_OTHER", 4, null);
   }

   public static void handleLockdownHandler(LockdownHandler lockdownHandler, Object obj, String text, boolean flag, int value, Object obj2) {
      if ((value & 2) != 0) {
         text = "";
      }

      if ((value & 4) != 0) {
         flag = true;
      }

      lockdownHandler.handleObj(obj, text, flag);
   }

   public final String formatKickMessage(@NotNull String text, boolean flag) {
      CharSequence message = flag ? Message.LOCKDOWN_KICK_MESSAGE_GLOBAL : Message.LOCKDOWN_KICK_MESSAGE;
      CharSequence[] messages = new CharSequence[4];
      messages[0] = "reason";
      CharSequence message2 = this.kickMessage;
      if (this.kickMessage == null) {
         message2 = "";
      }

      messages[1] = message2;
      messages[2] = "server";
      messages[3] = text;
      return Message.computeText(TextUtil.replacePairs(message, messages), true);
   }

   @Override
   public String getName() {
      return "lockdown";
   }
}
