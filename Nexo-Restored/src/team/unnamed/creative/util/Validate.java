package team.unnamed.creative.util;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

/** @deprecated */
@Deprecated
@Internal
@ScheduledForRemoval(
   inVersion = "2.0.0"
)
public final class Validate {
   private Validate() {
   }

   public static <T> T isNotNull(T object, String message, Object... parameters) {
      if (object == null) {
         throw new NullPointerException(String.format(message, parameters));
      } else {
         return object;
      }
   }

   public static <T> T isNotNull(T object) {
      if (object == null) {
         throw new NullPointerException();
      } else {
         return object;
      }
   }

   public static void isState(boolean expression, String message, Object... parameters) {
      if (!expression) {
         throw new IllegalStateException(String.format(message, parameters));
      }
   }

   public static void isState(boolean expression) {
      if (!expression) {
         throw new IllegalStateException();
      }
   }

   public static void isTrue(boolean expression, String message, Object... parameters) {
      if (!expression) {
         throw new IllegalArgumentException(String.format(message, parameters));
      }
   }

   public static void isTrue(boolean expression) {
      if (!expression) {
         throw new IllegalArgumentException();
      }
   }

   public static String isNotEmpty(String string, String message, Object... parameters) {
      if (string == null) {
         throw new NullPointerException(String.format(message, parameters));
      } else if (string.isEmpty()) {
         throw new IllegalArgumentException(String.format(message, parameters));
      } else {
         return string;
      }
   }

   public static String isNotEmpty(String string) {
      if (string == null) {
         throw new NullPointerException("Null string");
      } else if (string.isEmpty()) {
         throw new IllegalArgumentException("Empty string");
      } else {
         return string;
      }
   }
}
