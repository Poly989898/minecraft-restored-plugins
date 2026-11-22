package com.Zrips.CMI.Modules.Jail;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Containers.CMIUserJailData;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.commands.list.jail;
import com.Zrips.CMI.events.CMIPlayerJailEvent;
import com.Zrips.CMI.events.CMIPlayerUnjailEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Time.CMITimeManager;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class JailManager {
  private SortedMap<String, CMIJail> jails;
  protected Map<String, Map<CuboidArea.ChunkRef, Set<CMIJail>>> chunkJails;
  private Set<CMIUser> onlineInjail = new HashSet<>();
  protected ConcurrentHashMap<UUID, CMIUserJailData> jailed = new ConcurrentHashMap<>();
  private int JailCheckInterval = 500;
  private Integer DefaultTime = 500;
  private Integer JailChatRange = 20;
  private boolean CountWhileOffline = false;
  private boolean NoAfk = false;
  private boolean PreventDamage = true;
  private boolean noHunger = true;
  private List<String> cmdsOnUnJail;
  private List<String> cmdsOnJail;
  private List<String> whiteList;
  private CMI plugin;
  private CMITask sched = null;
  private String fileName = "Jails.yml";

  public JailManager(CMI cmi) {
    this.plugin = cmi;
    this.jails = new TreeMap<>();
    this.chunkJails = new HashMap<>();
  }

  public synchronized CMIUserJailData getJailData(UUID uuid) {
    return this.jailed.get(uuid);
  }

  public synchronized CMIUserJailData getOrCreateJailData(UUID uuid) {
    return this.jailed.computeIfAbsent(uuid, key -> new CMIUserJailData());
  }

  public void recheckAllOnline() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
      if (cmiUser.isJailed()) {
        this.plugin.getJailManager().placePlayerIntoJail(cmiUser);
      }
    }
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  private void tasker() {
    if (this.sched == null) {
      this.sched =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (this.onlineInjail.isEmpty()) {
                  this.stop();
                } else {
                  ArrayList<CMIUser> arrayList = null;

                  for (CMIUser cmiUser : this.onlineInjail) {
                    if (!this.NoAfk || !cmiUser.isAfk()) {
                      if (!cmiUser.isOnline()) {
                        this.onlineInjail.remove(cmiUser);
                      } else {
                        cmiUser.setJailedForTime(cmiUser.getJailedForTime() - 1000L);
                        if (!cmiUser.isJailed()) {
                          if (arrayList == null) {
                            arrayList = new ArrayList<>();
                          }

                          arrayList.add(cmiUser);
                        }
                      }
                    }
                  }

                  if (arrayList != null) {
                    for (CMIUser prisoner : arrayList) {
                      prisoner.unjail();
                    }
                  }
                }
              },
              0L,
              20L);
    }
  }

  public boolean isOnlineAndInJail(CMIUser cmiUser) {
    return cmiUser != null && this.onlineInjail.contains(cmiUser);
  }

  public boolean anyJailedOnline() {
    return !this.onlineInjail.isEmpty();
  }

  public void removeOnline(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.onlineInjail.remove(cmiUser);
    }
  }

  public void addOnline(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.onlineInjail.add(cmiUser);
      this.tasker();
    }
  }

  public void addJail(CMIJail cmiJail) {
    this.jails.put(cmiJail.getName().toLowerCase(), cmiJail);
    this.recalculateChunks(cmiJail);
  }

  public void recalculateChunks() {
    this.chunkJails.clear();

    for (var entry : this.jails.entrySet()) {
      this.recalculateChunks((CMIJail) entry.getValue());
    }
  }

  public void recalculateChunks(CMIJail cmiJail) {
    if (cmiJail.getArea().getWorldName() != null) {
      var map = this.chunkJails.get(cmiJail.getArea().getWorldName());
      if (map == null) {
        map = new HashMap();
      }

      for (CuboidArea.ChunkRef chunkRef : getChunks(cmiJail)) {
        HashSet<CMIJail> hashSet = new HashSet();
        if (map.containsKey(chunkRef)) {
          hashSet.addAll((Collection) map.get(chunkRef));
        }

        hashSet.add(cmiJail);
        map.put(chunkRef, hashSet);
      }

      this.chunkJails.put(cmiJail.getArea().getWorldName(), map);
    }
  }

  public CMIJail getByName(String string) {
    return string == null ? null : this.jails.get(string.toLowerCase());
  }

  public CMIJail getByLoc(Location location) {
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

    CMIJail cmiJail = null;
    CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
    if (!this.chunkJails.containsKey(string)) {
      return null;
    }

    var map = this.chunkJails.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMIJail jail : (Set<CMIJail>) map.get(chunkRef)) {
        if (jail != null && jail.containsLoc(location)) {
          cmiJail = jail;
          break;
        }
      }
    }

    return cmiJail;
  }

  public CMIJail collidesWithJail(CuboidArea cuboidArea) {
    return this.collidesWithJail(cuboidArea, null);
  }

  public CMIJail collidesWithJail(CuboidArea cuboidArea, CMIJail cmiJail) {
    for (var entry : this.jails.entrySet()) {
      CMIJail jail = (CMIJail) entry.getValue();
      if (jail.checkCollision(cuboidArea) && (cmiJail == null || !cmiJail.equals(jail))) {
        return (CMIJail) entry.getValue();
      }
    }

    return null;
  }

  private static List<CuboidArea.ChunkRef> getChunks(CMIJail cmiJail) {
    return cmiJail.getArea().getChunks();
  }

  private static List<CuboidArea.ChunkRef> getChunks(CMIJail cmiJail, int value) {
    ArrayList arrayList = new ArrayList();
    arrayList.addAll(cmiJail.getArea().getChunks(value));
    return arrayList;
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Jail.CheckInterval",
        new String[] {
          "Defines in milliseconds how often to check if player leaves jail area",
          "Bigger numbers can help slightly lower server load"
        });
    this.JailCheckInterval = configReader.get("Jail.CheckInterval", 500);
    configReader.addComment(
        "Jail.DefaultTime",
        new String[] {"Defines default jail time when time is not povided with command"});
    this.DefaultTime = configReader.get("Jail.DefaultTime", 300);
    configReader.addComment(
        "Jail.ChatRange",
        new String[] {
          "Chat range in blocks while player is in jail",
          "Set to 0 to allow talking",
          "set to -1 to prevent talking in general while jailed"
        });
    this.JailChatRange = configReader.get("Jail.ChatRange", 20);
    configReader.addComment(
        "Jail.CountWhileOffline",
        new String[] {
          "When set to true jail time will decreese while player is offline",
          "When set to false jail time will only be counted while player is online"
        });
    this.CountWhileOffline = configReader.get("Jail.CountWhileOffline", false);
    configReader.addComment(
        "Jail.NoAfk",
        new String[] {
          "When set to true jail time will not decreese if player gets into afk mode while being"
              + " jailed",
          "When set to false, time will pass normally"
        });
    this.NoAfk = configReader.get("Jail.NoAfk", false);
    configReader.addComment(
        "Jail.PreventDamage",
        new String[] {"Do you want to prevent players damage while he is in jail"});
    this.PreventDamage = configReader.get("Jail.PreventDamage", true);
    configReader.addComment(
        "Jail.PreventHunger",
        new String[] {"Do you want to prevent players hunger while he is in jail"});
    this.noHunger = configReader.get("Jail.PreventHunger", true);
    configReader.addComment(
        "Jail.Commands.OnJail", new String[] {"Commands to be performed when player gets jailed"});
    this.cmdsOnJail = configReader.get("Jail.Commands.OnJail", Arrays.asList(""));
    configReader.addComment(
        "Jail.Commands.OnUnJail",
        new String[] {"Commands to be performed when player gets unjailed"});
    this.cmdsOnUnJail = configReader.get("Jail.Commands.OnUnJail", Arrays.asList(""));
    configReader.addComment(
        "Jail.WhiteListedCmds",
        new String[] {
          "If you want to white list base CMI command then use its full name and not its shortened"
              + " alias version"
        });
    this.whiteList =
        configReader.get("Jail.WhiteListedCmds", Arrays.asList("cmi msg", "cmi reply"));

    for (int value = 0; value < this.whiteList.size(); value++) {
      this.whiteList.set(value, this.whiteList.get(value).toLowerCase());
    }
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    this.jails.clear();
    this.chunkJails.clear();
    this.plugin.safeRenameFile(
        this.fileName.toLowerCase(), "Saves" + File.separator + this.fileName);
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
      } catch (Exception exception) {
        return;
      }

      if (!yamlConfiguration.getKeys(false).isEmpty()) {
        var map = yamlConfiguration.getValues(false);

        for (Entry entry : map.entrySet()) {
          var jailValues = yamlConfiguration.getValues(false);
          if (jailValues != null) {
            try {
              this.loadMap(jailValues);
            } catch (Exception exception2) {
              CMIMessages.sendMessage(
                  Bukkit.getConsoleSender(),
                  ChatColor.RED
                      + "Error in loading jails file for world: "
                      + (String) entry.getKey());
            }
          }
        }

        this.plugin.loadMessage(this.jails.size(), "jails", System.currentTimeMillis() - startTime);
      }
    }
  }

  public void loadMap(Map<String, Object> map) {
    if (map != null) {
      for (var entry : map.entrySet()) {
        try {
          CMIJail cmiJail = new CMIJail();
          cmiJail.setName((String) entry.getKey());
          MemorySection memorySection = (MemorySection) entry.getValue();
          cmiJail.loadBounds(memorySection.getString("Loc"));
          if (memorySection.contains("safeLoc")) {
            cmiJail.setOutsideLocation(
                this.plugin
                    .getPlayerManager()
                    .convertStringToLocation(memorySection.getString("safeLoc")));
          }

          if (memorySection.contains("cells")) {
            for (String string : memorySection.getConfigurationSection("cells").getKeys(false)) {
              MemorySection cellSection = (MemorySection) memorySection.get("cells." + string);
              Integer integer = null;

              try {
                integer = Integer.parseInt(string);
                CMIJailCell cmiJailCell = new CMIJailCell(cmiJail);
                cmiJailCell.setId(integer);
                cmiJailCell.setWorld(cmiJail.getArea().getWorldName());
                if (cellSection.contains("location")) {
                  cmiJailCell.setTeleportInLocation(
                      this.plugin
                          .getPlayerManager()
                          .convertStringToLocation(cellSection.getString("location")));
                }

                cmiJail.addCell(cmiJailCell);
              } catch (Exception exception) {
                this.plugin.consoleMessage("&cFailed to load " + string);
              }
            }
          }

          this.addJail(cmiJail);
        } catch (Exception exception2) {
          CMIMessages.sendMessage(
              Bukkit.getConsoleSender(),
              ChatColor.RED
                  + " Failed to load jail ("
                  + (String) entry.getKey()
                  + ")! Reason:"
                  + exception2.getMessage()
                  + " Error Log:");
          Logger.getLogger(JailManager.class.getName()).log(Level.SEVERE, null, exception2);
        }
      }
    }
  }

  public void save() {
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

        for (var jailEntry : this.jails.entrySet()) {
          CMIJail cmiJail = (CMIJail) jailEntry.getValue();
          configReader.get(
              cmiJail.getName() + ".Loc",
              cmiJail.getArea().getWorldName() + ":" + cmiJail.getArea().toString());
          if (cmiJail.getOutsideLocation() != null) {
            configReader.get(
                cmiJail.getName() + ".safeLoc",
                this.plugin.getPlayerManager().convertLocToString(cmiJail.getOutsideLocation()));
          }

          for (var cellEntry : cmiJail.getCells().entrySet()) {
            if (((CMIJailCell) cellEntry.getValue()).getTeleportInLocation() != null) {
              configReader.get(
                  cmiJail.getName() + ".cells." + cellEntry.getKey() + ".location",
                  this.plugin
                      .getPlayerManager()
                      .convertLocToString(
                          ((CMIJailCell) cellEntry.getValue()).getTeleportInLocation()));
            }
          }
        }

        configReader.save();
      }
    }
  }

  public SortedMap<String, CMIJail> getJails() {
    return this.jails;
  }

  public List<CMIJail> getJailsByDistance(Location location) {
    HashMap<CMIJail, Integer> hashMap = new HashMap();
    ArrayList<CMIJail> arrayList = new ArrayList();

    for (var entry : this.jails.entrySet()) {
      if (location != null
          && location.getWorld() != null
          && !location
              .getWorld()
              .getName()
              .equals(((CMIJail) entry.getValue()).getArea().getWorldName())) {
        arrayList.add((CMIJail) entry.getValue());
      } else if (!((CMIJail) entry.getValue()).getCells().isEmpty()
          && location != null
          && location.getWorld() != null) {
        hashMap.put(
            entry.getValue(),
            (int)
                new CMILocation(((CMIJail) entry.getValue()).getArea().getMiddleLocation())
                    .distance(location));
      }
    }

    hashMap = (HashMap) this.plugin.getSortingManager().sortByValueObject((Map) hashMap);
    ArrayList<CMIJail> jailsByName = new ArrayList();

    for (Entry mapEntry : hashMap.entrySet()) {
      jailsByName.add((CMIJail) mapEntry.getKey());
    }

    if (!arrayList.isEmpty()) {
      jailsByName.addAll(arrayList);
    }

    return jailsByName;
  }

  public void removeJail(CMIJail cmiJail) {
    this.jails.remove(cmiJail.getName().toLowerCase());
    this.recalculateChunks();
    this.save();
  }

  public int getJailsCheckInterval() {
    return this.JailCheckInterval;
  }

  private boolean isCellOk(CMIJailCell cmiJailCell) {
    if (cmiJailCell == null) {
      return false;
    } else {
      CMIJail cmiJail = this.getByName(cmiJailCell.getJail().getName());
      if (cmiJail != null && cmiJail.equals(cmiJailCell.getJail())) {
        CMIJailCell cellCopy = cmiJail.getCells().get(cmiJailCell.getId());
        return cellCopy == null ? false : cellCopy.equals(cmiJailCell);
      } else {
        return false;
      }
    }
  }

  private boolean isJailOk(CMIJail cmiJail) {
    return cmiJail != null && this.getByName(cmiJail.getName()) != null;
  }

  public CMIJailCell getValidCell(CMIUser cmiUser) {
    return this.getValidCell(cmiUser, null, null);
  }

  public CMIJailCell getValidCell(CMIUser cmiUser, CMIJail cmiJail, Integer integer) {
    if (cmiUser.getCell() != null && this.isCellOk(cmiUser.getCell())) {
      return cmiUser.getCell();
    }

    CMIJail nearestJail =
        cmiUser.getCell() != null
            ? (this.isJailOk(cmiUser.getCell().getJail()) ? cmiUser.getCell().getJail() : null)
            : null;
    CMIJailCell cmiJailCell = null;
    if (nearestJail == null) {
      if (cmiJail != null) {
        nearestJail = cmiJail;
      } else {
        for (CMIJail jailCandidate : this.getJailsByDistance(cmiUser.getLocation())) {
          if (!jailCandidate.getCells().isEmpty()) {
            nearestJail = jailCandidate;
            break;
          }
        }
      }
    }

    if (nearestJail == null) {
      return null;
    }

    if (integer != null) {
      cmiJailCell = nearestJail.getCells().get(integer);
    }

    if (cmiJailCell == null) {
      int value = Integer.MAX_VALUE;
      CMIJailCell selectedCell = null;
      var arrayList = new ArrayList<>(nearestJail.getCells().keySet());
      Collections.shuffle(arrayList);

      for (Integer cellId : arrayList) {
        CMIJailCell candidateCell = nearestJail.getCells().get(cellId);
        if (candidateCell != null && candidateCell.getTeleportInLocation() != null) {
          if (candidateCell.getJailed().isEmpty()) {
            selectedCell = candidateCell;
            break;
          }

          if (selectedCell == null || value > candidateCell.getJailed().size()) {
            selectedCell = candidateCell;
            value = candidateCell.getJailed().size();
          }
        }
      }

      cmiJailCell = selectedCell;
    }

    return cmiJailCell;
  }

  public void refreshBossBar(CMIUser cmiUser) {
    if (cmiUser != null) {
      this.hideBossBar(cmiUser);
      this.showBossBar(cmiUser);
    }
  }

  public void showBossBar(CMIUser cmiUser) {
    if (!this.isNoAfk() || !cmiUser.isAfk()) {
      if (cmiUser.isJailed()) {
        BossBarInfo bossBarInfo = new BossBarInfo(cmiUser.getPlayer(), "CMIJailBar");
        Double remainingSeconds = cmiUser.getJailedForTime() / 1000.0 - 1.0;
        bossBarInfo.setKeepForTicks(22);
        bossBarInfo.setColor(BarColor.GREEN);
        bossBarInfo.setTitleOfBar(CMILC.getIM(jail.class, "bossbar"));
        bossBarInfo.setAdjustPerc(-(1.0 / remainingSeconds));
        bossBarInfo.setPercentage(remainingSeconds, remainingSeconds);
        bossBarInfo.setStyle(BarStyle.SEGMENTED_20);
        bossBarInfo.setAuto(20);
        cmiUser.addBossBar(bossBarInfo);
      }
    }
  }

  public void hideBossBar(CMIUser cmiUser) {
    if (cmiUser != null) {
      cmiUser.removeBossBar(cmiUser.getBossBar("CMIJailBar"));
    }
  }

  public void removePlayerFromJail(CMIUser cmiUser) {
    if (cmiUser != null) {
      CMIJailCell cmiJailCell = cmiUser.getCell();
      this.onlineInjail.remove(cmiUser);
      cmiUser.setJailedForTime(0L);
      cmiUser.setCell(null);
      CMIPlayerUnjailEvent cmiPlayerUnjailEvent = new CMIPlayerUnjailEvent(cmiUser, cmiJailCell);
      Bukkit.getPluginManager().callEvent(cmiPlayerUnjailEvent);
      this.hideBossBar(cmiUser);
      if (cmiJailCell != null) {
        cmiJailCell.removeJailed(cmiUser.getUniqueId());
        if (cmiJailCell.getJail() != null) {
          if (cmiJailCell.getJail().getOutsideLocation() == null) {
            this.processOnUnjailCommands(cmiUser);
          } else {
            if (cmiUser.isOnline()) {
              this.plugin
                  .getTeleportHandler()
                  .teleportPlayer(cmiUser.getPlayer(), cmiJailCell.getJail().getOutsideLocation());
            } else {
              this.plugin
                  .getNMS()
                  .setMiscLocation(cmiUser.getPlayer(), cmiJailCell.getJail().getOutsideLocation());
            }

            this.processOnUnjailCommands(cmiUser);
          }
        }
      }
    }
  }

  private void processOnUnjailCommands(CMIUser cmiUser) {
    Snd snd = new Snd();
    snd.setSender(cmiUser).setTarget(cmiUser);
    ArrayList<String> arrayList = new ArrayList();

    for (String string : this.cmdsOnUnJail) {
      String processedCmd = this.plugin.getLM().updateSnd(snd, string);
      arrayList.add(processedCmd);
    }

    if (!arrayList.isEmpty()) {
      this.plugin
          .getSpecializedCommandManager()
          .processCmds(arrayList, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
    }
  }

  public void placePlayerIntoJail(CMIUser cmiUser) {
    this.placePlayerIntoJail(cmiUser, null, null, cmiUser.getJailedForTime());
  }

  public boolean placePlayerIntoJail(
      CMIUser cmiUser, CMIJail cmiJail, Integer integer, Long jailedTime) {
    if (cmiJail != null || integer != null) {
      cmiUser.setCell(null);
    }

    CMIJailCell cmiJailCell = cmiUser.getCell();
    if (!this.isCellOk(cmiJailCell)) {
      cmiJailCell = this.getValidCell(cmiUser, cmiJail, integer);
    }

    if (cmiJailCell != null
        && cmiJailCell.getTeleportInLocation() != null
        && cmiJailCell.getTeleportInLocation().getWorld() != null) {
      CMIPlayerJailEvent cmiPlayerJailEvent = new CMIPlayerJailEvent(cmiUser, cmiJailCell);
      Bukkit.getPluginManager().callEvent(cmiPlayerJailEvent);
      if (cmiPlayerJailEvent.isCancelled()) {
        return false;
      }

      cmiJailCell = cmiPlayerJailEvent.getCell();
      if (cmiJailCell != null
          && cmiJailCell.getTeleportInLocation() != null
          && cmiJailCell.getTeleportInLocation().getWorld() != null) {
        if (cmiUser.isOnline()) {
          cmiJailCell.teleportToCell(cmiUser);
          cmiUser.getPlayer().setFlying(false);
          cmiUser.getPlayer().setAllowFlight(false);
          this.onlineInjail.add(cmiUser);
        }

        cmiJailCell.addJailed(cmiUser.getUniqueId());
        cmiUser.setCell(cmiJailCell);
        if (Version.isCurrentHigher(Version.v1_8_R3)) {
          CMIScheduler.runTask(this.plugin, () -> this.refreshBossBar(cmiUser));
        }

        Snd snd = new Snd();
        snd.setSender(cmiUser).setTarget(cmiUser);
        ArrayList<String> arrayList = new ArrayList();

        for (String string : this.cmdsOnJail) {
          String processedCmd = this.plugin.getLM().updateSnd(snd, string);
          arrayList.add(processedCmd);
        }

        if (!arrayList.isEmpty()) {
          this.plugin
              .getSpecializedCommandManager()
              .processCmds(arrayList, cmiUser.isOnline() ? cmiUser.getPlayer() : null);
        }

        this.tasker();
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public void informAboutLeftTime(Player player) {
    this.informAboutLeftTime(this.plugin.getPlayerManager().getUser(player));
  }

  public void informAboutLeftTime(CMIUser cmiUser) {
    if (cmiUser.isJailed()) {
      long longValue = cmiUser.getJailedForTime();
      CMILC.sendMessage(
          cmiUser,
          CMILC.getIM(jail.class, "leftTime", "[time]", CMITimeManager.to24hourShort(longValue)));
    }
  }

  public boolean canUseCommand(String string) {
    string = string.toLowerCase();
    if (string.contains("/")) {
      string = string.substring(1);
    }

    for (String whiteListEntry : this.whiteList) {
      if (string.startsWith(whiteListEntry)) {
        return true;
      }
    }

    return false;
  }

  public Integer getDefaultTime() {
    return this.DefaultTime;
  }

  public void setDefaultTime(int value) {
    this.DefaultTime = value;
  }

  public Integer getJailChatRange() {
    return this.JailChatRange;
  }

  public void setJailChatRange(Integer integer) {
    this.JailChatRange = integer;
  }

  public boolean isNoAfk() {
    return this.NoAfk;
  }

  public boolean isCountWhileOffline() {
    return this.CountWhileOffline;
  }

  public void setCountWhileOffline(boolean flag) {
    this.CountWhileOffline = flag;
  }

  public boolean isPreventDamage() {
    return this.PreventDamage;
  }

  public boolean isNoHunger() {
    return this.noHunger;
  }
}
