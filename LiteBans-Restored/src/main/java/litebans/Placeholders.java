package litebans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Placeholders {
   public static final String replacePlaceholder(@NotNull CharSequence message, @NotNull CharSequence message2, @Nullable Object obj) {
      CharSequence message3 = message2;
      return CacheModule.CACHE_MODULE_COMPANION.computeText(message, "" + '$' + message3, obj);
   }

   public static final String replacePlaceholderLazy(@NotNull CharSequence message, @NotNull CharSequence message2, @NotNull Function0 supplier) {
      return containsText(message, message2) ? replacePlaceholder(message, message2, supplier.obtainObj2()) : message.toString();
   }

   public static final CharSequence getBanScreenMessage(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, int value, int value2) {
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      Punishment punishment2 = punishment;
      if (punishment2 != null && punishment2.getTemplate() != -1) {
         Template template = module.getTemplateManager().findTemplate(punishment);
         if (template != null) {
            TemplateGroup templateGroup = module.getTemplateManager().computeTemplateGroup(punishment.getType(), template);
            int value3 = value;
            TemplateInfo templateInfo;
            if (templateGroup != null) {
               if (value3 == -1) {
                  String uuid = punishment.getUuid();
                  Intrinsics.checkNotNull(uuid);
                  Pair pair = databaseSession.computePair(uuid, punishment.getIp(), templateGroup, template);
                  value3 = (int)Math.floor(((Number)pair.getSecond()).doubleValue()) + value2;
               }

               templateInfo = templateGroup.computeTemplateInfo(value3);
            } else {
               if (value3 == -1) {
                  String uuid2 = punishment.getUuid();
                  Intrinsics.checkNotNull(uuid2);
                  value3 = databaseSession.countTemplateUses(template, uuid2, punishment.getIp()) + value2;
               }

               templateInfo = template.getInfoForCount(value3);
            }

            String text = templateInfo.getMessage();
            CharSequence message = text != null ? text : template.getDefaultInfo().getMessage();
            if (message == null) {
               return null;
            }

            return replaceTemplatePlaceholders(databaseSession, message, template, value3);
         }
      }

      return null;
   }

   public static CharSequence getBanScreenMessageWithDefaults(DatabaseSession databaseSession, Punishment punishment, int value, int value2, int value3, Object obj) {
      if ((value3 & 2) != 0) {
         value = -1;
      }

      if ((value3 & 4) != 0) {
         value2 = -1;
      }

      return getBanScreenMessage(databaseSession, punishment, value, value2);
   }

   public static final String replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull PunishmentRequest punishmentRequest) {
      return replaceTemplatePlaceholders(databaseSession, message, punishmentRequest.getTemplate(), punishmentRequest.getStackIndex());
   }

   public static final String replaceTemplatePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull Template template, int value) {
      Template template2 = template;
      Serializable serializable = Intrinsics.compare(template2.getId() & 255, -1 & 0xFF) < 0 ? template.getName() : Message.STRINGS_NULL;
      int value2 = value + 1;
      CharSequence replacePlaceholder2 = replacePlaceholder(message, "templateStep", value2);
      CharSequence[] messages = new CharSequence[]{"templateName", String.valueOf(serializable)};
      return TextUtil.replacePairs(replacePlaceholder2, messages);
   }

   public static final Pair getLuckPermsPrefixSuffix(@NotNull DatabaseSession databaseSession, @Nullable String text) {
      if (text == null) {
         return new Pair("", "");
      }

      Object obj = null;
      if (databaseSession.getPlatform().createBridge().getPlugin("LuckPerms") != null) {
         obj = LiteHelper.LITE_HELPER.getLuckPerms();
      }

      if (obj != null) {
         Object obj2 = LiteHelper.LITE_HELPER.loadUser(obj, text);
         if (obj2 != null) {
            Pair pair;
            String text2 = LiteHelper.LITE_HELPER.getPrefix(obj2);
            if (text2 == null) {
               text2 = "";
            }

            String text3 = LiteHelper.LITE_HELPER.getSuffix(obj2);
            if (text3 == null) {
               text3 = "";
            }

            pair = new Pair(text2, text3);
            return pair;
         }
      }

      return new Pair("", "");
   }

   public static final CharSequence getPunishmentMessage(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull LiteSender liteSender, int value) {
      CharSequence banScreenMessage = getBanScreenMessage(databaseSession, punishment, value, -1);
      if (banScreenMessage == null) {
         banScreenMessage = punishment.isPermanent() ? Message.BANNED_MESSAGE_PERMANENT : Message.BANNED_MESSAGE;
      }

      return replacePunishmentPlaceholdersForWithDefaults(databaseSession, punishment, liteSender, banScreenMessage, 0L, 8, null);
   }

   public static final String getKickMessage(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull LiteSender liteSender) {
      String string = punishment.getReason().toString();
      CharSequence message = string.length() == 0 ? Message.KICK_REASON_EMPTY : Message.KICK_REASON;
      CharSequence[] messages = new CharSequence[]{"reason", string};
      CharSequence message2 = TextUtil.replacePairs(message, messages);
      return replacePunishmentPlaceholdersForWithDefaults(databaseSession, punishment, liteSender, message2, 0L, 8, null);
   }

   public static final String getGeoipBlockMessage(@NotNull DatabaseSession databaseSession, @NotNull String text, @NotNull String text2) {
      CharSequence message = Message.BANNED_MESSAGE_GEOIP_BLACKLIST.computeText("geoip", text);
      CharSequence[] messages = new CharSequence[]{"serverOrigin", text2};
      String text3 = TextUtil.replacePairs(message, messages);
      return Message.computeText(text3, true);
   }

   public static final CharSequence replaceTargetPlaceholders(
      @NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, int value
   ) {
      CharSequence banScreenMessage = getBanScreenMessage(databaseSession, punishment, value, -1);
      if (banScreenMessage == null) {
         banScreenMessage = punishment.isPermanent() ? Message.BANNED_MESSAGE_PERMANENT : Message.BANNED_MESSAGE;
      }

      CharSequence message = replacePunishmentPlaceholdersWithDefaults(databaseSession, punishment, banScreenMessage, 0L, false, 12, null);
      return replaceDetailPlaceholdersWithDefaults(databaseSession, text, text, text2, text3, text4, new Date(System.currentTimeMillis()), message, false, 128, null);
   }

   public static final String replacePunishmentPlaceholdersFor(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull LiteSender liteSender, @NotNull CharSequence message, long value) {
      CharSequence message2 = replacePunishmentPlaceholdersWithDefaults(databaseSession, punishment, message, value, false, 8, null);
      return replacePlaceholdersWithDefaults(databaseSession, liteSender, message2, false, 4, null);
   }

   public static String replacePunishmentPlaceholdersForWithDefaults(DatabaseSession databaseSession, Punishment punishment, LiteSender liteSender, CharSequence message, long value, int value2, Object obj) {
      if ((value2 & 8) != 0) {
         value = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
      }

      return replacePunishmentPlaceholdersFor(databaseSession, punishment, liteSender, message, value);
   }

   public static final CharSequence replacePunishmentPlaceholders(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @NotNull CharSequence message, long value, boolean flag) {
      if (message.length() == 0) {
         return "";
      }

      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      BroadcastModule module2 = (BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class);
      CharSequence message2 = message;
      boolean isPermanent2 = punishment.isPermanent();
      String bannedByUuid = punishment.getBannedByUuid();
      String text = punishment.getType().name();
      String lowerCase = text.toLowerCase(Locale.ENGLISH);
      String serverScope = punishment.getServerScope();
      String serverOrigin = punishment.getServerOrigin();
      CharSequence formatDate2 = module2.formatDate(punishment.getTime(), false);
      CharSequence removedByReason = punishment.getRemovedByReason();
      if (removedByReason == null) {
         removedByReason = "";
      }

      CharSequence message3 = isPermanent2 ? Message.FOREVER : module2.formatDate(punishment.getUntil(), false);
      long removedByDate = punishment.getRemovedByDate();
      CharSequence message4 = removedByDate <= 0L ? Message.STRINGS_NULL : module2.formatDate(removedByDate, true);
      long value2 = value - punishment.getTime();
      long value3 = punishment.getUntil() - value;
      if (value2 - 999 <= 999L) {
         value3 += value2 + 450;
      }

      if (value3 <= 999L) {
         value3 += 999;
      }

      CharSequence message5 = punishment.isExpiredAt(value) ? Message.EXPIRED : Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value3, false, 2, null);
      String text2 = computeText(message2);
      CharSequence message6 = text2;
      CharSequence[] messages = new CharSequence[]{"duration", message5};
      String text3 = TextUtil.replacePairs(message6, messages);
      CharSequence replacePlaceholders2 = databaseSession.replacePlaceholders(text3, module2.getDisplayName(punishment.getBannedByName(), bannedByUuid), bannedByUuid, flag);
      CharSequence[] messages2 = new CharSequence[]{"removalReason", removedByReason};
      String text4 = TextUtil.replacePairs(replacePlaceholders2, messages2);
      message6 = text4;
      CharSequence[] messages3 = new CharSequence[]{"serverScope", module.formatBoolean(serverScope)};
      message6 = TextUtil.replacePairs(message6, messages3);
      CharSequence[] messages4 = new CharSequence[]{"serverOrigin", module.formatBoolean(serverOrigin)};
      String text5 = TextUtil.replacePairs(message6, messages4);
      message6 = text5;
      CharSequence[] messages5 = new CharSequence[]{"dateStart", formatDate2};
      message6 = TextUtil.replacePairs(message6, messages5);
      CharSequence[] messages6 = new CharSequence[]{"dateEnd", message3};
      message6 = TextUtil.replacePairs(message6, messages6);
      CharSequence[] messages7 = new CharSequence[]{"dateRemoved", message4};
      message6 = TextUtil.replacePairs(message6, messages7);
      CharSequence[] messages8 = new CharSequence[]{"date", formatDate2};
      String text6 = TextUtil.replacePairs(message6, messages8);
      message6 = text6;
      CharSequence[] messages9 = new CharSequence[]{"originalDuration", isPermanent2 ? Message.FOREVER : Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, punishment.getUntil() - punishment.getTime(), false, 2, null)};
      String text7 = TextUtil.replacePairs(message6, messages9);
      message6 = text7;
      CharSequence[] messages10 = new CharSequence[]{"timeSince", Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, value2, false, 2, null)};
      String text8 = TextUtil.replacePairs(message6, messages10);
      String replacePlaceholder2 = replacePlaceholder(text8, "uuid", punishment.getUuid());
      message6 = replacePlaceholder2;
      CharSequence[] messages11 = new CharSequence[]{"permanent", module.formatBoolean(isPermanent2)};
      String text9 = TextUtil.replacePairs(message6, messages11);
      message6 = text9;
      CharSequence[] messages12 = new CharSequence[]{"ipban", module.formatBoolean(punishment.isIpBan())};
      String text10 = TextUtil.replacePairs(message6, messages12);
      message6 = text10;
      CharSequence[] messages13 = new CharSequence[]{"silent", module.formatBoolean(punishment.isSilent())};
      String text11 = TextUtil.replacePairs(message6, messages13);
      message6 = text11;
      CharSequence[] messages14 = new CharSequence[]{"type", lowerCase};
      String text12 = TextUtil.replacePairs(message6, messages14);
      String replaceStatistics2 = replaceStatistics(databaseSession, text12);
      message6 = replaceStatistics2;
      CharSequence[] messages15 = new CharSequence[]{"active", module.formatBoolean(punishment.isActive() && !punishment.isExpiredAt(value))};
      String text13 = TextUtil.replacePairs(message6, messages15);
      String replacePlaceholderLazy2 = replacePlaceholderLazy(text13, "geoip", () -> {
         GeoIpLookup geoIp = module.getGeoIp();
         return geoIp != null ? geoIp.computeText(punishment.getIp()) : null;
      });
      message6 = replacePlaceholderLazy2;
      CharSequence[] messages16 = new CharSequence[]{"reason", punishment.getReason()};
      String text14 = TextUtil.replacePairs(message6, messages16);
      if (flag) {
         text14 = Message.computeText(text14, true);
      }

      String replacePlaceholderLazy3 = replacePlaceholderLazy(text14, "idRandom", () -> module.obtainKotlinData2().computeText(punishment.getId()));
      String replacePlaceholder3 = replacePlaceholder(replacePlaceholderLazy3, "id", punishment.getId());
      return replacePlaceholder3;
   }

   public static CharSequence replacePunishmentPlaceholdersWithDefaults(DatabaseSession databaseSession, Punishment punishment, CharSequence message, long value, boolean flag, int value2, Object obj) {
      if ((value2 & 4) != 0) {
         value = DatabaseModule.getCurrentTimeWithDefaults(databaseSession.getDatabaseModule(), false, 1, null);
      }

      if ((value2 & 8) != 0) {
         flag = true;
      }

      return replacePunishmentPlaceholders(databaseSession, punishment, message, value, flag);
   }

   public static final String replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull LoginRecord loginRecord, @NotNull CharSequence message, boolean flag) {
      return replaceDetailPlaceholders(databaseSession, loginRecord.getName(), loginRecord.getName(), loginRecord.getUuid(), loginRecord.getIp(), null, loginRecord.getDate(), message, flag);
   }

   public static final String replacePlaceholders(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender, @NotNull CharSequence message, boolean flag) {
      String string = message.toString();
      if (containsPlaceholder(string, "player")) {
         if (databaseSession.getPlatform().getPlatformId() == 0) {
            PlatformBridge bridge = databaseSession.getPlatform().createBridge();
            Intrinsics.checkNotNullExpressionValue(bridge, "");
            VaultBridge vault = ((BukkitBridge)bridge).getVault();
            string = replacePlaceholderLazy(replacePlaceholderLazy(string, "playerPrefix", () -> Message.computeText(vault.getPrefix(liteSender.getHandle()), true)), "playerSuffix", () -> Message.computeText(vault.getSuffix(liteSender.getHandle()), true));
         } else {
            Pair luckPermsPrefixSuffix = getLuckPermsPrefixSuffix(databaseSession, liteSender.getUuidString());
            string = replacePlaceholderLazy(replacePlaceholderLazy(string, "playerPrefix", () -> luckPermsPrefixSuffix.getFirst()), "playerSuffix", () -> luckPermsPrefixSuffix.getSecond());
         }
      }

      return replaceDetailPlaceholders(databaseSession, liteSender.getName(), liteSender.getDisplayName(), liteSender.getUuidString(), liteSender.getIpAddress(), liteSender.getServerName(), new java.util.Date(), string, flag);
   }

   public static String replacePlaceholdersWithDefaults(DatabaseSession databaseSession, LiteSender liteSender, CharSequence message, boolean flag, int value, Object obj) {
      if ((value & 4) != 0) {
         flag = true;
      }

      return replacePlaceholders(databaseSession, liteSender, message, flag);
   }

   public static final String replaceDetailPlaceholders(
      @NotNull DatabaseSession databaseSession,
   @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   @Nullable String text4,
   @Nullable String text5,
   @Nullable java.util.Date date,
   @NotNull CharSequence message,
   boolean flag
) {
      ConfigModule module = (ConfigModule)databaseSession.getPlatform().getModule(ConfigModule.class);
      KotlinSerializableImpl kotlinSerializable = new KotlinSerializableImpl();
      String text6 = text5;
      if (text5 == null) {
         text6 = module.getSettings().getServerName();
      }

      kotlinSerializable.obj = text6;
      String string = message.toString();
      if (StringsKt.isBlank(string)) {
         return string;
      }

      String replacePlaceholderLazy2 = replacePlaceholderLazy(string, "geoip", () -> module.getCountry(text4));
      String replacePlaceholder2 = replacePlaceholder(replacePlaceholder(replacePlaceholderLazy2, "name", text), "target", text);
      String replacePlaceholder3 = replacePlaceholder(replacePlaceholder2, "uuid", text3);
      if (containsPlaceholder(replacePlaceholder3, "player")) {
         String text7 = module.getSettings().isUseDisplayNames() ? text2 : text;
         KotlinSerializableImpl kotlinSerializable2 = new KotlinSerializableImpl();
         kotlinSerializable2.obj = "";
         KotlinSerializableImpl kotlinSerializable3 = new KotlinSerializableImpl();
         kotlinSerializable3.obj = "";
         byte platformId = databaseSession.getPlatform().getPlatformId();
         if (text3 != null && TargetParser.isUuid(text3)) {
            if (platformId == 0) {
               PlatformBridge bridge = databaseSession.getPlatform().createBridge();
               Intrinsics.checkNotNullExpressionValue(bridge, "");
               VaultBridge vault = ((BukkitBridge)bridge).getVault();

               try {
                  UUID fromString2 = UUID.fromString(text3);
                  replacePlaceholder3 = replacePlaceholderLazy(replacePlaceholder3, "playerPrefix", () -> vault.getPrefix(fromString2));
                  replacePlaceholder3 = replacePlaceholderLazy(replacePlaceholder3, "playerSuffix", () -> vault.getSuffix(fromString2));
               } catch (IllegalArgumentException e) {
                  ConfigModule.handleConfigModule(module, e, 0, 2, null);
               }
            } else {
               Pair luckPermsPrefixSuffix = getLuckPermsPrefixSuffix(databaseSession, text3);
               kotlinSerializable2.obj = luckPermsPrefixSuffix.getFirst();
               kotlinSerializable3.obj = luckPermsPrefixSuffix.getSecond();
            }
         }

         String replacePlaceholderLazy3 = replacePlaceholderLazy(replacePlaceholder3, "playerPrefix", () -> kotlinSerializable2.obj);
         String replacePlaceholderLazy4 = replacePlaceholderLazy(replacePlaceholderLazy3, "playerSuffix", () -> kotlinSerializable3.obj);
         CharSequence replacePlaceholder4 = replacePlaceholder(replacePlaceholder(replacePlaceholder(replacePlaceholder(replacePlaceholderLazy4, "playerDisplayName", text7), "playerName", text), "playerUUID", text3), "playerIP", text4);
         CharSequence[] messages = new CharSequence[]{"playerServer", (CharSequence)kotlinSerializable.obj};
         String replacePlaceholderLazy5 = replacePlaceholderLazy(TextUtil.replacePairs(replacePlaceholder4, messages), "playerLoginDate", () -> ((BroadcastModule)databaseSession.getPlatform().getModule(BroadcastModule.class)).formatDate(date != null ? date.getTime() : 0L, true));
         String replacePlaceholderLazy6 = replacePlaceholderLazy(
            replacePlaceholderLazy(replacePlaceholderLazy(replacePlaceholderLazy(replacePlaceholderLazy5, "playerBans", () -> databaseSession.countEntries(HistoryQueryFactory.createQueryWithDefaults(HistoryQuery.HISTORY_QUERY_FACTORY, databaseSession, text3, (String)kotlinSerializable.obj, null, false, null, false, null, null, 252, null), PunishmentType.BAN)), "playerMutes", () -> databaseSession.countEntries(HistoryQueryFactory.createQueryWithDefaults(HistoryQuery.HISTORY_QUERY_FACTORY, databaseSession, text3, (String)kotlinSerializable.obj, null, false, null, false, null, null, 252, null), PunishmentType.MUTE)), "playerWarnings", () -> databaseSession.countEntries(HistoryQueryFactory.createQueryWithDefaults(HistoryQuery.HISTORY_QUERY_FACTORY, databaseSession, text3, (String)kotlinSerializable.obj, null, false, null, false, null, null, 252, null), PunishmentType.WARN)),
            "playerKicks",
            () -> databaseSession.countEntries(HistoryQueryFactory.createQueryWithDefaults(HistoryQuery.HISTORY_QUERY_FACTORY, databaseSession, text3, (String)kotlinSerializable.obj, null, false, null, false, null, null, 252, null), PunishmentType.KICK)
         );
         replacePlaceholder3 = replacePlaceholder(replacePlaceholderLazy6, "player", text7);
      }

      return !flag ? databaseSession.replacePlaceholders(replacePlaceholder3).toString() : Message.computeText(databaseSession.replacePlaceholders(replacePlaceholder3), true);
   }

   public static String replaceDetailPlaceholdersWithDefaults(
      DatabaseSession databaseSession, String text, String text2, String text3, String text4, String text5, java.util.Date date, CharSequence message, boolean flag, int value, Object obj
   ) {
      if ((value & 128) != 0) {
         flag = true;
      }

      return replaceDetailPlaceholders(databaseSession, text, text2, text3, text4, text5, date, message, flag);
   }

   public static final String replaceStatistics(@NotNull DatabaseSession databaseSession, @NotNull CharSequence message) {
      String string = message.toString();

      try {
         string = replacePlaceholderLazy(
            replacePlaceholderLazy(
               replacePlaceholderLazy(
                  replacePlaceholderLazy(replacePlaceholderLazy(replacePlaceholderLazy(replacePlaceholderLazy(string, "activeBans", () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.BANS, (String) null, true, 2, null)), "activeMutes", () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.MUTES, (String) null, true, 2, null)), "activeWarnings", () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.WARNINGS, (String) null, true, 2, null)), "totalBans", () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.BANS, (String) null, false, 6, null)),
                  "totalMutes",
                  () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.MUTES, (String) null, false, 6, null)
               ),
               "totalWarnings",
               () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.WARNINGS, (String) null, false, 6, null)
            ),
            "totalKicks",
            () -> PunishmentQueries.countEntriesWithDefaults(databaseSession, PunishmentTable.KICKS, (String) null, false, 6, null)
         );
      } catch (Exception e) {
         DatabaseModule module = (DatabaseModule)databaseSession.getPlatform().getModule(DatabaseModule.class);
         module.logConnectionError(e);
      }

      return string;
   }

   public static final String removeExecutorPlaceholder(@NotNull CharSequence message) {
      CharSequence message2 = "executor";
      CharSequence message3 = "" + '$' + message2;
      CharSequence[] messages = new CharSequence[]{"bannerDisplayName", message3};
      CharSequence message4 = TextUtil.replacePairs(message, messages);
      CharSequence[] messages2 = new CharSequence[]{"banningPlayer", message3};
      message4 = TextUtil.replacePairs(message4, messages2);
      CharSequence[] messages3 = new CharSequence[]{"bannerName", message3};
      message4 = TextUtil.replacePairs(message4, messages3);
      CharSequence[] messages4 = new CharSequence[]{"bannedBy", message3};
      message4 = TextUtil.replacePairs(message4, messages4);
      CharSequence[] messages5 = new CharSequence[]{"mutedBy", message3};
      message4 = TextUtil.replacePairs(message4, messages5);
      CharSequence[] messages6 = new CharSequence[]{"warnedBy", message3};
      message4 = TextUtil.replacePairs(message4, messages6);
      CharSequence[] messages7 = new CharSequence[]{"kickedBy", message3};
      message4 = TextUtil.replacePairs(message4, messages7);
      CharSequence[] messages8 = new CharSequence[]{"kicker", message3};
      message4 = TextUtil.replacePairs(message4, messages8);
      CharSequence[] messages9 = new CharSequence[]{"displayName", message3};
      message4 = TextUtil.replacePairs(message4, messages9);
      CharSequence[] messages10 = new CharSequence[]{"bannedFrom", null};
      CharSequence message5 = "dateStart";
      messages10[1] = "" + '$' + message5;
      message4 = TextUtil.replacePairs(message4, messages10);
      CharSequence[] messages11 = new CharSequence[]{"bannedUntil", null};
      CharSequence message6 = "dateEnd";
      messages11[1] = "" + '$' + message6;
      message4 = TextUtil.replacePairs(message4, messages11);
      CharSequence[] messages12 = new CharSequence[]{"banTimeLeft", null};
      CharSequence message7 = "duration";
      messages12[1] = "" + '$' + message7;
      String text = TextUtil.replacePairs(message4, messages12);
      Iterable iterable = PunishmentType.PUNISHMENT_TYPE_COMPANION.obtainList();
      var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

      for (Object obj : iterable) {
         PunishmentType punishmentType = (PunishmentType)obj;
         String text2;
         switch (PunishmentTypeSwitchMap32.value[punishmentType.obtainPunishmentType().ordinal()]) {
            case 1:
               text2 = "banned";
               break;
            case 2:
               text2 = "muted";
               break;
            case 3:
               text2 = "warned";
               break;
            case 4:
               text2 = "kicked";
               break;
            default:
               StandardKt.notImplemented();
               throw new CommandAbortException();
         }

         arrayList.add(text2);
      }


      for (Object obj2 : arrayList) {
         String string = (String)obj2;
         message4 = text;
         CharSequence[] messages13 = new CharSequence[]{string + "Player", null};
         CharSequence message8 = "playerDisplayName";
         messages13[1] = "" + '$' + message8;
         String text3 = TextUtil.replacePairs(message4, messages13);
         message4 = text3;
         CharSequence[] messages14 = new CharSequence[]{string + "IP", null};
         CharSequence message9 = "playerDisplayName";
         messages14[1] = "" + '$' + message9;
         text = TextUtil.replacePairs(message4, messages14);
      }

      return text;
   }

   public static final String computeText(@NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{"base", Message.c5_f};
      CharSequence message2 = TextUtil.replacePairs(message, messages);
      CharSequence[] messages2 = new CharSequence[]{"appealMessage", Message.BANNED_MESSAGE_APPEAL};
      return TextUtil.replacePairs(message2, messages2);
   }

   public static final CharSequence placeholder(@NotNull CharSequence message) {
      return "" + '$' + message;
   }

   public static final boolean containsPlaceholder(@NotNull CharSequence message, @NotNull CharSequence message2) {
      CharSequence message3 = message2;
      return StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)("" + '$' + message3), false, 2, null);
   }

   public static final boolean containsText(@NotNull CharSequence message, @NotNull CharSequence message2) {
      return StringsKt.containsWithDefaults((CharSequence)message, (CharSequence)message2, false, 2, null) && containsPlaceholder(message, message2);
   }

}
