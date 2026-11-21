package io.lumine.mythic.bukkit.utils.uuid;

import java.util.UUID;

public final class UndashedUUIDs {
   public static String toString(UUID uuid) {
      return digits(uuid.getMostSignificantBits() >> 32, 8)
         + digits(uuid.getMostSignificantBits() >> 16, 4)
         + digits(uuid.getMostSignificantBits(), 4)
         + digits(uuid.getLeastSignificantBits() >> 48, 4)
         + digits(uuid.getLeastSignificantBits(), 12);
   }

   private static String digits(long val, int digits) {
      long hi = 1L << digits * 4;
      return Long.toHexString(hi | val & hi - 1L).substring(1);
   }

   public static UUID fromString(String string) throws IllegalArgumentException {
      if (string.length() != 32) {
         throw new IllegalArgumentException("Invalid length " + string.length() + ": " + string);
      } else {
         try {
            return new UUID(Long.parseUnsignedLong(string.substring(0, 16), 16), Long.parseUnsignedLong(string.substring(16), 16));
         } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid uuid string: " + string, e);
         }
      }
   }

   private UndashedUUIDs() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
