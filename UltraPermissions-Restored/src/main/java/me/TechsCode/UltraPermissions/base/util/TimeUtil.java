package me.TechsCode.UltraPermissions.base.util;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import me.TechsCode.UltraPermissions.base.debug.TechException;

public final class TimeUtil {
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
   private static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
   private static final DateFormat DATE_FORMAT_MONTH = new SimpleDateFormat("dd.MM HH:mm");
   private static final Pattern TOKEN_PATTERN = Pattern.compile(
      "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?",
      2
   );

   public static long currentTimeSeconds() {
      return System.currentTimeMillis() / 1000L;
   }

   public static long currentTimeTicks() {
      return System.currentTimeMillis() / 50L;
   }

   public static String getFormattedDate() {
      return getFormattedDate(System.currentTimeMillis());
   }

   public static String getFormattedDate(long millis) {
      return DATE_FORMAT.format(millis);
   }

   public static String getFormattedDateShort() {
      return DATE_FORMAT_SHORT.format(System.currentTimeMillis());
   }

   public static String getFormattedDateShort(long millis) {
      return DATE_FORMAT_SHORT.format(millis);
   }

   public static String getFormattedDateMonth(long millis) {
      return DATE_FORMAT_MONTH.format(millis);
   }

   public static long toTicks(String text) {
      if (text == null) {
         throw new TechException("Time is null");
      }

      long ticks = 0L;
      String[] parts = text.split(" ");
      if (parts.length < 2) {
         throw new IllegalArgumentException("Expected human readable time like '1 second', got '" + text + "' instead");
      }

      for (int i = 1; i < parts.length; i++) {
         String unit = parts[i].toLowerCase();
         int amount = 0;
         long unitSeconds = 0L;
         boolean isTicks = false;

         try {
            amount = Integer.parseInt(parts[i - 1]);
         } catch (NumberFormatException exception) {
            continue;
         }

         if (unit.startsWith("tick")) {
            isTicks = true;
         } else if (unit.startsWith("second")) {
            unitSeconds = 1L;
         } else if (unit.startsWith("minute")) {
            unitSeconds = 60L;
         } else if (unit.startsWith("hour")) {
            unitSeconds = 3600L;
         } else if (unit.startsWith("day")) {
            unitSeconds = 86400L;
         } else if (unit.startsWith("week")) {
            unitSeconds = 604800L;
         } else if (unit.startsWith("month")) {
            unitSeconds = 2629743L;
         } else if (unit.startsWith("year")) {
            unitSeconds = 31556926L;
         } else {
            if (!unit.startsWith("potato")) {
               throw new IllegalArgumentException("Must define date type! Example: '1 second' (Got '" + unit + "')");
            }

            unitSeconds = 1337L;
         }

         ticks += amount * (isTicks ? 1L : unitSeconds * 20L);
      }

      return ticks;
   }

   public static String formatTimeGeneric(long totalSeconds) {
      long seconds = totalSeconds % 60L;
      long totalMinutes = totalSeconds / 60L;
      String result = "";
      if (totalMinutes >= 60L) {
         long hours = totalSeconds / 60L / 60L;
         totalMinutes %= 60L;
         result = hours + (hours == 1L ? " hour" : " hours") + " ";
      }

      return result
         + (totalMinutes != 0L ? totalMinutes : "")
         + (totalMinutes > 0L ? (totalMinutes == 1L ? " minute" : " minutes") + " " : "")
         + Long.parseLong(String.valueOf(seconds))
         + (Long.parseLong(String.valueOf(seconds)) == 1L ? " second" : " seconds");
   }

   public static String formatTimeDays(long totalSeconds) {
      long totalMinutes = totalSeconds / 60L;
      long totalHours = totalMinutes / 60L;
      long days = totalHours / 24L;
      return days + " days " + totalHours % 24L + " hours " + totalMinutes % 60L + " minutes " + totalSeconds % 60L + " seconds";
   }

   public static String formatTimeShort(long totalSeconds) {
      long totalMinutes = totalSeconds / 60L;
      long totalHours = totalMinutes / 60L;
      long days = totalHours / 24L;
      totalHours %= 24L;
      totalMinutes %= 60L;
      totalSeconds %= 60L;
      return (days > 0L ? days + "d " : "") + (totalHours > 0L ? totalHours + "h " : "") + (totalMinutes > 0L ? totalMinutes + "m " : "") + totalSeconds + "s";
   }

   public static String formatTimeColon(long totalSeconds) {
      long totalMinutes = totalSeconds / 60L;
      long totalHours = totalMinutes / 60L;
      long days = totalHours / 24L;
      totalHours %= 24L;
      totalMinutes %= 60L;
      totalSeconds %= 60L;
      return (days > 0L ? (days < 10L ? "0" : "") + days + ":" : "")
         + (totalHours > 0L ? (totalHours < 10L ? "0" : "") + totalHours + ":" : "")
         + (totalMinutes > 0L ? (totalMinutes < 10L ? "0" : "") + totalMinutes + ":" : "00")
         + (totalSeconds < 10L ? "0" : "")
         + totalSeconds;
   }

   public static long parseToken(String text) {
      Matcher matcher = TOKEN_PATTERN.matcher(text);
      long years = 0L;
      long months = 0L;
      long weeks = 0L;
      long days = 0L;
      long hours = 0L;
      long minutes = 0L;
      long seconds = 0L;
      boolean found = false;

      while (matcher.find()) {
         if (matcher.group() != null && !matcher.group().isEmpty()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
               if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                  found = true;
                  break;
               }
            }

            if (found) {
               for (int group = 1; group < 8; group++) {
                  if (matcher.group(group) != null && !matcher.group(group).isEmpty()) {
                     long amount = Long.parseLong(matcher.group(group));
                     if (group == 1) {
                        checkLimit("years", amount, 10);
                        years = amount;
                     } else if (group == 2) {
                        checkLimit("months", amount, 1200);
                        months = amount;
                     } else if (group == 3) {
                        checkLimit("weeks", amount, 400);
                        weeks = amount;
                     } else if (group == 4) {
                        checkLimit("days", amount, 3100);
                        days = amount;
                     } else if (group == 5) {
                        checkLimit("hours", amount, 2400);
                        hours = amount;
                     } else if (group == 6) {
                        checkLimit("minutes", amount, 6000);
                        minutes = amount;
                     } else if (group == 7) {
                        checkLimit("seconds", amount, 6000);
                        seconds = amount;
                     }
                  }
               }
               break;
            }
         }
      }

      if (!found) {
         throw new NumberFormatException("Date not found from: " + text);
      } else {
         return (seconds + minutes * 60L + hours * 3600L + days * 86400L + weeks * 7L * 86400L + months * 30L * 86400L + years * 365L * 86400L) * 1000L;
      }
   }

   private static void checkLimit(String unit, long amount, int limit) {
      if (amount > limit) {
         throw new IllegalArgumentException("Value type " + unit + " is out of bounds! Max limit: " + limit + ", given: " + amount);
      }
   }

   public static String toSQLTimestamp() {
      return toSQLTimestamp(System.currentTimeMillis());
   }

   public static String toSQLTimestamp(long millis) {
      Date date = new Date(millis);
      return new Timestamp(date.getTime()).toString();
   }

   public static long fromSQLTimestamp(String text) {
      return Timestamp.valueOf(text).getTime();
   }

   @Generated
   private TimeUtil() {
   }
}
