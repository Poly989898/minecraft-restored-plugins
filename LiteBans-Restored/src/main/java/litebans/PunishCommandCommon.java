package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class PunishCommandCommon extends AbstractCommand {
   public PunishCommandCommon(@NotNull String text, @NotNull Platform platform) {
      super(text, platform);
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull PunishmentRequest punishmentRequest) {
      PunishmentRequest punishmentRequest2 = punishmentRequest;
      if (punishmentRequest2.hasTemplate()) {
         ThrottleModule module = (ThrottleModule)databaseSession.getPlatform().getModule(ThrottleModule.class);
         List list = CollectionsKt.emptyList();
         if (punishmentRequest2.getTemplateGroup() != null) {
            if (punishmentRequest2.isFlag16()) {
               TemplateGroup templateGroup = punishmentRequest2.getTemplateGroup();
               Intrinsics.checkNotNull(templateGroup);
               list = templateGroup.computeTemplateInfo(punishmentRequest2.getStackIndex()).getActions();
            }
         } else {
            list = punishmentRequest2.getTemplate().getInfoForCount(punishmentRequest2.getStackIndex()).getActions();
            if (list.isEmpty()) {
               list = punishmentRequest2.getTemplate().getDefaultInfo().getActions();
            }
         }

         for (String text : (Iterable<String>) list) {
            String text2 = text;
            boolean flag = false;
            if (StringsKt.checkText(text, "console:", false, 2, null)) {
               flag = true;
               text2 = StringsKt.substringAfterWithDefaults(text2, "console:", null, 2, null);
            }

            if (StringsKt.startsWithWithDefaults(text2, '/', false, 2, null)) {
               text2 = text2.substring(1);
            }

            CharSequence message = text2;
            char[] chars = new char[]{' '};
            String first2 = (String)CollectionsKt.first(StringsKt.splitWithDefaults(message, chars, false, 0, 6, null));
            boolean flag2 = this.checkText(first2);
            if (flag && !StringsKt.containsWithDefaults((CharSequence)text2, (CharSequence)"--sender", false, 2, null) && flag2) {
               text2 = text2 + " --sender-uuid=" + punishmentRequest2.getSender().getUuidString() + " --sender=" + punishmentRequest2.getSender().getName();
            }

            if (flag2) {
               text2 = text2 + " --stack=" + (punishmentRequest.getTemplateId() + 1);
            }

            String string = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, text2, punishment, false, 2, null), punishmentRequest2.getTargetRecord(), false, 2, null).toString();
            String text3 = Placeholders.replacePlaceholders(databaseSession, string, punishmentRequest);
            DatabaseSession databaseSession2 = databaseSession;
            Intrinsics.checkNotNull(module);
            CommandTask commandTask = this;
            ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).debug(10, text3);
            LiteSender liteSender = flag ? databaseSession2.getPlatform().getConsole() : this.getSender();
            if (liteSender.isPlayer()) {
               module.handleByteValue((byte)3, liteSender.getUuidString());
            }

            databaseSession2.getPlatform().runSync(new SendMessageTask(liteSender, text3));
         }
      }
   }

   private final boolean checkText(String text) {
      if (!Intrinsics.areEqual(text, "ban") && !Intrinsics.areEqual(text, "kick")) {
         String[] texts = new String[]{
            "mute",
            "warn",
            "unban",
            "unmute",
            "unwarn",
            "tempban",
            "tempmute",
            "ipban",
            "banip",
            "ban-ip",
            "ipmute",
            "muteip",
            "tempipban",
            "tempbanip",
            "tempipmute",
            "tempmuteip"
         };
         if (!ArraysKt.contains(texts, text)) {
            return false;
         }
      }

      return true;
   }

   public final void handlePunishmentRequest(@NotNull PunishmentRequest punishmentRequest) {
      CommandTask commandTask = this;
      ConfigFile configFile = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getMessages().getConfigFile();
      if ((configFile != null ? configFile.getError() : null) != null) {
         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, punishmentRequest.getSender(), Message.RELOAD_FAIL_MESSAGES, null, 4, null);
      }

      CommandTask commandTask2 = this;
      if (((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getTemplateManager().getConfig() == null) {
         CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, punishmentRequest.getSender(), Message.RELOAD_FAIL_TEMPLATES, null, 4, null);
      }
   }
}
