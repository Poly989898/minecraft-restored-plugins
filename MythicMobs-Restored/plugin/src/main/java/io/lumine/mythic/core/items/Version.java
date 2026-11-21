package io.lumine.mythic.core.items;

public class Version {
   private final int major;
   private final int minor;
   private final int patch;

   public Version(int major, int minor, int patch) {
      if (major < 0 || minor < 0 || patch < 0) {
         throw new IllegalArgumentException("Version numbers cannot be negative.");
      } else if (major <= 255 && minor <= 255 && patch <= 255) {
         this.major = major;
         this.minor = minor;
         this.patch = patch;
      } else {
         throw new IllegalArgumentException("Version numbers cannot exceed 255.");
      }
   }

   public Version(int patch) {
      this(0, 0, patch);
   }

   public int getMajor() {
      return this.major;
   }

   public int getMinor() {
      return this.minor;
   }

   public int getPatch() {
      return this.patch;
   }

   public boolean hasVersion() {
      return this.major > 0 || this.minor > 0 || this.patch > 0;
   }

   public int getNumeralVersion() {
      return this.major << 16 | this.minor << 8 | this.patch;
   }

   public static Version parse(String version) {
      if (version != null && !version.isEmpty()) {
         String[] parts = version.split("\\.");
         int patch = 0;
         int minor = 0;
         int major = 0;
         if (parts.length == 1) {
            patch = Integer.parseInt(parts[0]);
         } else if (parts.length == 2) {
            minor = Integer.parseInt(parts[0]);
            patch = Integer.parseInt(parts[1]);
         } else if (parts.length >= 3) {
            major = Integer.parseInt(parts[0]);
            minor = Integer.parseInt(parts[1]);
            patch = Integer.parseInt(parts[2]);
         }

         return new Version(major, minor, patch);
      } else {
         throw new IllegalArgumentException("Version string cannot be null or empty.");
      }
   }

   public boolean isUpToDate(int patch) {
      if (this.major > 0) {
         return false;
      } else {
         return this.minor > 0 ? false : patch >= this.patch;
      }
   }

   public boolean isUpToDate(Version target) {
      if (this.major != target.major) {
         return this.major >= target.major;
      } else {
         return this.minor != target.minor ? this.minor >= target.minor : this.patch >= target.patch;
      }
   }
}
