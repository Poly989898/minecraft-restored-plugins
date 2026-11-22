package com.Zrips.CMI.commands.list;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.CAnnotation;
import com.Zrips.CMI.commands.Cmd;
import com.Zrips.CMI.utils.CMIDiskUsage;
import com.Zrips.CMI.utils.Lag;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Logs.CMIDebug;
import net.Zrips.CMILib.RawMessages.RawMessage;
import net.Zrips.CMILib.Version.PaperMethods.PaperLib;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class status implements Cmd {
  static final DecimalFormat df = new DecimalFormat("0.00");
  private static String javaVersion = null;

  @Override
  public void getExtra(ConfigReader configReader) {
    configReader.get(
        "Platform",
        " {gcp}Platform: {gcs}[platform] {gcp}({gcs}[os]{gcp}) {gcp}Running threads:"
            + " {gcs}[threads]");
    configReader.get("Tps", " {gcp}TPS: {gcs}[tps]");
    configReader.get(
        "CpuUsage", " {gcp}Cpu usage: {gcs}[cpuusage]% {gcp}({gcs}[cores] cores{gcp})");
    configReader.get("Uptime.main", " {gcp}Uptime: ");
    configReader.get("Uptime.weeks", "{gcs}[weeks] {gcp}weeks ");
    configReader.get("Uptime.days", "{gcs}[days] {gcp}days ");
    configReader.get("Uptime.hours", "{gcs}[hours] {gcp}hours ");
    configReader.get("Uptime.mins", "{gcs}[mins] {gcp}mins ");
    configReader.get("Uptime.secs", "{gcs}[secs] {gcp}secs ");
    configReader.get(
        "MemoryUsage",
        " {gcp}Memory usage: {gcs}[percent]% {gcp}({gcs}[current]{gcp}/{gcs}[max] MB{gcp})");
    configReader.get(
        "MemoryUsageHover",
        "{gcp}Free: {gcs}[free] \n"
            + "{gcp}Allocated: {gcs}[allocated] \n"
            + "{gcp}Max: {gcs}[max] \n"
            + "{gcp}Total: {gcs}[total]");
    configReader.get(
        "Java",
        " {gcp}Java version: {gcs}[mainV].[majorV].[minorV]{gcp}({gcs}[update]{gcp}) Build:"
            + " {gcs}[build]");
    configReader.get(
        "DiskUsage",
        " {gcp}Disk usage: {gcs}[percent]% {gcp}({gcs}[current]{gcp}/{gcs}[max] GB{gcp})");
    configReader.get(
        "Worlds",
        "{gcs} [number]. {gcp}[world] {gcs}[chunks] {gcp}Chunks {gcs}[entities] {gcp}Entities"
            + " {gcs}[tiles] {gcp}tiles {gcs}[players] {gcp}players");
    configReader.get("MostTiles", "{gcp}Most tiles ({gcs}[tiles]{gcp}) in {gcs}[coord] {gcp}chunk");
    configReader.get(
        "MostEntities", "{gcp}Most entities ({gcs}[entities]&7) in {gcs}[coord] &7chunk");
    configReader.get("Pending", "{gcp}Pending Tasks: {gcs}[taskCount]");
  }

  private static List<String> getPendingTasks() {
    HashMap<String, Integer> hashMap = new HashMap<>();
    new ArrayList<>(Bukkit.getScheduler().getPendingTasks())
        .forEach(
            bukkitTask -> {
              String string = "Unknown";

              try {
                string = bukkitTask.getOwner().getName();
              } catch (Throwable throwable) {
              }

              hashMap.merge(string, 1, Integer::sum);
            });
    List list =
        hashMap.entrySet().stream()
            .sorted(Entry.<String, Integer>comparingByValue().reversed())
            .collect(Collectors.toList());
    ArrayList<String> arrayList = new ArrayList();
    arrayList.add(
        CMILC.getIM(
            status.class,
            "Pending",
            "[taskCount]",
            Bukkit.getScheduler().getPendingTasks().size()));
    byte b = 5;
    int value = 0;
    StringBuilder stringBuilder = new StringBuilder();

    for (Entry entry : ((Iterable<Entry>) list)) {
      if (value++ % b == 0) {
        arrayList.add(stringBuilder.toString());
        stringBuilder = new StringBuilder();
      }

      stringBuilder
          .append("&f")
          .append((String) entry.getKey())
          .append(": &7")
          .append(entry.getValue())
          .append(" ");
    }

    if (!stringBuilder.toString().isEmpty()) {
      arrayList.add(stringBuilder.toString());
    }

    return arrayList;
  }

  @CAnnotation(
      info = "Show server status",
      regVar = 0,
      consoleVar = 0,
      customAlias = {"gc", "!lag"})
  @Override
  public Boolean perform(CMI cmi, CommandSender commandSender, String[] strings) {
    status status = this;
    CMIScheduler.runTaskAsynchronously(
        cmi,
        () -> {
          RawMessage rawMessage = new RawMessage();
          if (!(commandSender instanceof Player)) {
            rawMessage.addText("\n");
          }

          rawMessage.addText(LC.info_Spliter.getLocale(new Object[0]));

          try {
            OperatingSystemMXBean operatingSystemMXBean =
                ManagementFactory.getOperatingSystemMXBean();
            int value = operatingSystemMXBean.getAvailableProcessors();
            rawMessage.addText("\n");
            rawMessage.addText(
                CMILC.getIM(
                    status,
                    "Platform",
                    "[platform]",
                    operatingSystemMXBean.getArch(),
                    "[os]",
                    operatingSystemMXBean.getName(),
                    "[threads]",
                    Thread.getAllStackTraces().keySet().size()));
            StringBuilder stringBuilder = new StringBuilder();
            var map =
                Thread.getAllStackTraces().keySet().stream()
                    .collect(Collectors.groupingBy(Thread::getName, Collectors.counting()));
            map.entrySet().stream()
                .sorted(Entry.<String, Long>comparingByValue().reversed())
                .limit(10L)
                .forEach(
                    object -> {
                      if (!stringBuilder.toString().isEmpty()) {
                        stringBuilder.append("\n");
                      }

                      stringBuilder
                          .append("&f")
                          .append(((String) object.getKey()).toString())
                          .append(" -> &7")
                          .append(object.getValue());
                    });
            rawMessage.addHover(getPendingTasks());
            if (commandSender instanceof Player) {
              rawMessage.addText("\n");
              rawMessage
                  .addText(
                      CMILC.getIM(status, "Tps", "[tps]", df.format(cmi.getLagMeter().getTPS(20))))
                  .addHover(LC.info_sec.getLocale(new Object[] {"[secs]", 1}));
              rawMessage
                  .addText(" (" + df.format(cmi.getLagMeter().getTPS(200)))
                  .addHover(LC.info_sec.getLocale(new Object[] {"[secs]", 10}));
              rawMessage
                  .addText(" " + df.format(cmi.getLagMeter().getTPS(600)))
                  .addHover(LC.info_sec.getLocale(new Object[] {"[secs]", 30}));
              rawMessage
                  .addText(" " + df.format(cmi.getLagMeter().getTPS(1200)))
                  .addHover(LC.info_min.getLocale(new Object[] {"[mins]", 1}));
              rawMessage
                  .addText(" " + df.format(cmi.getLagMeter().getTPS(6000)) + ")")
                  .addHover(LC.info_min.getLocale(new Object[] {"[mins]", 10}));
            } else {
              rawMessage.addText("\n");
              rawMessage.addText(
                  CMILC.getIM(status, "Tps", "[tps]", df.format(cmi.getLagMeter().getTPS(20)))
                      + " ("
                      + df.format(cmi.getLagMeter().getTPS(200))
                      + " "
                      + df.format(cmi.getLagMeter().getTPS(600))
                      + " "
                      + df.format(cmi.getLagMeter().getTPS(1200))
                      + " "
                      + df.format(cmi.getLagMeter().getTPS(6000))
                      + ")");
            }

            double number = Lag.getProcessCpuLoad();
            rawMessage.addText("\n");
            rawMessage.addText(
                CMILC.getIM(status, "CpuUsage", "[cpuusage]", df.format(number), "[cores]", value));
            rawMessage.addHover(stringBuilder.toString());
          } catch (Throwable throwable) {
          }

          long longValue = ManagementFactory.getRuntimeMXBean().getStartTime();
          longValue = System.currentTimeMillis() - longValue;
          long seconds = longValue / 1000L % 60L;
          long minutes = longValue / 60000L % 60L;
          long hours = longValue / 3600000L % 24L;
          long days = longValue / 86400000L % 7L;
          long weeks = longValue / 604800000L;
          String string = CMILC.getIM(status, "Uptime.main");
          if (weeks > 0L) {
            string = string + CMILC.getIM(status, "Uptime.weeks", "[weeks]", weeks);
          }

          if (days > 0L) {
            string = string + CMILC.getIM(status, "Uptime.days", "[days]", days);
          }

          if (hours > 0L) {
            string = string + CMILC.getIM(status, "Uptime.hours", "[hours]", hours);
          }

          if (minutes > 0L) {
            string = string + CMILC.getIM(status, "Uptime.mins", "[mins]", minutes);
          }

          if (seconds > 0L) {
            string = string + CMILC.getIM(status, "Uptime.secs", "[secs]", seconds);
          }

          rawMessage.addText("\n");
          rawMessage.addText(string);
          Runtime runtime = Runtime.getRuntime();
          long maxMemory = runtime.maxMemory();
          long totalMemory = runtime.totalMemory();
          long freeMemory = runtime.freeMemory();
          long usedMemory = maxMemory - (totalMemory - freeMemory);
          rawMessage.addText("\n");
          rawMessage.addText(
              CMILC.getIM(
                  status,
                  "MemoryUsage",
                  "[percent]",
                  df.format(100.0 - usedMemory * 100.0 / maxMemory),
                  "[current]",
                  df.format((maxMemory - usedMemory) / 1024L / 1024L),
                  "[max]",
                  maxMemory / 1024L / 1024L));
          rawMessage.addHover(
              CMILC.getIM(
                  status,
                  "MemoryUsageHover",
                  "[free]",
                  df.format(freeMemory / 1024L / 1024L),
                  "[allocated]",
                  df.format(totalMemory / 1024L / 1024L),
                  "[total]",
                  df.format(maxMemory / 1024L / 1024L),
                  "[max]",
                  df.format(maxMemory / 1024L / 1024L)));
          rawMessage.addText("\n");
          rawMessage.addText(getJavaVersionText());
          rawMessage.addText("\n");
          rawMessage.addText(getDiskUsageText());
          this.getInfo()
              .thenAccept(
                  hashMap -> {
                    int count = 0;

                    for (var entry : hashMap.entrySet()) {
                      count++;
                      status.Data data = (status.Data) entry.getValue();
                      rawMessage.addText("\n");
                      World world = (World) entry.getKey();
                      rawMessage.addText(
                          CMILC.getIM(
                              status,
                              "Worlds",
                              "[number]",
                              count,
                              "[world]",
                              world.getName(),
                              "[chunks]",
                              data.getLoadedChunks(),
                              "[entities]",
                              data.getEntities(),
                              "[tiles]",
                              data.getTileEntities(),
                              "[players]",
                              data.getPlayerCount()));
                      ArrayList<String> arrayList = new ArrayList();
                      if (data.getTileEntities() > 0) {
                        arrayList.add(
                            CMILC.getIM(
                                status.class,
                                "MostTiles",
                                "[tiles]",
                                data.getMostTileEntities(),
                                "[coord]",
                                data.getMostTileEntitiesAt()));
                      }

                      if (data.getMostEntities() > 0) {
                        arrayList.add(
                            CMILC.getIM(
                                status.class,
                                "MostEntities",
                                "[entities]",
                                data.getMostEntities(),
                                "[coord]",
                                data.getMostEntitiesAt()));
                      }

                      if (CMIDebug.isTestServer()) {
                        rawMessage.addSuggestion(world.getUID().toString());
                      }

                      if (!arrayList.isEmpty()) {
                        rawMessage.addHover(arrayList);
                      }
                    }

                    rawMessage.addText("\n");
                    rawMessage.addText(LC.info_Spliter.getLocale(new Object[0]));
                    rawMessage.show(commandSender);
                  });
        });
    return true;
  }

  private static String getDiskUsageText() {
    try {
      int value = (int) (CMIDiskUsage.getTotal() / 1024L / 1024L / 1024L);
      int usableGb = (int) (CMIDiskUsage.getUsable() / 1024L / 1024L / 1024L);
      String string = df.format((value - usableGb) * 100.0 / value);
      return CMILC.getIM(
          status.class,
          "DiskUsage",
          "[percent]",
          string,
          "[current]",
          value - usableGb,
          "[max]",
          value);
    } catch (Throwable throwable) {
      return "";
    }
  }

  private static String getJavaVersionText() {
    if (javaVersion != null) {
      return javaVersion;
    }

    String string = "";
    String majorVersion = "";
    String minorVersion = "";
    String updateVersion = "";
    String buildNumber = "";
    String[] strings = System.getProperty("java.runtime.version").split("\\.|_|-b");

    try {
      string = strings[0];
      majorVersion = strings[1];
      minorVersion = strings[2];
      updateVersion = strings[3];
      buildNumber = strings[4];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      try {
        String javaVersionProp = System.getProperty("java.version");
        updateVersion = javaVersionProp.split("_")[1];
        String[] versionParts = javaVersionProp.split("_")[0].split(".");
        string = versionParts[0];
        majorVersion = versionParts[1];
        minorVersion = versionParts[2];
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException2) {
      }
    }

    javaVersion =
        CMILC.getIM(
            status.class,
            "Java",
            "[mainV]",
            string,
            "[majorV]",
            majorVersion,
            "[minorV]",
            minorVersion,
            "[update]",
            updateVersion,
            "[build]",
            buildNumber);
    return javaVersion;
  }

  private CompletableFuture<HashMap<World, status.Data>> getInfo() {
    HashSet<CompletableFuture<org.bukkit.entity.Entity[]>> hashSet = new HashSet();
    LinkedHashMap<World, com.Zrips.CMI.commands.list.status.Data> linkedHashMap =
        new LinkedHashMap();

    for (World world : Bukkit.getServer().getWorlds()) {
      status.Data data = new status.Data(world.getPlayers().size(), world.getLoadedChunks().length);
      linkedHashMap.put(world, data);

      try {
        Chunk[] chunks;
        int value = (chunks = world.getLoadedChunks()).length;

        for (int chunkIndex = 0; chunkIndex < value; chunkIndex++) {
          Chunk chunk = chunks[chunkIndex];

          try {
            hashSet.add(getEntitiesFromChunks(world, chunk.getX(), chunk.getZ(), data));
          } catch (Throwable throwable) {
            throwable.printStackTrace();
            break;
          }
        }
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }
    }

    try {
      CompletableFuture completableFuture =
          CompletableFuture.allOf(hashSet.toArray(new CompletableFuture[0]));
      return completableFuture.thenApply(object -> linkedHashMap);
    } catch (Throwable throwable3) {
      throwable3.printStackTrace();
      return CompletableFuture.completedFuture(linkedHashMap);
    }
  }

  private static CompletableFuture<Entity[]> getEntitiesFromChunks(
      World world, int value, int chunkZ, status.Data data) {
    var completableFuture = PaperLib.getChunkAtAsync(world, value, chunkZ, false);
    return completableFuture.thenCompose(
        object -> {
          if (object == null) {
            return CompletableFuture.completedFuture(null);
          }

          CompletableFuture entitiesFuture = new CompletableFuture();
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              object,
              () -> {
                try {
                  data.add(
                      value, chunkZ, object.getTileEntities().length, object.getEntities().length);
                  entitiesFuture.complete(object.getEntities());
                } catch (Throwable throwable) {
                  entitiesFuture.completeExceptionally(throwable);
                }
              });
          return entitiesFuture;
        });
  }

  class Data {
    private int tileEntities = 0;
    private int entities = 0;
    private int players = 0;
    private int loadedChunks = 0;
    private int mostTileEntities = 0;
    private String mostTileEntitiesAt = "";
    private int mostEntities = 0;
    private String mostEntitiesAt = "";

    public Data(int value, int loadedChunkCount) {
      this.players = value;
      this.loadedChunks = loadedChunkCount;
    }

    public int getTileEntities() {
      return this.tileEntities;
    }

    public int getEntities() {
      return this.entities;
    }

    public void add(int value, int chunkZ, int tileEntityCount, int entityCount) {
      if (tileEntityCount > this.mostTileEntities) {
        this.mostTileEntities = tileEntityCount;
        this.mostTileEntitiesAt = value + ":" + chunkZ;
      }

      if (entityCount > this.mostEntities) {
        this.mostEntities = entityCount;
        this.mostEntitiesAt = value + ":" + chunkZ;
      }

      this.tileEntities += tileEntityCount;
      this.entities += entityCount;
    }

    public String getMostTileEntitiesAt() {
      return this.mostTileEntitiesAt;
    }

    public String getMostEntitiesAt() {
      return this.mostEntitiesAt;
    }

    public int getMostTileEntities() {
      return this.mostTileEntities;
    }

    public int getMostEntities() {
      return this.mostEntities;
    }

    public int getPlayerCount() {
      return this.players;
    }

    public int getLoadedChunks() {
      return this.loadedChunks;
    }
  }
}
