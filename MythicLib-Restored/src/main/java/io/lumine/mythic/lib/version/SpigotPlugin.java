package io.lumine.mythic.lib.version;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin {
   private final JavaPlugin plugin;
   private final int id;
   private String version;

   public SpigotPlugin(int id, JavaPlugin plugin) {
      this.plugin = plugin;
      this.id = id;
   }

   public void checkForUpdate() {
      Bukkit.getScheduler()
         .runTaskAsynchronously(
            this.plugin,
            () -> {
               try {
                  HttpsURLConnection connection = (HttpsURLConnection)new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.id)
                     .openConnection();
                  connection.setRequestMethod("GET");
                  this.version = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
               } catch (Throwable throwable) {
                  this.plugin.getLogger().log(Level.INFO, "Could not check latest plugin version: " + throwable.getMessage());
                  return;
               }

               if (this.isOutdated(this.version, this.plugin.getDescription().getVersion())) {
                  this.plugin
                     .getLogger()
                     .log(Level.INFO, "A new build is available: " + this.version + " (you are running " + this.plugin.getDescription().getVersion() + ")");
                  this.plugin.getLogger().log(Level.INFO, "Download it here: " + this.getResourceUrl());
                  if (this.plugin.getConfig().getBoolean("update-notify")) {
                     Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler(priority = EventPriority.MONITOR)
                        public void onPlayerJoin(PlayerJoinEvent event) {
                           Player player = event.getPlayer();
                           if (player.hasPermission(SpigotPlugin.this.plugin.getName().toLowerCase() + ".update-notify")) {
                              SpigotPlugin.this.getOutOfDateMessage().forEach(msg -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg)));
                           }
                        }
                     }, this.plugin));
                  }
               }
            }
         );
   }

   private boolean isOutdated(String v1, String v2) {
      if (v1.equals(v2)) {
         return false;
      }

      String[] netVersion = v1.replaceAll("[^0-9.]", "").split("\\.");
      String[] localVersion = v2.replaceAll("[^0-9.]", "").split("\\.");

      for (int i = 0; i < Math.max(netVersion.length, localVersion.length); i++) {
         if ((i >= netVersion.length ? 0 : Integer.parseInt(netVersion[i])) > (i >= localVersion.length ? 0 : Integer.parseInt(localVersion[i]))) {
            return true;
         }
      }

      return false;
   }

   private List<String> getOutOfDateMessage() {
      return Arrays.asList(
         "&8--------------------------------------------",
         "&a" + this.plugin.getName() + " " + this.version + " is available!",
         "&a" + this.getResourceUrl(),
         "&7&oYou can disable this notification in the config file.",
         "&8--------------------------------------------"
      );
   }

   private String getResourceUrl() {
      return "https://www.spigotmc.org/resources/" + this.id + "/";
   }
}
