package com.ticxo.modelengine.api.utils.math;

import static org.junit.jupiter.api.Assertions.*;

import com.ticxo.modelengine.api.TestApi;
import org.bukkit.util.RayTraceResult;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OrientedBoundingBoxTest {
   @BeforeAll
   static void installApi() {
      TestApi.install();
   }

   private static OrientedBoundingBox cube(float min, float max) {
      return new OrientedBoundingBox(min, min, min, max, max, max);
   }

   /** Box centred on the origin, 4 long on X, then turned 90 degrees around Y so it is long on Z. */
   private static OrientedBoundingBox longOnZ() {
      return new OrientedBoundingBox(new Vector3f(0, 0, 0), new Vector3f(4, 1, 1), new Vector3f(0, (float)(Math.PI / 2), 0), 0F);
   }

   @Test
   void axisAlignedContainsPointsInsideOnly() {
      OrientedBoundingBox box = cube(0F, 2F);
      assertTrue(box.contains(new Vector3f(1, 1, 1)));
      assertFalse(box.contains(new Vector3f(3, 1, 1)));
      assertFalse(box.contains(new Vector3f(1, -0.5F, 1)));
   }

   @Test
   void rotatedBoxContainsPointsAlongItsTurnedAxis() {
      OrientedBoundingBox box = longOnZ();
      assertTrue(box.contains(new Vector3f(0, 0, 1.5F)));
      assertFalse(box.contains(new Vector3f(1.5F, 0, 0)));
   }

   @Test
   void intersectsDetectsOverlapAndSeparation() {
      assertTrue(cube(0F, 2F).intersects(cube(1F, 3F)));
      assertFalse(cube(0F, 2F).intersects(cube(5F, 7F)));
   }

   @Test
   void rotatedBoxIntersectsOnlyWhereItReaches() {
      OrientedBoundingBox box = longOnZ();
      OrientedBoundingBox onZ = new OrientedBoundingBox(-0.25F, -0.25F, 1.5F, 0.25F, 0.25F, 2.5F);
      OrientedBoundingBox onX = new OrientedBoundingBox(1.5F, -0.25F, -0.25F, 2.5F, 0.25F, 0.25F);
      assertTrue(box.intersects(onZ));
      assertFalse(box.intersects(onX));
   }

   @Test
   void rayTraceHitsFrontFaceOfAxisAlignedBox() {
      RayTraceResult hit = cube(0F, 2F).rayTrace(new Vector3f(-1, 1, 1), new Vector3f(1, 0, 0), 10.0, null);
      assertNotNull(hit);
      assertEquals(0.0, hit.getHitPosition().getX(), 1e-5);
      assertEquals(1.0, hit.getHitPosition().getY(), 1e-5);
   }

   @Test
   void rayTraceMissesAndRespectsMaxDistance() {
      assertNull(cube(0F, 2F).rayTrace(new Vector3f(-1, 5, 1), new Vector3f(1, 0, 0), 10.0, null));
      assertNull(cube(0F, 2F).rayTrace(new Vector3f(-10, 1, 1), new Vector3f(1, 0, 0), 5.0, null));
   }

   @Test
   void rayTraceOnRotatedBoxHitsTheTurnedEnd() {
      RayTraceResult hit = longOnZ().rayTrace(new Vector3f(0, 0, -5), new Vector3f(0, 0, 1), 10.0, null);
      assertNotNull(hit);
      assertEquals(-2.0, hit.getHitPosition().getZ(), 1e-4);
      RayTraceResult side = longOnZ().rayTrace(new Vector3f(-5, 0, 0), new Vector3f(1, 0, 0), 10.0, null);
      assertNotNull(side);
      assertEquals(-0.5, side.getHitPosition().getX(), 1e-4);
   }

   @Test
   void distanceSquaredIsZeroInsideAndGrowsOutside() {
      OrientedBoundingBox box = cube(0F, 2F);
      assertEquals(0.0, box.distanceSquared(new Vector3f(1, 1, 1)), 1e-9);
      assertEquals(4.0, box.distanceSquared(new Vector3f(4, 1, 1)), 1e-6);
   }

   @Test
   void isWithinRangeIsBoundedByHalfDiagonalPlusRange() {
      OrientedBoundingBox box = cube(-1F, 1F);
      assertTrue(box.isWithinRange(new Vector3f(3, 0, 0), 3.0));
      assertFalse(box.isWithinRange(new Vector3f(10, 0, 0), 3.0));
   }
}
