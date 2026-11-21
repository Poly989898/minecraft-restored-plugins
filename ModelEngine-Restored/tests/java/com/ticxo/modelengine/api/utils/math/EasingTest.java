package com.ticxo.modelengine.api.utils.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EasingTest {
   @Test
   void everyEasingStartsAtZeroAndEndsAtOne() {
      for (Easing easing : Easing.values()) {
         assertEquals(0.0, easing.apply(0.0), 1e-9, easing + " at 0");
         assertEquals(1.0, easing.apply(1.0), 1e-9, easing + " at 1");
      }
   }

   @Test
   void everyEasingIsFiniteInsideTheUnitInterval() {
      for (Easing easing : Easing.values()) {
         for (int i = 0; i <= 100; i++) {
            assertTrue(Double.isFinite(easing.apply(i / 100.0)), easing + " at " + i + "%");
         }
      }
   }

   @Test
   void knownMidpoints() {
      assertEquals(0.3, Easing.LINEAR.apply(0.3), 1e-12);
      assertEquals(0.25, Easing.EASE_IN_QUAD.apply(0.5), 1e-12);
      assertEquals(0.75, Easing.EASE_OUT_QUAD.apply(0.5), 1e-12);
      assertEquals(0.5, Easing.EASE_IN_OUT_QUAD.apply(0.5), 1e-12);
      assertEquals(0.125, Easing.EASE_IN_CUBIC.apply(0.5), 1e-12);
   }

   @Test
   void lookupAcceptsSnakeKebabAndCamelCase() {
      assertSame(Easing.EASE_IN_SINE, Easing.getOrNull("ease_in_sine"));
      assertSame(Easing.EASE_IN_SINE, Easing.getOrNull("EASE_IN_SINE"));
      assertSame(Easing.EASE_IN_SINE, Easing.getOrNull("ease-in-sine"));
      assertSame(Easing.EASE_IN_SINE, Easing.getOrNull("easeInSine"));
      assertSame(Easing.EASE_IN_SINE, Easing.getOrNull("  ease_in_sine "));
   }

   @Test
   void unknownNamesAreNullOrLinear() {
      assertNull(Easing.getOrNull("does_not_exist"));
      assertNull(Easing.getOrNull(null));
      assertSame(Easing.LINEAR, Easing.get("does_not_exist"));
      assertSame(Easing.LINEAR, Easing.get(null));
   }
}
