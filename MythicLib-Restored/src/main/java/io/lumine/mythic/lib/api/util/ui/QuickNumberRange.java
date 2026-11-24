package io.lumine.mythic.lib.api.util.ui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuickNumberRange implements Cloneable {
   @Nullable
   Double minimumInclusive;
   @Nullable
   Double maximumInclusive;

   @Nullable
   public Double getMinimumInclusive() {
      return this.minimumInclusive;
   }

   @Nullable
   public Double getMaximumInclusive() {
      return this.maximumInclusive;
   }

   public boolean hasMin() {
      return this.minimumInclusive != null;
   }

   public boolean hasMax() {
      return this.maximumInclusive != null;
   }

   public boolean isSimple() {
      return this.hasMax() && this.hasMin() ? this.getMaximumInclusive().equals(this.getMinimumInclusive()) : false;
   }

   public double getAsDouble(double def) {
      if (this.hasMin()) {
         return this.getMinimumInclusive();
      } else {
         return this.hasMax() ? this.getMaximumInclusive() : def;
      }
   }

   @Contract("null -> null")
   @Nullable
   public static QuickNumberRange getFromString(@Nullable String qnr) {
      return getFromString(qnr, null);
   }

   @Contract("null, _ -> null")
   @Nullable
   public static QuickNumberRange getFromString(@Nullable String qnr, @Nullable FriendlyFeedbackProvider ffp) {
      if (qnr == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No value provided to parse QuickNumberRange. ");
         return null;
      }

      if (qnr.equals("..")) {
         return new QuickNumberRange(null, null);
      }

      Double asItself = SilentNumbers.DoubleParse(qnr);
      if (asItself != null) {
         return new QuickNumberRange(asItself, asItself);
      }

      if (qnr.contains("..")) {
         String[] blit = qnr.split("\\.\\.");
         if (blit.length == 2 || blit.length == 1) {
            boolean failure = false;
            String m = "";
            String M = "";
            if (blit.length == 2) {
               m = blit[0];
               M = blit[1];
            } else if (qnr.startsWith("..")) {
               M = blit[0];
            } else {
               m = blit[0];
            }

            Double min = SilentNumbers.DoubleParse(m);
            Double max = SilentNumbers.DoubleParse(M);
            if (m.length() > 0 && min == null) {
               failure = true;
               FriendlyFeedbackProvider.log(
                  ffp, FriendlyFeedbackCategory.ERROR, "Expected numeric value (negative and decimals supported) instead of $u{0}. ", m
               );
            }

            if (M.length() > 0 && max == null) {
               failure = true;
               FriendlyFeedbackProvider.log(
                  ffp, FriendlyFeedbackCategory.ERROR, "Expected numeric value (negative and decimals supported) instead of $u{0}. ", M
               );
            }

            if (!failure) {
               return new QuickNumberRange(min, max);
            }
         }
      }

      FriendlyFeedbackProvider.log(
         ffp,
         FriendlyFeedbackCategory.ERROR,
         "Invalid syntax in '$u{0}$b': Expected a $enumber$b, or a range in the format $e[m]..[M]$b where m is the minimum number, and M is the maximum number (both optional). ",
         qnr
      );
      return null;
   }

   public QuickNumberRange(@Nullable Double min, @Nullable Double max) {
      this.minimumInclusive = min;
      this.maximumInclusive = max;
   }

   public boolean inRange(double test) {
      return this.hasMin() && test < this.getMinimumInclusive() ? false : !this.hasMax() || !(test > this.getMaximumInclusive());
   }

   @Override
   public String toString() {
      if (this.isSimple()) {
         return String.valueOf(this.getMaximumInclusive());
      }

      String minn = "";
      String maxn = "";
      if (this.hasMin()) {
         minn = String.valueOf(this.getMinimumInclusive());
      }

      if (this.hasMax()) {
         maxn = String.valueOf(this.getMaximumInclusive());
      }

      return minn + ".." + maxn;
   }

   @NotNull
   public String toStringColored() {
      if (this.isSimple()) {
         return "$r" + this.getMaximumInclusive();
      }

      String minn = "-∞";
      String maxn = "∞";
      if (this.hasMin()) {
         minn = String.valueOf(this.getMinimumInclusive());
      }

      if (this.hasMax()) {
         maxn = String.valueOf(this.getMaximumInclusive());
      }

      return "$r" + minn + " $bto$r " + maxn;
   }

   public QuickNumberRange clone() {
      return new QuickNumberRange(this.getMinimumInclusive(), this.getMaximumInclusive());
   }
}
