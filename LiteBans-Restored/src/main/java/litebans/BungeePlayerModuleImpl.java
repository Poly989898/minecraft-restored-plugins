package litebans;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@PlatformMarker(obtainByteValue = 1)
public final class BungeePlayerModuleImpl extends BungeePlayerModule implements BungeeListener {
   public BungeePlayerModuleImpl(@NotNull Platform platform) {
      super(platform);
   }

   @Override
   public void disable() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.unregisterListeners(platformListeners);
   }

   @Override
   public void enable() {
      if (this.platform.getPlatformId() != 1) {
         String text = "Failed requirement.";
         throw new IllegalArgumentException(text.toString());
      }

      this.platform.setPlayerModule(this);
      this.obtainBungeePlayerModuleImpl();
   }

   @EventHandler
   public final void handlePlayerDisconnectEvent(@NotNull PlayerDisconnectEvent playerDisconnectEvent) {
      this.platform.removeSender(playerDisconnectEvent.getPlayer());
      ConfigModule module = (ConfigModule)this.platform.getModule(ConfigModule.class);
      int value = module.getMaxPlayerCount();
      PlatformBridge bridge = this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      int playerCount = ((BungeeBridge)bridge).getPlayerCount();
      module.setMaxPlayerCount(Math.max(value, playerCount));
   }

   public BungeePlayerModuleImpl obtainBungeePlayerModuleImpl() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainBungeePlayerModuleImpl();
   }
}
