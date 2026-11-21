package litebans;

import java.util.Iterator;
import org.jetbrains.annotations.Nullable;

public class IntProgression implements Iterable, KMappedMarker {
   public static final IntProgressionCompanion INT_PROGRESSION_COMPANION = new IntProgressionCompanion(null);
   private final int first;
   private final int last;
   private final int step;

   public IntProgression(int value, int value2, int value3) {
      if (value3 == 0) {
         throw new IllegalArgumentException("Step must be non-zero.");
      }

      if (value3 == Integer.MIN_VALUE) {
         throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
      }

      this.first = value;
      this.last = KotlinUtil10.computeIntValue2(value, value2, value3);
      this.step = value3;
   }

   public final int getFirst() {
      return this.first;
   }

   public final int getLast() {
      return this.last;
   }

   public final int getStep() {
      return this.step;
   }

   public KotlinIteratorImpl4 iteratorImpl() {
      return new KotlinHelper30(this.first, this.last, this.step);
   }

   public boolean isEmpty() {
      return this.step > 0 ? this.first > this.last : this.first < this.last;
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      return obj instanceof IntProgression
         && (this.isEmpty() && ((IntProgression)obj).isEmpty() || this.first == ((IntProgression)obj).first && this.last == ((IntProgression)obj).last && this.step == ((IntProgression)obj).step);
   }

   @Override
   public int hashCode() {
      return this.isEmpty() ? -1 : 31 * (31 * this.first + this.last) + this.step;
   }

   @Override
   public String toString() {
      return this.step > 0 ? this.first + ".." + this.last + " step " + this.step : this.first + " downTo " + this.last + " step " + -this.step;
   }

   @Override
   public Iterator iterator() {
      return this.iteratorImpl();
   }
}
