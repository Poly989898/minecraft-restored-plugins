package io.lumine.mythic.bukkit.utils.version;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class MinecraftVersion implements Comparable<MinecraftVersion> {
   private final int major;
   private final int minor;
   private final int build;

   public static MinecraftVersion getRuntimeVersion() {
      return MinecraftVersions.RUNTIME_VERSION;
   }

   public static MinecraftVersion of(int major, int minor, int build) {
      return new MinecraftVersion(major, minor, build);
   }

   public static MinecraftVersion parse(String version) throws IllegalArgumentException {
      String[] parts = version.split("-");

      int[] versionComponents;
      try {
         versionComponents = parseVersion(parts[0]);
      } catch (IllegalStateException e) {
         return of(1, 0, 0);
      }

      int major = versionComponents[0];
      int minor = versionComponents[1];
      int build = versionComponents[2];
      return of(major, minor, build);
   }

   private static int[] parseVersion(String version) {
      String[] elements = version.split("\\.");
      int[] numbers = new int[3];
      if (elements.length < 1) {
         throw new IllegalStateException("Corrupt MC version: " + version);
      } else {
         for (int i = 0; i < Math.min(numbers.length, elements.length); i++) {
            numbers[i] = Integer.parseInt(elements[i].trim());
         }

         return numbers;
      }
   }

   private MinecraftVersion(int major, int minor, int build) {
      this.major = major;
      this.minor = minor;
      this.build = build;
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public int getBuild() {
      return this.build;
   }

   @Nonnull
   public String getVersion() {
      return String.format("%s.%s.%s", this.getMajor(), this.getMinor(), this.getBuild());
   }

   public int compareTo(MinecraftVersion that) {
      if (this == that) {
         return 0;
      } else if (that == null) {
         return 1;
      } else {
         int c = Integer.compare(this.major, that.major);
         if (c != 0) {
            return c;
         } else {
            c = Integer.compare(this.minor, that.minor);
            return c != 0 ? c : Integer.compare(this.build, that.build);
         }
      }
   }

   public boolean isAfter(MinecraftVersion other) {
      return this.compareTo(other) > 0;
   }

   public boolean isAfterOrEq(MinecraftVersion other) {
      return this.compareTo(other) >= 0;
   }

   public boolean isBefore(MinecraftVersion other) {
      return this.compareTo(other) < 0;
   }

   public boolean isBeforeOrEq(MinecraftVersion other) {
      return this.compareTo(other) <= 0;
   }

   public boolean isBetween(MinecraftVersion o1, MinecraftVersion o2) {
      return this.isAfterOrEq(o1) && this.isBeforeOrEq(o2) || this.isBeforeOrEq(o1) && this.isAfterOrEq(o2);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else {
         return !(obj instanceof MinecraftVersion other)
            ? false
            : this.getMajor() == other.getMajor() && this.getMinor() == other.getMinor() && this.getBuild() == other.getBuild();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getMajor(), this.getMinor(), this.getBuild());
   }

   @Override
   public String toString() {
      return String.format("(MC: %s)", this.getVersion());
   }
}
