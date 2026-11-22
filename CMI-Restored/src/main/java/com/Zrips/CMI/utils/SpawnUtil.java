package com.Zrips.CMI.utils;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SpawnUtil {
  public static final String defaultG = "CMISpawnGroup";
  public static final String defaultGW = "CMISWG";
  static HashMap<String, SpawnUtil.SpawnPoint> map = new HashMap<>();
  private static Set<String> ignoredSpawnWorlds = new HashSet<>();
  private static boolean SpawnSpawnOnJoin = false;

  public static void addNew(String string, CMILocation cmiLocation, boolean flag) {
    addNew(string, cmiLocation, flag, null);
  }

  public static void addNew(String string, CMILocation cmiLocation, boolean flag, Integer integer) {
    addNew(string, cmiLocation, flag, integer, new ArrayList<>());
  }

  public static void addNew(
      String string, CMILocation cmiLocation, boolean flag, Integer integer, Set<String> set) {
    if (string == null && !set.isEmpty()) {
      string = "CMISWG" + new Random(2147483647L).nextInt(100000);
    }

    if (string == null) {
      string = "CMISpawnGroup";
    }

    if (cmiLocation != null) {
      map.put(string, new SpawnUtil.SpawnPoint(string, cmiLocation, flag, integer, set));
    }
  }

  public static void addNew(
      String string, CMILocation cmiLocation, boolean flag, Integer integer, List<World> list) {
    if (string == null && !list.isEmpty()) {
      string = "CMISWG" + new Random(2147483647L).nextInt(100000);
    }

    if (string == null) {
      string = "CMISpawnGroup";
    }

    if (cmiLocation != null) {
      map.put(string, new SpawnUtil.SpawnPoint(string, cmiLocation, flag, integer, list));
    }
  }

  public static CMILocation getSpawnPoint(Player player) {
    if (player != null) {
      CMILocation cmiLocation = null;

      for (var entry : map.entrySet()) {
        if (!((String) entry.getKey()).equals("CMISpawnGroup")
            && !((String) entry.getKey()).startsWith("CMISWG")
            && PermissionsManager.CMIPerm.spawngroup_$1.hasPermission(
                player, (String) entry.getKey())) {
          if (((SpawnUtil.SpawnPoint) entry.getValue()).getWorlds().isEmpty()) {
            cmiLocation = ((SpawnUtil.SpawnPoint) entry.getValue()).getLocation();
            break;
          }

          if (((SpawnUtil.SpawnPoint) entry.getValue()).getWorlds().contains(player.getWorld())) {
            cmiLocation = ((SpawnUtil.SpawnPoint) entry.getValue()).getLocation();
            break;
          }
        }
      }

      if (cmiLocation == null) {
        for (var spawnEntry : map.entrySet()) {
          if (!((SpawnUtil.SpawnPoint) spawnEntry.getValue()).getWorlds().isEmpty()
              && ((SpawnUtil.SpawnPoint) spawnEntry.getValue())
                  .getWorlds()
                  .contains(player.getWorld())) {
            cmiLocation = ((SpawnUtil.SpawnPoint) spawnEntry.getValue()).getLocation();
            break;
          }
        }
      }

      if (cmiLocation != null) {
        return cmiLocation.clone();
      }
    }

    SpawnUtil.SpawnPoint spawnPoint = map.get("CMISpawnGroup");
    return spawnPoint != null ? spawnPoint.getLocation().clone() : null;
  }

  public static CMILocation getGroupReSpawnPoint(Player player) {
    if (player != null) {
      CMILocation cmiLocation = null;

      for (var entry : map.entrySet()) {
        if (!((String) entry.getKey()).equals("CMISpawnGroup")
            && !((String) entry.getKey()).startsWith("CMISWG")
            && ((SpawnUtil.SpawnPoint) entry.getValue()).isRespawn()
            && PermissionsManager.CMIPerm.respawngroup_$1.hasPermission(
                player, (String) entry.getKey())) {
          if (((SpawnUtil.SpawnPoint) entry.getValue()).getWorlds().isEmpty()) {
            cmiLocation = ((SpawnUtil.SpawnPoint) entry.getValue()).getLocation();
            break;
          }

          if (((SpawnUtil.SpawnPoint) entry.getValue()).getWorlds().contains(player.getWorld())) {
            cmiLocation = ((SpawnUtil.SpawnPoint) entry.getValue()).getLocation();
            break;
          }
        }
      }

      if (cmiLocation == null) {
        for (var spawnEntry : map.entrySet()) {
          if (!((SpawnUtil.SpawnPoint) spawnEntry.getValue()).getWorlds().isEmpty()
              && ((SpawnUtil.SpawnPoint) spawnEntry.getValue()).isRespawn()
              && ((SpawnUtil.SpawnPoint) spawnEntry.getValue())
                  .getWorlds()
                  .contains(player.getWorld())) {
            cmiLocation = ((SpawnUtil.SpawnPoint) spawnEntry.getValue()).getLocation();
            break;
          }
        }
      }

      if (cmiLocation != null) {
        return cmiLocation;
      }
    }

    SpawnUtil.SpawnPoint spawnPoint = map.get("CMISpawnGroup");
    return spawnPoint != null && spawnPoint.isRespawn() ? spawnPoint.getLocation() : null;
  }

  public static void save() {
    File file = new File(CMI.getInstance().getDataFolder(), "config.yml");
    YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    yamlConfiguration.options().copyDefaults(true);

    for (var entry : map.entrySet()) {
      String string = (String) entry.getKey();
      if (string.equals("CMISpawnGroup")) {
        setToConfig(yamlConfiguration, "Spawn.Main", (SpawnUtil.SpawnPoint) entry.getValue());
      } else {
        String groupKey = "Spawn.Groups." + string;
        setToConfig(yamlConfiguration, groupKey, (SpawnUtil.SpawnPoint) entry.getValue());
      }
    }

    try {
      yamlConfiguration.save(file);
    } catch (IOException ioException) {
      ioException.printStackTrace();
    }

    CMI.getInstance().getConfigManager().load();
  }

  private static void setToConfig(
      YamlConfiguration yamlConfiguration, String string, SpawnUtil.SpawnPoint spawnPoint) {
    yamlConfiguration.set(string, null);
    string = string + ".";
    yamlConfiguration.set(string + "Location", CMILocation.toString(spawnPoint.getLocation()));
    yamlConfiguration.set(string + "RespawnLocation", spawnPoint.isRespawn());
    yamlConfiguration.set(string + "Rng", spawnPoint.getRng());
    if (!spawnPoint.getWorlds().isEmpty()) {
      yamlConfiguration.set(string + "TeleportFrom", spawnPoint.getWorldsAsStringList());
    }
  }

  public static void loadConfig() {
    ConfigReader configReader = CMI.getInstance().getConfigManager().getConfig();
    configReader.addComment(
        "Spawn.SpawnOnJoin",
        new String[] {
          "Forces players to login in defined spawn point when logging into server",
          "Can be bypasses with "
              + PermissionsManager.CMIPerm.spawnonjoin_bypass.getPermission()
              + " permission node"
        });
    SpawnSpawnOnJoin = configReader.get("Spawn.SpawnOnJoin", false);
    configReader.addComment(
        "Spawn.IgnoredWorlds",
        new String[] {
          "List of worlds which should be ignored and players joining in those servers will not be"
              + " teleported to appropriate spawn point but will login at their log out location"
        });
    List list = configReader.get("Spawn.IgnoredWorlds", new ArrayList());
    CMIList.toLowerCase(list);
    ignoredSpawnWorlds.clear();
    ignoredSpawnWorlds.addAll(list);
    configReader.addComment(
        "Spawn.Main",
        new String[] {
          "Defines players spawn point after death if set to true, if not, then it will be used"
              + " only for /cmi spawn command",
          "RespawnLocation will indicate if you want to use this location as possible respawn point"
              + " for player after death",
          "TeleportFrom will indicate list of worlds from which player should be teleported to this"
              + " spawn point"
        });
    CMILocation cmiLocation = null;
    if (configReader.getC().isString("Spawn.Main.World")) {
      String string = configReader.get("Spawn.Main.World", "None");
      double number = configReader.get("Spawn.Main.X", 0.0);
      double mainY = configReader.get("Spawn.Main.Y", 0.0);
      double mainZ = configReader.get("Spawn.Main.Z", 0.0);
      double mainPitch = configReader.get("Spawn.Main.Pitch", 0.0);
      double mainYaw = configReader.get("Spawn.Main.Yaw", 0.0);
      cmiLocation = new CMILocation(string, number, mainY, mainZ);
      cmiLocation.setPitch((float) mainPitch);
      cmiLocation.setYaw((float) mainYaw);
    } else {
      cmiLocation = CMILocation.fromString(configReader.get("Spawn.Main.Location", ""));
    }

    Boolean respawnLocation = configReader.get("Spawn.Main.RespawnLocation", false);
    Integer integer = configReader.get("Spawn.Main.Rng", 0);
    HashSet hashSet = new HashSet();
    hashSet.addAll(configReader.get("Spawn.Main.TeleportFrom", new ArrayList()));
    if (cmiLocation != null && !cmiLocation.getWorldName().equalsIgnoreCase("none")) {
      addNew(null, cmiLocation, respawnLocation, integer, hashSet);
    }

    try {
      if (configReader.getC().isConfigurationSection("Spawn.Groups")) {
        for (String groupName :
            configReader.getC().getConfigurationSection("Spawn.Groups").getKeys(false)) {
          hashSet = new HashSet();
          String groupKeyPrefix = "Spawn.Groups." + groupName + ".";
          cmiLocation = null;
          if (configReader.getC().isString(groupKeyPrefix + "World")) {
            String world = configReader.get(groupKeyPrefix + "World", "None");
            double x = configReader.get(groupKeyPrefix + "X", 0.0);
            double spawnY = configReader.get(groupKeyPrefix + "Y", 0.0);
            double spawnZ = configReader.get(groupKeyPrefix + "Z", 0.0);
            double spawnPitch = configReader.get(groupKeyPrefix + "Pitch", 0.0);
            double spawnYaw = configReader.get(groupKeyPrefix + "Yaw", 0.0);
            cmiLocation = new CMILocation(world, x, spawnY, spawnZ);
            cmiLocation.setPitch((float) spawnPitch);
            cmiLocation.setYaw((float) spawnYaw);
          } else {
            cmiLocation = CMILocation.fromString(configReader.get(groupKeyPrefix + "Location", ""));
          }

          respawnLocation = configReader.get(groupKeyPrefix + "RespawnLocation", false);
          if (configReader.getC().isInt(groupKeyPrefix + "Rng")) {
            integer = configReader.get(groupKeyPrefix + "Rng", 0);
          }

          if (configReader.getC().isList(groupKeyPrefix + "TeleportFrom")) {
            hashSet.addAll(configReader.get(groupKeyPrefix + "TeleportFrom", new ArrayList()));
          }

          if (cmiLocation != null && !cmiLocation.getWorldName().equalsIgnoreCase("none")) {
            addNew(groupName, cmiLocation, respawnLocation, integer, hashSet);
          }
        }
      }
    } catch (Exception exception) {
    }
  }

  public static boolean isSpawnSpawnOnJoin() {
    return SpawnSpawnOnJoin;
  }

  public static Set<String> getIgnoredSpawnWorlds() {
    return ignoredSpawnWorlds;
  }

  public static boolean isIgnoresSpawnWorld(String string) {
    return ignoredSpawnWorlds.isEmpty() ? false : ignoredSpawnWorlds.contains(string.toLowerCase());
  }

  public static class SpawnPoint {
    private String group;
    private CMILocation location;
    private boolean respawn = false;
    private Integer rng = null;
    private List<World> worlds;
    private Set<String> worldNames = null;

    public SpawnPoint(String string, CMILocation cmiLocation, boolean flag) {
      this(string, cmiLocation, flag, null, new ArrayList<>());
    }

    public SpawnPoint(String string, CMILocation cmiLocation, boolean flag, Integer integer) {
      this(string, cmiLocation, flag, integer, new ArrayList<>());
    }

    public SpawnPoint(
        String string, CMILocation cmiLocation, boolean flag, Integer integer, List<World> list) {
      this.group = string;
      this.location = cmiLocation;
      this.respawn = flag;
      this.rng = integer;
      this.worlds = list;
    }

    public SpawnPoint(
        String string, CMILocation cmiLocation, boolean flag, Integer integer, Set<String> set) {
      this.group = string;
      this.location = cmiLocation;
      this.respawn = flag;
      this.rng = integer;
      this.worldNames = set;
    }

    public String getGroup() {
      return this.group;
    }

    public void setGroup(String string) {
      this.group = string;
    }

    public CMILocation getLocation() {
      if (this.rng != null && this.rng > 0) {
        Random random = new Random(System.currentTimeMillis());
        double offsetX = (random.nextInt(this.rng * 10 * 2) - this.rng * 10) / 10.0;
        double offsetZ = (random.nextInt(this.rng * 10 * 2) - this.rng * 10) / 10.0;
        return new CMILocation(this.location.clone().add(offsetX, 0.0, offsetZ));
      } else {
        return this.location;
      }
    }

    public void setLocation(CMILocation cmiLocation) {
      this.location = cmiLocation;
    }

    public boolean isRespawn() {
      return this.respawn;
    }

    public void setRespawn(boolean flag) {
      this.respawn = flag;
    }

    public Integer getRng() {
      return this.rng;
    }

    public void setRng(Integer integer) {
      this.rng = integer;
    }

    public List<World> getWorlds() {
      if (this.worlds == null) {
        this.worlds = new ArrayList<>();
        if (this.worldNames != null) {
          for (String string : this.worldNames) {
            World world = CMI.getInstance().getUtilManager().getWorld(string);
            if (world != null) {
              this.worlds.add(world);
            }
          }

          this.worldNames = null;
        }
      }

      return this.worlds;
    }

    public List<String> getWorldsAsStringList() {
      ArrayList<String> arrayList = new ArrayList();
      if (!this.getWorlds().isEmpty()) {
        for (World world : this.getWorlds()) {
          if (world != null) {
            arrayList.add(world.getName());
          }
        }
      }

      return arrayList;
    }

    public void setWorlds(List<World> list) {
      this.worlds = list;
    }
  }
}
