package litebans;

import org.jetbrains.annotations.NotNull;

public final class LiteHelper2 {
   private final PunishmentRequest punishmentRequest2;

   public LiteHelper2(@NotNull PunishmentRequest punishmentRequest) {
      this.punishmentRequest2 = punishmentRequest;
   }

   public final void handleTexts(@NotNull String[] texts, boolean flag, boolean flag2) {
      PunishmentRequest punishmentRequest = this.punishmentRequest2;
      boolean flag3 = false;
      int value = 0;

      label200:
      for (int i2 = texts.length; value < i2; value++) {
         String text = texts[value];
         boolean flag4 = true;
         if (StringsKt.startsWithWithDefaults(text, '-', false, 2, null) && text.length() > 1 && !flag3) {
            if (Intrinsics.areEqual(text, "--")) {
               flag3 = true;
            } else {
               char[] chars = new char[]{'-'};
               String trim2 = StringsKt.trim(text, chars);
               if (!Intrinsics.areEqual(trim2, "s") && !Intrinsics.areEqual(trim2, "s:true") && !Intrinsics.areEqual(trim2, "s:" + Message.STRINGS_TRUE)) {
                  if (!Intrinsics.areEqual(trim2, "s:false") && !Intrinsics.areEqual(trim2, "s:" + Message.STRINGS_FALSE)) {
                     label218: {
                        if (StringsKt.checkText(trim2, "sender", false, 2, null) || StringsKt.checkText(trim2, "server-origin", false, 2, null)) {
                           if (flag2) {
                              break label218;
                           }

                           LiteSender sender = punishmentRequest.getSender();
                           String text2 = "litebans.admin";
                           if (sender.hasPermission(text2)) {
                              break label218;
                           }
                        }

                        if (StringsKt.checkText(trim2, "stack=", false, 2, null)) {
                           punishmentRequest.setTemplateId(Byte.parseByte(StringsKt.substringAfterWithDefaults(trim2, "stack=", null, 2, null)));
                           if (punishmentRequest.getTemplateId() > 32) {
                              CommandTask.COMMAND_OUTPUT.returnResult(Message.ERROR_STACK_OVERFLOW);
                              throw new CommandAbortException();
                           }
                           continue;
                        }

                        label162: {
                           switch (trim2.hashCode()) {
                              case -1335458389:
                                 if (trim2.equals("delete")) {
                                    punishmentRequest.setDelete(true);
                                    break label162;
                                 }
                                 break;
                              case -1068795718:
                                 if (trim2.equals("modify")) {
                                    punishmentRequest.setModify(true);
                                    break label162;
                                 }
                                 break;
                              case -439474683:
                                 if (trim2.equals("no-queue")) {
                                    punishmentRequest.setNoQueue(true);
                                    break label162;
                                 }
                                 break;
                              case 3202370:
                                 if (trim2.equals("hide")) {
                                    if (!flag2) {
                                       LiteSender sender2 = punishmentRequest.getSender();
                                       String text3 = "litebans.admin";
                                       if (!sender2.hasPermission(text3)) {
                                          break label162;
                                       }
                                    }

                                    punishmentRequest.setHidden(true);
                                    break label162;
                                 }
                                 break;
                              case 3532159:
                                 if (trim2.equals("skip")) {
                                    if (!flag2) {
                                       LiteSender sender3 = punishmentRequest.getSender();
                                       String text4 = "litebans.admin";
                                       if (!sender3.hasPermission(text4)) {
                                          break label162;
                                       }
                                    }

                                    punishmentRequest.setSkip(true);
                                    break label162;
                                 }
                                 break;
                              case 951117504:
                                 if (trim2.equals("confirm")) {
                                    punishmentRequest.setConfirmed(true);
                                    break label162;
                                 }
                           }

                           flag4 = false;
                        }

                        if (flag4) {
                           continue;
                        }

                        char[] charArray = trim2.toCharArray();
                        int value2 = 0;
                        int length2 = charArray.length;

                        while (value2 < length2) {
                           char c2 = charArray[value2];
                           switch (c2) {
                              case 'I':
                              case 'M':
                              case 'N':
                              case 'S':
                              case 'T':
                              case 'U':
                              case 'W':
                              case 'd':
                              case 'm':
                              case 'p':
                                 value2++;
                                 break;
                              default:
                                 if (flag) {
                                    punishmentRequest.getExtraArgs().add(text);
                                 }
                                 continue label200;
                           }
                        }

                        int value3 = 0;

                        for (int i3 = charArray.length; value3 < i3; value3++) {
                           char c3 = charArray[value3];
                           switch (c3) {
                              case 'I':
                                 punishmentRequest.setIpBan(true);
                                 break;
                              case 'M':
                                 punishmentRequest.setType(PunishmentType.MUTE);
                                 break;
                              case 'N':
                                 punishmentRequest.setOverride(true);
                                 break;
                              case 'S':
                                 if (!flag2) {
                                    LiteSender sender4 = punishmentRequest.getSender();
                                    String text5 = "litebans.extrasilent";
                                    if (!sender4.hasPermission(text5)) {
                                       break;
                                    }
                                 }

                                 punishmentRequest.setSilent(true);
                                 punishmentRequest.setExtraSilent(true);
                                 break;
                              case 'T':
                                 punishmentRequest.setTemporary(true);
                                 break;
                              case 'U':
                                 punishmentRequest.toUndoType();
                                 break;
                              case 'W':
                                 punishmentRequest.setType(PunishmentType.WARN);
                                 punishmentRequest.setTemporary(false);
                                 break;
                              case 'd':
                                 punishmentRequest.setDelete(true);
                                 break;
                              case 'm':
                                 punishmentRequest.setModify(true);
                                 break;
                              case 'p':
                                 if (!flag2) {
                                    LiteSender sender5 = punishmentRequest.getSender();
                                    String text6 = "litebans.public";
                                    if (!sender5.hasPermission(text6)) {
                                       continue;
                                    }
                                 }

                                 punishmentRequest.setSilent(false);
                                 punishmentRequest.setExtraSilent(false);
                           }
                        }
                        continue;
                     }

                     if (StringsKt.checkText(trim2, "sender=", false, 2, null)) {
                        punishmentRequest.setExecutorName(trim2.substring(7));
                        punishmentRequest.setCustomSender(true);
                     } else if (StringsKt.checkText(trim2, "sender-name=", false, 2, null)) {
                        punishmentRequest.setExecutorName(trim2.substring(12));
                        punishmentRequest.setCustomSender(true);
                     } else if (StringsKt.checkText(trim2, "sender-uuid=", false, 2, null)) {
                        punishmentRequest.setExecutorUuid(TargetParser.normalize(trim2.substring(12)));
                        punishmentRequest.setCustomSender(true);
                     } else if (StringsKt.checkText(trim2, "server-origin=", false, 2, null)) {
                        punishmentRequest.setServerOrigin(trim2.substring(14));
                     }
                  } else {
                     punishmentRequest.setSilent(false);
                     punishmentRequest.setExtraSilent(false);
                  }
               } else {
                  if (!flag2) {
                     LiteSender sender6 = punishmentRequest.getSender();
                     String[] texts2 = new String[]{"litebans.notify", "litebans.notify.silent"};
                     int value4 = 0;
                     int length3 = texts2.length;

                     boolean flag5;
                     while (true) {
                        if (value4 >= length3) {
                           flag5 = false;
                           break;
                        }

                        String text7 = texts2[value4];
                        if (sender6.hasPermission(text7)) {
                           flag5 = true;
                           break;
                        }

                        value4++;
                     }

                     if (!flag5) {
                        continue;
                     }
                  }

                  punishmentRequest.setSilent(true);
               }
            }
         } else if (flag) {
            punishmentRequest.getExtraArgs().add(text);
         }
      }

      if (punishmentRequest.isModify()) {
         punishmentRequest.toUndoType();
      }
   }

   public static void handleLiteHelper(LiteHelper2 liteHelper, String[] texts, boolean flag, boolean flag2, int value, Object obj) {
      if ((value & 1) != 0) {
         texts = liteHelper.punishmentRequest2.getArgs();
      }

      if ((value & 2) != 0) {
         flag = true;
      }

      if ((value & 4) != 0) {
         flag2 = false;
      }

      liteHelper.handleTexts(texts, flag, flag2);
   }

   public final boolean checkText(@NotNull String text) {
      return StringsKt.startsWithWithDefaults(text, '*', false, 2, null)
         && !StringsKt.endsWithWithDefaults(text, '*', false, 2, null)
         && text.length() > 3
         && !StringsKt.containsWithDefaults(text, '.', false, 2, null)
         && !StringsKt.containsWithDefaults(text, ':', false, 2, null);
   }
}
