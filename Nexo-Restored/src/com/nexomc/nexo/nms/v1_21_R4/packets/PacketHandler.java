package com.nexomc.nexo.nms.v1_21_R4.packets;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.nms.IPacketHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.Connection;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/nms/v1_21_R4/packets/PacketHandler;", "Lcom/nexomc/nexo/nms/IPacketHandler;", "<init>", "()V", "inject", "", "player", "Lorg/bukkit/entity/Player;", "uninject", "com.nexomc:v1_21_R4"}
)
public final class PacketHandler implements IPacketHandler {
   public void inject(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
      Channel channel = ((CraftPlayer)player).getHandle().connection.connection.channel;
      ChannelPipeline pipeline = channel.pipeline();
      PacketHandler var4 = this;

      try {
         PacketHandler $this$inject_u24lambda_u240 = var4;
         int var6 = 0;
         Object var14 = ResultShim.ok(pipeline.remove("nexo_packet_handler"));
      } catch (Throwable var11) {
         Object $this$inject_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var11));
      }

      NexoChannelHandler nexoChannelHandler = new NexoChannelHandler(player);
      Iterable $this$forEach$iv = (Iterable)pipeline.toMap().keySet();

      for(Object element$iv : $this$forEach$iv) {
         String it = (String)element$iv;
         int var10 = 0;
         if (pipeline.get(it) instanceof Connection) {
            pipeline.addBefore(it, "nexo_packet_handler", (ChannelHandler)nexoChannelHandler);
         }
      }

   }

   public void uninject(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
      Channel channel = ((CraftPlayer)player).getHandle().connection.connection.channel;
      ChannelPipeline pipeline = channel.pipeline();
      channel.eventLoop().submit(PacketHandler::uninject$lambda$0);
   }

   private static final void uninject$lambda$0(ChannelPipeline $pipeline) {
      $pipeline.remove("nexo_packet_handler");
   }
}
