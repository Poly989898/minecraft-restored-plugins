package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayDay;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTROneTime;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTRRepeat;
import com.Zrips.CMI.utils.CMIThread;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Logs.CMIDebug;

public abstract class DBDAO {
  private DBConnectionPool pool;
  private String prefix;
  protected CMI plugin;
  private DBManager.DataBaseType dbType = DBManager.DataBaseType.SqLite;
  public DBDAO.mysqltypes Format = DBDAO.mysqltypes.MySQL;
  private PreparedStatement updateBatch = null;
  private boolean updateBatchExecuted = true;
  private PreparedStatement insertBatch = null;
  private boolean insertBatchExecuted = true;
  private PreparedStatement inventoryUpdateBatch = null;
  private boolean inventoryUpdateBatchExecuted = true;
  private PreparedStatement inventoryInsertBatch = null;
  private boolean inventoryInsertBatchExecuted = true;
  private PreparedStatement playtimerewardUpdateBatch = null;
  private boolean playtimerewardUpdateBatchExecuted = true;
  private PreparedStatement playtimerewardInsertBatch = null;
  private boolean playtimerewardInsertBatchExecuted = true;
  private PreparedStatement playtimeUpdateBatch = null;
  private boolean playtimeUpdateBatchExecuted = true;
  private PreparedStatement playtimeInsertBatch = null;
  private boolean playtimeInsertBatchExecuted = true;
  private boolean autoCommit = true;
  private boolean locked = false;

  protected DBDAO(
      CMI cmi,
      String string,
      String connectionOptions,
      String dbUser,
      String dbPassword,
      String tablePrefix) {
    this.plugin = cmi;
    this.prefix = tablePrefix;

    try {
      this.pool = new DBConnectionPool(string, connectionOptions, dbUser, dbPassword);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    try {
      this.pool.getConnection();
    } catch (Throwable throwable) {
      try {
        this.pool =
            new DBConnectionPool(
                string,
                connectionOptions.replace("utf8mb4_unicode_ci", "utf-8"),
                dbUser,
                dbPassword);
      } catch (Exception exception2) {
        exception2.printStackTrace();
      }
    }

    try {
      DatabaseMetaData databaseMetaData = this.pool.getConnection().getMetaData();
      String productName = databaseMetaData.getDatabaseProductName();
      String productVersion = databaseMetaData.getDatabaseProductVersion();
      CMI.getInstance()
          .consoleMessage(productName + " " + productVersion + " data base type detected");
      if (productName.toLowerCase().contains("mariadb")
          || productVersion.toLowerCase().contains("mariadb")) {
        this.Format = DBDAO.mysqltypes.MariaDB;
      }
    } catch (Error | Exception value) {
      cmi.consoleMessage("&cUnable to connect to the database: " + value.getMessage());
    }
  }

  public final synchronized void setUp() {
    if (!this.plugin.isFullyLoaded()) {
      this.setUpTables();
    }
  }

  public final synchronized void setUpTables() {
    this.createDefaultTable(DBDAO.DBTables.UserTable);
    String string = this.getTableCharSet(DBDAO.DBTables.UserTable);
    if (string != null && !string.contains("utf8")) {
      this.convertTableToUTF8(DBDAO.DBTables.UserTable);
    }

    String rowFormat = this.getTableRowFormat(DBDAO.DBTables.UserTable);
    if (rowFormat != null && !rowFormat.equalsIgnoreCase("dynamic")) {
      this.convertTableRowFormat(DBDAO.DBTables.UserTable);
    }

    this.checkDefaultUserCollumns();
    this.createDefaultTable(DBDAO.DBTables.InvTable);
    this.checkDefaultInvCollumns();
    this.createDefaultTable(DBDAO.DBTables.PlayTime);
    this.checkDefaultPlayTimeCollumns();
    this.createDefaultTable(DBDAO.DBTables.PlayTimeReward);
    this.checkDefaultPlayTimeRewardCollumns();
  }

  public abstract Statement prepareStatement(String string);

  public abstract boolean createTable(String string);

  public abstract boolean isTable(String string);

  public abstract boolean isCollumn(String string, String columnName);

  public abstract boolean truncate(String string);

  public abstract boolean addCollumn(String string, String columnName, String columnType);

  public abstract boolean convertTableToUTF8(DBDAO.DBTables dbTables);

  public abstract String getTableCharSet(DBDAO.DBTables dbTables);

  public abstract String getTableRowFormat(DBDAO.DBTables dbTables);

  public abstract boolean convertTableRowFormat(DBDAO.DBTables dbTables);

  public abstract boolean isColumnUnique(DBDAO.DBTables dbTables, String string);

  public boolean containsDuplicates(DBDAO.DBTables dbTables, String string) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    Statement statement = null;
    ResultSet resultSet = null;
    boolean flag = false;

    try {
      statement = dbConnection.createStatement();
      String duplicateQuery =
          "SELECT COUNT(*) - COUNT(DISTINCT "
              + string
              + ") AS duplicates FROM "
              + dbTables.getTableName();
      resultSet = statement.executeQuery(duplicateQuery);
      if (resultSet.next()) {
        int value = resultSet.getInt("duplicates");
        flag = value > 0;
        if (value > 0) {
          CMIDebug.c(new Object[] {"Column " + string + " has " + value + " duplicate(s)."});
        } else {
          CMIDebug.c(new Object[] {"No duplicates found in column " + string + "."});
        }
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } finally {
      close(resultSet);
      close(statement);
    }

    return flag;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public void updateUUID(int value, UUID uuid) {
    this.locked = true;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "UPDATE `"
                    + DBDAO.DBTables.UserTable.getTableName()
                    + "` SET `"
                    + DBDAO.UserTablesFields.player_uuid.getCollumn()
                    + "` = ? WHERE `id` = ?;");
        preparedStatement.setString(1, uuid.toString());
        preparedStatement.setInt(2, value);
        preparedStatement.execute();
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
      } finally {
        close(preparedStatement);
      }

      this.locked = false;
    }
  }

  public void updateUserName(int value, String string) {
    this.locked = true;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "UPDATE `"
                    + DBDAO.DBTables.UserTable.getTableName()
                    + "` SET `"
                    + DBDAO.UserTablesFields.username.getCollumn()
                    + "` = ? WHERE `id` = ?;");
        preparedStatement.setString(1, string);
        preparedStatement.setInt(2, value);
        preparedStatement.execute();
      } catch (SQLException sqlException) {
        sqlException.printStackTrace();
      } finally {
        close(preparedStatement);
      }

      this.locked = false;
    }
  }

  public void executeSQL(String string) throws SQLException {
    DBConnection dbConnection = this.getConnection();
    Statement statement = null;

    try {
      statement = dbConnection.createStatement();
      statement.execute(string);
    } finally {
      close(statement);
    }
  }

  public boolean isConnected() {
    try {
      return this.pool.getConnection() != null && !this.pool.getConnection().isClosed();
    } catch (Error | Exception value) {
      return false;
    }
  }

  public DBConnection getConnection() {
    try {
      return this.pool.getConnection();
    } catch (Error | Exception value) {
      this.plugin.consoleMessage("&cUnable to connect to the database: " + value.getMessage());
      return null;
    }
  }

  public Connection getTemporaryConnection() {
    try {
      return this.pool.getTemporaryConnection();
    } catch (Error | Exception value) {
      this.plugin.consoleMessage("&cUnable to connect to the database: " + value.getMessage());
      return null;
    }
  }

  public synchronized void closeConnections() throws SQLException {
    this.pool.closeConnection();
  }

  public static void close(ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  public static void close(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  public boolean updatePlayer(CMIUser cmiUser) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    if (dbConnection.isClosed()) {
      return false;
    }

    int value = cmiUser.getId();
    return value == 0 ? this.createNewPlayerRecord(cmiUser) : this.updatePlayerRecord(cmiUser);
  }

  public boolean createNewPlayerRecord(CMIUser cmiUser) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    if (dbConnection.isClosed()) {
      return false;
    }

    try {
      if (this.insertBatch == null) {
        this.insertBatch =
            dbConnection.prepareStatement(
                DBDAO.DBTables.UserTable.getInsertQuery(this.dbType, this.Format));
      }

      try {
        this.plugin.getPlayerManager().savePlayerToDB(cmiUser, this.insertBatch, false);
      } catch (Throwable throwable) {
        if (this.insertBatch != null) {
          try {
            this.insertBatch.close();
          } catch (SQLException sqlException) {
            sqlException.printStackTrace();
          }
        }

        this.insertBatch = null;
        throwable.printStackTrace();
        return false;
      }

      if (this.insertBatch != null) {
        this.insertBatch.addBatch();
        this.insertBatchExecuted = false;
      }

      return true;
    } catch (Throwable throwable2) {
      if (this.insertBatch != null) {
        try {
          this.insertBatch.close();
        } catch (SQLException sqlException2) {
          sqlException2.printStackTrace();
          return false;
        }
      }

      this.insertBatch = null;
      throwable2.printStackTrace();
      return false;
    }
  }

  private boolean updatePlayerRecord(CMIUser cmiUser) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    if (dbConnection.isClosed()) {
      return false;
    }

    try {
      if (this.updateBatch == null) {
        this.updateBatch = dbConnection.prepareStatement(DBDAO.DBTables.UserTable.getUpdateQuery());
      }

      try {
        this.plugin.getPlayerManager().savePlayerToDB(cmiUser, this.updateBatch, true);
      } catch (Throwable throwable) {
        if (this.updateBatch != null) {
          try {
            this.updateBatch.close();
          } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
          }
        }

        this.updateBatch = null;
        throwable.printStackTrace();
        return false;
      }

      if (this.updateBatch != null) {
        this.updateBatch.addBatch();
        this.updateBatchExecuted = false;
      }

      return true;
    } catch (Throwable throwable2) {
      if (this.updateBatch != null) {
        try {
          this.updateBatch.close();
        } catch (SQLException sqlException2) {
          sqlException2.printStackTrace();
          return false;
        }
      }

      this.updateBatch = null;
      throwable2.printStackTrace();
      return false;
    }
  }

  public void updatePlayerPlayTime(CMIUser cmiUser) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      try {
        for (CMIPlayDay cmiPlayDay : new HashSet<>(cmiUser.getCMIPlayTime().getForSave())) {
          int value = cmiPlayDay.getId();
          if (value == 0) {
            if (this.playtimeInsertBatch == null) {
              this.playtimeInsertBatch =
                  dbConnection.prepareStatement(
                      DBDAO.DBTables.PlayTime.getInsertQuery(this.getDbType(), this.Format));
            }

            int paramIndex = 1;
            this.playtimeInsertBatch.setInt(paramIndex, cmiUser.getId());
            this.playtimeInsertBatch.setInt(++paramIndex, cmiPlayDay.getDate());
            paramIndex++;
            int hourIndex = 0;
            DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
            int fieldCount = (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;

            for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
              DBDAO.PlaytimeTablesFields playtimeTablesFields = playtimeTablesFieldses[fieldIndex];
              if (playtimeTablesFields.isTimeField()) {
                this.playtimeInsertBatch.setLong(paramIndex, cmiPlayDay.getHourPlaytime(hourIndex));
                hourIndex++;
                paramIndex++;
              }
            }

            this.playtimeInsertBatch.addBatch();
            this.playtimeInsertBatchExecuted = false;
          } else {
            if (this.playtimeUpdateBatch == null) {
              this.playtimeUpdateBatch =
                  dbConnection.prepareStatement(DBDAO.DBTables.PlayTime.getUpdateQuery());
            }

            int updateParamIndex = 1;
            int updateHourIndex = 0;
            DBDAO.PlaytimeTablesFields[] updateFields;
            int updateFieldCount = (updateFields = DBDAO.PlaytimeTablesFields.values()).length;

            for (int updateFieldIndex = 0;
                updateFieldIndex < updateFieldCount;
                updateFieldIndex++) {
              DBDAO.PlaytimeTablesFields updateField = updateFields[updateFieldIndex];
              if (updateField.isTimeField()) {
                this.playtimeUpdateBatch.setLong(
                    updateParamIndex, cmiPlayDay.getHourPlaytime(updateHourIndex));
                updateHourIndex++;
                updateParamIndex++;
              }
            }

            this.playtimeUpdateBatch.setInt(updateParamIndex, value);
            this.playtimeUpdateBatch.addBatch();
            this.playtimeUpdateBatchExecuted = false;
          }
        }

        cmiUser.getCMIPlayTime().getForSave().clear();
      } catch (Error | Exception value10) {
        value10.printStackTrace();
      }
    }
  }

  public void updatePlayerInventory(CMIUser cmiUser, String string) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      int value = cmiUser.getInvId();

      try {
        if (value == 0) {
          if (this.inventoryInsertBatch == null) {
            this.inventoryInsertBatch =
                dbConnection.prepareStatement(
                    DBDAO.DBTables.InvTable.getInsertQuery(this.getDbType(), this.Format));
          }

          this.inventoryInsertBatch.setInt(1, cmiUser.getId());
          this.inventoryInsertBatch.setString(2, string);
          this.inventoryInsertBatch.addBatch();
          this.inventoryInsertBatchExecuted = false;
        } else {
          if (this.inventoryUpdateBatch == null) {
            this.inventoryUpdateBatch =
                dbConnection.prepareStatement(DBDAO.DBTables.InvTable.getUpdateQuery());
          }

          this.inventoryUpdateBatch.setString(1, string);
          this.inventoryUpdateBatch.setInt(2, value);
          this.inventoryUpdateBatch.addBatch();
          this.inventoryUpdateBatchExecuted = false;
        }
      } catch (Error | Exception value2) {
        value2.printStackTrace();
      }
    }
  }

  public void updatePlayerPlayTimeRewards(CMIUser cmiUser) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      int value = cmiUser.getPlayTimeRewardId();

      try {
        if (value == 0) {
          if (this.playtimerewardInsertBatch == null) {
            this.playtimerewardInsertBatch =
                dbConnection.prepareStatement(
                    DBDAO.DBTables.PlayTimeReward.getInsertQuery(this.getDbType(), this.Format));
          }

          this.playtimerewardInsertBatch.setInt(1, cmiUser.getId());
          this.playtimerewardInsertBatch.setString(2, cmiUser.getPTRRString());
          this.playtimerewardInsertBatch.setString(3, cmiUser.getPTROString());
          this.playtimerewardInsertBatch.addBatch();
          this.playtimerewardInsertBatchExecuted = false;
        } else {
          if (this.playtimerewardUpdateBatch == null) {
            this.playtimerewardUpdateBatch =
                dbConnection.prepareStatement(DBDAO.DBTables.PlayTimeReward.getUpdateQuery());
          }

          this.playtimerewardUpdateBatch.setString(1, cmiUser.getPTRRString());
          this.playtimerewardUpdateBatch.setString(2, cmiUser.getPTROString());
          this.playtimerewardUpdateBatch.setInt(3, cmiUser.getPlayTimeRewardId());
          this.playtimerewardUpdateBatch.addBatch();
          this.playtimerewardUpdateBatchExecuted = false;
        }
      } catch (Error | Exception value2) {
        value2.printStackTrace();
      }
    }
  }

  public void setAutoCommit(boolean flag) {
    try {
      this.getConnection().setAutoCommit(flag);
      this.autoCommit = flag;
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
  }

  public boolean executeTempBatch() {
    if (this.locked) {
      return false;
    }

    this.setAutoCommit(false);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    try {
      if (!this.updateBatchExecuted) {
        this.updateBatch.executeBatch();
        this.updateBatch.close();
        this.updateBatch = null;
        this.updateBatchExecuted = true;
      }
    } catch (Throwable throwable) {
      if (this.updateBatch != null) {
        try {
          this.updateBatch.close();
        } catch (SQLException sqlException) {
          sqlException.printStackTrace();
        }
      }

      this.updateBatch = null;
      throwable.printStackTrace();
    }

    try {
      if (!this.insertBatchExecuted) {
        this.insertBatch.executeBatch();
        this.insertBatch.close();
        this.insertBatch = null;
        this.insertBatchExecuted = true;
      }
    } catch (Throwable throwable2) {
      if (!this.Format.isFallback()) {
        throwable2.printStackTrace();
      }

      this.Format.setFallback(true);
      if (this.insertBatch != null) {
        try {
          this.insertBatch.close();
        } catch (SQLException sqlException2) {
          sqlException2.printStackTrace();
        }
      }

      this.insertBatch = null;
      this.insertBatchExecuted = true;
    }

    try {
      if (!this.playtimeUpdateBatchExecuted) {
        this.playtimeUpdateBatch.executeBatch();
        this.playtimeUpdateBatch.close();
        this.playtimeUpdateBatch = null;
        this.playtimeUpdateBatchExecuted = true;
      }
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
    }

    try {
      if (!this.playtimeInsertBatchExecuted) {
        this.playtimeInsertBatch.executeBatch();
        this.playtimeInsertBatch.close();
        this.playtimeInsertBatch = null;
        this.playtimeInsertBatchExecuted = true;
      }
    } catch (Throwable throwable4) {
      throwable4.printStackTrace();
    }

    try {
      if (!this.inventoryUpdateBatchExecuted) {
        this.inventoryUpdateBatch.executeBatch();
        this.inventoryUpdateBatch.close();
        this.inventoryUpdateBatch = null;
        this.inventoryUpdateBatchExecuted = true;
      }
    } catch (Throwable throwable5) {
      throwable5.printStackTrace();
    }

    try {
      if (!this.inventoryInsertBatchExecuted) {
        this.inventoryInsertBatch.executeBatch();
        this.inventoryInsertBatch.close();
        this.inventoryInsertBatch = null;
        this.inventoryInsertBatchExecuted = true;
      }
    } catch (Throwable throwable6) {
      throwable6.printStackTrace();
    }

    try {
      if (!this.playtimerewardUpdateBatchExecuted) {
        this.playtimerewardUpdateBatch.executeBatch();
        this.playtimerewardUpdateBatch.close();
        this.playtimerewardUpdateBatch = null;
        this.playtimerewardUpdateBatchExecuted = true;
      }
    } catch (Throwable throwable7) {
      throwable7.printStackTrace();
    }

    try {
      if (!this.playtimerewardInsertBatchExecuted) {
        this.playtimerewardInsertBatch.executeBatch();
        this.playtimerewardInsertBatch.close();
        this.playtimerewardInsertBatch = null;
        this.playtimerewardInsertBatchExecuted = true;
      }
    } catch (Throwable throwable8) {
      throwable8.printStackTrace();
    }

    this.setAutoCommit(false);
    if (this.locked) {
      return false;
    }

    try {
      if (!this.autoCommit) {
        dbConnection.commit();
      }
    } catch (Error | Exception value) {
      value.printStackTrace();
    }

    return true;
  }

  public void prepareTempBatch() {
    if (!this.locked) {
      this.setAutoCommit(false);
    }
  }

  public int getInvId(int value) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int invId = 0;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT `id` FROM `"
                  + DBDAO.DBTables.InvTable.getTableName()
                  + "` WHERE `player_id` = ? LIMIT 1;");
      preparedStatement.setInt(1, value);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        invId = resultSet.getInt("id");
      }
    } catch (Error | Exception value3) {
      value3.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return invId;
  }

  private int getPlayTimeId(CMIPlayDay cmiPlayDay, CMIUser cmiUser) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT `id` FROM `"
                  + DBDAO.DBTables.PlayTime.getTableName()
                  + "` WHERE `player_id` = ? AND `date` = ? LIMIT 1;");
      preparedStatement.setInt(1, cmiUser.getId());
      preparedStatement.setInt(2, cmiPlayDay.getDate());
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        value = resultSet.getInt("id");
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return value;
  }

  private int getPlayTimeRewardId(CMIUser cmiUser) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT `id` FROM `"
                  + DBDAO.DBTables.PlayTimeReward.getTableName()
                  + "` WHERE `player_id` = ? LIMIT 1;");
      preparedStatement.setInt(1, cmiUser.getId());
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        value = resultSet.getInt("id");
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return value;
  }

  private int getId(String string) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT `id` FROM `"
                  + DBDAO.DBTables.UserTable.getTableName()
                  + "` WHERE `player_uuid` = ? LIMIT 1;");
      preparedStatement.setString(1, string);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        value = resultSet.getInt("id");
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return value;
  }

  public String getInv(CMIUser cmiUser) {
    this.locked = true;
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      this.locked = false;
      return null;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String string = null;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT * FROM `"
                  + DBDAO.DBTables.InvTable.getTableName()
                  + "` WHERE `player_id` = ? LIMIT 1;");
      preparedStatement.setInt(1, cmiUser.getId());
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        cmiUser.setInvId(resultSet.getInt("id"));
        string = resultSet.getString(DBDAO.InventoryTablesFields.inventories.getCollumn());
      }
    } catch (Error | Exception value) {
      value.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    this.locked = false;
    return string;
  }

  public CompletableFuture<String> getSavedInventories(CMIUser cmiUser) {
    return CompletableFuture.supplyAsync(
        () -> {
          this.locked = true;
          this.setAutoCommit(true);
          DBConnection dbConnection = this.getConnection();
          if (dbConnection == null) {
            this.locked = false;
            return null;
          }

          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;
          String string = null;

          try {
            preparedStatement =
                dbConnection.prepareStatement(
                    "SELECT * FROM `"
                        + DBDAO.DBTables.InvTable.getTableName()
                        + "` WHERE `player_id` = ? LIMIT 1;");
            preparedStatement.setInt(1, cmiUser.getId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
              cmiUser.setInvId(resultSet.getInt("id"));
              string = resultSet.getString(DBDAO.InventoryTablesFields.inventories.getCollumn());
            }
          } catch (Error | Exception value) {
            value.printStackTrace();
          } finally {
            close(resultSet);
            close(preparedStatement);
          }

          this.locked = false;
          return string;
        });
  }

  public void loadUser(UUID uuid) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `"
                    + DBDAO.DBTables.UserTable.getTableName()
                    + "` WHERE `"
                    + DBDAO.UserTablesFields.player_uuid.getColumn()
                    + "` = ? LIMIT 1;");
        preparedStatement.setString(1, uuid.toString());
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          this.plugin.getPlayerManager().loadUserFromDb(resultSet);
        }
      } catch (Error | Exception value) {
        value.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public CompletableFuture<Void> loadUserAsync(UUID uuid) {
    return CompletableFuture.runAsync(() -> this.loadUser(uuid), CMIThread.getExecutor());
  }

  public void loadUser(int value) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `"
                    + DBDAO.DBTables.UserTable.getTableName()
                    + "` WHERE `id` = ? LIMIT 1;");
        preparedStatement.setInt(1, value);
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          this.plugin.getPlayerManager().loadUserFromDb(resultSet);
        }
      } catch (Error | Exception value2) {
        value2.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public void loadAllUsers() {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `" + DBDAO.DBTables.UserTable.getTableName() + "`;");
        resultSet = preparedStatement.executeQuery();
        this.plugin.consoleMessage(
            "&3Executed Player DB querry in &e"
                + (System.currentTimeMillis() - longValue)
                + "&6ms");

        while (resultSet.next()) {
          this.plugin.getPlayerManager().loadUserFromDb(resultSet);
        }

        this.plugin.getDbManager().clear();
      } catch (Error | Exception value) {
        value.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public void loadPlayTimes() {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `" + DBDAO.DBTables.PlayTime.getTableName() + "`;");
        resultSet = preparedStatement.executeQuery();
        int value = 0;

        while (true) {
          label80:
          while (true) {
            if (!resultSet.next()) {
              this.plugin.loadMessage(
                  value, "playtime records", System.currentTimeMillis() - longValue);
              return;
            }

            try {
              CMIUser cmiUser =
                  this.plugin.getPlayerManager().getUser(resultSet.getInt("player_id"));
              if (cmiUser != null) {
                CMIPlayDay cmiPlayDay =
                    cmiUser.getCMIPlayTime().getPlayDay(resultSet.getInt("date"));
                cmiPlayDay.setId(resultSet.getInt("id"));
                DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
                int fieldCount =
                    (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;
                int fieldIndex = 0;

                while (true) {
                  if (fieldIndex >= fieldCount) {
                    break label80;
                  }

                  DBDAO.PlaytimeTablesFields playtimeTablesFields =
                      playtimeTablesFieldses[fieldIndex];
                  switch (playtimeTablesFields) {
                    default:
                      Object object =
                          this.plugin
                              .getPlayerManager()
                              .getValueFromDb(
                                  resultSet,
                                  playtimeTablesFields.getFieldType(),
                                  playtimeTablesFields.getCollumn());
                      if (object != null) {
                        Long playtimeValue = (Long) object;
                        cmiPlayDay.setHourPlaytime(
                            playtimeTablesFields.getHour(), playtimeValue, true);
                      }
                    case player_id:
                    case date:
                      fieldIndex++;
                  }
                }
              }
            } catch (Exception exception) {
              exception.printStackTrace();
              break;
            }
          }

          value++;
        }
      } catch (Error | Exception value4) {
        value4.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public LinkedHashSet<CMIUser> getLastLogOffList(int value, int fakeFilter) {
    DBConnection dbConnection = this.getConnection();
    LinkedHashSet<CMIUser> linkedHashSet = new LinkedHashSet();
    if (dbConnection == null) {
      return linkedHashSet;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    value = value < 0 ? 0 : value;
    this.setAutoCommit(true);

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT "
                  + DBDAO.UserTablesFields.player_uuid.getColumn()
                  + " FROM `"
                  + DBDAO.DBTables.UserTable.getTableName()
                  + "` WHERE `"
                  + DBDAO.UserTablesFields.LastLogoffTime.getColumn()
                  + "` > ? AND `"
                  + DBDAO.UserTablesFields.FakeAccount.getColumn()
                  + "` = ? ORDER BY `"
                  + DBDAO.UserTablesFields.LastLogoffTime.getColumn()
                  + "` DESC LIMIT "
                  + value
                  + ","
                  + fakeFilter
                  + ";");
      preparedStatement.setInt(1, 0);
      preparedStatement.setInt(2, 0);
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        try {
          CMIUser cmiUser =
              this.plugin
                  .getPlayerManager()
                  .getUser(
                      UUID.fromString(
                          resultSet.getString(DBDAO.UserTablesFields.player_uuid.getColumn())));
          if (cmiUser != null) {
            linkedHashSet.add(cmiUser);
          }
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    } catch (Error | Exception value3) {
      value3.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return linkedHashSet;
  }

  public int getLogOffCount(long longValue) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    long cutoffMillis = System.currentTimeMillis() - longValue * 1000L;
    int value = 0;
    this.setAutoCommit(true);

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "SELECT COUNT(*)  FROM `"
                  + DBDAO.DBTables.UserTable.getTableName()
                  + "` WHERE (`"
                  + DBDAO.UserTablesFields.LastLogoffTime.getCollumn()
                  + "` > ? OR `"
                  + DBDAO.UserTablesFields.LastLoginTime.getCollumn()
                  + "` > ?) AND `"
                  + DBDAO.UserTablesFields.FakeAccount.getCollumn()
                  + "` = ?;");
      preparedStatement.setLong(1, cutoffMillis);
      preparedStatement.setLong(2, cutoffMillis);
      preparedStatement.setInt(3, 0);
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        value += resultSet.getInt(1);
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return value;
  }

  public void loadPlayTimes(CMIUser cmiUser) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `"
                    + DBDAO.DBTables.PlayTime.getTableName()
                    + "`  WHERE `player_id` = ?;");
        preparedStatement.setInt(1, cmiUser.getId());
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
          try {
            CMIPlayDay cmiPlayDay = cmiUser.getCMIPlayTime().getPlayDay(resultSet.getInt("date"));
            cmiPlayDay.setId(resultSet.getInt("id"));
            DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
            int value = (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;
            int fieldIndex = 0;

            while (fieldIndex < value) {
              DBDAO.PlaytimeTablesFields playtimeTablesFields = playtimeTablesFieldses[fieldIndex];
              switch (playtimeTablesFields) {
                default:
                  Object object =
                      this.plugin
                          .getPlayerManager()
                          .getValueFromDb(
                              resultSet,
                              playtimeTablesFields.getFieldType(),
                              playtimeTablesFields.getCollumn());
                  if (object != null) {
                    Long playtimeValue = (Long) object;
                    cmiPlayDay.setHourPlaytime(playtimeTablesFields.getHour(), playtimeValue, true);
                  }
                case player_id:
                case date:
                  fieldIndex++;
              }
            }
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      } catch (Error | Exception value3) {
        value3.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public void loadPlayerPlayTimeRewards() {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `" + DBDAO.DBTables.PlayTimeReward.getTableName() + "`;");
        resultSet = preparedStatement.executeQuery();
        int value = 0;

        while (true) {
          while (true) {
            if (!resultSet.next()) {
              this.plugin.loadMessage(
                  value, "playtime reward records", System.currentTimeMillis() - longValue);
              return;
            }

            try {
              CMIUser cmiUser =
                  this.plugin
                      .getPlayerManager()
                      .getUser(resultSet.getInt(DBDAO.PlaytimeRewardTablesFields.player_id.name()));
              if (cmiUser != null) {
                this.processPlayTimeRewards(cmiUser, resultSet);
                break;
              }
            } catch (Exception exception) {
              exception.printStackTrace();
              break;
            }
          }

          value++;
        }
      } catch (Error | Exception value2) {
        value2.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  private void processPlayTimeRewards(CMIUser cmiUser, ResultSet resultSet) {
    try {
      cmiUser.setPlayTimeRewardId(resultSet.getInt("id"));
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    }

    DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
    int value = (playtimeRewardTablesFieldses = DBDAO.PlaytimeRewardTablesFields.values()).length;

    for (int fieldIndex = 0; fieldIndex < value; fieldIndex++) {
      DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
          playtimeRewardTablesFieldses[fieldIndex];
      switch (playtimeRewardTablesFields) {
        case player_id:
        default:
          break;
        case repeatable:
          Object object =
              this.plugin
                  .getPlayerManager()
                  .getValueFromDb(
                      resultSet,
                      playtimeRewardTablesFields.getFieldType(),
                      playtimeRewardTablesFields.getCollumn());
          if (object == null) {
            break;
          }

          HashMap hashMap = (HashMap) object;

          for (Entry entry : ((java.util.Map<?, ?>) hashMap).entrySet()) {
            PTRRepeat ptrRepeat =
                this.plugin
                    .getPlayTimeRewardManager()
                    .getRepeatingRewardByName((String) entry.getKey());
            if (ptrRepeat != null) {
              cmiUser.addRepeatableReward((String) entry.getKey(), (Long) entry.getValue());
            }
          }
          break;
        case onetime:
          Object onetimeValue =
              this.plugin
                  .getPlayerManager()
                  .getValueFromDb(
                      resultSet,
                      playtimeRewardTablesFields.getFieldType(),
                      playtimeRewardTablesFields.getCollumn());
          if (onetimeValue != null) {
            for (String string : (List<String>) onetimeValue) {
              PTROneTime ptrOneTime =
                  this.plugin.getPlayTimeRewardManager().getOneTimeRewardByName(string);
              if (ptrOneTime != null) {
                cmiUser.addOneTimeReward(string);
              }
            }
          }
      }
    }
  }

  public void loadPlayerPlayTimeRewards(CMIUser cmiUser) {
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM `"
                    + DBDAO.DBTables.PlayTimeReward.getTableName()
                    + "`  WHERE `player_id` = ?;");
        preparedStatement.setInt(1, cmiUser.getId());
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          try {
            this.processPlayTimeRewards(cmiUser, resultSet);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      } catch (Error | Exception value) {
        value.printStackTrace();
      } finally {
        close(resultSet);
        close(preparedStatement);
      }
    }
  }

  public void getUserIds(HashMap<String, CMIUser> hashMap) {
    if (!this.locked) {
      this.setAutoCommit(true);
      if (hashMap.size() < 4) {
        for (var entry : hashMap.entrySet()) {
          ((CMIUser) entry.getValue())
              .setId(
                  this.plugin
                      .getDbManager()
                      .getDB()
                      .getId(((CMIUser) entry.getValue()).getUniqueId().toString()));
        }
      } else {
        DBConnection dbConnection = this.getConnection();
        if (dbConnection != null) {
          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;

          try {
            preparedStatement =
                dbConnection.prepareStatement(
                    "SELECT * FROM `" + DBDAO.DBTables.UserTable.getTableName() + "`;");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
              if (this.locked) {
                close(resultSet);
                close(preparedStatement);
                return;
              }

              if (hashMap.containsKey(
                  resultSet.getString(DBDAO.UserTablesFields.player_uuid.getCollumn()))) {
                CMIUser cmiUser =
                    (CMIUser)
                        hashMap.get(
                            resultSet.getString(DBDAO.UserTablesFields.player_uuid.getCollumn()));
                cmiUser.setId(resultSet.getInt("id"));
              }
            }
          } catch (Error | Exception value) {
            value.printStackTrace();
          } finally {
            close(resultSet);
            close(preparedStatement);
          }
        }
      }
    }
  }

  public void getUserPlayTimeIds(HashMap<CMIPlayDay, CMIUser> hashMap) {
    if (!this.locked) {
      this.setAutoCommit(true);

      for (Entry entry : ((Iterable<Entry>) new HashMap(hashMap).entrySet())) {
        ((CMIPlayDay) entry.getKey())
            .setId(
                this.plugin
                    .getDbManager()
                    .getDB()
                    .getPlayTimeId((CMIPlayDay) entry.getKey(), (CMIUser) entry.getValue()));
      }
    }
  }

  public void getUserInvIds(HashMap<Integer, CMIUser> hashMap) {
    if (!this.locked) {
      this.setAutoCommit(true);
      if (hashMap.size() < 4) {
        for (var entry : hashMap.entrySet()) {
          ((CMIUser) entry.getValue())
              .setInvId(
                  this.plugin
                      .getDbManager()
                      .getDB()
                      .getInvId(((CMIUser) entry.getValue()).getId()));
        }
      } else {
        DBConnection dbConnection = this.getConnection();
        if (dbConnection != null) {
          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;

          try {
            preparedStatement =
                dbConnection.prepareStatement(
                    "SELECT * FROM `" + DBDAO.DBTables.InvTable.getTableName() + "`;");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
              if (this.locked) {
                close(resultSet);
                close(preparedStatement);
                return;
              }

              if (hashMap.containsKey(
                  resultSet.getInt(DBDAO.InventoryTablesFields.player_id.getCollumn()))) {
                CMIUser cmiUser =
                    (CMIUser)
                        hashMap.get(
                            resultSet.getInt(DBDAO.InventoryTablesFields.player_id.getCollumn()));
                cmiUser.setInvId(resultSet.getInt("id"));
              }
            }
          } catch (Error | Exception value) {
            value.printStackTrace();
          } finally {
            close(resultSet);
            close(preparedStatement);
          }
        }
      }
    }
  }

  public void getUserPlayTimeRewardIds(HashMap<Integer, CMIUser> hashMap) {
    if (!this.locked) {
      this.setAutoCommit(true);
      if (hashMap.size() < 4) {
        for (var entry : hashMap.entrySet()) {
          ((CMIUser) entry.getValue())
              .setPlayTimeRewardId(
                  this.plugin
                      .getDbManager()
                      .getDB()
                      .getPlayTimeRewardId((CMIUser) entry.getValue()));
        }
      } else {
        DBConnection dbConnection = this.getConnection();
        if (dbConnection != null) {
          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;

          try {
            preparedStatement =
                dbConnection.prepareStatement(
                    "SELECT * FROM `" + DBDAO.DBTables.PlayTimeReward.getTableName() + "`;");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
              if (this.locked) {
                close(resultSet);
                close(preparedStatement);
                return;
              }

              if (hashMap.containsKey(
                  resultSet.getInt(DBDAO.PlaytimeRewardTablesFields.player_id.getCollumn()))) {
                CMIUser cmiUser =
                    (CMIUser)
                        hashMap.get(
                            resultSet.getInt(
                                DBDAO.PlaytimeRewardTablesFields.player_id.getCollumn()));
                cmiUser.setPlayTimeRewardId(resultSet.getInt("id"));
              }
            }
          } catch (Error | Exception value) {
            value.printStackTrace();
          } finally {
            close(resultSet);
            close(preparedStatement);
          }
        }
      }
    }
  }

  public boolean removeUser(int value) {
    this.locked = true;
    this.setAutoCommit(true);
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      this.locked = false;
      return false;
    }

    PreparedStatement preparedStatement = null;
    PreparedStatement deleteInvTableStatement = null;
    PreparedStatement deletePlayTimeStatement = null;
    PreparedStatement deletePlayTimeRewardStatement = null;
    boolean flag = false;

    try {
      preparedStatement =
          dbConnection.prepareStatement(
              "DELETE FROM `" + DBDAO.DBTables.UserTable.getTableName() + "` WHERE `id` = ?;");
      preparedStatement.setInt(1, value);
      flag = preparedStatement.execute();
      close(preparedStatement);
      deleteInvTableStatement =
          dbConnection.prepareStatement(
              "DELETE FROM `"
                  + DBDAO.DBTables.InvTable.getTableName()
                  + "` WHERE `player_id` = ?;");
      deleteInvTableStatement.setInt(1, value);
      deleteInvTableStatement.execute();
      close(deleteInvTableStatement);
      deletePlayTimeStatement =
          dbConnection.prepareStatement(
              "DELETE FROM `"
                  + DBDAO.DBTables.PlayTime.getTableName()
                  + "` WHERE `player_id` = ?;");
      deletePlayTimeStatement.setInt(1, value);
      deletePlayTimeStatement.execute();
      close(deletePlayTimeStatement);
      deletePlayTimeRewardStatement =
          dbConnection.prepareStatement(
              "DELETE FROM `"
                  + DBDAO.DBTables.PlayTimeReward.getTableName()
                  + "` WHERE `player_id` = ?;");
      deletePlayTimeRewardStatement.setInt(1, value);
      deletePlayTimeRewardStatement.execute();
      close(deletePlayTimeRewardStatement);
      flag = true;
    } catch (Error | Exception value2) {
      value2.printStackTrace();
      flag = false;
    } finally {
      close(preparedStatement);
      close(deleteInvTableStatement);
      close(deletePlayTimeStatement);
      close(deletePlayTimeRewardStatement);
    }

    this.locked = false;
    return flag;
  }

  private boolean createDefaultTable(DBDAO.DBTables dbTables) {
    if (this.isTable(dbTables.getTableName())) {
      return true;
    }

    try {
      this.createTable(dbTables.getQuery(this.dbType));
      return true;
    } catch (Error | Exception value) {
      value.printStackTrace();
      return false;
    }
  }

  private boolean checkDefaultUserCollumns() {
    DBDAO.UserTablesFields[] userTablesFieldses;
    int value = (userTablesFieldses = DBDAO.UserTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      DBDAO.UserTablesFields userTablesFields = userTablesFieldses[index];
      if (!this.isCollumn(DBDAO.DBTables.UserTable.getTableName(), userTablesFields.getColumn())) {
        this.addCollumn(
            DBDAO.DBTables.UserTable.getTableName(),
            userTablesFields.getColumn(),
            userTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultInvCollumns() {
    DBDAO.InventoryTablesFields[] inventoryTablesFieldses;
    int value = (inventoryTablesFieldses = DBDAO.InventoryTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      DBDAO.InventoryTablesFields inventoryTablesFields = inventoryTablesFieldses[index];
      if (!this.isCollumn(
          DBDAO.DBTables.InvTable.getTableName(), inventoryTablesFields.getCollumn())) {
        this.addCollumn(
            DBDAO.DBTables.InvTable.getTableName(),
            inventoryTablesFields.getCollumn(),
            inventoryTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultPlayTimeCollumns() {
    DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
    int value = (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      DBDAO.PlaytimeTablesFields playtimeTablesFields = playtimeTablesFieldses[index];
      if (!this.isCollumn(
          DBDAO.DBTables.PlayTime.getTableName(), playtimeTablesFields.getCollumn())) {
        this.addCollumn(
            DBDAO.DBTables.PlayTime.getTableName(),
            playtimeTablesFields.getCollumn(),
            playtimeTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultPlayTimeRewardCollumns() {
    DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
    int value = (playtimeRewardTablesFieldses = DBDAO.PlaytimeRewardTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
          playtimeRewardTablesFieldses[index];
      if (!this.isCollumn(
          DBDAO.DBTables.PlayTimeReward.getTableName(), playtimeRewardTablesFields.getCollumn())) {
        this.addCollumn(
            DBDAO.DBTables.PlayTimeReward.getTableName(),
            playtimeRewardTablesFields.getCollumn(),
            playtimeRewardTablesFields.getType());
      }
    }

    return true;
  }

  public DBManager.DataBaseType getDbType() {
    return this.dbType;
  }

  public void setDbType(DBManager.DataBaseType dataBaseType) {
    this.dbType = dataBaseType;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public enum DBTables {
    UserTable(
        "users",
        "CREATE TABLE `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
        "CREATE TABLE `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);"),
    InvTable(
        "inventories",
        "CREATE TABLE `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
        "CREATE TABLE `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);"),
    PlayTime(
        "playtime",
        "CREATE TABLE `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
        "CREATE TABLE `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);"),
    PlayTimeReward(
        "playtimereward",
        "CREATE TABLE `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
        "CREATE TABLE `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);");

    private String mySQL;
    private String sQlite;
    private String tableName;

    DBTables(String string, String mySqlTemplate, String sqliteTemplate) {
      this.tableName = string;
      this.mySQL = mySqlTemplate;
      this.sQlite = sqliteTemplate;
    }

    public static DBDAO.DBTables get(String string) {
      DBDAO.DBTables[] dbTableses;
      int value = (dbTableses = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.DBTables dbTables = dbTableses[index];
        if (dbTables.toString().equalsIgnoreCase(string)
            || dbTables.getTableName().equalsIgnoreCase(string)) {
          return dbTables;
        }
      }

      return null;
    }

    private String getQR(DBManager.DataBaseType dataBaseType) {
      switch (dataBaseType) {
        case MySQL:
          return this.mySQL.replace(
              "[tableName]", CMI.getInstance().getDbManager().getPrefix() + this.tableName);
        case SqLite:
          return this.sQlite.replace("[tableName]", this.tableName);
        default:
          return "";
      }
    }

    public boolean hasColumn(String string) {
      switch (this) {
        case UserTable:
          if (DBDAO.UserTablesFields.getByName(string) != null) {
            return true;
          }

          return false;
        case InvTable:
          if (DBDAO.InventoryTablesFields.getByName(string) != null) {
            return true;
          }

          return false;
        case PlayTime:
          if (DBDAO.PlaytimeTablesFields.getByName(string) != null) {
            return true;
          }

          return false;
        case PlayTimeReward:
          if (DBDAO.PlaytimeRewardTablesFields.getByName(string) != null) {
            return true;
          }

          return false;
        default:
          return false;
      }
    }

    public String getQuery(DBManager.DataBaseType dataBaseType) {
      String string = "";
      switch (this) {
        case UserTable:
          DBDAO.UserTablesFields[] userTablesFieldses;
          int value = (userTablesFieldses = DBDAO.UserTablesFields.values()).length;

          for (int userFieldIndex = 0; userFieldIndex < value; userFieldIndex++) {
            DBDAO.UserTablesFields userTablesFields = userTablesFieldses[userFieldIndex];
            string =
                string + ", `" + userTablesFields.getColumn() + "` " + userTablesFields.getType();
          }
          break;
        case InvTable:
          DBDAO.InventoryTablesFields[] inventoryTablesFieldses;
          int invFieldCount =
              (inventoryTablesFieldses = DBDAO.InventoryTablesFields.values()).length;

          for (int invFieldIndex = 0; invFieldIndex < invFieldCount; invFieldIndex++) {
            DBDAO.InventoryTablesFields inventoryTablesFields =
                inventoryTablesFieldses[invFieldIndex];
            string =
                string
                    + ", `"
                    + inventoryTablesFields.getCollumn()
                    + "` "
                    + inventoryTablesFields.getType();
          }
          break;
        case PlayTime:
          DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
          int playtimeFieldCount =
              (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;

          for (int playtimeFieldIndex = 0;
              playtimeFieldIndex < playtimeFieldCount;
              playtimeFieldIndex++) {
            DBDAO.PlaytimeTablesFields playtimeTablesFields =
                playtimeTablesFieldses[playtimeFieldIndex];
            string =
                string
                    + ", `"
                    + playtimeTablesFields.getCollumn()
                    + "` "
                    + playtimeTablesFields.getType();
          }
          break;
        case PlayTimeReward:
          DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
          int rewardFieldCount =
              (playtimeRewardTablesFieldses = DBDAO.PlaytimeRewardTablesFields.values()).length;

          for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
            DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
                playtimeRewardTablesFieldses[rewardFieldIndex];
            string =
                string
                    + ", `"
                    + playtimeRewardTablesFields.getCollumn()
                    + "` "
                    + playtimeRewardTablesFields.getType();
          }
      }

      return this.getQR(dataBaseType).replace("[fields]", string);
    }

    public String getUpdateQuery() {
      switch (this) {
        case UserTable:
          String string = "";
          DBDAO.UserTablesFields[] userTablesFieldses;
          int value = (userTablesFieldses = DBDAO.UserTablesFields.values()).length;

          for (int userFieldIndex = 0; userFieldIndex < value; userFieldIndex++) {
            DBDAO.UserTablesFields userTablesFields = userTablesFieldses[userFieldIndex];
            if (userTablesFields != DBDAO.UserTablesFields.player_uuid) {
              if (!string.isEmpty()) {
                string = string + ", ";
              }

              string = string + "`" + userTablesFields.getColumn() + "` = ?";
            }
          }

          return "UPDATE `" + this.getTableName() + "` SET " + string + " WHERE `id` = ?;";
        case InvTable:
          String invAssignments = "";
          DBDAO.InventoryTablesFields[] inventoryTablesFieldses;
          int invFieldCount =
              (inventoryTablesFieldses = DBDAO.InventoryTablesFields.values()).length;

          for (int invFieldIndex = 0; invFieldIndex < invFieldCount; invFieldIndex++) {
            DBDAO.InventoryTablesFields inventoryTablesFields =
                inventoryTablesFieldses[invFieldIndex];
            if (inventoryTablesFields != DBDAO.InventoryTablesFields.player_id) {
              if (!invAssignments.isEmpty()) {
                invAssignments = invAssignments + ", ";
              }

              invAssignments = invAssignments + "`" + inventoryTablesFields.getCollumn() + "` = ?";
            }
          }

          return "UPDATE `" + this.getTableName() + "` SET " + invAssignments + " WHERE `id` = ?;";
        case PlayTime:
          String playtimeAssignments = "";
          DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
          int playtimeFieldCount =
              (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;

          for (int playtimeFieldIndex = 0;
              playtimeFieldIndex < playtimeFieldCount;
              playtimeFieldIndex++) {
            DBDAO.PlaytimeTablesFields playtimeTablesFields =
                playtimeTablesFieldses[playtimeFieldIndex];
            if (playtimeTablesFields.isTimeField()) {
              if (!playtimeAssignments.isEmpty()) {
                playtimeAssignments = playtimeAssignments + ", ";
              }

              playtimeAssignments =
                  playtimeAssignments + "`" + playtimeTablesFields.getCollumn() + "` = ?";
            }
          }

          return "UPDATE `"
              + this.getTableName()
              + "` SET "
              + playtimeAssignments
              + " WHERE `id` = ?;";
        case PlayTimeReward:
          String rewardAssignments = "";
          DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
          int rewardFieldCount =
              (playtimeRewardTablesFieldses = DBDAO.PlaytimeRewardTablesFields.values()).length;

          for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
            DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
                playtimeRewardTablesFieldses[rewardFieldIndex];
            if (playtimeRewardTablesFields != DBDAO.PlaytimeRewardTablesFields.player_id) {
              if (!rewardAssignments.isEmpty()) {
                rewardAssignments = rewardAssignments + ", ";
              }

              rewardAssignments =
                  rewardAssignments + "`" + playtimeRewardTablesFields.getCollumn() + "` = ?";
            }
          }

          return "UPDATE `"
              + this.getTableName()
              + "` SET "
              + rewardAssignments
              + " WHERE `id` = ?;";
        default:
          return "";
      }
    }

    public String getInsertQuery(DBManager.DataBaseType dataBaseType, DBDAO.mysqltypes mysqlType) {
      String string = "";
      String valuesPart = "";
      switch (this) {
        case UserTable:
          DBDAO.UserTablesFields[] userTablesFieldses;
          int value = (userTablesFieldses = DBDAO.UserTablesFields.values()).length;

          for (int userFieldIndex = 0; userFieldIndex < value; userFieldIndex++) {
            DBDAO.UserTablesFields userTablesFields = userTablesFieldses[userFieldIndex];
            if (!string.isEmpty()) {
              string = string + ", ";
            }

            string = string + "`" + userTablesFields.getCollumn() + "`";
            if (!valuesPart.isEmpty()) {
              valuesPart = valuesPart + ", ";
            }

            valuesPart = valuesPart + "?";
          }
          break;
        case InvTable:
          DBDAO.InventoryTablesFields[] inventoryTablesFieldses;
          int invFieldCount =
              (inventoryTablesFieldses = DBDAO.InventoryTablesFields.values()).length;

          for (int invFieldIndex = 0; invFieldIndex < invFieldCount; invFieldIndex++) {
            DBDAO.InventoryTablesFields inventoryTablesFields =
                inventoryTablesFieldses[invFieldIndex];
            if (!string.isEmpty()) {
              string = string + ", ";
            }

            string = string + "`" + inventoryTablesFields.getCollumn() + "`";
            if (!valuesPart.isEmpty()) {
              valuesPart = valuesPart + ", ";
            }

            valuesPart = valuesPart + "?";
          }
          break;
        case PlayTime:
          DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
          int playtimeFieldCount =
              (playtimeTablesFieldses = DBDAO.PlaytimeTablesFields.values()).length;

          for (int playtimeFieldIndex = 0;
              playtimeFieldIndex < playtimeFieldCount;
              playtimeFieldIndex++) {
            DBDAO.PlaytimeTablesFields playtimeTablesFields =
                playtimeTablesFieldses[playtimeFieldIndex];
            if (!string.isEmpty()) {
              string = string + ", ";
            }

            string = string + "`" + playtimeTablesFields.getCollumn() + "`";
            if (!valuesPart.isEmpty()) {
              valuesPart = valuesPart + ", ";
            }

            valuesPart = valuesPart + "?";
          }
          break;
        case PlayTimeReward:
          DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
          int rewardFieldCount =
              (playtimeRewardTablesFieldses = DBDAO.PlaytimeRewardTablesFields.values()).length;

          for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
            DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
                playtimeRewardTablesFieldses[rewardFieldIndex];
            if (!string.isEmpty()) {
              string = string + ", ";
            }

            string = string + "`" + playtimeRewardTablesFields.getCollumn() + "`";
            if (!valuesPart.isEmpty()) {
              valuesPart = valuesPart + ", ";
            }

            valuesPart = valuesPart + "?";
          }
      }

      switch (this) {
        case UserTable:
          if (dataBaseType.equals(DBManager.DataBaseType.SqLite)) {
            string =
                "INSERT INTO `"
                    + this.getTableName()
                    + "` ("
                    + string
                    + ") SELECT "
                    + valuesPart
                    + " WHERE NOT EXISTS (SELECT 1 FROM `"
                    + this.getTableName()
                    + "` WHERE "
                    + DBDAO.UserTablesFields.player_uuid.getColumn()
                    + " = ?);";
          } else if (mysqlType.isFallback()) {
            string =
                "INSERT INTO `"
                    + this.getTableName()
                    + "` ("
                    + string
                    + ") VALUES ("
                    + valuesPart
                    + ");";
          } else {
            switch (mysqlType) {
              case MySQL:
                string =
                    "INSERT INTO `"
                        + this.getTableName()
                        + "` ("
                        + string
                        + ") SELECT "
                        + valuesPart
                        + " WHERE NOT EXISTS (SELECT "
                        + DBDAO.UserTablesFields.player_uuid.getColumn()
                        + " FROM "
                        + this.getTableName()
                        + " WHERE "
                        + DBDAO.UserTablesFields.player_uuid.getColumn()
                        + " = ?) LIMIT 1;";
                return string;
              case MariaDB:
                string =
                    "INSERT INTO `"
                        + this.getTableName()
                        + "` ("
                        + string
                        + ") SELECT "
                        + valuesPart
                        + " FROM dual WHERE NOT EXISTS (SELECT "
                        + DBDAO.UserTablesFields.player_uuid.getColumn()
                        + " FROM "
                        + this.getTableName()
                        + " WHERE "
                        + DBDAO.UserTablesFields.player_uuid.getColumn()
                        + " = ?) LIMIT 1;";
            }
          }
          break;
        default:
          string =
              "INSERT INTO `"
                  + this.getTableName()
                  + "` ("
                  + string
                  + ") VALUES ("
                  + valuesPart
                  + ");";
      }

      return string;
    }

    public String getTableName() {
      return CMI.getInstance().getDbManager().getPrefix() + this.tableName;
    }

    public String getTableNameOnly() {
      return this.tableName;
    }
  }

  public enum InventoryTablesFields {
    player_id("player_id", "int", DBDAO.TablesFieldsType.number),
    inventories("inventories", "longtext", DBDAO.TablesFieldsType.longtext);

    private String column;
    private String type;
    private DBDAO.TablesFieldsType fieldType;

    InventoryTablesFields(String string, String sqlType, DBDAO.TablesFieldsType tablesFieldsType) {
      this.column = string;
      this.type = sqlType;
      this.fieldType = tablesFieldsType;
    }

    public String getCollumn() {
      return this.column;
    }

    public String getType() {
      return this.type;
    }

    public DBDAO.TablesFieldsType getFieldType() {
      return this.fieldType;
    }

    public static DBDAO.InventoryTablesFields getByName(String string) {
      DBDAO.InventoryTablesFields[] inventoryTablesFieldses;
      int value = (inventoryTablesFieldses = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.InventoryTablesFields inventoryTablesFields = inventoryTablesFieldses[index];
        if (inventoryTablesFields.name().equalsIgnoreCase(string)) {
          return inventoryTablesFields;
        }
      }

      return null;
    }
  }

  public enum PlaytimeRewardTablesFields {
    player_id(DBDAO.TablesFieldsType.number),
    repeatable(DBDAO.TablesFieldsType.stringLongMap),
    onetime(DBDAO.TablesFieldsType.stringList);

    private DBDAO.TablesFieldsType fieldType;

    PlaytimeRewardTablesFields(DBDAO.TablesFieldsType tablesFieldsType) {
      this.fieldType = tablesFieldsType;
    }

    public String getCollumn() {
      return this.name();
    }

    public String getType() {
      return this.fieldType.getType();
    }

    public DBDAO.TablesFieldsType getFieldType() {
      return this.fieldType;
    }

    public static DBDAO.PlaytimeRewardTablesFields getByName(String string) {
      DBDAO.PlaytimeRewardTablesFields[] playtimeRewardTablesFieldses;
      int value = (playtimeRewardTablesFieldses = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.PlaytimeRewardTablesFields playtimeRewardTablesFields =
            playtimeRewardTablesFieldses[index];
        if (playtimeRewardTablesFields.name().equalsIgnoreCase(string)) {
          return playtimeRewardTablesFields;
        }
      }

      return null;
    }
  }

  public enum PlaytimeTablesFields {
    player_id(DBDAO.TablesFieldsType.number),
    date(DBDAO.TablesFieldsType.number),
    h0(0, DBDAO.TablesFieldsType.longNumber),
    h1(1, DBDAO.TablesFieldsType.longNumber),
    h2(2, DBDAO.TablesFieldsType.longNumber),
    h3(3, DBDAO.TablesFieldsType.longNumber),
    h4(4, DBDAO.TablesFieldsType.longNumber),
    h5(5, DBDAO.TablesFieldsType.longNumber),
    h6(6, DBDAO.TablesFieldsType.longNumber),
    h7(7, DBDAO.TablesFieldsType.longNumber),
    h8(8, DBDAO.TablesFieldsType.longNumber),
    h9(9, DBDAO.TablesFieldsType.longNumber),
    h10(10, DBDAO.TablesFieldsType.longNumber),
    h11(11, DBDAO.TablesFieldsType.longNumber),
    h12(12, DBDAO.TablesFieldsType.longNumber),
    h13(13, DBDAO.TablesFieldsType.longNumber),
    h14(14, DBDAO.TablesFieldsType.longNumber),
    h15(15, DBDAO.TablesFieldsType.longNumber),
    h16(16, DBDAO.TablesFieldsType.longNumber),
    h17(17, DBDAO.TablesFieldsType.longNumber),
    h18(18, DBDAO.TablesFieldsType.longNumber),
    h19(19, DBDAO.TablesFieldsType.longNumber),
    h20(20, DBDAO.TablesFieldsType.longNumber),
    h21(21, DBDAO.TablesFieldsType.longNumber),
    h22(22, DBDAO.TablesFieldsType.longNumber),
    h23(23, DBDAO.TablesFieldsType.longNumber);

    private DBDAO.TablesFieldsType fieldType;
    private int hour = 0;

    PlaytimeTablesFields(DBDAO.TablesFieldsType tablesFieldsType) {
      this.fieldType = tablesFieldsType;
    }

    PlaytimeTablesFields(int value, DBDAO.TablesFieldsType tablesFieldsType) {
      this.fieldType = tablesFieldsType;
      this.hour = value;
    }

    public String getCollumn() {
      return this.name();
    }

    public String getType() {
      return this.fieldType.getType();
    }

    public DBDAO.TablesFieldsType getFieldType() {
      return this.fieldType;
    }

    public boolean isTimeField() {
      return this.name().startsWith("h") && this.name().length() < 4;
    }

    public int getHour() {
      return this.hour;
    }

    public static DBDAO.PlaytimeTablesFields getByName(String string) {
      DBDAO.PlaytimeTablesFields[] playtimeTablesFieldses;
      int value = (playtimeTablesFieldses = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.PlaytimeTablesFields playtimeTablesFields = playtimeTablesFieldses[index];
        if (playtimeTablesFields.name().equalsIgnoreCase(string)) {
          return playtimeTablesFields;
        }
      }

      return null;
    }
  }

  public enum TablesFieldsType {
    decimal("double"),
    number("int"),
    longtext("longtext"),
    text("text"),
    stringList("longtext"),
    stringLongMap("text"),
    stringIntMap("text"),
    stringDoubleMap("text"),
    stringStringMap("text"),
    locationMap("text"),
    state("boolean"),
    location("text"),
    longNumber("bigint");

    private String type;

    TablesFieldsType(String string) {
      this.type = string;
    }

    public static DBDAO.TablesFieldsType getByname(String string) {
      DBDAO.TablesFieldsType[] tablesFieldsTypes;
      int value = (tablesFieldsTypes = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.TablesFieldsType tablesFieldsType = tablesFieldsTypes[index];
        if (tablesFieldsType.name().equalsIgnoreCase(string)) {
          return tablesFieldsType;
        }
      }

      return null;
    }

    public String getType() {
      return this.type;
    }
  }

  public enum UserTablesFields {
    player_uuid("text", DBDAO.TablesFieldsType.text),
    username("text", DBDAO.TablesFieldsType.text),
    nickname("text", DBDAO.TablesFieldsType.text),
    LogOutLocation("text", DBDAO.TablesFieldsType.location),
    DeathLocation("text", DBDAO.TablesFieldsType.location),
    TeleportLocation("text", DBDAO.TablesFieldsType.location),
    Homes("text", DBDAO.TablesFieldsType.locationMap),
    LastLoginTime("bigint", DBDAO.TablesFieldsType.longNumber),
    LastLogoffTime("bigint", DBDAO.TablesFieldsType.longNumber),
    TotalPlayTime("bigint", DBDAO.TablesFieldsType.longNumber),
    tFly("bigint", DBDAO.TablesFieldsType.longNumber),
    tGod("bigint", DBDAO.TablesFieldsType.longNumber),
    Glow("text", DBDAO.TablesFieldsType.text),
    Ips("text", DBDAO.TablesFieldsType.stringIntMap),
    Cuffed("boolean", DBDAO.TablesFieldsType.state),
    AlertUntil("bigint", DBDAO.TablesFieldsType.longNumber),
    AlertReason("text", DBDAO.TablesFieldsType.text),
    JoinedCounter("boolean", DBDAO.TablesFieldsType.state),
    LockedIps("text", DBDAO.TablesFieldsType.stringList),
    pTime("bigint", DBDAO.TablesFieldsType.longNumber),
    Kits("text", DBDAO.TablesFieldsType.stringLongMap),
    Charges("text", DBDAO.TablesFieldsType.text),
    Cooldowns("mediumtext", DBDAO.TablesFieldsType.text),
    Balance("double", DBDAO.TablesFieldsType.decimal),
    Notes("text", DBDAO.TablesFieldsType.stringList),
    Rank("text", DBDAO.TablesFieldsType.text),
    BannedUntil("bigint", DBDAO.TablesFieldsType.longNumber),
    BannedAt("bigint", DBDAO.TablesFieldsType.longNumber),
    BannedBy("text", DBDAO.TablesFieldsType.text),
    BanReason("text", DBDAO.TablesFieldsType.text),
    Ignores("text", DBDAO.TablesFieldsType.text),
    Vanish("text", DBDAO.TablesFieldsType.text),
    Economy("text", DBDAO.TablesFieldsType.stringDoubleMap),
    Mail("mediumtext", DBDAO.TablesFieldsType.stringList),
    FlightCharge("double", DBDAO.TablesFieldsType.decimal),
    UserMeta("text", DBDAO.TablesFieldsType.stringStringMap),
    Flying("boolean", DBDAO.TablesFieldsType.state),
    Votifier("int", DBDAO.TablesFieldsType.number),
    Jail("text", DBDAO.TablesFieldsType.text),
    JailedUntil("bigint", DBDAO.TablesFieldsType.longNumber),
    FakeAccount("boolean", DBDAO.TablesFieldsType.state),
    PlaytimeOptimized("bigint", DBDAO.TablesFieldsType.longNumber),
    flightChargeEnabled("boolean", DBDAO.TablesFieldsType.state),
    JailReason("text", DBDAO.TablesFieldsType.text),
    Skin("text", DBDAO.TablesFieldsType.text),
    Collision("boolean", DBDAO.TablesFieldsType.state),
    NamePrefix("text", DBDAO.TablesFieldsType.text),
    NameSuffix("text", DBDAO.TablesFieldsType.text),
    Warnings("text", DBDAO.TablesFieldsType.stringLongMap),
    NameColor("text", DBDAO.TablesFieldsType.text),
    Muted("text", DBDAO.TablesFieldsType.text),
    AFRecharge("text", DBDAO.TablesFieldsType.text),
    DisplayName("text", DBDAO.TablesFieldsType.text),
    Options("text", DBDAO.TablesFieldsType.text),
    ChatColor("text", DBDAO.TablesFieldsType.text);

    private String type;
    private DBDAO.TablesFieldsType fieldType;

    UserTablesFields(String string, DBDAO.TablesFieldsType tablesFieldsType) {
      this.type = string;
      this.fieldType = tablesFieldsType;
    }

    @Deprecated
    public String getCollumn() {
      return this.name();
    }

    public String getColumn() {
      return this.name();
    }

    public String getType() {
      return this.type;
    }

    public DBDAO.TablesFieldsType getFieldType() {
      return this.fieldType;
    }

    public static DBDAO.UserTablesFields getByName(String string) {
      DBDAO.UserTablesFields[] userTablesFieldses;
      int value = (userTablesFieldses = values()).length;

      for (int index = 0; index < value; index++) {
        DBDAO.UserTablesFields userTablesFields = userTablesFieldses[index];
        if (userTablesFields.name().equalsIgnoreCase(string)) {
          return userTablesFields;
        }
      }

      return null;
    }
  }

  public enum mysqltypes {
    MySQL,
    MariaDB;

    boolean fallback = false;

    public boolean isFallback() {
      return this.fallback;
    }

    public void setFallback(boolean flag) {
      this.fallback = flag;
    }
  }
}
