package me.TechsCode.UltraPermissions.base.update;

import java.util.concurrent.TimeUnit;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateEvent;
import me.TechsCode.UltraPermissions.base.legacy.task.UpdateTime;
import me.TechsCode.UltraPermissions.base.misc.Callback;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.update.responses.CreateUpdateRequestResponse;
import me.TechsCode.UltraPermissions.base.update.responses.GetUpdateRequestResponse;
import me.TechsCode.UltraPermissions.base.update.view.RestartView;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class UpdateProcess implements Listener {
   private static final Phrase CLICK_TO_AUTHENTICATE = Phrase.create(
      "update.clickToAuthenticate", "Click the link below to authenticate using **Discord**:", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase UNAUTHORIZED_TEXT = Phrase.create(
      "update.unauthorized", "Authorization Failed, You are not verified on our **Discord**", Colors.GRAY, Colors.AQUA
   );
   private static final Phrase UPDATED_PLUGIN = Phrase.create("update.updatedPlugin", "Updated the plugin to **%**, reloading..", Colors.GRAY, Colors.YELLOW);
   private static final Phrase SERVER_OFFLINE = Phrase.create("update.serverOffline", "There was an error communicating with our update Server", Colors.GRAY);
   private static final Phrase DOWNLOAD_FAILED = Phrase.create("update.downloadFailed", "The download has failed, please try again later", Colors.GRAY);
   private final SpigotTechPlugin plugin;
   private final Player p;
   private final UpdateServer updateServer;
   private final CreateUpdateRequestResponse createUpdateRequestResponse;
   private final String version;
   private final long start;
   private final Callback<UpdateProcess.CloseReason> callback;

   public UpdateProcess(SpigotTechPlugin spigotTechPlugin, Player player, String text, Callback<UpdateProcess.CloseReason> callback) {
      this.plugin = spigotTechPlugin;
      this.p = player;
      this.updateServer = new UpdateServer();
      this.version = text;
      this.start = System.currentTimeMillis();
      this.callback = callback;
      this.createUpdateRequestResponse = this.updateServer.createUpdateRequest(spigotTechPlugin);
      String authorizeUrl = this.updateServer.getAuthorizeUrl(this.createUpdateRequestResponse.getUpdateToken());
      player.sendMessage("");
      player.sendMessage(spigotTechPlugin.getPrefix() + CLICK_TO_AUTHENTICATE);
      player.sendMessage(Colors.YELLOW + authorizeUrl);
      player.sendMessage("");
      Bukkit.getPluginManager().registerEvents(this, (Plugin)spigotTechPlugin.getBootstrap());
   }

   @EventHandler
   public void check(UpdateEvent event) {
      if (event.getUpdateTime() == UpdateTime.SLOW) {
         long value = System.currentTimeMillis() - this.start;
         if (TimeUnit.MILLISECONDS.toMinutes(value) >= 15L) {
            this.close(UpdateProcess.CloseReason.TIME_OUT);
         } else {
            GetUpdateRequestResponse updateRequest = this.updateServer.getUpdateRequest(this.createUpdateRequestResponse.getUpdateToken());
            if (updateRequest.getStatusCode() != 200) {
               this.close(UpdateProcess.CloseReason.SERVER_OFFLINE);
            } else {
               UpdateRequest request = updateRequest.getUpdateRequest();
               if (request == null) {
                  this.close(UpdateProcess.CloseReason.SERVER_OFFLINE);
               } else {
                  switch (request.getStatus()) {
                     case WAITING_AUTHENTICATION:
                     default:
                        break;
                     case UNAUTHORIZED:
                        this.p.sendMessage(this.plugin.getPrefix() + UNAUTHORIZED_TEXT);
                        this.close(UpdateProcess.CloseReason.NO_REQUIREMENTS);
                        break;
                     case AUTHORIZED:
                        this.plugin.getUpdateAgent().sendUpdateRequestToBungeeCord(request, this.version);
                        Thread thread = this.updateServer.downloadJar(this.plugin, request.getUpdateToken());
                        Thread watcherThread = new Thread(() -> {
                           while (thread.isAlive()) {
                              try {
                                 Thread.sleep(5000L);
                              } catch (InterruptedException exception) {
                                 throw new RuntimeException(exception);
                              }
                           }

                           if (!this.updateServer.hasJarDownloadFailed()) {
                              new RestartView(this.p, this.plugin) {};
                           } else {
                              this.p.sendMessage(this.plugin.getPrefix() + DOWNLOAD_FAILED.get());
                           }
                        });
                        watcherThread.start();
                        this.close(UpdateProcess.CloseReason.COMPLETE);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void leave(PlayerQuitEvent event) {
      this.close(UpdateProcess.CloseReason.QUIT);
   }

   private void close(UpdateProcess.CloseReason closeReason) {
      HandlerList.unregisterAll(this);
      this.callback.run(closeReason);
   }

   enum CloseReason {
      QUIT,
      COMPLETE,
      NO_REQUIREMENTS,
      TIME_OUT,
      SERVER_OFFLINE;

      CloseReason() {
      }
   }
}
