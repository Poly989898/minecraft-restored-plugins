package fr.phoenixdevt.mmoprofiles.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import fr.phoenixdevt.mmoprofiles.shared.player.PlayerDataMap;
import fr.phoenixdevt.mmoprofiles.velocity.listener.GameProfileInjector;
import fr.phoenixdevt.mmoprofiles.velocity.listener.PlayerListener;
import java.util.logging.Logger;

@Plugin(
   id = "mmoprofiles_velocity",
   name = "MMOProfiles-Velocity",
   version = "1.0-SNAPSHOT",
   url = "https://www.spigotmc.org/resources/mmoprofiles.109942/",
   description = "MMOProfiles extension for Velocity.",
   authors = "PhoenixDevt"
)
public class MMOProfiles {
   private final ProxyServer proxy;
   private final Logger logger;
   private final RequestHandler requestHandler;
   private final PlayerDataMap playerDataMap;

   @Inject
   public MMOProfiles(ProxyServer proxy, Logger logger) {
      this.proxy = proxy;
      this.logger = logger;
      this.requestHandler = new RequestHandler(this);
      this.playerDataMap = new PlayerDataMap();
   }

   public ProxyServer getProxy() {
      return this.proxy;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public RequestHandler getRequestHandler() {
      return this.requestHandler;
   }

   public PlayerDataMap getPlayerDataMap() {
      return this.playerDataMap;
   }

   @Subscribe
   public void onProxyInitialization(ProxyInitializeEvent event) {
      this.proxy.getEventManager().register(this, this.requestHandler);
      this.proxy.getEventManager().register(this, new PlayerListener(this));
      this.proxy.getEventManager().register(this, new GameProfileInjector(this));
   }
}
