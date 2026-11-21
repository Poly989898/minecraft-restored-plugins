package litebans;

import org.jetbrains.annotations.Nullable;

public final class KotlinIntProgressionSub extends IntProgression implements KotlinAInterface4, KotlinAInterface2 {
   public static final KotlinIntProgressionSubCompanion KOTLIN_INT_PROGRESSION_SUB_COMPANION = new KotlinIntProgressionSubCompanion(null);
   private static final KotlinIntProgressionSub return3 = new KotlinIntProgressionSub(1, 0);

   public KotlinIntProgressionSub(int value, int value2) {
      super(value, value2, 1);
   }

   public Integer obtainInteger() {
      return this.getFirst();
   }

   public Integer obtainInteger2() {
      return this.getLast();
   }

   @Override
   public boolean isEmpty() {
      return this.getFirst() > this.getLast();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return obj instanceof KotlinIntProgressionSub && (this.isEmpty() && ((KotlinIntProgressionSub)obj).isEmpty() || this.getFirst() == ((KotlinIntProgressionSub)obj).getFirst() && this.getLast() == ((KotlinIntProgressionSub)obj).getLast());
   }

   @Override
   public int hashCode() {
      return this.isEmpty() ? -1 : 31 * this.getFirst() + this.getLast();
   }

   @Override
   public String toString() {
      return this.getFirst() + ".." + this.getLast();
   }

   public static final KotlinIntProgressionSub obtainKotlinIntProgressionSub2() {
      return return3;
   }
}
