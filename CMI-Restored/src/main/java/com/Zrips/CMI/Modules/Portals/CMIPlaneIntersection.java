package com.Zrips.CMI.Modules.Portals;

import java.util.concurrent.CompletableFuture;
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
    lineEnd.setX(lineEnd.getX() + 1.0E-4);
    CMIVector3D hit = intersectPoint(cmiVector3D, lineStart, lineEnd, cmiPlane);
    if (hit == null) {
      return null;
    }

    CMIPlaneIntersection cmiPlaneIntersection = new CMIPlaneIntersection();
    double number = Math.sqrt(Math.pow(cmiPlane.getCenter().getY() - hit.getY(), 2.0));
    CMIVector3D relative = hit.minus(cmiPlane.getCenter());
    CMIVector3D normalized = relative.minus(lineEnd.multiply(lineEnd.dot(relative)));
    double sumXZ = normalized.getX() + normalized.getZ();
    CMIVector3D planeNormal = new CMIVector3D(1.0, 0.0, 1.0);
    double angle = -(Math.toDegrees(Math.atan2(lineEnd.getX(), lineEnd.getZ())) - 180.0);
    sumXZ = relative.dot(planeNormal);
    if (angle <= 315.0 && angle >= 135.0) {
      sumXZ = -sumXZ;
    }

    if (cmiVector3D.dot(lineEnd) < 0.0) {
      sumXZ = -sumXZ;
    }

    double heightDiff = Math.abs(hit.getY()) - Math.abs(cmiPlane.getCenter().getY());
    if (heightDiff < 0.0) {
      number = -number;
    }

    if (sumXZ < cmiPlane.getSizeMin().getX() || sumXZ > cmiPlane.getSizeMax().getX()) {
      return null;
    } else if (!(number < cmiPlane.getSizeMin().getY())
        && !(number > cmiPlane.getSizeMax().getY())) {
      cmiPlaneIntersection.setGlobalLoc(hit);
      cmiPlaneIntersection.setLocalLoc(new CMIVector2D(sumXZ, number));
      return cmiPlaneIntersection;
    } else {
      return null;
    }
  }

  public static CMIVector3D rotateAroundCenter(
      CMIVector3D cmiVector3D, double number, double yawDegrees) {
    double x = cmiVector3D.getX();
    double y = cmiVector3D.getY();
    double z = cmiVector3D.getZ();
    double angleXZ = (Math.PI * 3.0 / 2.0) - Math.atan2(y, x);
    double length = Math.sqrt(x * x + y * y + z * z);
    double sinA = Math.sin(angleXZ) * length;
    double rotX = sinA * Math.cos((Math.PI / 180.0) * number);
    double cosA = Math.cos(angleXZ) * length;
    double rotZ = sinA * Math.sin((Math.PI / 180.0) * number);
    CMIVector3D rotated = new CMIVector3D(rotX, cosA, rotZ);
    if (yawDegrees != 0.0) {
      rotated = rotateX(rotated, yawDegrees);
    }

    if (number != 0.0) {
      rotated = rotateY(rotated, -number);
    }

    return rotated;
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

  private static CMIVector3D rotateX(CMIVector3D cmiVector3D, double number) {
    double cosYaw = Math.cos(number);
    double sinYaw = Math.sin(number);
    double rotY = cosYaw * cmiVector3D.getY() - sinYaw * cmiVector3D.getZ();
    double rotZ = sinYaw * cmiVector3D.getY() + cosYaw * cmiVector3D.getZ();
    cmiVector3D.setY(rotY);
    return cmiVector3D.setZ(rotZ);
  }

  private static CMIVector3D rotateY(CMIVector3D cmiVector3D, double number) {
    double cosYaw = Math.cos(number);
    double sinYaw = Math.sin(number);
    double rotX = cosYaw * cmiVector3D.getX() + sinYaw * cmiVector3D.getZ();
    double rotZ = -sinYaw * cmiVector3D.getX() + cosYaw * cmiVector3D.getZ();
    cmiVector3D.setX(rotX);
    return cmiVector3D.setZ(rotZ);
  }

  public static double xPosYaw(double number, double y, double z) {
    return Math.sin(number) * y * Math.cos((Math.PI / 180.0) * z);
  }

  public static double yPosYaw(double number, double z) {
    return Math.cos(number) * z;
  }

  public static double zPosYaw(double number, double x, double y) {
    return Math.sin(number) * x * Math.sin((Math.PI / 180.0) * y);
  }

  private static CMIVector3D intersectPoint(
      CMIVector3D cmiVector3D, CMIVector3D linePoint, CMIVector3D planeNormal, CMIPlane cmiPlane) {
    Vector vector = cmiPlane.getCenter().clone();
    vector.setY(vector.getY() + cmiPlane.getSizeMin().getY());
    CMIVector3D pointFromCenter = linePoint.minus(vector);
    double number = pointFromCenter.dot(planeNormal);
    double denominator = cmiVector3D.dot(planeNormal);
    double intersectionT = number / denominator;
    return intersectionT > 0.0 ? null : linePoint.minus(cmiVector3D.multiply(intersectionT));
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
