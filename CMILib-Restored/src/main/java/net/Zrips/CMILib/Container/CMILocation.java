package net.Zrips.CMILib.Container;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

public class CMILocation extends Location {
  private static final String separator = ";";
  private String worldName;
  private static MethodHandle getBlockTypeId = null;
  private static MethodHandle getBlockData = null;

  public CMILocation(World world, double x, double y, double z, float yaw, float pitch) {
    super(world, x, y, z, yaw, pitch);
    this.worldName = world.getName();
  }

  public CMILocation(World world, double x, double y, double z) {
    super(world, x, y, z);
    if (world != null) {
      this.worldName = world.getName();
    }
  }

  public CMILocation(Location loc) {
    super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    if (loc.getWorld() != null) {
      this.worldName = loc.getWorld().getName();
    }
  }

  public CMILocation(String world, double x, double y, double z, float yaw, float pitch) {
    super(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    this.worldName = world;
  }

  public CMILocation(String world, double x, double y, double z) {
    super(Bukkit.getWorld(world), x, y, z);
    this.worldName = world;
  }

  public void recheck() {
    this.updateWorld();
  }

  private void updateWorld() {
    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1)
          && super.getWorld() != null
          && !super.isWorldLoaded()) {
        if (this.worldName != null) {
          this.setWorld(null);
        }

        return;
      }
    } catch (Throwable var3) {
    }

    try {
      if (super.getWorld() == null && this.worldName != null) {
        World w = null;
        if (this.worldName.length() == 36) {
          w = Bukkit.getWorld(UUID.fromString(this.worldName));
        } else {
          w = Bukkit.getWorld(this.worldName);
        }

        if (w != null) {
          super.setWorld(w);
          this.worldName = super.getWorld().getName();
        }
      }
    } catch (Exception var2) {
    }
  }

  public String getWorldName() {
    return this.worldName != null
        ? this.worldName
        : (this.getWorld() == null ? null : this.getWorld().getName());
  }

  public Location getBukkitLoc() {
    this.updateWorld();
    return this;
  }

  public boolean isValid() {
    this.updateWorld();
    return this.getWorld() != null
        && this.getWorld().getName() != null
        && Bukkit.getWorld(this.getWorld().getUID()) != null;
  }

  public boolean isWorldNull() {
    this.updateWorld();
    return this.getWorld() != null
        && this.getWorld().getName() != null
        && Bukkit.getWorld(this.getWorld().getUID()) != null;
  }

  public World getWorld() {
    this.updateWorld();

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !super.isWorldLoaded()) {
        return null;
      }
    } catch (Throwable var2) {
    }

    return super.getWorld();
  }

  public Chunk getChunk() {
    this.updateWorld();
    return super.getChunk();
  }

  public Block getBlock() {
    this.updateWorld();
    return super.getWorld() == null ? null : super.getBlock();
  }

  public Location add(Location vec) {
    this.updateWorld();
    return super.add(vec);
  }

  public Location subtract(Location vec) {
    this.updateWorld();
    return super.subtract(vec);
  }

  public double distanceSquared(Location o) {
    if (o == null) {
      return 2.147483647E9;
    } else {
      return !this.isValid() ? 2.147483647E9 : super.distanceSquared(o);
    }
  }

  public double distance(Location o) {
    if (o == null) {
      return 2.147483647E9;
    } else {
      return !this.isValid() ? 2.147483647E9 : super.distance(o);
    }
  }

  public boolean equals(Object obj) {
    this.updateWorld();
    return super.equals(obj);
  }

  public int hashCode() {
    this.updateWorld();
    return super.hashCode();
  }

  public String toString() {
    this.updateWorld();
    return super.toString();
  }

  public CMILocation clone() {
    this.updateWorld();
    return new CMILocation(super.clone());
  }

  public Map<String, Object> serialize() {
    this.updateWorld();
    return super.serialize();
  }

  public int getHighestBlockYAt() {
    if (this.getWorld() == null) {
      return 63;
    }

    ChunkSnapshot chunk =
        this.getWorld()
            .getEmptyChunkSnapshot(this.getBlockX() >> 4, this.getBlockZ() >> 4, true, true);
    int x = this.getBlockX() % 16;
    x = x < 0 ? 16 + x : x;
    int z = this.getBlockZ() % 16;
    z = z < 0 ? 16 + z : z;
    return chunk.getHighestBlockYAt(x, z);
  }

  public Material getBlockType() {
    Location loc = this.getBukkitLoc();
    if (loc == null) {
      return Material.AIR;
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !super.isWorldLoaded()) {
        return Material.AIR;
      }
    } catch (Throwable var3) {
    }

    return this.getBlock().getType();
  }

  public CMIMaterial getBlockCMIType() {
    Location loc = this.getBukkitLoc();
    if (loc == null) {
      return CMIMaterial.AIR;
    }

    try {
      if (Version.isCurrentEqualOrHigher(Version.v1_16_R1) && !super.isWorldLoaded()) {
        return CMIMaterial.AIR;
      }
    } catch (Throwable var3) {
    }

    CMIMaterial mat = CMIMaterial.get(this.getBlock());
    return mat == CMIMaterial.NONE ? CMIMaterial.AIR : mat;
  }

  public static Material getBlockTypeSafe(Location loc) {
    if (loc == null) {
      return Material.AIR;
    }

    int x = loc.getBlockX();
    int z = loc.getBlockZ();
    int y = loc.getBlockY();
    int cx = Math.abs(loc.getBlockX() % 16);
    int cz = Math.abs(loc.getBlockZ() % 16);
    World world = loc.getWorld();
    ChunkSnapshot chunkSnapshot = null;
    if (!world.getBlockAt(x, 0, z).getChunk().isLoaded()) {
      world.getBlockAt(x, 0, z).getChunk().load();
      chunkSnapshot = world.getBlockAt(x, 0, z).getChunk().getChunkSnapshot(false, false, false);
      world.getBlockAt(x, 0, z).getChunk().unload();
    } else {
      chunkSnapshot = world.getBlockAt(x, 0, z).getChunk().getChunkSnapshot();
    }

    if (chunkSnapshot == null) {
      return Material.AIR;
    }

    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      BlockData type = chunkSnapshot.getBlockData(cx, y, cz);
      return type.getMaterial();
    }

    if (getBlockTypeId == null) {
      Lookup lookup = MethodHandles.lookup();

      try {
        getBlockTypeId =
            lookup
                .findVirtual(
                    ChunkSnapshot.class,
                    "getBlockTypeId",
                    MethodType.methodType(int.class, int.class, int.class))
                .asType(MethodType.methodType(int.class, ChunkSnapshot.class));
        getBlockData =
            lookup
                .findVirtual(
                    ChunkSnapshot.class,
                    "getBlockData",
                    MethodType.methodType(int.class, int.class, int.class))
                .asType(MethodType.methodType(int.class, ChunkSnapshot.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    if (getBlockTypeId != null && getBlockData != null) {
      try {
        int type = (int) getBlockTypeId.invokeExact((ChunkSnapshot) chunkSnapshot);
        if (type == 0) {
          return Material.AIR;
        }

        int data = (int) getBlockData.invokeExact((ChunkSnapshot) chunkSnapshot);
        return CMIMaterial.get(type, data).getMaterial();
      } catch (Throwable e) {
        e.printStackTrace();
        return Material.AIR;
      }
    } else {
      return Material.AIR;
    }
  }

  public static CMILocation fromString(String map) {
    return fromString(map, map.contains(";") ? ";" : ":");
  }

  public static CMILocation fromString(String map, String separator) {
    CMILocation loc = null;
    if (map == null) {
      return null;
    }

    if (!map.contains(separator)) {
      return null;
    }

    String[] split = map.replace(",", ".").split(separator);
    Double x = null;
    Double y = null;
    Double z = null;
    Float yaw = null;
    Float pitch = null;
    World world = null;
    String worldName = null;

    for (String one : split) {
      try {
        if (x == null) {
          x = Double.parseDouble(one);
          continue;
        }

        if (y == null) {
          y = Double.parseDouble(one);
          continue;
        }

        if (z == null) {
          z = Double.parseDouble(one);
          continue;
        }

        if (yaw == null) {
          yaw = Float.parseFloat(one);
          continue;
        }

        if (pitch == null) {
          pitch = Float.parseFloat(one);
          continue;
        }
      } catch (Exception var16) {
      }

      worldName = one;
      if (world == null) {
        world = CMIWorld.getWorld(one);
      }
    }

    worldName = world == null ? worldName : world.getName();
    if (worldName == null) {
      return null;
    }

    loc = new CMILocation(worldName, x == null ? 0.0 : x, y == null ? 0.0 : y, z == null ? 0.0 : z);
    loc.setYaw(yaw == null ? 0.0F : yaw);
    loc.setPitch(pitch == null ? 0.0F : pitch);
    return loc;
  }

  public static List<CMILocation> fromString(List<String> map) {
    return fromString(map, ";");
  }

  public static List<CMILocation> fromString(List<String> maps, String separator) {
    List<CMILocation> ls = new ArrayList<>();

    for (String map : maps) {
      ls.add(fromString(map, separator));
    }

    return ls;
  }

  public static String toString(Location loc) {
    return toString(loc, ";", false, false);
  }

  public static String toString(Location loc, boolean noPitchYaw) {
    return toString(loc, ";", noPitchYaw, false);
  }

  public static String toString(Location loc, String separator) {
    return toString(loc, separator, false, false);
  }

  public static String toString(Location loc, String separator, boolean noPitchYaw) {
    return toString(loc, separator, noPitchYaw, false);
  }

  public static String toString(
      Location loc, String separator, boolean noPitchYaw, boolean integers) {
    return new CMILocation(loc).toString(separator, noPitchYaw, integers);
  }

  public static String toString(CMILocation loc) {
    return toString(loc, ";", false, false);
  }

  public static String toString(CMILocation loc, boolean noPitchYaw) {
    return toString(loc, ";", noPitchYaw, false);
  }

  public static String toString(CMILocation loc, String separator) {
    return toString(loc, separator, false, false);
  }

  public static String toString(CMILocation loc, String separator, boolean noPitchYaw) {
    return toString(loc, separator, noPitchYaw, false);
  }

  public static String toString(
      CMILocation loc, String separator, boolean noPitchYaw, boolean integers) {
    return loc.toString(separator, noPitchYaw, integers);
  }

  public String toString(String separator) {
    return this.toString(separator, false, false);
  }

  public String toString(boolean noPitchYaw) {
    return this.toString(";", noPitchYaw, false);
  }

  public String toString(String separator, boolean noPitchYaw) {
    return this.toString(separator, noPitchYaw, false);
  }

  public String toString(String separator, boolean noPitchYaw, boolean integers) {
    StringBuilder map = new StringBuilder();

    try {
      if (this.getWorldName() != null) {
        map.append(this.getWorldName());
        if (integers) {
          map.append(separator + this.getBlockX());
          map.append(separator + this.getBlockY());
          map.append(separator + this.getBlockZ());
        } else {
          map.append(separator + CMINumber.formatNumber(this.getX()));
          map.append(separator + CMINumber.formatNumber(this.getY()));
          map.append(separator + CMINumber.formatNumber(this.getZ()));
        }

        if (!noPitchYaw) {
          if (integers) {
            map.append(separator + (long) this.getYaw());
            map.append(separator + (long) this.getPitch());
          } else {
            map.append(separator + CMINumber.formatNumber(this.getYaw()));
            map.append(separator + CMINumber.formatNumber(this.getPitch()));
          }
        }
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return map.toString().replace(",", ".");
  }

  public static List<String> toString(List<CMILocation> loc) {
    return toString(loc, ";");
  }

  public static List<String> toString(List<CMILocation> locations, String separator) {
    List<String> ls = new ArrayList<>();

    for (Location loc : locations) {
      ls.add(toString(loc, separator));
    }

    return ls;
  }

  public static World getWorld(String name) {
    return CMIWorld.getWorld(name);
  }

  public static int getMinHeight(World world) {
    return Version.isCurrentEqualOrLower(Version.v1_16_R3) ? 0 : world.getMinHeight();
  }

  public static double getDistance(Location firstLoc, Location secondLoc) {
    if (firstLoc != null && secondLoc != null && firstLoc.getWorld() == secondLoc.getWorld()) {
      try {
        return firstLoc.distance(secondLoc);
      } catch (Throwable e) {
        return 2.147483647E9;
      }
    } else {
      return 2.147483647E9;
    }
  }
}
