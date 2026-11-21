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
public final class TextureMerger$stitchFull$lambda$8$$inlined$sortedBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      Map.Entry it = (Map.Entry)a;
      int value = 0;
      Comparable asString = (Comparable)((TextureMerger.CropKey)it.getKey()).getTextureKey().asString();
      it = (Map.Entry)b;
      Comparable comparable = asString;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)((TextureMerger.CropKey)it.getKey()).getTextureKey().asString());
   }
}
