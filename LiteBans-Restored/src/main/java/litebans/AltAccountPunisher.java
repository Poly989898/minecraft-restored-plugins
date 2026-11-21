package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import litebans.api.PlayerProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AltAccountPunisher {
   public static final String getReasonArgument(@NotNull CommandTask commandTask, @NotNull LiteSender liteSender, @NotNull String[] texts, @NotNull String text) {
      ServerScope serverScope = ServerScopes.SERVER_SCOPE;
      CommandTask commandTask2 = commandTask;
      String serverName = ((DatabaseModule)commandTask2.getPlatform().getModule(DatabaseModule.class)).getServerName();
      ServerScopes scopes = serverScope.parseScopes(liteSender, CollectionsKt.mutableListOf(Arrays.copyOf(texts, texts.length)), serverName, false);
      CharSequence message = scopes.getScope();
      return message == null || message.length() == 0 ? ServerScopes.SERVER_SCOPE.resolveScope(text, serverName, liteSender) : scopes.getScope();
   }

   public static String getReasonArgumentWithDefaults(CommandTask commandTask, LiteSender liteSender, String[] texts, String text, int value, Object obj) {
      if ((value & 4) != 0) {
         text = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getDefaultServerScope();
      }

      return getReasonArgument(commandTask, liteSender, texts, text);
   }

   public static final void sendMessage(@NotNull CommandTask commandTask, @NotNull CharSequence message) {
      LiteSender liteSender = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
      if (liteSender != null) {
         CommandTask.COMMAND_OUTPUT.send(liteSender, message, commandTask.getPlatform());
      }
   }

   public static final Punishment savePunishment(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, boolean flag, boolean flag2) {
      Punishment punishment2 = punishment;
      if (flag) {
         KotlinHelper29.KOTLIN_HELPER.handleDatabaseSession2(databaseSession, databaseSession.getPlatform(), punishment);
         PunishmentLookupModule module = (PunishmentLookupModule)databaseSession.getPlatform().getModule(PunishmentLookupModule.class);
         BroadcastModule module2 = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
         if (flag2) {
            module.isMutedInChat(punishment);
         }

         module2.isBungeeBan(punishment);
      } else {
         databaseSession.insertPunishment(punishment);
         punishment.setId(databaseSession.getPunishmentId(punishment));
         KotlinHelper29.KOTLIN_HELPER.handleDatabaseSession(databaseSession, databaseSession.getPlatform(), punishment);
      }

      return punishment2;
   }

   public static Punishment savePunishmentWithDefaults(DatabaseSession databaseSession, Punishment punishment, boolean flag, boolean flag2, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = false;
      }

      if ((value & 4) != 0) {
         flag2 = true;
      }

      return savePunishment(databaseSession, punishment, flag, flag2);
   }

   public static final void sendSync(@NotNull DatabaseSession databaseSession, @NotNull SyncMessageType syncMessageType, @NotNull CharSequence message, @Nullable PunishmentType punishmentType, @Nullable String text) {
      SyncModule module = (SyncModule)databaseSession.getPlatform().getModule(SyncModule.class);
      CharSequence message2 = message;
      if (punishmentType != null) {
         CharSequence[] messages = new CharSequence[]{message, punishmentType, null};
         String text2 = text;
         if (text == null) {
            text2 = "";
         }

         messages[2] = text2;
         message2 = ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null);
      }

      SyncModule.sendSyncMessageWithDefaults(module, syncMessageType, databaseSession, message2, 0, 8, null);
   }

   public static void sendSyncWithDefaults(DatabaseSession databaseSession, SyncMessageType syncMessageType, CharSequence message, PunishmentType punishmentType, String text, int value, Object obj) {
      if ((value & 4) != 0) {
         punishmentType = null;
      }

      if ((value & 8) != 0) {
         text = null;
      }

      sendSync(databaseSession, syncMessageType, message, punishmentType, text);
   }

   public static final void notifyPunishment(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, int value) {
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      ConfigModule module2 = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      LiteSender[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
      Intrinsics.checkNotNull(onlineSenders);
      if (onlineSenders.length != 0) {
         String serverScope = punishment.getServerScope();
         if (serverScope == null) {
            ConfigModule.handleConfigModule(module2, punishment, 0, 2, null);
         } else {
            var arrayList = new ArrayList();
            int value2 = 0;

            for (int i = onlineSenders.length; value2 < i; value2++) {
               LiteSender liteSender = onlineSenders[value2];
               Intrinsics.checkNotNull(liteSender);
               if (databaseSession.isAffecting(punishment, liteSender) && isAllowedFor(databaseSession, punishment, liteSender)) {
                  arrayList.add(liteSender);
               }
            }


            for (Object obj : arrayList) {
               LiteSender liteSender2 = (LiteSender)obj;
               Intrinsics.checkNotNull(liteSender2);
               CharSequence punishmentMessage = Placeholders.getPunishmentMessage(databaseSession, punishment, liteSender2, value);
               module.sendMessageSync(liteSender2, punishmentMessage);
            }
         }
      }
   }

   public static void notifyPunishmentWithDefaults(DatabaseSession databaseSession, Punishment punishment, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = -1;
      }

      notifyPunishment(databaseSession, punishment, value);
   }

   public static final boolean isAllowedFor(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull LiteSender liteSender) {
      return !punishment.isIpBan() || Intrinsics.areEqual(punishment.getUuid(), liteSender.getUuidString()) || !databaseSession.hasAllowEntry(liteSender.getUuid(), punishment.getType());
   }

   public static final Punishment createDupePunishment(
      @NotNull DatabaseSession databaseSession,
   @NotNull DupeIpResult dupeIpResult,
   @NotNull Punishment punishment,
   @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   @Nullable String text4,
   boolean flag
) {
      Punishment punishment2 = punishment;
      BroadcastModule module = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      ConfigModule module2 = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      LiteSender liteSender = (LiteSender)CommandTask.COMMAND_OUTPUT.getSenderHolder().get();
      if (liteSender == null) {
         liteSender = databaseSession.getPlatform().getConsole();
      }

      LiteSender liteSender2 = liteSender;
      databaseSession.getPlatform().getLogger().info("Banning linked account: " + text + " (linked to " + dupeIpResult.getText3() + " via " + text4 + ')');
      PunishmentType type = punishment2.getType();
      boolean isSilent2 = punishment2.isSilent();
      long time = punishment2.getTime();
      long until = punishment2.getUntil();
      Template template = module2.getTemplateManager().findTemplate(punishment2);
      if (template == null) {
         template = Template.TEMPLATE_COMPANION.obtainTemplate();
      }

      Template template2 = template;
      LoginRecord loginRecord = new LoginRecord(text, text3, text4, null, 8, null);
      Intrinsics.checkNotNull(liteSender2);
      String staffName = databaseSession.getStaffName(liteSender2);
      String uuidString = liteSender2.getUuidString();
      String serverOrigin = punishment2.getServerOrigin();
      String serverScope = punishment2.getServerScope();
      if (serverScope == null) {
         serverScope = ServerScopes.SERVER_SCOPE.getLocalScope();
      }

      String text5 = serverScope;
      PunishmentRequest punishmentRequest = new PunishmentRequest(
         type,
         false,
         isSilent2,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         false,
         true,
         time,
         until,
         0L,
         (byte)0,
         template2,
         null,
         false,
         null,
         0,
         null,
         loginRecord,
         null,
         null,
         liteSender2,
         staffName,
         uuidString,
         text5,
         serverOrigin,
         null,
         null,
         null,
         null,
         null,
         117121018,
         31,
         null
      );
      punishmentRequest.getReasonBuilder().append(punishment2.getReason());
      punishmentRequest.fireStage(PunishmentStage.POST_TARGET);
      PunishmentType type2 = punishmentRequest.getType();
      LoginRecord targetRecord = punishmentRequest.getTargetRecord();
      String text6 = targetRecord != null ? targetRecord.getUuid() : null;
      LoginRecord targetRecord2 = punishmentRequest.getTargetRecord();
      Punishment punishment3 = new Punishment(
         type2,
         text6,
         targetRecord2 != null ? targetRecord2.getIp() : null,
         punishmentRequest.getReasonBuilder().toString(),
         punishment2.getBannedByUuid(),
         punishment2.getBannedByName(),
         punishmentRequest.getServerScope(),
         punishmentRequest.getServerOrigin(),
         punishmentRequest.getCreationTime(),
         punishmentRequest.getUntil(),
         punishmentRequest.getTemplate().getId(),
         punishmentRequest.isSilent(),
         punishmentRequest.isIpBan() || flag,
         punishment2.isActive(),
         0L,
         16384,
         null
      );
      punishmentRequest.setPunishment(punishment3);
      punishmentRequest.fireStage(PunishmentStage.ENTRY_CREATED);
      savePunishmentWithDefaults(databaseSession, punishment3, false, false, 6, null);
      punishmentRequest.fireStage(PunishmentStage.ENTRY_EMITTED);
      notifyPunishmentWithDefaults(databaseSession, punishment3, 0, 2, null);
      punishmentRequest.fireStage(PunishmentStage.ENTRY_APPLIED);
      module.handleDatabaseSession(databaseSession, punishment3, text);
      punishmentRequest.fireStage(PunishmentStage.FINALIZED);
      return punishment3;
   }

   public static Punishment computePunishment(DatabaseSession databaseSession, DupeIpResult dupeIpResult, Punishment punishment, String text, String text2, String text3, String text4, boolean flag, int value, Object obj) {
      if ((value & 16) != 0) {
         text3 = dupeIpResult.getText4();
      }

      if ((value & 32) != 0) {
         text4 = text2;
      }

      if ((value & 64) != 0) {
         flag = false;
      }

      return createDupePunishment(databaseSession, dupeIpResult, punishment, text, text2, text3, text4, flag);
   }

   public static final CharSequence computeMessage(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @Nullable LoginRecord loginRecord, long value) {
      CharSequence message = PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Message.COOLDOWN, loginRecord, false, 2, null);
      CharSequence[] messages = new CharSequence[]{"duration", Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value, false, 2, null)};
      return PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession, Placeholders.replacePlaceholder(TextUtil.replacePairs(message, messages), "seconds", value / 1000), punishment, false, 2, null);
   }

   public static final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull DatabaseSession databaseSession2, @NotNull Punishment punishment, long value, long value2, long value3, @Nullable LoginRecord loginRecord, boolean flag) {
      Punishment punishment2 = PunishmentQueries.findPunishmentWithDefaults(databaseSession2, punishment.getUuid(), punishment.getIp(), punishment.getType(), punishment.getServerScope(), true, false, 32, null);
      if ((punishment2 != null ? punishment2.isActive() : false) && isWithinWindow(punishment2, value, value2)) {
         long timeDelta = getTimeDelta(punishment2, value, value2);
         CommandTask.COMMAND_OUTPUT.returnResult(computeMessage(databaseSession, punishment2, loginRecord, timeDelta));
         throw new CommandAbortException();
      }

      if (flag) {
         String bannedByUuid = punishment.getBannedByUuid();
         PunishmentType type = punishment.getType();
         String serverScope = punishment.getServerScope();
         if (serverScope == null) {
            serverScope = ServerScopes.SERVER_SCOPE.getLocalScope();
         }

         Punishment firstOrNull2 = (Punishment)CollectionsKt.firstOrNull(databaseSession2.getHistory(bannedByUuid, type, serverScope, 1));
         if ((firstOrNull2 != null ? firstOrNull2.isActive() : false) && isWithinWindow(firstOrNull2, value, value2)) {
            long timeDelta2 = getTimeDelta(firstOrNull2, value, value2);
            CommandTask.COMMAND_OUTPUT.returnResult(computeMessage(databaseSession, firstOrNull2, loginRecord, timeDelta2));
            throw new CommandAbortException();
         }

         if (value3 > 0L && Intrinsics.areEqual(firstOrNull2 != null ? firstOrNull2.getRemovedByUuid() : null, punishment.getBannedByUuid()) && !firstOrNull2.isActive() && isWithinWindow(firstOrNull2, value, value3)) {
            long timeDelta3 = getTimeDelta(firstOrNull2, value, value3);
            CommandTask.COMMAND_OUTPUT.returnResult(computeMessage(databaseSession, firstOrNull2, loginRecord, timeDelta3));
            throw new CommandAbortException();
         }
      }
   }

   public static void handleDatabaseSession(DatabaseSession databaseSession, DatabaseSession databaseSession2, Punishment punishment, long value, long value2, long value3, LoginRecord loginRecord, boolean flag, int value4, Object obj) {
      if ((value4 & 64) != 0) {
         flag = true;
      }

      handleDatabaseSession(databaseSession, databaseSession2, punishment, value, value2, value3, loginRecord, flag);
   }

   private static final boolean isWithinWindow(Punishment punishment, long value, long value2) {
      return value > punishment.getTime() && value - punishment.getTime() <= value2;
   }

   private static final long getTimeDelta(Punishment punishment, long value, long value2) {
      return Math.abs(value - punishment.getTime() - (value2 + 1000));
   }

   public static final String resolveName(@NotNull CommandTask commandTask, @NotNull String text) {
      String text2 = TargetParser.TARGET_PARSER.parseTarget(text).getByteValue() == 0 ? PlayerProvider.get().provide(text) : text;
      Intrinsics.checkNotNull(text2);
      return text2;
   }

   public static final String computeText(@NotNull CommandTask commandTask, @NotNull String text) {
      if (!TargetParser.TARGET_PARSER.isIpv6(text)) {
         return text;
      }

      CommandTask commandTask2 = commandTask;
      long value = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).obtainKotlinData2().computeLongValue(text);
      return value == -1L ? text : String.valueOf(value);
   }

   public static final LiteSender findTargetSender(@NotNull CommandTask commandTask, @NotNull String text) {
      PunishmentTarget target = TargetParser.TARGET_PARSER.parseTarget(text);
      if (target.getByteValue() != 1) {
         String text2 = text;
         CommandTask commandTask2 = commandTask;
         boolean flag = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings().isAutocompleteOnlinePlayerNames();
         if (flag) {
            String text3 = text2;
            text2 = text3.toLowerCase(Locale.ENGLISH);
         }

         LiteSender liteSender = null;
         Object[] onlineSenders = commandTask.getPlatform().getOnlineSenders();
         int value = 0;

         for (int i = onlineSenders.length; value < i; value++) {
            Object obj = onlineSenders[value];
            LiteSender liteSender2 = (LiteSender)obj;
            String name = liteSender2.getName();
            if (flag) {
               if (StringsKt.checkText(name.toLowerCase(Locale.ENGLISH), text2, false, 2, null)) {
                  liteSender = liteSender2;
               }
            }

            if (StringsKt.checkText(name, text2, true)) {
               return liteSender2;
            }
         }

         return liteSender;
      } else {
         CommandTask commandTask3 = commandTask;
         Object[] onlineSenders2 = commandTask3.getPlatform().getOnlineSenders();
         int value2 = 0;
         int length2 = onlineSenders2.length;

         Object obj2;
         while (true) {
            if (value2 >= length2) {
               obj2 = null;
               break;
            }

            Object obj3 = onlineSenders2[value2];
            LiteSender liteSender3 = (LiteSender)obj3;
            if (Intrinsics.areEqual(liteSender3.getUuidString(), text)) {
               obj2 = obj3;
               break;
            }

            value2++;
         }

         return (LiteSender)obj2;
      }
   }

   public static final void handleCommandTask(@NotNull CommandTask commandTask, @NotNull String text) {
      LiteSender sender = commandTask.getSender();
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean flag = !sender.hasPermission(text);
      CharSequence message = Message.PERMISSION_ERROR;
      if (flag) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }
   }

   public static final void handleCommandTask(@NotNull CommandTask commandTask, @NotNull String[] texts) {
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      LiteSender sender = commandTask.getSender();
      String[] copyOf2 = Arrays.copyOf(texts, texts.length);
      String[] copyOf3 = Arrays.copyOf(copyOf2, copyOf2.length);
      int value = 0;
      int length2 = copyOf3.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = false;
            break;
         }

         String text = copyOf3[value];
         if (sender.hasPermission(text)) {
            flag = true;
            break;
         }

         value++;
      }

      boolean flag2 = !flag;
      CharSequence message = Message.PERMISSION_ERROR;
      if (flag2) {
         commandOutput.returnResult(message);
         throw new CommandAbortException();
      }
   }

   public static final void handleCommandTask(@NotNull CommandTask commandTask, @NotNull Template template, @NotNull TemplateInfo templateInfo) {
      String text;
      CommandOutput commandOutput;
      boolean flag;
      label39: {
         String text2 = template.getDefaultInfo().getPermission();
         text = templateInfo.getPermission();
         commandOutput = CommandTask.COMMAND_OUTPUT;
         if (text2 != null) {
            LiteSender sender = commandTask.getSender();
            if (!sender.hasPermission(text2)) {
               flag = true;
               break label39;
            }
         }

         flag = false;
      }

      boolean flag2 = flag;
      CharSequence message = Message.PERMISSION_ERROR;
      CommandOutput commandOutput2 = commandOutput;
      if (flag2) {
         commandOutput2.returnResult(message);
         throw new CommandAbortException();
      }

      CommandOutput commandOutput3;
      label32: {
         commandOutput3 = CommandTask.COMMAND_OUTPUT;
         if (text != null) {
            LiteSender sender2 = commandTask.getSender();
            if (!sender2.hasPermission(text)) {
               flag = true;
               break label32;
            }
         }

         flag = false;
      }

      boolean flag3 = flag;
      CharSequence message2 = Message.PERMISSION_ERROR;
      CommandOutput commandOutput4 = commandOutput3;
      if (flag3) {
         commandOutput4.returnResult(message2);
         throw new CommandAbortException();
      }
   }

   private static final LoginRecord requireLoginRecord(DatabaseSession databaseSession, String text) {
      LoginRecord loginRecord = databaseSession.findLoginRecord(text);
      if (loginRecord == null) {
         throw new EmptyMarker();
      } else {
         return loginRecord;
      }
   }

   public static final LoginRecord parseTargetRecord(@NotNull DatabaseSession databaseSession, @NotNull String text, boolean flag) {
      String text2 = text;
      if (StringsKt.containsWithDefaults(text2, ':', false, 2, null)) {
         text2 = StringsKt.substringAfterWithDefaults(text2, "user:", null, 2, null);
      }

      LoginRecord requireLoginRecord2 = requireLoginRecord(databaseSession, text2);
      if (flag) {
         boolean flag2;
         label24: {
            String uuid = requireLoginRecord2.getUuid();
            if (uuid != null) {
               Character character = StringsKt.computeCharacter(uuid, 14);
               byte b = 52;
               if (character != null && character == b) {
                  flag2 = true;
                  break label24;
               }
            }

            flag2 = false;
         }

         if (flag2 && !TargetParser.isUuid(text2)) {
            String uuid2 = requireLoginRecord2.getUuid();
            Intrinsics.checkNotNull(uuid2);
            return requireLoginRecord(databaseSession, uuid2);
         }
      }

      return requireLoginRecord2;
   }

   public static LoginRecord parseTargetRecordWithDefaults(DatabaseSession databaseSession, String text, boolean flag, int value, Object obj) {
      if ((value & 2) != 0) {
         flag = true;
      }

      return parseTargetRecord(databaseSession, text, flag);
   }

   public static final String[] computeTexts(@NotNull MutedCommandFilter mutedCommandFilter, @NotNull String text, @NotNull String[] texts) {
      String text2 = text;
      int length2 = texts.length;
      boolean flag = StringsKt.checkText(text2, "temp", false, 2, null);
      if (flag) {
         text2 = text2.substring(4);
      }

      boolean flag2 = StringsKt.checkText(text2, "litebans:", false, 2, null);
      if (flag2) {
         text2 = text2.substring(9);
      }

      String text3;
      label68: {
         label67: {
            label74: {
               String text4 = text2;
               switch (text4.hashCode()) {
                  case -1396405339:
                     if (text4.equals("ban-ip")) {
                        break label67;
                     }
                     break;
                  case -1182108736:
                     if (text4.equals("ipmute")) {
                        break label74;
                     }
                     break;
                  case -1062781696:
                     if (text4.equals("muteip")) {
                        break label74;
                     }
                     break;
                  case -840405966:
                     if (text4.equals("unmute")) {
                        text3 = "-MU";
                        break label68;
                     }
                     break;
                  case -840127329:
                     if (text4.equals("unwarn")) {
                        text3 = "-WU";
                        break label68;
                     }
                     break;
                  case 3363353:
                     if (text4.equals("mute")) {
                        text3 = "-M";
                        break label68;
                     }
                     break;
                  case 3641990:
                     if (text4.equals("warn")) {
                        text3 = "-W";
                        break label68;
                     }
                     break;
                  case 93503862:
                     if (text4.equals("banip")) {
                        break label67;
                     }
                     break;
                  case 100403592:
                     if (text4.equals("ipban")) {
                        break label67;
                     }
                     break;
                  case 111426262:
                     if (text4.equals("unban")) {
                        text3 = "-U";
                        break label68;
                     }
               }

               if (flag2) {
                  return texts;
               }

               text3 = "-";
               break label68;
            }

            text3 = "-IM";
            break label68;
         }

         text3 = "-I";
      }

      String text5 = text3;
      if (flag) {
         text5 = text5 + 'T';
      }

      String[] texts2 = (String[])ArraysKt.computeObjArray(texts, new String[length2 + 1], 1, 0, 0, 12, null);
      texts2[0] = text5;
      return Intrinsics.areEqual(text5, "-") ? texts : texts2;
   }
}
