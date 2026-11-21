package com.nexomc.nexo.pack;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import team.unnamed.creative.model.Model;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class TextureMerger$stitchFull$$inlined$sortedBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      Model it = (Model)a;
      int value = 0;
      Comparable asString = (Comparable)it.key().asString();
      it = (Model)b;
      Comparable comparable = asString;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)it.key().asString());
   }
}
