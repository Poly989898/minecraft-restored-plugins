package com.Zrips.CMI.Modules.Portals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CuboidArea {
  private Vector p1;
  private Vector p2;
  private Vector highPoints;
  private Vector lowPoints;
  private World world;
  private String worldName = null;
  private static final int MIN_HEIGHT = 0;

  public CuboidArea(World worldRef, CMIVectorInt3D cmiVectorInt3D, CMIVectorInt3D maxCorner) {
    this.highPoints =
        new Vector(cmiVectorInt3D.getX(), cmiVectorInt3D.getY(), cmiVectorInt3D.getZ());
    this.p1 = new Vector(cmiVectorInt3D.getX(), cmiVectorInt3D.getY(), cmiVectorInt3D.getZ());
    this.lowPoints = new Vector(maxCorner.getX(), maxCorner.getY(), maxCorner.getZ());
    this.p2 = new Vector(maxCorner.getX(), maxCorner.getY(), maxCorner.getZ());
    this.world = worldRef;
    this.recheck();
  }

  public CuboidArea(World worldRef, Vector vector, Vector maxCorner) {
    if (vector != null) {
      this.highPoints = vector.clone();
      this.p1 = vector.clone();
    }

    if (maxCorner != null) {
      this.lowPoints = maxCorner.clone();
      this.p2 = maxCorner.clone();
    }

    this.world = worldRef;
    this.recheck();
  }

  public CuboidArea(CMILocation cmiLocation, CMILocation maxCorner) {
    this.highPoints = cmiLocation.toVector().clone();
    this.p1 = cmiLocation.toVector().clone();
    this.lowPoints = maxCorner.toVector().clone();
    this.p2 = maxCorner.toVector().clone();
    this.world = cmiLocation.getWorld();
    this.worldName =
        cmiLocation.getWorldName() == null ? maxCorner.getWorldName() : cmiLocation.getWorldName();
    this.recheck();
  }

  public CuboidArea(Location location, Location maxCorner) {
    this.highPoints = location.toVector().clone();
    this.p1 = location.toVector().clone();
    this.lowPoints = maxCorner.toVector().clone();
    this.p2 = maxCorner.toVector().clone();
    this.world = location.getWorld();
    this.recheck();
  }

  public CuboidArea clone() {
    CuboidArea cuboidArea = new CuboidArea(this.world);
    cuboidArea.highPoints = this.highPoints == null ? null : this.highPoints.clone();
    cuboidArea.lowPoints = this.lowPoints == null ? null : this.lowPoints.clone();
    cuboidArea.p1 = this.p1 == null ? null : this.p1.clone();
    cuboidArea.p2 = this.p2 == null ? null : this.p2.clone();
    return cuboidArea;
  }

  private void recheck() {
    if (this.p1 != null && this.p2 != null) {
      this.highPoints =
          new Vector(
              Math.max(this.p1.getBlockX(), this.p2.getBlockX()),
              Math.max(this.p1.getBlockY(), this.p2.getBlockY()),
              Math.max(this.p1.getBlockZ(), this.p2.getBlockZ()));
      this.lowPoints =
          new Vector(
              Math.min(this.p1.getBlockX(), this.p2.getBlockX()),
              Math.min(this.p1.getBlockY(), this.p2.getBlockY()),
              Math.min(this.p1.getBlockZ(), this.p2.getBlockZ()));
    }
  }

  public boolean valid() {
    return this.p1 != null && this.p2 != null;
  }

  public CuboidArea(World worldRef) {
    this.world = worldRef;
  }

  public boolean isAreaWithinArea(CuboidArea cuboidArea) {
    return this.containsLoc(cuboidArea.highPoints) && this.containsLoc(cuboidArea.lowPoints);
  }

  public Location getOutsideFreeLoc() {
    if (!this.valid()) {
      return null;
    }

    ArrayList<RandomLoc> arrayList = new ArrayList();

    for (int value = -1; value < this.getZSize() + 1; value++) {
      arrayList.add(
          new RandomLoc(this.getLowPoint().getX(), 0.0, this.getLowPoint().getZ() + value));
      arrayList.add(
          new RandomLoc(
              this.getLowPoint().getX() + this.getXSize(), 0.0, this.getLowPoint().getZ() + value));
    }

    for (int x = -1; x < this.getXSize() + 1; x++) {
      arrayList.add(new RandomLoc(this.getLowPoint().getX() + x, 0.0, this.getLowPoint().getZ()));
      arrayList.add(
          new RandomLoc(
              this.getLowPoint().getX() + x, 0.0, this.getLowPoint().getZ() + this.getZSize()));
    }

    Location location = this.getMiddlePoint().toLocation(this.getWorld()).clone();
    boolean flag = false;
    int attempts = 0;
    byte b = 30;

    while (!flag && attempts < b) {
      attempts++;
      Random random = new Random(System.currentTimeMillis());
      if (arrayList.isEmpty()) {
        break;
      }

      int randomIndex = random.nextInt(arrayList.size());
      RandomLoc randomLoc = (RandomLoc) arrayList.get(randomIndex);
      arrayList.remove(randomIndex);
      double number = randomLoc.getX();
      double randomZ = randomLoc.getZ();
      location.setX(number);
      location.setZ(randomZ);
      location.setY(this.getHighPoint().getBlockY());
      int highY = this.getHighPoint().getBlockY();
      highY = this.getWorld().getEnvironment() == Environment.NETHER ? 100 : highY;

      for (int y = highY; y > this.getLowPoint().getY(); y--) {
        location.setY(y);
        Block block = location.getBlock();
        Block above = location.clone().add(0.0, 1.0, 0.0).getBlock();
        Block below = location.clone().add(0.0, -1.0, 0.0).getBlock();
        if (below.getType() != Material.AIR
            && block.getType() == Material.AIR
            && above.getType() == Material.AIR) {
          break;
        }
      }

      if (location.getBlock().getType() == Material.AIR
          && location.clone().add(0.0, -1.0, 0.0).getBlock().getState().getType() != Material.LAVA
          && !location
              .clone()
              .add(0.0, -1.0, 0.0)
              .getBlock()
              .getState()
              .getType()
              .equals(CMIMaterial.LAVA.getMaterial())
          && location.clone().add(0.0, -1.0, 0.0).getBlock().getState().getType() != Material.WATER
          && !location
              .clone()
              .add(0.0, -1.0, 0.0)
              .getBlock()
              .getState()
              .getType()
              .equals(CMIMaterial.WATER.getMaterial())) {
        flag = true;
        location.setY(location.getY() + 2.0);
        location.add(0.5, 0.0, 0.5);
        break;
      }
    }

    return location;
  }

  public boolean containsLoc(Location location) {
    if (location == null) {
      return false;
    } else if (location.getWorld() == null) {
      return false;
    } else {
      return !location.getWorld().equals(this.getWorld())
          ? false
          : this.containsLoc(location.toVector(), 0);
    }
  }

  public boolean containsLoc(Vector vector) {
    return this.containsLoc(vector, 0);
  }

  public boolean containsLoc(Location location, int value) {
    if (location == null) {
      return false;
    } else {
      return !location.getWorld().equals(this.getWorld())
          ? false
          : this.containsLoc(location.toVector(), value);
    }
  }

  public boolean containsLoc(Vector vector, int value) {
    if (vector == null) {
      return false;
    } else if (this.lowPoints.getBlockX() - value > vector.getBlockX()) {
      return false;
    } else if (this.highPoints.getBlockX() + value < vector.getBlockX()) {
      return false;
    } else if (this.lowPoints.getBlockZ() - value > vector.getBlockZ()) {
      return false;
    } else if (this.highPoints.getBlockZ() + value < vector.getBlockZ()) {
      return false;
    } else {
      return this.lowPoints.getBlockY() - value > vector.getBlockY()
          ? false
          : this.highPoints.getBlockY() + value >= vector.getBlockY();
    }
  }

  public boolean checkCollision(CuboidArea cuboidArea) {
    if (cuboidArea.getWorld() != null && this.getWorld() != null) {
      if (!cuboidArea.getWorld().equals(this.getWorld())) {
        return false;
      } else if (!this.valid()) {
        return false;
      } else {
        return !cuboidArea.containsLoc(this.lowPoints)
                && !cuboidArea.containsLoc(this.highPoints)
                && !this.containsLoc(cuboidArea.highPoints)
                && !this.containsLoc(cuboidArea.lowPoints)
            ? advCuboidCheckCollision(
                this.highPoints, this.lowPoints, cuboidArea.highPoints, cuboidArea.lowPoints)
            : true;
      }
    } else {
      return false;
    }
  }

  private static boolean advCuboidCheckCollision(
      Vector vector, Vector secondPoint, Vector thirdPoint, Vector fourthPoint) {
    int value = vector.getBlockX();
    int pointBX = secondPoint.getBlockX();
    int pointAY = vector.getBlockY();
    int pointBY = secondPoint.getBlockY();
    int pointAZ = vector.getBlockZ();
    int pointBZ = secondPoint.getBlockZ();
    int pointCX = thirdPoint.getBlockX();
    int pointDX = fourthPoint.getBlockX();
    int pointCY = thirdPoint.getBlockY();
    int pointDY = fourthPoint.getBlockY();
    int pointCZ = thirdPoint.getBlockZ();
    int pointDZ = fourthPoint.getBlockZ();
    return (value >= pointDX && value <= pointCX
            || pointBX >= pointDX && pointBX <= pointCX
            || pointCX >= pointBX && pointCX <= value
            || pointDX >= pointBX && pointDX <= value)
        && (pointAY >= pointDY && pointAY <= pointCY
            || pointBY >= pointDY && pointBY <= pointCY
            || pointCY >= pointBY && pointCY <= pointAY
            || pointDY >= pointBY && pointDY <= pointAY)
        && (pointAZ >= pointDZ && pointAZ <= pointCZ
            || pointBZ >= pointDZ && pointBZ <= pointCZ
            || pointCZ >= pointBZ && pointCZ <= pointAZ
            || pointDZ >= pointBZ && pointDZ <= pointAZ);
  }

  public long getSize() {
    if (!this.valid()) {
      return 0L;
    }

    int value = this.highPoints.getBlockX() - this.lowPoints.getBlockX() + 1;
    int sizeZ = this.highPoints.getBlockZ() - this.lowPoints.getBlockZ() + 1;
    int sizeY = this.highPoints.getBlockY() - this.lowPoints.getBlockY() + 1;
    return value * sizeY * sizeZ;
  }

  public int getXSize() {
    return !this.valid() ? 0 : this.highPoints.getBlockX() - this.lowPoints.getBlockX() + 1;
  }

  public int getYSize() {
    return !this.valid() ? 0 : this.highPoints.getBlockY() - this.lowPoints.getBlockY() + 1;
  }

  public int getZSize() {
    return !this.valid() ? 0 : this.highPoints.getBlockZ() - this.lowPoints.getBlockZ() + 1;
  }

  public Location getHighLoc() {
    return !this.valid() ? null : this.highPoints.toLocation(this.getWorld());
  }

  public Vector getHighPoint() {
    return this.highPoints;
  }

  public Location getLowLoc() {
    return !this.valid() ? null : this.lowPoints.toLocation(this.getWorld());
  }

  public Vector getLowPoint() {
    return this.lowPoints;
  }

  public World getWorld() {
    if (this.world == null && this.worldName != null) {
      this.world = CMIWorld.getWorld(this.worldName);
    }

    return this.world;
  }

  public List<CuboidArea.ChunkRef> getChunks() {
    return this.getChunks(0);
  }

  public List<CuboidArea.ChunkRef> getChunks(int value) {
    ArrayList<com.Zrips.CMI.Modules.Portals.CuboidArea.ChunkRef> arrayList = new ArrayList();
    Vector vector = this.highPoints;
    Vector low = this.lowPoints;
    int minChunkX = CuboidArea.ChunkRef.getChunkCoord(low.getBlockX() - value);
    int minChunkZ = CuboidArea.ChunkRef.getChunkCoord(low.getBlockZ() - value);
    int maxChunkX = CuboidArea.ChunkRef.getChunkCoord(vector.getBlockX() + value);
    int maxChunkZ = CuboidArea.ChunkRef.getChunkCoord(vector.getBlockZ() + value);

    for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
      for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
        arrayList.add(new CuboidArea.ChunkRef(chunkX, chunkZ));
      }
    }

    return arrayList;
  }

  public void setArea(CuboidArea cuboidArea) {
    this.highPoints = cuboidArea.getHighPoint().clone();
    this.lowPoints = cuboidArea.getLowPoint().clone();
    this.p1 = cuboidArea.p1.clone();
    this.p2 = cuboidArea.p2.clone();
    this.world = cuboidArea.getWorld();
    this.recheck();
  }

  public void setHighLocation(Location location) {
    if (location != null) {
      this.world = location.getWorld();
      this.setHighPoint(location.toVector());
    }
  }

  public void setHighPoint(Vector vector) {
    this.p1 = vector;
    this.recheck();
  }

  public void setLowLocation(Location location) {
    if (location != null) {
      this.world = location.getWorld();
      this.setLowPoint(location.toVector());
    }
  }

  public void setLowPoint(Vector vector) {
    this.p2 = vector;
    this.recheck();
  }

  public void setWorld(World newWorld) {
    this.world = newWorld;
  }

  public void setLocation(Location location) {
    if (location != null) {
      this.world = location.getWorld();
      this.setPoint(location.toVector());
    }
  }

  public void setPoint(Vector vector) {
    if (vector != null) {
      if (this.p1 == null) {
        this.p1 = vector;
      } else {
        this.p2 = vector;
      }

      this.recheck();
    }
  }

  @Deprecated
  public Location getMiddleLocation() {
    return !this.valid()
        ? null
        : new Location(
            this.world,
            (this.getLowPoint().getX() + this.getHighPoint().getX()) / 2.0,
            (this.getLowPoint().getY() + this.getHighPoint().getY()) / 2.0,
            (this.getLowPoint().getZ() + this.getHighPoint().getZ()) / 2.0);
  }

  public Vector getMiddlePoint() {
    return !this.valid()
        ? null
        : new Vector(
            (this.getLowPoint().getX() + this.getHighPoint().getX()) / 2.0,
            (this.getLowPoint().getY() + this.getHighPoint().getY()) / 2.0,
            (this.getLowPoint().getZ() + this.getHighPoint().getZ()) / 2.0);
  }

  @Override
  public String toString() {
    if (!this.valid()) {
      return null;
    }

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append(this.lowPoints.getX())
        .append(":")
        .append(this.lowPoints.getY())
        .append(":")
        .append(this.lowPoints.getZ())
        .append(":")
        .append(this.highPoints.getX())
        .append(":")
        .append(this.highPoints.getY())
        .append(":")
        .append(this.highPoints.getZ());
    return stringBuilder.toString();
  }

  public boolean shift(Player player, double number) {
    return this.shift(getDirection(player), number);
  }

  public boolean shift(CuboidArea.Direction direction, double number) {
    if (!this.valid()) {
      return false;
    }

    if (direction == null) {
      return false;
    }

    switch (direction) {
      case UP:
        double highLimit = this.getHighPoint().getBlockY() + number;
        if (highLimit > this.getMaxWorldHeight()) {
          highLimit = this.getMaxWorldHeight();
        }

        this.getHighPoint().setY(highLimit);
        this.getLowPoint().setY(this.getLowPoint().getBlockY() + number);
        break;
      case DOWN:
        double lowLimit = this.getLowPoint().getBlockY() - number;
        if (lowLimit < 0.0) {
          lowLimit = 0.0;
        }

        this.getLowPoint().setY(lowLimit);
        this.getHighPoint().setY(this.getHighPoint().getBlockY() - number);
        break;
      case PLUSX:
        this.getHighPoint().setX(this.getHighPoint().getBlockX() + number);
        this.getLowPoint().setX(this.getLowPoint().getBlockX() + number);
        break;
      case PLUSZ:
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() + number);
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() + number);
        break;
      case MINUSX:
        this.getLowPoint().setX(this.getLowPoint().getBlockX() - number);
        this.getHighPoint().setX(this.getHighPoint().getBlockX() - number);
        break;
      case MINUSZ:
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() - number);
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() - number);
    }

    return true;
  }

  public boolean expand(Player player, double number) {
    return this.expand(getDirection(player), number);
  }

  public boolean expand(CuboidArea.Direction direction, double number) {
    if (!this.valid()) {
      return false;
    }

    if (direction == null) {
      return false;
    }

    switch (direction) {
      case UP:
        double highLimit = this.getHighPoint().getBlockY() + number;
        if (highLimit > this.getMaxWorldHeight()) {
          highLimit = this.getMaxWorldHeight();
        }

        this.getHighPoint().setY(highLimit);
        break;
      case DOWN:
        double lowLimit = this.getLowPoint().getBlockY() - number;
        if (lowLimit < 0.0) {
          lowLimit = 0.0;
        }

        this.getLowPoint().setY(lowLimit);
        break;
      case PLUSX:
        this.getHighPoint().setX(this.getHighPoint().getBlockX() + number);
        break;
      case PLUSZ:
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() + number);
        break;
      case MINUSX:
        this.getLowPoint().setX(this.getLowPoint().getBlockX() - number);
        break;
      case MINUSZ:
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() - number);
    }

    return true;
  }

  public boolean contract(Player player, double number) {
    return !this.valid() ? false : this.contract(getDirection(player), number);
  }

  public boolean contract(CuboidArea.Direction direction, double number) {
    if (!this.valid()) {
      return false;
    }

    if (direction == null) {
      return false;
    }

    switch (direction) {
      case UP:
        double lowYPlus = this.getLowPoint().getBlockY() + number;
        if (lowYPlus < 0.0) {
          lowYPlus = 0.0;
        }

        lowYPlus = this.getLowPoint().getBlockY() + number;
        lowYPlus = this.getHighPoint().getY() < lowYPlus ? this.getHighPoint().getY() : lowYPlus;
        this.getLowPoint().setY(lowYPlus);
        break;
      case DOWN:
        double highYMinus = this.getHighPoint().getBlockY() - number;
        if (highYMinus > this.getMaxWorldHeight()) {
          highYMinus = this.getMaxWorldHeight();
        }

        highYMinus = this.getHighPoint().getBlockY() - number;
        highYMinus =
            this.getLowPoint().getY() > highYMinus ? this.getLowPoint().getY() : highYMinus;
        this.getHighPoint().setY(highYMinus);
        break;
      case PLUSX:
        double lowXPlus = this.getLowPoint().getBlockX() + number;
        lowXPlus = this.getHighPoint().getX() < lowXPlus ? this.getHighPoint().getX() : lowXPlus;
        this.getLowPoint().setX(lowXPlus);
        break;
      case PLUSZ:
        double lowZPlus = this.getLowPoint().getBlockZ() + number;
        lowZPlus = this.getHighPoint().getZ() < lowZPlus ? this.getHighPoint().getZ() : lowZPlus;
        this.getLowPoint().setZ(lowZPlus);
        break;
      case MINUSX:
        double highXMinus = this.getHighPoint().getBlockX() - number;
        highXMinus =
            this.getLowPoint().getX() > highXMinus ? this.getLowPoint().getX() : highXMinus;
        this.getHighPoint().setX(highXMinus);
        break;
      case MINUSZ:
        double highZMinus = this.getHighPoint().getBlockZ() - number;
        highZMinus =
            this.getLowPoint().getZ() > highZMinus ? this.getLowPoint().getZ() : highZMinus;
        this.getHighPoint().setZ(highZMinus);
    }

    return true;
  }

  private int getMaxWorldHeight() {
    if (this.getWorld() == null) {
      return 256;
    }

    switch (this.getWorld().getEnvironment()) {
      case NORMAL:
      case THE_END:
        return 256;
      case NETHER:
        return 128;
      default:
        return 256;
    }
  }

  private static CuboidArea.Direction getDirection(Player player) {
    int value = (int) player.getLocation().getYaw();
    if (value < 0) {
      value += 360;
    }

    value += 45;
    value %= 360;
    int quarterTurns = value / 90;
    float floatValue = player.getLocation().getPitch();
    if (floatValue < -50.0F) {
      return CuboidArea.Direction.UP;
    } else if (floatValue > 50.0F) {
      return CuboidArea.Direction.DOWN;
    } else if (quarterTurns == 1) {
      return CuboidArea.Direction.MINUSX;
    } else if (quarterTurns == 3) {
      return CuboidArea.Direction.PLUSX;
    } else if (quarterTurns == 2) {
      return CuboidArea.Direction.MINUSZ;
    } else {
      return quarterTurns == 0 ? CuboidArea.Direction.PLUSZ : null;
    }
  }

  public String getWorldName() {
    return this.worldName != null
        ? this.worldName
        : (this.world == null ? null : this.world.getName());
  }

  public static final class ChunkRef {
    private final int z;
    private final int x;

    public static int getChunkCoord(int value) {
      return value >> 4;
    }

    public ChunkRef(Location location) {
      this.x = getChunkCoord(location.getBlockX());
      this.z = getChunkCoord(location.getBlockZ());
    }

    public ChunkRef(int value, int z) {
      this.x = value;
      this.z = z;
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }

      if (object == null) {
        return false;
      }

      if (this.getClass() != object.getClass()) {
        return false;
      }

      CuboidArea.ChunkRef chunkRef = (CuboidArea.ChunkRef) object;
      return this.x == chunkRef.x && this.z == chunkRef.z;
    }

    @Override
    public int hashCode() {
      return this.x ^ this.z;
    }

    @Override
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("{ x: ").append(this.x).append(", z: ").append(this.z).append(" }");
      return stringBuilder.toString();
    }
  }

  public enum Direction {
    UP,
    DOWN,
    PLUSX,
    PLUSZ,
    MINUSX,
    MINUSZ;
  }
}
