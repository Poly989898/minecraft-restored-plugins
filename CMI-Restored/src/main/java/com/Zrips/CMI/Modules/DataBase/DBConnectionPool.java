package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionPool {
  private DBConnection connection;
  private String url;
  private String username;
  private String password;

  public DBConnectionPool(String string, String jdbcUrl, String userName, String dbPassword)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
    Driver driver =
        (Driver) Class.forName(string, true, CMI.getInstance().getDBClassloader()).newInstance();
    DBDrivers dbDrivers = new DBDrivers(driver);
    DriverManager.registerDriver(dbDrivers);
    this.url = jdbcUrl;
    this.username = userName;
    this.password = dbPassword;
  }

  public synchronized DBConnection getConnection() throws SQLException {
    try {
      if (this.connection != null && (this.connection.isClosed() || !this.connection.isValid(1))) {
        try {
          this.connection.closeConnection();
        } catch (SQLException sqlException) {
        }

        this.connection = null;
      }
    } catch (SQLException sqlException2) {
      this.connection.closeConnection();
    }

    if (this.connection == null) {
      Connection rawConnection =
          DriverManager.getConnection(this.url, this.username, this.password);
      this.connection = new DBConnection(rawConnection);
    }

    return this.connection;
  }

  public Connection getTemporaryConnection() throws SQLException {
    try {
      return DriverManager.getConnection(this.url, this.username, this.password);
    } catch (SQLException sqlException) {
      sqlException.printStackTrace();
      return null;
    }
  }

  public synchronized void closeConnection() throws SQLException {
    if (this.connection != null) {
      try {
        this.connection.closeConnection();
      } catch (SQLException sqlException) {
      }
    }
  }
}
