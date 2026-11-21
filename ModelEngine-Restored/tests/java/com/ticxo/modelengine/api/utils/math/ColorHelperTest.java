package com.ticxo.modelengine.api.utils.math;

import static org.junit.jupiter.api.Assertions.*;

import com.ticxo.modelengine.api.TestApi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ColorHelperTest {
   @BeforeAll
   static void installApi() {
      TestApi.install();
   }

   @Test
   void argbPacksAndUnpacksChannels() {
      int argb = ColorHelper.getArgb(255, 1, 2, 3);
      assertEquals(0xFF010203, argb);
      assertEquals(255, ColorHelper.getAlpha(argb));
      assertEquals(1, ColorHelper.getRed(argb));
      assertEquals(2, ColorHelper.getGreen(argb));
      assertEquals(3, ColorHelper.getBlue(argb));
   }

   @Test
   void mixMultipliesChannels() {
      assertEquals(0xFF808080, ColorHelper.mixColor(0xFFFFFFFF, 0xFF808080));
      assertEquals(0xFF000000, ColorHelper.mixColor(0xFF000000, 0xFFFFFFFF));
   }

   @Test
   void lerpReturnsEndpointsAtZeroAndOne() {
      int a = 0xFF102030;
      int b = 0xFFF0E0D0;
      assertEquals(a, ColorHelper.lerpColor(a, b, 0.0F));
      assertEquals(b, ColorHelper.lerpColor(a, b, 1.0F));
   }

   @Test
   void primaryColoursHaveExpectedHue() {
      ColorHelper.HSL red = ColorHelper.toHSL(0xFFFF0000);
      ColorHelper.HSL green = ColorHelper.toHSL(0xFF00FF00);
      ColorHelper.HSL blue = ColorHelper.toHSL(0xFF0000FF);
      assertEquals(0F, red.hue, 1e-3F);
      assertEquals(120F, green.hue, 1e-3F);
      assertEquals(240F, blue.hue, 1e-3F);
      for (ColorHelper.HSL c : new ColorHelper.HSL[]{red, green, blue}) {
         assertEquals(1F, c.saturation, 1e-3F);
         assertEquals(0.5F, c.lightness, 1e-3F);
         assertEquals(255, c.alpha);
      }
   }

   @Test
   void greyHasNoSaturation() {
      ColorHelper.HSL grey = ColorHelper.toHSL(0xFF808080);
      assertEquals(0F, grey.saturation, 1e-6F);
      assertEquals(0F, grey.hue, 1e-6F);
   }

   @Test
   void primaryColoursSurviveHslRoundTrip() {
      for (int color : new int[]{0xFFFF0000, 0xFF00FF00, 0xFF0000FF}) {
         assertEquals(color, ColorHelper.fromHSL(ColorHelper.toHSL(color)));
      }
   }
}
