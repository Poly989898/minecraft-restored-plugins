package litebans;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.jetbrains.annotations.NotNull;

public final class BukkitLoginListener implements BukkitListener {
   private final Platform platform;
   private final BroadcastModule broadcastModule;

   public BukkitLoginListener(@NotNull Platform platform) {
      this.platform = platform;
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
   }

   public final Platform getPlatform6() {
      return this.platform;
   }

   public final BroadcastModule getBroadcastModule() {
      return this.broadcastModule;
   }

   @EventHandler(priority = EventPriority.LOW)
   public final void handlePlayerLoginEvent(@NotNull PlayerLoginEvent playerLoginEvent) {
      LockdownHandler lockdownHandler = this.broadcastModule.getLockdownHandler();
      if (playerLoginEvent.getResult() == Result.ALLOWED && lockdownHandler != null) {
         LiteSender wrapSender2 = lockdownHandler.getPlatform().wrapSender(playerLoginEvent.getPlayer());
         Intrinsics.checkNotNull(wrapSender2);
         String text = "litebans.lockdown.bypass";
         if (!wrapSender2.hasPermission(text)) {
            CommandTask commandTask = lockdownHandler;
            String formatKickMessage2 = lockdownHandler.formatKickMessage(((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).getServerName(), false);
            PlayerKicker.disallowLoginWithDefaults(lockdownHandler.getPlatform().createBridge(), playerLoginEvent, formatKickMessage2, null, false, "KICK_OTHER", 12, null);
         }
      }
   }

   public BukkitLoginListener obtainLiteBukkitListenerImpl3() {
      Platform platform = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteBukkitListenerImpl3();
   }
}
