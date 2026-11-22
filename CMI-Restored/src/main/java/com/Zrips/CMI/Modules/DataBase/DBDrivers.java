package com.Zrips.CMI.Modules.DataBase;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DBDrivers implements Driver {
  private Driver driver;

  public DBDrivers(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Connection connect(String string, Properties properties) throws SQLException {
    return this.driver.connect(string, properties);
  }

  @Override
  public boolean acceptsURL(String string) throws SQLException {
    return this.driver.acceptsURL(string);
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String string, Properties properties)
      throws SQLException {
    return this.driver.getPropertyInfo(string, properties);
  }

  @Override
  public int getMajorVersion() {
    return this.driver.getMajorVersion();
  }

  @Override
  public int getMinorVersion() {
    return this.driver.getMinorVersion();
  }

  @Override
  public boolean jdbcCompliant() {
    return this.driver.jdbcCompliant();
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return this.driver.getParentLogger();
  }
}
