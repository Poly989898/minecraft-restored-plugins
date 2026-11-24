package net.Indyuce.mmocore.loot;

import java.util.Collection;
import java.util.Random;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.jetbrains.annotations.NotNull;

public class RandomWeightedRoll<T extends Weighted> {
   private final Collection<T> collection;
   private final T rolled;
   private static final Random RANDOM = new Random();

   public RandomWeightedRoll(PlayerData player, Collection<T> collection, double chanceWeight) {
      this.collection = collection;
      double partialSum = 0.0;
      double randomCoefficient = RANDOM.nextDouble();
      double effectiveLuck = 0.07 * chanceWeight * player.getStats().getStat("CHANCE");
      double sum = this.weightedSum(effectiveLuck);

      for (T item : collection) {
         partialSum += this.computeRealWeight(item, effectiveLuck);
         if (partialSum >= randomCoefficient * sum) {
            this.rolled = item;
            return;
         }
      }

      throw new RuntimeException("Could not roll item, effective luck is " + effectiveLuck);
   }

   @NotNull
   public T rollItem() {
      return this.rolled;
   }

   private double weightedSum(double effectiveLuck) {
      double sum = 0.0;

      for (T item : this.collection) {
         sum += this.computeRealWeight(item, effectiveLuck);
      }

      return sum;
   }

   private double computeRealWeight(T item, double effectiveLuck) {
      return Math.pow(item.getWeight(), Math.pow(1.0 + effectiveLuck, -0.33333333333));
   }
}
