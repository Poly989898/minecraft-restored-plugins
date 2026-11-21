package com.nexomc.nexo.nms.v1_21_R11.packets;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoChannelHandler$special$$inlined$registerTransformer$5 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $transformer;
   public NexoChannelHandler$special$$inlined$registerTransformer$5(Function1 $transformer) {
      this.$transformer = $transformer;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      return packet instanceof ClientboundContainerSetContentPacket ? (Packet)this.$transformer.invoke(packet) : packet;
   }
}
