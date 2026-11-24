package net.Indyuce.mmoitems.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Pair<K, V> {
   private final K key;
   private final V value;

   public Pair(@NotNull K value, @NotNull V value2) {
      this.key = (K)value;
      this.value = (V)value2;
   }

   public static <K, V> Pair<K, V> of(@NotNull K value, @NotNull V value2) {
      return new Pair<>((K)value, (V)value2);
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Pair pair = (Pair)obj;
         return this.key.equals(pair.key) && this.value.equals(pair.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.key, this.value);
   }
}
