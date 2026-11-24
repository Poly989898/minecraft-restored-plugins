package fr.phoenixdevt.profiles.util;

import org.jetbrains.annotations.Nullable;

public class Pair<L, R> {
   @Nullable
   private final L left;
   @Nullable
   private final R right;

   private Pair(@Nullable L left, @Nullable R right) {
      this.left = left;
      this.right = right;
   }

   @Nullable
   public L getLeft() {
      return this.left;
   }

   @Nullable
   public R getRight() {
      return this.right;
   }

   public static <L, R> Pair<L, R> of(@Nullable L left, @Nullable R right) {
      return new Pair<>(left, right);
   }
}
