package com.nexomc.nexo.nms.v1_21_R8.packets;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoChannelHandler$special$$inlined$registerIgnored$default$1 implements Function1<Packet<?>, Packet<?>> {
   public static final NexoChannelHandler$special$$inlined$registerIgnored$default$1 INSTANCE = new NexoChannelHandler$special$$inlined$registerIgnored$default$1();

   public final Packet<?> invoke(Packet<?> packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      return packet;
   }
}
