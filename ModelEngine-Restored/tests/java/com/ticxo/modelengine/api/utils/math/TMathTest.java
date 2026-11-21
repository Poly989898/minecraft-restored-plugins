package com.ticxo.modelengine.api.utils.math;

import static org.junit.jupiter.api.Assertions.*;

import com.ticxo.modelengine.api.TestApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class TMathTest {
   @BeforeAll
   static void installApi() {
      TestApi.install();
   }

   @Test
   void clampKeepsValueInsideRange() {
      assertEquals(3.0, TMath.clamp(5.0, 0.0, 3.0));
      assertEquals(0.0, TMath.clamp(-1.0, 0.0, 3.0));
      assertEquals(2.0F, TMath.clamp(2.0F, 0.0F, 3.0F));
      assertEquals(7, TMath.clamp(9, 1, 7));
      assertEquals(1, TMath.clamp(-4, 1, 7));
   }

   @Test
   void absMaxReturnsIndexOfLargestMagnitude() {
      assertEquals(0, TMath.absMax(-7F, 2F, 3F));
      assertEquals(1, TMath.absMax(1F, -5F, 3F));
      assertEquals(2, TMath.absMax(1F, 2F, -3F));
   }

   @Test
   void bitHelpersSetAndReadSingleBits() {
      assertEquals(8, TMath.setBit((byte)0, 3, true));
      assertEquals((byte)0xFE, TMath.setBit((byte)0xFF, 0, false));
      assertTrue(TMath.getBit((byte)0b0100, 2));
      assertFalse(TMath.getBit((byte)0b0100, 1));
   }

   @Test
   void floorAndCeilRoundTowardsInfinities() {
      assertEquals(-2, TMath.floor(-1.5));
      assertEquals(-1, TMath.ceil(-1.5));
      assertEquals(2, TMath.floor(2.0));
      assertEquals(3, TMath.ceil(2.1));
   }

   @Test
   void tryParseFallsBackToDefault() {
      assertEquals(12, TMath.tryParse("12", 0));
      assertEquals(7, TMath.tryParse("x", 7));
      assertEquals(3, TMath.tryParse(null, 3));
      assertEquals(1.5, TMath.tryParse("1.5", 0.0));
      assertEquals(2.5, TMath.tryParse("nope", 2.5));
      assertEquals(2.5, TMath.tryParse((String)null, 2.5));
   }

   @Test
   void wrapDegreeProducesRangeMinus180To180() {
      assertEquals(-170F, TMath.wrapDegree(190F));
      assertEquals(170F, TMath.wrapDegree(-190F));
      assertEquals(180F, TMath.wrapDegree(180F));
      assertEquals(0F, TMath.wrapDegree(360F));
      assertEquals(180F, TMath.wrapDegree(540F));
   }

   @Test
   void wrapRadianProducesRangeMinusPiToPi() {
      assertEquals(-Math.PI / 2, TMath.wrapRadian((float)(3 * Math.PI / 2)), 1e-5);
      assertEquals(Math.PI / 2, TMath.wrapRadian((float)(-3 * Math.PI / 2)), 1e-5);
      assertEquals(0.5F, TMath.wrapRadian(0.5F), 1e-6F);
   }

   @Test
   void degreeDifferenceTakesShortestWay() {
      assertEquals(20F, TMath.degreeDifference(350F, 10F));
      assertEquals(-20F, TMath.degreeDifference(10F, 350F));
      assertEquals(0F, TMath.degreeDifference(45F, 45F));
   }

   @Test
   void rotationByteEncodingMatchesVanillaScale() {
      assertEquals(64, TMath.rotToByte(90F));
      assertEquals(-128, TMath.rotToByte(180F));
      assertEquals(90F, TMath.byteToRot((byte)64));
   }

   @Test
   void lerpAndInverseLerp() {
      assertEquals(2.5, TMath.lerp(0.0, 10.0, 0.25));
      assertEquals(0.25, TMath.invLerp(0.0, 10.0, 2.5));
      assertEquals(2.5F, TMath.lerp(0F, 10F, 0.25F));
      assertEquals(3.0, TMath.lerp(2.0, 4.0, 0.5, 0.5));
   }

   @Test
   void rotLerpInterpolatesAcrossTheWrapAround() {
      assertEquals(360.0, TMath.rotLerp(350.0, 10.0, 0.5), 1e-9);
      assertEquals(0F, TMath.rotLerp(0F, 90F, 0.0), 1e-9F);
   }

   @Test
   void isSimilarUsesTolerance() {
      assertTrue(TMath.isSimilar(1.0F, 1.0F));
      assertFalse(TMath.isSimilar(1.0F, 2.0F));
      assertTrue(TMath.isSimilar(1.0, 1.0));
      assertFalse(TMath.isSimilar(1.0, 2.0));
   }
}
