package com.Zrips.CMI.Modules.TabList;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.Snd;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModule;
import com.Zrips.CMI.Modules.ModuleHandling.CMIModuleManager;
import com.Zrips.CMI.Modules.Permissions.PermissionInfo;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabListManager {
  private HashMap<Integer, TabList> TabList = new HashMap<>();
  private ConcurrentHashMap<UUID, CMITabSortPlayerData> playerData = new ConcurrentHashMap<>();
  private List<CMITabSort> sortOrder = new ArrayList<>();
  private boolean sortingEnabled = false;
  private CMI plugin;
  private CMITask sched = null;
  private double interval = 1.0;
  private boolean async = true;
  private boolean UpdateTabListNames = true;
  private boolean GroupedEnabled = true;
  private boolean UpdatesOnJoin = true;
  private boolean UpdatesOnLeave = true;
  private boolean UpdatesOnWorldChange = true;
  private boolean UpdatesOnPlayerDeath = false;
  private boolean UpdatesOnAfkStateChange = false;
  private boolean UpdatesOnPlayerTeleport = false;
  private boolean UpdatesOnNickChange = false;
  private boolean addTabListHeader = true;
  private boolean addTabListFooter = true;
  private String fileName = "TabList.yml";

  public TabListManager(CMI cmi) {
    this.plugin = cmi;
  }

  public void stop() {
    if (this.sched != null) {
      this.sched.cancel();
      this.sched = null;
    }
  }

  public void loadConfig() {
    this.stop();
    CMI.getInstance().safeRenameFile("tabList.yml", "Settings" + File.separator + this.fileName);
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(CMI.getInstance(), "Settings" + File.separator + this.fileName);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader != null) {
      ConfigReader configReaderValue = this.plugin.getConfigManager().getConfig();
      if (configReaderValue.getC().isBoolean("TabList.UpdateTabListNames")) {
        configReader.set("TabList", configReaderValue.getC().get("TabList"));
        configReader.save();

        try {
          configReader =
              new ConfigReader(CMI.getInstance(), "Settings" + File.separator + this.fileName);
        } catch (Exception exception2) {
          exception2.printStackTrace();
        }
      }

      configReader.addComment(
          "TabList", new String[] {"To disable tablist handling visit Modules.yml file"});
      Boolean flagSecond =
          configReader.getC().isBoolean("TabList.Enabled")
              ? configReader.getC().getBoolean("TabList.Enabled", true)
              : null;
      if (flagSecond != null) {
        CMIModuleManager.switchInFile(CMIModule.tablist, flagSecond);
        if (!CMIModule.tablist.isEnabled() && flagSecond) {
          CMIModule.tablist.setEnabled(true);
          CMIModule.tablist.registerListener();
        }
      }

      configReader.addComment(
          "TabList.Async",
          new String[] {
            "Defines if we want to run tablist updater in async mode",
            "While enabled it can increase overall performance but some plugins can have same"
                + " issues handling async placeholder requests"
          });
      this.async = configReader.get("TabList.Async", true);
      configReader.addComment(
          "TabList.UpdateTabListNamess",
          new String[] {"Defines if you want to update tablist player names with CMI format"});
      this.UpdateTabListNames = configReader.get("TabList.UpdateTabListNames", true);
      configReader.addComment(
          "TabList.GroupedEnabled",
          new String[] {
            "Enable or disable grouped format tablist's",
            "You can save some resources by disabling this if you are not interested in grouped"
                + " format tablist feature",
            "If disabled then this will use only default format and will skip checking for custom"
                + " one to save some resources if needed"
          });
      this.GroupedEnabled = configReader.get("TabList.GroupedEnabled", true);
      configReader.addComment(
          "TabList.Updates.AutoInterval",
          new String[] {
            "automatically updates tab list every x seconds for ALL online players",
            "If you are using static Footer and Header you can disable this by setting to -1",
            "Consider setting this to -1 if its completely enough to update on player events"
          });
      this.interval = configReader.get("TabList.Updates.AutoInterval", 60);
      this.UpdatesOnJoin = configReader.get("TabList.Updates.OnJoin", true);
      this.UpdatesOnLeave = configReader.get("TabList.Updates.OnLeave", true);
      this.UpdatesOnAfkStateChange = configReader.get("TabList.Updates.afkStateChange", true);
      this.UpdatesOnWorldChange = configReader.get("TabList.Updates.OnWorldChange", true);
      this.UpdatesOnPlayerDeath = configReader.get("TabList.Updates.OnDeath", false);
      this.UpdatesOnPlayerTeleport = configReader.get("TabList.Updates.OnTeleport", false);
      this.UpdatesOnNickChange = configReader.get("TabList.Updates.OnNickChange", true);
      configReader.addComment(
          "TabList.addTabListHeader",
          new String[] {"When set to false we will not add header text to tablist"});
      this.addTabListHeader = configReader.get("TabList.addTabListHeader", true);
      configReader.addComment(
          "TabList.addTabListFooter",
          new String[] {"When set to false we will not add footer text to tablist"});
      this.addTabListFooter = configReader.get("TabList.addTabListFooter", true);
      configReader.addComment(
          "TabList.GeneralFormat",
          new String[] {
            "PlaceholderAPI supported for any custom variable you want to insert into this",
            "In addition CMI will handle some placeholders without PlaceHolderAPI",
            "Full list can be checked ingame with /cmi placeholders"
          });
      TabList tabList = new TabList();
      configReader.addComment(
          "TabList.GeneralFormat.PlayerName",
          new String[] {"This will define how others see players name in tab list"});
      tabList.setPlayerNameFormat(
          configReader.get("TabList.GeneralFormat.PlayerName", "[playerDisplayName]"));
      List list = null;
      if (configReader.getC().isList("TabList.GeneralFormat.Header")) {
        list = configReader.getC().getStringList("TabList.GeneralFormat.Header");
      }

      if (list != null) {
        tabList.addHeaderAnimation(configReader.get("TabList.GeneralFormat.Header.1", list));
      } else if (!configReader.getC().isConfigurationSection("TabList.GeneralFormat.Header")) {
        tabList.addHeaderAnimation(
            configReader.get(
                "TabList.GeneralFormat.Header.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7Welcome",
                    "&7Online &f%server_online%&7/&f%server_max_players%",
                    "&f------------------------------------")));
      } else {
        for (String string :
            configReader
                .getC()
                .getConfigurationSection("TabList.GeneralFormat.Header")
                .getKeys(false)) {
          tabList.addHeaderAnimation(
              configReader.get("TabList.GeneralFormat.Header." + string, new ArrayList()));
        }
      }

      list = null;
      if (configReader.getC().isList("TabList.GeneralFormat.Footer")) {
        list = configReader.getC().getStringList("TabList.GeneralFormat.Footer");
      }

      if (list != null) {
        tabList.addFooterAnimation(configReader.get("TabList.GeneralFormat.Footer.1", list));
      } else if (!configReader.getC().isConfigurationSection("TabList.GeneralFormat.Footer")) {
        tabList.addFooterAnimation(
            configReader.get(
                "TabList.GeneralFormat.Footer.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7%player_world% &f%player_x%:&7%player_y%:&f%player_z%",
                    "&7Money: &f%vault_eco_balance_formatted% &7Time: &f%server_time_HH:mm:ss%",
                    "&f------------------------------------")));
      } else {
        for (String text :
            configReader
                .getC()
                .getConfigurationSection("TabList.GeneralFormat.Footer")
                .getKeys(false)) {
          tabList.addFooterAnimation(
              configReader.get("TabList.GeneralFormat.Footer." + text, new ArrayList()));
        }
      }

      this.TabList.put(-1, tabList);
      configReader.addComment(
          "TabList.useGeneralHeader",
          new String[] {
            "When this set to true, in case you dint defined PlayerName, Header or Footer in"
                + " GroupFormat, then default one from GeneralFormat will be used"
          });
      boolean flag = configReader.get("TabList.useGeneralName", false);
      boolean flagThird = configReader.get("TabList.useGeneralHeader", false);
      boolean flagFourth = configReader.get("TabList.useGeneralFooter", false);
      configReader.addComment(
          "TabList.GroupFormat",
          new String[] {
            "Defines custom formats to be used for players.",
            "Any player which have cmi.tablist.[number] permission node will use defined grouped"
                + " format in tablist",
            "Id should be a number and in case player has more than one, bigger number id will be"
                + " used",
            "You can have as many groups as you want by increasing increment"
          });
      if (!configReader.getC().isConfigurationSection("TabList.GroupFormat")) {
        tabList = new TabList();
        tabList.setPlayerNameFormat(
            configReader.get("TabList.GroupFormat.1.PlayerName", "&2{&r[playerDisplayName]&2}"));
        tabList.addHeaderAnimation(
            configReader.get(
                "TabList.GroupFormat.1.Header.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7Online &f%server_online%&7/&f%server_max_players%",
                    "&f------------------------------------")));
        tabList.addFooterAnimation(
            configReader.get(
                "TabList.GroupFormat.1.Footer.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7%player_world% &f%player_x%:&7%player_y%:&f%player_z%",
                    "&7Time: &f%server_time_HH:mm:ss%",
                    "&f------------------------------------")));
        this.TabList.put(1, tabList);
        tabList = new TabList();
        tabList.setPlayerNameFormat(
            configReader.get("TabList.GroupFormat.2.PlayerName", "&c[&r[playerDisplayName]&c]"));
        tabList.addHeaderAnimation(
            configReader.get(
                "TabList.GroupFormat.2.Header.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7Online &f%server_online%&7/&f%server_max_players%",
                    "&f------------------------------------")));
        tabList.addFooterAnimation(
            configReader.get(
                "TabList.GroupFormat.2.Footer.1",
                Arrays.asList(
                    "&f------------------------------------",
                    "&7Money: &f%vault_eco_balance_formatted% &7Time: &f%server_time_HH:mm:ss%",
                    "&f------------------------------------")));
        this.TabList.put(2, tabList);
      } else {
        try {
          if (configReader.getC().isConfigurationSection("TabList.GroupFormat")) {
            var iterator =
                configReader
                    .getC()
                    .getConfigurationSection("TabList.GroupFormat")
                    .getKeys(false)
                    .iterator();

            breakSecond:
            while (true) {
              Object object;
              while (true) {
                if (!iterator.hasNext()) {
                  break breakSecond;
                }

                String textSecond = (String) iterator.next();
                object = null;

                try {
                  object = Integer.parseInt(textSecond);
                  break;
                } catch (Exception exception3) {
                }
              }

              tabList = new TabList();
              if (configReader.getC().isString("TabList.GroupFormat." + object + ".PlayerName")) {
                tabList.setPlayerNameFormat(
                    configReader.get(
                        "TabList.GroupFormat." + object + ".PlayerName",
                        "&c->&r[playerDisplayName]&c<-"));
              } else if (flag) {
                tabList.setPlayerNameFormat(this.TabList.get(-1).getPlayerNameFormat());
              }

              list = null;
              if (configReader.getC().isList("TabList.GroupFormat." + object + ".Header")) {
                list =
                    configReader.getC().getStringList("TabList.GroupFormat." + object + ".Header");
              }

              if (list != null) {
                tabList.addHeaderAnimation(
                    configReader.get("TabList.GroupFormat." + object + ".Header.1", list));
              } else if (!configReader
                  .getC()
                  .isConfigurationSection("TabList.GroupFormat." + object + ".Header")) {
                if (flagThird) {
                  tabList.setHeaderMap(new HashMap<>(this.TabList.get(-1).getHeaderMap()));
                } else {
                  tabList.addHeaderAnimation(Arrays.asList());
                }
              } else {
                for (String textThird :
                    configReader
                        .getC()
                        .getConfigurationSection("TabList.GroupFormat." + object + ".Header")
                        .getKeys(false)) {
                  tabList.addHeaderAnimation(
                      configReader.get(
                          "TabList.GroupFormat." + object + ".Header." + textThird,
                          new ArrayList()));
                }
              }

              list = null;
              if (configReader.getC().isList("TabList.GroupFormat." + object + ".Footer")) {
                list =
                    configReader.getC().getStringList("TabList.GroupFormat." + object + ".Footer");
              }

              if (list != null) {
                tabList.addFooterAnimation(
                    configReader.get("TabList.GroupFormat." + object + ".Footer.1", list));
              } else if (!configReader
                  .getC()
                  .isConfigurationSection("TabList.GroupFormat." + object + ".Footer")) {
                if (flagFourth) {
                  tabList.setFooterMap(new HashMap<>(this.TabList.get(-1).getFooterMap()));
                } else {
                  tabList.addFooterAnimation(Arrays.asList());
                }
              } else {
                for (String textFourth :
                    configReader
                        .getC()
                        .getConfigurationSection("TabList.GroupFormat." + object + ".Footer")
                        .getKeys(false)) {
                  tabList.addFooterAnimation(
                      configReader.get(
                          "TabList.GroupFormat." + object + ".Footer." + textFourth,
                          new ArrayList()));
                }
              }

              this.TabList.put((Integer) object, tabList);
            }
          }
        } catch (Exception exception4) {
        }
      }

      configReader.addComment(
          "TabList.Sorting.Enabled",
          new String[] {
            "For 1.19.3+ servers only. Enable or disable tab list sorting. This will sort tablist"
                + " by defined criteria"
          });
      this.sortingEnabled = configReader.get("TabList.Sorting.Enabled", false);
      configReader.addComment(
          "TabList.Sorting.Order",
          new String[] {
            "In which order should we sort players",
            "Potential options: "
                + Arrays.stream(CMITabSortingType.values())
                    .map(Enum::toString)
                    .collect(Collectors.joining(", ")),
            "Optionally add ASC or DESC for specific order for that type of ordering, for example"
                + " 'Weight-DESC'",
            "Weight is determined by players access to "
                + PermissionsManager.CMIPerm.tablistweight_$1.getCleanPermissionForShow()
                + " permission node and highest value of it, for example "
                + PermissionsManager.CMIPerm.tablistweight_$1.getPermission("5"),
            "Sorting is updated at same time head and/or footer is"
          });
      var varSecond =
          configReader.get(
              "TabList.Sorting.Order",
              Arrays.asList(
                  "Weight-DESC",
                  "Group-ASC",
                  "Name-ASC",
                  "NickName-ASC",
                  "Balance-ASC",
                  "World-ASC"));
      this.sortOrder.clear();

      for (String textFifth : varSecond) {
        CMITabSort cmiTabSort = CMITabSort.fromString(textFifth);
        if (cmiTabSort != null) {
          this.sortOrder.add(cmiTabSort);
        }
      }

      configReader.save();
      if (this.plugin.isEnabled()) {
        this.tasker();
      }
    }
  }

  private void tasker() {
    if (this.sched == null) {
      if (this.interval < 0.0) {
        this.stop();
      } else {
        int value = (int) (this.interval * 20.0);
        if (value < 1) {
          value = 1;
        }

        this.sched = CMIScheduler.runTimerAsync(this.plugin, this::updateTabList, 0L, value);
      }
    }
  }

  @Deprecated
  public TabList getTL(Player player) {
    return this.getTabListOfPlayer(player);
  }

  public TabList getTabListOfPlayer(Player player) {
    PermissionInfo permissionInfo = null;
    if (this.GroupedEnabled) {
      permissionInfo =
          this.plugin
              .getPermissionsManager()
              .getPermissionInfo(player, PermissionsManager.CMIPerm.tablist_$1, 1000L);
    }

    if (permissionInfo != null && permissionInfo.getMaxValue() != null) {
      int value = permissionInfo.getMaxValue().intValue();
      return this.TabList.containsKey(value) ? this.TabList.get(value) : this.TabList.get(-1);
    } else {
      return this.TabList.get(-1);
    }
  }

  public void updateTabList(int value) {
    if (CMIModule.tablist.isEnabled()) {
      CMIScheduler.runTaskLater(
          this.plugin, () -> this.plugin.getTabListManager().updateTabList(), value);
    }
  }

  public void updateTabList() {
    if (CMIModule.tablist.isEnabled()) {
      for (Player player : Bukkit.getOnlinePlayers()) {
        this.updateTabList(player);
      }

      for (var entry : this.TabList.entrySet()) {
        ((TabList) entry.getValue()).prepareNextAnimationForFooter();
        ((TabList) entry.getValue()).prepareNextAnimationForHeader();
      }

      if (this.sortingEnabled && Version.isCurrentEqualOrHigher(Version.v1_19_3)) {
        this.sortPlayers();
      }
    }
  }

  public void addPlayerData(Player player) {
    this.playerData.put(player.getUniqueId(), new CMITabSortPlayerData(player));
  }

  public void removePlayerData(Player player) {
    this.playerData.remove(player.getUniqueId());
  }

  public void sortPlayers() {
    var concurrentHashMap = new ConcurrentHashMap<>(this.getPlayerData());

    for (Entry entry : concurrentHashMap.entrySet()) {
      Player player = Bukkit.getPlayer((UUID) entry.getKey());
      if (player == null) {
        this.playerData.remove(entry.getKey());
      } else {
        ((CMITabSortPlayerData) entry.getValue()).update();
      }
    }

    var list = this.getPlayerData().values().stream().collect(Collectors.toList());
    list.sort(buildComparator(this.sortOrder));
    int value = 9999;

    for (CMITabSortPlayerData cmiTabSortPlayerData : list) {
      value--;
      Player playerSecond = cmiTabSortPlayerData.getPlayer();
      if (playerSecond != null && playerSecond.isOnline()) {
        try {
          if (cmiTabSortPlayerData.getPreviousOrder() != value) {
            playerSecond.setPlayerListOrder(value);
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        cmiTabSortPlayerData.setPreviousOrder(value);
      }
    }
  }

  public ConcurrentHashMap<UUID, CMITabSortPlayerData> getPlayerData() {
    return this.playerData;
  }

  public CMITabSortPlayerData getPlayerData(Player player) {
    return this.playerData.computeIfAbsent(
        player.getUniqueId(), uuid -> new CMITabSortPlayerData(player));
  }

  private static Comparator<CMITabSortPlayerData> buildComparator(List<CMITabSort> list) {
    Comparator comparator = (object, other) -> 0;

    for (CMITabSort cmiTabSort : list) {
      Comparator comparatorValue;
      switch (cmiTabSort.getSortingType()) {
        case Weight:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue = Comparator.comparingInt(CMITabSortPlayerData::getWeight);
          } else {
            comparatorValue = Comparator.comparingInt(CMITabSortPlayerData::getWeight).reversed();
          }
          break;
        case Group:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getGroup, String.CASE_INSENSITIVE_ORDER);
          } else {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getGroup, String.CASE_INSENSITIVE_ORDER)
                    .reversed();
          }
          break;
        case Name:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getName, String.CASE_INSENSITIVE_ORDER);
          } else {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getName, String.CASE_INSENSITIVE_ORDER)
                    .reversed();
          }
          break;
        case NickName:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue =
                Comparator.comparing(
                    CMITabSortPlayerData::getNickName, String.CASE_INSENSITIVE_ORDER);
          } else {
            comparatorValue =
                Comparator.comparing(
                        CMITabSortPlayerData::getNickName, String.CASE_INSENSITIVE_ORDER)
                    .reversed();
          }
          break;
        case Balance:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue = Comparator.comparingDouble(CMITabSortPlayerData::getBalance);
          } else {
            comparatorValue =
                Comparator.comparingDouble(CMITabSortPlayerData::getBalance).reversed();
          }
          break;
        case World:
          if (cmiTabSort.getSortingOrder() == CMITabSortingOrder.ASC) {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getWorld, String.CASE_INSENSITIVE_ORDER);
          } else {
            comparatorValue =
                Comparator.comparing(CMITabSortPlayerData::getWorld, String.CASE_INSENSITIVE_ORDER)
                    .reversed();
          }
          break;
        default:
          continue;
      }

      comparator = comparator.thenComparing(comparatorValue);
    }

    return comparator;
  }

  public void updateTablistName(Player player) {
    Snd snd = new Snd().setSender(player).setTarget(player);
    TabList tabList = this.getTabListOfPlayer(player);
    if (tabList != null
        && tabList.getPlayerNameFormat() != null
        && !tabList.getPlayerNameFormat().isEmpty()) {
      String string = this.plugin.getLM().updateSnd(snd, tabList.getPlayerNameFormat());
      string = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, string);
      if (!player.getPlayerListName().equals(string)) {
        if (Version.isCurrentEqualOrLower(Version.v1_7_10)
            && CMIChatColor.translate(string).length() > 16) {
          player.setPlayerListName(CMIChatColor.translate(string).substring(0, 15));
        } else {
          player.setPlayerListName(CMIChatColor.translate(string));
        }
      }
    }
  }

  public void updateTabList(Player player) {
    if (this.async) {
      this.updateTabListAsync(player);
    } else {
      this.updateTabListSync(player);
    }
  }

  public void updateTabListSync(Player player) {
    if (CMIModule.tablist.isEnabled()) {
      Snd snd = new Snd().setSender(player).setTarget(player);
      TabList tabList = this.getTabListOfPlayer(player);
      this.send(player, tabList, snd);
    }
  }

  public void updateTabListAsync(Player player) {
    if (CMIModule.tablist.isEnabled()) {
      Snd snd = new Snd().setSender(player).setTarget(player);
      TabList tabList = this.getTabListOfPlayer(player);
      CMIScheduler.runTaskAsynchronously(this.plugin, () -> this.send(player, tabList, snd));
    }
  }

  private void send(Player player, TabList tabList, Snd snd) {
    if (tabList != null && player != null && player.isOnline()) {
      if (this.isUpdateTabListNames()) {
        this.updateTablistName(player);
      }

      List list = null;
      if (this.addTabListHeader) {
        list = tabList.getHeader();
        list = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, list);
        list = this.plugin.getLM().updateSnd(snd, list);
      }

      List items = null;
      if (this.addTabListFooter) {
        items = tabList.getFooter();
        items = this.plugin.getPlaceholderAPIManager().updatePlaceHolders(player, items);
        items = this.plugin.getLM().updateSnd(snd, items);
      }

      this.plugin.getTabListHandler().send(player, list, items);
    }
  }

  public boolean isUpdatesOnJoin() {
    return this.UpdatesOnJoin;
  }

  public boolean isUpdatesOnLeave() {
    return this.UpdatesOnLeave;
  }

  public boolean isUpdatesOnWorldChange() {
    return this.UpdatesOnWorldChange;
  }

  public boolean isUpdatesOnPlayerDeath() {
    return this.UpdatesOnPlayerDeath;
  }

  public boolean isUpdatesOnPlayerTeleport() {
    return this.UpdatesOnPlayerTeleport;
  }

  public boolean isUpdatesOnNickChange() {
    return this.UpdatesOnNickChange;
  }

  public boolean isUpdatesOnAfkStateChange() {
    return this.UpdatesOnAfkStateChange;
  }

  public boolean isEnabled() {
    return CMIModule.tablist.isEnabled();
  }

  public boolean isUpdateTabListNames() {
    return this.UpdateTabListNames;
  }

  public boolean isSortingEnabled() {
    return this.sortingEnabled;
  }
}
