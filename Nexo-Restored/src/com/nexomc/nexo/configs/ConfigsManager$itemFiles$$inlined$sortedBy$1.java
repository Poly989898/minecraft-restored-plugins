package com.nexomc.nexo.configs;

import java.io.File;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class ConfigsManager$itemFiles$$inlined$sortedBy$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      File p0 = (File)a;
      int value = 0;
      Comparable nameWithoutExtension = (Comparable)FilesKt.getNameWithoutExtension(p0);
      p0 = (File)b;
      Comparable comparable = nameWithoutExtension;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)FilesKt.getNameWithoutExtension(p0));
   }
}
