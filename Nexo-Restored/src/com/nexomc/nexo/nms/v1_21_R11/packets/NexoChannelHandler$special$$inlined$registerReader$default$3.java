package com.nexomc.nexo.nms.v1_21_R11.packets;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPickItemFromEntityPacket;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoChannelHandler$special$$inlined$registerReader$default$3 implements Function1<Packet<?>, Packet<?>> {
   private final Function1 $reader;
   public NexoChannelHandler$special$$inlined$registerReader$default$3(Function1 $reader) {
      this.$reader = $reader;
   }

   public final Packet<?> invoke(Packet<?> packet) {
      Intrinsics.checkNotNullParameter(packet, "packet");
      if (Intrinsics.areEqual(packet.getClass(), ServerboundPickItemFromEntityPacket.class)) {
         this.$reader.invoke((Packet)((ServerboundPickItemFromEntityPacket)packet));
      }

      return packet;
   }
}
