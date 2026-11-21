package io.lumine.mythic.bukkit.utils.random;

import javax.annotation.Nonnegative;

public interface Weighted {
   Weigher<? super Weighted> WEIGHER = Weighted::getWeight;

   @Nonnegative
   double getWeight();
}
