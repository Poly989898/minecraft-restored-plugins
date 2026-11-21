package litebans;

import java.io.Closeable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BroadcastModule extends PluginModule {
   public static final BroadcastModuleCompanion BROADCAST_MODULE_COMPANION = new BroadcastModuleCompanion(null);
   private final Lazy databaseModuleLazy = KotlinStub5.computeLazy(() -> (DatabaseModule)this.platform.getModule(DatabaseModule.class));
   private final Lazy configModuleLazy = KotlinStub5.computeLazy(() -> (ConfigModule)this.platform.getModule(ConfigModule.class));
   public CharSequence sqlFalse;
   public CharSequence sqlTrue;
   public CharSequence sqlLikeOperator;
   public CharSequence historyColumns;
   public CharSequence collation;
   private LockdownHandler lockdownHandler;
   private final Set pendingUnbans = Collections.newSetFromMap(new ConcurrentHashMap());
   private final Lazy loginHandlerLazy = KotlinStub5.computeLazy(() -> this.createLoginHandler(this.platform));
   public static final int luckPerms = 512;

   public BroadcastModule(@Nullable Platform platform) {
      super(platform);
   }

   public final DatabaseModule getDatabaseModule() {
      Lazy lazy = this.databaseModuleLazy;
      return (DatabaseModule)lazy.getValue();
   }

   public final ConfigModule getConfigModule() {
      Lazy lazy = this.configModuleLazy;
      return (ConfigModule)lazy.getValue();
   }

   public final void setSqlFalse(@NotNull CharSequence message) {
      this.sqlFalse = message;
   }

   public final CharSequence getSqlTrue() {
      if (this.sqlTrue != null) {
         return this.sqlTrue;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setSqlTrue(@NotNull CharSequence message) {
      this.sqlTrue = message;
   }

   public final CharSequence getSqlLikeOperator() {
      if (this.sqlLikeOperator != null) {
         return this.sqlLikeOperator;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setSqlLikeOperator(@NotNull CharSequence message) {
      this.sqlLikeOperator = message;
   }

   public final CharSequence getHistoryColumns() {
      if (this.historyColumns != null) {
         return this.historyColumns;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setHistoryColumns(@NotNull CharSequence message) {
      this.historyColumns = message;
   }

   public final CharSequence getCollation() {
      if (this.collation != null) {
         return this.collation;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setCollation(@NotNull CharSequence message) {
      this.collation = message;
   }

   public final LockdownHandler getLockdownHandler() {
      return this.lockdownHandler;
   }

   public final Set getPendingUnbans() {
      return this.pendingUnbans;
   }

   public final LoginHandler getLoginHandler() {
      Lazy lazy = this.loginHandlerLazy;
      return (LoginHandler)lazy.getValue();
   }

   public final LoginHandler createLoginHandler(@NotNull Platform platform) {
      byte platformId = platform.getPlatformId();
      LoginHandler loginHandler;
      if (platformId == 0 || platformId == 3) {
         loginHandler = new BroadcastLoginHandler(platform);
      } else if (platformId == 1) {
         loginHandler = new BungeeLoginHandler(platform);
      } else {
         if (platformId != 2) {
            StandardKt.notImplemented();
            throw new CommandAbortException();
         }

         loginHandler = new LoginHandler(platform);
      }

      return loginHandler;
   }

   public final void broadcast(
      @NotNull DatabaseSession databaseSession, @NotNull CharSequence message, @NotNull SyncMessageType syncMessageType, @Nullable PunishmentType punishmentType, @Nullable String text, boolean flag, boolean flag2, @Nullable LiteSender liteSender
   ) {
      this.broadcast(databaseSession, message, syncMessageType.obtainText(), punishmentType, text, flag, flag2, liteSender);
   }

   public static void handleBroadcastModule(BroadcastModule broadcastModule, DatabaseSession databaseSession, CharSequence message, SyncMessageType syncMessageType, PunishmentType punishmentType, String text, boolean flag, boolean flag2, LiteSender liteSender, int value, Object obj) {
      if ((value & 4) != 0) {
         punishmentType = null;
      }

      if ((value & 8) != 0) {
         text = null;
      }

      if ((value & 16) != 0) {
         flag = true;
      }

      if ((value & 32) != 0) {
         flag2 = true;
      }

      if ((value & 64) != 0) {
         liteSender = null;
      }

      broadcastModule.broadcast(databaseSession, message, syncMessageType, punishmentType, text, flag, flag2, liteSender);
   }

   public final void broadcast(
      @NotNull DatabaseSession databaseSession,
   @NotNull CharSequence message,
   @Nullable String text,
   @Nullable PunishmentType punishmentType,
   @Nullable String text2,
   boolean flag,
   boolean flag2,
   @Nullable LiteSender liteSender
   ) {
      Settings settings = this.getConfigModule().getSettings();
      if (!settings.isLocalMessages() && flag2) {
         ConfigModule configModule = this.getConfigModule();
         byte b = 10;
         if (configModule.isDebugAtLeast(b)) {
            configModule.logDebug("Local messages are disabled, discarding message: \"" + message + '"');
         }
      } else if (flag) {
         if (settings.isNotifyNotifyConsole()) {
            if (databaseSession.getPlatform().getPlatformId() == 3) {
               databaseSession.getPlatform().getLogger().info(message.toString());
            } else {
               CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, databaseSession.getPlatform().getConsole(), message, null, 4, null);
            }
         }

         if (liteSender != null && !liteSender.isConsole()) {
            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, message, null, 4, null);
         }

         KotlinHelper29.KOTLIN_HELPER.handleDatabaseSession(databaseSession, databaseSession.getPlatform(), message, text);
         if (!Intrinsics.areEqual("console", text)) {
            LiteSender[] onlineSenders = databaseSession.getPlatform().getOnlineSenders();
            if (text == null) {
               Iterator iterator = KotlinUtil12.computeIterator(onlineSenders);

               while (iterator.hasNext()) {
                  LiteSender liteSender2 = (LiteSender)iterator.next();
                  CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
                  Intrinsics.checkNotNull(liteSender2);
                  CommandOutput.sendWithDefaults(commandOutput, liteSender2, message, null, 4, null);
               }
            } else {
               Iterator iterator2 = KotlinUtil12.computeIterator(onlineSenders);

               while (iterator2.hasNext()) {
                  LiteSender liteSender3 = (LiteSender)iterator2.next();
                  if (!Intrinsics.areEqual(liteSender3, liteSender)) {
                     Intrinsics.checkNotNull(liteSender3);
                     if (this.canSeeNotification(liteSender3, text, punishmentType, text2)) {
                        CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender3, message, null, 4, null);
                     }
                  }
               }
            }
         }
      }
   }

   public static void handleBroadcastModule(BroadcastModule broadcastModule, DatabaseSession databaseSession, CharSequence message, String text, PunishmentType punishmentType, String text2, boolean flag, boolean flag2, LiteSender liteSender, int value, Object obj) {
      if ((value & 4) != 0) {
         punishmentType = null;
      }

      if ((value & 8) != 0) {
         text2 = null;
      }

      if ((value & 16) != 0) {
         flag = true;
      }

      if ((value & 32) != 0) {
         flag2 = true;
      }

      if ((value & 64) != 0) {
         liteSender = null;
      }

      broadcastModule.broadcast(databaseSession, message, text, punishmentType, text2, flag, flag2, liteSender);
   }

   public final boolean canSeeNotification(@NotNull LiteSender liteSender, @NotNull String text, @Nullable PunishmentType punishmentType, @Nullable String text2) {
      LiteSender liteSender2 = liteSender;
      String[] texts = new String[]{"litebans.notify", "litebans.notify." + text};
      int value = 0;
      int length2 = texts.length;

      boolean flag;
      while (true) {
         if (value >= length2) {
            flag = false;
            break;
         }

         String text3 = texts[value];
         if (liteSender2.hasPermission(text3)) {
            flag = true;
            break;
         }

         value++;
      }

      if (flag) {
         return true;
      }

      if (punishmentType != null && text2 != null) {
         String serverName = this.getDatabaseModule().getServerName();
         String serverName2 = liteSender.getServerName();
         if (serverName2 == null) {
            serverName2 = serverName;
         }

         LiteSender liteSender3 = liteSender;
         String text4 = "litebans.notify." + text + '.' + text2 + '.' + punishmentType;
         if (!liteSender3.hasPermission(text4)) {
            if (Intrinsics.areEqual(serverName2, text2)) {
               LiteSender liteSender4 = liteSender;
               String text5 = "litebans.notify." + text + ".local." + punishmentType;
               if (liteSender4.hasPermission(text5)) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public final boolean removePendingUnban(@NotNull Punishment punishment, @NotNull String text) {
      if (!this.pendingUnbans.isEmpty()) {
         String text2 = text;
         if (this.pendingUnbans.remove(text2.toLowerCase(Locale.ROOT))) {
            LiteSender console = this.platform.getConsole();
            this.getDatabaseModule().handleFunction(obj -> {
               PunishmentQueries.handleDatabaseSession(((DatabaseSession) obj), punishment, console.getUuidString(), console.getName(), null, false, 24, null);
               this.isBungeeBan(punishment);
               return Unit.UNIT;
            });
            this.platform.getLogger().info("Unbanned player: " + text + '.');
            return true;
         }
      }

      return false;
   }

   public final void setDatabaseType(@NotNull String text) {
      if (Intrinsics.areEqual(text, "pgsql") || Intrinsics.areEqual(text, "postgresql")) {
         this.setSqlFalse("B'0'");
         this.setSqlTrue("B'1'");
         this.setSqlLikeOperator(" ILIKE ");
      } else {
         this.setSqlFalse("0");
         this.setSqlTrue("1");
         this.setSqlLikeOperator("=");
      }

      if (Intrinsics.areEqual(text, "sqlite")) {
         this.setHistoryColumns("name,uuid,ip,datetime(date) as date");
         this.setCollation(" COLLATE NOCASE ");
      } else if (DatabaseModule.DATABASE_TYPE.checkText(text)) {
         this.setHistoryColumns("name,uuid,ip,UNIX_TIMESTAMP(date) as date");
         this.setCollation(" ");
      } else {
         this.setHistoryColumns("*");
         this.setCollation(" ");
      }
   }

   @Override
   public void disable() {
      if (this.lockdownHandler != null) {
         LockdownHandler lockdownHandler = this.lockdownHandler;
         Intrinsics.checkNotNull(this.lockdownHandler);
         lockdownHandler.perform3();
      }
   }

   public final LiteSender findOnlineSender(@NotNull String text) {
      Object[] onlineSenders = this.platform.getOnlineSenders();
      int value = 0;
      int length2 = onlineSenders.length;

      Object obj;
      while (true) {
         if (value >= length2) {
            obj = null;
            break;
         }

         Object obj2 = onlineSenders[value];
         LiteSender liteSender = (LiteSender)obj2;
         if (Intrinsics.areEqual(liteSender.getUuidString(), text)) {
            obj = obj2;
            break;
         }

         value++;
      }

      return (LiteSender)obj;
   }

   public final CharSequence formatDate(long value, boolean flag) {
      Pair dateFormat = this.getDateFormat();
      return value == -1L ? Message.FOREVER : ((SimpleDateFormat)dateFormat.getSecond()).format(new Date(value + this.getTimezoneOffset(flag)));
   }

   private final Pair getDateFormat() {
      Pair pair = (Pair)this.getConfigModule().getThreadLocal().get();
      if (!Intrinsics.areEqual(pair.getFirst(), Message.DATE_FORMAT.toString())) {
         this.getConfigModule().getThreadLocal().remove();
         pair = (Pair)this.getConfigModule().getThreadLocal().get();
      }

      Pair pair2 = pair;
      Intrinsics.checkNotNull(pair2);
      return pair2;
   }

   public final java.util.Date computeDate(@NotNull ResultSet resultSet, @NotNull String text, boolean flag, @Nullable String text2) {
      try {
         if (!this.isSqliteResult(resultSet)) {
            String text3 = text2;
            if (text2 == null) {
               text3 = this.getConfigModule().getSettings().getSqlDriver();
            }

            if (DatabaseModule.DATABASE_TYPE.checkText(text3) && flag) {
               return new Date(resultSet.getLong(text) * 1000L);
            }

            Calendar instance = Calendar.getInstance();
            instance.setTimeZone(TimeZone.getTimeZone("UTC"));
            Timestamp timestamp = resultSet.getTimestamp(text);
            if (timestamp == null) {
               return new java.util.Date();
            }

            instance.setTime(timestamp);
            return instance.getTime();
         } else {
            java.util.Date date;
            try {
               String string = resultSet.getString(text);
               if (string == null || string.length() == 0) {
                  return new java.util.Date();
               }

               date = ((SimpleDateFormat)DateFormats.CONNECTION_UTIL.obtainThreadLocal().get()).parse(string);
            } catch (Exception e2) {
               this.getConfigModule().handleObj(e2, 0);
               date = new java.util.Date();
            }

            return date;
         }
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   public final String getRemovedByName(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment) {
      String removedByUuid = punishment.getRemovedByUuid();
      if (removedByUuid == null) {
         return null;
      }

      if (!Intrinsics.areEqual(removedByUuid, "[Console]") && !Intrinsics.areEqual(removedByUuid, "CONSOLE")) {
         String removedByName = punishment.getRemovedByName();
         if (this.getConfigModule().getSettings().isUseDisplayNames() && removedByName != null) {
            return removedByName;
         }

         LoginRecord loginRecord = databaseSession.findLoginRecord(removedByUuid);
         return loginRecord != null ? loginRecord.getName() : null;
      } else {
         return this.getConfigModule().getSettings().getConsoleSenderName();
      }
   }

   public final long getTimezoneOffset(boolean flag) {
      if ((flag || !this.getConfigModule().getSettings().isUseDatabaseTime()) && this.getConfigModule().getSettings().isUseTimezone()) {
         Calendar instance = Calendar.getInstance();
         return instance.get(15) + instance.get(16);
      } else {
         return 0L;
      }
   }

   public final void handleText(@Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, boolean flag) {
      Intrinsics.checkNotNull(text3);
      String normalize2 = TargetParser.normalize(text3);
      Settings settings = this.getConfigModule().getSettings();
      PunishmentLookupModule module = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
      boolean flag2 = !this.getConfigModule().isAuthMeSupported() && !this.platform.obtainFlag4() && !settings.isIpHistoryIgnored();
      boolean flag3 = flag2 || flag && this.platform.obtainFlag4() && this.platform.isOnlineMode();
      if (flag3) {
         Platform platform = this.platform;
         Intrinsics.checkNotNull(text);
         new QueueCommandTask(platform, text, normalize2, text2).run();
      }

      if (!flag) {
         Platform platform2 = module.platform;
         Intrinsics.checkNotNull(normalize2);
         new ActiveMuteNoticeTask(platform2, text, normalize2, text2, text4).run();
         handleBroadcastModule(this, normalize2, text, 0, 4, null);
      }
   }

   private final void handleText(String text, String text2, int value) {
      this.platform.runAsyncLater(() -> {
         try {
            LiteSender onlineSender = this.findOnlineSender(text);
            if (onlineSender == null) {
               if (value < 5) {
                  this.handleText(text, text2, value + 1);
               }

               return;
            }

            if (!this.platform.obtainFlag4()) {
               String text3 = "litebans.exempt.dupeip_join";
               if (!onlineSender.hasPermission(text3)) {
                  EntryQueueModule module = (EntryQueueModule)this.platform.getModule(EntryQueueModule.class);
                  String ipAddress = onlineSender.getIpAddress();
                  if (ipAddress != null) {
                     module.handleText(text2, onlineSender.getUuidString(), ipAddress);
                  }
               }
            }

            String text4 = "litebans.notify.warned.offline";
            if (onlineSender.hasPermission(text4)) {
               DatabaseModule databaseModule = this.getDatabaseModule();

               try {
                  DatabaseSession session = databaseModule.openSession();
                  Throwable error = null;

                  try {
                     Iterable history = session.getHistory(onlineSender, PunishmentType.WARN, true, 512);
                     var arrayList = new ArrayList();

                     for (Object obj : history) {
                        Punishment punishment = (Punishment)obj;
                        if (!punishment.isWarned()) {
                           arrayList.add(obj);
                        }
                     }

                     List list = arrayList;
                     if (!list.isEmpty()) {
                        session.markWarned(text);
                        CharSequence replacePlaceholders2 = session.replacePlaceholders(Message.WARNED_JOIN, onlineSender);
                        CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, onlineSender, replacePlaceholders2, null, 4, null);

                        for (Punishment punishment2 : (Iterable<Punishment>) list) {
                           CharSequence banScreenMessage = Placeholders.getBanScreenMessage(session, punishment2, -1, -1);
                           if (banScreenMessage == null) {
                              banScreenMessage = Message.az_f;
                           }

                           CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, onlineSender, Placeholders.replacePunishmentPlaceholdersForWithDefaults(session, punishment2, onlineSender, banScreenMessage, 0L, 8, null), null, 4, null);
                        }

                        int value2 = list.size();
                        Logger logger = session.getPlatform().getLogger();
                        String text5 = "Notified %s of %d %s.";
                        Object[] objs = new Object[]{onlineSender.getName(), value2, value2 == 1 ? "warning" : "warnings"};
                        logger.info(String.format(text5, Arrays.copyOf(objs, objs.length)));
                     }

                  } catch (Throwable e) {
                     error = e;
                     throw SneakyThrow.<RuntimeException>rethrow(e);
                  } finally {
                     CloseableKt.handleCloseable(session, error);
                  }
               } catch (Throwable e2) {
                  if (!(e2 instanceof SQLException)) {
                     throw SneakyThrow.<RuntimeException>rethrow(e2);
                  }
                  SQLException sqlException = (SQLException) e2;
                  if (!databaseModule.isConnectionFailure(sqlException)) {
                     throw sqlException;
                  }
               }
            }

            String text6 = "litebans.admin";
            if (onlineSender.hasPermission(text6)) {
               ConfigFile configFile = this.platform.getConfigFile();
               if (configFile.getError() != null) {
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, onlineSender, Message.RELOAD_FAIL_CONFIG, null, 4, null);
               }

               if (this.getConfigModule().getTemplateManager().getConfig() == null) {
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, onlineSender, Message.RELOAD_FAIL_TEMPLATES, null, 4, null);
               }

               ConfigFile configFile2 = this.getConfigModule().getMessages().getConfigFile();
               if ((configFile2 != null ? configFile2.getError() : null) != null) {
                  CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, onlineSender, Message.RELOAD_FAIL_MESSAGES, null, 4, null);
               }
            }
         } catch (Exception e3) {
            this.getDatabaseModule().logConnectionError(e3);
         }
      }, 30L * value * value);
   }

   static void handleBroadcastModule(BroadcastModule broadcastModule, String text, String text2, int value, int value2, Object obj) {
      if ((value2 & 4) != 0) {
         value = 1;
      }

      broadcastModule.handleText(text, text2, value);
   }

   @Override
   public void enable() {
      this.getConfigModule().enable();
      if (this.lockdownHandler == null) {
         this.handleText("", null, false);
      }
   }

   public final Object getLuckPerms() {
      return this.platform.createBridge().hasPlugin("LuckPerms") ? LiteHelper.LITE_HELPER.getLuckPerms() : null;
   }

   public final boolean isSqliteResult(@Nullable Object obj) {
      return obj instanceof ResultSetWrapper
         ? StringsKt.containsWithDefaults((CharSequence)((ResultSetWrapper)obj).toString(), (CharSequence)"org.sqlite", false, 2, null)
         : obj != null && StringsKt.checkText(obj.getClass().getName(), "org.sqlite", false, 2, null);
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull PunishmentRequest punishmentRequest) {
      Punishment punishment = punishmentRequest.getPunishment();
      Intrinsics.checkNotNull(punishment);
      LoginRecord targetRecord = punishmentRequest.getTargetRecord();
      this.handleDatabaseSession(databaseSession, punishment, targetRecord != null ? targetRecord.getName() : null);
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Punishment punishment, @Nullable String text) {
      if (databaseSession.getPlatform().getPlatformId() == 1 && punishment.getType() == PunishmentType.BAN) {
         String uuid = punishment.getUuid();
         if (TargetParser.isUuid(uuid)) {
            LoginHandler loginHandler = this.getLoginHandler();
            Intrinsics.checkNotNullExpressionValue(loginHandler, "");
            BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
            String text2 = text;
            if (text == null) {
               LoginRecord loginRecord = databaseSession.findLoginRecord(punishment.getUuid());
               text2 = loginRecord != null ? loginRecord.getName() : null;
               if (text2 == null) {
                  text2 = "";
               }
            }

            UUID fromString2 = UUID.fromString(punishment.getUuid());
            String ip = punishment.getIp();
            String serverScope = punishment.getServerScope();
            if (serverScope == null) {
               serverScope = ServerScopes.SERVER_SCOPE.getLocalScope();
            }

            bungeeLoginHandler.handleDatabaseSession(databaseSession, text2, fromString2, ip, serverScope, CollectionsKt.computeList(punishment));
         }
      }
   }

   public static void handleBroadcastModule(BroadcastModule broadcastModule, DatabaseSession databaseSession, Punishment punishment, String text, int value, Object obj) {
      if ((value & 2) != 0) {
         text = null;
      }

      broadcastModule.handleDatabaseSession(databaseSession, punishment, text);
   }

   public final void checkBanOnJoin(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender) {
      List history = databaseSession.getHistory(liteSender.getUuidString(), liteSender.getIpAddress(), PunishmentType.BAN, "__ALL__", true, 0);
      LoginHandler loginHandler = this.getLoginHandler();
      Intrinsics.checkNotNullExpressionValue(loginHandler, "");
      BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
      UUID uuid = liteSender.getUuid();
      int mostSignificantBits = (int)uuid.getMostSignificantBits();
      Integer boxed = mostSignificantBits;
      bungeeLoginHandler.getConcurrentHashMap2().put(boxed, history);
   }

   public final boolean isBungeeBan(@Nullable Punishment punishment) {
      if ((punishment != null ? punishment.getType() : null) == PunishmentType.BAN) {
         String uuid = punishment.getUuid();
         if (TargetParser.isUuid(uuid) && this.platform.getPlatformId() == 1) {
            LoginHandler loginHandler = this.getLoginHandler();
            Intrinsics.checkNotNullExpressionValue(loginHandler, "");
            BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
            String uuid2 = punishment.getUuid();
            Intrinsics.checkNotNull(uuid2);
            int value = bungeeLoginHandler.computeIntValue(uuid2);
            Collection collection = (Collection)bungeeLoginHandler.getConcurrentHashMap2().get(value);
            if (collection == null) {
               return false;
            }

            Map map = bungeeLoginHandler.getConcurrentHashMap2();
            Integer boxed = value;
            Iterable iterable = collection;
            var arrayList = new ArrayList();

            for (Object obj : iterable) {
               Punishment punishment2 = (Punishment)obj;
               if (punishment2.getId() != punishment.getId()) {
                  arrayList.add(obj);
               }
            }

            List list = arrayList;
            map.put(boxed, list);
            return true;
         }
      }

      return false;
   }

   public final void sendPluginMessage(@NotNull LiteSender liteSender, @Nullable String text) {
      PlatformBridge bridge = this.platform.createBridge();
      if (bridge instanceof BukkitBridge && this.getConfigModule().getSettings().isBungeecord()) {
         BukkitBridge bukkitBridge = (BukkitBridge)bridge;
         Intrinsics.checkNotNull(text);
         bukkitBridge.connectToServer(liteSender, text);
      } else {
         Intrinsics.checkNotNull(text);
         liteSender.disconnect(text);
      }
   }

   public final void sendMessageSync(@NotNull LiteSender liteSender, @NotNull CharSequence message) {
      this.platform.runSync(() -> {
         this.sendPluginMessage(liteSender, message.toString());
      });
   }

   public final void handleText(@NotNull String text, @Nullable CharSequence message, boolean flag) {
      if (this.lockdownHandler != null) {
         LockdownHandler lockdownHandler = this.lockdownHandler;
         Intrinsics.checkNotNull(this.lockdownHandler);
         lockdownHandler.perform3();
      }

      this.lockdownHandler = new LockdownHandler(this.platform, message, text, flag);
      LockdownHandler lockdownHandler2 = this.lockdownHandler;
      Intrinsics.checkNotNull(this.lockdownHandler);
      lockdownHandler2.perform2();
   }

   public final void handleText2(@NotNull String text, @NotNull CharSequence message, boolean flag) {
      new BannedJoinBroadcastTask(this.platform, text, message, flag).run();
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull LiteSender liteSender, @NotNull Punishment punishment, int value, @NotNull String text) {
      if (this.getConfigModule().getSettings().isLocalMessages()) {
         LiteSender liteSender2 = liteSender;
         String text2 = "litebans.notify." + text;
         if (liteSender2.hasPermission(text2)) {
            Message message;
            switch (BroadcastModuleSwitchMap.value[punishment.getType().ordinal()]) {
               case 1:
                  message = Message.BANNED_MESSAGE;
                  break;
               case 2:
                  message = punishment.isPermanent() ? Message.MESSAGE_PERMANENT : Message.MUTE_MESSAGE;
                  break;
               default:
                  message = Message.WARN_MESSAGE;
            }

            Message message2 = message;
            message = (Message) Placeholders.getBanScreenMessageWithDefaults(databaseSession, punishment, value, 0, 4, null);
            if (message == null) {
               message = message2;
            }

            CharSequence message3 = message;
            if (punishment.getType() == PunishmentType.WARN) {
               message3 = Placeholders.replacePlaceholder(message3, "duration", this.getConfigModule().getSettings().getWarningsExpireAfter());
            }

            CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, liteSender, Placeholders.replacePunishmentPlaceholdersForWithDefaults(databaseSession, punishment, liteSender, message3, 0L, 8, null), null, 4, null);
         }
      }
   }

   public final String getDisplayName(@Nullable String text, @Nullable String text2) {
      if (this.getConfigModule().getSettings().isUseDisplayNames() && text2 != null) {
         LiteSender onlineSender = this.findOnlineSender(text2);
         if (onlineSender != null) {
            return onlineSender.getDisplayName();
         }
      }

      return text;
   }

}
