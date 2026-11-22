package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Display.CMIDisplay;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOption;
import com.Zrips.CMI.Modules.Worlds.CMIWorldListener;
import com.Zrips.CMI.Modules.Worlds.UpdateOnWorldLoad;
import com.Zrips.CMI.commands.list.hologram;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import net.Zrips.CMILib.Container.CMIChunkReference;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.FileHandler.CMIBackupMessageType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class HologramManager {
  private static HashMap<String, CMIHologram> holograms = new HashMap<>();
  protected static Map<String, Map<CMIChunkReference, Set<CMIHologram>>> chunkHoloUpdateRange =
      new ConcurrentHashMap<>();
  protected static Map<String, Map<CMIChunkReference, Set<CMIHologram>>> chunkHoloVisibilityRange =
      new ConcurrentHashMap<>();
  private static Map<UUID, Set<CMIHologram>> playersInUpdateRange = new ConcurrentHashMap<>();
  private static Map<UUID, Set<CMIHologram>> playersInVisibilityRange = new ConcurrentHashMap<>();
  private static Set<UUID> inPacketListener = new HashSet<>();
  private CMITask saveScheduler = null;
  private CMIHologramLine autoPage;
  private CMI plugin;
  private String fileName = "Holograms.yml";
  private static int visualRange = 16;
  private static int updateRange = 8;
  private static int updateIntervalTicks = 0;
  private static int pageChangeIntervalTicks = 0;
  private static String basePrefix = "{#white}";
  private static String hoverPrefix = "{#gray}";
  private boolean savedBackup = false;

  public void onPlayerQuit(UUID uuid) {
    this.clearCachedData(uuid);
    this.hideAllHologramsForPlayer(uuid);
    this.clearCache(uuid);
    CMIAiming.removeAimAt(uuid);
  }

  public void clearCache(UUID uuid) {
    playersInUpdateRange.remove(uuid);
    playersInVisibilityRange.remove(uuid);
    inPacketListener.remove(uuid);
  }

  public void clearCachedData(UUID uuid) {
    Optional.ofNullable(this.getPlayerActiveHolograms(uuid))
        .ifPresent(set -> set.forEach(cmiHologram -> cmiHologram.instantHide(uuid)));
  }

  public HologramManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void stop() {
    if (this.saveScheduler != null) {
      this.saveScheduler.cancel();
      this.saveScheduler = null;
      this.saveHolograms();
    }
  }

  public CompletableFuture<Boolean> stopAsync() {
    if (this.saveScheduler != null) {
      this.saveScheduler.cancel();
      this.saveScheduler = null;
      return this.saveHologramsAsync();
    } else {
      return CompletableFuture.completedFuture(false);
    }
  }

  public void add(CMIHologram cmiHologram) {
    this.add(cmiHologram, true, true);
  }

  public void add(CMIHologram cmiHologram, boolean flag) {
    this.add(cmiHologram, flag, true);
  }

  public void add(CMIHologram cmiHologram, boolean flag, boolean silent) {
    if (CMIModule.holograms.isEnabled()) {
      CMIHologram existing = holograms.get(cmiHologram.getName().toLowerCase());
      if (existing != null && !cmiHologram.equals(existing)) {
        existing.remove();
      }

      holograms.put(cmiHologram.getName().toLowerCase(), cmiHologram);
      this.updateHologramGroup(cmiHologram, cmiHologram.getSettings().getGroup());

      try {
        if (silent) {
          this.recalculateChunks(cmiHologram);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }

      try {
        if (flag) {
          this.addPlayersByUpdateRange(cmiHologram, flag);
          this.addPlayersByVisibilityRange(cmiHologram, flag);
        }
      } catch (Throwable throwable2) {
        throwable2.printStackTrace();
      }

      if (!holograms.isEmpty()) {
        CMIHologramUpdator.start();
      }
    }
  }

  public void recalculateChunks() {
    chunkHoloUpdateRange.clear();

    for (var entry : holograms.entrySet()) {
      this.recalculateUpdateChunks((CMIHologram) entry.getValue());
    }

    chunkHoloVisibilityRange.clear();

    for (var hologramEntry : holograms.entrySet()) {
      this.recalculateVisibilityChunks((CMIHologram) hologramEntry.getValue());
    }
  }

  public void removeChunkRecords(CMIHologram cmiHologram) {
    if (cmiHologram.getWorld() != null && cmiHologram.getWorld().getName() != null) {
      String string = cmiHologram.getWorldName();
      var map = chunkHoloUpdateRange.get(string);
      if (map != null) {
        for (CMIChunkReference cmiChunkReference : cmiHologram.getUpdateArea().getChunks()) {
          Set set = (Set) map.get(cmiChunkReference);
          if (set != null) {
            set.remove(cmiHologram);
            map.put(cmiChunkReference, set);
          }
        }
      }

      var chunkVisibility = chunkHoloVisibilityRange.get(string);
      if (chunkVisibility != null) {
        for (CMIChunkReference chunkRef : cmiHologram.getVisibilityArea().getChunks()) {
          Set visibleHolograms = (Set) chunkVisibility.get(chunkRef);
          if (visibleHolograms != null) {
            visibleHolograms.remove(cmiHologram);
            chunkVisibility.put(chunkRef, visibleHolograms);
            chunkHoloVisibilityRange.put(string, chunkVisibility);
          }
        }
      }
    }
  }

  public void recalculateChunks(CMIHologram cmiHologram) {
    this.removeChunkRecords(cmiHologram);
    cmiHologram.resetVisibilityArea();
    cmiHologram.resetUpdateArea();
    this.recalculateUpdateChunks(cmiHologram);
    this.recalculateVisibilityChunks(cmiHologram);
  }

  private void recalculateUpdateChunks(final CMIHologram cmiHologram) {
    if (cmiHologram.getWorld() == null) {
      UpdateOnWorldLoad updateOnWorldLoad =
          new UpdateOnWorldLoad() {
            @Override
            public void update(World world) {
              cmiHologram.getLocation().setWorld(world);
            }
          };
      CMIWorldListener.delayRecheck(cmiHologram.getWorldName(), updateOnWorldLoad);
    } else {
      var map =
          chunkHoloUpdateRange.computeIfAbsent(
              cmiHologram.getWorldName(), string -> new HashMap<>());

      for (CMIChunkReference cmiChunkReference : cmiHologram.getUpdateArea().getChunks()) {
        map.computeIfAbsent(cmiChunkReference, object -> new HashSet<>()).add(cmiHologram);
      }

      chunkHoloUpdateRange.put(cmiHologram.getWorldName(), map);
    }
  }

  private void recalculateVisibilityChunks(final CMIHologram cmiHologram) {
    if (cmiHologram.getWorld() == null) {
      UpdateOnWorldLoad updateOnWorldLoad =
          new UpdateOnWorldLoad() {
            @Override
            public void update(World world) {
              HologramManager.this.recalculateVisibilityChunks(cmiHologram);
            }
          };
      CMIWorldListener.delayRecheck(cmiHologram.getWorldName(), updateOnWorldLoad);
    } else {
      var map =
          chunkHoloUpdateRange.computeIfAbsent(
              cmiHologram.getWorldName(), string -> new HashMap<>());

      for (CMIChunkReference cmiChunkReference : cmiHologram.getVisibilityArea().getChunks()) {
        map.computeIfAbsent(cmiChunkReference, object -> new HashSet<>()).add(cmiHologram);
      }

      chunkHoloVisibilityRange.put(cmiHologram.getWorldName(), map);
    }
  }

  public CMIHologram getByName(String string) {
    return string == null ? null : holograms.get(string.toLowerCase());
  }

  public CMIHologram getByUpdateRange(Location location) {
    if (location == null) {
      return null;
    }

    World world = location.getWorld();
    if (world == null) {
      return null;
    }

    String string = world.getName();
    if (string == null) {
      return null;
    }

    CMIChunkReference cmiChunkReference = new CMIChunkReference(location);
    if (!chunkHoloUpdateRange.containsKey(string)) {
      return null;
    }

    var map = chunkHoloUpdateRange.get(string);
    if (!map.containsKey(cmiChunkReference)) {
      return null;
    }

    for (CMIHologram cmiHologram : (Set<CMIHologram>) map.get(cmiChunkReference)) {
      if (cmiHologram != null && cmiHologram.getLocation().toString().equals(location.toString())) {
        return cmiHologram;
      }
    }

    return null;
  }

  public CMIHologram getByLocation(Location location) {
    return this.getByVisibilityRange(location);
  }

  public CMIHologram getByVisibilityRange(Location location) {
    if (location == null) {
      return null;
    }

    World world = location.getWorld();
    if (world == null) {
      return null;
    }

    String string = world.getName();
    if (string == null) {
      return null;
    }

    CMIChunkReference cmiChunkReference = new CMIChunkReference(location);
    if (!chunkHoloVisibilityRange.containsKey(string)) {
      return null;
    }

    var map = chunkHoloVisibilityRange.get(string);
    if (!map.containsKey(cmiChunkReference)) {
      return null;
    }

    for (CMIHologram cmiHologram : (Set<CMIHologram>) map.get(cmiChunkReference)) {
      if (cmiHologram != null && cmiHologram.getLocation().toString().equals(location.toString())) {
        return cmiHologram;
      }
    }

    return null;
  }

  public Set<CMIHologram> getAllByUpdateRange(Location location) {
    CopyOnWriteArraySet<CMIHologram> copyOnWriteArraySet = new CopyOnWriteArraySet();
    if (location == null) {
      return copyOnWriteArraySet;
    }

    World world = location.getWorld();
    if (world == null) {
      return copyOnWriteArraySet;
    }

    String string = world.getName();
    if (string == null) {
      return copyOnWriteArraySet;
    }

    CMIChunkReference cmiChunkReference = new CMIChunkReference(location);
    if (!chunkHoloUpdateRange.containsKey(string)) {
      return copyOnWriteArraySet;
    }

    var map = chunkHoloUpdateRange.get(string);
    Set set = (Set) map.get(cmiChunkReference);
    if (set == null) {
      return copyOnWriteArraySet;
    }

    for (CMIHologram cmiHologram : ((Iterable<CMIHologram>) new HashSet(set))) {
      if (cmiHologram != null && cmiHologram.getUpdateArea().containsLoc(location)) {
        copyOnWriteArraySet.add(cmiHologram);
      }
    }

    return copyOnWriteArraySet;
  }

  public Set<CMIHologram> getAllByVisibilityRange(Location location) {
    CopyOnWriteArraySet<CMIHologram> copyOnWriteArraySet = new CopyOnWriteArraySet();
    if (location == null) {
      return copyOnWriteArraySet;
    }

    World world = location.getWorld();
    if (world == null) {
      return copyOnWriteArraySet;
    }

    String string = world.getName();
    if (string == null) {
      return copyOnWriteArraySet;
    }

    CMIChunkReference cmiChunkReference = new CMIChunkReference(location);
    if (!chunkHoloVisibilityRange.containsKey(string)) {
      return copyOnWriteArraySet;
    }

    var map = chunkHoloVisibilityRange.get(string);
    Set set = (Set) map.get(cmiChunkReference);
    if (set == null) {
      return copyOnWriteArraySet;
    }

    for (CMIHologram cmiHologram : ((Iterable<CMIHologram>) new HashSet(set))) {
      if (cmiHologram != null && cmiHologram.getVisibilityArea().containsLoc(location)) {
        copyOnWriteArraySet.add(cmiHologram);
      }
    }

    return copyOnWriteArraySet;
  }

  public void updatePlayer(UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player != null) {
      this.updatePlayer(player, player.getLocation());
    }
  }

  public void updatePlayer(Player player) {
    this.updatePlayer(player, player.getLocation());
  }

  public void updatePlayer(Player player, Location location) {
    try {
      HologramManager.hologramChange change =
          this.updatePlayerPositionByVisibilityRange(player, location);
      if (!change.getTo().isEmpty()
          || !change.getLeft().isEmpty()
          || !change.getEntered().isEmpty()) {
        this.updatePlayerPositionByUpdateRange(player, location);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private HologramManager.hologramChange updatePlayerPositionByUpdateRange(
      Player player, Location location) {
    var set = playersInUpdateRange.get(player.getUniqueId());
    var nearby = this.getAllByUpdateRange(location);
    HashSet<CMIHologram> hashSet = new HashSet();
    HashSet<CMIHologram> updateSet = new HashSet();
    if (set != null) {
      for (CMIHologram cmiHologram : set) {
        if (CMILocation.getDistance(cmiHologram.getLocation(), location)
            > cmiHologram.getSettings().getUpdateRange()) {
          hashSet.add(cmiHologram);
        }
      }

      for (CMIHologram hologram : nearby) {
        if (!set.contains(hologram)
            && CMILocation.getDistance(hologram.getLocation(), location)
                <= hologram.getSettings().getUpdateRange()) {
          updateSet.add(hologram);
        }
      }
    } else {
      for (CMIHologram hologram : nearby) {
        if (CMILocation.getDistance(hologram.getLocation(), location)
            <= hologram.getSettings().getUpdateRange()) {
          updateSet.add(hologram);
        }
      }
    }

    for (CMIHologram hologram : hashSet) {
      this.removeFromUpdateRange(player.getUniqueId(), hologram);
    }

    CMIUser cmiUser = CMIUser.getUser(player);
    if (cmiUser != null) {
      boolean flag = cmiUser.getOptionState(PlayerOption.visibleHolograms);

      for (CMIHologram hologram : updateSet) {
        if ((!hologram.getSettings().isRequiresPermission()
                || hologram.getSettings().isRequiresPermission()
                    && PermissionsManager.CMIPerm.hologram_$1.hasPermission(
                        player, hologram.getName().toLowerCase()))
            && (flag || !flag && !hologram.getSettings().isSaveToFile())) {
          this.addToUpdateRange(player.getUniqueId(), hologram);
        }
      }
    }

    return new HologramManager.hologramChange(set, nearby, hashSet, updateSet);
  }

  private HologramManager.hologramChange updatePlayerPositionByVisibilityRange(
      Player player, Location location) {
    var set = playersInVisibilityRange.get(player.getUniqueId());
    var visible = this.getAllByVisibilityRange(location);
    HashSet<CMIHologram> hashSet = new HashSet();
    HashSet<CMIHologram> visibleSet = new HashSet();
    if (set != null) {
      for (CMIHologram cmiHologram : set) {
        if (CMILocation.getDistance(cmiHologram.getLocation(), location)
            > cmiHologram.getSettings().getVisibilityRange()) {
          hashSet.add(cmiHologram);
        }
      }

      for (CMIHologram hologram : visible) {
        if (!set.contains(hologram)
            && CMILocation.getDistance(hologram.getLocation(), location)
                <= hologram.getSettings().getVisibilityRange()) {
          visibleSet.add(hologram);
        }
      }
    } else {
      for (CMIHologram hologram : visible) {
        if (CMILocation.getDistance(hologram.getLocation(), location)
            <= hologram.getSettings().getVisibilityRange()) {
          visibleSet.add(hologram);
        }
      }
    }

    for (CMIHologram hologram : hashSet) {
      this.removeFromVisibilityRange(player.getUniqueId(), hologram);
    }

    boolean flag = CMIUser.getUser(player).getOptionState(PlayerOption.visibleHolograms);

    for (CMIHologram hologram : visibleSet) {
      if ((!hologram.getSettings().isRequiresPermission()
              || hologram.getSettings().isRequiresPermission()
                  && PermissionsManager.CMIPerm.hologram_$1.hasPermission(
                      player, hologram.getName().toLowerCase()))
          && (flag || !flag && !hologram.getSettings().isSaveToFile())) {
        this.addToVisibilityRange(player.getUniqueId(), hologram);
      }
    }

    return new HologramManager.hologramChange(set, visible, hashSet, visibleSet);
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Holograms.Defaults.viewRange",
        new String[] {
          "Default value for hologram view range",
          "This defines from how far holograms will appear for the player or when they will"
              + " disapear"
        });
    visualRange = CMINumber.clamp(configReader.get("Holograms.Defaults.viewRange", 16), 1, 128);
    configReader.addComment(
        "Holograms.Defaults.updateRange",
        new String[] {
          "Default value for hologram update range",
          "This defines how close player should get for hologram to start updating its text",
          "Keep it at reasoble numbers as there is no point to update hologram across map"
        });
    updateRange = CMINumber.clamp(configReader.get("Holograms.Defaults.updateRange", 8), 0, 128);
    configReader.addComment(
        "Holograms.Defaults.Interval.Update",
        new String[] {
          "Defines default update interval in ticks, 20 ticks = 1 second",
          "When value is set to 0 or lower, holograms will not be updated",
          "This only applies for holograms with dynamic contents"
        });
    updateIntervalTicks = configReader.get("Holograms.Defaults.Interval.Update", 0);
    updateIntervalTicks = CMINumber.clamp(updateIntervalTicks, 0, updateIntervalTicks);
    configReader.addComment(
        "Holograms.Defaults.pageChangeInterval",
        new String[] {
          "Defines default page change interval in ticks",
          "When value is set to 0 or lower, pages will not be changed"
        });
    pageChangeIntervalTicks = configReader.get("Holograms.Defaults.pageChangeInterval", 0);
    pageChangeIntervalTicks = CMINumber.clamp(pageChangeIntervalTicks, 0, pageChangeIntervalTicks);
    configReader.addComment(
        "Holograms.Defaults.Interaction.BasePrefix",
        new String[] {
          "Prefix to be used for lines utilizing CText",
          "This only applies for the sections that have set commands and will not have effect on"
              + " basic text even with hover text"
        });
    basePrefix = configReader.get("Holograms.Defaults.Interaction.BasePrefix", "{#white}");
    configReader.addComment(
        "Holograms.Defaults.Interaction.HoverPrefix",
        new String[] {
          "Prefix to be used for lines utilizing CText when you hover over it",
          "Can be overriden with hologram editor"
        });
    hoverPrefix = configReader.get("Holograms.Defaults.Interaction.HoverPrefix", "{#gray}");
    new CMIDisplay(null, null);
  }

  public static int getDefaultVisibilityRange() {
    return visualRange;
  }

  public static int getDefaultUpdateRange() {
    return updateRange;
  }

  public static int getDefaultUpdateInterval() {
    return updateIntervalTicks;
  }

  public static int getDefaultpageChangeInterval() {
    return pageChangeIntervalTicks;
  }

  public static String getBasePrefix() {
    return basePrefix;
  }

  public static String getHoverPrefix() {
    return hoverPrefix;
  }

  public CMIHologramLine getAutoPageLine() {
    if (this.autoPage == null) {
      this.autoPage = new CMIHologramLine().setText(CMILC.getIM(hologram.class, "autoPage"));
    }

    return this.autoPage;
  }

  public void reload() {
    this.stopAsync()
        .whenComplete(
            (result, throwable) -> {
              this.autoPage = null;
              ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();

              for (CMIHologram cmiHologram : new HashSet<>(holograms.values())) {
                arrayList.add(cmiHologram.instantHide());
                cmiHologram.resetScheduler();
              }

              CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]))
                  .thenRun(this::load);
            });
  }

  private void checkLegacyfile(ConfigReader configReader) {
    YamlConfiguration yamlConfiguration = configReader.getC();

    for (String string : yamlConfiguration.getKeys(false)) {
      var map = yamlConfiguration.getConfigurationSection(string).getValues(true);
      if (map.containsKey("Text")
          || map.containsKey("Settings")
          || map.containsKey("Board")
          || map.containsKey("Animation")
          || map.containsKey("Interaction")) {
        return;
      }

      if ((map.containsKey("Filler")
              || map.containsKey("NewDisplay")
              || map.containsKey("Interval")
              || map.containsKey("PageInterval")
              || map.containsKey("RangeExtra")
              || map.containsKey("ND")
              || map.containsKey("Range"))
          && !this.savedBackup) {
        CMIMessages.consoleMessage("Legacy holograms file detected, saving backup");

        try {
          configReader.saveToBackup(this.plugin, CMIBackupMessageType.Info);
        } catch (Exception exception) {
          configReader.saveToBackup(true);
        }

        this.savedBackup = true;
        return;
      }
    }
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    if (CMIModule.holograms.isEnabled()) {
      CMIHologramGroup.clear();
      chunkHoloUpdateRange.clear();
      chunkHoloVisibilityRange.clear();
      playersInUpdateRange.clear();
      playersInVisibilityRange.clear();
      File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
      if (!file.exists()) {
        try {
          file.createNewFile();
        } catch (IOException ioException) {
        }
      } else {
        ConfigReader configReader = null;

        try {
          configReader = new ConfigReader(file);
        } catch (Throwable throwable) {
          throwable.printStackTrace();
          return;
        }

        YamlConfiguration yamlConfiguration = configReader.getC();
        if (yamlConfiguration == null) {
          CMIMessages.consoleMessage("&cFailed to load hologram file");
        } else if (!yamlConfiguration.getKeys(false).isEmpty()) {
          var set = yamlConfiguration.getKeys(false);
          holograms.clear();

          for (String string : set) {
            try {
              var map = yamlConfiguration.getConfigurationSection(string).getValues(true);
              map.put("Name", string);
              this.checkLegacyfile(configReader);
              CMIHologram cmiHologram = CMIHologram.deserialize(map);
              if (cmiHologram == null) {
                CMIMessages.consoleMessage("Failed to load hologram " + string);
              } else {
                this.add(cmiHologram, this.plugin.isFullyLoaded());
              }
            } catch (Throwable throwable2) {
              throwable2.printStackTrace();
            }
          }

          this.plugin.loadMessage(
              holograms.size(), "holograms", System.currentTimeMillis() - startTime);
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                  this.plugin.getHologramManager().updatePlayer(player, player.getLocation());
                }
              },
              3L);
        }
      }
    }
  }

  public void save() {
    if (this.saveScheduler == null) {
      this.saveScheduler =
          CMIScheduler.runTaskLater(
              this.plugin,
              () ->
                  this.saveHologramsAsync()
                      .whenComplete((result, throwable) -> this.saveScheduler = null),
              60L);
    }
  }

  public CompletableFuture<Boolean> saveHologramsAsync() {
    return CompletableFuture.supplyAsync(this::saveHolograms);
  }

  public Boolean saveHolograms() {
    if (!CMIModule.holograms.isEnabled()) {
      return false;
    }

    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
        return false;
      }

      return true;
    } else {
      ConfigReader configReader;
      try {
        configReader = new ConfigReader(file);
      } catch (Exception exception) {
        exception.printStackTrace();
        return false;
      }

      if (configReader == null) {
        return false;
      }

      for (var entry : configReader.getC().getValues(false).entrySet()) {
        configReader.getC().set((String) entry.getKey(), null);
      }

      for (var hologramEntry : holograms.entrySet()) {
        CMIHologram cmiHologram = (CMIHologram) hologramEntry.getValue();
        if (cmiHologram.getSettings().isSaveToFile()) {
          String string = cmiHologram.getName().replace(".", "_");
          var map = cmiHologram.serialize();
          configReader.set(string, map);
        }
      }

      try {
        configReader.save();
      } catch (Exception exception2) {
        exception2.printStackTrace();
        return false;
      }

      return true;
    }
  }

  public void hideAllHologramsForPlayer(UUID uuid) {
    CMIHologramHoverData.getData(uuid).setFakeInfo(null);
    var set = playersInUpdateRange.remove(uuid);
    var removedPlayers = playersInVisibilityRange.remove(uuid);
    if (removedPlayers != null) {
      if (set == null) {
        set = removedPlayers;
      } else {
        set.addAll(removedPlayers);
      }
    }

    if (set != null) {
      for (CMIHologram cmiHologram : set) {
        var playerSet = cmiHologram.getPlayersFromVisibilityRange();
        if (playerSet != null) {
          playerSet.remove(uuid);
        }

        playerSet = cmiHologram.getPlayersFromUpdateRange();
        if (playerSet != null) {
          playerSet.remove(uuid);
        }

        cmiHologram.hide(uuid);
      }
    }
  }

  public CompletableFuture<Void> hide(CMIHologram cmiHologram) {
    return cmiHologram == null ? CompletableFuture.completedFuture(null) : cmiHologram.hide();
  }

  public CompletableFuture<Void> reset(CMIHologram cmiHologram) {
    this.recalculateChunks(cmiHologram);
    return this.hide(cmiHologram)
        .thenRun(
            () -> {
              this.addPlayersByVisibilityRange(cmiHologram, true);
              this.addPlayersByUpdateRange(cmiHologram, true);
            });
  }

  public CompletableFuture<Void> addPlayersByUpdateRange(CMIHologram cmiHologram, boolean flag) {
    ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();
    if (cmiHologram.getWorld() == null) {
      return CompletableFuture.completedFuture(null);
    }

    for (Player player : cmiHologram.getWorld().getPlayers()) {
      if (cmiHologram.getUpdateArea().containsLoc(player.getLocation())
          && (!cmiHologram.getSettings().isRequiresPermission()
              || PermissionsManager.CMIPerm.hologram_$1.hasPermission(
                  player, 5000L, cmiHologram.getName().toLowerCase()))) {
        cmiHologram.addPlayerToUpdateRange(player.getUniqueId());
        this.recheckInteractableHolograms(player);
        if (flag) {
          arrayList.add(
              cmiHologram.requestUpdate(
                  player.getUniqueId(),
                  CMIHologramUpdateType.Text,
                  CMIHologramUpdateType.Show,
                  CMIHologramUpdateType.ShowIcon));
        }
      }
    }

    cmiHologram.recheckScheduler();
    return arrayList.isEmpty()
        ? CompletableFuture.completedFuture(null)
        : CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]));
  }

  public CompletableFuture<Void> addPlayersByVisibilityRange(
      CMIHologram cmiHologram, boolean flag) {
    ArrayList<CompletableFuture<Void>> arrayList = new ArrayList();
    if (cmiHologram.getWorld() == null) {
      return CompletableFuture.completedFuture(null);
    }

    for (Player player : cmiHologram.getWorld().getPlayers()) {
      if (cmiHologram.getVisibilityArea().containsLoc(player.getLocation())
          && (!cmiHologram.getSettings().isRequiresPermission()
              || PermissionsManager.CMIPerm.hologram_$1.hasPermission(
                  player, 5000L, cmiHologram.getName().toLowerCase()))) {
        cmiHologram.addPlayerToVisibilityRange(player.getUniqueId());
        if (flag) {
          arrayList.add(
              cmiHologram.requestUpdate(player.getUniqueId(), CMIHologramUpdateType.Show));
        }
      }
    }

    cmiHologram.recheckScheduler();
    return arrayList.isEmpty()
        ? CompletableFuture.completedFuture(null)
        : CompletableFuture.allOf(arrayList.toArray(new CompletableFuture[0]));
  }

  public HashMap<String, CMIHologram> getHolograms() {
    return holograms;
  }

  public List<CMIHologram> getHologramsSortedByDistance(Location location) {
    HashMap<CMIHologram, Integer> hashMap = new HashMap();
    ArrayList<CMIHologram> arrayList = new ArrayList();

    for (var entry : holograms.entrySet()) {
      if (((CMIHologram) entry.getValue()).getWorld() != null) {
        if (!location.getWorld().equals(((CMIHologram) entry.getValue()).getWorld())) {
          arrayList.add((CMIHologram) entry.getValue());
        } else if (((CMIHologram) entry.getValue()).getSettings().isSaveToFile()) {
          hashMap.put(
              entry.getValue(),
              (int)
                  CMI.getInstance()
                      .getUtilManager()
                      .getDistanceSquared(
                          ((CMIHologram) entry.getValue()).getVisibilityArea().getMiddlePoint(),
                          location.toVector()));
        }
      }
    }

    hashMap = (HashMap) this.plugin.getSortingManager().sortByValueObject((Map) hashMap);
    ArrayList<CMIHologram> results = new ArrayList();

    for (Entry mapEntry : hashMap.entrySet()) {
      results.add((CMIHologram) mapEntry.getKey());
    }

    if (!arrayList.isEmpty()) {
      results.addAll(arrayList);
    }

    return results;
  }

  public void remove(CMIHologram cmiHologram) {
    if (cmiHologram != null) {
      holograms.remove(cmiHologram.getName().toLowerCase());
      CMIHologramGroup.updateHologramGroup(cmiHologram, null);
      this.hide(cmiHologram);
      this.removeChunkRecords(cmiHologram);

      for (var entry : playersInUpdateRange.entrySet()) {
        ((Set) entry.getValue()).remove(cmiHologram);
      }

      for (var playerEntry : playersInVisibilityRange.entrySet()) {
        ((Set) playerEntry.getValue()).remove(cmiHologram);
      }

      cmiHologram.disable();
      cmiHologram.getPlayersFromUpdateRange().clear();
      cmiHologram.getPlayersFromVisibilityRange().clear();
      cmiHologram.resetScheduler();
      if (cmiHologram.getSettings().isSaveToFile()) {
        this.save();
      }
    }
  }

  public boolean isNearHolo(UUID uuid) {
    var set = playersInUpdateRange.get(uuid);
    return set != null && !set.isEmpty();
  }

  public boolean recheckInteractableHolograms(UUID uuid) {
    return this.recheckInteractableHolograms(CMIUser.getOnlinePlayer(uuid));
  }

  public boolean recheckInteractableHolograms(Player player) {
    if (player == null) {
      return false;
    }

    var set = playersInUpdateRange.get(player.getUniqueId());
    if (set != null && !set.isEmpty()) {
      boolean flag = false;

      for (CMIHologram cmiHologram : set) {
        if (cmiHologram.getInteractionSettings().isInteractable()) {
          flag = true;
          break;
        }
      }

      if ((!flag || !inPacketListener.contains(player.getUniqueId()))
          && (flag || inPacketListener.contains(player.getUniqueId()))) {
        try {
          if (!flag) {
            CMIScheduler.runTask(
                this.plugin, () -> this.plugin.getPacketInjector().removePlayer(player));
            inPacketListener.remove(player.getUniqueId());
          } else {
            CMIScheduler.runTask(
                this.plugin, () -> this.plugin.getPacketInjector().addPlayer(player));
            inPacketListener.add(player.getUniqueId());
          }
        } catch (Exception | Error value) {
        }

        return true;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

  public void addToUpdateRange(UUID uuid, CMIHologram cmiHologram) {
    var set = playersInUpdateRange.get(uuid);
    if (set == null) {
      set = new CopyOnWriteArraySet();
    }

    cmiHologram.addPlayerToUpdateRange(uuid);
    set.add(cmiHologram);
    playersInUpdateRange.put(uuid, set);
    this.recheckInteractableHolograms(uuid);
  }

  private void removeFromUpdateRange(UUID uuid, CMIHologram cmiHologram) {
    var set = playersInUpdateRange.get(uuid);
    if (set != null) {
      cmiHologram.removePlayerFromUpdateRange(uuid);
      set.remove(cmiHologram);
      this.recheckInteractableHolograms(CMIUser.getOnlinePlayer(uuid));
      playersInUpdateRange.put(uuid, set);
    }
  }

  public CompletableFuture<Void> addToVisibilityRange(UUID uuid, CMIHologram cmiHologram) {
    var set = playersInVisibilityRange.get(uuid);
    if (set == null) {
      set = new CopyOnWriteArraySet();
    }

    set.add(cmiHologram);
    cmiHologram.addPlayerToVisibilityRange(uuid);
    playersInVisibilityRange.put(uuid, set);
    return cmiHologram.requestUpdate(uuid, CMIHologramUpdateType.Show);
  }

  public CompletableFuture<Void> removePlayerFromHologram(UUID uuid, CMIHologram cmiHologram) {
    this.removeFromUpdateRange(uuid, cmiHologram);
    return this.removeFromVisibilityRange(uuid, cmiHologram);
  }

  private CompletableFuture<Void> removeFromVisibilityRange(UUID uuid, CMIHologram cmiHologram) {
    var set = playersInVisibilityRange.get(uuid);
    if (set == null) {
      return CompletableFuture.completedFuture(null);
    }

    set.remove(cmiHologram);
    playersInVisibilityRange.put(uuid, set);
    return cmiHologram.removePlayerFromVisibilityRange(uuid);
  }

  public void openGui(Player player, CMIHologram cmiHologram) {
    HologramGUIEditor.openGui(player, cmiHologram);
  }

  public void hideAllHolograms() {
    for (var entry : holograms.entrySet()) {
      this.hide((CMIHologram) entry.getValue());
    }
  }

  public void hideAllHolograms(UUID uuid) {
    for (var entry : holograms.entrySet()) {
      ((CMIHologram) entry.getValue()).hide(uuid);
    }
  }

  @Nullable
  public Set<CMIHologram> getPlayerActiveHolograms(UUID uuid) {
    return playersInUpdateRange.get(uuid);
  }

  @Nullable
  public Map<UUID, Set<CMIHologram>> getPlayerInUpdateRangeSnapshot() {
    return new HashMap<>(playersInUpdateRange);
  }

  @Nullable
  public Set<CMIHologram> getPlayerVisibleHolograms(UUID uuid) {
    return playersInVisibilityRange.get(uuid);
  }

  public void updateHologramGroup(CMIHologram cmiHologram, String string) {
    CMIHologramGroup.updateHologramGroup(cmiHologram, string);
  }

  public Set<CMIHologram> getHologramsByGroup(String string) {
    return CMIHologramGroup.getHologramsByGroup(string);
  }

  public Optional<Location> getCenterLocation(Set<CMIHologram> set) {
    if (set.isEmpty()) {
      return Optional.empty();
    }

    var iterator = set.iterator();
    CMILocation cmiLocation = ((CMIHologram) iterator.next()).getLocation();
    if (cmiLocation != null && cmiLocation.getWorld() != null) {
      String string = cmiLocation.getWorld().getName();
      double number = 0.0;
      double offsetX = 0.0;
      double offsetZ = 0.0;
      int value = 0;

      for (CMIHologram cmiHologram : set) {
        CMILocation hologramCenter = cmiHologram.getLocation();
        if (hologramCenter != null
            && hologramCenter.getWorld() != null
            && hologramCenter.getWorld().getName().equals(string)) {
          number += hologramCenter.getX();
          offsetX += hologramCenter.getY();
          offsetZ += hologramCenter.getZ();
          value++;
        }
      }

      return value == 0
          ? Optional.empty()
          : Optional.of(
              new Location(
                  cmiLocation.getWorld(), number / value, offsetX / value, offsetZ / value));
    } else {
      return Optional.empty();
    }
  }

  public Location getCenterOfGroup(String string) {
    return CMIHologramGroup.getCenterOfGroup(string);
  }

  public void relocateHologramGroup(Location location, String string) {
    CMIHologramGroup.relocateHologramGroup(location, string);
  }

  public void shiftHologramGroup(Vector vector, String string) {
    CMIHologramGroup.shiftHologramGroup(vector, string);
  }

  public void rotateHologramGroup(int value, int count, String string, Location location) {
    CMIHologramGroup.rotateHologramGroup(value, count, string, location);
  }

  public void rotateHologramYawGroup(int value, String string, Location location) {
    CMIHologramGroup.rotateHologramYawGroup(value, string, location);
  }

  public CompletableFuture<CMIHologram> getLookingAt(UUID uuid) {
    return CMIAiming.getLookingAt(uuid);
  }

  public CompletableFuture<CMIHologram> getLookingAtHologram(UUID uuid) {
    return CMIAiming.getLookingAtHologram(uuid);
  }

  public CompletableFuture<CMIHologram> getLookingAtHologramApproximate(UUID uuid) {
    return CMIAiming.getLookingAtHologramApproximate(uuid);
  }

  @Deprecated(forRemoval = true)
  public void addAimAt(UUID uuid, String string, CMIVector3D cmiVector3D) {
    CMIAiming.addAimAt(uuid, this.getByName(string), cmiVector3D);
  }

  @Deprecated(forRemoval = true)
  public void addAimAt(UUID uuid, String string, CMIVector3D cmiVector3D, double number) {
    CMIAiming.addAimAt(uuid, this.getByName(string), cmiVector3D, new CMIVector2D(number, 0.0));
  }

  @Deprecated(forRemoval = true)
  public CMIAiming getAimAt(UUID uuid) {
    return CMIAiming.getAimAt(uuid);
  }

  @Deprecated(forRemoval = true)
  public void removeAimAt(UUID uuid) {
    CMIAiming.removeAimAt(uuid);
  }

  @Deprecated(forRemoval = true)
  public CMIHologramLine getPrevPageSection() {
    return new CMIHologramLine().setText("");
  }

  @Deprecated(forRemoval = true)
  public void setPrevPageSection(CMIHologramLine cmiHologramLine) {}

  @Deprecated(forRemoval = true)
  public CMIHologramLine getNextPageSection() {
    return new CMIHologramLine().setText("");
  }

  @Deprecated(forRemoval = true)
  public void setNextPageSection(CMIHologramLine cmiHologramLine) {}

  @Deprecated(forRemoval = true)
  public int getPrevPageLength() {
    return 0;
  }

  @Deprecated(forRemoval = true)
  public void setPrevPageLength(int value) {}

  @Deprecated(forRemoval = true)
  public int getNextPageLength() {
    return 0;
  }

  @Deprecated(forRemoval = true)
  public void setNextPageLength(int value) {}

  @Deprecated(forRemoval = true)
  public void handleHoloRangeUpdates(Player player, Location location) {
    this.updatePlayerPositionByVisibilityRange(player, location);
  }

  @Deprecated(forRemoval = true)
  public void handleHoloUpdates(Player player, Location location) {
    this.updatePlayer(player, location);
  }

  @Deprecated(forRemoval = true)
  public Set<CMIHologram> getAllInRangeByLocExtra(Location location) {
    return this.getAllByVisibilityRange(location);
  }

  @Deprecated(forRemoval = true)
  public Set<CMIHologram> getAllInRangeByLoc(Location location) {
    return this.getAllByUpdateRange(location);
  }

  @Deprecated(forRemoval = true)
  public CMIHologram getByLocExtra(Location location) {
    return this.getByVisibilityRange(location);
  }

  @Deprecated(forRemoval = true)
  public CMIHologram getByLoc(Location location) {
    return this.getByUpdateRange(location);
  }

  @Deprecated(forRemoval = true)
  public void recalculateChunksExtra(CMIHologram cmiHologram) {
    this.recalculateUpdateChunks(cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void addHologram(CMIHologram cmiHologram, boolean flag) {
    this.add(cmiHologram, flag, true);
  }

  @Deprecated(forRemoval = true)
  public void addHologram(CMIHologram cmiHologram, boolean flag, boolean silent) {
    this.add(cmiHologram, flag, silent);
  }

  @Deprecated(forRemoval = true)
  public void addHologram(CMIHologram cmiHologram) {
    this.add(cmiHologram, true, true);
  }

  @Deprecated(forRemoval = true)
  public void removeNearHoloExtra(UUID uuid, CMIHologram cmiHologram) {
    this.removePlayerFromHologram(uuid, cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void addNearHoloExtra(UUID uuid, CMIHologram cmiHologram) {
    this.addToVisibilityRange(uuid, cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void removeNearHolo(UUID uuid, CMIHologram cmiHologram) {
    this.removeFromUpdateRange(uuid, cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void addNearHolo(UUID uuid, CMIHologram cmiHologram) {
    this.addToUpdateRange(uuid, cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void removeHolo(CMIHologram cmiHologram) {
    this.remove(cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public List<CMIHologram> getHologramsByDistance(Location location) {
    return this.getHologramsSortedByDistance(location);
  }

  @Deprecated(forRemoval = true)
  public void addPlayersNearHoloExtra(CMIHologram cmiHologram, boolean flag) {
    this.addPlayersByVisibilityRange(cmiHologram, flag);
  }

  @Deprecated(forRemoval = true)
  public void addPlayersNearHolo(CMIHologram cmiHologram, boolean flag) {
    this.addPlayersByUpdateRange(cmiHologram, flag);
  }

  @Deprecated(forRemoval = true)
  public void resetHoloForAllPlayers(CMIHologram cmiHologram) {
    this.reset(cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void hideHoloForAllPlayers(CMIHologram cmiHologram) {
    this.hide(cmiHologram);
  }

  @Deprecated(forRemoval = true)
  public void removeLastHologramInRange(UUID uuid) {
    this.hideAllHologramsForPlayer(uuid);
  }

  @Deprecated(forRemoval = true)
  public int getHoloCheckInterval() {
    return 1;
  }

  private class hologramChange {
    private Set<CMIHologram> from = new HashSet<>();
    private Set<CMIHologram> to = new HashSet<>();
    private Set<CMIHologram> left = new HashSet<>();
    private Set<CMIHologram> entered = new HashSet<>();

    public hologramChange(
        Set<CMIHologram> set,
        Set<CMIHologram> toSet,
        Set<CMIHologram> leftSet,
        Set<CMIHologram> enteredSet) {
      this.from = set;
      this.to = toSet;
      this.left = leftSet;
      this.entered = enteredSet;
    }

    public Set<CMIHologram> getLeft() {
      return this.left;
    }

    public Set<CMIHologram> getEntered() {
      return this.entered;
    }

    public Set<CMIHologram> getFrom() {
      return this.from;
    }

    public Set<CMIHologram> getTo() {
      return this.to;
    }
  }
}
