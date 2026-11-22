package com.Zrips.CMI.Modules.DataBase_NEW;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSQLite_NEW extends DBDAO_NEW {
  public void initialize() {
    this.setUp();
  }

  DBSQLite_NEW(CMI cmi, File file) {
    super(
        cmi,
        "org.sqlite.JDBC",
        "jdbc:sqlite:" + new File(file, "cmi.sqlite.db").getPath(),
        null,
        null,
        "");
    if (!file.exists()) {
      file.mkdirs();
    }

    this.setDbFormat(CMISqlType.SqLite);
  }

  @Override
  public boolean createTable(String string) {
    Statement statement = null;
    Connection connection = this.getConnection();

    try {
      if (string != null && !string.equals("")) {
        statement = connection.createStatement();
        statement.execute(string);
        this.close(statement);
        this.close(connection);
        return true;
      } else {
        this.plugin.consoleMessage("&cCould not create table: query is empty or null.");
        return false;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not create table, SQLException: " + sqlException.getMessage());
      this.close(statement);
      this.close(connection);
      return false;
    }
  }

  @Override
  public boolean isTable(String string) {
    ResultSet resultSet = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      DatabaseMetaData databaseMetaData = connection.getMetaData();
      resultSet = databaseMetaData.getTables(null, null, string, null);
      if (resultSet.next()) {
        flag = true;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check if table \""
              + string
              + "\" exists, SQLException: "
              + sqlException.getMessage());
    } finally {
      this.close(resultSet);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public boolean isCollumn(String string, String columnName) {
    DatabaseMetaData databaseMetaData = null;
    Connection connection = this.getConnection();
    ResultSet resultSet = null;
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      databaseMetaData = connection.getMetaData();
      resultSet = databaseMetaData.getColumns(null, null, string, columnName);
      if (resultSet.next()) {
        flag = true;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check if table \""
              + string
              + "\" exists, SQLException: "
              + sqlException.getMessage());
    } finally {
      this.close(resultSet);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public boolean addCollumn(String string, String columnName, String columnType) {
    Statement statement = null;
    Connection connection = this.getConnection();
    if (connection == null) {
      return false;
    }

    boolean flag = false;

    try {
      statement = connection.createStatement();
      statement.executeQuery("ALTER TABLE " + string + " ADD " + columnName + " " + columnType);
      flag = true;
    } catch (SQLException sqlException) {
      flag = false;
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
      if (!this.isTable(string)) {
        this.plugin.consoleMessage("&cTable \"" + string + "\" does not exist.");
        flag = false;
      } else {
        statement = connection.createStatement();
        statement.executeQuery("DELETE FROM " + string + ";");
        flag = true;
      }
    } catch (SQLException sqlException) {
      if (!sqlException.getMessage().toLowerCase().contains("locking")
          && !sqlException.getMessage().toLowerCase().contains("locked")
          && !sqlException.toString().contains("not return ResultSet")) {
        this.plugin.consoleMessage("&cError in wipeTable() query: " + sqlException);
      }
    } finally {
      this.close(statement);
      this.close(connection);
    }

    return flag;
  }

  @Override
  public String getTableCharSet(CMIDBTables cmidbTables) {
    return null;
  }

  @Override
  public boolean convertTableToUTF8(CMIDBTables cmidbTables) {
    return true;
  }

  @Override
  public String getTableRowFormat(CMIDBTables cmidbTables) {
    return null;
  }

  @Override
  public boolean convertTableRowFormat(CMIDBTables cmidbTables) {
    return true;
  }
}
