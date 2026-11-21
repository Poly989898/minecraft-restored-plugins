package io.lumine.mythic.bukkit.utils.storage.sql;

import io.lumine.mythic.bukkit.utils.lib.jooq.DSLContext;
import io.lumine.mythic.bukkit.utils.lib.jooq.impl.DSL;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.plugin.PluginModule;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariConfig;
import io.lumine.mythic.bukkit.utils.storage.sql.hikari.HikariDataSource;

public class SqlConnector<T extends LuminePlugin> extends PluginModule<T> {
   private SqlProvider provider;
   private SqlCredentials credentials;
   private HikariDataSource dataSource;

   public SqlConnector(T plugin, SqlProvider provider, SqlCredentials credentials) {
      super(plugin, false);
      this.provider = provider;
      this.credentials = credentials;
      this.load(plugin);
   }

   @Override
   public void load(T plugin) {
      String user = this.credentials.getUsername();
      String pass = this.credentials.getPassword();
      String address = this.provider.formatAddress(this.credentials);
      System.setProperty("io.lumine.mythic.bukkit.utils.lib.jooq.no-tips", "true");
      System.setProperty("io.lumine.mythic.bukkit.utils.lib.jooq.no-logo", "true");

      try {
         HikariConfig config = new HikariConfig();
         config.setJdbcUrl(address);
         config.setUsername(user);
         config.setPassword(pass);
         config.addDataSourceProperty("port", this.credentials.getPort());
         config.addDataSourceProperty("databaseName", this.credentials.getDatabase());
         config.addDataSourceProperty("cachePrepStmts", "true");
         config.addDataSourceProperty("prepStmtCacheSize", "250");
         config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         config.addDataSourceProperty("useSSL", "false");
         this.dataSource = new HikariDataSource(config);
         this.dataSource.setMinimumIdle(1);
         this.dataSource.setMaximumPoolSize(3);
         this.dataSource.setConnectionTimeout(1000L);
         this.dataSource.setIdleTimeout(60000L);
         this.dataSource.setMaxLifetime(30000L);
         this.dataSource.setLeakDetectionThreshold(30000L);
      } catch (Error | Exception e) {
         e.printStackTrace();
      }

      Log.info("Loaded database connector");
   }

   @Override
   public void unload() {
      this.dataSource.close();
   }

   public DSLContext open() {
      try {
         return DSL.using(this.dataSource, this.provider.getDialect());
      } catch (Error | Exception e) {
         e.printStackTrace();
         return null;
      }
   }
}
