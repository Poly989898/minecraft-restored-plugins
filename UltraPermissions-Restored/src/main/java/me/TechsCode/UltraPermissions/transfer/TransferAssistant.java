package me.TechsCode.UltraPermissions.transfer;

import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;

public class TransferAssistant implements Listener {
   private static final Phrase KICK_MESSAGE = Phrase.create(
      "transferAssistant.kickMessage", "UltraPermissions will now convert data. The server will shut down when complete", Colors.GRAY
   );
   private static final Phrase LOGIN_MESSAGE = Phrase.create(
      "transferAssistant.loginMessage", "Ultra Permissions is currently transferring. Wait until the server is stopped.", Colors.GRAY
   );

   public TransferAssistant(UltraPermissions ultraPermissions, boolean toGlobal) {
      Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(KICK_MESSAGE.get()));
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
      Bukkit.getScheduler().runTaskAsynchronously((Plugin)ultraPermissions.getBootstrap(), () -> {
         ultraPermissions.log("Transferring from " + (toGlobal ? "File to MySQL" : "MySQL to File"));
         TransferStorageGroup fromLocal = new TransferStorageGroup(ultraPermissions, false);
         TransferStorageGroup fromGlobal = new TransferStorageGroup(ultraPermissions, true);
         TransferStorageGroup source = toGlobal ? fromLocal : fromGlobal;
         TransferStorageGroup target = toGlobal ? fromGlobal : fromLocal;
         source.copyTo(target);
         ultraPermissions.getScheduler().runTaskTimer(() -> {
            long sinceLastWrite = System.currentTimeMillis() - target.getLastWrite();
            if (sinceLastWrite > 3000L) {
               Bukkit.shutdown();
            }
         }, 0L, 1200L);
      });
   }

   @EventHandler
   public void login(PlayerLoginEvent event) {
      event.disallow(Result.KICK_OTHER, LOGIN_MESSAGE.get());
   }
}
