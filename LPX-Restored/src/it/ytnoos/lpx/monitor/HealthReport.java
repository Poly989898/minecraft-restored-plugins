package it.ytnoos.lpx.monitor;

import it.ytnoos.lpx.LPXBukkit;
import it.ytnoos.lpx.config.messages.MessagesConfig;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import org.bukkit.ChatColor;

public class HealthReport {
   private final DecimalFormat df = new DecimalFormat("#0");
   private final String mainColor;
   private final String secondColor;

   public HealthReport(LPXBukkit lpxBukkit) {
      MessagesConfig messagesConfig = lpxBukkit.getConfigManager().getMessagesConfig();
      this.mainColor = messagesConfig.getMainColor();
      this.secondColor = messagesConfig.getSecondColor();
   }

   public String generate(PacketTickMonitor packetTickMonitor) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(this.mainColor)
         .append("Packet Tick Delay Report ")
         .append(this.secondColor)
         .append("min")
         .append(ChatColor.GRAY)
         .append("/")
         .append(this.secondColor)
         .append("med")
         .append(ChatColor.GRAY)
         .append("/")
         .append(this.secondColor)
         .append("95%ile")
         .append(ChatColor.GRAY)
         .append("/")
         .append(this.secondColor)
         .append("max\n");
      this.appendLine(stringBuilder, "10 seconds", packetTickMonitor.getMin10s(), packetTickMonitor.getMedian10s(), packetTickMonitor.getP95At10s(), packetTickMonitor.getMax10s());
      this.appendLine(stringBuilder, "minute", packetTickMonitor.getMin1m(), packetTickMonitor.getMedian1m(), packetTickMonitor.getP95At1m(), packetTickMonitor.getMax1m());
      this.appendLine(stringBuilder, "5 minutes", packetTickMonitor.getMin5m(), packetTickMonitor.getMedian5m(), packetTickMonitor.getP95At5m(), packetTickMonitor.getMax5m());
      stringBuilder.append("\n \n");
      stringBuilder.append(this.mainColor).append("General status: ");
      double number = packetTickMonitor.getHighPercentile10s().doubleValue();
      stringBuilder.append(this.getStatus(number));
      stringBuilder.append(ChatColor.GRAY).append(" (90%ile: ").append(this.format(number)).append(")\n");
      Map map = packetTickMonitor.getThreadPlayers();
      if (!map.isEmpty()) {
         stringBuilder.append("\n");
         stringBuilder.append(this.mainColor).append("Netty Threads:\n");
         ArrayList arrayList = new ArrayList(map.keySet());
         Collections.sort(arrayList);

         for (Object var47_o : arrayList) { Integer integer = (Integer) var47_o;
            PacketTickMonitor.Sample sample = (PacketTickMonitor.Sample)map.get(integer);
            double number2 = sample.getValue().doubleValue();
            int value = sample.getPlayerCount();
            stringBuilder.append(ChatColor.GRAY).append("[").append(integer + 1).append("] ").append(this.getStatus(number2)).append(" ").append(ChatColor.GRAY);
            stringBuilder.append("(").append(value).append(" ").append(value == 1 ? "player" : "players").append(")");
            if (this.isCritical(number2)) {
               stringBuilder.append(" (90%ile: ").append(this.format(number2)).append(") ");
            }

            stringBuilder.append("\n");
         }
      }

      stringBuilder.append("\n");
      return stringBuilder.toString();
   }

   private void appendLine(StringBuilder stringBuilder, String string, BigDecimal bigDecimal, BigDecimal bigDecimal2, BigDecimal bigDecimal3, BigDecimal bigDecimal4) {
      stringBuilder.append(this.secondColor).append("last ").append(string).append(" ");
      if (bigDecimal != null) {
         double number = bigDecimal.doubleValue();
         stringBuilder.append(this.getColor(number)).append(this.format(number)).append(ChatColor.GRAY).append("/");
      } else {
         stringBuilder.append(ChatColor.GRAY).append("?/");
      }

      if (bigDecimal2 != null) {
         double number2 = bigDecimal2.doubleValue();
         stringBuilder.append(this.getColor(number2)).append(this.format(number2)).append(ChatColor.GRAY).append("/");
      } else {
         stringBuilder.append(ChatColor.GRAY).append("?/");
      }

      if (bigDecimal3 != null) {
         double number3 = bigDecimal3.doubleValue();
         stringBuilder.append(this.getColor(number3)).append(this.format(number3)).append(ChatColor.GRAY).append("/");
      } else {
         stringBuilder.append(ChatColor.GRAY).append("?/");
      }

      if (bigDecimal4 != null) {
         double number4 = bigDecimal4.doubleValue();
         stringBuilder.append(this.getColor(number4)).append(this.format(number4));
      } else {
         stringBuilder.append(ChatColor.GRAY).append("?");
      }

      stringBuilder.append("\n");
   }

   private String format(double number) {
      return this.df.format(number);
   }

   private String getStatus(double number) {
      if (number == 0.0) {
         return ChatColor.GREEN + "HEALTHY :D";
      } else if (number <= 1.0) {
         return ChatColor.YELLOW + "UNSTABLE :/";
      } else {
         return number <= 2.0 ? ChatColor.RED + "BAD :(" : ChatColor.DARK_RED + "VERY BAD D:";
      }
   }

   private boolean isCritical(double number) {
      return number > 2.0;
   }

   private ChatColor getColor(double number) {
      if (number <= 1.0) {
         return ChatColor.GREEN;
      } else if (number <= 2.0) {
         return ChatColor.YELLOW;
      } else if (number <= 3.0) {
         return ChatColor.GOLD;
      } else {
         return number <= 5.0 ? ChatColor.RED : ChatColor.DARK_RED;
      }
   }

}
