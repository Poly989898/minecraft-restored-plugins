package com.Zrips.CMI.Modules.Mirror;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIPlayerConnection;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.commands.list.mirror;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.Container.CMIBlock.FlipDirection;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CMIBlockMirroring {
  private HashMap<CMIMirrorType, CMIMirrorMode> map = new HashMap<>();
  private Location blockPosition = null;
  private boolean paused = false;
  private Vector edge = new Vector();
  private long lastAction = 0L;
  private CMITask timer = null;
  private Player player;
  private CMIMIrrorFlipType flipType = CMIMIrrorFlipType.Axis;
  CMIEffect centerEffect = new CMIEffect(CMIParticle.DUST);
  int cycles = 0;

  public CMIBlockMirroring(Player targetPlayer, Location location) {
    this.centerEffect.setColorFrom(Color.fromBGR(0, 255, 0));
    this.setCenter(location);
    this.player = targetPlayer;
    this.runTimer();
    Arrays.asList(CMIMirrorType.values())
        .forEach(cmiMirrorType -> this.map.put(cmiMirrorType, new CMIMirrorMode(cmiMirrorType)));
  }

  public CMIBlockMirroring reset() {
    this.map.clear();
    return this;
  }

  public boolean is(CMIMirrorType cmiMirrorType) {
    return this.map.get(cmiMirrorType) != null && this.map.get(cmiMirrorType).isEnabled();
  }

  public CMIBlockMirroring set(CMIMirrorType cmiMirrorType, CMIMirrorMode cmiMirrorMode) {
    this.map.put(cmiMirrorType, cmiMirrorMode);
    return this;
  }

  public CMIMirrorMode get(CMIMirrorType cmiMirrorType) {
    return this.map.computeIfAbsent(cmiMirrorType, ignoredType -> new CMIMirrorMode(cmiMirrorType));
  }

  public Location getCenter() {
    return this.blockPosition;
  }

  public CMIBlockMirroring setCenter(Location location) {
    this.blockPosition = location.clone();
    this.updateEdge();
    return this;
  }

  public boolean isPaused() {
    return this.paused;
  }

  public CMIBlockMirroring setPaused(boolean flag) {
    this.paused = flag;
    this.runTimer();
    return this;
  }

  public CMIBlockMirroring clone() {
    CMIBlockMirroring cmiBlockMirroring =
        new CMIBlockMirroring(this.player, this.blockPosition.clone());
    cmiBlockMirroring.map = this.map;
    cmiBlockMirroring.paused = this.paused;
    cmiBlockMirroring.edge = this.edge;
    cmiBlockMirroring.lastAction = this.lastAction;
    cmiBlockMirroring.flipType = this.flipType;
    return cmiBlockMirroring;
  }

  public long getLastAction() {
    return this.lastAction;
  }

  public void setLastAction(long longValue) {
    this.lastAction = longValue;
  }

  public Vector getEdge() {
    return this.edge;
  }

  public Vector getNextEdge() {
    double number = 0.5;
    boolean flag = true;
    int value = (int) (this.getEdge().getX() + 0.6);
    int edgeY = (int) (this.getEdge().getY() + 0.6);
    int edgeZ = (int) (this.getEdge().getZ() + 0.6);
    if (++edgeZ > 1) {
      edgeZ = 0;
      if (++edgeY > 1) {
        edgeY = 0;
        if (++value > 1) {
          value = 0;
        }
      }
    }

    return new Vector(value * 0.5, edgeY * 0.5, edgeZ * 0.5);
  }

  public int edgeToNumber() {
    int value = this.getEdge().getX() == 0.5 ? 1 : 0;
    int edgeYOffset = this.getEdge().getY() == 0.5 ? 1 : 0;
    int edgeZOffset = this.getEdge().getZ() == 0.5 ? 1 : 0;
    return value << 2 | edgeYOffset << 1 | edgeZOffset;
  }

  public CMIBlockMirroring setEdge(Vector vector) {
    this.edge = vector;
    this.updateEdge();
    return this;
  }

  private void updateEdge() {
    this.blockPosition.setX(this.blockPosition.getBlockX());
    this.blockPosition.setY(this.blockPosition.getBlockY());
    this.blockPosition.setZ(this.blockPosition.getBlockZ());
    this.blockPosition.add(this.getEdge());
  }

  private double normalizeAngle(double number) {
    while (number <= -Math.PI) {
      number += Math.PI * 2;
    }

    while (number > Math.PI) {
      number -= Math.PI * 2;
    }

    return number;
  }

  private HashMap<Vector, CMIMirrorFlipInformation> calculateFlipAngles(
      Set<Vector> points, Vector vector) {
    LinkedHashMap<Vector, CMIMirrorFlipInformation> linkedHashMap = new LinkedHashMap<>();
    Vector center = this.getCenter().toVector();
    CMIMIrrorFlipType cmimIrrorFlipType = this.getFlipType();
    double number = vector.getX() - center.getX();
    double dy = vector.getY() - center.getY();
    double dz = vector.getZ() - center.getZ();
    double angleA = Math.atan2(dy, number);
    double angleB = Math.atan2(dz, number);

    for (Vector point : points) {
      double dx = point.getX() - center.getX();
      double pointDy = point.getY() - center.getY();
      double pointDz = point.getZ() - center.getZ();
      double pointAngleA = Math.atan2(pointDy, dx);
      double pointAngleB = Math.atan2(pointDz, dx);
      double deltaA = this.normalizeAngle(angleA - pointAngleA);
      double deltaB = this.normalizeAngle(angleB - pointAngleB);
      double degreesA = Math.toDegrees(deltaA);
      double degreesB = Math.toDegrees(deltaB);
      boolean flag = dx * number <= 0.0;
      boolean flipY = pointDy * dy < 0.0;
      boolean flipZ = pointDz * dz < 0.0;
      boolean crossA = (dx - pointDz) * (number - dz) < 0.0;
      boolean crossB = (dx + pointDz) * (number + dz) < 0.0;
      CMIMirrorFlipInformation cmiMirrorFlipInformation =
          linkedHashMap.computeIfAbsent(point, object -> new CMIMirrorFlipInformation());
      if (cmimIrrorFlipType == CMIMIrrorFlipType.Axis) {
        if (flipY) {
          cmiMirrorFlipInformation.add(FlipDirection.UP_DOWN);
        }

        if (point.getBlockX() != vector.getBlockX() && point.getBlockZ() != vector.getBlockZ()) {
          if (!crossA && crossB && !flag && flipZ) {
            if ((!(dx < 0.0) || !(pointDz < 0.0)) && (!(dx > 0.0) || !(pointDz > 0.0))) {
              if (dx < 0.0 && pointDz > 0.0 || dx > 0.0 && pointDz < 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (!crossA && !crossB && !flag && flipZ) {
            if ((!(dx < 0.0) || !(pointDz < 0.0)) && (!(dx > 0.0) || !(pointDz > 0.0))) {
              if (dx < 0.0 && pointDz > 0.0 || dx > 0.0 && pointDz < 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (crossA && !crossB && !flag && flipZ) {
            if ((!(dx < 0.0) || !(pointDz < 0.0)) && (!(dx > 0.0) || !(pointDz > 0.0))) {
              if (dx < 0.0 && pointDz > 0.0 || dx > 0.0 && pointDz < 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (crossA && !crossB && flag && !flipZ) {
            if ((!(dx <= 0.0) || !(pointDz > 0.0)) && (!(dx >= 0.0) || !(pointDz < 0.0))) {
              if (dx < 0.0 && pointDz <= 0.0 || dx > 0.0 && pointDz >= 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (!crossA && !crossB && flag && !flipZ) {
            if ((!(dx <= 0.0) || !(pointDz > 0.0)) && (!(dx >= 0.0) || !(pointDz < 0.0))) {
              if (dx < 0.0 && pointDz <= 0.0 || dx > 0.0 && pointDz >= 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (!crossA && crossB && flag && !flipZ) {
            if ((!(dx < 0.0) || !(pointDz >= 0.0)) && (!(dx > 0.0) || !(pointDz <= 0.0))) {
              if (dx <= 0.0 && pointDz <= 0.0 || dx >= 0.0 && pointDz >= 0.0) {
                cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              }
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            }
          }

          if (crossA && crossB && flag && flipZ) {
            cmiMirrorFlipInformation.add(FlipDirection.ROTATE_180);
          }

          if (crossA && crossB && flag && !flipZ) {
            cmiMirrorFlipInformation.add(FlipDirection.ROTATE_180);
          }

          if (crossA && !crossB && flag && flipZ) {
            if (Math.abs(dx) == Math.abs(pointDz)) {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_180);
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
              cmiMirrorFlipInformation.add(FlipDirection.NORTH_SOUTH);
            }
          }

          if (!crossA && crossB && flag && flipZ) {
            if (Math.abs(dx) == Math.abs(pointDz)) {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_180);
            } else {
              cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
              cmiMirrorFlipInformation.add(FlipDirection.NORTH_SOUTH);
            }
          }

          if (crossA && !crossB && !flag && !flipZ) {
            cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            cmiMirrorFlipInformation.add(FlipDirection.WEST_EAST);
          }

          if (!crossA && crossB && !flag && !flipZ) {
            cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
            cmiMirrorFlipInformation.add(FlipDirection.NORTH_SOUTH);
          }
        } else {
          if (flag && !flipZ) {
            cmiMirrorFlipInformation.add(FlipDirection.WEST_EAST);
          }

          if (flipZ && !flag) {
            cmiMirrorFlipInformation.add(FlipDirection.NORTH_SOUTH);
          }

          if (flipZ && flag) {
            cmiMirrorFlipInformation.add(FlipDirection.NORTH_SOUTH);
            cmiMirrorFlipInformation.add(FlipDirection.WEST_EAST);
          }
        }
      } else {
        if (degreesA != 0.0 && point.getBlockY() != vector.getBlockY()) {
          cmiMirrorFlipInformation.add(FlipDirection.UP_DOWN);
        }

        if (degreesB >= 45.0 && degreesB <= 135.0) {
          cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CCW);
        }

        if (degreesB <= -45.0 && degreesB >= -135.0) {
          cmiMirrorFlipInformation.add(FlipDirection.ROTATE_90_CW);
        }

        if (degreesB > 135.0 || degreesB < -135.0 || degreesB > 90.0 || degreesB < -90.0) {
          cmiMirrorFlipInformation.add(FlipDirection.ROTATE_180);
        }
      }
    }

    return linkedHashMap;
  }

  private Set<Vector> getPointsRecursive(Set<Vector> points) {
    int value = points.size();
    this.cycles++;
    if (this.cycles > 5) {
      this.cycles = 0;
      return points;
    }

    if (points.size() > CMI.getInstance().getMirrorManager().getMaxBlocks() * 2) {
      return points;
    }

    Vector vector = this.getCenter().toVector();
    HashSet<Vector> hashSet = new HashSet();

    for (Vector point : points) {
      if (this.is(CMIMirrorType.XR)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.XR));
      }

      if (this.is(CMIMirrorType.YR)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.YR));
      }

      if (this.is(CMIMirrorType.ZR)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.ZR));
      }

      if (this.is(CMIMirrorType.ZX180)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.ZX180));
      }

      if (this.is(CMIMirrorType.XY180)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.XY180));
      }

      if (this.is(CMIMirrorType.YZ180)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.YZ180));
      }

      if (this.is(CMIMirrorType.XYZ180)) {
        hashSet.add(getFlippedPosition(vector, point, CMIMirrorType.XYZ180));
      }

      if (this.is(CMIMirrorType.XYM)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.XYM));
      }

      if (this.is(CMIMirrorType.XYMR)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.XYMR));
      }

      if (this.is(CMIMirrorType.YZM)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.YZM));
      }

      if (this.is(CMIMirrorType.YZMR)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.YZMR));
      }

      if (this.is(CMIMirrorType.ZXM)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.ZXM));
      }

      if (this.is(CMIMirrorType.ZXMR)) {
        hashSet.add(getFlippedMirrorPosition(vector, point, CMIMirrorType.ZXMR));
      }

      if (this.cycles == 1) {
        if (this.is(CMIMirrorType.X90)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.X90));
        }

        if (this.is(CMIMirrorType.Y90)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.Y90));
        }

        if (this.is(CMIMirrorType.Z90)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.Z90));
        }

        if (this.is(CMIMirrorType.X90R)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.X90R));
        }

        if (this.is(CMIMirrorType.Y90R)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.Y90R));
        }

        if (this.is(CMIMirrorType.Z90R)) {
          hashSet.add(rotate90(vector, point, CMIMirrorType.Z90R));
        }

        if (this.is(CMIMirrorType.XS)) {
          hashSet.addAll(
              getPoints(
                  vector,
                  point,
                  this.get(CMIMirrorType.XS).getSections(),
                  CMIBlockMirroring.Axis.X));
        }

        if (this.is(CMIMirrorType.ZS)) {
          hashSet.addAll(
              getPoints(
                  vector,
                  point,
                  this.get(CMIMirrorType.ZS).getSections(),
                  CMIBlockMirroring.Axis.Z));
        }

        if (this.is(CMIMirrorType.YS)) {
          hashSet.addAll(
              getPoints(
                  vector,
                  point,
                  this.get(CMIMirrorType.YS).getSections(),
                  CMIBlockMirroring.Axis.Y));
        }
      }
    }

    points.addAll(hashSet);
    return points.size() != value ? this.getPointsRecursive(points) : points;
  }

  public boolean inRange(Location location) {
    return !this.getCenter().getWorld().getName().equalsIgnoreCase(location.getWorld().getName())
        ? false
        : this.getCenter().distance(location) < CMI.getInstance().getMirrorManager().getMaxRange();
  }

  private void cancel() {
    if (this.timer != null) {
      this.timer.cancel();
      this.timer = null;
    }
  }

  private void runTimer() {
    if (this.isPaused()) {
      this.cancel();
    } else {
      this.timer =
          CMIScheduler.runTimerAsync(
              CMI.getInstance(),
              () -> {
                if (this.isPaused()) {
                  this.cancel();
                } else if (this.player != null && this.player.isOnline()) {
                  RayTraceResult rayTraceResult = this.player.rayTraceBlocks(5.0);
                  if (rayTraceResult != null && rayTraceResult.getHitBlockFace() != null) {
                    Location location =
                        rayTraceResult
                            .getHitBlock()
                            .getLocation()
                            .clone()
                            .add(0.5, 0.5, 0.5)
                            .add(rayTraceResult.getHitBlockFace().getDirection());
                    Vector vector = location.toVector();
                    boolean flag = this.inRange(location);
                    var hashMap = this.calculatePointsWithFlip(vector);
                    CMIEffect cmiEffect = new CMIEffect(flag ? CMIParticle.CRIT : CMIParticle.DUST);
                    int value = 0;

                    for (Entry entry : hashMap.entrySet()) {
                      value++;
                      if (CMIPlayerConnection.getConnection(this.player) != null) {
                        cmiEffect.show(
                            CMIPlayerConnection.getConnection(this.player),
                            ((Vector) entry.getKey()).toLocation(this.player.getWorld()));
                      }

                      if (value + 1 >= CMI.getInstance().getMirrorManager().getMaxBlocks()) {
                        break;
                      }
                    }

                    if (CMIPlayerConnection.getConnection(this.player) != null) {
                      cmiEffect.show(
                          CMIPlayerConnection.getConnection(this.player),
                          vector.toLocation(this.player.getWorld()));
                    }

                    if (flag) {
                      CMIActionBar.send(
                          this.player,
                          CMILC.getIM(
                              mirror.class,
                              "blocksToPlace",
                              "[blocks]",
                              value + 1,
                              "[total]",
                              hashMap.size() + 1));
                    }

                    if (CMIPlayerConnection.getConnection(this.player) != null) {
                      this.centerEffect.show(
                          CMIPlayerConnection.getConnection(this.player), this.getCenter());
                    }
                  }
                } else {
                  this.setPaused(true);
                  this.cancel();
                }
              },
              1L,
              1L);
    }
  }

  private HashMap<Vector, CMIMirrorFlipInformation> calculatePointsWithFlip(Vector vector) {
    vector = vector.clone();
    vector.setX(vector.getBlockX() + 0.5);
    vector.setY(vector.getBlockY() + 0.5);
    vector.setZ(vector.getBlockZ() + 0.5);
    HashSet<Vector> hashSet = new HashSet();
    hashSet.add(vector);
    this.cycles = 0;
    Set points = this.getPointsRecursive(hashSet);
    HashMap hashMap = this.calculateFlipAngles(points, vector);
    hashMap.remove(vector);
    return hashMap;
  }

  private Set<Vector> calculatePoints(Vector vector) {
    vector = vector.clone();
    vector.setX(vector.getBlockX() + 0.5);
    vector.setY(vector.getBlockY() + 0.5);
    vector.setZ(vector.getBlockZ() + 0.5);
    HashSet<Vector> hashSet = new HashSet();
    hashSet.add(vector);
    this.cycles = 0;
    Set points = this.getPointsRecursive(hashSet);
    points.remove(vector);
    return points;
  }

  public CompletableFuture<HashMap<Vector, CMIMirrorFlipInformation>> getPointsWithFlipAsync(
      Vector vector) {
    return CompletableFuture.supplyAsync(() -> this.calculatePointsWithFlip(vector));
  }

  public CompletableFuture<Set<Vector>> getPointsAsync(Vector vector) {
    return CompletableFuture.supplyAsync(() -> this.calculatePoints(vector));
  }

  private static Vector getFlippedMirrorPosition(
      Vector vector, Vector point, CMIMirrorType cmiMirrorType) {
    Vector mirrorVector = cmiMirrorType.getVector();
    Vector offset = point.clone().subtract(vector);
    Vector normalizedMirror = mirrorVector.clone().normalize();
    double number = offset.dot(normalizedMirror);
    Vector projection = normalizedMirror.clone().multiply(number);
    Vector reflectionOffset = offset.subtract(projection).subtract(projection);
    Vector reflected = vector.clone().add(reflectionOffset);
    reflected.setX(reflected.getBlockX() + 0.5);
    reflected.setY(reflected.getBlockY() + 0.5);
    reflected.setZ(reflected.getBlockZ() + 0.5);
    return reflected;
  }

  private static Vector getFlippedPosition(
      Vector vector, Vector point, CMIMirrorType cmiMirrorType) {
    Vector mirrorVector =
        new Vector(
            vector.getX() - point.getX(),
            vector.getY() - point.getY(),
            vector.getZ() - point.getZ());
    mirrorVector.multiply(cmiMirrorType.getVector()).multiply(2);
    point = point.clone();
    point.setX(point.getBlockX() + 0.5);
    point.setY(point.getBlockY() + 0.5);
    point.setZ(point.getBlockZ() + 0.5);
    return point.clone().add(mirrorVector);
  }

  private static Vector rotate90(Vector vector, Vector point, CMIMirrorType cmiMirrorType) {
    Vector axisVector = cmiMirrorType.getVector().clone().normalize();
    Vector offset = point.clone().subtract(vector);
    double number = -Math.PI / 2;
    double cosAngle = Math.cos(number);
    Vector rotated =
        offset
            .clone()
            .multiply(cosAngle)
            .add(axisVector.clone().crossProduct(offset).multiply(Math.sin(number)))
            .add(axisVector.clone().multiply(axisVector.dot(offset) * (1.0 - cosAngle)));
    rotated.add(vector);
    return rotated;
  }

  private static Set<Vector> getPoints(
      Vector vector, Vector point, int value, CMIBlockMirroring.Axis axis) {
    HashSet<Vector> hashSet = new HashSet();
    point = point.clone();
    Vector axisComplement = new Vector(1, 1, 1).subtract(axis.getVector());
    double number =
        vector.clone().multiply(axisComplement).distance(point.clone().multiply(axisComplement));
    number = (int) (number * 10.0) / 10.0;
    double baseAngle;
    if (axis == CMIBlockMirroring.Axis.X) {
      baseAngle = Math.atan2(point.getZ() - vector.getZ(), point.getY() - vector.getY());
    } else if (axis == CMIBlockMirroring.Axis.Y) {
      baseAngle = Math.atan2(point.getZ() - vector.getZ(), point.getX() - vector.getX());
    } else {
      baseAngle = Math.atan2(point.getY() - vector.getY(), point.getX() - vector.getX());
    }

    double angleStep = (Math.PI * 2) / value;

    for (int stepIndex = 0; stepIndex < value; stepIndex++) {
      double angle = baseAngle + stepIndex * angleStep;
      double pointX = vector.getX();
      double pointY = vector.getY();
      double pointZ = vector.getZ();
      if (axis == CMIBlockMirroring.Axis.X) {
        pointY = vector.getY() + number * Math.cos(angle);
        pointZ = vector.getZ() + number * Math.sin(angle);
      } else if (axis == CMIBlockMirroring.Axis.Y) {
        pointX = vector.getX() + number * Math.cos(angle);
        pointZ = vector.getZ() + number * Math.sin(angle);
      } else if (axis == CMIBlockMirroring.Axis.Z) {
        pointX = vector.getX() + number * Math.cos(angle);
        pointY = vector.getY() + number * Math.sin(angle);
      }

      Vector pointVector = new Vector(pointX, pointY, pointZ);
      Vector scaledAxis = point.multiply(axis.getVector());
      if (scaledAxis.getX() != 0.0) {
        pointVector.setX(scaledAxis.getX());
      }

      if (scaledAxis.getY() != 0.0) {
        pointVector.setY(scaledAxis.getY());
      }

      if (scaledAxis.getZ() != 0.0) {
        pointVector.setZ(scaledAxis.getZ());
      }

      double epsilon = 0.1;
      Vector direction = vector.clone().subtract(pointVector).normalize();
      if (axis == CMIBlockMirroring.Axis.X) {
        pointVector.setY(pointVector.getY() + direction.getY() * epsilon);
        pointVector.setZ(pointVector.getZ() + direction.getZ() * epsilon);
      } else if (axis == CMIBlockMirroring.Axis.Y) {
        pointVector.setX(pointVector.getX() + direction.getX() * epsilon);
        pointVector.setZ(pointVector.getZ() + direction.getZ() * epsilon);
      } else if (axis == CMIBlockMirroring.Axis.Z) {
        pointVector.setX(pointVector.getX() + direction.getX() * epsilon);
        pointVector.setY(pointVector.getY() + direction.getY() * epsilon);
      }

      Vector pointCopy =
          new Vector(pointVector.getBlockX(), pointVector.getBlockY(), pointVector.getBlockZ());
      pointCopy.setX(pointCopy.getBlockX() + 0.5);
      pointCopy.setY(pointCopy.getBlockY() + 0.5);
      pointCopy.setZ(pointCopy.getBlockZ() + 0.5);
      hashSet.add(pointCopy);
    }

    return hashSet;
  }

  public CMIMIrrorFlipType getFlipType() {
    return this.flipType;
  }

  public void setFlipType(CMIMIrrorFlipType cmimIrrorFlipType) {
    this.flipType = cmimIrrorFlipType;
  }

  public enum Axis {
    X(1, 0, 0),
    Y(0, 1, 0),
    Z(0, 0, 1);

    Vector vector;

    Axis(int value, int secondComponent, int thirdComponent) {
      this.vector = new Vector(value, secondComponent, thirdComponent);
    }

    public Vector getVector() {
      return this.vector;
    }
  }
}
