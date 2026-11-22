package litebans;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;
import javax.sql.DataSource;

public final class DriverDataSource implements DataSource {
   private static final PoolLogger POOL_LOGGER = PoolLogger.computePoolLogger(DriverDataSource.class);
   private static final String TEXT = "password";
   private static final String USER_PROPERTY = "user";
   private final String url;
   private final Properties properties;
   private Driver parentLogger;

   public DriverDataSource(String text, String text2, Properties properties, String text3, String text4) {
      this.url = text;
      this.properties = new Properties();

      for (Entry entry : properties.entrySet()) {
         this.properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
      }

      if (text3 != null) {
         this.properties.put("user", this.properties.getProperty("user", text3));
      }

      if (text4 != null) {
         this.properties.put("password", this.properties.getProperty("password", text4));
      }

      if (text2 != null) {
         Enumeration drivers = DriverManager.getDrivers();

         while (drivers.hasMoreElements()) {
            Driver nextElement2 = (Driver)drivers.nextElement();
            if (nextElement2.getClass().getName().equals(text2)) {
               this.parentLogger = nextElement2;
               break;
            }
         }

         if (this.parentLogger == null) {
            POOL_LOGGER.handleText4("Registered driver with driverClassName={} was not found, trying direct instantiation.", new Object[]{text2});
            Class class2 = null;
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

            try {
               if (contextClassLoader != null) {
                  try {
                     class2 = contextClassLoader.loadClass(text2);
                     POOL_LOGGER.handleText("Driver class {} found in Thread context class loader {}", new Object[]{text2, contextClassLoader});
                  } catch (ClassNotFoundException e) {
                     POOL_LOGGER.handleText(
                        "Driver class {} not found in Thread context class loader {}, trying classloader {}",
                        new Object[]{text2, contextClassLoader, this.getClass().getClassLoader()}
                     );
                  }
               }

               if (class2 == null) {
                  class2 = this.getClass().getClassLoader().loadClass(text2);
                  POOL_LOGGER.handleText("Driver class {} found in the HikariConfig class classloader {}", new Object[]{text2, this.getClass().getClassLoader()});
               }
            } catch (ClassNotFoundException e2) {
               POOL_LOGGER.handleText("Failed to load driver class {} from HikariConfig class classloader {}", new Object[]{text2, this.getClass().getClassLoader()});
            }

            if (class2 != null) {
               try {
                  this.parentLogger = (Driver)class2.getDeclaredConstructor().newInstance();
               } catch (Exception e3) {
                  POOL_LOGGER.handleText4("Failed to create instance of driver class {}, trying jdbcUrl resolution", new Object[]{text2, e3});
               }
            }
         }
      }

      String replaceAll2 = text.replaceAll("([?&;]password=)[^&#;]*(.*)", "$1<masked>$2");

      try {
         if (this.parentLogger == null) {
            this.parentLogger = DriverManager.getDriver(text);
            POOL_LOGGER.handleText("Loaded driver with class name {} for jdbcUrl={}", new Object[]{this.parentLogger.getClass().getName(), replaceAll2});
         } else if (!this.parentLogger.acceptsURL(text)) {
            throw new RuntimeException("Driver " + text2 + " claims to not accept jdbcUrl, " + replaceAll2);
         }
      } catch (SQLException e4) {
         throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + replaceAll2, e4);
      }
   }

   @Override
   public Connection getConnection() {
      try {
         return this.parentLogger.connect(this.url, this.properties);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Connection getConnection(String text, String text2) {
      try {
         Properties clone2 = (Properties)this.properties.clone();
         if (text != null) {
            clone2.put("user", text);
            if (clone2.containsKey("username")) {
               clone2.put("username", text);
            }
         }

         if (text2 != null) {
            clone2.put("password", text2);
         }

         return this.parentLogger.connect(this.url, clone2);
      } catch (SQLException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public PrintWriter getLogWriter() {
      try {
         throw new SQLFeatureNotSupportedException();
      } catch (SQLFeatureNotSupportedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLogWriter(PrintWriter printWriter) {
      try {
         throw new SQLFeatureNotSupportedException();
      } catch (SQLFeatureNotSupportedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public void setLoginTimeout(int value) {
      DriverManager.setLoginTimeout(value);
   }

   @Override
   public int getLoginTimeout() {
      return DriverManager.getLoginTimeout();
   }

   @Override
   public Logger getParentLogger() {
      try {
         return this.parentLogger.getParentLogger();
      } catch (SQLFeatureNotSupportedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public Object unwrap(Class class2) {
      try {
         throw new SQLFeatureNotSupportedException();
      } catch (SQLFeatureNotSupportedException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean isWrapperFor(Class class2) {
      return false;
   }
}
