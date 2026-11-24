package io.lumine.mythic.lib.data.sql;

import io.lumine.mythic.lib.MythicLib;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.lumine.mythic.lib.util.Tasks;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SQLDataSource {
   private final JavaPlugin plugin;
   private final HikariDataSource dataSource;
   private static final String DEFAULT_HOST = "localhost";
   private static final String DEFAULT_USERNAME = "root";
   private static final String DEFAULT_PASSWORD = "";
   private static final String DEFAULT_DATABASE = "minecraft";
   private static final int DEFAULT_PORT = 3306;

   public SQLDataSource(@NotNull JavaPlugin plugin) {
      this.plugin = plugin;
      ConfigurationSection config = plugin.getConfig().getConfigurationSection("mysql");
      HikariConfig hikari = new HikariConfig();
      hikari.setPoolName("hikari-" + plugin.getName());
      hikari.setJdbcUrl(
         "jdbc:mysql://" + config.getString("host", "localhost") + ":" + config.getInt("port", 3306) + "/" + config.getString("database", "minecraft")
      );
      hikari.setUsername(config.getString("user", "root"));
      hikari.setPassword(config.getString("pass", ""));
      hikari.setMaximumPoolSize(config.getInt("maxPoolSize", 10));
      hikari.setMaxLifetime(config.getLong("maxLifeTime", 300000L));
      hikari.setConnectionTimeout(config.getLong("connectionTimeOut", 10000L));
      hikari.setLeakDetectionThreshold(config.getLong("leakDetectionThreshold", 150000L));
      if (config.isConfigurationSection("properties")) {
         for (String s : config.getConfigurationSection("properties").getKeys(false)) {
            hikari.addDataSourceProperty(s, config.getString("properties." + s));
         }
      }

      this.dataSource = new HikariDataSource(hikari);
   }

   @NotNull
   public JavaPlugin getPlugin() {
      return this.plugin;
   }

   public void getResult(String sql, Consumer<ResultSet> supplier) {
      this.execute(connection -> {
         try {
            PreparedStatement statement = connection.prepareStatement(sql);

            try {
               supplier.accept(statement.executeQuery());
            } catch (Throwable throwable) {
               throwable.printStackTrace();
            }

            statement.close();
         } catch (SQLException exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL result statement:");
            exception.printStackTrace();
         }
      });
   }

   public CompletableFuture<Void> getResultAsync(String sql, Consumer<ResultSet> supplier) {
      return Tasks.runAsync(this.plugin, () -> this.getResult(sql, supplier));
   }

   public void executeUpdate(String sql) {
      this.execute(connection -> {
         try {
            PreparedStatement statement = connection.prepareStatement(sql);

            try {
               statement.executeUpdate();
            } catch (Throwable throwable) {
               throwable.printStackTrace();
            }

            statement.close();
         } catch (SQLException exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL statement:");
            exception.printStackTrace();
         }
      });
   }

   public CompletableFuture<Void> executeUpdateAsync(String sql) {
      return Tasks.runAsync(this.plugin, () -> this.executeUpdate(sql));
   }

   public void execute(Consumer<Connection> execute) {
      try {
         Connection connection = this.dataSource.getConnection();

         try {
            execute.accept(connection);
         } catch (Throwable throwable) {
            throwable.printStackTrace();
         } finally {
            connection.close();
         }
      } catch (SQLException exception) {
         MythicLib.plugin.getLogger().log(Level.WARNING, "Could not open SQL connection:");
         exception.printStackTrace();
      }
   }

   public CompletableFuture<Void> executeAsync(Consumer<Connection> execute) {
      return Tasks.runAsync(this.plugin, () -> this.execute(execute));
   }

   public Connection getConnection() throws SQLException {
      return this.dataSource.getConnection();
   }

   public void close() {
      if (this.dataSource != null) {
         this.dataSource.close();
      }
   }
}
