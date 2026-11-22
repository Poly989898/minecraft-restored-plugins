package litebans;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

public final class AuthMeLoginListener implements BukkitListener {
   private final Platform platform;

   public AuthMeLoginListener(@NotNull Platform platform) {
      this.platform = platform;
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public final void handleLoginEvent(@NotNull LoginEvent loginEvent) {
      DatabaseModule module = (DatabaseModule)this.platform.getModule(DatabaseModule.class);

      try {
         Player player = loginEvent.getPlayer();
         if (player == null) {
            return;
         }

         LiteSender wrapSender2 = this.platform.wrapSender(player);
         String name = wrapSender2.getName();
         String uuidString = wrapSender2.getUuidString();
         String ipAddress = wrapSender2.getIpAddress();
         new QueueCommandTask(this.platform, name, uuidString, ipAddress).run();
      } catch (Throwable e) {
         this.platform.getLogger().severe("Handling AuthMe login event failed. Further events will not be handled.");
         ConfigModule module2 = (ConfigModule)this.platform.getModule(ConfigModule.class);
         module.logConnectionError(e);
         Platform platform = this.platform;
         PlatformListener[] platformListeners = new PlatformListener[]{this};
         platform.unregisterListeners(platformListeners);
         module2.getStateFlags().setFlag3(true);
      }
   }

   public AuthMeLoginListener obtainLiteBukkitListenerImpl() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteBukkitListenerImpl();
   }
}
