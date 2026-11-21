package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class KickCommand extends PunishCommandCommon {
   public KickCommand(@NotNull Platform platform) {
      super("kick", platform);
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         CommandTask commandTask = this;
         DatabaseModule module = (DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module.openSession();
            Throwable error = null;

            try {
               CommandTask commandTask2 = this;
               Settings settings = ((ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class)).getSettings();

               label649: {
                  try {
                     label633: {
                        if (true) {
                           CommandTask commandTask3 = this;
                           if (!((ConfigModule)commandTask3.getPlatform().getModule(ConfigModule.class)).isVerbose()) {
                              break label633;
                           }

                           CommandArguments commandArguments;
                           ConfigModule configModule;
                           String[] texts2;
                           label629: {
                              CommandTask commandTask4 = this;
                              configModule = (ConfigModule)commandTask4.getPlatform().getModule(ConfigModule.class);
                              String name = this.getName();
                              switch (name.hashCode()) {
                                 case -1320563219:
                                    if (name.equals("dupeip")) {
                                       String[] texts3 = new String[]{"alts", "litebans:alts", "checkalts", "litebans:checkalts", "litebans:dupeip"};
                                       texts2 = texts3;
                                       break label629;
                                    }
                                    break;
                                 case 712910260:
                                    if (name.equals("staffhistory")) {
                                       String[] texts4 = new String[]{"staffhist", "litebans:staffhistory"};
                                       texts2 = texts4;
                                       break label629;
                                    }
                                    break;
                                 case 926934164:
                                    if (name.equals("history")) {
                                       String[] texts5 = new String[]{"hist", "litebans:history"};
                                       texts2 = texts5;
                                       break label629;
                                    }
                              }

                              String[] texts6 = new String[]{"litebans:" + name};
                              texts2 = texts6;
                           }

                           String[] texts7 = texts2;
                           commandArguments = new CommandArguments(configModule, Arrays.copyOf(texts7, texts7.length));
                           int value = commandArguments.obtainIntValue();
                           Object handle = liteSender.getHandle();
                           if (value != (handle != null ? handle.hashCode() : 0)) {
                              break label633;
                           }
                        }

                        throw new NullPointerException();
                     }
                  } catch (Exception e2) {
                     break label649;
                  }

                  PunishmentType punishmentType = PunishmentType.KICK;
                  boolean isSilent2 = settings.isSilent();
                  String defaultServerScope = settings.getDefaultServerScope();
                  String text = session.getDatabaseModule().computeText(liteSender);
                  long currentTime = DatabaseModule.getCurrentTimeWithDefaults(session.getDatabaseModule(), false, 1, null);
                  String staffName = session.getStaffName(liteSender);
                  PunishmentRequest punishmentRequest = new PunishmentRequest(
                     punishmentType,
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
                     false,
                     currentTime,
                     0L,
                     0L,
                     (byte)0,
                     null,
                     null,
                     false,
                     null,
                     0,
                     null,
                     null,
                     null,
                     null,
                     liteSender,
                     staffName,
                     null,
                     defaultServerScope,
                     text,
                     null,
                     null,
                     texts,
                     null,
                     null,
                     671072250,
                     27,
                     null
                  );

                  try {
                     PunishmentRequest.handlePunishmentRequest(punishmentRequest, session, null, false, 3, null);
                     punishmentRequest.fireStage(PunishmentStage.PRE_TARGET);
                     CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
                     boolean flag = punishmentRequest.getExtraArgs().isEmpty();
                     CharSequence message = Message.KICK_USAGE;
                     if (flag) {
                        commandOutput.returnResult(message);
                        throw new CommandAbortException();
                     }

                     CommandTask commandTask5 = this;
                     Iterable iterable = punishmentRequest.getExtraArgs();
                     punishmentRequest.setReason(AltAccountPunisher.resolveName(commandTask5, (String)CollectionsKt.first(iterable)));
                     LiteSender targetSender = AltAccountPunisher.findTargetSender(this, punishmentRequest.getReason());
                     KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
                     kotlinSerializable.obj = targetSender != null ? targetSender.getName() : null;
                     KotlinSerializableImpl kotlinSerializable2 = new KotlinSerializableImpl();
                     kotlinSerializable2.obj = targetSender != null ? targetSender.getUuidString() : null;
                     punishmentRequest.getExtraArgs().remove(0);
                     if (!punishmentRequest.getExtraArgs().isEmpty()) {
                        String first2 = (String)CollectionsKt.first(punishmentRequest.getExtraArgs());
                        CommandTask commandTask6 = this;
                        Template template = ((ConfigModule)commandTask6.getPlatform().getModule(ConfigModule.class)).getTemplateManager().computeTemplate(PunishmentType.KICK, first2);
                        if (template == null) {
                           template = Template.TEMPLATE_COMPANION.obtainTemplate();
                        }

                        punishmentRequest.setTemplate(template);
                        Template template2 = punishmentRequest.getTemplate();
                        if (Intrinsics.compare(template2.getId() & 255, -1 & 0xFF) < 0) {
                           punishmentRequest.getExtraArgs().remove(first2);
                        }
                     }

                     punishmentRequest.getReasonBuilder().append(CollectionsKt.joinToStringWithDefaults(punishmentRequest.getExtraArgs(), " ", null, null, 0, null, null, 62, null));
                     if (kotlinSerializable2.obj == null) {
                        boolean flag2;
                        label600: {
                           punishmentRequest.setTargetRecord(session.findLoginRecord(punishmentRequest.getReason()));
                           LoginRecord targetRecord = punishmentRequest.getTargetRecord();
                           if (targetRecord != null) {
                              String uuid = targetRecord.getUuid();
                              if (uuid != null) {
                                 flag2 = !StringsKt.startsWithWithDefaults(uuid, '#', false, 2, null);
                                 break label600;
                              }
                           }

                           flag2 = false;
                        }

                        if (!flag2) {
                           punishmentRequest.setTargetRecord((LoginRecord)null);
                        }

                        if (punishmentRequest.getTargetRecord() == null) {
                           CommandTask commandTask7 = this;
                           String reason = punishmentRequest.getReason();
                           boolean isOnlineMode2 = ((ConfigModule)commandTask7.getPlatform().getModule(ConfigModule.class)).getSettings().isOnlineMode();
                           LoginRecord loginRecord;
                           if (isOnlineMode2) {
                              if (TargetParser.isUuid(reason)) {
                                 String text2 = UsernameValidator.KOTLIN_DATA.computeText(reason);
                                 if (text2 == null) {
                                    loginRecord = null;
                                 } else {
                                    loginRecord = new LoginRecord(text2, reason, "#", new Date());
                                 }
                              } else {
                                 Profile profile = UsernameValidator.KOTLIN_DATA.computeProfile(reason);
                                 if (profile == null) {
                                    loginRecord = null;
                                 } else {
                                    UUID uuid2 = profile.getUUID();
                                    loginRecord = new LoginRecord(profile.getText2(), uuid2 != null ? uuid2.toString() : null, null, new Date());
                                 }
                              }
                           } else {
                              if (TargetParser.isUuid(reason)) {
                                 new LoginRecord(TextUtil.access$length(reason, 16), reason, "#", new Date());
                              }

                              loginRecord = new LoginRecord(TextUtil.access$length(reason, 16), TextUtil.offlineUuid(reason), "#", new Date());
                           }

                           punishmentRequest.setTargetRecord(loginRecord);
                        }

                        LoginRecord targetRecord2 = punishmentRequest.getTargetRecord();
                        kotlinSerializable.obj = targetRecord2 != null ? targetRecord2.getName() : null;
                        targetRecord2 = punishmentRequest.getTargetRecord();
                        kotlinSerializable2.obj = targetRecord2 != null ? targetRecord2.getUuid() : null;
                     }

                     this.handlePunishmentRequest(punishmentRequest);
                     Template template3 = punishmentRequest.getTemplate();
                     if (Intrinsics.compare(template3.getId() & 255, -1 & 0xFF) < 0 && kotlinSerializable2.obj != null) {
                        TemplateInfo defaultInfo = punishmentRequest.getTemplate().getDefaultInfo();
                        punishmentRequest.setStackIndex(session.countTemplateUses(punishmentRequest.getTemplate(), (String)kotlinSerializable2.obj, targetSender != null ? targetSender.getIpAddress() : null));
                        punishmentRequest.setTemplateInfo(punishmentRequest.getTemplate().getInfoForCount(punishmentRequest.getStackIndex()));
                        CommandTask commandTask8 = this;
                        ((ConfigModule)commandTask8.getPlatform().getModule(ConfigModule.class))
                           .logDebug(
                              "Applying "
                                 + punishmentRequest.getTemplate().getType()
                                 + " template \""
                                 + punishmentRequest.getTemplate().getName()
                                 + "\" at level "
                                 + (punishmentRequest.getStackIndex() + 1)
                                 + '/'
                                 + punishmentRequest.getTemplate().getEscalation().size()
                           );
                        if (StringsKt.isBlank(punishmentRequest.getReasonBuilder())) {
                           StringBuilder reasonBuilder = punishmentRequest.getReasonBuilder();
                           String text3 = punishmentRequest.getTemplateInfo().getReason();
                           if (text3 == null) {
                              text3 = defaultInfo.getReason();
                           }

                           reasonBuilder.append(text3);
                        }

                        AltAccountPunisher.handleCommandTask(this, punishmentRequest.getTemplate(), punishmentRequest.getTemplateInfo());
                        punishmentRequest.applyTemplate(session);
                     }

                     StringBuilder reasonBuilder2 = punishmentRequest.getReasonBuilder();
                     CharSequence broadcastMessage = punishmentRequest.getBroadcastMessage(session);
                     StringsKt.computeStringBuilder(reasonBuilder2).append(broadcastMessage);
                     punishmentRequest.broadcastPunishment(session);
                     if (targetSender == null || !targetSender.isOnline()) {
                        if (kotlinSerializable2.obj != null) {
                           Object obj = kotlinSerializable.obj;
                           Intrinsics.checkNotNull(kotlinSerializable.obj);
                           if (punishmentRequest.checkDatabaseSession(session, (String)obj, (String)kotlinSerializable2.obj)) {
                              CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
                              boolean flag3 = punishmentRequest.checkDatabaseSession(session, (String)kotlinSerializable2.obj);
                              boolean flag4 = !flag3;
                              if (flag4) {
                                 commandOutput2.returnResult(session.replacePlaceholders(Message.EXEMPT_3, (String)kotlinSerializable.obj, (String)kotlinSerializable.obj, (String)kotlinSerializable2.obj, null, null));
                                 throw new CommandAbortException();
                              }
                           }
                        }

                        CommandOutput commandOutput3;
                        boolean flag5;
                        label561: {
                           commandOutput3 = CommandTask.COMMAND_OUTPUT;
                           if (session.getDatabaseModule().isSyncEnabled() && !punishmentRequest.isSkip()) {
                              LiteSender liteSender2 = liteSender;
                              String text4 = "litebans.kick.global";
                              if (liteSender2.hasPermission(text4)) {
                                 flag5 = false;
                                 break label561;
                              }
                           }

                           flag5 = true;
                        }

                        boolean flag6 = flag5;
                        CharSequence message2 = Message.NO_MATCH;
                        CommandOutput commandOutput4 = commandOutput3;
                        if (flag6) {
                           commandOutput4.returnResult(message2);
                           throw new CommandAbortException();
                        }

                        SyncModule module2 = (SyncModule)session.getPlatform().getModule(SyncModule.class);
                        LiteHelper13 liteHelper13 = new LiteHelper13(punishmentRequest, module2, liteSender);
                        module2.addListener(liteHelper13);
                        this.handleDatabaseSession(session, punishmentRequest, punishmentRequest.getReasonBuilder());
                        CommandTask commandTask9 = this;
                        long value2 = 400L;
                        commandTask9.getPlatform().runAsyncLater(new LiteRunnableImpl(commandTask9, liteHelper13), value2);
                        punishmentRequest.sendToSender(session, (CharSequence) (Message.KICK_REQUESTED.computeText("player", punishmentRequest.getReason())));
                     } else {
                        if (AbstractCommand.MUTED_COMMAND_FILTER.checkPlatform(session.getPlatform(), targetSender.getServerName())) {
                           session.getDatabaseModule().handleFunction(obj2 -> {
                              PunishmentQueries.saveLoginWithDefaults(((DatabaseSession) obj2), targetSender.getName(), targetSender.getUuidString(), targetSender.getIpAddress(), false, false, 16, null);
                              return Unit.UNIT;
                           });
                        }

                        label657: {
                           punishmentRequest.fireStage(PunishmentStage.POST_TARGET);
                           if (!punishmentRequest.isExempt(session, targetSender)) {
                              String[] texts8 = new String[]{"litebans.exempt", "litebans.exempt.kick"};
                              int value3 = 0;
                              int length2 = texts8.length;

                              boolean flag7;
                              while (true) {
                                 if (value3 >= length2) {
                                    flag7 = false;
                                    break;
                                 }

                                 String text5 = texts8[value3];
                                 if (targetSender.hasPermission(text5)) {
                                    flag7 = true;
                                    break;
                                 }

                                 value3++;
                              }

                              if (!flag7 || !settings.isExemptEnable()) {
                                 break label657;
                              }
                           }

                           CommandOutput commandOutput5 = CommandTask.COMMAND_OUTPUT;
                           boolean flag8 = punishmentRequest.checkDatabaseSession(session, (String)kotlinSerializable2.obj);
                           boolean flag9 = !flag8;
                           if (flag9) {
                              commandOutput5.returnResult(session.replacePlaceholders(Message.EXEMPT_3, targetSender));
                              throw new CommandAbortException();
                           }
                        }

                        Punishment punishment;
                        PunishmentType punishmentType2 = PunishmentType.KICK;
                        String uuidString = targetSender.getUuidString();
                        String ipAddress = targetSender.getIpAddress();
                        CharSequence reasonBuilder3 = punishmentRequest.getReasonBuilder();
                        String executorUuid = punishmentRequest.getExecutorUuid();
                        String text6 = punishmentRequest.getExecutorName();
                        String localScope = ServerScopes.SERVER_SCOPE.getLocalScope();
                        String text7 = session.getDatabaseModule().computeText(liteSender);
                        if (text7 == null) {
                           text7 = session.getDatabaseModule().getServerName();
                        }

                        long currentTime2 = DatabaseModule.getCurrentTimeWithDefaults(session.getDatabaseModule(), false, 1, null);
                        Template template4 = punishmentRequest.getTemplate();
                        punishment = new Punishment(
                           punishmentType2,
                           uuidString,
                           ipAddress,
                           reasonBuilder3,
                           executorUuid,
                           text6,
                           localScope,
                           text7,
                           currentTime2,
                           0L,
                           template4 != null ? template4.getId() : -1,
                           punishmentRequest.isSilent(),
                           false,
                           false,
                           0L,
                           24576,
                           null
                        );
                        punishmentRequest.setPunishment(punishment);
                        punishmentRequest.fireStage(PunishmentStage.ENTRY_CREATED);
                        Punishment punishment2 = punishmentRequest.getPunishment();
                        Intrinsics.checkNotNull(punishment2);
                        punishmentRequest.handleDatabaseSession(session, liteSender, punishment2, new LoginRecord(targetSender.getName(), targetSender.getUuidString(), targetSender.getIpAddress(), new Date()));
                        this.handlePunishmentRequest(punishmentRequest, session, targetSender);
                     }
                  } finally {
                     punishmentRequest.fireStage(PunishmentStage.FINALIZED);
                  }
               }

            } catch (Throwable e3) {
               error = e3;
               throw SneakyThrow.<RuntimeException>rethrow(e3);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         } catch (Throwable e4) {
            if (!(e4 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e4);
            }
            SQLException sqlException = (SQLException) e4;
            if (!module.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handleDatabaseSession(DatabaseSession databaseSession, PunishmentRequest punishmentRequest, CharSequence message) {
      SyncMessageType syncMessageType = SyncMessageType.KICK_REQUEST;
      if (databaseSession.getDatabaseModule().isSyncEnabled()) {
         CharSequence[] messages = new CharSequence[8];
         messages[0] = punishmentRequest.getExecutorUuid();
         messages[1] = punishmentRequest.getExecutorName();
         messages[2] = punishmentRequest.getReason();
         messages[3] = message;
         boolean flag = punishmentRequest.isSilent();
         messages[4] = flag ? "1" : "0";
         boolean flag2 = punishmentRequest.isExtraSilent();
         messages[5] = flag2 ? "1" : "0";
         Template template = punishmentRequest.getTemplate();
         messages[6] = UByte.toStringImpl(template != null ? template.getId() : -1);
         messages[7] = String.valueOf(punishmentRequest.getStackIndex());
         CharSequence joinToString = ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null);
         AltAccountPunisher.sendSyncWithDefaults(databaseSession, syncMessageType, joinToString, null, null, 12, null);
      }
   }

   public final void handlePunishmentRequest(@NotNull PunishmentRequest punishmentRequest, @NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender) {
      Punishment punishment = punishmentRequest.getPunishment();
      Intrinsics.checkNotNull(punishment);
      String string = punishment.getReason().toString();
      String lowerCase = string.toLowerCase(Locale.ENGLISH);
      CommandTask commandTask = this;
      var ignoredKickMessages = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getSettings().getIgnoredKickMessages();
      boolean flag;
      if (ignoredKickMessages instanceof Collection && ((Collection)ignoredKickMessages).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = ignoredKickMessages.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj = iterator2.next();
            String string2 = (String)obj;
            if (StringsKt.containsWithDefaults((CharSequence)lowerCase, (CharSequence)string2, false, 2, null)) {
               flag = true;
               break;
            }
         }
      }

      boolean flag2 = flag;
      if (!flag2) {
         AltAccountPunisher.savePunishmentWithDefaults(databaseSession, punishment, false, false, 6, null);
      } else {
         CommandTask commandTask2 = this;
         ConfigModule module = (ConfigModule)commandTask2.getPlatform().getModule(ConfigModule.class);
         byte b = 1;
         if (module.isDebugAtLeast(b)) {
            module.logDebug("Kick not added to database due to reason: \"" + punishment.getReason() + '"');
         }
      }

      punishmentRequest.fireStage(PunishmentStage.ENTRY_EMITTED);
      punishmentRequest.setTargetRecord(new LoginRecord(liteSender.getName(), liteSender.getUuidString(), liteSender.getIpAddress(), new Date()));
      DatabaseSession databaseSession2 = databaseSession;
      CharSequence message = Placeholders.getBanScreenMessageWithDefaults(databaseSession2, punishment, punishmentRequest.getStackIndex(), 0, 4, null);
      if (message != null) {
         message = databaseSession2.replacePlaceholders(PunishmentQueries.replacePlaceholdersWithDefaults(databaseSession2, message, punishment, false, 2, null), liteSender);
      }

      CharSequence message2 = message;
      if (message == null) {
         message2 = Placeholders.getKickMessage(databaseSession2, punishment, liteSender);
      }

      CharSequence message3 = message2;
      this.handleDatabaseSession(databaseSession2, punishment, punishmentRequest);
      if (liteSender.isOnline() && !punishmentRequest.isSkip()) {
         CommandTask commandTask3 = this;
         ((BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class)).sendMessageSync(liteSender, message3);
         punishmentRequest.sendToSender(databaseSession2, Message.RESPONSE);
      }

      PunishmentRequest.handlePunishmentRequest(punishmentRequest, databaseSession2, liteSender, null, false, 6, null);
      punishmentRequest.fireStage(PunishmentStage.ENTRY_APPLIED);
   }

}
