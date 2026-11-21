package litebans;

import java.io.Closeable;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import litebans.api.Database;
import litebans.api.Events;
import litebans.api.PlayerProvider;
import litebans.api.RandomID;
import litebans.api.exception.MissingImplementationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ConfigModule extends PluginModule {
   public static final FileDownloader FILE_DOWNLOADER = new FileDownloader(null);
   private byte debugLevel;
   private byte verboseLevel;
   private byte loadState;
   private final ThreadLocal threadLocal = new KotlinThreadLocalSub(this);
   public Settings settings;
   public ArchiveHelper9 archiveHelper9;
   public TemplateManager templateManager;
   public WebhookConfig kotlinData8;
   private final UnsafeLazyImpl unsafeLazyImpl = new UnsafeLazyImpl(() -> new KotlinData12(this.getSettings().getSecurityObscureSeed()));
   private final Lazy authMeInstalled = KotlinStub5.computeLazy(() -> this.platform.createBridge().hasPlugin("AuthMe"));
   private Object serverListPlusBanProvider;
   private GeoIpLookup geoIpLookup;
   private boolean enabled;
   private boolean serverUuidPersisted = true;
   private int maxPlayerCount;
   private final StateFlags stateFlags = new StateFlags(false, false, 3, null);
   private UserRecord userRecord4;
   public static final int VALUE_8 = 8;
   public static final String TEXT_5 = "geoip";
   public static final int VALUE_40000 = 40000;
   private static int value32;
   private static final AtomicBoolean return7 = new AtomicBoolean();
   private static final AtomicBoolean return8 = new AtomicBoolean();

   public ConfigModule(@NotNull Platform platform) {
      super(platform);
      this.verboseLevel = 47;
   }

   public final byte getDebugLevel() {
      return this.debugLevel;
   }

   public final void setDebugLevel(byte b) {
      this.debugLevel = b;
   }

   public final ThreadLocal getThreadLocal() {
      return this.threadLocal;
   }

   public final Settings getSettings() {
      if (this.settings != null) {
         return this.settings;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setSettings(@NotNull Settings settings) {
      this.settings = settings;
   }

   public final ArchiveHelper9 getMessages() {
      if (this.archiveHelper9 != null) {
         return this.archiveHelper9;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setArchiveHelper9(@NotNull ArchiveHelper9 archiveHelper) {
      this.archiveHelper9 = archiveHelper;
   }

   public final TemplateManager getTemplateManager() {
      if (this.templateManager != null) {
         return this.templateManager;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setTemplateManager4(@NotNull TemplateManager templateManager) {
      this.templateManager = templateManager;
   }

   public final WebhookConfig obtainKotlinData() {
      if (this.kotlinData8 != null) {
         return this.kotlinData8;
      }

      Intrinsics.throwUninitializedProperty("");
      return null;
   }

   public final void setKotlinData8(@NotNull WebhookConfig kotlinData) {
      this.kotlinData8 = kotlinData;
   }

   public final KotlinData12 obtainKotlinData2() {
      Lazy lazy = this.unsafeLazyImpl;
      return (KotlinData12)lazy.getValue();
   }

   public final boolean obtainFlag2() {
      Lazy lazy = this.authMeInstalled;
      return (Boolean)lazy.getValue();
   }

   public final Object getObj18() {
      return this.serverListPlusBanProvider;
   }

   public final GeoIpLookup getGeoIp() {
      return this.geoIpLookup;
   }

   public final void setGeoIp(@Nullable GeoIpLookup geoIpLookup) {
      this.geoIpLookup = geoIpLookup;
   }

   public final boolean isServerUuidPersisted() {
      return this.serverUuidPersisted;
   }

   public final void setServerUuidPersisted(boolean flag) {
      this.serverUuidPersisted = flag;
   }

   public final int getMaxPlayerCount() {
      return this.maxPlayerCount;
   }

   public final void setMaxPlayerCount(int value) {
      this.maxPlayerCount = value;
   }

   public final StateFlags getStateFlags() {
      return this.stateFlags;
   }

   public final boolean isDebugAtLeast(int value) {
      return this.debugLevel >= value;
   }

   public final void debug(int value, @Nullable Object obj) {
      if (this.isDebugAtLeast(value)) {
         this.logDebug(obj);
      }
   }

   public final void logDebug(@Nullable Object obj) {
      if (obj instanceof Throwable) {
         ((Throwable)obj).printStackTrace();
      } else {
         PluginModule pluginModule = this;
         String text = String.valueOf(obj);
         pluginModule.platform.getLogger().info(text);
      }
   }

   public final boolean isVerbose() {
      return this.debugLevel == this.verboseLevel;
   }

   public final int computeIntValue(@NotNull CharSequence[] messages) {
      if (!this.isVerbose()) {
         return -1;
      }

      CharSequence[] messages2 = messages;
      int value = 0;

      for (int i = messages2.length; value < i; value++) {
         CharSequence message = messages2[value];
         this.logDebug(message);
      }

      return UInt.constructorImpl(messages2.length);
   }

   public final void handleObj(@Nullable Object obj, int value) {
      if (this.isVerbose()) {
         if (obj instanceof Throwable) {
            PluginModule pluginModule = this;
            String text = "0xAAA";
            pluginModule.platform.getLogger().warning(text);
            ((Throwable)obj).printStackTrace();
         } else {
            PluginModule pluginModule2 = this;
            String text2 = "0xAAA " + obj;
            pluginModule2.platform.getLogger().warning(text2);
            Thread.dumpStack();
         }
      }
   }

   public static void handleConfigModule(ConfigModule configModule, Object obj, int value, int value2, Object obj2) {
      if ((value2 & 2) != 0) {
         value = 0;
      }

      configModule.handleObj(obj, value);
   }

   public final String getCountry(@Nullable String text) {
      if (this.geoIpLookup != null) {
         String text2 = this.geoIpLookup.computeText(text);
         if (text2 != null) {
            return text2;
         }
      }

      return "geoip.unavailable";
   }

   @Override
   public void disable() {
      this.loadState = 0;
      this.platform.cancelTasks();
      return8.set(false);
      if (this.getSettings().isLockdownPersistEnabled()) {
         try {
            BroadcastModule module = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
            File dataFolder = this.platform.getDataFolder();
            String text = "lockdown.yml";
            File file = new File(dataFolder, text);
            if (!file.exists() || YamlConfigFile.obtainArchiveHelper3() == null) {
               File dataFolder2 = this.platform.getDataFolder();
               String text2 = "lockdown.json";
               file = new File(dataFolder2, text2);
            }

            LockdownHandler lockdownHandler = module.getLockdownHandler();
            if ((lockdownHandler != null ? lockdownHandler.getKickMessage() : null) == null) {
               FileUtils.computeFile2(file);
            } else {
               ConfigFile throwIfFailed2 = this.platform.loadConfigFile(file).throwIfFailed();
               LockdownHandler lockdownHandler2 = module.getLockdownHandler();
               ConfigFile configFile = throwIfFailed2.set("reason", lockdownHandler2 != null ? lockdownHandler2.getKickMessage() : null);
               lockdownHandler2 = module.getLockdownHandler();
               configFile.set("scope", (Object) (lockdownHandler2 != null ? lockdownHandler2.getReason() : null)).save();
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void load() {
      PlatformHolder.PLATFORM_STATE.handleFlag(true);
      PlatformHolder.PLATFORM_STATE.handlePlatform(this.platform);
      if (this.loadState < 1) {
         this.loadState = 1;
         this.perform5();

         try {
            this.platform.saveResource("config.yml");
         } catch (Exception e) {
            this.handleConfigError(e);
         }

         this.platform.createConfig();
         Logger logger = this.platform.getLogger();
         Throwable error = this.platform.getConfigFile().getError();
         if (error != null) {
            this.logLoadError("config", error);
            if (this.platform.getConfigFile().getFile().exists()) {
               logger.severe("[!!] Your configuration might be invalid. It can be verified with https://yaml-online-parser.appspot.com/");
            } else {
               logger.severe(
                  "[!!] Configuration does not exist! Ensure that read/write permissions are correct. (\"" + this.platform.getConfigFile().getFile().getAbsolutePath() + "\")"
               );
            }

            if (this.isDebugAtLeast(2)) {
               this.handleConfigError(error);
            }
         }

         Settings settings = new Settings(this.platform, null, 2, null);
         this.setSettings(settings);
      }
   }

   private final void perform5() {
      if (YamlConfigFile.obtainArchiveHelper3() == null) {
         DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
         DriverDefinition namedRecord = new DriverDefinition(
            "SnakeYAML",
            ArchiveSupplierImpl.class.getName(),
            "https://repo1.maven.org/maven2/org/yaml/snakeyaml/2.4/snakeyaml-2.4.jar",
            "2.4",
            "ef779af5d29a9dde8cc70ce0341f5c6f7735e23edff9685ceaa9d35359b7bb7f"
         );
         Object driver = DatabaseModule.loadDriverWithDefaults(module, namedRecord, false, false, 2, null);
         Intrinsics.checkNotNullExpressionValue(driver, "");
         Class class2 = (Class)driver;
         this.platform.setBootstrap(new ArchiveBootstrapImpl(this, class2));
      }
   }

   @Override
   public void enable() {
      PunishmentType punishmentType = null;
      Platform platform = this.platform;
      BroadcastModule module = (BroadcastModule)platform.getModule(BroadcastModule.class);
      if (this.loadState < 2) {
         this.load();
         this.loadState = 2;
         platform.execute(() -> {
            Intrinsics.checkNotNull(platform);
            ArchiveHelper9 archiveHelper92 = new ArchiveHelper9(platform, null, 2, null);

            ArchiveHelper9 archiveHelper;
            label80: {
               ConfigFile configFile;
               try {
                  archiveHelper92.getPlatform().saveResource("messages.yml");
                  configFile = archiveHelper92.getPlatform().loadConfigFile(archiveHelper92.getFile()).throwIfFailed();
                  configFile.computeConfigFile(ConfigFile.computeText("messages.yml", null));
                  archiveHelper92.setConfigFile(configFile);
                  ConfigFile configFile2 = archiveHelper92.getConfigFile();
                  Intrinsics.checkNotNull(configFile2);
                  Message[] messages = Message.values();
                  int value3 = 0;

                  for (int i2 = messages.length; value3 < i2; value3++) {
                     Message message2 = messages[value3];
                     String text31 = message2.text3;
                     CharSequence c3_f2 = message2.c3_f;
                     if (c3_f2 != null) {
                        String string12 = configFile2.getString(text31, null);
                        if (string12 != null) {
                           message2.handleMessage(configFile2.getString(text31, c3_f2.toString()));
                           if (string12.length() > 0) {
                              configFile2.set(text31, (Object) Message.computeText(message2, string12));
                           }
                        } else {
                           configFile2.set(text31, (Object) StringsKt.computeText(c3_f2.toString(), '§', '&', false, 4, null));
                        }
                     }
                  }

                  String string22 = configFile.getString("locale", "system");
                  if (!Intrinsics.areEqual(string22, "system")) {
                     Locale locale = archiveHelper92.obtainLocale();
                     archiveHelper92.setLocale(new Locale(string22));
                     archiveHelper92.getPlatform().getLogger().info("Using configured locale (" + archiveHelper92.getLocale().getLanguage() + ')');
                     if (Intrinsics.areEqual(archiveHelper92.getLocale(), locale)) {
                        String text210 = "Supported locales: " + ArraysKt.joinToStringWithDefaults((Object[])Locale.getAvailableLocales(), null, null, null, 0, null, KotlinFunction1Impl6.KOTLIN_FUNCTION1_IMPL, 31, null);
                        Logger logger2 = archiveHelper92.getPlatform().getLogger();
                        CharSequence message22 = TextUtil.computeMessage("Note: If the configured locale is unrecognized, the system default locale is used.", text210);
                        logger2.info(message22.toString());
                     }
                  } else {
                     archiveHelper92.getPlatform().getLogger().info("Using system locale (" + archiveHelper92.getLocale().getLanguage() + ')');
                  }

                  archiveHelper92.setConfigFile((ConfigFile)null);
               } catch (Exception e4) {
                  archiveHelper92.handleException(e4);
                  archiveHelper = archiveHelper92;
                  break label80;
               }

               if (YamlConfigFile.obtainArchiveHelper3() != null) {
                  try {
                     configFile.save();
                  } catch (Exception e22) {
                     archiveHelper92.getPlatform().getLogger().severe("[!!] Could not save messages.yml.");
                     archiveHelper92.getConfigModule().handleConfigError(e22);
                  }
               }

               Message a2_f2 = Message.BROADCAST_UNMUTE;
               Message message3 = Message.BROADCAST_UNWARN;
               if (StringsKt.containsWithDefaults((CharSequence)a2_f2, (CharSequence)"$bannedPlayer", false, 2, null)
                  || StringsKt.containsWithDefaults((CharSequence)message3, (CharSequence)"$bannedPlayer", false, 2, null)) {
                  a2_f2.c3_f = a2_f2.computeText("$bannedPlayer", "$mutedPlayer");
                  message3.c3_f = message3.computeText("$bannedPlayer", "$warnedPlayer");
               }

               try {
                  Durations.formatDurationWithDefaults(DurationUnit.DURATIONS, 0L, false, 2, null);
               } catch (Exception e32) {
                  if (!(e32 instanceof IllegalFormatException)) {
                     throw e32;
                  }

                  String text32 = "%d %s";
                  Logger logger22 = archiveHelper92.getPlatform().getLogger();
                  CharSequence message4 = TextUtil.computeMessage(
                     "Duration format (\"" + Message.DURATION_FORMAT + "\") is invalid! (" + e32.getClass().getSimpleName() + ')',
                     "Using default format instead (\"" + text32 + "\")."
                  );
                  logger22.warning(message4.toString());
                  Message.DURATION_FORMAT.handleMessage(text32);
               }

               archiveHelper = archiveHelper92;
            }

            this.setArchiveHelper9(archiveHelper);
         });
         module.setDatabaseType(this.getSettings().getSqlDriver());
         Intrinsics.checkNotNull(platform);
         TemplateManager templateManager = new TemplateManager(platform, null, false, 6, null);

         TemplateManager templateManager2;
         label528: {
            try {
               templateManager.getPlatform().saveResource("templates.yml");
               ConfigFile throwIfFailed2 = templateManager.getPlatform().loadConfigFile(templateManager.getFolder()).throwIfFailed();
               throwIfFailed2.computeConfigFile(ConfigFile.computeText("templates.yml", null));
               templateManager.setConfig(throwIfFailed2);
               templateManager.getTemplates().clear();
               ConfigFile config = templateManager.getConfig();
               Intrinsics.checkNotNull(config);
               Iterable iterable = PunishmentType.PUNISHMENT_TYPE_COMPANION.obtainList();
               var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

               for (Object obj : iterable) {
                  PunishmentType punishmentType2 = (PunishmentType)obj;
                  arrayList.add(TuplesKt.computePair(punishmentType2, config.getSection(punishmentType2 + "-templates")));
               }

               for (Pair pair : (Iterable<Pair>) arrayList) {
                  byte b2 = 0;
                  ConfigKeyReader configKeyReader = (ConfigKeyReader)pair.getSecond();
                  if (configKeyReader != null) {
                     Collection keys = configKeyReader.getKeys(false);
                     Collection templates = templateManager.getTemplates();
                     Intrinsics.checkNotNull(keys);
                     Iterable iterable2 = keys;
                     var arrayList2 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable2, 10));

                     for (Object obj2 : iterable2) {
                        String string = (String)obj2;
                        byte b3 = b2;
                        b2 = UByte.constructorImpl((byte)(b3 + 1));
                        PunishmentType punishmentType3 = (PunishmentType)pair.getFirst();
                        ConfigKeyReader configKeyReader2 = (ConfigKeyReader)pair.getSecond();
                        Intrinsics.checkNotNull(configKeyReader2);
                        ConfigKeyReader section = configKeyReader2.getSection(string);
                        Intrinsics.checkNotNull(string);
                        if (section == null) {
                           String text = "Empty template: '" + string + '\'';
                           throw new IllegalArgumentException(text.toString());
                        }

                        if (StringsKt.containsWithDefaults(string, ' ', false, 2, null) || StringsKt.containsWithDefaults(string, '.', false, 2, null)) {
                           String text2 = "Template name '" + string + "' cannot include dots or spaces! Use dashes or underscores instead.";
                           throw new IllegalArgumentException(text2.toString());
                        }

                        String text3 = "";
                        if (section == null) {
                           String text4 = punishmentType3 + " template '" + text3 + "' has invalid section.";
                           throw new IllegalArgumentException(text4.toString());
                        }

                        Collection keys2 = section.getKeys(false);
                        Long boxed = null;
                        String text5 = null;
                        String text6 = null;
                        String text7 = null;
                        String text8 = null;
                        List list = CollectionsKt.emptyList();
                        List list2 = CollectionsKt.emptyList();
                        Intrinsics.checkNotNull(keys2);
                        Iterable iterable3 = keys2;

                        for (Object obj3 : iterable3) {
                           String string2 = (String)obj3;
                           if (string2 != null) {
                              switch (string2.hashCode()) {
                                 case -1992012396:
                                    if (string2.equals("duration")) {
                                       boxed = (long)Durations.parseDurationWithDefaults(DurationUnit.DURATIONS, section.getString(string2), 0.0, 2, null);
                                    }
                                    break;
                                 case -1618876223:
                                    if (string2.equals("broadcast")) {
                                       text7 = section.getString(string2);
                                    }
                                    break;
                                 case -1161803523:
                                    if (string2.equals("actions")) {
                                       list = templateManager.computeList(section, string2);
                                    }
                                    break;
                                 case -934964668:
                                    if (string2.equals("reason")) {
                                       text6 = section.getString(string2);
                                    }
                                    break;
                                 case -517618225:
                                    if (string2.equals("permission")) {
                                       text8 = section.getString(string2);
                                    }
                                    break;
                                 case 97513095:
                                    if (string2.equals("flags")) {
                                       list2 = templateManager.computeList(section, string2);
                                    }
                                    break;
                                 case 954925063:
                                    if (string2.equals("message")) {
                                       text5 = section.getString(string2);
                                    }
                              }
                           }
                        }

                        TemplateInfo templateInfo = ConfigModuleSwitchMap.value[punishmentType3.ordinal()] == 1
                           ? new TemplateInfo(text5, text6, text7, text8, list, list2)
                           : new LongHolder(boxed, text5, text6, text7, text8, list, list2);
                        configKeyReader2 = section.getSection("ladder");
                        List list3;
                        if (configKeyReader2 == null) {
                           list3 = CollectionsKt.emptyList();
                        } else {
                           ConfigKeyReader configKeyReader3 = configKeyReader2;
                           Iterable keys3 = configKeyReader3.getKeys(false);
                           var arrayList3 = new ArrayList(CollectionsKt.computeIntValue((Iterable)keys3, 10));

                           for (Object obj4 : keys3) {
                              String string3 = (String)obj4;
                              arrayList3.add(configKeyReader3.getSection(string3));
                           }

                           Iterable iterable4 = arrayList3;
                           var arrayList4 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList3, 10));

                           for (Object obj5 : iterable4) {
                              ConfigKeyReader configKeyReader4 = (ConfigKeyReader)obj5;
                              if (configKeyReader4 == null) {
                                 String text9 = punishmentType3 + " template '" + string + "' has invalid section.";
                                 throw new IllegalArgumentException(text9.toString());
                              }

                              Collection keys4 = configKeyReader4.getKeys(false);
                              Long boxed2 = null;
                              String text10 = null;
                              String text11 = null;
                              String text12 = null;
                              String text13 = null;
                              List list4 = CollectionsKt.emptyList();
                              List list5 = CollectionsKt.emptyList();
                              Intrinsics.checkNotNull(keys4);
                              Iterable iterable5 = keys4;

                              for (Object obj6 : iterable5) {
                                 String string4 = (String)obj6;
                                 if (string4 != null) {
                                    switch (string4.hashCode()) {
                                       case -1992012396:
                                          if (string4.equals("duration")) {
                                             boxed2 = (long)Durations.parseDurationWithDefaults(DurationUnit.DURATIONS, configKeyReader4.getString(string4), 0.0, 2, null);
                                          }
                                          break;
                                       case -1618876223:
                                          if (string4.equals("broadcast")) {
                                             text12 = configKeyReader4.getString(string4);
                                          }
                                          break;
                                       case -1161803523:
                                          if (string4.equals("actions")) {
                                             list4 = templateManager.computeList(configKeyReader4, string4);
                                          }
                                          break;
                                       case -934964668:
                                          if (string4.equals("reason")) {
                                             text11 = configKeyReader4.getString(string4);
                                          }
                                          break;
                                       case -517618225:
                                          if (string4.equals("permission")) {
                                             text13 = configKeyReader4.getString(string4);
                                          }
                                          break;
                                       case 97513095:
                                          if (string4.equals("flags")) {
                                             list5 = templateManager.computeList(configKeyReader4, string4);
                                          }
                                          break;
                                       case 954925063:
                                          if (string4.equals("message")) {
                                             text10 = configKeyReader4.getString(string4);
                                          }
                                    }
                                 }
                              }

                              arrayList4.add(
                                 ConfigModuleSwitchMap.value[punishmentType3.ordinal()] == 1
                                    ? new TemplateInfo(text10, text11, text12, text13, list4, list5)
                                    : new LongHolder(boxed2, text10, text11, text12, text13, list4, list5)
                              );
                           }

                           list3 = CollectionsKt.reversed((Iterable) arrayList4);
                        }

                        List list6 = list3;
                        long duration = (long)Durations.parseDurationWithDefaults(DurationUnit.DURATIONS, section.getString("expire_ladder", "0"), 0.0, 2, null);
                        boolean boolean2 = section.getBoolean("ip_template", false);
                        arrayList2.add(new Template(b3, string, punishmentType3, templateInfo, boolean2, list6, duration, null));
                     }

                     CollectionsKt.checkCollection(templates, (Iterable)arrayList2);
                  }
               }

               templateManager.getPlatform().getLogger().info("Loaded " + templateManager.getTemplates().size() + " templates from templates.yml!");
               ConfigKeyReader section2 = config.getSection("template-groups");
               if (section2 != null) {
                  Collection keys5 = section2.getKeys(false);
                  Collection groups = templateManager.getGroups();
                  Intrinsics.checkNotNull(keys5);
                  Iterable iterable6 = keys5;
                  Collection collection = groups;
                  var arrayList5 = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable6, 10));

                  for (Object obj7 : iterable6) {
                     String string5 = (String)obj7;
                     ConfigKeyReader section3 = section2.getSection(string5);
                     Intrinsics.checkNotNull(string5);
                     ConfigKeyReader configKeyReader5 = section3 != null ? section3.getSection("weights") : null;
                     if (section3 == null) {
                        String text14 = "Empty template group: '" + string5 + '\'';
                        throw new IllegalArgumentException(text14.toString());
                     }

                     if (configKeyReader5 == null) {
                        String text15 = "No weights for template group '" + string5 + "'!";
                        throw new IllegalArgumentException(text15.toString());
                     }

                     PunishmentType[] punishmentTypes = PunishmentType.values();
                     byte b4 = 0;

                     label458: {
                        for (int i = punishmentTypes.length; b4 < i; b4 += 1) {
                           PunishmentType punishmentType4 = punishmentTypes[b4];
                           String string6 = section3.getString("type", "");
                           String text16;
                           if (string6 != null) {
                              text16 = string6.toLowerCase(Locale.ROOT);
                           } else {
                              text16 = null;
                           }

                           if (Intrinsics.areEqual(text16, punishmentType4.toString())) {
                              punishmentType = punishmentType4;
                              break label458;
                           }
                        }

                        punishmentType = null;
                     }

                     PunishmentType punishmentType5 = punishmentType;
                     if (punishmentType5 == null || punishmentType5 != punishmentType5.obtainPunishmentType()) {
                        String text17 = "Template group '" + string5 + "' type is missing: " + punishmentType5;
                        throw new IllegalArgumentException(text17.toString());
                     }

                     ConfigKeyReader section4 = section3.getSection("ladder");
                     List list7;
                     if (section4 == null) {
                        list7 = CollectionsKt.emptyList();
                     } else {
                        Iterable keys6 = section4.getKeys(false);
                        var arrayList6 = new ArrayList(CollectionsKt.computeIntValue((Iterable)keys6, 10));

                        for (Object obj8 : keys6) {
                           String string7 = (String)obj8;
                           arrayList6.add(section4.getSection(string7));
                        }

                        Iterable iterable7 = arrayList6;
                        var arrayList7 = new ArrayList(CollectionsKt.computeIntValue((Iterable)arrayList6, 10));

                        for (Object obj9 : iterable7) {
                           ConfigKeyReader configKeyReader6 = (ConfigKeyReader)obj9;
                           if (configKeyReader6 == null) {
                              String text18 = punishmentType5 + " template '" + string5 + "' has invalid section.";
                              throw new IllegalArgumentException(text18.toString());
                           }

                           Collection keys7 = configKeyReader6.getKeys(false);
                           Long boxed3 = null;
                           String text19 = null;
                           String text20 = null;
                           String text21 = null;
                           String text22 = null;
                           List list8 = CollectionsKt.emptyList();
                           List list9 = CollectionsKt.emptyList();
                           Intrinsics.checkNotNull(keys7);
                           Iterable iterable8 = keys7;

                           for (Object obj10 : iterable8) {
                              String string8 = (String)obj10;
                              if (string8 != null) {
                                 switch (string8.hashCode()) {
                                    case -1992012396:
                                       if (string8.equals("duration")) {
                                          boxed3 = (long)Durations.parseDurationWithDefaults(DurationUnit.DURATIONS, configKeyReader6.getString(string8), 0.0, 2, null);
                                       }
                                       break;
                                    case -1618876223:
                                       if (string8.equals("broadcast")) {
                                          text21 = configKeyReader6.getString(string8);
                                       }
                                       break;
                                    case -1161803523:
                                       if (string8.equals("actions")) {
                                          list8 = templateManager.computeList(configKeyReader6, string8);
                                       }
                                       break;
                                    case -934964668:
                                       if (string8.equals("reason")) {
                                          text20 = configKeyReader6.getString(string8);
                                       }
                                       break;
                                    case -517618225:
                                       if (string8.equals("permission")) {
                                          text22 = configKeyReader6.getString(string8);
                                       }
                                       break;
                                    case 97513095:
                                       if (string8.equals("flags")) {
                                          list9 = templateManager.computeList(configKeyReader6, string8);
                                       }
                                       break;
                                    case 954925063:
                                       if (string8.equals("message")) {
                                          text19 = configKeyReader6.getString(string8);
                                       }
                                 }
                              }
                           }

                           arrayList7.add(
                              ConfigModuleSwitchMap.value[punishmentType5.ordinal()] == 1
                                 ? new TemplateInfo(text19, text20, text21, text22, list8, list9)
                                 : new LongHolder(boxed3, text19, text20, text21, text22, list8, list9)
                           );
                        }

                        list7 = CollectionsKt.reversed((Iterable) arrayList7);
                     }

                     List list10 = list7;
                     long duration2 = (long)Durations.parseDurationWithDefaults(DurationUnit.DURATIONS, section3.getString("expire_ladder", "0"), 0.0, 2, null);
                     if (list10.isEmpty()) {
                        String text23 = "No ladder for template group '" + string5 + "'!";
                        throw new IllegalArgumentException(text23.toString());
                     }

                     Iterable keys8 = configKeyReader5.getKeys(false);
                     LinkedHashMap linkedHashMap = new LinkedHashMap(KotlinRangesKtSub.coerceAtLeast(KotlinStub15.computeIntValue(CollectionsKt.computeIntValue((Iterable)keys8, 10)), 16));

                     for (Object obj11 : keys8) {
                        Map map = linkedHashMap;
                        String string9 = (String)obj11;
                        Float double2 = (float)configKeyReader5.getDouble(string9);
                        map.put(obj11, double2);
                     }

                     Map map2 = linkedHashMap;
                     Map linkedHashMap2 = new LinkedHashMap(KotlinStub15.computeIntValue(map2.size()));
                     Iterable iterable9 = map2.entrySet();

                     for (Object obj12 : iterable9) {
                        Entry entry = (Entry)obj12;
                        Template template = templateManager.computeTemplate(punishmentType5, (String)entry.getKey());
                        if (template == null) {
                           String text24 = "Template group '" + string5 + "' refers to non-existing " + punishmentType5 + " template: '" + (String)entry.getKey() + '\'';
                           throw new IllegalArgumentException(text24.toString());
                        }

                        if (!template.getEscalation().isEmpty()) {
                           templateManager.getPlatform()
                              .getLogger()
                              .warning(
                                 "Template '"
                                    + (String)entry.getKey()
                                    + "' belongs to template group '"
                                    + string5
                                    + "', its ladder will be ignored. Remove the ladder from '"
                                    + (String)entry.getKey()
                                    + "' to silence this warning."
                              );
                        }

                        Entry entry2 = (Entry)obj12;
                        Object value = entry2.getValue();
                        linkedHashMap2.put(template, value);
                     }

                     if (linkedHashMap2.isEmpty()) {
                        String text25 = "No weights for template group '" + string5 + "'!";
                        throw new IllegalArgumentException(text25.toString());
                     }

                     Iterable iterable10 = linkedHashMap2.values();
                     var arrayList8 = new ArrayList();

                     for (Object obj13 : iterable10) {
                        float floatValue2 = ((Number)obj13).floatValue();
                        if (floatValue2 <= 0.0) {
                           arrayList8.add(obj13);
                        }
                     }


                     for (Object obj14 : arrayList8) {
                        float floatValue3 = ((Number)obj14).floatValue();
                        templateManager.getPlatform().getLogger().warning("Template group '" + string5 + "' includes a bad weight: " + floatValue3);
                     }

                     Map linkedHashMap3 = new LinkedHashMap(KotlinStub15.computeIntValue(linkedHashMap2.size()));
                     Iterable iterable11 = linkedHashMap2.entrySet();

                     for (Object obj15 : iterable11) {
                        Entry entry3 = (Entry)obj15;
                        groups = (Collection)entry3.getKey();
                        Entry entry4 = (Entry)obj15;
                        Object obj16 = groups;
                        Template key = (Template)entry4.getKey();
                        linkedHashMap3.put(obj16, key);
                     }

                     Map linkedHashMap4 = new LinkedHashMap(KotlinStub15.computeIntValue(linkedHashMap3.size()));
                     Iterable iterable12 = linkedHashMap3.entrySet();

                     for (Object obj17 : iterable12) {
                        Entry entry5 = (Entry)obj17;
                        UByte uByte = UByte.box(((Template)entry5.getKey()).getId());
                        Entry entry6 = (Entry)obj17;
                        Object value2 = entry6.getValue();
                        linkedHashMap4.put(uByte, value2);
                     }

                     arrayList5.add(new TemplateGroup(string5, punishmentType5, linkedHashMap2, linkedHashMap4, list10, duration2));
                  }

                  List list11 = arrayList5;
                  templateManager.setGroups(CollectionsKt.plus(collection, list11));
               }

               if (!templateManager.getGroups().isEmpty()) {
                  templateManager.getPlatform().getLogger().info("Loaded " + templateManager.getGroups().size() + " template groups from templates.yml!");
               } else {
                  templateManager.setGroups(CollectionsKt.emptyList());
               }
            } catch (Exception e) {
               templateManager.handleException(e);
               templateManager2 = templateManager;
               break label528;
            }

            templateManager2 = templateManager;
         }

         TemplateManager templateManager3 = templateManager2;
         this.setTemplateManager4(templateManager3);
         this.setKotlinData8(new WebhookConfig(platform, null, 2, null).obtainKotlinData());
         if (platform.obtainFlag4()) {

            try {
               Object serverListPlus = this.getServerListPlus();
               if (serverListPlus != null) {
                  ServerListPlusBanProvider serverListPlusBanProvider = new ServerListPlusBanProvider(this.platform);
                  this.serverListPlusBanProvider = serverListPlusBanProvider;
                  if (!FeatureDetection.obtainFlag2()) {
                     serverListPlusBanProvider.handleObj(serverListPlus);
                  }
               }
            } catch (Throwable e2) {
               PluginModule pluginModule = this;
               CharSequence message = TextUtil.computeMessage("Failed to hook into ServerListPlus.", "Try using the latest ServerListPlus development build.");
               Logger logger = pluginModule.platform.getLogger();
               logger.warning(message.toString());
               if (this.isDebugAtLeast(1)) {
                  e2.printStackTrace();
               }
            }
         } else {
            platform.runSync(() -> {
               try {
                  Object serverListPlus2 = this.getServerListPlus();
                  if (serverListPlus2 != null) {
                     ServerListPlusBanProvider serverListPlusBanProvider2 = new ServerListPlusBanProvider(this.platform);
                     this.serverListPlusBanProvider = serverListPlusBanProvider2;
                     if (!FeatureDetection.obtainFlag2()) {
                        serverListPlusBanProvider2.handleObj(serverListPlus2);
                     }
                  }
               } catch (Throwable e4) {
                  PluginModule pluginModule2 = this;
                  CharSequence message2 = TextUtil.computeMessage("Failed to hook into ServerListPlus.", "Try using the latest ServerListPlus development build.");
                  Logger logger2 = pluginModule2.platform.getLogger();
                  logger2.warning(message2.toString());
                  if (this.isDebugAtLeast(1)) {
                     e4.printStackTrace();
                  }
               }
            });
         }

         Settings settings = this.getSettings();
         if (settings.isGeoipEnable()) {
            File dataFolder = this.platform.getDataFolder();
            String text26 = "geoip";
            File file = FileUtils.computeFile3(new File(dataFolder, text26));
            this.platform.execute(new KotlinRunnableImpl2(this, file, settings));
         }

         try {
            this.enabled = true;
            BroadcastModule module2 = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
            File dataFolder2 = this.platform.getDataFolder();
            String text27 = "lockdown.yml";
            File file2 = new File(dataFolder2, text27);
            if (!file2.exists() || YamlConfigFile.obtainArchiveHelper3() == null) {
               File dataFolder3 = this.platform.getDataFolder();
               String text28 = "lockdown.json";
               file2 = new File(dataFolder3, text28);
            }

            if (file2.exists()) {
               ConfigFile throwIfFailed3 = this.platform.loadConfigFile(file2).throwIfFailed();
               String string10 = throwIfFailed3.getString("scope", "*");
               String string11 = throwIfFailed3.getString("reason", "");
               if (this.getSettings().isLockdownPersistEnabled()) {
                  Intrinsics.checkNotNull(string11);
                  if (string11.length() > 0) {
                     this.platform.getLogger().info("Applying saved lockdown, reason: \"" + string11 + '"');
                     Intrinsics.checkNotNull(string10);
                     module2.handleText(string10, string11, true);
                  }
               }
            }

            this.perform6();
            String text29 = BuildInfoUtil.obtainText2();
            String version = this.platform.getVersion();
            if (!Intrinsics.areEqual(text29, version)) {
               String text30 = "This is LiteBans version " + text29 + ", NOT version " + version + '!';
               Runnable configErrorTask = new ConfigErrorTask(this, text30);
               configErrorTask.run();
               this.platform.runAsyncLater(configErrorTask, 20L);
            }

            if (this.isVerbose()) {
               platform.runAsyncLater(() -> {
                  try {
                     BroadcastModule module3 = (BroadcastModule)platform.getModule(BroadcastModule.class);
                     int value3 = platform.getCommandManager().getCollection4().size();
                     if (value3 <= 8) {
                        Integer boxed4 = value3;
                        throw new IllegalArgumentException(boxed4.toString());
                     }

                     ClassUtil classUtil = platform.getClassUtil();
                     Class[] classArray = new Class[]{Platform.class};
                     Object[] objs = new Object[]{platform};
                     List list12 = classUtil.computeList(ImportSource.class, classArray, objs);
                     if (list12.size() <= 1) {
                        Intrinsics.checkNotNull(list12);
                        throw new IllegalArgumentException(list12.toString());
                     }

                     DatabaseModule module22 = (DatabaseModule)platform.getModule(DatabaseModule.class);

                     try {
                        DatabaseSession session = module22.openSession();
                        Throwable error = null;

                        try {
                           ResultSet resultSet = SqlUtil.computeResultSet(session.prepare("SELECT NOW() as now"));
                           resultSet.next();
                           Timestamp timestamp = resultSet.getTimestamp("now");
                           PluginModule pluginModule2 = this;
                           String text31 = "t: " + timestamp.getTime();
                           pluginModule2.platform.getLogger().info(text31);
                           PluginModule pluginModule22 = this;
                           String text210 = "f: " + module3.formatDate(timestamp.getTime(), true) + " +" + module3.getTimezoneOffset(true);
                           pluginModule22.platform.getLogger().info(text210);
                           Intrinsics.checkNotNull(platform);
                           DupeIpAlertTask kotlinCommandTaskSub12 = new DupeIpAlertTask(platform, "", "", "");
                           LoginRecord loginRecord = session.findLoginRecord("[CONSOLE]");
                           Intrinsics.checkNotNull(loginRecord);
                           String uuid = loginRecord.getUuid();
                           Intrinsics.checkNotNull(uuid);
                           CharSequence message2 = kotlinCommandTaskSub12.computeMessage(session, loginRecord, new DupeIpScanTask(uuid, session, null, false, 0, 28, null).obtainPunishmentApplyTask());
                           if (StringsKt.containsWithDefaults((CharSequence)message2, (CharSequence)"))", false, 2, null)) {
                              throw new IllegalArgumentException(message2.toString());
                           }

                           session.getHistory(loginRecord.getUuid(), kotlinCommandTaskSub12.getText57(), PunishmentType.BAN, ServerScopes.SERVER_SCOPE.getLocalScope(), false, 1);
                           session.getHistory(loginRecord.getUuid(), kotlinCommandTaskSub12.getText57(), PunishmentType.BAN, ServerScopes.SERVER_SCOPE.getLocalScope(), true, 1);
                           String text32 = "*";
                           String normalize2 = ServerScopes.SERVER_SCOPE.normalize("global");
                           if (!ServerScopes.SERVER_SCOPE.isGlobal(normalize2)) {
                              String text42 = normalize2 + ' ' + ServerScopes.SERVER_SCOPE.getLocalScope();
                              throw new IllegalArgumentException(text42.toString());
                           }

                           if (!Intrinsics.areEqual(normalize2, text32) || normalize2.charAt(0) != text32.charAt(0)) {
                              String text52 = normalize2 + ' ' + text32;
                              throw new IllegalArgumentException(text52.toString());
                           }

                           LiteCommand liteCommand = platform.getCommandManager().computeLiteCommand("ban");
                           Intrinsics.checkNotNull(liteCommand);
                           CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
                           LiteSender console = platform.getConsole();

                           try {
                              commandOutput.getSenderHolder().set(console);
                              LiteSender console2 = platform.getConsole();
                              String[] texts = new String[]{"test", "server:*"};
                              liteCommand.execute(console2, texts);
                           } finally {
                              commandOutput.getSenderHolder().remove();
                           }

                           LoginRecord loginRecord2 = session.findLoginRecord("test");
                           Intrinsics.checkNotNull(loginRecord2);
                           String uuid2 = loginRecord2.getUuid();
                           Punishment punishment = PunishmentQueries.findPunishmentWithDefaults(session, uuid2, null, PunishmentType.BAN, text32, false, false, 48, null);
                           if (punishment == null || !Intrinsics.areEqual(punishment.getServerScope(), text32)) {
                              String text62 = String.valueOf(punishment);
                              throw new IllegalArgumentException(text62.toString());
                           }

                           if (platform.getPlatformId() == 0 && Intrinsics.areEqual(platform.getPlayerModule().getClass(), BukkitPlayerModule.class)) {
                              String text72 = "Failed requirement.";
                              throw new IllegalArgumentException(text72.toString());
                           }

                           PluginModule pluginModule3 = this;
                           String text82 = "" + platform.getConsole().getHandle();
                           pluginModule3.platform.getLogger().info(text82);
                        } catch (Throwable e22) {
                           error = e22;
                           throw SneakyThrow.<RuntimeException>rethrow(e22);
                        } finally {
                           CloseableKt.handleCloseable(session, error);
                        }
                     } catch (Throwable e32) {
                        if (!(e32 instanceof SQLException)) {
                           throw SneakyThrow.<RuntimeException>rethrow(e32);
                        }
                        SQLException sqlException = (SQLException) e32;
                        if (!module22.isConnectionFailure(sqlException)) {
                           throw sqlException;
                        }
                     }
                  } catch (SQLException e4) {
                     throw SneakyThrow.<RuntimeException>rethrow(e4);
                  }
               }, 140L);
            }

            if (platform.getPlatformId() == 0) {
               platform.runAsyncLater(() -> {
                  try {
                     try {
                        PlatformBridge bridge = platform.createBridge();
                        Intrinsics.checkNotNullExpressionValue(bridge, "");
                        VaultBridge vault = ((BukkitBridge)bridge).getVault();
                        Object permission = vault.getPermission();
                        Object chat = vault.getChat();
                        DatabaseModule module3 = (DatabaseModule)platform.getModule(DatabaseModule.class);

                        try {
                           DatabaseSession session = module3.openSession();
                           Throwable error = null;

                           try {
                              if (session.isOpen()) {
                                 PunishmentTable punishmentTable = PunishmentTable.HISTORY;
                                 CharSequence message2 = "uuid";
                                 ResultSet resultSet = SqlUtil.computeResultSet(session.prepare(SqlText.box(SqlText.limit(SqlText.orderByDesc(SqlText.constructorImpl("SELECT " + message2 + " FROM " + punishmentTable), "id"), 1))));
                                 byte b = 1;
                                 String text31;
                                 if (resultSet.next()) {

                                    String text210;
                                    try {
                                       text210 = resultSet.getString(b);
                                    } catch (SQLException e22) {
                                       text210 = null;
                                    }

                                    text31 = text210;
                                 } else {
                                    text31 = null;
                                 }

                                 String text32 = text31;
                                 if (TargetParser.isUuid(text32)) {
                                    UUID fromString2 = UUID.fromString(TargetParser.TARGET_PARSER.normalizeNullable(text32));
                                    BukkitBridge bridge2 = (BukkitBridge) platform.createBridge();
                                    Intrinsics.checkNotNullExpressionValue(bridge2, "");
                                    bridge2 = bridge2;
                                    Intrinsics.checkNotNull(fromString2);
                                    VaultBridge.hasPermissionWithDefaults(vault, bridge2.getOfflinePlayer(fromString2), "test", null, 4, null);
                                    vault.getPrefix(fromString2);
                                 }
                              }

                           } catch (Throwable e32) {
                              error = e32;
                              throw SneakyThrow.<RuntimeException>rethrow(e32);
                           } finally {
                              CloseableKt.handleCloseable(session, error);
                           }
                        } catch (Throwable e4) {
                           if (!(e4 instanceof SQLException)) {
                              throw SneakyThrow.<RuntimeException>rethrow(e4);
                           }
                           SQLException sqlException = (SQLException) e4;
                           if (!module3.isConnectionFailure(sqlException)) {
                              throw sqlException;
                           }
                        }

                        if (this.isVerbose()) {
                           this.logDebug("" + permission + chat + '\n');
                        }
                     } catch (LinkageError e5) {
                        handleConfigModule(this, e5, 0, 2, null);
                     }
                  } catch (SQLException e6) {
                     throw SneakyThrow.<RuntimeException>rethrow(e6);
                  }
               }, 300L);
            }
         } catch (Throwable e3) {
            this.handleConfigError(e3);
         }
      }
   }

   private final void perform6() {

      Database database;
      Events events;
      PlayerProvider playerProvider;
      RandomID randomID;
      try {
         Database database2 = Database.get();
         Intrinsics.checkNotNullExpressionValue(database2, "");
         database = (KotlinDatabaseSub)database2;
         Events events2 = Events.get();
         Intrinsics.checkNotNullExpressionValue(events2, "");
         events = (KotlinEventsSub)events2;
         playerProvider = PlayerProvider.get();
         randomID = RandomID.get();
      } catch (MissingImplementationException e) {
         database = new KotlinDatabaseSub(this.platform);
         events = new KotlinEventsSub();
         playerProvider = new LiteBansPlayerProvider(this.platform);
         randomID = new LiteBansRandomId(this.platform);
      }

      Database.setInstance(database);
      Events.setInstance(events);
      PlayerProvider.setInstance(playerProvider);
      RandomID.setInstance(randomID);
   }

   @Override
   public void reloadConfig() {
      this.platform.saveResource("config.yml");
      this.platform.getConfigFile().reload();
      this.loadState = 0;
      this.enable();
   }

   public final void handleConfigError(@NotNull Throwable error) {
      if (error instanceof ChecksumMismatchException) {
         PluginModule pluginModule = this;
         String message = error.getMessage();
         Intrinsics.checkNotNull(message);
         pluginModule.platform.getLogger().severe(message);
      } else {
         StringWriter stringWriter = new StringWriter();
         error.printStackTrace(new PrintWriter(stringWriter));
         this.handleText(stringWriter.toString());
      }
   }

   public final void handleText(@NotNull String text) {
      CharSequence message = text;
      String[] texts = new String[]{System.lineSeparator()};
      List split = StringsKt.splitWithDefaults(message, texts, false, 0, 6, null);
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      if (split.size() > 2 && (module == null || !module.isVerbose())) {
         Iterable iterable = split;
         var arrayList = new ArrayList(CollectionsKt.computeIntValue((Iterable)iterable, 10));

         for (Object obj : iterable) {
            String string = (String)obj;
            arrayList.add(this.computeText(string));
         }

         split = arrayList;
      }

      Iterable iterable2 = split;
      var arrayList2 = new ArrayList();

      for (Object obj2 : iterable2) {
         String string2 = (String)obj2;
         if (string2.length() > 0) {
            arrayList2.add(obj2);
         }
      }


      for (Object obj3 : arrayList2) {
         String string3 = (String)obj3;
         PluginModule pluginModule = this;
         pluginModule.platform.getLogger().warning(string3);
      }
   }

   public final String computeText(@NotNull String text) {
      String text2 = text;
      CharSequence message = text2;
      int value = 0;
      int value2 = message.length() - 1;
      boolean flag = false;

      while (value <= value2) {
         int value3 = !flag ? value : value2;
         char charAt2 = message.charAt(value3);
         boolean flag2 = Intrinsics.compare(charAt2, 32) <= 0;
         if (!flag) {
            if (!flag2) {
               flag = true;
            } else {
               value++;
            }
         } else {
            if (!flag2) {
               break;
            }

            value2--;
         }
      }

      String string = message.subSequence(value, value2 + 1).toString();
      return !StringsKt.checkText(string, "at Thread.run(", false, 2, null)
            && !StringsKt.checkText(string, "at java.util.concurrent.ThreadPoolExecutor$Worker.run(", false, 2, null)
            && !StringsKt.checkText(string, "at java.util.concurrent.ThreadPoolExecutor.runWorker(", false, 2, null)
            && !StringsKt.checkText(string, DatabaseModule.DATABASE_TYPE.obtainText(), false, 2, null)
            && !StringsKt.checkText(string, DatabaseModule.DATABASE_TYPE.obtainText2(), false, 2, null)
            && !StringsKt.checkText(string, DatabaseModule.DATABASE_TYPE.obtainText3(), false, 2, null)
            && !StringsKt.checkText(string, "at sun.reflect.NativeConstructorAccessorImpl.newInstance", false, 2, null)
            && !StringsKt.checkText(string, "at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(", false, 2, null)
            && !StringsKt.checkText(string, "at java.lang.reflect.Constructor.newInstance(", false, 2, null)
         ? text
         : "";
   }

   public final void logLoadError(@NotNull String text, @NotNull Throwable error) {
      PluginModule pluginModule = this;
      String text2 = "[!!] Could not load " + text + ".yml.";
      pluginModule.platform.getLogger().severe(text2);
      String text3 = "org.bukkit.configuration.InvalidConfigurationException: ";
      String[] texts = new String[]{
         text3 + "while scanning an anchor",
         text3 + "while scanning a simple key",
         text3 + "while scanning for the next token",
         text3 + "while parsing a block mapping",
         text3 + "while parsing a block collection",
         text3
      };
      String message = error.getMessage();
      if (message == null) {
         message = "";
      }

      String text4 = message;
      int value = 0;

      for (int i = texts.length; value < i; value++) {
         String text5 = texts[value];
         text4 = StringsKt.computeText(text4, text5, "", false, 4, null);
      }

      String text6 = StringsKt.computeText(text4, "\\t(TAB)", "TAB", false, 4, null);
      PluginModule pluginModule2 = this;
      String text7 = "[!!] Configuration error found: " + text6;
      pluginModule2.platform.getLogger().severe(text7);
      PluginModule pluginModule3 = this;
      String text8 = "[!!] LiteBans will use the default " + text + " until errors have been fixed.";
      pluginModule3.platform.getLogger().severe(text8);
   }

   public final Object getServerListPlus() {
      return this.platform.createBridge().getPlugin("ServerListPlus");
   }

   public final ConfigModule requireEnabled() {
      if (!this.enabled) {
         PluginModule pluginModule = this;
         String text = this.platform.getName() + " is not enabled yet!";
         pluginModule.platform.getLogger().severe(text);
         throw new LiteException();
      } else {
         return this;
      }
   }

   public final CharSequence formatBoolean(@Nullable Object obj) {
      if (obj == null) {
         return Message.STRINGS_NULL;
      } else {
         String string = obj.toString();
         if (Intrinsics.areEqual(string, "true")) {
            return Message.STRINGS_TRUE;
         } else if (Intrinsics.areEqual(string, "false")) {
            return Message.STRINGS_FALSE;
         } else {
            return ServerScopes.SERVER_SCOPE.isGlobal(string) ? Message.STRINGS_GLOBAL : string;
         }
      }
   }

   public final String getSenderAddress(@NotNull LiteSender liteSender, @NotNull Object obj) {
      String ipAddress = liteSender.getIpAddress();
      if (ipAddress == null && !this.getSettings().isSuppressGetAddressWarning()) {
         this.platform.getLogger().warning("Player[name=" + liteSender.getName() + "].getAddress() returned null on " + obj.getClass().getSimpleName());
      }

      return ipAddress;
   }

   public final boolean isCommandEnabled(@NotNull String text) {
      if (this.getSettings().isMutesEnabled() || !StringsKt.checkText2(text, "mute", false, 2, null) && !StringsKt.checkText2(text, "muteip", false, 2, null)) {
         return !this.getSettings().isWarningsEnabled() && StringsKt.checkText2(text, "warn", false, 2, null) ? false : this.getSettings().isLockdownEnabled() || !Intrinsics.areEqual(text, "lockdown");
      } else {
         return false;
      }
   }

   public final boolean isAuthMeSupported() {
      return this.platform.getPlatformId() == 0 && this.obtainFlag2() && !this.stateFlags.isFlag3() && this.getSettings().isSupportAuthme();
   }

   public final SimpleDateFormat createDateFormat(@NotNull CharSequence message, @NotNull CharSequence message2) {
      SimpleDateFormat simpleDateFormat;
      try {
         simpleDateFormat = new SimpleDateFormat(StringsKt.computeText(message.toString(), "YYYY", "yyyy", false, 4, null), this.getMessages().getLocale());
      } catch (Exception e) {
         if (!(e instanceof LiteMessageException)) {
            PluginModule pluginModule = this;
            CharSequence message3 = TextUtil.computeMessage(
               "Invalid date format: \"" + message + "\". Using default date format \"" + message2 + "\".",
               "Date format documentation: https://docs.oracle.com/javase/tutorial/i18n/format/simpleDateFormat.html"
            );
            Logger logger = pluginModule.platform.getLogger();
            logger.warning(message3.toString());
         }

         simpleDateFormat = new SimpleDateFormat(message2.toString());
      }

      SimpleDateFormat simpleDateFormat2 = simpleDateFormat;
      simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
      return simpleDateFormat2;
   }

   public static SimpleDateFormat createDateFormatWithDefaults(ConfigModule configModule, CharSequence message, CharSequence message2, int value, Object obj) {
      if ((value & 2) != 0) {
         message2 = "yyyy-MM-dd";
      }

      return configModule.createDateFormat(message, message2);
   }

   public static final int obtainIntValue3() {
      return value32;
   }

   public static final void handleIntValue2(int value) {
      value32 = value;
   }

   public static final AtomicBoolean obtainAtomicBoolean() {
      return return7;
   }

   public static final AtomicBoolean obtainAtomicBoolean2() {
      return return8;
   }
}
