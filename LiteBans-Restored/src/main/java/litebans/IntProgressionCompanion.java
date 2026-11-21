package litebans;

public final class IntProgressionCompanion {
   private IntProgressionCompanion() {
   }

   public final IntProgression computeIntProgression(int value, int value2, int value3) {
      return new IntProgression(value, value2, value3);
   }

   public IntProgressionCompanion(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
