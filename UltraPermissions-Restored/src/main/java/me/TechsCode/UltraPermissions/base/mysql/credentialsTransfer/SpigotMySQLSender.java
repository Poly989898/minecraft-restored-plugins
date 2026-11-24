package me.TechsCode.UltraPermissions.base.mysql.credentialsTransfer;

import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.base.messaging.QueuedMessage;
import me.TechsCode.UltraPermissions.base.mysql.MySQLManager;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class SpigotMySQLSender implements Listener {
   private SpigotTechPlugin plugin;
   private MySQLManager manager;

   public SpigotMySQLSender(SpigotTechPlugin spigotTechPlugin, MySQLManager mySQLManager) {
      this.plugin = spigotTechPlugin;
      this.manager = mySQLManager;
      this.sendMySQLCredentials(false);
      Bukkit.getPluginManager().registerEvents(this, (Plugin)spigotTechPlugin.getBootstrap());
   }

   private void sendMySQLCredentials(boolean flag) {
      if (this.manager.isEnabled()) {
         JsonObject jsonObject = new JsonObject();
         jsonObject.addProperty("force", flag);
         jsonObject.add("state", this.manager.getRegistry().getState());
         this.plugin.getMessagingService().send(new QueuedMessage("mysql", jsonObject) {
            @Override
            public void onSend() {
            }
         });
      }
   }

   @EventHandler
   public void send(UpdateEvent event) {
      if (event.getUpdateTime() == UpdateTime.HALFMIN) {
         this.sendMySQLCredentials(false);
      }
   }

   @EventHandler
   public void join(PlayerJoinEvent event) {
      this.sendMySQLCredentials(false);
   }

   public void forceUpdateBungeeMySQLCredentials() {
      this.sendMySQLCredentials(true);
   }
}
