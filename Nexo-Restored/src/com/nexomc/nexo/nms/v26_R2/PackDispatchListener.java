package com.nexomc.nexo.nms.v26_R2;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.v26_R2.packets.PacketHandler;
import com.nexomc.nexo.pack.server.NexoPackServer;
import io.papermc.paper.connection.PaperPlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionReconfigureEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\r\u001a\u00020\u000e*\u00020\u000fH\u0007b\u0002\b\u0010J\u0010\u0010\u0011\u001a\u00020\u000e*\u00020\u0012H\u0007b\u0002\b\u0010J\u0010\u0010\u0013\u001a\u00020\u000e*\u00020\u0014H\u0007b\u0002\b\u0010J\u0010\u0010\u0015\u001a\u00020\u000e*\u00020\u0016H\u0007b\u0002\b\u0010R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R \u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/PackDispatchListener;", "Lorg/bukkit/event/Listener;", "packetHandler", "Lcom/nexomc/nexo/nms/v26_R2/packets/PacketHandler;", "<init>", "(Lcom/nexomc/nexo/nms/v26_R2/packets/PacketHandler;)V", "getPacketHandler", "()Lcom/nexomc/nexo/nms/v26_R2/packets/PacketHandler;", "futures", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "onInitialize", "", "Lio/papermc/paper/event/connection/configuration/PlayerConnectionInitialConfigureEvent;", "Lorg/bukkit/event/EventHandler;", "onInitialConfig", "Lio/papermc/paper/event/connection/configuration/AsyncPlayerConnectionConfigureEvent;", "onReconfig", "Lio/papermc/paper/event/connection/configuration/PlayerConnectionReconfigureEvent;", "onClose", "Lcom/destroystokyo/paper/event/player/PlayerConnectionCloseEvent;", "com.nexomc:v26_R2"}
)
public final class PackDispatchListener implements Listener {
   @NotNull
   private final PacketHandler packetHandler;
   @NotNull
   private final ConcurrentHashMap<UUID, CompletableFuture<Void>> futures;

   public PackDispatchListener(@NotNull PacketHandler packetHandler) {
      super();
      this.packetHandler = packetHandler;
      this.futures = new ConcurrentHashMap();
   }

   @NotNull
   public final PacketHandler getPacketHandler() {
      return this.packetHandler;
   }

   @EventHandler
   public final void onInitialize(@NotNull PlayerConnectionInitialConfigureEvent playerConnectionInitialConfigureEvent) {
      if (!Settings.DISABLE_NETTY_INJECT.toBool()) {
         PacketHandler packetHandler2 = this.packetHandler;
         PlayerConfigurationConnection connection = playerConnectionInitialConfigureEvent.getConnection();
         packetHandler2.inject((PaperPlayerConfigurationConnection)connection);
      }

   }

   @EventHandler
   public final void onInitialConfig(@NotNull AsyncPlayerConnectionConfigureEvent asyncPlayerConnectionConfigureEvent) {
      if (Settings.PACK_SEND_PRE_JOIN.toBool() && NexoPlugin.Companion.instance().packServer().isPackUploaded()) {
         UUID id = asyncPlayerConnectionConfigureEvent.getConnection().getProfile().getId();
         if (id != null) {
            UUID uuid = id;

            try {
               CompletableFuture completableFuture = (CompletableFuture)this.futures.remove(uuid);
               if (completableFuture != null) {
                  completableFuture.complete((Object)null);
               }
            } catch (Exception e) {
            }

            NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
            PlayerConfigurationConnection connection = asyncPlayerConnectionConfigureEvent.getConnection();
            CompletableFuture completableFuture3 = packServer.sendPack(connection, false);
            if (completableFuture3 != null) {
               int value = 0;
               ((Map)this.futures).put(uuid, completableFuture3);
               CompletableFuture completableFuture2 = completableFuture3.completeOnTimeout((Object)null, 60L, TimeUnit.SECONDS);
               if (completableFuture2 != null) {
                  Void join = (Void)completableFuture2.join();
               }
            }

         }
      }
   }

   @EventHandler
   public final void onReconfig(@NotNull PlayerConnectionReconfigureEvent playerConnectionReconfigureEvent) {
      if (Settings.PACK_SEND_PRE_JOIN.toBool() && NexoPlugin.Companion.instance().packServer().isPackUploaded()) {
         NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
         PlayerConfigurationConnection connection = playerConnectionReconfigureEvent.getConnection();
         packServer.sendPack(connection, true);
      }
   }

   @EventHandler
   public final void onClose(@NotNull PlayerConnectionCloseEvent playerConnectionCloseEvent) {

      try {
         CompletableFuture completableFuture = (CompletableFuture)this.futures.remove(playerConnectionCloseEvent.getPlayerUniqueId());
         if (completableFuture != null) {
            completableFuture.complete((Object)null);
         }
      } catch (Exception e) {
      }

   }
}
