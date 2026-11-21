package com.nexomc.nexo.mechanics.furniture.seats;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Vector;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class FurnitureSeat$Companion$sitOnSeat$$inlined$sortedBy$1 implements Comparator<Object> {
   private final ItemDisplay $baseEntity$inlined;
   private final Vector $clickOffsetWorld$inlined;
   public FurnitureSeat$Companion$sitOnSeat$$inlined$sortedBy$1(ItemDisplay itemDisplay, Vector vector) {
      this.$baseEntity$inlined = itemDisplay;
      this.$clickOffsetWorld$inlined = vector;
   }

   public final int compare(Object a, Object b) {
      FurnitureSeat it = (FurnitureSeat)a;
      int value = 0;
      Comparable comparable = (Comparable)it.offset(this.$baseEntity$inlined.getYaw()).distanceSquared(this.$clickOffsetWorld$inlined);
      it = (FurnitureSeat)b;
      Comparable comparable2 = comparable;
      value = 0;
      return ComparisonsKt.compareValues(comparable2, (Comparable)it.offset(this.$baseEntity$inlined.getYaw()).distanceSquared(this.$clickOffsetWorld$inlined));
   }
}
