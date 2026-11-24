package io.lumine.mythic.lib.api.util.ui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class PlusMinusPercent {
   double constant;
   boolean relative;
   boolean multiply;

   public PlusMinusPercent(double constant, boolean additive, boolean multiplicative) {
      this.constant = constant;
      this.relative = additive;
      this.multiply = multiplicative;
   }

   public double apply(double base) {
      double oV = this.constant;
      if (this.multiply) {
         if (this.relative) {
            oV++;
         }

         return base * oV;
      } else {
         return this.relative ? base + oV : oV;
      }
   }

   public double reverse(double base) {
      double oV = this.constant;
      if (this.multiply) {
         if (this.relative) {
            oV++;
         }

         return base / oV;
      } else {
         return this.relative ? base - oV : base;
      }
   }

   public void setConstant(double c) {
      this.constant = c;
   }

   public void setAdditive(boolean additivity) {
      this.relative = additivity;
   }

   public void setMultiplicative(boolean multiplicativity) {
      this.multiply = multiplicativity;
   }

   public double getConstant() {
      return this.constant;
   }

   public boolean isAdditive() {
      return this.relative;
   }

   public boolean isMultiplicative() {
      return this.multiply;
   }

   @Nullable
   @Contract("null -> null")
   public static PlusMinusPercent getFromString(@Nullable String pmp) {
      return getFromString(pmp, null);
   }

   @Nullable
   @Contract("null, _ -> null")
   public static PlusMinusPercent getFromString(@Nullable String pmp, @Nullable FriendlyFeedbackProvider ffp) {
      if (pmp == null) {
         FriendlyFeedbackProvider.log(ffp, FriendlyFeedbackCategory.ERROR, "No value provided to parse PlusMinusPercent. ");
         return null;
      }

      boolean relativity = false;
      boolean multiplicativity = false;
      double positivity = 1.0;
      String unsignedArg = pmp;
      if (pmp.startsWith("-")) {
         relativity = true;
         positivity = -1.0;
         unsignedArg = pmp.substring(1);
      } else if (pmp.startsWith("+")) {
         relativity = true;
         unsignedArg = pmp.substring(1);
      } else if (pmp.startsWith("n")) {
         positivity = -1.0;
         unsignedArg = pmp.substring(1);
      }

      if (pmp.endsWith("%")) {
         multiplicativity = true;
         positivity *= 0.01;
         unsignedArg = unsignedArg.substring(0, unsignedArg.length() - 1);
      }

      Double constnt = SilentNumbers.DoubleParse(unsignedArg);
      if (constnt != null) {
         double value = constnt * positivity;
         return new PlusMinusPercent(value, relativity, multiplicativity);
      } else {
         FriendlyFeedbackProvider.log(
            ffp,
            FriendlyFeedbackCategory.ERROR,
            "Cant parse numeric value from '$r{0}$b' (The numeric part of $i{1}$b: After removing ±, n, and %). ",
            unsignedArg,
            pmp
         );
         return null;
      }
   }

   @Override
   public String toString() {
      StringBuilder str = new StringBuilder();
      if (this.isAdditive() && !this.isMultiplicative()) {
         if (this.getConstant() < 0.0) {
            str.append('-');
         } else {
            str.append('+');
         }
      } else if (this.getConstant() < 0.0) {
         str.append('n');
      }

      double val = this.getConstant();
      if (this.isMultiplicative()) {
         val *= 100.0;
      }

      str.append(SilentNumbers.readableRounding(val, 2));
      if (this.isMultiplicative()) {
         str.append('%');
      }

      return str.toString();
   }
}
