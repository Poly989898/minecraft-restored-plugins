package litebans;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

public final class PoolBungeeListenerImpl implements BungeeListener {
   private final Platform platform;
   private final PunishmentLookupModule punishmentLookupModule2;

   public PoolBungeeListenerImpl(@NotNull Platform platform2) {
      this.platform = platform2;
      this.punishmentLookupModule2 = (PunishmentLookupModule)this.platform.getModule(PunishmentLookupModule.class);
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   @EventHandler(priority = -32)
   public final void handleChatEvent(@NotNull ChatEvent chatEvent) {
      if (!chatEvent.isCancelled()) {
         Connection sender = chatEvent.getSender();
         ProxiedPlayer proxiedPlayer = sender instanceof ProxiedPlayer ? (ProxiedPlayer)sender : null;
         if ((sender instanceof ProxiedPlayer ? (ProxiedPlayer)sender : null) != null) {
            String message = chatEvent.getMessage();
            MuteChatTask muteChatTask = this.punishmentLookupModule2.getMuteChatTask();
            Intrinsics.checkNotNull(message);
            MuteChatTask.handleMuteChatTask(muteChatTask, chatEvent, message, this.platform.wrapSender(proxiedPlayer), false, 8, null);
         }
      }
   }

   public PoolBungeeListenerImpl obtainPoolBungeeListenerImpl() {
      Platform platform2 = this.platform;
      PlatformListener[] platformListeners = new PlatformListener[]{this};
      platform2.registerListeners(platformListeners);
      return this;
   }

   @Override
   public PlatformListener obtainPlatformListener() {
      return this.obtainPoolBungeeListenerImpl();
   }
}
