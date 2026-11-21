package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CommandCooldowns {
   private final long longValue;
   private final long longValue2;
   private final long longValue3;
   private final long longValue4;
   private final long longValue5;

   public CommandCooldowns(long value, long value2, long value3, long value4, long value5) {
      this.longValue = value;
      this.longValue2 = value2;
      this.longValue3 = value3;
      this.longValue4 = value4;
      this.longValue5 = value5;
   }

   public final long getLongValue5() {
      return this.longValue5;
   }

   public final long computeLongValue(@NotNull Punishment punishment) {
      long value;
      switch (PunishmentTypeSwitchMap5.value[punishment.getType().ordinal()]) {
         case 1:
            value = this.longValue;
            break;
         case 2:
            value = this.longValue2;
            break;
         case 3:
            value = this.longValue3;
            break;
         case 4:
            value = this.longValue4;
            break;
         default:
            StandardKt.notImplemented();
            throw new CommandAbortException();
      }

      return value;
   }

   @Override
   public String toString() {
      return "(a=" + this.longValue + ", mute=" + this.longValue2 + ", warn=" + this.longValue3 + ", kick=" + this.longValue4 + ", redo=" + this.longValue5 + ')';
   }

   @Override
   public int hashCode() {
      int value = Long.hashCode(this.longValue);
      int value2 = value * 31 + Long.hashCode(this.longValue2);
      int value3 = value2 * 31 + Long.hashCode(this.longValue3);
      int value4 = value3 * 31 + Long.hashCode(this.longValue4);
      return value4 * 31 + Long.hashCode(this.longValue5);
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof CommandCooldowns)) {
         return false;
      } else {
         CommandCooldowns kotlinData = (CommandCooldowns)obj;
         if (this.longValue != kotlinData.longValue) {
            return false;
         } else if (this.longValue2 != kotlinData.longValue2) {
            return false;
         } else if (this.longValue3 != kotlinData.longValue3) {
            return false;
         } else {
            return this.longValue4 != kotlinData.longValue4 ? false : this.longValue5 == kotlinData.longValue5;
         }
      }
   }
}
