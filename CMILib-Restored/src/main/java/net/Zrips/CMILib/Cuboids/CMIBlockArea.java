package net.Zrips.CMILib.Cuboids;

import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.Container.CMIChunkReference;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class CMIBlockArea extends CMIArea {
  protected CMIVectorInt3D p1 = null;
  protected CMIVectorInt3D p2 = null;
  protected CMIVectorInt3D highPoint = null;
  protected CMIVectorInt3D lowPoint = null;

  public CMIBlockArea() {}

  public CMIBlockArea(Vector startLoc, Vector endLoc) {
    this(new CMIVectorInt3D(startLoc), new CMIVectorInt3D(endLoc));
  }

  public CMIBlockArea(Location startLoc, Location endLoc) {
    this(new CMIVectorInt3D(startLoc.toVector()), new CMIVectorInt3D(endLoc.toVector()));
  }

  public CMIBlockArea(CMIVectorInt3D startLoc, CMIVectorInt3D endLoc) {
    if (startLoc != null) {
      this.p1 = startLoc.clone();
      this.highPoint = startLoc.clone();
    }

    if (endLoc != null) {
      this.p2 = endLoc.clone();
      this.lowPoint = endLoc.clone();
    }

    this.recheck();
  }

  public CMIBlockArea clone() {
    CMIBlockArea clone = new CMIBlockArea();
    clone.p1 = this.p1 == null ? null : this.p1.clone();
    clone.p2 = this.p2 == null ? null : this.p2.clone();
    clone.highPoint = this.highPoint == null ? null : this.highPoint.clone();
    clone.lowPoint = this.lowPoint == null ? null : this.lowPoint.clone();
    return clone;
  }

  private void recheck() {
    if (this.p1 != null && this.p2 != null) {
      this.highPoint =
          new CMIVectorInt3D(
              Math.max(this.p1.getX(), this.p2.getX()),
              Math.max(this.p1.getY(), this.p2.getY()),
              Math.max(this.p1.getZ(), this.p2.getZ()));
      this.lowPoint =
          new CMIVectorInt3D(
              Math.min(this.p1.getX(), this.p2.getX()),
              Math.min(this.p1.getY(), this.p2.getY()),
              Math.min(this.p1.getZ(), this.p2.getZ()));
    }
  }

  public boolean valid() {
    return this.highPoint != null && this.lowPoint != null;
  }

  public boolean isAreaWithinArea(CMIBlockArea area) {
    return this.containsLoc(area.getHighPoint()) && this.containsLoc(area.getLowPoint());
  }

  public boolean containsLoc(Location loc) {
    if (loc == null) {
      return false;
    } else {
      return loc.getWorld() == null
          ? false
          : this.containsLoc(new CMIVectorInt3D(loc.toVector()), 0);
    }
  }

  public boolean containsLoc(CMIVectorInt3D vector) {
    return this.containsLoc(vector, 0);
  }

  public boolean containsLoc(Vector vector) {
    return this.containsLoc(new CMIVectorInt3D(vector), 0);
  }

  public boolean containsLoc(Location loc, int extraRange) {
    return loc == null ? false : this.containsLoc(new CMIVectorInt3D(loc.toVector()), extraRange);
  }

  public boolean containsLoc(CMIVectorInt3D vector, int extraRange) {
    if (vector == null) {
      return false;
    } else if (this.lowPoint.getX() - extraRange > vector.getX()) {
      return false;
    } else if (this.highPoint.getX() + extraRange < vector.getX()) {
      return false;
    } else if (this.lowPoint.getZ() - extraRange > vector.getZ()) {
      return false;
    } else if (this.highPoint.getZ() + extraRange < vector.getZ()) {
      return false;
    } else {
      return this.lowPoint.getY() - extraRange > vector.getY()
          ? false
          : this.highPoint.getY() + extraRange >= vector.getY();
    }
  }

  public boolean checkCollision(CMIBlockArea area) {
    if (!this.valid()) {
      return false;
    } else {
      return !area.containsLoc(this.lowPoint)
              && !area.containsLoc(this.highPoint)
              && !this.containsLoc(area.highPoint)
              && !this.containsLoc(area.lowPoint)
          ? advCuboidCheckCollision(this.highPoint, this.lowPoint, area.highPoint, area.lowPoint)
          : true;
    }
  }

  private static boolean advCuboidCheckCollision(
      CMIVectorInt3D A1High, CMIVectorInt3D A1Low, CMIVectorInt3D A2High, CMIVectorInt3D A2Low) {
    int A1HX = A1High.getX();
    int A1LX = A1Low.getX();
    int A1HY = A1High.getY();
    int A1LY = A1Low.getY();
    int A1HZ = A1High.getZ();
    int A1LZ = A1Low.getZ();
    int A2HX = A2High.getX();
    int A2LX = A2Low.getX();
    int A2HY = A2High.getY();
    int A2LY = A2Low.getY();
    int A2HZ = A2High.getZ();
    int A2LZ = A2Low.getZ();
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

    int xsize = this.highPoint.getX() - this.lowPoint.getX() + 1;
    int zsize = this.highPoint.getZ() - this.lowPoint.getZ() + 1;
    int ysize = this.highPoint.getY() - this.lowPoint.getY() + 1;
    return xsize * ysize * zsize;
  }

  public int getXSize() {
    return !this.valid() ? 0 : this.highPoint.getX() - this.lowPoint.getX() + 1;
  }

  public int getYSize() {
    return !this.valid() ? 0 : this.highPoint.getY() - this.lowPoint.getY() + 1;
  }

  public int getZSize() {
    return !this.valid() ? 0 : this.highPoint.getZ() - this.lowPoint.getZ() + 1;
  }

  public CMIVectorInt3D getHighPoint() {
    return this.highPoint;
  }

  public CMIVectorInt3D getLowPoint() {
    return this.lowPoint;
  }

  public List<CMIChunkReference> getChunks() {
    return this.getChunks(0);
  }

  public List<CMIChunkReference> getChunks(int range) {
    List<CMIChunkReference> chunks = new ArrayList<>();
    CMIVectorInt3D high = this.highPoint;
    CMIVectorInt3D low = this.lowPoint;
    int lowX = CMIChunkReference.getChunkCoord(low.getX() - range);
    int lowZ = CMIChunkReference.getChunkCoord(low.getZ() - range);
    int highX = CMIChunkReference.getChunkCoord(high.getX() + range);
    int highZ = CMIChunkReference.getChunkCoord(high.getZ() + range);

    for (int x = lowX; x <= highX; x++) {
      for (int z = lowZ; z <= highZ; z++) {
        chunks.add(new CMIChunkReference(x, z));
      }
    }

    return chunks;
  }

  public void setArea(CMIBlockArea area) {
    this.highPoint = area.getHighPoint().clone();
    this.lowPoint = area.getLowPoint().clone();
    this.recheck();
  }

  public void setHighLocation(Location highLocation) {
    if (highLocation != null) {
      this.setHighPoint(new CMIVectorInt3D(highLocation.toVector()));
    }
  }

  public void setHighPoint(Vector highLocation) {
    this.setHighPoint(new CMIVectorInt3D(highLocation));
  }

  public void setHighPoint(CMIVectorInt3D highPoint) {
    this.p1 = highPoint;
    this.recheck();
  }

  public void setLowLocation(Location lowLocation) {
    if (lowLocation != null) {
      this.setLowPoint(new CMIVectorInt3D(lowLocation.toVector()));
    }
  }

  public void setLowPoint(Vector lowPoint) {
    this.setLowPoint(new CMIVectorInt3D(lowPoint));
  }

  public void setLowPoint(CMIVectorInt3D lowPoint) {
    this.p2 = lowPoint;
    this.recheck();
  }

  public void setLocation(Location location) {
    if (location != null) {
      this.setPoint(location.toVector());
    }
  }

  public void setPoint(Vector vector) {
    if (vector != null) {
      if (this.p1 == null) {
        this.p1 = new CMIVectorInt3D(vector);
      } else {
        this.p2 = new CMIVectorInt3D(vector);
      }

      this.recheck();
    }
  }

  public Vector getMiddlePoint() {
    return !this.valid()
        ? null
        : new Vector(
            (this.getLowPoint().getX() + this.getHighPoint().getX()) / 2,
            (this.getLowPoint().getY() + this.getHighPoint().getY()) / 2,
            (this.getLowPoint().getZ() + this.getHighPoint().getZ()) / 2);
  }

  @Nullable
  @Override
  public String toString() {
    if (!this.valid()) {
      return null;
    }

    StringBuilder sb = new StringBuilder(32);
    sb.append(this.lowPoint.getX())
        .append(';')
        .append(this.lowPoint.getY())
        .append(';')
        .append(this.lowPoint.getZ())
        .append(';')
        .append(this.highPoint.getX())
        .append(';')
        .append(this.highPoint.getY())
        .append(';')
        .append(this.highPoint.getZ());
    return sb.toString();
  }

  @Nullable
  public static CMIBlockArea fromString(String value) {
    String[] parts = value.split(";");

    try {
      CMIVectorInt3D lowPoint =
          new CMIVectorInt3D(
              Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
      CMIVectorInt3D highPoint =
          new CMIVectorInt3D(
              Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
      return new CMIBlockArea(lowPoint, highPoint);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public boolean shift(Player player, int amount) {
    return this.shift(getDirection(player), player.getWorld(), amount);
  }

  public boolean shift(CMIActionDirection d, World world, int amount) {
    return this.shift(d, new CMIWorld(world), amount);
  }

  public boolean shift(CMIActionDirection d, CMIWorld world, int amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case MINUS_Y:
        int oldy = this.getLowPoint().getY() - amount;
        if (oldy < CMIWorld.getMinHeight(world)) {
          oldy = CMIWorld.getMinHeight(world);
        }

        this.getLowPoint().setY(oldy);
        this.getHighPoint().setY(this.getHighPoint().getY() - amount);
        break;
      case PLUS_Y:
        int newHighY = this.getHighPoint().getY() + amount;
        if (newHighY > CMIWorld.getMaxHeight(world)) {
          newHighY = CMIWorld.getMaxHeight(world);
        }

        this.getHighPoint().setY(newHighY);
        this.getLowPoint().setY(this.getLowPoint().getY() + amount);
        break;
      case MINUS_X:
        this.getLowPoint().setX(this.getLowPoint().getX() - amount);
        this.getHighPoint().setX(this.getHighPoint().getX() - amount);
        break;
      case MINUS_Z:
        this.getLowPoint().setZ(this.getLowPoint().getZ() - amount);
        this.getHighPoint().setZ(this.getHighPoint().getZ() - amount);
        break;
      case PLUS_X:
        this.getHighPoint().setX(this.getHighPoint().getX() + amount);
        this.getLowPoint().setX(this.getLowPoint().getX() + amount);
        break;
      case PLUS_Z:
        this.getHighPoint().setZ(this.getHighPoint().getZ() + amount);
        this.getLowPoint().setZ(this.getLowPoint().getZ() + amount);
    }

    return true;
  }

  public boolean expand(Player player, int amount) {
    return this.expand(getDirection(player), player.getWorld(), amount);
  }

  public boolean expand(CMIActionDirection d, World world, int amount) {
    return this.expand(d, new CMIWorld(world), amount);
  }

  public boolean expand(CMIActionDirection d, CMIWorld world, int amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case MINUS_Y:
        int newLowY = this.getLowPoint().getY() - amount;
        if (newLowY < CMIWorld.getMinHeight(world)) {
          newLowY = CMIWorld.getMinHeight(world);
        }

        this.getLowPoint().setY(newLowY);
        break;
      case PLUS_Y:
        int newHighY = this.getHighPoint().getY() + amount;
        if (newHighY > CMIWorld.getMaxHeight(world)) {
          newHighY = CMIWorld.getMaxHeight(world);
        }

        this.getHighPoint().setY(newHighY);
        break;
      case MINUS_X:
        this.getLowPoint().setX(this.getLowPoint().getX() - amount);
        break;
      case MINUS_Z:
        this.getLowPoint().setZ(this.getLowPoint().getZ() - amount);
        break;
      case PLUS_X:
        this.getHighPoint().setX(this.getHighPoint().getX() + amount);
        break;
      case PLUS_Z:
        this.getHighPoint().setZ(this.getHighPoint().getZ() + amount);
    }

    return true;
  }

  public boolean contract(Player player, int amount) {
    return !this.valid() ? false : this.contract(getDirection(player), player.getWorld(), amount);
  }

  public boolean contract(CMIActionDirection d, World world, int amount) {
    return this.contract(d, new CMIWorld(world), amount);
  }

  public boolean contract(CMIActionDirection d, CMIWorld world, int amount) {
    if (!this.valid()) {
      return false;
    }

    if (d == null) {
      return false;
    }

    switch (d) {
      case MINUS_Y:
        int newHighY = this.getHighPoint().getY() - amount;
        if (newHighY > CMIWorld.getMaxHeight(world)) {
          newHighY = CMIWorld.getMaxHeight(world);
        }

        newHighY = this.getHighPoint().getY() - amount;
        newHighY = this.getLowPoint().getY() > newHighY ? this.getLowPoint().getY() : newHighY;
        this.getHighPoint().setY(newHighY);
        break;
      case PLUS_Y:
        int newLowY = this.getLowPoint().getY() + amount;
        if (newLowY < CMIWorld.getMinHeight(world)) {
          newLowY = CMIWorld.getMinHeight(world);
        }

        newLowY = this.getLowPoint().getY() + amount;
        newLowY = this.getHighPoint().getY() < newLowY ? this.getHighPoint().getY() : newLowY;
        this.getLowPoint().setY(newLowY);
        break;
      case MINUS_X:
        {
          int res = this.getHighPoint().getX() - amount;
          res = this.getLowPoint().getX() > res ? this.getLowPoint().getX() : res;
          this.getHighPoint().setX(res);
          break;
        }
      case MINUS_Z:
        {
          int res = this.getHighPoint().getZ() - amount;
          res = this.getLowPoint().getZ() > res ? this.getLowPoint().getZ() : res;
          this.getHighPoint().setZ(res);
          break;
        }
      case PLUS_X:
        {
          int res = this.getLowPoint().getX() + amount;
          res = this.getHighPoint().getX() < res ? this.getHighPoint().getX() : res;
          this.getLowPoint().setX(res);
          break;
        }
      case PLUS_Z:
        {
          int res = this.getLowPoint().getZ() + amount;
          res = this.getHighPoint().getZ() < res ? this.getHighPoint().getZ() : res;
          this.getLowPoint().setZ(res);
        }
    }

    return true;
  }

  private static CMIActionDirection getDirection(Player player) {
    int yaw = (int) player.getLocation().getYaw();
    if (yaw < 0) {
      yaw += 360;
    }

    yaw += 45;
    yaw %= 360;
    int facing = yaw / 90;
    float pitch = player.getLocation().getPitch();
    if (pitch < -50.0F) {
      return CMIActionDirection.PLUS_Y;
    } else if (pitch > 50.0F) {
      return CMIActionDirection.MINUS_Y;
    } else if (facing == 1) {
      return CMIActionDirection.MINUS_X;
    } else if (facing == 3) {
      return CMIActionDirection.PLUS_X;
    } else if (facing == 2) {
      return CMIActionDirection.MINUS_Z;
    } else {
      return facing == 0 ? CMIActionDirection.PLUS_Z : null;
    }
  }
}
