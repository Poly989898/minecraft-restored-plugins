package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl3 implements Comparator {
   final Punishment punishment;

   public KotlinComparatorImpl3(Punishment punishment) {
      this.punishment = punishment;
   }

   @Override
   public final int compare(Object obj, Object obj2) {
      DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj;
      CommandCooldowns kotlinData = kotlinData6.getCooldownsAlias();
      Comparable comparable = kotlinData.computeLongValue(this.punishment);
      DurationLimitGroup kotlinData62 = (DurationLimitGroup)obj2;
      CommandCooldowns kotlinData2 = kotlinData62.getCooldownsAlias();
      return KotlinCollection.computeIntValue(comparable, kotlinData2.computeLongValue(this.punishment));
   }
}
