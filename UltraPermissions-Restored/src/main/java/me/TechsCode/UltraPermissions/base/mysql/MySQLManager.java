package me.TechsCode.UltraPermissions.base.mysql;

import java.sql.SQLException;
import java.sql.Connection;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.TechPlugin;
import me.TechsCode.UltraPermissions.base.debug.Debugger;
import me.TechsCode.UltraPermissions.base.mysql.credentialsTransfer.ProxyMySQLReceiver;
import me.TechsCode.UltraPermissions.base.mysql.credentialsTransfer.SpigotMySQLSender;
import me.TechsCode.UltraPermissions.base.registry.RegistrationChoice;

public class MySQLManager {
   private final TechPlugin<?> plugin;
   private final MySQLRegistry registry;
   private ConnectionFactory factory;

   public MySQLManager(TechPlugin<?> plugin) {
      this.plugin = plugin;
      this.registry = plugin.getRegistry().register(MySQLRegistry.class, RegistrationChoice.LOCAL);
      if (plugin instanceof BungeeTechPlugin) {
         Debugger.debug("MySQL", "Registering ProxyMySQLReceiver");
         new ProxyMySQLReceiver((BungeeTechPlugin)plugin, this);
      } else if (plugin instanceof SpigotTechPlugin) {
         Debugger.debug("MySQL", "Registering SpigotMySQLSender");
         new SpigotMySQLSender((SpigotTechPlugin)plugin, this);
      }

      this.setup();
   }

   public void setup() {
      this.close();
      if (this.registry.hasCredentials()) {
         this.factory = new ConnectionFactory(
            this.registry.getCredentials(), this.registry.hasSSL(), this.registry.getMinimumIdle(), this.registry.getMaximumPoolSize()
         );
         ConnectionTestResult connectionTestResult = this.factory.testConnection();
         if (!connectionTestResult.isValid()) {
            this.factory = null;
            this.plugin.log("Could not contact MySQL Server:");
            this.plugin.log(connectionTestResult.getError());
         }

         Debugger.debug("MySQL", "ConnectionFactory created");
      } else {
         this.factory = null;
      }
   }

   public MySQLRegistry getRegistry() {
      return this.registry;
   }

   public boolean isEnabled() {
      return this.factory != null;
   }

   public Connection getConnection() throws SQLException {
      return this.factory != null ? this.factory.newConnection() : null;
   }

   public Boolean connectionAvailable() {
      if (this.factory == null) {
         return false;
      }

      int maximumPoolSize = this.factory.getMaximumPoolSize();
      int activeConnections = this.factory.getActiveConnections();
      Debugger.debug("MySQL", "Active Connections: " + activeConnections + " | Max Connections: " + maximumPoolSize);
      return activeConnections < maximumPoolSize;
   }

   public void close() {
      if (this.factory != null) {
         this.factory.close();
         this.factory = null;
      }
   }
}
