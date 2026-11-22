package com.Zrips.CMI.Modules.DataBase_NEW;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayDay;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.configuration.file.YamlConfiguration;

public class DBManager_NEW {
  private DBDAO_NEW dao;
  private CMI plugin;
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
  private ConcurrentHashMap<CMIUser, DBManager_NEW.InvSave> invToSave = new ConcurrentHashMap<>();
  private Set<CMIUser> playerListToSave = ConcurrentHashMap.newKeySet();
  private Set<CMIUser> playerPlayTimeRewardToSave = ConcurrentHashMap.newKeySet();
  CMITask autosaveBukkitId = null;
  CompletableFuture<Void> task = null;
  Long startedAt = 0L;
  private Runnable autoSave =
      new Runnable() {
        @Override
        public void run() {
          if (DBManager_NEW.this.task != null
              && DBManager_NEW.this.startedAt + 60000L < System.currentTimeMillis()) {
            DBManager_NEW.this.task.cancel(false);
            DBManager_NEW.this.task = null;
          }

          if (DBManager_NEW.this.task == null) {
            DBManager_NEW.this.startedAt = System.currentTimeMillis();
            DBManager_NEW.this.task =
                CMIScheduler.runTaskAsynchronously(
                    DBManager_NEW.this.plugin,
                    () -> {
                      DBManager_NEW.this.saveBatch(DBManager_NEW.this.all);
                      DBManager_NEW.this.all = false;
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

  public DBManager_NEW(CMI cmi) {
    this.plugin = cmi;
  }

  public DBDAO_NEW getDB() {
    return this.dao;
  }

  public boolean isConnected() {
    return this.getDB() != null && this.getDB().isConnected();
  }

  public void closeConnections() {
    if (this.dao != null) {
      DBDAO_NEW.getPool().closeAll();
    }
  }

  public void switchDataBase() {
    if (this.dao != null) {
      DBDAO_NEW.getPool().closeAll();
    }

    this.plugin.setFullyLoaded(false);
    switch (this.getDB().getDbFormat()) {
      case SqLite:
        this.getDB().setDbFormat(CMISqlType.MySQL);
        this.dao = this.startMysql(false);
        break;
      case MySQL:
      case MariaDB:
        this.getDB().setDbFormat(CMISqlType.SqLite);
        this.dao = this.startSqlite(false);
    }

    this.plugin.setFullyLoaded(true);
  }

  public void load() {
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
      ConfigReader globalConfig = CMI.getInstance().getConfigManager().getConfig();
      if (globalConfig.getC().isConfigurationSection("storage")) {
        configReader.getC().set("storage", globalConfig.getC().get("storage"));
      }

      if (globalConfig.getC().isConfigurationSection("mysql")) {
        configReader.getC().set("mysql", globalConfig.getC().get("mysql"));
      }

      if (globalConfig.getC().isInt("AutoSaveInterval")) {
        configReader.getC().set("AutoSaveInterval", globalConfig.getC().get("AutoSaveInterval"));
      }

      if (globalConfig.getC().isBoolean("ForceSaveOnLogOut")) {
        configReader.getC().set("ForceSaveOnLogOut", globalConfig.getC().get("ForceSaveOnLogOut"));
      }

      if (globalConfig.getC().isBoolean("ForceLoadOnLogIn")) {
        configReader.getC().set("ForceLoadOnLogIn", globalConfig.getC().get("ForceLoadOnLogIn"));
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
        this.dao = this.startMysql(true);
        this.dao.setDbFormat(CMISqlType.MySQL);
      } else if (string.equalsIgnoreCase("sqlite")) {
        this.dao = this.startSqlite(true);
        this.dao.setDbFormat(CMISqlType.SqLite);
      } else {
        this.plugin.consoleMessage("&cInvalid storage method!  Changing method to sqlite!");
        configReader.getC().set("storage.method", "sqlite");
        this.dao = this.startSqlite(true);
        this.dao.setDbFormat(CMISqlType.SqLite);
      }

      configReader.save();
      this.start();
    }
  }

  private synchronized DBMySQL_NEW startMysql(boolean flag) {
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

    String newUsername = yamlConfiguration.getString("mysql.username");
    if (newUsername == null) {
      this.plugin.consoleMessage("&cmysql.username property invalid or missing");
    }

    String newPassword = yamlConfiguration.getString("mysql.password");
    String newHostname = yamlConfiguration.getString("mysql.hostname");
    String newDatabase = yamlConfiguration.getString("mysql.database");
    String newPrefix = yamlConfiguration.getString("mysql.tablePrefix");
    boolean newAutoReconnect = yamlConfiguration.getBoolean("mysql.autoReconnect");
    boolean newUseSSL = yamlConfiguration.getBoolean("mysql.useSSL");
    boolean newVerifyServerCertificate =
        yamlConfiguration.getBoolean("mysql.verifyServerCertificate");
    if (flag && this.dao != null) {
      boolean changed = false;
      if (!this.username.equals(newUsername)) {
        changed = true;
      }

      if (!this.password.equals(newPassword)) {
        changed = true;
      }

      if (!this.hostname.equals(newHostname)) {
        changed = true;
      }

      if (!this.database.equals(newDatabase)) {
        changed = true;
      }

      if (!this.prefix.equals(newPrefix)) {
        changed = true;
      }

      if (this.autoReconnect != newAutoReconnect) {
        changed = true;
      }

      if (this.useSSL != newUseSSL) {
        changed = true;
      }

      if (this.verifyServerCertificate != newVerifyServerCertificate) {
        changed = true;
      }

      if (this.dao != null && !changed) {
        return (DBMySQL_NEW) this.dao;
      }
    }

    this.username = newUsername;
    this.password = newPassword;
    this.hostname = newHostname;
    this.database = newDatabase;
    this.prefix = newPrefix;
    this.autoReconnect = newAutoReconnect;
    this.useSSL = newUseSSL;
    this.verifyServerCertificate = newVerifyServerCertificate;
    if (this.plugin.isEnabled()) {
      DBMySQL_NEW dbMySQL_NEW =
          new DBMySQL_NEW(
              this.plugin,
              newHostname,
              newDatabase,
              newUsername,
              newPassword,
              newPrefix,
              newAutoReconnect,
              newVerifyServerCertificate,
              newUseSSL);
      dbMySQL_NEW.initialize();
      return dbMySQL_NEW;
    } else {
      return null;
    }
  }

  private synchronized DBSQLite_NEW startSqlite(boolean flag) {
    if (flag && this.dao != null) {
      return (DBSQLite_NEW) this.dao;
    }

    DBSQLite_NEW dbsqLite_NEW = new DBSQLite_NEW(this.plugin, this.plugin.getDataFolder());
    dbsqLite_NEW.initialize();
    return dbsqLite_NEW;
  }

  public CMISqlType getDbType() {
    return this.getDB().getDbFormat();
  }

  public void stop() {
    if (this.autosaveBukkitId != null) {
      CMIDebug.d(new Object[] {"Stopped auto save task"});
      this.autosaveBukkitId.cancel();
    }

    if (this.task != null) {
      this.task.cancel(false);
    }
  }

  public void start() {
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

  public void addForSave(CMIUser cmiUser) {
    this.playerListToSave.add(cmiUser);
  }

  public void addForPlayTimeRewardSave(CMIUser cmiUser) {
    this.playerPlayTimeRewardToSave.add(cmiUser);
  }

  public void addForSave(@Nonnull CMIUser cmiUser, String string) {
    if (cmiUser != null) {
      synchronized (this.invToSave) {
        this.invToSave.put(cmiUser, new DBManager_NEW.InvSave(cmiUser, string));
      }
    }
  }

  public void clear() {
    this.getPlayerId.clear();
    this.getPlayerInvId.clear();
    this.getPlayerPlayTimeId.clear();
    this.getPlayerPlayTimeRewardId.clear();
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

    int savedCount = 0;
    int batchLimit = this.oldRapidvalue == null ? 50 : 250;
    int tenPercentCount = (int) (this.playerListToSave.size() * 0.1);
    batchLimit = tenPercentCount > batchLimit ? tenPercentCount : batchLimit;
    batchLimit = batchLimit > 500 ? 500 : batchLimit;

    for (CMIUser queuedUser : this.playerListToSave) {
      if (savedCount >= batchLimit) {
        break;
      }

      hashSet.add(queuedUser);
      savedCount++;
    }

    return hashSet;
  }

  public void saveAllInNextCicle() {
    this.all = true;
  }

  public void saveBatchAsync(boolean flag) {
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

  public void startRapidSave() {
    this.oldRapidvalue = this.autoSaveInterval;
    this.autoSaveInterval = 1;
    this.all = false;
  }

  private boolean checkConnection() {
    if (!this.getDB().isConnected() && this.getDB().getDbFormat() == CMISqlType.MySQL) {
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
        return false;
      }
    }

    if ((this.getDB() == null || !this.getDB().isConnected())
        && this.getDB().getDbFormat() == CMISqlType.MySQL) {
      CMI.getInstance()
          .consoleMessage("&cCan't save player data. DataBase connection can't be established");
      return false;
    } else {
      return true;
    }
  }

  public void saveBatch(boolean flag) {
    if (!this.startingDb) {
      try {
        if (!this.playerListToSave.isEmpty()
            || !this.invToSave.isEmpty()
            || !this.playerPlayTimeRewardToSave.isEmpty()) {
          if (!this.checkConnection()) {
            return;
          }

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

              if (!this.getDB().updatePlayer(cmiUser)) {
                this.addForSave(cmiUser);
              }
            }
          }

          this.saveCMIPlaytime(hashSet);
          this.saveInventories();
          this.savePlaytimeRewards();

          try {
            if (this.getDB().executeTempBatch()) {
              this.playerListToSave.removeAll(hashSet);
            } else {
              CMIDebug.d(new Object[] {"Failed to save"});
            }
          } catch (Throwable throwable) {
            throwable.printStackTrace();
          }

          if (!this.getPlayerId.isEmpty()) {
            this.getDB().getUserIds(this.getPlayerId);
          }

          if (!this.getPlayerPlayTimeId.isEmpty()) {
            this.getDB().getUserPlayTimeIds(this.getPlayerPlayTimeId);
          }

          if (!this.getPlayerInvId.isEmpty()) {
            this.getDB().getUserInvIds(this.getPlayerInvId);
          }

          if (!this.getPlayerPlayTimeRewardId.isEmpty()) {
            this.getDB().getUserPlayTimeRewardIds(this.getPlayerPlayTimeRewardId);
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
      } catch (Exception exception) {
        this.plugin.consoleMessage(" SEVERE SAVE ERROR");
        exception.printStackTrace();
        this.task = null;
        return;
      }

      this.task = null;
    }
  }

  private void saveInventories() {
    try {
      synchronized (this.invToSave) {
        var iterator = this.invToSave.values().iterator();

        while (iterator.hasNext()) {
          DBManager_NEW.InvSave invSave = (DBManager_NEW.InvSave) iterator.next();
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
