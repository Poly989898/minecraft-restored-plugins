package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.Zrips.CMILib.Logs.CMIDebug;

public class DBMySQL extends DBDAO {
  private CMI plugin;
  private String database;
  private static String path = "";

  static {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      path = "com.mysql.cj.jdbc.Driver";
    } catch (Throwable var6) {
      path = "com.mysql.jdbc.Driver";
    }

    String text = "";
    String[] parts = System.getProperty("java.runtime.version").split("\\.|_|-b");

    try {
      text = parts[0];
    } catch (Throwable var5) {
      String textSecond = System.getProperty("java.version");
      String[] partsSecond = textSecond.split("_")[0].split(".");
      text = partsSecond[1];
    }

    CMI.getInstance().consoleMessage("Detected Java" + text);
  }

  public DBMySQL(
      CMI cmi,
      String string,
      String text,
      String textSecond,
      String textThird,
      String textFourth,
      boolean flag,
      boolean flagSecond,
      boolean flagThird) {
    super(
        cmi,
        path,
        "jdbc:mysql://"
            + string
            + "/"
            + text
            + "?autoReconnect="
            + flag
            + "&useSSL="
            + flagThird
            + "&verifyServerCertificate="
            + flagSecond
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true&characterEncoding=utf8mb4_unicode_ci",
        textSecond,
        textThird,
        textFourth);
    this.plugin = cmi;
    this.database = text;
    this.setDbType(DBManager.DataBaseType.MySQL);
  }

  public void initialize() {
    this.setUp();
  }

  public DBMySQL initialize(
      CMI cmi,
      String string,
      String text,
      String textSecond,
      String textThird,
      String textFourth,
      boolean flag,
      boolean flagSecond,
      boolean flagThird) {
    this.plugin = cmi;
    DBMySQL dbMySQL =
        new DBMySQL(
            cmi, string, text, textSecond, textThird, textFourth, flag, flagSecond, flagThird);

    dbMySQL.setUp();

    return dbMySQL;
  }

  @Override
  public Statement prepareStatement(String string) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return null;
    }

    PreparedStatement preparedStatement = null;

    try {
      preparedStatement = dbConnection.prepareStatement(string);
    } catch (SQLException | NumberFormatException value) {
      value.printStackTrace();
    }

    return preparedStatement;
  }

  @Override
  public boolean createTable(String string) {
    this.plugin.consoleMessage(string);
    Statement statement = null;
    if (string != null && !string.equals("")) {
      DBConnection dbConnection = this.getConnection();
      if (dbConnection == null) {
        return false;
      }

      try {
        statement = dbConnection.createStatement();
        statement.execute(string);
        statement.close();
        return true;
      } catch (SQLException sqlException) {
        this.plugin.consoleMessage(
            "&cCould not create table, SQLException: " + sqlException.getMessage());
        close(statement);
      } finally {
        close(statement);
      }

      return false;
    } else {
      this.plugin.consoleMessage("&cCould not create table: query is empty or null.");
      return false;
    }
  }

  @Override
  public boolean isTable(String string) {
    Statement statement = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    breakSecond:
    {
      try {
        statement = dbConnection.createStatement();
        break breakSecond;
      } catch (SQLException sqlException) {
        this.plugin.consoleMessage(
            "&cCould not check if its table, SQLException: " + sqlException.getMessage());
      } finally {
        close(statement);
      }

      return false;
    }

    try {
      ResultSet resultSet =
          dbConnection.getMetaData().getTables(this.database, null, string, new String[] {"TABLE"});
      return resultSet.next();
    } catch (SQLException sqlException2) {
      this.plugin.consoleMessage(
          "Not a table |SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='"
              + string
              + "';"
              + "|");
      PreparedStatement preparedStatement = null;
      ResultSet resultSetSecond = null;

      try {
        preparedStatement =
            dbConnection.prepareStatement(
                "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + string + "';");
        resultSetSecond = preparedStatement.executeQuery();
        if (resultSetSecond.next()) {
          resultSetSecond.close();
          preparedStatement.close();
          return true;
        }
      } catch (SQLException sqlException3) {
        this.plugin.consoleMessage(
            "Not a table |SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='"
                + string
                + "';"
                + "|");
        return false;
      } finally {
        close(statement);
        close(preparedStatement);
        close(resultSetSecond);
      }

      return false;
    }
  }

  @Override
  public boolean isCollumn(String string, String text) {
    Statement statement = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    breakSecond:
    {
      try {
        statement = dbConnection.createStatement();
        break breakSecond;
      } catch (SQLException sqlException) {
        this.plugin.consoleMessage(
            "&cCould not check if its collumn, SQLException: " + sqlException.getMessage());
      } finally {
        close(statement);
      }

      return false;
    }

    ResultSet resultSet = null;

    try {
      DatabaseMetaData databaseMetaData = dbConnection.getMetaData();
      resultSet = databaseMetaData.getColumns(this.database, null, string, text);
      if (resultSet.next()) {
        resultSet.close();
        return true;
      }
    } catch (SQLException sqlException2) {
      return false;
    } finally {
      close(resultSet);
    }

    return false;
  }

  @Override
  public boolean addCollumn(String string, String text, String textSecond) {
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    Statement statement;
    try {
      statement = dbConnection.createStatement();
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not add new collumn, SQLException: " + sqlException.getMessage());
      return false;
    }

    try {
      this.plugin.consoleMessage(
          "Creating culumn |" + string + " " + text + " " + textSecond + "|");
      statement.executeUpdate(
          "ALTER TABLE `" + string + "` ADD COLUMN `" + text + "` " + textSecond + ";");
      statement.close();
      return true;
    } catch (SQLException sqlException2) {
      close(statement);
      sqlException2.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean truncate(String string) {
    Statement statement = null;
    String text = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    try {
      if (!this.isTable(string)) {
        this.plugin.consoleMessage("&cTable \"" + string + "\" does not exist.");
        return false;
      } else {
        statement = dbConnection.createStatement();
        text = "DELETE FROM " + string + ";";
        statement.executeUpdate(text);
        statement.close();
        return true;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not wipe table, SQLException: " + sqlException.getMessage());
      close(statement);
      sqlException.printStackTrace();
      return false;
    }
  }

  @Override
  public String getTableCharSet(DBDAO.DBTables dbTables) {
    Object object = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return null;
    }

    try {
      if (!this.isTable(dbTables.getTableName())) {
        this.plugin.consoleMessage("&cTable \"" + dbTables.getTableName() + "\" does not exist.");
        return null;
      }

      PreparedStatement preparedStatement =
          dbConnection.prepareStatement(
              "SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_COLLATION, ROW_FORMAT FROM"
                  + " INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='"
                  + dbTables.getTableName()
                  + "';");
      ResultSet resultSet = preparedStatement.executeQuery();
      String string = null;
      if (resultSet.next()) {
        string = resultSet.getString(3);
      }

      resultSet.close();
      preparedStatement.close();
      return string;
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check table chatser, SQLException: " + sqlException.getMessage());
      close((Statement) object);
      sqlException.printStackTrace();
      return null;
    }
  }

  @Override
  public String getTableRowFormat(DBDAO.DBTables dbTables) {
    Object object = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return null;
    }

    try {
      if (!this.isTable(dbTables.getTableName())) {
        this.plugin.consoleMessage("&cTable \"" + dbTables.getTableName() + "\" does not exist.");
        return null;
      }

      PreparedStatement preparedStatement =
          dbConnection.prepareStatement(
              "SELECT ROW_FORMAT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='"
                  + dbTables.getTableName()
                  + "';");
      ResultSet resultSet = preparedStatement.executeQuery();
      String string = null;
      if (resultSet.next()) {
        string = resultSet.getString(1);
      }

      resultSet.close();
      preparedStatement.close();
      return string;
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check table chatser, SQLException: " + sqlException.getMessage());
      close((Statement) object);
      sqlException.printStackTrace();
      return null;
    }
  }

  @Override
  public boolean convertTableToUTF8(DBDAO.DBTables dbTables) {
    Statement statement = null;
    String string = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    try {
      if (this.isTable(dbTables.getTableName())) {
        statement = dbConnection.createStatement();
        string = "ALTER TABLE `" + dbTables.getTableName() + "` CONVERT TO CHARSET utf;";
        statement.executeUpdate(string);
        return true;
      }

      this.plugin.consoleMessage("&cTable \"" + dbTables.getTableName() + "\" does not exist.");
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not convert table, SQLException: " + sqlException.getMessage());
      sqlException.printStackTrace();
      return true;
    } finally {
      close(statement);
    }

    return false;
  }

  @Override
  public boolean convertTableRowFormat(DBDAO.DBTables dbTables) {
    Statement statement = null;
    String string = null;
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    try {
      if (this.isTable(dbTables.getTableName())) {
        statement = dbConnection.createStatement();
        string = "ALTER TABLE " + dbTables.getTableName() + " ROW_FORMAT=DYNAMIC;";
        statement.executeUpdate(string);
        return true;
      }

      this.plugin.consoleMessage("&cTable \"" + dbTables.getTableName() + "\" does not exist.");
    } catch (SQLException sqlException) {
      return true;
    } finally {
      close(statement);
    }

    return false;
  }

  @Override
  public boolean isColumnUnique(DBDAO.DBTables dbTables, String string) {
    CMIDebug.c(new Object[] {"Check if unique", dbTables.getTableName(), string});
    String text =
        "SELECT COUNT(*) AS is_unique FROM information_schema.TABLE_CONSTRAINTS tc JOIN"
            + " information_schema.KEY_COLUMN_USAGE kcu ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME"
            + " AND tc.TABLE_SCHEMA = kcu.TABLE_SCHEMA AND tc.TABLE_NAME = kcu.TABLE_NAME WHERE"
            + " tc.TABLE_NAME = ? AND kcu.COLUMN_NAME = ? AND tc.CONSTRAINT_TYPE = 'UNIQUE' ";
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    boolean flag = false;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      preparedStatement = dbConnection.prepareStatement(text);
      preparedStatement.setString(1, dbTables.getTableName());
      preparedStatement.setString(2, string);
      resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        flag = resultSet.getInt("is_unique") > 0;
        CMIDebug.c(new Object[] {"MYSQL isColumnUnique", dbTables.getTableName(), string, flag});
      }
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } finally {
      close(resultSet);
      close(preparedStatement);
    }

    return flag;
  }
}
