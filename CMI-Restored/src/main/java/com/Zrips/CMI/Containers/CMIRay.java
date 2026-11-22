package com.Zrips.CMI.Containers;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIRay {
  private Vector origin;
  private Vector direction;

  public CMIRay(Vector vector, Vector direction) {
    this.origin = vector;
    this.direction = direction;
  }

  public CMIRay(Player player) {
    this(player.getEyeLocation().toVector(), player.getLocation().getDirection());
  }

  public Vector getOrigin() {
    return this.origin;
  }

  public Vector getDirection() {
    return this.direction;
  }

  public double origin(int value) {
    switch (value) {
      case 0:
        return this.origin.getX();
      case 1:
        return this.origin.getY();
      case 2:
        return this.origin.getZ();
      default:
        return 0.0;
    }
  }

  public double direction(int value) {
    switch (value) {
      case 0:
        return this.direction.getX();
      case 1:
        return this.direction.getY();
      case 2:
        return this.direction.getZ();
      default:
        return 0.0;
    }
  }

  public Vector getPoint(double number) {
    return this.direction.clone().normalize().multiply(number).add(this.origin);
  }
}
