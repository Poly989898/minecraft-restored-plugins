package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PunishmentTarget {
   public static final PunishmentTargetCompanion PUNISHMENT_TARGET_COMPANION = new PunishmentTargetCompanion(null);
   private final byte byteValue;
   private final String text2;
   public static final byte VALUE_0 = 0;
   public static final byte VALUE_1 = 1;
   public static final byte VALUE_2 = 2;

   public PunishmentTarget(byte b, @NotNull String text) {
      this.byteValue = b;
      this.text2 = text;
   }

   public final byte getByteValue() {
      return this.byteValue;
   }

   public final String getText2() {
      return this.text2;
   }

   public final String obtainText() {
      byte b = this.byteValue;
      if (b == 0) {
         return "name";
      } else if (b == 1) {
         return "uuid";
      } else if (b == 2) {
         return "ip";
      } else {
         throw new AssertionError();
      }
   }

   @Override
   public String toString() {
      return "Target(type=" + this.byteValue + ", target=" + this.text2 + ')';
   }

   @Override
   public int hashCode() {
      int value = Byte.hashCode(this.byteValue);
      return value * 31 + this.text2.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof PunishmentTarget)) {
         return false;
      }

      PunishmentTarget punishmentTarget = (PunishmentTarget)obj;
      return this.byteValue != punishmentTarget.byteValue ? false : Intrinsics.areEqual(this.text2, punishmentTarget.text2);
   }
}
