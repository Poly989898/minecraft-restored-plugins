package fr.phoenixdevt.mmoprofiles.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import fr.phoenixdevt.mmoprofiles.shared.player.ProxiedPlayerData;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerChoseProfileRequest;
import fr.phoenixdevt.mmoprofiles.shared.request.PlayerUnselectProfileRequest;
import fr.phoenixdevt.mmoprofiles.shared.request.Request;
import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

public class RequestHandler {
   private final MMOProfiles plugin;
   private final ChannelIdentifier messageChannel = new LegacyChannelIdentifier("mmoprofiles:main");

   public RequestHandler(MMOProfiles plugin) {
      this.plugin = plugin;
      plugin.getProxy().getChannelRegistrar().register(new ChannelIdentifier[]{this.messageChannel});
   }

   @Subscribe
   public void onMessage(PluginMessageEvent event) {
      if (event.getIdentifier().getId().equals(this.messageChannel.getId())) {
         this.handleRequest(Request.fromByteArray(event.getData()));
      }
   }

   public void handleRequest(Request request) {
      switch (request.getType()) {
         case PLAYER_CHOSE_PROFILE:
            this.playerChoseProfile((PlayerChoseProfileRequest)request);
            return;
         case PLAYER_UNSELECT_PROFILE:
            this.playerUnselectProfile((PlayerUnselectProfileRequest)request);
            return;
         case PROFILE_SELECTION_FALLBACK:
         case PLAYER_SWITCH_PROFILE:
         case SERVER_DISPATCH_PLAYER_INFO:
            throw new NotImplementedException();
         default:
            throw new RuntimeException("Request of type " + request.getType().name() + " not handled");
      }
   }

   private boolean playerUnselectProfile(@NotNull PlayerUnselectProfileRequest request) {
      ProxiedPlayerData playerData;
      try {
         playerData = this.plugin.getPlayerDataMap().fromInitialId(request.getPlayerId());
      } catch (Exception exception) {
         return false;
      }

      playerData.setProfileId(null);
      playerData.setCurrentRequest(null);
      return true;
   }

   private boolean playerChoseProfile(@NotNull PlayerChoseProfileRequest request) {
      Optional<Player> playerOpt = this.plugin.getProxy().getPlayer(request.getPlayerId());
      if (!playerOpt.isPresent()) {
         return false;
      }

      Player player = playerOpt.get();
      Optional<RegisteredServer> targetServerOpt = Optional.empty();
      if (!request.getTargetServers().isEmpty()) {
         targetServerOpt = request.getTargetServers()
            .stream()
            .map(id -> this.plugin.getProxy().getServer(id))
            .filter(opt -> opt.isPresent())
            .map(opt -> (RegisteredServer)opt.get())
            .findAny();
         if (!targetServerOpt.isPresent()) {
            return false;
         }
      }

      if (!player.getCurrentServer().isPresent()) {
         return false;
      }

      String currentServerName = ((ServerConnection)player.getCurrentServer().get()).getServer().getServerInfo().getName();
      request.setOriginalServer(currentServerName);
      ProxiedPlayerData playerData = this.plugin.getPlayerDataMap().fromInitialId(request.getPlayerId());
      playerData.setProfileId(request.getProfileId());
      playerData.setCurrentRequest(request);
      if (targetServerOpt.isPresent()) {
         player.createConnectionRequest(targetServerOpt.get()).fireAndForget();
      }

      return true;
   }
}
