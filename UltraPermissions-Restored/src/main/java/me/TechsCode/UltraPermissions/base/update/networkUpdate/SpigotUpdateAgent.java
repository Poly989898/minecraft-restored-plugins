package me.TechsCode.UltraPermissions.base.update.networkUpdate;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import me.TechsCode.UltraPermissions.base.messaging.QueuedMessage;
import me.TechsCode.UltraPermissions.base.messaging.SpigotMessagingListener;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.update.UpdateRequest;
import me.TechsCode.UltraPermissions.base.update.UpdateServer;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotUpdateAgent implements SpigotMessagingListener {
   private static final Phrase MESSAGE = Phrase.create("update.remote.notify", "Updated from **%from%** to **%to%**", Colors.GRAY, Colors.RED, Colors.GREEN);
   private static final Phrase SERVER_RESTARTING = Phrase.create(
      "update.remote.restart", "Server is **now** restarting to update **%pluginName%**", Colors.GRAY, Colors.GOLD, Colors.WHITE
   );
   private SpigotTechPlugin plugin;

   public SpigotUpdateAgent(SpigotTechPlugin spigotTechPlugin) {
      this.plugin = spigotTechPlugin;
      spigotTechPlugin.getMessagingService().register(this);
   }

   @Override
   public void onMessage(Message message) {
      if (message.getKey().equals("restart")) {
         for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("*")) {
               onlinePlayer.sendMessage(SERVER_RESTARTING.get().replace("%pluginName%", this.plugin.getName()));
            }
         }

         Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
      }

      if (message.getKey().equals("update")) {
         JsonObject data = message.getData();
         String asString = data.get("update_token").getAsString();
         String version = data.get("version").getAsString();
         if (!this.plugin.getVersion().equals(version)) {
            UpdateServer updateServer = new UpdateServer();
            updateServer.downloadJar(this.plugin, asString);
            updateServer.destroy();
         }
      }
   }

   public void sendUpdateRequestToBungeeCord(UpdateRequest updateRequest, String text) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("update_token", updateRequest.getUpdateToken());
      jsonObject.addProperty("version", text);
      this.plugin.getMessagingService().send(new QueuedMessage("update", jsonObject) {
         @Override
         public void onSend() {
         }
      });
   }

   public void sendRestartRequestToBungee() {
      this.plugin.getMessagingService().send(new QueuedMessage("restart", null) {
         @Override
         public void onSend() {
         }
      });
   }
}
