package com.nexomc.nexo.pack;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import team.unnamed.creative.metadata.overlays.OverlayEntry;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class Overlays$addToResourcepack$$inlined$compareBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      OverlayEntry it = (OverlayEntry)a;
      int value = 0;
      Comparable comparable = it.formats().min();
      it = (OverlayEntry)b;
      Comparable comparable2 = comparable;
      value = 0;
      return ComparisonsKt.compareValues(comparable2, it.formats().min());
   }
}
