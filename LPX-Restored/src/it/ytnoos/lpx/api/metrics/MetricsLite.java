package it.ytnoos.lpx.api.metrics;

import io.github.retrooper.packetevents.util.folia.FoliaScheduler;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MetricsLite {
   private final Plugin plugin;
   private final MetricsLite.MetricsBase metricsBase;

   public MetricsLite(Plugin plugin2) {
      this.plugin = plugin2;
      File file = new File(plugin2.getDataFolder().getParentFile(), "bStats");
      File file2 = new File(file, "config.yml");
      YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file2);
      if (!yamlConfiguration.isSet("serverUuid")) {
         yamlConfiguration.addDefault("enabled", true);
         yamlConfiguration.addDefault("serverUuid", UUID.randomUUID().toString());
         yamlConfiguration.addDefault("logFailedRequests", false);
         yamlConfiguration.addDefault("logSentData", false);
         yamlConfiguration.addDefault("logResponseStatusText", false);
         yamlConfiguration.options()
            .header(
               "bStats (https://bStats.org) collects some basic information for plugin authors, like how\nmany people use their plugin and their total player count. It's recommended to keep bStats\nenabled, but if you're not comfortable with this, you can turn this setting off. There is no\nperformance penalty associated with having metrics enabled, and data sent to bStats is fully\nanonymous."
            )
            .copyDefaults(true);

         try {
            yamlConfiguration.save(file2);
         } catch (IOException ioException) {
         }
      }

      boolean flag = yamlConfiguration.getBoolean("enabled", true);
      String string = yamlConfiguration.getString("serverUuid");
      boolean flag2 = yamlConfiguration.getBoolean("logFailedRequests", false);
      boolean flag3 = yamlConfiguration.getBoolean("logSentData", false);
      boolean flag4 = yamlConfiguration.getBoolean("logResponseStatusText", false);
      this.metricsBase = new MetricsLite.MetricsBase(
         "bukkit",
         string,
         9156,
         flag,
         this::appendPlatformData,
         this::appendServiceData,
         runnable -> FoliaScheduler.getGlobalRegionScheduler().run(plugin2, object -> runnable.run()),
         plugin2::isEnabled,
         (string2, throwable) -> this.plugin.getLogger().log(Level.WARNING, string2, throwable),
         string3 -> this.plugin.getLogger().log(Level.INFO, string3),
         flag2,
         flag3,
         flag4
      );
   }

   public void shutdown() {
      this.metricsBase.shutdown();
   }

   public void addCustomChart(MetricsLite.CustomChart customChart) {
      this.metricsBase.addCustomChart(customChart);
   }

   private void appendPlatformData(MetricsLite.JsonObjectBuilder jsonObjectBuilder) {
      jsonObjectBuilder.appendField("playerAmount", this.getPlayerAmount());
      jsonObjectBuilder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
      jsonObjectBuilder.appendField("bukkitVersion", Bukkit.getVersion());
      jsonObjectBuilder.appendField("bukkitName", Bukkit.getName());
      jsonObjectBuilder.appendField("javaVersion", System.getProperty("java.version"));
      jsonObjectBuilder.appendField("osName", System.getProperty("os.name"));
      jsonObjectBuilder.appendField("osArch", System.getProperty("os.arch"));
      jsonObjectBuilder.appendField("osVersion", System.getProperty("os.version"));
      jsonObjectBuilder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
   }

   private void appendServiceData(MetricsLite.JsonObjectBuilder jsonObjectBuilder) {
      jsonObjectBuilder.appendField("pluginVersion", this.plugin.getDescription().getVersion());
   }

   private int getPlayerAmount() {
      try {
         Method method = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
         return method.getReturnType().equals(Collection.class)
            ? ((Collection)method.invoke(Bukkit.getServer())).size()
            : ((Player[])method.invoke(Bukkit.getServer())).length;
      } catch (Exception exception) {
         return Bukkit.getOnlinePlayers().size();
      }
   }

   public static class AdvancedBarChart extends MetricsLite.CustomChart {
      private final Callable<Map<String, int[]>> callable;

      public AdvancedBarChart(String string, Callable<Map<String, int[]>> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         Map map = this.callable.call();
         if (map != null && !map.isEmpty()) {
            boolean flag = true;

            for (Object var6_o : map.entrySet()) { Entry entry = (Entry) var6_o;
               if (((int[])entry.getValue()).length != 0) {
                  flag = false;
                  jsonObjectBuilder.appendField((String)entry.getKey(), (int[])entry.getValue());
               }
            }

            return flag ? null : new MetricsLite.JsonObjectBuilder().appendField("values", jsonObjectBuilder.build()).build();
         } else {
            return null;
         }
      }
   }

   public static class AdvancedPie extends MetricsLite.CustomChart {
      private final Callable<Map<String, Integer>> callable;

      public AdvancedPie(String string, Callable<Map<String, Integer>> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         Map map = this.callable.call();
         if (map != null && !map.isEmpty()) {
            boolean flag = true;

            for (Object var6_o : map.entrySet()) { Entry entry = (Entry) var6_o;
               if ((Integer)entry.getValue() != 0) {
                  flag = false;
                  jsonObjectBuilder.appendField((String)entry.getKey(), (Integer)entry.getValue());
               }
            }

            return flag ? null : new MetricsLite.JsonObjectBuilder().appendField("values", jsonObjectBuilder.build()).build();
         } else {
            return null;
         }
      }
   }

   public abstract static class CustomChart {
      private final String chartId;

      protected CustomChart(String string) {
         if (string == null) {
            throw new IllegalArgumentException("chartId must not be null");
         }

         this.chartId = string;
      }

      public MetricsLite.JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> biConsumer, boolean flag) {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         jsonObjectBuilder.appendField("chartId", this.chartId);

         try {
            MetricsLite.JsonObjectBuilder.JsonObject jsonObject = this.getChartData();
            if (jsonObject == null) {
               return null;
            }

            jsonObjectBuilder.appendField("data", jsonObject);
         } catch (Throwable throwable) {
            if (flag) {
               biConsumer.accept("Failed to get data for custom chart with id " + this.chartId, throwable);
            }

            return null;
         }

         return jsonObjectBuilder.build();
      }

      protected abstract MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception;
   }

   public static class DrilldownPie extends MetricsLite.CustomChart {
      private final Callable<Map<String, Map<String, Integer>>> callable;

      public DrilldownPie(String string, Callable<Map<String, Map<String, Integer>>> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      public MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         Map map = this.callable.call();
         if (map != null && !map.isEmpty()) {
            boolean flag = true;

            for (Object var6_o : map.entrySet()) { Entry entry = (Entry) var6_o;
               MetricsLite.JsonObjectBuilder jsonObjectBuilder2 = new MetricsLite.JsonObjectBuilder();
               boolean flag2 = true;

               for (Object var10_o : ((Map)map.get(entry.getKey())).entrySet()) { Entry entry2 = (Entry) var10_o;
                  jsonObjectBuilder2.appendField((String)entry2.getKey(), (Integer)entry2.getValue());
                  flag2 = false;
               }

               if (!flag2) {
                  flag = false;
                  jsonObjectBuilder.appendField((String)entry.getKey(), jsonObjectBuilder2.build());
               }
            }

            return flag ? null : new MetricsLite.JsonObjectBuilder().appendField("values", jsonObjectBuilder.build()).build();
         } else {
            return null;
         }
      }
   }

   public static class JsonObjectBuilder {
      private StringBuilder builder = new StringBuilder();
      private boolean hasAtLeastOneField = false;

      public JsonObjectBuilder() {
         this.builder.append("{");
      }

      private static String escape(String string) {
         StringBuilder stringBuilder = new StringBuilder();

         for (int value = 0; value < string.length(); value++) {
            char ch = string.charAt(value);
            if (ch == '"') {
               stringBuilder.append("\\\"");
            } else if (ch == '\\') {
               stringBuilder.append("\\\\");
            } else if (ch <= 15) {
               stringBuilder.append("\\u000").append(Integer.toHexString(ch));
            } else if (ch <= 31) {
               stringBuilder.append("\\u00").append(Integer.toHexString(ch));
            } else {
               stringBuilder.append(ch);
            }
         }

         return stringBuilder.toString();
      }

      public MetricsLite.JsonObjectBuilder appendNull(String string) {
         this.appendFieldUnescaped(string, "null");
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, String string2) {
         if (string2 == null) {
            throw new IllegalArgumentException("JSON value must not be null");
         }

         this.appendFieldUnescaped(string, "\"" + escape(string2) + "\"");
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, int value) {
         this.appendFieldUnescaped(string, String.valueOf(value));
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, MetricsLite.JsonObjectBuilder.JsonObject jsonObject) {
         if (jsonObject == null) {
            throw new IllegalArgumentException("JSON object must not be null");
         }

         this.appendFieldUnescaped(string, jsonObject.toString());
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, String[] strings) {
         if (strings == null) {
            throw new IllegalArgumentException("JSON values must not be null");
         }

         String string2 = Arrays.stream(strings).map(string3 -> "\"" + escape(string3) + "\"").collect(Collectors.joining(","));
         this.appendFieldUnescaped(string, "[" + string2 + "]");
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, int[] values) {
         if (values == null) {
            throw new IllegalArgumentException("JSON values must not be null");
         }

         String string2 = Arrays.stream(values).mapToObj(String::valueOf).collect(Collectors.joining(","));
         this.appendFieldUnescaped(string, "[" + string2 + "]");
         return this;
      }

      public MetricsLite.JsonObjectBuilder appendField(String string, MetricsLite.JsonObjectBuilder.JsonObject[] jsonObjects) {
         if (jsonObjects == null) {
            throw new IllegalArgumentException("JSON values must not be null");
         }

         String string2 = Arrays.stream(jsonObjects).map(MetricsLite.JsonObjectBuilder.JsonObject::toString).collect(Collectors.joining(","));
         this.appendFieldUnescaped(string, "[" + string2 + "]");
         return this;
      }

      private void appendFieldUnescaped(String string, String string2) {
         if (this.builder == null) {
            throw new IllegalStateException("JSON has already been built");
         }

         if (string == null) {
            throw new IllegalArgumentException("JSON key must not be null");
         }

         if (this.hasAtLeastOneField) {
            this.builder.append(",");
         }

         this.builder.append("\"").append(escape(string)).append("\":").append(string2);
         this.hasAtLeastOneField = true;
      }

      public MetricsLite.JsonObjectBuilder.JsonObject build() {
         if (this.builder == null) {
            throw new IllegalStateException("JSON has already been built");
         }

         MetricsLite.JsonObjectBuilder.JsonObject jsonObject = new MetricsLite.JsonObjectBuilder.JsonObject(this.builder.append("}").toString());
         this.builder = null;
         return jsonObject;
      }

      public static class JsonObject {
         private final String value;

         private JsonObject(String string) {
            this.value = string;
         }

         @Override
         public String toString() {
            return this.value;
         }
      }
   }

   public static class MetricsBase {
      public static final String METRICS_VERSION = "3.0.2";
      private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s";
      private final ScheduledExecutorService scheduler;
      private final String platform;
      private final String serverUuid;
      private final int serviceId;
      private final Consumer<MetricsLite.JsonObjectBuilder> appendPlatformDataConsumer;
      private final Consumer<MetricsLite.JsonObjectBuilder> appendServiceDataConsumer;
      private final Consumer<Runnable> submitTaskConsumer;
      private final Supplier<Boolean> checkServiceEnabledSupplier;
      private final BiConsumer<String, Throwable> errorLogger;
      private final Consumer<String> infoLogger;
      private final boolean logErrors;
      private final boolean logSentData;
      private final boolean logResponseStatusText;
      private final Set<MetricsLite.CustomChart> customCharts = new HashSet<>();
      private final boolean enabled;

      public MetricsBase(
         String string,
         String string2,
         int value,
         boolean flag,
         Consumer<MetricsLite.JsonObjectBuilder> consumer,
         Consumer<MetricsLite.JsonObjectBuilder> consumer2,
         Consumer<Runnable> consumer3,
         Supplier<Boolean> supplier,
         BiConsumer<String, Throwable> biConsumer,
         Consumer<String> consumer4,
         boolean flag2,
         boolean flag3,
         boolean flag4
      ) {
         ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, runnable -> new Thread(runnable, "bStats-Metrics"));
         scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
         this.scheduler = scheduledThreadPoolExecutor;
         this.platform = string;
         this.serverUuid = string2;
         this.serviceId = value;
         this.enabled = flag;
         this.appendPlatformDataConsumer = consumer;
         this.appendServiceDataConsumer = consumer2;
         this.submitTaskConsumer = consumer3;
         this.checkServiceEnabledSupplier = supplier;
         this.errorLogger = biConsumer;
         this.infoLogger = consumer4;
         this.logErrors = flag2;
         this.logSentData = flag3;
         this.logResponseStatusText = flag4;
         this.checkRelocation();
         if (flag) {
            this.startSubmitting();
         }
      }

      private static byte[] compress(String string) throws IOException {
         if (string == null) {
            return null;
         }

         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
         }

         return byteArrayOutputStream.toByteArray();
      }

      public void addCustomChart(MetricsLite.CustomChart customChart) {
         this.customCharts.add(customChart);
      }

      public void shutdown() {
         this.scheduler.shutdown();
      }

      private void startSubmitting() {
         Runnable runnable = () -> {
            if (this.enabled && this.checkServiceEnabledSupplier.get()) {
               if (this.submitTaskConsumer != null) {
                  this.submitTaskConsumer.accept(this::submitData);
               } else {
                  this.submitData();
               }
            } else {
               this.scheduler.shutdown();
            }
         };
         long longValue = (long)(60000.0 * (3.0 + Math.random() * 3.0));
         long longValue2 = (long)(60000.0 * (Math.random() * 30.0));
         this.scheduler.schedule(runnable, longValue, TimeUnit.MILLISECONDS);
         this.scheduler.scheduleAtFixedRate(runnable, longValue + longValue2, 1800000L, TimeUnit.MILLISECONDS);
      }

      private void submitData() {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         this.appendPlatformDataConsumer.accept(jsonObjectBuilder);
         MetricsLite.JsonObjectBuilder jsonObjectBuilder2 = new MetricsLite.JsonObjectBuilder();
         this.appendServiceDataConsumer.accept(jsonObjectBuilder2);
         MetricsLite.JsonObjectBuilder.JsonObject[] jsonObjects = this.customCharts
            .stream()
            .map(customChart -> customChart.getRequestJsonObject(this.errorLogger, this.logErrors))
            .filter(Objects::nonNull)
            .toArray(MetricsLite.JsonObjectBuilder.JsonObject[]::new);
         jsonObjectBuilder2.appendField("id", this.serviceId);
         jsonObjectBuilder2.appendField("customCharts", jsonObjects);
         jsonObjectBuilder.appendField("service", jsonObjectBuilder2.build());
         jsonObjectBuilder.appendField("serverUUID", this.serverUuid);
         jsonObjectBuilder.appendField("metricsVersion", "3.0.2");
         MetricsLite.JsonObjectBuilder.JsonObject jsonObject = jsonObjectBuilder.build();
         this.scheduler.execute(() -> {
            try {
               this.sendData(jsonObject);
            } catch (Exception exception) {
               if (this.logErrors) {
                  this.errorLogger.accept("Could not submit bStats metrics data", exception);
               }
            }
         });
      }

      private void sendData(MetricsLite.JsonObjectBuilder.JsonObject jsonObject) throws Exception {
         if (this.logSentData) {
            this.infoLogger.accept("Sent bStats metrics data: " + jsonObject.toString());
         }

         String string = String.format("https://bStats.org/api/v2/data/%s", this.platform);
         HttpsURLConnection httpsURLConnection = (HttpsURLConnection)new URL(string).openConnection();
         byte[] bs = compress(jsonObject.toString());
         httpsURLConnection.setRequestMethod("POST");
         httpsURLConnection.addRequestProperty("Accept", "application/json");
         httpsURLConnection.addRequestProperty("Connection", "close");
         httpsURLConnection.addRequestProperty("Content-Encoding", "gzip");
         httpsURLConnection.addRequestProperty("Content-Length", String.valueOf(bs.length));
         httpsURLConnection.setRequestProperty("Content-Type", "application/json");
         httpsURLConnection.setRequestProperty("User-Agent", "Metrics-Service/1");
         httpsURLConnection.setDoOutput(true);

         try (DataOutputStream dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream())) {
            dataOutputStream.write(bs);
         }

         StringBuilder stringBuilder = new StringBuilder();
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));

         String string2;
         try {
            while ((string2 = bufferedReader.readLine()) != null) {
               stringBuilder.append(string2);
            }
         } catch (Throwable throwable) {
            try {
               bufferedReader.close();
            } catch (Throwable throwable2) {
               throwable.addSuppressed(throwable2);
            }

            throw throwable;
         }

         bufferedReader.close();
         if (this.logResponseStatusText) {
            this.infoLogger.accept("Sent data to bStats and received response: " + stringBuilder);
         }
      }

      private void checkRelocation() {
         if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
            String string = new String(new byte[]{111, 114, 103, 46, 98, 115, 116, 97, 116, 115});
            String string2 = new String(new byte[]{121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101});
            if (MetricsLite.MetricsBase.class.getPackage().getName().startsWith(string) || MetricsLite.MetricsBase.class.getPackage().getName().startsWith(string2)) {
               throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
         }
      }
   }

   public static class MultiLineChart extends MetricsLite.CustomChart {
      private final Callable<Map<String, Integer>> callable;

      public MultiLineChart(String string, Callable<Map<String, Integer>> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         Map map = this.callable.call();
         if (map != null && !map.isEmpty()) {
            boolean flag = true;

            for (Object var6_o : map.entrySet()) { Entry entry = (Entry) var6_o;
               if ((Integer)entry.getValue() != 0) {
                  flag = false;
                  jsonObjectBuilder.appendField((String)entry.getKey(), (Integer)entry.getValue());
               }
            }

            return flag ? null : new MetricsLite.JsonObjectBuilder().appendField("values", jsonObjectBuilder.build()).build();
         } else {
            return null;
         }
      }
   }

   public static class SimpleBarChart extends MetricsLite.CustomChart {
      private final Callable<Map<String, Integer>> callable;

      public SimpleBarChart(String string, Callable<Map<String, Integer>> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         MetricsLite.JsonObjectBuilder jsonObjectBuilder = new MetricsLite.JsonObjectBuilder();
         Map map = this.callable.call();
         if (map != null && !map.isEmpty()) {
            for (Object var5_o : map.entrySet()) { Entry entry = (Entry) var5_o;
               jsonObjectBuilder.appendField((String)entry.getKey(), new int[]{(Integer)entry.getValue()});
            }

            return new MetricsLite.JsonObjectBuilder().appendField("values", jsonObjectBuilder.build()).build();
         } else {
            return null;
         }
      }
   }

   public static class SimplePie extends MetricsLite.CustomChart {
      private final Callable<String> callable;

      public SimplePie(String string, Callable<String> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         String string = this.callable.call();
         return string != null && !string.isEmpty() ? new MetricsLite.JsonObjectBuilder().appendField("value", string).build() : null;
      }
   }

   public static class SingleLineChart extends MetricsLite.CustomChart {
      private final Callable<Integer> callable;

      public SingleLineChart(String string, Callable<Integer> callable2) {
         super(string);
         this.callable = callable2;
      }

      @Override
      protected MetricsLite.JsonObjectBuilder.JsonObject getChartData() throws Exception {
         int value = this.callable.call();
         return value == 0 ? null : new MetricsLite.JsonObjectBuilder().appendField("value", value).build();
      }
   }
}
