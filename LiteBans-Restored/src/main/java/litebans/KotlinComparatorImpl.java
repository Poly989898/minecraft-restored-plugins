package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl implements Comparator {
   @Override
   public final int compare(Object obj, Object obj2) {
      DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj;
      boolean flag = kotlinData6.getGroupLimitOther().isTemplateOnly();
      Comparable comparable = flag ? 1 : 0;
      DurationLimitGroup kotlinData62 = (DurationLimitGroup)obj2;
      boolean flag2 = kotlinData62.getGroupLimitOther().isTemplateOnly();
      return KotlinCollection.computeIntValue(comparable, flag2 ? 1 : 0);
   }
}
