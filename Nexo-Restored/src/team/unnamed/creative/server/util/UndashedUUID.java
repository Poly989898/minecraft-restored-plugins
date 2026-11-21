package team.unnamed.creative.server.util;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class UndashedUUID {
   private static final int HEX_RADIX = 16;

   private UndashedUUID() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }

   public static @NotNull UUID fromUndashedString(@NotNull String s) {
      if (s.length() != 32) {
         throw new IllegalArgumentException("Undashed string must be 32 characters long");
      } else {
         long msb = parseUnsignedHexLong(s.substring(0, 16));
         long lsb = parseUnsignedHexLong(s.substring(16));
         return new UUID(msb, lsb);
      }
   }

   private static long parseUnsignedHexLong(@NotNull String s) {
      int len = s.length();
      if (s.charAt(0) == '-') {
         throw new IllegalArgumentException("Illegal leading minus sign on unsigned string " + s);
      } else {
         long first = Long.parseLong(s.substring(0, len - 1), 16);
         int second = Character.digit(s.charAt(len - 1), 16);
         if (second < 0) {
            throw new IllegalArgumentException("Bad digit at end of " + s);
         } else {
            long result = first * 16L + (long)second;
            int guard = 16 * (int)(first >>> 57);
            if (guard < 128 && (result < 0L || guard < 92)) {
               return result;
            } else {
               throw new IllegalArgumentException("String value " + s + " exceeds range of unsigned long");
            }
         }
      }
   }
}
