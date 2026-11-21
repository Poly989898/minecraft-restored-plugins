package litebans;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent.ServerResult;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;

public final class VelocityLockdownConnectListener implements VelocityListener {
   private final Platform platform3;
   private final BroadcastModule broadcastModule6;

   public VelocityLockdownConnectListener(@NotNull Platform platform) {
      this.platform3 = platform;
      this.broadcastModule6 = (BroadcastModule)this.platform3.getModule(BroadcastModule.class);
   }

   public final Platform getPlatform3() {
      return this.platform3;
   }

   @Subscribe(order = PostOrder.FIRST)
   public final void handleServerPreConnectEvent(@NotNull ServerPreConnectEvent serverPreConnectEvent) {
      boolean flag = false;
      Object obj = null;
      if (serverPreConnectEvent.getResult().isAllowed()) {
         LiteSender wrapSender2 = this.platform3.wrapSender(serverPreConnectEvent.getPlayer());
         LockdownHandler lockdownHandler = this.broadcastModule6.getLockdownHandler();
         if (lockdownHandler != null) {
            Platform platform = this.platform3;
            Intrinsics.checkNotNullExpressionValue(this.platform3, "");
            VelocityPlugin velocityPlugin = (VelocityPlugin)platform;
            Intrinsics.checkNotNull(wrapSender2);
            String text = "litebans.lockdown.bypass";
            if (!wrapSender2.hasPermission(text)) {
               String text2;
               label54: {
                  text2 = ((RegisteredServer)serverPreConnectEvent.getResult().getServer().get()).getServerInfo().getName();
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
                  LockdownHandler.handleLockdownHandler(lockdownHandler, serverPreConnectEvent, lockdownHandler.formatKickMessage(text2, true), false, 4, null);
               }

               Intrinsics.checkNotNull(text2);
               String text3 = text2;
               if (StringsKt.checkText(lockdownHandler.getReason(), text3, true)) {
                  String formatKickMessage2 = lockdownHandler.formatKickMessage(text2, false);
                  if (wrapSender2.getServerName() == null) {
                     Iterable attemptConnectionOrder = velocityPlugin.proxyServer.getConfiguration().getAttemptConnectionOrder();

                     label45: {
                        for (Object obj2 : attemptConnectionOrder) {
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
                        LockdownHandler.handleLockdownHandler(lockdownHandler, serverPreConnectEvent, formatKickMessage2, false, 4, null);
                     } else {
                        serverPreConnectEvent.setResult(ServerResult.allowed((RegisteredServer)CollectionsKt.first(velocityPlugin.proxyServer.matchServer(string2))));
                        CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, wrapSender2, formatKickMessage2, null, 4, null);
                     }
                  } else {
                     LockdownHandler.handleLockdownHandler(lockdownHandler, serverPreConnectEvent, null, false, 2, null);
                     CommandOutput.sendWithDefaults(CommandTask.COMMAND_OUTPUT, wrapSender2, formatKickMessage2, null, 4, null);
                  }
               }
            }
         }
      }
   }

   public VelocityLockdownConnectListener obtainLiteVelocityListenerImpl() {
      Platform platform = this.platform3;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteVelocityListenerImpl();
   }
}
