package io.lumine.mythic.bukkit.utils.storage.sql.hikari.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map.Entry;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverDataSource implements DataSource {
   private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
   private final String jdbcUrl;
   private final Properties driverProperties;
   private Driver driver;

   public DriverDataSource(String jdbcUrl, String driverClassName, Properties properties, String username, String password) {
      this.jdbcUrl = jdbcUrl;
      this.driverProperties = new Properties();

      for (Entry<Object, Object> entry : properties.entrySet()) {
         this.driverProperties.setProperty(entry.getKey().toString(), entry.getValue().toString());
      }

      if (username != null) {
         this.driverProperties.put("user", this.driverProperties.getProperty("user", username));
      }

      if (password != null) {
         this.driverProperties.put("password", this.driverProperties.getProperty("password", password));
      }

      if (driverClassName != null) {
         Enumeration<Driver> drivers = DriverManager.getDrivers();

         while (drivers.hasMoreElements()) {
            Driver d = drivers.nextElement();
            if (d.getClass().getName().equals(driverClassName)) {
               this.driver = d;
               break;
            }
         }

         if (this.driver == null) {
            LOGGER.warn("Registered driver with driverClassName={} was not found, trying direct instantiation.", driverClassName);

            try {
               Class<?> driverClass = this.getClass().getClassLoader().loadClass(driverClassName);
               this.driver = (Driver)driverClass.newInstance();
            } catch (Exception e) {
               LOGGER.warn("Failed to create instance of driver class {}, trying jdbcUrl resolution", driverClassName, e);
            }
         }
      }

      try {
         if (this.driver == null) {
            this.driver = DriverManager.getDriver(jdbcUrl);
         } else if (!this.driver.acceptsURL(jdbcUrl)) {
            throw new RuntimeException("Driver " + driverClassName + " claims to not accept jdbcUrl, " + jdbcUrl);
         }
      } catch (SQLException ex) {
         throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + jdbcUrl, ex);
      }
   }

   @Override
   public Connection getConnection() throws SQLException {
      return this.driver.connect(this.jdbcUrl, this.driverProperties);
   }

   @Override
   public Connection getConnection(String username, String password) throws SQLException {
      return this.getConnection();
   }

   @Override
   public PrintWriter getLogWriter() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public void setLogWriter(PrintWriter logWriter) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public void setLoginTimeout(int seconds) throws SQLException {
      DriverManager.setLoginTimeout(seconds);
   }

   @Override
   public int getLoginTimeout() throws SQLException {
      return DriverManager.getLoginTimeout();
   }

   @Override
   public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.driver.getParentLogger();
   }

   @Override
   public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   @Override
   public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return false;
   }
}
