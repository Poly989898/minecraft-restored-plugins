package com.nexomc.nexo.nms.v26_R1.packets;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoChannelHandler$special$$inlined$registerReader$default$4 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $reader;
   public NexoChannelHandler$special$$inlined$registerReader$default$4(Function1 $reader) {
      this.$reader = $reader;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      if (Intrinsics.areEqual(packet.getClass(), ClientboundForgetLevelChunkPacket.class)) {
         this.$reader.invoke((Packet)((ClientboundForgetLevelChunkPacket)packet));
      }

      return packet;
   }
}
