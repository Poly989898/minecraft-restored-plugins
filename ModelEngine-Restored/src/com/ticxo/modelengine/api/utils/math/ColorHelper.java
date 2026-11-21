package com.ticxo.modelengine.api.utils.math;

import lombok.AllArgsConstructor;


public class ColorHelper {
   public static int getAlpha(int argb) {
      return argb >>> 24;
   }

   public static int getRed(int argb) {
      return argb >> 16 & 0xFF;
   }

   public static int getGreen(int argb) {
      return argb >> 8 & 0xFF;
   }

   public static int getBlue(int argb) {
      return argb & 0xFF;
   }

   public static int getArgb(int alpha, int red, int green, int blue) {
      return alpha << 24 | red << 16 | green << 8 | blue;
   }

   public static int getArgbFloat(float alpha, float red, float green, float blue) {
      return getArgb(
         TMath.floor(TMath.clamp(alpha * 255.0F, 0.0F, 1.0F)),
         TMath.floor(TMath.clamp(red * 255.0F, 0.0F, 1.0F)),
         TMath.floor(TMath.clamp(green * 255.0F, 0.0F, 1.0F)),
         TMath.floor(TMath.clamp(blue * 255.0F, 0.0F, 1.0F))
      );
   }

   public static ColorHelper.HSL toHSL(int color) {
      return toHSL(getAlpha(color), getRed(color), getGreen(color), getBlue(color));
   }

   public static ColorHelper.HSL toHSL(int alpha, int red, int green, int blue) {
      ColorHelper.HSL color = new ColorHelper.HSL();
      color.alpha = alpha;
      float r = red / 255.0F;
      float g = green / 255.0F;
      float b = blue / 255.0F;
      float cMax = Math.max(r, Math.max(g, b));
      float cMin = Math.min(r, Math.min(g, b));
      float delta = cMax - cMin;
      color.lightness = (cMax + cMin) * 0.5F;
      if (Math.abs(delta) <= 1.0E-5F) {
         color.hue = 0.0F;
         color.saturation = 0.0F;
      } else {
         if (cMax != r) {
            if (cMax == g) {
               color.hue = 60.0F * ((b - r) / delta + 2.0F);
            } else if (cMax == b) {
               color.hue = 60.0F * ((r - g) / delta + 4.0F);
            }
         } else {
            float f = (g - b) / delta;

            while (f < 0.0F) {
               f += 6.0F;
            }

            color.hue = 60.0F * (f % 6.0F);
         }

         color.saturation = delta / (1.0F - Math.abs(2.0F * color.lightness - 1.0F));
      }

      color.sanitize();
      return color;
   }

   public static int fromHSL(ColorHelper.HSL hsl) {
      hsl.sanitize();
      float c = (1.0F - Math.abs(2.0F * hsl.lightness - 1.0F)) * hsl.saturation;
      float x = c * (1.0F - Math.abs(hsl.hue / 60.0F % 2.0F - 1.0F));
      float m = hsl.lightness - c * 0.5F;
      float r = 0.0F;
      float g = 0.0F;
      float b = 0.0F;
      switch ((int)hsl.hue / 60 % 6) {
         case 0:
            r = c;
            g = x;
            break;
         case 1:
            r = x;
            g = c;
            break;
         case 2:
            g = c;
            b = x;
            break;
         case 3:
            g = x;
            b = c;
            break;
         case 4:
            r = x;
            b = c;
            break;
         case 5:
            r = c;
            b = x;
      }

      return getArgb(hsl.alpha, TMath.floor((r + m) * 255.0F), TMath.floor((g + m) * 255.0F), TMath.floor((b + m) * 255.0F));
   }

   public static int mixColor(int first, int second) {
      return getArgb(
         getAlpha(first) * getAlpha(second) / 255,
         getRed(first) * getRed(second) / 255,
         getGreen(first) * getGreen(second) / 255,
         getBlue(first) * getBlue(second) / 255
      );
   }

   public static int lerpColor(int colorA, int colorB, float ratio) {
      int aA = getAlpha(colorA);
      int aR = getRed(colorA);
      int aG = getGreen(colorA);
      int aB = getBlue(colorA);
      int bA = getAlpha(colorB);
      int bR = getRed(colorB);
      int bG = getGreen(colorB);
      int bB = getBlue(colorB);
      int fA = TMath.floor(Math.sqrt(TMath.lerp(aA * aA, bA * bA, ratio)));
      int fR = TMath.floor(Math.sqrt(TMath.lerp(aR * aR, bR * bR, ratio)));
      int fG = TMath.floor(Math.sqrt(TMath.lerp(aG * aG, bG * bG, ratio)));
      int fB = TMath.floor(Math.sqrt(TMath.lerp(aB * aB, bB * bB, ratio)));
      return getArgb(fA, fR, fG, fB);
   }

   @AllArgsConstructor
   public static class HSL {
      public int alpha;
      public float hue;
      public float saturation;
      public float lightness;

      public HSL() {
      }

      public void sanitize() {
         while (this.hue < 0.0F) {
            this.hue += 360.0F;
         }

         this.saturation = Math.max(Math.min(this.saturation, 1.0F), 0.0F);
         this.lightness = Math.max(Math.min(this.lightness, 1.0F), 0.0F);
      }
   }
}
