package fr.phoenixdevt.mmoprofiles.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.phoenixdevt.mmoprofiles.shared.player.ProxiedPlayerData;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerChoseProfileRequest;
import fr.phoenixdevt.mmoprofiles.velocity.MMOProfiles;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.Validate;

public class PlayerListener {
   private final MMOProfiles plugin;

   public PlayerListener(MMOProfiles plugin) {
      this.plugin = plugin;
   }

   @Subscribe(order = PostOrder.LAST)
   public void onLogin(LoginEvent event) {
      if (event.getResult().isAllowed()) {
         this.plugin.getPlayerDataMap().initialize(event.getPlayer().getUniqueId());
      }
   }

   @Subscribe(order = PostOrder.LAST)
   public void onDisconnect(DisconnectEvent event) {
      this.plugin.getPlayerDataMap().unload(event.getPlayer().getUniqueId());
   }

   @Subscribe
   public void handleSwitchServer(ServerPostConnectEvent event) {
      ProxiedPlayerData playerData = this.plugin.getPlayerDataMap().fromInitialId(event.getPlayer().getUniqueId());
      if (playerData.isOnTemporaryServer()) {
         PlayerChoseProfileRequest request = (PlayerChoseProfileRequest)playerData.getCurrentRequest();
         String lastServer = request.getOriginalServer();
         this.plugin.getProxy().getScheduler().buildTask(this.plugin, () -> {
            Optional<RegisteredServer> serverOpt = this.plugin.getProxy().getServer(lastServer);
            Validate.isTrue(serverOpt.isPresent(), "Could not find last server of " + event.getPlayer().getUsername());
            playerData.setCurrentRequest(null);
            event.getPlayer().createConnectionRequest(serverOpt.get()).fireAndForget();
         }).delay(50L * request.getTemporaryServerDelay(), TimeUnit.MILLISECONDS).schedule();
      }
   }
}
