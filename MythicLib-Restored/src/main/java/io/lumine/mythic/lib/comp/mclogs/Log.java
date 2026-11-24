package io.lumine.mythic.lib.comp.mclogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Log {
   private String content;
   private static final Pattern IPV4_PATTERN = Pattern.compile("(?:[1-2]?[0-9]{1,2}\\.){3}[1-2]?[0-9]{1,2}");
   private static final Pattern IPV6_PATTERN = Pattern.compile("(?:[0-9a-f]{0,4}:){7}[0-9a-f]{0,4}%", 2);

   public Log(String dir, String file) throws IOException {
      File logs = new File(dir);
      File log = new File(logs, file);
      if (log.getParentFile().equals(logs) && file.matches(".*\\.log(\\.gz)?")) {
         InputStream in = new FileInputStream(log);
         if (file.endsWith(".gz")) {
            in = new GZIPInputStream(in);
         }

         this.content = Util.inputStreamToString(in);
         this.filter();
      } else {
         throw new FileNotFoundException();
      }
   }

   private void filter() {
      this.filterIPv4();
      this.filterIPv6();
   }

   private void filterIPv4() {
      Matcher matcher = IPV4_PATTERN.matcher(this.content);
      StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(sb, matcher.group().replaceAll("[0-9]", "*"));
      }

      matcher.appendTail(sb);
      this.content = sb.toString();
   }

   private void filterIPv6() {
      Matcher matcher = IPV6_PATTERN.matcher(this.content);
      StringBuffer sb = new StringBuffer();

      while (matcher.find()) {
         matcher.appendReplacement(sb, matcher.group().replaceAll("[0-9a-fA-F]", "*"));
      }

      matcher.appendTail(sb);
      this.content = sb.toString();
   }

   public String getContent() {
      return this.content;
   }
}
