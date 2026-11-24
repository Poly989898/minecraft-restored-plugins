package me.TechsCode.UltraPermissions.base.legacy;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.translations.Words;
import me.TechsCode.UltraPermissions.base.visual.Text;

public class Tools {
   public Tools() {
   }

   public static String getNumberString(long value) {
      DecimalFormat decimalFormat = new DecimalFormat("#,###");
      return decimalFormat.format(value);
   }

   public static String getNumberString(double value) {
      DecimalFormat decimalFormat = new DecimalFormat("#,###");
      return decimalFormat.format(value);
   }

   public static String getNumberString(float value) {
      DecimalFormat decimalFormat = new DecimalFormat("#,###");
      return decimalFormat.format(value);
   }

   public static String readableFileSize(long value) {
      if (value <= 0L) {
         return "0";
      }

      String[] texts = new String[]{"B", "kB", "MB", "GB", "TB"};
      int index = (int)(Math.log10(value) / Math.log10(1024.0));
      return new DecimalFormat("#,##0.#").format(value / Math.pow(1024.0, index)) + " " + texts[index];
   }

   public static String[] splitCamelCase(String text) {
      return text.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
   }

   public static String firstUpperCase(String text) {
      return text.substring(0, 1).toUpperCase() + text.substring(1);
   }

   public static String[] lineSplitter(String text, int count) {
      ArrayList<String> texts = new ArrayList<>();
      String line = "";

      for (String word : text.split(" ")) {
         line = line.trim();
         if (line.length() + word.length() > count) {
            texts.add(line);
            line = "";
         }

         line = line + " " + word;
      }

      line = line.trim();
      if (line.length() != 0) {
         texts.add(line);
      }

      return texts.toArray(new String[0]);
   }

   public static String removeEofsAndLineBreakers(String text) {
      if (text != null && text.length() != 0) {
         int length = text.length();
         char[] chars = new char[length];
         int count = 0;
         int whitespaceRun = 0;
         boolean flag = true;

         for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            boolean isWhitespace = Character.isWhitespace(ch);
            if (!isWhitespace) {
               flag = false;
               chars[count++] = ch == 160 ? ' ' : ch;
               whitespaceRun = 0;
            } else {
               if (whitespaceRun == 0 && !flag) {
                  chars[count++] = " ".charAt(0);
               }

               whitespaceRun++;
            }
         }

         return flag ? "" : new String(chars, 0, count - (whitespaceRun > 0 ? 1 : 0)).trim();
      } else {
         return text;
      }
   }

   public static String getEnumName(Enum<?> enumValue) {
      return String.join(
         " ",
         Arrays.stream(enumValue.name().split("_"))
            .map(text -> text.toUpperCase().charAt(0) + text.toLowerCase().substring(1))
            .collect(Collectors.toCollection(ArrayList::new))
      );
   }

   public static String getSecretPassword(String text) {
      return String.join("", Collections.nCopies(text.length(), "*"));
   }

   public static String getFormattedAgoString(long value, TimeUnit timeUnit) {
      value = timeUnit.toSeconds(value);
      if (value < 1L) {
         return "Now";
      } else if (value > TimeUnit.DAYS.toSeconds(1L)) {
         return getTimeString(TimeUnit.SECONDS.toDays(value), TimeUnit.DAYS);
      } else if (value > TimeUnit.HOURS.toSeconds(1L)) {
         return getTimeString(TimeUnit.SECONDS.toHours(value), TimeUnit.HOURS);
      } else {
         return value > TimeUnit.MINUTES.toSeconds(1L)
            ? getTimeString(TimeUnit.SECONDS.toMinutes(value), TimeUnit.MINUTES)
            : getTimeString(TimeUnit.SECONDS.toSeconds(value), TimeUnit.SECONDS);
      }
   }

   public static String getTicksString(long value) {
      return value == 0L ? value + " Ticks" : value + " Tick";
   }

   public static String getTimeString(long value) {
      return getTimeString(value, TimeUnit.SECONDS);
   }

   public static String getTimeString(long value, TimeUnit timeUnit) {
      return getTimeString(value, timeUnit, 0);
   }

   public static String getTimeString(long value, TimeUnit timeUnit, int count) {
      value = timeUnit.toSeconds(value);
      if (value < 1L) {
         return "Now";
      }

      long days = TimeUnit.SECONDS.toDays(value);
      value -= TimeUnit.DAYS.toSeconds(days);
      long hours = TimeUnit.SECONDS.toHours(value);
      value -= TimeUnit.HOURS.toSeconds(hours);
      long minutes = TimeUnit.SECONDS.toMinutes(value);
      value -= TimeUnit.MINUTES.toSeconds(minutes);
      long seconds = TimeUnit.SECONDS.toSeconds(value);
      ArrayList list = new ArrayList();
      if (days != 0L) {
         Phrase phrase = days == 1L ? Words.DAY : Words.DAYS;
         list.add(days + " " + phrase.firstUpper());
      }

      if (hours != 0L) {
         Phrase phrase = hours == 1L ? Words.HOUR : Words.HOURS;
         list.add(hours + " " + phrase.firstUpper());
      }

      if (minutes != 0L) {
         Phrase phrase = minutes == 1L ? Words.MINUTE : Words.MINUTES;
         list.add(minutes + " " + phrase.firstUpper());
      }

      if (seconds != 0L) {
         Phrase phrase = seconds == 1L ? Words.SECOND : Words.SECONDS;
         list.add(seconds + " " + phrase.firstUpper());
      }

      if (count != 0) {
         while (list.size() > count) {
            list.remove(list.size() - 1);
         }
      }

      return String.join(", ", list);
   }

   public static File exportFile(String text, boolean flag, SpigotTechPlugin spigotTechPlugin) {
      File file = new File(spigotTechPlugin.getPluginFolder() + "/" + text);
      if (flag || !file.exists()) {
         spigotTechPlugin.getBootstrap().saveResource(text, flag);
      }

      return file;
   }

   public static String getProgressBar(int count, int total, int length, String text, String filledColor, String emptyColor) {
      float value = (float)count / total;
      int filled = (int)(length * value);
      int empty = length - filled;
      StringBuilder builder = new StringBuilder();
      builder.append(Text.chatColor(filledColor));
      IntStream.range(0, filled).mapToObj(index -> text).forEach(builder::append);
      builder.append(Text.chatColor(emptyColor));
      IntStream.range(0, empty).mapToObj(index -> text).forEach(builder::append);
      return builder.toString();
   }

   public static long getTimeSecondsFromString(String text) {
      return Arrays.stream(text.split(" ")).mapToLong(Tools::getTimeFromWord).sum();
   }

   private static long getTimeFromWord(String text) {
      try {
         if (text.length() < 2) {
            return 0L;
         }

         String unitChar = text.toCharArray()[text.length() - 1] + "";
         int count = Integer.parseInt(text.substring(0, text.length() - 1));
         TimeUnit[] timeUnits = new TimeUnit[]{TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS};
         TimeUnit timeUnit = Arrays.stream(timeUnits).filter(unit -> unit.toString().toLowerCase().startsWith(unitChar)).findFirst().orElse(null);
         return timeUnit == null ? 0L : timeUnit.toSeconds(count);
      } catch (NumberFormatException exception) {
         return 0L;
      }
   }
}
