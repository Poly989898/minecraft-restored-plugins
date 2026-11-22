package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DurationLimits {
   private final long longValue;
   private final long muteDuration;

   public DurationLimits(long value, long value2) {
      this.longValue = value;
      this.muteDuration = value2;
   }

   public final long computeLongValue(@NotNull PunishmentType punishmentType) {
      long value;
      switch (PunishmentTypeSwitchMap.value[punishmentType.ordinal()]) {
         case 1:
            value = this.longValue;
            break;
         case 2:
            value = this.muteDuration;
            break;
         default:
            StandardKt.notImplemented();
            throw new CommandAbortException();
      }

      return value;
   }

   @Override
   public String toString() {
      return "(a=" + this.longValue + ", mute=" + this.muteDuration + ')';
   }

   @Override
   public int hashCode() {
      int value = Long.hashCode(this.longValue);
      return value * 31 + Long.hashCode(this.muteDuration);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof DurationLimits)) {
         return false;
      }

      DurationLimits kotlinData2 = (DurationLimits)obj;
      return this.longValue != kotlinData2.longValue ? false : this.muteDuration == kotlinData2.muteDuration;
   }
}
