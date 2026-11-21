package team.unnamed.creative.metadata.pack;

import org.jetbrains.annotations.NotNull;

public record FormatVersion(int major, int minor) implements Comparable<FormatVersion> {
   public static final int FORMAT_1_21_1 = 34;
   public static final int FORMAT_1_21_3 = 42;
   public static final int FORMAT_1_21_4 = 46;
   public static final int FORMAT_1_21_5 = 55;
   public static final int FORMAT_1_21_6 = 63;
   public static final int FORMAT_1_21_10 = 69;
   public static final int FORMAT_1_21_11 = 75;
   public static final int FORMAT_26_1 = 84;
   public static final int FORMAT_26_2 = 88;

   public FormatVersion(int major, int minor) {
      if (major >= 0 && minor >= 0) {
         this.major = major;
         this.minor = minor;
      } else {
         throw new IllegalArgumentException("PackVersion cannot be negative: " + major + "." + minor);
      }
   }

   public static @NotNull FormatVersion of(int major) {
      return new FormatVersion(major, 0);
   }

   public static @NotNull FormatVersion of(int major, int minor) {
      return new FormatVersion(major, minor);
   }

   public static @NotNull FormatVersion parse(@NotNull String value) {
      String[] parts = value.trim().split("\\.");
      if (parts.length == 1) {
         return of(Integer.parseInt(parts[0]));
      } else if (parts.length == 2) {
         return of(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
      } else {
         throw new IllegalArgumentException("Invalid pack version: " + value);
      }
   }

   public static @NotNull FormatVersion min(@NotNull FormatVersion a, @NotNull FormatVersion b) {
      return a.compareTo(b) <= 0 ? a : b;
   }

   public static @NotNull FormatVersion max(@NotNull FormatVersion a, @NotNull FormatVersion b) {
      return a.compareTo(b) >= 0 ? a : b;
   }

   public int compareTo(@NotNull FormatVersion o) {
      int c = Integer.compare(this.major, o.major);
      return c != 0 ? c : Integer.compare(this.minor, o.minor);
   }

   public @NotNull String toString() {
      return this.minor == 0 ? Integer.toString(this.major) : this.major + "." + this.minor;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof FormatVersion)) {
         return false;
      } else {
         FormatVersion that = (FormatVersion)o;
         return this.major == that.major && this.minor == that.minor;
      }
   }
}
