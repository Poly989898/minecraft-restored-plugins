package com.nexomc.nexo.glyphs;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class Shift$cachedPowers$$inlined$sortedByDescending$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      Pair it = (Pair)b;
      int value = 0;
      Object second = it.getSecond();
      Comparable comparable = (Comparable)Math.abs(((Number)second).intValue());
      it = (Pair)a;
      Comparable comparable2 = comparable;
      value = 0;
      Object obj = it.getSecond();
      return ComparisonsKt.compareValues(comparable2, (Comparable)Math.abs(((Number)obj).intValue()));
   }
}
