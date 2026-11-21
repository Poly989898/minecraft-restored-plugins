package litebans;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public final class BungeeLockdownLoginListener implements BungeeListener {
   private final Platform platform8;
   private final BroadcastModule broadcastModule7;

   public BungeeLockdownLoginListener(@NotNull Platform platform) {
      this.platform8 = platform;
      this.broadcastModule7 = (BroadcastModule)this.platform8.getModule(BroadcastModule.class);
   }

   @EventHandler(priority = -64)
   public final void handleServerConnectEvent2(@NotNull ServerConnectEvent serverConnectEvent) {
      boolean flag = false;
      Object obj = null;
      if (!serverConnectEvent.isCancelled()) {
         LiteSender wrapSender2 = this.platform8.wrapSender(serverConnectEvent.getPlayer());
         LockdownHandler lockdownHandler = this.broadcastModule7.getLockdownHandler();
         if (lockdownHandler != null) {
            PlatformBridge bridge = this.platform8.createBridge();
            Intrinsics.checkNotNullExpressionValue(bridge, "");
            ProxyServer proxy = ((BungeeBridge)bridge).asBungeePlugin(this.platform8).getProxy();
            Intrinsics.checkNotNull(wrapSender2);
            String text = "litebans.lockdown.bypass";
            if (!wrapSender2.hasPermission(text)) {
               String text2;
               label54: {
                  text2 = serverConnectEvent.getTarget().getName();
                  if (!ServerScopes.SERVER_SCOPE.isGlobal(lockdownHandler.getReason())) {
                     CommandTask commandTask = lockdownHandler;
                     String serverName = ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).getServerName();
                     if (!StringsKt.checkText(lockdownHandler.getReason(), serverName, true)) {
                        flag = false;
                        break label54;
                     }
                  }

                  flag = true;
               }

               if (flag) {
                  Intrinsics.checkNotNull(text2);
                  LockdownHandler.handleLockdownHandler(lockdownHandler, serverConnectEvent, lockdownHandler.formatKickMessage(text2, true), false, 4, null);
               }

               Intrinsics.checkNotNull(text2);
               String text3 = text2;
               if (StringsKt.checkText(lockdownHandler.getReason(), text3, true)) {
                  String formatKickMessage2 = lockdownHandler.formatKickMessage(text2, false);
                  if (wrapSender2.getServerName() == null) {
                     Iterable serverPriority = serverConnectEvent.getPlayer().getPendingConnection().getListener().getServerPriority();

                     label45: {
                        for (Object obj2 : serverPriority) {
                           String string = (String)obj2;
                           Intrinsics.checkNotNull(string);
                           if (!StringsKt.checkText(lockdownHandler.getReason(), string, true)) {
                              obj = obj2;
                              break label45;
                           }
                        }

                        obj = null;
                     }

                     String string2 = (String)obj;
                     if (string2 == null) {
                        LockdownHandler.handleLockdownHandler(lockdownHandler, serverConnectEvent, formatKickMessage2, false, 4, null);
                     } else {
                        serverConnectEvent.setTarget(proxy.getServerInfo(string2));
                        CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, wrapSender2, formatKickMessage2, null, 4, null);
                     }
                  } else {
                     LockdownHandler.handleLockdownHandler(lockdownHandler, serverConnectEvent, null, false, 2, null);
                     CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, wrapSender2, formatKickMessage2, null, 4, null);
                  }
               }
            }
         }
      }
   }

   public BungeeLockdownLoginListener obtainBungeeLockdownLoginListener() {
      Platform platform = this.platform8;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainBungeeLockdownLoginListener();
   }
}
