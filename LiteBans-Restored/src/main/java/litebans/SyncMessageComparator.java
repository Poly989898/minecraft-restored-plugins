package litebans;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public final class SyncMessageComparator implements Comparator {
   @Override
   public final int compare(Object obj, Object obj2) {
      Entry entry = (Entry)obj2;
      Comparable comparable = ((List)entry.getValue()).size();
      Entry entry2 = (Entry)obj;
      return KotlinCollection.computeIntValue(comparable, ((List)entry2.getValue()).size());
   }
}
