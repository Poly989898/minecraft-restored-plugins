package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl4 implements Comparator {
   @Override
   public final int compare(Object obj, Object obj2) {
      KotlinUtil26 kotlinUtil26 = (KotlinUtil26)obj2;
      Comparable comparable = kotlinUtil26.obtainLongValue();
      KotlinUtil26 kotlinUtil262 = (KotlinUtil26)obj;
      return KotlinCollection.computeIntValue(comparable, kotlinUtil262.obtainLongValue());
   }
}
