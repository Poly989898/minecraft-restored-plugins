package litebans;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActiveMuteNoticeTask extends CommandTask {
   private final String text46;
   private final String text47;
   private final String text48;
   private final String text49;

   public ActiveMuteNoticeTask(@NotNull Platform platform, @Nullable String text, @NotNull String text2, @Nullable String text3, @Nullable String text4) {
      super(platform);
      this.text46 = text;
      this.text47 = text2;
      this.text48 = text3;
      this.text49 = text4;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isMutesEnabled()) {
         CommandTask commandTask2 = this;
         ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
            DatabaseSession databaseSession = (DatabaseSession) obj;
            Object obj2 = null;
            String text = this.text49;
            if (this.text49 == null) {
               CommandTask commandTask3 = this;
               String text2 = this.text47;
               Object[] onlineSenders = commandTask3.getPlatform().getOnlineSenders();
               int value = 0;
               int length2 = onlineSenders.length;

               while (true) {
                  if (value >= length2) {
                     obj2 = null;
                     break;
                  }

                  Object obj22 = onlineSenders[value];
                  LiteSender liteSender = (LiteSender)obj22;
                  if (Intrinsics.areEqual(liteSender.getUuidString(), text2)) {
                     obj2 = obj22;
                     break;
                  }

                  value++;
               }

               text = (LiteSender)obj2 != null ? ((LiteSender)obj2).getServerName() : null;
               if (text == null) {
                  CommandTask commandTask22 = this;
                  text = ((ConfigModule)commandTask22.getPlatform().getModule(ConfigModule.class)).getSettings().getServerName();
               }
            }

            String text3 = TextUtil.access$length(text, 32);
            Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(databaseSession, this.text47, this.text48, PunishmentType.MUTE, text3, false, false, 48, null);
            if (punishment == null && this.text46 != null) {
               CommandTask commandTask32 = this;
               if (((ConfigModule)commandTask32.getPlatform().getModule(ConfigModule.class)).getSettings().isBanNames()) {
                  punishment = databaseSession.findPunishment(this.text46, this.text47, text3, PunishmentType.MUTE);
               }
            }

            CommandTask commandTask4 = this;
            ConfigModule module = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
            byte b = 3;
            if (module.isDebugAtLeast(b)) {
               module.logDebug("Active mute for " + this.text47 + " on server " + text3 + ": " + punishment);
            }

            if (punishment != null) {
               CommandTask commandTask5 = this;
               ((PunishmentLookupModule)commandTask5.getPlatform().getModule(PunishmentLookupModule.class)).handleText(this.text47, punishment);
            } else {
               CommandTask commandTask6 = this;
               ((PunishmentLookupModule)commandTask6.getPlatform().getModule(PunishmentLookupModule.class)).getMuteChatTask().getMutedPlayers().remove(UUID.fromString(this.text47));
            }

            return Unit.UNIT;
         });
      }
   }

}
