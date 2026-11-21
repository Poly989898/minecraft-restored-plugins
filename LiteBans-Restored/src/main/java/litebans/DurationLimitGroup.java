package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DurationLimitGroup {
   private final String name;
   private final DurationLimits durations;
   private final CommandCooldowns cooldowns;
   private final GroupLimitOther groupLimitOther;

   public DurationLimitGroup(@NotNull String text, @NotNull DurationLimits kotlinData, @NotNull CommandCooldowns kotlinData2, @NotNull GroupLimitOther groupLimitOther) {
      this.name = text;
      this.durations = kotlinData;
      this.cooldowns = kotlinData2;
      this.groupLimitOther = groupLimitOther;
   }

   public final String getName() {
      return this.name;
   }

   public final DurationLimits getDurations() {
      return this.durations;
   }

   public final CommandCooldowns getCooldowns() {
      return this.cooldowns;
   }

   public final GroupLimitOther getGroupLimitOther() {
      return this.groupLimitOther;
   }

   public final DurationLimits getDurationsAlias() {
      return this.durations;
   }

   public final CommandCooldowns getCooldownsAlias() {
      return this.cooldowns;
   }

   @Override
   public String toString() {
      return "(a=" + this.name + ", duration=" + this.durations + ", cooldown=" + this.cooldowns + ", other=" + this.groupLimitOther + ')';
   }

   @Override
   public int hashCode() {
      int value = this.name.hashCode();
      int value2 = value * 31 + this.durations.hashCode();
      int value3 = value2 * 31 + this.cooldowns.hashCode();
      return value3 * 31 + this.groupLimitOther.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof DurationLimitGroup)) {
         return false;
      } else {
         DurationLimitGroup kotlinData6 = (DurationLimitGroup)obj;
         if (!Intrinsics.areEqual(this.name, kotlinData6.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.durations, kotlinData6.durations)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.cooldowns, kotlinData6.cooldowns) ? false : Intrinsics.areEqual(this.groupLimitOther, kotlinData6.groupLimitOther);
         }
      }
   }
}
