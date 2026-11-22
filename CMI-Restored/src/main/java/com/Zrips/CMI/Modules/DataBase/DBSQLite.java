package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import net.Zrips.CMILib.Logs.CMIDebug;

public class DBSQLite extends DBDAO {
  public void initialize() {
    this.setUp();
  }

  public DBSQLite(CMI cmi, File file) {
    super(cmi, "org.sqlite.JDBC", "jdbc:sqlite:" + file.getPath(), null, null, "");
    if (!file.exists()) {
      file.mkdirs();
    }

    this.setDbType(DBManager.DataBaseType.SqLite);
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
    Statement statement = null;

    try {
      if (string != null && !string.equals("")) {
        statement = this.getConnection().createStatement();
        statement.execute(string);
        statement.close();
        return true;
      } else {
        this.plugin.consoleMessage("&cCould not create table: query is empty or null.");
        return false;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not create table, SQLException: " + sqlException.getMessage());
      close(statement);
      return false;
    }
  }

  @Override
  public boolean isTable(String string) {
    DatabaseMetaData databaseMetaData = null;

    try {
      databaseMetaData = this.getConnection().getMetaData();
      ResultSet resultSet = databaseMetaData.getTables(null, null, string, null);
      if (resultSet.next()) {
        resultSet.close();
        return true;
      } else {
        resultSet.close();
        return false;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check if table \""
              + string
              + "\" exists, SQLException: "
              + sqlException.getMessage());
      return false;
    }
  }

  @Override
  public boolean isCollumn(String string, String columnName) {
    DatabaseMetaData databaseMetaData = null;

    try {
      databaseMetaData = this.getConnection().getMetaData();
      ResultSet resultSet = databaseMetaData.getColumns(null, null, string, columnName);
      if (resultSet.next()) {
        resultSet.close();
        return true;
      } else {
        resultSet.close();
        return false;
      }
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not check if table \""
              + string
              + "\" exists, SQLException: "
              + sqlException.getMessage());
      return false;
    }
  }

  @Override
  public boolean addCollumn(String string, String columnName, String columnType) {
    Statement statement;
    try {
      statement = this.getConnection().createStatement();
    } catch (SQLException sqlException) {
      this.plugin.consoleMessage(
          "&cCould not add new collumn, SQLException: " + sqlException.getMessage());
      return false;
    }

    try {
      statement.executeQuery("ALTER TABLE " + string + " ADD " + columnName + " " + columnType);
      statement.close();
      return true;
    } catch (SQLException sqlException2) {
      close(statement);
      return false;
    }
  }

  @Override
  public boolean truncate(String string) {
    Statement statement = null;

    try {
      if (!this.isTable(string)) {
        this.plugin.consoleMessage("&cTable \"" + string + "\" does not exist.");
        return false;
      } else {
        statement = this.getConnection().createStatement();
        statement.executeQuery("DELETE FROM " + string + ";");
        statement.close();
        return true;
      }
    } catch (SQLException sqlException) {
      if (!sqlException.getMessage().toLowerCase().contains("locking")
          && !sqlException.getMessage().toLowerCase().contains("locked")
          && !sqlException.toString().contains("not return ResultSet")) {
        this.plugin.consoleMessage("&cError in wipeTable() query: " + sqlException);
      }

      close(statement);
      return false;
    }
  }

  @Override
  public String getTableCharSet(DBDAO.DBTables dbTables) {
    return null;
  }

  @Override
  public boolean convertTableToUTF8(DBDAO.DBTables dbTables) {
    return true;
  }

  @Override
  public String getTableRowFormat(DBDAO.DBTables dbTables) {
    return null;
  }

  @Override
  public boolean convertTableRowFormat(DBDAO.DBTables dbTables) {
    return true;
  }

  @Override
  public boolean isColumnUnique(DBDAO.DBTables dbTables, String string) {
    String sql =
        "SELECT name FROM sqlite_master WHERE type = 'index' AND tbl_name = ? AND sql LIKE"
            + " '%UNIQUE%' AND sql LIKE '%' || ? || '%' ";
    DBConnection dbConnection = this.getConnection();
    if (dbConnection == null) {
      return false;
    }

    boolean flag = false;
    Object object = null;
    ResultSet resultSet = null;

    try {
      PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
      preparedStatement.setString(1, dbTables.getTableName());
      preparedStatement.setString(2, string);
      resultSet = preparedStatement.executeQuery();
      flag = resultSet.next();
      CMIDebug.c(new Object[] {"MYSQL isColumnUnique", dbTables.getTableName(), string, flag});
    } catch (SQLException sqlException) {
    } finally {
      close(resultSet);
      close((Statement) object);
    }

    return flag;
  }
}
