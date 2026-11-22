package com.Zrips.CMI.Modules.Teleportations;

import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class CMITeleportLocation {
  private World world;
  private Vector position = null;
  private float yaw = 0.0F;
  private float pitch = 0.0F;
  private CMITeleportCondition condition = CMITeleportCondition.Good;
  private double fallDistance = 0.0;
  private CMIMaterial floor = null;
  private CMITeleportType type = CMITeleportType.Unknown;

  public CMITeleportLocation(World newWorld) {
    this.world = newWorld;
  }

  public Vector getPosition() {
    return this.position;
  }

  public CMITeleportLocation setPosition(Vector vector) {
    this.position = vector;
    return this;
  }

  public World getWorld() {
    return this.world;
  }

  @Nullable
  public Location getLocation() {
    return this.position == null
        ? null
        : new Location(
            this.world,
            this.position.getX(),
            this.position.getY(),
            this.position.getZ(),
            this.yaw,
            this.pitch);
  }

  public float getYaw() {
    return this.yaw;
  }

  public CMITeleportLocation setYaw(float floatValue) {
    this.yaw = floatValue;
    return this;
  }

  public float getPitch() {
    return this.pitch;
  }

  public CMITeleportLocation setPitch(float floatValue) {
    this.pitch = floatValue;
    return this;
  }

  public CMITeleportCondition getCondition() {
    return this.condition;
  }

  public CMITeleportLocation setCondition(CMITeleportCondition cmiTeleportCondition) {
    this.condition = cmiTeleportCondition;
    return this;
  }

  public double getFallDistance() {
    return this.fallDistance;
  }

  public CMITeleportLocation setFallDistance(double number) {
    this.fallDistance = number;
    return this;
  }

  public CMIMaterial getFloor() {
    return this.floor;
  }

  public CMITeleportLocation setFloor(CMIMaterial cmiMaterial) {
    this.floor = cmiMaterial;
    return this;
  }

  public CMITeleportType getType() {
    return this.type;
  }

  public void setType(CMITeleportType cmiTeleportType) {
    this.type = cmiTeleportType;
  }
}
