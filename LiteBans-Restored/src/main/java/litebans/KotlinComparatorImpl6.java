package litebans;

import java.util.Comparator;

public final class KotlinComparatorImpl6 implements Comparator {
   final PunishmentType punishmentType;

   public KotlinComparatorImpl6(PunishmentType punishmentType) {
      this.punishmentType = punishmentType;
   }

   @Override
   public final int compare(Object obj, Object obj2) {
      DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj2;
      DurationLimits kotlinData = kotlinData6.getDurationsAlias();
      Comparable comparable = kotlinData.computeLongValue(this.punishmentType);
      DurationLimitGroup kotlinData62 = (DurationLimitGroup)obj;
      DurationLimits kotlinData2 = kotlinData62.getDurationsAlias();
      return KotlinCollection.computeIntValue(comparable, kotlinData2.computeLongValue(this.punishmentType));
   }
}
