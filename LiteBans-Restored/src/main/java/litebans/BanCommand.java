package litebans;

import java.io.Closeable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public final class BanCommand extends PunishCommandCommon {
   public static final BanCommandCompanion BAN_COMMAND_COMPANION = new BanCommandCompanion(null);

   public BanCommand(@NotNull Platform platform) {
      super("ban", platform);
   }

   @Override
   public boolean obtainFlag() {
      return false;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      try {
         String text = null;
         CommandTask commandTask = this;
         ConfigModule module = (ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class);
         Map senderCache = this.getPlatform().getSenderCache();
         int value = senderCache.size();

         if (module.isVerbose()) {
            module.logDebug("[c] " + value);
         }

         CommandTask commandTask3 = this;
         DatabaseModule module2 = (DatabaseModule)commandTask3.getPlatform().getModule(DatabaseModule.class);

         try {
            DatabaseSession session = module2.openSession();
            Throwable error = null;

            try {
               if (session.isClosed()) {
                  CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_NO_SQL_CONNECTION);
                  throw new CommandAbortException();
               }

               try {
                  Settings settings = module.getSettings();
                  LiteSender[] onlineSenders = session.getPlatform().getOnlineSenders();
                  String defaultServerScope = settings.getDefaultServerScope();
                  PunishmentType punishmentType = PunishmentType.BAN;
                  boolean isPunishIp2 = settings.isPunishIp();
                  boolean isSilent2 = settings.isSilent();
                  String text4 = session.getDatabaseModule().computeText(liteSender);
                  long currentTime = DatabaseModule.getCurrentTimeWithDefaults(session.getDatabaseModule(), false, 1, null);
                  String staffName = session.getStaffName(liteSender);
                  PunishmentRequest punishmentRequest = new PunishmentRequest(
                     punishmentType,
                     isPunishIp2,
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
                     text4,
                     null,
                     null,
                     texts,
                     null,
                     null,
                     671072248,
                     27,
                     null
                  );

                  try {
                     PunishmentRequest.handlePunishmentRequest(punishmentRequest, session, null, false, 3, null);
                     CommandOutput commandOutput2 = CommandTask.COMMAND_OUTPUT;
                     boolean flag2 = punishmentRequest.getType().obtainPunishmentType() == PunishmentType.MUTE && !settings.isMutesEnabled();
                     CharSequence message7 = Message.ERROR_NOT_ENABLED;
                     if (flag2) {
                        commandOutput2.returnResult(message7);
                        throw new CommandAbortException();
                     }

                     CommandOutput commandOutput3 = CommandTask.COMMAND_OUTPUT;
                     boolean flag3 = punishmentRequest.isCustomSender() && liteSender.isPlayer() && settings.isSecuritySenderCustom();
                     CharSequence message8 = Message.ERROR_CONSOLE_ONLY;
                     if (flag3) {
                        commandOutput3.returnResult(message8);
                        throw new CommandAbortException();
                     }

                     CommandOutput commandOutput4 = CommandTask.COMMAND_OUTPUT;
                     boolean flag4 = punishmentRequest.isModify() && liteSender.isPlayer() && settings.isSecurityModify();
                     CharSequence message9 = Message.ERROR_CONSOLE_ONLY;
                     if (flag4) {
                        commandOutput4.returnResult(message9);
                        throw new CommandAbortException();
                     }

                     byte b3 = 0;

                     while (b3 < punishmentRequest.getExtraArgs().size()) {
                        Object obj = punishmentRequest.getExtraArgs().get(b3);
                        String string2 = (String)obj;
                        if (string2.length() == 0) {
                           b3 += 1;
                        } else if (!Intrinsics.areEqual(string2, "duration:permanent") || !punishmentRequest.isModify() && (punishmentRequest.isUndo() || punishmentRequest.getType().obtainPunishmentType() == PunishmentType.WARN)) {
                           String text5 = CacheModule.CACHE_MODULE_COMPANION.computeText(string2, "\\-", "-");
                           boolean flag5 = b3 == punishmentRequest.getExtraArgs().size() - 1;
                           String text6 = KotlinUtil9.computeText(DurationUnit.DURATIONS.obtainRegex2(), text5, "");
                           Template template = this.computeTemplate(punishmentRequest, text5);
                           if (template != null) {
                              this.handlePunishmentRequest(punishmentRequest, template);
                           } else if ((punishmentRequest.isModify() || !punishmentRequest.isUndo())
                              && punishmentRequest.getType().obtainPunishmentType() != PunishmentType.WARN
                              && punishmentRequest.getUntil() == -1L
                              && Character.isDigit(text5.charAt(0))
                              && !StringsKt.containsWithDefaults(text5, ':', false, 2, null)
                              && !StringsKt.containsWithDefaults(text5, '%', false, 2, null)
                              && !StringsKt.containsWithDefaults(text5, '*', false, 2, null)
                              && TargetParser.TARGET_PARSER.isNumeric(text6)
                              && !TargetParser.isUuid(text5)) {
                              if (punishmentRequest.getReason().length() == 0 && text5.length() > 1) {
                                 String stripColor2 = LiteColor.stripColor(text5);
                                 Intrinsics.checkNotNull(onlineSenders);
                                 Intrinsics.checkNotNull(stripColor2);
                                 boolean flag6 = true;
                                 int value4 = 0;
                                 int length2 = onlineSenders.length;

                                 LiteSender liteSender3;
                                 while (true) {
                                    if (value4 >= length2) {
                                       liteSender3 = null;
                                       break;
                                    }

                                    LiteSender liteSender4 = onlineSenders[value4];
                                    if (StringsKt.checkText(liteSender4.getName(), stripColor2, flag6)) {
                                       liteSender3 = liteSender4;
                                       break;
                                    }

                                    value4++;
                                 }

                                 LiteSender liteSender5 = liteSender3;
                                 if (liteSender5 != null) {
                                    String name2 = liteSender5.getName();
                                    punishmentRequest.setReason(name2);
                                    if (AbstractCommand.MUTED_COMMAND_FILTER.checkPlatform(session.getPlatform(), punishmentRequest.getServerOrigin())) {
                                       session.getDatabaseModule().handleFunction(obj2 -> {
                                          PunishmentQueries.saveLoginWithDefaults(((DatabaseSession) obj2), liteSender5.getName(), liteSender5.getUuidString(), liteSender5.getIpAddress(), false, false, 16, null);
                                          return Unit.UNIT;
                                       });
                                    }

                                    b3 += 1;
                                    continue;
                                 }

                                 punishmentRequest.setTargetRecord(session.findLoginRecord(text5));
                                 if (punishmentRequest.getTargetRecord() != null) {
                                    String text7;
                                    label4206: {
                                       LoginRecord targetRecord = punishmentRequest.getTargetRecord();
                                       if (targetRecord != null) {
                                          String name3 = targetRecord.getName();
                                          text7 = name3;
                                          if (name3 != null) {
                                             break label4206;
                                          }
                                       }

                                       text7 = "";
                                    }

                                    punishmentRequest.setReason(text7);
                                    b3 += 1;
                                    continue;
                                 }
                              }

                              Double double2 = StringsKt.computeDouble(text6);
                              if (double2 == null) {
                                 if (punishmentRequest.getReason().length() == 0) {
                                    punishmentRequest.setReason(text5);
                                 }

                                 b3 += 1;
                                 continue;
                              }

                              String text8 = KotlinUtil9.computeText(DurationUnit.DURATIONS.obtainRegex(), text5, "");
                              DurationUnit unit = DurationUnit.DURATIONS.findUnit(text8);
                              if (unit == null) {
                                 if (flag5) {
                                    if (punishmentRequest.getReason().length() == 0) {
                                       punishmentRequest.setReason(text5);
                                    } else {
                                       this.handlePunishmentRequest(punishmentRequest, text5);
                                    }

                                    b3 += 1;
                                    continue;
                                 }

                                 ArrayList list = punishmentRequest.getExtraArgs();
                                 b3 += 1;
                                 Object obj3 = list.get(b3);
                                 String string3 = (String)obj3;
                                 unit = DurationUnit.DURATIONS.findUnit(string3);
                                 if (unit == null) {
                                    if (punishmentRequest.getReason().length() == 0) {
                                       punishmentRequest.setReason(text5);
                                       String text9 = KotlinUtil9.computeText(DurationUnit.DURATIONS.obtainRegex(), string3, "");
                                       DurationUnit unit2 = DurationUnit.DURATIONS.findUnit(text9);
                                       if (unit2 == null) {
                                          this.handlePunishmentRequest(punishmentRequest, string3);
                                       } else {
                                          Double double3 = StringsKt.computeDouble(KotlinUtil9.computeText(DurationUnit.DURATIONS.obtainRegex2(), string3, ""));
                                          if (double3 == null) {
                                             this.handlePunishmentRequest(punishmentRequest, string3);
                                          } else {
                                             punishmentRequest.setDuration(unit2.computeLongValue(double3));
                                             punishmentRequest.setUntil(punishmentRequest.getCreationTime() + punishmentRequest.getDuration());
                                             punishmentRequest.setDurationText(NumberFormats.computeText2(double3) + ' ' + unit2.computeMessage(double3));
                                          }
                                       }
                                    } else {
                                       punishmentRequest.getReasonBuilder().append(text5).append(' ').append(string3).append(' ');
                                    }

                                    b3 += 1;
                                    continue;
                                 }
                              }

                              punishmentRequest.setDuration(unit.computeLongValue(double2));
                              punishmentRequest.setUntil(punishmentRequest.getCreationTime() + punishmentRequest.getDuration());
                              punishmentRequest.setDurationText(NumberFormats.computeText2(double2) + ' ' + unit.computeMessage(double2));
                           } else if (punishmentRequest.getReason().length() == 0) {
                              punishmentRequest.setReason(text5);
                           } else {
                              this.handlePunishmentRequest(punishmentRequest, text5);
                           }

                           b3 += 1;
                        } else {
                           punishmentRequest.setUntil(-2L);
                           punishmentRequest.setTemporary(false);
                           b3 += 1;
                        }
                     }

                     if (punishmentRequest.getUntil() > 0L) {
                        punishmentRequest.setTemporary(true);
                     }

                     PunishmentRequest punishmentRequest2;
                     CommandTask commandTask5;
                     boolean flag7;
                     boolean flag8;
                     label4187: {
                        punishmentRequest2 = punishmentRequest;
                        commandTask5 = this;
                        flag7 = PunishmentRequest.isWildcardIpWithDefaults(punishmentRequest2, null, 1, null);
                        if (punishmentRequest2.isIpBan()) {
                           CommandTask commandTask6 = commandTask5;
                           if (!((ConfigModule)commandTask6.getPlatform().getModule(ConfigModule.class)).getSettings().isPunishIp()) {
                              flag8 = true;
                              break label4187;
                           }
                        }

                        flag8 = false;
                     }

                     boolean flag9 = flag8;
                     switch (PunishmentTypeSwitchMap4.value[punishmentRequest2.getType().ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                           String text10 = punishmentRequest2.isModify() ? "litebans.modify" : "litebans." + punishmentRequest2.getType();
                           String[] texts8 = new String[]{text10, text10 + ".own"};
                        AltAccountPunisher.handleCommandTask(commandTask5, texts8);
                        break;
                        case 4:
                        case 5:
                           String[] texts9;
                           if (punishmentRequest2.isTemporary()) {
                              String[] texts10 = new String[]{"litebans.temp" + punishmentRequest2.getType(), "litebans." + punishmentRequest2.getType(), "litebans.ip" + punishmentRequest2.getType()};
                              texts9 = texts10;
                           } else {
                              String[] texts11 = new String[]{"litebans." + punishmentRequest2.getType(), "litebans.ip" + punishmentRequest2.getType()};
                              texts9 = texts11;
                           }

                           String[] texts12 = texts9;
                           if (flag9) {
                              CommandTask commandTask7 = commandTask5;
                              String text11 = "litebans.ip" + punishmentRequest2.getType();
                              LiteSender sender = commandTask7.getSender();
                              CommandOutput commandOutput5 = CommandTask.COMMAND_OUTPUT;
                              boolean flag10 = !sender.hasPermission(text11);
                              CharSequence message10 = Message.PERMISSION_ERROR;
                              if (flag10) {
                                 commandOutput5.returnResult(message10);
                                 throw new CommandAbortException();
                              }
                           }

                           AltAccountPunisher.handleCommandTask(commandTask5, Arrays.copyOf(texts12, texts12.length));
                           break;
                        case 6:
                           CommandTask commandTask8 = commandTask5;
                           String text12 = "litebans." + punishmentRequest2.getType();
                           LiteSender sender2 = commandTask8.getSender();
                           CommandOutput commandOutput6 = CommandTask.COMMAND_OUTPUT;
                           boolean flag11 = !sender2.hasPermission(text12);
                           CharSequence message11 = Message.PERMISSION_ERROR;
                           if (flag11) {
                              commandOutput6.returnResult(message11);
                              throw new CommandAbortException();
                           }
                           break;
                        default:
                           StandardKt.notImplemented();
                           throw new CommandAbortException();
                     }

                     if (!punishmentRequest2.isUndo() && flag7) {
                        CommandTask commandTask9 = commandTask5;
                        String text13 = "litebans.ip" + punishmentRequest2.getType() + ".wildcard";
                        LiteSender sender3 = commandTask9.getSender();
                        CommandOutput commandOutput7 = CommandTask.COMMAND_OUTPUT;
                        boolean flag12 = !sender3.hasPermission(text13);
                        CharSequence message12 = Message.PERMISSION_ERROR;
                        if (flag12) {
                           commandOutput7.returnResult(message12);
                           throw new CommandAbortException();
                        }
                     }

                     punishmentRequest.fireStage(PunishmentStage.PRE_TARGET);
                     if (punishmentRequest.getReason().length() == 0) {
                        PunishmentType type = punishmentRequest.getType();
                        boolean flag13 = punishmentRequest.isUndo();
                        Message message13 = Message.BAN_USAGE;
                        CharSequence message14;
                        if (flag13) {
                           message14 = Message.computeMessage2(type + "_USAGE");
                        } else {
                           Message message15;
                           switch (PunishmentTypeSwitchMap22.value[type.ordinal()]) {
                              case 1:
                                 message15 = message13 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                                 break;
                              case 2:
                                 message15 = message13 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                                 break;
                              case 3:
                                 message15 = Message.KICK_USAGE;
                                 break;
                              default:
                                 message15 = message13;
                           }

                           message14 = message15;
                        }

                        CharSequence message16 = message14;
                        CommandOutput commandOutput8 = CommandTask.COMMAND_OUTPUT;
                        boolean flag14 = type == PunishmentType.KICK;
                        if (flag14) {
                           commandOutput8.returnResult(message16);
                           throw new CommandAbortException();
                        }

                        Message message17 = Message.EXAMPLE;
                        CharSequence message18;
                        if (flag13) {
                           message18 = Message.computeMessage2(type + "_USAGE");
                        } else {
                           Message message19;
                           switch (PunishmentTypeSwitchMap22.value[type.ordinal()]) {
                              case 1:
                                 message19 = message17 == Message.EXAMPLE ? Message.WARN_EXAMPLE : Message.WARN_USAGE;
                                 break;
                              case 2:
                                 message19 = message17 == Message.EXAMPLE ? Message.EXAMPLE_2 : Message.MUTE_USAGE;
                                 break;
                              case 3:
                                 message19 = Message.KICK_USAGE;
                                 break;
                              default:
                                 message19 = message17;
                           }

                           message18 = message19;
                        }

                        CharSequence message20 = message18;
                        CommandTask.COMMAND_OUTPUT.returnResult(TextUtil.computeMessage(message16, !flag13, message20));
                        throw new CommandAbortException();
                     }

                     LiteSender liteSender6;
                     label4176: {
                        this.handlePunishmentRequest(punishmentRequest);
                        punishmentRequest.setReason(punishmentRequest.computeText(this));
                        liteSender6 = AltAccountPunisher.findTargetSender(this, punishmentRequest.getReason());
                        if (liteSender6 != null) {
                           String serverName = liteSender6.getServerName();
                           if (serverName != null) {
                              text = TextUtil.access$length(serverName, 32);
                              break label4176;
                           }
                        }

                        text = null;
                     }

                     String text14;
                     text14 = text;
                     label4162:
                     if (!ServerScopes.SERVER_SCOPE.isGlobal(defaultServerScope) && !StringsKt.checkText(punishmentRequest.getServerScope(), defaultServerScope, true)) {
                        if (ServerScopes.SERVER_SCOPE.isLocal(defaultServerScope)) {
                           String serverScope = punishmentRequest.getServerScope();
                           String serverOrigin = punishmentRequest.getServerOrigin();
                           String text15 = serverOrigin;
                           if (serverOrigin == null) {
                              text15 = text14;
                              if (text14 == null) {
                                 text15 = session.getDatabaseModule().getServerName();
                              }
                           }

                           if (StringsKt.checkText(serverScope, text15, true)) {
                              break label4162;
                           }
                        }

                        LiteSender liteSender7 = liteSender;
                        String[] texts13 = new String[]{"litebans.server." + punishmentRequest.getServerScope(), "litebans.server." + punishmentRequest.getServerScope() + '.' + punishmentRequest.getType().obtainPunishmentType()};
                        String[] copyOf2 = Arrays.copyOf(texts13, texts13.length);
                        byte b4 = 0;
                        int length3 = copyOf2.length;

                        while (true) {
                           if (b4 >= length3) {
                              flag8 = false;
                              break;
                           }

                           String text16 = copyOf2[b4];
                           if (liteSender7.hasPermission(text16)) {
                              flag8 = true;
                              break;
                           }

                           b4 += 1;
                        }

                        if (!flag8) {
                           punishmentRequest.setServerScope(ServerScopes.SERVER_SCOPE.resolveScope(defaultServerScope, session.getDatabaseModule().getServerName(), liteSender));
                        }
                     }

                     boolean flag15 = ServerScopes.SERVER_SCOPE.scopesMatch(session.getDatabaseModule().getServerName(), punishmentRequest.getServerScope()) || text14 != null && ServerScopes.SERVER_SCOPE.scopesMatch(text14, punishmentRequest.getServerScope());
                     if (module.isVerbose()) {
                        String serverScope2 = punishmentRequest.getServerScope();
                        module.logDebug(serverScope2);
                     }

                     KotlinHelper13 kotlinHelper;
                     if (liteSender6 != null) {
                        String reason = punishmentRequest.getReason();
                        String name4 = liteSender6.getName();
                        String displayName = liteSender6.getDisplayName();
                        String name5 = liteSender6.getName();
                        kotlinHelper = new KotlinHelper13(reason, name4, module.getSettings().isUseDisplayNames() ? displayName : name5, liteSender6.getUuidString(), liteSender6.getIpAddress(), liteSender6, null, 64, null);
                        punishmentRequest.setTargetRecord(new LoginRecord(kotlinHelper.getTarget(), kotlinHelper.getText8(), kotlinHelper.getText9(), new Date(punishmentRequest.getCreationTime())));
                     } else {
                        label4141: {
                           punishmentRequest.setTargetRecord(session.findLoginRecord(punishmentRequest.getReason()));
                           LoginRecord targetRecord2 = punishmentRequest.getTargetRecord();
                           if (targetRecord2 != null) {
                              String uuid = targetRecord2.getUuid();
                              if (uuid != null) {
                                 flag8 = !StringsKt.startsWithWithDefaults(uuid, '#', false, 2, null);
                                 break label4141;
                              }
                           }

                           flag8 = false;
                        }

                        if (!flag8) {
                           punishmentRequest.setTargetRecord((LoginRecord)null);
                        }

                        if (punishmentRequest.getTargetRecord() == null) {
                           String reason2 = punishmentRequest.getReason();
                           if (TargetParser.TARGET_PARSER.checkText(reason2)) {
                              punishmentRequest.setIpBan(true);
                              CommandOutput commandOutput9 = CommandTask.COMMAND_OUTPUT;
                              LiteSender liteSender8 = liteSender;
                              String text17 = "litebans.ip" + punishmentRequest.getType().obtainPunishmentType();
                              boolean flag16 = !liteSender8.hasPermission(text17);
                              CharSequence message21 = Message.PERMISSION_ERROR;
                              if (flag16) {
                                 commandOutput9.returnResult(message21);
                                 throw new CommandAbortException();
                              }

                              punishmentRequest.setTargetRecord(new LoginRecord("#offline#", "#offline#", punishmentRequest.getReason(), new Date()));
                           } else {
                              if (punishmentRequest.isUndo()) {
                                 String text18 = AltAccountPunisher.computeText(this, punishmentRequest.getReason());
                                 if (!Intrinsics.areEqual(text18, punishmentRequest.getReason())) {
                                    Punishment punishment21 = PunishmentQueries.findPunishmentWithDefaults(session, text18, punishmentRequest.getType(), null, !punishmentRequest.isDelete(), 4, null);
                                    if (punishmentRequest.isDelete() || (punishment21 != null ? session.isAffecting(punishment21) : false)) {
                                       String text19;
                                       label4124: {
                                          punishmentRequest.setTargetRecord(session.findLoginRecord(punishment21 != null ? punishment21.getUuid() : null));
                                          if (punishment21 != null) {
                                             String serverScope3 = punishment21.getServerScope();
                                             text19 = serverScope3;
                                             if (serverScope3 != null) {
                                                break label4124;
                                             }
                                          }

                                          text19 = punishmentRequest.getServerScope();
                                       }

                                       punishmentRequest.setServerScope(text19);
                                       punishmentRequest.setExistingPunishment(punishment21);
                                    }
                                 }

                                 if (punishmentRequest.getTargetRecord() == null && TargetParser.TARGET_PARSER.isIpv4(punishmentRequest.getReason())) {
                                    Punishment punishment22 = PunishmentQueries.findPunishmentWithDefaults(session, punishmentRequest.getReason(), punishmentRequest.getType(), null, !punishmentRequest.isDelete(), 4, null);
                                    if (punishmentRequest.isDelete() || (punishment22 != null ? session.isAffecting(punishment22) : false)) {
                                       String text20;
                                       label4105: {
                                          punishmentRequest.setTargetRecord(session.findLoginRecord(punishment22 != null ? punishment22.getUuid() : null));
                                          if (punishment22 != null) {
                                             String serverScope4 = punishment22.getServerScope();
                                             text20 = serverScope4;
                                             if (serverScope4 != null) {
                                                break label4105;
                                             }
                                          }

                                          text20 = punishmentRequest.getServerScope();
                                       }

                                       punishmentRequest.setServerScope(text20);
                                       punishmentRequest.setExistingPunishment(punishment22);
                                    }
                                 }
                              }

                              if (punishmentRequest.getTargetRecord() == null) {
                                 CommandTask commandTask10 = this;
                                 String reason3 = punishmentRequest.getReason();
                                 boolean isOnlineMode2 = ((ConfigModule)commandTask10.getPlatform().getModule(ConfigModule.class)).getSettings().isOnlineMode();
                                 LoginRecord loginRecord;
                                 if (isOnlineMode2) {
                                    if (TargetParser.isUuid(reason3)) {
                                       String text21 = UsernameValidator.KOTLIN_DATA.computeText(reason3);
                                       if (text21 == null) {
                                          loginRecord = null;
                                       } else {
                                          loginRecord = new LoginRecord(text21, reason3, "#", new Date());
                                       }
                                    } else {
                                       Profile profile = UsernameValidator.KOTLIN_DATA.computeProfile(reason3);
                                       if (profile == null) {
                                          loginRecord = null;
                                       } else {
                                          UUID uuid2 = profile.getUUID();
                                          loginRecord = new LoginRecord(profile.getName(), uuid2 != null ? uuid2.toString() : null, null, new Date());
                                       }
                                    }
                                 } else {
                                    if (TargetParser.isUuid(reason3)) {
                                       new LoginRecord(TextUtil.access$length(reason3, 16), reason3, "#", new Date());
                                    }

                                    loginRecord = new LoginRecord(TextUtil.access$length(reason3, 16), TextUtil.offlineUuid(reason3), "#", new Date());
                                 }

                                 LoginRecord loginRecord2 = loginRecord;
                                 if (loginRecord2 == null) {
                                    punishmentRequest.computeVoid(session, Message.ERROR_NO_UUID_FOUND.computeText("player", punishmentRequest.getReason()));
                                    throw new CommandAbortException();
                                 }

                                 punishmentRequest.setTargetRecord(loginRecord2);
                              }
                           }

                           LoginRecord targetRecord3 = punishmentRequest.getTargetRecord();
                           if ((targetRecord3 != null ? targetRecord3.getUuid() : null) != null) {
                              LoginRecord targetRecord4 = punishmentRequest.getTargetRecord();
                              Intrinsics.checkNotNull(targetRecord4);
                              String uuid3 = targetRecord4.getUuid();
                              Intrinsics.checkNotNull(uuid3);
                              if (!StringsKt.startsWithWithDefaults(uuid3, '#', false, 2, null)) {
                                 TargetParser targetParser = TargetParser.TARGET_PARSER;
                                 LoginRecord targetRecord5 = punishmentRequest.getTargetRecord();
                                 if (!targetParser.isIpAddress(targetRecord5 != null ? targetRecord5.getName() : null) && AbstractCommand.MUTED_COMMAND_FILTER.checkPlatform(session.getPlatform(), punishmentRequest.getServerOrigin())) {
                                    session.getDatabaseModule().handleFunction(obj4 -> {
                                       LoginRecord targetRecord13 = punishmentRequest.getTargetRecord();
                                       Intrinsics.checkNotNull(targetRecord13);
                                       ((DatabaseSession) obj4).saveLoginRecord(targetRecord13, false);
                                       return Unit.UNIT;
                                    });
                                 }
                              }
                           }
                        }

                        LoginRecord targetRecord6 = punishmentRequest.getTargetRecord();
                        String text22 = targetRecord6 != null ? targetRecord6.getName() : null;
                        LoginRecord targetRecord7 = punishmentRequest.getTargetRecord();
                        String text23 = targetRecord7 != null ? targetRecord7.getUuid() : null;
                        LoginRecord targetRecord8 = punishmentRequest.getTargetRecord();
                        kotlinHelper = new KotlinHelper13(text22, null, null, text23, targetRecord8 != null ? targetRecord8.getIp() : null, null, punishmentRequest.getReason(), 38, null);
                        if (kotlinHelper.obtainFlag()) {
                           label4087: {
                              LoginRecord targetRecord9 = punishmentRequest.getTargetRecord();
                              if (targetRecord9 != null) {
                                 String name6 = targetRecord9.getName();
                                 if (name6 != null) {
                                    flag8 = StringsKt.startsWithWithDefaults(name6, '#', false, 2, null);
                                    break label4087;
                                 }
                              }

                              flag8 = false;
                           }

                           if (flag8) {
                              KotlinHelper13 kotlinHelper2 = kotlinHelper;
                              if (module.getSettings().isSecurityIpban()) {
                                 module.logDebug("Hiding IP address from broadcast: " + kotlinHelper2.getText9());
                                 kotlinHelper2.setTarget(Message.BAN_HIDDEN_IP.toString());
                              } else {
                                 kotlinHelper2.setTarget(kotlinHelper2.getText9());
                              }

                              kotlinHelper2.setExecutor(kotlinHelper2.getTarget());
                              String text24 = kotlinHelper.getText9();
                              String text25 = text24;
                              if (text24 == null) {
                                 text25 = "";
                              }

                              punishmentRequest.setReason(text25);
                           }
                        }
                     }

                     boolean isWildcardIp = PunishmentRequest.isWildcardIpWithDefaults(punishmentRequest, null, 1, null);
                     CommandOutput commandOutput10 = CommandTask.COMMAND_OUTPUT;
                     boolean flag17 = isWildcardIp && !liteSender.isConsole() && module.getSettings().getSecurityIpbanCommand() >= 1;
                     CharSequence message22 = Message.ERROR_CONSOLE_ONLY;
                     if (flag17) {
                        commandOutput10.returnResult(message22);
                        throw new CommandAbortException();
                     }

                     CommandOutput commandOutput11 = CommandTask.COMMAND_OUTPUT;
                     boolean flag18 = punishmentRequest.isIpBan() && module.getSettings().getSecurityIpbanCommand() >= 2 && !liteSender.isConsole();
                     CharSequence message23 = Message.ERROR_CONSOLE_ONLY;
                     if (flag18) {
                        commandOutput11.returnResult(message23);
                        throw new CommandAbortException();
                     }

                     String serverOrigin2 = punishmentRequest.getServerOrigin();
                     String text26 = serverOrigin2;
                     if (serverOrigin2 == null) {
                        text26 = text14;
                        if (text14 == null) {
                           text26 = session.getDatabaseModule().getServerName();
                        }
                     }

                     punishmentRequest.setServerOrigin(text26);
                     punishmentRequest.setExecutorName(TextUtil.access$length(punishmentRequest.getExecutorName(), 128));
                     punishmentRequest.setExecutorUuid(TextUtil.access$length(punishmentRequest.getExecutorUuid(), 36));
                     boolean flag19 = punishmentRequest.isCustomSender();
                     String text27 = punishmentRequest.getExecutorName();
                     String executorUuid = punishmentRequest.getExecutorUuid();
                     CommandTask commandTask11 = this;
                     ((DatabaseModule)commandTask11.getPlatform().getModule(DatabaseModule.class)).handleFunction(new KotlinFunction1Impl11(flag19, text27, this, executorUuid, liteSender));
                     punishmentRequest.fireStage(PunishmentStage.POST_TARGET);
                     if (punishmentRequest.hasTemplate() && kotlinHelper.getText8() != null) {
                        List escalation = punishmentRequest.getTemplate().getEscalation();
                        if (punishmentRequest.getTemplateGroup() == null) {
                           punishmentRequest.setStackIndex(session.countTemplateUses(punishmentRequest.getTemplate(), kotlinHelper.getText8(), kotlinHelper.getText9()));
                           punishmentRequest.setTemplateInfo(punishmentRequest.getTemplate().getInfoForCount(punishmentRequest.getStackIndex()));
                        } else {
                           TemplateGroup templateGroup = punishmentRequest.getTemplateGroup();
                           Intrinsics.checkNotNull(templateGroup);
                           escalation = templateGroup.getLevels();
                           Pair pair = session.computePair(kotlinHelper.getText8(), kotlinHelper.getText9(), templateGroup, punishmentRequest.getTemplate());
                           punishmentRequest.setStackIndex((int)Math.floor(((Number)pair.getSecond()).doubleValue()));
                           punishmentRequest.setTemplateInfo(templateGroup.computeTemplateInfo(punishmentRequest.getStackIndex()));
                           double doubleValue2 = ((Number)pair.getFirst()).doubleValue();
                           if (doubleValue2 <= 0.0 || punishmentRequest.getStackIndex() != (int)Math.floor(doubleValue2)) {
                              punishmentRequest.setFlag16(true);
                           }

                           String text28 = NumberFormats.computeText2(doubleValue2);
                           String text29 = NumberFormats.computeText2(((Number)pair.getSecond()).doubleValue());
                           int value5 = Math.min(templateGroup.getLevels().size(), punishmentRequest.getStackIndex() + 1);
                           module.logDebug(
                              "Applying "
                                 + templateGroup.getPunishmentType()
                                 + " template group "
                                 + templateGroup.getName()
                                 + ": weight "
                                 + text28
                                 + " => "
                                 + text29
                                 + " ("
                                 + value5
                                 + '/'
                                 + escalation.size()
                                 + ')'
                           );
                        }

                        int value6 = Math.min(escalation.size(), punishmentRequest.getStackIndex() + 1);
                        module.logDebug("Applying " + punishmentRequest.getTemplate().getType() + " template \"" + punishmentRequest.getTemplate().getName() + "\" at level " + value6 + '/' + escalation.size());
                        TemplateInfo templateInfo = punishmentRequest.getTemplateInfo();
                        Intrinsics.checkNotNullExpressionValue(templateInfo, "");
                        LongHolder longHolder = (LongHolder)templateInfo;
                        TemplateInfo defaultInfo = punishmentRequest.getTemplate().getDefaultInfo();
                        Intrinsics.checkNotNullExpressionValue(defaultInfo, "");
                        LongHolder longHolder2 = (LongHolder)defaultInfo;
                        Long boxed = longHolder.getLong2();
                        Long boxed2 = boxed;
                        if (boxed == null) {
                           boxed2 = longHolder2.getLong2();
                        }

                        Long boxed3 = boxed2;
                        if (boxed3 != null) {
                           if (boxed3 > 0L) {
                              punishmentRequest.setUntil(punishmentRequest.getCreationTime() + boxed3);
                              punishmentRequest.setTemporary(true);
                              long value7 = boxed3;
                              punishmentRequest.setDurationText(Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value7, false, 2, null));
                           } else {
                              punishmentRequest.setUntil(-1L);
                              punishmentRequest.setTemporary(false);
                              punishmentRequest.setDurationText(Message.FOREVER);
                           }
                        }

                        if (punishmentRequest.getTemplate().isFlag2()) {
                           punishmentRequest.setIpBan(true);
                        }

                        AltAccountPunisher.handleCommandTask(this, punishmentRequest.getTemplate(), punishmentRequest.getTemplateInfo());
                        punishmentRequest.applyTemplate(session);
                     }

                     CommandOutput commandOutput12 = CommandTask.COMMAND_OUTPUT;
                     boolean flag20 = punishmentRequest.isTemporary() && !punishmentRequest.isUndo() && punishmentRequest.getUntil() <= 0L;
                     CharSequence message24 = Message.ERROR_NO_DURATION;
                     if (flag20) {
                        commandOutput12.returnResult(message24);
                        throw new CommandAbortException();
                     }

                     CharSequence broadcastMessage = punishmentRequest.getBroadcastMessage(session);
                     String target = kotlinHelper.getTarget();
                     Intrinsics.checkNotNull(target);
                     String text30 = kotlinHelper.getText8();
                     Intrinsics.checkNotNull(text30);
                     punishmentRequest.handleDatabaseSession(session, target, text30);
                     if (punishmentRequest.isUndo()) {
                        BooleanFlag booleanFlag = new BooleanFlag();
                        Punishment punishment = null;
                        Punishment existingPunishment = punishmentRequest.getExistingPunishment();
                        Punishment punishment2 = existingPunishment;
                        if (existingPunishment == null) {
                           punishment2 = session.findPunishment(kotlinHelper.getText8(), kotlinHelper.getText9(), punishmentRequest.getType(), punishmentRequest.getServerScope(), true, true);
                        }

                        punishmentRequest.setPunishment(punishment2);
                        Punishment punishment3 = punishmentRequest.getPunishment();
                        boolean areEqual2 = Intrinsics.areEqual(punishment3 != null ? punishment3.getBannedByUuid() : null, liteSender.getUuidString());
                        String text31 = areEqual2 ? ".own" : "";
                        if (punishmentRequest.getPunishment() != null && !areEqual2 && module.getSettings().isExemptUseGroupWeights()) {
                           CommandOutput commandOutput13 = CommandTask.COMMAND_OUTPUT;
                           Punishment punishment4 = punishmentRequest.getPunishment();
                           boolean flag21 = !punishmentRequest.checkDatabaseSession(session, punishment4 != null ? punishment4.getBannedByUuid() : null);
                           CharSequence message25 = Message.PERMISSION_ERROR;
                           if (flag21) {
                              commandOutput13.returnResult(message25);
                              throw new CommandAbortException();
                           }
                        }

                        Punishment punishment5 = punishmentRequest.getPunishment();
                        if ((punishment5 != null ? punishment5.getUuid() : null) != null) {
                           Punishment punishment6 = punishmentRequest.getPunishment();
                           if (punishment6 != null && punishment6.getTemplate() != -1) {
                              TemplateManager templateManager = module.getTemplateManager();
                              Punishment punishment7 = punishmentRequest.getPunishment();
                              Intrinsics.checkNotNull(punishment7);
                              Template template2 = templateManager.findTemplate(punishment7);
                              if (template2 != null) {
                                 Punishment punishment8 = punishmentRequest.getPunishment();
                                 target = punishment8 != null ? punishment8.getUuid() : null;
                                 Punishment punishment9 = punishmentRequest.getPunishment();
                                 int countTemplateUses2 = session.countTemplateUses(template2, target, punishment9 != null ? punishment9.getIp() : null);
                                 TemplateInfo infoForCount = template2.getInfoForCount(countTemplateUses2);
                                 String text32 = infoForCount.getPermission();
                                 String text33 = text32;
                                 if (text32 == null) {
                                    text33 = template2.getDefaultInfo().getPermission();
                                 }

                                 CommandOutput commandOutput14;
                                 label3982: {
                                    String text34 = text33;
                                    commandOutput14 = CommandTask.COMMAND_OUTPUT;
                                    if (text34 != null) {
                                       LiteSender liteSender9 = liteSender;
                                       if (!liteSender9.hasPermission(text34)) {
                                          flag8 = true;
                                          break label3982;
                                       }
                                    }

                                    flag8 = false;
                                 }

                                 boolean flag22 = flag8;
                                 CharSequence message26 = Message.PERMISSION_ERROR;
                                 CommandOutput commandOutput15 = commandOutput14;
                                 if (flag22) {
                                    commandOutput15.returnResult(message26);
                                    throw new CommandAbortException();
                                 }
                              }
                           }
                        }

                        if (module.isVerbose()) {
                           StringBuilder builder2 = new StringBuilder().append("[0xff] ");
                           Punishment punishment10 = punishmentRequest.getPunishment();
                           String string4 = builder2.append(punishment10 != null ? punishment10.getBannedByUuid() : null)
                              .append(' ')
                              .append(liteSender.getUuidString())
                              .append(" (x=")
                              .append(areEqual2)
                              .append(')')
                              .toString();
                           module.logDebug(string4);
                        }

                        if (punishmentRequest.isModify()) {
                           Punishment punishment11 = punishmentRequest.getPunishment();
                           if (punishment11 == null) {
                              punishmentRequest.computeVoid(session, Message.BAN_ERROR_UNBAN_FAIL);
                              throw new CommandAbortException();
                           }

                           CommandTask commandTask12 = this;
                           String text35 = "litebans.modify" + text31;
                           LiteSender sender4 = commandTask12.getSender();
                           CommandOutput commandOutput16 = CommandTask.COMMAND_OUTPUT;
                           boolean flag23 = !sender4.hasPermission(text35);
                           CharSequence message27 = Message.PERMISSION_ERROR;
                           if (flag23) {
                              commandOutput16.returnResult(message27);
                              throw new CommandAbortException();
                           }

                           PunishmentType type2 = punishmentRequest.getType();
                           CharSequence message28;
                           switch (PunishmentTypeSwitchMap32.value[type2.obtainPunishmentType().ordinal()]) {
                              case 1:
                                 message28 = Message.DEFAULT_UNBAN_REASON;
                                 break;
                              case 2:
                                 message28 = Message.DEFAULT_UNMUTE_REASON;
                                 break;
                              case 3:
                                 message28 = Message.DEFAULT_UNWARN_REASON;
                                 break;
                              default:
                                 message28 = null;
                           }

                           CharSequence message29 = Intrinsics.areEqual(broadcastMessage, message28) ? punishment11.getReason() : broadcastMessage;
                           long value8 = punishmentRequest.getUntil() == -1L ? punishment11.getUntil() : punishmentRequest.getUntil();
                           punishmentRequest.broadcastPunishment(session);
                           session.updateExpiry(message29, value8, punishment11);
                           KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
                           kotlinSerializable.obj = new Punishment(
                              punishment11.getType(),
                              punishment11.getUuid(),
                              punishment11.getIp(),
                              message29,
                              punishment11.getBannedByUuid(),
                              punishment11.getBannedByName(),
                              punishment11.getServerScope(),
                              punishment11.getServerOrigin(),
                              punishment11.getTime(),
                              value8,
                              punishment11.getTemplate(),
                              punishment11.isSilent(),
                              punishment11.isIpBan(),
                              punishment11.isActive(),
                              punishment11.getId(),
                              null
                           );
                           punishmentRequest.sendToSender(session, PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.BAN_MODIFY_SUCCESS, (Punishment)kotlinSerializable.obj, false, 2, null));
                        } else if (punishmentRequest.isDelete()) {
                           Punishment punishment12 = punishmentRequest.getPunishment();
                           if (punishment12 == null) {
                              punishmentRequest.computeVoid(session, Message.BAN_ERROR_UNBAN_FAIL);
                              throw new CommandAbortException();
                           }

                           CommandTask commandTask13 = this;
                           String text36 = "litebans.delete" + text31;
                           LiteSender sender5 = commandTask13.getSender();
                           CommandOutput commandOutput17 = CommandTask.COMMAND_OUTPUT;
                           boolean flag24 = !sender5.hasPermission(text36);
                           CharSequence message30 = Message.PERMISSION_ERROR;
                           if (flag24) {
                              commandOutput17.returnResult(message30);
                              throw new CommandAbortException();
                           }

                           punishmentRequest.broadcastPunishment(session);
                           PunishmentQueries.handleDatabaseSession(session, punishment12, null, null, null, true, 14, null);
                           punishmentRequest.sendToSender(session, Message.BAN_DELETE_SUCCESS);
                        } else {
                           CommandTask commandTask14 = this;
                           String text37 = "litebans." + punishmentRequest.getType() + text31;
                           LiteSender sender6 = commandTask14.getSender();
                           CommandOutput commandOutput18 = CommandTask.COMMAND_OUTPUT;
                           boolean flag25 = !sender6.hasPermission(text37);
                           CharSequence message31 = Message.PERMISSION_ERROR;
                           if (flag25) {
                              commandOutput18.returnResult(message31);
                              throw new CommandAbortException();
                           }

                           if (punishmentRequest.getExistingPunishment() == null && settings.isUnbanAllHistory() && punishmentRequest.getType() != PunishmentType.UNWARN) {
                              List loginRecords = session.findLoginRecords(kotlinHelper.getText10(), 1024);
                              if (!loginRecords.isEmpty()) {
                                 HashSet hashSet = new HashSet();

                                 for (LoginRecord loginRecord3 : (Iterable<LoginRecord>) loginRecords) {
                                    String component22 = loginRecord3.component2();
                                    String component32 = loginRecord3.component3();
                                    Punishment punishment23 = PunishmentQueries.findPunishmentWithDefaults(session, component22, component32, punishmentRequest.getType(), punishmentRequest.getServerScope(), false, true, 16, null);
                                    if (punishment23 != null && !CollectionsKt___CollectionsKt.contains((Iterable) hashSet, component22) && !punishment23.isIpBan()) {
                                       if (module.isVerbose()) {
                                          module.logDebug(punishment23);
                                       }

                                       punishment = punishment23;
                                       booleanFlag.flag = true;
                                       PunishmentQueries.handleDatabaseSession(session, punishmentRequest.getExecutorUuid(), punishmentRequest.getExecutorName(), broadcastMessage, punishment23, false, 16, null);
                                       AltAccountPunisher.savePunishment(session, punishment23, true, flag15);
                                       Intrinsics.checkNotNull(component22);
                                       hashSet.add(component22);
                                    }
                                 }
                              }
                           }

                           if (punishmentRequest.getPunishment() == null) {
                              if (!booleanFlag.flag) {
                                 byte b5 = 1;
                                 if (module.isDebugAtLeast(b5)) {
                                    CharSequence message32 = "No active "
                                       + punishmentRequest.getType().obtainPunishmentType()
                                       + " found for uuid:"
                                       + kotlinHelper.getText8()
                                       + " / ip:"
                                       + kotlinHelper.getText9()
                                       + " on server:"
                                       + punishmentRequest.getServerScope()
                                       + " (target: '"
                                       + kotlinHelper.getTarget()
                                       + "')";
                                    boolean flag26 = !Intrinsics.areEqual(punishmentRequest.getReason(), kotlinHelper.getText8());
                                    CharSequence message33;
                                    if (flag26) {
                                       CharSequence message34 = "Try targeting by UUID instead of name.";
                                       if (message34.length() > 0) {
                                          CharSequence message35 = "\n";
                                          CharSequence message36 = "" + message32 + message35;
                                          message33 = "" + message36 + message34;
                                       } else {
                                          message33 = message32;
                                       }
                                    } else {
                                       message33 = message32;
                                    }

                                    module.logDebug(message33);
                                 }

                                 PunishmentType type3 = punishmentRequest.getType();
                                 CharSequence message37;
                                 switch (PunishmentTypeSwitchMap32.value[type3.obtainPunishmentType().ordinal()]) {
                                    case 1:
                                       message37 = Message.BAN_ERROR_UNBAN_FAIL;
                                       break;
                                    case 2:
                                       message37 = Message.UNMUTE_FAIL;
                                       break;
                                    case 3:
                                       message37 = Message.UNWARN_FAIL;
                                       break;
                                    default:
                                       StandardKt.notImplemented();
                                       throw new CommandAbortException();
                                 }

                                 CharSequence message38 = message37;
                                 if (punishmentRequest.getType() == PunishmentType.UNBAN && !kotlinHelper.obtainFlag() && flag15 && !TargetParser.isUuid(kotlinHelper.getText10())) {
                                    LiteSender liteSender10 = liteSender;
                                    String text38 = "litebans.unban.queue";
                                    if (liteSender10.hasPermission(text38) && !punishmentRequest.isNoQueue()) {
                                       this.handleKotlinHelper(kotlinHelper);
                                       message38 = Message.UNBAN_QUEUED;
                                    }
                                 }

                                 punishmentRequest.computeVoid(session, message38);
                                 throw new CommandAbortException();
                              }
                           } else {
                              if (module.isVerbose()) {
                                 module.logDebug(punishmentRequest.getPunishment());
                              }

                              if (punishmentRequest.getType() == PunishmentType.UNWARN && punishmentRequest.getExistingPunishment() == null) {
                                 Punishment punishment13 = punishmentRequest.getPunishment();
                                 Intrinsics.checkNotNull(punishment13);
                                 PunishmentQueries.handleDatabaseSession(session, punishment13, punishmentRequest.getExecutorUuid(), punishmentRequest.getExecutorName(), broadcastMessage, false, 16, null);
                              } else {
                                 text26 = punishmentRequest.getExecutorUuid();
                                 target = punishmentRequest.getExecutorName();
                                 Punishment punishment14 = punishmentRequest.getPunishment();
                                 Intrinsics.checkNotNull(punishment14);
                                 session.handleText(text26, target, broadcastMessage, punishment14, punishmentRequest.getExistingPunishment() != null);
                              }

                              if (punishmentRequest.getType() == PunishmentType.UNWARN) {
                                 punishmentRequest.sendToSender(session, session.replacePlaceholders(Message.UNWARN_RESPONSE, liteSender6));
                              }

                              SyncMessageType syncMessageType = SyncMessageType.SYNC_MESSAGE_TYPE_COMPANION.computeSyncMessageType(punishmentRequest.getType());
                              if (session.getDatabaseModule().isSyncEnabled()) {
                                 Punishment punishment15 = punishmentRequest.getPunishment();
                                 Intrinsics.checkNotNull(punishment15);
                                 KotlinHelper13 kotlinHelper3 = kotlinHelper;
                                 CharSequence[] messages = new CharSequence[2];
                                 MojangProfileLookup kotlinData = UsernameValidator.KOTLIN_DATA;
                                 text30 = kotlinHelper3.getText8();
                                 if (text30 == null) {
                                    text30 = "";
                                 }

                                 messages[0] = kotlinData.computeText2(text30);
                                 messages[1] = punishment15.isIpBan() ? String.valueOf(kotlinHelper3.getText9()) : "";
                                 CharSequence joinToString = ArraysKt.joinToStringWithDefaults(messages, "\ufeff", null, null, 0, null, null, 62, null);
                                 AltAccountPunisher.sendSyncWithDefaults(session, syncMessageType, joinToString, null, null, 12, null);
                              }

                              KotlinHelper29 kotlinHelper4 = KotlinHelper29.KOTLIN_HELPER;
                              Platform platform = session.getPlatform();
                              Punishment punishment16 = punishmentRequest.getPunishment();
                              Intrinsics.checkNotNull(punishment16);
                              kotlinHelper4.handleDatabaseSession2(session, platform, punishment16);
                           }

                           if (punishmentRequest.getPunishment() == null) {
                              punishmentRequest.setPunishment(punishment);
                           }

                           if (punishmentRequest.getPunishment() != null) {
                              PunishmentType type4 = punishmentRequest.getType();
                              CharSequence message39;
                              switch (PunishmentTypeSwitchMap32.value[type4.obtainPunishmentType().ordinal()]) {
                                 case 1:
                                    message39 = Message.BROADCAST_UNBAN;
                                    break;
                                 case 2:
                                    message39 = Message.BROADCAST_UNMUTE;
                                    break;
                                 case 3:
                                    message39 = Message.BROADCAST_UNWARN;
                                    break;
                                 default:
                                    StandardKt.notImplemented();
                                    throw new CommandAbortException();
                              }

                              CharSequence message40 = message39;
                              CharSequence message41 = PunishmentQueries.replacePlaceholdersWithDefaults(session, message40, punishmentRequest.getExecutorName(), punishmentRequest.getExecutorUuid(), false, 4, null);
                              CharSequence replacePlaceholders2 = session.replacePlaceholders(message41, kotlinHelper);
                              new PunishmentAnnounceTask(punishmentRequest, liteSender6, punishmentRequest.getTargetRecord(), replacePlaceholders2, session.getPlatform(), null, false, punishmentRequest.isSilent(), 32, null).run();
                              if (punishmentRequest.getType() == PunishmentType.UNMUTE) {
                                 if (flag15) {
                                    CommandTask commandTask15 = this;
                                    ((PunishmentLookupModule)commandTask15.getPlatform().getModule(PunishmentLookupModule.class)).isMutedInChat(punishmentRequest.getPunishment());
                                 }

                                 SyncMessageType syncMessageType2 = SyncMessageType.UNMUTE;
                                 if (session.getDatabaseModule().isSyncEnabled()) {
                                    Punishment punishment17 = punishmentRequest.getPunishment();
                                    Intrinsics.checkNotNull(punishment17);
                                    KotlinHelper13 kotlinHelper5 = kotlinHelper;
                                    CharSequence[] messages2 = new CharSequence[2];
                                    MojangProfileLookup kotlinData2 = UsernameValidator.KOTLIN_DATA;
                                    text30 = kotlinHelper5.getText8();
                                    if (text30 == null) {
                                       text30 = "";
                                    }

                                    messages2[0] = kotlinData2.computeText2(text30);
                                    messages2[1] = punishment17.isIpBan() ? String.valueOf(kotlinHelper5.getText9()) : "";
                                    CharSequence joinToString2 = ArraysKt.joinToStringWithDefaults(messages2, "\ufeff", null, null, 0, null, null, 62, null);
                                    AltAccountPunisher.sendSyncWithDefaults(session, syncMessageType2, joinToString2, null, null, 12, null);
                                 }
                              }

                              CommandTask commandTask16 = this;
                              Boolean.valueOf(((BroadcastModule)commandTask16.getPlatform().getModule(BroadcastModule.class)).isBungeeBan(punishmentRequest.getPunishment()));
                           } else {
                              ConfigModule.handleConfigModule(module, null, 0, 2, null);
                           }
                        }
                     } else {
                        if (punishmentRequest.getType() == PunishmentType.WARN) {
                           Template template3 = punishmentRequest.getTemplate();
                           if (Intrinsics.compare(template3.getId() & 255, -1 & 0xFF) >= 0) {
                              Durations durations = DurationUnit.DURATIONS;
                              long value9 = punishmentRequest.getCreationTime();
                              String warningsExpireAfter = settings.getWarningsExpireAfter();
                              double duration = Durations.parseDurationWithDefaults(durations, warningsExpireAfter, 0.0, 2, null);
                              punishmentRequest.setUntil((long)(duration < 1.0 ? -1.0 : value9 + duration));
                           }
                        }

                        punishmentRequest.broadcastPunishment(session);
                        punishmentRequest.setUntil(Math.max(punishmentRequest.getUntil(), 0L));
                        Punishment punishment18 = new Punishment(punishmentRequest, kotlinHelper, broadcastMessage);
                        punishmentRequest.setPunishment(punishment18);
                        punishmentRequest.fireStage(PunishmentStage.ENTRY_CREATED);
                        punishmentRequest.handleDatabaseSession(session, liteSender, punishment18, punishmentRequest.getTargetRecord());
                        CharSequence executor = kotlinHelper.getExecutor();
                        boolean flag27 = punishmentRequest.isOverride();
                        LoginRecord targetRecord10 = punishmentRequest.getTargetRecord();
                        switch (BanCommandSwitchMap.value[punishment18.getType().ordinal()]) {
                           case 1:
                           case 2:
                              Punishment punishment24 = PunishmentQueries.findPunishmentWithDefaults(session, punishment18.getUuid(), punishment18.getIp(), punishment18.getType(), punishment18.getServerScope(), false, false, 48, null);
                              if (punishment24 != null
                                 && (!ServerScopes.SERVER_SCOPE.isGlobal(punishment24.getServerScope()) || ServerScopes.SERVER_SCOPE.isGlobal(punishment18.getServerScope()))
                                 && (!punishment24.isIpBan() || punishment18.isIpBan())
                                 && (!punishment24.isIpBan() || !punishment18.isIpBan() || Intrinsics.areEqual(punishment18.getUuid(), punishment24.getUuid()) && Intrinsics.areEqual(punishment18.getIp(), punishment24.getIp()))) {
                                 LiteSender sender7 = this.getSender();
                                 String text39 = "litebans.override";
                                 if (!sender7.hasPermission(text39)) {
                                    CharSequence message42 = PunishmentQueries.replacePlaceholdersWithDefaults(
                                       session,
                                       PunishmentQueries.replacePlaceholdersWithDefaults(session, punishment18.getType() == PunishmentType.BAN ? Message.BAN_PREVIOUS_BAN_EXISTING : Message.PREVIOUS_MUTE_EXISTING, punishment24, false, 2, null),
                                       targetRecord10,
                                       false,
                                       2,
                                       null
                                    );
                                    CommandTask.COMMAND_OUTPUT.returnResult(message42);
                                    throw new CommandAbortException();
                                 }

                                 if (flag27) {
                                    label4022: {
                                       CommandTask commandTask17 = this;
                                       if (((ConfigModule)commandTask17.getPlatform().getModule(ConfigModule.class)).getSettings().isOverrideLowerDuration()) {
                                          if (!punishment24.isPermanent() && (punishment18.isPermanent() || punishment18.getDuration() > punishment24.getDuration())) {
                                             flag8 = true;
                                             break label4022;
                                          }
                                       }

                                       flag8 = false;
                                    }

                                    boolean flag28 = flag8;
                                    CommandOutput commandOutput19 = CommandTask.COMMAND_OUTPUT;
                                    CharSequence message43 = PunishmentQueries.replacePlaceholdersWithDefaults(session, punishment18.getType() == PunishmentType.BAN ? Message.BAN_ERROR_NO_OVERRIDE : Message.NO_OVERRIDE, targetRecord10, false, 2, null);
                                    boolean flag29 = !flag28;
                                    if (flag29) {
                                       commandOutput19.returnResult(message43);
                                       throw new CommandAbortException();
                                    }
                                 }

                                 PunishmentQueries.handleDatabaseSession(session, punishmentRequest.getExecutorUuid(), punishmentRequest.getExecutorName(), punishment18.getReason(), punishment24, false, 16, null);
                                 CharSequence message44 = PunishmentQueries.replacePlaceholdersWithDefaults(session, punishment18.getType() == PunishmentType.BAN ? Message.BAN_PREVIOUS_BAN_REMOVED : Message.PREVIOUS_MUTE_REMOVED, punishment24, false, 2, null);
                                 CharSequence[] messages3 = new CharSequence[4];
                                 messages3[0] = "bannedPlayer";
                                 Intrinsics.checkNotNull(executor);
                                 messages3[1] = executor;
                                 messages3[2] = "mutedPlayer";
                                 messages3[3] = executor;
                                 CharSequence message45 = PunishmentQueries.replacePlaceholdersWithDefaults(session, TextUtil.replacePairs(message44, messages3), targetRecord10, false, 2, null);
                                 if (StringsKt.containsWithDefaults(message45, '#', false, 2, null)) {
                                    message45 = CacheModule.CACHE_MODULE_COMPANION.computeText(message45, "#offline#", punishment18.getIp());
                                 }

                                 CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, this.getSender(), message45, null, 4, null);
                                 AltAccountPunisher.savePunishment(session, punishment24, true, flag15);
                              }
                        }

                        if (punishmentRequest.getType() == PunishmentType.WARN && liteSender6 != null && flag15) {
                           punishment18.setWarned(true);
                        }

                        AltAccountPunisher.savePunishmentWithDefaults(session, punishment18, false, false, 6, null);
                        punishmentRequest.handleDatabaseSession(session, liteSender6, kotlinHelper, flag15);
                        this.handlePunishmentRequest(punishmentRequest, kotlinHelper);
                        punishmentRequest.fireStage(PunishmentStage.ENTRY_EMITTED);

                        try {
                           this.handleDatabaseSession(session, punishment18, punishmentRequest);
                           if (!punishmentRequest.isSkip()) {
                              PunishmentType type5 = punishmentRequest.getType();
                              CharSequence message46;
                              switch (PunishmentTypeSwitchMap32.value[type5.obtainPunishmentType().ordinal()]) {
                                 case 1:
                                    message46 = Message.BAN_RESPONSE;
                                    break;
                                 case 2:
                                    message46 = Message.MUTE_RESPONSE;
                                    break;
                                 case 3:
                                    message46 = Message.WARN_RESPONSE;
                                    break;
                                 case 4:
                                    message46 = Message.RESPONSE;
                                    break;
                                 default:
                                    StandardKt.notImplemented();
                                    throw new CommandAbortException();
                              }

                              punishmentRequest.sendToSender(session, (CharSequence) (Placeholders.replacePlaceholder(message46, "tempDuration", punishmentRequest.getDurationText())));
                              switch (BanCommandSwitchMap.value[punishment18.getType().ordinal()]) {
                                 case 1:
                                    AltAccountPunisher.notifyPunishment(session, punishment18, punishmentRequest.getStackIndex());
                                    break;
                                 case 2:
                                    LiteSender[] onlineSenders2 = session.getPlatform().getOnlineSenders();
                                    Intrinsics.checkNotNull(onlineSenders2);
                                    if (onlineSenders2.length != 0) {
                                       var arrayList = new ArrayList();
                                       byte b6 = 0;

                                       for (int i = onlineSenders2.length; b6 < i; b6 += 1) {
                                          LiteSender liteSender11 = onlineSenders2[b6];
                                          Intrinsics.checkNotNull(liteSender11);
                                          if (session.isAffecting(punishment18, liteSender11)) {
                                             arrayList.add(liteSender11);
                                          }
                                       }


                                       for (Object obj5 : arrayList) {
                                          LiteSender liteSender12 = (LiteSender)obj5;
                                          CommandTask commandTask18 = this;
                                          ((PunishmentLookupModule)commandTask18.getPlatform().getModule(PunishmentLookupModule.class)).handleText(liteSender12.getUuidString(), punishment18);
                                       }
                                    }
                                    break;
                                 case 3:
                                    CommandTask commandTask19 = this;
                                    if (((ConfigModule)commandTask19.getPlatform().getModule(ConfigModule.class)).getSettings().getWarningsActions().length != 0) {
                                       ThrottleModule module3 = (ThrottleModule)session.getPlatform().getModule(ThrottleModule.class);
                                       if (punishment18.getUuid() == null) {
                                          CommandTask commandTask20 = this;
                                          ConfigModule.handleConfigModule((ConfigModule)commandTask20.getPlatform().getModule(ConfigModule.class), "null", 0, 2, null);
                                       } else {
                                          List history = session.getHistory(punishment18, 512);
                                          LoginRecord loginRecord4 = session.findLoginRecord(punishment18.getUuid());
                                          if (loginRecord4 == null) {
                                             CommandTask commandTask21 = this;
                                             ConfigModule.handleConfigModule((ConfigModule)commandTask21.getPlatform().getModule(ConfigModule.class), "null", 0, 2, null);
                                          } else {
                                             String name7 = loginRecord4.getName();
                                             int value10 = history.size();
                                             CommandTask commandTask22 = this;
                                             ConfigModule module4 = (ConfigModule)commandTask22.getPlatform().getModule(ConfigModule.class);
                                             byte b7 = 10;
                                             if (module4.isDebugAtLeast(b7)) {
                                                module4.logDebug("Active warnings: " + value10);
                                             }

                                             CommandTask commandTask23 = this;
                                             String[] warningsActions = ((ConfigModule)commandTask23.getPlatform().getModule(ConfigModule.class)).getSettings().getWarningsActions();
                                             byte b8 = 0;

                                             for (int i2 = warningsActions.length; b8 < i2; b8 += 1) {
                                                String text40 = warningsActions[b8];
                                                if (!StringsKt.isBlank(text40)) {
                                                   CharSequence message47 = text40;
                                                   char[] chars = new char[]{':'};
                                                   Collection split = StringsKt.splitWithDefaults(message47, chars, false, 2, 2, null);
                                                   String[] array = (String[])split.toArray(new String[0]);
                                                   boolean flag30 = false;
                                                   if (StringsKt.endsWithWithDefaults(array[0], '+', false, 2, null)) {
                                                      flag30 = true;
                                                      array[0] = array[0].substring(0, array[0].length() - 1);
                                                   }

                                                   if (StringsKt.containsWithDefaults(text40, ':', false, 2, null) && TargetParser.TARGET_PARSER.isIpv4(array[0])) {
                                                      int int2 = Integer.parseInt(array[0]);
                                                      if (int2 < 1 && !flag30) {
                                                         session.getPlatform()
                                                            .getLogger()
                                                            .warning("Warning action is invalid (threshold " + int2 + " < 1): '" + text40 + "'. Ignored.");
                                                      } else if (value10 == int2 || flag30 && value10 > int2) {
                                                         KotlinSerializableImpl kotlinSerializable2 = new KotlinSerializableImpl();
                                                         kotlinSerializable2.obj = array[1];
                                                         if (StringsKt.startsWithWithDefaults((CharSequence)kotlinSerializable2.obj, '/', false, 2, null)) {
                                                            CharSequence charSequence = (CharSequence)kotlinSerializable2.obj;
                                                            byte b9 = 1;
                                                            int value11 = charSequence.length();
                                                            kotlinSerializable2.obj = charSequence.subSequence(b9, value11).toString();
                                                         }

                                                         if (Placeholders.containsPlaceholder((CharSequence)kotlinSerializable2.obj, "list")) {
                                                            StringBuilder stringBuilder = new StringBuilder();
                                                            Iterable iterable = history;

                                                            for (Object obj6 : iterable) {
                                                               Punishment punishment19 = (Punishment)obj6;
                                                               stringBuilder.append('\n').append(PunishmentQueries.replacePlaceholdersWithDefaults(session, Message.LIST_ENTRY, punishment19, false, 2, null));
                                                            }

                                                            kotlinSerializable2.obj = Placeholders.replacePlaceholder((CharSequence)kotlinSerializable2.obj, "list", stringBuilder);
                                                         }

                                                         kotlinSerializable2.obj = PunishmentQueries.replacePlaceholdersWithDefaults(
                                                            session,
                                                            Placeholders.replacePlaceholder(PunishmentQueries.replacePlaceholdersWithDefaults(session, (CharSequence)kotlinSerializable2.obj, punishment18, false, 2, null), "player", name7),
                                                            loginRecord4,
                                                            false,
                                                            2,
                                                            null
                                                         );
                                                         CommandTask commandTask24 = this;
                                                         ((ConfigModule)commandTask24.getPlatform().getModule(ConfigModule.class)).debug(10, kotlinSerializable2.obj);
                                                         CommandTask commandTask25 = this;
                                                         LiteSender liteSender13 = ((ConfigModule)commandTask25.getPlatform().getModule(ConfigModule.class)).getSettings().isWarningsActionsExecuteAsConsole() ? session.getPlatform().getConsole() : this.getSender();
                                                         Intrinsics.checkNotNull(liteSender13);
                                                         module3.handleByteValue((byte)3, liteSender13.getUuidString());
                                                         session.getPlatform().runSync(new KotlinRunnableImpl(liteSender13, kotlinSerializable2));
                                                         CommandTask commandTask26 = this;
                                                         if (((ConfigModule)commandTask26.getPlatform().getModule(ConfigModule.class)).getSettings().isWarningsDeleteWarningsAfterAction()) {
                                                            CommandTask commandTask27 = this;
                                                            if (text40 == ArraysKt.last(((ConfigModule)commandTask27.getPlatform().getModule(ConfigModule.class)).getSettings().getWarningsActions())) {
                                                               Iterable iterable2 = history;

                                                               for (Object obj7 : iterable2) {
                                                                  Punishment punishment20 = (Punishment)obj7;
                                                                  PunishmentQueries.handleDatabaseSession(session, punishment20, punishment18.getBannedByUuid(), punishment18.getBannedByName(), null, false, 24, null);
                                                               }
                                                            }
                                                         }
                                                      }
                                                   } else {
                                                      session.getPlatform().getLogger().warning("Warning action is invalid (bad format): '" + text40 + "'. Ignored.");
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                    break;
                                 case 4:
                                    StandardKt.notImplemented();
                                    throw new CommandAbortException();
                                 default:
                                    StandardKt.notImplemented();
                                    throw new CommandAbortException();
                              }

                              CommandTask commandTask28 = this;
                              BroadcastModule module5 = (BroadcastModule)commandTask28.getPlatform().getModule(BroadcastModule.class);
                              module5.handleDatabaseSession(session, punishmentRequest);
                           }

                           if (!punishmentRequest.isSkip()) {
                              CommandTask commandTask29 = this;
                              if (((ConfigModule)commandTask29.getPlatform().getModule(ConfigModule.class)).getSettings().getByteValue() > 0 && punishment18.getType() == PunishmentType.BAN && punishment18.isIpBan() && punishment18.getUuid() != null) {
                                 CommandOutput commandOutput20 = CommandTask.COMMAND_OUTPUT;
                                 boolean flag31 = !session.getDatabaseModule().isDisclaimerAccepted();
                                 CharSequence message48 = Message.ALTS_GUARDRAIL_DISCLAIMER;
                                 if (flag31) {
                                    commandOutput20.returnResult(message48);
                                    throw new CommandAbortException();
                                 }

                                 DupeIpScanTask punishmentApplyTask = new DupeIpScanTask(punishment18.getUuid(), session, null, false, 0, 28, null).obtainPunishmentApplyTask();
                                 if (punishmentApplyTask.isBannedFound()) {
                                    CommandOutput commandOutput21 = CommandTask.COMMAND_OUTPUT;
                                    boolean flag32 = punishmentApplyTask.getResultCount() > 10 && !punishmentRequest.isConfirmed();
                                    if (flag32) {
                                       commandOutput21.returnResult(Message.BAN_ALTS_GUARDRAIL_BAN_MANY.computeText("list", punishmentApplyTask.getResultText()));
                                       throw new CommandAbortException();
                                    }

                                    CommandOutput commandOutput22 = CommandTask.COMMAND_OUTPUT;
                                    var list2 = punishmentApplyTask.getResults();
                                    if (list2 instanceof Collection && list2.isEmpty()) {
                                       flag8 = false;
                                    } else {
                                       Iterator iterator2 = list2.iterator();

                                       while (true) {
                                          if (!iterator2.hasNext()) {
                                             flag8 = false;
                                             break;
                                          }

                                          Object obj8 = iterator2.next();
                                          DupeIpResult dupeIpResult = (DupeIpResult)obj8;
                                          if (Intrinsics.areEqual(dupeIpResult.getText4(), punishmentRequest.getExecutorUuid())) {
                                             flag8 = true;
                                             break;
                                          }
                                       }
                                    }

                                    boolean flag33 = flag8;
                                    CharSequence message49 = Message.BAN_ALTS_GUARDRAIL_BAN_SELF;
                                    if (flag33) {
                                       commandOutput22.returnResult(message49);
                                       throw new CommandAbortException();
                                    }

                                    for (DupeIpResult dupeIpResult2 : (Iterable<DupeIpResult>) punishmentApplyTask.getResults()) {
                                       UUID uuid4 = TargetParser.TARGET_PARSER.parseUuid(dupeIpResult2.getText4());
                                       if (uuid4 != null) {
                                          if (!Intrinsics.areEqual(dupeIpResult2.getText4(), punishment18.getUuid())
                                             && (dupeIpResult2.getPunishment() == null || !Intrinsics.areEqual(dupeIpResult2.getPunishment().getUuid(), dupeIpResult2.getText4()))
                                             && !PunishmentQueries.hasAllowEntryWithDefaults(session, uuid4, null, 2, null)) {
                                             punishmentRequest.handleDatabaseSession(session, dupeIpResult2.getText3(), dupeIpResult2.getText4());
                                             LoginRecord targetRecord11 = punishmentRequest.getTargetRecord();
                                             text30 = targetRecord11 != null ? targetRecord11.getName() : null;
                                             LoginRecord targetRecord12 = punishmentRequest.getTargetRecord();
                                             AltAccountPunisher.computePunishment(session, dupeIpResult2, punishment18, text30, targetRecord12 != null ? targetRecord12.getIp() : null, null, null, false, 112, null);
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        } finally {
                           SyncMessageType syncMessageType3 = SyncMessageType.SYNC_MESSAGE_TYPE_COMPANION.computeSyncMessageType(punishmentRequest.getType());
                           if (session.getDatabaseModule().isSyncEnabled()) {
                              KotlinHelper13 kotlinHelper6 = kotlinHelper;
                              CharSequence[] messages4 = new CharSequence[2];
                              MojangProfileLookup kotlinData3 = UsernameValidator.KOTLIN_DATA;
                              text30 = kotlinHelper6.getText8();
                              if (text30 == null) {
                                 text30 = "";
                              }

                              messages4[0] = kotlinData3.computeText2(text30);
                              messages4[1] = punishment18.isIpBan() ? String.valueOf(kotlinHelper6.getText9()) : "";
                              CharSequence joinToString3 = ArraysKt.joinToStringWithDefaults(messages4, "\ufeff", null, null, 0, null, null, 62, null);
                              AltAccountPunisher.sendSyncWithDefaults(session, syncMessageType3, joinToString3, null, null, 12, null);
                           }
                        }

                        punishmentRequest.fireStage(PunishmentStage.ENTRY_APPLIED);
                     }
                  } finally {
                     punishmentRequest.fireStage(PunishmentStage.FINALIZED);
                  }
               } catch (LiteException e3) {
               }

            } catch (Throwable e4) {
               error = e4;
               throw SneakyThrow.<RuntimeException>rethrow(e4);
            } finally {
               CloseableKt.handleCloseable(session, error);
            }
         } catch (Throwable e5) {
            if (!(e5 instanceof SQLException)) {
               throw SneakyThrow.<RuntimeException>rethrow(e5);
            }
            SQLException sqlException = (SQLException) e5;
            if (!module2.isConnectionFailure(sqlException)) {
               throw sqlException;
            }
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void handlePunishmentRequest(PunishmentRequest punishmentRequest, String text) {
      Template template = this.computeTemplate(punishmentRequest, text);
      if (template == null) {
         punishmentRequest.getReasonBuilder().append(text).append(' ');
      } else {
         this.handlePunishmentRequest(punishmentRequest, template);
      }
   }

   private final void handlePunishmentRequest(PunishmentRequest punishmentRequest, Template template) {
      punishmentRequest.setTemplate(template);
      CommandTask commandTask = this;
      punishmentRequest.setTemplateGroup(((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getTemplateManager().computeTemplateGroup(punishmentRequest.getType(), template));
   }

   private final Template computeTemplate(PunishmentRequest punishmentRequest, String text) {
      Template template;
      if (punishmentRequest.getReasonBuilder().length() <= 0 && punishmentRequest.getReason().length() != 0 && !punishmentRequest.hasTemplate() && !punishmentRequest.isUndo()) {
         CommandTask commandTask = this;
         template = ((ConfigModule)commandTask.getPlatform().getModule(ConfigModule.class)).getTemplateManager().computeTemplate(punishmentRequest.getType(), text);
      } else {
         template = null;
      }

      return template;
   }

   private final void handlePunishmentRequest(PunishmentRequest punishmentRequest, KotlinHelper13 kotlinHelper) {
      if (punishmentRequest.getType() == PunishmentType.BAN) {
         CommandTask commandTask = this;
         Collection pendingUnbans = ((BroadcastModule)commandTask.getPlatform().getModule(BroadcastModule.class)).getPendingUnbans();
         String target = kotlinHelper.getTarget();
         if (target != null) {
            String text = target;
            target = text.toLowerCase(Locale.ENGLISH);
         } else {
            target = null;
         }

         String text2 = target;
         KotlinUtil30.asMutableCollection(pendingUnbans).remove(text2);
      }
   }

   private final void handleKotlinHelper(KotlinHelper13 kotlinHelper) {
      CommandTask commandTask = this;
      if (((BroadcastModule)commandTask.getPlatform().getModule(BroadcastModule.class)).getPendingUnbans().size() >= 128) {
         CommandTask commandTask2 = this;
         ((BroadcastModule)commandTask2.getPlatform().getModule(BroadcastModule.class)).getPendingUnbans().clear();
         this.getPlatform().getLogger().warning("Too many unbanned players in queue! Cleared list to prevent memory leak.");
      }

      String text = kotlinHelper.getText10();
      if (text != null) {
         String text2 = text;
         text = text2.toLowerCase(Locale.ENGLISH);
         if (text != null) {
            String text3 = text;
            CommandTask commandTask3 = this;
            ((BroadcastModule)commandTask3.getPlatform().getModule(BroadcastModule.class)).getPendingUnbans().add(text3);
            return;
         }
      }
   }

}
