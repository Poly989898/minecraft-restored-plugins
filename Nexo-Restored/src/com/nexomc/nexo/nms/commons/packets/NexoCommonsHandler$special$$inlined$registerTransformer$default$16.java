package com.nexomc.nexo.nms.commons.packets;

import com.nexomc.nexo.nms.commons.furniture.ClientboundRemoveFurnitureLightsPacket;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import net.minecraft.network.protocol.Packet;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoCommonsHandler$special$$inlined$registerTransformer$default$16 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $transformer;
   public NexoCommonsHandler$special$$inlined$registerTransformer$default$16(Function1 transformer) {
      this.$transformer = transformer;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      return packet instanceof ClientboundRemoveFurnitureLightsPacket ? (Packet)this.$transformer.invoke(packet) : packet;
   }
}
