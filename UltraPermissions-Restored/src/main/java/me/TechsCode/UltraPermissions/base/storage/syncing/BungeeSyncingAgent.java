package me.TechsCode.UltraPermissions.base.storage.syncing;

import java.util.ArrayList;
import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.BungeeMessagingListener;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeSyncingAgent extends SyncingAgent implements BungeeMessagingListener {
   private BungeeTechPlugin plugin;

   public BungeeSyncingAgent(BungeeTechPlugin bungeeTechPlugin) {
      this.plugin = bungeeTechPlugin;
      bungeeTechPlugin.getMessagingService().register(this);
   }

   @Override
   public void onMessage(ServerInfo serverInfo, Message message) {
      if (message.getKey().equals("cache")) {
         ArrayList<ServerInfo> serverInfos = new ArrayList<>(ProxyServer.getInstance().getServers().values());
         serverInfos.remove(serverInfo);
         this.plugin.getMessagingService().send(message, serverInfos.toArray(new ServerInfo[0]));
         String asString = message.getData().get("model").getAsString();
         String dataKey = message.getData().get("dataKey").getAsString();
         this.executeLocalSynchronization(asString, dataKey);
      }
   }

   @Override
   public void notifyNewDataChanges(Storage storage, String text) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("model", storage.getModelName());
      jsonObject.addProperty("dataKey", text);
      this.plugin.getMessagingService().send(new Message("cache", jsonObject));
   }
}
