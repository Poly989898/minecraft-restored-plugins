package com.Zrips.CMI.Modules.Teleportations;

import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Container.CMIWorld;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CMITeleportationBounds {
  private World world;
  private CMIVector3D position;
  private int maxY;
  private int minY;
  private float yaw;
  private float pitch;
  private boolean imortal = false;
  private boolean canFly = false;
  private boolean flying = false;
  private Player player = null;
  private boolean ignoreYLimits = false;

  CMITeleportationBounds(Location location) {
    this.world = location.getWorld();
    this.yaw = location.getYaw();
    this.pitch = location.getPitch();
    this.position = new CMIVector3D(location.getX(), location.getY(), location.getZ());
    this.maxY = location.getBlockY();
    this.minY = location.getBlockY() - 2;
  }

  public World getWorld() {
    return this.world;
  }

  public CMITeleportationBounds setUpRange(double number) {
    this.maxY =
        CMINumber.clamp(
            (int) (this.position.getY() + number),
            CMIWorld.getMinHeight(this.world),
            CMIWorld.getMaxHeight(this.world));
    return this;
  }

  public CMITeleportationBounds setDownRange(double number) {
    this.minY =
        CMINumber.clamp(
            (int) (this.position.getY() - number),
            CMIWorld.getMinHeight(this.world),
            CMIWorld.getMaxHeight(this.world));
    return this;
  }

  public CMITeleportationBounds setRange(double number) {
    this.setUpRange(number);
    this.setDownRange(number);
    return this;
  }

  public CMITeleportationBounds setWorld(World newWorld) {
    this.world = newWorld;
    return this;
  }

  public int getMaxY() {
    return this.maxY;
  }

  public CMITeleportationBounds setMaxY(int value) {
    this.maxY = value;
    return this;
  }

  public int getMinY() {
    return this.minY;
  }

  public CMITeleportationBounds setMinY(int value) {
    this.minY = value;
    return this;
  }

  public CMIVector3D getPosition() {
    return this.position;
  }

  public CMITeleportationBounds setPosition(CMIVector3D cmiVector3D) {
    this.position = cmiVector3D;
    return this;
  }

  public float getYaw() {
    return this.yaw;
  }

  public CMITeleportationBounds setYaw(float floatValue) {
    this.yaw = floatValue;
    return this;
  }

  public float getPitch() {
    return this.pitch;
  }

  public CMITeleportationBounds setPitch(float floatValue) {
    this.pitch = floatValue;
    return this;
  }

  public boolean isCanFly() {
    return this.canFly;
  }

  public CMITeleportationBounds setCanFly(boolean flag) {
    this.canFly = flag;
    return this;
  }

  public boolean isFlying() {
    return this.flying;
  }

  public void setFlying(boolean flag) {
    this.flying = flag;
  }

  public boolean isImortal() {
    return this.imortal;
  }

  public CMITeleportationBounds setImortal(boolean flag) {
    this.imortal = flag;
    return this;
  }

  public Player getPlayer() {
    return this.player;
  }

  public void setPlayer(Player newPlayer) {
    this.player = newPlayer;
  }

  public boolean isIgnoreYLimits() {
    return this.ignoreYLimits;
  }

  public void setIgnoreYLimits(boolean flag) {
    this.ignoreYLimits = flag;
  }
}
