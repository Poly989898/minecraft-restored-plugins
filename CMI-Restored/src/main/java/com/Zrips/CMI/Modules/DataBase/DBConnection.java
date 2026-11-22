package com.Zrips.CMI.Modules.DataBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
  private Connection conn;

  public DBConnection(Connection connection) {
    this.conn = connection;
  }

  public synchronized boolean isClosed() {
    try {
      return this.getConnection().isClosed();
    } catch (SQLException sqlException) {
      return true;
    }
  }

  public synchronized boolean isValid(int value) throws SQLException {
    try {
      return this.getConnection().isValid(value);
    } catch (AbstractMethodError abstractMethodError) {
      return true;
    }
  }

  public synchronized void closeConnection() throws SQLException {
    this.getConnection().close();
  }

  public synchronized Statement createStatement() throws SQLException {
    return this.getConnection().createStatement();
  }

  public synchronized PreparedStatement prepareStatement(String string) throws SQLException {
    return this.getConnection().prepareStatement(string);
  }

  public synchronized PreparedStatement prepareStatement(String string, int value)
      throws SQLException {
    return this.getConnection().prepareStatement(string, value);
  }

  public synchronized void setAutoCommit(Boolean autoCommit) throws SQLException {
    this.getConnection().setAutoCommit(autoCommit);
  }

  public synchronized void commit() throws SQLException {
    this.getConnection().commit();
  }

  public synchronized DatabaseMetaData getMetaData() throws SQLException {
    return this.getConnection().getMetaData();
  }

  public Connection getConnection() {
    return this.conn;
  }
}
