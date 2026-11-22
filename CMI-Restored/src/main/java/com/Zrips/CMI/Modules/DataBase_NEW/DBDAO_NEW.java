package com.Zrips.CMI.Modules.DataBase_NEW;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.PlayTime.CMIPlayDay;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTROneTime;
import com.Zrips.CMI.Modules.PlayTimeRewards.PTRRepeat;
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
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Messages.CMIMessages;

public abstract class DBDAO_NEW {
  private static DBConnectionPool_NEW pool;
  private static String prefix;
  protected CMI plugin;
  private CMISqlType format = CMISqlType.MySQL;
  private CMIDbBatch playerInsertBatch = new CMIDbBatch();
  private CMIDbBatch playerUpdateBatch = new CMIDbBatch();
  private CMIDbBatch inventoryUpdateBatch = new CMIDbBatch();
  private CMIDbBatch inventoryInsertBatch = new CMIDbBatch();
  private CMIDbBatch playtimerewardUpdateBatch = new CMIDbBatch();
  private CMIDbBatch playtimerewardInsertBatch = new CMIDbBatch();
  private CMIDbBatch playtimeUpdateBatch = new CMIDbBatch();
  private CMIDbBatch playtimeInsertBatch = new CMIDbBatch();

  public static DBConnectionPool_NEW getPool() {
    return pool;
  }

  protected DBDAO_NEW(
      CMI cmi,
      String string,
      String connectionOptions,
      String dbUser,
      String dbPassword,
      String tablePrefix) {
    this.plugin = cmi;
    prefix = tablePrefix;

    try {
      pool = new DBConnectionPool_NEW(connectionOptions, dbUser, dbPassword);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    try {
      Connection connection = pool.getConnection();
      pool.releaseConnection(connection);
    } catch (Throwable throwable) {
      try {
        pool.closeAll();
      } catch (Exception exception2) {
      }

      try {
        pool =
            new DBConnectionPool_NEW(
                connectionOptions.replace("utf8mb4_unicode_ci", "utf-8"), dbUser, dbPassword);
      } catch (Exception exception3) {
        exception3.printStackTrace();
      }
    }

    Connection activeConnection = null;

    try {
      activeConnection = pool.getConnection();
      DatabaseMetaData databaseMetaData = activeConnection.getMetaData();
      String productName = databaseMetaData.getDatabaseProductName();
      String productVersion = databaseMetaData.getDatabaseProductVersion();
      CMI.getInstance()
          .consoleMessage(productName + " " + productVersion + " data base type detected");
      if (productName.toLowerCase().contains("mariadb")
          || productVersion.toLowerCase().contains("mariadb")) {
        this.format = CMISqlType.MariaDB;
      }
    } catch (Error | Exception value) {
      value.printStackTrace();
    } finally {
      pool.releaseConnection(activeConnection);
    }
  }

  public final synchronized void setUp() {
    if (!this.plugin.isFullyLoaded()) {
      this.createDefaultTable(CMIDBTables.UserTable);
      String string = this.getTableCharSet(CMIDBTables.UserTable);
      if (string != null && !string.contains("utf8")) {
        this.convertTableToUTF8(CMIDBTables.UserTable);
      }

      String rowFormat = this.getTableRowFormat(CMIDBTables.UserTable);
      if (rowFormat != null && !rowFormat.equalsIgnoreCase("dynamic")) {
        this.convertTableRowFormat(CMIDBTables.UserTable);
      }

      this.checkDefaultUserCollumns();
      this.createDefaultTable(CMIDBTables.InvTable);
      this.checkDefaultInvCollumns();
      this.createDefaultTable(CMIDBTables.PlayTime);
      this.checkDefaultPlayTimeCollumns();
      this.createDefaultTable(CMIDBTables.PlayTimeReward);
      this.checkDefaultPlayTimeRewardCollumns();
    }
  }

  public abstract boolean createTable(String string);

  public abstract boolean isTable(String string);

  public abstract boolean isCollumn(String string, String columnName);

  public abstract boolean truncate(String string);

  public abstract boolean addCollumn(String string, String columnName, String columnType);

  public abstract boolean convertTableToUTF8(CMIDBTables cmidbTables);

  public abstract String getTableCharSet(CMIDBTables cmidbTables);

  public abstract String getTableRowFormat(CMIDBTables cmidbTables);

  public abstract boolean convertTableRowFormat(CMIDBTables cmidbTables);

  public static String getPrefix() {
    return prefix;
  }

  public CompletableFuture<Void> updateUUID(int value, UUID uuid) {
    return CompletableFuture.runAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection != null) {
            PreparedStatement preparedStatement = null;

            try {
              preparedStatement =
                  connection.prepareStatement(
                      "UPDATE `"
                          + CMIDBTables.UserTable.getTableName()
                          + "` SET `"
                          + CMIUserTablesFields.player_uuid.getColumn()
                          + "` = ? WHERE `id` = ?;");
              preparedStatement.setString(1, uuid.toString());
              preparedStatement.setInt(2, value);
              preparedStatement.executeUpdate();
            } catch (SQLException sqlException) {
              sqlException.printStackTrace();
            } finally {
              this.close(preparedStatement);
              this.close(connection);
            }
          }
        });
  }

  public CompletableFuture<Void> updateUserName(int value, String string) {
    return CompletableFuture.runAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection != null) {
            PreparedStatement preparedStatement = null;

            try {
              preparedStatement =
                  connection.prepareStatement(
                      "UPDATE `"
                          + CMIDBTables.UserTable.getTableName()
                          + "` SET `"
                          + CMIUserTablesFields.username.getColumn()
                          + "` = ? WHERE `id` = ?;");
              preparedStatement.setString(1, string);
              preparedStatement.setInt(2, value);
              preparedStatement.executeUpdate();
            } catch (SQLException sqlException) {
              sqlException.printStackTrace();
            } finally {
              this.close(preparedStatement);
              this.close(connection);
            }
          }
        });
  }

  public boolean isConnected() {
    return pool.hasValidConnection();
  }

  protected Connection getConnection() {
    try {
      return pool.getConnection();
    } catch (Error | Exception value) {
      this.plugin.consoleMessage("&cUnable to connect to the database: " + value.getMessage());
      return null;
    }
  }

  public static Connection getPooledConnection() {
    try {
      return pool.getConnection();
    } catch (Error | Exception value) {
      CMI.getInstance()
          .consoleMessage("&cUnable to connect to the database: " + value.getMessage());
      return null;
    }
  }

  public void close(ResultSet resultSet) {
    if (resultSet != null) {
      try {
        resultSet.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  public void close(Statement statement) {
    if (statement != null) {
      try {
        statement.close();
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  public void close(Connection connection) {
    if (connection != null) {
      try {
        pool.releaseConnection(connection);
      } catch (Error | Exception value) {
        value.printStackTrace();
      }
    }
  }

  public boolean updatePlayer(CMIUser cmiUser) {
    return cmiUser.getId() <= 0
        ? this.createNewPlayerRecord(cmiUser)
        : this.updatePlayerRecord(cmiUser);
  }

  private boolean createNewPlayerRecord(CMIUser cmiUser) {
    try {
      this.playerInsertBatch.prepareBatch(CMIDBTables.UserTable.getInsertQuery(this.getDbFormat()));

      try {
        this.plugin
            .getPlayerManager()
            .savePlayerToDB(cmiUser, this.playerInsertBatch.getBatch(), false);
      } catch (Throwable throwable) {
        this.playerInsertBatch.close();
        throwable.printStackTrace();
        return false;
      }

      this.playerInsertBatch.addBatch();
      return true;
    } catch (Throwable throwable2) {
      this.playerInsertBatch.close();
      throwable2.printStackTrace();
      return false;
    }
  }

  private boolean updatePlayerRecord(CMIUser cmiUser) {
    try {
      this.playerUpdateBatch.prepareBatch(CMIDBTables.UserTable.getUpdateQuery());

      try {
        this.plugin
            .getPlayerManager()
            .savePlayerToDB(cmiUser, this.playerUpdateBatch.getBatch(), true);
      } catch (Throwable throwable) {
        this.playerUpdateBatch.close();
        throwable.printStackTrace();
        return false;
      }

      this.playerUpdateBatch.addBatch();
      return true;
    } catch (Throwable throwable2) {
      this.playerUpdateBatch.close();
      throwable2.printStackTrace();
      return false;
    }
  }

  public void updatePlayerPlayTime(CMIUser cmiUser) {
    try {
      for (CMIPlayDay cmiPlayDay : new HashSet<>(cmiUser.getCMIPlayTime().getForSave())) {
        int value = cmiPlayDay.getId();
        if (value == 0) {
          this.playtimeInsertBatch.prepareBatch(
              CMIDBTables.PlayTime.getInsertQuery(this.getDbFormat()));
          int paramIndex = 1;
          this.playtimeInsertBatch.getBatch().setInt(paramIndex, cmiUser.getId());
          this.playtimeInsertBatch.getBatch().setInt(++paramIndex, cmiPlayDay.getDate());
          paramIndex++;
          int hourIndex = 0;
          CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
          int fieldCount = (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;

          for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
            CMIPlaytimeTablesFields cmiPlaytimeTablesFields = cmiPlaytimeTablesFieldses[fieldIndex];
            if (cmiPlaytimeTablesFields.isTimeField()) {
              this.playtimeInsertBatch
                  .getBatch()
                  .setLong(paramIndex, cmiPlayDay.getHourPlaytime(hourIndex));
              hourIndex++;
              paramIndex++;
            }
          }

          this.playtimeInsertBatch.addBatch();
        } else {
          this.playtimeUpdateBatch.prepareBatch(CMIDBTables.PlayTime.getUpdateQuery());
          int updateParamIndex = 1;
          int updateHourIndex = 0;
          CMIPlaytimeTablesFields[] updateFields;
          int updateFieldCount = (updateFields = CMIPlaytimeTablesFields.values()).length;

          for (int updateFieldIndex = 0; updateFieldIndex < updateFieldCount; updateFieldIndex++) {
            CMIPlaytimeTablesFields updateField = updateFields[updateFieldIndex];
            if (updateField.isTimeField()) {
              this.playtimeUpdateBatch
                  .getBatch()
                  .setLong(updateParamIndex, cmiPlayDay.getHourPlaytime(updateHourIndex));
              updateHourIndex++;
              updateParamIndex++;
            }
          }

          this.playtimeUpdateBatch.getBatch().setInt(updateParamIndex, value);
          this.playtimeUpdateBatch.addBatch();
        }
      }

      cmiUser.getCMIPlayTime().getForSave().clear();
    } catch (Error | Exception value10) {
      value10.printStackTrace();
    }
  }

  public void updatePlayerInventory(CMIUser cmiUser, String string) {
    int value = cmiUser.getInvId();

    try {
      if (value == 0) {
        this.inventoryInsertBatch.prepareBatch(
            CMIDBTables.InvTable.getInsertQuery(this.getDbFormat()));
        this.inventoryInsertBatch.getBatch().setInt(1, cmiUser.getId());
        this.inventoryInsertBatch.getBatch().setString(2, string);
        this.inventoryInsertBatch.addBatch();
      } else {
        this.inventoryUpdateBatch.prepareBatch(CMIDBTables.InvTable.getUpdateQuery());
        this.inventoryUpdateBatch.getBatch().setString(1, string);
        this.inventoryUpdateBatch.getBatch().setInt(2, value);
        this.inventoryUpdateBatch.addBatch();
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    }
  }

  public void updatePlayerPlayTimeRewards(CMIUser cmiUser) {
    int value = cmiUser.getPlayTimeRewardId();

    try {
      if (value == 0) {
        this.playtimerewardInsertBatch.prepareBatch(
            CMIDBTables.PlayTimeReward.getInsertQuery(this.getDbFormat()));
        this.playtimerewardInsertBatch.getBatch().setInt(1, cmiUser.getId());
        this.playtimerewardInsertBatch.getBatch().setString(2, cmiUser.getPTRRString());
        this.playtimerewardInsertBatch.getBatch().setString(3, cmiUser.getPTROString());
        this.playtimerewardInsertBatch.addBatch();
      } else {
        this.playtimerewardUpdateBatch.prepareBatch(CMIDBTables.PlayTimeReward.getUpdateQuery());
        this.playtimerewardUpdateBatch.getBatch().setString(1, cmiUser.getPTRRString());
        this.playtimerewardUpdateBatch.getBatch().setString(2, cmiUser.getPTROString());
        this.playtimerewardUpdateBatch.getBatch().setInt(3, cmiUser.getPlayTimeRewardId());
        this.playtimerewardUpdateBatch.addBatch();
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    }
  }

  public boolean executeTempBatch() {
    synchronized (this.playerUpdateBatch) {
      this.playerUpdateBatch.commit();
    }

    synchronized (this.playerInsertBatch) {
      if (!this.playerInsertBatch.commit()) {
        if (!this.format.isFallback()) {
          CMIMessages.consoleMessage("Switching to old insert querry!");
        }

        this.format.setFallback(true);
      }
    }

    synchronized (this.playtimeUpdateBatch) {
      this.playtimeUpdateBatch.commit();
    }

    synchronized (this.playtimeInsertBatch) {
      this.playtimeInsertBatch.commit();
    }

    synchronized (this.inventoryUpdateBatch) {
      this.inventoryUpdateBatch.commit();
    }

    synchronized (this.inventoryInsertBatch) {
      this.inventoryInsertBatch.commit();
    }

    synchronized (this.playtimerewardUpdateBatch) {
      this.playtimerewardUpdateBatch.commit();
    }

    synchronized (this.playtimerewardInsertBatch) {
      this.playtimerewardInsertBatch.commit();
    }

    CMIDbBatch.commitBatch();
    return true;
  }

  public CompletableFuture<Integer> getInvId(int value) {
    return CompletableFuture.supplyAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection == null) {
            return 0;
          }

          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;
          int count = 0;

          try {
            preparedStatement =
                connection.prepareStatement(
                    "SELECT `id` FROM `"
                        + CMIDBTables.InvTable.getTableName()
                        + "` WHERE `player_id` = ? LIMIT 1;");
            preparedStatement.setInt(1, value);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
              count = resultSet.getInt("id");
            }
          } catch (Error | Exception value3) {
            value3.printStackTrace();
          } finally {
            this.close(resultSet);
            this.close(preparedStatement);
            this.close(connection);
          }

          return count;
        });
  }

  private int getPlayTimeId(CMIPlayDay cmiPlayDay, CMIUser cmiUser) {
    Connection connection = this.getConnection();
    if (connection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          connection.prepareStatement(
              "SELECT `id` FROM `"
                  + CMIDBTables.PlayTime.getTableName()
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
      this.close(resultSet);
      this.close(preparedStatement);
      this.close(connection);
    }

    return value;
  }

  private int getPlayTimeRewardId(CMIUser cmiUser) {
    Connection connection = this.getConnection();
    if (connection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          connection.prepareStatement(
              "SELECT `id` FROM `"
                  + CMIDBTables.PlayTimeReward.getTableName()
                  + "` WHERE `player_id` = ? LIMIT 1;");
      preparedStatement.setInt(1, cmiUser.getId());
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        value = resultSet.getInt("id");
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      this.close(resultSet);
      this.close(preparedStatement);
      this.close(connection);
    }

    return value;
  }

  private int getId(String string) {
    Connection connection = this.getConnection();
    if (connection == null) {
      return 0;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    int value = 0;

    try {
      preparedStatement =
          connection.prepareStatement(
              "SELECT `id` FROM `"
                  + CMIDBTables.UserTable.getTableName()
                  + "` WHERE `player_uuid` = ? LIMIT 1;");
      preparedStatement.setString(1, string);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        value = resultSet.getInt("id");
      }
    } catch (Error | Exception value2) {
      value2.printStackTrace();
    } finally {
      this.close(resultSet);
      this.close(preparedStatement);
      this.close(connection);
    }

    return value;
  }

  public CompletableFuture<String> getSavedInventories(CMIUser cmiUser) {
    return CompletableFuture.supplyAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection == null) {
            return null;
          }

          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;
          String string = null;

          try {
            preparedStatement =
                connection.prepareStatement(
                    "SELECT * FROM `"
                        + CMIDBTables.InvTable.getTableName()
                        + "` WHERE `player_id` = ? LIMIT 1;");
            preparedStatement.setInt(1, cmiUser.getId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
              cmiUser.setInvId(resultSet.getInt("id"));
              string = resultSet.getString(CMIInventoryTablesFields.inventories.getColumn());
            }
          } catch (Error | Exception value) {
            value.printStackTrace();
          } finally {
            this.close(resultSet);
            this.close(preparedStatement);
            this.close(connection);
          }

          return string;
        });
  }

  public CompletableFuture<Void> loadUser(UUID uuid) {
    return CompletableFuture.runAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection != null) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
              preparedStatement =
                  connection.prepareStatement(
                      "SELECT * FROM `"
                          + CMIDBTables.UserTable.getTableName()
                          + "` WHERE `"
                          + CMIUserTablesFields.player_uuid.getColumn()
                          + "` = ? LIMIT 1;");
              preparedStatement.setString(1, uuid.toString());
              resultSet = preparedStatement.executeQuery();
              if (resultSet.next()) {
                this.plugin.getPlayerManager().loadUserFromDb(resultSet);
              }
            } catch (Error | Exception value) {
              value.printStackTrace();
            } finally {
              this.close(resultSet);
              this.close(preparedStatement);
              this.close(connection);
            }
          }
        });
  }

  public CompletableFuture<Void> loadUser(int value) {
    return CompletableFuture.runAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection != null) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
              preparedStatement =
                  connection.prepareStatement(
                      "SELECT * FROM `"
                          + CMIDBTables.UserTable.getTableName()
                          + "` WHERE `id` = ? LIMIT 1;");
              preparedStatement.setInt(1, value);
              resultSet = preparedStatement.executeQuery();
              if (resultSet.next()) {
                this.plugin.getPlayerManager().loadUserFromDb(resultSet);
              }
            } catch (Error | Exception value2) {
              value2.printStackTrace();
            } finally {
              this.close(resultSet);
              this.close(preparedStatement);
              this.close(connection);
            }
          }
        });
  }

  public void loadAllUsers() {
    Connection connection = this.getConnection();
    if (connection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            connection.prepareStatement(
                "SELECT * FROM `" + CMIDBTables.UserTable.getTableName() + "`;");
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
        this.close(resultSet);
        this.close(preparedStatement);
        this.close(connection);
      }
    }
  }

  public void loadPlayTimes() {
    Connection connection = this.getConnection();
    if (connection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            connection.prepareStatement(
                "SELECT * FROM `" + CMIDBTables.PlayTime.getTableName() + "`;");
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
                CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
                int fieldCount =
                    (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;
                int fieldIndex = 0;

                while (true) {
                  if (fieldIndex >= fieldCount) {
                    break label80;
                  }

                  CMIPlaytimeTablesFields cmiPlaytimeTablesFields =
                      cmiPlaytimeTablesFieldses[fieldIndex];
                  switch (cmiPlaytimeTablesFields) {
                    default:
                      Object object =
                          this.plugin
                              .getPlayerManager()
                              .getValueFromDb(
                                  resultSet,
                                  cmiPlaytimeTablesFields.getFieldType(),
                                  cmiPlaytimeTablesFields.getColumn());
                      if (object != null) {
                        Long playtimeValue = (Long) object;
                        cmiPlayDay.setHourPlaytime(
                            cmiPlaytimeTablesFields.getHour(), playtimeValue, true);
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
        this.close(resultSet);
        this.close(preparedStatement);
        this.close(connection);
      }
    }
  }

  public CompletableFuture<LinkedHashSet<CMIUser>> getLastLogOffList(int value, int fakeFilter) {
    return CompletableFuture.supplyAsync(
        () -> {
          Connection connection = this.getConnection();
          LinkedHashSet<CMIUser> linkedHashSet = new LinkedHashSet();
          if (connection == null) {
            return linkedHashSet;
          }

          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;

          try {
            preparedStatement =
                connection.prepareStatement(
                    "SELECT "
                        + CMIUserTablesFields.player_uuid.getColumn()
                        + " FROM `"
                        + CMIDBTables.UserTable.getTableName()
                        + "` WHERE `"
                        + CMIUserTablesFields.LastLogoffTime.getColumn()
                        + "` > ? AND `"
                        + CMIUserTablesFields.FakeAccount.getColumn()
                        + "` = ? ORDER BY `"
                        + CMIUserTablesFields.LastLogoffTime.getColumn()
                        + "` DESC LIMIT "
                        + CMINumber.clamp(value, 0, value)
                        + ","
                        + fakeFilter
                        + ";");
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, 0);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
              try {
                CMIUser cmiUser =
                    CMIUser.getUser(
                        UUID.fromString(
                            resultSet.getString(CMIUserTablesFields.player_uuid.getColumn())));
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
            this.close(resultSet);
            this.close(preparedStatement);
            this.close(connection);
          }

          return linkedHashSet;
        });
  }

  public CompletableFuture<Integer> getLogOffCount(long longValue) {
    return CompletableFuture.supplyAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection == null) {
            return 0;
          }

          PreparedStatement preparedStatement = null;
          ResultSet resultSet = null;
          long cutoffMillis = System.currentTimeMillis() - longValue * 1000L;
          int value = 0;

          try {
            preparedStatement =
                connection.prepareStatement(
                    "SELECT COUNT(*)  FROM `"
                        + CMIDBTables.UserTable.getTableName()
                        + "` WHERE (`"
                        + CMIUserTablesFields.LastLogoffTime.getColumn()
                        + "` > ? OR `"
                        + CMIUserTablesFields.LastLoginTime.getColumn()
                        + "` > ?) AND `"
                        + CMIUserTablesFields.FakeAccount.getColumn()
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
            this.close(resultSet);
            this.close(preparedStatement);
            this.close(connection);
          }

          return value;
        });
  }

  public CompletableFuture<Void> loadPlayTimes(CMIUser cmiUser) {
    return CompletableFuture.runAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection != null) {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
              preparedStatement =
                  connection.prepareStatement(
                      "SELECT * FROM `"
                          + CMIDBTables.PlayTime.getTableName()
                          + "`  WHERE `player_id` = ?;");
              preparedStatement.setInt(1, cmiUser.getId());
              resultSet = preparedStatement.executeQuery();

              while (resultSet.next()) {
                try {
                  CMIPlayDay cmiPlayDay =
                      cmiUser.getCMIPlayTime().getPlayDay(resultSet.getInt("date"));
                  cmiPlayDay.setId(resultSet.getInt("id"));
                  CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
                  int value = (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;
                  int counter = 0;

                  while (counter < value) {
                    CMIPlaytimeTablesFields cmiPlaytimeTablesFields =
                        cmiPlaytimeTablesFieldses[counter];
                    switch (cmiPlaytimeTablesFields) {
                      default:
                        Object object =
                            this.plugin
                                .getPlayerManager()
                                .getValueFromDb(
                                    resultSet,
                                    cmiPlaytimeTablesFields.getFieldType(),
                                    cmiPlaytimeTablesFields.getColumn());
                        if (object != null) {
                          Long playtimeValue = (Long) object;
                          cmiPlayDay.setHourPlaytime(
                              cmiPlaytimeTablesFields.getHour(), playtimeValue, true);
                        }
                      case player_id:
                      case date:
                        counter++;
                    }
                  }
                } catch (Exception exception) {
                  exception.printStackTrace();
                }
              }
            } catch (Error | Exception value3) {
              value3.printStackTrace();
            } finally {
              this.close(resultSet);
              this.close(preparedStatement);
              this.close(connection);
            }
          }
        });
  }

  public void loadPlayerPlayTimeRewards() {
    Connection connection = this.getConnection();
    if (connection != null) {
      PreparedStatement preparedStatement = null;
      ResultSet resultSet = null;

      try {
        long longValue = System.currentTimeMillis();
        preparedStatement =
            connection.prepareStatement(
                "SELECT * FROM `" + CMIDBTables.PlayTimeReward.getTableName() + "`;");
        resultSet = preparedStatement.executeQuery();
        int value = 0;

        while (true) {
          label128:
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
                      .getUser(resultSet.getInt(CMIPlaytimeRewardTablesFields.player_id.name()));
              if (cmiUser != null) {
                cmiUser.setPlayTimeRewardId(resultSet.getInt("id"));
                CMIPlaytimeRewardTablesFields[] cmiPlaytimeRewardTablesFieldses;
                int count =
                    (cmiPlaytimeRewardTablesFieldses = CMIPlaytimeRewardTablesFields.values())
                        .length;
                int index = 0;

                while (true) {
                  if (index >= count) {
                    break label128;
                  }

                  CMIPlaytimeRewardTablesFields cmiPlaytimeRewardTablesFields =
                      cmiPlaytimeRewardTablesFieldses[index];
                  switch (cmiPlaytimeRewardTablesFields) {
                    case player_id:
                    default:
                      break;
                    case repeatable:
                      Object object =
                          this.plugin
                              .getPlayerManager()
                              .getValueFromDb(
                                  resultSet,
                                  cmiPlaytimeRewardTablesFields.getFieldType(),
                                  cmiPlaytimeRewardTablesFields.getColumn());
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
                          cmiUser.addRepeatableReward(
                              (String) entry.getKey(), (Long) entry.getValue());
                        }
                      }
                      break;
                    case onetime:
                      Object rawValue =
                          this.plugin
                              .getPlayerManager()
                              .getValueFromDb(
                                  resultSet,
                                  cmiPlaytimeRewardTablesFields.getFieldType(),
                                  cmiPlaytimeRewardTablesFields.getColumn());
                      if (rawValue != null) {
                        for (String string : (List<String>) rawValue) {
                          PTROneTime ptrOneTime =
                              this.plugin.getPlayTimeRewardManager().getOneTimeRewardByName(string);
                          if (ptrOneTime != null) {
                            cmiUser.addOneTimeReward(string);
                          }
                        }
                      }
                  }

                  index++;
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
        this.close(resultSet);
        this.close(preparedStatement);
        this.close(connection);
      }
    }
  }

  public CompletableFuture<Void> getUserIds(HashMap<String, CMIUser> hashMap) {
    return CompletableFuture.runAsync(
        () -> {
          if (hashMap.size() < 4) {
            for (var entry : hashMap.entrySet()) {
              ((CMIUser) entry.getValue())
                  .setId(this.getId(((CMIUser) entry.getValue()).getUniqueId().toString()));
            }
          } else {
            Connection connection = this.getConnection();
            if (connection != null) {
              PreparedStatement preparedStatement = null;
              ResultSet resultSet = null;

              try {
                preparedStatement =
                    connection.prepareStatement(
                        "SELECT * FROM `" + CMIDBTables.UserTable.getTableName() + "`;");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                  if (hashMap.containsKey(
                      resultSet.getString(CMIUserTablesFields.player_uuid.getColumn()))) {
                    CMIUser cmiUser =
                        (CMIUser)
                            hashMap.get(
                                resultSet.getString(CMIUserTablesFields.player_uuid.getColumn()));
                    cmiUser.setId(resultSet.getInt("id"));
                  }
                }
              } catch (Error | Exception value) {
                value.printStackTrace();
              } finally {
                this.close(resultSet);
                this.close(preparedStatement);
                this.close(connection);
              }
            }
          }
        });
  }

  public CompletableFuture<Void> getUserPlayTimeIds(HashMap<CMIPlayDay, CMIUser> hashMap) {
    return CompletableFuture.runAsync(
        () -> {
          for (Entry entry : ((Iterable<Entry>) new HashMap(hashMap).entrySet())) {
            ((CMIPlayDay) entry.getKey())
                .setId(this.getPlayTimeId((CMIPlayDay) entry.getKey(), (CMIUser) entry.getValue()));
          }
        });
  }

  public CompletableFuture<Void> getUserInvIds(HashMap<Integer, CMIUser> hashMap) {
    return CompletableFuture.runAsync(
        () -> {
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
            Connection connection = this.getConnection();
            if (connection != null) {
              PreparedStatement preparedStatement = null;
              ResultSet resultSet = null;

              try {
                preparedStatement =
                    connection.prepareStatement(
                        "SELECT * FROM `" + CMIDBTables.InvTable.getTableName() + "`;");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                  if (hashMap.containsKey(
                      resultSet.getInt(CMIInventoryTablesFields.player_id.getColumn()))) {
                    CMIUser cmiUser =
                        (CMIUser)
                            hashMap.get(
                                resultSet.getInt(CMIInventoryTablesFields.player_id.getColumn()));
                    cmiUser.setInvId(resultSet.getInt("id"));
                  }
                }
              } catch (Error | Exception value) {
                value.printStackTrace();
              } finally {
                this.close(resultSet);
                this.close(preparedStatement);
                this.close(connection);
              }
            }
          }
        });
  }

  public CompletableFuture<Void> getUserPlayTimeRewardIds(HashMap<Integer, CMIUser> hashMap) {
    return CompletableFuture.runAsync(
        () -> {
          if (hashMap.size() < 4) {
            for (var entry : hashMap.entrySet()) {
              ((CMIUser) entry.getValue())
                  .setPlayTimeRewardId(this.getPlayTimeRewardId((CMIUser) entry.getValue()));
            }
          } else {
            Connection connection = this.getConnection();
            if (connection != null) {
              PreparedStatement preparedStatement = null;
              ResultSet resultSet = null;

              try {
                preparedStatement =
                    connection.prepareStatement(
                        "SELECT * FROM `" + CMIDBTables.PlayTimeReward.getTableName() + "`;");
                resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                  if (hashMap.containsKey(
                      resultSet.getInt(CMIPlaytimeRewardTablesFields.player_id.getColumn()))) {
                    CMIUser cmiUser =
                        (CMIUser)
                            hashMap.get(
                                resultSet.getInt(
                                    CMIPlaytimeRewardTablesFields.player_id.getColumn()));
                    cmiUser.setPlayTimeRewardId(resultSet.getInt("id"));
                  }
                }
              } catch (Error | Exception value) {
                value.printStackTrace();
              } finally {
                this.close(resultSet);
                this.close(preparedStatement);
                this.close(connection);
              }
            }
          }
        });
  }

  public CompletableFuture<Boolean> removeUser(int value) {
    return CompletableFuture.supplyAsync(
        () -> {
          Connection connection = this.getConnection();
          if (connection == null) {
            return false;
          }

          PreparedStatement preparedStatement = null;
          PreparedStatement invStatement = null;
          PreparedStatement playtimeStatement = null;
          PreparedStatement rewardStatement = null;
          boolean flag = false;

          try {
            preparedStatement =
                connection.prepareStatement(
                    "DELETE FROM `" + CMIDBTables.UserTable.getTableName() + "` WHERE `id` = ?;");
            preparedStatement.setInt(1, value);
            preparedStatement.execute();
            invStatement =
                connection.prepareStatement(
                    "DELETE FROM `"
                        + CMIDBTables.InvTable.getTableName()
                        + "` WHERE `player_id` = ?;");
            invStatement.setInt(1, value);
            invStatement.execute();
            playtimeStatement =
                connection.prepareStatement(
                    "DELETE FROM `"
                        + CMIDBTables.PlayTime.getTableName()
                        + "` WHERE `player_id` = ?;");
            playtimeStatement.setInt(1, value);
            playtimeStatement.execute();
            rewardStatement =
                connection.prepareStatement(
                    "DELETE FROM `"
                        + CMIDBTables.PlayTimeReward.getTableName()
                        + "` WHERE `player_id` = ?;");
            rewardStatement.setInt(1, value);
            rewardStatement.execute();
            flag = true;
          } catch (Error | Exception value2) {
            value2.printStackTrace();
            flag = false;
          } finally {
            this.close(preparedStatement);
            this.close(invStatement);
            this.close(playtimeStatement);
            this.close(rewardStatement);
            this.close(connection);
          }

          return flag;
        });
  }

  private boolean createDefaultTable(CMIDBTables cmidbTables) {
    if (this.isTable(cmidbTables.getTableName())) {
      return true;
    }

    try {
      CMI.getInstance()
          .consoleMessage(cmidbTables.toString() + " creating " + cmidbTables.getQuery());
      this.createTable(cmidbTables.getQuery());
      return true;
    } catch (Error | Exception value) {
      value.printStackTrace();
      return false;
    }
  }

  private boolean checkDefaultUserCollumns() {
    CMIUserTablesFields[] cmiUserTablesFieldses;
    int value = (cmiUserTablesFieldses = CMIUserTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      CMIUserTablesFields cmiUserTablesFields = cmiUserTablesFieldses[index];
      if (!this.isCollumn(CMIDBTables.UserTable.getTableName(), cmiUserTablesFields.getColumn())) {
        this.addCollumn(
            CMIDBTables.UserTable.getTableName(),
            cmiUserTablesFields.getColumn(),
            cmiUserTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultInvCollumns() {
    CMIInventoryTablesFields[] cmiInventoryTablesFieldses;
    int value = (cmiInventoryTablesFieldses = CMIInventoryTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      CMIInventoryTablesFields cmiInventoryTablesFields = cmiInventoryTablesFieldses[index];
      if (!this.isCollumn(
          CMIDBTables.InvTable.getTableName(), cmiInventoryTablesFields.getColumn())) {
        this.addCollumn(
            CMIDBTables.InvTable.getTableName(),
            cmiInventoryTablesFields.getColumn(),
            cmiInventoryTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultPlayTimeCollumns() {
    CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
    int value = (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      CMIPlaytimeTablesFields cmiPlaytimeTablesFields = cmiPlaytimeTablesFieldses[index];
      if (!this.isCollumn(
          CMIDBTables.PlayTime.getTableName(), cmiPlaytimeTablesFields.getColumn())) {
        this.addCollumn(
            CMIDBTables.PlayTime.getTableName(),
            cmiPlaytimeTablesFields.getColumn(),
            cmiPlaytimeTablesFields.getType());
      }
    }

    return true;
  }

  private boolean checkDefaultPlayTimeRewardCollumns() {
    CMIPlaytimeRewardTablesFields[] cmiPlaytimeRewardTablesFieldses;
    int value = (cmiPlaytimeRewardTablesFieldses = CMIPlaytimeRewardTablesFields.values()).length;

    for (int index = 0; index < value; index++) {
      CMIPlaytimeRewardTablesFields cmiPlaytimeRewardTablesFields =
          cmiPlaytimeRewardTablesFieldses[index];
      if (!this.isCollumn(
          CMIDBTables.PlayTimeReward.getTableName(), cmiPlaytimeRewardTablesFields.getColumn())) {
        this.addCollumn(
            CMIDBTables.PlayTimeReward.getTableName(),
            cmiPlaytimeRewardTablesFields.getColumn(),
            cmiPlaytimeRewardTablesFields.getType());
      }
    }

    return true;
  }

  public CMISqlType getDbFormat() {
    return this.format;
  }

  public void setDbFormat(CMISqlType cmiSqlType) {
    this.format = cmiSqlType;
  }
}
