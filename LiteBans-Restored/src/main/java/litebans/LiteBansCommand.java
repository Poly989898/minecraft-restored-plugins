package litebans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;

public class LiteBansCommand extends AbstractCommand {
   final Map map2 = new HashMap();

   @Override
   public boolean obtainFlag() {
      return false;
   }

   public LiteBansCommand(Platform platform) {
      super("litebans", platform);
   }

   private final void handleLiteSender(LiteSender liteSender, CharSequence message) {
      handleLiteSender(liteSender, message, this.getPlatform());
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, String[] texts) {
      if (texts.length != 0 && !texts[0].equalsIgnoreCase("version")) {
         Platform platform = this.getPlatform();
         SyncModule module = (SyncModule)platform.getModule(SyncModule.class);
         BroadcastModule broadcastModule = this.getBroadcastModule();
         DatabaseModule databaseModule = this.getDatabaseModule();
         ConfigModule configModule = this.getConfigModule();
         PunishmentLookupModule lookupModule = this.getLookupModule();
         Settings settings = configModule.getSettings();
         String lowerCase = texts[0].toLowerCase(Locale.ENGLISH);
         if (lowerCase.equals("broadcast")) {
            new BroadcastCommandTask(texts, platform).run();
         } else if (lowerCase.equals("allow")) {
            new AllowTask(texts, platform, true).run();
         } else if (!lowerCase.equals("unlink") && !lowerCase.equals("clearhistory") && !lowerCase.equals("cleariphistory")) {
            if (lowerCase.equals("reveal") && texts.length >= 2) {
               new RevealTask(texts, platform).run();
            } else if (lowerCase.equals("license")) {
               new LicenseCommandTask(platform).run();
            } else {
               AltAccountPunisher.handleCommandTask(this, "litebans.admin");

               if (lowerCase.equals("reload")) {
                  configModule.disable();

                  try {
                     for (PluginModule pluginModule : platform.getActiveModules()) {
                        pluginModule.reloadConfig();
                     }

                     Throwable error = platform.getConfigFile().getError();
                     if (error != null) {
                        this.handleLiteSender(liteSender, Message.RELOAD_FAIL);
                        this.handleLiteSender(liteSender, Message.RELOAD_FAIL_CONFIG);
                        throw error;
                     }

                     platform.execute(() -> this.handleDatabaseModule(databaseModule, broadcastModule, configModule, liteSender));
                  } catch (Throwable e2) {
                     databaseModule.logConnectionError(e2);
                  }
               } else if (lowerCase.equals("info")) {
                  platform.execute(() -> this.handleDatabaseModule(databaseModule, liteSender, configModule, module));
               } else if (lowerCase.equals("servers")) {
                  new LiteCommandTaskSubX(platform).run();
               } else if (lowerCase.equals("accept")) {
                  new AcceptDisclaimerTask(platform).run();
               } else if (lowerCase.equals("debug")) {
                  if (texts.length >= 2) {
                     byte int2 = (byte)Integer.parseInt(texts[1]);
                     configModule.setDebugLevel(int2);
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Debug level has been set to: " + LiteColor.GOLD + int2);
                  }

                  if (configModule.isVerbose()) {
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Debug levels: " + configModule.getDebugLevel() + " (config).");
                  }
               } else if (lowerCase.equals("timezone")) {
                  SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                  TimeZone timeZone = TimeZone.getTimeZone(databaseModule.getCurrentTime());
                  Calendar instance2 = Calendar.getInstance(timeZone);
                  simpleDateFormat2.setCalendar(instance2);
                  if (texts.length == 1) {
                     this.handleLiteSender(
                        liteSender,
                        LiteColor.GREEN + "Current timezone: " + LiteColor.GOLD + databaseModule.getCurrentTime() + "\n" + LiteColor.GREEN + "Current time: " + LiteColor.GOLD + simpleDateFormat2.format(instance2.getTime())
                     );
                  } else {
                     String text4 = texts[1];
                     platform.execute(() -> this.handleDatabaseModule(databaseModule, text4, instance2, liteSender, simpleDateFormat2));
                  }
               } else if (lowerCase.equals("fixhistory") || lowerCase.equals("fixiphistory")) {
                  platform.runAsync(() -> this.handleDatabaseModule2(databaseModule, liteSender));
               } else if (lowerCase.startsWith("clearmute")) {
                  if (texts.length >= 2) {
                     UUID fromString2 = UUID.fromString(texts[1]);
                     Punishment punishment = (Punishment)lookupModule.getMuteChatTask().getMutedPlayers().remove(fromString2);
                     if (punishment != null) {
                        this.handleLiteSender(liteSender, LiteColor.GREEN + "Removed active mute for " + punishment + " successfully.");
                     } else {
                        CommandTask.COMMAND_OUTPUT.returnResult(LiteColor.RED + "No mute found for " + fromString2);
                     }
                  } else {
                     lookupModule.getMuteChatTask().getMutedPlayers().clear();
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Active mutes have been cleared.");
                  }
               } else if (lowerCase.equals("listmutes") && liteSender.isConsole()) {
                  this.handleLiteSender(liteSender, Arrays.toString(lookupModule.getMuteChatTask().getMutedPlayers().entrySet().toArray()));
               } else if (lowerCase.equals("import")) {
                  if (texts.length == 1) {
                     CommandTask.COMMAND_OUTPUT.returnResult(Message.IMPORT_USAGE);
                  }

                  String lowerCase2 = texts[1].toLowerCase(Locale.ENGLISH);
                  if (lowerCase2.equals("start")) {
                     platform.execute(() -> this.handleSettings(settings, databaseModule, liteSender, platform, configModule));
                  }
               } else if (lowerCase.equals("reset-database")) {
                  new ResetDatabaseTask(texts, platform).run();
               } else if (lowerCase.equals("reset-templates")) {
                  new LiteCommandTaskSub(platform).run();
               } else if (lowerCase.equals("addhistory") || lowerCase.equals("addiphistory") || lowerCase.equals("add-login")) {
                  new AddLoginTask(texts, platform).run();
               } else if (lowerCase.equals("json-disconnect") && texts.length >= 3) {
                  String text5 = texts[1];
                  String text6 = KotlinUtil32.computeText(Arrays.copyOfRange(texts, 2, texts.length), " ");
                  if (configModule.isVerbose()) {
                     configModule.logDebug("target:" + text5 + ", reason:" + text6);
                  }

                  LiteSender targetSender = AltAccountPunisher.findTargetSender(this, text5);
                  if (targetSender != null) {
                     switch (platform.getPlatformId()) {
                        case 0:
                           LockdownState.LOCKDOWN_STATE.handleObj(targetSender.getHandle(), text6);
                           break;
                        case 1:
                           BungeeBridge bridge = (BungeeBridge)platform.createBridge();
                           bridge.handleLiteSender(targetSender, text6);
                           break;
                        default:
                           throw new UnsupportedOperationException();
                     }
                  }
               } else {
                  if (lowerCase.equals("test-vault") && texts.length >= 2) {
                     String text7 = texts[1];
                     UUID fromString3 = UUID.fromString(text7);
                     String text8;
                     if (texts.length == 3) {
                        text8 = texts[2];
                     } else {
                        text8 = null;
                     }

                     if (platform.getPlatformId() == 0) {
                        platform.execute(() -> this.handlePlatform(platform, fromString3, text8, liteSender));
                        return;
                     }

                     CommandTask.COMMAND_OUTPUT.returnResult("Command is not supported on this platform.");
                  }

                  if (lowerCase.equals("refresh") && texts.length >= 2) {
                     String text9 = UsernameValidator.KOTLIN_DATA.computeText3(texts[1]);
                     String text10 = null;
                     if (texts.length >= 3) {
                        text10 = texts[2];
                     }

                     LiteSender onlineSender = broadcastModule.findOnlineSender(text9);
                     String text11 = configModule.settings.getServerName();
                     if (onlineSender != null) {
                        String serverName = onlineSender.getServerName();
                        if (serverName != null) {
                           text11 = serverName;
                        }
                     }

                     String text12 = null;
                     if (onlineSender != null) {
                        text12 = onlineSender.getName();
                     }

                     lookupModule.handleText(text12, text9, text10, text11);
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Refreshed player: '" + text9 + "', server: " + text11);
                  } else if (lowerCase.equals("expiry-update")) {
                     platform.execute(() -> {
                        try {
                           DatabaseSession session = databaseModule.openSession();

                           try {
                              session.updateExpired(true);
                           } catch (Throwable e) {
                              if (session != null) {
                                 try {
                                    session.close();
                                 } catch (Throwable e22) {
                                    e.addSuppressed(e22);
                                 }
                              }

                              throw e;
                           }

                           if (session != null) {
                              session.close();
                           }
                        } catch (Exception e3) {
                           databaseModule.logConnectionError(e3);
                        }
                     });
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Expiry update executed.");
                  } else if (lowerCase.equals("upgrade")) {
                     this.handleLiteSender2(liteSender);
                  } else if (lowerCase.equals("benchgen") && liteSender.isConsole() && configModule.isVerbose()) {
                     new BenchGenTask(texts, platform).run();
                  } else if (lowerCase.equals("benchjoin") && liteSender.isConsole() && configModule.isVerbose()) {
                     new BenchJoinTask(texts, platform).run();
                  } else {
                     this.handleLiteSender3(liteSender);
                  }
               }
            }
         } else if (texts.length >= 2) {
            new UnlinkTask(texts, platform).run();
         }
      } else {
         this.handleLiteSender3(liteSender);
      }
   }

   @Override
   public boolean obtainFlag7() {
      return false;
   }

   @Override
   public String getPermission() {
      return null;
   }

   public void handleText2(String text) {
      this.getPlatform().getLogger().info("Begin import from: " + text);
   }

   public void handleLiteSender2(LiteSender liteSender) {
      DatabaseModule module = (DatabaseModule)this.getPlatform().getModule(DatabaseModule.class);
      this.getPlatform().execute(() -> this.handleDatabaseModule(module, liteSender));
   }

   void handleDatabaseModule(DatabaseModule databaseModule, LiteSender liteSender, boolean flag) {
      try {
         DatabaseSession session = databaseModule.openSession();

         label96: {
            try {
               if (session.isClosed()) {
                  break label96;
               }

               for (PunishmentTable punishmentTable : PunishmentTable.values()) {
                  if (punishmentTable.obtainFlag()) {
                     long value = 0L;
                     if (!flag) {
                        this.handleLiteSender(liteSender, Message.FIX_HISTORY.computeText("table", punishmentTable.toString()));
                     }

                     PreparedQuery prepare2 = session.prepare("SELECT * FROM " + punishmentTable);
                     ResultSet executeQueryKeepOpen2 = databaseModule.executeQueryKeepOpen(prepare2);
                     int value2 = 0;

                     for (Punishment punishment : (Iterable<Punishment>) session.readPunishments(executeQueryKeepOpen2, punishmentTable.obtainPunishmentType())) {
                        String uuid = punishment.getUuid();
                        if (uuid != null) {
                           if (UsernameValidator.KOTLIN_DATA.checkText(uuid)) {
                              value++;
                           } else {
                              LoginRecord loginRecord = session.findLoginRecord(uuid);
                              if (loginRecord == null) {
                                 String text = UsernameValidator.KOTLIN_DATA.computeText(uuid);
                                 if (text != null) {
                                    session.saveLogin(text, uuid, "#", false, true);
                                    value2++;
                                 }
                              }
                           }
                        }
                     }

                     if (!flag) {
                        this.handleLiteSender(liteSender, Message.FIX_HISTORY_RESULT.computeText("amount", value2));
                     }

                     if (value > 0L && !flag) {
                        this.handleLiteSender(liteSender, Message.FIX_HISTORY_OFFLINE_UUIDS.computeText("amount", value));
                     }
                  }
               }
            } catch (Throwable e) {
               if (session != null) {
                  try {
                     session.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (session != null) {
               session.close();
            }

            return;
         }

         if (session != null) {
            session.close();
         }
      } catch (Exception e3) {
         databaseModule.logConnectionError(e3);
      }
   }

   void handleLiteSender3(LiteSender liteSender) {
      ConfigModule module = (ConfigModule)this.getPlatform().getModule(ConfigModule.class);
      String text;
      switch (this.getPlatform().getPlatformId()) {
         case 0:
            if (FeatureDetection.obtainFlag2()) {
               text = "Folia";
            } else if (FeatureDetection.obtainFlag3()) {
               text = "Paper";
            } else {
               text = "Bukkit";

               try {
                  Class.forName("org.spigotmc.SpigotConfig");
                  text = "Spigot";
               } catch (ClassNotFoundException e) {
               }
            }
            break;
         case 1:
            text = "BungeeCord";
            break;
         case 2:
            text = "Velocity";
            break;
         case 3:
            text = "Fabric";
            break;
         default:
            text = "Unknown";
      }

      String string = LiteData.LITE_HELPER.computeLiteData(this.getPlatform().createBridge().getServerVersion(), new Integer[]{0, 0, 0}).toString();
      this.handleLiteSender(liteSender, LiteColor.GREEN + "LiteBans version " + LiteColor.GOLD + BuildInfo.text + LiteColor.GREEN + " running on " + LiteColor.gold(text) + " " + LiteColor.lightPurple(string));
      this.handleLiteSender(
         liteSender, LiteColor.GOLD.toString() + LiteColor.UNDERLINE + "https://www.spigotmc.org/resources/litebans.3715/ {url: https://www.spigotmc.org/resources/litebans.3715/}"
      );
   }

   private final void handleDatabaseModule(DatabaseModule databaseModule, LiteSender liteSender) {
      try {
         DatabaseSession session = databaseModule.openSession();

         try {
            databaseModule.initializeSchema(session, ((DatabaseSessionImpl)session).createSchemaBuilder(), true, true);
            session.close();
            databaseModule.disable();
            databaseModule.enable();
            this.handleLiteSender(liteSender, LiteColor.GREEN + "Upgrade check executed successfully.");
         } catch (Throwable e) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable e2) {
                  e.addSuppressed(e2);
               }
            }

            throw e;
         }

         if (session != null) {
            session.close();
         }
      } catch (Exception e3) {
         databaseModule.logConnectionError(e3);
      }
   }

   private final void handlePlatform(Platform platform, UUID uuid, String text, LiteSender liteSender) {
      BukkitBridge bridge = (BukkitBridge)platform.createBridge();
      boolean hasPermission2 = bridge.getVault().hasPermission(bridge.getOfflinePlayer(uuid), "litebans.test", text);
      this.handleLiteSender(liteSender, LiteColor.GREEN + "litebans.test: " + (hasPermission2 ? LiteColor.GREEN + "true" : LiteColor.RED + "false"));
   }

   private final void handleSettings(Settings settings, DatabaseModule databaseModule, LiteSender liteSender, Platform platform, ConfigModule configModule) {
      String lowerCase = settings.getImportFrom().toLowerCase(Locale.ENGLISH);
      if (lowerCase.contains(" ")) {
         lowerCase = lowerCase.replace(" ", "");
      }

      boolean flag = lowerCase.equals("vanilla") || lowerCase.contains("banned-players") || lowerCase.contains("essentials");
      Connection connection = null;

      try {
         DatabaseSession session = databaseModule.openSession();

         label382: {
            label383: {
               label384: {
                  label385: {
                     try {
                        label361: {
                           if (session.isClosed()) {
                              this.handleLiteSender(liteSender, Message.ERROR_NO_SQL_CONNECTION);
                              break label384;
                           }

                           if (!flag) {
                              Properties properties = new Properties();
                              String text = null;
                              String text2 = null;
                              String text3 = "sqlite";
                              String text4;
                              if (settings.isImportSqlite()) {
                                 String importSqliteFile = settings.getImportSqliteFile();
                                 boolean flag2 = lowerCase.equals("litebans");
                                 if (flag2) {
                                    text3 = "h2";
                                    importSqliteFile = new File(platform.getDataFolder(), "litebans").getAbsolutePath();
                                    text = settings.getImportImportMysqlUsername();
                                    text2 = settings.getImportImportMysqlPassword();
                                 } else if (lowerCase.equals("banmanager7") || lowerCase.equals("banmanagerv7")) {
                                    text3 = "h2";
                                    importSqliteFile = new File("plugins/BanManager", "local_bans").getAbsolutePath();
                                 }

                                 File file = new File(importSqliteFile);
                                 File file2 = new File(importSqliteFile + ".mv.db");
                                 if (!flag2 && !file.exists() && !file2.exists()) {
                                    throw new FileNotFoundException(importSqliteFile);
                                 }

                                 this.handleLiteSender(liteSender, Message.IMPORT_START.computeText("db", importSqliteFile));
                                 text4 = "jdbc:" + text3 + "://" + file.getAbsolutePath();
                              } else {
                                 text3 = "mysql";
                                 text = settings.getImportImportMysqlUsername();
                                 text2 = settings.getImportImportMysqlPassword();
                                 text4 = "jdbc:mysql://" + settings.getImportImportMysqlAddress() + '/' + settings.getImportImportMysqlDatabase();
                                 this.handleLiteSender(liteSender, Message.IMPORT_START.computeText("db", text4));
                              }

                              Driver driver = (Driver)databaseModule.loadDriver(Objects.requireNonNull(databaseModule.findDriver(configModule, text3)), true, true);
                              if (driver == null) {
                                 break label385;
                              }

                              String text5 = "30000";
                              properties.put("connectTimeout", text5);
                              properties.put("socketTimeout", text5);
                              properties.put("autoReconnect", "true");
                              properties.put("useUnicode", "true");
                              properties.put("connectionCollation", "utf8mb4_unicode_ci");
                              properties.put("characterEncoding", "utf-8");
                              properties.put("characterSetResults", "utf-8");
                              properties.put("serverTimezone", "UTC");
                              properties.put("timezone", "UTC");
                              if (text != null && !text.isEmpty()) {
                                 properties.put("user", text);
                              }

                              if (text2 != null && !text2.isEmpty()) {
                                 properties.put("password", text2);
                              }

                              connection = driver.connect(text4, properties);
                           } else {
                              this.handleLiteSender(liteSender, Message.IMPORT_START.computeText("db", "banned-players.json"));
                           }

                           boolean isImportImportIpbans2 = settings.isImportImportIpbans();
                           List list = platform.getClassUtil().computeList(ImportSource.class, new Class[]{Platform.class}, new Object[]{platform});
                           boolean flag3 = false;

                           for (ImportSource punishCommandBase : (Iterable<ImportSource>) list) {
                              if (punishCommandBase.isSource(lowerCase) && (!punishCommandBase.obtainFlag5() || connection != null)) {
                                 this.handleText2(punishCommandBase.getSource());
                                 String importImportTablePrefix = configModule.settings.getImportImportTablePrefix();
                                 if (!importImportTablePrefix.equals("default")) {
                                    punishCommandBase.setTablePrefix(importImportTablePrefix);
                                 }

                                 punishCommandBase.handleText(lowerCase, connection, session, isImportImportIpbans2);
                                 flag3 = true;
                                 punishCommandBase.handleLiteSender(liteSender);
                                 if (!punishCommandBase.obtainFlag6()) {
                                    break label361;
                                 }
                                 break;
                              }
                           }

                           if (!flag3) {
                              this.handleLiteSender(liteSender, Message.IMPORT_UNSUPPORTED.computeText("name", lowerCase));
                              break label382;
                           }

                           this.handleDatabaseModule(databaseModule, liteSender, true);
                           break label383;
                        }
                     } catch (Throwable e) {
                        if (session != null) {
                           try {
                              session.close();
                           } catch (Throwable e2) {
                              e.addSuppressed(e2);
                           }
                        }

                        throw e;
                     }

                     if (session != null) {
                        session.close();
                     }

                     return;
                  }

                  if (session != null) {
                     session.close();
                  }

                  return;
               }

               if (session != null) {
                  session.close();
               }

               return;
            }

            if (session != null) {
               session.close();
            }

            return;
         }

         if (session != null) {
            session.close();
         }
      } catch (Throwable e3) {
         this.handleLiteSender(liteSender, Message.IMPORT_FAIL);
         databaseModule.logConnectionError(e3);
         return;
      } finally {
         this.map2.clear();
         if (connection != null) {
            try {
               connection.close();
            } catch (Exception e4) {
               throw new RuntimeException(e4);
            }
         }
      }
   }

   private final void handleDatabaseModule2(DatabaseModule databaseModule, LiteSender liteSender) {
      this.handleDatabaseModule(databaseModule, liteSender, false);
   }

   private final void handleDatabaseModule(DatabaseModule databaseModule, String text, Calendar calendar, LiteSender liteSender, SimpleDateFormat simpleDateFormat) {
      try {
         databaseModule.setCurrentTime(text);
         calendar.setTimeZone(TimeZone.getTimeZone(text));
         DatabaseSession session = databaseModule.openSession();

         try {
            PreparedQuery bindString2 = session.prepare("UPDATE " + PunishmentTable.CONFIG + " SET timezone=?").bindString(text);

            try {
               databaseModule.executeUpdate(bindString2);
            } catch (Throwable e) {
               if (bindString2 != null) {
                  try {
                     bindString2.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (bindString2 != null) {
               bindString2.close();
            }

            this.handleLiteSender(liteSender, LiteColor.GREEN + "Timezone updated: " + LiteColor.GOLD + databaseModule.getCurrentTime() + "\n" + LiteColor.GREEN + "Current time: " + LiteColor.GOLD + simpleDateFormat.format(calendar.getTime()));
         } catch (Throwable e3) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable e4) {
                  e3.addSuppressed(e4);
               }
            }

            throw e3;
         }

         if (session != null) {
            session.close();
         }
      } catch (Exception e5) {
         databaseModule.logConnectionError(e5);
      }
   }

   private final void handleDatabaseModule(DatabaseModule databaseModule, LiteSender liteSender, ConfigModule configModule, SyncModule syncModule) {
      try {
         DatabaseSession session = databaseModule.openSession();

         label63: {
            try {
               HikariDataSource dataSource = databaseModule.getDataSource();
               if (!session.isClosed() && dataSource != null) {
                  HikariPoolMXBean poolBInterface = dataSource.obtainHikariPoolMXBean();
                  String sqlDriver = configModule.getSettings().getSqlDriver();
                  String text = DatabaseModule.DATABASE_TYPE.computeText(sqlDriver);
                  this.handleLiteSender(liteSender, LiteColor.green("LiteBans is connected to a ") + LiteColor.gold(text) + LiteColor.green(" database."));
                  int sqlPoolMaxConnections = configModule.getSettings().getSqlPoolMaxConnections();
                  int sqlPoolMinConnections = configModule.getSettings().getSqlPoolMinConnections();
                  UserRecord serverRecord = databaseModule.getServerRecord();
                  if (serverRecord != null) {
                     String text2 = "" + LiteColor.GOLD + serverRecord.getId2();
                     if (serverRecord.obtainFlag2()) {
                        text2 = "" + LiteColor.RED + serverRecord.getId2() + "!!!";
                     }

                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Server name: " + LiteColor.GOLD + serverRecord.getName());
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Server UUID: " + LiteColor.GOLD + serverRecord.getUuid() + LiteColor.GREEN + " <" + text2 + LiteColor.GREEN + ">");
                  }

                  this.handleLiteSender(
                     liteSender,
                     LiteColor.GREEN
                        + "Pooled connections: "
                        + LiteColor.GOLD
                        + poolBInterface.getTotalConnections()
                        + LiteColor.GREEN
                        + "/"
                        + LiteColor.GOLD
                        + sqlPoolMaxConnections
                        + LiteColor.GREEN
                        + " (minimum: "
                        + LiteColor.GOLD
                        + sqlPoolMinConnections
                        + LiteColor.GREEN
                        + ", idle: "
                        + LiteColor.GOLD
                        + poolBInterface.getIdleConnections()
                        + LiteColor.GREEN
                        + ")"
                  );
                  int threadsAwaitingConnection = poolBInterface.getThreadsAwaitingConnection();
                  if (threadsAwaitingConnection > 0) {
                     this.handleLiteSender(liteSender, LiteColor.GREEN + "Threads awaiting connection: " + LiteColor.GOLD + threadsAwaitingConnection);
                  }

                  Triple triple = syncModule.obtainTriple();
                  AtomicLong atomicLong = (AtomicLong)triple.component1();
                  long value = ((AtomicLong)triple.component2()).get();
                  long value2 = Math.max(0L, ((AtomicLong)triple.component3()).get() - value);
                  this.handleLiteSender(
                     liteSender, LiteColor.GREEN + "Sync events in/out/skipped: " + LiteColor.GOLD + atomicLong + LiteColor.GREEN + ", " + LiteColor.GOLD + value + LiteColor.GREEN + ", " + LiteColor.GOLD + value2
                  );
                  break label63;
               }

               this.handleLiteSender(liteSender, Message.ERROR_NO_SQL_CONNECTION);
            } catch (Throwable e) {
               if (session != null) {
                  try {
                     session.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (session != null) {
               session.close();
            }

            return;
         }

         if (session != null) {
            session.close();
         }
      } catch (Exception e3) {
         databaseModule.logConnectionError(e3);
      }
   }

   private final void handleDatabaseModule(DatabaseModule databaseModule, BroadcastModule broadcastModule, ConfigModule configModule, LiteSender liteSender) {
      boolean flag = true;

      try {
         databaseModule.disable();
         broadcastModule.setDatabaseType(configModule.getSettings().getSqlDriver());
         databaseModule.enable();
         DatabaseSession session = databaseModule.openSession();

         label72: {
            try {
               if (session.isClosed()) {
                  this.handleLiteSender(liteSender, Message.RELOAD_FAIL_CONNECT);
                  break label72;
               }

               ConfigFile configFile = configModule.getMessages().getConfigFile();
               if (configModule.templateManager.getConfig() == null) {
                  this.handleLiteSender(liteSender, Message.RELOAD_FAIL_TEMPLATES);
                  flag = false;
               }

               if (configFile != null && configFile.getError() != null) {
                  this.handleLiteSender(liteSender, Message.RELOAD_FAIL.computeText(Message.RELOAD_FAIL_MESSAGES));
                  flag = false;
               }

               if (flag) {
                  this.handleLiteSender(liteSender, Message.RELOAD_SUCCESS);
               }
            } catch (Throwable e) {
               if (session != null) {
                  try {
                     session.close();
                  } catch (Throwable e2) {
                     e.addSuppressed(e2);
                  }
               }

               throw e;
            }

            if (session != null) {
               session.close();
            }

            return;
         }

         if (session != null) {
            session.close();
         }
      } catch (Exception e3) {
         databaseModule.logConnectionError(e3);
      }
   }
}
