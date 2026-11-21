package io.lumine.mythic.bukkit.utils.random;

import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WeightedObject<T> implements Weighted {
   private final T object;
   private final double weight;

   @Nonnull
   public static <T> WeightedObject<T> of(@Nonnull T object, double weight) {
      return new WeightedObject<>(object, weight);
   }

   private WeightedObject(T object, double weight) {
      Preconditions.checkArgument(weight >= 0.0, "weight cannot be negative");
      this.object = Objects.requireNonNull(object, "object");
      this.weight = weight;
   }

   @Nonnull
   public T get() {
      return this.object;
   }

   @Override
   public double getWeight() {
      return this.weight;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else {
         return !(o instanceof WeightedObject other) ? false : this.object.equals(other.object) && Double.compare(this.getWeight(), other.getWeight()) == 0;
      }
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.object.hashCode();
      return result * 59 + (int)(Double.doubleToLongBits(this.getWeight()) >>> 32 ^ Double.doubleToLongBits(this.getWeight()));
   }

   @Override
   public String toString() {
      return "WeightedObject(object=" + this.object + ", weight=" + this.getWeight() + ")";
   }
}
