package io.lumine.mythic.bukkit.utils.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface VariableAmount {
   @Nonnull
   static VariableAmount fixed(double value) {
      return new VariableAmount.Fixed(value);
   }

   @Nonnull
   static VariableAmount range(double min, double max) {
      return new VariableAmount.BaseAndAddition(min, fixed(max - min));
   }

   @Nonnull
   static VariableAmount baseWithVariance(double base, double variance) {
      return new VariableAmount.BaseAndVariance(base, fixed(variance));
   }

   @Nonnull
   static VariableAmount baseWithVariance(double base, @Nonnull VariableAmount variance) {
      return new VariableAmount.BaseAndVariance(base, variance);
   }

   @Nonnull
   static VariableAmount baseWithRandomAddition(double base, double addition) {
      return new VariableAmount.BaseAndAddition(base, fixed(addition));
   }

   @Nonnull
   static VariableAmount baseWithRandomAddition(double base, @Nonnull VariableAmount addition) {
      return new VariableAmount.BaseAndAddition(base, addition);
   }

   @Nonnull
   static VariableAmount baseWithOptionalVariance(double base, double variance, double chance) {
      return new VariableAmount.OptionalAmount(base, chance, baseWithVariance(base, variance));
   }

   @Nonnull
   static VariableAmount baseWithOptionalVariance(double base, @Nonnull VariableAmount variance, double chance) {
      return new VariableAmount.OptionalAmount(base, chance, baseWithVariance(base, variance));
   }

   @Nonnull
   static VariableAmount baseWithOptionalAddition(double base, double addition, double chance) {
      return new VariableAmount.OptionalAmount(base, chance, baseWithRandomAddition(base, addition));
   }

   @Nonnull
   static VariableAmount baseWithOptionalAddition(double base, @Nonnull VariableAmount addition, double chance) {
      return new VariableAmount.OptionalAmount(base, chance, baseWithRandomAddition(base, addition));
   }

   double getAmount(@Nonnull Random random);

   default double getAmount() {
      return this.getAmount(ThreadLocalRandom.current());
   }

   default int getFlooredAmount(@Nonnull Random random) {
      return (int)Math.floor(this.getAmount(random));
   }

   default int getFlooredAmount() {
      return (int)Math.floor(this.getAmount());
   }

   public static final class BaseAndAddition implements VariableAmount {
      private final double base;
      private final VariableAmount addition;

      private BaseAndAddition(double base, VariableAmount addition) {
         this.base = base;
         this.addition = addition;
      }

      @Override
      public double getAmount(@Nonnull Random random) {
         return this.base + random.nextDouble() * this.addition.getAmount(random);
      }

      @Override
      public String toString() {
         return "VariableAmount.BaseAndAddition(base=" + this.base + ", addition=" + this.addition + ")";
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof VariableAmount.BaseAndAddition other)
               ? false
               : Double.compare(this.base, other.base) == 0 && this.addition.equals(other.addition);
         }
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (int)(Double.doubleToLongBits(this.base) >>> 32 ^ Double.doubleToLongBits(this.base));
         return result * 59 + this.addition.hashCode();
      }
   }

   public static final class BaseAndVariance implements VariableAmount {
      private final double base;
      private final VariableAmount variance;

      private BaseAndVariance(double base, @Nonnull VariableAmount variance) {
         this.base = base;
         this.variance = variance;
      }

      @Override
      public double getAmount(@Nonnull Random random) {
         double var = this.variance.getAmount(random);
         return this.base + random.nextDouble() * var * 2.0 - var;
      }

      @Override
      public String toString() {
         return "VariableAmount.BaseAndVariance(base=" + this.base + ", variance=" + this.variance + ")";
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof VariableAmount.BaseAndVariance other)
               ? false
               : Double.compare(this.base, other.base) == 0 && this.variance.equals(other.variance);
         }
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (int)(Double.doubleToLongBits(this.base) >>> 32 ^ Double.doubleToLongBits(this.base));
         return result * 59 + this.variance.hashCode();
      }
   }

   public static final class Fixed implements VariableAmount {
      private final double amount;

      private Fixed(double amount) {
         this.amount = amount;
      }

      @Override
      public double getAmount(@Nonnull Random random) {
         return this.amount;
      }

      @Override
      public String toString() {
         return "VariableAmount.Fixed(amount=" + this.getAmount() + ")";
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof VariableAmount.Fixed other) ? false : Double.compare(this.amount, other.amount) == 0;
         }
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         return result * 59 + (int)(Double.doubleToLongBits(this.amount) >>> 32 ^ Double.doubleToLongBits(this.amount));
      }
   }

   public static final class OptionalAmount implements VariableAmount {
      private final double base;
      private final double chance;
      private final VariableAmount inner;

      OptionalAmount(double base, double chance, VariableAmount inner) {
         this.base = base;
         this.chance = chance;
         this.inner = inner;
      }

      @Override
      public double getAmount(@Nonnull Random random) {
         return random.nextDouble() < this.chance ? this.inner.getAmount(random) : this.base;
      }

      @Override
      public String toString() {
         return "VariableAmount.OptionalAmount(base=" + this.base + ", chance=" + this.chance + ", inner=" + this.inner + ")";
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof VariableAmount.OptionalAmount other)
               ? false
               : Double.compare(this.base, other.base) == 0 && Double.compare(this.chance, other.chance) == 0 && this.inner.equals(other.inner);
         }
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + (int)(Double.doubleToLongBits(this.base) >>> 32 ^ Double.doubleToLongBits(this.base));
         result = result * 59 + (int)(Double.doubleToLongBits(this.chance) >>> 32 ^ Double.doubleToLongBits(this.chance));
         return result * 59 + this.inner.hashCode();
      }
   }
}
