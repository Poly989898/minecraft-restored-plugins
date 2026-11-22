package com.Zrips.CMI.Containers;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CMIWorldLimits {
  private static boolean worldLimits = false;
  private static boolean flyAboveRoof = true;
  private static HashMap<String, GameMode> worldGameMode = new HashMap<>();
  private static HashMap<String, Boolean> worldFlyMode = new HashMap<>();
  private static HashMap<String, Boolean> worldGodMode = new HashMap<>();
  private static HashMap<String, Boolean> worldElytraMode = new HashMap<>();
  private static HashMap<String, Integer> flyAboveRoofLimitations = new HashMap<>();
  private static HashMap<String, List<SpawnReason>> blockedSpawnReasons = new HashMap<>();
  private static boolean reenabledFlyAfterDeath = false;

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "WorldLimits.Enabled",
        new String[] {
          "By setting to true fly and gamemode limitations per world will be aplied for player on"
              + " world change if they don't have appropiate permission node"
        });
    worldLimits = configReader.get("WorldLimits.Enabled", false);
    String string = "";
    GameMode[] gameModes;
    int value = (gameModes = GameMode.values()).length;

    for (int index = 0; index < value; index++) {
      GameMode gameMode = gameModes[index];
      string = string + gameMode.name().toLowerCase() + ", ";
    }

    configReader.addComment(
        "WorldLimits.Gamemode",
        new String[] {
          "World list with default game modes",
          "If player will have cmi.worldlimit.gamemode.bypass permission node, game mode wont be"
              + " changed",
          "Possible modes: " + string
        });
    var list = configReader.get("WorldLimits.Gamemode", Arrays.asList("testWorld:Survival"));
    getWorldGameMode().clear();

    for (String entry : list) {
      if (entry.contains(":")) {
        String worldName = entry.split(":")[0];
        String gameModeName = entry.split(":")[1];
        GameMode gameMode = null;
        World matchedWorld = null;
        GameMode[] worldGameModes;
        int gameModeCount = (worldGameModes = GameMode.values()).length;

        for (int gameModeIndex = 0; gameModeIndex < gameModeCount; gameModeIndex++) {
          GameMode candidateGameMode = worldGameModes[gameModeIndex];
          if (candidateGameMode.name().equalsIgnoreCase(gameModeName)) {
            gameMode = candidateGameMode;
            break;
          }
        }

        if (gameMode != null) {
          for (World candidateWorld : Bukkit.getWorlds()) {
            if (candidateWorld.getName().equalsIgnoreCase(worldName)) {
              matchedWorld = candidateWorld;
              break;
            }
          }

          if (matchedWorld == null) {
            getWorldGameMode().put(worldName.toLowerCase(), gameMode);
          } else if (isWorldLimits()) {
            getWorldGameMode().put(matchedWorld.getName().toLowerCase(), gameMode);
          }
        }
      }
    }

    configReader.addComment(
        "WorldLimits.Fly",
        new String[] {
          "If player will have cmi.worldlimit.fly.bypass permission node, fly mode wont be changed"
        });
    var flyWorlds = configReader.get("WorldLimits.Fly", Arrays.asList("testWorld:False"));
    getWorldFlyMode().clear();
    Iterator iterator = flyWorlds.iterator();

    while (true) {
      String flyWorld;
      Boolean flyEnabled;
      World flyTarget;
      while (true) {
        if (!iterator.hasNext()) {
          configReader.addComment(
              "WorldLimits.ReenabledFlyAfterDeath",
              new String[] {
                "When enabled player will be able to fly after death if they were flying before"
                    + " death"
              });
          reenabledFlyAfterDeath = configReader.get("WorldLimits.ReenabledFlyAfterDeath", false);
          configReader.addComment(
              "WorldLimits.ElytraFlight",
              new String[] {
                "If player will have "
                    + PermissionsManager.CMIPerm.worldlimit_elytra_bypass.getPermission()
                    + " permission node, elytra flight will not be prevented",
                "Players joining worlds with disable elytra flight will get their elytra dismounted"
                    + " if possible"
              });
          var limitList =
              configReader.get("WorldLimits.ElytraFlight", Arrays.asList("worldName:False"));
          getWorldElytraMode().clear();

          for (String limitEntry : limitList) {
            if (limitEntry.contains(":")) {
              String limitWorld = limitEntry.split(":")[0];
              String limitValue = limitEntry.split(":")[1];
              Boolean limitEnabled = limitValue.equalsIgnoreCase("true");
              if (isWorldLimits()) {
                getWorldElytraMode().put(limitWorld.toLowerCase().toLowerCase(), limitEnabled);
              }
            }
          }

          configReader.addComment(
              "WorldLimits.FlyAboveRoof",
              new String[] {
                "When set to false, only players with cmi.worldlimit.fly.aboveroof can fly above"
                    + " world build limit"
              });
          flyAboveRoof = configReader.get("WorldLimits.FlyAboveRoof", true);
          configReader.addComment(
              "WorldLimits.FlyAboveRoofLimitations",
              new String[] {
                "When set to false, only players with cmi.worldlimit.fly.aboveroof can fly above"
                    + " world build limit"
              });
          ArrayList<String> arrayList = new ArrayList();

          for (World roofWorld : Bukkit.getWorlds()) {
            arrayList.add(roofWorld.getName() + "-" + CMIWorld.getMaxHeight(roofWorld));
          }

          List roofList = configReader.get("WorldLimits.FlyAboveRoofLimitations", arrayList);
          flyAboveRoofLimitations = new HashMap<>();

          for (String roofEntry : ((Iterable<String>) roofList)) {
            if (roofEntry.contains("-")) {
              try {
                getFlyAboveRoofLimitationsMap()
                    .put(
                        roofEntry.split("-")[0].toLowerCase(),
                        Integer.parseInt(roofEntry.split("-")[1]));
              } catch (Exception exception) {
              }
            }
          }

          configReader.addComment(
              "WorldLimits.GodMode",
              new String[] {
                "If player will have cmi.worldlimit.god.bypass permission node, god mode wont be"
                    + " changed"
              });
          var godList = configReader.get("WorldLimits.GodMode", Arrays.asList("testWorld:False"));
          getWorldGodMode().clear();
          Iterator godIterator = godList.iterator();

          while (true) {
            World godWorld;
            String godWorldName;
            Boolean godEnabled;
            while (true) {
              if (!godIterator.hasNext()) {
                String reasonText = "";
                SpawnReason[] spawnReasons;
                int reasonCount = (spawnReasons = SpawnReason.values()).length;

                for (int reasonIndex = 0; reasonIndex < reasonCount; reasonIndex++) {
                  SpawnReason spawnReason = spawnReasons[reasonIndex];
                  if (!reasonText.isEmpty()) {
                    reasonText = reasonText + ", ";
                  }

                  reasonText = reasonText + spawnReason.name();
                }

                configReader.addComment(
                    "WorldLimits.SpawnReasons",
                    new String[] {
                      "Prevents particular entity spawn reasons in defined worlds. All possible"
                          + " reasons: "
                          + reasonText
                    });
                getBlockedSpawnReasons().clear();
                if (!configReader.getC().isConfigurationSection("WorldLimits.SpawnReasons")) {
                  for (World spawnWorld : Bukkit.getWorlds()) {
                    var spawnList =
                        configReader.get(
                            "WorldLimits.SpawnReasons." + spawnWorld.getName(),
                            Arrays.asList("None"));
                    ArrayList<org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason>
                        spawnReasonList = new ArrayList();

                    for (String spawnEntry : spawnList) {
                      SpawnReason[] allSpawnReasons;
                      int allReasonCount = (allSpawnReasons = SpawnReason.values()).length;

                      for (int allReasonIndex = 0;
                          allReasonIndex < allReasonCount;
                          allReasonIndex++) {
                        SpawnReason spawnReason = allSpawnReasons[allReasonIndex];
                        if (spawnReason.name().equalsIgnoreCase(spawnEntry)) {
                          spawnReasonList.add(spawnReason);
                          break;
                        }
                      }
                    }

                    getBlockedSpawnReasons()
                        .put(spawnWorld.getName().toLowerCase(), spawnReasonList);
                  }
                } else {
                  for (String reasonEntry :
                      configReader
                          .getC()
                          .getConfigurationSection("WorldLimits.SpawnReasons")
                          .getKeys(false)) {
                    var disabledList =
                        configReader.get(
                            "WorldLimits.SpawnReasons." + reasonEntry, Arrays.asList("None"));
                    ArrayList<org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason>
                        disabledReasons = new ArrayList();

                    for (String disabledEntry : disabledList) {
                      SpawnReason[] allReasonsCopy;
                      int disabledReasonCount = (allReasonsCopy = SpawnReason.values()).length;

                      for (int disabledReasonIndex = 0;
                          disabledReasonIndex < disabledReasonCount;
                          disabledReasonIndex++) {
                        SpawnReason disabledReason = allReasonsCopy[disabledReasonIndex];
                        if (disabledReason.name().equalsIgnoreCase(disabledEntry)) {
                          disabledReasons.add(disabledReason);
                          break;
                        }
                      }
                    }

                    getBlockedSpawnReasons().put(reasonEntry.toLowerCase(), disabledReasons);
                  }
                }

                return;
              }

              String godEntry = (String) godIterator.next();
              if (godEntry.contains(":")) {
                godWorldName = godEntry.split(":")[0];
                String godValue = godEntry.split(":")[1];
                godEnabled = false;
                godWorld = null;
                if (godValue.equalsIgnoreCase("true")) {
                  godEnabled = true;
                  break;
                }

                if (godValue.equalsIgnoreCase("false")) {
                  godEnabled = false;
                  break;
                }
              }
            }

            for (World limitTargetWorld : Bukkit.getWorlds()) {
              if (limitTargetWorld.getName().equalsIgnoreCase(godWorldName)) {
                godWorld = limitTargetWorld;
                break;
              }
            }

            if (godWorld == null) {
              getWorldGodMode().put(godWorldName.toLowerCase(), godEnabled);
            } else {
              getWorldGodMode().put(godWorld.getName().toLowerCase(), godEnabled);
            }
          }
        }

        String fieldEntry = (String) iterator.next();
        if (fieldEntry.contains(":")) {
          flyWorld = fieldEntry.split(":")[0];
          String fieldValue = fieldEntry.split(":")[1];
          flyEnabled = false;
          flyTarget = null;
          if (fieldValue.equalsIgnoreCase("true")) {
            flyEnabled = true;
            break;
          }

          if (fieldValue.equalsIgnoreCase("false")) {
            flyEnabled = false;
            break;
          }
        }
      }

      for (World fieldWorld : Bukkit.getWorlds()) {
        if (fieldWorld.getName().equalsIgnoreCase(flyWorld)) {
          flyTarget = fieldWorld;
          break;
        }
      }

      if (flyTarget == null) {
        getWorldFlyMode().put(flyWorld.toLowerCase(), flyEnabled);
      } else if (isWorldLimits()) {
        getWorldFlyMode().put(flyTarget.getName().toLowerCase(), flyEnabled);
      }
    }
  }

  public static boolean isBlockedSpawnReason(String string, SpawnReason spawnReason) {
    if (getBlockedSpawnReasons().isEmpty()) {
      return false;
    }

    var list = getBlockedSpawnReasons().get(string.toLowerCase());
    return list == null ? false : list.contains(spawnReason);
  }

  public static boolean isWorldLimits() {
    return worldLimits;
  }

  public static boolean isFlyAboveRoof() {
    return flyAboveRoof;
  }

  public static HashMap<String, List<SpawnReason>> getBlockedSpawnReasons() {
    return blockedSpawnReasons;
  }

  public static HashMap<String, Integer> getFlyAboveRoofLimitationsMap() {
    return flyAboveRoofLimitations;
  }

  public static HashMap<String, Boolean> getWorldElytraMode() {
    return worldElytraMode;
  }

  public static HashMap<String, Boolean> getWorldGodMode() {
    return worldGodMode;
  }

  public static HashMap<String, Boolean> getWorldFlyMode() {
    return worldFlyMode;
  }

  public static HashMap<String, GameMode> getWorldGameMode() {
    return worldGameMode;
  }

  @Nullable
  public static Integer getFlyAboveRoof(String string) {
    return getFlyAboveRoofLimitationsMap().get(string.toLowerCase());
  }

  public static GameMode getWorldGameMode(String string) {
    return getWorldGameMode().get(string.toLowerCase());
  }

  public static boolean isGameModeLimited(String string) {
    return getWorldGameMode(string) != null;
  }

  @Nullable
  public static Boolean getWorldFlyMode(String string) {
    return getWorldFlyMode().get(string.toLowerCase());
  }

  public static boolean isFlyModeLimited(String string) {
    return getWorldFlyMode(string) != null;
  }

  @Nullable
  public static Boolean getWorldGodMode(String string) {
    return getWorldGodMode().get(string.toLowerCase());
  }

  public static boolean isGodModeLimited(String string) {
    return getWorldGodMode(string) != null;
  }

  @Nullable
  public static Boolean getWorldElytraMode(String string) {
    return getWorldElytraMode().get(string.toLowerCase());
  }

  public static boolean isElytraModeLimited(String string) {
    return getWorldElytraMode(string) != null;
  }

  public static boolean isReenabledFlyAfterDeath() {
    return reenabledFlyAfterDeath;
  }
}
