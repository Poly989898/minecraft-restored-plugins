package com.Zrips.CMI.Modules.Teleportations;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.RandomTeleport;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.commands.list.rt;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.Zrips.CMILib.Container.CMIBiome;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMIList;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class RandomTeleportationManager {
  private CMI plugin;
  Set<String> cacheFilling = new HashSet<>();
  private HashMap<String, RandomTeleport> randomTeleports = new HashMap<>();
  private int randomTeleportCooldown = 3;
  private int randomTeleportMaxTries = 10;
  private List<String> randomTeleportExcludedBiomes = new ArrayList<>();
  static Random random = new Random(System.nanoTime());
  private static final String fileName = "RandomTeleportations.yml";
  ConcurrentHashMap<UUID, CMIRunningRandomTeleports> playerRandomTeleports =
      new ConcurrentHashMap<>();
  String[] substrings = new String[] {"...", "..", "."};

  public RandomTeleportationManager(CMI cmi) {
    this.plugin = cmi;
  }

  private Set<String> toLowerCase(List<String> list) {
    return list.stream()
        .map(string -> string.toLowerCase().replace(" ", "").replace("_", ""))
        .collect(Collectors.toSet());
  }

  private Set<String> toLowerCase(Set<String> set) {
    return set.stream()
        .map(string -> string.toLowerCase().replace(" ", "").replace("_", ""))
        .collect(Collectors.toSet());
  }

  public void loadConfig() {
    ConfigReader configReader = null;

    try {
      configReader =
          new ConfigReader(
              CMI.getInstance(), "Settings" + File.separator + "RandomTeleportations.yml");
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    if (configReader == null) {
      CMI.getInstance()
          .consoleMessage(
              "&cFailed to load Settings" + File.separator + "RandomTeleportations.yml" + " file");
    } else {
      ConfigReader mainConfig = CMI.getInstance().getConfigManager().getConfig();
      if (mainConfig.getC().isConfigurationSection("RandomTeleportation")) {
        configReader
            .getC()
            .set("RandomTeleportation", mainConfig.getC().get("RandomTeleportation"));
      }

      HashSet<String> hashSet = new HashSet();

      for (World world : Bukkit.getWorlds()) {
        hashSet.add(world.getName());
      }

      boolean flag = false;
      this.randomTeleports.clear();
      configReader.addComment(
          "RandomTeleportation.Cooldown",
          new String[] {"How long force player to wait before using command again."});
      this.randomTeleportCooldown = configReader.get("RandomTeleportation.Cooldown", 5);
      configReader.addComment(
          "RandomTeleportation.MaxTries",
          new String[] {
            "How many times to try find correct location for teleportation.",
            "Keep it at low number, as player always can try again after delay"
          });
      this.randomTeleportMaxTries = configReader.get("RandomTeleportation.MaxTries", 20);
      configReader.addComment(
          "RandomTeleportation.ExcludedBiomes",
          new String[] {"List of biomes to exclude from random teleportation"});
      var list =
          configReader.get(
              "RandomTeleportation.ExcludedBiomes",
              Arrays.asList(
                  "Ocean",
                  "Deep ocean",
                  "Deep cold ocean",
                  "Cold ocean",
                  "Deep lukewarm ocean",
                  "Lukewarm ocean",
                  "Warm ocean",
                  "River"));
      this.randomTeleportExcludedBiomes.clear();

      for (String string : list) {
        string = string.replace(" ", "").replace("_", "");

        for (String biome : CMIBiome.values()) {
          if (biome.replace("_", "").equalsIgnoreCase(string)) {
            this.randomTeleportExcludedBiomes.add(biome);
          }
        }
      }

      CMIList.toLowerCase(this.randomTeleportExcludedBiomes);
      configReader.addComment(
          "RandomTeleportation.AutoGenerateWorlds",
          new String[] {
            "If this set to true we will generate random teleport default settings for all detected"
                + " worlds",
            "Setting to false will no longer generate world setups, but you can add them manually"
                + " if needed"
          });
      Boolean autoGenerateWorlds = configReader.get("RandomTeleportation.AutoGenerateWorlds", true);
      if (configReader.getC().isConfigurationSection("RandomTeleportation.Worlds")) {
        ConfigurationSection configurationSection =
            configReader.getC().getConfigurationSection("RandomTeleportation.Worlds");
        if (configurationSection != null) {
          for (String worldName : configurationSection.getKeys(false)) {
            String worldPrefix = "RandomTeleportation.Worlds." + worldName + ".";
            boolean enabled = configReader.get(worldPrefix + "Enabled", true);
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "MaxTries",
                  new String[] {
                    "Amount of times we should try to find valid location. This is mostly directly"
                        + " correlates with amount of chunks we will check",
                    "If set to 0 we will use default MaxTries set at the top of file"
                  });
            }

            int value = configReader.get(worldPrefix + "MaxTries", 0);
            value = value < 1 ? this.randomTeleportMaxTries : value;
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Cooldown",
                  new String[] {
                    "Cooldown when looking for valid location, if set to 0 we will use default"
                        + " Cooldown set at the top of file"
                  });
            }

            int cooldown =
                CMINumber.clamp(
                    configReader.get(worldPrefix + "Cooldown", 0), 0, Integer.MAX_VALUE);
            if (!flag) {
              configReader.addComment(
                  "RandomTeleportation.Worlds." + worldName,
                  new String[] {
                    "World name to use this feature. Add another one with appropriate name to"
                        + " enable random teleportation"
                  });
            }

            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Range.Max",
                  new String[] {
                    "Max coordinate to teleport, setting to 1000, player can be teleported between"
                        + " -1000 and 1000 blocks between defined center location",
                    "For example having centerX at 2000 and centerZ at 3000 while MaxRange is set"
                        + " to 1500 we will teleport player between x:500;z:1500 and x:3500;z:4500"
                        + " coordinates"
                  });
            }

            int maxRange =
                configReader.get(
                    worldPrefix + "Range.Max",
                    configReader.getC().getInt(worldPrefix + "MaxRange", 1000));
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Range.Min",
                  new String[] {
                    "If maxcord set to 1000 and mincord to 500, then player can be teleported"
                        + " between -1000 to -500 and 1000 to 500 coordinates"
                  });
            }

            int minRange =
                configReader.get(
                    worldPrefix + "Range.Min",
                    configReader.getC().getInt(worldPrefix + "MinRange", 500));
            int centerX =
                configReader.get(
                    worldPrefix + "Center.X",
                    configReader.getC().getInt(worldPrefix + "CenterX", 0));
            int centerZ =
                configReader.get(
                    worldPrefix + "Center.Z",
                    configReader.getC().getInt(worldPrefix + "CenterZ", 0));
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Circle",
                  new String[] {
                    "Shape of area player can be teleported into. If your map is square shaped then"
                        + " keep this at false"
                  });
            }

            boolean circle = configReader.get(worldPrefix + "Circle", false);
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "SurfaceOnly",
                  new String[] {
                    "With this option we will only attempt to teleport player on highest block and"
                        + " ignore any cave teleportations",
                    "Exception when you have Ignore.Leaves set to true which will attempt to place"
                        + " player under the tree leaves if there is any empty space"
                  });
            }

            boolean surfaceOnly =
                configReader.get(
                    worldPrefix + "SurfaceOnly",
                    configReader.getC().getBoolean(worldPrefix + "surfaceOnly", false));
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "FromAnotherWorld",
                  new String[] {
                    "When enabled player will be able to random teleport at specied world while"
                        + " being in another one"
                  });
            }

            boolean fromAnotherWorld = configReader.get(worldPrefix + "FromAnotherWorld", true);
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "RequireWorldPermission",
                  new String[] {
                    "When enabled player will be required to have ("
                        + PermissionsManager.CMIPerm.randomteleport_$1.getPermission(worldName)
                        + ") permission node to random teleport inside this world"
                  });
            }

            boolean requireWorldPermission =
                configReader.get(worldPrefix + "RequireWorldPermission", false);
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "YLimits.Min",
                  new String[] {
                    "Location detection limitations on Y axis. If you want to minimize"
                        + " teleportations underground then you can set this value to 63",
                    "Higher limitations might increase time needed to find valid one"
                  });
            }

            int minYLimit =
                configReader.get(
                    worldPrefix + "YLimits.Min",
                    configReader.getC().getInt(worldPrefix + "minY", 0));
            int maxYLimit =
                configReader.get(
                    worldPrefix + "YLimits.Max",
                    configReader.getC().getInt(worldPrefix + "maxY", 256));
            boolean ignoreWater =
                configReader.get(
                    worldPrefix + "Ignore.Water",
                    configReader.getC().getBoolean(worldPrefix + "IgnoreWater", false));
            boolean ignoreLava =
                configReader.get(
                    worldPrefix + "Ignore.Lava",
                    configReader.getC().getBoolean(worldPrefix + "IgnoreLava", false));
            boolean ignoreLeaves =
                configReader.get(
                    worldPrefix + "Ignore.Leaves",
                    configReader.getC().getBoolean(worldPrefix + "IgnoreLeaves", false));
            boolean ignorePowderSnow =
                configReader.get(
                    worldPrefix + "Ignore.PowderSnow",
                    configReader.getC().getBoolean(worldPrefix + "ignorePowderSnow", false));
            boolean ignoreCrawlSpaces = false;
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
              if (!flag) {
                configReader.addComment(
                    worldPrefix + "Ignore.CrawlSpaces",
                    new String[] {
                      "When enabled we will not teleport player to single block spaces which forces"
                          + " player into crawl mode"
                    });
              }

              ignoreCrawlSpaces = configReader.get(worldPrefix + "Ignore.CrawlSpaces", false);
            }

            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Ignore.Materials",
                  new String[] {
                    "List of floor materials we should ignore when random teleporting"
                  });
            }

            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll(
                configReader.get(
                    worldPrefix + "Ignore.Materials",
                    (List<String>)
                        configReader
                            .getC()
                            .getList(worldPrefix + "ignoredMaterials", new ArrayList<String>())));
            Set set =
                arrayList.stream()
                    .<CMIMaterial>map(CMIMaterial::get)
                    .filter(biome -> biome != null && !biome.isNone())
                    .collect(Collectors.toCollection(HashSet::new));
            if (!flag) {
              configReader.addComment(
                  worldPrefix + "Ignore.Biomes",
                  new String[] {
                    "List of biomes we should ignore when random teleporting",
                    "Keeping it empty will include all of them",
                    "If this is empty and ExcludedBiomes contains set biomes then we will use it"
                  });
            }

            Set allowedBiomes =
                this.toLowerCase(configReader.get(worldPrefix + "Ignore.Biomes", new ArrayList()));
            if (allowedBiomes.isEmpty()) {
              allowedBiomes.addAll(this.getRandomTeleportExcludedBiomes());
            }

            if (!flag) {
              configReader.addComment(
                  worldPrefix + "PreferredBiomes",
                  new String[] {
                    "List of biomes we should look for when random teleporting",
                    "If this contains biome names then we will ignore Ignore.Biomes section"
                  });
            }

            Set excludedBiomes =
                this.toLowerCase(
                    configReader.get(worldPrefix + "PreferredBiomes", new ArrayList()));
            if (!excludedBiomes.isEmpty()) {
              allowedBiomes.clear();
            }

            World worldRef = this.plugin.getUtilManager().getWorld(worldName);
            if (worldRef == null) {
              this.plugin.consoleMessage(
                  "&e(RandomTeleportation) Can't find world with (" + worldName + ") name");
            }

            flag = true;
            hashSet.remove(worldName);
            RandomTeleport teleport =
                new RandomTeleport(
                        enabled,
                        maxRange,
                        minRange,
                        new CMILocation(worldName, centerX, 63.0, centerZ))
                    .setCircle(circle)
                    .setIgnoreLava(ignoreLava)
                    .setIgnoreLeaves(ignoreLeaves)
                    .setIgnoreWater(ignoreWater)
                    .setIgnorePowderSnow(ignorePowderSnow)
                    .setIgnoredMaterials(set)
                    .setMinY(minYLimit)
                    .setMaxY(maxYLimit)
                    .setFromAnotherWorld(fromAnotherWorld)
                    .setRequireWorldPermission(requireWorldPermission)
                    .setSurfaceOnly(surfaceOnly)
                    .setIgnoredBiomes(allowedBiomes)
                    .setPreferredBiomes(excludedBiomes)
                    .setMaxTries(value)
                    .setCooldown(cooldown)
                    .setIgnoreCrawl(ignoreCrawlSpaces);
            if (!circle && worldRef != null) {
              try {
                Location location = worldRef.getWorldBorder().getCenter();
                double number = worldRef.getWorldBorder().getSize() / 2.0;
                double locMinX = location.getX() - number;
                double locMaxX = location.getX() + number;
                double locMinZ = location.getZ() - number;
                double locMaxZ = location.getZ() + number;
                double minCenterX = teleport.getCenter().getX() - teleport.getMaxDistance();
                double maxCenterX = teleport.getCenter().getX() + teleport.getMaxDistance();
                double minCenterZ = teleport.getCenter().getZ() - teleport.getMaxDistance();
                double maxCenterZ = teleport.getCenter().getZ() + teleport.getMaxDistance();
                if (locMinX > minCenterX) {
                  this.plugin.consoleMessage(
                      "&5Random teleport location is out of &6minimal ("
                          + locMinX
                          + " -> "
                          + minCenterX
                          + ") &5world border &6X &5coordinate bounds for &6"
                          + worldRef.getName()
                          + " &5world. Please update values.");
                }

                if (locMaxX < maxCenterX) {
                  this.plugin.consoleMessage(
                      "&5Random teleport location is out of &6maximal ("
                          + locMaxX
                          + " -> "
                          + maxCenterX
                          + ") &5world border &6X &5coordinate bounds for &6"
                          + worldRef.getName()
                          + " &5world. Please update values.");
                }

                if (locMinZ > minCenterZ) {
                  this.plugin.consoleMessage(
                      "&5Random teleport location is out of &6minimal ("
                          + locMinZ
                          + " -> "
                          + minCenterZ
                          + ") &5world border &6Z &5coordinate bounds for &6"
                          + worldRef.getName()
                          + " &5world. Please update values.");
                }

                if (locMaxZ < maxCenterZ) {
                  this.plugin.consoleMessage(
                      "&5Random teleport location is out of &6maximal ("
                          + locMaxZ
                          + " -> "
                          + maxCenterZ
                          + ") &5world border &6Z &5coordinate bounds for &6"
                          + worldRef.getName()
                          + " &5world. Please update values.");
                }
              } catch (NoSuchMethodError noSuchMethodError) {
              }
            }

            this.randomTeleports.put(worldName, teleport);
          }
        }
      }

      if (autoGenerateWorlds) {
        for (World bukkitWorld : Bukkit.getWorlds()) {
          if (hashSet.contains(bukkitWorld.getName())) {
            String worldName = bukkitWorld.getName();
            worldName = worldName.replace(".", "_");
            String worldPrefix = "RandomTeleportation.Worlds." + worldName + ".";
            boolean enabled = configReader.get(worldPrefix + "Enabled", true);
            int maxTries = configReader.get(worldPrefix + "MaxTries", 0);
            int cooldown = configReader.get(worldPrefix + "Cooldown", 0);
            int maxRange = configReader.get(worldPrefix + "Range.Max", 1000);
            int minRange = configReader.get(worldPrefix + "Range.Min", 500);
            int centerX = configReader.get(worldPrefix + "Center.X", 0);
            int centerZ = configReader.get(worldPrefix + "Center.Z", 0);
            boolean circle = configReader.get(worldPrefix + "Circle", false);
            boolean ignoreWater = configReader.get(worldPrefix + "Ignore.Water", true);
            boolean ignoreLava = configReader.get(worldPrefix + "Ignore.Lava", true);
            boolean ignoreLeaves = configReader.get(worldPrefix + "Ignore.Leaves", true);
            boolean ignorePowderSnow = configReader.get(worldPrefix + "Ignore.PowderSnow", false);
            boolean ignoreCrawlSpaces = false;
            if (Version.isCurrentEqualOrHigher(Version.v1_14_R1)) {
              ignoreCrawlSpaces = configReader.get(worldPrefix + "Ignore.CrawlSpaces", false);
            }

            ArrayList<String> biomeNames = new ArrayList<>();
            biomeNames.addAll(configReader.get(worldPrefix + "Ignore.Materials", biomeNames));
            configReader.get(worldPrefix + "Ignore.Biomes", new ArrayList());
            configReader.get(worldPrefix + "PreferredBiomes", new ArrayList());
            Set worldBiomes =
                biomeNames.stream()
                    .<CMIMaterial>map(CMIMaterial::get)
                    .filter(biome -> biome != null && !biome.isNone())
                    .collect(Collectors.toCollection(HashSet::new));
            boolean surfaceOnly = configReader.get(worldPrefix + "SurfaceOnly", false);
            boolean fromAnotherWorld = configReader.get(worldPrefix + "FromAnotherWorld", true);
            boolean requireWorldPermission =
                configReader.get(worldPrefix + "RequireWorldPermission", false);
            int minYLimit =
                configReader.get(worldPrefix + "YLimits.Min", CMIWorld.getMinHeight(bukkitWorld));
            int maxYLimit =
                configReader.get(worldPrefix + "YLimits.Max", CMIWorld.getMaxHeight(bukkitWorld));
            RandomTeleport teleport =
                new RandomTeleport(
                    enabled,
                    maxRange,
                    minRange,
                    new CMILocation(
                        bukkitWorld, centerX, CMIWorld.getMaxHeight(bukkitWorld) / 2, centerZ));
            teleport.setCircle(circle);
            teleport
                .setCircle(circle)
                .setIgnoreLava(ignoreLava)
                .setIgnoreWater(ignoreWater)
                .setIgnoreLeaves(ignoreLeaves)
                .setIgnorePowderSnow(ignorePowderSnow)
                .setIgnoredMaterials(worldBiomes)
                .setMinY(minYLimit)
                .setMaxY(maxYLimit)
                .setFromAnotherWorld(fromAnotherWorld)
                .setRequireWorldPermission(requireWorldPermission)
                .setSurfaceOnly(surfaceOnly)
                .setIgnoreCrawl(ignoreCrawlSpaces);
            this.randomTeleports.put(bukkitWorld.getName(), teleport);
          }
        }
      }

      configReader.save();
    }
  }

  public RandomTeleport getRandomTeleport(World world) {
    return this.getRandomTeleport(world, false);
  }

  public RandomTeleport getRandomTeleport(String string) {
    return this.getRandomTeleport(string, false);
  }

  public RandomTeleport getRandomTeleport(World world, boolean flag) {
    return world == null ? null : this.getRandomTeleport(world.getName(), flag);
  }

  public RandomTeleport getRandomTeleport(String string, boolean flag) {
    if (string == null) {
      return null;
    }

    RandomTeleport teleport = this.randomTeleports.get(string);
    return teleport == null
        ? null
        : (teleport.isEnabled() && teleport.getCenter().getWorld() != null
            ? teleport
            : (flag ? teleport : null));
  }

  public HashMap<String, RandomTeleport> getRandomTeleports() {
    return this.randomTeleports;
  }

  public int getRandomTeleportMaxTries() {
    return this.randomTeleportMaxTries;
  }

  public int getRandomTeleportCooldown() {
    return this.randomTeleportCooldown;
  }

  public List<String> getRandomTeleportExcludedBiomes() {
    return this.randomTeleportExcludedBiomes;
  }

  public CompletableFuture<CMITeleportLocation> getRandomTeleportationLocation(
      RandomTeleport teleport,
      Location location,
      CompletableFuture<CMIChunkSnapShot> completableFuture) {
    return completableFuture.thenComposeAsync(
        cmiChunkSnapShot ->
            CompletableFuture.supplyAsync(
                () -> {
                  CMITeleportLocation cmiTeleportLocation =
                      new CMITeleportLocation(cmiChunkSnapShot.getWorld());
                  ChunkSnapshot chunkSnapshot = cmiChunkSnapShot.getSnapshot();
                  int value = -999;
                  int bestY = -999;
                  PositionRelativeData positionRelativeData = new PositionRelativeData(location);
                  Boolean found = null;
                  boolean flag = false;

                  for (int scanY = teleport.getMaxY() - 2; scanY > teleport.getMinY(); scanY--) {
                    if (value != -999
                        || this.plugin
                            .getTeleportHandler()
                            .isEmptySpaceForPlayer(
                                chunkSnapshot,
                                scanY,
                                positionRelativeData,
                                teleport.isIgnoreCrawl())) {
                      value = scanY;
                      CMIMaterial cmiMaterial =
                          this.plugin
                              .getTeleportHandler()
                              .getBlockType(chunkSnapshot, scanY - 1, positionRelativeData);
                      if (found == null) {
                        if (cmiMaterial.containsCriteria(CMIMC.LEAVES)) {
                          found = true;
                        } else if (!cmiMaterial.isAir()) {
                          found = false;
                        }
                      }

                      if (!this.plugin.getTeleportHandler().isEmpty(cmiMaterial, false)
                          && this.plugin
                              .getTeleportHandler()
                              .isEmptySpaceForPlayer(
                                  chunkSnapshot,
                                  scanY,
                                  positionRelativeData,
                                  teleport.isIgnoreCrawl())) {
                        try {
                          Object object = null;
                          if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
                            object =
                                chunkSnapshot.getBiome(
                                    positionRelativeData.getLocalX(),
                                    scanY,
                                    positionRelativeData.getLocalZ());
                          } else {
                            object =
                                chunkSnapshot.getBiome(
                                    positionRelativeData.getLocalX(),
                                    positionRelativeData.getLocalZ());
                          }

                          String string = CMIBiome.getBiomeName(object);
                          if (teleport.isIgnoredBiome(string)) {
                            return cmiTeleportLocation;
                          }

                          if (!teleport.getPreferredBiomes().isEmpty()
                              && !teleport.isPreferredBiome(string)) {
                            return cmiTeleportLocation;
                          }
                        } catch (Throwable throwable) {
                          throwable.printStackTrace();
                        }

                        if (teleport.isIgnoreLava()
                            && this.plugin.getTeleportHandler().isBad(cmiMaterial)) {
                          if (teleport.isSurfaceOnly()) {
                            break;
                          }
                        } else if (!teleport.isIgnoreWater()
                            || !cmiMaterial.isWater()
                                && !this.plugin
                                    .getTeleportHandler()
                                    .getBlockType(chunkSnapshot, scanY, positionRelativeData)
                                    .isWater()) {
                          if (teleport.isIgnorePowderedSnow()
                              && cmiMaterial.equals(CMIMaterial.POWDER_SNOW)) {
                            if (teleport.isSurfaceOnly()) {
                              break;
                            }
                          } else if (teleport.isIgnoreLeaves()
                              && cmiMaterial.containsCriteria(CMIMC.LEAVES)) {
                            if (teleport.isSurfaceOnly() && found != null && !found) {
                              break;
                            }
                          } else if (teleport.isIgnoreMaterials()
                              && teleport.isIgnoredMaterial(cmiMaterial)) {
                            if (teleport.isSurfaceOnly()) {
                              break;
                            }
                          } else if (!teleport.isIgnoreWater() || !flag) {
                            flag = false;
                            bestY = scanY;
                            break;
                          }
                        } else {
                          flag = true;
                          if (teleport.isSurfaceOnly()) {
                            break;
                          }
                        }
                      }
                    }
                  }

                  cmiTeleportLocation.setYaw(random.nextInt(359));
                  return bestY > -999
                      ? cmiTeleportLocation.setPosition(
                          new Vector(location.getBlockX() + 0.5, bestY, location.getBlockZ() + 0.5))
                      : cmiTeleportLocation;
                }));
  }

  public CompletableFuture<Boolean> randomTeleport(Player player, World world) {
    if (world == null) {
      return CompletableFuture.completedFuture(false);
    }

    RandomTeleport teleport = this.plugin.getRandomTeleportationManager().getRandomTeleport(world);
    if (teleport == null) {
      return CompletableFuture.completedFuture(false);
    }

    if (this.playerRandomTeleports.containsKey(player.getUniqueId())
        && this.playerRandomTeleports.get(player.getUniqueId()).getEndTime()
            > System.currentTimeMillis()) {
      return CompletableFuture.completedFuture(false);
    }

    int value = this.plugin.getUtilManager().getMaxWorldHeight(world);
    this.playerRandomTeleports.put(player.getUniqueId(), new CMIRunningRandomTeleports());
    return CompletableFuture.supplyAsync(
        () -> {
          CMICuboidArea cmiCuboidArea = null;

          try {
            Location location = world.getWorldBorder().getCenter();
            double number = world.getWorldBorder().getSize();
            Vector vector = location.toVector();
            cmiCuboidArea = new CMICuboidArea(world);
            cmiCuboidArea.setHighPoint(
                vector.clone().setY(600).setX(vector.getX() + number).setZ(vector.getZ() + number));
            cmiCuboidArea.setLowPoint(
                vector
                    .clone()
                    .setY(-600)
                    .setX(vector.getX() - number)
                    .setZ(vector.getZ() - number));
          } catch (Throwable throwable) {
          }

          int maxTries = teleport.getMaxTries();
          int attemptCount = teleport.getMaxTries();

          while (maxTries > 0) {
            int substringIndex = maxTries % this.substrings.length;
            CMILC.sendMessage(
                player,
                CMILC.getIM(
                    rt.class.getSimpleName(),
                    "ongoingSearch",
                    "[max]",
                    attemptCount,
                    "[current]",
                    attemptCount - maxTries + 1,
                    "[dots]",
                    this.substrings[substringIndex]));
            maxTries--;
            CMIVectorInt3D cmiVectorInt3D = teleport.getRandomLocation();
            Location candidate =
                new Location(
                    world,
                    cmiVectorInt3D.getX(),
                    world.getEnvironment().equals(Environment.NETHER) ? value - 10 : value / 2,
                    cmiVectorInt3D.getZ());
            if (candidate.getBlockY() > teleport.getMaxY()) {
              candidate.setY(teleport.getMaxY());
            }

            if (cmiCuboidArea == null || cmiCuboidArea.containsLoc(candidate)) {
              var completableFuture =
                  this.plugin.getTeleportHandler().getSnapshot(candidate, true, true);
              CompletableFuture pendingLocation =
                  this.getRandomTeleportationLocation(teleport, candidate, completableFuture);
              Boolean isValid = false;

              try {
                CompletableFuture pendingCheck =
                    teleportPlayerRandom(player, TeleportCause.PLUGIN, pendingLocation);
                pendingCheck =
                    pendingCheck.exceptionally(
                        object -> {
                          this.playerRandomTeleports.remove(player.getUniqueId());
                          return false;
                        });
                isValid = (Boolean) pendingCheck.get();
              } catch (Throwable throwable2) {
                throwable2.printStackTrace();
                this.playerRandomTeleports.remove(player.getUniqueId());
                Thread.currentThread().interrupt();
              }

              if (isValid) {
                this.playerRandomTeleports.remove(player.getUniqueId());
                return isValid;
              }
            }
          }

          this.playerRandomTeleports.remove(player.getUniqueId());
          return false;
        });
  }

  private static CompletableFuture<Boolean> teleportPlayerRandom(
      Player player,
      TeleportCause teleportCause,
      CompletableFuture<CMITeleportLocation> completableFuture) {
    return completableFuture.thenComposeAsync(
        cmiTeleportLocation -> {
          if (cmiTeleportLocation.getPosition() == null) {
            return CompletableFuture.completedFuture(false);
          }

          cmiTeleportLocation.setType(CMITeleportType.randomTp);
          return CMI.getInstance()
              .getTeleportHandler()
              .finalizeTeleportation(player, teleportCause, cmiTeleportLocation);
        });
  }
}
