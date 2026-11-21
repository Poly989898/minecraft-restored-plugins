package io.lumine.mythic.bukkit.utils.metadata;

public class Pair<T, U> {
   private final T key;
   private final U value;

   public static final <T, U> Pair<T, U> of(T key, U value) {
      return new Pair<>(key, value);
   }

   private Pair(T key, U value) {
      this.key = key;
      this.value = value;
   }

   public T getKey() {
      return this.key;
   }

   public U getValue() {
      return this.value;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Pair<?, ?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$key = this.getKey();
         Object other$key = other.getKey();
         if (this$key == null ? other$key == null : this$key.equals(other$key)) {
            Object this$value = this.getValue();
            Object other$value = other.getValue();
            return this$value == null ? other$value == null : this$value.equals(other$value);
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Pair;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $key = this.getKey();
      result = result * 59 + ($key == null ? 43 : $key.hashCode());
      Object $value = this.getValue();
      return result * 59 + ($value == null ? 43 : $value.hashCode());
   }

   @Override
   public String toString() {
      return "Pair(key=" + this.getKey() + ", value=" + this.getValue() + ")";
   }
}
