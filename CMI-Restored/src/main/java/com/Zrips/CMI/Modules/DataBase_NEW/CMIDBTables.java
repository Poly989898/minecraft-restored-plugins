package com.Zrips.CMI.Modules.DataBase_NEW;

import com.Zrips.CMI.CMI;
import net.Zrips.CMILib.Logs.CMIDebug;

public enum CMIDBTables {
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

  CMIDBTables(String string, String mySqlTemplate, String sqliteTemplate) {
    this.tableName = string;
    this.mySQL = mySqlTemplate;
    this.sQlite = sqliteTemplate;
  }

  private String getQR() {
    switch (CMI.getInstance().getDbManager_NEW().getDbType()) {
      case SqLite:
        return this.sQlite.replace("[tableName]", this.tableName);
      case MySQL:
      case MariaDB:
        return this.mySQL.replace("[tableName]", DBDAO_NEW.getPrefix() + this.tableName);
      default:
        return "";
    }
  }

  public String getQuery() {
    String string = "";
    switch (this) {
      case UserTable:
        CMIUserTablesFields[] cmiUserTablesFieldses;
        int value = (cmiUserTablesFieldses = CMIUserTablesFields.values()).length;

        for (int index = 0; index < value; index++) {
          CMIUserTablesFields cmiUserTablesFields = cmiUserTablesFieldses[index];
          string =
              string
                  + ", `"
                  + cmiUserTablesFields.getColumn()
                  + "` "
                  + cmiUserTablesFields.getType();
        }
        break;
      case InvTable:
        CMIInventoryTablesFields[] cmiInventoryTablesFieldses;
        int inventoryFieldCount =
            (cmiInventoryTablesFieldses = CMIInventoryTablesFields.values()).length;

        for (int inventoryFieldIndex = 0;
            inventoryFieldIndex < inventoryFieldCount;
            inventoryFieldIndex++) {
          CMIInventoryTablesFields cmiInventoryTablesFields =
              cmiInventoryTablesFieldses[inventoryFieldIndex];
          string =
              string
                  + ", `"
                  + cmiInventoryTablesFields.getColumn()
                  + "` "
                  + cmiInventoryTablesFields.getType();
        }
        break;
      case PlayTime:
        CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
        int playtimeFieldCount =
            (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;

        for (int playtimeFieldIndex = 0;
            playtimeFieldIndex < playtimeFieldCount;
            playtimeFieldIndex++) {
          CMIPlaytimeTablesFields cmiPlaytimeTablesFields =
              cmiPlaytimeTablesFieldses[playtimeFieldIndex];
          string =
              string
                  + ", `"
                  + cmiPlaytimeTablesFields.getColumn()
                  + "` "
                  + cmiPlaytimeTablesFields.getType();
        }
        break;
      case PlayTimeReward:
        CMIPlaytimeRewardTablesFields[] cmiPlaytimeRewardTablesFieldses;
        int rewardFieldCount =
            (cmiPlaytimeRewardTablesFieldses = CMIPlaytimeRewardTablesFields.values()).length;

        for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
          CMIPlaytimeRewardTablesFields cmiPlaytimeRewardTablesFields =
              cmiPlaytimeRewardTablesFieldses[rewardFieldIndex];
          string =
              string
                  + ", `"
                  + cmiPlaytimeRewardTablesFields.getColumn()
                  + "` "
                  + cmiPlaytimeRewardTablesFields.getType();
        }
    }

    return this.getQR().replace("[fields]", string);
  }

  public String getUpdateQuery() {
    switch (this) {
      case UserTable:
        String string = "";
        CMIUserTablesFields[] cmiUserTablesFieldses;
        int value = (cmiUserTablesFieldses = CMIUserTablesFields.values()).length;

        for (int index = 0; index < value; index++) {
          CMIUserTablesFields cmiUserTablesFields = cmiUserTablesFieldses[index];
          if (cmiUserTablesFields != CMIUserTablesFields.player_uuid) {
            if (!string.isEmpty()) {
              string = string + ", ";
            }

            string = string + "`" + cmiUserTablesFields.getColumn() + "` = ?";
          }
        }

        return "UPDATE `" + this.getTableName() + "` SET " + string + " WHERE `id` = ?;";
      case InvTable:
        String columnText = "";
        CMIInventoryTablesFields[] cmiInventoryTablesFieldses;
        int inventoryFieldCount =
            (cmiInventoryTablesFieldses = CMIInventoryTablesFields.values()).length;

        for (int inventoryFieldIndex = 0;
            inventoryFieldIndex < inventoryFieldCount;
            inventoryFieldIndex++) {
          CMIInventoryTablesFields cmiInventoryTablesFields =
              cmiInventoryTablesFieldses[inventoryFieldIndex];
          if (cmiInventoryTablesFields != CMIInventoryTablesFields.player_id) {
            if (!columnText.isEmpty()) {
              columnText = columnText + ", ";
            }

            columnText = columnText + "`" + cmiInventoryTablesFields.getColumn() + "` = ?";
          }
        }

        return "UPDATE `" + this.getTableName() + "` SET " + columnText + " WHERE `id` = ?;";
      case PlayTime:
        String inventoryText = "";
        CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
        int playtimeFieldCount =
            (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;

        for (int playtimeFieldIndex = 0;
            playtimeFieldIndex < playtimeFieldCount;
            playtimeFieldIndex++) {
          CMIPlaytimeTablesFields cmiPlaytimeTablesFields =
              cmiPlaytimeTablesFieldses[playtimeFieldIndex];
          if (cmiPlaytimeTablesFields.isTimeField()) {
            if (!inventoryText.isEmpty()) {
              inventoryText = inventoryText + ", ";
            }

            inventoryText = inventoryText + "`" + cmiPlaytimeTablesFields.getColumn() + "` = ?";
          }
        }

        return "UPDATE `" + this.getTableName() + "` SET " + inventoryText + " WHERE `id` = ?;";
      case PlayTimeReward:
        String playtimeText = "";
        CMIPlaytimeRewardTablesFields[] cmiPlaytimeRewardTablesFieldses;
        int rewardFieldCount =
            (cmiPlaytimeRewardTablesFieldses = CMIPlaytimeRewardTablesFields.values()).length;

        for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
          CMIPlaytimeRewardTablesFields cmiPlaytimeRewardTablesFields =
              cmiPlaytimeRewardTablesFieldses[rewardFieldIndex];
          if (cmiPlaytimeRewardTablesFields != CMIPlaytimeRewardTablesFields.player_id) {
            if (!playtimeText.isEmpty()) {
              playtimeText = playtimeText + ", ";
            }

            playtimeText = playtimeText + "`" + cmiPlaytimeRewardTablesFields.getColumn() + "` = ?";
          }
        }

        return "UPDATE `" + this.getTableName() + "` SET " + playtimeText + " WHERE `id` = ?;";
      default:
        return "";
    }
  }

  public String getInsertQuery(CMISqlType cmiSqlType) {
    String string = "";
    String updateText = "";
    switch (this) {
      case UserTable:
        CMIUserTablesFields[] cmiUserTablesFieldses;
        int value = (cmiUserTablesFieldses = CMIUserTablesFields.values()).length;

        for (int index = 0; index < value; index++) {
          CMIUserTablesFields cmiUserTablesFields = cmiUserTablesFieldses[index];
          if (!string.isEmpty()) {
            string = string + ", ";
          }

          string = string + "`" + cmiUserTablesFields.getColumn() + "`";
          if (!updateText.isEmpty()) {
            updateText = updateText + ", ";
          }

          updateText = updateText + "?";
        }
        break;
      case InvTable:
        CMIInventoryTablesFields[] cmiInventoryTablesFieldses;
        int inventoryFieldCount =
            (cmiInventoryTablesFieldses = CMIInventoryTablesFields.values()).length;

        for (int inventoryFieldIndex = 0;
            inventoryFieldIndex < inventoryFieldCount;
            inventoryFieldIndex++) {
          CMIInventoryTablesFields cmiInventoryTablesFields =
              cmiInventoryTablesFieldses[inventoryFieldIndex];
          if (!string.isEmpty()) {
            string = string + ", ";
          }

          string = string + "`" + cmiInventoryTablesFields.getColumn() + "`";
          if (!updateText.isEmpty()) {
            updateText = updateText + ", ";
          }

          updateText = updateText + "?";
        }
        break;
      case PlayTime:
        CMIPlaytimeTablesFields[] cmiPlaytimeTablesFieldses;
        int playtimeFieldCount =
            (cmiPlaytimeTablesFieldses = CMIPlaytimeTablesFields.values()).length;

        for (int playtimeFieldIndex = 0;
            playtimeFieldIndex < playtimeFieldCount;
            playtimeFieldIndex++) {
          CMIPlaytimeTablesFields cmiPlaytimeTablesFields =
              cmiPlaytimeTablesFieldses[playtimeFieldIndex];
          if (!string.isEmpty()) {
            string = string + ", ";
          }

          string = string + "`" + cmiPlaytimeTablesFields.getColumn() + "`";
          if (!updateText.isEmpty()) {
            updateText = updateText + ", ";
          }

          updateText = updateText + "?";
        }
        break;
      case PlayTimeReward:
        CMIPlaytimeRewardTablesFields[] cmiPlaytimeRewardTablesFieldses;
        int rewardFieldCount =
            (cmiPlaytimeRewardTablesFieldses = CMIPlaytimeRewardTablesFields.values()).length;

        for (int rewardFieldIndex = 0; rewardFieldIndex < rewardFieldCount; rewardFieldIndex++) {
          CMIPlaytimeRewardTablesFields cmiPlaytimeRewardTablesFields =
              cmiPlaytimeRewardTablesFieldses[rewardFieldIndex];
          if (!string.isEmpty()) {
            string = string + ", ";
          }

          string = string + "`" + cmiPlaytimeRewardTablesFields.getColumn() + "`";
          if (!updateText.isEmpty()) {
            updateText = updateText + ", ";
          }

          updateText = updateText + "?";
        }
    }

    switch (this) {
      case UserTable:
        if (cmiSqlType.isFallback()) {
          CMIDebug.c(new Object[] {"Using fallback"});
          return "INSERT INTO `"
              + this.getTableName()
              + "` ("
              + string
              + ") VALUES ("
              + updateText
              + ");";
        } else {
          CMIDebug.c(new Object[] {"Using default"});
          switch (cmiSqlType) {
            case SqLite:
              return "INSERT INTO `"
                  + this.getTableName()
                  + "` ("
                  + string
                  + ") SELECT "
                  + updateText
                  + " WHERE NOT EXISTS (SELECT 1 FROM `"
                  + this.getTableName()
                  + "` WHERE "
                  + CMIUserTablesFields.player_uuid.getColumn()
                  + " = ?);";
            case MySQL:
              return "INSERT INTO `"
                  + this.getTableName()
                  + "` ("
                  + string
                  + ") SELECT "
                  + updateText
                  + " WHERE NOT EXISTS (SELECT "
                  + CMIUserTablesFields.player_uuid.getColumn()
                  + " FROM "
                  + this.getTableName()
                  + " WHERE "
                  + CMIUserTablesFields.player_uuid.getColumn()
                  + " = ?) LIMIT 1;";
            case MariaDB:
              return "INSERT INTO `"
                  + this.getTableName()
                  + "` ("
                  + string
                  + ") SELECT "
                  + updateText
                  + " FROM dual WHERE NOT EXISTS (SELECT "
                  + CMIUserTablesFields.player_uuid.getColumn()
                  + " FROM "
                  + this.getTableName()
                  + " WHERE "
                  + CMIUserTablesFields.player_uuid.getColumn()
                  + " = ?) LIMIT 1;";
          }
        }
      default:
        return "INSERT INTO `"
            + this.getTableName()
            + "` ("
            + string
            + ") VALUES ("
            + updateText
            + ");";
    }
  }

  public String getTableName() {
    return DBDAO_NEW.getPrefix() + this.tableName;
  }
}
