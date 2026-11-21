package com.nexomc.nexo.pack;

import java.io.File;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;

@Metadata(
   mv = {2, 4, 0},
   k = 3,
   xi = 48
)
public final class NexoPackSquash$squashPack$$inlined$sortedByDescending$1 implements Comparator<Object> {
   public final int compare(Object a, Object b) {
      File p0 = (File)b;
      int value = 0;
      Comparable lastModified = (Comparable)p0.lastModified();
      p0 = (File)a;
      Comparable comparable = lastModified;
      value = 0;
      return ComparisonsKt.compareValues(comparable, (Comparable)p0.lastModified());
   }
}
