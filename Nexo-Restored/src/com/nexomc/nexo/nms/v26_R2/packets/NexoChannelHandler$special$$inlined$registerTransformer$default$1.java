package com.nexomc.nexo.nms.v26_R2.packets;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoChannelHandler$special$$inlined$registerTransformer$default$1 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $transformer;
   public NexoChannelHandler$special$$inlined$registerTransformer$default$1(Function1 transformer) {
      this.$transformer = transformer;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      return packet instanceof ClientboundShowDialogPacket ? (Packet)this.$transformer.invoke(packet) : packet;
   }
}
