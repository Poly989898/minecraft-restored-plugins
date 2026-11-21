package it.ytnoos.lpx.common.utils;

public class TimeUtils {
   private TimeUtils() {
   }

   public static long difference(long longValue) {
      return current() - longValue;
   }

   public static long current() {
      return System.currentTimeMillis();
   }
}
