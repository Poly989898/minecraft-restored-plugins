package com.nexomc.nexo.mechanics.custom_block.chorusblock;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import org.bukkit.block.BlockFace;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class ChorusBlockFactory$blockstateVariant$lambda$0$$inlined$sortedBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      BlockFace it = (BlockFace)a;
      int value = 0;
      Comparable comparable = (Comparable)it.name();
      it = (BlockFace)b;
      Comparable comparable2 = comparable;
      value = 0;
      return ComparisonsKt.compareValues(comparable2, (Comparable)it.name());
   }
}
