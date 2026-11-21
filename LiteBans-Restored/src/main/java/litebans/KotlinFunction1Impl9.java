package litebans;

import java.util.ArrayList;

public final class KotlinFunction1Impl9 implements Function1 {
   final LockdownHandler lockdownHandler2;
   final LiteSender liteSender4;

   public KotlinFunction1Impl9(LockdownHandler lockdownHandler, LiteSender liteSender) {
      this.lockdownHandler2 = lockdownHandler;
      this.liteSender4 = liteSender;
   }

   public final void handleDatabaseSession(DatabaseSession databaseSession) {
      CharSequence message;
      if (this.lockdownHandler2.getKickMessage() != null) {
         message = Message.MESSAGE_5;
         CharSequence[] messages = new CharSequence[]{"serverScope", this.lockdownHandler2.getReason(), "reason", this.lockdownHandler2.getKickMessage()};
         message = TextUtil.replacePairs(message, messages);
      } else {
         message = Message.STOPPED;
      }

      CharSequence message2 = message;
      LiteSender liteSender = this.liteSender4;
      Intrinsics.checkNotNull(liteSender);
      CharSequence replacePlaceholdersNullable2 = databaseSession.replacePlaceholdersNullable(message2, liteSender);
      Object[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
      LockdownHandler lockdownHandler = this.lockdownHandler2;
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = onlineSenders.length; value < i; value++) {
         Object obj = onlineSenders[value];
         LiteSender liteSender2 = (LiteSender)obj;
         ServerScope serverScope = ServerScopes.SERVER_SCOPE;
         String serverName = liteSender2.getServerName();
         if (serverName == null) {
            CommandTask commandTask = lockdownHandler;
            serverName = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName();
         }

         if (serverScope.scopesMatch(serverName, lockdownHandler.getReason())) {
            arrayList.add(obj);
         }
      }


      for (Object obj2 : arrayList) {
         LiteSender liteSender3 = (LiteSender)obj2;
         Intrinsics.checkNotNull(liteSender3);
         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender3, replacePlaceholdersNullable2, null, 4, null);
      }

      CommandTask commandTask2 = this.lockdownHandler2;
      if (((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isNotifyNotifyConsole()) {
         if (databaseSession.getPlatform().getPlatformId() == 3) {
            databaseSession.getPlatform().getLogger().info(replacePlaceholdersNullable2.toString());
         } else {
            LiteSender console = databaseSession.getPlatform().getConsole();
            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, console, replacePlaceholdersNullable2, null, 4, null);
         }
      }
   }

   @Override
   public Object computeObj(Object obj) {
      this.handleDatabaseSession((DatabaseSession)obj);
      return Unit.UNIT;
   }
}
