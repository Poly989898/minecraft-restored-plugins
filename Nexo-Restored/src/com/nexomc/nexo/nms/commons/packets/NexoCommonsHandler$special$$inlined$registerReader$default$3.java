package com.nexomc.nexo.nms.commons.packets;

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
public final class NexoCommonsHandler$special$$inlined$registerReader$default$3 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $reader;
   public NexoCommonsHandler$special$$inlined$registerReader$default$3(Function1 reader) {
      this.$reader = reader;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      if (Intrinsics.areEqual(packet.getClass(), ClientboundForgetLevelChunkPacket.class)) {
         this.$reader.invoke((Packet)((ClientboundForgetLevelChunkPacket)packet));
      }

      return packet;
   }
}
