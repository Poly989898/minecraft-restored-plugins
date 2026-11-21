package com.nexomc.nexo.pack;

import java.util.Comparator;
import java.util.Map;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class TextureMerger$stitchFull$$inlined$sortedByDescending$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      Map.Entry it = (Map.Entry)b;
      int value = 0;
      Comparable key = (Comparable)((Integer)it.getKey());
      it = (Map.Entry)a;
      Comparable comparable = key;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)((Integer)it.getKey()));
   }
}
