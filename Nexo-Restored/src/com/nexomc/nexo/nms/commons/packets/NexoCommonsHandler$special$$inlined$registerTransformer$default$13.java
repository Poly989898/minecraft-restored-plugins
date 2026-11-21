package com.nexomc.nexo.nms.commons.packets;

import com.nexomc.nexo.nms.commons.furniture.ClientboundSendBarrierHitboxPacket;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import net.minecraft.network.protocol.Packet;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoCommonsHandler$special$$inlined$registerTransformer$default$13 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $transformer;
   public NexoCommonsHandler$special$$inlined$registerTransformer$default$13(Function1 transformer) {
      this.$transformer = transformer;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      return packet instanceof ClientboundSendBarrierHitboxPacket ? (Packet)this.$transformer.invoke(packet) : packet;
   }
}
