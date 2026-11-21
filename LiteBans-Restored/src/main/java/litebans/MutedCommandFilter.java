package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MutedCommandFilter {
   private MutedCommandFilter() {
   }

   public final boolean checkPlatform(@NotNull Platform platform, @Nullable String text) {
      ConfigModule module = (ConfigModule)platform.getModule(ConfigModule.class);
      if (!module.getSettings().isIpHistoryIgnored()) {
         List ignoredIphistoryServers = module.getSettings().getIgnoredIphistoryServers();
         String text2 = text;
         if (text == null) {
            text2 = module.getSettings().getServerName();
         }

         if (!ignoredIphistoryServers.contains(text2)) {
            return true;
         }
      }

      return false;
   }

   public final String[] computeTexts(@NotNull ConfigModule configModule) {
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
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = texts.length; value < i; value++) {
         String text = texts[value];
         if (configModule.isCommandEnabled(text)) {
            arrayList.add(text);
         }
      }

      Collection collection = arrayList;
      return (String[]) collection.toArray(new String[0]);
   }

   public final void handlePlatform(@NotNull Platform platform, int value) {
      SyncModule module = (SyncModule)platform.getModule(SyncModule.class);
      module.getKotlinCommandTaskSub7().getCopyOnWriteArrayList().add((byte)value);
      if (module.getKotlinCommandTaskSub7().getAtomicBoolean().compareAndSet(false, true)) {
         platform.execute(module.getKotlinCommandTaskSub7());
      }
   }

   public static void handleMutedCommandFilter(MutedCommandFilter mutedCommandFilter, Platform platform, int value, int value2, Object obj) {
      if ((value2 & 2) != 0) {
         value = -1;
      }

      mutedCommandFilter.handlePlatform(platform, value);
   }

   public final List computeList(@Nullable LiteCommand liteCommand, @NotNull String text, @NotNull LiteSender liteSender, @NotNull Platform platform, @NotNull String[] texts) {
      boolean flag = false;
      PunishmentType punishmentType = null;
      try {
         handleMutedCommandFilter(this, platform, 0, 2, null);
         ConfigModule module = (ConfigModule)platform.getModule(ConfigModule.class);
         byte b2 = 8;
         ArrayList arrayList = new ArrayList();
         String last2 = (String)ArraysKt.last(texts);
         int length2 = texts.length;
         String[] texts2 = texts;
         int value = 0;
         int value2 = 0;

         for (int i = texts2.length; value2 < i; value2++) {
            String text2 = texts2[value2];
            if (StringsKt.startsWithWithDefaults(text2, '-', false, 2, null)) {
               value++;
            }
         }

         int value3 = value;
         int value4 = length2 - value3;
         if (platform.getPlatformId() == 3) {
            value4--;
         }

         if (value4 > 1) {
            if (StringsKt.checkText(last2, "server:", false, 2, null)) {
               String substringAfter = StringsKt.substringAfterWithDefaults(last2, "server:", null, 2, null);
               handleMutedCommandFilter(this, platform, substringAfter, arrayList, 0, 8, null);
            } else if (value4 == 2 && !StringsKt.checkText(text, "un", false, 2, null)) {
               PunishmentType[] punishmentTypes = PunishmentType.values();
               int value5 = 0;
               int length3 = punishmentTypes.length;

               while (true) {
                  if (value5 >= length3) {
                     punishmentType = null;
                     break;
                  }

                  PunishmentType punishmentType2;
                  label240: {
                     punishmentType2 = punishmentTypes[value5];
                     PunishmentType punishmentType3 = punishmentType2;
                     String text3 = text;
                     if (!StringsKt.checkText(text3, punishmentType3.toString(), false, 2, null)) {
                        String text4 = text;
                        label141:
                        if (!StringsKt.checkText2(text4, punishmentType3.toString(), false, 2, null)) {
                           if (StringsKt.checkText2(text, "ip", false, 2, null)) {
                              CharSequence message = text;
                              CharSequence message2 = punishmentType3;
                              CharSequence message3 = "ip";
                              if (StringsKt.endsWithWithDefaults((CharSequence)message, (CharSequence)("" + message2 + message3), false, 2, null)) {
                                 break label141;
                              }
                           }

                           flag = false;
                           break label240;
                        }
                     }

                     flag = true;
                  }

                  if (flag) {
                     punishmentType = punishmentType2;
                     break;
                  }

                  value5++;
               }

               PunishmentType punishmentType4 = punishmentType;
               if (punishmentType4 != null) {
                  Iterable templates = module.getTemplateManager().getTemplates();
                  var arrayList2 = new ArrayList();

                  for (Object obj : templates) {
                     Template template = (Template)obj;
                     if (template.getType() == punishmentType4) {
                        arrayList2.add(obj);
                     }
                  }

                  for (Template template2 : (Iterable<Template>) arrayList2) {
                     if (StringsKt.checkText2(template2.getName(), last2, true)) {
                        if (arrayList.size() >= b2) {
                           return arrayList;
                        }

                        String text5 = template2.getDefaultInfo().getPermission();
                        if (text5 != null) {
                           LiteSender liteSender2 = liteSender;
                           if (!liteSender2.hasPermission(text5)) {
                              continue;
                           }
                        }

                        arrayList.add(template2.getName());
                     }
                  }
               }
            }
         } else {
            Iterator iterator2 = KotlinUtil12.computeIterator(platform.getOnlineSenders());

            while (iterator2.hasNext()) {
               LiteSender liteSender3 = (LiteSender)iterator2.next();
               if (StringsKt.checkText2(liteSender3.getName(), last2, true)) {
                  arrayList.add(liteSender3.getName());
                  if (arrayList.size() >= b2) {
                     return arrayList;
                  }
               }
            }

            if (module.getSettings().getOfflineTabSize() > 0) {
               String firstOrNull2 = (String)ArraysKt.firstOrNull(texts);
               if (firstOrNull2 == null) {
                  return arrayList;
               }

               String lowerCase = firstOrNull2.toLowerCase(Locale.ENGLISH);
               if (lowerCase == null) {
                  return arrayList;
               }

               String text6 = lowerCase;
               String text7 = "";
               boolean flag2 = StringsKt.startsWithWithDefaults(text6, '.', false, 2, null) || StringsKt.startsWithWithDefaults(text6, '*', false, 2, null);
               if (flag2) {
                  text7 = String.valueOf(StringsKt.computeCharValue(text6));
                  text6 = text6.substring(1);
               }

               if (!StringsKt.isBlank(text6)) {
                  SyncModule module2 = (SyncModule)platform.getModule(SyncModule.class);
                  int value6 = module2.computeIntValue(StringsKt.computeCharValue(text6));
                  KotlinHelper8 kotlinHelper = module2.computeKotlinHelper(value6);
                  if (kotlinHelper == null) {
                     this.handlePlatform(platform, value6);
                     return arrayList;
                  }

                  int[] ints = module2.computeInts(text6);
                  int value7 = 0;

                  for (int i2 = ints.length; value7 < i2; value7++) {
                     int value8 = ints[value7];
                     Iterable iterable = (Iterable)kotlinHelper.getMap2().get(value8);
                     if (iterable != null) {

                        for (Object obj2 : iterable) {
                           String string = (String)obj2;
                           if (StringsKt.checkText(string, text6, false, 2, null)) {
                              boolean flag3;
                              if (arrayList instanceof Collection && arrayList.isEmpty()) {
                                 flag3 = true;
                              } else {
                                 Iterator iterator3 = arrayList.iterator();

                                 while (true) {
                                    if (!iterator3.hasNext()) {
                                       flag3 = true;
                                       break;
                                    }

                                    Object obj3 = iterator3.next();
                                    String string2 = (String)obj3;
                                    if (StringsKt.checkText(string2, string, true)) {
                                       flag3 = false;
                                       break;
                                    }
                                 }
                              }

                              if (flag3) {
                                 arrayList.add(text7 + string);
                                 if (arrayList.size() >= b2) {
                                    return arrayList;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (module.isVerbose()) {
            CharSequence message4 = text;
            Collection collection = arrayList;
            module.logDebug(TextUtil.computeMessage(message4, Arrays.toString(collection.toArray(new String[0]))));
         }

         return arrayList;
      } catch (LiteMessageException e) {
         return CollectionsKt.emptyList();
      }
   }

   private final void handlePlatform(Platform platform, String text, ArrayList list, int value) {
      DatabaseModule module = (DatabaseModule)platform.getModule(DatabaseModule.class);
      String[] texts = new String[]{"global", "local"};
      Collection serverNames = platform.createBridge().getServerNames();
      Collection collection = module.getServerRecords().values();
      HashSet hashSet = new HashSet();
      int value2 = 0;

      for (int i = texts.length; value2 < i; value2++) {
         String text2 = texts[value2];
         if (this.checkText(text2, text, hashSet, value)) {
            break;
         }
      }

      for (String text3 : (Iterable<String>) serverNames) {
         if (this.checkText(text3, text, hashSet, value)) {
            break;
         }
      }

      for (UserRecord userRecord : (Iterable<UserRecord>) collection) {
         if (this.checkText(userRecord.getName(), text, hashSet, value)) {
            break;
         }
      }

      list.addAll(hashSet);
   }

   static void handleMutedCommandFilter(MutedCommandFilter mutedCommandFilter, Platform platform, String text, ArrayList list, int value, int value2, Object obj) {
      if ((value2 & 8) != 0) {
         value = 5;
      }

      mutedCommandFilter.handlePlatform(platform, text, list, value);
   }

   private final boolean checkText(String text, String text2, HashSet set, int value) {
      if (text == null) {
         return false;
      }

      if (StringsKt.isBlank(text2) || StringsKt.checkText2(text, text2, true)) {
         set.add("server:" + text);
      }

      return set.size() >= value;
   }

   public MutedCommandFilter(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
