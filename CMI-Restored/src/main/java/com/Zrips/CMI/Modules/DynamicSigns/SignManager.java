package com.Zrips.CMI.Modules.DynamicSigns;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.Portals.CuboidArea;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.Modules.Worlds.CMIWorldListener;
import com.Zrips.CMI.Modules.Worlds.UpdateOnWorldLoad;
import com.Zrips.CMI.commands.list.dsign;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.CMIGui;
import net.Zrips.CMILib.GUI.CMIGuiButton;
import net.Zrips.CMILib.GUI.GUIManager.GUIClickType;
import net.Zrips.CMILib.GUI.GUIManager.GUIRows;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SignManager {
  private Set<CMISign> signs;
  protected Map<String, Map<CuboidArea.ChunkRef, Set<CMISign>>> chunkSignsRange;
  private Map<UUID, Set<CMISign>> playerNearSigns;
  private Map<CMISign, Set<UUID>> lastSignInRange;
  private boolean signRequireShift = false;
  private boolean signDialogEditor = false;
  private int SignRangeCheckInterval = 500;
  private HashMap<CMIChatColor, CMIChatColor> colorChange = new HashMap<>();
  private CMITask saveId = null;
  private CMI plugin;
  private CMITask sched = null;
  private List<String> signEditBlackList = new ArrayList<>();
  private String fileName = "DynamicSigns.yml";
  boolean saving = false;

  public SignManager(CMI cmi) {
    this.plugin = cmi;
    this.lastSignInRange = new HashMap<>();
    this.signs = new HashSet<>();
    this.chunkSignsRange = new HashMap<>();
    this.playerNearSigns = new HashMap<>();
  }

  public void stop() {
    if (this.saveId != null) {
      this.saveId.cancel();
      this.saveId = null;
      this.saveSigns();
    }

    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  public void addSign(CMISign cmiSign) {
    this.signs.add(cmiSign);
    this.recalculateChunks(cmiSign);
  }

  public void recalculateChunks() {
    this.chunkSignsRange.clear();

    for (CMISign cmiSign : this.signs) {
      this.recalculateChunks(cmiSign);
    }
  }

  public void recalculateChunks(final CMISign cmiSign) {
    if (cmiSign.getLoc().getWorld() == null) {
      UpdateOnWorldLoad updateOnWorldLoad =
          new UpdateOnWorldLoad() {
            @Override
            public void update(World world) {
              cmiSign.getLoc().setWorld(world);
              SignManager.this.recalculateChunks(cmiSign);
              SignManager.this.plugin.consoleMessage(
                  "Missing ("
                      + world.getName()
                      + ") world loaded. Recalculating dynamic sign position");
            }
          };
      CMIWorldListener.delayRecheck(cmiSign.getLoc().getWorldName(), updateOnWorldLoad);
    } else {
      var completableFuture = getChunks(cmiSign);
      completableFuture.thenAccept(
          object -> {
            var map = this.chunkSignsRange.get(cmiSign.getLoc().getWorldName());
            if (map == null) {
              map = new HashMap();
            }

            for (CuboidArea.ChunkRef chunkRef : object) {
              HashSet<CMISign> hashSet = new HashSet();
              if (map.containsKey(chunkRef)) {
                hashSet.addAll((Collection) map.get(chunkRef));
              }

              hashSet.add(cmiSign);
              map.put(chunkRef, hashSet);
            }

            this.chunkSignsRange.put(cmiSign.getLoc().getWorldName(), map);
          });
    }
  }

  public CMISign getByLoc(Location location) {
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

    CuboidArea.ChunkRef chunkRef = new CuboidArea.ChunkRef(location);
    if (!this.chunkSignsRange.containsKey(string)) {
      return null;
    }

    var map = this.chunkSignsRange.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMISign cmiSign : (Set<CMISign>) map.get(chunkRef)) {
        if (cmiSign != null && cmiSign.getLoc().toString().equals(location.toString())) {
          return cmiSign;
        }
      }
    }

    return null;
  }

  public Set<CMISign> getAllInRangeByLoc(Location location) {
    HashSet<CMISign> hashSet = new HashSet();
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
    if (!this.chunkSignsRange.containsKey(string)) {
      return hashSet;
    }

    var map = this.chunkSignsRange.get(string);
    if (map.containsKey(chunkRef)) {
      for (CMISign cmiSign : (Set<CMISign>) map.get(chunkRef)) {
        if (cmiSign != null && cmiSign.getBasicArea().containsLoc(location)) {
          hashSet.add(cmiSign);
        }
      }
    }

    return hashSet;
  }

  public void handleSignUpdates(Player player, Location location) {
    var set = this.playerNearSigns.get(player.getUniqueId());
    var nearbySigns = this.getAllInRangeByLoc(location);
    HashSet<CMISign> hashSet = new HashSet();
    HashSet<CMISign> newNearSigns = new HashSet();
    if (set != null) {
      for (CMISign cmiSign : set) {
        if (!nearbySigns.contains(cmiSign)) {
          hashSet.add(cmiSign);
        }
      }

      for (CMISign candidate : nearbySigns) {
        if (!set.contains(candidate)) {
          newNearSigns.add(candidate);
        }
      }
    } else {
      newNearSigns.addAll(nearbySigns);
    }

    if (nearbySigns.isEmpty()) {
      this.playerNearSigns.remove(player.getUniqueId());
    } else {
      this.playerNearSigns.put(player.getUniqueId(), nearbySigns);
    }

    for (CMISign removedSign : hashSet) {
      this.removeLastSignInRange(removedSign, player.getUniqueId());
      this.removeNearSign(player.getUniqueId(), removedSign);
    }

    for (CMISign addedSign : newNearSigns) {
      this.addLastSignInRange(addedSign, player.getUniqueId());
      this.addNearSign(player.getUniqueId(), addedSign);
    }
  }

  private static CompletableFuture<List<CuboidArea.ChunkRef>> getChunks(CMISign cmiSign) {
    return cmiSign.getArea().thenApply(CuboidArea::getChunks);
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "Signs.CheckInterval",
        new String[] {
          "Defines in milliseconds how often to check if player entered Dynamic Sign trigger area",
          "Bigger numbers can help slightly lower server load",
          "This is not essential to keep in low numbers"
        });
    this.SignRangeCheckInterval = configReader.get("Signs.CheckInterval", 3000);
    configReader.addComment(
        "Signs.editBlackList",
        new String[] {
          "Defines list sign top lines which cant be edited with shift right click",
          "Usually used for preventing shop sign modifications"
        });
    this.signEditBlackList = configReader.get("Signs.editBlackList", Arrays.asList("[Shop]"));
    CMIList.toLowerCase(this.signEditBlackList);
    CMIList.removeColors(this.signEditBlackList);
    if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
      configReader.addComment(
          "Signs.RequireSneaking",
          new String[] {"When enabled player needs to be sneaking to edit signs"});
      this.signRequireShift = configReader.get("Signs.RequireSneaking", false);
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_21_R5)
        && Version.isPaperBranch()
        && (Version.isCurrentSubEqualOrHigher(7) || Version.isCurrentHigher(Version.v1_21_R5))) {
      configReader.addComment(
          "Signs.DialogEditor",
          new String[] {
            "When enabled sign editor will be represented with new Dialog editor introduced in"
                + " 1.21.7+",
            "Only for Paper servers"
          });
      this.signDialogEditor = configReader.get("Signs.DialogEditor", true);
    }
  }

  public void load() {
    Long startTime = System.currentTimeMillis();
    this.stop();
    this.signs.clear();
    this.chunkSignsRange.clear();
    this.lastSignInRange.clear();
    this.playerNearSigns.clear();
    this.plugin.safeRenameFile("dynamicSigns.yml", "Saves" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader = new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
    } catch (Exception exception) {
    }

    if (configReader != null && !configReader.getC().getKeys(false).isEmpty()) {
      for (String string : configReader.getC().getKeys(false)) {
        if (configReader.getC().isString(string + ".Loc")) {
          CMILocation cmiLocation =
              this.plugin
                  .getPlayerManager()
                  .convertStringToLocation(configReader.getC().getString(string + ".Loc"));
          if (cmiLocation != null && configReader.getC().isList(string + ".Lines")) {
            CMISign cmiSign = this.getByLoc(cmiLocation);
            if (cmiSign == null) {
              CMISign newSign = new CMISign(cmiLocation);
              newSign.setLines(configReader.getC().getStringList(string + ".Lines"));
              if (configReader.getC().isBoolean(string + ".Personal")) {
                newSign.setPersonal(configReader.getC().getBoolean(string + ".Personal"));
              }

              if (configReader.getC().isDouble(string + ".Interval")) {
                newSign.setUpdateIntervalSec(configReader.getC().getDouble(string + ".Interval"));
              } else if (configReader.getC().isInt(string + ".Interval")) {
                newSign.setUpdateIntervalSec(configReader.getC().getInt(string + ".Interval"));
              }

              if (configReader.getC().isInt(string + ".Range")) {
                newSign.setActivationRange(configReader.getC().getInt(string + ".Range"));
              }

              this.addSign(newSign);
            }
          }
        }
      }

      this.plugin.loadMessage(
          this.signs.size(), "dynamic signs", System.currentTimeMillis() - startTime);
    }
  }

  public void save() {
    if (this.saveId == null) {
      this.saveId =
          CMIScheduler.runTaskLater(
              this.plugin,
              () -> {
                this.saveSigns();
                this.saveId = null;
              },
              100L);
    }
  }

  private void saveSigns() {
    if (!this.saving) {
      this.saving = true;

      try {
        ConfigReader configReader = null;

        try {
          configReader =
              new ConfigReader(CMI.getInstance(), "Saves" + File.separator + this.fileName);
        } catch (Exception exception) {
        }

        if (configReader == null) {
          return;
        }

        for (var entry : configReader.getC().getValues(false).entrySet()) {
          configReader.getC().set((String) entry.getKey(), null);
        }

        int value = 0;

        for (CMISign cmiSign : this.signs) {
          configReader.get(
              ++value + ".Loc",
              this.plugin.getPlayerManager().convertLocToStringShort(cmiSign.getLoc()));
          configReader.get(value + ".Interval", cmiSign.getUpdateIntervalSec());
          configReader.get(value + ".Range", cmiSign.getActivationRange());
          configReader.get(value + ".Personal", cmiSign.isPersonal());
          configReader.get(value + ".Lines", cmiSign.getLines());
        }

        configReader.save();
      } catch (Exception | Error value2) {
        value2.printStackTrace();
      }

      this.saving = false;
    }
  }

  public void removeLastSignInRange(CMISign cmiSign, UUID uuid) {
    var set = this.lastSignInRange.get(cmiSign);
    if (set != null) {
      set.remove(uuid);
      if (set.isEmpty()) {
        this.lastSignInRange.remove(cmiSign);
      } else {
        this.lastSignInRange.put(cmiSign, set);
      }
    }
  }

  public void addLastSignInRange(CMISign cmiSign, UUID uuid) {
    var set = this.lastSignInRange.get(cmiSign);
    if (set == null) {
      set = new HashSet();
    }

    set.add(uuid);
    this.lastSignInRange.put(cmiSign, set);
    if (this.sched == null) {
      this.tasker();
    }
  }

  public void removeLastSignInRange(UUID uuid) {
    var hashMap = new HashMap<>(this.lastSignInRange);

    for (Entry entry : hashMap.entrySet()) {
      Set set = (Set) entry.getValue();
      if (set != null) {
        set.remove(uuid);
        if (set.isEmpty()) {
          this.lastSignInRange.remove(entry.getKey());
        } else {
          this.lastSignInRange.put((CMISign) entry.getKey(), set);
        }
      }
    }
  }

  private void tasker() {
    if (CMIModule.dynamicSigns.isEnabled()) {
      this.sched =
          CMIScheduler.scheduleSyncRepeatingTask(
              this.plugin,
              () -> {
                if (this.playerNearSigns.isEmpty() && this.sched != null) {
                  this.sched.cancel();
                  this.sched = null;
                } else {
                  for (var entry : new HashMap<>(this.lastSignInRange).entrySet()) {
                    updateSign((CMISign) entry.getKey(), (Set<UUID>) entry.getValue());
                  }
                }
              },
              0L,
              2L);
    }
  }

  public void addPlayersNearSign(CMISign cmiSign) {
    var set = this.lastSignInRange.get(cmiSign);
    if (set == null) {
      set = new HashSet();
    }

    set.clear();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (cmiSign.getArea() != null && cmiSign.getBasicArea().containsLoc(player.getLocation())) {
        set.add(player.getUniqueId());
      }
    }

    this.lastSignInRange.put(cmiSign, set);
    if (this.sched == null) {
      this.tasker();
    }
  }

  private static void updateSign(Player player, CMISign cmiSign) {
    if (!cmiSign.isPersonal()) {
      cmiSign.update();
    } else {
      cmiSign.update(player);
    }
  }

  private void updateSign(CMISign cmiSign) {
    var set = this.lastSignInRange.get(cmiSign);
    if (set != null && !set.isEmpty()) {
      updateSign(cmiSign, set);
    }
  }

  private static void updateSign(CMISign cmiSign, Set<UUID> set) {
    if (set != null && !set.isEmpty() && cmiSign.isTimeToUpdate()) {
      if (!cmiSign.isPersonal()) {
        cmiSign.update();
      } else {
        for (UUID uuid : new HashSet<>(set)) {
          Player player = CMIUser.getOnlinePlayer(uuid);
          cmiSign.update(player);
        }
      }

      cmiSign.setAsUpdated();
      cmiSign.updateCurrentLine();
    }
  }

  public Set<CMISign> getSigns() {
    return this.signs;
  }

  public List<CMISign> getSignsByDistance(Location location) {
    HashMap<CMISign, Integer> hashMap = new HashMap();
    ArrayList<CMISign> arrayList = new ArrayList();

    for (CMISign cmiSign : this.signs) {
      if (!location.getWorld().equals(cmiSign.getWorld())) {
        arrayList.add(cmiSign);
      } else {
        hashMap.put(cmiSign, (int) cmiSign.getBasicArea().getMiddleLocation().distance(location));
      }
    }

    hashMap = (HashMap) this.plugin.getSortingManager().sortByValueObject((Map) hashMap);
    ArrayList<CMISign> resultList = new ArrayList();

    for (Entry entry : hashMap.entrySet()) {
      resultList.add((CMISign) entry.getKey());
    }

    if (!arrayList.isEmpty()) {
      resultList.addAll(arrayList);
    }

    return resultList;
  }

  public void removeSign(CMISign cmiSign) {
    this.signs.remove(cmiSign);
    this.lastSignInRange.remove(cmiSign);
    this.recalculateChunks();
    this.save();
  }

  public int getSignCheckInterval() {
    return this.SignRangeCheckInterval;
  }

  public boolean isNearSign(UUID uuid) {
    var set = this.playerNearSigns.get(uuid);
    return set != null && !set.isEmpty();
  }

  public void addNearSign(UUID uuid, CMISign cmiSign) {
    var set = this.playerNearSigns.get(uuid);
    if (set == null) {
      set = new HashSet();
    }

    set.add(cmiSign);
    this.playerNearSigns.put(uuid, set);
    updateSign(CMIUser.getOnlinePlayer(uuid), cmiSign);
  }

  public void removeNearSign(UUID uuid, CMISign cmiSign) {
    var set = this.playerNearSigns.get(uuid);
    if (set != null) {
      set.remove(cmiSign);
      if (set.isEmpty()) {
        this.playerNearSigns.remove(uuid);
      }
    }
  }

  public void removeNearSign(UUID uuid) {
    this.playerNearSigns.remove(uuid);
  }

  public void openGui(final Player player, final CMISign cmiSign) {
    CMIGui cmiGui = new CMIGui(player);
    cmiGui.setTitle("Dynamic sign");
    cmiGui.setInvSize(GUIRows.r3);
    CMIGuiButton cmiGuiButton =
        new CMIGuiButton(10, cmiSign.isPersonal() ? CMIMaterial.GREEN_WOOL : CMIMaterial.RED_WOOL) {
          public void click(GUIClickType guiClickType) {
            SignManager.changeIndividual(player, cmiSign);
          }
        };
    cmiGuiButton.setName(CMILC.getIM(dsign.class, "individual", "[state]", cmiSign.isPersonal()));
    cmiGuiButton.addLore(CMILC.getIML(dsign.class, "individualLore"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(12, CMIMaterial.BROWN_WOOL) {
          public void click(GUIClickType clickType) {
            SignManager.changeInterval(player, clickType, cmiSign);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(dsign.class, "interval", "[second]", cmiSign.getUpdateIntervalSec()));
    cmiGuiButton.addLore(CMILC.getIML(dsign.class, "intervalLore"));
    cmiGui.addButton(cmiGuiButton);
    cmiGuiButton =
        new CMIGuiButton(14, CMIMaterial.YELLOW_WOOL) {
          public void click(GUIClickType clickType) {
            SignManager.changeRange(player, clickType, cmiSign);
          }
        };
    cmiGuiButton.setName(
        CMILC.getIM(dsign.class, "range", "[blocks]", cmiSign.getActivationRange()));
    cmiGuiButton.addLore(CMILC.getIML(dsign.class, "rangeLore"));
    cmiGui.addButton(cmiGuiButton);
    cmiGui.fillEmptyButtons();
    cmiGui.open();
  }

  public static void changeIndividual(Player player, CMISign cmiSign) {
    cmiSign.setPersonal(!cmiSign.isPersonal());
    CMI.getInstance().getSignManager().openGui(player, cmiSign);
    CMI.getInstance().getSignManager().save();
  }

  public static void changeRange(Player player, GUIClickType guiClickType, CMISign cmiSign) {
    switch (guiClickType) {
      case Left:
        cmiSign.setActivationRange(cmiSign.getActivationRange() + 1);
        break;
      case LeftShift:
        cmiSign.setActivationRange(cmiSign.getActivationRange() + 10);
        break;
      case Right:
        cmiSign.setActivationRange(cmiSign.getActivationRange() - 1);
        break;
      case RightShift:
        cmiSign.setActivationRange(cmiSign.getActivationRange() - 10);
    }

    cmiSign.setArea(null);
    CMI.getInstance().getSignManager().recalculateChunks(cmiSign);
    CMI.getInstance().getSignManager().addPlayersNearSign(cmiSign);
    CMI.getInstance().getSignManager().openGui(player, cmiSign);
    CMI.getInstance().getSignManager().save();
    CMI.getInstance().getSelectionManager().setSelectionCuboid(player, cmiSign.getBasicArea());
    SelectionManager.getPlayerSelection(player).showFull();
  }

  public static void changeInterval(Player player, GUIClickType guiClickType, CMISign cmiSign) {
    switch (guiClickType) {
      case Left:
        if (cmiSign.getUpdateIntervalSec() >= 0.0 && cmiSign.getUpdateIntervalSec() < 1.0) {
          cmiSign.setUpdateIntervalSec(((int) (cmiSign.getUpdateIntervalSec() * 10.0) + 1) / 10.0);
        } else {
          cmiSign.setUpdateIntervalSec(cmiSign.getUpdateIntervalSec() + 1.0);
        }
        break;
      case LeftShift:
        cmiSign.setUpdateIntervalSec(cmiSign.getUpdateIntervalSec() + 10.0);
        break;
      case Right:
        if (cmiSign.getUpdateIntervalSec() >= 0.0 && cmiSign.getUpdateIntervalSec() <= 1.0) {
          cmiSign.setUpdateIntervalSec(((int) (cmiSign.getUpdateIntervalSec() * 10.0) - 1) / 10.0);
        } else {
          cmiSign.setUpdateIntervalSec(cmiSign.getUpdateIntervalSec() - 1.0);
        }
        break;
      case RightShift:
        cmiSign.setUpdateIntervalSec(cmiSign.getUpdateIntervalSec() - 10.0);
    }

    CMI.getInstance().getSignManager().openGui(player, cmiSign);
    CMI.getInstance().getSignManager().save();
  }

  public HashMap<CMIChatColor, CMIChatColor> getColorChange() {
    return this.colorChange;
  }

  public void setColorChange(HashMap<CMIChatColor, CMIChatColor> hashMap) {
    this.colorChange = hashMap;
  }

  public List<String> getSignEditBlackList() {
    return this.signEditBlackList;
  }

  public boolean isSignEditRequireSneaking() {
    return this.signRequireShift;
  }

  public boolean isSignDialogEditor() {
    return this.signDialogEditor;
  }
}
