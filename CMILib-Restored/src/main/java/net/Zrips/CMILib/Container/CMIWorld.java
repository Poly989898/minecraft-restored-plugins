package net.Zrips.CMILib.Container;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public class CMIWorld {
  @Nullable private UUID uuid = null;
  @Nullable private String name = null;
  static HashMap<String, String> worldNames = new HashMap<>();
  static HashMap<UUID, String> worldNamesByUUID = new HashMap<>();
  static CMITask saveTask = null;

  public CMIWorld() {}

  public CMIWorld(Location loc) {
    if (loc != null) {
      if (loc.getWorld() != null) {
        this.uuid =
            Version.isCurrentEqualOrHigher(Version.v1_16_R1) ? loc.getWorld().getUID() : null;
        this.name = loc.getWorld().getName();
      }
    }
  }

  public CMIWorld(CMILocation loc) {
    if (loc.getWorld() != null) {
      this.uuid = Version.isCurrentEqualOrHigher(Version.v1_16_R1) ? loc.getWorld().getUID() : null;
      this.name = loc.getWorld().getName();
    } else {
      this.name = loc.getWorldName();
    }
  }

  public CMIWorld(World world) {
    if (world != null) {
      this.uuid = Version.isCurrentEqualOrHigher(Version.v1_16_R1) ? world.getUID() : null;
      this.name = world.getName();
    }
  }

  public CMIWorld(String worldName) {
    if (worldName.length() == 36) {
      try {
        this.uuid = UUID.fromString(worldName);
        return;
      } catch (Throwable var3) {
      }
    }

    this.name = worldName;
  }

  public CMIWorld(UUID uuid) {
    this.uuid = uuid;
  }

  @Nullable
  public World getWorld() {
    if (this.uuid != null && Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      return Bukkit.getWorld(this.uuid);
    } else {
      return this.name != null ? Bukkit.getWorld(this.name) : null;
    }
  }

  @Nullable
  public UUID getUUID() {
    return this.uuid;
  }

  @Nullable
  public String getWorldName() {
    if (this.name != null) {
      return this.name;
    }

    World world = this.getWorld();
    return world != null ? world.getName() : null;
  }

  public static void onDisable() {
    worldNames.clear();
  }

  public static String getWorldNameFormatted(World world) {
    if (world != null && world.getName() != null) {
      String name = worldNames.get(world.getName());
      return name != null ? name : world.getName();
    } else {
      return "Unknown";
    }
  }

  public static void initialize() {
    List<String> worlds = new ArrayList<>();

    for (World one : Bukkit.getWorlds()) {
      worlds.add(
          one.getName() + "-&2" + CMIText.everyFirstToUpperCase(one.getName().replace("_", " ")));
    }

    ConfigReader locale = CMILib.getInstance().getConfigManager().getLocaleConfig();
    worlds = locale.get("Location.WorldNames", worlds);
    worldNames.clear();

    for (String one : worlds) {
      if (one.endsWith(" ")) {
        one = one.substring(0, one.length() - 1);
      }

      String[] wsplit = one.split("-", 2);
      if (wsplit.length != 1) {
        worldNames.put(wsplit[0], wsplit[1]);
      }
    }

    if (worldNames.isEmpty()) {
      worldNames.put("--NothingFound--", "--NothingFound--");
    }

    initWorldNames();
  }

  private static void initWorldNames() {
    if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      loadWorldNames();

      try {
        for (World one : Bukkit.getWorlds()) {
          addWorldName(one.getUID(), one.getName());
        }
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }

  private static void loadWorldNames() {
    ConfigReader cfg = null;

    try {
      cfg = new ConfigReader(CMILib.getInstance(), "Saves" + File.separator + "worldNames.yml");
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (cfg != null) {
      for (String one : cfg.getC().getKeys(false)) {
        UUID uuid = null;

        try {
          uuid = UUID.fromString(one);
        } catch (Throwable e) {
          e.printStackTrace();
        }

        if (uuid != null) {
          String name = cfg.getC().getString(one);
          if (name != null) {
            worldNamesByUUID.put(uuid, name);
          }
        }
      }
    }
  }

  public static void cacheWorld(World world) {
    if (world != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
        addWorldName(world.getUID(), world.getName());
      }
    }
  }

  private static void addWorldName(UUID uuid, String name) {
    String old = worldNamesByUUID.get(uuid);
    if (old == null || !old.equals(name)) {
      saveWorldNames();
    }

    worldNamesByUUID.put(uuid, name);
  }

  private static void saveWorldNames() {
    if (saveTask == null) {
      saveTask =
          CMIScheduler.runLaterAsync(
              CMILib.getInstance(),
              () -> {
                ConfigReader cfg = null;

                try {
                  cfg =
                      new ConfigReader(
                          CMILib.getInstance(), "Saves" + File.separator + "worldNames.yml");
                } catch (Exception e) {
                  e.printStackTrace();
                }

                if (cfg != null) {
                  for (Entry<UUID, String> one : worldNamesByUUID.entrySet()) {
                    UUID uuid = one.getKey();
                    String name = one.getValue();
                    cfg.set(uuid.toString(), name);
                  }

                  cfg.save();
                  saveTask = null;
                }
              },
              100L);
    }
  }

  public static boolean insideWorldBorder(Location loc) {
    try {
      Location center = loc.getWorld().getWorldBorder().getCenter();
      double size = loc.getWorld().getWorldBorder().getSize() / 2.0;
      double minX = center.getX() - size;
      double maxX = center.getX() + size;
      double minZ = center.getZ() - size;
      double maxZ = center.getZ() + size;
      return loc.getX() >= minX && loc.getX() < maxX && loc.getZ() >= minZ && loc.getZ() < maxZ;
    } catch (Throwable e) {
      e.printStackTrace();
      return true;
    }
  }

  public static int getMinHeight(CMIWorld world) {
    if (Version.isCurrentEqualOrLower(Version.v1_16_R3)) {
      return 0;
    } else {
      return world != null && world.getWorld() != null ? world.getWorld().getMinHeight() : 0;
    }
  }

  public static int getMinHeight(World world) {
    if (Version.isCurrentEqualOrLower(Version.v1_16_R3)) {
      return 0;
    } else {
      return world == null ? 0 : world.getMinHeight();
    }
  }

  public static int getMaxHeight(CMIWorld world) {
    World w = world.getWorld();
    return w == null ? 256 : getMaxHeight(w);
  }

  public static int getMaxHeight(World world) {
    if (world == null) {
      return 256;
    }

    if (world.getEnvironment() == null) {
      return 256;
    }

    try {
      switch (world.getEnvironment()) {
        case NETHER:
          return 128;
        case NORMAL:
        case THE_END:
          if (Version.isCurrentEqualOrHigher(Version.v1_17_R1)) {
            return world.getMaxHeight();
          }

          return 256;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return 256;
  }

  @Nullable
  public static CMIWorld getCMIWorld(String name) {
    World w = getWorld(name);
    return w != null ? new CMIWorld(w) : null;
  }

  @Nullable
  public static World getWorld(String name) {
    if (name == null) {
      return null;
    }

    World w = Bukkit.getWorld(name);
    if (w != null) {
      return w;
    }

    String originalName = name;
    name = name.replaceAll("[_|.|-]", "");

    for (World one : Bukkit.getWorlds()) {
      String n = one.getName().replaceAll("[_|.|-]", "");
      if (n.equalsIgnoreCase(name)) {
        return one;
      }
    }

    if (originalName.length() == 36) {
      try {
        return Bukkit.getWorld(UUID.fromString(originalName));
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  @Nullable
  public static CMIWorld getCMIWorld(UUID uuid) {
    World w = getWorld(uuid);
    return w != null ? new CMIWorld(w) : null;
  }

  @Nullable
  public static World getWorld(UUID uuid) {
    return !Version.isCurrentEqualOrHigher(Version.v1_16_R1) ? null : Bukkit.getWorld(uuid);
  }

  @Nullable
  public static String getWorldName(UUID uuid) {
    if (!Version.isCurrentEqualOrHigher(Version.v1_16_R1)) {
      return null;
    }

    String name = worldNamesByUUID.get(uuid);
    if (name != null) {
      return name;
    }

    World w = getWorld(uuid);
    return w != null ? w.getName() : null;
  }
}
