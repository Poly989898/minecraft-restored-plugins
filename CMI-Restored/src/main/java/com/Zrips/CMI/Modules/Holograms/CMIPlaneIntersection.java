package com.Zrips.CMI.Modules.Holograms;

import java.util.concurrent.CompletableFuture;
import net.Zrips.CMILib.Container.CMIPlane;
import net.Zrips.CMILib.Container.CMIVector2D;
import net.Zrips.CMILib.Container.CMIVector3D;
import org.bukkit.util.Vector;

public class CMIPlaneIntersection {
  private CMIVector3D globalLoc;
  private CMIVector2D localLoc;
  private static final double _HPI = Math.PI / 180.0;

  public static CompletableFuture<CMIPlaneIntersection> get(
      Vector vector, Vector pointA, Vector pointB, CMIPlane cmiPlane) {
    return CompletableFuture.supplyAsync(
        () ->
            get(
                new CMIVector3D(vector),
                new CMIVector3D(pointA),
                new CMIVector3D(pointB),
                cmiPlane));
  }

  private static CMIPlaneIntersection get(
      CMIVector3D cmiVector3D, CMIVector3D lineStart, CMIVector3D lineEnd, CMIPlane cmiPlane) {
    CMIVector3D hit =
        intersectPoint(cmiVector3D.clone(), lineStart.clone(), lineEnd.clone(), cmiPlane);
    if (hit == null) {
      return null;
    }

    CMIVector3D relative = lineEnd.clone().normalize();
    CMIVector3D normalized = new CMIVector3D(0.0, 1.0, 0.0).cross(relative);
    if (normalized.lengthSquared() == 0.0) {
      normalized = new CMIVector3D(1.0, 0.0, 0.0);
    }

    normalized = normalized.normalize();
    CMIVector3D planeNormal = relative.cross(normalized).normalize();
    CMIVector3D planeOffset = hit.clone().minus(cmiPlane.getCenter());
    double number = planeOffset.dot(normalized);
    double distance = planeOffset.dot(planeNormal);
    if (number < cmiPlane.getSizeMin().getX() || number > cmiPlane.getSizeMax().getX()) {
      return null;
    } else if (!(distance < cmiPlane.getSizeMin().getY())
        && !(distance > cmiPlane.getSizeMax().getY())) {
      CMIPlaneIntersection cmiPlaneIntersection = new CMIPlaneIntersection();
      cmiPlaneIntersection.globalLoc = hit;
      cmiPlaneIntersection.localLoc = new CMIVector2D(-number, distance);
      return cmiPlaneIntersection;
    } else {
      return null;
    }
  }

  public static CMIVector3D rotateAroundCenter(
      CMIVector3D cmiVector3D, double number, double pitchRad) {
    double x = cmiVector3D.getX();
    double y = cmiVector3D.getY();
    double z = cmiVector3D.getZ();
    double cosPitch = -Math.cos(pitchRad);
    double sinPitch = Math.sin(pitchRad);
    double yRot = y * cosPitch - z * sinPitch;
    double zRot = y * sinPitch + z * cosPitch;
    double cosYaw = -Math.cos(number);
    double sinYaw = Math.sin(number);
    double xFinal = x * cosYaw + zRot * sinYaw;
    double zFinal = -x * sinYaw + zRot * cosYaw;
    return new CMIVector3D(xFinal, yRot, zFinal);
  }

  public static CMIVector3D rotateAroundCenter(double number, double y, double z, double yaw) {
    double angle = (Math.PI * 3.0 / 2.0) - Math.atan2(y, number);
    double length = Math.sqrt(y * y + number * number);
    double sinA = Math.sin(angle) * length;
    double rotX = sinA * Math.cos((Math.PI / 180.0) * z);
    double cosA = Math.cos(angle) * length;
    double rotZ = sinA * Math.sin((Math.PI / 180.0) * z);
    CMIVector3D cmiVector3D = new CMIVector3D(rotX, cosA, rotZ);
    if (yaw != 0.0) {
      cmiVector3D = rotateX(cmiVector3D, yaw);
    }

    if (z != 0.0) {
      cmiVector3D = rotateY(cmiVector3D, -z);
    }

    return cmiVector3D;
  }

  public static double xPosYaw(double number, double amount, double amountSecond) {
    return Math.sin(number) * amount * Math.cos((Math.PI / 180.0) * amountSecond);
  }

  public static double yPosYaw(double number, double amount) {
    return Math.cos(number) * amount;
  }

  public static double zPosYaw(double number, double amount, double amountSecond) {
    return Math.sin(number) * amount * Math.sin((Math.PI / 180.0) * amountSecond);
  }

  private static CMIVector3D rotateX(CMIVector3D cmiVector3D, double number) {
    double amount = Math.cos(number);
    double amountSecond = Math.sin(number);
    double amountThird = amount * cmiVector3D.getY() - amountSecond * cmiVector3D.getZ();
    double amountFourth = amountSecond * cmiVector3D.getY() + amount * cmiVector3D.getZ();
    cmiVector3D.setY(amountThird);
    return cmiVector3D.setZ(amountFourth);
  }

  private static CMIVector3D rotateY(CMIVector3D cmiVector3D, double number) {
    double amount = Math.cos(number);
    double amountSecond = Math.sin(number);
    double amountThird = amount * cmiVector3D.getX() + amountSecond * cmiVector3D.getZ();
    double amountFourth = -amountSecond * cmiVector3D.getX() + amount * cmiVector3D.getZ();
    cmiVector3D.setX(amountThird);
    return cmiVector3D.setZ(amountFourth);
  }

  private static CMIVector3D intersectPoint(
      CMIVector3D cmiVector3D,
      CMIVector3D cMIVector3DSecond,
      CMIVector3D cMIVector3DThird,
      CMIPlane cmiPlane) {
    Vector vector = cmiPlane.getCenter().clone();
    CMIVector3D cMIVector3DFourth = cMIVector3DSecond.clone().minus(new CMIVector3D(vector));
    double number = cmiVector3D.dot(cMIVector3DThird);
    if (Math.abs(number) < 1.0E-8) {
      return null;
    }

    double amount = -cMIVector3DFourth.dot(cMIVector3DThird) / number;
    return amount < 0.0
        ? null
        : cMIVector3DSecond.clone().plus(cmiVector3D.clone().multiply(amount));
  }

  public CMIVector3D getGlobalLoc() {
    return this.globalLoc;
  }

  public void setGlobalLoc(CMIVector3D cmiVector3D) {
    this.globalLoc = cmiVector3D;
  }

  public CMIVector2D getLocalLoc() {
    return this.localLoc;
  }

  public void setLocalLoc(CMIVector2D cmiVector2D) {
    this.localLoc = cmiVector2D;
  }
}
