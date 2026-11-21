package litebans;

import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class VelocityConnectListener implements VelocityListener {
   private final Platform platform;
   private final EntryQueueModule entryQueueModule;
   private final ConfigModule configModule;
   private final BroadcastModule broadcastModule;
   private final DatabaseModule databaseModule;

   public VelocityConnectListener(@NotNull Platform platform2) {
      this.platform = platform2;
      this.entryQueueModule = (EntryQueueModule)this.platform.getModule(EntryQueueModule.class);
      this.configModule = (ConfigModule)this.platform.getModule(ConfigModule.class);
      this.broadcastModule = (BroadcastModule)this.platform.getModule(BroadcastModule.class);
      this.databaseModule = (DatabaseModule)this.platform.getModule(DatabaseModule.class);
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

   public final DatabaseModule getDatabaseModule() {
      return this.databaseModule;
   }

   @Subscribe
   public final EventTask computeEventTask(@NotNull ServerPreConnectEvent serverPreConnectEvent) {
      return !serverPreConnectEvent.getResult().isAllowed() ? null : EventTask.async(() -> {
         this.broadcastModule.getLoginHandler().handleObj(serverPreConnectEvent);
      });
   }

   @Subscribe
   public final void handleServerConnectedEvent(@NotNull ServerConnectedEvent serverConnectedEvent) {
      LiteSender wrapSender2 = this.platform.wrapSender(serverConnectedEvent.getPlayer());
      List ignoredIphistoryServers = this.configModule.getSettings().getIgnoredIphistoryServers();
      RegisteredServer server = serverConnectedEvent.getServer();
      String name = server.getServerInfo().getName();
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

   public VelocityConnectListener obtainLiteVelocityListenerImpl3() {
      Platform platform2 = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform2.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainLiteVelocityListenerImpl3();
   }
}
