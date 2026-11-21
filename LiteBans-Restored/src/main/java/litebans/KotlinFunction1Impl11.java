package litebans;

public final class KotlinFunction1Impl11 implements Function1 {
   final boolean flag3;
   final String text8;
   final BanCommand banCommand;
   final String text9;
   final LiteSender liteSender;

   public KotlinFunction1Impl11(boolean flag, String text, BanCommand banCommand, String text2, LiteSender liteSender) {
      this.flag3 = flag;
      this.text8 = text;
      this.banCommand = banCommand;
      this.text9 = text2;
      this.liteSender = liteSender;
   }

   public final void handleDatabaseSession2(DatabaseSession databaseSession) {
      if (this.flag3) {
         CharSequence message = this.text8;
         CommandTask commandTask = this.banCommand;
         byte usernameMaxLength = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getUsernameMaxLength();
         PunishmentQueries.saveLoginWithDefaults(databaseSession, TextUtil.access$length(message.toString(), usernameMaxLength & 255), this.text9, this.liteSender.getIpAddress(), false, false, 16, null);
      } else if (this.liteSender.isPlayer()) {
         CharSequence name = this.liteSender.getName();
         CommandTask commandTask2 = this.banCommand;
         byte usernameMaxLength2 = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().getUsernameMaxLength();
         PunishmentQueries.saveLoginWithDefaults(databaseSession, TextUtil.access$length(name.toString(), usernameMaxLength2 & 255), this.liteSender.getUuidString(), this.liteSender.getIpAddress(), false, false, 16, null);
      } else if ((!this.liteSender.isConsole() || StringsKt.checkText(this.text8, "CONSOLE", true)) && this.liteSender.isConsole()) {
         if (this.liteSender.isConsole()) {
            PunishmentQueries.saveLoginWithDefaults(databaseSession, "CONSOLE", "CONSOLE", "#", false, false, 16, null);
         }
      } else {
         CharSequence message2 = this.text8;
         CommandTask commandTask3 = this.banCommand;
         byte usernameMaxLength3 = ((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).getSettings().getUsernameMaxLength();
         PunishmentQueries.saveLoginWithDefaults(databaseSession, TextUtil.access$length(message2.toString(), usernameMaxLength3 & 255), this.text9, "#", false, false, 16, null);
      }
   }

   @Override
   public Object computeObj(Object obj) {
      this.handleDatabaseSession2((DatabaseSession)obj);
      return Unit.UNIT;
   }
}
