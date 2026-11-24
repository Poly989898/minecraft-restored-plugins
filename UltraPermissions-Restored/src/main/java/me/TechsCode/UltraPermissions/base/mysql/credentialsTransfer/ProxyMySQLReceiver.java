package me.TechsCode.UltraPermissions.base.mysql.credentialsTransfer;

import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.BungeeMessagingListener;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import me.TechsCode.UltraPermissions.base.mysql.MySQLManager;
import me.TechsCode.UltraPermissions.base.mysql.MySQLRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.md_5.bungee.api.config.ServerInfo;

public class ProxyMySQLReceiver implements BungeeMessagingListener {
   private BungeeTechPlugin plugin;
   private MySQLManager manager;

   public ProxyMySQLReceiver(BungeeTechPlugin bungeeTechPlugin, MySQLManager mySQLManager) {
      this.plugin = bungeeTechPlugin;
      this.manager = mySQLManager;
      bungeeTechPlugin.getMessagingService().register(this);
   }

   @Override
   public void onMessage(ServerInfo serverInfo, Message message) {
      if (message.getKey().equals("mysql")) {
         boolean asBoolean = message.getData().get("force").getAsBoolean();
         MySQLRegistry registry = this.manager.getRegistry();
         if (asBoolean || !registry.hasCredentials()) {
            try {
               JsonObject asJsonObject = message.getData().getAsJsonObject("state");
               registry.updateState(asJsonObject);
               if (!asBoolean) {
                  this.manager.setup();
               }
            } catch (JsonParseException exception) {
               this.plugin.log("Can not process received MySQL Credentials.. is everything up-to-date?");
               this.plugin.log("Error Message: " + exception.getMessage());
               this.plugin.log("Content (May Contain Sensitive Information): " + message);
            }
         }
      }
   }
}
