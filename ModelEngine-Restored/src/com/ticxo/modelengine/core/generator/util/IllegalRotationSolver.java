package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.data.Triple;
import com.ticxo.modelengine.api.utils.math.Axis;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class IllegalRotationSolver {
   private static final List<Vector3d> AXES = List.of(Axis.X.getVector(), Axis.Y.getVector(), Axis.Z.getVector());

   public static ProcessedBone.Cube solve(ProcessedBone.Cube cube) {
      if (isLegal(cube)) {
         return cube;
      }

      Quaterniond quaternion = cube.getQuaternion();
      HashMap<Direction, Triple<Vector3d, Vector3d, ProcessedBone.Face>> uvs = new HashMap<>();

      for (Entry<Direction, ProcessedBone.Face> entry : cube.getFaces().entrySet()) {
         Direction dir = entry.getKey();
         ProcessedBone.Face uv = entry.getValue();
         uvs.put(dir, new Triple<>(dir.getNormal().rotate(quaternion), dir.getUvUp().rotate(quaternion), uv));
      }

      List<Vector3d> corners = cube.getCorners();
      corners.forEach(vector3d -> vector3d.sub(cube.getOrigin()).rotate(quaternion).add(cube.getOrigin()));
      Vector3d srcDir = new Vector3d(0.0, 0.0, 1.0).rotate(quaternion);
      Vector3d dstAxis = closestAxis(srcDir);
      double dot = srcDir.dot(dstAxis);
      Quaterniond undoQuat;
      if (TMath.isSimilar(dot, 1.0)) {
         srcDir = new Vector3d(0.0, 1.0, 0.0).rotate(quaternion);
         dstAxis = closestAxis(srcDir);
         dot = srcDir.dot(dstAxis);
         if (TMath.isSimilar(dot, 1.0)) {
            undoQuat = new Quaterniond();
         } else {
            Vector3d cross = srcDir.cross(dstAxis, new Vector3d()).normalize();
            undoQuat = new Quaterniond().rotateAxis(Math.acos(dot), cross);
         }
      } else {
         Vector3d cross = srcDir.cross(dstAxis, new Vector3d()).normalize();
         undoQuat = new Quaterniond().rotateAxis(Math.acos(dot), cross);
      }

      HashMap<Direction, ProcessedBone.Face> newUVs = new HashMap<>();

      for (Entry<Direction, Triple<Vector3d, Vector3d, ProcessedBone.Face>> entry : uvs.entrySet()) {
         Triple<Vector3d, Vector3d, ProcessedBone.Face> triple = entry.getValue();
         Vector3d currNormal = triple.getFirst().rotate(undoQuat);
         Direction currDir = Direction.fromNormal(currNormal);
         if (currDir != null) {
            Vector3d currUVUp = currDir.getUvUp();
            Vector3d targetUVUp = triple.getSecond().rotate(undoQuat);
            int angle = 0;
            double upDot = targetUVUp.dot(currUVUp);
            if (TMath.isSimilar(upDot, -1.0)) {
               angle = 180;
            } else if (TMath.isSimilar(upDot, 0.0)) {
               Vector3d cross = targetUVUp.cross(currUVUp, new Vector3d());
               angle = cross.dot(currNormal) > 0.0 ? 90 : 270;
            }

            ProcessedBone.Face face = triple.getThird();
            ProcessedBone.UV uv = face.uv();
            ProcessedBone.Face tFace = new ProcessedBone.Face(
               new ProcessedBone.UV(uv.u1(), uv.v1(), uv.u2(), uv.v2(), (uv.rotation() + angle) % 360), face.texture()
            );
            newUVs.put(currDir, tFace);
         }
      }

      Vector3d from = new Vector3d(2.147483647E9);
      Vector3d to = new Vector3d(-2.1474836E9F);
      corners.forEach(vector3d -> {
         Vector3d vec = vector3d.sub(cube.getOrigin()).rotate(undoQuat).add(cube.getOrigin());
         from.set(Math.min(from.x, vec.x), Math.min(from.y, vec.y), Math.min(from.z, vec.z));
         to.set(Math.max(to.x, vec.x), Math.max(to.y, vec.y), Math.max(to.z, vec.z));
      });
      Vector3d rotation = TMath.fixEuler(TMath.toEulerZYX(undoQuat.invert()));
      return new ProcessedBone.Cube(cube.getName(), cube.getOrigin(), rotation, from, to, newUVs, cube.getInflate());
   }

   private static Vector3d closestAxis(Vector3d dir) {
      if (TMath.isSimilar(dir.lengthSquared(), 0.0)) {
         return new Vector3d((Vector3dc)AXES.get(2));
      }

      Vector3d closest = null;
      double dot = 0.0;

      for (Vector3d axis : AXES) {
         double d = Math.abs(dir.dot(axis));
         if (d > dot) {
            dot = d;
            closest = axis;
         }
      }

      if (closest == null) {
         return new Vector3d((Vector3dc)AXES.get(2));
      } else {
         return closest.dot(dir) > 0.0 ? new Vector3d(closest) : closest.negate(new Vector3d());
      }
   }

   private static boolean isLegal(ProcessedBone.Cube cube) {
      Vector3d rot = cube.getRotation();
      return TMath.isAlmostBetween(rot.x, -45.0, 45.0) && TMath.isInterval(rot.x, 22.5) && TMath.isSimilar(rot.y, 0.0) && TMath.isSimilar(rot.z, 0.0)
         || TMath.isAlmostBetween(rot.y, -45.0, 45.0) && TMath.isInterval(rot.y, 22.5) && TMath.isSimilar(rot.x, 0.0) && TMath.isSimilar(rot.z, 0.0)
         || TMath.isAlmostBetween(rot.z, -45.0, 45.0) && TMath.isInterval(rot.z, 22.5) && TMath.isSimilar(rot.x, 0.0) && TMath.isSimilar(rot.y, 0.0);
   }
}
