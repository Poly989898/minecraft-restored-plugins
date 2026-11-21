package com.ticxo.modelengine.core.generator.util;

import com.ticxo.modelengine.api.utils.math.Axis;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.processed.ProcessedBone;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Map.Entry;
import lombok.Generated;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotationSolver {
   @Generated
   private static final Logger log = LoggerFactory.getLogger(RotationSolver.class);
   private static final List<Vector3d> CARDINAL_AXES = new ArrayList<Vector3d>() {
      {
         this.add(new Vector3d(1.0, 0.0, 0.0));
         this.add(new Vector3d(0.0, 1.0, 0.0));
         this.add(new Vector3d(0.0, 0.0, 1.0));
         this.add(new Vector3d(-1.0, 0.0, 0.0));
         this.add(new Vector3d(0.0, -1.0, 0.0));
         this.add(new Vector3d(0.0, 0.0, -1.0));
      }
   };
   private final Int2ObjectMap<ProcessedBone.Cube> cubes = new Int2ObjectOpenHashMap();
   private final Int2ObjectMap<List<Vector3d>> axes = new Int2ObjectOpenHashMap();

   public static void solve(Collection<ItemGroup> result, Collection<ProcessedBone.Cube> cubes) {
      RotationSolver solver = new RotationSolver();
      solver.initialize(result, cubes);
      solver.solve(result);
   }

   private void initialize(Collection<ItemGroup> result, Collection<ProcessedBone.Cube> collection) {
      int cubeId = 0;
      ArrayList<ProcessedBone.Cube> list = new ArrayList<>();

      for (ProcessedBone.Cube cube : collection) {
         if (isLegal(cube)) {
            list.add(IllegalRotationSolver.solve(cube));
         } else {
            this.cubes.put(cubeId++, cube);
         }
      }

      if (!list.isEmpty()) {
         result.add(new ItemGroup(-1, new Quaterniond(), new Vector3d(), list));
      }
   }

   private void solve(Collection<ItemGroup> result) {
      Map<Vector3d, IntLinkedOpenHashSet> axisGroup = this.groupByAxis();
      List<PlaneGroup> moduloGroup = this.groupByModulo(axisGroup);
      Map<PlaneGroup, Set<PlaneGroup>> planeGroup = this.combineGroup(moduloGroup);
      this.fixGroups(result, planeGroup);
   }

   private void simpleConvert(Collection<ItemGroup> result, List<PlaneGroup> moduloGroup) {
      for (PlaneGroup plane : moduloGroup) {
         ArrayList<ProcessedBone.Cube> list = new ArrayList<>();
         this.rotateCubes(list, plane.cubes(), plane.invOrigin());
         result.add(new ItemGroup(0, new Quaterniond(plane.origin()), TMath.toEulerXYZ(plane.origin()), list));
      }
   }

   private List<Vector3d> toAxes(int cubeId) {
      ProcessedBone.Cube cube = this.cubes.get(cubeId);
      Vector3d px = TMath.fixVector(new HashedVector3d(Axis.X, 1.0, 0.0, 0.0).rotate(cube.getQuaternion()));
      Vector3d py = TMath.fixVector(new HashedVector3d(Axis.Y, 0.0, 1.0, 0.0).rotate(cube.getQuaternion()));
      Vector3d pz = TMath.fixVector(new HashedVector3d(Axis.Z, 0.0, 0.0, 1.0).rotate(cube.getQuaternion()));
      return Arrays.asList(px, py, pz);
   }

   private Map<Vector3d, IntLinkedOpenHashSet> groupByAxis() {
      Object2ObjectLinkedOpenHashMap<Vector3d, IntLinkedOpenHashSet> map = new Object2ObjectLinkedOpenHashMap();
      IntIterator intIterator = this.cubes.keySet().iterator();

      while (intIterator.hasNext()) {
         int key = intIterator.next();
         List<Vector3d> axes = this.toAxes(key);
         this.axes.put(key, axes);

         for (Vector3d axis : axes) {
            map.computeIfAbsent(axis, vector3d -> new IntLinkedOpenHashSet()).add(key);
         }
      }

      return fetch(map);
   }

   private List<PlaneGroup> groupByModulo(Map<Vector3d, IntLinkedOpenHashSet> byAxis) {
      Object2ObjectLinkedOpenHashMap<Vector3d, Int2ObjectMap<IntSet>> temp = new Object2ObjectLinkedOpenHashMap();
      ArrayList<PlaneGroup> result = new ArrayList<>();

      for (Entry<Vector3d, IntLinkedOpenHashSet> entry : byAxis.entrySet()) {
         Quaterniond toOrigin = null;
         IntListIterator subGroup = entry.getValue().iterator();

         while (subGroup.hasNext()) {
            int cubeId = subGroup.next();
            ProcessedBone.Cube cube = this.cubes.get(cubeId);
            double angle;
            if (toOrigin == null) {
               toOrigin = cube.getQuaternion().invert(new Quaterniond());
               angle = 0.0;
            } else {
               Quaterniond localQ = toOrigin.mul(cube.getQuaternion(), new Quaterniond());
               Vector3d lqVec = new Vector3d(localQ.x, localQ.y, localQ.z);
               double sign = Math.signum(lqVec.dot((Vector3dc)entry.getKey()));
               angle = sign * 2.0 * Math.acos(localQ.w) * (180.0 / Math.PI);
            }

            int groupId = getGroupId(angle);
            ((IntSet)(temp.computeIfAbsent(entry.getKey(), vector3d -> new Int2ObjectLinkedOpenHashMap()))
                  .computeIfAbsent(groupId, integer -> new IntLinkedOpenHashSet()))
               .add(cubeId);
         }
      }

      ObjectBidirectionalIterator objectBidirectionalIterator = temp.entrySet().iterator();

      while (objectBidirectionalIterator.hasNext()) {
         Entry<Vector3d, Int2ObjectMap<IntSet>> group = (Entry<Vector3d, Int2ObjectMap<IntSet>>)objectBidirectionalIterator.next();
         ObjectIterator objectIterator = group.getValue().int2ObjectEntrySet().iterator();

         while (objectIterator.hasNext()) {
            it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<IntSet> subGroup = (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<IntSet>)objectIterator.next();
            int firstCubeId = subGroup.getValue().iterator().nextInt();
            Quaterniond rotation = this.cubes.get(firstCubeId).getQuaternion();
            result.add(
               new PlaneGroup(
                  new Vector3d((Vector3dc)group.getKey()),
                  subGroup.getIntKey(),
                  new Quaterniond(rotation),
                  rotation.invert(new Quaterniond()),
                  subGroup.getValue()
               )
            );
         }
      }

      return result;
   }

   private Map<PlaneGroup, Set<PlaneGroup>> combineGroup(List<PlaneGroup> planes) {
      Object2ObjectLinkedOpenHashMap<PlaneGroup, Set<PlaneGroup>> map = new Object2ObjectLinkedOpenHashMap();

      for (int a = 0; a < planes.size(); a++) {
         PlaneGroup groupA = planes.get(a);
         Set<PlaneGroup> set = (Set<PlaneGroup>)map.computeIfAbsent(groupA, planeGroup -> new ObjectLinkedOpenHashSet());
         set.add(groupA);

         outer:
         for (int b = a + 1; b < planes.size(); b++) {
            PlaneGroup groupB = planes.get(b);
            if (TMath.isSimilar(groupA.axis().dot(groupB.axis()), -1.0)) {
               List<Vector3d> axesA = this.axes.get(groupA.cubes().iterator().nextInt());
               List<Vector3d> axesB = this.axes.get(groupB.cubes().iterator().nextInt());

               for (int i = 0; i < 3; i++) {
                  Vector3d axisA = axesA.get(i);
                  Vector3d axisB = axesB.get(i);
                  if (getGroupId(Math.acos(axisA.dot(axisB)) * (180.0 / Math.PI)) != 0) {
                     continue outer;
                  }
               }
            } else {
               for (PlaneGroup g : set) {
                  double dot = g.axis().dot(groupB.axis());
                  if (!TMath.isSimilar(dot, 0.0) && !TMath.isSimilar(dot, -1.0)) {
                     continue outer;
                  }

                  Vector3d vA = groupB.axis().rotate(g.invOrigin(), new Vector3d());
                  Vector3d vB = g.axis().rotate(groupB.invOrigin(), new Vector3d());
                  if (!isGroupable(vA) || !isGroupable(vB)) {
                     continue outer;
                  }
               }
            }

            set.add(groupB);
         }
      }

      return fetch(map);
   }

   private void fixGroups(Collection<ItemGroup> result, Map<PlaneGroup, Set<PlaneGroup>> planeGroupMap) {
      for (Entry<PlaneGroup, Set<PlaneGroup>> entry : planeGroupMap.entrySet()) {
         Set<PlaneGroup> planeGroups = entry.getValue();
         if (planeGroups.size() < 2) {
            PlaneGroup group = planeGroups.iterator().next();
            result.add(
               new ItemGroup(
                  0, new Quaterniond(group.origin()), TMath.toEulerXYZ(group.origin()), this.rotateCubes(new ArrayList<>(), group.cubes(), group.invOrigin())
               )
            );
         } else {
            Iterator<PlaneGroup> iterator = planeGroups.iterator();
            PlaneGroup groupA = iterator.next();
            Vector3d axisA = groupA.axis();
            Vector3d axisB = iterator.next().axis();

            while (TMath.isSimilar(Math.abs(axisA.dot(axisB)), 1.0) && iterator.hasNext()) {
               axisB = iterator.next().axis();
            }

            if (TMath.isSimilar(Math.abs(axisA.dot(axisB)), 1.0)) {
               ArrayList<ProcessedBone.Cube> list = new ArrayList<>();

               for (PlaneGroup planeGroup : planeGroups) {
                  this.rotateCubes(list, planeGroup.cubes(), groupA.invOrigin());
               }

               result.add(new ItemGroup(0, new Quaterniond(groupA.origin()), TMath.toEulerXYZ(groupA.origin()), list));
            } else {
               Vector3d cardinalA = getClosestCardinal(axisA);
               Quaterniond aToCardinal = axisA.rotationTo(cardinalA, new Quaterniond());
               Vector3d axisBPrime = axisB.rotate(aToCardinal, new Vector3d());
               Vector3d cardinalB = getClosestCardinal(axisBPrime);
               Quaterniond bPrimeToCardinal = axisBPrime.rotationTo(cardinalB, new Quaterniond());
               Quaterniond invRoot = bPrimeToCardinal.mul(aToCardinal);
               Quaterniond rootRotation = invRoot.invert(new Quaterniond());
               Vector3d rootEuler = TMath.fixEuler(TMath.toEulerXYZ(rootRotation));
               ArrayList<ProcessedBone.Cube> list = new ArrayList<>();

               for (PlaneGroup planeGroup : planeGroups) {
                  this.rotateCubes(list, planeGroup.cubes(), invRoot);
               }

               result.add(new ItemGroup(1, rootRotation, rootEuler, list));
            }
         }
      }
   }

   private List<ProcessedBone.Cube> rotateCubes(List<ProcessedBone.Cube> result, IntSet cubeIds, Quaterniond quaterniond) {
      IntIterator intIterator = cubeIds.iterator();

      while (intIterator.hasNext()) {
         int cubeId = intIterator.next();
         ProcessedBone.Cube cube = this.cubes.get(cubeId);
         cube.rotate(quaterniond);
         result.add(IllegalRotationSolver.solve(cube));
      }

      return result;
   }

   private static <T, R extends Collection<S>, S> Map<T, R> fetch(Map<T, R> map) {
      PriorityQueue<Pair<T, R>> queue = new PriorityQueue<>(Comparator.<Pair<T, R>, Integer>comparing(pair -> ((Collection)pair.second()).size()).reversed());
      map.forEach((t, rs) -> queue.add(Pair.of(t, rs)));
      Object2ObjectLinkedOpenHashMap<T, R> result = new Object2ObjectLinkedOpenHashMap();

      while (!queue.isEmpty()) {
         Pair<T, R> largest = queue.poll();
         if (((Collection)largest.second()).isEmpty()) {
            break;
         }

         queue.clear();
         map.remove(largest.first());
         result.put(largest.first(), largest.second());
         map.forEach((t, rs) -> {
            rs.removeAll((Collection<?>)largest.second());
            queue.add(Pair.of(t, rs));
         });
      }

      return result;
   }

   private static int getGroupId(double angle) {
      while (angle < 0.0) {
         angle += 360.0;
      }

      int intAngle = (int)Math.round(angle * 10000.0);
      return intAngle % 225000;
   }

   private static boolean isGroupable(Vector3d vec) {
      for (Axis axis : Axis.values()) {
         if (getGroupId(Math.acos(vec.dot(axis.getVector())) * (float) (180.0 / Math.PI)) != 0) {
            return false;
         }
      }

      return true;
   }

   private static Vector3d getClosestCardinal(Vector3d vec) {
      double dot = -1.0;
      Vector3d cardinal = null;

      for (Vector3d axis : CARDINAL_AXES) {
         double d = vec.dot(axis);
         if (d > dot) {
            dot = d;
            cardinal = axis;
         }
      }

      return cardinal;
   }

   private static boolean isLegal(ProcessedBone.Cube cube) {
      return legalId(cube.getRotation().x) + legalId(cube.getRotation().y) + legalId(cube.getRotation().z) < 2;
   }

   private static int legalId(double angle) {
      if (TMath.isInterval(angle, 90.0)) {
         return 0;
      } else {
         return TMath.isInterval(angle, 22.5) ? 1 : 999;
      }
   }

   private RotationSolver() {
   }
}
