package com.Zrips.CMI.Modules.Portals;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Worlds.CMIWorldListener;
import com.Zrips.CMI.Modules.Worlds.UpdateOnWorldLoad;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PortalManager {
  private SortedMap<String, CMIPortal> portals;
  protected Map<String, Map<CuboidArea.ChunkRef, Set<CMIPortal>>> chunkPortals;
  protected Map<String, Map<CuboidArea.ChunkRef, Set<CMIPortal>>> chunkPortalsRange1;
  private Map<UUID, Set<CMIPortal>> playerNearPortals;
  private ConcurrentHashMap<UUID, Set<CMIPortal>> lastPortals;
  private ConcurrentHashMap<CMIPortal, Set<UUID>> lastPortalInRange;
  private int PortalsCheckInterval = 500;
  private int PortalsCheckParticleInterval = 500;
  private List<String> commandsOnTeleport = new ArrayList<>();
  public static boolean performCmd = true;
  private CMI plugin;
  private CMITask sched = null;
  private String fileName = "Portals.yml";
  private CMITask id = null;

  public PortalManager(CMI cmi) {
    this.plugin = cmi;
    this.lastPortals = new ConcurrentHashMap<>();
    this.lastPortalInRange = new ConcurrentHashMap<>();
    this.portals = new TreeMap<>();
    this.chunkPortals = new HashMap<>();
    this.chunkPortalsRange1 = new HashMap<>();
    this.playerNearPortals = new HashMap<>();
    this.tasker();
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  private void tasker() {
    if (Version.isSpigot()) {
      this.sched =
          CMIScheduler.runTimerAsync(
              this.plugin,
              () -> {
                if (this.getLastPortalInRange().isEmpty() && this.sched != null) {
                  this.sched.cancel();
                  this.sched = null;
                } else {
                  this.getLastPortalInRange()
                      .forEach((cmiPortal, set) -> showParticlesForPortal(cmiPortal));
                }
              },
              1L,
              2L);
    }
  }

  private static void showParticlesForPortal(CMIPortal cmiPortal) {
    if (cmiPortal.isEnabled() && cmiPortal.isShowParticles()) {
      Location location = cmiPortal.getArea().getLowLoc();
      Random random = new Random(System.nanoTime());
      if (cmiPortal.getCMIEffect() != null && cmiPortal.getCMIEffect().getParticle() != null) {
        for (int value = 0; value < cmiPortal.getParticleAmount(); value++) {
          double number = random.nextInt(cmiPortal.getArea().getXSize() * 100) / 100.0;
          double offsetY = 0.0;
          switch (cmiPortal.getParticleOrigin()) {
            case Any:
              offsetY = random.nextInt(cmiPortal.getArea().getYSize() * 100) / 100.0;
              break;
            case Top:
              offsetY = cmiPortal.getArea().getYSize();
              break;
            case Bottom:
              offsetY = 0.0;
              break;
            case Center:
              offsetY = cmiPortal.getArea().getYSize() / 2.0;
          }

          double offsetZ = random.nextInt(cmiPortal.getArea().getZSize() * 100) / 100.0;
          if (cmiPortal.getPercentToHide() > 0) {
            int hideRoll = random.nextInt(100 - cmiPortal.getPercentToHide());
            double distanceX = cmiPortal.getArea().getXSize() / 2.0 - number;
            if (distanceX < 0.0) {
              distanceX *= -1.0;
            }

            double percentX = distanceX * 100.0 / (cmiPortal.getArea().getXSize() / 2.0);
            if (cmiPortal.getPercentToHide() > 0 && hideRoll < percentX) {
              continue;
            }

            if (cmiPortal.getParticleOrigin().equals(CMIPortalParticleOrigin.Any)) {
              double distanceY = cmiPortal.getArea().getYSize() / 2.0 - offsetY;
              if (distanceY < 0.0) {
                distanceY *= -1.0;
              }

              double percentY = distanceY * 100.0 / (cmiPortal.getArea().getYSize() / 2.0);
              if (cmiPortal.getPercentToHide() > 0 && hideRoll < percentY) {
                continue;
              }
            }

            double distanceZ = cmiPortal.getArea().getZSize() / 2.0 - offsetZ;
            if (distanceZ < 0.0) {
              distanceZ *= -1.0;
            }

            double percentZ = distanceZ * 100.0 / (cmiPortal.getArea().getZSize() / 2.0);
            if (cmiPortal.getPercentToHide() > 0 && hideRoll < percentZ) {
              continue;
            }
          }

          CMIEffect cmiEffect = cmiPortal.getCMIEffect();
          Location shiftedLocation = location.clone().add(number, offsetY, offsetZ);

          for (UUID uuid : cmiPortal.getParticleForPlayers()) {
            Player player = CMIUser.getOnlinePlayer(uuid);
            if (player != null) {
              cmiEffect.show(player, shiftedLocation);
            }
          }
        }
      }
    }
  }

  public void addPortal(CMIPortal cmiPortal) {
    this.portals.put(cmiPortal.getName().toLowerCase(), cmiPortal);
    this.recalculateChunks(cmiPortal);
  }

  public void recalculateChunks() {
    this.chunkPortals.clear();
    this.chunkPortalsRange1.clear();

    for (var entry : this.portals.entrySet()) {
      this.recalculateChunks((CMIPortal) entry.getValue());
    }
  }

  public void recalculateChunks(final CMIPortal cmiPortal) {
    if (cmiPortal.getArea().getWorld() == null) {
      UpdateOnWorldLoad updateOnWorldLoad =
          new UpdateOnWorldLoad() {
            @Override
            public void update(World world) {
              cmiPortal.getArea().setWorld(world);
              PortalManager.this.recalculateChunks(cmiPortal);
              PortalManager.this.plugin.consoleMessage(
                  "Missing ("
                      + world.getName()
                      + ") world loaded. Recalculating "
                      + cmiPortal.getName()
                      + " portal position");
            }
          };
      CMIWorldListener.delayRecheck(cmiPortal.getWorldName(), updateOnWorldLoad);
    }

    var map = this.chunkPortals.get(cmiPortal.getWorldName());
    if (map == null) {
      map = new HashMap();
    }

    for (CuboidArea.ChunkRef chunkRef : getChunks(cmiPortal)) {
      HashSet<CMIPortal> hashSet = new HashSet();
      if (map.containsKey(chunkRef)) {
        hashSet.addAll((Collection) map.get(chunkRef));
      }

      hashSet.add(cmiPortal);
      map.put(chunkRef, hashSet);
    }

    this.chunkPortals.put(cmiPortal.getWorldName(), map);
    var chunkMap = this.chunkPortalsRange1.get(cmiPortal.getWorldName());
    if (chunkMap == null) {
      chunkMap = new HashMap();
    }

    for (CuboidArea.ChunkRef chunkRef : getChunks(cmiPortal, cmiPortal.getActivationRange())) {
      HashSet<CMIPortal> chunkPortals = new HashSet();
      if (chunkMap.containsKey(chunkRef)) {
        chunkPortals.addAll((Collection) chunkMap.get(chunkRef));
      }

      chunkPortals.add(cmiPortal);
      chunkMap.put(chunkRef, chunkPortals);
    }

    this.chunkPortalsRange1.put(cmiPortal.getWorldName(), chunkMap);
  }

  public CMIPortal getByName(String string) {
    return string == null ? null : this.portals.get(string.toLowerCase());
  }

  public CMIPortal getByLoc(Location location) {
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

    CMIPortal cmiPortal = null;
    CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
    if (!this.chunkPortals.containsKey(string)) {
      return null;
    }

    var map = this.chunkPortals.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMIPortal portal : (Set<CMIPortal>) map.get(chunkRef)) {
        if (portal != null && portal.containsLoc(location)) {
          cmiPortal = portal;
          break;
        }
      }
    }

    return cmiPortal;
  }

  public Set<CMIPortal> getByLocList(Location location) {
    if (location == null) {
      return new HashSet<>();
    }

    World world = location.getWorld();
    if (world == null) {
      return new HashSet<>();
    }

    String string = world.getName();
    if (string == null) {
      return new HashSet<>();
    }

    HashSet<CMIPortal> hashSet = new HashSet();
    CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
    if (!this.chunkPortals.containsKey(string)) {
      return new HashSet<>();
    }

    var map = this.chunkPortals.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMIPortal cmiPortal : (Set<CMIPortal>) map.get(chunkRef)) {
        if (cmiPortal != null && cmiPortal.containsLoc(location)) {
          hashSet.add(cmiPortal);
        }
      }
    }

    return hashSet;
  }

  public Set<CMIPortal> getByLocExtended(Location location) {
    HashSet<CMIPortal> hashSet = new HashSet();
    if (location == null) {
      return hashSet;
    }

    World world = location.getWorld();
    if (world == null) {
      return hashSet;
    }

    String string = world.getName();
    if (string == null) {
      return hashSet;
    }

    CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
    if (!this.chunkPortalsRange1.containsKey(string)) {
      return hashSet;
    }

    var map = this.chunkPortalsRange1.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMIPortal cmiPortal : (Set<CMIPortal>) map.get(chunkRef)) {
        if (cmiPortal != null && cmiPortal.containsLoc(location, cmiPortal.getActivationRange())) {
          hashSet.add(cmiPortal);
        }
      }
    }

    return hashSet;
  }

  public CMIPortal collidesWithPortal(CuboidArea cuboidArea) {
    for (var entry : this.portals.entrySet()) {
      CMIPortal cmiPortal = (CMIPortal) entry.getValue();
      if (cmiPortal.checkCollision(cuboidArea)) {
        return (CMIPortal) entry.getValue();
      }
    }

    return null;
  }

  private static List<CuboidArea.ChunkRef> getChunks(CMIPortal cmiPortal) {
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(cmiPortal.getArea().getChunks());
    return arrayList;
  }

  private static List<CuboidArea.ChunkRef> getChunks(CMIPortal cmiPortal, int value) {
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(cmiPortal.getArea().getChunks(value));
    return arrayList;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Portals.CheckInterval",
        new String[] {
          "Defines in milliseconds how often to check if player entered portal or not",
          "Bigger numbers can help slightly lower server load but small portals, 1 block depth"
              + " without back wall can be passed through without teleportations if player moves"
              + " fast enought"
        });
    this.PortalsCheckInterval = configReader.get("Portals.CheckInterval", 300);
    configReader.addComment(
        "Portals.CheckParticleInterval",
        new String[] {
          "Defines in milliseconds how often to check if player entered portal range for particles"
              + " to apear"
        });
    this.PortalsCheckParticleInterval = configReader.get("Portals.CheckParticleInterval", 500);
    configReader.addComment(
        "Portals.Defaults.PerformCommands",
        new String[] {
          "Should we perform commands without set destination location by default",
          "This only effects newly created portal areas",
          "When set to true at moment you create portal you can enter it and commands defined"
              + " belove will be performed without teleporting you anywhere",
          "This can be change for each portal independently with ingame portal editor"
        });
    performCmd = configReader.get("Portals.Defaults.PerformCommands", true);
    configReader.addComment(
        "Portals.Defaults.Commands", new String[] {"Commands to be performed on teleport event"});
    this.commandsOnTeleport =
        configReader.get(
            "Portals.Defaults.Commands", Arrays.asList("cmi effect [playerName] blindness 2 1 -s"));
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    this.portals.clear();
    this.chunkPortals.clear();
    this.chunkPortalsRange1.clear();
    CMI.getInstance().safeRenameFile("portals.yml", "Saves" + File.separator + this.fileName);
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
      }
    } else {
      YamlConfiguration yamlConfiguration = null;

      try {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
      } catch (Exception | Error value) {
        return;
      }

      if (!yamlConfiguration.getKeys(false).isEmpty()) {
        var map = yamlConfiguration.getValues(false);

        for (Entry entry : map.entrySet()) {
          var portalValues =
              yamlConfiguration.getConfigurationSection((String) entry.getKey()).getValues(false);
          if (portalValues != null) {
            try {
              this.loadMap((String) entry.getKey(), portalValues);
            } catch (Exception exception) {
              Bukkit.getConsoleSender()
                  .sendMessage(
                      ChatColor.RED
                          + "Error in loading portal file for world: "
                          + (String) entry.getKey());
            }
          }
        }

        this.plugin.loadMessage(
            this.portals.size(), "portals", System.currentTimeMillis() - startTime);
      }
    }
  }

  public void loadMap(String string, Map<String, Object> map) {
    if (map != null) {
      for (var entry : map.entrySet()) {
        try {
          CMIPortal cmiPortal = new CMIPortal();
          cmiPortal.setName((String) entry.getKey());
          cmiPortal.setWorldName(string);
          MemorySection memorySection = (MemorySection) entry.getValue();
          if (memorySection.isString("world")) {
            cmiPortal.setWorldName(memorySection.getString("world"));
          }

          if (memorySection.contains("Tp")) {
            if (memorySection.isString("Tp")) {
              cmiPortal.setTpLoc(
                  this.plugin
                      .getPlayerManager()
                      .convertStringToLocation(memorySection.getString("Tp")));
            } else {
              cmiPortal.loadTpLoc(memorySection.get("Tp"));
            }
          }

          cmiPortal.loadBounds(memorySection.getString("Loc"));
          if (memorySection.isString("effect")) {
            cmiPortal.setCMIEffect(
                CMIEffect.deserialize(memorySection.getString("effect").replace(" ", "")));
          }

          if (memorySection.isInt("color")) {
            cmiPortal.getCMIEffect().setColor(Color.fromRGB(memorySection.getInt("color")));
          }

          if (memorySection.isInt("size")) {
            cmiPortal.getCMIEffect().setSize(memorySection.getInt("size"));
          }

          if (memorySection.contains("safeLoc")) {
            if (memorySection.isString("safeLoc")) {
              cmiPortal.setSafeLoc(
                  this.plugin
                      .getPlayerManager()
                      .convertStringToLocation(memorySection.getString("safeLoc")));
            } else {
              cmiPortal.setSafeLoc((Location) memorySection.get("safeLoc"));
            }
          }

          if (memorySection.isString("bungeeServer")) {
            cmiPortal.setBungeeServer(memorySection.getString("bungeeServer"));
          }

          if (memorySection.isBoolean("kickBack")) {
            cmiPortal.setKickBack(memorySection.getBoolean("kickBack"));
          }

          if (memorySection.isBoolean("cancelWarmup")) {
            cmiPortal.setCancelCmdWarmup(memorySection.getBoolean("cancelWarmup"));
          }

          if (memorySection.isBoolean("particlesByPermission")) {
            cmiPortal.setParticlesByPermission(memorySection.getBoolean("particlesByPermission"));
          }

          if (memorySection.isBoolean("exactBungeeLocation")) {
            cmiPortal.setToExactBungeeLocation(memorySection.getBoolean("exactBungeeLocation"));
          }

          if (memorySection.isString("bungeeLocation")) {
            cmiPortal.setBungeeLocation(memorySection.getString("bungeeLocation"));
          }

          if (memorySection.isBoolean("enabled")) {
            cmiPortal.setEnabled(memorySection.getBoolean("enabled"));
          }

          if (memorySection.isBoolean("showParticles")) {
            cmiPortal.setShowParticles(memorySection.getBoolean("showParticles"));
          }

          if (memorySection.isInt("particleAmount")) {
            cmiPortal.setParticleAmount(memorySection.getInt("particleAmount"));
          }

          if (memorySection.isString("particleOrigin")) {
            cmiPortal.setParticleOrigin(
                CMIPortalParticleOrigin.get(memorySection.getString("particleOrigin")));
          }

          if (memorySection.isInt("particleHide")) {
            cmiPortal.setPercentToHide(memorySection.getInt("particleHide"));
          }

          if (memorySection.isInt("activationRange")) {
            cmiPortal.setActivationRange(memorySection.getInt("activationRange"));
          }

          if (memorySection.isBoolean("commandsWithoutTp")) {
            cmiPortal.setPerformCommandsWithoutTp(memorySection.getBoolean("commandsWithoutTp"));
          }

          if (memorySection.isBoolean("requiresPerm")) {
            cmiPortal.setRequiresPermission(memorySection.getBoolean("requiresPerm"));
          }

          if (memorySection.isBoolean("informOnMissingPerm")) {
            cmiPortal.setInformOnMissingPerm(memorySection.getBoolean("informOnMissingPerm"));
          }

          if (memorySection.isList("commands")) {
            cmiPortal.setCommands(memorySection.getStringList("commands"));
          }

          this.addPortal(cmiPortal);
        } catch (Exception | Error value) {
          Bukkit.getConsoleSender()
              .sendMessage(
                  ChatColor.RED
                      + " Failed to load portal ("
                      + (String) entry.getKey()
                      + ")! Reason:"
                      + value.getMessage()
                      + " Error Log:");
          Logger.getLogger(PortalManager.class.getName()).log(Level.SEVERE, null, value);
        }
      }
    }
  }

  public void handlePortalVisualizerUpdates() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.handlePortalVisualizerUpdates(player, null, player.getLocation());
    }
  }

  public boolean handlePortalVisualizerUpdates(
      Player player, Location location, Location destination) {
    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          synchronized (this.lastPortals) {
            var set = this.lastPortals.get(player.getUniqueId());
            var nearby = this.plugin.getPortalManager().getByLocExtended(destination);
            HashSet<CMIPortal> hashSet = new HashSet();
            HashSet<CMIPortal> candidates = new HashSet();
            if (set != null) {
              for (CMIPortal cmiPortal : set) {
                if (!nearby.contains(cmiPortal)) {
                  hashSet.add(cmiPortal);
                }
              }

              for (CMIPortal portal : nearby) {
                if (!set.contains(portal)) {
                  candidates.add(portal);
                }
              }
            } else {
              candidates.addAll(nearby);
            }

            this.lastPortals.put(player.getUniqueId(), nearby);

            for (CMIPortal activePortal : hashSet) {
              this.plugin
                  .getPortalManager()
                  .removeLastPortalInRange(activePortal, player.getUniqueId());
              this.removeNearPortal(player.getUniqueId(), activePortal);
            }

            for (CMIPortal candidatePortal : candidates) {
              this.plugin
                  .getPortalManager()
                  .addLastPortalInRange(candidatePortal, player.getUniqueId());
              this.addNearPortal(player.getUniqueId(), candidatePortal);
            }
          }
        });
    return true;
  }

  public void savePortals() {
    if (this.id == null) {
      this.id =
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                this.save();
                this.id = null;
              },
              100L);
    }
  }

  private void save() {
    File file = new File(this.plugin.getDataFolder(), "Saves" + File.separator + this.fileName);
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    } else {
      ConfigReader configReader = null;

      try {
        configReader = new ConfigReader(file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }

      if (configReader != null) {
        for (var entry : configReader.getC().getValues(false).entrySet()) {
          configReader.getC().set((String) entry.getKey(), null);
        }

        configReader.header(
            Arrays.asList(
                "Command section can utilize same custom variables as scheduler. Check"
                    + " Schedules.yml file for all possible variables."));

        for (var portalEntry : this.portals.entrySet()) {
          CMIPortal cmiPortal = (CMIPortal) portalEntry.getValue();
          String string = cmiPortal.getName().replace(".", "_");
          String worldName = cmiPortal.getWorldName();
          boolean flag = false;
          if (worldName.contains(".")) {
            worldName = worldName.split("\\.", 2)[0];
            flag = true;
          }

          String key = worldName + "." + string;
          configReader.get(key + ".Loc", cmiPortal.getArea().toString());
          if (flag) {
            configReader.get(key + ".world", cmiPortal.getWorldName());
          }

          configReader.get(key + ".enabled", cmiPortal.isEnabled() ? null : false);

          try {
            configReader.get(key + ".effect", cmiPortal.getCMIEffect().serialize());
          } catch (Throwable throwable) {
            throwable.printStackTrace();
            configReader.get(
                key + ".effect", cmiPortal.getCMIEffect().getParticle().getName().replace(" ", ""));
            if (cmiPortal.getCMIEffect().getParticle().isColored()) {
              configReader.get(key + ".color", cmiPortal.getCMIEffect().getColor().asRGB());
            }

            if (cmiPortal.getCMIEffect().getSize() != 1) {
              configReader.get(key + ".size", cmiPortal.getCMIEffect().getSize());
            }
          }

          configReader.get(key + ".showParticles", cmiPortal.isShowParticles() ? null : false);
          if (cmiPortal.getSafeLoc() != null) {
            configReader.get(
                key + ".safeLoc",
                this.plugin.getPlayerManager().convertLocToString(cmiPortal.getSafeLoc()));
          }

          if (cmiPortal.getBungeeServer() != null) {
            configReader.get(key + ".bungeeServer", cmiPortal.getBungeeServer());
          }

          if (cmiPortal.getBungeeServer() != null) {
            configReader.get(key + ".exactBungeeLocation", cmiPortal.isToExactBungeeLocation());
          }

          if (cmiPortal.getBungeeLocation() != null) {
            configReader.get(key + ".bungeeLocation", cmiPortal.getBungeeLocation());
          }

          if (cmiPortal.getTpLoc() != null) {
            configReader.get(
                key + ".Tp",
                this.plugin.getPlayerManager().convertLocToString(cmiPortal.getTpLoc()));
          }

          configReader.get(key + ".kickBack", cmiPortal.isKickBack() ? null : false);
          configReader.get(key + ".cancelWarmup", cmiPortal.isCancelCmdWarmup() ? true : null);
          configReader.get(
              key + ".particlesByPermission", cmiPortal.isParticlesByPermission() ? true : null);
          configReader.get(key + ".requiresPerm", cmiPortal.isRequiresPermission() ? true : null);
          configReader.get(
              key + ".informOnMissingPerm", cmiPortal.isInformOnMissingPerm() ? true : null);
          configReader.get(
              key + ".particleOrigin",
              cmiPortal.getParticleOrigin() == CMIPortalParticleOrigin.Any
                  ? null
                  : cmiPortal.getParticleOrigin().toString());
          configReader.get(
              key + ".particleAmount",
              cmiPortal.getParticleAmount() == 10 ? null : cmiPortal.getParticleAmount());
          configReader.get(
              key + ".particleHide",
              cmiPortal.getPercentToHide() == 0 ? null : cmiPortal.getPercentToHide());
          configReader.get(
              key + ".activationRange",
              cmiPortal.getActivationRange() == 16 ? null : cmiPortal.getActivationRange());
          configReader.get(key + ".commandsWithoutTp", cmiPortal.getPerformCommandsWithoutTp());
          configReader.get(key + ".commands", cmiPortal.getCommands());
        }

        configReader.save();
      }
    }
  }

  public synchronized void removeLastPortalInRange(CMIPortal cmiPortal, UUID uuid) {
    var set = this.lastPortalInRange.get(cmiPortal);
    if (set != null) {
      set.remove(uuid);
      synchronized (this.lastPortalInRange) {
        if (set.isEmpty()) {
          this.lastPortalInRange.remove(cmiPortal);
        } else {
          this.lastPortalInRange.put(cmiPortal, set);
        }
      }
    }
  }

  public synchronized void removeLastPortalInRange(UUID uuid) {
    var set = this.getPlayerNearPortals().get(uuid);
    var map = this.getLastPortalInRange();
    if (set != null) {
      for (CMIPortal cmiPortal : set) {
        Set portalSet = (Set) map.get(cmiPortal);
        if (portalSet != null) {
          portalSet.remove(uuid);
          synchronized (this.lastPortalInRange) {
            if (portalSet.isEmpty()) {
              this.lastPortalInRange.remove(cmiPortal);
            }
          }
        }
      }
    }

    this.lastPortals.remove(uuid);
  }

  public synchronized void addLastPortalInRange(CMIPortal cmiPortal, UUID uuid) {
    var set = this.lastPortalInRange.get(cmiPortal);
    if (set == null) {
      set = new HashSet();
    }

    set.add(uuid);
    synchronized (this.lastPortalInRange) {
      this.lastPortalInRange.put(cmiPortal, set);
    }

    if (this.sched == null) {
      this.tasker();
    }
  }

  public synchronized Map<CMIPortal, Set<UUID>> getLastPortalInRange() {
    return new HashMap<>(this.lastPortalInRange);
  }

  public synchronized Map<UUID, Set<CMIPortal>> getPlayerNearPortals() {
    return new HashMap<>(this.playerNearPortals);
  }

  public SortedMap<String, CMIPortal> getPortals() {
    return this.portals;
  }

  public List<CMIPortal> getPortalsByDistance(Location location) {
    if (location == null) {
      location = new Location((World) Bukkit.getWorlds().get(0), 0.0, 0.0, 0.0);
    } else {
      try {
        location.setWorld(Bukkit.getWorld(location.getWorld().getUID()));
      } catch (Throwable throwable) {
      }
    }

    HashMap<CMIPortal, Integer> hashMap = new HashMap();
    ArrayList<CMIPortal> arrayList = new ArrayList();

    for (var entry : this.portals.entrySet()) {
      if (!location.getWorld().getName().equals(((CMIPortal) entry.getValue()).getWorldName())) {
        arrayList.add((CMIPortal) entry.getValue());
      } else {
        hashMap.put(
            entry.getValue(),
            (int) ((CMIPortal) entry.getValue()).getArea().getMiddleLocation().distance(location));
      }
    }

    hashMap = (HashMap) this.plugin.getSortingManager().sortByValueObject((Map) hashMap);
    ArrayList<CMIPortal> results = new ArrayList();

    for (Entry hashEntry : hashMap.entrySet()) {
      results.add((CMIPortal) hashEntry.getKey());
    }

    if (!arrayList.isEmpty()) {
      results.addAll(arrayList);
    }

    return results;
  }

  public synchronized void removePortal(CMIPortal cmiPortal) {
    this.portals.remove(cmiPortal.getName().toLowerCase());
    synchronized (this.lastPortalInRange) {
      this.lastPortalInRange.remove(cmiPortal);
    }

    this.recalculateChunks();
    this.savePortals();
  }

  public int getPortalsCheckInterval() {
    return this.PortalsCheckInterval;
  }

  public int getPortalsCheckParticleInterval() {
    return this.PortalsCheckParticleInterval;
  }

  public List<String> getCommandsOnTeleport() {
    return this.commandsOnTeleport;
  }

  public boolean isNearPortal(UUID uuid) {
    if (!this.getPlayerNearPortals().isEmpty() && uuid != null) {
      var set = this.getPlayerNearPortals().get(uuid);
      return set != null && !set.isEmpty();
    } else {
      return false;
    }
  }

  public synchronized void addNearPortal(UUID uuid, CMIPortal cmiPortal) {
    var set = this.getPlayerNearPortals().get(uuid);
    if (set == null) {
      set = new HashSet();
    }

    set.add(cmiPortal);
    cmiPortal.updateParticleLimitations(CMIUser.getOnlinePlayer(uuid));
    this.playerNearPortals.put(uuid, set);
  }

  public synchronized void removeNearPortal(UUID uuid, CMIPortal cmiPortal) {
    var set = this.getPlayerNearPortals().get(uuid);
    if (set != null) {
      set.remove(cmiPortal);
      cmiPortal.removeParticleLimitations(uuid);
      this.playerNearPortals.put(uuid, set);
    }
  }

  public synchronized void removeNearPortal(UUID uuid) {
    var set = this.playerNearPortals.remove(uuid);
    if (set != null) {
      for (CMIPortal cmiPortal : set) {
        cmiPortal.removeParticleLimitations(uuid);
      }
    }
  }

  public void forceUpdate(CMIPortal cmiPortal) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      this.forceUpdate(player.getUniqueId(), cmiPortal);
    }
  }

  public void forceUpdate(UUID uuid, CMIPortal cmiPortal) {
    if (cmiPortal != null && uuid != null) {
      this.plugin.getPortalManager().removeNearPortal(uuid, cmiPortal);
      this.plugin.getPortalManager().removeLastPortalInRange(uuid);
      this.plugin.getPortalManager().handlePortalVisualizerUpdates();
    }
  }
}
