package io.lumine.mythic.lib.comp.mclogs;

import io.lumine.mythic.lib.MythicLib;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MclogsAPI {
   public static String mcversion = MythicLib.plugin.getVersion().toString();
   public static String userAgent = MythicLib.plugin.getName();
   public static String version = MythicLib.plugin.getDescription().getVersion();
   private static String apiHost = "api.mclo.gs";
   private static String protocol = "https";

   public static APIResponse share(String uploaded) throws IOException {
      URL url = new URL(protocol + "://" + apiHost + "/1/log");
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection)con;
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      String content = "content=" + URLEncoder.encode(uploaded, StandardCharsets.UTF_8.toString());
      byte[] out = content.getBytes(StandardCharsets.UTF_8);
      int length = out.length;
      http.setFixedLengthStreamingMode(length);
      http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      http.setRequestProperty("User-Agent", userAgent + "/" + version + "/" + mcversion);
      http.connect();
      OutputStream os = http.getOutputStream();

      try {
         os.write(out);
      } catch (Throwable var11) {
         if (os != null) {
            try {
               os.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (os != null) {
         os.close();
      }

      return APIResponse.parse(Util.inputStreamToString(http.getInputStream()));
   }

   public static String[] listLogs(String rundir) {
      File logdir = new File(rundir, "logs");
      String[] files = logdir.list();
      if (files == null) {
         files = new String[0];
      }

      return Arrays.stream(files).filter(file -> file.endsWith(".log") || file.endsWith(".log.gz")).toArray(String[]::new);
   }

   public static String getApiHost() {
      return apiHost;
   }

   public static void setApiHost(String apiHost) {
      if (apiHost != null && apiHost.length() > 0) {
         MclogsAPI.apiHost = apiHost;
      }
   }

   public static String getProtocol() {
      return protocol;
   }

   public static void setProtocol(String protocol) {
      if (protocol != null) {
         switch (protocol) {
            case "http":
            case "https":
               MclogsAPI.protocol = protocol;
         }
      }
   }
}
