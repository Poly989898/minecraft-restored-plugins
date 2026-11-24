package io.lumine.mythic.lib.util;

import java.util.Objects;

public class Triplet<L, C, R> {
   private final L left;
   private final C center;
   private final R right;

   private Triplet(L left, C center, R right) {
      this.left = left;
      this.center = center;
      this.right = right;
   }

   public L getLeft() {
      return this.left;
   }

   public R getRight() {
      return this.right;
   }

   public static <L, C, R> Triplet<L, C, R> of(L left, C center, R right) {
      return new Triplet<>(left, center, right);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Triplet<?, ?, ?> triplet = (Triplet<?, ?, ?>)o;
         return Objects.equals(this.left, triplet.left) && Objects.equals(this.center, triplet.center) && Objects.equals(this.right, triplet.right);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.left, this.center, this.right);
   }
}
