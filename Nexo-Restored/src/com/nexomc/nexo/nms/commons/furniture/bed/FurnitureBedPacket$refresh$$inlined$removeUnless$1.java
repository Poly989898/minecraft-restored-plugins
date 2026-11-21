package com.nexomc.nexo.nms.commons.furniture.bed;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import net.minecraft.network.Connection;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class FurnitureBedPacket$refresh$$inlined$removeUnless$1 implements Function1<Connection, Boolean> {
   private final List $receiver$inlined;
   public FurnitureBedPacket$refresh$$inlined$removeUnless$1(List list) {
      this.$receiver$inlined = list;
   }

   public final Boolean invoke(Connection it) {
      Connection p0 = (Connection)it;
      int value = 0;
      return !this.$receiver$inlined.contains(p0);
   }
}
