package com.nexomc.nexo.nms.v26_R1;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.nms.v26_R1.packets.PacketHandler;
import com.nexomc.nexo.pack.server.NexoPackServer;
import io.papermc.paper.connection.PaperPlayerConfigurationConnection;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent;
import io.papermc.paper.event.connection.configuration.PlayerConnectionInitialConfigureEvent;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\r\u001a\u00020\u000e*\u00020\u000fH\u0007b\u0002\b\u0010J\u0010\u0010\u0011\u001a\u00020\u000e*\u00020\u0012H\u0007b\u0002\b\u0010J\u0010\u0010\u0013\u001a\u00020\u000e*\u00020\u0014H\u0007b\u0002\b\u0010R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R \u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R1/PackDispatchListener;", "Lorg/bukkit/event/Listener;", "packetHandler", "Lcom/nexomc/nexo/nms/v26_R1/packets/PacketHandler;", "<init>", "(Lcom/nexomc/nexo/nms/v26_R1/packets/PacketHandler;)V", "getPacketHandler", "()Lcom/nexomc/nexo/nms/v26_R1/packets/PacketHandler;", "futures", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/util/UUID;", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "onInitialize", "", "Lio/papermc/paper/event/connection/configuration/PlayerConnectionInitialConfigureEvent;", "Lorg/bukkit/event/EventHandler;", "onInitialConfig", "Lio/papermc/paper/event/connection/configuration/AsyncPlayerConnectionConfigureEvent;", "onClose", "Lcom/destroystokyo/paper/event/player/PlayerConnectionCloseEvent;", "com.nexomc:v26_R1"}
)
public final class PackDispatchListener implements Listener {
   @NotNull
   private final PacketHandler packetHandler;
   @NotNull
   private final ConcurrentHashMap<UUID, CompletableFuture<Void>> futures;

   public PackDispatchListener(@NotNull PacketHandler packetHandler) {
      Intrinsics.checkNotNullParameter(packetHandler, "packetHandler");
      super();
      this.packetHandler = packetHandler;
      this.futures = new ConcurrentHashMap();
   }

   @NotNull
   public final PacketHandler getPacketHandler() {
      return this.packetHandler;
   }

   @EventHandler
   public final void onInitialize(@NotNull PlayerConnectionInitialConfigureEvent $this$onInitialize) {
      Intrinsics.checkNotNullParameter($this$onInitialize, "<this>");
      if (!Settings.DISABLE_NETTY_INJECT.toBool()) {
         PacketHandler var10000 = this.packetHandler;
         PlayerConfigurationConnection var10001 = $this$onInitialize.getConnection();
         Intrinsics.checkNotNull(var10001, "null cannot be cast to non-null type io.papermc.paper.connection.PaperPlayerConfigurationConnection");
         var10000.inject((PaperPlayerConfigurationConnection)var10001);
      }

   }

   @EventHandler
   public final void onInitialConfig(@NotNull AsyncPlayerConnectionConfigureEvent $this$onInitialConfig) {
      Intrinsics.checkNotNullParameter($this$onInitialConfig, "<this>");
      if (Settings.PACK_SEND_PRE_JOIN.toBool() && NexoPlugin.Companion.instance().packServer().isPackUploaded()) {
         UUID var10000 = $this$onInitialConfig.getConnection().getProfile().getId();
         if (var10000 != null) {
            UUID uuid = var10000;

            try {
               CompletableFuture var9 = (CompletableFuture)this.futures.remove(uuid);
               if (var9 != null) {
                  var9.complete((Object)null);
               }
            } catch (Exception var8) {
            }

            NexoPackServer var10 = NexoPlugin.Companion.instance().packServer();
            PlayerConfigurationConnection var10001 = $this$onInitialConfig.getConnection();
            Intrinsics.checkNotNullExpressionValue(var10001, "getConnection(...)");
            CompletableFuture $this$onInitialConfig_u24lambda_u240 = var10.sendPack(var10001, false);
            if ($this$onInitialConfig_u24lambda_u240 != null) {
               int var7 = 0;
               ((Map)this.futures).put(uuid, $this$onInitialConfig_u24lambda_u240);
               CompletableFuture var5 = $this$onInitialConfig_u24lambda_u240.completeOnTimeout((Object)null, 60L, TimeUnit.SECONDS);
               if (var5 != null) {
                  Void var11 = (Void)var5.join();
               }
            }

         }
      }
   }

   @EventHandler
   public final void onClose(@NotNull PlayerConnectionCloseEvent $this$onClose) {
      Intrinsics.checkNotNullParameter($this$onClose, "<this>");

      try {
         CompletableFuture var10000 = (CompletableFuture)this.futures.remove($this$onClose.getPlayerUniqueId());
         if (var10000 != null) {
            var10000.complete((Object)null);
         }
      } catch (Exception var3) {
      }

   }
}
