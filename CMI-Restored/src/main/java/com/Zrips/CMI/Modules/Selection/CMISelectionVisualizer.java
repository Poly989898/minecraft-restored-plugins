package com.Zrips.CMI.Modules.Selection;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.events.CMISelectionVisualizationEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMICuboidArea;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Container.CMIVectorInt3D;
import net.Zrips.CMILib.Container.CMIWorld;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Items.CMIMaterial;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMISelectionVisualizer {
  private Player player;
  private UUID uuid;
  private CMISelectionPoint point1 = new CMISelectionPoint();
  private CMISelectionPoint point2 = new CMISelectionPoint();
  private CMICuboidArea selectedCuboid = new CMICuboidArea(null);
  private Visualizer visualizer = null;
  private static long visualizerShowFor = 30000L;
  int place = 0;
  private CMITask task = null;
  private static final int RANGE = 16;

  public CMISelectionVisualizer(UUID playerUuid) {
    this.uuid = playerUuid;
    this.player = Bukkit.getPlayer(playerUuid);
    this.visualizer = new Visualizer(this.player);
    if (this.player != null && this.player.isOnline()) {
      this.selectedCuboid = new CMICuboidArea(this.player.getWorld());
    }
  }

  private Player getPlayer() {
    if (this.player != null && this.player.isOnline()) {
      return this.player;
    }

    this.player = Bukkit.getPlayer(this.uuid);
    return this.player;
  }

  public void cancelAll() {
    this.destroyOldPoint1();
    this.destroyOldPoint2();
    this.getVisualizer().cancelAll();
  }

  private void destroyOldPoint1() {
    if (this.player != null && this.player.isOnline()) {
      this.point1.getSelectionVisualBlock().destroy(this.player);
    }
  }

  private void destroyOldPoint2() {
    if (this.player != null && this.player.isOnline()) {
      this.point2.getSelectionVisualBlock().destroy(this.player);
    }
  }

  public CMISelectionPoint getPoint1() {
    return this.point1;
  }

  public CMISelectionVisualizer setPoint1(Location location) {
    return this.setPoint1(location.getWorld(), new CMIVector3D(location.toVector()));
  }

  public CMISelectionVisualizer setPoint1(World world, CMIVector3D cmiVector3D) {
    return this.setPoint1(new CMISelectionPoint(world, cmiVector3D));
  }

  public CMISelectionVisualizer setPoint1(World world, CMIVectorInt3D cmiVectorInt3D) {
    return this.setPoint1(new CMISelectionPoint(world, cmiVectorInt3D));
  }

  public CMISelectionVisualizer setPoint1(CMISelectionPoint cmiSelectionPoint) {
    if (this.point1.getPoint() != null
        && cmiSelectionPoint != null
        && !this.point1.getPoint().toString().equals(cmiSelectionPoint.getPoint().toString())) {
      this.destroyOldPoint1();
    } else if (this.point1.getPoint() != null) {
      return this;
    }

    this.point1 = cmiSelectionPoint;
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
      this.getPoint1().getSelectionVisualBlock().setMaterial(CMIMaterial.GREEN_WOOL);
    }

    this.recheckCuboid();
    return this;
  }

  public boolean placedBothPoints() {
    return this.point1.isValid()
        && this.point2.isValid()
        && Objects.equals(this.point1.getSelectionWorld(), this.point2.getSelectionWorld());
  }

  private void recheckCuboid() {
    if (this.placedBothPoints()) {
      this.selectedCuboid = new CMICuboidArea(this.point1.toLocation(), this.point2.toLocation());
    }
  }

  public CMISelectionPoint getPoint2() {
    return this.point2;
  }

  public CMISelectionVisualizer setPoint2(Location location) {
    return this.setPoint2(location.getWorld(), new CMIVector3D(location.toVector()));
  }

  public CMISelectionVisualizer setPoint2(World world, CMIVector3D cmiVector3D) {
    return this.setPoint2(new CMISelectionPoint(world, cmiVector3D));
  }

  public CMISelectionVisualizer setPoint2(World world, CMIVectorInt3D cmiVectorInt3D) {
    return this.setPoint2(new CMISelectionPoint(world, cmiVectorInt3D));
  }

  public CMISelectionVisualizer setPoint2(CMISelectionPoint cmiSelectionPoint) {
    if (this.point2.getPoint() != null
        && cmiSelectionPoint != null
        && !this.point2.getPoint().toString().equals(cmiSelectionPoint.getPoint().toString())) {
      this.destroyOldPoint2();
    } else if (this.point2.getPoint() != null) {
      return this;
    }

    this.point2 = cmiSelectionPoint;
    if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
      this.getPoint2().getSelectionVisualBlock().setMaterial(CMIMaterial.BLUE_WOOL);
    }

    this.recheckCuboid();
    return this;
  }

  public CMICuboidArea getSelectedCuboid() {
    return this.selectedCuboid;
  }

  public CMISelectionVisualizer setSelectedCuboid(CMICuboidArea cmiCuboidArea) {
    this.selectedCuboid = cmiCuboidArea;
    return this;
  }

  public Visualizer getVisualizer() {
    return this.visualizer;
  }

  public void updateParticles(boolean flag) {
    if (this.placedBothPoints()) {
      this.getVisualizer().setStart(System.currentTimeMillis());
      this.getVisualizer().cancelAll();
      if (flag) {
        this.getVisualizer().setLoc(null);
      }

      this.getVisualizer().setAreas(this.getSelectedCuboid());
      this.showParticleBounds();
    }
  }

  public void showFull() {
    this.showSelectionPoints();
    this.updateParticles(true);
  }

  public void showSelectionPoints() {
    if (this.player != null && this.player.isOnline()) {
      if (Version.isCurrentEqualOrHigher(Version.v1_19_R3)) {
        if (this.point1.isValid()) {
          this.point1.showSelectionVisualBlock(this.player);
        }

        if (this.point2.isValid()) {
          this.point2.showSelectionVisualBlock(this.player);
        }

        this.rerunAutoRemoval();
      }
    }
  }

  private void rerunAutoRemoval() {
    if (this.task != null) {
      this.task.cancel();
    }

    this.task =
        CMIScheduler.runTaskLater(
            CMI.getInstance(),
            () -> {
              this.destroyOldPoint1();
              this.destroyOldPoint2();
              this.task = null;
            },
            visualizerShowFor / 50L);
  }

  public void showParticleBounds() {
    if (this.getVisualizer() != null) {
      this.getVisualizer().cancelAll();
    }

    if (this.placedBothPoints()) {
      CMISelectionVisualizationEvent cmiSelectionVisualizationEvent =
          new CMISelectionVisualizationEvent(this.player, this.getVisualizer());
      Bukkit.getPluginManager().callEvent(cmiSelectionVisualizationEvent);
      if (!cmiSelectionVisualizationEvent.isCancelled()) {
        if (this.getVisualizer().isOnce()) {
          this.makeBorders();
        } else {
          this.getVisualizer()
              .setTask(
                  CMIScheduler.runTimerAsync(
                      CMI.getInstance(),
                      () -> {
                        if (!this.makeBorders() || this.getVisualizer().getAreas().isEmpty()) {
                          this.getVisualizer().cancelAll();
                        }
                      },
                      1L,
                      1L));
        }
      }
    }
  }

  public boolean makeBorders() {
    if (this.getVisualizer() == null) {
      return false;
    }

    if (!this.placedBothPoints()) {
      return false;
    }

    var list = this.getVisualizer().getAreas();
    Location location = this.player.getLocation();
    ArrayList arrayList = new ArrayList();
    ArrayList points = new ArrayList();
    boolean flag = this.getVisualizer().isSameLoc();
    if (!flag) {
      for (CMICuboidArea cmiCuboidArea : list) {
        if (cmiCuboidArea != null) {
          CMICuboidArea areaCopy = cmiCuboidArea.clone();
          areaCopy.getHighPoint().add(new Vector(1, 1, 1));
          SelectionSides selectionSides = new SelectionSides();
          double number = location.getBlockX() - 16;
          double minZ = location.getBlockZ() - 16;
          double minY = location.getBlockY() - 16;
          double maxX = location.getBlockX() + 16;
          double maxZ = location.getBlockZ() + 16;
          double maxY = location.getBlockY() + 16;
          if (areaCopy.getLowPoint().getBlockX() < number) {
            areaCopy.getLowPoint().setX(number);
            selectionSides.setWestSide(false);
          }

          if (areaCopy.getHighPoint().getBlockX() > maxX) {
            areaCopy.getHighPoint().setX(maxX);
            selectionSides.setEastSide(false);
          }

          if (areaCopy.getLowPoint().getBlockZ() < minZ) {
            areaCopy.getLowPoint().setZ(minZ);
            selectionSides.setNorthSide(false);
          }

          if (areaCopy.getHighPoint().getBlockZ() > maxZ) {
            areaCopy.getHighPoint().setZ(maxZ);
            selectionSides.setSouthSide(false);
          }

          if (areaCopy.getLowPoint().getBlockY() < minY) {
            areaCopy.getLowPoint().setY(minY);
            selectionSides.setBottomSide(false);
          }

          if (areaCopy.getHighPoint().getBlockY() > maxY) {
            areaCopy.getHighPoint().setY(maxY);
            selectionSides.setTopSide(false);
          }

          double sizeX = areaCopy.getXSize() - 1;
          double sizeY = areaCopy.getYSize() - 1;
          double sizeZ = areaCopy.getZSize() - 1;
          arrayList.addAll(
              this.getLocationsWallsByData(
                  new CMIVector3D(location.toVector()),
                  new CMIVector3D(sizeX, sizeY, sizeZ),
                  new CMIVector3D(areaCopy.getLowPoint()),
                  selectionSides));
          points.addAll(
              this.getLocationsCornersByData(
                  new CMIVector3D(location.toVector()),
                  new CMIVector3D(sizeX, sizeY, sizeZ),
                  new CMIVector3D(areaCopy.getLowPoint()),
                  selectionSides));
        }
      }

      this.getVisualizer().setLoc(this.player.getLocation());
    } else {
      arrayList.addAll(this.getVisualizer().getLocations());
      points.addAll(this.getVisualizer().getLocations2());
    }

    CMIEffect cmiEffect = new CMIEffect(CMIParticle.DUST);
    cmiEffect.setAmount(1);
    cmiEffect.setOffset(new Vector(0, 0, 0));
    cmiEffect.setSpeed(0.0F);
    cmiEffect.setColorFrom(Color.fromRGB(150, 200, 100));
    CMIEffect effect = new CMIEffect(CMIParticle.DUST);
    effect.setAmount(1);
    effect.setOffset(new Vector(0, 0, 0));
    effect.setSpeed(0.0F);
    effect.setColorFrom(Color.fromRGB(255, 255, 255));
    byte b = 20;

    try {
      for (int pointIndex = this.place; pointIndex < arrayList.size(); pointIndex += b) {
        CMIVector3D cmiVector3D = (CMIVector3D) arrayList.get(pointIndex);
        cmiEffect.show(this.player, cmiVector3D.toLocation(this.player.getWorld()));
      }

      for (int secondIndex = this.place; secondIndex < points.size(); secondIndex += b) {
        CMIVector3D point = (CMIVector3D) points.get(secondIndex);
        effect.show(this.player, point.toLocation(this.player.getWorld()));
      }
    } catch (Exception exception) {
      exception.printStackTrace();
      return false;
    }

    this.place++;
    if (this.place > b) {
      this.place = 0;
    }

    if (!flag) {
      this.getVisualizer().setLocations(arrayList);
      this.getVisualizer().setLocations2(points);
    }

    return this.getVisualizer().getStart() + visualizerShowFor >= System.currentTimeMillis();
  }

  public static List<CMIVector3D> getLocations(
      Location location, Location secondLocation, Vector vector, boolean flag) {
    ArrayList arrayList = new ArrayList();
    return location.getWorld() != secondLocation.getWorld()
        ? arrayList
        : getLocations(
            new CMIVector3D(location.toVector()),
            new CMIVector3D(secondLocation.toVector()),
            new CMIVector3D(vector),
            flag);
  }

  private static double distance(CMIVector3D cmiVector3D, CMIVector3D other) {
    double number = other.getX() - cmiVector3D.getX();
    double dy = other.getY() - cmiVector3D.getY();
    double dz = other.getZ() - cmiVector3D.getZ();
    return Math.sqrt(number * number + dy * dy + dz * dz);
  }

  private static double distanceSquared(CMIVector3D cmiVector3D, CMIVector3D other) {
    double number = other.getX() - cmiVector3D.getX();
    double dy = other.getY() - cmiVector3D.getY();
    double dz = other.getZ() - cmiVector3D.getZ();
    return number * number + dy * dy + dz * dz;
  }

  public static List<CMIVector3D> getLocations(
      CMIVector3D cmiVector3D, CMIVector3D minPoint, CMIVector3D maxPoint, boolean flag) {
    double number = 1.0;
    double step = 1.0;
    if (maxPoint.getX() == 0.0) {
      maxPoint.setX(number + number * 0.1);
    }

    if (maxPoint.getY() == 0.0) {
      maxPoint.setY(step + step * 0.1);
    }

    if (maxPoint.getZ() == 0.0) {
      maxPoint.setZ(number + number * 0.1);
    }

    double startX = number;
    double startY = step;
    if (flag) {
      startX = 0.0;
      startY = 0.0;
    }

    ArrayList<CMIVector3D> arrayList = new ArrayList();

    for (double x = startX; x < maxPoint.getX(); x += number) {
      CMIVector3D sample =
          new CMIVector3D(cmiVector3D.getX(), cmiVector3D.getY(), cmiVector3D.getZ());
      if (maxPoint.getX() > number + number * 0.1) {
        sample.plus(x, 0.0, 0.0);
      }

      for (double y = startY; y < maxPoint.getY(); y += step) {
        CMIVector3D sampleY = new CMIVector3D(sample.getX(), sample.getY(), sample.getZ());
        if (maxPoint.getY() > step + step * 0.1) {
          sampleY.plus(0.0, y, 0.0);
        }

        for (double z = startX; z < maxPoint.getZ(); z += number) {
          CMIVector3D sampleZ = new CMIVector3D(sampleY.getX(), sampleY.getY(), sampleY.getZ());
          if (maxPoint.getZ() > number + number * 0.1) {
            sampleZ.plus(0.0, 0.0, z);
          }

          double sampleDistance = distance(minPoint, sampleZ);
          if (sampleDistance < 16.0) {
            arrayList.add(new CMIVector3D(sampleZ.getX(), sampleZ.getY(), sampleZ.getZ()));
          }
        }
      }
    }

    return arrayList;
  }

  private static CMIVector3D clone(CMIVector3D cmiVector3D) {
    return new CMIVector3D(cmiVector3D.getX(), cmiVector3D.getY(), cmiVector3D.getZ());
  }

  private List<CMIVector3D> getLocationsWallsByData(
      CMIVector3D cmiVector3D,
      CMIVector3D pointA,
      CMIVector3D pointB,
      SelectionSides selectionSides) {
    ArrayList arrayList = new ArrayList();
    if (selectionSides.ShowNorthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), pointA.getY(), 0.0),
              false));
    }

    if (selectionSides.ShowSouthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, 0.0, pointA.getZ()),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), pointA.getY(), 0.0),
              false));
    }

    if (selectionSides.ShowWestSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY(), pointA.getZ()),
              false));
    }

    if (selectionSides.ShowEastSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(pointA.getX(), 0.0, 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY(), pointA.getZ()),
              false));
    }

    if (selectionSides.ShowTopSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, pointA.getY(), 0.0),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), 0.0, pointA.getZ()),
              false));
    }

    if (selectionSides.ShowBottomSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), 0.0, pointA.getZ()),
              false));
    }

    return arrayList;
  }

  private List<CMIVector3D> getLocationsCornersByData(
      CMIVector3D cmiVector3D,
      CMIVector3D pointA,
      CMIVector3D pointB,
      SelectionSides selectionSides) {
    ArrayList arrayList = new ArrayList();
    if (selectionSides.ShowBottomSide() && selectionSides.ShowNorthSide()) {
      arrayList.addAll(
          getLocations(clone(pointB), cmiVector3D, new CMIVector3D(pointA.getX(), 0.0, 0.0), true));
    }

    if (selectionSides.ShowTopSide() && selectionSides.ShowNorthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, pointA.getY(), 0.0),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), 0.0, 0.0),
              true));
    }

    if (selectionSides.ShowBottomSide() && selectionSides.ShowSouthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, 0.0, pointA.getZ()),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), 0.0, 0.0),
              true));
    }

    if (selectionSides.ShowTopSide() && selectionSides.ShowSouthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, pointA.getY(), pointA.getZ()),
              cmiVector3D,
              new CMIVector3D(pointA.getX(), 0.0, 0.0),
              true));
    }

    if (selectionSides.ShowWestSide() && selectionSides.ShowNorthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, 0.0, 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY(), 0.0),
              true));
    }

    if (selectionSides.ShowEastSide() && selectionSides.ShowNorthSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(pointA.getX(), 0.0, 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY(), 0.0),
              true));
    }

    if (selectionSides.ShowSouthSide() && selectionSides.ShowWestSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, 0.0, pointA.getZ()),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY(), 0.0),
              true));
    }

    if (selectionSides.ShowSouthSide() && selectionSides.ShowEastSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(pointA.getX(), 0.0, pointA.getZ()),
              cmiVector3D,
              new CMIVector3D(0.0, pointA.getY() + 1.0, 0.0),
              true));
    }

    if (selectionSides.ShowWestSide() && selectionSides.ShowBottomSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, 0.0, 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, 0.0, pointA.getZ()),
              true));
    }

    if (selectionSides.ShowEastSide() && selectionSides.ShowBottomSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(pointA.getX(), 0.0, 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, 0.0, pointA.getZ()),
              true));
    }

    if (selectionSides.ShowWestSide() && selectionSides.ShowTopSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(0.0, pointA.getY(), 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, 0.0, pointA.getZ()),
              true));
    }

    if (selectionSides.ShowEastSide() && selectionSides.ShowTopSide()) {
      arrayList.addAll(
          getLocations(
              clone(pointB).plus(pointA.getX(), pointA.getY(), 0.0),
              cmiVector3D,
              new CMIVector3D(0.0, 0.0, pointA.getZ()),
              true));
    }

    return arrayList;
  }

  public void contract(int amount) {
    this.localContract(amount);
    this.resetSelectionPoints();
  }

  public void expand(int amount) {
    this.localExpand(amount);
    this.resetSelectionPoints();
  }

  public void shift(int amount) {
    this.localShift(amount);
    this.resetSelectionPoints();
  }

  private void resetSelectionPoints() {
    this.getPoint1().getSelectionVisualBlock().destroy(this.player);
    this.getPoint1().getSelectionVisualBlock().show(this.player, this.getPoint1().getPoint());
    this.getPoint2().getSelectionVisualBlock().destroy(this.player);
    this.getPoint2().getSelectionVisualBlock().show(this.player, this.getPoint2().getPoint());
  }

  private boolean localShift(double number) {
    if (!this.placedBothPoints()) {
      return false;
    }

    CMISelectionVisualizer.Direction direction = getDirection(this.getPlayer());
    if (direction == null) {
      return false;
    }

    int maxY = CMIWorld.getMaxHeight(this.getPlayer().getWorld()) - 1;
    int minY = CMIWorld.getMinHeight(this.getPlayer().getWorld());
    switch (direction) {
      case UP:
        if (this.getPoint1().isValid()) {
          CMIVector3D cmiVector3D = this.getPoint1().getPoint();
          cmiVector3D.setY(CMINumber.clamp(cmiVector3D.getY() + number, minY, maxY));
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperA = this.getPoint2().getPoint();
          upperA.setY(CMINumber.clamp(upperA.getY() + number, minY, maxY));
        }
        break;
      case DOWN:
        if (this.getPoint1().isValid()) {
          CMIVector3D lowerA = this.getPoint1().getPoint();
          lowerA.setY(CMINumber.clamp(lowerA.getY() - number, minY, maxY));
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperB = this.getPoint2().getPoint();
          upperB.setY(CMINumber.clamp(upperB.getY() - number, minY, maxY));
        }
        break;
      case PLUSX:
        if (this.getPoint1().isValid()) {
          CMIVector3D lowerB = this.getPoint1().getPoint();
          lowerB.setX(lowerB.getX() + number);
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperC = this.getPoint2().getPoint();
          upperC.setX(upperC.getX() + number);
        }
        break;
      case PLUSZ:
        if (this.getPoint1().isValid()) {
          CMIVector3D lowerC = this.getPoint1().getPoint();
          lowerC.setZ(lowerC.getZ() + number);
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperD = this.getPoint2().getPoint();
          upperD.setZ(upperD.getZ() + number);
        }
        break;
      case MINUSX:
        if (this.getPoint1().isValid()) {
          CMIVector3D lowerD = this.getPoint1().getPoint();
          lowerD.setX(lowerD.getX() - number);
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperE = this.getPoint2().getPoint();
          upperE.setX(upperE.getX() - number);
        }
        break;
      case MINUSZ:
        if (this.getPoint1().isValid()) {
          CMIVector3D lowerE = this.getPoint1().getPoint();
          lowerE.setZ(lowerE.getZ() - number);
        }

        if (this.getPoint2().isValid()) {
          CMIVector3D upperF = this.getPoint2().getPoint();
          upperF.setZ(upperF.getZ() - number);
        }
    }

    this.recheckCuboid();
    return true;
  }

  private static CMIVector3D pick(
      CMIVector3D cmiVector3D, CMIVector3D vector, CMISelectionVisualizer.Axis axis, boolean flag) {
    if (cmiVector3D == null && vector == null) {
      return null;
    }

    if (cmiVector3D == null) {
      return vector;
    }

    if (vector == null) {
      return cmiVector3D;
    }

    double number = value(cmiVector3D, axis);
    double axisValue = value(vector, axis);
    return flag
        ? (number <= axisValue ? cmiVector3D : vector)
        : (number >= axisValue ? cmiVector3D : vector);
  }

  private static double value(CMIVector3D cmiVector3D, CMISelectionVisualizer.Axis axis) {
    switch (axis) {
      case X:
        return cmiVector3D.getX();
      case Y:
        return cmiVector3D.getY();
      case Z:
        return cmiVector3D.getZ();
      default:
        throw new IllegalStateException();
    }
  }

  public boolean localExpand(double number) {
    if (!this.placedBothPoints()) {
      return false;
    }

    CMISelectionVisualizer.Direction direction = getDirection(this.getPlayer());
    if (direction == null) {
      return false;
    }

    int maxY = CMIWorld.getMaxHeight(this.getPlayer().getWorld()) - 1;
    int minY = CMIWorld.getMinHeight(this.getPlayer().getWorld());
    switch (direction) {
      case UP:
        CMIVector3D cmiVector3D =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Y,
                false);
        if (cmiVector3D != null) {
          cmiVector3D.setY(CMINumber.clamp(cmiVector3D.getY() + number, minY, maxY));
        }
        break;
      case DOWN:
        CMIVector3D upperA =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Y,
                true);
        if (upperA != null) {
          upperA.setY(CMINumber.clamp(upperA.getY() - number, minY, maxY));
        }
        break;
      case PLUSX:
        CMIVector3D lowerA =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.X,
                false);
        if (lowerA != null) {
          lowerA.setX(lowerA.getX() + number);
        }
        break;
      case PLUSZ:
        CMIVector3D upperB =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Z,
                false);
        if (upperB != null) {
          upperB.setZ(upperB.getZ() + number);
        }
        break;
      case MINUSX:
        CMIVector3D lowerB =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.X,
                true);
        if (lowerB != null) {
          lowerB.setX(lowerB.getX() - number);
        }
        break;
      case MINUSZ:
        CMIVector3D upperC =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Z,
                true);
        if (upperC != null) {
          upperC.setZ(upperC.getZ() - number);
        }
    }

    this.recheckCuboid();
    return true;
  }

  public boolean localContract(double number) {
    if (!this.placedBothPoints()) {
      return false;
    }

    CMISelectionVisualizer.Direction direction = getDirection(this.getPlayer());
    if (direction == null) {
      return false;
    }

    int maxY = CMIWorld.getMaxHeight(this.getPlayer().getWorld()) - 1;
    int minY = CMIWorld.getMinHeight(this.getPlayer().getWorld());
    switch (direction) {
      case UP:
        CMIVector3D cmiVector3D =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Y,
                true);
        if (cmiVector3D != null) {
          cmiVector3D.setY(CMINumber.clamp(cmiVector3D.getY() + number, minY, maxY));
        }
        break;
      case DOWN:
        CMIVector3D upperD =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Y,
                true);
        if (upperD != null) {
          upperD.setY(CMINumber.clamp(upperD.getY() - number, minY, maxY));
        }
        break;
      case PLUSX:
        CMIVector3D lowerD =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.X,
                true);
        if (lowerD != null) {
          lowerD.setX(lowerD.getX() + number);
        }
        break;
      case PLUSZ:
        CMIVector3D upperE =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Z,
                true);
        if (upperE != null) {
          upperE.setZ(upperE.getZ() + number);
        }
        break;
      case MINUSX:
        CMIVector3D lowerE =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.X,
                false);
        if (lowerE != null) {
          lowerE.setX(lowerE.getX() - number);
        }
        break;
      case MINUSZ:
        CMIVector3D upperF =
            pick(
                this.getPoint1().getPoint(),
                this.getPoint2().getPoint(),
                CMISelectionVisualizer.Axis.Z,
                false);
        if (upperF != null) {
          upperF.setZ(upperF.getZ() - number);
        }
    }

    this.recheckCuboid();
    return true;
  }

  private static CMISelectionVisualizer.Direction getDirection(Player player) {
    int yawDegrees = (int) player.getLocation().getYaw();
    if (yawDegrees < 0) {
      yawDegrees += 360;
    }

    yawDegrees += 45;
    yawDegrees %= 360;
    int directionIndex = yawDegrees / 90;
    float floatValue = player.getLocation().getPitch();
    if (floatValue < -50.0F) {
      return CMISelectionVisualizer.Direction.UP;
    } else if (floatValue > 50.0F) {
      return CMISelectionVisualizer.Direction.DOWN;
    } else if (directionIndex == 1) {
      return CMISelectionVisualizer.Direction.MINUSX;
    } else if (directionIndex == 3) {
      return CMISelectionVisualizer.Direction.PLUSX;
    } else if (directionIndex == 2) {
      return CMISelectionVisualizer.Direction.MINUSZ;
    } else {
      return directionIndex == 0 ? CMISelectionVisualizer.Direction.PLUSZ : null;
    }
  }

  enum Axis {
    X,
    Y,
    Z;
  }

  private enum Direction {
    UP,
    DOWN,
    PLUSX,
    PLUSZ,
    MINUSX,
    MINUSZ;
  }
}
