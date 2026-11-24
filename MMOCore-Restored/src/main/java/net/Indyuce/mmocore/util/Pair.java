package net.Indyuce.mmocore.util;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class Pair<L, R> {
   private final L left;
   private final R right;

   private Pair(L left, R right) {
      this.left = left;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }

   @NotNull
   @Override
   public String toString() {
      return "(" + this.getLeft() + "," + this.getRight() + ")";
   }

   @NotNull
   public static <L, R> Pair<L, R> of(L left, R right) {
      return new Pair<>(left, right);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         Pair<?, ?> pair = (Pair<?, ?>)object;
         return Objects.equals(this.left, pair.left) && Objects.equals(this.right, pair.right);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.left, this.right);
   }
}
