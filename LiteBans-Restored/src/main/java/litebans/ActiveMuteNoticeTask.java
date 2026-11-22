package litebans;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ActiveMuteNoticeTask extends CommandTask {
   private final String serverScope;
   private final String uuid;
   private final String ip;
   private final String serverName;

   public ActiveMuteNoticeTask(@NotNull Platform platform, @Nullable String text, @NotNull String text2, @Nullable String text3, @Nullable String text4) {
      super(platform);
      this.serverScope = text;
      this.uuid = text2;
      this.ip = text3;
      this.serverName = text4;
   }

   @Override
   public void run() {
      CommandTask commandTask = this;
      if (((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().isMutesEnabled()) {
         CommandTask commandTask2 = this;
         ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).handleFunction(obj -> {
            DatabaseSession databaseSession = (DatabaseSession) obj;
            Object obj2 = null;
            String text = this.serverName;
            if (this.serverName == null) {
               CommandTask commandTask3 = this;
               String text2 = this.uuid;
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
            Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(databaseSession, this.uuid, this.ip, PunishmentType.MUTE, text3, false, false, 48, null);
            if (punishment == null && this.serverScope != null) {
               CommandTask commandTask32 = this;
               if (((ConfigModule)commandTask32.getPlatform().getModule(ConfigModule.class)).getSettings().isBanNames()) {
                  punishment = databaseSession.findPunishment(this.serverScope, this.uuid, text3, PunishmentType.MUTE);
               }
            }

            CommandTask commandTask4 = this;
            ConfigModule module = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
            byte b = 3;
            if (module.isDebugAtLeast(b)) {
               module.logDebug("Active mute for " + this.uuid + " on server " + text3 + ": " + punishment);
            }

            if (punishment != null) {
               CommandTask commandTask5 = this;
               ((PunishmentLookupModule)commandTask5.getPlatform().getModule(PunishmentLookupModule.class)).handleText(this.uuid, punishment);
            } else {
               CommandTask commandTask6 = this;
               ((PunishmentLookupModule)commandTask6.getPlatform().getModule(PunishmentLookupModule.class)).getMuteChatTask().getMutedPlayers().remove(UUID.fromString(this.uuid));
            }

            return Unit.UNIT;
         });
      }
   }

}
