package com.nexomc.nexo.pack;

import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.SingleAtlasSource;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class AtlasGenerator$generateLegacyAtlasSetup$$inlined$sortBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      AtlasSource it = (AtlasSource)a;
      int value = 0;
      Comparable comparable = (Comparable)((it instanceof SingleAtlasSource ? (SingleAtlasSource)it : null) != null ? (it instanceof SingleAtlasSource ? (SingleAtlasSource)it : null).resource() : null);
      it = (AtlasSource)b;
      Comparable comparable2 = comparable;
      value = 0;
      return ComparisonsKt.compareValues(comparable2, (Comparable)((it instanceof SingleAtlasSource ? (SingleAtlasSource)it : null) != null ? (it instanceof SingleAtlasSource ? (SingleAtlasSource)it : null).resource() : null));
   }
}
