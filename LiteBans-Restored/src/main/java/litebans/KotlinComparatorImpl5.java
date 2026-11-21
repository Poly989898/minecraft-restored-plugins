package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl5 implements Comparator {
   @Override
   public final int compare(Object obj, Object obj2) {
      Pair pair = (Pair)obj2;
      var long2 = (Long)pair.getFirst();
      Pair pair2 = (Pair)obj;
      return KotlinCollection.computeIntValue(long2, (Long)pair2.getFirst());
   }
}
