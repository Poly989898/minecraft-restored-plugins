package me.TechsCode.UltraPermissions.base.update.networkUpdate;

import me.TechsCode.UltraPermissions.base.BungeeTechPlugin;
import me.TechsCode.UltraPermissions.base.messaging.BungeeMessagingListener;
import me.TechsCode.UltraPermissions.base.messaging.Message;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.update.UpdateServer;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.config.ServerInfo;

public class BungeeUpdateAgent implements BungeeMessagingListener {
   private BungeeTechPlugin plugin;
   private static final Phrase RESTART_MANUALLY = Phrase.create(
      "BungeeUpdateAgent.restartManually", "A manually restart of your proxy is required to update **%pluginName%**", Colors.GRAY, Colors.WHITE
   );

   public BungeeUpdateAgent(BungeeTechPlugin bungeeTechPlugin) {
      this.plugin = bungeeTechPlugin;
      bungeeTechPlugin.getMessagingService().register(this);
   }

   @Override
   public void onMessage(ServerInfo serverInfo, Message message) {
      if (message.getKey().equals("restart")) {
         this.plugin.log(RESTART_MANUALLY.get().replace("%pluginName%", this.plugin.getName()));
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
}
