package io.lumine.mythic.bukkit.utils.random;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public interface RandomSelector<E> {
   static <E> RandomSelector<E> uniform(Collection<E> elements) {
      return RandomSelectorImpl.uniform(elements);
   }

   static <E extends Weighted> RandomSelector<E> weighted(Collection<E> elements) {
      return weighted(elements, Weighted.WEIGHER);
   }

   static <E> RandomSelector<E> weighted(Collection<E> elements, Weigher<? super E> weigher) {
      return RandomSelectorImpl.weighted(elements, weigher);
   }

   E pick(Random random);

   default E pick() {
      return this.pick(ThreadLocalRandom.current());
   }

   Stream<E> stream(Random random);

   default Stream<E> stream() {
      return this.stream(ThreadLocalRandom.current());
   }
}
