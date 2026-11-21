package litebans;

import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DupeIpResult {
   private final CharSequence charSequence;
   private final String text3;
   private final String text4;
   private final Date date;
   private final Punishment punishment;
   private final Punishment punishment3;

   public DupeIpResult(@NotNull CharSequence message, @NotNull String text, @NotNull String text2, @NotNull Date date, @Nullable Punishment punishment, @Nullable Punishment punishment2) {
      this.charSequence = message;
      this.text3 = text;
      this.text4 = text2;
      this.date = date;
      this.punishment = punishment;
      this.punishment3 = punishment2;
   }

   public final CharSequence getCharSequence() {
      return this.charSequence;
   }

   public final String getText3() {
      return this.text3;
   }

   public final String getText4() {
      return this.text4;
   }

   public final Punishment getPunishment() {
      return this.punishment;
   }

   @Override
   public String toString() {
      return "DupeIPResult(prefix="
         + this.charSequence
         + ", name="
         + this.text3
         + ", uuid="
         + this.text4
         + ", date="
         + this.date
         + ", ban="
         + this.punishment
         + ", mute="
         + this.punishment3
         + ')';
   }

   @Override
   public int hashCode() {
      int value = this.charSequence.hashCode();
      int value2 = value * 31 + this.text3.hashCode();
      int value3 = value2 * 31 + this.text4.hashCode();
      int value4 = value3 * 31 + this.date.hashCode();
      int value5 = value4 * 31 + (this.punishment == null ? 0 : this.punishment.hashCode());
      return value5 * 31 + (this.punishment3 == null ? 0 : this.punishment3.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof DupeIpResult)) {
         return false;
      } else {
         DupeIpResult dupeIpResult = (DupeIpResult)obj;
         if (!Intrinsics.areEqual(this.charSequence, dupeIpResult.charSequence)) {
            return false;
         } else if (!Intrinsics.areEqual(this.text3, dupeIpResult.text3)) {
            return false;
         } else if (!Intrinsics.areEqual(this.text4, dupeIpResult.text4)) {
            return false;
         } else if (!Intrinsics.areEqual(this.date, dupeIpResult.date)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.punishment, dupeIpResult.punishment) ? false : Intrinsics.areEqual(this.punishment3, dupeIpResult.punishment3);
         }
      }
   }
}
