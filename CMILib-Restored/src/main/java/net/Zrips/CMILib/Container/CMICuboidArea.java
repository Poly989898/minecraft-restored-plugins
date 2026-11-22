package net.Zrips.CMILib.Container;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMICuboidArea {
  private Vector p1;
  private Vector p2;
  private Vector highPoints;
  private Vector lowPoints;
  private World world;
  private static final int MIN_HEIGHT = 0;

  public CMICuboidArea(World world, Vector startLoc, Vector endLoc) {
    this.highPoints = startLoc.clone();
    this.p1 = startLoc.clone();
    this.lowPoints = endLoc.clone();
    this.p2 = endLoc.clone();
    this.world = world;
    this.recheck();
  }

  public CMICuboidArea(Location startLoc, Location endLoc) {
    this.highPoints = startLoc.toVector().clone();
    this.p1 = startLoc.toVector().clone();
    this.lowPoints = endLoc.toVector().clone();
    this.p2 = endLoc.toVector().clone();
    this.world = startLoc.getWorld();
    this.recheck();
  }

  public CMICuboidArea clone() {
    CMICuboidArea clone = new CMICuboidArea(this.world);
    clone.highPoints = this.highPoints == null ? null : this.highPoints.clone();
    clone.lowPoints = this.lowPoints == null ? null : this.lowPoints.clone();
    clone.p1 = this.p1 == null ? null : this.p1.clone();
    clone.p2 = this.p2 == null ? null : this.p2.clone();
    return clone;
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

  public CMICuboidArea(World world) {
    this.world = world;
  }

  public boolean isAreaWithinArea(CMICuboidArea area) {
    return this.containsLoc(area.highPoints) && this.containsLoc(area.lowPoints);
  }

  public Location getOutsideFreeLoc() {
    if (!this.valid()) {
      return null;
    }

    List<RandomLoc> randomLocList = new ArrayList<>();

    for (int z = -1; z < this.getZSize() + 1; z++) {
      randomLocList.add(
          new RandomLoc(this.getLowPoint().getX(), 0.0, this.getLowPoint().getZ() + z));
      randomLocList.add(
          new RandomLoc(
              this.getLowPoint().getX() + this.getXSize(), 0.0, this.getLowPoint().getZ() + z));
    }

    for (int x = -1; x < this.getXSize() + 1; x++) {
      randomLocList.add(
          new RandomLoc(this.getLowPoint().getX() + x, 0.0, this.getLowPoint().getZ()));
      randomLocList.add(
          new RandomLoc(
              this.getLowPoint().getX() + x, 0.0, this.getLowPoint().getZ() + this.getZSize()));
    }

    Location loc = this.getMiddlePoint().toLocation(this.getWorld()).clone();
    boolean found = false;
    int it = 0;
    int maxIt = 30;

    while (!found && it < maxIt) {
      it++;
      Random ran = new Random(System.currentTimeMillis());
      if (randomLocList.isEmpty()) {
        break;
      }

      int check = ran.nextInt(randomLocList.size());
      RandomLoc place = randomLocList.get(check);
      randomLocList.remove(check);
      double x = place.getX();
      double z = place.getZ();
      loc.setX(x);
      loc.setZ(z);
      loc.setY(this.getHighPoint().getBlockY());
      int max = this.getHighPoint().getBlockY();
      max = this.getWorld().getEnvironment() == Environment.NETHER ? 100 : max;

      for (int i = max; i > this.getLowPoint().getY(); i--) {
        loc.setY(i);
        Block block = loc.getBlock();
        Block blockAbove = loc.clone().add(0.0, 1.0, 0.0).getBlock();
        Block blockBelow = loc.clone().add(0.0, -1.0, 0.0).getBlock();
        if (blockBelow.getType() != Material.AIR
            && block.getType() == Material.AIR
            && blockAbove.getType() == Material.AIR) {
          break;
        }
      }

      if (loc.getBlock().getType() == Material.AIR
          && loc.clone().add(0.0, -1.0, 0.0).getBlock().getState().getType() != Material.LAVA
          && !loc.clone()
              .add(0.0, -1.0, 0.0)
              .getBlock()
              .getState()
              .getType()
              .equals(CMIMaterial.LAVA.getMaterial())
          && loc.clone().add(0.0, -1.0, 0.0).getBlock().getState().getType() != Material.WATER
          && !loc.clone()
              .add(0.0, -1.0, 0.0)
              .getBlock()
              .getState()
              .getType()
              .equals(CMIMaterial.WATER.getMaterial())) {
        found = true;
        loc.setY(loc.getY() + 2.0);
        loc.add(0.5, 0.0, 0.5);
        break;
      }
    }

    return loc;
  }

  public boolean containsLoc(Location loc) {
    if (loc == null) {
      return false;
    } else if (loc.getWorld() == null) {
      return false;
    } else {
      return !loc.getWorld().equals(this.world) ? false : this.containsLoc(loc.toVector(), 0);
    }
  }

  public boolean containsLoc(Vector vector) {
    return this.containsLoc(vector, 0);
  }

  public boolean containsLoc(Location loc, int extraRange) {
    if (loc == null) {
      return false;
    } else {
      return !loc.getWorld().equals(this.world)
          ? false
          : this.containsLoc(loc.toVector(), extraRange);
    }
  }

  public boolean containsLoc(Vector vector, int extraRange) {
    if (vector == null) {
      return false;
    } else if (this.lowPoints.getBlockX() - extraRange > vector.getBlockX()) {
      return false;
    } else if (this.highPoints.getBlockX() + extraRange < vector.getBlockX()) {
      return false;
    } else if (this.lowPoints.getBlockZ() - extraRange > vector.getBlockZ()) {
      return false;
    } else if (this.highPoints.getBlockZ() + extraRange < vector.getBlockZ()) {
      return false;
    } else {
      return this.lowPoints.getBlockY() - extraRange > vector.getBlockY()
          ? false
          : this.highPoints.getBlockY() + extraRange >= vector.getBlockY();
    }
  }

  public boolean checkCollision(CMICuboidArea area) {
    if (area.getWorld() != null && this.getWorld() != null) {
      if (!area.getWorld().equals(this.getWorld())) {
        return false;
      } else if (!this.valid()) {
        return false;
      } else {
        return !area.containsLoc(this.lowPoints)
                && !area.containsLoc(this.highPoints)
                && !this.containsLoc(area.highPoints)
                && !this.containsLoc(area.lowPoints)
            ? advCuboidCheckCollision(
                this.highPoints, this.lowPoints, area.highPoints, area.lowPoints)
            : true;
      }
    } else {
      return false;
    }
  }

  private static boolean advCuboidCheckCollision(
      Vector A1High, Vector A1Low, Vector A2High, Vector A2Low) {
    int A1HX = A1High.getBlockX();
    int A1LX = A1Low.getBlockX();
    int A1HY = A1High.getBlockY();
    int A1LY = A1Low.getBlockY();
    int A1HZ = A1High.getBlockZ();
    int A1LZ = A1Low.getBlockZ();
    int A2HX = A2High.getBlockX();
    int A2LX = A2Low.getBlockX();
    int A2HY = A2High.getBlockY();
    int A2LY = A2Low.getBlockY();
    int A2HZ = A2High.getBlockZ();
    int A2LZ = A2Low.getBlockZ();
    return (A1HX >= A2LX && A1HX <= A2HX
            || A1LX >= A2LX && A1LX <= A2HX
            || A2HX >= A1LX && A2HX <= A1HX
            || A2LX >= A1LX && A2LX <= A1HX)
        && (A1HY >= A2LY && A1HY <= A2HY
            || A1LY >= A2LY && A1LY <= A2HY
            || A2HY >= A1LY && A2HY <= A1HY
            || A2LY >= A1LY && A2LY <= A1HY)
        && (A1HZ >= A2LZ && A1HZ <= A2HZ
            || A1LZ >= A2LZ && A1LZ <= A2HZ
            || A2HZ >= A1LZ && A2HZ <= A1HZ
            || A2LZ >= A1LZ && A2LZ <= A1HZ);
  }

  public long getSize() {
    if (!this.valid()) {
      return 0L;
    }

    int xsize = this.highPoints.getBlockX() - this.lowPoints.getBlockX() + 1;
    int zsize = this.highPoints.getBlockZ() - this.lowPoints.getBlockZ() + 1;
    int ysize = this.highPoints.getBlockY() - this.lowPoints.getBlockY() + 1;
    return xsize * ysize * zsize;
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

  public Location getHighLocation() {
    return !this.valid() ? null : this.highPoints.toLocation(this.getWorld());
  }

  public Vector getHighPoint() {
    return this.highPoints;
  }

  public Location getLowLocation() {
    return !this.valid() ? null : this.lowPoints.toLocation(this.getWorld());
  }

  public Vector getLowPoint() {
    return this.lowPoints;
  }

  public World getWorld() {
    return this.world;
  }

  public List<CMIChunkReference> getChunks() {
    return this.getChunks(0);
  }

  public List<CMIChunkReference> getChunks(int range) {
    List<CMIChunkReference> chunks = new ArrayList<>();
    Vector high = this.highPoints;
    Vector low = this.lowPoints;
    int lowX = CMIChunkReference.getChunkCoord(low.getBlockX() - range);
    int lowZ = CMIChunkReference.getChunkCoord(low.getBlockZ() - range);
    int highX = CMIChunkReference.getChunkCoord(high.getBlockX() + range);
    int highZ = CMIChunkReference.getChunkCoord(high.getBlockZ() + range);

    for (int x = lowX; x <= highX; x++) {
      for (int z = lowZ; z <= highZ; z++) {
        chunks.add(new CMIChunkReference(x, z));
      }
    }

    return chunks;
  }

  public void setArea(CMICuboidArea area) {
    this.highPoints = area.getHighPoint().clone();
    this.lowPoints = area.getLowPoint().clone();
    this.p1 = area.p1.clone();
    this.p2 = area.p2.clone();
    this.world = area.getWorld();
    this.recheck();
  }

  public void setHighLocation(Location highLocation) {
    if (highLocation != null) {
      this.world = highLocation.getWorld();
      this.setHighPoint(highLocation.toVector());
    }
  }

  public void setHighPoint(Vector highLocation) {
    this.p1 = highLocation;
    this.recheck();
  }

  public void setLowLocation(Location lowLocation) {
    if (lowLocation != null) {
      this.world = lowLocation.getWorld();
      this.setLowPoint(lowLocation.toVector());
    }
  }

  public void setLowPoint(Vector lowPoint) {
    this.p2 = lowPoint;
    this.recheck();
  }

  public void setWorld(World world) {
    this.world = world;
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

    StringBuilder sb = new StringBuilder();
    sb.append(this.lowPoints.getX())
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
    return sb.toString();
  }

  public boolean shift(Player player, double amount) {
    return this.shift(getDirection(player), amount);
  }

  public boolean shift(CMICuboidArea.Direction d, double amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case UP:
        double oldy = this.getHighPoint().getBlockY() + amount;
        if (oldy > this.getMaxWorldHeight()) {
          oldy = this.getMaxWorldHeight();
        }

        this.getHighPoint().setY(oldy);
        this.getLowPoint().setY(this.getLowPoint().getBlockY() + amount);
        break;
      case DOWN:
        double newLowY = this.getLowPoint().getBlockY() - amount;
        if (newLowY < 0.0) {
          newLowY = 0.0;
        }

        this.getLowPoint().setY(newLowY);
        this.getHighPoint().setY(this.getHighPoint().getBlockY() - amount);
        break;
      case PLUSX:
        this.getHighPoint().setX(this.getHighPoint().getBlockX() + amount);
        this.getLowPoint().setX(this.getLowPoint().getBlockX() + amount);
        break;
      case PLUSZ:
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() + amount);
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() + amount);
        break;
      case MINUSX:
        this.getLowPoint().setX(this.getLowPoint().getBlockX() - amount);
        this.getHighPoint().setX(this.getHighPoint().getBlockX() - amount);
        break;
      case MINUSZ:
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() - amount);
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() - amount);
    }

    return true;
  }

  public boolean expand(Player player, double amount) {
    return this.expand(getDirection(player), amount);
  }

  public boolean expand(CMICuboidArea.Direction d, double amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case UP:
        double newHighY = this.getHighPoint().getBlockY() + amount;
        if (newHighY > this.getMaxWorldHeight()) {
          newHighY = this.getMaxWorldHeight();
        }

        this.getHighPoint().setY(newHighY);
        break;
      case DOWN:
        double newLowY = this.getLowPoint().getBlockY() - amount;
        if (newLowY < 0.0) {
          newLowY = 0.0;
        }

        this.getLowPoint().setY(newLowY);
        break;
      case PLUSX:
        this.getHighPoint().setX(this.getHighPoint().getBlockX() + amount);
        break;
      case PLUSZ:
        this.getHighPoint().setZ(this.getHighPoint().getBlockZ() + amount);
        break;
      case MINUSX:
        this.getLowPoint().setX(this.getLowPoint().getBlockX() - amount);
        break;
      case MINUSZ:
        this.getLowPoint().setZ(this.getLowPoint().getBlockZ() - amount);
    }

    return true;
  }

  public boolean contract(Player player, double amount) {
    return !this.valid() ? false : this.contract(getDirection(player), amount);
  }

  public boolean contract(CMICuboidArea.Direction d, double amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case UP:
        double newLowY = this.getLowPoint().getBlockY() + amount;
        if (newLowY < 0.0) {
          newLowY = 0.0;
        }

        newLowY = this.getLowPoint().getBlockY() + amount;
        newLowY = this.getHighPoint().getY() < newLowY ? this.getHighPoint().getY() : newLowY;
        this.getLowPoint().setY(newLowY);
        break;
      case DOWN:
        double newHighY = this.getHighPoint().getBlockY() - amount;
        if (newHighY > this.getMaxWorldHeight()) {
          newHighY = this.getMaxWorldHeight();
        }

        newHighY = this.getHighPoint().getBlockY() - amount;
        newHighY = this.getLowPoint().getY() > newHighY ? this.getLowPoint().getY() : newHighY;
        this.getHighPoint().setY(newHighY);
        break;
      case PLUSX:
        {
          double res = this.getLowPoint().getBlockX() + amount;
          res = this.getHighPoint().getX() < res ? this.getHighPoint().getX() : res;
          this.getLowPoint().setX(res);
          break;
        }
      case PLUSZ:
        {
          double res = this.getLowPoint().getBlockZ() + amount;
          res = this.getHighPoint().getZ() < res ? this.getHighPoint().getZ() : res;
          this.getLowPoint().setZ(res);
          break;
        }
      case MINUSX:
        {
          double res = this.getHighPoint().getBlockX() - amount;
          res = this.getLowPoint().getX() > res ? this.getLowPoint().getX() : res;
          this.getHighPoint().setX(res);
          break;
        }
      case MINUSZ:
        {
          double res = this.getHighPoint().getBlockZ() - amount;
          res = this.getLowPoint().getZ() > res ? this.getLowPoint().getZ() : res;
          this.getHighPoint().setZ(res);
        }
    }

    return true;
  }

  private int getMaxWorldHeight() {
    if (this.getWorld() == null) {
      return 256;
    }

    switch (this.getWorld().getEnvironment()) {
      case NETHER:
        return 128;
      case NORMAL:
      case THE_END:
        return 256;
      default:
        return 256;
    }
  }

  private static CMICuboidArea.Direction getDirection(Player player) {
    int yaw = (int) player.getLocation().getYaw();
    if (yaw < 0) {
      yaw += 360;
    }

    yaw += 45;
    yaw %= 360;
    int facing = yaw / 90;
    float pitch = player.getLocation().getPitch();
    if (pitch < -50.0F) {
      return CMICuboidArea.Direction.UP;
    } else if (pitch > 50.0F) {
      return CMICuboidArea.Direction.DOWN;
    } else if (facing == 1) {
      return CMICuboidArea.Direction.MINUSX;
    } else if (facing == 3) {
      return CMICuboidArea.Direction.PLUSX;
    } else if (facing == 2) {
      return CMICuboidArea.Direction.MINUSZ;
    } else {
      return facing == 0 ? CMICuboidArea.Direction.PLUSZ : null;
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
