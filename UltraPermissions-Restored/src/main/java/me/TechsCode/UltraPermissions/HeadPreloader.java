package me.TechsCode.UltraPermissions;

import java.util.Optional;
import me.TechsCode.UltraPermissions.base.legacy.SkinTexture;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class HeadPreloader implements Listener {
   private UltraPermissions plugin;

   public HeadPreloader(UltraPermissions ultraPermissions) {
      this.plugin = ultraPermissions;
      Bukkit.getPluginManager().registerEvents(this, (Plugin)ultraPermissions.getBootstrap());
   }

   @EventHandler
   public void login(PlayerJoinEvent event) {
      this.plugin.getScheduler().runTaskLaterAsync(() -> {
         SkinTexture skinTexture = Bukkit.getOnlineMode() ? SkinTexture.fromPlayer(event.getPlayer()) : SkinTexture.fromMojangAPI(event.getPlayer().getName());
         if (skinTexture != null && skinTexture.getUrl() != null) {
            Optional<User> optional = this.plugin.getUsers().uuid(event.getPlayer().getUniqueId());
            optional.ifPresent(user -> user.setSkinTexture(skinTexture));
         }
      }, 90L);
   }
}
