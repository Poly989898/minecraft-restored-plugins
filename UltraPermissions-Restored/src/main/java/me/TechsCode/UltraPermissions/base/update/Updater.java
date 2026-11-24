package me.TechsCode.UltraPermissions.base.update;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.misc.Callback;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.update.responses.IsAvailableResponse;
import me.TechsCode.UltraPermissions.base.update.responses.IsUpToDateResponse;
import me.TechsCode.UltraPermissions.base.update.view.UpdateView;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.entity.Player;

public class Updater {
   private static final List<Player> updateAskedPlayers = new ArrayList<>();
   private static final List<Player> restartAskedPlayers = new ArrayList<>();
   private static final Phrase UPDATE_READY = Phrase.create(
      "Updater.updateReady", "**An update is waiting to be loaded!** Restart your server to make this update take effect", Colors.GRAY, Colors.WHITE
   );

   public Updater() {
   }

   public static void suggestUpdateIfAvailable(SpigotTechPlugin spigotTechPlugin, Player player, Callback<Player> callback, boolean force) {
      UpdateServer updateServer = new UpdateServer();
      IsAvailableResponse isAvailable = updateServer.isAvailable();
      if (isAvailable.isAvailable()) {
         updateServer.destroy();
         File pluginFolder = spigotTechPlugin.getPluginFolder();
         boolean hasDownloadedUpdate = new File(pluginFolder, "update/" + spigotTechPlugin.getName() + ".jar").exists();
         if (hasDownloadedUpdate && restartAskedPlayers.contains(player)) {
            restartAskedPlayers.add(player);
            player.sendMessage(spigotTechPlugin.getPrefix() + UPDATE_READY.get());
         } else if (force || !updateAskedPlayers.contains(player)) {
            if (spigotTechPlugin.getAppearanceRegistry().isUpdaterEnabled()) {
               spigotTechPlugin.getScheduler().runAsync(() -> {
                  UpdateServer checkServer = new UpdateServer();
                  IsUpToDateResponse isUpToDate = checkServer.isUpToDate(spigotTechPlugin);
                  checkServer.destroy();
                  if (isUpToDate.isSuccessFull() && !isUpToDate.isUpToDate()) {
                     updateAskedPlayers.add(player);
                     String version = spigotTechPlugin.getVersion();
                     String latestVersion = isUpToDate.getLatestVersion();
                     spigotTechPlugin.getScheduler().run(() -> new UpdateView(player, spigotTechPlugin, version, latestVersion) {
                        @Override
                        public void onResponse(boolean accepted) {
                           if (accepted) {
                              new UpdateProcess(spigotTechPlugin, this.p, latestVersion, closeReason -> {
                                 if (closeReason == UpdateProcess.CloseReason.NO_REQUIREMENTS) {
                                    Updater.updateAskedPlayers.remove(this.p);
                                 }
                              });
                           } else {
                              callback.run(this.p);
                              this.p.closeInventory();
                           }
                        }
                     });
                  }
               });
            }
         }
      }
   }

   public static String getOSSystem() {
      return System.getProperty("os.name");
   }
}
