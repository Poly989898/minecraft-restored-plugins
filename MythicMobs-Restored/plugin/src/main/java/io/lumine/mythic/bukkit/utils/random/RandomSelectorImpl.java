package io.lumine.mythic.bukkit.utils.random;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;

final class RandomSelectorImpl<E> implements RandomSelector<E> {
   private final E[] elements;
   private final RandomSelectorImpl.IndexSelector selection;

   static <E> RandomSelector<E> uniform(Collection<E> elements) {
      Objects.requireNonNull(elements, "elements must not be null");
      Preconditions.checkArgument(!elements.isEmpty(), "elements must not be empty");
      int size = elements.size();
      E[] array = (E[])elements.toArray(new Object[size]);
      return new RandomSelectorImpl<>(array, new RandomSelectorImpl.BoundedRandomSelector(size));
   }

   static <E> RandomSelector<E> weighted(Collection<E> elements, Weigher<? super E> weigher) {
      Objects.requireNonNull(elements, "elements must not be null");
      Objects.requireNonNull(weigher, "weigher must not be null");
      Preconditions.checkArgument(!elements.isEmpty(), "elements must not be empty");
      int size = elements.size();
      E[] elementArray = (E[])elements.toArray(new Object[size]);
      double totalWeight = 0.0;
      double[] probabilities = new double[size];

      for (int i = 0; i < size; i++) {
         double weight = weigher.weigh(elementArray[i]);
         Preconditions.checkArgument(weight > 0.0, "weigher returned a negative number");
         probabilities[i] = weight;
         totalWeight += weight;
      }

      for (int i = 0; i < size; i++) {
         probabilities[i] /= totalWeight;
      }

      return new RandomSelectorImpl<>(elementArray, new RandomSelectorImpl.WeightedSelector(probabilities));
   }

   private RandomSelectorImpl(E[] elements, RandomSelectorImpl.IndexSelector selection) {
      this.elements = elements;
      this.selection = selection;
   }

   @Override
   public E pick(Random random) {
      return this.elements[this.selection.pickIndex(random)];
   }

   @Override
   public Stream<E> stream(Random random) {
      Objects.requireNonNull(random, "random must not be null");
      return Stream.generate(() -> this.pick(random));
   }

   private static final class BoundedRandomSelector implements RandomSelectorImpl.IndexSelector {
      private final int bound;

      private BoundedRandomSelector(int bound) {
         this.bound = bound;
      }

      @Override
      public int pickIndex(Random random) {
         return random.nextInt(this.bound);
      }
   }

   private interface IndexSelector {
      int pickIndex(Random random);
   }

   private static final class WeightedSelector implements RandomSelectorImpl.IndexSelector {
      private final double[] probabilities;
      private final int[] alias;

      WeightedSelector(double[] probabilities) {
         int size = probabilities.length;
         double average = 1.0 / size;
         int[] small = new int[size];
         int smallSize = 0;
         int[] large = new int[size];
         int largeSize = 0;

         for (int i = 0; i < size; i++) {
            if (probabilities[i] < average) {
               small[smallSize++] = i;
            } else {
               large[largeSize++] = i;
            }
         }

         double[] pr = new double[size];
         int[] al = new int[size];
         this.probabilities = pr;
         this.alias = al;

         while (largeSize != 0 && smallSize != 0) {
            int less = small[--smallSize];
            int more = large[--largeSize];
            pr[less] = probabilities[less] * size;
            al[less] = more;
            probabilities[more] += probabilities[less] - average;
            if (probabilities[more] < average) {
               small[smallSize++] = more;
            } else {
               large[largeSize++] = more;
            }
         }

         while (smallSize != 0) {
            pr[small[--smallSize]] = 1.0;
         }

         while (largeSize != 0) {
            pr[large[--largeSize]] = 1.0;
         }
      }

      @Override
      public int pickIndex(Random random) {
         int column = random.nextInt(this.probabilities.length);
         return random.nextDouble() < this.probabilities[column] ? column : this.alias[column];
      }
   }
}
