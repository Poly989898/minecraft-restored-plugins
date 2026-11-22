package net.Zrips.CMILib.Cuboids;

import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CMIBlockWorldArea extends CMIBlockArea {
  private CMIWorld world = new CMIWorld();

  public CMIBlockWorldArea() {}

  public CMIBlockWorldArea(Location startLoc, Location endLoc) {
    this(startLoc.getWorld(), startLoc.toVector(), endLoc.toVector());
  }

  public CMIBlockWorldArea(CMILocation startLoc, CMILocation endLoc) {
    this(startLoc.getWorld(), startLoc.toVector(), endLoc.toVector());
    this.world = new CMIWorld(startLoc);
  }

  public CMIBlockWorldArea(World world, Vector startLoc, Vector endLoc) {
    super(startLoc, endLoc);
    this.world = new CMIWorld(world);
  }

  public void setWorld(@NotNull World world) {
    this.world = world == null ? new CMIWorld() : new CMIWorld(world);
  }

  @NotNull
  public CMIWorld getWorld() {
    return this.world;
  }

  public CMIBlockWorldArea clone() {
    CMIBlockWorldArea clone = new CMIBlockWorldArea();
    clone.p1 = this.p1 == null ? null : this.p1.clone();
    clone.p2 = this.p2 == null ? null : this.p2.clone();
    clone.highPoint = this.highPoint == null ? null : this.highPoint.clone();
    clone.lowPoint = this.lowPoint == null ? null : this.lowPoint.clone();
    clone.world = this.world;
    return clone;
  }

  public boolean contract(CMIActionDirection d, int amount) {
    return super.contract(d, this.getWorld(), amount);
  }

  public boolean expand(CMIActionDirection d, int amount) {
    return super.expand(d, this.getWorld(), amount);
  }

  public boolean shift(CMIActionDirection d, int amount) {
    return super.shift(d, this.getWorld(), amount);
  }

  @Override
  public boolean containsLoc(Location loc) {
    return this.containsLoc(loc, 0);
  }

  @Override
  public boolean containsLoc(Location loc, int extraRange) {
    if (loc == null) {
      return false;
    } else if (loc.getWorld() == null) {
      return false;
    } else {
      return !loc.getWorld().equals(this.getWorld()) ? false : super.containsLoc(loc, extraRange);
    }
  }

  public boolean checkCollision(CMIBlockWorldArea area) {
    if (area.getWorld() != null && this.getWorld() != null) {
      return !area.getWorld().equals(this.getWorld()) ? false : super.checkCollision(area);
    } else {
      return false;
    }
  }

  @Override
  public void setLowLocation(Location lowLocation) {
    if (lowLocation != null) {
      this.setWorld(lowLocation.getWorld());
      super.setLowLocation(lowLocation);
    }
  }

  @Override
  public void setHighLocation(Location highLocation) {
    if (highLocation != null) {
      this.setWorld(highLocation.getWorld());
      super.setHighLocation(highLocation);
    }
  }

  @Nullable
  @Override
  public String toString() {
    if (!this.valid()) {
      return null;
    }

    StringBuilder sb = new StringBuilder(32);
    sb.append(this.getWorld().getWorldName())
        .append(';')
        .append(this.lowPoint.getX())
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
  public static CMIBlockWorldArea fromString(String value) {
    String[] parts = value.split(";");

    try {
      String worldName = parts[0];
      CMIVectorInt3D lowPoint =
          new CMIVectorInt3D(
              Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
      CMIVectorInt3D highPoint =
          new CMIVectorInt3D(
              Integer.parseInt(parts[4]), Integer.parseInt(parts[5]), Integer.parseInt(parts[6]));
      CMIBlockWorldArea cuboid = new CMIBlockWorldArea();
      cuboid.setLowPoint(lowPoint);
      cuboid.setHighPoint(highPoint);
      cuboid.world = new CMIWorld(worldName);
      return cuboid;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
