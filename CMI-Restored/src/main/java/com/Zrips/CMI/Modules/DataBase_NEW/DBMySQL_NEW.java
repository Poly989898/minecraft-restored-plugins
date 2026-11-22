package com.Zrips.CMI.Modules.DataBase_NEW;

import com.Zrips.CMI.CMI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBMySQL_NEW extends DBDAO_NEW {
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

  public DBMySQL_NEW(
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
    this.database = text;
    this.setDbFormat(CMISqlType.MySQL);
  }

  public void initialize() {
    this.setUp();
  }

  public DBMySQL_NEW initialize(
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
    DBMySQL_NEW dbMySQL_NEW =
        new DBMySQL_NEW(
            cmi, string, text, textSecond, textThird, textFourth, flag, flagSecond, flagThird);

    dbMySQL_NEW.setUp();

    return dbMySQL_NEW;
  }

  @Override
  public boolean createTable(String string) {
    this.plugin.consoleMessage(string);
    Statement statement = null;
    if (string != null && !string.equals("")) {
      Connection connection = this.getConnection();
      if (connection == null) {
        return false;
      }

      boolean flag = false;

      try {
        statement = connection.createStatement();
        statement.execute(string);
        flag = true;
      } catch (SQLException sqlException) {
        this.plugin.consoleMessage(
            "&cCould not create table, SQLException: " + sqlException.getMessage());
      } finally {
        this.close(statement);
        this.close(connection);
      }

      return flag;
    } else {
      this.plugin.consoleMessage("&cCould not create table: query is empty or null.");
      return false;
    }
  }

  @Override
  public boolean isTable(String string) {
    Connection connection = this.getConnection();
    Statement statement = null;
    ResultSet resultSet = null;
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      statement = connection.createStatement();
      resultSet =
          connection.getMetaData().getTables(this.database, null, string, new String[] {"TABLE"});
      flag = resultSet.next();
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "Not a table |SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='"
              + string
              + "';"
              + "|");
    } finally {
      this.close(resultSet);
      this.close(statement);
    }

    if (flag) {
      this.close(connection);
      return true;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSetSecond = null;

    try {
      preparedStatement =
          connection.prepareStatement(
              "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='" + string + "';");
      resultSetSecond = preparedStatement.executeQuery();
      flag = resultSetSecond.next();
    } catch (SQLException sqlException2) {
      this.plugin.consoleMessage(
          "Not a table |SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME ='"
              + string
              + "';"
              + "|");
    } finally {
      this.close(resultSetSecond);
      this.close(preparedStatement);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public boolean isCollumn(String string, String text) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    ResultSet resultSet = null;
    boolean flag = false;

    try {
      statement = connection.createStatement();
      DatabaseMetaData databaseMetaData = connection.getMetaData();
      resultSet = databaseMetaData.getColumns(this.database, null, string, text);
      flag = resultSet.next();
    } catch (SQLException sqlException) {
    } finally {
      this.close(resultSet);
      this.close(statement);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public boolean addCollumn(String string, String text, String textSecond) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      statement = connection.createStatement();
      this.plugin.consoleMessage(
          "Creating culumn |" + string + " " + text + " " + textSecond + "|");
      statement.executeUpdate(
          "ALTER TABLE `" + string + "` ADD COLUMN `" + text + "` " + textSecond + ";");
      flag = true;
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
    } finally {
      this.close(statement);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public boolean truncate(String string) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      if (this.isTable(string)) {
        statement = connection.createStatement();
        statement.executeUpdate("DELETE FROM " + string + ";");
        flag = true;
        return flag;
      }

      this.plugin.consoleMessage("&cTable \"" + string + "\" does not exist.");
      this.close(connection);
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not wipe table, SQLException: " + sqlException.getMessage());
      sqlException.printStackTrace();
      return flag;
    } finally {
      this.close(statement);
      this.close(connection);
    }

    return false;
  }

  @Override
  public String getTableCharSet(CMIDBTables cmidbTables) {
    Object object = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return null;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String string = null;

    try {
      if (this.isTable(cmidbTables.getTableName())) {
        preparedStatement =
            connection.prepareStatement(
                "SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_COLLATION, ROW_FORMAT FROM"
                    + " INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='"
                    + cmidbTables.getTableName()
                    + "';");
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          string = resultSet.getString(3);
        }

        return string;
      }

      this.plugin.consoleMessage("&cTable \"" + cmidbTables.getTableName() + "\" does not exist.");
      this.close(connection);
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check table chatser, SQLException: " + sqlException.getMessage());
      sqlException.printStackTrace();
      return string;
    } finally {
      this.close(resultSet);
      this.close(preparedStatement);
      this.close(connection);
    }

    return null;
  }

  @Override
  public String getTableRowFormat(CMIDBTables cmidbTables) {
    Connection connection = this.getConnection();
    if (connection == null) {
      return null;
    }

    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    String string = null;

    try {
      if (this.isTable(cmidbTables.getTableName())) {
        preparedStatement =
            connection.prepareStatement(
                "SELECT ROW_FORMAT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME ='"
                    + cmidbTables.getTableName()
                    + "';");
        resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
          string = resultSet.getString(1);
        }

        return string;
      }

      this.plugin.consoleMessage("&cTable \"" + cmidbTables.getTableName() + "\" does not exist.");
      this.close(connection);
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check table chatser, SQLException: " + sqlException.getMessage());
      sqlException.printStackTrace();
      return string;
    } finally {
      this.close(resultSet);
      this.close(preparedStatement);
      this.close(connection);
    }

    return null;
  }

  @Override
  public boolean convertTableToUTF8(CMIDBTables cmidbTables) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    try {
      if (this.isTable(cmidbTables.getTableName())) {
        statement = connection.createStatement();
        statement.executeUpdate(
            "ALTER TABLE `" + cmidbTables.getTableName() + "` CONVERT TO CHARSET utf;");
        return true;
      }

      this.plugin.consoleMessage("&cTable \"" + cmidbTables.getTableName() + "\" does not exist.");
      this.close(connection);
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not convert table, SQLException: " + sqlException.getMessage());
      sqlException.printStackTrace();
      return true;
    } finally {
      this.close(statement);
      this.close(connection);
    }

    return false;
  }

  @Override
  public boolean convertTableRowFormat(CMIDBTables cmidbTables) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    try {
      if (this.isTable(cmidbTables.getTableName())) {
        statement = connection.createStatement();
        statement.executeUpdate(
            "ALTER TABLE " + cmidbTables.getTableName() + " ROW_FORMAT=DYNAMIC;");
        return true;
      }

      this.plugin.consoleMessage("&cTable \"" + cmidbTables.getTableName() + "\" does not exist.");
      this.close(connection);
    } catch (SQLException sqlException) {
      return true;
    } finally {
      this.close(statement);
      this.close(connection);
    }

    return false;
  }
}
