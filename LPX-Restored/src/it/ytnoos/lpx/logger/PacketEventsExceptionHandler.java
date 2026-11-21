package it.ytnoos.lpx.logger;

import it.ytnoos.lpx.LPXBukkit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PacketEventsExceptionHandler implements BiConsumer<String, Throwable> {
   private static final long REPORT_COOLDOWN = 10000L;
   private static final Pattern PLAYER_PATTERN = Pattern.compile("(?:from|to) ([^ ]+) \\(");
   private static final String UNKNOWN_PLAYER = "an unknown player";
   private final LPXBukkit lpxBukkit;
   private final Map<String, Long> lastReports = new ConcurrentHashMap<>();

   public PacketEventsExceptionHandler(LPXBukkit lpxBukkit2) {
      this.lpxBukkit = lpxBukkit2;
   }

   public void accept(String string, Throwable throwable) {
      boolean flag = this.lpxBukkit.getConfigManager().getOptionsConfig().isKickOnException();
      this.lpxBukkit.getAlertManager().getLogWriter().execute(() -> this.handleAsync(string, throwable, flag));
   }

   private void handleAsync(String string, Throwable throwable, boolean flag) {
      String string2 = buildMessage(string, throwable, flag);
      if (flag) {
         this.lpxBukkit.getLogger().warning(string2);
         this.lpxBukkit.getAlertManager().getLogWriter().writeError(() -> "[PacketEvents] " + string2, throwable);
      } else {
         String string3 = getPlayerName(string);
         long longValue = System.currentTimeMillis();
         if (shouldLog(this.lastReports, string3, longValue)) {
            this.lpxBukkit.getLogger().warning(string2);
            this.lpxBukkit.getAlertManager().getLogWriter().writeError(() -> "[PacketEvents] " + string2, throwable);
         }
      }
   }

   static String buildMessage(String string, Throwable throwable, boolean flag) {
      String string2 = extractPlayerName(string);
      String string3 = extractReason(string, throwable);
      return flag
         ? "LPX blocked an invalid packet from " + string2 + " and disconnected the player for safety. Reason: " + string3 + ". Full stacktrace saved in LPX logs."
         : "LPX blocked an invalid packet from "
            + string2
            + ". The player was not disconnected because kick-on-exception is disabled. Reason: "
            + string3
            + ". Full stacktrace saved in LPX logs; throttling repeated messages for this player for 10 seconds.";
   }

   static String getPlayerName(String string) {
      return extractPlayerName(string);
   }

   public static boolean shouldLog(Map map, String string, long longValue) {
      Long long2 = (Long)map.get(string);
      if (long2 != null && longValue - long2 < 10000L) {
         return false;
      }

      map.put(string, longValue);
      return true;
   }

   private static String extractPlayerName(String string) {
      Matcher matcher = PLAYER_PATTERN.matcher(string);
      return matcher.find() ? matcher.group(1) : "an unknown player";
   }

   private static String extractReason(String string, Throwable throwable) {
      int value = string.lastIndexOf("): ");
      if (value != -1) {
         String string2 = string.substring(value + 3).trim();
         if (!string2.isEmpty()) {
            return string2;
         }
      }

      Throwable throwable2 = throwable;

      while (throwable2.getCause() != null) {
         throwable2 = throwable2.getCause();
      }

      String string3 = throwable2.getMessage();
      return string3 != null && !string3.trim().isEmpty() ? string3 : throwable2.getClass().getSimpleName();
   }

}
