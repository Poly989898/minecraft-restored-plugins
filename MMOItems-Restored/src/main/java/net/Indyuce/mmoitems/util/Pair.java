package net.Indyuce.mmoitems.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Pair<K, V> {
   private final K key;
   private final V value;

   public Pair(@NotNull K var1, @NotNull V var2) {
      this.key = (K)var1;
      this.value = (V)var2;
   }

   public static <K, V> Pair<K, V> of(@NotNull K var0, @NotNull V var1) {
      return new Pair<>((K)var0, (V)var1);
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Pair var2 = (Pair)var1;
         return this.key.equals(var2.key) && this.value.equals(var2.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key, this.value);
   }
}
