package kotlin.time;

/** Compile-time stub: Kotlin's inline class Duration has hyphenated JVM names that Java source cannot spell.
 *  Underscore names are rewritten back to hyphens in the class files by tools/fixnames. This class is NOT packaged. */
public final class Duration implements Comparable<Duration> {
   public static final Companion Companion = new Companion();
   private final long rawValue;
   private Duration(long v) { this.rawValue = v; }
   public static Duration box_impl(long v) { return new Duration(v); }
   public final long unbox_impl() { return rawValue; }
   @Override public int compareTo(Duration other) { return 0; }
   public static boolean isPositive_impl(long v) { return false; }
   public static boolean isNegative_impl(long v) { return false; }
   public static boolean isInfinite_impl(long v) { return false; }
   public static boolean isFinite_impl(long v) { return false; }
   public static long getInWholeMilliseconds_impl(long v) { return 0; }
   public static long getInWholeSeconds_impl(long v) { return 0; }
   public static long getInWholeMinutes_impl(long v) { return 0; }
   public static long getInWholeNanoseconds_impl(long v) { return 0; }
   public static int compareTo_LRDsOJo(long a, long b) { return 0; }
   public static long plus_LRDsOJo(long a, long b) { return 0; }
   public static long minus_LRDsOJo(long a, long b) { return 0; }
   public static long times_UwyO8pc(long a, int b) { return 0; }
   public static long times_UwyO8pc(long a, double b) { return 0; }
   public static long div_UwyO8pc(long a, int b) { return 0; }
   public static int hashCode_impl(long v) { return 0; }
   public static String toString_impl(long v) { return ""; }
   public static boolean equals_impl0(long a, long b) { return false; }
   public static double toDouble_impl(long v, DurationUnit u) { return 0; }
   public static long toLong_impl(long v, DurationUnit u) { return 0; }
   public static int toInt_impl(long v, DurationUnit u) { return 0; }
   public static final class Companion {
      public final long getZERO_UwyO8pc() { return 0; }
      public final long getINFINITE_UwyO8pc() { return 0; }
   }
}
