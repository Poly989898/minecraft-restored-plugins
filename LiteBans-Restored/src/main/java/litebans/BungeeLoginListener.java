package litebans;

import java.util.List;
import java.util.Locale;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public final class BungeeLoginListener implements BungeeListener {
   private final Platform platform;
   private final EntryQueueModule entryQueueModule;
   private final ConfigModule configModule;
   private final BroadcastModule broadcastModule;

   public BungeeLoginListener(@NotNull Platform platform2) {
      this.platform = platform2;
      this.entryQueueModule = (EntryQueueModule)this.platform.getModule(EntryQueueModule.class);
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
      this.configModule.enable();
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final EntryQueueModule getEntryQueueModule() {
      return this.entryQueueModule;
   }

   public final ConfigModule getConfigModule() {
      return this.configModule;
   }

   public final BroadcastModule getBroadcastModule() {
      return this.broadcastModule;
   }

   @EventHandler
   public final void handleLoginEvent(@NotNull LoginEvent loginEvent) {
      if (!loginEvent.isCancelled()) {
         Platform platform2 = this.platform;
         Intrinsics.checkNotNullExpressionValue(this.platform, "");
         loginEvent.registerIntent((Plugin)platform2);
         ((BungeePlugin)this.platform).runAsync(() -> {
            try {
               long value = System.nanoTime();
               LoginHandler loginHandler = this.broadcastModule.getLoginHandler();
               Intrinsics.checkNotNullExpressionValue(loginHandler, "");
               BungeeLoginHandler bungeeLoginHandler = (BungeeLoginHandler)loginHandler;
               bungeeLoginHandler.handleLoginEvent(loginEvent);
               ConfigModule configModule2 = this.configModule;
               byte b = 1;
               if (configModule2.isDebugAtLeast(b)) {
                  configModule2.logDebug("Handled login event: " + NumberFormats.computeText((System.nanoTime() - value) / 1000.0 / 1000.0) + " ms.");
               }
            } finally {
               loginEvent.completeIntent((Plugin)this.platform);
            }
         });
      }
   }

   @EventHandler
   public final void handleServerConnectEvent(@NotNull ServerConnectEvent serverConnectEvent) {
      if (!serverConnectEvent.isCancelled()) {
         this.broadcastModule.getLoginHandler().handleObj(serverConnectEvent);
      }
   }

   @EventHandler
   public final void handleServerConnectedEvent(@NotNull ServerConnectedEvent serverConnectedEvent) {
      LiteSender wrapSender2 = this.platform.wrapSender(serverConnectedEvent.getPlayer());
      List ignoredIphistoryServers = this.configModule.getSettings().getIgnoredIphistoryServers();
      Server server = serverConnectedEvent.getServer();
      String name = server.getInfo().getName();
      String lowerCase = name.toLowerCase(Locale.ENGLISH);
      if (!ignoredIphistoryServers.contains(lowerCase)) {
         new QueueCommandTask(this.platform, wrapSender2.getName(), wrapSender2.getUuidString(), wrapSender2.getIpAddress()).run();
         Intrinsics.checkNotNull(wrapSender2);
         String text = "litebans.exempt.dupeip_join";
         if (!wrapSender2.hasPermission(text)) {
            EntryQueueModule entryQueueModule2 = this.entryQueueModule;
            Intrinsics.checkNotNull(wrapSender2);
            entryQueueModule2.handleText(wrapSender2.getName(), wrapSender2.getUuidString(), wrapSender2.getIpAddress());
         }
      }
   }

   public BungeeLoginListener obtainLiteBungeeListenerImpl() {
      Platform platform2 = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform2.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteBungeeListenerImpl();
   }
}
