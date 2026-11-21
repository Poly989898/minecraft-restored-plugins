package com.nexomc.nexo.fonts;

import com.nexomc.nexo.glyphs.Glyph;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class FontManager$emojis$$inlined$sortedBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      Glyph it = (Glyph)a;
      int value = 0;
      Comparable id = (Comparable)it.getId();
      it = (Glyph)b;
      Comparable comparable = id;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)it.getId());
   }
}
