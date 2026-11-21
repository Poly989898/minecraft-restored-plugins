package io.lumine.mythic.bukkit.utils.random;

import javax.annotation.Nonnegative;

public interface Weigher<E> {
   @Nonnegative
   double weigh(E e);
}
