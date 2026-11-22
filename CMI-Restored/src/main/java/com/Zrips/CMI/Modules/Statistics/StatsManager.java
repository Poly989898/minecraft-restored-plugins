package com.Zrips.CMI.Modules.Statistics;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Config;
import com.Zrips.CMI.Containers.CMILeaderboard;
import com.Zrips.CMI.Containers.CMIUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMIText;
import net.Zrips.CMILib.Entities.CMIEntityType;
import net.Zrips.CMILib.Items.CMIItemStack;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class StatsManager {
  private CMI plugin;
  private static HashMap<String, StatsManager.CMIStatistic> cache = new HashMap<>();
  private CMITask autoTimerBukkitId = null;
  CMILeaderboard<Long> leaderboard = new CMILeaderboard<>(1200L);
  private CompletableFuture<Void> task;
  private boolean loaded = false;
  long lastUpdate = 0L;
  private Runnable autoTimer =
      new Runnable() {
        @Override
        public void run() {
          for (Player player : Bukkit.getOnlinePlayers()) {
            CMIUser cmiUser = StatsManager.this.plugin.getPlayerManager().getUser(player);
            if (!cmiUser.isVanished()) {
              StatsManager.this.addUserData(cmiUser);
            }
          }
        }
      };

  static {
    StatsManager.CMIStatistic[] parts;
    int countSecond = (parts = StatsManager.CMIStatistic.values()).length;

    for (int count = 0; count < countSecond; count++) {
      StatsManager.CMIStatistic cMIStatisticSecond = parts[count];
      cache.put(cMIStatisticSecond.toString().replace("_", "").toLowerCase(), cMIStatisticSecond);
      cache.put(
          cMIStatisticSecond.getName().replace("_", "").replace(" ", "").toLowerCase(),
          cMIStatisticSecond);
      if (cMIStatisticSecond.getNewName() != null) {
        cache.put(
            cMIStatisticSecond.getNewName().replace("_", "").replace(" ", "").toLowerCase(),
            cMIStatisticSecond);
      }
    }
  }

  public StatsManager(CMI cmi) {
    this.plugin = cmi;
    this.autoTimerBukkitId =
        CMIScheduler.scheduleSyncRepeatingTask(cmi, this.autoTimer, 1200L, 1200L);
    this.loadStats().whenComplete((ignored, throwable) -> this.getLeaderboard().rebuild());
  }

  public void stop() {
    if (this.autoTimerBukkitId != null) {
      this.autoTimerBukkitId.cancel();
    }

    this.leaderboard.shutdown();
  }

  public StatsManager.CMIStatistic getStatisticByName(String string) {
    return cache.get(string.toLowerCase().replace("_", "").replace(" ", ""));
  }

  public Statistic getStatistic(StatsManager.CMIStatistic cmiStatistic) {
    if (cmiStatistic.getBukkitStat() != null) {
      return cmiStatistic.getBukkitStat();
    }

    Statistic[] statistics;
    int value = (statistics = Statistic.values()).length;

    for (int count = 0; count < value; count++) {
      Statistic statistic = statistics[count];
      if (statistic.name().equalsIgnoreCase(cmiStatistic.name())) {
        return statistic;
      }
    }

    return null;
  }

  public synchronized boolean changeTo(
      Player player, StatsManager.CMIStatistic cmiStatistic, String string, Long time) {
    if (cmiStatistic.getBukkitStat() == null) {
      return false;
    }

    int value = 0;
    if (time > 2147483647L) {
      value = Integer.MAX_VALUE;
    } else {
      value = time.intValue();
    }

    switch (cmiStatistic.getSubType()) {
      case None:
      default:
        try {
          player.setStatistic(cmiStatistic.getBukkitStat(), value);
          break;
        } catch (Exception exception) {
          exception.printStackTrace();
          return false;
        }
      case Material:
        CMIItemStack cmiItemStack = CMILib.getInstance().getItemManager().getItem(string);
        if (cmiItemStack == null) {
          return false;
        }

        if (cmiItemStack.getMaterial() == null) {
          return false;
        }

        try {
          player.setStatistic(cmiStatistic.getBukkitStat(), cmiItemStack.getMaterial(), value);
          break;
        } catch (Exception exception2) {
          return false;
        }
      case Block:
        CMIItemStack cMIItemStackSecond = CMILib.getInstance().getItemManager().getItem(string);
        if (cMIItemStackSecond == null || !cMIItemStackSecond.getMaterial().isBlock()) {
          return false;
        }

        try {
          player.setStatistic(
              cmiStatistic.getBukkitStat(), cMIItemStackSecond.getMaterial(), value);
          break;
        } catch (Exception exception3) {
          return false;
        }
      case Entity:
        CMIEntityType cmiEntityType = CMIEntityType.get(string);
        if (cmiEntityType == null) {
          return false;
        }

        if (cmiEntityType.getType() == null) {
          return false;
        }

        try {
          player.setStatistic(cmiStatistic.getBukkitStat(), cmiEntityType.getType(), value);
        } catch (Exception exception4) {
          exception4.printStackTrace();
          return false;
        }
    }

    return true;
  }

  public boolean isScanning() {
    return this.task != null;
  }

  public boolean isDataLoaded() {
    return this.loaded;
  }

  public CMILeaderboard<Long> getLeaderboard() {
    return this.leaderboard;
  }

  @Deprecated
  public int getTotalCount() {
    return this.getLeaderboard().getRecordCount();
  }

  @Deprecated
  public int getPages() {
    return this.getLeaderboard().getRecordCount() / 10;
  }

  public void addUserData(CMIUser cmiUser) {
    if (!this.isScanning()
        && !Config.PlaytimeTopExclude.contains(cmiUser.getName(false).toLowerCase())
        && !this.plugin.getPlayerManager().getFakeUserName().equals(cmiUser.getName(false))) {
      Long time = cmiUser.getTotalPlayTime(false);
      this.getLeaderboard().setValue(cmiUser.getUniqueId(), time);
    }
  }

  public Long getPlaytimeFromCache(CMIUser cmiUser) {
    return this.getLeaderboard().getValue(cmiUser.getUniqueId());
  }

  @Deprecated
  public SortedMap<Long, CMIUser> getTop() {
    if (!Config.PlayTimeAutoUpdater && this.lastUpdate + 60000L < System.currentTimeMillis()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        CMIUser cmiUser = this.plugin.getPlayerManager().getUser(player);
        if (!cmiUser.isVanished()) {
          this.addUserData(cmiUser);
        }
      }

      this.lastUpdate = System.currentTimeMillis();
    }

    TreeMap<Long, CMIUser> treeMap = new TreeMap(Collections.reverseOrder());
    var arrayList = new ArrayList<>(this.getLeaderboard().getValues().entrySet());

    for (int value = 0; value < arrayList.size(); value++) {
      Entry entry = (Entry) arrayList.get(value);
      UUID uuid = (UUID) entry.getKey();
      Long time = (Long) entry.getValue();
      CMIUser cMIUserSecond = CMIUser.getUser(uuid);
      if (cMIUserSecond != null) {
        treeMap.put(time, cMIUserSecond);
      }
    }

    return treeMap;
  }

  @Deprecated
  public synchronized SortedMap<Long, CMIUser> getTop(int value) {
    var list = this.getLeaderboard().getTop(value);
    TreeMap<Long, CMIUser> treeMap = new TreeMap();

    for (Entry entry : list) {
      CMIUser cmiUser = CMIUser.getUser((UUID) entry.getKey());
      if (cmiUser != null) {
        treeMap.put((Long) entry.getValue(), cmiUser);
      }
    }

    return treeMap;
  }

  @Deprecated
  public synchronized Long getTimeByPlace(int value) {
    return this.getLeaderboard().getValueByPlace(value);
  }

  @Deprecated
  public synchronized CMIUser getUserByPlace(int value) {
    return CMIUser.getUser(this.getLeaderboard().getUUIDByPlace(value));
  }

  @Deprecated
  public int getPlace(CMIUser cmiUser) {
    return this.getLeaderboard().getRank(cmiUser.getUniqueId());
  }

  public CompletableFuture<Void> loadStats() {
    long longValue = 86400000L * Config.PlaytimeTopOffline;
    if (this.loaded) {
      return CompletableFuture.completedFuture(null);
    }

    this.task =
        CMIScheduler.runTaskAsynchronously(
            this.plugin,
            () -> {
              try {
                new HashMap<>(this.plugin.getPlayerManager().getAllUsers())
                    .values()
                    .forEach(
                        cmiUser -> {
                          if (cmiUser.getTotalPlayTimeClean() > 0L
                              && cmiUser.getName(false) != null
                              && !Config.PlaytimeTopExclude.contains(
                                  cmiUser.getName(false).toLowerCase())) {
                            if (Config.PlaytimeTopOffline <= 0L
                                || cmiUser.getLastLogin(false) == 0L
                                || System.currentTimeMillis() - cmiUser.getLastLogin(false)
                                    <= longValue) {
                              this.getLeaderboard()
                                  .setValue(cmiUser.getUniqueId(), cmiUser.getTotalPlayTimeClean());
                            }
                          }
                        });
              } catch (Throwable throwable) {
                throwable.printStackTrace();
              }

              this.loaded = true;
              this.task = null;
            });
    return this.task;
  }

  public enum CMIStatistic {
    LEAVE_GAME("Game quit", StatsManager.svt.Number),
    PLAY_ONE_TICK(
        "Play time",
        StatsManager.svt.Time,
        false,
        false,
        StatsManager.CMIType.None,
        "PLAY_ONE_MINUTE"),
    TIME_SINCE_DEATH("From last death", StatsManager.svt.Time),
    TIME_SINCE_REST("From last rest", StatsManager.svt.Time),
    SNEAK_TIME(StatsManager.svt.Time),
    ACCOUNT_AGE(StatsManager.svt.Time),
    TRAVEL(StatsManager.svt.Distance),
    WALK_ONE_CM("Walk", StatsManager.svt.Distance),
    CROUCH_ONE_CM("Sneak", StatsManager.svt.Distance),
    SPRINT_ONE_CM("Sprint", StatsManager.svt.Distance),
    SWIM_ONE_CM("Swim", StatsManager.svt.Distance),
    FALL_ONE_CM("Fall", StatsManager.svt.Distance),
    CLIMB_ONE_CM("Climb", StatsManager.svt.Distance),
    FLY_ONE_CM("Fly", StatsManager.svt.Distance),
    DIVE_ONE_CM("Dive", StatsManager.svt.Distance),
    WALK_UNDER_WATER_ONE_CM("Walk under water", StatsManager.svt.Distance),
    WALK_ON_WATER_ONE_CM("Walk on water", StatsManager.svt.Distance),
    MINECART_ONE_CM("Minecart travel", StatsManager.svt.Distance),
    BOAT_ONE_CM("Boat travel", StatsManager.svt.Distance),
    PIG_ONE_CM("Pig travel", StatsManager.svt.Distance),
    HORSE_ONE_CM("Horse travel", StatsManager.svt.Distance),
    AVIATE_ONE_CM("Elytra travel", StatsManager.svt.Distance),
    HAPPY_GHAST_ONE_CM("Ghast travel", StatsManager.svt.Distance),
    JUMP(StatsManager.svt.Number),
    DAMAGE_DEALT(StatsManager.svt.Damage),
    DAMAGE_TAKEN(StatsManager.svt.Damage),
    DAMAGE_DEALT_ABSORBED(StatsManager.svt.Damage),
    DAMAGE_DEALT_RESISTED(StatsManager.svt.Damage),
    DAMAGE_BLOCKED_BY_SHIELD(StatsManager.svt.Damage),
    DAMAGE_ABSORBED(StatsManager.svt.Damage),
    DAMAGE_RESISTED(StatsManager.svt.Damage),
    DEATHS(StatsManager.svt.Number),
    ENTITY_KILLED_BY(
        "Killed by", StatsManager.svt.Number, false, true, StatsManager.CMIType.Entity),
    MOB_KILLS(StatsManager.svt.Number),
    PLAYER_KILLS(StatsManager.svt.Number),
    ITEM_ENCHANTED(StatsManager.svt.Number),
    ANIMALS_BRED(StatsManager.svt.Number),
    FISH_CAUGHT(StatsManager.svt.Number),
    TALKED_TO_VILLAGER(StatsManager.svt.Number),
    TRADED_WITH_VILLAGER(StatsManager.svt.Number),
    CAKE_SLICES_EATEN(StatsManager.svt.Number),
    CAULDRON_FILLED(StatsManager.svt.Number),
    CAULDRON_USED(StatsManager.svt.Number),
    ARMOR_CLEANED(StatsManager.svt.Number),
    BANNER_CLEANED(StatsManager.svt.Number),
    BREWINGSTAND_INTERACTION(StatsManager.svt.Number),
    BEACON_INTERACTION(StatsManager.svt.Number),
    CRAFTING_TABLE_INTERACTION(StatsManager.svt.Number),
    FURNACE_INTERACTION(StatsManager.svt.Number),
    DISPENSER_INSPECTED(StatsManager.svt.Number),
    DROPPER_INSPECTED(StatsManager.svt.Number),
    HOPPER_INSPECTED(StatsManager.svt.Number),
    CHEST_OPENED(StatsManager.svt.Number),
    TRAPPED_CHEST_TRIGGERED(StatsManager.svt.Number),
    ENDERCHEST_OPENED(StatsManager.svt.Number),
    NOTEBLOCK_PLAYED(StatsManager.svt.Number),
    NOTEBLOCK_TUNED(StatsManager.svt.Number),
    FLOWER_POTTED(StatsManager.svt.Number),
    RECORD_PLAYED(StatsManager.svt.Number),
    SLEEP_IN_BED(StatsManager.svt.Number),
    SHULKER_BOX_OPENED(StatsManager.svt.Number),
    CLEAN_SHULKER_BOX("Shulker box cleaned", StatsManager.svt.Number),
    DROP("Item dropped", StatsManager.svt.Number, false, true, StatsManager.CMIType.Material),
    MINE_BLOCK("Blocks mined", StatsManager.svt.Number, true, true, StatsManager.CMIType.Block),
    PLACE_BLOCK(
        "Blocks placed",
        StatsManager.svt.Number,
        true,
        true,
        StatsManager.CMIType.Block,
        Statistic.USE_ITEM),
    BREAK_ITEM("Item breaks", StatsManager.svt.Number, false, true, StatsManager.CMIType.Material),
    CRAFT_ITEM("Item crafts", StatsManager.svt.Number, false, true, StatsManager.CMIType.Material),
    USE_ITEM("Items used", StatsManager.svt.Number, false, true, StatsManager.CMIType.Material),
    PICKUP("Item pickups", StatsManager.svt.Number, false, true, StatsManager.CMIType.Material),
    KILL_ENTITY("Monster kills", StatsManager.svt.Number, false, true, StatsManager.CMIType.Entity),
    STRIDER_ONE_CM("Strider travel", StatsManager.svt.Distance),
    INTERACT_WITH_BLAST_FURNACE("Interaction with blast furnace", StatsManager.svt.Number),
    INTERACT_WITH_SMOKER("Interaction with smoker", StatsManager.svt.Number),
    INTERACT_WITH_LECTERN("Interaction with lectern", StatsManager.svt.Number),
    INTERACT_WITH_CAMPFIRE("Interaction with campfire", StatsManager.svt.Number),
    INTERACT_WITH_CARTOGRAPHY_TABLE("Interaction with cartography table", StatsManager.svt.Number),
    INTERACT_WITH_LOOM("Interaction with loom", StatsManager.svt.Number),
    INTERACT_WITH_STONECUTTER("Interaction with stonecutter", StatsManager.svt.Number),
    INTERACT_WITH_ANVIL("Interaction with anvil", StatsManager.svt.Number),
    INTERACT_WITH_GRINDSTONE("Interaction with grindstone", StatsManager.svt.Number),
    INTERACT_WITH_SMITHING_TABLE("Interaction with smithing table", StatsManager.svt.Number),
    BELL_RING(StatsManager.svt.Number),
    RAID_TRIGGER(StatsManager.svt.Number),
    RAID_WIN(StatsManager.svt.Number),
    TARGET_HIT(StatsManager.svt.Number),
    OPEN_BARREL(StatsManager.svt.Number),
    DROP_COUNT(StatsManager.svt.Number),
    TOTAL_WORLD_TIME("Time in world", StatsManager.svt.Time);

    private StatsManager.svt type;
    private boolean isBlock;
    private boolean isSubSatistic;
    private StatsManager.CMIType subType;
    private String name;
    private String newName;
    private Statistic bukkit;
    private boolean checked = false;

    CMIStatistic(
        String string,
        StatsManager.svt svtValue,
        boolean flag,
        boolean flagSecond,
        StatsManager.CMIType cmiType,
        Statistic statistic) {
      this(string, svtValue, flag, flagSecond, cmiType, "");
      this.bukkit = statistic;
    }

    CMIStatistic(
        String string,
        StatsManager.svt svtValue,
        boolean flag,
        boolean flagSecond,
        StatsManager.CMIType cmiType,
        String text) {
      this.name = string == null ? CMIText.firstToUpperCase(this.toString()) : string;
      this.type = svtValue;
      this.isBlock = flag;
      this.isSubSatistic = flagSecond;
      this.subType = cmiType;
      this.newName = text.isEmpty() ? null : text;
    }

    CMIStatistic(
        String string,
        StatsManager.svt svtValue,
        boolean flag,
        boolean flagSecond,
        StatsManager.CMIType cmiType) {
      this(string, svtValue, flag, flagSecond, cmiType, "");
    }

    CMIStatistic(String string, StatsManager.svt svtValue) {
      this(string, svtValue, false, false, StatsManager.CMIType.None, "");
    }

    CMIStatistic(StatsManager.svt svtValue) {
      this(null, svtValue, false, false, StatsManager.CMIType.None, "");
    }

    public StatsManager.svt getType() {
      return this.type;
    }

    public boolean isBlock() {
      return this.isBlock;
    }

    public void setBlock(boolean flag) {
      this.isBlock = flag;
    }

    public boolean isSubSatistic() {
      return this.isSubSatistic;
    }

    public void setSubSatistic(boolean flag) {
      this.isSubSatistic = flag;
    }

    public StatsManager.CMIType getSubType() {
      return this.subType;
    }

    public Statistic getBukkitStat() {
      if (this.bukkit == null && !this.checked) {
        this.checked = true;
        Statistic[] statistics;
        int value = (statistics = Statistic.values()).length;

        for (int count = 0; count < value; count++) {
          Statistic statistic = statistics[count];
          if (statistic.name().equalsIgnoreCase(this.name())) {
            this.bukkit = statistic;
          }

          if (this.getNewName() != null && statistic.name().equalsIgnoreCase(this.getNewName())) {
            this.bukkit = statistic;
          }
        }
      }

      return this.bukkit;
    }

    public String getName() {
      return this.name;
    }

    public String getNewName() {
      return this.newName;
    }

    public static StatsManager.CMIStatistic getByName(String string) {
      return StatsManager.cache.get(string.toLowerCase().replace("_", ""));
    }
  }

  public enum CMIType {
    None,
    Material,
    Block,
    Entity;
  }

  public enum svt {
    Time,
    Distance,
    Damage,
    Number;
  }
}
