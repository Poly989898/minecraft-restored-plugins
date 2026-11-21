package litebans;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;

public final class Settings {
   private final Platform platform;
   private final ConfigFile config;
   private String serverUuid;
   private String serverName;
   private String sqlDriver;
   private final String sqlAddress;
   private final String sqlUsername;
   private final String sqlPassword;
   private final String sqlDatabase;
   private final String sqlTablePrefix;
   private final String sqlEngine;
   private final String sqlOptions;
   private final String[] sqlDrivers;
   private final int sqlPoolMinConnections;
   private final int sqlPoolMaxConnections;
   private final long sqlPoolKeepalive;
   private final long sqlPoolTimeout;
   private final long sqlPoolIdleTimeout;
   private final long sqlPoolMaxLifetime;
   private List mutesCommandPrefixes;
   private final Set mutesCommandBlacklist;
   private final boolean warningsEnabled;
   private final String[] warningsActions;
   private final boolean warningsActionsExecuteAsConsole;
   private final boolean warningsDeleteWarningsAfterAction;
   private final String warningsExpireAfter;
   private final boolean exemptEnable;
   private final boolean exemptUseGroupWeights;
   private final boolean exemptPermitSameWeight;
   private final String exemptPermissionWorld;
   private final String[] exemptExemptGroups;
   private final String[] exemptExemptPlayers;
   private final boolean importImportIpbans;
   private final boolean importSqlite;
   private final String importSqliteFile;
   private final String importFrom;
   private final String importImportMysqlAddress;
   private final String importImportMysqlDatabase;
   private final String importImportMysqlUsername;
   private final String importImportMysqlPassword;
   private final String importImportTablePrefix;
   private final String defaultServerScope;
   private final String consoleSenderName;
   private byte byteValue;
   private final byte ipreportMinimumAccounts;
   private final int banlistBansPerPage;
   private final int notificationThrottleTicks;
   private final int offlineTabSize;
   private final long durationsConsoleCooldown;
   private final long durationsEffectiveIpbanDuration;
   private final long securityObscureSeed;
   private final long notifyDupeipScanFilterDuration;
   private final int notifyDupeipScanLimit;
   private final int notifyDupeipOnJoinThreshold;
   private final String notifyNotifyDiscord;
   private final boolean notifyBannedPlayerJoin;
   private final boolean notifyMutedPlayerChat;
   private final boolean notifyDupeipOnJoin;
   private final boolean notifyDupeipOnBannedAccount;
   private final boolean notifyDupeipOnMutedAccount;
   private final boolean notifyDupeipShowMutedAccounts;
   private final boolean notifyNotifyConsole;
   private final boolean bungeecord;
   private final boolean onlineMode;
   private final boolean punishIp;
   private final boolean silent;
   private final boolean mutesPreventBypass;
   private final boolean mutesEnabled;
   private final boolean lockdownEnabled;
   private final boolean lockdownPersistEnabled;
   private final boolean supportAuthme;
   private final boolean autocompleteOnlinePlayerNames;
   private final boolean dupeipScanAllIps;
   private final boolean deletePreviousHistory;
   private final boolean unbanAllHistory;
   private final boolean banlistShowActiveOnly;
   private final boolean useDisplayNames;
   private final boolean banNames;
   private final boolean requireKickReason;
   private final boolean requireBanMuteReason;
   private final boolean requireUnbanUnmuteReason;
   private final boolean useDatabaseTime;
   private final boolean durationsReduceToLimit;
   private final boolean overrideLowerDuration;
   private final boolean sync;
   private final boolean syncBroadcasts;
   private final boolean syncNotifications;
   private final boolean syncDupeipNotifications;
   private final boolean localMessages;
   private final boolean updateCheck;
   private final boolean useProtocollib;
   private final boolean useTimezone;
   private final boolean securityDupeip;
   private final boolean securityStaffrollback;
   private final boolean securityIphistory;
   private final boolean securityIpban;
   private final byte securityIpbanCommand;
   private final boolean securityModify;
   private final boolean securityPrunehistory;
   private final boolean securitySenderCustom;
   private final boolean allowExemptBypass;
   private final boolean suppressGetAddressWarning;
   private final boolean disableLoginBanCheck;
   private final boolean disableWildcardBans;
   private final boolean disableExpiryUpdate;
   private final boolean checkAfterJoin;
   private boolean ipHistoryIgnored;
   private final boolean geoipEnable;
   private final String geoipDownloadDb;
   private final String[] geoipBlacklist;
   private final String[] geoipWhitelist;
   private final String[] geoipDownloadApi;
   private final List ignoredKickMessages;
   private final List limitGroups;
   private final Set hiddenDupeipPlayers;
   private final Set disableLoginBanCheckServers;
   private final List ignoredIphistoryServers;
   private final long databaseTimeOffset;
   private final long syncPollTicks;
   private final int syncPollSkip;
   private final int defaultHistoryLimit;
   private final byte usernameMaxLength;

   public Settings(@NotNull Platform platform, @NotNull ConfigFile configFile) {
      int value = 0;
      this.platform = platform;
      this.config = configFile;
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      module.setSettings(this);
      String getenv2 = System.getenv("LITEBANS_CONFIG");
      if (getenv2 != null) {
         this.platform.getLogger().info("Loading environment options: " + getenv2);
         Object obj = new JSONParser().computeObj(getenv2);
         Intrinsics.checkNotNullExpressionValue(obj, "");
         HashMap hashMap = (HashMap)obj;
         Iterable iterable = hashMap.entrySet();

         for (Object obj2 : iterable) {
            Entry entry = (Entry)obj2;
            this.config.set((String)entry.getKey(), entry.getValue());
         }
      }

      DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.handleIntValue(this.config.getInt("online_player_select_limit", 64));
      this.suppressGetAddressWarning = this.config
         .getBoolean("i_accept_that_dupeip_geoip_iphistory_and_ipban_will_not_work_without_getAddress_and_just_want_to_only_disable_warnings_logged_to_console", false);
      this.disableWildcardBans = this.config.getBoolean("disable_wildcard_bans", false);
      this.disableExpiryUpdate = this.config.getBoolean("disable_expiry_update", false);
      String text = "database_time_offset";
      String text2 = "0 seconds";
      this.databaseTimeOffset = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text, text2), DurationUnit.DURATIONS.parseDuration(text2, 0.0));
      this.usernameMaxLength = UByte.constructorImpl((byte)this.config.getInt("username_max_length", 16));
      module.setDebugLevel((byte)Math.max(0, this.config.getInt("debug_level", 0)));
      this.bungeecord = this.config.getBoolean("bungeecord", false);
      this.onlineMode = this.config.getBoolean("online_mode", true);
      String string = this.config.getString("sql.driver", "h2");
      Locale locale = Locale.ENGLISH;
      String lowerCase = string.toLowerCase(locale);
      if (StringsKt.checkText(lowerCase, "postgres", false, 2, null)) {
         lowerCase = "pgsql";
      }

      this.sqlDriver = lowerCase;
      String string2 = this.config.getString("sql.address", "localhost:3306");
      String string3 = this.config.getString("sql.database", "litebans");
      String string4 = this.config.getString("sql.username", "");
      String string5 = this.config.getString("sql.password", "");
      String string6 = this.config.getString("sql.table_prefix", "litebans_");
      if (Intrinsics.areEqual(lowerCase, "h2")) {
         try {
            File dataFolder = this.platform.getDataFolder();
            String text3 = "h2.yml";
            File file = new File(dataFolder, text3);
            if (!file.exists() || YamlConfigFile.obtainArchiveHelper3() == null) {
               File dataFolder2 = this.platform.getDataFolder();
               String text4 = "h2.json";
               file = new File(dataFolder2, text4);
            }

            if (file.exists()) {
               ConfigFile throwIfFailed2 = this.platform.loadConfigFile(file).throwIfFailed();
               string4 = throwIfFailed2.getString("user", string4);
               string5 = throwIfFailed2.getString("password", string5);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      if (!Intrinsics.areEqual(string6, "litebans_")) {
         String text5 = string6;
         Intrinsics.checkNotNull(text5);
         CharSequence message = text5;
         Regex regex = new Regex("^[0-9a-zA-Z_]*$");
         if (!regex.matches(message)) {
            this.platform.getLogger().warning("Table prefix '" + string6 + "' is invalid! Using default ('litebans_').");
            string6 = "litebans_";
         }
      }

      String text6;
      label326: {
         Intrinsics.checkNotNull(string2);
         this.sqlAddress = string2;
         String text7 = string4;
         Intrinsics.checkNotNull(text7);
         this.sqlUsername = text7;
         String text8 = string5;
         Intrinsics.checkNotNull(text8);
         this.sqlPassword = text8;
         Intrinsics.checkNotNull(string3);
         this.sqlDatabase = string3;
         String text9 = string6;
         Intrinsics.checkNotNull(text9);
         this.sqlTablePrefix = text9;
         String string7 = this.config.getString("sql.engine", "InnoDB");
         if (!Intrinsics.areEqual(string7, "InnoDB")) {
            String[] texts = new String[]{"mysql", "mariadb", "h2", "sqlite"};
            if (ArraysKt.contains(texts, string7.toLowerCase(Locale.ROOT))) {
               text6 = "InnoDB";
               break label326;
            }
         }

         text6 = string7;
      }

      this.sqlEngine = text6;
      String string8 = this.config.getString("sql.options", "useSSL=false&serverTimezone=UTC");
      this.sqlOptions = string8;
      String text10 = "version:8";
      String[] texts2 = new String[]{
         text10,
         "mysql:com.mysql.cj.jdbc.Driver:https://repo1.maven.org/maven2/mysql/mysql-connector-java/{}/mysql-connector-java-{}.jar:8.0.29:d4e32d2a6026b5acc00300b73a86c28fb92681ae9629b21048ee67014c911db6",
         "pgsql:org.postgresql.Driver:https://repo1.maven.org/maven2/org/postgresql/postgresql/{}/postgresql-{}.jar:42.4.0:fe25b9c0a2c59458504ec88862853df522ee87f8a02564835d537c29ae4cb125",
         "mariadb:org.mariadb.jdbc.Driver:https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/{}/mariadb-java-client-{}.jar:3.1.2:aaec1ad348d030a65b25c93c65cdaf472bf8b4b6b314b965e5ba13aec81bc622",
         "sqlite:org.sqlite.JDBC:https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/{}/sqlite-jdbc-{}.jar:3.8.11.1:58ab29176a24a85a0c76177561257a986c4865e45730b79882c688846371d341",
         "h2:org.h2.Driver:https://repo1.maven.org/maven2/com/h2database/h2/{}/h2-{}.jar:1.4.197:37f5216e14af2772930dff9b8734353f0a80e89ba3f33e065441de6537c5e842"
      };
      String text11 = "sql.drivers";
      String[] copyOf2 = Arrays.copyOf(texts2, texts2.length);
      String[] texts3;
      if (!this.getConfig().isSet(text11)) {
         texts3 = Arrays.copyOf(copyOf2, copyOf2.length);
      } else {
         Collection stringList = this.getConfig().getStringList(text11);
         texts3 = (String[]) stringList.toArray(new String[0]);
      }

      String[] texts4 = texts3;
      if (!Intrinsics.areEqual(ArraysKt.getOrNull(texts4, 0), text10)) {
         texts4 = texts2;
         module.logDebug("Using default database drivers (" + text10 + ").");
      }

      this.sqlDrivers = texts4;
      this.sqlPoolMinConnections = this.config.getInt("sql.pool.min_connections", 1);
      this.sqlPoolMaxConnections = this.config.getInt("sql.pool.max_connections", 10);
      String text12 = "sql.pool.keepalive";
      String text13 = "0 seconds";
      this.sqlPoolKeepalive = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text12, text13), DurationUnit.DURATIONS.parseDuration(text13, 0.0));
      String text14 = "sql.pool.timeout";
      String text15 = "95 seconds";
      this.sqlPoolTimeout = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text14, text15), DurationUnit.DURATIONS.parseDuration(text15, 0.0));
      String text16 = "sql.pool.idle_timeout";
      String text17 = "1 minute";
      this.sqlPoolIdleTimeout = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text16, text17), DurationUnit.DURATIONS.parseDuration(text17, 0.0));
      String text18 = "sql.pool.max_lifetime";
      String text19 = "15 minutes";
      this.sqlPoolMaxLifetime = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text18, text19), DurationUnit.DURATIONS.parseDuration(text19, 0.0));
      ConfigKeyReader section = this.config.getSection("durations.limits");
      ArrayList arrayList = new ArrayList();
      if (section != null) {
         Collection keys = section.getKeys(false);
         Intrinsics.checkNotNull(keys);
         Iterable iterable2 = keys;

         for (Object obj3 : iterable2) {
            String string9 = (String)obj3;
            ConfigKeyReader section2 = section.getSection(string9);
            if (section2 != null) {
               String[] texts5 = new String[]{"permission", "tempban", "tempmute"};
               var arrayList2 = new ArrayList();
               int value2 = 0;

               for (int i = texts5.length; value2 < i; value2++) {
                  String text20 = texts5[value2];
                  if (section2.get(text20) == null) {
                     arrayList2.add(text20);
                  }
               }

               Iterable iterable3 = arrayList2;
               var arrayList3 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList2, 10));

               for (Object obj4 : iterable3) {
                  String string10 = (String)obj4;
                  arrayList3.add(
                     TextUtil.computeMessage(
                        "The duration limit group \"" + string9 + "\" does not have the \"" + string10 + "\" option specified.",
                        "Note that tempban/tempmute durations will default to 27 seconds and the permission will default to none."
                     )
                  );
               }

               Logger logger = this.platform.getLogger();

               for (Object obj5 : arrayList3) {
                  CharSequence charSequence = (CharSequence)obj5;
                  logger.warning(charSequence.toString());
               }

               String text21 = "27 seconds";
               String text22 = "5 seconds";
               String string11 = section2.getString("permission", "none");
               String text23 = "tempban";
               long duration = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text23, text21), DurationUnit.DURATIONS.parseDuration(text21, 0.0));
               String text24 = "tempmute";
               long duration2 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text24, text21), DurationUnit.DURATIONS.parseDuration(text21, 0.0));
               if (duration <= 0L) {
                  duration = Long.MAX_VALUE;
               }

               if (duration2 <= 0L) {
                  duration2 = Long.MAX_VALUE;
               }

               String text25 = "cooldown_ban";
               long duration3 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text25, text22), DurationUnit.DURATIONS.parseDuration(text22, 0.0));
               String text26 = "cooldown_mute";
               long duration4 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text26, text22), DurationUnit.DURATIONS.parseDuration(text22, 0.0));
               String text27 = "cooldown_warn";
               long duration5 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text27, text22), DurationUnit.DURATIONS.parseDuration(text22, 0.0));
               String text28 = "cooldown_kick";
               long duration6 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text28, text22), DurationUnit.DURATIONS.parseDuration(text22, 0.0));
               String text29 = "cooldown_redo";
               String text30 = "1 second";
               long duration7 = (long)DurationUnit.DURATIONS.parseDuration(section2.getString(text29, text30), DurationUnit.DURATIONS.parseDuration(text30, 0.0));
               boolean boolean2 = section2.getBoolean("require_template", false);
               Intrinsics.checkNotNull(string11);
               DurationLimitGroup kotlinData6 = new DurationLimitGroup(string11, new DurationLimits(duration, duration2), new CommandCooldowns(duration3, duration4, duration5, duration6, duration7), new GroupLimitOther(boolean2));
               arrayList.add(kotlinData6);
            }
         }
      }

      this.limitGroups = arrayList;
      String text31 = "durations.console_cooldown";
      String text32 = "1 second";
      this.durationsConsoleCooldown = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text31, text32), DurationUnit.DURATIONS.parseDuration(text32, 0.0));
      String text33 = "durations.effective_ipban_duration";
      String text34 = "permanent";
      this.durationsEffectiveIpbanDuration = (long)DurationUnit.DURATIONS.parseDuration(this.getConfig().getString(text33, text34), DurationUnit.DURATIONS.parseDuration(text34, 0.0));
      String string12 = this.config.getString("security.obscure_seed", "0");
      if (Intrinsics.areEqual(string12, "0")) {
         String text35 = "";
         Long boxed = StringsKt.computeLong(text35);
         if (boxed != null) {
            boxed = boxed * 127;
         }

         String text36;
         label287: {
            Long boxed2 = boxed;
            if (boxed2 != null) {
               String string13 = boxed2.toString();
               text36 = string13;
               if (string13 != null) {
                  break label287;
               }
            }

            text36 = "1";
         }

         string12 = text36;
      }

      this.securityObscureSeed = string12.hashCode();
      this.mutesEnabled = this.config.getBoolean("mutes.enabled", true);
      this.durationsReduceToLimit = this.config.getBoolean("durations.reduce_to_limit", this.config.getBoolean("durations.round_down", true));
      this.mutesPreventBypass = this.config.getBoolean("mutes.prevent_bypass", true);
      this.lockdownEnabled = this.config.getBoolean("lockdown.enabled", true);
      this.lockdownPersistEnabled = this.config.getBoolean("lockdown.persist_enabled", true);
      String text37 = "mutes.command_blacklist";
      String[] texts6 = new String[]{"/me", "/say", "/msg", "/tell"};
      if (!this.getConfig().isSet(text37)) {
         texts3 = Arrays.copyOf(texts6, texts6.length);
      } else {
         Collection stringList2 = this.getConfig().getStringList(text37);
         texts3 = (String[]) stringList2.toArray(new String[0]);
      }

      String[] texts7 = texts3;
      Iterable iterable4 = StringMatching.computeList(texts7);
      var arrayList4 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable4, 10));

      for (Object obj6 : iterable4) {
         String string14 = (String)obj6;
         arrayList4.add(StringsKt.endsWithWithDefaults(string14, ' ', false, 2, null) ? string14 : string14 + ' ');
      }

      List list = arrayList4;
      this.mutesCommandPrefixes = list;
      this.mutesCommandBlacklist = CollectionsKt.toHashSet(StringMatching.computeList(texts7));
      this.warningsEnabled = this.config.getBoolean("warnings.enabled", true);
      String string15 = this.config.getString("warnings.expire_after", "3 days");
      this.warningsExpireAfter = string15;
      String text38 = "warnings.actions";
      String[] texts8 = new String[0];
      String[] texts9;
      if (!this.getConfig().isSet(text38)) {
         texts9 = Arrays.copyOf(texts8, texts8.length);
      } else {
         Collection stringList3 = this.getConfig().getStringList(text38);
         texts9 = (String[]) stringList3.toArray(new String[0]);
      }

      label275: {
         this.warningsActions = texts9;
         this.warningsActionsExecuteAsConsole = this.config.getBoolean("warnings.actions_execute_as_console", false);
         this.warningsDeleteWarningsAfterAction = this.config.getBoolean("warnings.delete_warnings_after_action");
         String string16 = this.config.getString("ban_alts", "");
         String text39 = string16 != null ? string16.toLowerCase(Locale.ROOT) : null;
         if (text39 != null) {
            switch (text39.hashCode()) {
               case -1609594047:
                  if (text39.equals("enabled")) {
                     value = 1;
                     break label275;
                  }
                  break;
               case -1305285460:
                  if (text39.equals("extreme")) {
                     value = 3;
                     break label275;
                  }
                  break;
               case 1841730422:
                  if (text39.equals("enhanced")) {
                     value = 2;
                     break label275;
                  }
            }
         }

         value = 0;
      }

      this.byteValue = (byte) value;
      ConfigKeyReader configKeyReader = this.config;
      String text40 = "notify.dupeip_scan_filter_duration";
      String text41 = "7d";
      this.notifyDupeipScanFilterDuration = (long)DurationUnit.DURATIONS.parseDuration(configKeyReader.getString(text40, text41), DurationUnit.DURATIONS.parseDuration(text41, 0.0));
      this.notifyDupeipOnJoinThreshold = this.config.getInt("notify.dupeip_on_join_threshold", 9999999);
      this.notifyDupeipScanLimit = this.config.getInt("notify.dupeip_scan_limit", 20);
      String string17 = this.config.getString("notify.notify_discord", "");
      this.notifyNotifyDiscord = string17;
      this.notifyBannedPlayerJoin = this.config.getBoolean("notify.banned_player_join", true);
      this.notifyMutedPlayerChat = this.config.getBoolean("notify.muted_player_chat", true);
      this.notifyDupeipOnJoin = this.config.getBoolean("notify.dupeip_on_join", true) || this.byteValue > 1;
      this.notifyDupeipOnBannedAccount = this.config.getBoolean("notify.dupeip_on_banned_account", true);
      this.notifyDupeipOnMutedAccount = this.config.getBoolean("notify.dupeip_on_muted_account", false);
      this.notifyDupeipShowMutedAccounts = this.config.getBoolean("notify.dupeip_show_muted_accounts", false);
      this.notifyNotifyConsole = this.config.getBoolean("notify.notify_console", true);
      this.exemptEnable = this.config.getBoolean("exempt.enable", true);
      this.exemptUseGroupWeights = this.config.getBoolean("exempt.use_group_weights", false);
      this.exemptPermitSameWeight = this.config.getBoolean("exempt.permit_same_weight", true);
      String string18 = this.config.getString("exempt.permission_world", "default");
      this.exemptPermissionWorld = string18;
      String text42 = "exempt.exempt_groups";
      String[] texts10 = new String[0];
      String[] texts11;
      if (!this.getConfig().isSet(text42)) {
         texts11 = Arrays.copyOf(texts10, texts10.length);
      } else {
         Collection stringList4 = this.getConfig().getStringList(text42);
         texts11 = (String[]) stringList4.toArray(new String[0]);
      }

      this.exemptExemptGroups = texts11;
      String text43 = "exempt.exempt_players";
      String[] texts12 = new String[0];
      String[] texts13;
      if (!this.getConfig().isSet(text43)) {
         texts13 = Arrays.copyOf(texts12, texts12.length);
      } else {
         Collection stringList5 = this.getConfig().getStringList(text43);
         texts13 = (String[]) stringList5.toArray(new String[0]);
      }

      this.exemptExemptPlayers = texts13;
      this.geoipEnable = this.config.getBoolean("geoip.enable", false);
      String text44 = "geoip.blacklist";
      String[] texts14 = new String[0];
      String[] texts15;
      if (!this.getConfig().isSet(text44)) {
         texts15 = Arrays.copyOf(texts14, texts14.length);
      } else {
         Collection stringList6 = this.getConfig().getStringList(text44);
         texts15 = (String[]) stringList6.toArray(new String[0]);
      }

      this.geoipBlacklist = texts15;
      String text45 = "geoip.whitelist";
      String[] texts16 = new String[0];
      String[] texts17;
      if (!this.getConfig().isSet(text45)) {
         texts17 = Arrays.copyOf(texts16, texts16.length);
      } else {
         Collection stringList7 = this.getConfig().getStringList(text45);
         texts17 = (String[]) stringList7.toArray(new String[0]);
      }

      this.geoipWhitelist = texts17;
      String text46 = "geoip.download.api";
      String[] texts18 = new String[]{
         "https://repo1.maven.org/maven2/com/maxmind/geoip2/geoip2/2.1.0/geoip2-2.1.0.jar:81035e738fc392bcfb00be5d39f50fde56692ba305b21338412da9c0fc98e3fb",
         "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-annotations/2.4.3/jackson-annotations-2.4.3.jar:3c680cfafad00cfe1432514f19c5c13fde17913aa2269a9844882404242f6452",
         "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-core/2.4.3/jackson-core-2.4.3.jar:2c77b1c9441f257eb9e3dda476758a034a9383ef848f97b9949287a0519923e6",
         "https://repo1.maven.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.4.3/jackson-databind-2.4.3.jar:d7ac4f5ce138ce63478673e64378c2efd27f0294602f0fc69cb867685c723797",
         "https://repo1.maven.org/maven2/com/maxmind/db/maxmind-db/1.0.0/maxmind-db-1.0.0.jar:3cd5c1c731d8d9cb561d4e53ad6017862e4917cc08c24f45111061b1c2170b06"
      };
      String[] texts19;
      if (!this.getConfig().isSet(text46)) {
         texts19 = Arrays.copyOf(texts18, texts18.length);
      } else {
         Collection stringList8 = this.getConfig().getStringList(text46);
         texts19 = (String[]) stringList8.toArray(new String[0]);
      }

      this.geoipDownloadApi = texts19;
      String string19 = this.config
         .getString("geoip.download.db", "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key=YOUR_LICENSE_KEY&suffix=tar.gz");
      this.geoipDownloadDb = string19;
      String string20 = this.config.getString("import.from", "vanilla");
      this.importFrom = string20;
      this.importSqlite = this.config.getBoolean("import.sqlite", true);
      this.importImportIpbans = this.config.getBoolean("import.import_ipbans", true);
      String string21 = this.config.getString("import.sqlite_file", "plugins/MaxBans/bans.db");
      this.importSqliteFile = string21;
      String string22 = this.config.getString("import.import_mysql_address", "localhost:3306");
      this.importImportMysqlAddress = string22;
      String string23 = this.config.getString("import.import_mysql_username", "");
      this.importImportMysqlUsername = string23;
      String string24 = this.config.getString("import.import_mysql_password", "");
      this.importImportMysqlPassword = string24;
      String string25 = this.config.getString("import.import_mysql_database", "maxbans");
      this.importImportMysqlDatabase = string25;
      String string26 = this.config.getString("import.import_table_prefix", "default");
      this.importImportTablePrefix = string26;
      String text47 = Message.computeText(this.config.getString("console_sender_name", "Console"), true);
      CharSequence message2 = text47;
      byte usernameMaxLength2 = this.usernameMaxLength;
      this.consoleSenderName = TextUtil.access$length(message2.toString(), usernameMaxLength2 & 255);
      this.autocompleteOnlinePlayerNames = this.config.getBoolean("autocomplete_online_player_names", false);
      this.dupeipScanAllIps = this.config.getBoolean("dupeip_scan_all_ips", false);
      ConfigFile configFile2 = this.config;
      String text48 = "ipreport_minimum_accounts";
      byte b2 = 1;
      this.ipreportMinimumAccounts = (byte)configFile2.getInt(text48, b2);
      this.deletePreviousHistory = this.config.getBoolean("delete_previous_history", true);
      this.unbanAllHistory = this.config.getBoolean("unban_all_history", false);
      this.supportAuthme = this.config.getBoolean("support_authme", true);
      this.useDisplayNames = this.config.getBoolean("use_display_names", false);
      this.banlistShowActiveOnly = this.config.getBoolean("banlist_show_active_only", false);
      this.banlistBansPerPage = Math.max(0, this.config.getInt("banlist_bans_per_page", 5));
      boolean boolean3 = this.config.getBoolean("ban_names", false);
      if (this.platform.getPlatformId() == 0 && this.platform.createBridge().hasPlugin("FastLogin")) {
         module.logDebug("FastLogin detected; forcing `ban_names=true`.");
         boolean3 = true;
      }

      this.banNames = boolean3;
      this.offlineTabSize = this.config.getInt("offline_tab_size", 12000);
      this.notificationThrottleTicks = this.config.getInt("notification_throttle_ticks", 12000);
      this.requireKickReason = this.config.getBoolean("require_kick_reason", false);
      this.requireBanMuteReason = this.config.getBoolean("require_ban_mute_reason", false);
      this.requireUnbanUnmuteReason = this.config.getBoolean("require_unban_unmute_reason", false);
      Pair pair = loadServerIdentityWithDefaults(this, false, 1, null);
      this.serverName = (String)pair.getFirst();
      this.serverUuid = (String)pair.getSecond();
      String string27 = this.config.getString("default_server_scope", "*");
      this.defaultServerScope = TextUtil.access$length(string27, 32);
      this.syncPollTicks = Math.max(40L, this.config.getLong("sync_poll_ticks", 55L));
      this.syncPollSkip = Math.max(1, this.config.getInt("sync_poll_skip", 3));
      String text49 = "hidden_dupeip_players";
      String[] texts20 = new String[0];
      String[] texts21;
      if (!this.getConfig().isSet(text49)) {
         texts21 = Arrays.copyOf(texts20, texts20.length);
      } else {
         Collection stringList9 = this.getConfig().getStringList(text49);
         texts21 = (String[]) stringList9.toArray(new String[0]);
      }

      this.hiddenDupeipPlayers = CollectionsKt.toSet(StringMatching.computeList(texts21));
      String text50 = "ignored_kick_messages";
      String[] texts22 = new String[]{"restarting"};
      String[] texts23;
      if (!this.getConfig().isSet(text50)) {
         texts23 = Arrays.copyOf(texts22, texts22.length);
      } else {
         Collection stringList10 = this.getConfig().getStringList(text50);
         texts23 = (String[]) stringList10.toArray(new String[0]);
      }

      this.ignoredKickMessages = StringMatching.computeList(texts23);
      String text51 = "ignored_iphistory_servers";
      String text52 = "ignored_history_servers";
      String[] texts24 = new String[0];
      String[] texts25;
      if (!this.getConfig().isSet(text52)) {
         texts25 = Arrays.copyOf(texts24, texts24.length);
      } else {
         Collection stringList11 = this.getConfig().getStringList(text52);
         texts25 = (String[]) stringList11.toArray(new String[0]);
      }

      String[] texts26 = texts25;
      String[] copyOf3 = Arrays.copyOf(texts26, texts26.length);
      String[] texts27;
      if (!this.getConfig().isSet(text51)) {
         texts27 = Arrays.copyOf(copyOf3, copyOf3.length);
      } else {
         Collection stringList12 = this.getConfig().getStringList(text51);
         texts27 = (String[]) stringList12.toArray(new String[0]);
      }

      this.ignoredIphistoryServers = StringMatching.computeList(texts27);
      String text53 = "disable_login_ban_check_servers";
      String[] texts28 = new String[0];
      String[] texts29;
      if (!this.getConfig().isSet(text53)) {
         texts29 = Arrays.copyOf(texts28, texts28.length);
      } else {
         Collection stringList13 = this.getConfig().getStringList(text53);
         texts29 = (String[]) stringList13.toArray(new String[0]);
      }

      this.disableLoginBanCheckServers = CollectionsKt.toSet(StringMatching.computeList(texts29));
      Iterable ignoredIphistoryServers2 = this.ignoredIphistoryServers;
      boolean flag;
      if (ignoredIphistoryServers2 instanceof Collection && ((Collection)ignoredIphistoryServers2).isEmpty()) {
         flag = false;
      } else {
         Iterator iterator2 = ignoredIphistoryServers2.iterator();

         while (true) {
            if (!iterator2.hasNext()) {
               flag = false;
               break;
            }

            Object obj7 = iterator2.next();
            String string28 = (String)obj7;
            if (ServerScopes.SERVER_SCOPE.isLocal(string28)) {
               flag = true;
               break;
            }
         }
      }

      boolean flag2 = flag;
      this.ipHistoryIgnored = flag2 || this.ignoredIphistoryServers.contains(pair.getFirst());
      this.defaultHistoryLimit = this.config.getInt("default_history_limit", 10);
      this.sync = this.config.getBoolean("sync", true);
      this.syncBroadcasts = this.config.getBoolean("sync_broadcasts", true);
      this.syncNotifications = this.config.getBoolean("sync_notifications", true);
      this.syncDupeipNotifications = this.config.getBoolean("sync_dupeip_notifications", false);
      this.useDatabaseTime = this.config.getBoolean("use_database_time", false);
      this.localMessages = this.config.getBoolean("local_messages", true);
      this.checkAfterJoin = this.config.getBoolean("check_after_join", false);
      this.useProtocollib = this.config.getBoolean("use_protocollib", true);
      this.overrideLowerDuration = this.config.getBoolean("override_lower_duration", true);
      this.allowExemptBypass = this.exemptUseGroupWeights || this.config.getBoolean("allow_exempt_bypass", true);
      this.silent = this.config.getBoolean("silent", false);
      this.punishIp = this.config.getBoolean("punish_ip", false);
      this.useTimezone = this.config.getBoolean("use_timezone", true);
      this.updateCheck = this.config.getBoolean("update_check", true);
      this.securityDupeip = this.config.getBoolean("security.dupeip", true);
      this.securityIphistory = this.config.getBoolean("security.iphistory", true);
      this.securityStaffrollback = this.config.getBoolean("security.staffrollback", true);
      this.securityModify = this.config.getBoolean("security.modify", false);
      this.securityPrunehistory = this.config.getBoolean("security.prunehistory", false);
      this.securitySenderCustom = this.config.getBoolean("security.sender_custom", true);
      this.securityIpban = this.config.getBoolean("security.ipban", true);
      this.securityIpbanCommand = (byte)Math.min(this.config.getInt("security.ipban_command", 1), 2);
      this.disableLoginBanCheck = this.config.getBoolean("disable_login_ban_check", false) && this.platform.getPlatformId() == 0;
      Logger logger2 = this.platform.getLogger();
      if (!this.localMessages) {
         logger2.warning("Local messages are disabled! If you don't see any broadcasts, try enabling local_messages.");
      }

      if (this.disableLoginBanCheck) {
         logger2.warning("Login ban checks are disabled! Bans will not take effect here.");
      }

      if (this.disableWildcardBans) {
         logger2.warning("Wildcard bans are disabled! Wildcard IP-bans and IP-mutes will not take effect.");
      }

      if (this.disableExpiryUpdate) {
         logger2.warning("Expiry update query is disabled! One instance should have this enabled, at least.");
      }

      if (this.byteValue == 0) {
         String text54;
         label223: {
            String string29 = this.config.getString("ban_alts", "");
            if (string29 != null) {
               text54 = string29.toLowerCase(Locale.ROOT);
               if (text54 != null) {
                  break label223;
               }
            }

            text54 = "";
         }

         String text55 = text54;
         if (text55.length() > 0 && !Intrinsics.areEqual(text55, "disabled")) {
            logger2.warning("Unrecognized setting for ban_alts: \"" + text55 + "\". This option will be disabled.");
         }
      }

      if (module.isVerbose()) {
         CharSequence message3 = TextUtil.computeMessage(
            TextUtil.computeMessage(CollectionsKt.joinToStringWithDefaults(this.limitGroups, null, null, null, 0, null, null, 63, null), "m:" + Arrays.toString(texts7)), "gw: " + Arrays.toString(this.geoipWhitelist)
         );
         module.logDebug(message3);
      }
   }

   public Settings(Platform platform, ConfigFile configFile, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         configFile = platform.getConfigFile();
      }

      this(platform, configFile);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final ConfigFile getConfig() {
      return this.config;
   }

   public final String getServerUuid() {
      return this.serverUuid;
   }

   public final void setText56(@NotNull String text) {
      this.serverUuid = text;
   }

   public final String getServerName() {
      return this.serverName;
   }

   public final void setText57(@NotNull String text) {
      this.serverName = text;
   }

   public final String getSqlDriver() {
      return this.sqlDriver;
   }

   public final void setSqlDriver(@NotNull String text) {
      this.sqlDriver = text;
   }

   public final String getSqlAddress() {
      return this.sqlAddress;
   }

   public final String getSqlUsername() {
      return this.sqlUsername;
   }

   public final String getSqlPassword() {
      return this.sqlPassword;
   }

   public final String getSqlDatabase() {
      return this.sqlDatabase;
   }

   public final String getSqlTablePrefix() {
      return this.sqlTablePrefix;
   }

   public final String a_() {
      return this.sqlEngine;
   }

   public final String getA__f() {
      return this.sqlOptions;
   }

   public final String[] getSqlDrivers() {
      return this.sqlDrivers;
   }

   public final int getSqlPoolMinConnections() {
      return this.sqlPoolMinConnections;
   }

   public final int getSqlPoolMaxConnections() {
      return this.sqlPoolMaxConnections;
   }

   public final long getSqlPoolKeepalive() {
      return this.sqlPoolKeepalive;
   }

   public final long getSqlPoolTimeout() {
      return this.sqlPoolTimeout;
   }

   public final long getSqlPoolIdleTimeout() {
      return this.sqlPoolIdleTimeout;
   }

   public final List getMutesCommandPrefixes() {
      return this.mutesCommandPrefixes;
   }

   public final Set getMutesCommandBlacklist() {
      return this.mutesCommandBlacklist;
   }

   public final boolean isWarningsEnabled() {
      return this.warningsEnabled;
   }

   public final String[] getWarningsActions() {
      return this.warningsActions;
   }

   public final boolean isWarningsActionsExecuteAsConsole() {
      return this.warningsActionsExecuteAsConsole;
   }

   public final boolean isWarningsDeleteWarningsAfterAction() {
      return this.warningsDeleteWarningsAfterAction;
   }

   public final String getWarningsExpireAfter() {
      return this.warningsExpireAfter;
   }

   public final boolean isExemptEnable() {
      return this.exemptEnable;
   }

   public final boolean isExemptUseGroupWeights() {
      return this.exemptUseGroupWeights;
   }

   public final boolean isExemptPermitSameWeight() {
      return this.exemptPermitSameWeight;
   }

   public final String getExemptPermissionWorld() {
      return this.exemptPermissionWorld;
   }

   public final String[] getExemptExemptGroups() {
      return this.exemptExemptGroups;
   }

   public final String[] getExemptExemptPlayers() {
      return this.exemptExemptPlayers;
   }

   public final boolean isImportImportIpbans() {
      return this.importImportIpbans;
   }

   public final boolean isImportSqlite() {
      return this.importSqlite;
   }

   public final String getImportSqliteFile() {
      return this.importSqliteFile;
   }

   public final String getImportFrom() {
      return this.importFrom;
   }

   public final String getImportImportMysqlAddress() {
      return this.importImportMysqlAddress;
   }

   public final String getImportImportMysqlDatabase() {
      return this.importImportMysqlDatabase;
   }

   public final String getImportImportMysqlUsername() {
      return this.importImportMysqlUsername;
   }

   public final String getImportImportMysqlPassword() {
      return this.importImportMysqlPassword;
   }

   public final String getImportImportTablePrefix() {
      return this.importImportTablePrefix;
   }

   public final String getDefaultServerScope() {
      return this.defaultServerScope;
   }

   public final String getConsoleSenderName() {
      return this.consoleSenderName;
   }

   public final byte getByteValue() {
      return this.byteValue;
   }

   public final byte getIpreportMinimumAccounts() {
      return this.ipreportMinimumAccounts;
   }

   public final int getBanlistBansPerPage() {
      return this.banlistBansPerPage;
   }

   public final int getNotificationThrottleTicks() {
      return this.notificationThrottleTicks;
   }

   public final int getOfflineTabSize() {
      return this.offlineTabSize;
   }

   public final long getDurationsConsoleCooldown() {
      return this.durationsConsoleCooldown;
   }

   public final long getDurationsEffectiveIpbanDuration() {
      return this.durationsEffectiveIpbanDuration;
   }

   public final long getSecurityObscureSeed() {
      return this.securityObscureSeed;
   }

   public final long getNotifyDupeipScanFilterDuration() {
      return this.notifyDupeipScanFilterDuration;
   }

   public final int getNotifyDupeipScanLimit() {
      return this.notifyDupeipScanLimit;
   }

   public final int getNotifyDupeipOnJoinThreshold() {
      return this.notifyDupeipOnJoinThreshold;
   }

   public final String getNotifyNotifyDiscord() {
      return this.notifyNotifyDiscord;
   }

   public final boolean isNotifyBannedPlayerJoin() {
      return this.notifyBannedPlayerJoin;
   }

   public final boolean isNotifyMutedPlayerChat() {
      return this.notifyMutedPlayerChat;
   }

   public final boolean isNotifyDupeipOnJoin() {
      return this.notifyDupeipOnJoin;
   }

   public final boolean isNotifyDupeipOnBannedAccount() {
      return this.notifyDupeipOnBannedAccount;
   }

   public final boolean isNotifyDupeipOnMutedAccount() {
      return this.notifyDupeipOnMutedAccount;
   }

   public final boolean isNotifyDupeipShowMutedAccounts() {
      return this.notifyDupeipShowMutedAccounts;
   }

   public final boolean isNotifyNotifyConsole() {
      return this.notifyNotifyConsole;
   }

   public final boolean isBungeecord() {
      return this.bungeecord;
   }

   public final boolean isOnlineMode() {
      return this.onlineMode;
   }

   public final boolean isPunishIp() {
      return this.punishIp;
   }

   public final boolean isSilent() {
      return this.silent;
   }

   public final boolean isMutesPreventBypass() {
      return this.mutesPreventBypass;
   }

   public final boolean isMutesEnabled() {
      return this.mutesEnabled;
   }

   public final boolean isLockdownEnabled() {
      return this.lockdownEnabled;
   }

   public final boolean isLockdownPersistEnabled() {
      return this.lockdownPersistEnabled;
   }

   public final boolean isSupportAuthme() {
      return this.supportAuthme;
   }

   public final boolean isAutocompleteOnlinePlayerNames() {
      return this.autocompleteOnlinePlayerNames;
   }

   public final boolean isDupeipScanAllIps() {
      return this.dupeipScanAllIps;
   }

   public final boolean isDeletePreviousHistory() {
      return this.deletePreviousHistory;
   }

   public final boolean isUnbanAllHistory() {
      return this.unbanAllHistory;
   }

   public final boolean isBanlistShowActiveOnly() {
      return this.banlistShowActiveOnly;
   }

   public final boolean isUseDisplayNames() {
      return this.useDisplayNames;
   }

   public final boolean isBanNames() {
      return this.banNames;
   }

   public final boolean isRequireKickReason() {
      return this.requireKickReason;
   }

   public final boolean isRequireBanMuteReason() {
      return this.requireBanMuteReason;
   }

   public final boolean isRequireUnbanUnmuteReason() {
      return this.requireUnbanUnmuteReason;
   }

   public final boolean isUseDatabaseTime() {
      return this.useDatabaseTime;
   }

   public final boolean isDurationsReduceToLimit() {
      return this.durationsReduceToLimit;
   }

   public final boolean isOverrideLowerDuration() {
      return this.overrideLowerDuration;
   }

   public final boolean isSync() {
      return this.sync;
   }

   public final boolean isSyncBroadcasts() {
      return this.syncBroadcasts;
   }

   public final boolean isSyncNotifications() {
      return this.syncNotifications;
   }

   public final boolean isSyncDupeipNotifications() {
      return this.syncDupeipNotifications;
   }

   public final boolean isLocalMessages() {
      return this.localMessages;
   }

   public final boolean isUpdateCheck() {
      return this.updateCheck;
   }

   public final boolean isUseProtocollib() {
      return this.useProtocollib;
   }

   public final boolean isUseTimezone() {
      return this.useTimezone;
   }

   public final boolean isSecurityDupeip() {
      return this.securityDupeip;
   }

   public final boolean isSecurityStaffrollback() {
      return this.securityStaffrollback;
   }

   public final boolean isSecurityIphistory() {
      return this.securityIphistory;
   }

   public final boolean isSecurityIpban() {
      return this.securityIpban;
   }

   public final byte getSecurityIpbanCommand() {
      return this.securityIpbanCommand;
   }

   public final boolean isSecurityModify() {
      return this.securityModify;
   }

   public final boolean isSecurityPrunehistory() {
      return this.securityPrunehistory;
   }

   public final boolean isSecuritySenderCustom() {
      return this.securitySenderCustom;
   }

   public final boolean isAllowExemptBypass() {
      return this.allowExemptBypass;
   }

   public final boolean isSuppressGetAddressWarning() {
      return this.suppressGetAddressWarning;
   }

   public final boolean isDisableLoginBanCheck() {
      return this.disableLoginBanCheck;
   }

   public final boolean isDisableWildcardBans() {
      return this.disableWildcardBans;
   }

   public final boolean isDisableExpiryUpdate() {
      return this.disableExpiryUpdate;
   }

   public final boolean isCheckAfterJoin() {
      return this.checkAfterJoin;
   }

   public final boolean isIpHistoryIgnored() {
      return this.ipHistoryIgnored;
   }

   public final boolean isGeoipEnable() {
      return this.geoipEnable;
   }

   public final String getGeoipDownloadDb() {
      return this.geoipDownloadDb;
   }

   public final String[] getGeoipBlacklist() {
      return this.geoipBlacklist;
   }

   public final String[] getGeoipWhitelist() {
      return this.geoipWhitelist;
   }

   public final String[] getGeoipDownloadApi() {
      return this.geoipDownloadApi;
   }

   public final List getIgnoredKickMessages() {
      return this.ignoredKickMessages;
   }

   public final List getLimitGroups() {
      return this.limitGroups;
   }

   public final Set getHiddenDupeipPlayers() {
      return this.hiddenDupeipPlayers;
   }

   public final Set getDisableLoginBanCheckServers() {
      return this.disableLoginBanCheckServers;
   }

   public final List getIgnoredIphistoryServers() {
      return this.ignoredIphistoryServers;
   }

   public final long getDatabaseTimeOffset() {
      return this.databaseTimeOffset;
   }

   public final long getSyncPollTicks() {
      return this.syncPollTicks;
   }

   public final int getSyncPollSkip() {
      return this.syncPollSkip;
   }

   public final int getDefaultHistoryLimit() {
      return this.defaultHistoryLimit;
   }

   public final byte getUsernameMaxLength() {
      return this.usernameMaxLength;
   }

   public final Pair loadServerIdentity(boolean flag) {
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      String text = TextUtil.access$length(this.config.getString("server_name", "litebans"), 32);
      if (ServerScopes.SERVER_SCOPE.isGlobal(text)) {
         module.logDebug("[Sync] Server name cannot be \"" + text + "\" (global context)! Reverting to default.");
         text = "litebans";
      }

      String text2 = null;
      File dataFolder = this.platform.getDataFolder();
      String text3 = "server.yml";
      File file = new File(dataFolder, text3);
      if (!file.exists() || YamlConfigFile.obtainArchiveHelper3() == null) {
         File dataFolder2 = this.platform.getDataFolder();
         String text4 = "server.json";
         file = new File(dataFolder2, text4);
      }

      try {
         boolean flag2 = flag || !file.exists();
         ConfigFile throwIfFailed2 = this.platform.loadConfigFile(file).throwIfFailed();
         String string = throwIfFailed2.getString("uuid", null);
         if (string != null && string.length() == 32) {
            text2 = string;
         } else {
            text2 = this.obtainText();
            flag2 = true;
         }

         if (flag2) {
            throwIfFailed2.set("uuid", (Object) text2);
            throwIfFailed2.save();
         } else {
            module.setServerUuidPersisted(false);
         }
      } catch (Exception e) {
         e.printStackTrace();
         module.setServerUuidPersisted(false);
      }

      String text5 = text2;
      if (text2 == null) {
         text5 = this.obtainText();
      }

      Pair pair = TuplesKt.computePair(text, text5);
      byte b = 10;
      if (module.isDebugAtLeast(b)) {
         module.logDebug("Server UUID: \"" + (String)pair.getSecond() + '"');
      }

      return pair;
   }

   public static Pair loadServerIdentityWithDefaults(Settings settings, boolean flag, int value, Object obj) {
      if ((value & 1) != 0) {
         flag = false;
      }

      return settings.loadServerIdentity(flag);
   }

   public final String obtainText() {
      return UsernameValidator.KOTLIN_DATA.computeText(UUID.randomUUID());
   }
}
