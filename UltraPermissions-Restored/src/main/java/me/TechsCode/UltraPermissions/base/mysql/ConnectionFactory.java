package me.TechsCode.UltraPermissions.base.mysql;

import java.sql.Connection;
import java.sql.SQLException;
import me.TechsCode.UltraPermissions.base.debug.Debugger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionFactory {
   private HikariDataSource dataSource;
   private String error;

   public ConnectionFactory(MySQLCredentials mySQLCredentials, boolean flag, int minIdle, int maxPoolSize) {
      Debugger.debug("MySQL", "Connecting to MySQL database...");
      HikariConfig hikariConfig = new HikariConfig();
      hikariConfig.setJdbcUrl(
         "jdbc:mysql://"
            + mySQLCredentials.getHostname()
            + ":"
            + mySQLCredentials.getPort()
            + "/"
            + mySQLCredentials.getDatabase()
            + "?useSSL="
            + flag
            + "&characterEncoding=utf-8&autoReconnect=true"
      );
      hikariConfig.setUsername(mySQLCredentials.getUsername());
      hikariConfig.setPassword(mySQLCredentials.getPassword());
      hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
      hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
      hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

      try {
         this.dataSource = new HikariDataSource(hikariConfig);
         this.dataSource.setMinimumIdle(minIdle);
         this.dataSource.setMaximumPoolSize(maxPoolSize);
         this.dataSource.setMaxLifetime(1800000L);
         this.dataSource.setConnectionTimeout(5000L);
         this.dataSource.setLeakDetectionThreshold(48000L);
      } catch (Exception exception) {
         this.error = exception.getMessage();
         Debugger.debug("MySQL", "Could not connect to MySQL database: " + exception.getMessage());
      }

      Debugger.debug("MySQL", "Connected to MySQL database.");
   }

   public Connection newConnection() throws SQLException {
      return this.dataSource != null ? this.dataSource.getConnection() : null;
   }

   public ConnectionTestResult testConnection() {
      if (this.error != null) {
         return new ConnectionTestResult(false, this.error);
      }

      try {
         long time = System.currentTimeMillis();
         Connection connection = this.newConnection();
         long endTime = System.currentTimeMillis();
         if (endTime - time > 2000L) {
            return new ConnectionTestResult(false, "Connection is slow, therefor it is possible that data is not being saved.");
         } else if (connection != null) {
            connection.close();
            return new ConnectionTestResult(true, "Connection is valid");
         } else {
            return new ConnectionTestResult(false, "Connection is null (not established)");
         }
      } catch (SQLException exception) {
         return new ConnectionTestResult(false, exception.getMessage());
      }
   }

   public void close() {
      if (this.dataSource != null) {
         this.dataSource.close();
      }
   }

   public int getMaximumPoolSize() {
      return this.dataSource.getMaximumPoolSize();
   }

   public int getActiveConnections() {
      return this.dataSource.getHikariPoolMXBean().getActiveConnections();
   }
}
