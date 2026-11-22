package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayDay;
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.configuration.file.YamlConfiguration;

public class DBManager {
  private DBDAO dao;
  private CMI plugin;
  private DBManager.DataBaseType DbType = DBManager.DataBaseType.SqLite;
  boolean databaseSwitch = false;
  int autoSaveInterval = 15;
  private boolean ForceSaveOnLogOut = false;
  private boolean ForceLoadOnLogIn = false;
  private static final String fileName = "DataBaseInfo.yml";
  private String username = "";
  private String password = "";
  private String hostname = "";
  private String database = "";
  private String prefix = "";
  private boolean autoReconnect = true;
  private boolean useSSL = true;
  private boolean verifyServerCertificate = true;
  private ConcurrentHashMap<CMIUser, DBManager.InvSave> invToSave = new ConcurrentHashMap<>();
  private Set<CMIUser> playerListToSave = ConcurrentHashMap.newKeySet();
  private Set<CMIUser> playerPlayTimeRewardToSave = ConcurrentHashMap.newKeySet();
  CMITask autosaveBukkitId = null;
  CompletableFuture<Void> task = null;
  Long startedAt = 0L;
  private Runnable autoSave =
      new Runnable() {
        @Override
        public void run() {
          if (DBManager.this.task != null
              && DBManager.this.startedAt + 60000L < System.currentTimeMillis()) {
            DBManager.this.task.cancel(false);
            DBManager.this.task = null;
          }

          if (DBManager.this.task == null) {
            DBManager.this.startedAt = System.currentTimeMillis();
            DBManager.this.task =
                CMIScheduler.runTaskAsynchronously(
                    DBManager.this.plugin,
                    () -> {
                      DBManager.this.saveBatch(DBManager.this.all);
                      DBManager.this.all = false;
                    });
          }
        }
      };
  HashMap<String, CMIUser> getPlayerId = new HashMap<>();
  HashMap<Integer, CMIUser> getPlayerInvId = new HashMap<>();
  HashMap<CMIPlayDay, CMIUser> getPlayerPlayTimeId = new HashMap<>();
  HashMap<Integer, CMIUser> getPlayerPlayTimeRewardId = new HashMap<>();
  Boolean all = false;
  Integer oldRapidvalue = null;
  boolean startingDb = false;

  public DBManager(CMI cmi) {
    this.plugin = cmi;
  }

  public DBDAO getDB() {
    return this.dao;
  }

  public boolean isConnected() {
    return this.databaseSwitch && Version.isTestServer()
        ? this.plugin.getDbManager_NEW().isConnected()
        : this.getDB() != null && this.getDB().isConnected();
  }

  public void closeConnections() throws SQLException {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().closeConnections();
    } else {
      if (this.dao != null) {
        this.dao.closeConnections();
      }
    }
  }

  public void loadAllUsers() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().getDB().loadAllUsers();
    } else {
      if (this.dao != null) {
        this.dao.loadAllUsers();
      }
    }
  }

  public void loadPlayTimes() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().getDB().loadPlayTimes();
    } else {
      if (this.dao != null) {
        this.dao.loadPlayTimes();
      }
    }
  }

  public void loadPlayerPlayTimeRewards() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().getDB().loadPlayerPlayTimeRewards();
    } else {
      if (this.dao != null) {
        this.dao.loadPlayerPlayTimeRewards();
      }
    }
  }

  public CompletableFuture<Void> loadUserAsync(UUID uuid) {
    return this.databaseSwitch && Version.isTestServer()
        ? this.plugin.getDbManager_NEW().getDB().loadUser(uuid)
        : this.dao.loadUserAsync(uuid);
  }

  public String getInv(CMIUser cmiUser) {
    return this.databaseSwitch && Version.isTestServer()
        ? this.plugin.getDbManager_NEW().getDB().getSavedInventories(cmiUser).join()
        : this.dao.getInv(cmiUser);
  }

  public CompletableFuture<String> getInventoriesString(CMIUser cmiUser) {
    return this.databaseSwitch && Version.isTestServer()
        ? this.plugin.getDbManager_NEW().getDB().getSavedInventories(cmiUser)
        : this.dao.getSavedInventories(cmiUser);
  }

  public void switchDataBase() throws SQLException {
    if (this.dao != null) {
      this.dao.closeConnections();
    }

    this.plugin.setFullyLoaded(false);
    switch (this.DbType) {
      case MySQL:
        this.DbType = DBManager.DataBaseType.SqLite;
        this.dao = this.startSqlite(false);
        this.dao.setDbType(this.DbType);
        break;
      case SqLite:
        this.DbType = DBManager.DataBaseType.MySQL;
        this.dao = this.startMysql(false);
        this.dao.setDbType(this.DbType);
    }

    this.plugin.setFullyLoaded(true);
  }

  public void load() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().load();
    } else {
      ConfigReader configReader = null;
      CMI.getInstance()
          .safeRenameFile("dataBaseInfo.yml", "Settings" + File.separator + "DataBaseInfo.yml");

      try {
        configReader =
            new ConfigReader(CMI.getInstance(), "Settings" + File.separator + "DataBaseInfo.yml");
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      if (configReader == null) {
        CMI.getInstance()
            .consoleMessage(
                "&cFailed to load Settings" + File.separator + "DataBaseInfo.yml" + " file");
      } else {
        ConfigReader dbConfig = CMI.getInstance().getConfigManager().getConfig();
        if (dbConfig.getC().isConfigurationSection("storage")) {
          configReader.getC().set("storage", dbConfig.getC().get("storage"));
        }

        if (dbConfig.getC().isConfigurationSection("mysql")) {
          configReader.getC().set("mysql", dbConfig.getC().get("mysql"));
        }

        if (dbConfig.getC().isInt("AutoSaveInterval")) {
          configReader.getC().set("AutoSaveInterval", dbConfig.getC().get("AutoSaveInterval"));
        }

        if (dbConfig.getC().isBoolean("ForceSaveOnLogOut")) {
          configReader.getC().set("ForceSaveOnLogOut", dbConfig.getC().get("ForceSaveOnLogOut"));
        }

        if (dbConfig.getC().isBoolean("ForceLoadOnLogIn")) {
          configReader.getC().set("ForceLoadOnLogIn", dbConfig.getC().get("ForceLoadOnLogIn"));
        }

        configReader.addComment(
            "storage.method",
            new String[] {
              "storage method, can be MySQL or sqlite",
              "ATTENTION! DON'T USE SAME DATABASE TABLES FOR MORE THEN ONE SERVER, YOU WILL HAVE"
                  + " UNEXPECTED ISSUES. EACH SERVER SHOULD HAVE ITS OWN DATABASE TABLE SETUP"
            });
        String string = configReader.get("storage.method", "sqlite");
        configReader.addComment("mysql.username", new String[] {"Requires Mysql."});
        configReader.get("mysql.username", "root");
        configReader.get("mysql.password", "");
        configReader.get("mysql.hostname", "localhost:3306");
        configReader.get("mysql.database", "minecraft");
        configReader.addComment(
            "mysql.tablePrefix", new String[] {"Valid characters are a-z 0-9 and _"});
        configReader.get("mysql.tablePrefix", "CMI_");
        configReader.addComment(
            "mysql.autoReconnect",
            new String[] {
              "If you have no clue what these values do, then keep it at default values"
            });
        configReader.get("mysql.autoReconnect", true);
        configReader.get("mysql.useSSL", false);
        configReader.get("mysql.verifyServerCertificate", false);
        configReader.addComment(
            "AutoSaveInterval",
            new String[] {
              "Auto save interval in seconds",
              "This will define how often to write down data into data base",
              "Save operation will not be performed if there is no changes to save",
              "!ATTENTION! Keep it in low numbers, around 60 seconds",
              "Minimal interval is 10 seconds"
            });
        this.autoSaveInterval = configReader.get("AutoSaveInterval", 15);
        this.autoSaveInterval = this.autoSaveInterval > 3600 ? 3600 : this.autoSaveInterval;
        configReader.addComment(
            "ForceSaveOnLogOut",
            new String[] {
              "Player data will be recorded straight after he logs out, without any delay"
            });
        this.ForceSaveOnLogOut = configReader.get("ForceSaveOnLogOut", false);
        configReader.addComment(
            "ForceLoadOnLogIn",
            new String[] {
              "EXPERIMENTAL. This is not fully tested yet and some minor issues can happen when"
                  + " ussing on bungee network",
              "Player data will be loaded from database each time player joins server",
              "When its set to false then precached data will be uses which is more efficient"
            });
        this.ForceLoadOnLogIn = configReader.get("ForceLoadOnLogIn", false);
        this.ForceSaveOnLogOut =
            this.ForceLoadOnLogIn ? this.ForceLoadOnLogIn : this.ForceSaveOnLogOut;
        configReader.save();
        if (string.equalsIgnoreCase("mysql")) {
          this.DbType = DBManager.DataBaseType.MySQL;
          this.dao = this.startMysql(true);
        } else if (string.equalsIgnoreCase("sqlite")) {
          this.DbType = DBManager.DataBaseType.SqLite;
          this.dao = this.startSqlite(true);
        } else {
          this.plugin.consoleMessage("&cInvalid storage method!  Changing method to sqlite!");
          configReader.getC().set("storage.method", "sqlite");
          this.DbType = DBManager.DataBaseType.SqLite;
          this.dao = this.startSqlite(true);
        }

        configReader.save();
        this.start();
      }
    }
  }

  private synchronized DBMySQL startMysql(boolean flag) {
    File file =
        new File(this.plugin.getDataFolder(), "Settings" + File.separator + "DataBaseInfo.yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    String string = yamlConfiguration.getString("mysql.url");
    if (string != null) {
      String jdbcPrefix = "jdbc:mysql://";
      if (string.toLowerCase().startsWith(jdbcPrefix)) {
        string = string.substring(jdbcPrefix.length());
        String[] strings = string.split("/");
        if (strings.length >= 2) {
          yamlConfiguration.set("mysql.hostname", strings[0]);
          yamlConfiguration.set("mysql.database", strings[1]);
        }
      }
    }

    String username = yamlConfiguration.getString("mysql.username");
    if (username == null) {
      this.plugin.consoleMessage("&cmysql.username property invalid or missing");
    }

    String password = yamlConfiguration.getString("mysql.password");
    String hostname = yamlConfiguration.getString("mysql.hostname");
    String database = yamlConfiguration.getString("mysql.database");
    String tablePrefix = yamlConfiguration.getString("mysql.tablePrefix");
    boolean autoReconnect = yamlConfiguration.getBoolean("mysql.autoReconnect");
    boolean useSsl = yamlConfiguration.getBoolean("mysql.useSSL");
    boolean verifyCertificate = yamlConfiguration.getBoolean("mysql.verifyServerCertificate");
    if (flag && this.dao != null) {
      boolean ready = false;
      if (!this.username.equals(username)) {
        ready = true;
      }

      if (!this.password.equals(password)) {
        ready = true;
      }

      if (!this.hostname.equals(hostname)) {
        ready = true;
      }

      if (!this.database.equals(database)) {
        ready = true;
      }

      if (!this.getPrefix().equals(tablePrefix)) {
        ready = true;
      }

      if (this.autoReconnect != autoReconnect) {
        ready = true;
      }

      if (this.useSSL != useSsl) {
        ready = true;
      }

      if (this.verifyServerCertificate != verifyCertificate) {
        ready = true;
      }

      if (this.dao != null && !ready) {
        return (DBMySQL) this.dao;
      }
    }

    this.username = username;
    this.password = password;
    this.hostname = hostname;
    this.database = database;
    this.prefix = tablePrefix;
    this.autoReconnect = autoReconnect;
    this.useSSL = useSsl;
    this.verifyServerCertificate = verifyCertificate;
    if (this.plugin.isEnabled()) {
      DBMySQL dbMySQL =
          new DBMySQL(
              this.plugin,
              hostname,
              database,
              username,
              password,
              tablePrefix,
              autoReconnect,
              verifyCertificate,
              useSsl);
      dbMySQL.initialize();
      return dbMySQL;
    } else {
      return null;
    }
  }

  private synchronized DBSQLite startSqlite(boolean flag) {
    if (flag && this.dao != null) {
      return (DBSQLite) this.dao;
    }

    DBSQLite dbsqLite =
        new DBSQLite(this.plugin, new File(this.plugin.getDataFolder(), "cmi.sqlite.db"));
    dbsqLite.initialize();
    return dbsqLite;
  }

  public DBManager.DataBaseType getDbType() {
    return this.DbType;
  }

  public void stop() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().stop();
    } else {
      if (this.autosaveBukkitId != null) {
        CMIDebug.d(new Object[] {"Stopped auto save task"});
        this.autosaveBukkitId.cancel();
      }

      if (this.task != null) {
        this.task.cancel(false);
      }
    }
  }

  public void start() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().start();
    } else {
      int value = this.autoSaveInterval;
      if (this.oldRapidvalue != null) {
        value = 1;
      }

      if (value < 1) {
        value = 1;
      }

      value *= 20;
      this.stop();
      this.autosaveBukkitId =
          CMIScheduler.scheduleSyncRepeatingTask(this.plugin, this.autoSave, value, value);
    }
  }

  public void addForSave(CMIUser cmiUser) {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().addForSave(cmiUser);
    } else {
      this.playerListToSave.add(cmiUser);
    }
  }

  public void addForPlayTimeRewardSave(CMIUser cmiUser) {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().addForPlayTimeRewardSave(cmiUser);
    } else {
      this.playerPlayTimeRewardToSave.add(cmiUser);
    }
  }

  public void addForSave(@Nonnull CMIUser cmiUser, String string) {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().addForSave(cmiUser, string);
    } else if (cmiUser != null) {
      synchronized (this.invToSave) {
        this.invToSave.put(cmiUser, new DBManager.InvSave(cmiUser, string));
      }
    }
  }

  public boolean isInventorySavePending(@Nonnull CMIUser cmiUser) {
    if (cmiUser == null) {
      return false;
    }

    synchronized (this.invToSave) {
      return this.invToSave.containsKey(cmiUser);
    }
  }

  public void clear() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().clear();
    } else {
      this.getPlayerId.clear();
      this.getPlayerInvId.clear();
      this.getPlayerPlayTimeId.clear();
      this.getPlayerPlayTimeRewardId.clear();
    }
  }

  private synchronized HashSet<CMIUser> getFirstPlayersForSave(boolean flag) {
    HashSet<CMIUser> hashSet = new HashSet();
    if (this.playerListToSave.size() < 10) {
      int value = 0;

      for (CMIUser cmiUser : this.playerListToSave) {
        value++;
      }

      if (value == 0) {
        this.playerListToSave.clear();
      }
    }

    if (flag) {
      hashSet.addAll(this.playerListToSave);
      return hashSet;
    }

    int count = 0;
    int threshold = this.oldRapidvalue == null ? 50 : 250;
    int batchSize = (int) (this.playerListToSave.size() * 0.1);
    threshold = batchSize > threshold ? batchSize : threshold;
    threshold = threshold > 500 ? 500 : threshold;

    for (CMIUser saveUser : this.playerListToSave) {
      if (count >= threshold) {
        break;
      }

      hashSet.add(saveUser);
      count++;
    }

    return hashSet;
  }

  public void saveAllInNextCicle() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().saveAllInNextCicle();
    } else {
      this.all = true;
    }
  }

  public void saveBatchAsync(boolean flag) {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().saveBatchAsync(flag);
    } else {
      if (this.task != null && this.startedAt + 60000L < System.currentTimeMillis()) {
        this.task.cancel(false);
        this.task = null;
      }

      if (this.task == null) {
        this.startedAt = System.currentTimeMillis();
        this.task =
            CMIScheduler.runTaskAsynchronously(
                this.plugin,
                () -> {
                  this.saveBatch(flag);
                  this.all = false;
                });
      }
    }
  }

  public void startRapidSave() {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().startRapidSave();
    } else {
      this.oldRapidvalue = this.autoSaveInterval;
      this.autoSaveInterval = 1;
      this.all = false;
    }
  }

  public void saveBatch(boolean flag) {
    if (this.databaseSwitch && Version.isTestServer()) {
      this.plugin.getDbManager_NEW().saveBatch(flag);
    } else if (!this.startingDb) {
      try {
        if (!this.playerListToSave.isEmpty()
            || !this.invToSave.isEmpty()
            || !this.playerPlayTimeRewardToSave.isEmpty()) {
          if (!this.getDB().isConnected() && this.DbType == DBManager.DataBaseType.MySQL) {
            if (!this.startingDb) {
              this.startingDb = true;

              try {
                this.dao = this.startMysql(false);
              } catch (Throwable throwable) {
                throwable.printStackTrace();
              } finally {
                this.startingDb = false;
              }
            }

            if (this.getDB() != null && this.getDB().isConnected()) {
              CMI.getInstance().consoleMessage("&2Re-established MySQL connection");
              return;
            }
          }

          if ((this.getDB() == null || !this.getDB().isConnected())
              && this.DbType == DBManager.DataBaseType.MySQL) {
            CMI.getInstance()
                .consoleMessage(
                    "&cCan't save player data. DataBase connection can't be established");
            return;
          }

          if (this.getDB().isLocked()) {
            return;
          }

          this.getDB().prepareTempBatch();
          var hashSet = this.getFirstPlayersForSave(flag);
          if (this.oldRapidvalue != null) {
            this.plugin.consoleMessage(
                "Saving player data: " + hashSet.size() + "/" + this.playerListToSave.size());
          }

          CMIDebug.d(
              new Object[] {
                "TempList",
                hashSet.size(),
                "pFullList",
                this.playerListToSave.size(),
                "pRewards:",
                this.playerPlayTimeRewardToSave.size(),
                "InvSave:",
                this.invToSave.size()
              });

          for (CMIUser cmiUser : hashSet) {
            if (cmiUser.getUniqueId() != null
                && (cmiUser.isFakeAccount()
                    || cmiUser.isOnline()
                    || cmiUser.isOnline() && cmiUser.getTotalPlayTime(true) > 0L
                    || cmiUser.getTotalPlayTime(false) > 0L
                    || cmiUser.getId() == 0)) {
              if (cmiUser.getId() == 0) {
                this.getPlayerId.put(cmiUser.getUniqueId().toString(), cmiUser);
              }

              boolean changed = false;

              try {
                changed = this.getDB().updatePlayer(cmiUser);
              } catch (Exception exception) {
                exception.printStackTrace();
              }

              if (!changed) {
                this.addForSave(cmiUser);
              }
            }
          }

          this.saveCMIPlaytime(hashSet);
          this.saveInventories();
          this.savePlaytimeRewards();
          if (this.getDB().isLocked()) {
            return;
          }

          try {
            if (this.getDB().executeTempBatch()) {
              this.playerListToSave.removeAll(hashSet);
            } else {
              CMIDebug.d(new Object[] {"Failed to save"});
            }
          } catch (Throwable throwable2) {
            throwable2.printStackTrace();
          }

          if (this.getDB().isLocked()) {
            return;
          }

          if (!this.getPlayerId.isEmpty()) {
            this.getDB().getUserIds(this.getPlayerId);
          }

          if (this.getDB().isLocked()) {
            return;
          }

          if (!this.getPlayerPlayTimeId.isEmpty()) {
            this.getDB().getUserPlayTimeIds(this.getPlayerPlayTimeId);
          }

          if (this.getDB().isLocked()) {
            return;
          }

          if (!this.getPlayerInvId.isEmpty()) {
            this.getDB().getUserInvIds(this.getPlayerInvId);
          }

          if (!this.getPlayerPlayTimeRewardId.isEmpty()) {
            this.getDB().getUserPlayTimeRewardIds(this.getPlayerPlayTimeRewardId);
          }

          if (this.getDB().isLocked()) {
            return;
          }

          this.clear();
          this.task = null;
          return;
        }

        if (this.oldRapidvalue != null) {
          this.autoSaveInterval = this.oldRapidvalue;
          this.oldRapidvalue = null;
          this.task = null;
          this.start();
          CMI.getInstance().consoleMessage("Saved all player data");
        }
      } catch (Exception exception2) {
        this.plugin.consoleMessage(" SEVERE SAVE ERROR");
        exception2.printStackTrace();
        this.task = null;
        return;
      }

      this.task = null;
    }
  }

  public void recheckIDs(CMIUser cmiUser) {
    this.getPlayerId.put(cmiUser.getUniqueId().toString(), cmiUser);
    this.getPlayerInvId.put(cmiUser.getId(), cmiUser);
    this.getPlayerPlayTimeRewardId.put(cmiUser.getId(), cmiUser);

    for (CMIPlayDay cmiPlayDay : cmiUser.getCMIPlayTime().getForSave()) {
      this.getPlayerPlayTimeId.put(cmiPlayDay, cmiUser);
    }
  }

  private void saveInventories() {
    try {
      synchronized (this.invToSave) {
        var iterator = this.invToSave.values().iterator();

        while (iterator.hasNext()) {
          DBManager.InvSave invSave = (DBManager.InvSave) iterator.next();
          if (invSave.getUser().getInvId() == 0 && invSave.getUser().getId() != 0) {
            this.getPlayerInvId.put(invSave.getUser().getId(), invSave.getUser());
          }

          this.getDB().updatePlayerInventory(invSave.getUser(), invSave.getInv());
          iterator.remove();
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private void savePlaytimeRewards() {
    try {
      for (var iterator = this.playerPlayTimeRewardToSave.iterator();
          iterator.hasNext();
          iterator.remove()) {
        CMIUser cmiUser = (CMIUser) iterator.next();
        if (cmiUser.getPlayTimeRewardId() == 0 && cmiUser.getId() != 0) {
          this.getPlayerPlayTimeRewardId.put(cmiUser.getId(), cmiUser);
        }

        if (cmiUser.getId() != 0) {
          this.getDB().updatePlayerPlayTimeRewards(cmiUser);
        }
      }
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  private void saveCMIPlaytime(HashSet<CMIUser> hashSet) {
    if (this.plugin.getConfigManager().isCMIPlayTimeTracking()) {
      try {
        for (CMIUser cmiUser : hashSet) {
          for (CMIPlayDay cmiPlayDay : cmiUser.getCMIPlayTime().getForSave()) {
            if (cmiPlayDay.getId() == 0) {
              this.getPlayerPlayTimeId.put(cmiPlayDay, cmiUser);
            }
          }

          this.getDB().updatePlayerPlayTime(cmiUser);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }
  }

  public boolean isRapidModeEnabled() {
    return this.oldRapidvalue != null;
  }

  public boolean isForceSaveOnLogOut() {
    return this.ForceSaveOnLogOut;
  }

  public boolean isForceLoadOnLogIn() {
    return this.ForceLoadOnLogIn;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public enum DataBaseType {
    MySQL,
    SqLite;
  }

  class InvSave {
    private CMIUser user;
    private String inv;

    public InvSave(CMIUser cmiUser, String string) {
      this.user = cmiUser;
      this.inv = string;
    }

    public String getInv() {
      return this.inv;
    }

    public CMIUser getUser() {
      return this.user;
    }
  }
}
