package com.Zrips.CMI.Modules.Particl;

import com.Zrips.CMI.CMI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIPECircle extends CMIPEAnimation {
  private double MaxMoveDistance = 2.0;
  int lastPos = 0;
  private boolean circling = false;
  private int times = -1;
  private Vector move;
  private Double moveMulty = null;
  private double radiusChange = 0.0;
  private double radius = 1.0;
  private double maxRadius = 2.0;
  private boolean followPitch = false;
  private boolean followYaw = false;
  private double pitch = 0.0;
  private double pitchAdjust = 0.0;
  private double yaw = 0.0;
  private double yawAdjust = 0.0;
  private boolean raindbowColorStatic = false;
  private boolean raindbowColor = false;
  private boolean raindbowColorFromStart = false;
  private short startingAngle = 0;

  public CMIPECircle() {}

  public CMIPECircle(CMIEffect cmiEffect) {
    this.setCMIEffect(cmiEffect);
  }

  @Deprecated
  public CMIPECircle(CMIParticle cmiParticle) {
    this.setParticle(cmiParticle);
  }

  @Override
  public boolean render(List<Player> list) {
    this.updateTimes++;
    Location location = this.center.clone();
    if (this.getMove() != null) {
      location =
          location.add(
              this.getMove().getX() * this.updateTimes,
              this.getMove().getY() * this.updateTimes,
              this.getMove().getZ() * this.updateTimes);
      if (CMI.getInstance().getUtilManager().getDistance(location, this.center)
          > this.MaxMoveDistance) {
        return false;
      }
    }

    this.radius = this.radius + this.radiusChange;
    if (this.radius < 0.0) {
      return false;
    }

    if (this.radius > this.maxRadius) {
      return false;
    }

    if (this.isFollowYaw() && !list.isEmpty()) {
      location.setYaw(((Player) list.get(0)).getLocation().getYaw());
    }

    if (this.isFollowPitch() && !list.isEmpty()) {
      location.setPitch(((Player) list.get(0)).getLocation().getPitch());
    }

    var locations =
        this.getLocations(location.add(this.getOffset()), this.radius, this.yaw, this.pitch);
    if (this.getParticle() == null) {
      return false;
    }

    int value = 1;
    if (this.times > 0) {
      value = locations.size() / this.times;
      if (this.circling) {
        this.lastPos++;
      }

      if (this.lastPos >= value) {
        this.lastPos = 0;
      }
    }

    if (this.isRaindbowColor() && this.getParticle().isColored()) {
      double number = (int) ((this.yaw + 540.0) % 360.0 * 100.0) / 100.0;
      double yawFraction = number * 100.0 / 360.0 / 100.0;
      this.setColor(Color.getHSBColor((float) yawFraction, 1.0F, 1.0F));
    }

    int index = -1;

    for (Location point : locations) {
      if (++index >= value) {
        index = 0;
      }

      if (index == this.lastPos) {
        if (this.getParticle().isColored()) {
          if (this.isRaindbowColorStatic()) {
            double yaw = CMI.getInstance().getUtilManager().getYaw(this.center, point);
            double yawNorm = (int) ((yaw + 540.0) % 360.0 * 100.0) / 100.0;
            double yawRatio = yawNorm * 100.0 / 360.0 / 100.0;
            double pitch = CMI.getInstance().getUtilManager().getPitch(this.center, point);
            double pitchNorm = (int) ((pitch + 135.0) % 180.0 * 100.0) / 100.0;
            double pitchRatio = pitchNorm * 100.0 / 180.0 / 100.0;
            double mixed = (yawRatio + pitchRatio) / 2.0;
            this.setColor(Color.getHSBColor((float) mixed, 1.0F, 1.0F));
          } else if (this.isRaindbowColorFromStart()) {
            double distance = CMI.getInstance().getUtilManager().getDistance(this.center, point);
            distance = distance > this.getMaxMoveDistance() ? this.getMaxMoveDistance() : distance;
            double distanceRatio = distance * 100.0 / this.getMaxMoveDistance() / 100.0;
            this.setColor(Color.getHSBColor((float) distanceRatio, 1.0F, 1.0F));
          }

          for (Player player : list) {
            CMILib.getInstance()
                .getReflectionManager()
                .playEffect(player, point, this.getCMIEffect());
          }
        } else {
          for (Player player : list) {
            CMILib.getInstance()
                .getReflectionManager()
                .playEffect(player, point, this.getCMIEffect());
          }
        }
      }
    }

    return true;
  }

  private List<Location> getLocations(Location location, double number, double x, double z) {
    if (this.pitchAdjust != 0.0) {
      this.pitch = this.pitch + this.pitchAdjust;
    }

    if (this.yawAdjust != 0.0) {
      this.yaw = this.yaw + this.yawAdjust;
    }

    x = (this.isFollowYaw() ? location.getYaw() : this.yaw) / 180.0 * Math.PI;
    z = (this.isFollowPitch() ? location.getPitch() : this.pitch) / 180.0 * Math.PI;
    ArrayList<Location> arrayList = new ArrayList();

    for (double angle = 0.0; angle < 360.0; angle += 15.0) {
      double radius = number;
      double xPos = this.xPosYaw(Math.toRadians(angle + this.getStartingAngle()), radius, x);
      double yPos = this.yPosYaw(Math.toRadians(angle + this.getStartingAngle()), radius);
      double zPos = this.zPosYaw(Math.toRadians(angle + this.getStartingAngle()), radius, x);
      Vector vector = new Vector(xPos, yPos, zPos);
      vector = rotateX(vector, z);
      vector = rotateY(vector, -x);
      Location point =
          new Location(
              location.getWorld(),
              vector.getX() + location.getX(),
              vector.getY() + location.getY(),
              vector.getZ() + location.getZ());
      arrayList.add(point);
    }

    return arrayList;
  }

  private static Vector rotateX(Vector vector, double number) {
    double rotY = Math.cos(number) * vector.getY() - Math.sin(number) * vector.getZ();
    double rotZ = Math.sin(number) * vector.getY() + Math.cos(number) * vector.getZ();
    return vector.setY(rotY).setZ(rotZ);
  }

  private static Vector rotateY(Vector vector, double number) {
    double rotX = Math.cos(number) * vector.getX() + Math.sin(number) * vector.getZ();
    double rotZ = -Math.sin(number) * vector.getX() + Math.cos(number) * vector.getZ();
    return vector.setX(rotX).setZ(rotZ);
  }

  public double xPosYaw(double number, double y, double z) {
    return Math.sin(number) * y * Math.cos((Math.PI / 180.0) * z);
  }

  public double yPosYaw(double number, double z) {
    return Math.cos(number) * z;
  }

  public double zPosYaw(double number, double x, double y) {
    return Math.sin(number) * x * Math.sin((Math.PI / 180.0) * y);
  }

  public boolean isRotating() {
    return this.circling;
  }

  public void setRotating(boolean flag) {
    this.circling = flag;
  }

  public int getTimes() {
    return this.times;
  }

  public void setTimes(int value) {
    this.times = value;
  }

  public Vector getMove() {
    return this.move;
  }

  public void setMove(Vector vector) {
    this.move = vector;
  }

  public double getRadius() {
    return this.radius;
  }

  public void setRadius(double number) {
    this.radius = number;
  }

  public double getRadiusChange() {
    return this.radiusChange;
  }

  public void setRadiusChange(double number) {
    this.radiusChange = number;
  }

  public boolean isFollowDirection() {
    return this.followPitch && this.followYaw;
  }

  public void setFollowDirection(boolean flag) {
    this.followPitch = flag;
    this.followYaw = flag;
  }

  public double getMaxRadius() {
    return this.maxRadius;
  }

  public void setMaxRadius(double number) {
    this.maxRadius = number;
  }

  public double getMaxMoveDistance() {
    return this.MaxMoveDistance;
  }

  public void setMaxMoveDistance(double number) {
    this.MaxMoveDistance = number;
  }

  public double getPitchAdjust() {
    return this.pitchAdjust;
  }

  public void setPitchAdjust(double number) {
    this.pitchAdjust = number;
  }

  public double getPitch() {
    return this.pitch;
  }

  public void setPitch(double number) {
    this.pitch = number;
  }

  public double getYaw() {
    return this.yaw;
  }

  public void setYaw(double number) {
    this.yaw = number;
  }

  public double getYawAdjust() {
    return this.yawAdjust;
  }

  public void setYawAdjust(double number) {
    this.yawAdjust = number;
  }

  public boolean isRaindbowColorStatic() {
    return this.raindbowColorStatic;
  }

  public void setRaindbowColorStatic(boolean flag) {
    this.raindbowColorStatic = flag;
  }

  public boolean isRaindbowColor() {
    return this.raindbowColor;
  }

  public void setRaindbowColor(boolean flag) {
    this.raindbowColor = flag;
  }

  public boolean isRaindbowColorFromStart() {
    return this.raindbowColorFromStart;
  }

  public void setRaindbowColorFromStart(boolean flag) {
    this.raindbowColorFromStart = flag;
  }

  public Double getMoveMulty() {
    return this.moveMulty;
  }

  public void setMoveMulty(Double moveMultiplier) {
    this.moveMulty = moveMultiplier;
  }

  public short getStartingAngle() {
    return this.startingAngle;
  }

  public void setStartingAngle(short shortValue) {
    this.startingAngle = shortValue;
  }

  public boolean isFollowPitch() {
    return this.followPitch;
  }

  public void setFollowPitch(boolean flag) {
    this.followPitch = flag;
  }

  public boolean isFollowYaw() {
    return this.followYaw;
  }

  public void setFollowYaw(boolean flag) {
    this.followYaw = flag;
  }
}
