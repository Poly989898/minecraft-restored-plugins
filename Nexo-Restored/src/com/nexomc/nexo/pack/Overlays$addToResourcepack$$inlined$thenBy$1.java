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
public final class Overlays$addToResourcepack$$inlined$thenBy$1 implements Comparator<Object> {
   private final Comparator $this_thenBy;
   public Overlays$addToResourcepack$$inlined$thenBy$1(Comparator receiver) {
      this.$this_thenBy = receiver;
   }

   public final int compare(Object a, Object b) {
      int previousCompare = this.$this_thenBy.compare(a, b);
      int value;
      if (previousCompare != 0) {
         value = previousCompare;
      } else {
         OverlayEntry it = (OverlayEntry)a;
         int value2 = 0;
         Comparable comparable = it.formats().max();
         it = (OverlayEntry)b;
         Comparable comparable2 = comparable;
         value2 = 0;
         value = ComparisonsKt.compareValues(comparable2, it.formats().max());
      }

      return value;
   }
}
