package com.Zrips.CMI.Modules.Search;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Hooks.CMIHookType;
import com.Zrips.CMI.commands.list.search;
import com.Zrips.CMI.utils.PlayerVaultManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CMIPlayerDataSearch extends CMIDataSearch {
  protected static CMIPlayerDataSearch activeSearch = null;
  private List<OfflinePlayer> players = new ArrayList<>();
  private ConcurrentHashMap<
          UUID, ConcurrentHashMap<CMIPlayerSearchPlaceType, CMIDataResultPlayerBase>>
      foundIn = new ConcurrentHashMap<>();

  public static CMIPlayerDataSearch getActiveSearch() {
    return activeSearch;
  }

  protected static void setActiveSearch(CMIPlayerDataSearch cmiPlayerDataSearch) {
    activeSearch = cmiPlayerDataSearch;
    if (CMI.getInstance().getConfigManager().SearchLogIntoFile) {
      activeSearch.createFileAppender("SearchInfo");
    }
  }

  public CMIPlayerDataSearch(CommandSender commandSender) {
    super(commandSender);
  }

  private List<OfflinePlayer> getPlayers() {
    return this.players;
  }

  private void add(Set<CMIDataResultBase> set) {
    if (set != null && !set.isEmpty()) {
      ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap();

      for (CMIDataResultBase cmiDataResultBase : set) {
        if (cmiDataResultBase instanceof CMIDataResultPlayerBase) {
          CMIDataResultPlayerBase cmiDataResultPlayerBase =
              (CMIDataResultPlayerBase) cmiDataResultBase;
          var groupedResults =
              this.foundIn.computeIfAbsent(
                  cmiDataResultPlayerBase.getUniqueId(), uuid -> new ConcurrentHashMap<>());
          if (!groupedResults.containsKey(cmiDataResultPlayerBase.getType())) {
            this.totalFound++;
          }

          String string =
              (cmiDataResultPlayerBase.getType().equals(CMIPlayerSearchPlaceType.Custom)
                      ? ""
                      : cmiDataResultPlayerBase.getType() + " ")
                  + getPotentialName(cmiDataResultPlayerBase.getUniqueId());
          if (cmiDataResultPlayerBase instanceof CMIDataResultItem) {
            CMIDataResultPlayerBase resultBase =
                groupedResults.computeIfAbsent(
                    cmiDataResultPlayerBase.getType(),
                    object ->
                        new CMIDataResultItem(object, cmiDataResultPlayerBase.getUniqueId(), 0));
            groupedResults.put(
                cmiDataResultPlayerBase.getType(),
                ((CMIDataResultValue) resultBase)
                    .addAmount(((CMIDataResultValue) cmiDataResultPlayerBase).getAmount()));
            concurrentHashMap.put(
                string,
                concurrentHashMap.getOrDefault(string, 0)
                    + ((CMIDataResultValue) cmiDataResultPlayerBase).getAmount());
          } else if (cmiDataResultPlayerBase instanceof CMIDataResultValue) {
            groupedResults.computeIfAbsent(
                cmiDataResultPlayerBase.getType(),
                key ->
                    new CMIDataResultValue(
                        key,
                        cmiDataResultPlayerBase.getUniqueId(),
                        ((CMIDataResultValue) cmiDataResultPlayerBase).getAmount()));
            concurrentHashMap.put(
                string,
                concurrentHashMap.getOrDefault(string, 0)
                    + ((CMIDataResultValue) cmiDataResultPlayerBase).getAmount());
          } else {
            groupedResults.computeIfAbsent(
                cmiDataResultPlayerBase.getType(),
                key -> new CMIDataResultPlayerBase(key, cmiDataResultPlayerBase.getUniqueId()));
            concurrentHashMap.put(string, 0);
          }
        }
      }

      if (CMI.getInstance().getConfigManager().SearchLogIntoFile) {
        for (Entry entry : concurrentHashMap.entrySet()) {
          if ((Integer) entry.getValue() > 0) {
            this.appender.appendData((String) entry.getKey() + " -> " + entry.getValue());
          } else {
            this.appender.appendData(entry.getKey());
          }
        }
      }
    }
  }

  private static String getPotentialName(UUID uuid) {
    CMIUser cmiUser = CMIUser.getUser(uuid);
    return cmiUser != null ? cmiUser.getName() : uuid.toString();
  }

  public ConcurrentMap<UUID, ConcurrentHashMap<CMIPlayerSearchPlaceType, CMIDataResultPlayerBase>>
      getSearchResults() {
    synchronized (this.foundIn) {
      return new ConcurrentHashMap<>(this.foundIn);
    }
  }

  @Override
  public void search() {
    if (this.getSearchingFor().isEmpty()) {
      CMILC.info(search.class, this.getInitiator(), "nothing");
    } else if (getActiveSearch() != null && getActiveSearch().isRunning()) {
      CMILC.info(search.class, this.getInitiator(), "active");
    } else {
      setActiveSearch(this);
      this.initSearch();
    }
  }

  @Override
  protected void initSearch() {
    CMILC.info(search.class, this.getInitiator(), "searching");
    CMIScheduler.runTaskAsynchronously(
        CMI.getInstance(),
        () -> {
          for (Player player : Bukkit.getOnlinePlayers()) {
            try {
              this.checkPlayer(player);
            } catch (Throwable throwable) {
              throwable.printStackTrace();
            }
          }

          OfflinePlayer[] offlinePlayers;
          int value = (offlinePlayers = Bukkit.getOfflinePlayers()).length;

          for (int index = 0; index < value; index++) {
            OfflinePlayer offlinePlayer = offlinePlayers[index];
            if (!offlinePlayer.isOnline()) {
              this.players.add(offlinePlayer);
            }
          }

          this.totalToCheck = this.players.size();
          this.checkOfflinePlayers();
        });
  }

  private void checkOfflinePlayers() {
    if (this.task == null) {
      this.task =
          CMIScheduler.scheduleSyncRepeatingTask(
              CMI.getInstance(),
              () -> {
                double number = CMI.getInstance().getLagMeter().getTPS(1);
                if (number < 19.0) {
                  this.searchSpeed -= 0.2;
                } else {
                  this.searchSpeed += 0.1;
                }

                this.searchSpeed = CMINumber.clamp(this.searchSpeed, 1.0, 500.0);
                int value = 0;

                for (int index = 0; index < this.searchSpeed; index++) {
                  value++;
                  if (this.getPlayers().isEmpty()) {
                    break;
                  }

                  OfflinePlayer offlinePlayer = this.getPlayers().remove(0);
                  CMIUser cmiUser =
                      CMI.getInstance().getPlayerManager().getUser(offlinePlayer, false);
                  if (cmiUser == null) {
                    cmiUser = new CMIUser(offlinePlayer);
                    CMI.getInstance().getPlayerManager().addUser(cmiUser);
                  }

                  if (!cmiUser.isOnline()) {
                    cmiUser.setName(offlinePlayer.getName());

                    try {
                      Player player = cmiUser.getPlayer(true);
                      this.checkPlayer(player);
                    } catch (Throwable throwable) {
                      throwable.printStackTrace();
                    }

                    cmiUser.unloadData();
                  }
                }

                if (!this.getPlayers().isEmpty()) {
                  this.onCycle(value);
                } else {
                  this.cancel();
                }
              },
              0L,
              1L);
    }
  }

  private void checkPlayer(Player player) {
    if (player != null) {
      this.addChecked();

      for (SearchTypeBase searchTypeBase : this.searchingFor) {
        if (searchTypeBase instanceof SearchTypePlayerBase) {
          this.add(((SearchTypePlayerBase) searchTypeBase).checkPlayer(player));
        } else if (searchTypeBase instanceof SearchTypeItem) {
          SearchTypeItem searchTypeItem = (SearchTypeItem) searchTypeBase;
          searchTypeItem.setCurrentUUID(player.getUniqueId());
          searchTypeItem.setCurrentCheckedLocation(CMIPlayerSearchPlaceType.EnderChest);
          this.add(searchTypeItem.checkContent(player.getEnderChest().getContents()));
          searchTypeItem.finishedLocationCheck();
          searchTypeItem.setCurrentCheckedLocation(CMIPlayerSearchPlaceType.Inventory);
          this.add(searchTypeItem.checkContent(player.getInventory().getContents()));
          searchTypeItem.finishedLocationCheck();
          if (CMIHookType.PlayerVaults.hasSubType()) {
            searchTypeItem.setCurrentCheckedLocation(CMIPlayerSearchPlaceType.PlayerVaults);

            for (int value = 0; value < 100; value++) {
              if (PlayerVaultManager.vaultExists(player.getUniqueId(), value)) {
                Inventory inventory =
                    PlayerVaultManager.loadOtherVault(player.getUniqueId(), value, 54);
                if (inventory != null) {
                  this.add(searchTypeItem.checkContent(inventory.getContents()));
                }
              }
            }

            searchTypeItem.finishedLocationCheck();
          }
        }
      }
    }
  }
}
