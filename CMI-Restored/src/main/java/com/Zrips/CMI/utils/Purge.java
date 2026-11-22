package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILwc;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

public class Purge {
  private CMITask purgeId = null;
  public boolean running = false;
  private CMI plugin;
  HashMap<Purge.PurgeType, Integer> map = new HashMap<>();
  private boolean CleanOnStart;
  private int OfflineDays;
  private boolean PlayerDataEnabled;
  private String PlayerDataSourceFolder;
  private boolean PlayerDataDeleteFiles;
  private String PlayerDataDestinationFolder;
  private boolean PlayerStatsEnabled;
  private String PlayerStatsSourceFolder;
  private boolean PlayerStatsDeleteFiles;
  private String PlayerStatsDestinationFolder;
  private boolean PlayerAdvancementsEnabled;
  private String PlayerAdvancementsSourceFolder;
  private boolean PlayerAdvancementsDeleteFiles;
  private String PlayerAdvancementsDestinationFolder;
  private boolean EssentialsEnabled;
  private String EssentialsSourceFolder;
  private boolean EssentialsDeleteFiles;
  private String EssentialsDestinationFolder;
  private boolean LwcEnabled;
  private static OfflinePlayer[] offPlayers;
  private static int x = 0;
  private static int count = 0;

  public Purge(CMI cmi) {
    this.plugin = cmi;
    this.fillZero();
  }

  public void loadConfig() {
    ConfigReader configReader = this.plugin.getConfigManager().getConfig();
    configReader.addComment(
        "purge",
        new String[] {
          "Removeuser command will use same configurations when removing player data files or"
              + " moving them to new place"
        });
    configReader.addComment("purge.CleanOnStart", new String[] {"Cleans files on server startup"});
    this.CleanOnStart = configReader.get("purge.CleanOnStart", false);
    configReader.addComment(
        "purge.OfflineDays",
        new String[] {"How long player should be offline for his data to be moved"});
    this.OfflineDays = configReader.get("purge.OfflineDays", 90);
    configReader.addComment(
        "purge.PlayerData.Enabled",
        new String[] {"Do you want to enable player data file cleaning"});
    this.PlayerDataEnabled = configReader.get("purge.PlayerData.Enabled", true);
    configReader.addComment(
        "purge.PlayerData.SourceFolder", new String[] {"Source folder to take files from"});
    this.PlayerDataSourceFolder =
        configReader.get(
            "purge.PlayerData.SourceFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "playerdata");
    configReader.addComment(
        "purge.PlayerData.DeleteFiles",
        new String[] {
          "When this is false, data files will be moved to backup folder. When its true files will"
              + " be deleted"
        });
    this.PlayerDataDeleteFiles = configReader.get("purge.PlayerData.DeleteFiles", false);
    configReader.addComment(
        "purge.PlayerData.DestinationFolder",
        new String[] {"Target folder to put files into if DeleteFiles set to false"});
    this.PlayerDataDestinationFolder =
        configReader.get(
            "purge.PlayerData.DestinationFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "playerdata_backup");
    configReader.addComment(
        "purge.PlayerStats.Enabled",
        new String[] {"Do you want to enable player stats file cleaning"});
    this.PlayerStatsEnabled = configReader.get("purge.PlayerStats.Enabled", true);
    configReader.addComment(
        "purge.PlayerStats.SourceFolder", new String[] {"Source folder to take files from"});
    this.PlayerStatsSourceFolder =
        configReader.get(
            "purge.PlayerStats.SourceFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "stats");
    configReader.addComment(
        "purge.PlayerStats.DeleteFiles",
        new String[] {
          "When this is false, data files will be moved to backup folder. When its true files will"
              + " be deleted"
        });
    this.PlayerStatsDeleteFiles = configReader.get("purge.PlayerStats.DeleteFiles", false);
    configReader.addComment(
        "purge.PlayerStats.DestinationFolder",
        new String[] {"Target folder to put files into if DeleteFiles set to false"});
    this.PlayerStatsDestinationFolder =
        configReader.get(
            "purge.PlayerStats.DestinationFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "stats_backup");
    configReader.addComment(
        "purge.PlayerAdvancements.Enabled",
        new String[] {"Do you want to enable player Advancements file cleaning"});
    this.PlayerAdvancementsEnabled = configReader.get("purge.PlayerAdvancements.Enabled", true);
    configReader.addComment(
        "purge.PlayerAdvancements.SourceFolder", new String[] {"Source folder to take files from"});
    this.PlayerAdvancementsSourceFolder =
        configReader.get(
            "purge.PlayerAdvancements.SourceFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "Advancements");
    configReader.addComment(
        "purge.PlayerAdvancements.DeleteFiles",
        new String[] {
          "When this is false, data files will be moved to backup folder. When its true files will"
              + " be deleted"
        });
    this.PlayerAdvancementsDeleteFiles =
        configReader.get("purge.PlayerAdvancements.DeleteFiles", false);
    configReader.addComment(
        "purge.PlayerAdvancements.DestinationFolder",
        new String[] {"Target folder to put files into if DeleteFiles set to false"});
    this.PlayerAdvancementsDestinationFolder =
        configReader.get(
            "purge.PlayerAdvancements.DestinationFolder",
            ((World) Bukkit.getWorlds().get(0)).getName() + File.separator + "Advancements_backup");
    if (Version.isCurrentLower(Version.v1_12_R1)) {
      this.PlayerAdvancementsEnabled = false;
    }

    configReader.addComment(
        "purge.Essentials.Enabled",
        new String[] {"Do you want to enable essentials playerdata file cleaning"});
    this.EssentialsEnabled = configReader.get("purge.Essentials.Enabled", false);
    configReader.addComment(
        "purge.Essentials.SourceFolder", new String[] {"Source folder to take files from"});
    this.EssentialsSourceFolder =
        configReader.get(
            "purge.Essentials.SourceFolder",
            "plugins" + File.separator + "Essentials" + File.separator + "userdata");
    configReader.addComment(
        "purge.Essentials.DeleteFiles",
        new String[] {
          "When this is false, data files will be moved to backup folder. When its true files will"
              + " be deleted"
        });
    this.EssentialsDeleteFiles = configReader.get("purge.Essentials.DeleteFiles", false);
    configReader.addComment(
        "purge.Essentials.DestinationFolder",
        new String[] {"Target folder to put files into if DeleteFiles set to false"});
    this.EssentialsDestinationFolder =
        configReader.get(
            "purge.Essentials.DestinationFolder",
            "plugins" + File.separator + "Essentials" + File.separator + "userdata_backup");
    configReader.addComment(
        "purge.LWC.Enabled", new String[] {"Do you want to enable lwc protection cleaning"});
    this.LwcEnabled = configReader.get("purge.LWC.Enabled", false);
  }

  public void initialize() {
    if (this.PlayerDataEnabled) {
      File file = new File(this.PlayerDataDestinationFolder);
      if (!file.exists()) {
        System.out.println("Creating directory: " + file.getParent());

        try {
          file.mkdir();
          System.out.println("DIR created: " + this.PlayerDataDestinationFolder);
        } catch (SecurityException securityException) {
        }
      }
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_12_R1) && this.PlayerAdvancementsEnabled) {
      File fileSecond = new File(this.PlayerAdvancementsDestinationFolder);
      if (!fileSecond.exists()) {
        System.out.println("Creating directory: " + fileSecond.getParent());

        try {
          fileSecond.mkdir();
          System.out.println("DIR created: " + this.PlayerAdvancementsDestinationFolder);
        } catch (SecurityException securityException2) {
        }
      }
    }

    if (this.PlayerStatsEnabled) {
      File fileThird = new File(this.PlayerStatsDestinationFolder);
      if (!fileThird.exists()) {
        System.out.println("Creating directory: " + fileThird.getParent());

        try {
          fileThird.mkdir();
          System.out.println("DIR created: " + this.PlayerStatsDestinationFolder);
        } catch (SecurityException securityException3) {
        }
      }
    }

    if (this.EssentialsEnabled) {
      File fileFourth = new File(this.EssentialsDestinationFolder);
      if (!fileFourth.exists()) {
        System.out.println("Creating directory: " + fileFourth.getParent());

        try {
          fileFourth.mkdir();
          System.out.println("DIR created: " + this.EssentialsDestinationFolder);
        } catch (SecurityException securityException4) {
        }
      }
    }

    this.fillZero();
    CMIScheduler.runTaskAsynchronously(
        this.plugin,
        () -> {
          this.plugin.getPurgeManager().running = true;
          this.StartingPurge();
        });
  }

  private void fillZero() {
    this.map.clear();
    Purge.PurgeType[] purgeTypes;
    int value = (purgeTypes = Purge.PurgeType.values()).length;

    for (int count = 0; count < value; count++) {
      Purge.PurgeType purgeType = purgeTypes[count];
      this.map.put(purgeType, 0);
    }
  }

  public void StartingPurge() {
    offPlayers = Bukkit.getOfflinePlayers();
    x = -1;
    count = 0;
    this.fillZero();
    if (this.purgeId != null) {
      this.purgeId.cancel();
    }

    this.purgeId =
        CMIScheduler.scheduleSyncRepeatingTask(
            this.plugin,
            () -> {
              if (this.running) {
                if (count >= 100) {
                  this.plugin.consoleMessage("&eChecking... &6" + offPlayers.length + "&e/&6" + x);
                  count = 0;
                }

                long longValue = System.currentTimeMillis();

                for (int value = 0; value < 10; value++) {
                  x++;
                  count++;
                  if (x >= offPlayers.length) {
                    if (this.purgeId != null) {
                      this.purgeId.cancel();
                    }

                    this.plugin.consoleMessage(
                        "&eChecking... &6" + offPlayers.length + "&e/&6" + x);
                    this.plugin.consoleMessage("&eFinished cleaning");
                    this.plugin.consoleMessage(
                        "&ePlayer data files removed: &6"
                            + this.map.get(Purge.PurgeType.PlayerData)
                            + " &eKeeping: &6"
                            + (offPlayers.length - this.map.get(Purge.PurgeType.PlayerData)));
                    if (this.map.get(Purge.PurgeType.PlayerStat) > 0) {
                      this.plugin.consoleMessage(
                          "&ePlayer stat files removed: &6"
                              + this.map.get(Purge.PurgeType.PlayerStat));
                    }

                    if (this.map.get(Purge.PurgeType.PlayerAdvancements) > 0) {
                      this.plugin.consoleMessage(
                          "&ePlayer advancements files removed: &6"
                              + this.map.get(Purge.PurgeType.PlayerAdvancements));
                    }

                    if (this.map.get(Purge.PurgeType.Essentials) > 0) {
                      this.plugin.consoleMessage(
                          "&eEssential data files removed: &6"
                              + this.map.get(Purge.PurgeType.Essentials));
                    }

                    if (this.map.get(Purge.PurgeType.Lwc) > 0) {
                      this.plugin.consoleMessage(
                          "&eLWC protections removed: &6" + this.map.get(Purge.PurgeType.Lwc));
                    }

                    return;
                  }

                  OfflinePlayer offlinePlayer = offPlayers[x];
                  if (!offlinePlayer.isOnline()) {
                    long time = offlinePlayer.getLastPlayed();
                    int countSecond = (int) ((longValue - time) / 1000L / 60L / 60L / 24L);
                    if (time <= 0L || countSecond >= this.OfflineDays) {
                      UUID uuid = offlinePlayer.getUniqueId();
                      this.purgePlayerDataFiles(uuid, true);
                      this.purgePlayerStatsFiles(uuid, true);
                      this.purgePlayerAdvancementsFiles(uuid, true);
                      this.purgeEssentialsFiles(uuid);
                      this.purgeLWC(uuid);
                    }
                  }
                }
              }
            },
            2L,
            2L);
  }

  private void addValue(Purge.PurgeType purgeType) {
    this.addValue(purgeType, 1);
  }

  private void addValue(Purge.PurgeType purgeType, int value) {
    this.map.put(purgeType, this.map.get(purgeType) + value);
  }

  public boolean purgePlayerAdvancementsFiles(UUID uuid, boolean flag) {
    if (!Version.isCurrentEqualOrLower(Version.v1_11_R1)
        && (this.PlayerAdvancementsEnabled || !flag)) {
      File file = new File(this.PlayerAdvancementsSourceFolder + File.separator + uuid + ".json");
      if (!file.exists()) {
        return false;
      }

      if (!this.PlayerAdvancementsDeleteFiles) {
        file.renameTo(
            new File(this.PlayerAdvancementsDestinationFolder + File.separator + file.getName()));
      } else {
        file.delete();
      }

      this.addValue(Purge.PurgeType.PlayerAdvancements);
      return true;
    } else {
      return false;
    }
  }

  public boolean purgePlayerDataFiles(UUID uuid, boolean flag) {
    if (!this.PlayerDataEnabled && flag) {
      return false;
    }

    File file = new File(this.PlayerDataSourceFolder + File.separator + uuid + ".dat");
    if (!file.exists()) {
      return false;
    }

    if (!this.PlayerDataDeleteFiles) {
      File fileSecond =
          new File(this.PlayerDataDestinationFolder + File.separator + file.getName());
      if (fileSecond.exists()) {
        fileSecond.delete();
      }

      file.renameTo(fileSecond);
    } else {
      file.delete();
    }

    this.addValue(Purge.PurgeType.PlayerData);
    return true;
  }

  public boolean purgePlayerStatsFiles(UUID uuid, boolean flag) {
    if (!this.PlayerStatsEnabled && flag) {
      return false;
    }

    File file = new File(this.PlayerStatsSourceFolder + File.separator + uuid + ".json");
    if (!file.exists()) {
      return false;
    }

    if (!this.PlayerStatsDeleteFiles) {
      File fileSecond =
          new File(this.PlayerStatsDestinationFolder + File.separator + file.getName());
      if (fileSecond.exists()) {
        fileSecond.delete();
      }

      file.renameTo(fileSecond);
    } else {
      file.delete();
    }

    this.addValue(Purge.PurgeType.PlayerStat);
    return true;
  }

  public void purgeEssentialsFiles(UUID uuid) {
    if (this.EssentialsEnabled) {
      File file = new File(this.EssentialsSourceFolder + File.separator + uuid + ".yml");
      if (file.exists()) {
        if (!this.EssentialsDeleteFiles) {
          File fileSecond =
              new File(this.EssentialsDestinationFolder + File.separator + file.getName());
          if (fileSecond.exists()) {
            fileSecond.delete();
          }

          file.renameTo(fileSecond);
        } else {
          file.delete();
        }

        this.addValue(Purge.PurgeType.Essentials);
      }
    }
  }

  public void purgeLWC(UUID uuid) {
    if (this.LwcEnabled && CMILwc.getLWC() != null) {
      int value = CMILwc.getLWC().getPhysicalDatabase().removeProtectionsByPlayer(uuid.toString());
      this.addValue(Purge.PurgeType.Lwc, value);
    }
  }

  public boolean isCleanOnStart() {
    return this.CleanOnStart;
  }

  public boolean isPlayerDataEnabled() {
    return this.PlayerDataEnabled;
  }

  public boolean isPlayerStatsEnabled() {
    return this.PlayerStatsEnabled;
  }

  public enum PurgeType {
    PlayerData,
    PlayerStat,
    PlayerAdvancements,
    Essentials,
    Lwc;
  }
}
