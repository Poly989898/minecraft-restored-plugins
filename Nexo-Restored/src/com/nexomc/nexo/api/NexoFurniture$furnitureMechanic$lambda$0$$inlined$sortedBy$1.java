package com.nexomc.nexo.api;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoFurniture$furnitureMechanic$lambda$0$$inlined$sortedBy$1 implements Comparator<Object> {
   private final Location $centerLoc$inlined;
   public NexoFurniture$furnitureMechanic$lambda$0$$inlined$sortedBy$1(Location location) {
      this.$centerLoc$inlined = location;
   }

   public final int compare(Object a, Object b) {
      ItemDisplay it = (ItemDisplay)a;
      int value = 0;
      Comparable comparable = (Comparable)it.getLocation().distanceSquared(this.$centerLoc$inlined);
      it = (ItemDisplay)b;
      Comparable comparable2 = comparable;
      value = 0;
      return ComparisonsKt.compareValues(comparable2, (Comparable)it.getLocation().distanceSquared(this.$centerLoc$inlined));
   }
}
