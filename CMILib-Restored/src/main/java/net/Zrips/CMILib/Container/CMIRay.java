package net.Zrips.CMILib.Container;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIRay {
  private Vector origin;
  private Vector direction;

  public CMIRay(Vector origin, Vector direction) {
    this.origin = origin;
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

  public double origin(int i) {
    switch (i) {
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

  public double direction(int i) {
    switch (i) {
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

  public Vector getPoint(double distance) {
    return this.direction.clone().normalize().multiply(distance).add(this.origin);
  }
}
