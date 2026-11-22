package com.Zrips.CMI.Modules.Holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Display.CMIInteractionEntity;
import com.Zrips.CMI.Modules.Holograms.Events.CMIHologramStartHoverEvent;
import com.Zrips.CMI.Modules.Holograms.Events.CMIHologramStopHoverEvent;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramSettings;
import com.Zrips.CMI.Modules.Holograms.Settings.CMIHologramTextSettings;
import com.Zrips.CMI.Modules.Packets.FakeInfo;
import com.Zrips.CMI.Modules.Packets.PacketHandler;
import com.Zrips.CMI.commands.list.util;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.Container.CMILocation;
import net.Zrips.CMILib.Container.CMINumber;
import net.Zrips.CMILib.Container.CMIPlane;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import net.Zrips.CMILib.Effects.CMIEffect;
import net.Zrips.CMILib.Effects.CMIEffectManager.CMIParticle;
import net.Zrips.CMILib.Effects.CMIParticleTrail;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CMIHologramHoverModule {
  private final CMIHologram hologramBase;

  public CMIHologramHoverModule(CMIHologram cmiHologram) {
    this.hologramBase = cmiHologram;
  }

  public CompletableFuture<Void> checkHoverPosition(UUID uuid) {
    return this.checkHoverPosition(uuid, false);
  }

  private void showCorners(Player player, CMIPlane cmiPlane, Vector vector) {
    Vector normal = vector.clone().normalize();
    Vector helper = Math.abs(normal.getY()) > 0.99 ? new Vector(1, 0, 0) : new Vector(0, 1, 0);
    Vector right = normal.clone().crossProduct(helper).normalize();
    Vector up = right.clone().crossProduct(normal).normalize();
    Vector center = cmiPlane.getCenterPoint().toVector();
    double number = cmiPlane.getSizeMin().getX();
    double minY = cmiPlane.getSizeMin().getY();
    double maxX = cmiPlane.getSizeMax().getX();
    double maxY = cmiPlane.getSizeMax().getY();
    double centerX = (number + maxX) * 0.5;
    double centerY = (minY + maxY) * 0.5;
    Vector rightHalfMin = right.clone().multiply(number);
    Vector rightMax = right.clone().multiply(maxX);
    Vector rightCenter = right.clone().multiply(centerX);
    Vector upMin = up.clone().multiply(minY);
    Vector upMax = up.clone().multiply(maxY);
    Vector upCenter = up.clone().multiply(centerY);
    Vector[] vectors =
        new Vector[] {
          center.clone().add(rightHalfMin).add(upMin),
          center.clone().add(rightHalfMin).add(upMax),
          center.clone().add(rightMax).add(upMin),
          center.clone().add(rightMax).add(upMax),
          center.clone().add(rightCenter).add(upMin),
          center.clone().add(rightCenter).add(upMax),
          center.clone().add(rightHalfMin).add(upCenter),
          center.clone().add(rightMax).add(upCenter)
        };
    CMIEffect cmiEffect = null;
    if (Version.isCurrentEqualOrHigher(Version.v1_13_R1)) {
      cmiEffect = new CMIEffect(CMIParticle.TRAIL);
      CMIParticleTrail cmiParticleTrail = (CMIParticleTrail) cmiEffect.getOptions();
      cmiParticleTrail.setColor(Color.fromBGR(250, 50, 50));
      cmiParticleTrail.setDuration(1);
    } else {
      cmiEffect = new CMIEffect(CMIParticle.DUST);
    }

    Vector[] pointsCopy = vectors;
    int value = vectors.length;

    for (int pointIndex = 0; pointIndex < value; pointIndex++) {
      Vector point = pointsCopy[pointIndex];
      cmiEffect.show(player, point.toLocation(player.getWorld()));
    }
  }

  public CompletableFuture<Void> checkHoverPosition(UUID uuid, boolean flag) {
    CompletableFuture completableFuture = new CompletableFuture();

    try {
      Player player = CMIUser.getOnlinePlayer(uuid);
      if (this.hologramBase.isEnabled() && player != null && player.isOnline()) {
        CMIHologramUserData cmiHologramUserData = this.hologramBase.getData(player);
        CMIHologramType cmiHologramType = this.hologramBase.getHologramType(uuid);
        CMIHologramPage cmiHologramPage =
            this.hologramBase.getPages().getPage(cmiHologramUserData.getPage());
        if (cmiHologramPage == null) {
          cmiHologramPage = this.hologramBase.getPages().getPage(1);
        }

        CMIHologramPage pageCopy = cmiHologramPage;
        CMIHologramHoverData cmiHologramHoverData = CMIHologramHoverData.getData(player);
        if (pageCopy == null) {
          completableFuture.complete(null);
          return completableFuture;
        }

        CMIHologramSettings cmiHologramSettings = this.hologramBase.getSettings();
        CMIHologramTextSettings cmiHologramTextSettings = this.hologramBase.getTextSettings();
        CMIHologramInteraction cmiHologramInteraction = this.hologramBase.getInteractionSettings();
        double number = pageCopy.getHeight();
        CMILocation cmiLocation = this.hologramBase.getLocation().clone();
        if (!flag
            && CMI.getInstance().getUtilManager().getDistance(player.getEyeLocation(), cmiLocation)
                > cmiHologramSettings.getUpdateRange()) {
          if (cmiHologramHoverData.getHoveringLine() > 0) {
            this.hologramBase.requestUpdate(uuid);
          }

          CMIAiming cmiAiming = CMIAiming.getAimAt(player.getUniqueId());
          if (cmiAiming != null && cmiAiming.getAimAt().equals(this.hologramBase)) {
            CMIAiming.removeAimAt(player.getUniqueId());
          }

          completableFuture.complete(null);
          return completableFuture;
        } else {
          int value = pageCopy.getLineCount();
          CMIVector3D cmiVector3D = cmiHologramSettings.getOffset().clone();
          double halfHeight = cmiHologramInteraction.getDimensions().getY() / 2.0;
          double halfWidth = cmiHologramInteraction.getDimensions().getX() / 2.0;
          CMIVector2D cmiVector2D =
              new CMIVector2D(
                  -(pageCopy.getWidth() / 78.0) * cmiHologramSettings.getScale().getX()
                      - halfWidth
                      - cmiVector3D.getX(),
                  cmiVector3D.getY());
          CMIVector2D localPoint =
              new CMIVector2D(
                  pageCopy.getWidth() / 78.0 * cmiHologramSettings.getScale().getX()
                      + halfWidth
                      - cmiVector3D.getX(),
                  value * this.hologramBase.getLineHeight() + halfHeight + cmiVector3D.getY());
          double yaw = cmiHologramSettings.getBillboard().getPlanePitch(player, this.hologramBase);
          double pitch = cmiHologramSettings.getBillboard().getPlaneYaw(player, this.hologramBase);
          if (this.hologramBase.getAnimationSettings().getAutoRotate() != 0) {
            double roll =
                cmiHologramUserData.getCurrentAutoRotateExtraAngle(
                    this.hologramBase.getAnimationSettings().getAutoRotate());
            roll = Math.toRadians(roll);
            pitch += roll;
          }

          double depthOffset = -this.hologramBase.getBoardSettings().getExtraDepth() / 2.0;
          if (cmiHologramSettings.getBillboard() == CMIBillboard.FIXED
              || cmiHologramSettings.getBillboard() == CMIBillboard.HORIZONTAL) {
            depthOffset = -depthOffset;
          }

          cmiVector3D = cmiVector3D.add(cmiHologramInteraction.getOffset());
          double cosPitch = Math.cos(pitch);
          double sinPitch = Math.sin(pitch);
          double cosYaw = Math.cos(yaw);
          double sinYaw = Math.sin(yaw);
          double rotA = -sinPitch * cosYaw;
          double rotB = -sinYaw;
          double rotC = cosPitch * cosYaw;
          double rotD = cosPitch;
          double rotE = 0.0;
          double rotF = sinPitch;
          double rotG = sinPitch * sinYaw;
          double rotH = cosYaw;
          double rotI = -cosPitch * sinYaw;
          depthOffset += cmiVector3D.getZ();
          double pointX = cmiVector3D.getX();
          double pointY = cmiVector3D.getY() - halfHeight / 2.0;
          double projX = rotA * depthOffset + rotD * pointX + rotG * pointY;
          double projY = rotB * depthOffset + rotE * pointX + rotH * pointY;
          double projZ = rotC * depthOffset + rotF * pointX + rotI * pointY;
          cmiLocation.add(projX, projY, projZ);
          if (!CMI.getInstance()
              .getUtilManager()
              .lookingAt(player, cmiLocation.toVector(), 100.0, 100.0)) {
            if (cmiHologramHoverData.getHoveringLine() > 0) {
              this.hologramBase.requestUpdate(uuid);
            }

            CMIAiming aiming = CMIAiming.getAimAt(player);
            if (aiming != null
                && aiming.getAimAt() != null
                && this.hologramBase.equals(aiming.getAimAt())) {
              this.callStopHoverEvent(player);
              CMIAiming.removeAimAt(player);
            }

            completableFuture.complete(null);
            return completableFuture;
          } else {
            CMIPlane cmiPlane =
                new CMIPlane(
                    cmiLocation.clone().toVector(), cmiVector2D.clone(), localPoint.clone());
            Vector vector = player.getLocation().getDirection().clone();
            Vector eyePos = player.getEyeLocation().toVector().clone();
            Vector lookPoint = vector.clone();
            if (cmiHologramType == CMIHologramType.TextDisplay) {
              double rotProjX = cosYaw * Math.sin(pitch);
              double rotProjY = Math.sin(yaw);
              double rotProjZ = -cosYaw * Math.cos(pitch);
              lookPoint.setX(rotProjX);
              lookPoint.setY(rotProjY);
              lookPoint.setZ(rotProjZ);
              double halfDepth = this.hologramBase.getBoardSettings().getExtraDepth() / 2.0;
              cmiPlane.setCenter(
                  cmiPlane.getCenter().add(lookPoint.clone().normalize().multiply(-halfDepth)));
            } else {
              CMIVector3D hitPoint =
                  this.hologramBase
                      .getSettings()
                      .getBillboard()
                      .getPlaneDirection(player, this.hologramBase);
              lookPoint.setX(hitPoint.getX());
              lookPoint.setY(hitPoint.getY());
              lookPoint.setZ(hitPoint.getZ());
            }

            if (cmiHologramHoverData.getShowCornersUntil() > System.currentTimeMillis()) {
              this.showCorners(player, cmiPlane, lookPoint);
            }

            double planeWidth =
                Math.abs(cmiPlane.getSizeMax().getX() - cmiPlane.getSizeMin().getX());
            var intersection = CMIPlaneIntersection.get(vector, eyePos, lookPoint, cmiPlane);
            var list = this.hologramBase.getAllLines(pageCopy);
            intersection
                .<Object>thenApply(
                    object -> {
                      if (object == null) {
                        int hoveringLine = cmiHologramHoverData.getHoveringLine();
                        CMIAiming aimingAlt = CMIAiming.getAimAt(player);
                        if (aimingAlt != null && aimingAlt.getAimAt().equals(this.hologramBase)) {
                          this.callStopHoverEvent(player);
                          CMIAiming.removeAimAt(player);
                        }

                        if (hoveringLine > 0) {
                          this.hologramBase.requestUpdate(uuid);
                        }

                        return null;
                      } else {
                        CMIAiming aimingCurrent = CMIAiming.getAimAt(player);

                        try {
                          if (aimingCurrent != null
                              && !this.hologramBase.equals(aimingCurrent.getAimAt())) {
                            CMIVector3D globalAim = aimingCurrent.getGlobalPos();
                            CMIVector3D globalLoc = object.getGlobalLoc();
                            double hitDistance =
                                eyePos.distance(
                                    new Vector(
                                        globalAim.getX(), globalAim.getY(), globalAim.getZ()));
                            double hitOffset =
                                eyePos.distance(
                                    new Vector(
                                        globalLoc.getX(), globalLoc.getY(), globalLoc.getZ()));
                            if (hitDistance < hitOffset) {
                              return null;
                            }
                          }
                        } catch (Exception exception) {
                          exception.printStackTrace();
                        }

                        if (cmiHologramInteraction.isShowHoverParticle()
                            && this.hologramBase.equals(
                                cmiHologramHoverData.getHoveringHologram())) {
                          var intersections =
                              this.getLocation(
                                  player, cmiHologramInteraction.getParticleCount(), cmiPlane);
                          if (intersections != null && !intersections.isEmpty()) {
                            for (CMIVector3D intersect : intersections) {
                              Location location =
                                  cmiLocation
                                      .clone()
                                      .add(
                                          -intersect.getX(),
                                          -intersect.getY()
                                              - this.hologramBase.getLineHeight() / 2.0,
                                          -intersect.getZ());
                              cmiHologramInteraction.getHoverEffect().show(player, location);
                            }
                          }
                        }

                        double stepSize = number / value;
                        if (cmiHologramType == CMIHologramType.TextDisplay) {
                          stepSize = this.hologramBase.getLineHeight();
                        }

                        double deltaY = object.getLocalLoc().getY() + -cmiPlane.getSizeMin().getY();
                        double ratioY = Math.abs(deltaY) / stepSize;
                        ratioY = Math.abs((int) ratioY) + 1;
                        ratioY = ratioY > value ? value : ratioY;
                        ratioY = value - ratioY + 1.0;
                        Vector lookDirection = player.getLocation().getDirection();
                        lookDirection.normalize();
                        lookPoint.normalize();
                        boolean facingPlane = lookDirection.dot(lookPoint) > 0.0;
                        double threshold = 0.5;
                        double hitRatio =
                            -object.getLocalLoc().getX() + -cmiPlane.getSizeMin().getX();
                        hitRatio = facingPlane ? -hitRatio : hitRatio;
                        threshold = planeWidth - Math.abs(hitRatio);
                        threshold /= planeWidth;
                        threshold = facingPlane ? threshold : 1.0 - threshold;
                        CMIAiming aimingFinal = CMIAiming.getAimAt(player);
                        if (aimingFinal != null) {
                          aimingFinal.setFrontSide(facingPlane);
                        }

                        if (util.hologramTestUuid != null
                                && util.hologramTestUuid.equals(player.getUniqueId())
                            || Version.isTestServer()) {
                          CMIActionBar.send(
                              player,
                              "{#white} X: {#gray}"
                                  + String.format("%.2f", threshold)
                                  + "{#white} Y:  {#gray}"
                                  + String.format("%.2f", deltaY / number)
                                  + "{#white} GX:  {#gray}"
                                  + String.format("%.2f", hitRatio + planeWidth)
                                  + "{#white} GY:  {#gray}"
                                  + String.format("%.2f", deltaY));
                        }

                        if (cmiHologramHoverData.getHoveringLine() != (int) ratioY
                            || aimingCurrent != null
                                && !Objects.equals(aimingCurrent.getAimAt(), this.hologramBase)) {
                          FakeEntityInfo fakeEntityInfo =
                              CMIHologramHoverData.getData(player).getFakeInfo();
                          if (fakeEntityInfo != null) {
                            fakeEntityInfo.setObject(this.hologramBase);
                          }
                        }

                        if (cmiHologramHoverData.getHoveringLine() != (int) ratioY) {
                          try {
                            cmiHologramHoverData.setHoveringLine(
                                (CMIHologramLine) list.get((int) ratioY - 1));
                            cmiHologramHoverData.setHoveringPart(-1.0);
                            if (aimingFinal != null) {
                              CMIHologram cmiHologram = aimingFinal.getAimAt();
                              if (cmiHologram != null && !cmiHologram.equals(this.hologramBase)) {
                                double widthRatio =
                                    CMI.getInstance()
                                        .getUtilManager()
                                        .getDistance(
                                            player.getLocation(), cmiHologram.getLocation());
                                double heightRatio =
                                    CMI.getInstance()
                                        .getUtilManager()
                                        .getDistance(player.getLocation(), cmiLocation);
                                if (widthRatio < heightRatio) {
                                  cmiHologramHoverData.setHoveringLine((int) ratioY);
                                  return null;
                                }

                                cmiHologramHoverData.setHoveringLine(-1);
                                if (!flag) {
                                  cmiHologram.requestUpdate(player.getUniqueId());
                                  this.callStopHoverEvent(player, cmiHologram);
                                  CMIAiming.removeAimAt(player);
                                  this.callStartHoverEvent(player, object);
                                }
                              }
                            } else {
                              this.callStartHoverEvent(player, object);
                            }

                            if (cmiHologramHoverData.getFakeInfo() != null
                                && !player
                                    .getWorld()
                                    .equals(
                                        cmiHologramHoverData
                                            .getFakeInfo()
                                            .getLocation()
                                            .getWorld())) {
                              cmiHologramHoverData.getFakeInfo().setEntity(null);
                            }

                            if (!flag) {
                              this.addInteractionCheck(uuid);
                              cmiHologramHoverData.setHoveringLine((int) ratioY);
                              cmiHologramHoverData.isHoveringOverNewLinePortion(
                                  threshold, pageCopy);
                              this.hologramBase.requestUpdate(uuid);
                            }
                          } catch (Throwable throwable) {
                            throwable.printStackTrace();
                          }
                        } else {
                          boolean hovered = false;
                          if (aimingFinal != null
                              && this.hologramBase.equals(aimingFinal.getAimAt())
                              && aimingFinal
                                  .getGlobalPos()
                                  .toString()
                                  .equals(object.getGlobalLoc().toString())) {
                            hovered = true;
                          }

                          if (!flag && cmiHologramHoverData.isChangeInHoverPart(threshold)) {
                            if (cmiHologramHoverData.isHoveringOverNewLinePortion(
                                threshold, pageCopy)) {
                              this.hologramBase.requestUpdate(uuid);
                            }

                            cmiHologramHoverData.setHoveringPart(threshold);
                          }

                          if (hovered) {
                            return null;
                          }

                          boolean hasHit =
                              !flag
                                  && aimingFinal != null
                                  && aimingFinal.getAimAt() != null
                                  && !this.hologramBase.equals(aimingFinal.getAimAt());
                          if (hasHit) {
                            this.hologramBase.requestUpdate(player.getUniqueId());
                            this.callStopHoverEvent(player, aimingFinal.getAimAt());
                            CMIAiming.removeAimAt(player);
                          }

                          CMIAiming.addAimAt(
                              player.getUniqueId(), this.hologramBase, object.getGlobalLoc());
                          cmiHologramHoverData.setHoveringLine((int) ratioY);
                          if (hasHit) {
                            this.callStartHoverEvent(player, object);
                          }
                        }

                        if (!flag) {
                          FakeEntityInfo hoverInfo = cmiHologramHoverData.getFakeInfo();
                          if (hoverInfo != null && hoverInfo.getEntity() != null) {
                            Object hoverTarget =
                                hoverInfo.getEntity() instanceof CMIInteractionEntity
                                    ? ((CMIInteractionEntity) hoverInfo.getEntity()).getDisplay()
                                    : hoverInfo.getEntity();
                            if (Version.isFolia()) {
                              CMIScheduler.runAtLocation(
                                  CMI.getInstance(),
                                  hoverInfo.getLocation(),
                                  () ->
                                      CMILib.getInstance()
                                          .getReflectionManager()
                                          .superficialEntityTeleport(
                                              player,
                                              hoverTarget,
                                              object
                                                  .getGlobalLoc()
                                                  .toLocation(player.getWorld())
                                                  .add(0.0, -0.25, 0.0)));
                            } else {
                              CMILib.getInstance()
                                  .getReflectionManager()
                                  .superficialEntityTeleport(
                                      player,
                                      hoverTarget,
                                      object
                                          .getGlobalLoc()
                                          .toLocation(player.getWorld())
                                          .add(0.0, -0.25, 0.0));
                            }
                          }
                        }

                        return null;
                      }
                    })
                .whenComplete((result, error) -> completableFuture.complete(null));
            return completableFuture;
          }
        }
      } else {
        completableFuture.complete(null);
        return completableFuture;
      }
    } catch (Throwable throwable2) {
      throwable2.printStackTrace();
      return completableFuture;
    }
  }

  private void callStartHoverEvent(Player player, CMIPlaneIntersection cmiPlaneIntersection) {
    CMIAiming.addAimAt(
        player.getUniqueId(),
        this.hologramBase,
        cmiPlaneIntersection.getGlobalLoc(),
        cmiPlaneIntersection.getLocalLoc());
    CMIHologramHoverData.getData(player).setHoveringHologram(this.hologramBase);
    CMIHologramStartHoverEvent cmiHologramStartHoverEvent =
        new CMIHologramStartHoverEvent(player, this.hologramBase, cmiPlaneIntersection);
    CompletableFuture.runAsync(
        () -> Bukkit.getPluginManager().callEvent(cmiHologramStartHoverEvent));
    FakeEntityInfo fakeEntityInfo = CMIHologramHoverData.getData(player).getFakeInfo();
    if (fakeEntityInfo != null && fakeEntityInfo.getEntity() != null) {
      if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)
          && fakeEntityInfo.getEntity() instanceof CMIInteractionEntity) {
        CMIInteractionEntity cmiInteractionEntity =
            (CMIInteractionEntity) fakeEntityInfo.getEntity();
        CMIScheduler.runAtLocation(
            CMI.getInstance(),
            cmiInteractionEntity.getLocation(),
            () -> {
              cmiInteractionEntity.show(player);
              this.checkHoverPosition(player.getUniqueId(), true);
            });
        return;
      }

      fakeEntityInfo.setObject(this.hologramBase);
    }

    this.addInteractionCheck(player.getUniqueId());
  }

  private void callStopHoverEvent(Player player) {
    this.callStopHoverEvent(player, this.hologramBase);
  }

  private void callStopHoverEvent(Player player, CMIHologram cmiHologram) {
    CMIHologramStopHoverEvent cmiHologramStopHoverEvent =
        new CMIHologramStopHoverEvent(player, cmiHologram);
    CompletableFuture.runAsync(
        () -> Bukkit.getPluginManager().callEvent(cmiHologramStopHoverEvent));
    CMIHologramHoverData.getData(player).resetHoverPosition();
    cmiHologram.requestUpdate(player.getUniqueId());
  }

  private void addInteractionCheck(UUID uuid) {
    if (this.hologramBase.getInteractionSettings().isInteractable()) {
      CMIHologramHoverData cmiHologramHoverData = CMIHologramHoverData.getData(uuid);
      FakeEntityInfo fakeEntityInfo = cmiHologramHoverData.getFakeInfo();
      if (fakeEntityInfo == null || fakeEntityInfo.getEntity() == null) {
        if (CMIUser.getOnlinePlayer(uuid) == null) {
          cmiHologramHoverData.setFakeInfo(null);
        } else if (Version.isCurrentEqualOrHigher(Version.v1_20_R1)) {
          CMIInteractionEntity cmiInteractionEntity =
              new CMIInteractionEntity(this.hologramBase.getCenterLocation());
          FakeEntityInfo lineInfo =
              new FakeEntityInfo(
                  cmiInteractionEntity.getId(), uuid, cmiInteractionEntity.getLocation());
          lineInfo.setEntity(cmiInteractionEntity);
          cmiHologramHoverData.setFakeInfo(lineInfo);
          lineInfo.setObject(this.hologramBase);
          PacketHandler.addFakeEntity(lineInfo);
          CMIScheduler.runAtLocation(
              CMI.getInstance(),
              cmiInteractionEntity.getLocation(),
              () -> {
                cmiInteractionEntity.setWidth(0.5);
                cmiInteractionEntity.setHeight(0.5);
                cmiInteractionEntity.show(CMIUser.getOnlinePlayer(uuid));
              });
        } else {
          FakeInfo fakeInfo =
              CMI.getInstance()
                  .getNMS()
                  .spawnClickable(
                      CMIUser.getOnlinePlayer(uuid),
                      this.hologramBase.getCenterLocation(),
                      this.hologramBase,
                      true);
          FakeEntityInfo lineInfoAlt =
              new FakeEntityInfo(
                  fakeInfo.getId(),
                  fakeInfo.getPlayer().getUniqueId(),
                  this.hologramBase.getCenterLocation());
          lineInfoAlt.setEntity(fakeInfo.getEntity());
          lineInfoAlt.setObject(this.hologramBase);
          cmiHologramHoverData.setFakeInfo(lineInfoAlt);
          PacketHandler.addFakeEntity(lineInfoAlt);
        }
      }
    }
  }

  protected List<CMIVector3D> getLocation(Player player, int value, CMIPlane cmiPlane) {
    CMIHologramPage cmiHologramPage = this.hologramBase.getPlayerPage(player.getUniqueId());
    if (cmiHologramPage == null) {
      cmiHologramPage = this.hologramBase.getPages().getPage(1);
    }

    if (cmiHologramPage == null) {
      return null;
    }

    float floatValue =
        (float) CMINumber.abs(cmiPlane.getSizeMax().getY() - cmiPlane.getSizeMin().getY());
    float lineWidth =
        (float) CMINumber.abs(cmiPlane.getSizeMax().getX() - cmiPlane.getSizeMin().getX());
    floatValue = (float) (floatValue + 0.2);
    lineWidth = (float) (lineWidth + 0.2);
    lineWidth /= 2.0F;
    float stepX = 0.1F;
    float stepY = 0.1F;
    CMIHologramSettings cmiHologramSettings = this.hologramBase.getSettings();
    CMIHologramInteraction cmiHologramInteraction = this.hologramBase.getInteractionSettings();
    boolean flag = false;
    if (cmiHologramInteraction.getParticleSpacing() != 0.0) {
      if (cmiHologramInteraction.getParticleSpacing() > 0.0) {
        stepX = (float) cmiHologramInteraction.getParticleSpacing();
      } else {
        stepX = -((float) cmiHologramInteraction.getParticleSpacing());
        flag = true;
      }

      stepY = stepX;
    } else {
      stepY = 1.0E-6F;
      stepX = 1.0E-6F;
    }

    double number =
        this.hologramBase.getSettings().getBillboard().getPlanePitch(player, this.hologramBase);
    double startX =
        this.hologramBase.getSettings().getBillboard().getPlaneYaw(player, this.hologramBase);
    if (this.hologramBase.getAnimationSettings().getAutoRotate() != 0) {
      CMIHologramUserData cmiHologramUserData = this.hologramBase.getData(player);
      double startY =
          cmiHologramUserData.getCurrentAutoRotateExtraAngle(
              this.hologramBase.getAnimationSettings().getAutoRotate());
      startY = Math.toRadians(startY);
      startX += startY;
    }

    lineWidth =
        (float)
            (lineWidth + this.hologramBase.getInteractionSettings().getParticleDimensions().getX());
    floatValue =
        (float)
            (floatValue
                + this.hologramBase.getInteractionSettings().getParticleDimensions().getY());
    float totalLength =
        lineWidth * 2.0F / stepX * 2.0F + (floatValue + stepY * 2.0F) / stepY * 2.0F - 3.0F;
    float segmentLength = totalLength / value;
    cmiHologramInteraction.setParticlePosition(
        (short) ((cmiHologramInteraction.getParticlePosition() + 1) % totalLength));
    int firstOffset = (int) (cmiHologramInteraction.getParticlePosition() % segmentLength);
    ArrayList<CMIVector3D> arrayList = new ArrayList();
    if (this.hologramBase.getSettings().getBillboard() == CMIBillboard.FIXED
        || this.hologramBase.getSettings().getBillboard() == CMIBillboard.HORIZONTAL) {
      flag = !flag;
    }

    float perimeterX = lineWidth * 2.0F / stepX;
    float perimeterY = floatValue / stepY;
    float perimeterTotal = perimeterX * 2.0F + perimeterY * 2.0F;
    float widthTerm = lineWidth * 2.0F;
    float heightTerm = floatValue;
    float depthTerm = lineWidth * 2.0F;
    float extraTerm = floatValue;
    float sumTerms = widthTerm + heightTerm + depthTerm + extraTerm;
    float ratio = sumTerms / perimeterTotal;

    for (int segmentIndex = 1; segmentIndex <= value; segmentIndex++) {
      float position = firstOffset + (segmentIndex - 1) * segmentLength;
      position %= perimeterTotal;
      if (position < 0.0F) {
        position += perimeterTotal;
      }

      position++;
      if (flag) {
        position = perimeterTotal - position + 1.0F;
      }

      float offset = (position - 0.5F) * ratio;
      float lerpStart;
      float lerpEnd;
      if (offset < widthTerm) {
        lerpStart = -lineWidth + offset;
        lerpEnd = 0.0F;
      } else if (offset < widthTerm + heightTerm) {
        lerpStart = lineWidth;
        lerpEnd = offset - widthTerm;
      } else if (offset < widthTerm + heightTerm + depthTerm) {
        lerpStart = lineWidth - (offset - widthTerm - heightTerm);
        lerpEnd = floatValue;
      } else {
        lerpStart = -lineWidth;
        lerpEnd = floatValue - (offset - widthTerm - heightTerm - depthTerm);
      }

      CMIVector3D cmiVector3D =
          new CMIVector3D(
              lerpStart
                  + cmiHologramInteraction.getParticleOffset().getX()
                  + cmiHologramSettings.getOffset().getX(),
              cmiPlane.getSizeMin().getY()
                  + lerpEnd
                  + cmiHologramInteraction.getParticleOffset().getY(),
              cmiHologramInteraction.getParticleOffset().getZ());
      arrayList.add(CMIPlaneIntersection.rotateAroundCenter(cmiVector3D, startX, number));
    }

    return arrayList;
  }
}
