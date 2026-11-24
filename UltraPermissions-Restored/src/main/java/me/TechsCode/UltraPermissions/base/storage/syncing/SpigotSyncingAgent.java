package me.TechsCode.UltraPermissions.base.storage.syncing;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import me.TechsCode.UltraPermissions.base.messaging.QueuedMessage;
import me.TechsCode.UltraPermissions.base.messaging.SpigotMessagingListener;
import me.TechsCode.UltraPermissions.base.storage.Storage;
import com.google.gson.JsonObject;

public class SpigotSyncingAgent extends SyncingAgent implements SpigotMessagingListener {
   private SpigotTechPlugin plugin;

   public SpigotSyncingAgent(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
      spigotTechPlugin.getMessagingService().register(this);
   }

   @Override
   public void onMessage(Message message) {
      if (message.getKey().equals("cache")) {
         String asString = message.getData().get("model").getAsString();
         String dataKey = message.getData().get("dataKey").getAsString();
         this.plugin.getScheduler().runAsync(() -> this.executeLocalSynchronization(asString, dataKey));
      }
   }

   @Override
   public void notifyNewDataChanges(Storage storage, String dataKey) {
      JsonObject data = new JsonObject();
      data.addProperty("model", storage.getModelName());
      data.addProperty("dataKey", dataKey);
      this.plugin.getMessagingService().send(new QueuedMessage("cache", data) {
         @Override
         public void onSend() {
         }
      });
   }
}
